package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.iotesters.Driver


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
        val mac2accu_mask = Output(Vec(conf.CMAC_ATOMK_HALF, Bool()))
        val mac2accu_mode = Output(Bool())
        val mac2accu_data = Output(Vec(conf.CMAC_ATOMK_HALF, UInt(conf.CMAC_RESULT_WIDTH.W)))
        val mac2accu_pd = Output(UInt(9.W))

        val sc2mac_dat_pvld = Input(Bool()) /* data valid */
        val sc2mac_dat_mask= Input(Vec(conf.CMAC_ATOMC, Bool()))
        val sc2mac_dat_data = Input(Vec(conf.CMAC_ATOMC, UInt(conf.CMAC_BPE.W)))
        val sc2mac_dat_pd = Input(UInt(9.W))

        val sc2mac_wt_pvld = Input(Bool()) /* data valid */
        val sc2mac_wt_mask= Input(Vec(conf.CMAC_ATOMC, Bool()))
        val sc2mac_wt_data = Input(Vec(conf.CMAC_ATOMC, UInt(conf.CMAC_BPE.W)))
        val sc2mac_wt_sel = Input(Vec(conf.CMAC_ATOMK_HALF, Bool()))

        //Port for SLCG
        val dla_clk_ovr_on_sync = Input(Clock())
        val global_clk_ovr_on_sync = Input(Clock())
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

withReset(!io.nvdla_core_rstn){
    
    val reg2dp_conv_mode = Wire(Bool())
    val reg2dp_op_en = Wire(Bool())
    val reg2dp_proc_precision = "b0".asUInt(2.W)
    val slcg_op_en = Wire(UInt(conf.CMAC_SLCG_NUM.W))

    //==========================================================
    // core
    //==========================================================

    val u_core = Module(new NV_NVDLA_CMAC_core)

    u_core.io.nvdla_core_clk := io.nvdla_core_clk               //|< i

    u_core.io.sc2mac_dat_pvld := io.sc2mac_dat_pvld               //|< i
    u_core.io.sc2mac_dat_mask := io.sc2mac_dat_mask        //|< i
    u_core.io.sc2mac_dat_data := io.sc2mac_dat_data        //|< i )
    u_core.io.sc2mac_dat_pd := io.sc2mac_dat_pd            //|< i

    u_core.io.sc2mac_wt_pvld := io.sc2mac_wt_pvld                //|< i
    u_core.io.sc2mac_wt_mask := io.sc2mac_wt_mask         //|< i
    u_core.io.sc2mac_wt_data := io.sc2mac_wt_data         //|< i )
    u_core.io.sc2mac_wt_sel := io.sc2mac_wt_sel            //|< i

    io.mac2accu_pvld := u_core.io.mac2accu_pvld                 //|> o
    io.mac2accu_mask := u_core.io.mac2accu_mask            //|> o
    io.mac2accu_mode := u_core.io.mac2accu_mode            //|> o(
    io.mac2accu_data := u_core.io.mac2accu_data         //|> o )
    io.mac2accu_pd := u_core.io.mac2accu_pd              //|> o

    u_core.io.reg2dp_op_en := reg2dp_op_en               //|< w
    u_core.io.reg2dp_conv_mode := reg2dp_conv_mode          //|< w
    val dp2reg_done = u_core.io.dp2reg_done                //|> w
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

    u_reg.io.csb2cmac_a_req_pd := io.csb2cmac_a_req_pd       //|< i
    u_reg.io.csb2cmac_a_req_pvld := io.csb2cmac_a_req_pvld           //|< i
    u_reg.io.dp2reg_done := dp2reg_done                   //|< w

    io.cmac_a2csb_resp_pd := u_reg.io.cmac_a2csb_resp_pd      //|> o
    io.cmac_a2csb_resp_valid := u_reg.io.cmac_a2csb_resp_valid         //|> o
    io.csb2cmac_a_req_prdy := u_reg.io.csb2cmac_a_req_prdy           //|> o

    reg2dp_conv_mode := u_reg.io.reg2dp_conv_mode              //|> w
    reg2dp_op_en := u_reg.io.reg2dp_op_en                  //|> w

    reg2dp_proc_precision_NC := u_reg.io.reg2dp_proc_precision    //|> w  //dangle
    slcg_op_en := u_reg.io.slcg_op_en              //|> w

}}


object NV_NVDLA_cmacDriver extends App {
  implicit val conf: cmacConfiguration = new cmacConfiguration
  chisel3.Driver.execute(args, () => new NV_NVDLA_cmac())
}
