// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_MRDMA_EG_cmd extends Module {
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())
//         val pwrbus_ram_pd = Input(UInt(32.W))

//         val eg_done = Input(Bool())

//         val cq2eg_pvld = Input(Bool())
//         val cq2eg_prdy = Output(Bool())
//         val cq2eg_pd = Input(UInt(14.W))

//         val cmd2dat_spt_pvld = Output(Bool())
//         val cmd2dat_spt_prdy = Input(Bool())
//         val cmd2dat_spt_pd = Output(UInt(13.W))

//         val cmd2dat_dma_pvld = Output(Bool())
//         val cmd2dat_dma_prdy = Input(Bool())
//         val cmd2dat_dma_pd = Output(UInt(15.W))

//         val reg2dp_height = Input(UInt(13.W))
//         val reg2dp_in_precision = Input(UInt(2.W))
//         val reg2dp_proc_precision = Input(UInt(2.W))
//         val reg2dp_width = Input(UInt(13.W))


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
//     val cfg_di_int16 = io.reg2dp_in_precision === 1.U
//     val cfg_do_int8 = io.reg2dp_proc_precision === 0.U
//     val cfg_do_int16 = io.reg2dp_proc_precision === 1.U
//     val cfg_do_fp16 = io.reg2dp_proc_precision === 2.U
//     val cfg_do_16 = cfg_do_int16 | cfg_do_fp16
//     val cfg_mode_1x1_pack = (io.reg2dp_width === 0.U) & (io.reg2dp_height === 0.U)

//     val ig2eg_size = io.cq2eg_pd(12, 0)
//     val ig2eg_cube_end = io.cq2eg_pd(13)

//     val cmd_vld = RegInit(false.B)
//     val cmd_rdy = Wire(Bool())
//     io.cq2eg_prdy := !cmd_vld || cmd_rdy
//     when(io.cq2eg_prdy){
//         cmd_vld := io.cq2eg_pvld
//     }

//     val cq2eg_accept = io.cq2eg_pvld & io.cq2eg_prdy

//     //dma_size is in unit of atomic_m * 1B
//     val cmd_spt_size = RegInit("b0".asUInt(13.W))
//     //dma_size is in unit of 16B
//     val cmd_dma_size = RegInit("b0".asUInt(14.W))
//     val cmd_cube_end = RegInit(false.B)
//     when(cq2eg_accept){
//         cmd_spt_size := ig2eg_size
//         cmd_dma_size := ig2eg_size
//         cmd_cube_end := ig2eg_cube_end
//     }

//     val dma_req_en = true.B
//     val spt_size = cmd_spt_size
//     val dma_size = cmd_dma_size
//     val dma_cube_end = cmd_cube_end

//     //==============
//     // OUTPUT PACK and PIPE: To EG_DAT
//     //==============
//     val spt_fifo_pd = spt_size
//     val dma_fifo_pd = Cat(dma_cube_end, dma_size)

//     val dma_fifo_prdy = Wire(Bool())
//     val spt_fifo_prdy = Wire(Bool())
//     val spt_fifo_pvld = cmd_vld & dma_fifo_prdy
//     val dma_fifo_pvld = cmd_vld & dma_req_en & spt_fifo_prdy
//     cmd_rdy := spt_fifo_prdy & dma_fifo_prdy

//     val u_sfifo = Module{new NV_NVDLA_SDP_fifo_flop_based(4, 13)}

//     u_sfifo.io.clk := io.nvdla_core_clk 
//     u_sfifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd

//     u_sfifo.io.wr_vld := spt_fifo_pvld
//     spt_fifo_prdy := u_sfifo.io.wr_rdy
//     u_sfifo.io.wr_data := spt_fifo_pd

//     io.cmd2dat_spt_pvld := u_sfifo.io.rd_vld
//     u_sfifo.io.rd_rdy := io.cmd2dat_spt_prdy
//     io.cmd2dat_spt_pd := u_sfifo.io.rd_data
    
//     val u_dfifo = Module{new NV_NVDLA_SDP_fifo_flop_based(4, 15)}

//     u_dfifo.io.clk := io.nvdla_core_clk 
//     u_dfifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd

//     u_dfifo.io.wr_vld := dma_fifo_pvld
//     dma_fifo_prdy := u_dfifo.io.wr_rdy
//     u_dfifo.io.wr_data := dma_fifo_pd

//     io.cmd2dat_dma_pvld := u_dfifo.io.rd_vld
//     u_dfifo.io.rd_rdy := io.cmd2dat_dma_prdy
//     io.cmd2dat_dma_pd := u_dfifo.io.rd_data
    
// }}


// object NV_NVDLA_SDP_MRDMA_EG_cmdDriver extends App {
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_MRDMA_EG_cmd)
// }