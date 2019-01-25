// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_CMAC_core(implicit val conf: cmacConfiguration) extends Module {
//     val io = IO(new Bundle {

//         val sc2mac_dat_pvld = Input(Bool())  /* data valid */
//         val sc2mac_dat_mask = Input(UInt(conf.CMAC_ATOMC.W))
//         val sc2mac_dat_data = Input(Vec(conf.CMAC_ATOMC, UInt(conf.CMAC_BPE.W)))
//         val sc2mac_dat_pd = Input(UInt(9.W))

//         val sc2mac_wt_pvld = Input(Bool())
//         val sc2mac_wt_mask = Input(UInt(conf.CMAC_ATOMC.W))
//         val sc2mac_wt_data = Input(Vec(conf.CMAC_INPUT_NUM, UInt(conf.CMAC_BPE.W)))
//         val sc2mac_wt_sel = Input(UInt(conf.CMAC_ATOMK_HALF.W))

//         val mac2accu_pvld = Output(Bool()) /* data valid */
//         val mac2accu_mask = Output(UInt(conf.CMAC_ATOMK_HALF.W))
//         val mac2accu_mode = Output(Bool())
//         val mac2accu_data = Output(Vec(conf.CMAC_ATOMK_HALF, UInt(conf.CMAC_RESULT_WIDTH.W)))
//         val mac2accu_pd = Output(UInt(9.W))

//     })
// //     
// //          ┌─┐       ┌─┐
// //       ┌──┘ ┴───────┘ ┴──┐
// //       │                 │
// //       │       ───       │
// //       │  ─┬┘       └┬─  │
// //       │                 │
// //       │       ─┴─       │
// //       │                 │
// //       └───┐         ┌───┘
// //           │         │
// //           │         │
// //           │         │
// //           │         └──────────────┐
// //           │                        │
// //           │                        ├─┐
// //           │                        ┌─┘    
// //           │                        │
// //           └─┐  ┐  ┌───────┬──┐  ┌──┘         
// //             │ ─┤ ─┤       │ ─┤ ─┤         
// //             └──┴──┘       └──┴──┘ 

//     val in_dat_pvld = Wire(Bool())
//     io.mac2accu_mode := false.B
//     val in_dat_pd = Wire(UInt(9.W))
//     val in_wt_mask = Wire(UInt(conf.CMAC_ATOMK_HALF.W))
//     val in_dat_mask = Wire(UInt(conf.CMAC_ATOMC.W))
//     val in_dat_stripe_end = Wire(Bool())
//     val in_dat_stripe_st = Wire(Bool())
//     val out_mask = Wire(UInt(conf.CMAC_ATOMK_HALF.W))

//     //: for (my $i=0; $i < CMAC_ATOMC; ++$i) {
//     //:   print qq(
//     //:     wire [CMAC_BPE-1:0] in_dat_data${i};
//     //:     wire [CMAC_BPE-1:0] in_wt_data${i};  );
//     //: }

//     val in_dat_data = Wire(Vec(conf.CMAC_ATOMC, UInt(conf.CMAC_BPE.W)))
//     val in_wt_data = Wire(Vec(conf.CMAC_ATOMC, UInt(conf.CMAC_BPE.W)))

//     //: for(my $i=0; $i<CMAC_ATOMK_HALF ; $i++){
//     //:   print qq(
//     //:      wire [CMAC_BPE*CMAC_ATOMC-1:0] dat${i}_actv_data;
//     //:      wire [CMAC_ATOMC-1:0] dat${i}_actv_nz;
//     //:      wire [CMAC_ATOMC-1:0] dat${i}_actv_pvld;
//     //:      wire [CMAC_ATOMC-1:0] dat${i}_pre_mask;
//     //:      wire dat${i}_pre_pvld;
//     //:      wire dat${i}_pre_stripe_end;
//     //:      wire dat${i}_pre_stripe_st;
//     //:      wire [CMAC_BPE*CMAC_ATOMC-1:0] wt${i}_actv_data;
//     //:      wire [CMAC_ATOMC-1:0] wt${i}_actv_nz;
//     //:      wire [CMAC_ATOMC-1:0] wt${i}_actv_pvld;
//     //:      wire [CMAC_ATOMC-1:0] wt${i}_sd_mask;
//     //:      wire wt${i}_sd_pvld;
//     //:   );
//     //: }

