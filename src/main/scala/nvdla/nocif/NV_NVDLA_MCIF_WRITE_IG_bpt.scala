package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

@chiselName
class NV_NVDLA_MCIF_WRITE_IG_bpt(implicit conf:nvdlaConfig)  extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val pwrbus_ram_pd = Input(UInt(32.W))

        val axid = Input(UInt(4.W))
        
        //dma2bpt
        val dma2bpt_req_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_WR_REQ.W)))

        //bpt2arb
        val bpt2arb_cmd_pd = DecoupledIO(UInt(conf.NVDLA_DMA_WR_IG_PW.W))
        val bpt2arb_dat_pd = DecoupledIO(UInt((conf.NVDLA_DMA_WR_REQ-1).W))
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
    val ipipe_rdy = Wire(Bool())

    val pipe_p1 = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_DMA_WR_REQ))
    pipe_p1.io.clk := io.nvdla_core_clk
    pipe_p1.io.vi := io.dma2bpt_req_pd.valid
    io.dma2bpt_req_pd.ready := pipe_p1.io.ro
    pipe_p1.io.di := io.dma2bpt_req_pd.bits

    val pipe_p2 = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_DMA_WR_REQ))
    pipe_p2.io.clk := io.nvdla_core_clk

    pipe_p2.io.vi := pipe_p1.io.vo
    pipe_p1.io.ri := pipe_p2.io.ro
    pipe_p2.io.di := pipe_p1.io.dout

    val ipipe_vld = pipe_p2.io.vo
    pipe_p2.io.ri := ipipe_rdy
    val ipipe_pd = pipe_p2.io.dout

    val ipipe_cmd_vld = ipipe_vld && (ipipe_pd(conf.NVDLA_DMA_WR_REQ-1) === 0.U)
    val dfifo_wr_pvld = ipipe_vld && (ipipe_pd(conf.NVDLA_DMA_WR_REQ-1) === 1.U)

    val dfifo_wr_data = ipipe_pd(conf.NVDLA_MEMIF_WIDTH-1, 0)
    val dfifo_wr_mask = ipipe_pd(conf.NVDLA_MEMIF_WIDTH+conf.NVDLA_DMA_MASK_BIT-1, conf.NVDLA_MEMIF_WIDTH)

    val ipipe_cmd_rdy = Wire(Bool())
    val dfifo_wr_prdy = Wire(Bool())
    ipipe_rdy := (ipipe_cmd_vld & ipipe_cmd_rdy) || (dfifo_wr_pvld & dfifo_wr_prdy)
    val ipipe_cmd_pd = ipipe_pd(conf.NVDLA_DMA_WR_CMD-1, 0)

    val in_cmd_rdy = Wire(Bool())
    val pipe_p3 = Module(new NV_NVDLA_BC_pipe(conf.NVDLA_DMA_WR_CMD))
    pipe_p3.io.clk := io.nvdla_core_clk
    pipe_p3.io.vi := ipipe_cmd_vld
    ipipe_cmd_rdy := pipe_p3.io.ro
    pipe_p3.io.di := ipipe_cmd_pd
    val in_cmd_vld = pipe_p3.io.vo
    pipe_p3.io.ri := in_cmd_rdy
    val in_cmd_pd = pipe_p3.io.dout

    val is_ltran = Wire(Bool())
    val is_last_beat = Wire(Bool())
    val bpt2arb_dat_accept = Wire(Bool())
    in_cmd_rdy := is_ltran & is_last_beat & bpt2arb_dat_accept
    val in_cmd_vld_pd = Fill(conf.NVDLA_DMA_WR_CMD, in_cmd_vld) & in_cmd_pd

    val in_cmd_addr = in_cmd_vld_pd(conf.NVDLA_MEM_ADDRESS_WIDTH-1, 0)
    val in_cmd_size = in_cmd_vld_pd(conf.NVDLA_DMA_WR_CMD-2, conf.NVDLA_MEM_ADDRESS_WIDTH)
    val in_cmd_require_ack  = in_cmd_vld_pd(conf.NVDLA_DMA_WR_CMD-1)

    val dfifo_rd_prdy = Wire(Bool())
    val u_dfifo = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_MEMIF_WIDTH))
    u_dfifo.io.clk := io.nvdla_core_clk
    u_dfifo.io.vi := dfifo_wr_pvld
    dfifo_wr_prdy := u_dfifo.io.ro
    u_dfifo.io.di := dfifo_wr_data
    val dfifo_rd_pvld = u_dfifo.io.vo
    u_dfifo.io.ri := dfifo_rd_prdy
    val dfifo_rd_data = u_dfifo.io.dout


    //==================
    // in_cmd analysis to determine how to pop data from dFIFO
    val ftran_size = Wire(UInt(3.W))
    val ltran_size = Wire(UInt(3.W))
    val mtran_num = Wire(UInt(conf.NVDLA_DMA_WR_SIZE.W))
    dontTouch(ftran_size)
    dontTouch(ltran_size)
    dontTouch(mtran_num)
    val is_single_tran = if(conf.NVDLA_MCIF_BURST_SIZE > 1) Some(Wire(Bool())) else None
    if(conf.NVDLA_MCIF_BURST_SIZE > 1){
        val stt_offset = in_cmd_addr(conf.NVDLA_MEMORY_ATOMIC_LOG2+conf.NVDLA_MCIF_BURST_SIZE_LOG2-1, conf.NVDLA_MEMORY_ATOMIC_LOG2)
        val size_offset = in_cmd_size(conf.NVDLA_MCIF_BURST_SIZE_LOG2-1, 0)
        val end_offset = stt_offset + size_offset

        // calculate how many trans to be split
        is_single_tran.get := (stt_offset +& in_cmd_size) < conf.NVDLA_MCIF_BURST_SIZE.U
        val ftran_size_tmp = Mux(is_single_tran.get , size_offset ,(conf.NVDLA_MCIF_BURST_SIZE-1).U -& stt_offset)
        val ltran_size_tmp = Mux(is_single_tran.get , size_offset, end_offset)

        ftran_size := ftran_size_tmp
        ltran_size := ltran_size_tmp
        mtran_num := in_cmd_size -& ftran_size -& ltran_size -& 1.U
    }
    else{
        ftran_size := 0.U
        ltran_size := 0.U
        mtran_num := in_cmd_size -& 1.U
    }

    val dat_en = RegInit(false.B)
    dfifo_rd_prdy := dat_en & io.bpt2arb_dat_pd.ready
    // DATA FIFO read side: valid
    val out_size = Wire(UInt(3.W))
    val out_dat_vld  = dat_en & dfifo_rd_pvld
    val out_dat_data = dfifo_rd_data
    val out_dat_mask = dfifo_rd_pvld; //dfifo_rd_mask
    val beat_size = out_size

    val bpt2arb_cmd_accept = Wire(Bool())
    val cmd_en = RegInit(true.B)
    when(bpt2arb_cmd_accept){
        cmd_en := false.B
        dat_en := true.B
    }
    .elsewhen(bpt2arb_dat_accept & is_last_beat){
        cmd_en := true.B
        dat_en := false.B
    }


    //================
    // Beat Count: to count data per split req
    //================
    val beat_count = RegInit("b0".asUInt(2.W))
    when(bpt2arb_dat_accept){
        when(is_last_beat){
            beat_count := 0.U
        }
        .otherwise{
            beat_count := beat_count + 1.U
        }
    }

    is_last_beat := (beat_count === beat_size)

    // in AXI format
    //================
    // bsp out: size: this is in unit of 64B, including masked 32B data
    //================
    val is_ftran = Wire(Bool())
    val is_mtran = Wire(Bool())

    if(conf.NVDLA_MCIF_BURST_SIZE > 1){
        when(is_ftran){
            out_size := ftran_size
        }
        .elsewhen(is_mtran){
            out_size := (conf.NVDLA_MCIF_BURST_SIZE-1).U
        }
        .elsewhen(is_ltran){
            out_size := ltran_size
        }
    }
    else{
        out_size := 0.U
    }

    //================
    // bpt2arb: addr
    //================
    val out_addr = Reg(UInt(conf.NVDLA_MEM_ADDRESS_WIDTH.W))
    when(bpt2arb_cmd_accept){
        when(is_ftran){
            out_addr := in_cmd_addr +& ((ftran_size +& 1.U) << conf.NVDLA_MEMORY_ATOMIC_LOG2.U)
        }
        .otherwise{
            out_addr := out_addr +& (conf.NVDLA_MCIF_BURST_SIZE.U << conf.NVDLA_MEMORY_ATOMIC_LOG2.U)
        }
    }

    //================
    // tran count
    //================
    val req_num = Wire(UInt(conf.NVDLA_DMA_WR_SIZE.W))

    if(conf.NVDLA_MCIF_BURST_SIZE > 1){
        when(is_single_tran.get){
            req_num := 0.U
        }
        .otherwise{
            req_num := 1.U +& mtran_num(12, conf.NVDLA_MCIF_BURST_SIZE_LOG2)
        }
    }
    else{
        req_num := in_cmd_size
    }

    val req_count = RegInit("b0".asUInt(13.W))
    when(bpt2arb_dat_accept & is_last_beat){
        when(is_ltran){
            req_count := 0.U
        }
        .otherwise{
            req_count := req_count + 1.U
        }
    } 


    is_ftran := (req_count === 0.U)
    is_mtran := (req_count > 0.U && req_count < req_num)
    is_ltran := (req_count === req_num)

    val out_cmd_vld = cmd_en & in_cmd_vld
    val out_cmd_addr = Mux(is_ftran, in_cmd_addr, out_addr)
    val out_cmd_size = out_size

    val out_cmd_inc = false.B
    val out_cmd_swizzle = false.B 
    val out_cmd_odd = false.B

    val out_cmd_ftran = is_ftran
    val out_cmd_ltran = is_ltran
    val out_cmd_axid = io.axid
    val out_cmd_require_ack = in_cmd_require_ack & is_ltran

    io.bpt2arb_cmd_pd.bits := Cat(out_cmd_ftran, out_cmd_ltran, out_cmd_inc, out_cmd_odd, 
                                  out_cmd_swizzle, out_cmd_size, out_cmd_addr, out_cmd_require_ack,
                                  out_cmd_axid)

    io.bpt2arb_dat_pd.bits := Cat(out_dat_mask, out_dat_data)

    io.bpt2arb_cmd_pd.valid := out_cmd_vld
    io.bpt2arb_dat_pd.valid := out_dat_vld

    bpt2arb_cmd_accept := io.bpt2arb_cmd_pd.valid & io.bpt2arb_cmd_pd.ready
    bpt2arb_dat_accept := io.bpt2arb_dat_pd.valid & io.bpt2arb_dat_pd.ready

}}   


 object NV_NVDLA_MCIF_WRITE_IG_bptDriver extends App {
     implicit val conf: nvdlaConfig = new nvdlaConfig
     chisel3.Driver.execute(args, () => new NV_NVDLA_MCIF_WRITE_IG_bpt())
 }