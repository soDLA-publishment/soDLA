// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_CDP_DP_INTP_unit extends Module {
//     val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val interp_in_vld = Input(Bool())
//         val interp_in_rdy = Output(Bool())
//         val interp_in0_pd = Input(UInt(39.W))
//         val interp_in1_pd = Input(UInt(38.W))
//         val interp_in_pd = Input(UInt(17.W))
//         val interp_in_scale = Input(UInt(17.W))
//         val interp_in_shift = Input(UInt(6.W))

//         val interp_out_vld = Output(Bool())
//         val interp_out_rdy = Input(Bool())
//         val interp_out_pd = Output(UInt(17.W))
        
//     })

// withClock(io.nvdla_core_clk){
//     /////////////////////////////////////////////////////////////////
//     ///////////////////////////////////////////
//     //interp_in_vld
//     val int_in_rdy = Wire(Bool())
//     io.interp_in_rdy := int_in_rdy 
//     ///////////////////////////////////////////

//     val int_vld_d0 = RegInit(false.B)
//     val int_rdy_d0 = Wire(Bool())
//     val int_in_vld = io.interp_in_vld;
//     int_in_rdy := !int_vld_d0 | int_rdy_d0;
//     val int_in_load = int_in_vld & int_in_rdy;

//     ///////////////////
//     //X1-X0
//     val int_sub = RegInit(0.U(40.W))
//     val interp_in0_pd_d0 = RegInit(0.U(17.W))
//     val interp_in_offset_d0 = RegInit(0.U(17.W))
//     val interp_in_shift_d0 = RegInit(0.U(6.W))
//     when(int_in_load){
//         int_sub := ((Cat(io.interp_in1_pd(37), io.interp_in1_pd(37,0))).asSInt -& io.interp_in0_pd.asSInt).asUInt
//         interp_in0_pd_d0 := io.interp_in_pd
//         interp_in_offset_d0 := io.interp_in_scale
//         interp_in_shift_d0 := io.interp_in_shift
//     }

//     when(int_in_vld){
//         int_vld_d0 := true.B
//     }.elsewhen(int_rdy_d0){
//         int_vld_d0 := false.B
//     }

//     val int_vld_d1 = RegInit(false.B)
//     val int_rdy_d1 = Wire(Bool())
//     int_rdy_d0 := !int_vld_d1 | int_rdy_d1
//     val int_in_load_d0 = int_vld_d0 & int_rdy_d0
//     ///////////////////
//     //(X1-X0)*frac

//     val int_mul = RegInit(0.U(57.W))
//     val interp_in0_pd_d1 = RegInit(0.U(17.W))
//     val interp_in_shift_d1 = RegInit(0.U(6.W))
//     when(int_in_load_d0){
//         int_mul := (int_sub.asSInt * interp_in_offset_d0.asSInt).asUInt
//         interp_in0_pd_d1 := interp_in0_pd_d0
//         interp_in_shift_d1 := interp_in_shift_d0
//     }

//     //>>16 proc for ((X1-X0)*frac) >>16
//     val intp_in_shift_inv = ~interp_in_shift_d1(4,0)
//     val intp_in_shift_inv_inc = intp_in_shift_inv +& 1.U
//     val interp_in_shift_abs = Mux(interp_in_shift_d1(5), intp_in_shift_inv_inc, interp_in_shift_d1)
//     val int_mul_shift_int_and_frac = Mux(interp_in_shift_d1(5), 
//                                         (Cat(Fill(31, int_mul(56)), int_mul(56,0), 0.U(32.W)) << interp_in_shift_abs),
//                                         (Cat(Fill(31, int_mul(56)), int_mul(56,0), 0.U(32.W)) >> interp_in_shift_abs)
//                                         )
//     val int_mul_shift_int = int_mul_shift_int_and_frac(118,32)
//     val int_mul_shift_frac = int_mul_shift_int_and_frac(31,0)

//     //rounding process for right shift
//     val int_mul_for_Rshift = Wire(UInt(58.W))

//     when(int_mul_shift_int(56)){
//         when(int_mul_shift_frac(31)){
//             when(!(int_mul_shift_frac(30,0).orR)){
//                 int_mul_for_Rshift := Cat(int_mul_shift_int(56), int_mul_shift_int(56,0))
//             }.otherwise{
//                 int_mul_for_Rshift := ((int_mul_shift_int(56,0)).asSInt +& (Cat(0.U(56.W), true.B)).asSInt).asUInt
//             }
//         }.otherwise{
//             int_mul_for_Rshift := Cat(int_mul_shift_int(56), int_mul_shift_int(56,0))
//         }
//     }.otherwise{
//         int_mul_for_Rshift := ((int_mul_shift_int(56,0)).asSInt +& (Cat(0.U(56.W),int_mul_shift_frac(31))).asSInt).asUInt
//     }
//     val int_mul_rs = Mux(interp_in_shift_d1(5), int_mul_shift_int, 
//                      Cat(Fill(30, int_mul_for_Rshift(57)), int_mul_for_Rshift))
    
//     when(int_vld_d0){
//         int_vld_d1 := true.B
//     }.elsewhen(int_rdy_d1){
//         int_vld_d1 := false.B
//     }

//     val int_vld_d2 = RegInit(false.B)
//     val int_rdy_d2 = Wire(Bool())
//     int_rdy_d1 := !int_vld_d2 | int_rdy_d2
//     val int_in_load_d1 = int_vld_d1 & int_rdy_d1

//     //Xo = X0+[(X1-X0)*frac>>16]
//     val int_add = RegInit(0.U(89.W))
//     when(int_in_load_d1){
//         int_add := (int_mul_rs.asSInt +& (Cat(Fill(71, interp_in0_pd_d1(16)), interp_in0_pd_d1(16,0))).asSInt).asUInt
//     }

//     val int_interp_out_pd = Mux(int_add(88), 
//                             Mux(int_add(88,15).andR, Cat(int_add(88),int_add(14,0)), 
//                             "h8000".asUInt(16.W)),
//                             Mux(int_add(88,15).orR, "h7fff".asUInt(16.W),
//                             int_add(15,0)))
//     when(int_vld_d1){
//         int_vld_d2 := true.B
//     }.elsewhen(int_rdy_d2){
//         int_vld_d2 := false.B
//     }

//     int_rdy_d2 := io.interp_out_rdy

//     //////////////////////////////////////////////////////////////////////////////////////////////
//     //////////////////////////////////////////////////////////////////////////////////////////////

//     io.interp_out_vld := int_vld_d2
//     io.interp_out_pd := Cat(int_interp_out_pd(15), int_interp_out_pd(15,0))

// }}


// object NV_NVDLA_CDP_DP_INTP_unitDriver extends App {
//   chisel3.Driver.execute(args, () => new NV_NVDLA_CDP_DP_INTP_unit())
// }
