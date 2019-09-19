// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_CDP_DP_sum(implicit val conf: nvdlaConfig) extends Module {
//     val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())
//         val normalz_buf_data = Input(UInt((conf.NVDLA_CDP_ICVTO_BWPE * (conf.NVDLA_CDP_THROUGHPUT + 8) + 17).W))
//         val sum2itp_pd = Output(UInt((conf.NVDLA_CDP_THROUGHPUT * (conf.NVDLA_CDP_ICVTO_BWPE * 2 + 3)).W))
//         val normalz_buf_data_pvld = Input(Bool())
//         val reg2dp_normalz_len = Input(UInt(2.W))
//         val sum2itp_prdy = Input(Bool())
//         val normalz_buf_data_prdy = Output(Bool())
//         val sum2itp_pvld = Output(Bool())
//     })

// withClock(io.nvdla_core_clk){

//     val cdp_buf2sum_ready = Wire(Bool())
//     val pipe_p1 = Module(new NV_NVDLA_BC_pipe(conf.NVDLA_CDP_ICVTO_BWPE * (conf.NVDLA_CDP_THROUGHPUT + 8) + 17))
//     pipe_p1.io.clk := io.nvdla_core_clk
//     pipe_p1.io.vi := io.normalz_buf_data_pvld
//     io.normalz_buf_data_prdy := pipe_p1.io.ro
//     pipe_p1.io.di := io.normalz_buf_data
//     val cdp_buf2sum_valid = pipe_p1.io.vo
//     pipe_p1.io.ri := cdp_buf2sum_ready
//     val cdp_buf2sum_pd = pipe_p1.io.dout

// /////////////////////////////////////////////
//     val buf2sum_din_prdy = Wire(Bool())
//     val buf2sum_rdy_f = buf2sum_din_prdy
//     val load_din = (cdp_buf2sum_valid & buf2sum_rdy_f)
//     cdp_buf2sum_ready := buf2sum_rdy_f

// //==========================================
//     val buf2sum_int8 = VecInit(
//         (0 until conf.NVDLA_CDP_THROUGHPUT+8) map 
//         {i => cdp_buf2sum_pd(
//             (i+1)*conf.NVDLA_CDP_ICVTO_BWPE-1, i*conf.NVDLA_CDP_ICVTO_BWPE
//             )}
//         )

// //========================================================
// //int mode
// //--------------------------------------------------------

//     val inv = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT+8, UInt(conf.NVDLA_CDP_ICVTO_BWPE.W)))
//     val int8_abs = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT+8, UInt(conf.NVDLA_CDP_ICVTO_BWPE.W)))
 
//     for(i <- 0 until conf.NVDLA_CDP_THROUGHPUT+8){
//         inv(i) := Mux(
//             buf2sum_int8(i)(conf.NVDLA_CDP_ICVTO_BWPE-1), 
//             (~buf2sum_int8(i)(conf.NVDLA_CDP_ICVTO_BWPE-2,0)), 
//             0.U
//             )
//         int8_abs(i) := Mux(
//             buf2sum_int8(i)(conf.NVDLA_CDP_ICVTO_BWPE-1),
//             (inv(i)(conf.NVDLA_CDP_ICVTO_BWPE-2,0) + Cat(Fill(conf.NVDLA_CDP_ICVTO_BWPE-2, false.B), true.B)),
//             buf2sum_int8(i)
//         )
//     }

//     // val len3 = Wire(Bool())
//     val len5 = Wire(Bool())
//     val len7 = Wire(Bool())
//     val len9 = Wire(Bool())
//     val int8_sq = RegInit(VecInit((Seq.fill(conf.NVDLA_CDP_THROUGHPUT+8)(0.U((conf.NVDLA_CDP_ICVTO_BWPE*2).W)))))

//     when(load_din){
//         int8_sq(0) := Mux(len9, (int8_abs(0) * int8_abs(0)), 0.U)
//         int8_sq(1) := Mux((len7 | len9), (int8_abs(1) * int8_abs(1)), 0.U)
//         int8_sq(2) := Mux((len5 | len7 | len9), (int8_abs(2) * int8_abs(2)), 0.U)
//         int8_sq(3) := (int8_abs(3) * int8_abs(3))

//     for(i <- 4 until 4+conf.NVDLA_CDP_THROUGHPUT){
//         int8_sq(i) := (int8_abs(i) * int8_abs(i))
//     }

