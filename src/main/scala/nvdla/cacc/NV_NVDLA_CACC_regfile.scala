package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver

//Implementation overview of ping-pong register file.

class NV_NVDLA_CACC_regfile extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())      

        //csb2cacc
        val csb2cacc_req_pd = Input(UInt(63.W))
        val csb2cacc_req_pvld = Input(Bool())
        val csb2cacc_req_prdy = Output(Bool())
        
        val cacc2csb_resp_pd = Output(UInt(34.W))
        val cacc2csb_resp_valid = Output(Bool())

        //reg2dp
        val dp2reg_done = Input(Bool())
        val dp2reg_sat_count = Input(UInt(32.W))
        val reg2dp_batches = Output(UInt(5.W))
        val reg2dp_clip_truncate = Output(UInt(5.W))
        val reg2dp_conv_mode = Output(Bool())
        val reg2dp_cya = Output(UInt(32.W))
        val reg2dp_dataout_addr = Output(UInt(32.W))
        val reg2dp_dataout_channel = Output(UInt(13.W))
        val reg2dp_dataout_height = Output(UInt(13.W))
        val reg2dp_dataout_width = Output(UInt(13.W))
        val reg2dp_line_packed = Output(Bool())
        val reg2dp_line_stride = Output(UInt(24.W))
        val reg2dp_op_en = Output(Bool())
        val reg2dp_proc_precision = Output(UInt(2.W))
        val reg2dp_surf_packed = Output(Bool())
        val reg2dp_surf_stride = Output(UInt(24.W))

        //slave cg op
        val slcg_op_en = Output(UInt(7.W))
    })
