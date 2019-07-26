// package nvdla


// import chisel3._
// import chisel3.experimental._
// import chisel3.util._


// class int_sum_block extends Module {
//     val io = IO(new Bundle {
//         //nvdla core clock
//         val nvdla_core_clk = Input(Clock())

//         //control signal
//         val len5 = Input(Bool())
//         val len7 = Input(Bool())
//         val len9 = Input(Bool())
//         val load_din_d = Input(Bool())
//         val load_din_2d = Input(Bool())
//         val reg2dp_normalz_len = Input(UInt(2.W))

//         //sq_pd as a input
//         val sq_pd_int8_lsb = Input(Vec(9, UInt(17.W)))
//         val sq_pd_int8_msb = Input(Vec(9, UInt(17.W)))

//         //output signal
//         val int8_sum = Output(UInt(42.W))
//     })

// withClock(io.nvdla_core_clk){

//     val int16_sum3 = RegInit("b0".asUInt(35.W))
//     val int16_sum5 = RegInit("b0".asUInt(36.W))
//     val int16_sum7 = RegInit("b0".asUInt(36.W))
//     val int16_sum9 = RegInit("b0".asUInt(37.W))
//     val int16_sum_0_8 = RegInit("b0".asUInt(34.W))
//     val int16_sum_1_7 = RegInit("b0".asUInt(34.W))
//     val int16_sum_2_6 = RegInit("b0".asUInt(34.W))
//     val int16_sum_3_5 = RegInit("b0".asUInt(34.W))
//     val int8_lsb_sum = Wire(UInt(21.W))
//     val int8_msb_sum = Wire(UInt(21.W))

//     val int8_msb_sum3 = RegInit("b0".asUInt(19.W))
//     val int8_msb_sum5 = RegInit("b0".asUInt(20.W))
//     val int8_msb_sum7 = RegInit("b0".asUInt(20.W))
//     val int8_msb_sum9 = RegInit("b0".asUInt(21.W))

//     val sq4_d = RegInit("b0".asUInt(33.W))
//     val sq_pd_int8_msb_4_d = RegInit("b0".asUInt(17.W))

//     val int8_lsb_sum3 = Wire(UInt(19.W))
//     val int8_lsb_sum5 = Wire(UInt(20.W))
//     val int8_lsb_sum7 = Wire(UInt(20.W))
//     val int8_lsb_sum9 = Wire(UInt(21.W))


//    //sum process
//     when(io.load_din_d){
//         int8_msb_sum_3_5 := io.sq_pd_int8_msb(3) +& io.sq_pd_int8_msb(5)
//     }
//     when(io.load_din_d & (io.len5|io.len7|io.len9)){
//         int8_msb_sum_2_6 := io.sq_pd_int8_msb(2) +& io.sq_pd_int8_msb(6)
//     }
//     when(io.load_din_d & (io.len7|io.len9)){
//         int8_msb_sum_1_7 := io.sq_pd_int8_msb(1) +& io.sq_pd_int8_msb(7)
//     }
//     when(io.load_din_d & (io.len9)){
//         int8_msb_sum_0_8 := io.sq_pd_int8_msb(0) +& io.sq_pd_int8_msb(8)
//     }

//     val int16_sum_0_8 = RegInit("b0".asUInt(34.W))
//     val int16_sum_1_7 = RegInit("b0".asUInt(34.W))
//     val int16_sum_2_6 = RegInit("b0".asUInt(34.W))
//     val int16_sum_3_5 = RegInit("b0".asUInt(34.W))
//     val sq = VecInit((0 to 8) map { i => Cat("b0".asUInt(16.W), sq_pd_int8_lsb(i))})
//     val sq4_d = RegInit("b0".asUInt(33.W))

