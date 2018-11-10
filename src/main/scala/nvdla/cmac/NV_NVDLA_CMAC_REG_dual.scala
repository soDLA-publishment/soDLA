package nvdla

import chisel3._




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

    val nvdla_cmac_a_d_misc_cfg_0_wren = (reg_offset_wr === ("h700c".UInt(32.W)&"h00000fff".UInt(32.W)))&io.reg_wr_en
    val nvdla_cmac_a_d_op_enable_0_wren = (reg_offset_wr === ("h7008".UInt(32.W)&"h00000fff".UInt(32.W)))&io.reg_wr_en
    nvdla_cmac_a_d_misc_cfg_0_out := Cat("b0".UInt(18.W), proc_precision, "b0".UInt(11.W), conv_mode)
    nvdla_cmac_a_d_op_enable_0_outt:=  Cat("b0".UInt(31.W), op_en)

    reg_offset_rd_int := io.reg_offset

    when(reg_offset_rd_int === ("h700c".UInt(32.W)&"h00000fff".UInt(32.W)){
        io.reg_rd_data = nvdla_cmac_a_d_misc_cfg_0_out 
    }
    .elsewhen(reg_offset_rd_int === ("h7008".UInt(32.W)&"h00000fff".UInt(32.W)){
        io.reg_rd_data := nvdla_cmac_a_d_op_enable_0_out
    }
    .otherwise{
        io.reg_rd_data := "b0".UInt(32.W)
    }

    withClock(io.nvdla_core_clk){
        when(!nvdla_core_rstn){
            io.conv_mode := false.B
            io.proc_precision := "b01".UInt(2.W)
        }
        .otherwise{
            when(nvdla_cmac_a_s_pointer_0_wren){
                io.proc_precision:= io.reg_wr_data(0)
            }
            when(nvdla_cmac_a_d_op_enable_0_wren){
                io.proc_precision:=io.reg_wr_data(13,12)
            }
        }
    }

}