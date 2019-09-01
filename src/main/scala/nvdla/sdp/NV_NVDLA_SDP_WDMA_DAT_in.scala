// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.iotesters.Driver

// class NV_NVDLA_SDP_WDMA_DAT_in(implicit val conf: nvdlaConfig) extends Module {
//    val io = IO(new Bundle {
//         //in clock
//         val nvdla_core_clk = Input(Clock())
//         val pwrbus_ram_pd = Input(UInt(32.W))
//         val op_load = Input(Bool())

//         //cmd2dat
//         val cmd2dat_spt_pvld = Input(Bool())
//         val cmd2dat_spt_prdy = Output(Bool())
//         val cmd2dat_spt_pd = Input(UInt(15.W))

//         //sdp_dp2wdma
//         val sdp_dp2wdma_valid = Input(Bool())
//         val sdp_dp2wdma_ready = Output(Bool())
//         val sdp_dp2wdma_pd = Input(UInt(conf.AM_DW.W))

//         //out dfifo
//         val dfifo_rd_pvld = Output(Vec(4, Bool()))
//         val dfifo_rd_prdy = Input(Vec(4, Bool()))
//         val dfifo_rd_pd = Output(Vec(4, UInt(conf.AM_DW.W)))

//         val reg2dp_batch_number = Input(UInt(5.W))
//         val reg2dp_height = Input(UInt(13.W))
//         val reg2dp_out_precision = Input(UInt(2.W))
//         val reg2dp_proc_precision = Input(UInt(2.W))
//         val reg2dp_width = Input(UInt(13.W))
//         val reg2dp_winograd = Input(Bool())
//         val dp2reg_status_nan_output_num = Output(UInt(32.W))
//     })

// withClock(io.nvdla_core_clk){

//     val cfg_mode_batch = io.reg2dp_batch_number =/= 0.U
//     val cfg_mode_winograd = io.reg2dp_winograd === 1.U
//     val cfg_mode_1x1_pack = (io.reg2dp_width === 0.U) & (io.reg2dp_height === 0.U)

//     val cfg_di_8 = io.reg2dp_proc_precision === 0.U
//     val cfg_do_8 = io.reg2dp_out_precision === 0.U
//     val cfg_do_int16 = io.reg2dp_out_precision === 1.U
//     val cfg_do_fp16 = io.reg2dp_out_precision === 2.U
//     val cfg_do_16 = cfg_do_int16 | cfg_do_fp16

//     //==================================
//     // DATA split and assembly
//     //==================================

//     val dp2wdma_data = io.sdp_dp2wdma_pd

//     //ndef NVDLA_FEATURE_DATA_TYPE_FP16
//     val dp2wdma_data_16 = dp2wdma_data
//     val dp2wdma_data_8  = dp2wdma_data
//     io.dp2reg_status_nan_output_num := 0.U

//     val in_dat_rdy = Wire(Bool())
//     io.sdp_dp2wdma_ready := in_dat_rdy

//     //pop comand
//     val in_dat_accept = Wire(Bool())
//     val is_last_beat = Wire(Bool())
//     val spt_vld = RegInit(false.B)
//     val spt_size = RegInit("b0".asUInt(14.W))
//     val beat_count = RegInit("b0".asUInt(14.W))

//     val spt_rdy = in_dat_accept & is_last_beat
//     val cmd2dat_spt_size = io.cmd2dat_spt_pd(13, 0)
//     val cmd2dat_spt_odd = io.cmd2dat_spt_pd(14)
//     io.cmd2dat_spt_prdy := spt_rdy || !spt_vld
//     when(io.cmd2dat_spt_prdy){
//         spt_vld := io.cmd2dat_spt_pvld
//     }

//     when(in_dat_accept){
//         when(is_last_beat){
//             beat_count := 0.U
//         }
//         .otherwise{
//             beat_count := beat_count + 1.U
//         }
//     }

//     is_last_beat := (beat_count === spt_size)
//     when(io.cmd2dat_spt_pvld & io.cmd2dat_spt_prdy){
//         spt_size := cmd2dat_spt_size
//     }

//     val dfifo_wr_en = VecInit((0 to 3) 
//                        map { i => beat_count(1, 0) === i.U})
//     val dfifo_wr_prdy = Wire(Vec(4, Bool()))
//     val dfifo_wr_pvld = VecInit((0 to 3) 
//                         map { i => io.sdp_dp2wdma_valid & dfifo_wr_en(i)})
//     val dfifo_wr_rdy = VecInit((0 to 3) 
//                        map { i => Mux(dfifo_wr_en(i), dfifo_wr_prdy(i), true.B)})
//     val dfifo_wr_pd = VecInit((0 to 3) 
//                        map { i => dp2wdma_data})

//     in_dat_rdy := dfifo_wr_rdy.asUInt.andR
//     in_dat_accept := VecInit((0 to 3) 
//                       map { i => dfifo_wr_pvld(i) & dfifo_wr_prdy(i)}).asUInt.orR
    

//     val u_dfifo = Array.fill(4){Module(new NV_NVDLA_IS_pipe(conf.AM_DW))}
//     for(i <- 0 to 3){
//         u_dfifo(i).io.clk := io.nvdla_core_clk
//         u_dfifo(i).io.vi := dfifo_wr_pvld(i)
//         dfifo_wr_prdy(i) := u_dfifo(i).io.ro
//         u_dfifo(i).io.di := dfifo_wr_pd(i)
//         io.dfifo_rd_pvld(i) := u_dfifo(i).io.vo
//         u_dfifo(i).io.ri := io.dfifo_rd_prdy(i)
//         io.dfifo_rd_pd(i) := u_dfifo(i).io.dout
//     }
            
// }}

 
// object NV_NVDLA_SDP_WDMA_DAT_inDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_WDMA_DAT_in())
// }


