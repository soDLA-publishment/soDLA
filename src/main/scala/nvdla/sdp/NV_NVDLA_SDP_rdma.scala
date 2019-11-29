package nvdla

import chisel3._
import chisel3.util._
import chisel3.experimental._

class NV_NVDLA_SDP_rdma(implicit conf: nvdlaConfig) extends Module {

val io = IO(new Bundle {
    //in clock
    val nvdla_clock = Flipped(new nvdla_clock_if)
    val pwrbus_ram_pd = Input(UInt(32.W))

    //csb2sdp_rdma
    val csb2sdp_rdma = new csb2dp_if

    //sdp_bcvif
    val sdp_b2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BS_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
    val sdp_b2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
    val cvif2sdp_b_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BS_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))) else None

    //sdp_e2cvif
    val sdp_e2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_EW_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
    val sdp_e2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_EW_ENABLE) Some(Output(Bool())) else None
    val cvif2sdp_e_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_EW_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))) else None
    
    //sdp_n2cvif
    val sdp_n2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BN_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
    val sdp_n2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BN_ENABLE) Some(Output(Bool())) else None
    val cvif2sdp_n_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE&conf.NVDLA_SDP_BN_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))) else None

    //sdp2cvif
    val sdp2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
    val sdp2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
    val cvif2sdp_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))) else None

    //sdp_b2mcif
    val sdp_b2mcif_rd_req_pd = if(conf.NVDLA_SDP_BS_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
    val sdp_b2mcif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SDP_BS_ENABLE) Some(Output(Bool())) else None
    val mcif2sdp_b_rd_rsp_pd = if(conf.NVDLA_SDP_BS_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))) else None

    val sdp_brdma2dp_alu_pd = if(conf.NVDLA_SDP_BS_ENABLE) Some(DecoupledIO(UInt((conf.AM_DW2+1).W))) else None
    val sdp_brdma2dp_mul_pd = if(conf.NVDLA_SDP_BS_ENABLE) Some(DecoupledIO(UInt((conf.AM_DW2+1).W))) else None

    //sdp_e2mcif
    val sdp_e2mcif_rd_req_pd = if(conf.NVDLA_SDP_EW_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
    val sdp_e2mcif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SDP_EW_ENABLE) Some(Output(Bool())) else None
    val mcif2sdp_e_rd_rsp_pd = if(conf.NVDLA_SDP_EW_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))) else None

    val sdp_erdma2dp_alu_pd = if(conf.NVDLA_SDP_EW_ENABLE) Some(DecoupledIO(UInt((conf.AM_DW2+1).W))) else None
    val sdp_erdma2dp_mul_pd = if(conf.NVDLA_SDP_EW_ENABLE) Some(DecoupledIO(UInt((conf.AM_DW2+1).W))) else None

    //sdp_n2mcif
    val sdp_n2mcif_rd_req_pd = if(conf.NVDLA_SDP_BN_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))) else None
    val sdp_n2mcif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SDP_BN_ENABLE) Some(Output(Bool())) else None
    val mcif2sdp_n_rd_rsp_pd = if(conf.NVDLA_SDP_BN_ENABLE) Some(Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))) else None

    val sdp_nrdma2dp_alu_pd = if(conf.NVDLA_SDP_BN_ENABLE) Some(DecoupledIO(UInt((conf.AM_DW2+1).W))) else None
    val sdp_nrdma2dp_mul_pd = if(conf.NVDLA_SDP_BN_ENABLE) Some(DecoupledIO(UInt((conf.AM_DW2+1).W))) else None
 
    val sdp2mcif_rd_req_pd = DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))
    val sdp2mcif_rd_cdt_lat_fifo_pop = Output(Bool())
    val mcif2sdp_rd_rsp_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_RSP.W)))

    val sdp_mrdma2cmux_pd = DecoupledIO(UInt((conf.DP_DIN_DW+2).W))
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
withClock(io.nvdla_clock.nvdla_core_clk){

    val dp2reg_done = Wire(Bool())
    val u_reg = Module(new NV_NVDLA_SDP_RDMA_reg)
    u_reg.io.nvdla_core_clk := io.nvdla_clock.nvdla_core_clk
    u_reg.io.csb2sdp_rdma <> io.csb2sdp_rdma
    u_reg.io.dp2reg_done := dp2reg_done
    val field = u_reg.io.reg2dp_field

    //=======================================
    // M-RDMA
    val mrdma_op_en = Wire(Bool())
    val mrdma_slcg_op_en = Wire(Bool())
    val mrdma_disable = Wire(Bool())
    val u_mrdma = Module(new NV_NVDLA_SDP_mrdma)
    u_mrdma.io.nvdla_clock <> io.nvdla_clock
    u_mrdma.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    u_mrdma.io.mrdma_slcg_op_en := mrdma_slcg_op_en
    u_mrdma.io.mrdma_disable := mrdma_disable
    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        u_mrdma.io.cvif2sdp_rd_rsp_pd.get <> io.cvif2sdp_rd_rsp_pd.get
        io.sdp2cvif_rd_cdt_lat_fifo_pop.get := u_mrdma.io.sdp2cvif_rd_cdt_lat_fifo_pop.get
        io.sdp2cvif_rd_req_pd.get <> u_mrdma.io.sdp2cvif_rd_req_pd.get
    }
    io.sdp2mcif_rd_cdt_lat_fifo_pop := u_mrdma.io.sdp2mcif_rd_cdt_lat_fifo_pop
    io.sdp2mcif_rd_req_pd <> u_mrdma.io.sdp2mcif_rd_req_pd
    u_mrdma.io.mcif2sdp_rd_rsp_pd <> io.mcif2sdp_rd_rsp_pd

    io.sdp_mrdma2cmux_pd <> u_mrdma.io.sdp_mrdma2cmux_pd

    u_mrdma.io.reg2dp_op_en := mrdma_op_en
    u_mrdma.io.reg2dp_batch_number := field.batch_number
    u_mrdma.io.reg2dp_channel := field.channel
    u_mrdma.io.reg2dp_height := field.height
    u_mrdma.io.reg2dp_width := field.width_a
    u_mrdma.io.reg2dp_in_precision := field.in_precision
    u_mrdma.io.reg2dp_proc_precision := field.proc_precision
    u_mrdma.io.reg2dp_src_ram_type := field.src_ram_type
    u_mrdma.io.reg2dp_src_base_addr_high := field.src_base_addr_high
    u_mrdma.io.reg2dp_src_base_addr_low := field.src_base_addr_low
    u_mrdma.io.reg2dp_src_line_stride := field.src_line_stride
    u_mrdma.io.reg2dp_src_surface_stride := field.src_surface_stride
    u_mrdma.io.reg2dp_perf_dma_en := field.perf_dma_en
    u_mrdma.io.reg2dp_perf_nan_inf_count_en := field.perf_nan_inf_count_en
    val mrdma_done = u_mrdma.io.dp2reg_done 
    u_reg.io.dp2reg_mrdma_stall := u_mrdma.io.dp2reg_mrdma_stall

    u_reg.io.dp2reg_status_inf_input_num := u_mrdma.io.dp2reg_status_inf_input_num 
    u_reg.io.dp2reg_status_nan_input_num := u_mrdma.io.dp2reg_status_nan_input_num

    val brdma_slcg_op_en = Wire(Bool())
    val brdma_disable = Wire(Bool())
    val brdma_done = Wire(Bool())
    val brdma_op_en = if(conf.NVDLA_SDP_BS_ENABLE) Some(Wire(Bool())) else None
    val u_brdma = if(conf.NVDLA_SDP_BS_ENABLE) Some(Module(new NV_NVDLA_SDP_brdma)) else None
    if(conf.NVDLA_SDP_BS_ENABLE){
        u_brdma.get.io.nvdla_clock <> io.nvdla_clock
        u_brdma.get.io.pwrbus_ram_pd := io.pwrbus_ram_pd
        u_brdma.get.io.brdma_slcg_op_en := brdma_slcg_op_en
        u_brdma.get.io.brdma_disable := brdma_disable
        if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
            u_brdma.get.io.cvif2sdp_b_rd_rsp_pd.get <> io.cvif2sdp_b_rd_rsp_pd.get
            io.sdp_b2cvif_rd_cdt_lat_fifo_pop.get := u_brdma.get.io.sdp_b2cvif_rd_cdt_lat_fifo_pop.get
            io.sdp_b2cvif_rd_req_pd.get <> u_brdma.get.io.sdp_b2cvif_rd_req_pd.get
        }
        io.sdp_b2mcif_rd_cdt_lat_fifo_pop.get := u_brdma.get.io.sdp_b2mcif_rd_cdt_lat_fifo_pop
        io.sdp_b2mcif_rd_req_pd.get <> u_brdma.get.io.sdp_b2mcif_rd_req_pd
        u_brdma.get.io.mcif2sdp_b_rd_rsp_pd <> io.mcif2sdp_b_rd_rsp_pd.get

        io.sdp_brdma2dp_alu_pd.get <> u_brdma.get.io.sdp_brdma2dp_alu_pd
        io.sdp_brdma2dp_mul_pd.get <> u_brdma.get.io.sdp_brdma2dp_mul_pd

        u_brdma.get.io.reg2dp_op_en := brdma_op_en.get
        u_brdma.get.io.reg2dp_batch_number := field.batch_number
        u_brdma.get.io.reg2dp_winograd := field.winograd
        u_brdma.get.io.reg2dp_channel := field.channel
        u_brdma.get.io.reg2dp_height := field.height
        u_brdma.get.io.reg2dp_width := field.width_a
        u_brdma.get.io.reg2dp_brdma_data_mode := field.brdma_data_mode
        u_brdma.get.io.reg2dp_brdma_data_size := field.brdma_data_size
        u_brdma.get.io.reg2dp_brdma_data_use := field.brdma_data_use
        u_brdma.get.io.reg2dp_brdma_ram_type := field.brdma_ram_type
        u_brdma.get.io.reg2dp_bs_base_addr_high := field.bs_base_addr_high
        u_brdma.get.io.reg2dp_bs_base_addr_low := field.bs_base_addr_low(31, conf.AM_AW)
        u_brdma.get.io.reg2dp_bs_line_stride := field.bs_line_stride(31, conf.AM_AW)
        u_brdma.get.io.reg2dp_bs_surface_stride := field.bs_surface_stride(31, conf.AM_AW)
        u_brdma.get.io.reg2dp_out_precision := field.out_precision
        u_brdma.get.io.reg2dp_proc_precision := field.proc_precision
        u_brdma.get.io.reg2dp_perf_dma_en := field.perf_dma_en
        u_reg.io.dp2reg_brdma_stall := u_brdma.get.io.dp2reg_brdma_stall
        brdma_done := u_brdma.get.io.dp2reg_done
    }

    val nrdma_slcg_op_en = Wire(Bool())
    val nrdma_disable = Wire(Bool())
    val nrdma_done = Wire(Bool())
    val nrdma_op_en = if(conf.NVDLA_SDP_BS_ENABLE) Some(Wire(Bool())) else None
    val u_nrdma = if(conf.NVDLA_SDP_BN_ENABLE) Some(Module(new NV_NVDLA_SDP_nrdma)) else None
    if(conf.NVDLA_SDP_BN_ENABLE){
        u_nrdma.get.io.nvdla_clock <> io.nvdla_clock
        u_nrdma.get.io.pwrbus_ram_pd := io.pwrbus_ram_pd
        u_nrdma.get.io.nrdma_slcg_op_en := nrdma_slcg_op_en
        u_nrdma.get.io.nrdma_disable := nrdma_disable
        if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
            u_nrdma.get.io.cvif2sdp_n_rd_rsp_pd.get <> io.cvif2sdp_n_rd_rsp_pd.get
            io.sdp_n2cvif_rd_cdt_lat_fifo_pop.get := u_nrdma.get.io.sdp_n2cvif_rd_cdt_lat_fifo_pop.get
            io.sdp_n2cvif_rd_req_pd.get <> u_nrdma.get.io.sdp_n2cvif_rd_req_pd.get
        }
        io.sdp_n2mcif_rd_cdt_lat_fifo_pop.get := u_nrdma.get.io.sdp_n2mcif_rd_cdt_lat_fifo_pop
        io.sdp_n2mcif_rd_req_pd.get <> u_nrdma.get.io.sdp_n2mcif_rd_req_pd 
        u_nrdma.get.io.mcif2sdp_n_rd_rsp_pd <> io.mcif2sdp_n_rd_rsp_pd.get

        io.sdp_nrdma2dp_alu_pd.get <> u_nrdma.get.io.sdp_nrdma2dp_alu_pd
        io.sdp_nrdma2dp_mul_pd.get <> u_nrdma.get.io.sdp_nrdma2dp_mul_pd

        u_nrdma.get.io.reg2dp_op_en := nrdma_op_en.get
        u_nrdma.get.io.reg2dp_batch_number := field.batch_number
        u_nrdma.get.io.reg2dp_winograd := field.winograd
        u_nrdma.get.io.reg2dp_channel := field.channel
        u_nrdma.get.io.reg2dp_height := field.height
        u_nrdma.get.io.reg2dp_width := field.width_a
        u_nrdma.get.io.reg2dp_nrdma_data_mode := field.nrdma_data_mode
        u_nrdma.get.io.reg2dp_nrdma_data_size := field.nrdma_data_size
        u_nrdma.get.io.reg2dp_nrdma_data_use := field.nrdma_data_use
        u_nrdma.get.io.reg2dp_nrdma_ram_type := field.nrdma_ram_type
        u_nrdma.get.io.reg2dp_bn_base_addr_high := field.bn_base_addr_high
        u_nrdma.get.io.reg2dp_bn_base_addr_low := field.bn_base_addr_low(31, conf.AM_AW)
        u_nrdma.get.io.reg2dp_bn_line_stride := field.bn_line_stride(31, conf.AM_AW)
        u_nrdma.get.io.reg2dp_bn_surface_stride := field.bn_surface_stride(31, conf.AM_AW)
        u_nrdma.get.io.reg2dp_out_precision := field.out_precision
        u_nrdma.get.io.reg2dp_proc_precision := field.proc_precision
        u_nrdma.get.io.reg2dp_perf_dma_en := field.perf_dma_en
        u_reg.io.dp2reg_nrdma_stall := u_nrdma.get.io.dp2reg_nrdma_stall
        nrdma_done := u_nrdma.get.io.dp2reg_done
    }
        
    val erdma_slcg_op_en = Wire(Bool())
    val erdma_disable = Wire(Bool())
    val erdma_done = Wire(Bool())
    val erdma_op_en = if(conf.NVDLA_SDP_EW_ENABLE) Some(Wire(Bool())) else None
    val u_erdma = if(conf.NVDLA_SDP_EW_ENABLE) Some(Module(new NV_NVDLA_SDP_erdma)) else None
    if(conf.NVDLA_SDP_EW_ENABLE){
        u_erdma.get.io.nvdla_clock <> io.nvdla_clock 
        u_erdma.get.io.pwrbus_ram_pd := io.pwrbus_ram_pd
        u_erdma.get.io.erdma_slcg_op_en := erdma_slcg_op_en
        u_erdma.get.io.erdma_disable := erdma_disable
        if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
            u_erdma.get.io.cvif2sdp_e_rd_rsp_pd.get <> io.cvif2sdp_e_rd_rsp_pd.get
            io.sdp_e2cvif_rd_cdt_lat_fifo_pop.get := u_erdma.get.io.sdp_e2cvif_rd_cdt_lat_fifo_pop.get
            io.sdp_e2cvif_rd_req_pd.get <> u_erdma.get.io.sdp_e2cvif_rd_req_pd.get
        }
        io.sdp_e2mcif_rd_cdt_lat_fifo_pop.get := u_erdma.get.io.sdp_e2mcif_rd_cdt_lat_fifo_pop
        io.sdp_e2mcif_rd_req_pd.get <> u_erdma.get.io.sdp_e2mcif_rd_req_pd 
        u_erdma.get.io.mcif2sdp_e_rd_rsp_pd <> io.mcif2sdp_e_rd_rsp_pd.get

        io.sdp_erdma2dp_alu_pd.get <> u_erdma.get.io.sdp_erdma2dp_alu_pd
        io.sdp_erdma2dp_mul_pd.get <> u_erdma.get.io.sdp_erdma2dp_mul_pd

        u_erdma.get.io.reg2dp_op_en := erdma_op_en.get
        u_erdma.get.io.reg2dp_batch_number := field.batch_number
        u_erdma.get.io.reg2dp_winograd := field.winograd
        u_erdma.get.io.reg2dp_channel := field.channel
        u_erdma.get.io.reg2dp_height := field.height
        u_erdma.get.io.reg2dp_width := field.width_a
        u_erdma.get.io.reg2dp_erdma_data_mode := field.erdma_data_mode
        u_erdma.get.io.reg2dp_erdma_data_size := field.erdma_data_size
        u_erdma.get.io.reg2dp_erdma_data_use := field.erdma_data_use
        u_erdma.get.io.reg2dp_erdma_ram_type := field.erdma_ram_type
        u_erdma.get.io.reg2dp_ew_base_addr_high := field.ew_base_addr_high
        u_erdma.get.io.reg2dp_ew_base_addr_low := field.ew_base_addr_low(31, conf.AM_AW)
        u_erdma.get.io.reg2dp_ew_line_stride := field.ew_line_stride(31, conf.AM_AW)
        u_erdma.get.io.reg2dp_ew_surface_stride := field.ew_surface_stride(31, conf.AM_AW)
        u_erdma.get.io.reg2dp_out_precision := field.out_precision
        u_erdma.get.io.reg2dp_proc_precision := field.proc_precision
        u_erdma.get.io.reg2dp_perf_dma_en := field.perf_dma_en
        u_reg.io.dp2reg_erdma_stall := u_erdma.get.io.dp2reg_erdma_stall
        erdma_done := u_erdma.get.io.dp2reg_done
    }
    else{
        u_reg.io.dp2reg_erdma_stall := 0.U
    }
    
    //=======================================
    // Configuration Register File
    mrdma_slcg_op_en := u_reg.io.slcg_op_en(0)
    brdma_slcg_op_en := u_reg.io.slcg_op_en(1)
    nrdma_slcg_op_en := u_reg.io.slcg_op_en(2)
    erdma_slcg_op_en := u_reg.io.slcg_op_en(3)

    val mrdma_done_pending = RegInit(false.B)
    when(dp2reg_done){
        mrdma_done_pending := false.B
    }
    .elsewhen(mrdma_done){
        mrdma_done_pending := true.B
    }
    mrdma_op_en := u_reg.io.reg2dp_op_en & ~mrdma_done_pending & ~mrdma_disable

    val brdma_done_pending = if(conf.NVDLA_SDP_BS_ENABLE) RegInit(false.B) else false.B
    if(conf.NVDLA_SDP_BS_ENABLE){
        when(dp2reg_done){
            brdma_done_pending := false.B
        }
        .elsewhen(brdma_done){
            brdma_done_pending := true.B
        }
        brdma_op_en.get := u_reg.io.reg2dp_op_en & ~brdma_done_pending & ~brdma_disable
    }
    else{
        brdma_done := false.B
    }

    val nrdma_done_pending = if(conf.NVDLA_SDP_BN_ENABLE) RegInit(false.B) else false.B
    if(conf.NVDLA_SDP_BN_ENABLE){
        when(dp2reg_done){
            nrdma_done_pending := false.B
        }
        .elsewhen(nrdma_done){
            nrdma_done_pending := true.B
        }
        nrdma_op_en.get := u_reg.io.reg2dp_op_en & ~nrdma_done_pending & ~nrdma_disable
    }
    else{
        nrdma_done := false.B
    }

    val erdma_done_pending = if(conf.NVDLA_SDP_EW_ENABLE) RegInit(false.B) else false.B
    if(conf.NVDLA_SDP_EW_ENABLE){
        when(dp2reg_done){
            erdma_done_pending := false.B
        }
        .elsewhen(erdma_done){
            erdma_done_pending := true.B
        }
        erdma_op_en.get := u_reg.io.reg2dp_op_en & ~erdma_done_pending & ~erdma_disable
    }
    else{
        erdma_done := false.B
    }

    dp2reg_done := u_reg.io.reg2dp_op_en & ((mrdma_done_pending || mrdma_done || mrdma_disable)&
                                  (brdma_done_pending || brdma_done || brdma_disable)&
                                  (nrdma_done_pending || nrdma_done || nrdma_disable)&
                                  (erdma_done_pending || erdma_done || erdma_disable));
    mrdma_disable := field.flying_mode === 1.U
    if(conf.NVDLA_SDP_BS_ENABLE){
        brdma_disable := field.brdma_disable === 1.U
        nrdma_disable := field.nrdma_disable === 1.U
        erdma_disable := field.erdma_disable === 1.U
    }
    else{
        brdma_disable := true.B
        nrdma_disable := true.B
        erdma_disable := true.B
    }
    
}}

object NV_NVDLA_SDP_rdmaDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_rdma())
}



