package nvdla

import chisel3._
import chisel3.util._

class NV_NVDLA_SDP_WDMA_dat(implicit val conf: nvdlaConfig) extends Module {
   val io = IO(new Bundle {
        //in clock
        val nvdla_core_clk = Input(Clock())
        //  cmd2dma_dma
        val cmd2dat_dma_pd = Flipped(DecoupledIO(UInt((conf.SDP_WR_CMD_DW+2).W)))
        //  cmd2dma_spt
        val cmd2dat_spt_pd = Flipped(DecoupledIO(UInt(15.W)))
        //  sdp_dp2wdma
        val sdp_dp2wdma_pd = Flipped(DecoupledIO(UInt(conf.AM_DW.W)))
        // dma_wr_req
        val dma_wr_req_pd = DecoupledIO(UInt(conf.NVDLA_DMA_WR_REQ.W))
        // INTR
        val intr_req_ptr = ValidIO(Bool())
        // reg2dp
        val reg2dp_batch_number = Input(UInt(5.W))
        val reg2dp_ew_alu_algo = Input(UInt(2.W))
        val reg2dp_ew_alu_bypass = Input(Bool())
        val reg2dp_ew_bypass = Input(Bool())
        val reg2dp_height = Input(UInt(13.W))
        val reg2dp_interrupt_ptr = Input(Bool())
        val reg2dp_out_precision = Input(UInt(2.W))
        val reg2dp_output_dst = Input(Bool())
        val reg2dp_proc_precision = Input(UInt(2.W))
        val reg2dp_width = Input(UInt(13.W))
        val reg2dp_winograd = Input(Bool())
        val dp2reg_done = Output(Bool())      
        val dp2reg_status_nan_output_num = Output(UInt(32.W))  
        val dp2reg_status_unequal = Output(Bool())

        val op_load = Input(Bool())
        val pwrbus_ram_pd = Input(UInt(32.W))


    })

    val u_in = Module(new NV_NVDLA_SDP_WDMA_DAT_in)
    u_in.io.nvdla_core_clk := io.nvdla_core_clk
    u_in.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    u_in.io.op_load := io.op_load
    //cmd2dat
    u_in.io.cmd2dat_spt_pd <> io.cmd2dat_spt_pd
    //sdp_dp2wdma
    u_in.io.sdp_dp2wdma_pd <> io.sdp_dp2wdma_pd
    //config
    u_in.io.reg2dp_batch_number := io.reg2dp_batch_number
    u_in.io.reg2dp_height := io.reg2dp_height
    u_in.io.reg2dp_out_precision := io.reg2dp_out_precision
    u_in.io.reg2dp_proc_precision := io.reg2dp_proc_precision
    u_in.io.reg2dp_width := io.reg2dp_width
    u_in.io.reg2dp_winograd := io.reg2dp_winograd
    io.dp2reg_status_nan_output_num := u_in.io.dp2reg_status_nan_output_num


    val u_out = Module(new NV_NVDLA_SDP_WDMA_DAT_out)
    u_out.io.nvdla_core_clk := io.nvdla_core_clk
    u_out.io.op_load := io.op_load
    //dma_wr_req
    io.dma_wr_req_pd <> u_out.io.dma_wr_req_pd
    //cmd2dat_dma
    u_out.io.cmd2dat_dma_pd <> io.cmd2dat_dma_pd
    //config
    u_out.io.reg2dp_batch_number := io.reg2dp_batch_number
    u_out.io.reg2dp_ew_alu_algo := io.reg2dp_ew_alu_algo
    u_out.io.reg2dp_ew_alu_bypass := io.reg2dp_ew_alu_bypass
    u_out.io.reg2dp_ew_bypass := io.reg2dp_ew_bypass
    u_out.io.reg2dp_height := io.reg2dp_height
    u_out.io.reg2dp_interrupt_ptr := io.reg2dp_interrupt_ptr
    u_out.io.reg2dp_out_precision := io.reg2dp_out_precision
    u_out.io.reg2dp_output_dst := io.reg2dp_output_dst
    u_out.io.reg2dp_proc_precision := io.reg2dp_proc_precision
    u_out.io.reg2dp_width := io.reg2dp_width
    u_out.io.reg2dp_winograd := io.reg2dp_winograd
    io.dp2reg_done := u_out.io.dp2reg_done
    io.dp2reg_status_unequal := u_out.io.dp2reg_status_unequal
    //intr
    io.intr_req_ptr <> u_out.io.intr_req_ptr

    //out dfifo
    u_out.io.dfifo_rd_pd <> u_in.io.dfifo_rd_pd         


}


