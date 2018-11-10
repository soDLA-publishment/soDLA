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



    

    // Address decode

    val nvdla_cmac_a_d_misc_cfg_0_wren = (reg_offset_wr === ("h700c".UInt(32.W)&"h00000fff".UInt(32.W)))&io.reg_wr_en
    val nvdla_cmac_a_d_op_enable_0_wren = (reg_offset_wr === ("h7008".UInt(32.W)&"h00000fff".UInt(32.W)))&io.reg_wr_en
    nvdla_cmac_a_d_misc_cfg_0_out := Cat("b0".UInt(18.W), proc_precision, "b0".UInt(11.W), conv_mode)
    nvdla_cmac_a_d_op_enable_0_outt:=  Cat("b0".UInt(31.W), op_en)

    reg_offset_rd_int := io.reg_offset

    when(reg_offset_rd_int === ("h700c".UInt(32.W)&"h00000fff".UInt(32.W)){
        io.reg_rd_data = nvdla_cmac_a_d_misc_cfg_0_out 
    }
    .elsewhen(reg_offset_rd_int === ("h7008".UInt(32.W)&"h00000fff".UInt(32.W)){
        io.reg_rd_data := nvdla_cmac_a_d_op_enable_0_out
    }
    .otherwise{
        io.reg_rd_data := "b0".UInt(32.W)
    }

    withClock(io.nvdla_core_clk|!io.nvdla_core_rstn.asBool){
        when(!io.nvdla_core_rstn){
            io.conv_mode := false.B
            io.proc_precision := "b01".UInt(2.W)
        }
        .otherwise{
            when(nvdla_cmac_a_s_pointer_0_wren){
                io.proc_precision:= io.reg_wr_data(0)
            }
            when(nvdla_cmac_a_d_op_enable_0_wren){
                io.proc_precision:=io.reg_wr_data(13,12)
            }
        }
    }

}