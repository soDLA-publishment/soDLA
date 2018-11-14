package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._




class NV_NVDLA_CMAC_REG_single(implicit val conf: cmacConfiguration) extends Module {
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

        val producer = Output(Bool())

        // Read-only register inputs

        val consumer = Input(Bool())
        val status_0 = Input(UInt(2.W))
        val status_1 = Input(UInt(2.W))       
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

    val nvdla_cmac_a_s_pointer_0_out = Wire(UInt(32.W))
    val nvdla_cmac_a_s_status_0_out = Wire(UInt(32.W))
    val reg_offset_rd_int = Wire(UInt(12.W))
    val reg_offset_wr = Wire(UInt(32.W))

    io.producer := Reg(Bool())
    io.reg_rd_data := Wire(UInt(32.W))

    reg_offset_wr := Cat("b0".asUInt(20.W), io.reg_offset)

    // Address decode

    val nvdla_cmac_a_s_pointer_0_wren = (reg_offset_wr === ("h7004".asUInt(32.W)&"h00000fff".asUInt(32.W)))&io.reg_wr_en
    val nvdla_cmac_a_s_status_0_wren = (reg_offset_wr === ("h7000".asUInt(32.W)&"h00000fff".asUInt(32.W)))&io.reg_wr_en
    nvdla_cmac_a_s_pointer_0_out := Cat("b0".asUInt(15.W), io.consumer, "b0".asUInt(15.W), io.producer)
    nvdla_cmac_a_s_status_0_out:=  Cat("b0".asUInt(14.W), io.status_1, "b0".asUInt(14.W), io.status_0)

    reg_offset_rd_int := io.reg_offset

    when(reg_offset_rd_int === ("h7004".asUInt(32.W)&"h00000fff".asUInt(32.W))){
        io.reg_rd_data = nvdla_cmac_a_s_pointer_0_out 
    }
    .elsewhen(reg_offset_rd_int === ("h7000".asUInt(32.W)&"h00000fff".asUInt(32.W))){
        io.reg_rd_data := nvdla_cmac_a_s_status_0_out
    }
    .otherwise{
        io.reg_rd_data := "b0".asUInt(32.W)
    }

    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){
        when(nvdla_cmac_a_s_pointer_0_wren){
            io.producer:= io.reg_wr_data(0)
        }
    }


}