package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._


class NV_NVDLA_cmac(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())  
        val nvdla_core_rstn = Input(Bool())
        //Port for SLCG
        val dla_clk_ovr_on_sync = Input(Clock())
        val global_clk_ovr_on_sync = Input(Clock())
        val tmc2slcg_disable_clock_gating = Input(Bool())

        //csb
        val cmac_a2csb_resp_valid = Output(Bool())  /* data valid */
        val cmac_a2csb_resp_pd = Output(UInt(34.W))/* pkt_id_width=1 pkt_widths=33,33  */

        val csb2cmac_a_req_pvld = Input(Bool())/* data valid */
        val csb2cmac_a_req_prdy = Output(Bool())/* data return handshake */
        val csb2cmac_a_req_pd = Input(UInt(63.W))

        //odif
        val mac2accu = ValidIO(new cmac2cacc_if) /* data valid */
        val sc2mac_dat = Flipped(ValidIO(new csc2cmac_data_if))  /* data valid */
        val sc2mac_wt = Flipped(ValidIO(new csc2cmac_wt_if))    /* data valid */
        
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

    u_core.io.sc2mac_dat_pvld := io.sc2mac_dat.valid               //|< i
    u_core.io.sc2mac_dat_mask := io.sc2mac_dat.bits.mask        //|< i
    u_core.io.sc2mac_dat_data := io.sc2mac_dat.bits.data        //|< i )
    u_core.io.sc2mac_dat_pd := Cat(io.sc2mac_dat.bits.layer_end, io.sc2mac_dat.bits.channel_end, io.sc2mac_dat.bits.stripe_end,
                                   io.sc2mac_dat.bits.stripe_st, io.sc2mac_dat.bits.batch_index)       //|< i

    u_core.io.sc2mac_wt_pvld := io.sc2mac_wt.valid                //|< i
    u_core.io.sc2mac_wt_mask := io.sc2mac_wt.bits.mask         //|< i
    u_core.io.sc2mac_wt_data := io.sc2mac_wt.bits.data         //|< i )
    u_core.io.sc2mac_wt_sel := io.sc2mac_wt.bits.sel            //|< i

    io.mac2accu.valid := u_core.io.mac2accu_pvld                 //|> o
    io.mac2accu.bits.mask := u_core.io.mac2accu_mask            //|> o
    io.mac2accu.bits.mode := u_core.io.mac2accu_mode            //|> o(
    io.mac2accu.bits.data := u_core.io.mac2accu_data         //|> o )
    io.mac2accu.bits.batch_index := u_core.io.mac2accu_pd(4, 0)              //|> o
    io.mac2accu.bits.stripe_st := u_core.io.mac2accu_pd(5)              //|> o
    io.mac2accu.bits.stripe_end := u_core.io.mac2accu_pd(6)              //|> o
    io.mac2accu.bits.channel_end := u_core.io.mac2accu_pd(7)              //|> o
    io.mac2accu.bits.layer_end := u_core.io.mac2accu_pd(8)              //|> o

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
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_cmac())
}
