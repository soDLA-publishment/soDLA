package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

//this module is to process dat

@chiselName
class NV_NVDLA_CACC_delivery_ctrl(implicit conf: caccConfiguration) extends Module {

    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())

        //dbuf
        val dbuf_rd_ready = Input(Bool())
        val dbuf_rd_addr = ValidIO(UInt(conf.CACC_DBUF_AWIDTH.W))
        val dbuf_rd_layer_end = Output(Bool())
        val dbuf_wr = new nvdla_wr_if(conf.CACC_ABUF_AWIDTH, conf.CACC_ABUF_WIDTH)

        //reg2dp
        val dp2reg_done = Output(Bool())

        //dlv
        val dlv_data = Input(Vec(conf.CACC_ATOMK, UInt(conf.CACC_FINAL_WIDTH.W)))
        val dlv_mask = Input(Bool())
        val dlv_pd = Input(UInt(2.W))
        val dlv_valid = Input(Bool())

        //wait for op
        val wait_for_op_en = Input(Bool())
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

//////////////////////////////////////////////////////////////
///// parse input status signal                          /////
//////////////////////////////////////////////////////////////
val dlv_stripe_end = io.dlv_pd(0)
val dlv_layer_end = io.dlv_pd(1)

//////////////////////////////////////////////////////////////
///// generate write signal, 1 pipe for write data
//////////////////////////////////////////////////////////////
val dbuf_wr_addr_pre = RegInit("b0".asUInt(conf.CACC_DBUF_AWIDTH.W))
val dbuf_wr_addr_out = RegInit("b0".asUInt(conf.CACC_DBUF_AWIDTH.W))
val dbuf_wr_en_out = RegInit(false.B)
val dbuf_wr_data_out = Reg(UInt(conf.CACC_DBUF_WIDTH.W))

val dbuf_wr_addr_w = dbuf_wr_addr_pre + 1.U

dbuf_wr_en_out := io.dlv_valid
when(io.dlv_valid){
    dbuf_wr_addr_pre := dbuf_wr_addr_w
    dbuf_wr_addr_out := dbuf_wr_addr_pre
    dbuf_wr_data_out := io.dlv_data.asUInt
}

io.dbuf_wr.addr.valid := dbuf_wr_en_out
io.dbuf_wr.addr.bits := dbuf_wr_addr_out
io.dbuf_wr.data := dbuf_wr_data_out


///// generate stored data size, add delay for write, due to ecc,could set 0 currently.
val dlv_push_valid = io.dlv_valid
val dlv_push_size = true.B

val dlv_push_valid_d = Wire(Bool()) +: 
                       Seq.fill(conf.CACC_D_RAM_WRITE_LATENCY)(RegInit(false.B))
val dlv_push_size_d = Wire(Bool()) +: 
                      Seq.fill(conf.CACC_D_RAM_WRITE_LATENCY)(RegInit(false.B))

dlv_push_valid_d(0) := dlv_push_valid
dlv_push_size_d(0) := dlv_push_size

for(t <- 0 to conf.CACC_D_RAM_WRITE_LATENCY-1){
    dlv_push_valid_d(t+1) := dlv_push_valid_d(t)
    when(dlv_push_valid_d(t)){
        dlv_push_size_d(t+1) := dlv_push_size_d(t)
    }    
}

val dlv_data_add_valid = dlv_push_valid_d(conf.CACC_D_RAM_WRITE_LATENCY)
val dlv_data_add_size = dlv_push_size_d(conf.CACC_D_RAM_WRITE_LATENCY)

//// dbuffer data counter 
val dlv_pop = Wire(Bool())
val dlv_data_avl = RegInit("b0".asUInt(conf.CACC_DBUF_DEPTH.W))

val dlv_data_avl_add = Mux(dlv_data_add_valid, dlv_data_add_size, "b0".asUInt(1.W))
val dlv_data_avl_sub = Mux(dlv_pop, "b1".asUInt(1.W), "b0".asUInt(1.W))
val dlv_data_sub_valid = dlv_pop
val dlv_data_avl_w = dlv_data_avl + dlv_data_avl_add - dlv_data_avl_sub

when(dlv_data_add_valid | dlv_data_sub_valid){
    dlv_data_avl := dlv_data_avl_w
}

///// generate dbuf read request   
val dbuf_rd_addr_cnt = RegInit("b0".asUInt(conf.CACC_DBUF_AWIDTH.W))

dlv_pop := io.dbuf_rd_addr.valid & io.dbuf_rd_ready
val dbuf_rd_addr_cnt_inc = dbuf_rd_addr_cnt + 1.U
val dbuf_empty = ~(dlv_data_avl.orR)
io.dbuf_rd_addr.valid := ~dbuf_empty
io.dbuf_rd_addr.bits := dbuf_rd_addr_cnt

when(dlv_pop){
    dbuf_rd_addr_cnt := dbuf_rd_addr_cnt_inc
}

/////// generate dp2reg_done signal
val dp2reg_done_w = io.dlv_valid & dlv_stripe_end & dlv_layer_end
io.dp2reg_done := RegNext(dp2reg_done_w, false.B)

/////// generate output package for sdp   
val dlv_end_tag0_vld = RegInit(false.B)
val dlv_end_tag1_vld = RegInit(false.B)
val dlv_end_tag0_addr = RegInit("b0".asUInt(conf.CACC_DBUF_AWIDTH.W))
val dlv_end_tag1_addr = RegInit("b0".asUInt(conf.CACC_DBUF_AWIDTH.W))

val dlv_end_set = io.dlv_valid & dlv_stripe_end & dlv_layer_end
val dlv_end_addr_w = dbuf_wr_addr_pre
val dlv_end_clr = dlv_pop & (io.dbuf_rd_addr.bits === dlv_end_tag0_addr) & dlv_end_tag0_vld
val dlv_end_tag0_vld_w = Mux(dlv_end_tag1_vld | dlv_end_set, true.B, Mux(dlv_end_clr, false.B, dlv_end_tag0_vld))
val dlv_end_tag1_vld_w = Mux(dlv_end_tag0_vld | dlv_end_set, true.B, Mux(dlv_end_clr, false.B, dlv_end_tag1_vld))
val dlv_end_tag0_en = (dlv_end_set & ~dlv_end_tag0_vld) | (dlv_end_set & dlv_end_clr) |(dlv_end_clr & dlv_end_tag1_vld);
val dlv_end_tag1_en = (dlv_end_set & dlv_end_tag0_vld & ~dlv_end_clr);
val dlv_end_tag0_addr_w = Mux(dlv_end_tag1_vld, dlv_end_tag1_addr, dlv_end_addr_w)
val dlv_end_tag1_addr_w = dlv_end_addr_w
io.dbuf_rd_layer_end := dlv_end_clr

dlv_end_tag0_vld := dlv_end_tag0_vld_w
dlv_end_tag1_vld := dlv_end_tag1_vld_w
when(dlv_end_tag0_en){
    dlv_end_tag0_addr := dlv_end_tag0_addr_w
}
when(dlv_end_tag1_en){
    dlv_end_tag1_addr := dlv_end_tag1_addr_w
}


}}


object NV_NVDLA_CACC_delivery_ctrlDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_CACC_delivery_ctrl())
}