//                             
//          ┌─┐       ┌─┐
//       ┌──┘ ┴───────┘ ┴──┐
//       │                 │              |-------------|
//       │       ───       │              |     CSB     |
//       │  ─┬┘       └┬─  │              |-------------|
//       │                 │                    ||
//       │       ─┴─       │                    reg   <= DP(data processor)
//       │                 │                    ||
//       └───┐         ┌───┘              |-------------|
//           │         │                  |     CMAC    |
//           │         │                  |-------------|
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

    //Instance single register group
    val dp2reg_consumer = RegInit(false.B)
    val reg.offset = Wire(UInt(12.W))
    val reg.wr_data = Wire(UInt(32.W))
    val s_reg.wr_en = Wire(Bool())
    val dp2reg_status_0 = Wire(Bool())
    val dp2reg_status_1 = Wire(Bool())

    val u_single_reg = Module(new NV_NVDLA_CACC_REG_single)

    u_single_reg.io.reg.offset := reg.offset
    u_single_reg.io.reg.wr_data := reg.wr_data 
    u_single_reg.io.reg.wr_en := s_reg.wr_en
    u_single_reg.io.nvdla_core_clk := io.nvdla_core_clk
    u_single_reg.io.consumer := dp2reg_consumer
    u_single_reg.io.status_0 := dp2reg_status_0
    u_single_reg.io.status_1 := dp2reg_status_1 
    val s_reg.rd_data = u_single_reg.io.reg.rd_data
    val reg2dp_producer = u_single_reg.io.producer

    //Instance two duplicated register groups
    val d0_reg.wr_en = Wire(Bool())
    val reg2dp_d0_op_en = RegInit(false.B)
    val dp2reg_d0_sat_count = RegInit("b0".asUInt(32.W))

    val u_dual_reg_d0 = Module(new NV_NVDLA_CACC_dual_reg)
    u_dual_reg_d0.io.reg.offset := reg.offset
    u_dual_reg_d0.io.reg.wr_data := reg.wr_data
    u_dual_reg_d0.io.reg.wr_en := d0_reg.wr_en
    u_dual_reg_d0.io.nvdla_core_clk := io.nvdla_core_clk
    u_dual_reg_d0.io.op_en := reg2dp_d0_op_en
    u_dual_reg_d0.io.sat_count := dp2reg_d0_sat_count

    val d0_reg.rd_data = u_dual_reg_d0.io.reg.rd_data
    val reg2dp_d0_batches = u_dual_reg_d0.io.batches
    val reg2dp_d0_clip_truncate = u_dual_reg_d0.io.clip_truncate
    val reg2dp_d0_cya = u_dual_reg_d0.io.cya
    val reg2dp_d0_dataout_addr = u_dual_reg_d0.io.dataout_addr
    val reg2dp_d0_line_packed = u_dual_reg_d0.io.line_packed
    val reg2dp_d0_surf_packed = u_dual_reg_d0.io.surf_packed 
    val reg2dp_d0_dataout_height = u_dual_reg_d0.io.dataout_height
    val reg2dp_d0_dataout_width = u_dual_reg_d0.io.dataout_width
    val reg2dp_d0_dataout_channel = u_dual_reg_d0.io.dataout_channel 
    val reg2dp_d0_line_stride = u_dual_reg_d0.io.line_stride  
    val reg2dp_d0_conv_mode = u_dual_reg_d0.io.conv_mode 
    val reg2dp_d0_proc_precision = u_dual_reg_d0.io.proc_precision
    val reg2dp_d0_op_en_trigger = u_dual_reg_d0.io.op_en_trigger 
    val reg2dp_d0_surf_stride = u_dual_reg_d0.io.surf_stride
 

    val d1_reg.wr_en = Wire(Bool())
    val reg2dp_d1_op_en = RegInit(false.B)
    val dp2reg_d1_sat_count = RegInit("b0".asUInt(32.W))

    val u_dual_reg_d1 = Module(new NV_NVDLA_CACC_dual_reg)
    u_dual_reg_d1.io.reg.offset := reg.offset
    u_dual_reg_d1.io.reg.wr_data := reg.wr_data
    u_dual_reg_d1.io.reg.wr_en := d1_reg.wr_en
    u_dual_reg_d1.io.nvdla_core_clk := io.nvdla_core_clk
    u_dual_reg_d1.io.op_en := reg2dp_d1_op_en
    u_dual_reg_d1.io.sat_count := dp2reg_d1_sat_count

    val d1_reg.rd_data = u_dual_reg_d1.io.reg.rd_data
    val reg2dp_d1_batches = u_dual_reg_d1.io.batches
    val reg2dp_d1_clip_truncate = u_dual_reg_d1.io.clip_truncate
    val reg2dp_d1_cya = u_dual_reg_d1.io.cya
    val reg2dp_d1_dataout_addr = u_dual_reg_d1.io.dataout_addr
    val reg2dp_d1_line_packed = u_dual_reg_d1.io.line_packed
    val reg2dp_d1_surf_packed = u_dual_reg_d1.io.surf_packed 
    val reg2dp_d1_dataout_height = u_dual_reg_d1.io.dataout_height
    val reg2dp_d1_dataout_width = u_dual_reg_d1.io.dataout_width
    val reg2dp_d1_dataout_channel = u_dual_reg_d1.io.dataout_channel 
    val reg2dp_d1_line_stride = u_dual_reg_d1.io.line_stride  
    val reg2dp_d1_conv_mode = u_dual_reg_d1.io.conv_mode 
    val reg2dp_d1_proc_precision = u_dual_reg_d1.io.proc_precision
    val reg2dp_d1_op_en_trigger = u_dual_reg_d1.io.op_en_trigger 
    val reg2dp_d1_surf_stride = u_dual_reg_d1.io.surf_stride      

    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // GENERATE CONSUMER PIONTER IN GENERAL SINGLE REGISTER GROUP         //
    //                                                                    //
    //////////////////////////////////////////////////////////////////////// 
    val dp2reg_consumer_w = ~dp2reg_consumer

    when(io.dp2reg_done){
        dp2reg_consumer := dp2reg_consumer_w
    }

    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // GENERATE TWO STATUS FIELDS IN GENERAL SINGLE REGISTER GROUP        //
    //                                                                    //
    ////////////////////////////////////////////////////////////////////////
    dp2reg_status_0 := Mux(reg2dp_d0_op_en === false.B, "h0".asUInt(2.W), 
                       Mux(dp2reg_consumer === true.B, "h2".asUInt(2.W), 
                       "h1".asUInt(2.W)))

    dp2reg_status_1 := Mux(reg2dp_d1_op_en === false.B, "h0".asUInt(2.W), 
                       Mux(dp2reg_consumer === false.B, "h2".asUInt(2.W), 
                       "h1".asUInt(2.W)))

    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // GENERATE OP_EN LOGIC                                               //
    //                                                                    //
    ////////////////////////////////////////////////////////////////////////
    val reg2dp_op_en_reg = RegInit("b0".asUInt(3.W))
    val reg2dp_d0_op_en_w = Mux(~reg2dp_d0_op_en & reg2dp_d0_op_en_trigger, reg.wr_data(0), 
                            Mux(io.dp2reg_done && dp2reg_consumer === false.B, false.B, reg2dp_d0_op_en))

    reg2dp_d0_op_en := reg2dp_d0_op_en_w

    val reg2dp_d1_op_en_w =  Mux(~reg2dp_d1_op_en & reg2dp_d1_op_en_trigger, reg.wr_data(0), 
                             Mux(io.dp2reg_done && dp2reg_consumer === true.B, false.B, reg2dp_d1_op_en))

    reg2dp_d1_op_en := reg2dp_d1_op_en_w

    val reg2dp_op_en_ori = Mux(dp2reg_consumer, reg2dp_d1_op_en, reg2dp_d0_op_en)
    val reg2dp_op_en_reg_w = Mux(io.dp2reg_done,  "b0".asUInt(3.W), Cat(reg2dp_op_en_reg(1,0), reg2dp_op_en_ori))

    reg2dp_op_en_reg := reg2dp_op_en_reg_w 
    io.reg2dp_op_en := reg2dp_op_en_reg(2)

    io.slcg_op_en := ShiftRegister(Fill(7, reg2dp_op_en_ori), 3)
    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // GENERATE ACCESS LOGIC TO EACH REGISTER GROUP                       //
    //                                                                    //
    ////////////////////////////////////////////////////////////////////////
    //EACH subunit has 4KB address space 
    val reg.wr_en = Wire(Bool())
    val select_s = Mux(reg.offset(11,0) < "h0008".asUInt(32.W), true.B, false.B)
    val select_d0 = (reg.offset(11,0) >= "h0008".asUInt(32.W)) & (reg2dp_producer === false.B)
    val select_d1 = (reg.offset(11,0) >= "h0008".asUInt(32.W)) & (reg2dp_producer === true.B)

    s_reg.wr_en := reg.wr_en & select_s
    d0_reg.wr_en := reg.wr_en & select_d0 & !reg2dp_d0_op_en
    d1_reg.wr_en := reg.wr_en & select_d1 & !reg2dp_d1_op_en

    val reg.rd_data = (Fill(32, select_s) & s_reg.rd_data)|
                        (Fill(32, select_d0) & d0_reg.rd_data)|
                        (Fill(32, select_d1)& d1_reg.rd_data)

    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // GENERATE CSB TO REGISTER CONNECTION LOGIC                          //
    //                                                                    //
    ////////////////////////////////////////////////////////////////////////
    val req_pvld = RegInit(false.B)
    val req_pd = RegInit("b0".asUInt(63.W))

    req_pvld := io.csb2cacc_req_pvld
    when(io.csb2cacc_req_pvld){
        req_pd := io.csb2cacc_req_pd
    }

    // PKT_UNPACK_WIRE( csb2xx_16m_be_lvl ,  req_ ,  req_pd ) 
    val req_addr = req_pd(21, 0)
    val req_wdat = req_pd(53, 22)
    val req_write = req_pd(54)
    val req_nposted = req_pd(55)
    val req_srcpriv = req_pd(56)
    val req_wrbe = req_pd(60, 57)
    val req_level = req_pd(62, 61)

    io.csb2cacc_req_prdy := true.B

    //Address in CSB master is word aligned while address in regfile is byte aligned.
    reg.offset := Cat(req_addr, "b0".asUInt(2.W))
    reg.wr_data := req_wdat
    reg.wr_en := req_pvld & req_write
    val reg_rd_en = req_pvld & ~req_write

    // PKT_PACK_WIRE_ID( nvdla_xx2csb_resp ,  dla_xx2csb_rd_erpt ,  csb_rresp_ ,  csb_rresp_pd_w )
    val csb_rresp_rdat = reg.rd_data
    val csb_rresp_error = false.B
    val csb_rresp_pd_w = Cat(false.B, csb_rresp_error, csb_rresp_rdat)

    // PKT_PACK_WIRE_ID( nvdla_xx2csb_resp ,  dla_xx2csb_wr_erpt ,  csb_wresp_ ,  csb_wresp_pd_w 
    val csb_wresp_rdat = "b0".asUInt(32.W)
    val csb_wresp_error = false.B
    val csb_wresp_pd_w = Cat(true.B, csb_wresp_error, csb_wresp_rdat)

    val cacc2csb_resp_pd_out = RegInit("b0".asUInt(34.W))
    val cacc2csb_resp_valid_out = RegInit(false.B)

    when(reg_rd_en){
        cacc2csb_resp_pd_out := csb_rresp_pd_w
    }
    .elsewhen(reg.wr_en & req_nposted){
        cacc2csb_resp_pd_out := csb_wresp_pd_w
    }
    cacc2csb_resp_valid_out := (reg.wr_en & req_nposted) | reg_rd_en

    io.cacc2csb_resp_pd := cacc2csb_resp_pd_out
    io.cacc2csb_resp_valid := cacc2csb_resp_valid_out

    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // GENERATE OUTPUT REGISTER FILED FROM DUPLICATED REGISTER GROUPS     //
    //                                                                    //
    ////////////////////////////////////////////////////////////////////////

    io.reg2dp_batches := Mux(dp2reg_consumer, reg2dp_d1_batches, reg2dp_d0_batches)
    io.reg2dp_clip_truncate := Mux(dp2reg_consumer, reg2dp_d1_clip_truncate, reg2dp_d0_clip_truncate)
    io.reg2dp_cya := Mux(dp2reg_consumer, reg2dp_d1_cya, reg2dp_d0_cya)
    io.reg2dp_dataout_addr := Mux(dp2reg_consumer, reg2dp_d1_dataout_addr, reg2dp_d0_dataout_addr)
    io.reg2dp_line_packed := Mux(dp2reg_consumer, reg2dp_d1_line_packed, reg2dp_d0_line_packed)
    io.reg2dp_surf_packed := Mux(dp2reg_consumer, reg2dp_d1_surf_packed, reg2dp_d0_surf_packed)
    io.reg2dp_dataout_height := Mux(dp2reg_consumer, reg2dp_d1_dataout_height, reg2dp_d0_dataout_height)
    io.reg2dp_dataout_width := Mux(dp2reg_consumer, reg2dp_d1_dataout_width, reg2dp_d0_dataout_width)
    io.reg2dp_dataout_channel := Mux(dp2reg_consumer, reg2dp_d1_dataout_channel, reg2dp_d0_dataout_channel)
    io.reg2dp_line_stride := Mux(dp2reg_consumer, reg2dp_d1_line_stride, reg2dp_d0_line_stride)
    io.reg2dp_conv_mode := Mux(dp2reg_consumer, reg2dp_d1_conv_mode, reg2dp_d0_conv_mode)
    io.reg2dp_proc_precision := Mux(dp2reg_consumer, reg2dp_d1_proc_precision, reg2dp_d0_proc_precision)
    io.reg2dp_surf_stride := Mux(dp2reg_consumer, reg2dp_d1_surf_stride, reg2dp_d0_surf_stride)

    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // PASTE ADDIFITON LOGIC HERE FROM EXTRA FILE                         //
    //                                                                    //
    ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    //  for general counting register                                     //
    ////////////////////////////////////////////////////////////////////////
    val dp2reg_d0_set = reg2dp_d0_op_en & ~reg2dp_d0_op_en_w
    val dp2reg_d0_clr = ~reg2dp_d0_op_en & reg2dp_d0_op_en_w
    val dp2reg_d0_reg = reg2dp_d0_op_en ^ reg2dp_d0_op_en_w

    val dp2reg_d1_set = reg2dp_d1_op_en & ~reg2dp_d1_op_en_w
    val dp2reg_d1_clr = ~reg2dp_d1_op_en & reg2dp_d1_op_en_w
    val dp2reg_d1_reg = reg2dp_d1_op_en ^ reg2dp_d1_op_en_w

    ////////////////////////////////////////////////////////////////////////
    //  for output saturation register                                    //
    ////////////////////////////////////////////////////////////////////////

    //////// group 0 ////////
    val dp2reg_d0_sat_count_w = Mux(dp2reg_d0_set, io.dp2reg_sat_count,
                                Mux(dp2reg_d0_clr, "b0".asUInt(32.W),
                                dp2reg_d0_sat_count))
    when(dp2reg_d0_reg){
        dp2reg_d0_sat_count := dp2reg_d0_sat_count_w
    }
    //////// group 1 ////////
    val dp2reg_d1_sat_count_w = Mux(dp2reg_d1_set, io.dp2reg_sat_count,
                                Mux(dp2reg_d1_clr, "b0".asUInt(32.W),
                                dp2reg_d1_sat_count))
    when(dp2reg_d1_reg){
        dp2reg_d1_sat_count := dp2reg_d1_sat_count_w       
    }


}}

object NV_NVDLA_CACC_regDriver extends App {
  chisel3.Driver.execute(args, () => new NV_NVDLA_CACC_regfile())
}
