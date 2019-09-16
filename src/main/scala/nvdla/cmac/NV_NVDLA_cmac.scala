package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._


class NV_NVDLA_cmac(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_clock = Flipped(new nvdla_clock_if)
        val nvdla_core_rstn = Input(Bool())
        //csb
        val csb2cmac_a = new csb2dp_if
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
    
    //==========================================================
    // core
    //==========================================================
    //==========================================================
    // reg
    //==========================================================
    val u_core = Module(new NV_NVDLA_CMAC_core)
    val u_reg = Module(new NV_NVDLA_CMAC_reg)
    //clk
    u_core.io.nvdla_clock <> io.nvdla_clock         //|< b
    u_reg.io.nvdla_core_clk := io.nvdla_clock.nvdla_core_clk        //|< i
    u_core.io.slcg_op_en := u_reg.io.slcg_op_en           
    
    u_core.io.sc2mac_dat <> io.sc2mac_dat               //|< b
    u_core.io.sc2mac_wt <> io.sc2mac_wt         //|< b
    io.mac2accu <> u_core.io.mac2accu                 //|> b
      
    u_reg.io.dp2reg_done := u_core.io.dp2reg_done       //|< i
    u_reg.io.csb2cmac_a <> io.csb2cmac_a        //|< b

}}


object NV_NVDLA_cmacDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_cmac())
}
