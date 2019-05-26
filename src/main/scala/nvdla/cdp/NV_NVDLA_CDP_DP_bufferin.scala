// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_CDP_DP_bufferin(implicit val conf: cdpConfiguration) extends Module {
//     val io = IO(new Bundle {
//         //clock
//         val nvdla_core_clk = Input(Clock())

//         //input:(atomk_half, cmac_result)
//         val out_data = Input(Vec(conf.CMAC_ATOMK_HALF, conf.CMAC_TYPE(conf.CMAC_RESULT_WIDTH.W)))
//         val out_mask = Input(Vec(conf.CMAC_ATOMK_HALF, Bool()))
//         val out_pd = Input(UInt(9.W))
//         val out_pvld = Input(Bool())

//         //output:(atomk_half, cmac_result)  
//         val mac2accu_data = Output(Vec(conf.CMAC_ATOMK_HALF, conf.CMAC_TYPE(conf.CMAC_RESULT_WIDTH.W)))
//         val mac2accu_mask = Output(Vec(conf.CMAC_ATOMK_HALF, Bool()))
//         val mac2accu_pd = Output(UInt(9.W))
//         val mac2accu_pvld = Output(Bool())

//         val dp2reg_done = Output(Bool())
//     })