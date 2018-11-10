package nvdla

import chisel3._




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
    io.reg_rd_data := Reg(UInt(32.W))

    reg_offset_wr := Cat(0.UInt(20.W), io.reg_offset)

    // Address decode

    val nvdla_cmac_a_s_pointer_0_wren = (reg_offset_wr === ("h7004".UInt(32.W)&"h00000fff".UInt(32.W)))&io.reg_wr_en
    val nvdla_cmac_a_s_status_0_wren = 




    
 

}