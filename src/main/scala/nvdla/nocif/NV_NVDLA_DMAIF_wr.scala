package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver

class NV_NVDLA_DMAIF_wr(DMABW: Int)(implicit conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())

        val mcif_wr_req_pd = DecoupledIO(UInt(DMABW.W))
        val mcif_wr_rsp_complete = Input(Bool())

        val cvif_wr_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(DMABW.W)))
                             else None
        val cvif_wr_rsp_complete = Input(Bool())

        val dmaif_wr_req_pd = Flipped(DecoupledIO(UInt(DMABW.W)))
        val dmaif_wr_rsp_complete = Output(Bool())

        val reg2dp_dst_ram_type = Input(Bool())

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
    //==============
    // DMA Interface
    //==============
    val dma_wr_req_type = io.reg2dp_dst_ram_type
    val cv_dma_wr_req_vld = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Wire(Bool())) else None
    val cv_dma_wr_req_rdy = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Wire(Bool())) else None
    val cv_wr_req_rdyi = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Wire(Bool())) else None
    val mc_wr_req_rdyi = Wire(Bool())
    val wr_req_rdyi = Wire(Bool())
    // wr Channel: Request 
    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        cv_dma_wr_req_vld.get := io.dmaif_wr_req_pd.valid & (dma_wr_req_type === false.B)
        cv_wr_req_rdyi.get := cv_dma_wr_req_rdy.get & (dma_wr_req_type === false.B)
        wr_req_rdyi := mc_wr_req_rdyi | cv_wr_req_rdyi.get;
    }
    else{
        wr_req_rdyi := mc_wr_req_rdyi
    }
    val mc_dma_wr_req_vld = io.dmaif_wr_req_pd.valid & (dma_wr_req_type === true.B)
    val mc_dma_wr_req_rdy = Wire(Bool())
    mc_wr_req_rdyi := mc_dma_wr_req_rdy & (dma_wr_req_type === true.B)
    io.dmaif_wr_req_pd.ready := wr_req_rdyi

    val is_pipe1 = Module{new NV_NVDLA_IS_pipe(DMABW+1)}
    is_pipe1.io.clk := io.nvdla_core_clk
    is_pipe1.io.vi := mc_dma_wr_req_vld
    mc_dma_wr_req_rdy := is_pipe1.io.ro
    is_pipe1.io.di := io.dmaif_wr_req_pd.bits
    io.mcif_wr_req_pd.valid := is_pipe1.io.vo
    is_pipe1.io.ri := io.mcif_wr_req_pd.ready
    io.mcif_wr_req_pd.bits := is_pipe1.io.dout

    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        val is_pipe2 = Module{new NV_NVDLA_IS_pipe(DMABW+1)}
        is_pipe2.io.clk := io.nvdla_core_clk
        is_pipe2.io.vi := cv_dma_wr_req_vld.get
        cv_dma_wr_req_rdy.get := is_pipe2.io.ro
        is_pipe2.io.di := io.dmaif_wr_req_pd.bits
        io.cvif_wr_req_pd.get.valid := is_pipe2.io.vo
        is_pipe2.io.ri := io.cvif_wr_req_pd.get.ready
        io.cvif_wr_req_pd.get.bits := is_pipe2.io.dout

    }
    // wr Channel: Response
    val mc_int_wr_rsp_complete = io.mcif_wr_rsp_complete
    val require_ack = if(conf.DMAIF>64) (io.dmaif_wr_req_pd.bits(DMABW-1) === 0.U) & (io.dmaif_wr_req_pd.bits(77) === 1.U)
                      else (io.dmaif_wr_req_pd.bits(DMABW-1) === 0.U) & (io.dmaif_wr_req_pd.bits(45) === 1.U)
    val ack_raw_vld = io.dmaif_wr_req_pd.valid & wr_req_rdyi & require_ack;
    val ack_raw_id  = dma_wr_req_type;
    // stage1: bot
    val ack_bot_rdy = Wire(Bool())
    val ack_bot_vld = RegInit(false.B)
    val ack_bot_id = RegInit(false.B)
    val ack_raw_rdy = ack_bot_rdy || !ack_bot_vld
    when(ack_raw_vld & ack_raw_rdy){
        ack_bot_id := ack_raw_id
    }
    when(ack_raw_rdy){
        ack_bot_vld := ack_raw_vld
    }
    // stage2: top
    val ack_top_vld = RegInit(false.B)
    val ack_top_id = RegInit(false.B)
    val ack_top_rdy = Wire(Bool())
    ack_bot_rdy := ack_top_rdy || !ack_top_vld
    when(ack_bot_vld & ack_bot_rdy){
        ack_top_id := ack_bot_id
    }
    when(ack_bot_rdy){
        ack_top_vld := ack_bot_vld
    }
    val releasing = Wire(Bool())
    ack_top_rdy := releasing

    val mc_dma_wr_rsp_complete = RegInit(false.B)
    mc_dma_wr_rsp_complete := io.mcif_wr_rsp_complete

    val cv_dma_wr_rsp_complete = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(RegInit(false.B)) else None
    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        cv_dma_wr_rsp_complete.get := io.cvif_wr_rsp_complete
    }

    val dmaif_wr_rsp_complete_out = RegInit(false.B)
    dmaif_wr_rsp_complete_out := releasing
    io.dmaif_wr_rsp_complete := dmaif_wr_rsp_complete_out

    val mc_pending = RegInit(false.B)
    when(ack_top_id === 0.U){
        when(mc_dma_wr_rsp_complete){
            mc_pending := true.B
        }
    }
    .elsewhen(ack_top_id === 1.U){
        when(mc_pending){
            mc_pending := false.B
        }
    }
    val mc_releasing = (ack_top_id === 1.U) & (mc_dma_wr_rsp_complete | mc_pending)

    val cv_releasing = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Wire(Bool())) else None
    val cv_pending = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(RegInit(false.B)) else None

    if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
        when(ack_top_id === 1.U){
            when(cv_dma_wr_rsp_complete.get){
                cv_pending.get := true.B
            }
        }
        .elsewhen(ack_top_id === 0.U){
            when(cv_pending.get){
                cv_pending.get := false.B
            }
        }
        cv_releasing.get := (ack_top_id === false.B) & (cv_dma_wr_rsp_complete.get | cv_pending.get)
        releasing := mc_releasing | cv_releasing.get
    }
    else{
        releasing := mc_releasing
    }
}}
