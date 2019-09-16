package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver

class NV_NVDLA_cacc(implicit conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        // clk
        val nvdla_clock = Flipped(new nvdla_clock_if)
        val nvdla_core_rstn = Input(Bool())

        //csb2cacc
        val csb2cacc = new csb2dp_if 

        //mac
        val mac_a2accu = Flipped(ValidIO(new cmac2cacc_if))    /* data valid */
        val mac_b2accu = Flipped(ValidIO(new cmac2cacc_if))    /* data valid */

        //sdp
        val cacc2sdp = DecoupledIO(new cacc2sdp_if)    /* data valid */

        //csc
        val accu2sc_credit_size = ValidIO((UInt(3.W)))

        //glb
        val cacc2glb_done_intr_pd = Output(UInt(2.W)) 
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

    val nvdla_cell_gated_clk = Wire(Clock())
    val nvdla_op_gated_clk = Wire(Vec(3, Clock()))

    //==========================================================
    // Regfile
    //==========================================================
    val u_regfile = Module(new NV_NVDLA_CACC_regfile)

    u_regfile.io.nvdla_core_clk := io.nvdla_clock.nvdla_core_clk  
    u_regfile.io.csb2cacc <> io.csb2cacc   
         
    val field = u_regfile.io.reg2dp_field  
    
    //==========================================================
    // Assembly controller
    //==========================================================
    val u_assembly_ctrl = Module(new NV_NVDLA_CACC_assembly_ctrl)

    u_assembly_ctrl.io.nvdla_core_clk := nvdla_op_gated_clk(0) 

    u_assembly_ctrl.io.mac_a2accu_pd.valid := io.mac_a2accu.valid
    u_assembly_ctrl.io.mac_a2accu_pd.bits := io.mac_a2accu.bits.pd
    
    u_assembly_ctrl.io.reg2dp_op_en := u_regfile.io.reg2dp_op_en    
    u_assembly_ctrl.io.reg2dp_conv_mode := field.conv_mode           
    u_assembly_ctrl.io.reg2dp_proc_precision := field.proc_precision
    u_assembly_ctrl.io.reg2dp_clip_truncate := field.clip_truncate
    

    //==========================================================
    // Assembly buffer
    //==========================================================
    val u_assembly_buffer = Module(new NV_NVDLA_CACC_assembly_buffer)
    
    u_assembly_buffer.io.nvdla_core_clk := nvdla_op_gated_clk(1)
    u_assembly_buffer.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    u_assembly_buffer.io.abuf_rd.addr.valid := u_assembly_ctrl.io.abuf_rd_addr.valid
    u_assembly_buffer.io.abuf_rd.addr.bits := u_assembly_ctrl.io.abuf_rd_addr.bits

    //==========================================================
    // CACC calculator
    //==========================================================
    val u_calculator = Module(new NV_NVDLA_CACC_calculator)

    u_calculator.io.nvdla_cell_clk := nvdla_cell_gated_clk
    u_calculator.io.nvdla_core_clk := nvdla_op_gated_clk(2)

    u_calculator.io.abuf_rd_data := u_assembly_buffer.io.abuf_rd.data
    u_assembly_buffer.io.abuf_wr <> u_calculator.io.abuf_wr

    u_calculator.io.accu_ctrl_pd <> u_assembly_ctrl.io.accu_ctrl_pd
    u_calculator.io.accu_ctrl_ram_valid := u_assembly_ctrl.io.accu_ctrl_ram_valid

    u_calculator.io.cfg_in_en_mask := u_assembly_ctrl.io.cfg_in_en_mask
    u_calculator.io.cfg_truncate := u_assembly_ctrl.io.cfg_truncate

    u_calculator.io.mac_a2accu_data := io.mac_a2accu.bits.data
    u_calculator.io.mac_a2accu_mask := io.mac_a2accu.bits.mask
    u_calculator.io.mac_a2accu_pvld := io.mac_a2accu.valid
    u_calculator.io.mac_b2accu_data := io.mac_b2accu.bits.data
    u_calculator.io.mac_b2accu_mask := io.mac_b2accu.bits.mask
    u_calculator.io.mac_b2accu_pvld := io.mac_b2accu.valid

    u_regfile.io.dp2reg_sat_count := u_calculator.io.dp2reg_sat_count

    //==========================================================
    // Delivery controller
    //==========================================================
    val u_delivery_ctrl = Module(new NV_NVDLA_CACC_delivery_ctrl)

    u_delivery_ctrl.io.nvdla_core_clk := io.nvdla_clock.nvdla_core_clk

    u_delivery_ctrl.io.dlv_data := u_calculator.io.dlv_data
    u_delivery_ctrl.io.dlv_mask := u_calculator.io.dlv_mask
    u_delivery_ctrl.io.dlv_pd := u_calculator.io.dlv_pd
    u_delivery_ctrl.io.dlv_valid := u_calculator.io.dlv_valid

    u_delivery_ctrl.io.wait_for_op_en := u_assembly_ctrl.io.wait_for_op_en

    u_delivery_ctrl.io.reg2dp_op_en := u_regfile.io.reg2dp_op_en
    u_delivery_ctrl.io.reg2dp_conv_mode := field.conv_mode
    u_delivery_ctrl.io.reg2dp_proc_precision := field.proc_precision
    u_delivery_ctrl.io.reg2dp_dataout_width := field.dataout_width
    u_delivery_ctrl.io.reg2dp_dataout_height := field.dataout_height
    u_delivery_ctrl.io.reg2dp_dataout_channel := field.dataout_channel
    u_delivery_ctrl.io.reg2dp_dataout_addr := field.dataout_addr(31, conf.NVDLA_MEMORY_ATOMIC_LOG2)
    u_delivery_ctrl.io.reg2dp_line_packed := field.line_packed
    u_delivery_ctrl.io.reg2dp_surf_packed := field.surf_packed
    u_delivery_ctrl.io.reg2dp_batches := field.batches
    u_delivery_ctrl.io.reg2dp_line_stride := field.line_stride
    u_delivery_ctrl.io.reg2dp_surf_stride := field.surf_stride

    u_regfile.io.dp2reg_done := u_delivery_ctrl.io.dp2reg_done 
    u_assembly_ctrl.io.dp2reg_done := u_delivery_ctrl.io.dp2reg_done 

    //==========================================================
    // Delivery buffer
    //==========================================================

    val u_delivery_buffer = Module(new NV_NVDLA_CACC_delivery_buffer)

    u_delivery_buffer.io.nvdla_core_clk := io.nvdla_clock.nvdla_core_clk
    u_delivery_buffer.io.pwrbus_ram_pd := io.pwrbus_ram_pd


    u_delivery_buffer.io.cacc2sdp <> io.cacc2sdp

    u_delivery_buffer.io.dbuf_rd_addr := u_delivery_ctrl.io.dbuf_rd_addr
    u_delivery_buffer.io.dbuf_rd_layer_end := u_delivery_ctrl.io.dbuf_rd_layer_end
    u_delivery_ctrl.io.dbuf_rd_ready := u_delivery_buffer.io.dbuf_rd_ready
    u_delivery_buffer.io.dbuf_wr := u_delivery_ctrl.io.dbuf_wr
    
    io.cacc2glb_done_intr_pd := u_delivery_buffer.io.cacc2glb_done_intr_pd

    io.accu2sc_credit_size <> u_delivery_buffer.io.accu2sc_credit_size

    //==========================================================
    // SLCG groups
    //==========================================================

    val u_slcg_op = Array.fill(3){Module(new NV_NVDLA_slcg(1, false))}

    for(i<- 0 to 2){
        u_slcg_op(i).io.nvdla_clock := io.nvdla_clock 
        u_slcg_op(i).io.slcg_en(0):= u_regfile.io.slcg_op_en(i)
        nvdla_op_gated_clk(i) := u_slcg_op(i).io.nvdla_core_gated_clk                                                                                               
    }

    val u_slcg_cell_0 = Module(new NV_NVDLA_slcg(1, false))
    u_slcg_cell_0.io.nvdla_clock := io.nvdla_clock
    u_slcg_cell_0.io.slcg_en(0) := u_regfile.io.slcg_op_en(3) | u_assembly_ctrl.io.slcg_cell_en
    nvdla_cell_gated_clk := u_slcg_cell_0.io.nvdla_core_gated_clk  
}}


object NV_NVDLA_caccDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_cacc())
}

