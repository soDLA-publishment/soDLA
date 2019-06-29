// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_MRDMA_EG_din(implicit val conf: sdpConfiguration) extends Module {
//    val io = IO(new Bundle {
//         //clk
//         val nvdla_core_clk = Input(Clock())
//         val pwrbus_ram_pd = Input(UInt(32.W))

//         //dma_rd
//         val dma_rd_rsp_vld = Input(Bool())
//         val dma_rd_rsp_rdy = Output(Bool())
//         val dma_rd_rsp_pd = Input(UInt(conf.NVDLA_DMA_RD_RSP.W))
//         val dma_rd_cdt_lat_fifo_pop = Output(Bool())             
//         val dma_rd_rsp_ram_type = Output(Bool())

//         //cmd2dat
//         val cmd2dat_spt_pvld = Input(Bool())
//         val cmd2dat_spt_prdy = Output(Bool())
//         val cmd2dat_spt_pd = Input(UInt(13.W))

//         //pfifo
//         val pfifo0_rd_pvld = Output(Bool())
//         val pfifo0_rd_prdy = Input(Bool())
//         val pfifo0_rd_pd = Output(UInt(conf.AM_DW.W))

//         val pfifo1_rd_pvld = Output(Bool())
//         val pfifo1_rd_prdy = Input(Bool())
//         val pfifo1_rd_pd = Output(UInt(conf.AM_DW.W))

//         val pfifo2_rd_pvld = Output(Bool())
//         val pfifo2_rd_prdy = Input(Bool())
//         val pfifo2_rd_pd = Output(UInt(conf.AM_DW.W))

//         val pfifo3_rd_pvld = Output(Bool())
//         val pfifo3_rd_prdy = Input(Bool())
//         val pfifo3_rd_pd = Output(UInt(conf.AM_DW.W))

//         val reg2dp_src_ram_type = Input(Bool())

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
//     // Latency FIFO to buffer return DATA
//     //==============
//     val lat_ecc_rd_pvld = Wire(Bool())
//     val lat_ecc_rd_prdy = Wire(Bool())
//     val lat_ecc_rd_pd = Wire(UInt(conf.NVDLA_DMA_RD_RSP.W))
    
//     val dma_rd_rsp_ram_type = io.reg2dp_src_ram_type
//     val dma_rd_cdt_lat_fifo_pop = lat_ecc_rd_pvld & lat_ecc_rd_prdy

//     val u_lat_fifo = Module(new NV_NVDLA_SDP_MRDMA_EG_lat_fifo(conf.NVDLA_VMOD_SDP_MRDMA_LATENCY_FIFO_DEPTH, conf.NVDLA_DMA_RD_RSP))
//     u_lat_fifo.io.clk := io.nvdla_core_clk
//     io.dma_rd_rsp_rdy := u_lat_fifo.io.wr_rdy
//     u_lat_fifo.io.wr_vld := io.dma_rd_rsp_vld
//     u_lat_fifo.io.wr_data := io.dma_rd_rsp_pd

//     u_lat_fifo.io.rd_rdy := lat_ecc_rd_prdy
//     lat_ecc_rd_pvld := u_lat_fifo.io.rd_vld
//     lat_ecc_rd_pd := u_lat_fifo.io.rd_data

//     u_lat_fifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd

//     val lat_ecc_rd_accept := lat_ecc_rd_pvld & lat_ecc_rd_prdy  

//     val lat_ecc_rd_data(NVDLA_MEMIF_WIDTH, 0) := lat_ecc_rd_pd(NVDLA_MEMIF_WIDTH, 0)    

//     val lat_ecc_rd_mask := Cat(0.U((4-NVDLA_DMA_MASK_BIT).W), lat_ecc_rd_pd(NVDLA_DMA_RD_RSP, NVDLA_MEMIF_WIDTH))

//     val lat_ecc_rd_size := lat_ecc_rd_mask(3) + lat_ecc_rd_mask(2) + lat_ecc_rd_mask(1) + lat_ecc_rd_mask(0)

//     // #ifdef NVDLA_SDP_DMAIF_FIX
//     // ...
//     // #endif

// //========command for pfifo wr ====================

//     val is_last_beat = Wire(Bool())
//     io.cmd2dat_spt_prdy := lat_ecc_rd_accept & is_last_beat

//     val cmd2dat_spt_size = io.cmd2dat_spt_pd

//     val cmd_size = Mux(io.cmd2dat_spt_pvld, (cmd2dat_spt_size+1), 0)

//     val beat_cnt = RegInit(0.U(13.W))
//     val beat_cnt_nxt = beat_cnt + lat_ecc_rd_size

//     when(lat_ecc_rd_accept){
//         when(is_last_beat){
//             beat_cnt := 0.U
//         }.otherwise{
//             beat_cnt = beat_cnt_nxt
//         }
//     }

//     is_last_beat := beat_cnt_nxt === cmd_size


// /////////combine lat fifo pd to 4*atomic_m*bpe//////

//     val lat_ecc_rd_beat_end = is_last_beat
//     val unpack_out_prdy = Wire(Bool())
    
//     val u_rdma_unpack = Module(new NV_NVDLA_SDP_RDMA_unpack(conf.NVDLA_DMA_RD_RSP, conf.AM_DW))
//     u_rdma_unpack.io.nvdla_core_clk := io.nvdla_core_clk
//     u_rdma_unpack.io.inp_end := lat_ecc_rd_beat_end
//     u_rdma_unpack.io.inp_pvld := lat_ecc_rd_pvld
//     lat_ecc_rd_prdy := u_rdma_unpack.io.inp_prdy
//     u_rdma_unpack.io.inp_data := lat_ecc_rd_pd

//     val unpack_out_pvld := u_rdma_unpack.io.out_pvld
//     u_rdma_unpack.io.out_prdy := unpack_out_prdy
//     val unpack_out_pd := u_rdma_unpack.io.out_data

//     val pfifo_wr_rdy = Wire(Bool())
//     unpack_out_prdy := pfifo_wr_rdy
//     val pfifo_wr_mask = unpack_out_pd(4*AM_DW+3, 4*AM_DW)
//     val pfifo_wr_vld = unpack_out_pvld



// }}