//     val dat_actv_data = Wire(Vec(conf.CMAC_ATOMK_HALF, UInt((conf.CMAC_BPE*conf.CMAC_ATOMC).W)))
//     val dat_actv_nz = Wire(Vec(conf.CMAC_ATOMK_HALF, UInt(conf.CMAC_ATOMC.W)))
//     val dat_actv_pvld = Wire(Vec(conf.CMAC_ATOMK_HALF, UInt(conf.CMAC_ATOMC.W)))
//     val dat_pre_mask = Wire(Vec(conf.CMAC_ATOMK_HALF, UInt(conf.CMAC_ATOMC.W)))
//     val dat_pre_pvld = Wire(Vec(conf.CMAC_ATOMK_HALF, Bool()))
//     val dat_pre_stripe_end = Wire(Vec(conf.CMAC_ATOMK_HALF, Bool()))
//     val dat_pre_stripe_st = Wire(Vec(conf.CMAC_ATOMK_HALF, Bool()))
//     val wt_actv_data = Wire(Vec(conf.CMAC_ATOMK_HALF,UInt((conf.CMAC_BPE*conf.CMAC_ATOMC).W)))
//     val wt_actv_nz = Wire(Vec(conf.CMAC_ATOMK_HALF, UInt(conf.CMAC_ATOMC.W)))
//     val wt_actv_pvld = Wire(Vec(conf.CMAC_ATOMK_HALF, UInt(conf.CMAC_ATOMC.W)))
//     val wt_sd_mask = Wire(Vec(conf.CMAC_ATOMK_HALF, UInt(conf.CMAC_ATOMC.W)))
//     val wt_sd_pvld = Wire(Vec(conf.CMAC_ATOMK_HALF, Bool()))

//     //: my $i=MAC_PD_LATENCY;
//     //: &eperl::retime("-stage ${i} -wid 9 -i in_dat_pd -o out_pd -cg_en_i in_dat_pvld -cg_en_o out_pvld -cg_en_rtm");

//     when(in_dat_pvld){
//         out_pd := ShiftRegister(in_dat_pd, conf.MAC_PD_LATENCY)
//         out_pvld := ShiftRegister(in_dat_pvld, conf.MAC_PD_LATENCY)
//     }

//     //==========================================================
//     // input retiming logic            
//     //==========================================================

//     val u_rt_in = Module(new NV_NVDLA_CMAC_CORE_rt_in)

//     u_rt_in.io.sc2mac_dat_data := io.sc2mac_dat_data        //|< i )
//     u_rt_in.io.sc2mac_dat_mask := io.sc2mac_dat_mask        //|< i
//     u_rt_in.io.sc2mac_dat_pd := io.sc2mac_dat_pd            //|< i
//     u_rt_in.io.sc2mac_dat_pvld := io.sc2mac_dat_pvld              //|< i

//     u_rt_in.io.sc2mac_wt_data := io.sc2mac_wt_data         //|< i )
//     u_rt_in.io.sc2mac_wt_mask := io.sc2mac_wt_mask         //|< i
//     u_rt_in.io.sc2mac_wt_sel := io.sc2mac_wt_sel            //|< i
//     u_rt_in.io.sc2mac_wt_pvld := io.sc2mac_wt_pvld                //|< i
    
//     in_dat_data := u_rt_in.io.in_dat_data          //|< i )
//     in_dat_mask := u_rt_in.io.in_dat_mask             //|> w
//     in_dat_pd := u_rt_in.io.in_dat_pd                 //|> w
//     in_dat_pvld := u_rt_in.io.in_dat_pvld                   //|> w
//     in_dat_stripe_st := u_rt_in.io.in_dat_stripe_st             //|> w
//     in_dat_stripe_end := u_rt_in.io.in_dat_stripe_end             //|> w

//     in_wt_data := u_rt_in.io.in_wt_data         //|< i )
//     in_wt_mask := u_rt_in.io.in_wt_mask             //|> w
//     in_wt_pvld := u_rt_in.io.in_wt_pvld                    //|> w
//     in_wt_sel := u_rt_in.io.in_wt_sel                //|> w

//     //==========================================================
//     // input shadow and active pipeline
//     //==========================================================

//     val u_active = Module(new NV_NVDLA_CMAC_CORE_active)

//     u_active.io.in_dat_data := in_dat_data     //|< i )
//     u_active.io.in_dat_mask := in_dat_mask            //|< w
//     u_active.io.in_dat_pvld := in_dat_pvld                   //|< w
//     u_active.io.in_dat_stripe_end := in_dat_stripe_end             //|< w
//     u_active.io.in_dat_stripe_st := in_dat_stripe_st              //|< w

//     u_active.io.in_wt_data := in_wt_data         //|< i )
//     u_active.io.in_wt_mask := in_wt_mask             //|< w
//     u_active.io.in_wt_pvld := in_wt_pvld                    //|< w
//     u_active.io.in_wt_sel := in_wt_sel                //|< w

//     dat_actv_data := u_active.io.dat_actv_data        //|> w
//     dat_actv_nz := u_active.io.dat_actv_nz           //|> w
//     dat_actv_pvld := u_active.io.dat_actv_pvld         //|> w
//     dat_pre_stripe_end := u_active.io.dat_pre_stripe_end           //|> w
//     dat_pre_stripe_st := u_active.io.dat_pre_stripe_st            //|> w

