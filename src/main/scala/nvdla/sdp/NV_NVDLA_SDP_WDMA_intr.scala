package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_SDP_WDMA_intr(implicit val conf: sdpConfiguration) extends Module {
   val io = IO(new Bundle {
        //in clock
        val nvdla_core_clk = Input(Clock())
        val pwrbus_ram_pd = Input(UInt(32.W))
        val op_load = Input(Bool())

        //
        val reg2dp_ew_alu_algo = Input(UInt(2.W))
        val reg2dp_ew_alu_bypass = Input(Bool())
        val reg2dp_ew_bypass = Input(Bool())
        val reg2dp_op_en = Input(Bool())
        val reg2dp_output_dst = Input(Bool())
        val reg2dp_perf_dma_en = Input(Bool())
        val dp2reg_wdma_stall = Output(UInt(32.W))
        val intr_req_ptr = Input(Bool())
        val intr_req_pvld = Input(Bool())
        val dma_wr_req_rdy = Input(Bool())
        val dma_wr_req_vld = Input(Bool())
        val dma_wr_rsp_complete = Input(Bool())
        val sdp2glb_done_intr_pd = Output(UInt(2.W))


    })

withClock(io.nvdla_core_clk){

//============================
// CFG
//============================

    val cfg_mode_eql = (io.reg2dp_ew_bypass === false.B) & 
                        (io.reg2dp_ew_alu_bypass === false.B) & 
                        (io.reg2dp_ew_alu_algo === 3.U)

    val cfg_mode_pdp = (io.reg2dp_output_dst === true.B)

    val cfg_mode_quite = cfg_mode_eql | cfg_mode_eql

//==============
// Interrupt
//==============

    val intr_fifo_wr_pvld = io.intr_req_pvld & !cfg_mode_quite
    val intr_fifo_wr_pd = io.intr_req_ptr
    val intr_fifo_rd_prdy = io.dma_wr_rsp_complete

    val u_NV_NVDLA_SDP_WDMA_DAT_DMAIF_intr_fifo = Module(new NV_NVDLA_SDP_WDMA_DAT_DMAIF_intr_fifo)
    u_NV_NVDLA_SDP_WDMA_DAT_DMAIF_intr_fifo.io.clk := io.nvdla_core_clk
    u_NV_NVDLA_SDP_WDMA_DAT_DMAIF_intr_fifo.io.intr_fifo_wr_pvld := intr_fifo_wr_pvld
    u_NV_NVDLA_SDP_WDMA_DAT_DMAIF_intr_fifo.io.intr_fifo_wr_pd := intr_fifo_wr_pd
    u_NV_NVDLA_SDP_WDMA_DAT_DMAIF_intr_fifo.io.intr_fifo_rd_prdy := intr_fifo_rd_prdy
    val intr_fifo_rd_pvld = u_NV_NVDLA_SDP_WDMA_DAT_DMAIF_intr_fifo.io.intr_fifo_rd_pvld
    val intr_fifo_rd_pd = u_NV_NVDLA_SDP_WDMA_DAT_DMAIF_intr_fifo.io.intr_fifo_rd_pd
    u_NV_NVDLA_SDP_WDMA_DAT_DMAIF_intr_fifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    val intr0_internal = cfg_mode_quite & io.intr_req_pvld & (io.intr_req_ptr === false.B)
    val intr0_wr  = io.dma_wr_rsp_complete & (intr_fifo_rd_pd === false.B)

    val intr1_internal = cfg_mode_quite & io.intr_req_pvld & (io.intr_req_ptr === true.B)
    val intr1_wr  = io.dma_wr_rsp_complete & (intr_fifo_rd_pd === true.B)

    val sdp2glb_done_intr_pd_out = RegInit(0.U(2.W))
    sdp2glb_done_intr_pd_out(0) := intr0_wr | intr0_internal
    sdp2glb_done_intr_pd_out(1) := intr1_wr | intr1_internal
    io.sdp2glb_done_intr_pd := sdp2glb_done_intr_pd_out

    // val mon_intr_fifo_rd_pvld = intr_fifo_rd_pvld


//==============
// PERF STATISTIC

    val wdma_stall_cnt_inc = io.dma_wr_req_vld & !io.dma_wr_req_rdy
    val wdma_stall_cnt_clr = io.op_load
    val wdma_stall_cnt_cen = io.reg2dp_op_en & io.reg2dp_perf_dma_en  

    val dp2reg_wdma_stall_dec = false.B

    val stl_adv = wdma_stall_cnt_inc ^ dp2reg_wdma_stall_dec

    val stl_cnt_cur = RegInit(0.U(32.W))
    val stl_cnt_ext = Cat(false.B, false.B, stl_cnt_cur)
    val stl_cnt_inc = Wire(UInt(34.W))
    stl_cnt_inc := stl_cnt_cur + true.B; // spyglass disable W164b
    val stl_cnt_dec = Wire(UInt(34.W))
    stl_cnt_dec := stl_cnt_cur - true.B; // spyglass disable W164b
    val stl_cnt_mod = Mux(
            (wdma_stall_cnt_inc && !dp2reg_wdma_stall_dec), 
            stl_cnt_inc, Mux(
                            (!wdma_stall_cnt_inc && dp2reg_wdma_stall_dec), 
                            stl_cnt_dec, stl_cnt_ext)
                            )   
    val stl_cnt_new = Mux(stl_adv, stl_cnt_mod, stl_cnt_ext)
    val stl_cnt_nxt = Mux(wdma_stall_cnt_clr, 0.U, stl_cnt_new)

    when(wdma_stall_cnt_cen){
        stl_cnt_cur := stl_cnt_nxt(31,0)
        }

    io.dp2reg_wdma_stall := stl_cnt_cur
            
}}

