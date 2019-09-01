// package nvdla

// import chisel3._
// import chisel3.util._
// import chisel3.experimental._

// class NV_NVDLA_SDP_wdma(implicit conf: nvdlaConfig) extends Module {

// val io = IO(new Bundle {
//     //in clock
//     val nvdla_clock = Flipped(new nvdla_clock_if)
//     val pwrbus_ram_pd = Input(UInt(32.W))

//     //cvif
//     val sdp2cvif_wr_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_WR_REQ.W))) else None
//     val cvif2sdp_wr_rsp_complete = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None

//     val sdp2mcif_wr_req_pd = DecoupledIO(UInt(conf.NVDLA_DMA_WR_REQ.W))
//     val mcif2sdp_wr_rsp_complete = Input(Bool())  

//     val sdp_dp2wdma_pd = Flipped(DecoupledIO(UInt(conf.DP_DOUT_DW.W)))

//     val reg2dp_batch_number = Input(UInt(5.W))
//     val reg2dp_channel = Input(UInt(13.W))
//     val reg2dp_dst_base_addr_high = Input(UInt(32.W))
//     val reg2dp_dst_base_addr_low = Input(UInt((32-conf.AM_AW).W))
//     val reg2dp_dst_batch_stride = Input(UInt((32-conf.AM_AW).W))
//     val reg2dp_dst_line_stride = Input(UInt((32-conf.AM_AW).W))
//     val reg2dp_dst_ram_type = Input(Bool())
//     val reg2dp_dst_surface_stride = Input(UInt((32-conf.AM_AW).W))
//     val reg2dp_ew_alu_algo = Input(UInt(2.W))
//     val reg2dp_ew_alu_bypass = Input(Bool())
//     val reg2dp_ew_bypass = Input(Bool())
//     val reg2dp_height = Input(UInt(13.W))
//     val reg2dp_interrupt_ptr = Input(Bool())
//     val reg2dp_op_en = Input(Bool())
//     val reg2dp_out_precision = Input(UInt(2.W))
//     val reg2dp_output_dst = Input(Bool())
//     val reg2dp_perf_dma_en = Input(Bool())
//     val reg2dp_proc_precision = Input(UInt(2.W))
//     val reg2dp_wdma_slcg_op_en = Input(Bool())
//     val reg2dp_width = Input(UInt(13.W))
//     val reg2dp_winograd = Input(Bool())
//     val dp2reg_done = Output(Bool())
//     val dp2reg_status_nan_output_num = Output(UInt(32.W))
//     val dp2reg_status_unequal = Output(Bool())
//     val dp2reg_wdma_stall = Output(UInt(32.W))

//     val sdp2glb_done_intr_pd = Output(UInt(2.W))

// })

//     //     
//     //          ┌─┐       ┌─┐
//     //       ┌──┘ ┴───────┘ ┴──┐
//     //       │                 │
//     //       │       ───       │          
//     //       │  ─┬┘       └┬─  │
//     //       │                 │
//     //       │       ─┴─       │
//     //       │                 │
//     //       └───┐         ┌───┘
//     //           │         │
//     //           │         │
//     //           │         │
//     //           │         └──────────────┐
//     //           │                        │
//     //           │                        ├─┐
//     //           │                        ┌─┘    
//     //           │                        │
//     //           └─┐  ┐  ┌───────┬──┐  ┌──┘         
//     //             │ ─┤ ─┤       │ ─┤ ─┤         
//     //             └──┴──┘       └──┴──┘ 
// withClock(io.nvdla_core_clk){
//     //==============
//     // Start Processing
//     //==============
//     val processing = RegInit(false.B)
//     val op_load = io.reg2dp_op_en & !processing
//     when(op_load){
//         processing := true.B
//     }
//     .elsewhen(io.dp2reg_done){
//         processing := false.B
//     }

//     //=======================================
//     val u_gate = Module(new NV_NVDLA_slcg(1, false))
//     u_gate.io.nvdla_clock := io.nvdla_clock
//     u_gate.io.slcg_en(0) := io.reg2dp_wdma_slcg_op_en
//     val nvdla_gated_clk = u_gate.io.nvdla_core_gated_clk 

