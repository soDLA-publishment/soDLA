// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// //this module is to process dat

// class NV_NVDLA_CACC_delivery_buffer(implicit conf: caccConfiguration) extends Module {

//     val io = IO(new Bundle {
//         //clk
//         val nvdla_core_clk = Input(Clock())

//         //cacc2sdp
//         val cacc2sdp_ready = Input(Bool())
//         val cacc2sdp_pd = Output(UInt(conf.CACC_SDP_WIDTH.W))
//         val cacc2sdp_valid = Output(Bool())

//         //cacc2glb
//         val cacc2glb_done_intr_pd = Output(UInt(2.W))

//         //accu2sc
//         val accu2sc_credit_size = Output(UInt(3.W))
//         val accu2sc_credit_vld = Output(Bool())

//         //dbuf
//         val dbuf_wr_en = Input(Bool())
//         val dbuf_wr_addr = Input(UInt(conf.CACC_DBUF_AWIDTH.W))
//         val dbuf_wr_data = Input(Vec(conf.CACC_ATOMK, UInt(conf.CACC_FINAL_WIDTH.W)))

//         val dbuf_rd_en = Input(Bool())
//         val dbuf_rd_layer_end = Input(Bool())
//         val dbuf_rd_ready = Output(Bool())
//         val dbuf_rd_addr = Input(UInt(conf.CACC_ABUF_AWIDTH.W))


//         //pwrbus
//         val pwrbus_ram_pd = Input(UInt(32.W))


//     })

// //     
// //          ┌─┐       ┌─┐
// //       ┌──┘ ┴───────┘ ┴──┐
// //       │                 │
// //       │       ───       │
// //       │  ─┬┘       └┬─  │
// //       │                 │                       
// //       │       ─┴─       │
// //       │                 │
// //       └───┐         ┌───┘
// //           │         │
// //           │         │
// //           │         │
// //           │         └──────────────┐
// //           │                        │
// //           │                        ├─┐         
// //           │                        ┌─┘    
// //           │                        │
// //           └─┐  ┐  ┌───────┬──┐  ┌──┘         
// //             │ ─┤ ─┤       │ ─┤ ─┤         
// //             └──┴──┘       └──┴──┘

// // Instance RAMs  
// val data_left_mask = RegInit("b0".asUInt(conf.CACC_DWIDTH_DIV_SWIDTH.W))
// val dbuf_rd_en_new = ~(data_left_mask.orR) & io.dbuf_rd_en

// val u_accu_dbuf = Module(new nv_ram_rws(conf.CACC_DBUF_DEPTH, conf.CACC_DBUF_WIDTH))
// val dbuf_rd_data = Wire(Vec(CACC_DWIDTH_DIV_SWIDTH, SInt()))

// u_accu_dbuf.io.clk := io.nvdla_core_clk
// u_accu_dbuf.io.ra := io.dbuf_rd_addr
// u_accu_dbuf.io.re := dbuf_rd_en_new
// u_accu_dbuf.io.we := io.dbuf_wr_en
// u_accu_dbuf.io.wa := io.dbuf_wr_addr
// u_accu_dbuf.io.di := io.dbuf_wr_data
// val dbuf_rd_data = u_accu_dbuf.io.dout

// //get signal for SDP
// val dbuf_rd_valid = RegInit(false.B)
// val rd_data_mask = RegInit("b1".asUInt(conf.CACC_DWIDTH_DIV_SWIDTH.W))
// val rd_data_mask_pre = if(conf.CACC_DWIDTH_DIV_SWIDTH>=2) 
//                        Mux(io.cacc2sdp_valid & io.cacc2sdp_ready, Cat(rd_data_mask(conf.CACC_DWIDTH_DIV_SWIDTH-2, 0), rd_data_mask(conf.CACC_DWIDTH_DIV_SWIDTH-1)), rd_data_mask)
//                        else
//                        rd_data_mask
// rd_data_mask := rd_data_mask_pre
// val data_left_mask_pre = Mux(dbuf_rd_en_new, Fill(conf.CACC_DWIDTH_DIV_SWIDTH, true.B), 
//                          Mux(io.cacc2sdp_valid & io.cacc2sdp_ready, data_left_mask<<1.U, data_left_mask))(conf.CACC_DWIDTH_DIV_SWIDTH-1, 0)
// data_left_mask := data_left_mask_pre
// io.cacc2sdp_valid := data_left_mask.orR
// io.dbuf_rd_ready := ~(data_left_mask.orR)





// }

