// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_partition_p(implicit val conf: nvdlaConfig) extends Module {
//     val io = IO(new Bundle {
//         //general clock
//         val dla_core_clk = Input(Bool())
//         val dla_csb_clk = Input(Bool())
//         val global_clk_ovr_on = Input(Clock())
//         val tmc2slcg_disable_clock_gating = Input(Bool())
//         val dla_reset_rstn = Input(Bool())
//         val direct_reset_ = Input(Bool())
//         val test_mode = Input(Bool())
//         //csb2nvdla
//         val csb2nvdla = Flipped(DecoupledIO(new csb2nvdla_if))
//         val nvdla2csb = ValidIO(new nvdla2csb_if)
//         val nvdla2csb_wr_complete = Output(Bool())
//         ///////////////
//         //axi
//         //2dbb
//         val nvdla_core2dbb_aw = DecoupledIO(new nocif_axi_wr_address_if)
//         val nvdla_core2dbb_w = DecoupledIO(new nocif_axi_wr_data_if)
//         val nvdla_core2dbb_b = Flipped(DecoupledIO(new nocif_axi_wr_response_if))
//         val nvdla_core2dbb_ar = Flipped(DecoupledIO(new nocif_axi_rd_address_if))
//         val nvdla_core2dbb_r = Flipped(DecoupledIO(new nocif_axi_rd_data_if)
//         //2cvsram
//         val nvdla_core2cvsram_aw = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(new nocif_axi_wr_address_if)) else None
//         val nvdla_core2cvsram_w = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(new nocif_axi_wr_data_if)) else None
//         val nvdla_core2cvsram_b = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(new nocif_axi_wr_response_if))) else None
//         val nvdla_core2cvsram_ar = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(new nocif_axi_rd_address_if))) else None
//         val nvdla_core2cvsram_r = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(new nocif_axi_rd_data_if)) else None
//         //pwr_ram_pd
//         val nvdla_pwrbus_ram_c_pd = Input(UInt(32.W))
//         val nvdla_pwrbus_ram_ma_pd = Input(UInt(32.W))
//         val nvdla_pwrbus_ram_mb_pd = Input(UInt(32.W))
//         val nvdla_pwrbus_ram_p_pd = Input(UInt(32.W))
//         val nvdla_pwrbus_ram_o_pd = Input(UInt(32.W))
//         val nvdla_pwrbus_ram_a_pd = Input(UInt(32.W))
//         val dla_intr = Output(Bool())
//     }
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

// if(NVDLA_SECONDARY_MEMIF_ENABLE){
//     io.nvdla_core2cvsram_aw.valid := false.B
//     io.
// }

// ////////////////////////////////////////////////////////////////////////
// //  NVDLA Partition O                                                 //
// ////////////////////////////////////////////////////////////////////////
//     val u_partition_o = Module(new NV_NVDLA_partition_o)
//     u_partition_o

// ////////////////////////////////////////////////////////////////////////
// //  NVDLA Partition C                                                 //
// ////////////////////////////////////////////////////////////////////////

// }


// object NV_NVDLADriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA())
// }
