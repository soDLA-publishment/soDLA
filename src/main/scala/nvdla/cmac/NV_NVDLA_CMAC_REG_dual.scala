package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._




class NV_NVDLA_CMAC_REG_dual(implicit val conf: cmacConfiguration) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())      
        val nvdla_core_rstn = Input(Bool())

        // Register control interface
        val reg_rd_data = Output(UInt(32.W))
        val reg_offset = Input(UInt(12.W))
        val reg_wr_data = Input(UInt(32.W))//(UNUSED_DEC)

        val reg_wr_en = Input(Bool())

        // Writable register flop/trigger outputs

        val conv_mode = Output(Bool())
        val proc_precision = Output(UInt(2.W))
        val op_en_trigger = Output(Bool())

        // Read-only register inputs

        val op_en = Input(Bool())    
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

    io.conv_mode := Reg(Bool())
    io.proc_precision := Reg(UInt(2.W))
    io.reg_rd_data := Wire(UInt(32.W))

    

    // Address decode

    val nvdla_cmac_a_d_misc_cfg_0_wren = (reg_offset_wr === ("h700c".asUInt(32.W)&"h00000fff".asUInt(32.W)))&io.reg_wr_en
    val nvdla_cmac_a_d_op_enable_0_wren = (reg_offset_wr === ("h7008".asUInt(32.W)&"h00000fff".asUInt(32.W)))&io.reg_wr_en
    nvdla_cmac_a_d_misc_cfg_0_out := Cat("b0".asUInt(18.W), io.proc_precision, "b0".asUInt(11.W), io.conv_mode)
    nvdla_cmac_a_d_op_enable_0_out:=  Cat("b0".asUInt(31.W), io.op_en)

    val reg_offset_rd_int = io.reg_offset

    when(reg_offset_rd_int === ("h700c".asUInt(32.W)&"h00000fff".asUInt(32.W))){
        io.reg_rd_data := nvdla_cmac_a_d_misc_cfg_0_out 
    }
    .elsewhen(reg_offset_rd_int === ("h7008".asUInt(32.W)&"h00000fff".asUInt(32.W))){
        io.reg_rd_data := nvdla_cmac_a_d_op_enable_0_out
    }
    .otherwise{
        io.reg_rd_data := "b0".asUInt(32.W)
    }

    withClock(io.nvdla_core_clk|(!io.nvdla_core_rstn.asBool())){
        when(!io.nvdla_core_rstn){
            io.conv_mode := false.B
            io.proc_precision := "b01".asUInt(2.W)
        }
        .otherwise{
            when(nvdla_cmac_a_s_pointer_0_wren){
                io.conv_mode:= io.reg_wr_data(0)
            }
            when(nvdla_cmac_a_d_op_enable_0_wren){
                io.proc_precision:=io.reg_wr_data(13,12)
            }
        }
    }

}