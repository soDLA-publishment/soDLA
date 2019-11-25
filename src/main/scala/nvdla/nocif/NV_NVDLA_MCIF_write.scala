// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._


// class NV_NVDLA_MCIF_write(implicit conf: nvdlaConfig) extends Module {
//     val io = IO(new Bundle {
//         //general clock
//         val nvdla_core_clk = Input(Clock())
//         val pwrbus_ram_pd = Input(UInt(32.W))

//         val client2mcif_wr_req_pd = Flipped(Vec(conf.WDMA_NUM, DecoupledIO(conf.NVDLA_DMA_WR_REQ.W)))
//         val mcif2client_wr_rsp_complete = Output(Vec(conf.WDMA_NUM, Bool()))

//         //noc2mcif
//         val noc2mcif_axi_b_id = Flipped(DecoupledIO(UInt(8.W)))

//         val reg2dp_wr_os_cnt = Input(UInt(8.W))
//         val reg2dp_wr_weight_client = Input(Vec(conf.WDMA_NUM, Bool()))
//     })
// withClock()
// }}