class NV_NVDLA_SDP_WDMA_DAT_DMAIF_intr_fifo(implicit val conf: sdpConfiguration) extends Module{
  val io = IO(new Bundle{
        val clk = Input(Clock())

        val intr_fifo_wr_pvld = Input(Bool())
        val intr_fifo_wr_pd = Input(Bool())        
        val intr_fifo_rd_pvld = Output(Bool())
        val intr_fifo_rd_prdy = Input(Bool())
        val intr_fifo_rd_pd = Output(Bool())
        val pwrbus_ram_pd = Input(UInt(32.W))
  })  

withClock(io.clk){
    // Master Clock Gating (SLCG)
    //
    val clk_mgated_enable = Wire(Bool())  // assigned by code at end of this module
    val clk_mgate = Module(new NV_CLK_gate_power)
    clk_mgate.io.clk := io.clk
    clk_mgate.io.clk_en := clk_mgated_enable 
    val clk_mgated = clk_mgate.io.clk_gated
// 
// WRITE SIDE
//
        
//          
// NOTE: 0-depth fifo has no write side
//          


//
// RAM
//

//
// NOTE: 0-depth fifo has no ram.

    val intr_fifo_rd_pd_p = io.intr_fifo_wr_pd

//
// SYNCHRONOUS BOUNDARY
//

//
// NOTE: 0-depth fifo has no real boundary between write and read sides
//


//
// READ SIDE
//

    val intr_fifo_rd_prdy_d = RegInit(true.B)
    intr_fifo_rd_prdy_d := io.intr_fifo_rd_prdy

    val intr_fifo_rd_pvld_int = RegInit(false.B)
    io.intr_fifo_rd_pvld := intr_fifo_rd_pvld_int

    val intr_fifo_rd_pvld_p = io.intr_fifo_wr_pvld
    val intr_fifo_rd_pvld_int_o = withClock(clk_mgated){RegInit(false.B)}
    val intr_fifo_rd_pvld_o = intr_fifo_rd_pvld_int_o
    val intr_fifo_rd_prdy_d_o = Wire(Bool())
    val rd_popping = intr_fifo_rd_pvld_p && !(intr_fifo_rd_pvld_int_o && !intr_fifo_rd_prdy_d_o)
// 
// SKID for -rd_busy_reg
//
    val intr_fifo_rd_pd_o = withClock(clk_mgated){Reg(Bool())}
    val rd_req_next_o = (intr_fifo_rd_pvld_p || (intr_fifo_rd_pvld_int_o && !intr_fifo_rd_prdy_d_o))
    intr_fifo_rd_pvld_int_o := rd_req_next_o
    when((intr_fifo_rd_pvld_int && rd_req_next_o && rd_popping)){
        intr_fifo_rd_pd_o := intr_fifo_rd_pd_p
    }
//
// FINAL OUTPUT
//
    val intr_fifo_rd_pd_out = Reg(Bool())
    val intr_fifo_rd_pvld_int_d = RegInit(false.B)
    intr_fifo_rd_prdy_d_o := !(intr_fifo_rd_pvld_o && intr_fifo_rd_pvld_int_d && !intr_fifo_rd_prdy_d)
    val rd_req_next = Mux(!intr_fifo_rd_prdy_d_o, intr_fifo_rd_pvld_o, intr_fifo_rd_pvld_p)
    when(!intr_fifo_rd_pvld_int || io.intr_fifo_rd_prdy){
        intr_fifo_rd_pvld_int := rd_req_next
    }
    intr_fifo_rd_pvld_int_d := intr_fifo_rd_pvld_int

    when(rd_req_next && (!intr_fifo_rd_pvld_int || io.intr_fifo_rd_prdy)){
        intr_fifo_rd_pd_out := MuxCase(
            false.B,
            Array(
                (!intr_fifo_rd_prdy_d_o === false.B) -> intr_fifo_rd_pd_p,
                (!intr_fifo_rd_prdy_d_o === true.B) -> intr_fifo_rd_pd_o)
            )
    }
    io.intr_fifo_rd_pd := intr_fifo_rd_pd_out
    clk_mgated_enable := (false.B || 
                        (io.intr_fifo_wr_pvld || 
                            (intr_fifo_rd_pvld_int && intr_fifo_rd_prdy_d) || 
                            (intr_fifo_rd_pvld_int_o && intr_fifo_rd_prdy_d_o)))

}



//   val is_pipe = Module(new NV_NVDLA_IS_pipe(1))

//   is_pipe.io.clk := io.nvdla_core_clk

//   is_pipe.io.vi := io.intr_fifo_wr_pvld
//   io.intr_fifo_wr_prdy := is_pipe.io.ro
//   is_pipe.io.di := io.intr_fifo_wr_pd

//   io.intr_fifo_rd_pvld := is_pipe.io.vo
//   is_pipe.io.ri := io.intr_fifo_rd_prdy
//   io.intr_fifo_rd_pd := is_pipe.io.dout
  
}

 
object NV_NVDLA_SDP_WDMA_intrDriver extends App {
  implicit val conf: sdpConfiguration = new sdpConfiguration
  chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_WDMA_intr())
}