//     wt_actv_data := u_active.io.wt_actv_data         //|> w
//     wt_actv_nz := u_active.io.wt_actv_nz            //|> w
//     wt_actv_pvld := u_active.io.wt_actv_pvld          //|> w


//     //==========================================================
//     // MAC CELLs
//     //==========================================================
  
//     val out_data = Wire(Vec(conf.CMAC_ATOMK_HALF, UInt(conf.CMAC_RESULT_WIDTH.W)))

//     val u_mac = Vec.fill(conf.CMAC_ATOMK_HALF){Module(new NV_NVDLA_CMAC_CORE_mac)}

//     for(i<- 0 to conf.CMAC_ATOMK_HALF-1){
//        u_mac(i).io.dat_actv_data := dat_actv_data(i)
//        u_mac(i).io.dat_actv_nz := dat_actv_nz(i)
//        u_mac(i).io.dat_actv_pvld := dat_actv_pvld(i)
//        u_mac(i).io.wt_actv_data := wt_actv_data(i)
//        u_mac(i).io.wt_actv_nz := wt_actv_nz(i)
//        u_mac(i).io.wt_actv_pvld := wt_actv_pvld(i)
//        u_mac(i).io.mac_out_data := out_data(i)
//        u_mac(i).io.mac_out_pvld := out_mask(i)                                                                                 
//     }

//     //==========================================================
//     // output retiming logic            
//     //==========================================================
//     //: my $i = CMAC_ATOMK_HALF+2;
//     //: print qq(
//     //:    wire nvdla_op_gated_clk_${i};  );
//     //NV_NVDLA_CMAC_CORE_rt_out u_rt_out (
//     //: my $i=CMAC_ATOMK_HALF+2;
//     //: print qq(
//     //:  .nvdla_core_clk                (nvdla_op_gated_clk_${i})          //|< w 
//     //: ,.nvdla_wg_clk                  (nvdla_op_gated_clk_${i})          //|< w );
//     //,.nvdla_core_rstn               (nvdla_core_rstn)               //|< i
//     //,.cfg_is_wg                     (cfg_is_wg)                     //|< w
//     //,.cfg_reg_en                    (cfg_reg_en)                    //|< w
//     //: for(my $i=0; $i<CMAC_ATOMK_HALF; $i++){
//     //: print qq(
//     //: ,.out_data${i}                     (out_data${i})              //|< w )
//     //: }
//     //,.out_mask                      (out_mask)                 //|< w
//     //,.out_pd                        (out_pd)                   //|< w
//     //,.out_pvld                      (out_pvld)                      //|< w
//     //,.dp2reg_done                   (dp2reg_done)                   //|> o
//     //: for(my $i=0; $i<CMAC_ATOMK_HALF; $i++){
//     //: print qq(
//     //: ,.mac2accu_data${i}                (mac2accu_data${i})         //|> o )
//     //: }
//     //,.mac2accu_mask                 (mac2accu_mask)            //|> o
//     //,.mac2accu_pd                   (mac2accu_pd)              //|> o
//     //,.mac2accu_pvld                 (mac2accu_pvld)                 //|> o
//     //);

//     val u_rt_out = Module(new NV_NVDLA_CMAC_CORE_rt_out)  // use seq

//     u_rt_out.io.nvdla_core_clk := nvdla_op_gated_clk(conf.CMAC_ATOMK_HALF+2)         //|< w 
//     u_rt_out.io.nvdla_wg_clk := nvdla_op_gated_clk(conf.CMAC_ATOMK_HALF+2)           //|< w );
//     u_rt_out.io.nvdla_core_rstn := io.nvdla_core_rstn               //|< i
//     u_rt_out.io.cfg_is_wg := cfg_is_wg                     //|< w
//     u_rt_out.io.cfg_reg_en := cfg_reg_en                    //|< w
//     u_rt_out.io.out_data := out_data              //|< w )
//     u_rt_out.io.out_mask := out_mask                 //|< w
//     u_rt_out.io.out_pd := out_pd                   //|< w
//     u_rt_out.io.out_pvld := out_pvld                      //|< w
//     u_rt_out.io.dp2reg_done := io.dp2reg_done                   //|> o
//     u_rt_out.io.mac2accu_data := io.mac2accu_data         //|> o )
//     u_rt_out.io.mac2accu_mask := io.mac2accu_mask            //|> o
//     u_rt_out.io.mac2accu_pd := io.mac2accu_pd              //|> o
//     u_rt_out.io.mac2accu_pvld := io.mac2accu_pvld                 //|> o  


// }