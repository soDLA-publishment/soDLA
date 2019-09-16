// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_WDMA_dat(implicit val conf: nvdlaConfig) extends Module {
//    val io = IO(new Bundle {
//         //in clock
//         val nvdla_core_clk = Input(Clock())

//         val cmd2dat_dma_pvld = Input(Bool())
//         val cmd2dat_dma_prdy = Output(Bool())
//         val cmd2dat_dma_pd = Input(UInt((conf.SDP_WR_CMD_DW+2).W))

//         val cmd2dat_spt_pvld = Input(Bool())
//         val cmd2dat_spt_prdy = Output(Bool())
//         val cmd2dat_spt_pd = Input(UInt(15.W))

//         val sdp_dp2wdma_valid = Input(Bool())
//         val sdp_dp2wdma_ready = Output(Bool())
//         val sdp_dp2wdma_pd = Input(UInt(conf.AM_DW.W))

//         val dma_wr_req_rdy = Input(Bool())
//         val op_load = Input(Bool())
//         val pwrbus_ram_pd = Input(UInt(32.W))
//         val reg2dp_batch_number = Input(UInt(5.W))
//         val reg2dp_ew_alu_algo = Input(UInt(2.W))
//         val reg2dp_ew_alu_bypass = Input(Bool())
//         val reg2dp_ew_bypass = Input(Bool())
//         val reg2dp_height = Input(UInt(13.W))
//         val reg2dp_interrupt_ptr = Input(Bool())
//         val reg2dp_out_precision = Input(UInt(2.W))
//         val reg2dp_output_dst = Input(Bool())
//         val reg2dp_proc_precision = Input(UInt(2.W))
//         val reg2dp_width = Input(UInt(13.W))
//         val reg2dp_winograd = Input(Bool())
//         val dma_wr_req_pd = Output(UInt(conf.NVDLA_DMA_WR_REQ.W))  
//         val dma_wr_req_vld = Output(Bool())
//         val dp2reg_done = Output(Bool())      
//         val dp2reg_status_nan_output_num = Output(UInt(32.W))  
//         val dp2reg_status_unequal = Output(Bool())
//         val intr_req_ptr = Output(Bool())
//         val intr_req_pvld = Output(Bool())
//     })

//     val dfifo_rd_pvld = Wire(Vec(4, Bool()))
//     val dfifo_rd_prdy = Wire(Vec(4, Bool()))
//     val dfifo_rd_pd = Wire(Vec(4, UInt(conf.AM_DW.W)))

//     val u_in = Module(new NV_NVDLA_SDP_WDMA_DAT_in)
//     u_in.io.nvdla_core_clk := io.nvdla_core_clk
//     u_in.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//     u_in.io.op_load := io.op_load
//     //cmd2dat
//     u_in.io.cmd2dat_spt_pvld := io.cmd2dat_spt_pvld
//     io.cmd2dat_spt_prdy := u_in.io.cmd2dat_spt_prdy
//     u_in.io.cmd2dat_spt_pd := io.cmd2dat_spt_pd
//     //sdp_dp2wdma
//     u_in.io.sdp_dp2wdma_valid := io.sdp_dp2wdma_valid
//     io.sdp_dp2wdma_ready := u_in.io.sdp_dp2wdma_ready
//     u_in.io.sdp_dp2wdma_pd := io.sdp_dp2wdma_pd
//     //out dfifo
//     dfifo_rd_pvld := u_in.io.dfifo_rd_pvld
//     u_in.io.dfifo_rd_prdy := dfifo_rd_prdy
//     dfifo_rd_pd := u_in.io.dfifo_rd_pd
//     //
//     u_in.io.reg2dp_batch_number := io.reg2dp_batch_number
//     u_in.io.reg2dp_height := io.reg2dp_height
//     u_in.io.reg2dp_out_precision := io.reg2dp_out_precision
//     u_in.io.reg2dp_proc_precision := io.reg2dp_proc_precision
//     u_in.io.reg2dp_width := io.reg2dp_width
//     u_in.io.reg2dp_winograd := io.reg2dp_winograd
//     io.dp2reg_status_nan_output_num := u_in.io.dp2reg_status_nan_output_num


//     val u_out = Module(new NV_NVDLA_SDP_WDMA_DAT_out)
//     u_out.io.nvdla_core_clk := io.nvdla_core_clk
//     u_out.io.op_load := io.op_load
//     //dma_wr
//     io.dma_wr_req_vld := u_out.io.dma_wr_req_vld
//     u_out.io.dma_wr_req_rdy := io.dma_wr_req_rdy
//     io.dma_wr_req_pd := u_out.io.dma_wr_req_pd
//     //cmd2dat_dma
//     u_out.io.cmd2dat_dma_pvld := io.cmd2dat_dma_pvld
//     io.cmd2dat_dma_prdy := u_out.io.cmd2dat_dma_prdy
//     u_out.io.cmd2dat_dma_pd := io.cmd2dat_dma_pd
//     //out dfifo
//     u_out.io.dfifo_rd_pvld := dfifo_rd_pvld
//     dfifo_rd_prdy := u_out.io.dfifo_rd_prdy
//     u_out.io.dfifo_rd_pd := dfifo_rd_pd
//     u_out.io.reg2dp_batch_number := io.reg2dp_batch_number
//     u_out.io.reg2dp_ew_alu_algo := io.reg2dp_ew_alu_algo
//     u_out.io.reg2dp_ew_alu_bypass := io.reg2dp_ew_alu_bypass
//     u_out.io.reg2dp_ew_bypass := io.reg2dp_ew_bypass
//     u_out.io.reg2dp_height := io.reg2dp_height
//     u_out.io.reg2dp_interrupt_ptr := io.reg2dp_interrupt_ptr
//     u_out.io.reg2dp_out_precision := io.reg2dp_out_precision
//     u_out.io.reg2dp_output_dst := io.reg2dp_output_dst
//     u_out.io.reg2dp_proc_precision := io.reg2dp_proc_precision
//     u_out.io.reg2dp_width := io.reg2dp_width
//     u_out.io.reg2dp_winograd := io.reg2dp_winograd
//     io.dp2reg_done := u_out.io.dp2reg_done
//     io.dp2reg_status_unequal := u_out.io.dp2reg_status_unequal
//     io.intr_req_ptr := u_out.io.intr_req_ptr
//     io.intr_req_pvld := u_out.io.intr_req_pvld           


// }


 
// object NV_NVDLA_SDP_WDMA_datDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_WDMA_dat())
// }


