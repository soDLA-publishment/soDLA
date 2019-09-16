package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_CACC_delivery_buffer(implicit conf: nvdlaConfig) extends Module {

    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())

        //cacc2sdp
        val cacc2sdp = DecoupledIO(new cacc2sdp_if)    

        //cacc2glb
        val cacc2glb_done_intr_pd = Output(UInt(2.W))

        //accu2sc
        val accu2sc_credit_size = ValidIO(UInt(3.W))

        //dbuf
        val dbuf_wr = Flipped(new nvdla_wr_if(conf.CACC_DBUF_AWIDTH, conf.CACC_DBUF_WIDTH))
        val dbuf_rd_layer_end = Input(Bool())
        val dbuf_rd_addr = Flipped(ValidIO(UInt(conf.CACC_ABUF_AWIDTH.W)))
        val dbuf_rd_ready = Output(Bool())

        //pwrbus
        val pwrbus_ram_pd = Input(UInt(32.W))


    })

//     
//          ┌─┐       ┌─┐
//       ┌──┘ ┴───────┘ ┴──┐
//       │                 │
//       │       ───       │
//       │  ─┬┘       └┬─  │
//       │                 │                       
//       │       ─┴─       │
//       │                 │
//       └───┐         ┌───┘
//           │         │
//           │         │
//           │         │
//           │         └──────────────┐
//           │                        │
//           │                        ├─┐         
//           │                        ┌─┘    
//           │                        │
//           └─┐  ┐  ┌───────┬──┐  ┌──┘         
//             │ ─┤ ─┤       │ ─┤ ─┤         
//             └──┴──┘       └──┴──┘
withClock(io.nvdla_core_clk){

// Instance RAMs 
val data_left_mask = RegInit("b0".asUInt(conf.CACC_DWIDTH_DIV_SWIDTH.W))
val dbuf_rd_en_new = ~(data_left_mask.orR) & io.dbuf_rd_addr.valid

val u_accu_dbuf = Module(new nv_ram_rws(conf.CACC_DBUF_DEPTH, conf.CACC_DBUF_WIDTH))

u_accu_dbuf.io.clk := io.nvdla_core_clk
u_accu_dbuf.io.ra := io.dbuf_rd_addr.bits
u_accu_dbuf.io.re := dbuf_rd_en_new
u_accu_dbuf.io.we := io.dbuf_wr.addr.valid
u_accu_dbuf.io.wa := io.dbuf_wr.addr.bits
u_accu_dbuf.io.di := io.dbuf_wr.data
val dbuf_rd_data = u_accu_dbuf.io.dout

//get signal for SDP
val dbuf_rd_valid = RegInit(false.B)

//which data to be fetched by sdp.
val rd_data_mask = RegInit("b1".asUInt(conf.CACC_DWIDTH_DIV_SWIDTH.W))
val rd_data_mask_pre = if(conf.CACC_DWIDTH_DIV_SWIDTH>=2) 
                       Mux(io.cacc2sdp.valid & io.cacc2sdp.ready, Cat(rd_data_mask(conf.CACC_DWIDTH_DIV_SWIDTH-2, 0), 
                            rd_data_mask(conf.CACC_DWIDTH_DIV_SWIDTH-1)), rd_data_mask)
                       else
                       rd_data_mask
rd_data_mask := rd_data_mask_pre
val data_left_mask_pre = Mux(dbuf_rd_en_new, Fill(conf.CACC_DWIDTH_DIV_SWIDTH, true.B), 
                         Mux(io.cacc2sdp.valid & io.cacc2sdp.ready, data_left_mask<<1.U, data_left_mask))(conf.CACC_DWIDTH_DIV_SWIDTH-1, 0)
data_left_mask := data_left_mask_pre
io.cacc2sdp.valid := data_left_mask.orR
io.dbuf_rd_ready := ~(data_left_mask.orR)

val cacc2sdp_pd_data = VecInit((0 to conf.CACC_DWIDTH_DIV_SWIDTH-1) map 
                      { i => dbuf_rd_data(conf.CACC_SDP_DATA_WIDTH*(i+1)-1, conf.CACC_SDP_DATA_WIDTH*i)&Fill(conf.CACC_SDP_DATA_WIDTH, rd_data_mask(i))}).reduce(_|_)

//layer_end handle
val dbuf_rd_layer_end_latch = RegInit(false.B)
val dbuf_rd_layer_end_latch_w = Mux(io.dbuf_rd_layer_end, true.B, 
                                Mux(~(data_left_mask.orR), false.B, 
                                dbuf_rd_layer_end_latch))
dbuf_rd_layer_end_latch := dbuf_rd_layer_end_latch_w

//regout to SDP
val last_data = Wire(Bool())
val cacc2sdp_batch_end = false.B
val cacc2sdp_layer_end = dbuf_rd_layer_end_latch & last_data & io.cacc2sdp.valid & io.cacc2sdp.ready; //data_left_mask=0;
io.cacc2sdp.bits.pd := Cat(cacc2sdp_layer_end, cacc2sdp_batch_end, cacc2sdp_pd_data)

// generate CACC done interrupt  
val intr_sel = RegInit(false.B)
val cacc_done_intr  = RegInit("b0".asUInt(2.W))
val cacc_done = io.cacc2sdp.valid & io.cacc2sdp.ready & cacc2sdp_layer_end
val cacc_done_intr_w = Cat(cacc_done & intr_sel, cacc_done & ~intr_sel)
val intr_sel_w = Mux(cacc_done, ~intr_sel, intr_sel)
intr_sel := intr_sel_w
cacc_done_intr := cacc_done_intr_w
io.cacc2glb_done_intr_pd := cacc_done_intr

///// generate credit signal
io.accu2sc_credit_size.bits := "h1".asUInt(3.W)
last_data := (data_left_mask === Cat(true.B, Fill(conf.CACC_DWIDTH_DIV_SWIDTH-1, false.B)));
io.accu2sc_credit_size.valid := RegNext(io.cacc2sdp.valid & io.cacc2sdp.ready & last_data, false.B)


}}
