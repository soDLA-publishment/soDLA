package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_CDP_DP_mul(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        val intp2mul_pd = Flipped(DecoupledIO(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(17.W))))
        val sync2mul_pd = Flipped(DecoupledIO(UInt((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE).W)))
        val mul2ocvt_pd = DecoupledIO(UInt((conf.NVDLA_CDP_THROUGHPUT*(conf.NVDLA_CDP_ICVTO_BWPE+16)).W))

        val reg2dp_mul_bypass = Input(Bool())
        
    })
//     
//          ┌─┐       ┌─┐
//       ┌──┘ ┴───────┘ ┴──┐
//       │                 │
//       │       ───       │
//       │  ─┬┘       └┬─  │
//       │                 │
//       │       ─┴─       │
//       │                 │
//       └───┐         ┌───┘
//           │         │
//           │         │
//           │         │
//           │         └──────────────┐
//           │                        │
//           │                        ├─┐
//           │                        ┌─┘    
//           │                        │
//           └─┐  ┐  ┌───────┬──┐  ┌──┘         
//             │ ─┤ ─┤       │ ─┤ ─┤         
//             └──┴──┘       └──┴──┘ 
withClock(io.nvdla_core_clk){

////////////////////////////////////////////////////////////////////////////////////////

    val mul2ocvt_pvld_f = Wire(Bool())
    val mul2ocvt_pd_f = Wire(UInt((conf.NVDLA_CDP_THROUGHPUT*(conf.NVDLA_CDP_ICVTO_BWPE+16)).W))
    val pipe_p1 = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_CDP_THROUGHPUT*(conf.NVDLA_CDP_ICVTO_BWPE+16)))
    pipe_p1.io.clk := io.nvdla_core_clk
    pipe_p1.io.vi := mul2ocvt_pvld_f
    val mul2ocvt_prdy_f = pipe_p1.io.ro
    pipe_p1.io.di := mul2ocvt_pd_f
    io.mul2ocvt_pd.valid := pipe_p1.io.vo
    pipe_p1.io.ri := io.mul2ocvt_pd.ready
    io.mul2ocvt_pd.bits := pipe_p1.io.dout

    val mul_bypass_en = RegInit(false.B)
    mul_bypass_en := io.reg2dp_mul_bypass === 1.U

    //interlock two path data 
    val mul_in_rdy = Wire(Bool())
    io.intp2mul_pd.ready := Mux(mul_bypass_en, mul2ocvt_prdy_f, mul_in_rdy) & io.sync2mul_pd.valid
    io.sync2mul_pd.ready := Mux(mul_bypass_en, mul2ocvt_prdy_f, mul_in_rdy) & io.intp2mul_pd.valid

    val mul_in_vld = Mux(mul_bypass_en, false.B, (io.sync2mul_pd.valid & io.intp2mul_pd.valid))
    val mul_rdy = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))
    mul_in_rdy := mul_rdy.asUInt.andR

    val mul_vld = VecInit((0 until conf.NVDLA_CDP_THROUGHPUT) map {i => mul_in_vld & mul_in_rdy})
    val mul_inb_pd = VecInit((0 until conf.NVDLA_CDP_THROUGHPUT) map 
        {i => io.intp2mul_pd.bits(i)(15, 0)})
    val mul_ina_pd = VecInit((0 until conf.NVDLA_CDP_THROUGHPUT) map
        {i => io.sync2mul_pd.bits((i + 1) * conf.NVDLA_CDP_ICVTO_BWPE - 1, i * conf.NVDLA_CDP_ICVTO_BWPE)})

    val mul_unit_vld = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))
    val mul_unit_rdy = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))
    val mul_unit_pd = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt((conf.NVDLA_CDP_ICVTO_BWPE+16).W)))

    val u_mul_unit = Array.fill(conf.NVDLA_CDP_THROUGHPUT){Module(new NV_NVDLA_CDP_DP_MUL_unit)}
    for(i <- 0 until conf.NVDLA_CDP_THROUGHPUT){
        u_mul_unit(i).io.nvdla_core_clk := io.nvdla_core_clk
        u_mul_unit(i).io.mul_vld := mul_vld(i)
        mul_rdy(i) := u_mul_unit(i).io.mul_rdy
        u_mul_unit(i).io.mul_ina_pd := mul_ina_pd(i)
        u_mul_unit(i).io.mul_inb_pd := mul_inb_pd(i)
        mul_unit_vld(i) := u_mul_unit(i).io.mul_unit_vld
        u_mul_unit(i).io.mul_unit_rdy := mul_unit_rdy(i)
        mul_unit_pd(i) := u_mul_unit(i).io.mul_unit_pd
    }

    for(i <- 0 until conf.NVDLA_CDP_THROUGHPUT){
        mul_unit_rdy(i) := mul2ocvt_prdy_f & (mul_unit_vld.asUInt.andR)
    }

///////////////////
//NaN propagation for mul_bypass condition
///////////////////
    val intp_out_ext = VecInit((0 to conf.NVDLA_CDP_THROUGHPUT-1) 
                map {i => Cat(Fill((conf.NVDLA_CDP_ICVTO_BWPE+16-17), io.intp2mul_pd.bits(i)(16)), io.intp2mul_pd.bits(i))}).asUInt
    

    mul2ocvt_pd_f := Mux(mul_bypass_en, intp_out_ext, mul_unit_pd.asUInt)

    //output select
    mul2ocvt_pvld_f := Mux(mul_bypass_en, (io.sync2mul_pd.valid & io.intp2mul_pd.valid), (mul_unit_vld.asUInt.andR))

}}


object NV_NVDLA_CDP_DP_mulDriver extends App {
    implicit val conf: nvdlaConfig = new nvdlaConfig
    chisel3.Driver.execute(args, () => new NV_NVDLA_CDP_DP_mul())
}
