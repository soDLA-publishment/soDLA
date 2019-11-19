// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._


// class NV_NVDLA_XXIF_WRITE_cq(vec_num: Int, width: Int)(implicit conf:nvdlaConfig) extends Module {
//     val io = IO(new Bundle {
//         //general clock
//         val nvdla_core_clk = Input(Clock())      

//         //cq_wr
//         val cq_wr_pd = Flipped(DecoupledIO(UInt(width.W)))
//         val cq_wr_thread_id = Input(UInt(log2Ceil(vec_num).W))
        
//         //cq_rd
//         val cq_rd_pd = Vec(vec_num, DecoupledIO(UInt(width.W)))

//         val pwrbus_ram_pd = Input(UInt(32.W))
//     })

// withClock(io.nvdla_core_clk){
//     // -rd_take_to_rd_busy internal credit/take/data signals (which would have been ports)
//     val cq_rd_take = Wire(Bool())
//     val cq_rd_pd_p = Wire(UInt(width.W))
//     val cq_rd_take_thread_id = Wire(UInt(log2Ceil(vec_num).W))

//     // We also declare some per-thread flags that indicate whether to have the write bypass the internal fifo.
//     // These per-class wr_bypassing* flags are set by the take-side logic.  We basically pretend that we never pushed the fifo,
//     // but make sure we return a credit to the sender.
//     val wr_bypassing = Wire(Bool()) // any thread bypassed

//     // Master Clock Gating (SLCG)
//     //
//     // We gate the clock(s) when idle or stalled.
//     // This allows us to turn off numerous miscellaneous flops
//     // that don't get gated during synthesis for one reason or another.
//     //
//     // We gate write side and read side separately. 
//     // If the fifo is synchronous, we also gate the ram separately, but if
//     // -master_clk_gated_unified or -status_reg/-status_logic_reg is specified, 
//     // then we use one clk gate for write, ram, and read.
//     //



// }}
 