//     val cmd2dat_spt_prdy = Wire(Bool())
//     val cmd2dat_dma_prdy = Wire(Bool())
//     val u_cmd = Module(new NV_NVDLA_SDP_WDMA_cmd)
//     u_cmd.io.nvdla_core_clk := nvdla_gated_clk
//     val cmd2dat_spt_pvld = u_cmd.io.cmd2dat_spt_pvld
//     u_cmd.io.cmd2dat_spt_prdy := cmd2dat_spt_prdy
//     val cmd2dat_spt_pd = u_cmd.io.cmd2dat_spt_pd
//     val cmd2dat_dma_pvld = u_cmd.io.cmd2dat_dma_pvld
//     u_cmd.io.cmd2dat_dma_prdy := cmd2dat_dma_prdy
//     val cmd2dat_dma_pd = u_cmd.io.cmd2dat_dma_pd
//     u_cmd.io.reg2dp_batch_number := io.reg2dp_batch_number
//     u_cmd.io.reg2dp_channel := io.reg2dp_channel
//     u_cmd.io.reg2dp_dst_base_addr_high := io.reg2dp_dst_base_addr_high
//     u_cmd.io.reg2dp_dst_base_addr_low := io.reg2dp_dst_base_addr_low
//     u_cmd.io.reg2dp_dst_batch_stride := io.reg2dp_dst_batch_stride 
//     u_cmd.io.reg2dp_dst_line_stride := io.reg2dp_dst_line_stride
//     u_cmd.io.reg2dp_dst_surface_stride := io.reg2dp_dst_surface_stride
//     u_cmd.io.reg2dp_ew_alu_algo := io.reg2dp_ew_alu_algo
//     u_cmd.io.reg2dp_ew_alu_bypass := io.reg2dp_ew_alu_bypass
//     u_cmd.io.reg2dp_ew_bypass := io.reg2dp_ew_bypass
//     u_cmd.io.reg2dp_height := io.reg2dp_height
//     u_cmd.io.reg2dp_out_precision := io.reg2dp_out_precision
//     u_cmd.io.reg2dp_output_dst := io.reg2dp_output_dst
//     u_cmd.io.reg2dp_proc_precision := io.reg2dp_proc_precision
//     u_cmd.io.reg2dp_width := io.reg2dp_width
//     u_cmd.io.reg2dp_winograd := io.reg2dp_winograd
//     u_cmd.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//     u_cmd.io.op_load := op_load

//     val dma_wr_req_rdy = Wire(Bool())
//     val u_dat = Module(new NV_NVDLA_SDP_WDMA_dat)
//     u_dat.io.nvdla_core_clk := nvdla_gated_clk
//     u_dat.io.cmd2dat_dma_pvld := cmd2dat_dma_pvld
//     cmd2dat_dma_prdy := u_dat.io.cmd2dat_dma_prdy
//     u_dat.io.cmd2dat_dma_pd := cmd2dat_dma_pd
//     u_dat.io.cmd2dat_spt_pvld := cmd2dat_spt_pvld
//     cmd2dat_spt_prdy := u_dat.io.cmd2dat_spt_prdy
//     u_dat.io.cmd2dat_spt_pd := cmd2dat_spt_pd
//     u_dat.io.sdp_dp2wdma_valid := io.sdp_dp2wdma_valid 
//     io.sdp_dp2wdma_ready := u_dat.io.sdp_dp2wdma_ready
//     u_dat.io.sdp_dp2wdma_pd := io.sdp_dp2wdma_pd
//     val dma_wr_req_vld = u_dat.io.dma_wr_req_vld
//     u_dat.io.dma_wr_req_rdy := dma_wr_req_rdy
//     val dma_wr_req_pd = u_dat.io.dma_wr_req_pd
//     u_dat.io.reg2dp_batch_number := io.reg2dp_batch_number
//     u_dat.io.reg2dp_ew_alu_algo := io.reg2dp_ew_alu_algo
//     u_dat.io.reg2dp_ew_alu_bypass := io.reg2dp_ew_alu_bypass
//     u_dat.io.reg2dp_ew_bypass := io.reg2dp_ew_bypass
//     u_dat.io.reg2dp_height := io.reg2dp_height
//     u_dat.io.reg2dp_interrupt_ptr := io.reg2dp_interrupt_ptr 
//     u_dat.io.reg2dp_out_precision := io.reg2dp_out_precision
//     u_dat.io.reg2dp_output_dst := io.reg2dp_output_dst
//     u_dat.io.reg2dp_proc_precision := io.reg2dp_proc_precision
//     u_dat.io.reg2dp_width := io.reg2dp_width
//     u_dat.io.reg2dp_winograd := io.reg2dp_winograd
//     io.dp2reg_done := u_dat.io.dp2reg_done
//     io.dp2reg_status_nan_output_num := u_dat.io.dp2reg_status_nan_output_num
//     io.dp2reg_status_unequal := u_dat.io.dp2reg_status_unequal
//     val intr_req_ptr = u_dat.io.intr_req_ptr
//     val intr_req_pvld = u_dat.io.intr_req_pvld
//     u_dat.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//     u_dat.io.op_load := op_load