//         int8_sq(conf.NVDLA_CDP_THROUGHPUT+4) := int8_abs(conf.NVDLA_CDP_THROUGHPUT+4) * int8_abs(conf.NVDLA_CDP_THROUGHPUT+4)
//         int8_sq(conf.NVDLA_CDP_THROUGHPUT+4+1) := Mux((len5 | len7 | len9), (int8_abs(conf.NVDLA_CDP_THROUGHPUT+4+1) * int8_abs(conf.NVDLA_CDP_THROUGHPUT+4+1)), 0.U)
//         int8_sq(conf.NVDLA_CDP_THROUGHPUT+4+2) := Mux((len7 | len9), (int8_abs(conf.NVDLA_CDP_THROUGHPUT+4+2) * int8_abs(conf.NVDLA_CDP_THROUGHPUT+4+2)), 0.U)
//         int8_sq(conf.NVDLA_CDP_THROUGHPUT+4+3) := Mux((len9), (int8_abs(conf.NVDLA_CDP_THROUGHPUT+4+3) * int8_abs(conf.NVDLA_CDP_THROUGHPUT+4+3)), 0.U)
//     }

//     val buf2sum_d_vld = RegInit(false.B)
//     val buf2sum_d_rdy = Wire(Bool())
//     buf2sum_din_prdy := ~buf2sum_d_vld | buf2sum_d_rdy
//     when(cdp_buf2sum_valid){
//         buf2sum_d_vld := true.B
//     }.elsewhen(buf2sum_d_rdy){
//         buf2sum_d_vld := false.B
//     }

//     val buf2sum_2d_vld = RegInit(false.B)
//     val buf2sum_2d_rdy = Wire(Bool())
//     buf2sum_d_rdy := ~buf2sum_2d_vld | buf2sum_2d_rdy

// //===========
// //sum process
// //-----------
//     // len3 = (reg2dp_normalz_len[1:0] == 2'h0 );
//     len5 := (io.reg2dp_normalz_len === 1.U )
//     len7 := (io.reg2dp_normalz_len === 2.U )
//     len9 := (io.reg2dp_normalz_len === 3.U )

//     val load_din_d = buf2sum_d_vld & buf2sum_d_rdy

//     when(buf2sum_d_vld){
//         buf2sum_2d_vld := true.B
//     }.elsewhen(buf2sum_2d_rdy){
//         buf2sum_2d_vld := false.B
//     }

//     val buf2sum_3d_vld = RegInit(false.B)
//     val buf2sum_3d_rdy = Wire(Bool())
//     buf2sum_2d_rdy := ~buf2sum_3d_vld | buf2sum_3d_rdy

//     val load_din_2d = buf2sum_2d_vld & buf2sum_2d_rdy

//     val int8_sum = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt((conf.NVDLA_CDP_ICVTO_BWPE*2-1+4).W)))
//     val u_sum_block = Array.fill(conf.NVDLA_CDP_THROUGHPUT){Module(new int_sum_block_tp1)}
//     for(i <- 0 until conf.NVDLA_CDP_THROUGHPUT){
//         u_sum_block(i).io.nvdla_core_clk := io.nvdla_core_clk
//         u_sum_block(i).io.len5 := len5
//         u_sum_block(i).io.len7 := len7
//         u_sum_block(i).io.len9 := len9
//         u_sum_block(i).io.load_din_d := load_din_d
//         u_sum_block(i).io.load_din_2d := load_din_2d
//         u_sum_block(i).io.reg2dp_normalz_len := io.reg2dp_normalz_len
//         for(j <- 0 until 9){
//             u_sum_block(i).io.sq_pd_int8(j) := int8_sq(i+j)
//         }
//         int8_sum(i) := u_sum_block(i).io.int8_sum        
//     }

//     when(buf2sum_2d_vld){
//         buf2sum_3d_vld := true.B
//     }.elsewhen(buf2sum_3d_rdy){
//         buf2sum_3d_vld := false.B
//     }

//     val sum_out_prdy = Wire(Bool())
//     buf2sum_3d_rdy := sum_out_prdy

// //=======================================================
// //data output select
// //-------------------------------------------------------

// // NVDLA_CDP_THROUGHPUT = 8
//     val sum_out_pd = Cat(
//         int8_sum(7), int8_sum(6), int8_sum(5), int8_sum(4), 
//         int8_sum(3), int8_sum(2), int8_sum(1), int8_sum(0)
//         )
    
//     val sum_out_pvld = buf2sum_3d_vld
//     val sum2itp_valid = sum_out_pvld
//     val sum2itp_data = sum_out_pd

//     val pipe_p2 = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_CDP_THROUGHPUT*21))
//     pipe_p2.io.clk := io.nvdla_core_clk
//     pipe_p2.io.vi := sum2itp_valid
//     val sum2itp_ready = pipe_p2.io.ro
//     pipe_p2.io.di := sum2itp_data
//     io.sum2itp_pvld := pipe_p2.io.vo
//     pipe_p2.io.ri := io.sum2itp_prdy
//     io.sum2itp_pd := pipe_p2.io.dout

//     sum_out_prdy := sum2itp_ready

// }}


// object NV_NVDLA_CDP_DP_sumDriver extends App {
//     implicit val conf: nvdlaConfig = new nvdlaConfig
//     chisel3.Driver.execute(args, () => new NV_NVDLA_CDP_DP_sum())
// }
