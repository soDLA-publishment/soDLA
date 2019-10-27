package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_SDP_MRDMA_eg(implicit val conf: nvdlaConfig) extends Module {
   val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())
        val pwrbus_ram_pd = Input(UInt(32.W))

        //cq2eg -- read
        val cq2eg_pd = Flipped(DecoupledIO(UInt(14.W)))
   
        //dma_rd
        val dma_rd_rsp_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))
        val dma_rd_cdt_lat_fifo_pop = Output(Bool())
        val dma_rd_rsp_ram_type = Output(Bool())

        //sdp_mrdma2cmux
        val sdp_mrdma2cmux_pd = DecoupledIO(UInt((conf.DP_DIN_DW+2).W))

        //config&status
        val op_load = Input(Bool())
        val eg_done = Output(Bool())

        //reg2dp
        val reg2dp_src_ram_type = Input(Bool())
        val reg2dp_height = Input(UInt(13.W))
        val reg2dp_width = Input(UInt(13.W))
        val reg2dp_in_precision = Input(UInt(2.W))
        val reg2dp_proc_precision = Input(UInt(2.W))
        val reg2dp_perf_nan_inf_count_en = Input(Bool())
        val dp2reg_status_inf_input_num = Output(UInt(32.W))
        val dp2reg_status_nan_input_num = Output(UInt(32.W))
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

    val u_cmd = Module(new NV_NVDLA_SDP_MRDMA_EG_cmd)
    val u_din = Module(new NV_NVDLA_SDP_MRDMA_EG_din)
    val u_dout = Module(new NV_NVDLA_SDP_MRDMA_EG_dout)
    //cmd
    u_cmd.io.nvdla_core_clk := io.nvdla_core_clk
    u_cmd.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    u_cmd.io.eg_done := io.eg_done
    u_cmd.io.cq2eg_pd <> io.cq2eg_pd

    u_cmd.io.reg2dp_in_precision := io.reg2dp_in_precision
    u_cmd.io.reg2dp_proc_precision := io.reg2dp_proc_precision
    u_cmd.io.reg2dp_height := io.reg2dp_height
    u_cmd.io.reg2dp_width := io.reg2dp_width

    //din
    u_din.io.nvdla_core_clk := io.nvdla_core_clk
    u_din.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    
    u_din.io.dma_rd_rsp_pd <> io.dma_rd_rsp_pd
    io.dma_rd_cdt_lat_fifo_pop := u_din.io.dma_rd_cdt_lat_fifo_pop
    io.dma_rd_rsp_ram_type := u_din.io.dma_rd_rsp_ram_type

    u_din.io.reg2dp_src_ram_type := io.reg2dp_src_ram_type
    
    //dout
    u_dout.io.nvdla_core_clk := io.nvdla_core_clk

    io.sdp_mrdma2cmux_pd <> u_dout.io.sdp_mrdma2cmux_pd

    u_dout.io.op_load := io.op_load
    io.eg_done := u_dout.io.eg_done
    u_dout.io.reg2dp_height := io.reg2dp_height
    u_dout.io.reg2dp_width := io.reg2dp_width
    u_dout.io.reg2dp_in_precision := io.reg2dp_in_precision
    u_dout.io.reg2dp_proc_precision := io.reg2dp_proc_precision
    u_dout.io.reg2dp_perf_nan_inf_count_en := io.reg2dp_perf_nan_inf_count_en
    io.dp2reg_status_inf_input_num := u_dout.io.dp2reg_status_inf_input_num
    io.dp2reg_status_nan_input_num := u_dout.io.dp2reg_status_nan_input_num

    //interconnect
    u_din.io.cmd2dat_spt_pd <> u_cmd.io.cmd2dat_spt_pd
    u_dout.io.cmd2dat_dma_pd <> u_cmd.io.cmd2dat_dma_pd
    u_dout.io.pfifo_rd_pd <> u_din.io.pfifo_rd_pd

}}




object NV_NVDLA_SDP_MRDMA_egDriver extends App {
    implicit val conf: nvdlaConfig = new nvdlaConfig
    chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_MRDMA_eg())
}