package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

//this module is to process dat

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
        val reg2dp_op_en = Input(Bool()) 
        val reg2dp_conv_mode = Input(Bool())
        val reg2dp_proc_precision = Input(UInt(2.W))
        val reg2dp_dataout_width = Input(UInt(13.W))
        val reg2dp_dataout_height = Input(UInt(13.W))
        val reg2dp_dataout_channel = Input(UInt(13.W))
        val reg2dp_dataout_addr = Input(UInt((32-conf.NVDLA_MEMORY_ATOMIC_LOG2).W))
        val reg2dp_line_packed = Input(Bool())
        val reg2dp_surf_packed = Input(Bool())
        val reg2dp_batches = Input(UInt(5.W))
        val reg2dp_line_stride = Input(UInt(24.W))
        val reg2dp_surf_stride = Input(UInt(24.W))
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
///// register input signal from regfile                 /////
//////////////////////////////////////////////////////////////
val cur_channel_w = io.reg2dp_dataout_channel(conf.CACC_CHANNEL_BITS-1, 5)

val cur_op_en = RegInit(false.B)
val cur_conv_mode = RegInit(false.B)
val cur_proc_precision = RegInit("b0".asUInt(2.W))
val cur_width = RegInit("b0".asUInt(13.W))
val cur_height = RegInit("b0".asUInt(13.W))
val cur_channel = RegInit("b0".asUInt((conf.CACC_CHANNEL_BITS-5).W))
val cur_dataout_addr = RegInit("b0".asUInt((32-conf.NVDLA_MEMORY_ATOMIC_LOG2).W))
val cur_batches = RegInit("b0".asUInt(5.W))
val cur_line_stride = RegInit("b0".asUInt(24.W))
val cur_surf_stride = RegInit("b0".asUInt(24.W))
val cur_line_packed = RegInit(false.B)
val cur_surf_packed = RegInit(false.B)

when(io.wait_for_op_en){
    cur_op_en := io.reg2dp_op_en
}
when(io.wait_for_op_en & io.reg2dp_op_en){
    cur_conv_mode := io.reg2dp_conv_mode
    cur_proc_precision := io.reg2dp_proc_precision
    cur_width := io.reg2dp_dataout_width
    cur_height := io.reg2dp_dataout_height
    cur_channel := cur_channel_w
    cur_dataout_addr := io.reg2dp_dataout_addr
    cur_batches := io.reg2dp_batches
    cur_line_stride := io.reg2dp_line_stride
    cur_surf_stride := io.reg2dp_surf_stride
    cur_line_packed := io.reg2dp_line_packed
    cur_surf_packed := io.reg2dp_surf_packed
}

//////////////////////////////////////////////////////////////
///// generate current status signals                    /////
//////////////////////////////////////////////////////////////
val is_int8_w = (io.reg2dp_proc_precision === conf.NVDLA_CACC_D_MISC_CFG_0_PROC_PRECISION_INT8.U)
val is_int8 = (cur_proc_precision === conf.NVDLA_CACC_D_MISC_CFG_0_PROC_PRECISION_INT8.U)
val is_winograd = false.B

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