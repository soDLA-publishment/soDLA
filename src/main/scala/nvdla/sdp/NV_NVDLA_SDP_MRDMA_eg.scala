// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_MRDMA_eg extends Module {
//    val io = IO(new Bundle {
//         //clk
//         val nvdla_core_clk = Input(Clock())
//         val pwrbus_ram_pd = Input(UInt(32.W))
//         val op_load = Input(Bool())
//         val eg_done = Output(Bool())

//         //cq2eg 
//         val cq2eg_pvld = Input(Bool())
//         val cq2eg_prdy = Output(Bool())
//         val cq2eg_pd = Input(UInt(14.W))
   
//         //dma_rd
//         val dma_rd_rsp_vld = Input(Bool())
//         val dma_rd_rsp_rdy = Output(Bool())
//         val dma_rd_cdt_lat_fifo_pop = Output(Bool())
//         val dma_rd_rsp_ram_type = Output(Bool())
//         val dma_rd_rsp_pd = Input(UInt((conf.NVDLA_DMA_RD_RSP).W))

//         //sdp_mrdma2cmux
//         val sdp_mrdma2cmux_valid = Output(Bool())
//         val sdp_mrdma2cmux_ready = Input(Bool())
//         val sdp_mrdma2cmux_pd = Output(UInt((conf.DP_DIN_DW+2).W))

//         //reg2dp
//         val reg2dp_src_ram_type = Input(Bool())
//         val reg2dp_height = Input(UInt(13.W))
//         val reg2dp_width = Input(UInt(13.W))
//         val reg2dp_in_precision = Input(UInt(2.W))
//         val reg2dp_proc_precision = Input(UInt(2.W))
//         val reg2dp_perf_nan_inf_count_en = Input(Bool())
//         val dp2reg_status_inf_input_num = Output(UInt(32.W))
//         val dp2reg_status_nan_input_num = Output(UInt(32.W))
//     })
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
// withClock(io.nvdla_core_clk)

//     val cmd2dat_spt_pvld = Wire(Bool())
//     val cmd2dat_spt_prdy = Wire(Bool())
//     val cmd2dat_spt_pd = Wire(UInt(13.W))

//     val cmd2dat_dma_pvld = Wire(Bool())
//     val cmd2dat_dma_prdy = Wire(Bool())
//     val cmd2dat_dma_pd = Wire(UInt(15.W))

//     val u_cmd = Module(new NV_NVDLA_SDP_MRDMA_EG_cmd)
//     u_cmd.io.nvdla_core_clk := io.nvdla_core_clk
//     u_cmd.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//     u_cmd.io.eg_done := io.eg_done
//     u_cmd.io.cq2eg_pvld := io.cq2eg_pvld
//     io.cq2eg_prdy := u_cmd.io.cq2eg_prdy
//     u_cmd.io.cq2eg_pd := io.cq2eg_pd
//     cmd2dat_spt_pvld := u_cmd.io.cmd2dat_spt_pvld
//     u_cmd.io.cmd2dat_spt_prdy := cmd2dat_spt_prdy
//     cmd2dat_spt_pd := u_cmd.io.cmd2dat_spt_pd
//     cmd2dat_dma_pvld := u_cmd.io.cmd2dat_dma_pvld
//     u_cmd.io.cmd2dat_dma_prdy := cmd2dat_dma_prdy
//     cmd2dat_dma_pd := u_cmd.io.cmd2dat_dma_pd
//     u_cmd.io.reg2dp_in_precision := io.reg2dp_in_precision
//     u_cmd.io.reg2dp_proc_precision := io.reg2dp_proc_precision
//     u_cmd.io.reg2dp_height := io.reg2dp_height
//     u_cmd.io.reg2dp_width := io.reg2dp_width

//     val u_din = Module(new NV_NVDLA_SDP_MRDMA_EG_din)
//     u_din
//     input          nvdla_core_clk;
// input          nvdla_core_rstn;
// input   [31:0] pwrbus_ram_pd;
// input          reg2dp_src_ram_type;
// output         dma_rd_rsp_ram_type;
// input  [NVDLA_DMA_RD_RSP-1:0] dma_rd_rsp_pd;
// input          dma_rd_rsp_vld;
// output         dma_rd_rsp_rdy;
// output         dma_rd_cdt_lat_fifo_pop;
// input   [12:0] cmd2dat_spt_pd;
// input          cmd2dat_spt_pvld;
// output         cmd2dat_spt_prdy;
// input          pfifo0_rd_prdy;
// input          pfifo1_rd_prdy;
// input          pfifo2_rd_prdy;
// input          pfifo3_rd_prdy;
// output [AM_DW-1:0] pfifo0_rd_pd;
// output         pfifo0_rd_pvld;
// output [AM_DW-1:0] pfifo1_rd_pd;
// output         pfifo1_rd_pvld;
// output [AM_DW-1:0] pfifo2_rd_pd;
// output         pfifo2_rd_pvld;
// output [AM_DW-1:0] pfifo3_rd_pd;
// output         pfifo3_rd_pvld;








// }}