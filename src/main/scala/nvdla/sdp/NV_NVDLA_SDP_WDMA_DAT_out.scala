// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.iotesters.Driver

// class NV_NVDLA_SDP_WDMA_DAT_out(implicit val conf: sdpConfiguration) extends Module {
//    val io = IO(new Bundle {
//         //in clock
//         val nvdla_core_clk = Input(Clock())
//         val op_load = Input(Bool())

//         //dma_wr
//         val dma_wr_req_vld = Output(Bool())
//         val dma_wr_req_rdy = Input(Bool())
//         val dma_wr_req_pd = Output(UInt(conf.NVDLA_DMA_WR_REQ.W))

//         //cmd2dat_dma
//         val cmd2dat_dma_pvld = Input(Bool())
//         val cmd2dat_dma_prdy = Output(Bool())
//         val cmd2dat_dma_pd = Input(UInt((conf.SDP_WR_CMD_DW+2).W))

//         //out dfifo0
//         val dfifo0_rd_pvld = Input(Bool())
//         val dfifo0_rd_prdy = Output(Bool())
//         val dfifo0_rd_pd = Input(UInt(conf.AM_DW.W))

//         //out dfifo1
//         val dfifo1_rd_pvld = Input(Bool())
//         val dfifo1_rd_prdy = Output(Bool())
//         val dfifo1_rd_pd = Input(UInt(conf.AM_DW.W))

//         //out dfifo2
//         val dfifo2_rd_pvld = Input(Bool())
//         val dfifo2_rd_prdy = Output(Bool())
//         val dfifo2_rd_pd = Input(UInt(conf.AM_DW.W))

//         //out dfifo3
//         val dfifo3_rd_pvld = Input(Bool())
//         val dfifo3_rd_prdy = Output(Bool())
//         val dfifo3_rd_pd = Input(UInt(conf.AM_DW.W))

//         val reg2dp_batch_number = Input(UInt(5.W))
//         val reg2dp_ew_alu_algo = Input(UInt(2.W))
//         val reg2dp_ew_alu_bypass = Input(Bool())
//         val reg2dp_ew_bypass = Input(Bool())
//         val reg2dp_height = Input(UInt(13.W))
//         val reg2dp_interrupt_ptr = Input(Bool())
//         val reg2dp_out_precision = Input(UInt(2.W))
//         val reg2dp_output_dst = Input(Bool())
//         val reg2dp_proc_precision = Input(UInt(2.W))
//         val reg2dp_width = Input(UInt(13.W))
//         val reg2dp_winograd = Input(Bool())

//         val dp2reg_done = Output(Bool())
//         val dp2reg_status_unequal = Output(Bool())

//         val intr_req_ptr = Output(Bool())
//         val intr_req_pvld = Output(Bool())
//     })

// withClock(io.nvdla_core_clk){

//     val cfg_mode_batch = io.reg2dp_batch_number =/= 0.U
//     val cfg_mode_winog = io.reg2dp_winograd === 1.U
//     val cfg_mode_eql = (io.reg2dp_ew_bypass === 0.U) & (io.reg2dp_ew_alu_bypass =/= 0.U) & (io.reg2dp_ew_alu_algo =/= 3.U)

//     val cfg_mode_pdp = io.reg2dp_output_dst === 1.U
//     val cfg_mode_quite = cfg_mode_eql | cfg_mode_pdp

//     val cfg_di_int8  = io.reg2dp_proc_precision  === 0.U 
//     val cfg_do_int16 = io.reg2dp_out_precision === 1.U 

//     val cfg_mode_1x1_pack = (io.reg2dp_width === 0.U) & (io.reg2dp_height === 0.U)

