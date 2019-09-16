// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_MRDMA_EG_dout(implicit val conf: nvdlaConfig) extends Module{
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val op_load = Input(Bool())
//         val eg_done = Output(Bool())

//         val sdp_mrdma2cmux_valid = Output(Bool())
//         val sdp_mrdma2cmux_ready = Input(Bool())
//         val sdp_mrdma2cmux_pd = Output(UInt((conf.DP_DIN_DW + 2).W))

//         val cmd2dat_dma_pvld = Input(Bool())
//         val cmd2dat_dma_prdy = Output(Bool())
//         val cmd2dat_dma_pd = Input(UInt(15.W))

//         val pfifo_rd_pvld = Input(Vec(4, Bool()))
//         val pfifo_rd_prdy = Output(Vec(4, Bool()))
//         val pfifo_rd_pd = Input(Vec(4, UInt(conf.AM_DW.W)))
        
//         val reg2dp_height = Input(UInt(13.W))
//         val reg2dp_width = Input(UInt(13.W))
//         val reg2dp_in_precision = Input(UInt(2.W))
//         val reg2dp_proc_precision = Input(UInt(2.W))
//         val reg2dp_perf_nan_inf_count_en = Input(Bool())
//         val dp2reg_status_inf_input_num = Output(UInt(32.W))
//         val dp2reg_status_nan_input_num = Output(UInt(32.W))

//     })
//     //     
//     //          ┌─┐       ┌─┐
//     //       ┌──┘ ┴───────┘ ┴──┐
//     //       │                 │
//     //       │       ───       │          
//     //       │  ─┬┘       └┬─  │
//     //       │                 │
//     //       │       ─┴─       │
//     //       │                 │
//     //       └───┐         ┌───┘
//     //           │         │
//     //           │         │
//     //           │         │
//     //           │         └──────────────┐
//     //           │                        │
//     //           │                        ├─┐
//     //           │                        ┌─┘    
//     //           │                        │
//     //           └─┐  ┐  ┌───────┬──┐  ┌──┘         
//     //             │ ─┤ ─┤       │ ─┤ ─┤         
//     //             └──┴──┘       └──┴──┘ 
// withClock(io.nvdla_core_clk){
//     //==============
//     // CFG
//     //==============
//     val cfg_di_int8 = io.reg2dp_in_precision === 0.U
//     val cfg_di_int16 = io.reg2dp_in_precision === 1.U
//     val cfg_di_fp16 = io.reg2dp_in_precision === 2.U
//     val cfg_di_16 = cfg_di_int16 | cfg_di_fp16
//     val cfg_do_int8 = io.reg2dp_proc_precision === 0.U
//     val cfg_mode_1x1_pack = (io.reg2dp_width === 0.U) & (io.reg2dp_height === 0.U)
//     val cfg_perf_nan_inf_count_en = io.reg2dp_perf_nan_inf_count_en

//     //pop command dat fifo //
//     val dat_accept = Wire(Bool())
//     val is_last_beat = Wire(Bool())
//     val fifo_vld = Wire(Bool())
//     val dat_rdy = Wire(Bool())

//     io.cmd2dat_dma_prdy := dat_accept & is_last_beat & fifo_vld & dat_rdy

//     val cmd2dat_dma_size = io.cmd2dat_dma_pd(13, 0)
//     val cmd2dat_dma_cube_end  = io.cmd2dat_dma_pd(14)
//     val size_of_beat = (Fill(14, io.cmd2dat_dma_pvld)) & cmd2dat_dma_size
//     val cmd_cube_end = io.cmd2dat_dma_pvld & cmd2dat_dma_cube_end

//     val beat_cnt = RegInit(0.U(14.W))
//     when(dat_accept){
//         when(is_last_beat){
//             beat_cnt := 0.U
//         }.otherwise{
//             beat_cnt := beat_cnt + 1.U
//         }
//     }
//     is_last_beat := (beat_cnt === size_of_beat)

//     val pfifo_sel = Wire(Vec(4, Bool()))
//     val pfifo_rd_sel_pvld = Wire(Vec(4, Bool()))
//     for(i <- 0 to 3){
//         pfifo_sel(i) := beat_cnt(1, 0) === i.U
//         pfifo_rd_sel_pvld(i) := io.pfifo_rd_pvld(i)&pfifo_sel(i)
//     }
//     val pfifo_vld = pfifo_rd_sel_pvld.asUInt.orR

//     fifo_vld := pfifo_vld
//     val dat_vld = fifo_vld  //& cmd2dat_dma_pvld;

