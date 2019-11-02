package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_PDP_CORE_CAL2D_add_pad_v(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())

        val pout_mem_size_v = Input(UInt(3.W))
        val pooling_size_v_cfg = Input(UInt(3.W))
        val average_pooling_en = Input(UInt(3.W))
        val reg2dp_kernel_width = Input(UInt(3.W))
        val reg2dp_pad_value_1x_cfg = Input(UInt(19.W))
        val reg2dp_pad_value_2x_cfg = Input(UInt(19.W))
        val reg2dp_pad_value_3x_cfg = Input(UInt(19.W))
        val reg2dp_pad_value_4x_cfg = Input(UInt(19.W))
        val reg2dp_pad_value_5x_cfg = Input(UInt(19.W))
        val reg2dp_pad_value_6x_cfg = Input(UInt(19.W))
        val reg2dp_pad_value_7x_cfg = Input(UInt(19.W))

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
withClock(io.nvdla_core_clk){
//===========================================================
//adding pad value in v direction
//-----------------------------------------------------------
//padding value 1x,2x,3x,4x,5x,6x,7x table
    val pout_mem_size_v_use =  io.pout_mem_size_v
    val padding_here = io.average_pooling_en & (pout_mem_size_v_use =/= io.pooling_size_v_cfg)
    val pad_table_index = io.pooling_size_v_cfg - pout_mem_size_v_use

    val pad_table_out = MuxLookup(pad_table_index, "b0".asUInt(19.W),
                        Array(
                            1.U -> io.reg2dp_pad_value_1x_cfg,
                            2.U -> io.reg2dp_pad_value_2x_cfg,
                            3.U -> io.reg2dp_pad_value_3x_cfg,
                            4.U -> io.reg2dp_pad_value_4x_cfg,
                            5.U -> io.reg2dp_pad_value_5x_cfg,
                            6.U -> io.reg2dp_pad_value_6x_cfg,
                            7.U -> io.reg2dp_pad_value_7x_cfg,
                        ))
    
    val kernel_width_cfg = io.reg2dp_kernel_width +& 1.U

    val pad_value = (pad_table_out.asSInt * Cat(false.B, kernel_width_cfg).asSInt).asUInt
}}