//     //pop comand data
//     val cmd2dat_dma_addr = io.cmd2dat_dma_pd(conf.NVDLA_MEM_ADDRESS_WIDTH-conf.AM_AW-1, 0)
//     val cmd2dat_dma_size = io.cmd2dat_dma_pd(conf.SDP_WR_CMD_DW-1, conf.NVDLA_MEM_ADDRESS_WIDTH - conf.AM_AW)
//     val cmd2dat_dma_odd = io.cmd2dat_dma_pd(conf.SDP_WR_CMD_DW)
//     val cmd2dat_dma_cube_end = io.cmd2dat_dma_pd(conf.SDP_WR_CMD_DW+1)

//     val cmd_rdy = Wire(Bool())
//     val cmd_vld = RegInit(false.B)
//     val dat_accept = Wire(Bool())
//     val is_last_beat = Wire(Bool())
//     val cmd2dat_dma_prdy = cmd_rdy || !cmd_vld
//     cmd_rdy := dat_accept & is_last_beat

//     when(cmd2dat_dma_prdy){
//         spt_vld := io.cmd2dat_dma_pvld
//     }




//     is_last_beat := (beat_count === spt_size)

//     when(io.cmd2dat_spt_pvld & io.cmd2dat_spt_prdy){
//         spt_size := cmd2dat_spt_size
//     }

//     val dfifo0_wr_en = beat_count(1, 0) === 0.U
//     val dfifo1_wr_en = beat_count(1, 0) === 1.U
//     val dfifo2_wr_en = beat_count(1, 0) === 2.U
//     val dfifo3_wr_en = beat_count(1, 0) === 3.U

//     val dfifo0_wr_prdy = Wire(Bool())
//     val dfifo0_wr_pvld = io.sdp_dp2wdma_valid & dfifo0_wr_en
//     val dfifo0_wr_rdy = Mux(dfifo0_wr_en, dfifo0_wr_prdy, true.B)
//     val dfifo0_wr_pd = dp2wdma_data

//     val dfifo1_wr_prdy = Wire(Bool())
//     val dfifo1_wr_pvld = io.sdp_dp2wdma_valid & dfifo1_wr_en
//     val dfifo1_wr_rdy = Mux(dfifo1_wr_en, dfifo1_wr_prdy, true.B)
//     val dfifo1_wr_pd = dp2wdma_data

//     val dfifo2_wr_prdy = Wire(Bool())
//     val dfifo2_wr_pvld = io.sdp_dp2wdma_valid & dfifo2_wr_en
//     val dfifo2_wr_rdy = Mux(dfifo2_wr_en, dfifo2_wr_prdy, true.B)
//     val dfifo2_wr_pd = dp2wdma_data

//     val dfifo3_wr_prdy = Wire(Bool())
//     val dfifo3_wr_pvld = io.sdp_dp2wdma_valid & dfifo3_wr_en
//     val dfifo3_wr_rdy = Mux(dfifo3_wr_en, dfifo3_wr_prdy, true.B)
//     val dfifo3_wr_pd = dp2wdma_data

//     val u_dfifo0 = Module{new NV_NVDLA_SDP_WDMA_DAT_IN_dfifo}

//     u_dfifo0.io.nvdla_core_clk := io.nvdla_core_clk

//     u_dfifo0.io.dfifo_wr_pvld := dfifo0_wr_pvld
//     dfifo0_wr_prdy := u_dfifo0.io.dfifo_wr_prdy
//     u_dfifo0.io.dfifo_wr_pd := dfifo0_wr_pd
//     io.dfifo0_rd_pvld := u_dfifo0.io.dfifo_rd_pvld
//     u_dfifo0.io.dfifo_rd_prdy := io.dfifo0_rd_prdy
//     io.dfifo0_rd_pd := u_dfifo0.io.dfifo_rd_pd

//     val u_dfifo1 = Module{new NV_NVDLA_SDP_WDMA_DAT_IN_dfifo}

//     u_dfifo1.io.nvdla_core_clk := io.nvdla_core_clk