//     val pfifo_rd_data = Wire(Vec(4, UInt(conf.AM_DW.W)))
//     for(i <- 0 to 3){
//         io.pfifo_rd_prdy(i) := dat_rdy & pfifo_sel(i)
//         pfifo_rd_data(i) := (Fill(conf.AM_DW, pfifo_sel(i))) & io.pfifo_rd_pd(i)
//     }

//     val pfifo_data_byte_16 = Wire(Vec(4, Vec(conf.NVDLA_MEMORY_ATOMIC_SIZE/2, UInt(16.W))))
//     val pfifo_data_ext_byte_int16 = Wire(Vec(4, Vec(conf.NVDLA_MEMORY_ATOMIC_SIZE/2, UInt(32.W))))
//     for(i <- 0 to 3){
//         for(j <- 0 to conf.NVDLA_MEMORY_ATOMIC_SIZE/2 - 1){
//             pfifo_data_byte_16(i)(j) := pfifo_rd_data(i)(16*j+15, 16*j)
//             pfifo_data_ext_byte_int16(i)(j) := Cat(Fill(16, pfifo_data_byte_16(i)(j)(15)), pfifo_data_byte_16(i)(j))
//         }
//     }

//     val pfifo_data_ext_byte_16 = pfifo_data_ext_byte_int16
//     val pfifo_data_16 = Wire(Vec(4, UInt(conf.DP_DIN_DW.W)))
//     for(i <- 0 to 3){
//         pfifo_data_16(i) := pfifo_data_ext_byte_16(i).asUInt
//     }

//     //// int8 ///////////
//     val pfifo_data_byte_8 = Wire(Vec(4, Vec(conf.NVDLA_MEMORY_ATOMIC_SIZE, UInt(8.W))))
//     val pfifo_data_ext_byte_8 = Wire(Vec(4, Vec(conf.NVDLA_MEMORY_ATOMIC_SIZE, UInt(32.W))))
//     for(i <- 0 to 3){
//         for(j <- 0 to conf.NVDLA_MEMORY_ATOMIC_SIZE - 1){
//             pfifo_data_byte_8(i)(j) := pfifo_rd_data(i)(8*j+7, 8*j)
//             pfifo_data_ext_byte_8(i)(j) := Cat(Fill(24, pfifo_data_byte_8(i)(j)(7)), pfifo_data_byte_8(i)(j))
//         }
//     }

//     // INT8, concate
//     val pfifo_data_8 = Wire(Vec(4, UInt(conf.DP_DIN_DW.W)))
//     for(i <- 0 to 3){
//         pfifo_data_8(i) := pfifo_data_ext_byte_8(i).asUInt
//     }

//     //=====PERF COUNT BEG============= 
//     io.dp2reg_status_inf_input_num := 0.U
//     io.dp2reg_status_nan_input_num := 0.U

//     //=====PERF COUNT END=============
//     val pfifo_data_r = MuxCase(
//         0.U,
//         (0 to 3) map { i => (pfifo_sel(i) === true.B) -> Mux(cfg_di_16, pfifo_data_16(i), pfifo_data_8(i))}
//     )
//     val dat_data = pfifo_data_r

//     dat_accept := dat_vld & dat_rdy
//     val dat_layer_end = cmd_cube_end & is_last_beat
//     val dat_batch_end = dat_layer_end & is_last_beat

//     val dat_pd = Cat(dat_layer_end, dat_batch_end, dat_data)

//     val pipe_p1 = Module{new NV_NVDLA_IS_pipe(conf.DP_DIN_DW+2)}
//     pipe_p1.io.clk := io.nvdla_core_clk
//     pipe_p1.io.vi := dat_vld
//     dat_rdy := pipe_p1.io.ro
//     pipe_p1.io.di := dat_pd
//     io.sdp_mrdma2cmux_valid := pipe_p1.io.vo
//     pipe_p1.io.ri := io.sdp_mrdma2cmux_ready
//     io.sdp_mrdma2cmux_pd := pipe_p1.io.dout

//     val sdp_mrdma2cmux_layer_end = io.sdp_mrdma2cmux_pd(conf.DP_DIN_DW+1)
//     io.eg_done := RegNext(sdp_mrdma2cmux_layer_end & io.sdp_mrdma2cmux_valid & io.sdp_mrdma2cmux_ready, false.B)

// }}

// object NV_NVDLA_SDP_MRDMA_EG_doutDriver extends App {
//     implicit val conf: nvdlaConfig = new nvdlaConfig
//     chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_MRDMA_EG_dout())
// }