// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_CDP_DP_MUL_unit(implicit val conf: nvdlaConfig) extends Module {
//     val pINA_BW = 9
//     val pINB_BW = 16
//     val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())
//         val mul_vld = Input(Bool())
//         val mul_rdy = Output(Bool())
//         val mul_ina_pd = Input(UInt(pINA_BW.W))
//         val mul_inb_pd = Input(UInt(pINA_BW.W))
//         val mul_unit_vld = Output(Bool())
//         val mul_unit_rdy = Input(Bool())
//         val mul_unit_pd = Output(UInt((pINA_BW+pINB_BW).W))
//     })

// withClock(io.nvdla_core_clk){

// ////////////////////////////////////////////////////////////////////////////////////////
//     io.mul_rdy := ~io.mul_unit_vld | io.mul_unit_rdy
//     val mul_unit_pd_out = RegInit(0.U((pINA_BW+pINB_BW).W))
//     when(io.mul_vld & io.mul_rdy){
//         mul_unit_pd_out := (io.mul_inb_pd.asSInt * io.mul_ina_pd.asSInt).asUInt
//     }
//     io.mul_unit_pd := mul_unit_pd_out

//     val mul_unit_vld_out = RegInit(false.B)
//     when(io.mul_vld){
//         mul_unit_vld_out := true.B
//     }.elsewhen(io.mul_unit_rdy){
//         mul_unit_vld_out := false.B
//     }
//     io.mul_unit_vld := mul_unit_vld_out

// }}


// object NV_NVDLA_CDP_DP_MUL_unitDriver extends App {
//     implicit val conf: nvdlaConfig = new nvdlaConfig
//     chisel3.Driver.execute(args, () => new NV_NVDLA_CDP_DP_MUL_unit())
// }
