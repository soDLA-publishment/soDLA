// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_WDMA_intr(implicit val conf: nvdlaConfig) extends Module {
//    val io = IO(new Bundle {
//         //in clock
//         val nvdla_core_clk = Input(Clock())
//         val pwrbus_ram_pd = Input(UInt(32.W))
//         val op_load = Input(Bool())

//         val dma_wr_req_rdy = Input(Bool())
//         val dma_wr_req_vld = Input(Bool())
//         val dma_wr_rsp_complete = Input(Bool())

//         val intr_req_ptr = Input(Bool())
//         val intr_req_pvld = Input(Bool())

//         val reg2dp_ew_alu_algo = Input(UInt(2.W))
//         val reg2dp_ew_alu_bypass = Input(Bool())
//         val reg2dp_ew_bypass = Input(Bool())
//         val reg2dp_op_en = Input(Bool())
//         val reg2dp_output_dst = Input(Bool())
//         val reg2dp_perf_dma_en = Input(Bool())
//         val dp2reg_wdma_stall = Output(UInt(32.W))
//         val sdp2glb_done_intr_pd = Output(UInt(2.W))
//     })
// //     
// //          ┌─┐       ┌─┐
// //       ┌──┘ ┴───────┘ ┴──┐
// //       │                 │
// //       │       ───       │          
// //       │  ─┬┘       └┬─  │
// //       │                 │
// //       │       ─┴─       │
// //       │                 │
// //       └───┐         ┌───┘
// //           │         │
// //           │         │
// //           │         │
// //           │         └──────────────┐
// //           │                        │
// //           │                        ├─┐
// //           │                        ┌─┘    
// //           │                        │
// //           └─┐  ┐  ┌───────┬──┐  ┌──┘         
// //             │ ─┤ ─┤       │ ─┤ ─┤         
// //             └──┴──┘       └──┴──┘ 
// withClock(io.nvdla_core_clk){
//     //============================
//     // CFG
//     //============================
//     val cfg_mode_eql = (io.reg2dp_ew_bypass === 0.U) & (io.reg2dp_ew_alu_bypass === 0.U) & (io.reg2dp_ew_alu_algo === 3.U)
//     val cfg_mode_pdp = (io.reg2dp_output_dst === 1.U)
//     val cfg_mode_quite = cfg_mode_eql | cfg_mode_pdp

//     //==============
//     // Interrupt
//     //==============
//     val u_NV_NVDLA_SDP_WDMA_DAT_DMAIF_intr_fifo = Module(new NV_NVDLA_SDP_fifo_no_depth(1))
//     u_NV_NVDLA_SDP_WDMA_DAT_DMAIF_intr_fifo.io.clk := io.nvdla_core_clk
//     u_NV_NVDLA_SDP_WDMA_DAT_DMAIF_intr_fifo.io.wr_pvld := io.intr_req_pvld & !cfg_mode_quite
//     u_NV_NVDLA_SDP_WDMA_DAT_DMAIF_intr_fifo.io.wr_pd := io.intr_req_ptr
//     val intr_fifo_rd_pvld = u_NV_NVDLA_SDP_WDMA_DAT_DMAIF_intr_fifo.io.rd_pvld
//     u_NV_NVDLA_SDP_WDMA_DAT_DMAIF_intr_fifo.io.rd_prdy := io.dma_wr_rsp_complete
//     val intr_fifo_rd_pd = u_NV_NVDLA_SDP_WDMA_DAT_DMAIF_intr_fifo.io.rd_pd
//     u_NV_NVDLA_SDP_WDMA_DAT_DMAIF_intr_fifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd

//     val intr0_internal = cfg_mode_quite & io.intr_req_pvld & (io.intr_req_ptr === false.B)
//     val intr0_wr  = io.dma_wr_rsp_complete & (intr_fifo_rd_pd === false.B)

//     val intr1_internal = cfg_mode_quite & io.intr_req_pvld & (io.intr_req_ptr === true.B)
//     val intr1_wr  = io.dma_wr_rsp_complete & (intr_fifo_rd_pd === true.B)

//     val sdp2glb_done_intr_pd_0 = RegInit(false.B)
//     val sdp2glb_done_intr_pd_1 = RegInit(false.B)
//     sdp2glb_done_intr_pd_0 := intr0_wr | intr0_internal
//     sdp2glb_done_intr_pd_1 := intr1_wr | intr1_internal
//     io.sdp2glb_done_intr_pd := Cat(sdp2glb_done_intr_pd_1, sdp2glb_done_intr_pd_0)

//     // val mon_intr_fifo_rd_pvld = intr_fifo_rd_pvld
//     //==============
//     // PERF STATISTIC
//     val wdma_stall_cnt_inc = io.dma_wr_req_vld & !io.dma_wr_req_rdy
//     val wdma_stall_cnt_clr = io.op_load
//     val wdma_stall_cnt_cen = io.reg2dp_op_en & io.reg2dp_perf_dma_en  

//     val dp2reg_wdma_stall_dec = false.B

//     val stl_adv = wdma_stall_cnt_inc ^ dp2reg_wdma_stall_dec

//     val stl_cnt_cur = RegInit(0.U(32.W))
//     val stl_cnt_ext = Cat(false.B, false.B, stl_cnt_cur)
//     val stl_cnt_inc = Wire(UInt(34.W))
//     stl_cnt_inc := stl_cnt_cur +& 1.U; // spyglass disable W164b
//     val stl_cnt_dec = Wire(UInt(34.W))
//     stl_cnt_dec := stl_cnt_cur -& 1.U; // spyglass disable W164b
//     val stl_cnt_mod = Mux(wdma_stall_cnt_inc && !dp2reg_wdma_stall_dec, stl_cnt_inc, 
//                       Mux(!wdma_stall_cnt_inc && dp2reg_wdma_stall_dec, stl_cnt_dec, 
//                       stl_cnt_ext))   
//     val stl_cnt_new = Mux(stl_adv, stl_cnt_mod, stl_cnt_ext)
//     val stl_cnt_nxt = Mux(wdma_stall_cnt_clr, 0.U, stl_cnt_new)

//     when(wdma_stall_cnt_cen){
//         stl_cnt_cur := stl_cnt_nxt(31,0)
//     }

//     io.dp2reg_wdma_stall := stl_cnt_cur
            
// }}

 
// object NV_NVDLA_SDP_WDMA_intrDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_WDMA_intr())
// }