//     when(io.load_din_d){
//         int16_sum_3_5:= sq(3) +& sq(5)
//     }
//     when(io.load_din_d & (io.len5|io.len7|io.len9)){
//         int16_sum_2_6 := sq(2) +& sq(6)
//     }
//     when(io.load_din_d & (io.len7|io.len9)){
//         int16_sum_1_7 := sq(1) +& sq(7)
//     }
//     when(io.load_din_d & (io.len9)){
//         int16_sum_0_8 := sq(0) +& sq(8)
//     }
//     when(io.load_din_d){
//             sq4_d:= Cat("d0".U(16.W), io.sq_pd_int8_lsb(4))
//     }
//     when(io.load_din_d){
//         sq_pd_int8_msb_4_d:=io.sq_pd_int8_msb(4)
//     }
   
//     int8_lsb_sum3 := int16_sum3(18,0)
//     int8_lsb_sum5 := int16_sum5(19,0)
//     int8_lsb_sum7 := int16_sum7(19,0)
//     int8_lsb_sum9 := int16_sum9(20,0)

//     //you may find that int16_sum have nothing to do with the output
//     withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){
//         when(io.load_din_2d){
//             int8_msb_sum3:=int8_msb_sum_3_5 + Cat("b0".U(1.W), sq_pd_int8_msb_4_d)
//         }
//         when(io.load_din_2d &(io.len5|io.len7|io.len9)){
//             int8_msb_sum5:=(int8_msb_sum_3_5 + Cat("b0".U(1.W), sq_pd_int8_msb_4_d))+ Cat("b0".U(1.W), int8_msb_sum_2_6)
//         }
//         when(io.load_din_2d &(io.len7|io.len9)){
//             int8_msb_sum7:=(int8_msb_sum_3_5 + Cat("b0".U(1.W), sq_pd_int8_msb_4_d))+ (int8_msb_sum_2_6 + int8_msb_sum_1_7)
//         }
//         when(io.load_din_2d &(io.len9)){
//             int8_msb_sum9:=((int8_msb_sum_3_5 + Cat("b0".U(1.W), sq_pd_int8_msb_4_d))+ (int8_msb_sum_2_6 + int8_msb_sum_1_7)) + Cat("d0".U(2.W), int8_msb_sum_0_8)
//         }
//         when(io.load_din_2d){
//             int16_sum3:= int16_sum_3_5 + Cat("b0".U(1.W), sq4_d)
//         }
//         when(io.load_din_2d&(io.len5|io.len7|io.len9)){
//             int16_sum5:= (int16_sum_3_5 + Cat("b0".U(1.W), sq4_d)) + Cat("b0".U(1.W), int16_sum_2_6)
//         } 
//         when(io.load_din_2d &(io.len7|io.len9)){
//             int16_sum7:= (int16_sum_3_5 + Cat("b0".U(1.W), sq4_d)) + (int16_sum_2_6 + int16_sum_1_7)
//         }
//         when(io.load_din_2d &(io.len9)){
//             int16_sum9:= (int16_sum_3_5 + Cat("b0".U(1.W), sq4_d)) + (int16_sum_2_6 + int16_sum_1_7) +  Cat("d0".U(2.W), int16_sum_0_8)
//         }        
//     }

//     when(io.reg2dp_normalz_len === "b00".U){
//         int8_lsb_sum := Cat("b00".U, int8_lsb_sum3)
//         int8_msb_sum := Cat("b00".U, int8_msb_sum3)
//     }
//     .elsewhen(io.reg2dp_normalz_len === "b01".U){
//         int8_lsb_sum := Cat("b0".U, int8_lsb_sum5)
//         int8_msb_sum := Cat("b0".U, int8_msb_sum5)  
//     }
//     .elsewhen(io.reg2dp_normalz_len === "b10".U){
//         int8_lsb_sum := Cat("b0".U, int8_lsb_sum7)
//         int8_msb_sum := Cat("b0".U, int8_msb_sum7) 
//     }
//     .otherwise{
//         int8_lsb_sum := int8_lsb_sum9
//         int8_msb_sum := int8_msb_sum9
//     }

//     io.int8_sum := Cat(int8_msb_sum , int8_lsb_sum )  

// }}