//     u_dfifo1.io.dfifo_wr_pvld := dfifo1_wr_pvld
//     dfifo1_wr_prdy := u_dfifo1.io.dfifo_wr_prdy
//     u_dfifo1.io.dfifo_wr_pd := dfifo1_wr_pd
//     io.dfifo1_rd_pvld := u_dfifo1.io.dfifo_rd_pvld
//     u_dfifo1.io.dfifo_rd_prdy := io.dfifo1_rd_prdy
//     io.dfifo1_rd_pd := u_dfifo1.io.dfifo_rd_pd

//     val u_dfifo2 = Module{new NV_NVDLA_SDP_WDMA_DAT_IN_dfifo}

//     u_dfifo2.io.nvdla_core_clk := io.nvdla_core_clk

//     u_dfifo2.io.dfifo_wr_pvld := dfifo2_wr_pvld
//     dfifo2_wr_prdy := u_dfifo2.io.dfifo_wr_prdy
//     u_dfifo2.io.dfifo_wr_pd := dfifo2_wr_pd
//     io.dfifo2_rd_pvld := u_dfifo2.io.dfifo_rd_pvld
//     u_dfifo2.io.dfifo_rd_prdy := io.dfifo2_rd_prdy
//     io.dfifo2_rd_pd := u_dfifo2.io.dfifo_rd_pd

//     val u_dfifo3 = Module{new NV_NVDLA_SDP_WDMA_DAT_IN_dfifo}

//     u_dfifo3.io.nvdla_core_clk := io.nvdla_core_clk

//     u_dfifo3.io.dfifo_wr_pvld := dfifo3_wr_pvld
//     dfifo3_wr_prdy := u_dfifo3.io.dfifo_wr_prdy
//     u_dfifo3.io.dfifo_wr_pd := dfifo3_wr_pd
//     io.dfifo3_rd_pvld := u_dfifo3.io.dfifo_rd_pvld
//     u_dfifo3.io.dfifo_rd_prdy := io.dfifo3_rd_prdy
//     io.dfifo3_rd_pd := u_dfifo3.io.dfifo_rd_pd

//     in_dat_rdy := dfifo0_wr_rdy & dfifo1_wr_rdy & dfifo2_wr_rdy & dfifo3_wr_rdy;
//     in_dat_accept := (dfifo0_wr_pvld & dfifo0_wr_prdy) | (dfifo1_wr_pvld & dfifo1_wr_prdy) | (dfifo2_wr_pvld & dfifo2_wr_prdy) | (dfifo3_wr_pvld & dfifo3_wr_prdy);

            
// }}

// class NV_NVDLA_SDP_WDMA_DAT_IN_dfifo(implicit val conf: sdpConfiguration) extends Module{
//   val io = IO(new Bundle{
//         val nvdla_core_clk = Input(Clock())

//         val dfifo_wr_pvld = Input(Bool())
//         val dfifo_wr_prdy = Output(Bool())
//         val dfifo_wr_pd = Input(UInt(conf.AM_DW.W))
        
//         val dfifo_rd_pvld = Output(Bool())
//         val dfifo_rd_prdy = Input(Bool())
//         val dfifo_rd_pd = Output(UInt(conf.AM_DW.W))
//   })  

//   val is_pipe = Module(new NV_NVDLA_IS_pipe(conf.AM_DW))

//   is_pipe.io.clk := io.nvdla_core_clk

//   is_pipe.io.vi := io.dfifo_wr_pvld
//   io.dfifo_wr_prdy := is_pipe.io.ro
//   is_pipe.io.di := io.dfifo_wr_pd

//   io.dfifo_rd_pvld := is_pipe.io.vo
//   is_pipe.io.ri := io.dfifo_rd_prdy
//   io.dfifo_rd_pd := is_pipe.io.dout
  
// }

 
// object NV_NVDLA_SDP_WDMA_DAT_inDriver extends App {
//   implicit val conf: sdpConfiguration = new sdpConfiguration
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_WDMA_DAT_in())
// }


