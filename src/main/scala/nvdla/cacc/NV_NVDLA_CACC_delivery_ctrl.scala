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
        val dbuf_rd_addr = Output(UInt(conf.CACC_DBUF_AWIDTH.W))
        val dbuf_rd_en = Output(Bool())
        val dbuf_rd_layer_end = Output(Bool())
        val dbuf_wr_addr = Output(UInt(conf.CACC_DBUF_AWIDTH.W))
        val dbuf_wr_data = Output(UInt(conf.CACC_DBUF_WIDTH.W))
        val dbuf_wr_en = Output(Bool())

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
        val dlv_data = Input(UInt(conf.CACC_DBUF_WIDTH.W))
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
    cur_channel := io.cur_channel_w
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
val dbuf_wr_addr = RegInit("b0".asUInt(conf.CACC_DBUF_AWIDTH.W))
val dbuf_wr_en = RegInit(false.B)
val dbuf_wr_data = Reg(UInt(conf.CACC_DBUF_WIDTH.W))

when(io.dlv_valid){
    dbuf_wr_addr_pre := dbuf_wr_addr_pre + 1.U
    dbuf_wr_addr := dbuf_wr_addr_pre
    dbuf_wr_en := io.dlv_valid
    dbuf_wr_data := io.dlv_data
}

///// generate stored data size, add delay for write, due to ecc,could set 0 currently.
val dlv_push_valid = io.dlv_valid
val dlv_push_size = true.B

val in_rt_dat_pd_d = Wire(Bool()) +: 
                        Seq.fill(conf.CMAC_IN_RT_LATENCY)(RegInit("b0".asUInt(9.W)))














}}

