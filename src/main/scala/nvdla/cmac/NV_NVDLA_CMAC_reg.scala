package nvdla

import chisel3._




class NV_NVDLA_CMAC_REG_dual(implicit val conf: cmacConfiguration) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())      
        val nvdla_core_rstn = Input(Bool())

        val csb2cmac_a_req_pd = Input(UInt(63.W))
        val csb2cmac_a_req_pvld = Input(Bool())
        val dp2reg_done = Input(Bool())

        val cmac_a2csb_resp_pd = Output(UInt(34.W))
        val cmac_a2csb_resp_valid = Output(Bool())
        val csb2cmac_a_req_prdy = Output(Bool())
        val reg2dp_conv_mode = Output(Bool())
        val reg2dp_op_en = Output(Bool())
        val reg2dp_proc_precision = Output(UInt(2.W))
        val slcg_op_en = Output(UInt(conf.CMAC_SLCG_NUM))
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

    val csb_rresp_error = Wire(Bool())
    val csb_rresp_pd_w = Wire(UInt(34.W))
    val csb_rresp_rdat = Wire(UInt(32.W))
    val csb_wresp_error = Wire(Bool())
    val csb_wresp_pd_w = Wire(UInt(34.W))
    val csb_wresp_rdat = Wire(UInt(32.W))
    val d0_reg_offset = Wire(UInt(24.W))
    val d0_reg_rd_data = Wire(UInt(32.W))
    val d0_reg_wr_data = Wire(UInt(32.W))
    val d0_reg_wr_en = Wire(Bool())
    val d1_reg_offset = Wire(UInt(24.W))
    val d1_reg_rd_data = Wire(UInt(32.W))
    val d1_reg_wr_data = Wire(UInt(32.W))
    val d1_reg_wr_en = Wire(Bool())
    val dp2reg_consumer_w = Wire(Bool())
    val reg2dp_d0_conv_mode = Wire(Bool())
    val reg2dp_d0_op_en_trigger = Wire(Bool())
    val reg2dp_d0_proc_precision = Wire(UInt(2.W))
    val reg2dp_d1_conv_mode = Wire(Bool())
    val reg2dp_d1_op_en_trigger = Wire(Bool())
    val reg2dp_d1_proc_precision = Wire(UInt(2.W))
    val reg2dp_op_en_reg_w = Wire(UInt(3.W))
    val reg2dp_producer = Wire(Bool())
    val reg_offset = Wire(UInt(24.W))
    val reg_rd_data = Wire(UInt(32.W))
    val reg_rd_en = Wire(Bool())
    val reg_wr_data = Wire(UInt(32.W))
    val reg_wr_en = Wire(Bool())
    val req_addr = Wire(UInt(22.W))
    val req_level = Wire(UInt(2.W))
    val req_nposted = Wire(Bool())
    val req_srcpriv = Wire(Bool())
    val req_wdat = Wire(UInt(32.W))
    val req_wrbe = Wire(UInt(4.W))
    val req_write = Wire(Bool())
    val s_reg_offset = Wire(UInt(24.W))
    val s_reg_rd_data = Wire(UInt(32.W))
    val s_reg_wr_data = Wire(UInt(32.W))
    val s_reg_wr_en = Wire(Bool())
    val select_d0 = Wire(Bool())
    val select_d1 = Wire(Bool())
    val select_s = Wire(Bool())
    val slcg_op_en_d0 = Wire(UInt(11.W))
    io.cmac_a2csb_resp_pd := Reg(UInt(34.W))
    io.cmac_a2csb_resp_valid := Reg(Bool())
    val dp2reg_consumer = Reg(Bool())
    val dp2reg_status_0 = Wire(UInt(2.W))
    val dp2reg_status_1 = Wire(UInt(2.W))
    io.reg2dp_conv_mode := Reg(Bool())
    val reg2dp_d0_op_en = Reg(Bool())
    val reg2dp_d0_op_en_w = Wire(Bool())
    val reg2dp_d1_op_en = Reg(Bool())
    val reg2dp_d1_op_en_w = Wire(Bool())
    val reg2dp_op_en_ori = Wire(Bool())
    val reg2dp_op_en_reg = Reg(UInt(3.W))
    io.reg2dp_proc_precision = Reg(UInt(2.W))
    val req_pd = Reg(UInt(63.W))
    val req_pvld = Reg(Bool())
    val slcg_op_en_d1 = Reg(UInt(11.W))
    val slcg_op_en_d2 = Reg(UInt(11.W))
    val slcg_op_en_d3 = Reg(UInt(11.W))

    //Instance single register group

    val u_single_reg = Module(new NV_NVDLA_CMAC_REG_single)
    u_single_reg.io.reg_rd_data := s_reg_rd_dat
    u_single_reg.io.reg_offset := s_reg_offset
    u_single_reg.io.reg_wr_data := s_reg_wr_data 
    u_single_reg.io.reg_wr_en := s_reg_wr_en
    u_single_reg.io.nvdla_core_clk := io.nvdla_core_clk
    u_single_reg.io.nvdla_core_rstn := io.nvdla_core_rstn
    u_single_reg.io.producer := io.reg2dp_producer
    u_single_reg.io.consumer := io.dp2reg_consumer
    u_single_reg.io.status_0 := dp2reg_status_0
    u_single_reg.io.status_1 := dp2reg_status_1 

    //Instance two duplicated register groups

    val u_dual_reg_d0 = Module(new NV_NVDLA_CMAC_REG_dual)
    u_dual_reg_d0.io.reg_rd_data := d0_reg_rd_data
    u_dual_reg_d0.io.reg_offset := d0_reg_offset
    u_dual_reg_d0.io.reg_wr_data := d0_reg_wr_data
    u_dual_reg_d0.io.reg_wr_en := d0_reg_wr_en
    u_dual_reg_d0.io.nvdla_core_clk := io.nvdla_core_clk
    u_dual_reg_d0.io.nvdla_core_rstn := io.nvdla_core_rstn
    u_dual_reg_d0.io.conv_mode := reg2dp_d0_conv_mode
    u_dual_reg_d0.io.proc_precision := reg2dp_d0_proc_precision
    u_dual_reg_d0.io.op_en_trigger := reg2dp_d0_op_en_trigger
    u_dual_reg_d0.io.op_en := reg2dp_d0_op_en

    val u_dual_reg_d1 = Module(new NV_NVDLA_CMAC_REG_dual)
    u_dual_reg_d1.io.reg_rd_data := d1_reg_rd_data
    u_dual_reg_d1.io.reg_offset := d1_reg_offset
    u_dual_reg_d1.io.reg_wr_data := d1_reg_wr_data
    u_dual_reg_d1.io.reg_wr_en := d1_reg_wr_en
    u_dual_reg_d1.io.nvdla_core_clk := io.nvdla_core_clk
    u_dual_reg_d1.io.nvdla_core_rstn := io.nvdla_core_rstn
    u_dual_reg_d1.io.conv_mode := reg2dp_d1_conv_mode
    u_dual_reg_d1.io.proc_precision := reg2dp_d1_proc_precision
    u_dual_reg_d1.io.op_en_trigger := reg2dp_d1_op_en_trigger
    u_dual_reg_d1.io.op_en := reg2dp_d1_op_en 

    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // GENERATE CONSUMER PIONTER IN GENERAL SINGLE REGISTER GROUP         //
    //                                                                    //
    //////////////////////////////////////////////////////////////////////// 

    dp2reg_consumer_w := !dp2reg_consumer
    withClockAndReset(io.nvdla_core_clk, !nvdla_core_rstn){
        when(io.dp2reg_done){
            dp2reg_consumer:=dp2reg_consumer_w
        }
    }

    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // GENERATE TWO STATUS FIELDS IN GENERAL SINGLE REGISTER GROUP        //
    //                                                                    //
    ////////////////////////////////////////////////////////////////////////

    dp2reg_status_0 := Mux(reg2dp_d0_op_en === false.B, "h0".UInt(2.W), Mux(dp2reg_consumer, "h0".UInt(2.W), "h1".UInt(2.W)))
    dp2reg_status_1 := Mux(reg2dp_d1_op_en === false.B, "h0".UInt(2.W), Mux(dp2reg_consumer === false.Bool, "h2".UInt(2.W), "h1".UInt(2.W)))

    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // GENERATE OP_EN LOGIC                                               //
    //                                                                    //
    ////////////////////////////////////////////////////////////////////////

    reg2dp_d0_op_en_w := Mux(!reg2dp_d0_op_en & reg2dp_d0_op_en_trigger, reg_wr_data(0), Mux(io.dp2reg_done && dp2reg_consumer === false.B, false.B, reg2dp_d0_op_en))
    withClockAndReset(io.nvdla_core_clk, !nvdla_core_rstn){
        reg2dp_d0_op_en:=reg2dp_d0_op_en_w
    }
    reg2dp_d1_op_en_w :=  Mux(!reg2dp_d1_op_en & reg2dp_d1_op_en_trigger, reg_wr_data(0), Mux(io.dp2reg_done && dp2reg_consumer === true.B, false.B, reg2dp_d1_op_en))
    withClockAndReset(io.nvdla_core_clk, !nvdla_core_rstn){
        reg2dp_d1_op_en:=reg2dp_d1_op_en_w
    }
    reg2dp_op_en_ori := Mux(dp2reg_consumer, reg2dp_d1_op_en, reg2dp_d0_op_en)

    reg2dp_op_en_reg_w := Mux(io.dp2reg_done,  "b0".asUInt(3.W), Cat(reg2dp_op_en_reg(1,0), reg2dp_op_en_ori.asUInt))

    withClockAndReset(io.nvdla_core_clk, !nvdla_core_rstn){
        reg2dp_op_en_reg:=reg2dp_op_en_reg_w
    }

    io.reg2dp_op_en := reg2dp_op_en_reg(3-1)

    slcg_op_en_d0 := Fill(11, reg2dp_op_en_ori.asUInt)

    withClockAndReset(io.nvdla_core_clk, !nvdla_core_rstn){
        slcg_op_en_d1:=slcg_op_en_d0
    }   
    withClockAndReset(io.nvdla_core_clk, !nvdla_core_rstn){
        slcg_op_en_d2:=slcg_op_en_d1
    }
    withClockAndReset(io.nvdla_core_clk, !nvdla_core_rstn){
        slcg_op_en_d3:=slcg_op_en_d2
    }

    io.slcg_op_en := slcg_op_en_d3

    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // GENERATE ACCESS LOGIC TO EACH REGISTER GROUP                       //
    //                                                                    //
    ////////////////////////////////////////////////////////////////////////
    //EACH subunit has 4KB address space

    select_s := Mux(reg_offset(11,0) < ("h7008".asUInt(32.W)  & "hfff".asUInt(32.W), true.B, false.B)
    select_d0 :=(reg_offset(11,0) >= ("h7008".asUInt(32.W) & "hfff".asUInt(32.W)))&(reg2dp_producer === false.B)
    select_d1 := (reg_offset(11,0) >= ("h7008".asUInt(32.W)  & "hfff".asUInt(32.W)))&(reg2dp_producer === true.B)

    s_reg_wr_en := reg_wr_en & select_s
    d0_reg_wr_en := reg_wr_en & select_d0 & !reg2dp_d0_op_en
    d1_reg_wr_en = reg_wr_en & select_d1 & !reg2dp_d1_op_en

    s_reg_offset := reg_offset
    d0_reg_offset := reg_offset
    d1_reg_offset := reg_offset

    s_reg_wr_data := reg_wr_data
    d0_reg_wr_data := reg_wr_data
    d1_reg_wr_data := reg_wr_data

    reg_rd_data := (Fill(32, select_s)&s_reg_rd_data)|(Fill(32, select_d0)& d0_reg_rd_data)|(Fill(32, select_d1)& d1_reg_rd_data)

    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // GENERATE CSB TO REGISTER CONNECTION LOGIC                          //
    //                                                                    //
    ////////////////////////////////////////////////////////////////////////

    withClockAndReset(io.nvdla_core_clk, !nvdla_core_rstn){
        req_pvld:=csb2cmac_a_req_pvld
    }
    withClockAndReset(io.nvdla_core_clk, !nvdla_core_rstn){
        when(csb2cmac_a_req_pvld){
            req_pd:=csb2cmac_a_req_pd
        }
    }

    // PKT_UNPACK_WIRE( csb2xx_16m_be_lvl ,  req_ ,  req_pd ) 

    req_addr := req_pd(21, 0)
    req_wdat := req_pd(53, 22)
    req_write := req_pd(54)
    req_nposted := req_pd(55)
    req_srcpriv := req_pd(56)
    req_wrbe := req_pd(60, 57)
    req_level := req_pd(62, 61)

    io.csb2cmac_a_req_prdy := true.B

    //Address in CSB master is word aligned while address in regfile is byte aligned.

    reg_offset := Cat(req_addr, "b0".UInt(2.W))
    reg_wr_data := req_wdat
    reg_wr_en := req_pvld&req_write
    reg_rd_en := req_pvld&!req_write

    // PKT_PACK_WIRE_ID( nvdla_xx2csb_resp ,  dla_xx2csb_rd_erpt ,  csb_rresp_ ,  csb_rresp_pd_w )
    csb_rresp_pd_w(31, 0) := csb_rresp_rdat
    csb_rresp_pd_w(32) := csb_rresp_error

    csb_rresp_pd_w(33) := false.B 

    // PKT_PACK_WIRE_ID( nvdla_xx2csb_resp ,  dla_xx2csb_wr_erpt ,  csb_wresp_ ,  csb_wresp_pd_w 
    csb_wresp_pd_w(31, 0) := csb_wresp_rdat
    csb_wresp_pd_w(32) := csb_wresp_error

    csb_wresp_pd_w(33) := true.B

    csb_rresp_rdat := reg_rd_data
    csb_rresp_error := false.B
    csb_wresp_rdat := "b0".asUInt(32.W)
    csb_wresp_error := false.B

    withClockAndReset(io.nvdla_core_clk, !nvdla_core_rstn){
        when(reg_rd_en){
            io.cmac_a2csb_resp_pd := csb_rresp_pd_w
        }
        .elsewhen(reg_wr_en & req_nposted){
            io.cmac_a2csb_resp_pd := csb_wresp_pd_w
        }

        io.cmac_a2csb_resp_valid := (reg_wr_en & req_nposted) | reg_rd_en
    }

    ////////////////////////////////////////////////////////////////////////
    //                                                                    //
    // GENERATE OUTPUT REGISTER FILED FROM DUPLICATED REGISTER GROUPS     //
    //                                                                    //
    ////////////////////////////////////////////////////////////////////////

    io.reg2dp_conv_mode := Mux(dp2reg_consumer, reg2dp_d1_conv_mode, reg2dp_d0_conv_mode)
    io.reg2dp_proc_precision := Mux(dp2reg_consumer, reg2dp_d1_proc_precision, reg2dp_d0_proc_precision)



}