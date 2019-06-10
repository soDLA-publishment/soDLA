// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_CDP_DP_bufferin(implicit val conf: cdpConfiguration) extends Module {
//     val cvt2buf_data_bw = conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE
//     val cvt2buf_info_bw = 15
//     val cvt2buf_dp_bw = cvt2buf_data_bw + cvt2buf_info_bw

//     val io = IO(new Bundle {
//         //clock
//         val nvdla_core_clk = Input(Clock())

//         //cdp_rdma2dp
//         val cdp_rdma2dp_valid = Input(Bool())
//         val cdp_rdma2dp_ready = Output(Bool())
//         val cdp_rdma2dp_pd = Input(UInt((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE+17).W))

//         //normalz_buf
//         val normalz_buf_data_pvld = Output(Bool())
//         val normalz_buf_data_prdy = Input(Bool())
//         val normalz_buf_data = Output(UInt(((conf.NVDLA_CDP_THROUGHPUT+8)*conf.NVDLA_CDP_ICVTO_BWPE+17).W))

//         val dp2reg_done = Output(Bool())
//     })

//     /////////////////////////////////////////////////////////////

