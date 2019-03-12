package nvdla

import chisel3._
import chisel3.experimental._


class NV_NVDLA_CMAC_CORE_cfg(implicit val conf: cmacConfiguration) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())    

        //input
        val dp2reg_done = Input(Bool())
        val reg2dp_conv_mode = Input(Bool())
        val reg2dp_op_en = Input(Bool())

        //config
        val cfg_reg_en = Output(Bool())     
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

    withClock(io.nvdla_core_clk) { 

    val cfg_reg_en_w = Wire(Bool())
  
    val op_en_d1 = RegNext(io.reg2dp_op_en, false.B)
    val op_done_d1 = RegNext(io.dp2reg_done, false.B)
    io.cfg_reg_en := RegNext(cfg_reg_en_w, false.B)
    val cfg_reg_en_d1 = RegNext(io.cfg_reg_en, false.B)  

    cfg_reg_en_w := (~op_en_d1|op_done_d1) & io.reg2dp_op_en     

    
    }}