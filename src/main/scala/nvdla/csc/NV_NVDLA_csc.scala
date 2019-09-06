package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._


class NV_NVDLA_csc(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_clock = Flipped(new nvdla_clock_if)
        val nvdla_core_rstn = Input(Bool())

        //cdma
        val sc2cdma_dat_pending_req = Output(Bool())  
        val sc2cdma_wt_pending_req = Output(Bool())
        val cdma2sc_dat_pending_ack = Input(Bool())
        val cdma2sc_wt_pending_ack = Input(Bool())

        val cdma2sc_dat_updt = Flipped(ValidIO(new updt_entries_slices_if))
        val sc2cdma_dat_updt = ValidIO(new updt_entry_slices_if)
        val cdma2sc_wt_updt = Flipped(ValidIO(new updt_entries_kernels_if))
        val sc2cdma_wt_updt = ValidIO(new updt_entries_kernels_if)  
        val sc2cdma_wmb_entries = Output(UInt(9.W))
        val cdma2sc_wmb_entries = Input(UInt(9.W))

        //accu
        val accu2sc_credit_size = Flipped(ValidIO(UInt(3.W)))

        //csb
        val csb2csc = new csb2dp_if

        //cbuf_dat & wt
        val sc2buf_dat_rd = new sc2buf_data_rd_if
        val sc2buf_wt_rd = new sc2buf_wt_rd_if

        //mac_dat & wt
        val sc2mac_dat_a = ValidIO(new csc2cmac_data_if)    /* data valid */
        val sc2mac_dat_b = ValidIO(new csc2cmac_data_if)    /* data valid */

        val sc2mac_wt_a = ValidIO(new csc2cmac_wt_if)    /* data valid */
        val sc2mac_wt_b = ValidIO(new csc2cmac_wt_if)    /* data valid */

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
withReset(!io.nvdla_core_rstn){

    val nvdla_op_gated_clk = Wire(Vec(3, Clock()))
    //==========================================================
    // Regfile
    //==========================================================
    val u_regfile = Module(new NV_NVDLA_CSC_regfile)
    val u_sg = Module(new NV_NVDLA_CSC_sg)

    u_regfile.io.nvdla_core_clk := io.nvdla_clock.nvdla_core_clk               
    u_regfile.io.csb2csc <> io.csb2csc     
    val field = u_regfile.io.reg2dp_field                           
    u_regfile.io.dp2reg_done := u_sg.io.dp2reg_done                        

    //==========================================================
    // Sequence generator
    //==========================================================
    

    u_sg.io.nvdla_core_ng_clk := io.nvdla_clock.nvdla_core_clk
    u_sg.io.nvdla_core_clk := nvdla_op_gated_clk(0)
    u_sg.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    u_sg.io.cdma2sc_dat_updt <> io.cdma2sc_dat_updt
    u_sg.io.cdma2sc_wt_updt <> io.cdma2sc_wt_updt
    u_sg.io.accu2sc_credit_size <> io.accu2sc_credit_size
    u_sg.io.cdma2sc_dat_pending_ack := io.cdma2sc_dat_pending_ack
    u_sg.io.cdma2sc_wt_pending_ack := io.cdma2sc_wt_pending_ack
    io.sc2cdma_dat_pending_req := u_sg.io.sc2cdma_dat_pending_req
    io.sc2cdma_wt_pending_req := u_sg.io.sc2cdma_wt_pending_req

    u_sg.io.reg2dp_op_en := u_regfile.io.reg2dp_op_en
    u_sg.io.reg2dp_conv_mode := field.conv_mode
    u_sg.io.reg2dp_proc_precision := field.proc_precision
    u_sg.io.reg2dp_data_reuse := field.data_reuse
    u_sg.io.reg2dp_skip_data_rls := field.skip_data_rls
    u_sg.io.reg2dp_weight_reuse := field.weight_reuse
    u_sg.io.reg2dp_skip_weight_rls := field.skip_weight_rls
    u_sg.io.reg2dp_weight_reuse := field.weight_reuse
    u_sg.io.reg2dp_skip_weight_rls := field.skip_weight_rls
    u_sg.io.reg2dp_batches := field.batches
    u_sg.io.reg2dp_datain_format := field.datain_format
    u_sg.io.reg2dp_datain_height_ext := field.datain_height_ext
    u_sg.io.reg2dp_y_extension := field.y_extension
    u_sg.io.reg2dp_weight_width_ext := field.weight_width_ext
    u_sg.io.reg2dp_weight_height_ext := field.weight_height_ext
    u_sg.io.reg2dp_weight_channel_ext := field.weight_channel_ext
    u_sg.io.reg2dp_weight_kernel := field.weight_kernel
    u_sg.io.reg2dp_dataout_width := field.dataout_width
    u_sg.io.reg2dp_dataout_height := field.dataout_height
    u_sg.io.reg2dp_data_bank := field.data_bank
    u_sg.io.reg2dp_weight_bank := field.weight_bank 
    u_sg.io.reg2dp_atomics := field.atomics
    u_sg.io.reg2dp_rls_slices := field.rls_slices 


    //==========================================================
    // Weight loader
    //==========================================================
    val u_wl = Module(new NV_NVDLA_CSC_wl)

    u_wl.io.nvdla_core_ng_clk := io.nvdla_clock.nvdla_core_clk 
    u_wl.io.nvdla_core_clk := nvdla_op_gated_clk(1)

    u_wl.io.sg2wl <> u_sg.io.sg2wl 
    u_wl.io.sc_state := u_sg.io.sc_state
    u_wl.io.sc2cdma_wt_pending_req := io.sc2cdma_wt_pending_req
    u_wl.io.cdma2sc_wt_updt <> io.cdma2sc_wt_updt
    io.sc2cdma_wt_updt <> u_wl.io.sc2cdma_wt_updt
    io.sc2buf_wt_rd <> u_wl.io.sc2buf_wt_rd
    u_wl.io.cdma2sc_wmb_entries := io.cdma2sc_wmb_entries
    io.sc2cdma_wmb_entries := u_wl.io.sc2cdma_wmb_entries
    io.sc2mac_wt_a <> u_wl.io.sc2mac_wt_a
    io.sc2mac_wt_b <> u_wl.io.sc2mac_wt_b

    u_wl.io.reg2dp_op_en := u_regfile.io.reg2dp_op_en
    u_wl.io.reg2dp_in_precision := field.in_precision
    u_wl.io.reg2dp_proc_precision := field.proc_precision
    u_wl.io.reg2dp_y_extension := field.y_extension
    u_wl.io.reg2dp_weight_reuse := field.weight_reuse
    u_wl.io.reg2dp_skip_weight_rls := field.skip_weight_rls
    u_wl.io.reg2dp_weight_format := field.weight_format
    u_wl.io.reg2dp_weight_bytes := field.weight_bytes
    u_wl.io.reg2dp_wmb_bytes := field.wmb_bytes
    u_wl.io.reg2dp_data_bank := field.data_bank
    u_wl.io.reg2dp_weight_bank := field.weight_bank


    //==========================================================
    // Data loader
    //==========================================================
    val u_dl = Module(new NV_NVDLA_CSC_dl)
    
    u_dl.io.nvdla_core_ng_clk := io.nvdla_clock.nvdla_core_clk
    u_dl.io.nvdla_core_clk := nvdla_op_gated_clk(2)
    u_dl.io.sg2dl <> u_sg.io.sg2dl 
    u_dl.io.sc_state := u_sg.io.sc_state
    u_dl.io.sc2cdma_dat_pending_req := io.sc2cdma_dat_pending_req
    u_dl.io.cdma2sc_dat_updt <> io.cdma2sc_dat_updt
    io.sc2cdma_dat_updt <> u_dl.io.sc2cdma_dat_updt
    io.sc2buf_dat_rd <> u_dl.io.sc2buf_dat_rd
    io.sc2mac_dat_a <> u_dl.io.sc2mac_dat_a
    io.sc2mac_dat_b <> u_dl.io.sc2mac_dat_b

    u_dl.io.reg2dp_op_en := u_regfile.io.reg2dp_op_en
    u_dl.io.reg2dp_conv_mode := field.conv_mode
    u_dl.io.reg2dp_batches := field.batches
    u_dl.io.reg2dp_proc_precision := field.proc_precision
    u_dl.io.reg2dp_datain_format := field.datain_format
    u_dl.io.reg2dp_skip_data_rls := field.skip_data_rls
    u_dl.io.reg2dp_datain_channel_ext := field.datain_channel_ext
    u_dl.io.reg2dp_datain_height_ext := field.datain_height_ext
    u_dl.io.reg2dp_datain_width_ext := field.datain_width_ext
    u_dl.io.reg2dp_y_extension := field.y_extension
    u_dl.io.reg2dp_weight_channel_ext := field.weight_channel_ext
    u_dl.io.reg2dp_entries := field.entries
    u_dl.io.reg2dp_dataout_width := field.dataout_width
    u_dl.io.reg2dp_rls_slices := field.rls_slices
    u_dl.io.reg2dp_conv_x_stride_ext := field.conv_x_stride_ext
    u_dl.io.reg2dp_conv_y_stride_ext := field.conv_y_stride_ext
    u_dl.io.reg2dp_x_dilation_ext := field.x_dilation_ext
    u_dl.io.reg2dp_y_dilation_ext := field.y_dilation_ext
    u_dl.io.reg2dp_pad_left := field.pad_left
    u_dl.io.reg2dp_pad_top := field.pad_top
    u_dl.io.reg2dp_pad_value := field.pad_value
    u_dl.io.reg2dp_data_bank := field.data_bank
    u_dl.io.reg2dp_pra_truncate := field.pra_truncate

    val slcg_wg_en = u_dl.io.slcg_wg_en
    

    //==========================================================
    // SLCG groups
    //==========================================================

    val u_slcg_op = Array.fill(3){Module(new NV_NVDLA_slcg(1, false))}

    for(i<- 0 to 2){
        u_slcg_op(i).io.nvdla_clock := io.nvdla_clock
        u_slcg_op(i).io.slcg_en(0) := u_regfile.io.slcg_op_en(i)
        nvdla_op_gated_clk(i) := u_slcg_op(i).io.nvdla_core_gated_clk                                                                                                 
    }


}}


object NV_NVDLA_cscDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_csc())
}
