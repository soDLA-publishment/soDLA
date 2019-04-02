package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver

class NV_NVDLA_cacc(implicit conf: caccConfiguration) extends Module {
    val io = IO(new Bundle {
        // clk
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

        //csb2cacc
        val csb2cacc_req_pvld = Input(Bool())   /* data valid */
        val csb2cacc_req_prdy = Output(Bool())  /* data return handshake */
        val csb2cacc_req_pd = Input(UInt(63.W))
        val cacc2csb_resp_valid = Output(Bool())    /* data valid */
        val cacc2csb_resp_pd = Output(UInt(34.W))   /* pkt_id_width=1 pkt_widths=33,33  */

        //mac
        val mac_a2accu_pvld = Input(Bool())
        val mac_a2accu_mode = Input(Bool())
        val mac_a2accu_mask = Input(Vec(conf.CACC_ATOMK/2, Bool()))
        val mac_a2accu_data = Input(Vec(conf.CACC_ATOMK/2, SInt(conf.CACC_IN_WIDTH.W)))
        val mac_a2accu_pd = Input(UInt(9.W))

        val mac_b2accu_pvld = Input(Bool())
        val mac_b2accu_mode = Input(Bool())
        val mac_b2accu_mask = Input(Vec(conf.CACC_ATOMK/2, Bool()))
        val mac_b2accu_data = Input(Vec(conf.CACC_ATOMK/2, SInt(conf.CACC_IN_WIDTH.W)))
        val mac_b2accu_pd = Input(UInt(9.W))

        //sdp
        val cacc2sdp_valid = Output(Bool())  /* data valid */
        val cacc2sdp_ready = Input(Bool())  /* data return handshake */
        val cacc2sdp_pd = Output(UInt(conf.CACC_SDP_WIDTH.W))

        //csc
        val accu2sc_credit_vld = Output(Bool())
        val accu2sc_credit_size = Output(UInt(3.W))

        //glb
        val cacc2glb_done_intr_pd = Output(UInt(2.W)) 

        //Port for SLCG 
        val dla_clk_ovr_on_sync = Input(Clock())
        val global_clk_ovr_on_sync = Input(Clock())
        val tmc2slcg_disable_clock_gating = Input(Bool())

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
withReset(!io.nvdla_core_rstn){

    val abuf_rd_addr = Wire(UInt(conf.CACC_ABUF_AWIDTH.W))
    val abuf_rd_data = Wire(UInt(conf.CACC_ABUF_WIDTH.W))
    val abuf_rd_en = Wire(Bool())
    val abuf_wr_addr = Wire(UInt(conf.CACC_ABUF_AWIDTH.W))
    val abuf_wr_data = Wire(UInt(conf.CACC_ABUF_WIDTH.W))
    val abuf_wr_en = Wire(Bool())
    val accu_ctrl_pd = Wire(UInt(13.W))
    val accu_ctrl_ram_valid = Wire(Bool())
    val accu_ctrl_valid = Wire(Bool())
    val cfg_in_en_mask = Wire(Bool())
    val cfg_is_wg = Wire(Bool())
    val cfg_truncate = Wire(UInt(5.W))
    val dbuf_rd_addr = Wire(UInt(conf.CACC_DBUF_AWIDTH.W))
    val dbuf_rd_en = Wire(Bool())
    val dbuf_rd_layer_end = Wire(Bool())
    val dbuf_rd_ready = Wire(Bool())
    val dbuf_wr_addr = Wire(UInt(conf.CACC_DBUF_AWIDTH.W))
    val dbuf_wr_data = Wire(UInt(conf.CACC_ABUF_WIDTH.W))
    val dbuf_wr_en = Wire(Bool())
    val dlv_data = Wire(Vec(conf.CACC_ATOMK, SInt(conf.CACC_FINAL_WIDTH.W)))
    val dlv_mask = Wire(Bool())
    val dlv_pd = Wire(UInt(2.W))
    val dlv_valid = Wire(Bool())
    val dp2reg_done = Wire(Bool())
    val dp2reg_sat_count = Wire(UInt(32.W))

    val slcg_cell_en = Wire(Bool())
    val wait_for_op_en = Wire(Bool())

    val nvdla_cell_gated_clk = Wire(Clock())
    val nvdla_op_gated_clk = Wire(Vec(3, Clock()))




    //==========================================================
    // Regfile
    //==========================================================

    val u_regfile = Module(new NV_NVDLA_CACC_regfile)

    u_regfile.io.nvdla_core_clk := io.nvdla_core_clk               

    u_regfile.io.csb2cacc_req_pd := io.csb2cacc_req_pd               
    u_regfile.io.csb2cacc_req_pvld := io.csb2cacc_req_pvld        
    u_regfile.io.dp2reg_done := dp2reg_done        
    u_regfile.io.dp2reg_sat_count := dp2reg_sat_count 

    io.cacc2csb_resp_pd := u_regfile.io.cacc2csb_resp_pd
    io.cacc2csb_resp_valid := u_regfile.io.cacc2csb_resp_valid
    io.csb2cacc_req_prdy := u_regfile.io.csb2cacc_req_prdy

    val reg2dp_batches = u_regfile.io.reg2dp_batches
    val reg2dp_clip_truncate = u_regfile.io.reg2dp_clip_truncate
    val reg2dp_conv_mode = u_regfile.io.reg2dp_conv_mode
    val reg2dp_cya = u_regfile.io.reg2dp_cya
    val reg2dp_dataout_addr = u_regfile.io.reg2dp_dataout_addr
    val reg2dp_dataout_channel = u_regfile.io.reg2dp_dataout_channel
    val reg2dp_dataout_height = u_regfile.io.reg2dp_dataout_height
    val reg2dp_dataout_width = u_regfile.io.reg2dp_dataout_width
    val reg2dp_line_packed = u_regfile.io.reg2dp_line_packed
    val reg2dp_line_stride = u_regfile.io.reg2dp_line_stride
    val reg2dp_op_en = u_regfile.io.reg2dp_op_en
    val reg2dp_proc_precision = u_regfile.io.reg2dp_proc_precision
    val reg2dp_surf_packed = u_regfile.io.reg2dp_surf_packed
    val reg2dp_surf_stride = u_regfile.io.reg2dp_surf_stride
    val slcg_op_en = u_regfile.io.slcg_op_en
    
    //==========================================================
    // Assembly controller
    //==========================================================

    val u_assembly_ctrl = Module(new NV_NVDLA_CACC_assembly_ctrl)

    u_assembly_ctrl.io.nvdla_core_clk := nvdla_op_gated_clk(0)                

    u_assembly_ctrl.io.reg2dp_op_en := reg2dp_op_en     
    u_assembly_ctrl.io.reg2dp_conv_mode := reg2dp_conv_mode           
    u_assembly_ctrl.io.reg2dp_proc_precision := reg2dp_proc_precision
    u_assembly_ctrl.io.reg2dp_clip_truncate := reg2dp_clip_truncate

    u_assembly_ctrl.io.dp2reg_done := dp2reg_done
    u_assembly_ctrl.io.mac_a2accu_pd := io.mac_a2accu_pd
    u_assembly_ctrl.io.mac_a2accu_pvld := io.mac_a2accu_pvld
    u_assembly_ctrl.io.mac_b2accu_pd := io.mac_b2accu_pd
    u_assembly_ctrl.io.mac_b2accu_pvld := io.mac_b2accu_pvld 

    abuf_rd_addr := u_assembly_ctrl.io.abuf_rd_addr
    abuf_rd_en := u_assembly_ctrl.io.abuf_rd_en
    accu_ctrl_pd := u_assembly_ctrl.io.accu_ctrl_pd
    accu_ctrl_ram_valid := u_assembly_ctrl.io.accu_ctrl_ram_valid
    accu_ctrl_valid := u_assembly_ctrl.io.accu_ctrl_valid
    cfg_in_en_mask := u_assembly_ctrl.io.cfg_in_en_mask
    cfg_is_wg := u_assembly_ctrl.io.cfg_is_wg
    cfg_truncate := u_assembly_ctrl.io.cfg_truncate
    slcg_cell_en := u_assembly_ctrl.io.slcg_cell_en
    wait_for_op_en := u_assembly_ctrl.io.wait_for_op_en

    //==========================================================
    // Assembly buffer
    //==========================================================
    
    val u_assembly_buffer = Module(new NV_NVDLA_CACC_assembly_buffer)

    u_assembly_buffer.io.nvdla_core_clk := nvdla_op_gated_clk(1)
    u_assembly_buffer.io.abuf_rd_addr := abuf_rd_addr
    u_assembly_buffer.io.abuf_rd_en := abuf_rd_en
    u_assembly_buffer.io.abuf_wr_addr := abuf_wr_addr
    u_assembly_buffer.io.abuf_wr_data := abuf_wr_data
    u_assembly_buffer.io.abuf_wr_en := abuf_wr_en
    u_assembly_buffer.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    abuf_rd_data := u_assembly_buffer.io.abuf_rd_data



    //==========================================================
    // CACC calculator
    //==========================================================

    val u_calculator = Module(new NV_NVDLA_CACC_calculator)

    u_calculator.io.nvdla_cell_clk := nvdla_cell_gated_clk
    u_calculator.io.nvdla_core_clk := nvdla_op_gated_clk(2)
    u_calculator.io.abuf_rd_data := abuf_rd_data
    u_calculator.io.accu_ctrl_pd := accu_ctrl_pd
    u_calculator.io.accu_ctrl_ram_valid := accu_ctrl_ram_valid
    u_calculator.io.accu_ctrl_valid := accu_ctrl_valid
    u_calculator.io.cfg_in_en_mask := cfg_in_en_mask
    u_calculator.io.cfg_is_wg := cfg_is_wg
    u_calculator.io.cfg_truncate := cfg_truncate
    u_calculator.io.mac_a2accu_data := io.mac_a2accu_data
    u_calculator.io.mac_a2accu_mask := io.mac_a2accu_mask
    u_calculator.io.mac_a2accu_mode := io.mac_a2accu_mode
    u_calculator.io.mac_a2accu_pvld := io.mac_a2accu_pvld
    u_calculator.io.mac_b2accu_data := io.mac_b2accu_data
    u_calculator.io.mac_b2accu_mask := io.mac_b2accu_mask
    u_calculator.io.mac_b2accu_mode := io.mac_b2accu_mode
    u_calculator.io.mac_b2accu_pvld := io.mac_b2accu_pvld
    abuf_wr_addr := u_calculator.io.abuf_wr_addr 
    abuf_wr_data := u_calculator.io.abuf_wr_data
    abuf_wr_en := u_calculator.io.abuf_wr_en 
    dlv_data := u_calculator.io.dlv_data
    dlv_mask := u_calculator.io.dlv_mask
    dlv_pd := u_calculator.io.dlv_pd 
    dlv_valid := u_calculator.io.dlv_valid
    dp2reg_sat_count := u_calculator.io.dp2reg_sat_count

    //==========================================================
    // Delivery controller
    //==========================================================

    val u_delivery_ctrl = Module(new NV_NVDLA_CACC_delivery_ctrl)

    u_delivery_ctrl.io.reg2dp_op_en := reg2dp_op_en
    u_delivery_ctrl.io.reg2dp_conv_mode := reg2dp_conv_mode
    u_delivery_ctrl.io.reg2dp_proc_precision := reg2dp_proc_precision
    u_delivery_ctrl.io.reg2dp_dataout_width := reg2dp_dataout_width
    u_delivery_ctrl.io.reg2dp_dataout_height := reg2dp_dataout_height
    u_delivery_ctrl.io.reg2dp_dataout_channel := reg2dp_dataout_channel
    u_delivery_ctrl.io.reg2dp_dataout_addr := reg2dp_dataout_addr(31, conf.NVDLA_MEMORY_ATOMIC_LOG2)
    u_delivery_ctrl.io.reg2dp_line_packed := reg2dp_line_packed
    u_delivery_ctrl.io.reg2dp_surf_packed := reg2dp_surf_packed
    u_delivery_ctrl.io.reg2dp_batches := reg2dp_batches
    u_delivery_ctrl.io.reg2dp_line_stride := reg2dp_line_stride
    u_delivery_ctrl.io.reg2dp_surf_stride := reg2dp_surf_stride
    u_delivery_ctrl.io.nvdla_core_clk := io.nvdla_core_clk
    u_delivery_ctrl.io.cacc2sdp_ready := io.cacc2sdp_ready
    u_delivery_ctrl.io.cacc2sdp_valid := io.cacc2sdp_valid
    u_delivery_ctrl.io.dbuf_rd_ready := dbuf_rd_ready
    u_delivery_ctrl.io.dlv_data := dlv_data
    u_delivery_ctrl.io.dlv_mask := dlv_mask
    u_delivery_ctrl.io.dlv_pd := dlv_pd
    u_delivery_ctrl.io.dlv_valid := dlv_valid
    u_delivery_ctrl.io.wait_for_op_en := wait_for_op_en

    dbuf_rd_addr := u_delivery_ctrl.io.dbuf_rd_addr
    dbuf_rd_en := u_delivery_ctrl.io.dbuf_rd_en
    dbuf_rd_layer_end := u_delivery_ctrl.io.dbuf_rd_layer_end
    dbuf_wr_addr := u_delivery_ctrl.io.dbuf_wr_addr
    dbuf_wr_data := u_delivery_ctrl.io.dbuf_wr_data
    dbuf_wr_en := u_delivery_ctrl.io.dbuf_wr_en
    dp2reg_done := u_delivery_ctrl.io.dp2reg_done

    //==========================================================
    // Delivery buffer
    //==========================================================

    val u_delivery_buffer = Module(new NV_NVDLA_CACC_delivery_buffer)

    u_delivery_buffer.io.nvdla_core_clk := io.nvdla_core_clk
    u_delivery_buffer.io.cacc2sdp_ready := io.cacc2sdp_ready
    u_delivery_buffer.io.dbuf_rd_addr := dbuf_rd_addr
    u_delivery_buffer.io.dbuf_rd_en := dbuf_rd_en
    u_delivery_buffer.io.dbuf_rd_layer_end := dbuf_rd_layer_end
    u_delivery_buffer.io.dbuf_wr_addr := dbuf_wr_addr
    u_delivery_buffer.io.dbuf_wr_data := dbuf_wr_data
    u_delivery_buffer.io.dbuf_wr_en := dbuf_wr_en
    u_delivery_buffer.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    io.cacc2glb_done_intr_pd := u_delivery_buffer.io.cacc2glb_done_intr_pd
    io.cacc2sdp_pd := u_delivery_buffer.io.cacc2sdp_pd
    io.cacc2sdp_valid := u_delivery_buffer.io.cacc2sdp_valid
    dbuf_rd_ready := u_delivery_buffer.io.dbuf_rd_ready
    io.accu2sc_credit_size := u_delivery_buffer.io.accu2sc_credit_size
    io.accu2sc_credit_vld := u_delivery_buffer.io.accu2sc_credit_vld

    //==========================================================
    // SLCG groups
    //==========================================================

    val u_slcg_op = Array.fill(3){Module(new NV_NVDLA_slcg)}

    for(i<- 0 to 2){

        u_slcg_op(i).io.dla_clk_ovr_on_sync := io.dla_clk_ovr_on_sync 
        u_slcg_op(i).io.global_clk_ovr_on_sync := io.global_clk_ovr_on_sync
        u_slcg_op(i).io.nvdla_core_clk := io.nvdla_core_clk

        u_slcg_op(i).io.slcg_en_src_0 := slcg_op_en(i)
        u_slcg_op(i).io.slcg_en_src_1 := true.B
        u_slcg_op(i).io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating 

        nvdla_op_gated_clk(i) := u_slcg_op(i).io.nvdla_core_gated_clk                                                                                               
    }

    val u_slcg_cell_0 = Module(new NV_NVDLA_slcg)

    u_slcg_cell_0.io.dla_clk_ovr_on_sync := io.dla_clk_ovr_on_sync 
    u_slcg_cell_0.io.global_clk_ovr_on_sync := io.global_clk_ovr_on_sync
    u_slcg_cell_0.io.nvdla_core_clk := io.nvdla_core_clk

    u_slcg_cell_0.io.slcg_en_src_0 := slcg_op_en(3)
    u_slcg_cell_0.io.slcg_en_src_1 := slcg_cell_en
    u_slcg_cell_0.io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating 

    nvdla_cell_gated_clk := u_slcg_cell_0.io.nvdla_core_gated_clk  

}}


object NV_NVDLA_caccDriver extends App {
  implicit val conf: caccConfiguration = new caccConfiguration
  chisel3.Driver.execute(args, () => new NV_NVDLA_cacc())
}

