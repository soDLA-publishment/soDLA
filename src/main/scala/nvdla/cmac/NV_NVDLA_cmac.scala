package nvdla

import chisel3._
import chisel3.experimental._




class NV_NVDLA_cmac(implicit val conf: cmacConfiguration) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())      
        val nvdla_core_rstn = Input(Bool())

        val cmac_a2csb_resp_valid = Output(Bool())  /* data valid */
        val cmac_a2csb_resp_pd = Output(UInt(34.W))/* pkt_id_width=1 pkt_widths=33,33  */

        val csb2cmac_a_req_pvld = Input(Bool())/* data valid */
        val csb2cmac_a_req_prdy = Output(Bool())/* data return handshake */
        val csb2cmac_a_req_pd = Input(UInt(63.W))

        val mac2accu_pvld = Output(Bool()) /* data valid */
        val mac2accu_mask = Output(UInt(conf.CMAC_ATOMK_HALF.W))
        val mac2accu_mode = Output(Bool())
        val mac2accu_data = Output(Vec(conf.CMAC_ATOMK_HALF, conf.CMAC_RESULT_WIDTH))
        val mac2accu_pd = Output(UInt(9.W))

        val sc2mac_dat_pvld = Input(Bool()) /* data valid */
        val sc2mac_dat_mask= Input(UInt(conf.CMAC_ATOMC.W))
        val sc2mac_dat_data = Input(conf.CMAC_ATOMC, UInt(conf.CMAC_BPE.W))
        val sc2mac_dat_pd = Input(UInt(9.W))

        val sc2mac_wt_pvld = Input(Bool()) /* data valid */
        val sc2mac_wt_mask= Input(UInt(conf.CMAC_ATOMC.W))
        val sc2mac_wt_data = Input(conf.CMAC_ATOMC, UInt(conf.CMAC_BPE.W))
        val sc2mac_wt_sel = Input(UInt(conf.CMAC_ATOMK_HALF.W))

        //Port for SLCG
        val dla_clk_ovr_on_sync = Input(Bool())
        val global_clk_ovr_on_sync = Input(Bool())
        val tmc2slcg_disable_clock_gating = Input(Bool())
           
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

    val dp2reg_done = Wire(Bool())
    val reg2dp_conv_mode = Wire(Bool())
    val reg2dp_op_en = Wire(Bool())
    val reg2dp_proc_precision = "b0".asUInt(2.W)
    val slcg_op_en = Wire(UInt(conf.CMAC_SLCG_NUM.W))

    //==========================================================
    // core
    //==========================================================

    val u_core = Module(new NV_NVDLA_CMAC_core)

    u_core.io.nvdla_core_clk := io.nvdla_core_clk               //|< i
    u_core.io.nvdla_core_rstn := io.nvdla_core_rstn               //|< i
    u_core.io.sc2mac_dat_pvld := io.sc2mac_dat_pvld               //|< i
    u_core.io.sc2mac_dat_mask := io.sc2mac_dat_mask        //|< i
    u_core.io.sc2mac_dat_data := io.sc2mac_dat_data        //|< i )
    u_core.io.sc2mac_dat_pd := io.sc2mac_dat_pd            //|< i
    u_core.io.sc2mac_wt_pvld := io.sc2mac_wt_pvld                //|< i
    u_core.io.sc2mac_wt_mask := io.sc2mac_wt_mask         //|< i
    u_core.io.sc2mac_wt_data := io.sc2mac_wt_data         //|< i )
    u_core.io.sc2mac_wt_sel := io.sc2mac_wt_sel            //|< i
    u_core.io.mac2accu_pvld := io.mac2accu_pvld                 //|> o
    u_core.io.mac2accu_mask := io.mac2accu_mask            //|> o
    u_core.io.mac2accu_mode := io.mac2accu_mode            //|> o(
    u_core.io.mac2accu_data :=io. mac2accu_data         //|> o )
    u_core.io.mac2accu_pd := io.mac2accu_pd              //|> o
    u_core.io.reg2dp_op_en := reg2dp_op_en               //|< w
    u_core.io.reg2dp_conv_mode := reg2dp_conv_mode          //|< w
    u_core.io.dp2reg_done := dp2reg_done                   //|> w
    u_core.io.dla_clk_ovr_on_sync := io.dla_clk_ovr_on_sync           //|< i
    u_core.io.global_clk_ovr_on_sync := io.global_clk_ovr_on_sync        //|< i
    u_core.io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating //|< i
    u_core.io.slcg_op_en := slcg_op_en              //|< w

    //==========================================================
    // reg
    //==========================================================

    val reg2dp_proc_precision_NC = Wire(UInt(2.W))

    val u_reg = Module(new NV_NVDLA_CMAC_reg)

    u_reg.io.nvdla_core_clk := io.nvdla_core_clk                //|< i
    u_reg.io.nvdla_core_rstn := io.nvdla_core_rstn               //|< i
    u_reg.io.csb2cmac_a_req_pd := io.csb2cmac_a_req_pd       //|< i
    u_reg.io.csb2cmac_a_req_pvld := io.csb2cmac_a_req_pvld           //|< i
    u_reg.io.dp2reg_done := dp2reg_done                   //|< w
    u_reg.io.cmac_a2csb_resp_pd := io.cmac_a2csb_resp_pd      //|> o
    u_reg.io.cmac_a2csb_resp_valid := io.cmac_a2csb_resp_valid         //|> o
    u_reg.io.csb2cmac_a_req_prdy := io.csb2cmac_a_req_prdy           //|> o
    u_reg.io.reg2dp_conv_mode := reg2dp_conv_mode              //|> w
    u_reg.io.reg2dp_op_en := reg2dp_op_en                  //|> w
    u_reg.io.reg2dp_proc_precision := reg2dp_proc_precision_NC    //|> w  //dangle
    u_reg.io.slcg_op_en := slcg_op_en              //|> w



}