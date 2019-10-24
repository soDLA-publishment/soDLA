// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._


// class NV_NVDLA_MCIF_WRITE_cq(implicit conf:nvdlaConfig) extends Module {
//     val io = IO(new Bundle {
//         //general clock
//         val nvdla_core_clk = Input(Clock())      
//         val nvdla_core_rstn = Input(Bool())
//         //cq_wr
//         val cq_wr_prdy = Output(Bool())
//         val cq_wr_pvld = Input(Bool())
//         val cq_wr_thread_id = Input(UInt(3.W))
//         val cq_wr_pause = if(conf.FV_RAND_WR_PAUSE) Some(Input(Bool())) else None
//         val cq_wr_pd = Input(UInt(3.W))
        
//         //cq_rd
//         val cq_rd_pd = Vec(5, DecoupledIO(UInt(3.W)))

//         val pwrbus_ram_pd = Input(UInt(32.W))
//     })

//     withClock(io.nvdla_core_clk){
//     }
// }





