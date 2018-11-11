package nvdla

import chisel3._




class NV_NVDLA_CMAC_core(implicit val conf: cmacConfiguration) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())      
        val nvdla_core_rstn = Input(Bool())

        val sc2mac_dat_pvld = Input(Bool())  /* data valid */
        val sc2mac_dat_mask = Input(UInt(conf.CMAC_ATOMC.W))
        val sc2mac_wt_data = Input(Vec(conf.CMAC_INPUT_NUM, UInt(conf.CMAC_ATOMC.W)))
        val sc2mac_wt_sel = Input(UInt(conf.CMAC_ATOMK_HALF.W))

        val mac2accu_pvld = Output(Bool()) /* data valid */
        val mac2accu_mask = Output(UInt(conf.CMAC_ATOMK_HALF.W))
        val mac2accu_mode = Output(Bool())
        val mac2accu_data = Output(Vec(conf.CMAC_ATOMK_HALF, conf.CMAC_RESULT_WIDTH))
        val mac2accu_pd = Output(UInt(9.W))

        val reg2dp_op_en = Input(Bool())
        val reg2dp_conv_mode = Input(Bool())
        val dp2reg_done = Output(Bool())

        //Port for SLCG
        val dla_clk_ovr_on_sync = Input(Bool())
        val global_clk_ovr_on_sync = Input(Bool())
        val tmc2slcg_disable_clock_gating = Input(Bool())
        val slcg_op_en = Input(UInt(conf.CMAC_SLCG_NUM))
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

    val cfg_is_wg = Bool()
    val cfg_reg_en =Bool()

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

    val nvdla_op_gated_clk = Wire(Vec(conf.CMAC_ATOMK_HALF, Clock()))

    val u_cfg = Module(new nv_ram_rws(conf.CBUF_RAM_DEPTH, conf.CBUF_RAM_WIDTH))








}