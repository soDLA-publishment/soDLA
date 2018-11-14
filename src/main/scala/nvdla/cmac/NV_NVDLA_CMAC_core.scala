package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._




class NV_NVDLA_CMAC_core(implicit val conf: cmacConfiguration) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())      
        val nvdla_core_rstn = Input(Bool())

        val sc2mac_dat_pvld = Input(Bool())  /* data valid */
        val sc2mac_dat_mask = Input(UInt(conf.CMAC_ATOMC.W))
        val sc2mac_dat_data = Input(Vec(conf.CMAC_INPUT_NUM, UInt(conf.CMAC_BPE.W)))
        val sc2mac_dat_pd = Input(UInt(9.W))
        val sc2mac_wt_pvld = Input(Bool())
        val sc2mac_wt_mask = Input(UInt(conf.CMAC_ATOMC.W))
        val sc2mac_wt_data = Input(Vec(conf.CMAC_INPUT_NUM, UInt(conf.CMAC_ATOMC.W)))
        val sc2mac_wt_sel = Input(UInt(conf.CMAC_ATOMK_HALF.W))

        val mac2accu_pvld = Output(Bool()) /* data valid */
        val mac2accu_mask = Output(UInt(conf.CMAC_ATOMK_HALF.W))
        val mac2accu_mode = Output(Bool())
        val mac2accu_data = Output(Vec(conf.CMAC_ATOMK_HALF, UInt(conf.CMAC_RESULT_WIDTH.W)))
        val mac2accu_pd = Output(UInt(9.W))

        val reg2dp_op_en = Input(Bool())
        val reg2dp_conv_mode = Input(Bool())
        val dp2reg_done = Output(Bool())

        //Port for SLCG
        val dla_clk_ovr_on_sync = Input(Bool())
        val global_clk_ovr_on_sync = Input(Bool())
        val tmc2slcg_disable_clock_gating = Input(Bool())
        val slcg_op_en = Input(UInt(conf.CMAC_SLCG_NUM.W))
    })
