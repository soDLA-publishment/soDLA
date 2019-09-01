//TODO: need to repair
package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_CACC_dual_reg extends Module{
   val io = IO(new Bundle{
       // clk
       val nvdla_core_clk = Input(Clock())

       //Register control interface
       val reg = new reg_control_if

       //Writable register flop/trigger outputs
       val field = new cacc_dual_reg_flop_outputs
       val op_en_trigger = Output(Bool())

       //Read-only register inputs
       val op_en = Input(Bool())
       val sat_count = Input(UInt(32.W))
   })

   //      ┌─┐       ┌─┐
   //   ┌──┘ ┴───────┘ ┴──┐
   //   │                 │
   //   │       ───       │
   //   │  ─┬┘       └┬─  │
   //   │                 │
   //   │       ─┴─       │
   //   │                 │
   //   └───┐         ┌───┘
   //       │         │
   //       │         │
   //       │         │
   //       │         └──────────────┐
   //       │                        │
   //       │                        ├─┐
   //       │                        ┌─┘
   //       │                        │
   //       └─┐  ┐  ┌───────┬──┐  ┌──┘
   //         │ ─┤ ─┤       │ ─┤ ─┤
   //         └──┴──┘       └──┴──┘
   withClock(io.nvdla_core_clk){

   // Address decode
   val nvdla_cacc_d_batch_number_0_wren = (io.reg.offset === "h1c".asUInt(32.W)) & io.reg.wr_en
   val nvdla_cacc_d_clip_cfg_0_wren = (io.reg.offset === "h2c".asUInt(32.W)) & io.reg.wr_en
   val nvdla_cacc_d_cya_0_wren = (io.reg.offset === "h34".asUInt(32.W)) & io.reg.wr_en
   val nvdla_cacc_d_dataout_addr_0_wren = (io.reg.offset === "h18".asUInt(32.W)) & io.reg.wr_en
   val nvdla_cacc_d_dataout_map_0_wren = (io.reg.offset === "h28".asUInt(32.W)) & io.reg.wr_en
   val nvdla_cacc_d_dataout_size_0_0_wren = (io.reg.offset === "h10".asUInt(32.W)) & io.reg.wr_en
   val nvdla_cacc_d_dataout_size_1_0_wren = (io.reg.offset === "h14".asUInt(32.W)) & io.reg.wr_en
   val nvdla_cacc_d_line_stride_0_wren = (io.reg.offset === "h20".asUInt(32.W)) & io.reg.wr_en
   val nvdla_cacc_d_misc_cfg_0_wren = (io.reg.offset === "h0c".asUInt(32.W)) & io.reg.wr_en
   val nvdla_cacc_d_op_enable_0_wren = (io.reg.offset === "h08".asUInt(32.W)) & io.reg.wr_en
   val nvdla_cacc_d_out_saturation_0_wren = (io.reg.offset === "h30".asUInt(32.W)) & io.reg.wr_en
   val nvdla_cacc_d_surf_stride_0_wren = (io.reg.offset === "h24".asUInt(32.W)) & io.reg.wr_en

   io.op_en_trigger := nvdla_cacc_d_op_enable_0_wren

   //Output mux

   io.reg.rd_data := MuxLookup(io.reg.offset, "b0".asUInt(32.W),
   Seq(
   //nvdla_cacc_d_batch_number_0_out
   "h1c".asUInt(32.W)  -> Cat("b0".asUInt(27.W), io.field.batches),
   //nvdla_cacc_d_clip_cfg_0_out
   "h2c".asUInt(32.W)  -> Cat("b0".asUInt(27.W), io.field.clip_truncate),
   //nvdla_cacc_d_cya_0_out
   "h34".asUInt(32.W)  -> io.field.cya,
   //nvdla_cacc_d_dataout_addr_0_out
   "h18".asUInt(32.W)  -> io.field.dataout_addr,
   //nvdla_cacc_d_dataout_map_0_out
   "h28".asUInt(32.W)  -> Cat("b0".asUInt(15.W), io.field.surf_packed, "b0".asUInt(15.W), io.field.line_packed),
   //nvdla_cacc_d_dataout_size_0_0_out
   "h10".asUInt(32.W)  -> Cat("b0".asUInt(3.W), io.field.dataout_height, "b0".asUInt(3.W), io.field.dataout_width),
   //nvdla_cacc_d_dataout_size_1_0_out
   "h14".asUInt(32.W)  -> Cat("b0".asUInt(19.W), io.field.dataout_channel),
   //nvdla_cacc_d_line_stride_0_out
   "h20".asUInt(32.W)  -> Cat("b0".asUInt(8.W), io.field.line_stride),
   //nvdla_cacc_d_misc_cfg_0_out
   "h0c".asUInt(32.W)  -> Cat("b0".asUInt(18.W), io.field.proc_precision, "b0".asUInt(11.W), io.field.conv_mode),
   //nvdla_cacc_d_op_enable_0_out
   "h08".asUInt(32.W)  -> Cat("b0".asUInt(31.W), io.op_en),
   //nvdla_cacc_d_out_saturation_0_out
   "h30".asUInt(32.W)  -> io.sat_count,
   //nvdla_cacc_d_surf_stride_0_out
   "h24".asUInt(32.W)  -> Cat("b0".asUInt(8.W), io.field.surf_stride)
   ))

   //Register flop declarations
   io.field.batches := RegEnable(io.reg.wr_data(4, 0), "b0".asUInt(5.W), nvdla_cacc_d_batch_number_0_wren)
   // Register: NVDLA_CACC_D_CLIP_CFG_0    Field: clip_truncate
   io.field.clip_truncate := RegEnable(io.reg.wr_data(4, 0), "b0".asUInt(5.W), nvdla_cacc_d_clip_cfg_0_wren)
   // Register: NVDLA_CACC_D_CYA_0    Field: cya
   io.field.cya := RegEnable(io.reg.wr_data(31, 0), "b0".asUInt(32.W), nvdla_cacc_d_cya_0_wren)
   // Register: NVDLA_CACC_D_DATAOUT_ADDR_0    Field: dataout_addr
   io.field.dataout_addr := RegEnable(io.reg.wr_data(31, 0), "b0".asUInt(32.W), nvdla_cacc_d_dataout_addr_0_wren)
   // Register: NVDLA_CACC_D_DATAOUT_MAP_0    Field: line_packed
   io.field.line_packed := RegEnable(io.reg.wr_data(0), false.B, nvdla_cacc_d_dataout_map_0_wren)
   // Register: NVDLA_CACC_D_DATAOUT_MAP_0    Field: surf_packed
   io.field.surf_packed := RegEnable(io.reg.wr_data(16), false.B, nvdla_cacc_d_dataout_map_0_wren)
   // Register: NVDLA_CACC_D_DATAOUT_SIZE_0_0    Field: dataout_height
   io.field.dataout_height := RegEnable(io.reg.wr_data(28, 16), "b0".asUInt(13.W), nvdla_cacc_d_dataout_size_0_0_wren)
   // Register: NVDLA_CACC_D_DATAOUT_SIZE_0_0    Field: dataout_width
   io.field.dataout_width := RegEnable(io.reg.wr_data(12, 0), "b0".asUInt(13.W), nvdla_cacc_d_dataout_size_0_0_wren)
   // Register: NVDLA_CACC_D_DATAOUT_SIZE_1_0    Field: dataout_channel
   io.field.dataout_channel := RegEnable(io.reg.wr_data(12, 0), "b0".asUInt(13.W), nvdla_cacc_d_dataout_size_1_0_wren)
   // Register: NVDLA_CACC_D_LINE_STRIDE_0    Field: line_stride
   io.field.line_stride := RegEnable(io.reg.wr_data(23, 0), "b0".asUInt(24.W), nvdla_cacc_d_line_stride_0_wren)
   // Register: NVDLA_CACC_D_MISC_CFG_0    Field: conv_mode
   io.field.conv_mode := RegEnable(io.reg.wr_data(0), false.B, nvdla_cacc_d_misc_cfg_0_wren)
   // Register: NVDLA_CACC_D_MISC_CFG_0    Field: proc_precision
   io.field.proc_precision := RegEnable(io.reg.wr_data(13, 12), "b01".asUInt(2.W), nvdla_cacc_d_misc_cfg_0_wren)
   // Register: NVDLA_CACC_D_SURF_STRIDE_0    Field: surf_stride
   io.field.surf_stride := RegEnable(io.reg.wr_data(23, 0), "b0".asUInt(24.W), nvdla_cacc_d_surf_stride_0_wren)

}}