//     val u_dmaif_wr = Module(new NV_NVDLA_DMAIF_wr)
//     u_dmaif_wr.io.nvdla_core_clk := io.nvdla_core_clk
//     u_dmaif_wr.io.reg2dp_dst_ram_type := io.reg2dp_dst_ram_type
//     if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
//         io.sdp2cvif_wr_req_valid.get := u_dmaif_wr.io.cvif_wr_req_valid.get
//         u_dmaif_wr.io.cvif_wr_req_ready.get := io.sdp2cvif_wr_req_ready.get
//         io.sdp2cvif_wr_req_pd.get := u_dmaif_wr.io.cvif_wr_req_pd.get
//         u_dmaif_wr.io.cvif_wr_rsp_complete.get := io.cvif2sdp_wr_rsp_complete.get
//     }
//     io.sdp2mcif_wr_req_valid := u_dmaif_wr.io.mcif_wr_req_valid 
//     u_dmaif_wr.io.mcif_wr_req_ready := io.sdp2mcif_wr_req_ready
//     io.sdp2mcif_wr_req_pd := u_dmaif_wr.io.mcif_wr_req_pd
//     u_dmaif_wr.io.mcif_wr_rsp_complete := io.mcif2sdp_wr_rsp_complete
//     u_dmaif_wr.io.dmaif_wr_req_pvld := dma_wr_req_vld
//     dma_wr_req_rdy := u_dmaif_wr.io.dmaif_wr_req_prdy
//     u_dmaif_wr.io.dmaif_wr_req_pd := dma_wr_req_pd
//     val dma_wr_rsp_complete = u_dmaif_wr.io.dmaif_wr_rsp_complete

//     val u_intr = Module(new NV_NVDLA_SDP_WDMA_intr)
//     u_intr.io.nvdla_core_clk := io.nvdla_core_clk
//     u_intr.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//     u_intr.io.op_load := op_load
//     u_intr.io.dma_wr_req_vld := dma_wr_req_vld
//     u_intr.io.dma_wr_req_rdy := dma_wr_req_rdy
//     u_intr.io.dma_wr_rsp_complete := dma_wr_rsp_complete
//     u_intr.io.intr_req_ptr := intr_req_ptr
//     u_intr.io.intr_req_pvld := intr_req_pvld
//     u_intr.io.reg2dp_ew_alu_algo := io.reg2dp_ew_alu_algo
//     u_intr.io.reg2dp_ew_alu_bypass := io.reg2dp_ew_alu_bypass
//     u_intr.io.reg2dp_ew_bypass := io.reg2dp_ew_bypass
//     u_intr.io.reg2dp_op_en := io.reg2dp_op_en
//     u_intr.io.reg2dp_output_dst := io.reg2dp_output_dst
//     u_intr.io.reg2dp_perf_dma_en := io.reg2dp_perf_dma_en
//     io.dp2reg_wdma_stall := u_intr.io.dp2reg_wdma_stall
//     io.sdp2glb_done_intr_pd := u_intr.io.sdp2glb_done_intr_pd
// }}

// object NV_NVDLA_SDP_wdmaDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_wdma())
// }


