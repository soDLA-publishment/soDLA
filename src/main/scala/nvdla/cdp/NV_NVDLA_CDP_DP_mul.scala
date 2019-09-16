// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_CDP_DP_mul(implicit val conf: nvdlaConfig) extends Module {
//     val pINA_BW = 9
//     val pINB_BW = 16
//     val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val intp2mul_pvld = Input(Bool())
//         val intp2mul_prdy = Output(Bool())
//         val intp2mul_pd = Input(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(17.W)))

//         val sync2mul_pvld = Input(Bool())
//         val sync2mul_prdy = Output(Bool())
//         val sync2mul_pd = Input(UInt((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE).W))
        
//         val mul2ocvt_pvld = Output(Bool())
//         val mul2ocvt_prdy = Input(Bool())
//         val mul2ocvt_pd = Output(UInt((conf.NVDLA_CDP_THROUGHPUT*(conf.NVDLA_CDP_ICVTO_BWPE+16)).W))

//         val reg2dp_mul_bypass = Input(Bool())
        
//     })

// withClock(io.nvdla_core_clk){

// ////////////////////////////////////////////////////////////////////////////////////////

//     val mul2ocvt_pvld_f = Wire(Bool())
//     val mul2ocvt_pd_f = Wire(UInt((conf.NVDLA_CDP_THROUGHPUT*(conf.NVDLA_CDP_ICVTO_BWPE+16)).W))

//     val pipe_p1 = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_CDP_THROUGHPUT*(conf.NVDLA_CDP_ICVTO_BWPE+16)))
//     pipe_p1.io.clk := io.nvdla_core_clk
//     pipe_p1.io.vi := mul2ocvt_pvld_f
//     val mul2ocvt_prdy_f = pipe_p1.io.ro
//     pipe_p1.io.di := mul2ocvt_pd_f
//     io.mul2ocvt_pvld := pipe_p1.io.vo
//     pipe_p1.io.ri := io.mul2ocvt_prdy
//     io.mul2ocvt_pd := pipe_p1.io.dout

//     val mul_bypass_en = RegInit(false.B)
//     mul_bypass_en := io.reg2dp_mul_bypass === 1.U

//     //interlock two path data 
//     val mul_in_rdy = Wire(Bool())
//     io.intp2mul_prdy := Mux(mul_bypass_en, mul2ocvt_prdy_f, mul_in_rdy) & io.sync2mul_pvld
//     io.sync2mul_prdy := Mux(mul_bypass_en, mul2ocvt_prdy_f, mul_in_rdy) & io.intp2mul_pvld

//     val mul_in_vld = Mux(mul_bypass_en, false.B, (io.sync2mul_pvld & io.intp2mul_pvld))
//     val mul_rdy = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))
//     mul_in_rdy := mul_rdy.asUInt.andR

//     val mul_vld = VecInit((0 until conf.NVDLA_CDP_THROUGHPUT) map {i => mul_in_vld & mul_in_rdy})
//     val mul_inb_pd = VecInit((0 until conf.NVDLA_CDP_THROUGHPUT) map {i => io.intp2mul_pd(i)})
//     val mul_ina_pd = VecInit((0 until conf.NVDLA_CDP_THROUGHPUT) map
//         {i => io.sync2mul_pd((i + 1) * conf.NVDLA_CDP_ICVTO_BWPE - 1, i * conf.NVDLA_CDP_ICVTO_BWPE)})

//     val mul_unit_vld = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))
//     val mul_unit_rdy = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))
//     val mul_unit_pd = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt((conf.NVDLA_CDP_ICVTO_BWPE+16).W)))

//     val u_mul_unit = Array.fill(conf.NVDLA_CDP_THROUGHPUT){Module(new NV_NVDLA_CDP_DP_MUL_unit)}
//     for(i <- 0 until conf.NVDLA_CDP_THROUGHPUT){
//         u_mul_unit(i).io.nvdla_core_clk := io.nvdla_core_clk
//         u_mul_unit(i).io.mul_vld := mul_vld(i)
//         mul_rdy(i) := u_mul_unit(i).io.mul_rdy
//         u_mul_unit(i).io.mul_ina_pd := mul_ina_pd(i)
//         u_mul_unit(i).io.mul_inb_pd := mul_inb_pd(i)
//         mul_unit_vld(i) := u_mul_unit(i).io.mul_unit_vld
//         u_mul_unit(i).io.mul_unit_rdy := mul_unit_rdy(i)
//         mul_unit_pd(i) := u_mul_unit(i).io.mul_unit_pd
//     }

//     for(i <- 0 until conf.NVDLA_CDP_THROUGHPUT){
//         mul_unit_rdy(i) := mul2ocvt_prdy_f & (mul_unit_vld.asUInt.andR)
//     }

// ///////////////////
// //NaN propagation for mul_bypass condition
// ///////////////////
// // NVDLA_CDP_THROUGHPUT = 8
//     val intp_out_ext = Cat(
//          Cat(Fill((conf.NVDLA_CDP_ICVTO_BWPE+16-17), io.intp2mul_pd(7)(16)), io.intp2mul_pd(7)),
         
//          Cat(Fill((conf.NVDLA_CDP_ICVTO_BWPE+16-17), io.intp2mul_pd(6)(16)), io.intp2mul_pd(6)),
         
//          Cat(Fill((conf.NVDLA_CDP_ICVTO_BWPE+16-17), io.intp2mul_pd(5)(16)), io.intp2mul_pd(5)),
         
//          Cat(Fill((conf.NVDLA_CDP_ICVTO_BWPE+16-17), io.intp2mul_pd(4)(16)), io.intp2mul_pd(4)),
         
//          Cat(Fill((conf.NVDLA_CDP_ICVTO_BWPE+16-17), io.intp2mul_pd(3)(16)), io.intp2mul_pd(3)),
         
//          Cat(Fill((conf.NVDLA_CDP_ICVTO_BWPE+16-17), io.intp2mul_pd(2)(16)), io.intp2mul_pd(2)),
         
//          Cat(Fill((conf.NVDLA_CDP_ICVTO_BWPE+16-17), io.intp2mul_pd(1)(16)), io.intp2mul_pd(1)),

//          Cat(Fill((conf.NVDLA_CDP_ICVTO_BWPE+16-17), io.intp2mul_pd(0)(16)), io.intp2mul_pd(0))  
//         )



//     mul2ocvt_pd_f := Mux(
//         mul_bypass_en, intp_out_ext, 
//         Cat(mul_unit_pd(7),mul_unit_pd(6),mul_unit_pd(5),mul_unit_pd(4),mul_unit_pd(3),mul_unit_pd(2),mul_unit_pd(1), mul_unit_pd(0))
//         )

//     //output select

//     mul2ocvt_pvld_f := Mux(mul_bypass_en, (io.sync2mul_pvld & io.intp2mul_pvld), (mul_unit_vld.asUInt.andR))

// }}


// object NV_NVDLA_CDP_DP_mulDriver extends App {
//     implicit val conf: nvdlaConfig = new nvdlaConfig
//     chisel3.Driver.execute(args, () => new NV_NVDLA_CDP_DP_mul())
// }