//     
//          ┌─┐       ┌─┐
//       ┌──┘ ┴───────┘ ┴──┐
//       │                 │
//       │       ───       │
//       │  ─┬┘       └┬─  │
//       │                 │
//       │       ─┴─       │
//       │                 │
//       └───┐         ┌───┘
//           │         │
//           │         │
//           │         │
//           │         └──────────────┐
//           │                        │
//           │                        ├─┐
//           │                        ┌─┘    
//           │                        │
//           └─┐  ┐  ┌───────┬──┐  ┌──┘         
//             │ ─┤ ─┤       │ ─┤ ─┤         
//             └──┴──┘       └──┴──┘ 

    val cfg_is_wg = Wire(Bool())
    val cfg_reg_en = Wire(Bool())

    // interface with register config   
    //==========================================================
    //: my $i=CMAC_ATOMK_HALF;
    //: print qq(
    //:    wire nvdla_op_gated_clk_${i};  );
    //: print qq(
    //: NV_NVDLA_CMAC_CORE_cfg u_cfg (
    //:    .nvdla_core_clk                (nvdla_op_gated_clk_${i})          //|< w
    //:   ,.nvdla_core_rstn               (nvdla_core_rstn)               //|< i
    //:   ,.dp2reg_done                   (dp2reg_done)                   //|< o
    //:   ,.reg2dp_conv_mode              (reg2dp_conv_mode)              //|< i
    //:   ,.reg2dp_op_en                  (reg2dp_op_en)                  //|< i
    //:   ,.cfg_is_wg                     (cfg_is_wg)                     //|> w
    //:   ,.cfg_reg_en                    (cfg_reg_en)                    //|> w
    //:   );
    //: );

    val nvdla_op_gated_clk = Wire(Vec(conf.CMAC_ATOMK_HALF+2, Clock()))

    val u_cfg = Module(new NV_NVDLA_CMAC_CORE_cfg)

    u_cfg.io.nvdla_core_clk := nvdla_op_gated_clk(conf.CMAC_ATOMK_HALF)
    u_cfg.io.nvdla_core_rstn := io.nvdla_core_rstn
    u_cfg.io.dp2reg_done := io.dp2reg_done
    u_cfg.io.reg2dp_conv_mode := io.reg2dp_conv_mode
    u_cfg.io.reg2dp_op_en := io.reg2dp_op_en
    u_cfg.io.cfg_is_wg := cfg_is_wg
    u_cfg.io.cfg_reg_en := cfg_reg_en 

    val in_dat_pvld = Wire(Bool())
    io.mac2accu_mode := false.B
    val in_dat_pd = Wire(UInt(9.W))
    val in_wt_mask = Wire(UInt(conf.CMAC_ATOMK_HALF.W))
    val in_dat_mask = Wire(UInt(conf.CMAC_ATOMC.W))
    val in_dat_stripe_end = Wire(Bool())
    val in_dat_stripe_st = Wire(Bool())
    val out_mask = Wire(UInt(conf.CMAC_ATOMK_HALF.W))

    //: for (my $i=0; $i < CMAC_ATOMC; ++$i) {
    //:   print qq(
    //:     wire [CMAC_BPE-1:0] in_dat_data${i};
    //:     wire [CMAC_BPE-1:0] in_wt_data${i};  );
    //: }

    val in_dat_data = Wire(Vec(conf.CMAC_ATOMC, UInt(conf.CMAC_BPE.W)))
    val in_wt_data = Wire(Vec(conf.CMAC_ATOMC, UInt(conf.CMAC_BPE.W)))

    //: for(my $i=0; $i<CMAC_ATOMK_HALF ; $i++){
    //:   print qq(
    //:      wire [CMAC_BPE*CMAC_ATOMC-1:0] dat${i}_actv_data;
    //:      wire [CMAC_ATOMC-1:0] dat${i}_actv_nz;
    //:      wire [CMAC_ATOMC-1:0] dat${i}_actv_pvld;
    //:      wire [CMAC_ATOMC-1:0] dat${i}_pre_mask;
    //:      wire dat${i}_pre_pvld;
    //:      wire dat${i}_pre_stripe_end;
    //:      wire dat${i}_pre_stripe_st;
    //:      wire [CMAC_BPE*CMAC_ATOMC-1:0] wt${i}_actv_data;
    //:      wire [CMAC_ATOMC-1:0] wt${i}_actv_nz;
    //:      wire [CMAC_ATOMC-1:0] wt${i}_actv_pvld;
    //:      wire [CMAC_ATOMC-1:0] wt${i}_sd_mask;
    //:      wire wt${i}_sd_pvld;
    //:   );
    //: }

    val dat_actv_data = Wire(Vec(conf.CMAC_ATOMK_HALF, UInt((conf.CMAC_BPE*conf.CMAC_ATOMC).W)))
    val dat_actv_nz = Wire(Vec(conf.CMAC_ATOMK_HALF, UInt(conf.CMAC_ATOMC.W)))
    val dat_actv_pvld = Wire(Vec(conf.CMAC_ATOMK_HALF, UInt(conf.CMAC_ATOMC.W)))
    val dat_pre_mask = Wire(Vec(conf.CMAC_ATOMK_HALF, UInt(conf.CMAC_ATOMC.W)))
    val dat_pre_pvld = Wire(Vec(conf.CMAC_ATOMK_HALF, Bool()))
    val dat_pre_stripe_end = Wire(Vec(conf.CMAC_ATOMK_HALF, Bool()))
    val dat_pre_stripe_st = Wire(Vec(conf.CMAC_ATOMK_HALF, Bool()))
    val wt_actv_data = Wire(Vec(conf.CMAC_ATOMK_HALF,UInt((conf.CMAC_BPE*conf.CMAC_ATOMC).W)))
    val wt_actv_nz = Wire(Vec(conf.CMAC_ATOMK_HALF, UInt(conf.CMAC_ATOMC.W)))
    val wt_actv_pvld = Wire(Vec(conf.CMAC_ATOMK_HALF, UInt(conf.CMAC_ATOMC.W)))
    val wt_sd_mask = Wire(Vec(conf.CMAC_ATOMK_HALF, UInt(conf.CMAC_ATOMC.W)))
    val wt_sd_pvld = Wire(Vec(conf.CMAC_ATOMK_HALF, Bool()))

    //: my $i=MAC_PD_LATENCY;
    //: &eperl::retime("-stage ${i} -wid 9 -i in_dat_pd -o out_pd -cg_en_i in_dat_pvld -cg_en_o out_pvld -cg_en_rtm");

    val out_pd = Reg(UInt(9.W))
    val out_pvld = Reg(Bool())
    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){
        when(in_dat_pvld){
            out_pd := ShiftRegister(in_dat_pd, conf.MAC_PD_LATENCY)
            out_pvld := ShiftRegister(in_dat_pvld, conf.MAC_PD_LATENCY)
        }
    }

    //==========================================================
    // input retiming logic            
    //==========================================================

    //NV_NVDLA_CMAC_CORE_rt_in u_rt_in (
    //: my $i= CMAC_ATOMK_HALF;
    //: print qq(
    //: .nvdla_core_clk                (nvdla_op_gated_clk_${i})          //|< w  );
    //,.nvdla_core_rstn               (nvdla_core_rstn)               //|< i
    //: for(my $i=0; $i<CMAC_INPUT_NUM ; $i++){
    //: print qq(
    //: ,.sc2mac_dat_data${i}        (sc2mac_dat_data${i})         //|< i )
    //: }
    //,.sc2mac_dat_mask               (sc2mac_dat_mask)        //|< i
    //,.sc2mac_dat_pd                 (sc2mac_dat_pd)            //|< i
    //,.sc2mac_dat_pvld               (sc2mac_dat_pvld)               //|< i
    //: for(my $i=0; $i<CMAC_INPUT_NUM ; $i++){
    //: print qq(
    //: ,.sc2mac_wt_data${i}        (sc2mac_wt_data${i})         //|< i )
    //: }
    // ,.sc2mac_wt_mask                (sc2mac_wt_mask)         //|< i
    // ,.sc2mac_wt_pvld                (sc2mac_wt_pvld)                //|< i
    //,.sc2mac_wt_sel                 (sc2mac_wt_sel)            //|< i
    //: for(my $i=0; $i<CMAC_INPUT_NUM ; $i++){
    //: print qq(
    //: ,.in_dat_data${i}           (in_dat_data${i})         //|< i )
    //: }
    //,.in_dat_mask                   (in_dat_mask)            //|> w
    //,.in_dat_pd                     (in_dat_pd)                //|> w
    //,.in_dat_pvld                   (in_dat_pvld)                   //|> w
    //,.in_dat_stripe_end             (in_dat_stripe_end)             //|> w
    //,.in_dat_stripe_st              (in_dat_stripe_st)              //|> w
    //: for(my $i=0; $i<CMAC_INPUT_NUM ; $i++){
    //: print qq(
    //: ,.in_wt_data${i}           (in_wt_data${i})         //|< i )
    //: }
    //,.in_wt_mask                    (in_wt_mask)             //|> w
    //,.in_wt_pvld                    (in_wt_pvld)                    //|> w
    //,.in_wt_sel                     (in_wt_sel)                //|> w
    //);

    val u_rt_in = Module(new NV_NVDLA_CMAC_CORE_rt_in)

    u_rt_in.io.nvdla_core_clk := nvdla_op_gated_clk(conf.CMAC_ATOMK_HALF)          //|< w  );
    u_rt_in.io.nvdla_core_rstn := io.nvdla_core_rstn     //|< i
    u_rt_in.io.sc2mac_dat_data := io.sc2mac_dat_data        //|< i )
    u_rt_in.io.sc2mac_dat_mask := io.sc2mac_dat_mask        //|< i
    u_rt_in.io.sc2mac_dat_pd := io.sc2mac_dat_pd            //|< i
    u_rt_in.io.sc2mac_dat_pvld := io.sc2mac_dat_pvld              //|< i
    u_rt_in.io.sc2mac_wt_data := io.sc2mac_wt_data         //|< i )
    u_rt_in.io.sc2mac_wt_mask := io.sc2mac_wt_mask         //|< i
    u_rt_in.io.sc2mac_wt_pvld := io.sc2mac_wt_pvld                //|< i
    u_rt_in.io.sc2mac_wt_sel := io.sc2mac_wt_sel            //|< i
    u_rt_in.io.in_dat_data := in_dat_data         //|< i )
    u_rt_in.io.in_dat_mask := in_dat_mask            //|> w
    u_rt_in.io.in_dat_pd := in_dat_pd                //|> w
    u_rt_in.io.in_dat_pvld := in_dat_pvld                  //|> w
    u_rt_in.io.in_dat_stripe_end := in_dat_stripe_end             //|> w
    u_rt_in.io.in_dat_stripe_st := in_dat_stripe_st             //|> w
    u_rt_in.io.in_wt_data := in_wt_data         //|< i )
    u_rt_in.io.in_wt_mask := in_wt_mask             //|> w
    u_rt_in.io.in_wt_pvld := in_wt_pvld                    //|> w
    u_rt_in.io.in_wt_sel := in_wt_sel                //|> w

    //==========================================================
    // input shadow and active pipeline
    //==========================================================


    //: my $i = CMAC_ATOMK_HALF+1;
    //: print qq(
    //:    wire nvdla_op_gated_clk_${i};  );
    //NV_NVDLA_CMAC_CORE_active u_active (
    //: my $i=CMAC_ATOMK_HALF+1;
    //: print qq(
    //:  .nvdla_core_clk                (nvdla_op_gated_clk_${i})         //|< w );
    //,.nvdla_core_rstn               (nvdla_core_rstn)               //|< i
    //: for(my $i=0; $i<CMAC_INPUT_NUM ; $i++){
    //: print qq(
    //: ,.in_dat_data${i}           (in_dat_data${i})     //|< i )
    //: }
    //,.in_dat_mask                   (in_dat_mask)            //|< w
    //,.in_dat_pvld                   (in_dat_pvld)                   //|< w
    //,.in_dat_stripe_end             (in_dat_stripe_end)             //|< w
    //,.in_dat_stripe_st              (in_dat_stripe_st)              //|< w
    //: for(my $i=0; $i<CMAC_INPUT_NUM ; $i++){
    //: print qq(
    //: ,.in_wt_data${i}           (in_wt_data${i})         //|< i )
    //: }
    //,.in_wt_mask                    (in_wt_mask)             //|< w
    //,.in_wt_pvld                    (in_wt_pvld)                    //|< w
    //,.in_wt_sel                     (in_wt_sel)                //|< w
    //: for(my $i=0; $i<CMAC_ATOMK_HALF ; $i++){
    //: print qq(
    //: ,.dat${i}_actv_data                (dat${i}_actv_data)        //|> w
    //: ,.dat${i}_actv_nz                  (dat${i}_actv_nz)           //|> w
    //: ,.dat${i}_actv_pvld                (dat${i}_actv_pvld)         //|> w
    //: ,.dat${i}_pre_mask                 (dat${i}_pre_mask)           //|> w
    //: ,.dat${i}_pre_pvld                 (dat${i}_pre_pvld)                 //|> w
    //: ,.dat${i}_pre_stripe_end           (dat${i}_pre_stripe_end)           //|> w
    //: ,.dat${i}_pre_stripe_st            (dat${i}_pre_stripe_st)            //|> w
    //: )
    //: }
    //: for(my $i=0; $i<CMAC_ATOMK_HALF ; $i++){
    //: print qq(
    //: ,.wt${i}_actv_data                 (wt${i}_actv_data)         //|> w
    //: ,.wt${i}_actv_nz                   (wt${i}_actv_nz)            //|> w
    //: ,.wt${i}_actv_pvld                 (wt${i}_actv_pvld)          //|> w
    //: ,.wt${i}_sd_mask                   (wt${i}_sd_mask)           //|> w
    //: ,.wt${i}_sd_pvld                   (wt${i}_sd_pvld)           //|> w
    //: )
    //: }
    //);

    val u_active = Module(new NV_NVDLA_CMAC_CORE_active)

    u_active.io.nvdla_core_clk := nvdla_op_gated_clk(conf.CMAC_ATOMK_HALF +1)        //|< w );
    u_active.io.nvdla_core_rstn := io.nvdla_core_rstn               //|< i
    u_active.io.in_dat_data := in_dat_data     //|< i )
    u_active.io.in_dat_mask := in_dat_mask            //|< w
    u_active.io.in_dat_pvld := in_dat_pvld                   //|< w
    u_active.io.in_dat_stripe_end := in_dat_stripe_end             //|< w
    u_active.io.in_dat_stripe_st := in_dat_stripe_st              //|< w
    u_active.io.in_wt_data := in_wt_data         //|< i )
    u_active.io.in_wt_mask := in_wt_mask             //|< w
    u_active.io.in_wt_pvld := in_wt_pvld                    //|< w
    u_active.io.in_wt_sel := in_wt_sel                //|< w
    u_active.io.dat_actv_data := dat_actv_data        //|> w
    u_active.io.dat_actv_nz := dat_actv_nz           //|> w
    u_active.io.dat_actv_pvld := dat_actv_pvld         //|> w
    u_active.io.dat_pre_mask := dat_pre_mask           //|> w
    u_active.io.dat_pre_pvld := dat_pre_pvld                 //|> w
    u_active.io.dat_pre_stripe_end := dat_pre_stripe_end           //|> w
    u_active.io.dat_pre_stripe_st := dat_pre_stripe_st            //|> w
    u_active.io.wt_actv_data := wt_actv_data         //|> w
    u_active.io.wt_actv_nz := wt_actv_nz            //|> w
    u_active.io.wt_actv_pvld := wt_actv_pvld          //|> w
    u_active.io.wt_sd_mask := wt_sd_mask           //|> w
    u_active.io.wt_sd_pvld := wt_sd_pvld           //|> w


    //==========================================================
    // MAC CELLs
    //==========================================================
    //:     my $total_num = CMAC_ATOMK_HALF;
    //:     for(my $i = 0; $i < $total_num; $i ++) {
    //:         print qq {
    //: wire nvdla_op_gated_clk_${i};
    //: wire nvdla_wg_gated_clk_${i};
    //: wire [CMAC_RESULT_WIDTH-1:0] out_data${i};
    //: NV_NVDLA_CMAC_CORE_mac u_mac_${i} (
    //:    .nvdla_core_clk                (nvdla_op_gated_clk_${i})          //|< w
    //:   ,.nvdla_wg_clk                  (nvdla_op_gated_clk_${i})          //|< w , need update for winograd
    //:   ,.nvdla_core_rstn               (nvdla_core_rstn)               //|< i
    //:   ,.cfg_is_wg                     (cfg_is_wg)                     //|< w
    //:   ,.cfg_reg_en                    (cfg_reg_en)                    //|< w
    //:   ,.dat_actv_data                 (dat${i}_actv_data)        //|< w
    //:   ,.dat_actv_nz                   (dat${i}_actv_nz)           //|< w
    //:   ,.dat_actv_pvld                 (dat${i}_actv_pvld)         //|< w
    //:   ,.wt_actv_data                  (wt${i}_actv_data)         //|< w
    //:   ,.wt_actv_nz                    (wt${i}_actv_nz)            //|< w
    //:   ,.wt_actv_pvld                  (wt${i}_actv_pvld)          //|< w
    //:   ,.mac_out_data                  (out_data${i})              //|> w
    //:   ,.mac_out_pvld                  (out_mask[${i}])            //|> w
    //:   );
    //:     }
    //:}

    
    val out_data = Wire(Vec(conf.CMAC_ATOMK_HALF, UInt(conf.CMAC_RESULT_WIDTH.W)))

    val u_mac = Vec.fill(conf.CMAC_ATOMK_HALF){Module(new NV_NVDLA_CMAC_CORE_mac)}

    for(i<- 0 to conf.CMAC_ATOMK_HALF-1){
       u_mac(i).io.nvdla_core_clk :=  nvdla_op_gated_clk(i)
       u_mac(i).io.nvdla_wg_clk := nvdla_op_gated_clk(i)
       u_mac(i).io.nvdla_core_rstn := io.nvdla_core_rstn
       u_mac(i).io.cfg_is_wg := cfg_is_wg
       u_mac(i).io.cfg_reg_en := cfg_reg_en
       u_mac(i).io.dat_actv_data := dat_actv_data(i)
       u_mac(i).io.dat_actv_nz := dat_actv_nz(i)
       u_mac(i).io.dat_actv_pvld := dat_actv_pvld(i)
       u_mac(i).io.wt_actv_data := wt_actv_data(i)
       u_mac(i).io.wt_actv_nz := wt_actv_nz(i)
       u_mac(i).io.wt_actv_pvld := wt_actv_pvld(i)
       u_mac(i).io.mac_out_data := out_data(i)
       u_mac(i).io.mac_out_pvld := out_mask(i)                                                                                 
    }

    //==========================================================
    // output retiming logic            
    //==========================================================
    //: my $i = CMAC_ATOMK_HALF+2;
    //: print qq(
    //:    wire nvdla_op_gated_clk_${i};  );
    //NV_NVDLA_CMAC_CORE_rt_out u_rt_out (
    //: my $i=CMAC_ATOMK_HALF+2;
    //: print qq(
    //:  .nvdla_core_clk                (nvdla_op_gated_clk_${i})          //|< w 
    //: ,.nvdla_wg_clk                  (nvdla_op_gated_clk_${i})          //|< w );
    //,.nvdla_core_rstn               (nvdla_core_rstn)               //|< i
    //,.cfg_is_wg                     (cfg_is_wg)                     //|< w
    //,.cfg_reg_en                    (cfg_reg_en)                    //|< w
    //: for(my $i=0; $i<CMAC_ATOMK_HALF; $i++){
    //: print qq(
    //: ,.out_data${i}                     (out_data${i})              //|< w )
    //: }
    //,.out_mask                      (out_mask)                 //|< w
    //,.out_pd                        (out_pd)                   //|< w
    //,.out_pvld                      (out_pvld)                      //|< w
    //,.dp2reg_done                   (dp2reg_done)                   //|> o
    //: for(my $i=0; $i<CMAC_ATOMK_HALF; $i++){
    //: print qq(
    //: ,.mac2accu_data${i}                (mac2accu_data${i})         //|> o )
    //: }
    //,.mac2accu_mask                 (mac2accu_mask)            //|> o
    //,.mac2accu_pd                   (mac2accu_pd)              //|> o
    //,.mac2accu_pvld                 (mac2accu_pvld)                 //|> o
    //);

    val u_rt_out = Module(new NV_NVDLA_CMAC_CORE_rt_out)

    u_rt_out.io.nvdla_core_clk := nvdla_op_gated_clk(conf.CMAC_ATOMK_HALF+2)         //|< w 
    u_rt_out.io.nvdla_wg_clk := nvdla_op_gated_clk(conf.CMAC_ATOMK_HALF+2)           //|< w );
    u_rt_out.io.nvdla_core_rstn := io.nvdla_core_rstn               //|< i
    u_rt_out.io.cfg_is_wg := cfg_is_wg                     //|< w
    u_rt_out.io.cfg_reg_en := cfg_reg_en                    //|< w
    u_rt_out.io.out_data := out_data              //|< w )
    u_rt_out.io.out_mask := out_mask                 //|< w
    u_rt_out.io.out_pd := out_pd                   //|< w
    u_rt_out.io.out_pvld := out_pvld                      //|< w
    u_rt_out.io.dp2reg_done := io.dp2reg_done                   //|> o
    u_rt_out.io.mac2accu_data := io.mac2accu_data         //|> o )
    u_rt_out.io.mac2accu_mask := io.mac2accu_mask            //|> o
    u_rt_out.io.mac2accu_pd := io.mac2accu_pd              //|> o
    u_rt_out.io.mac2accu_pvld := io.mac2accu_pvld                 //|> o  

    //==========================================================
    // SLCG groups
    //==========================================================
    //:     for(my $i = 0; $i < CMAC_SLCG_NUM; $i ++) {
    //:         print qq {
    //: NV_NVDLA_CMAC_CORE_slcg u_slcg_op_${i} (
    //:    .dla_clk_ovr_on_sync           (dla_clk_ovr_on_sync)           //|< i
    //:   ,.global_clk_ovr_on_sync        (global_clk_ovr_on_sync)        //|< i
    //:   ,.nvdla_core_clk                (nvdla_core_clk)                //|< i
    //:   ,.nvdla_core_rstn               (nvdla_core_rstn)               //|< i
    //:   ,.slcg_en_src_0                 (slcg_op_en[${i}])                 //|< i
    //:   ,.slcg_en_src_1                 (1'b1)                          //|< ?
    //:   ,.tmc2slcg_disable_clock_gating (tmc2slcg_disable_clock_gating) //|< i
    //:   ,.nvdla_core_gated_clk          (nvdla_op_gated_clk_${i})          //|> w
    //:   );
    //:     }
    //: }

    val u_slcg_op = Vec.fill(conf.CMAC_SLCG_NUM){Module(new NV_NVDLA_CMAC_CORE_slcg)}

    for(i<- 0 to conf.CMAC_SLCG_NUM-1){
       u_mac(i).io.nvdla_core_clk :=  nvdla_op_gated_clk(i)
       u_mac(i).io.nvdla_wg_clk := nvdla_op_gated_clk(i)
       u_mac(i).io.nvdla_core_rstn := io.nvdla_core_rstn
       u_mac(i).io.cfg_is_wg := cfg_is_wg
       u_mac(i).io.cfg_reg_en := cfg_reg_en
       u_mac(i).io.dat_actv_data := dat_actv_data(i)
       u_mac(i).io.dat_actv_nz := dat_actv_nz(i)
       u_mac(i).io.dat_actv_pvld := dat_actv_pvld(i)
       u_mac(i).io.wt_actv_data := wt_actv_data(i)
       u_mac(i).io.wt_actv_nz := wt_actv_nz(i)
       u_mac(i).io.wt_actv_pvld := wt_actv_pvld(i)
       u_mac(i).io.mac_out_data := out_data(i)
       u_mac(i).io.mac_out_pvld := out_mask(i)                                                                                 
    }








}