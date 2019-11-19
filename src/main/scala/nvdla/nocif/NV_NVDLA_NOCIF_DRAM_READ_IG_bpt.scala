package nvdla
import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_NOCIF_DRAM_READ_IG_bpt(implicit conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle{
        //general clock
        val nvdla_core_clk = Input(Clock())

        //dma2bpt
        val dma2bpt_req_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W)))
        val dma2bpt_cdt_lat_fifo_pop = Input(Bool())

        //bpt2arb
        val bpt2arb_req_pd = DecoupledIO(UInt(conf.NVDLA_DMA_RD_IG_PW.W))

        val tieoff_axid = Input(UInt(4.W))
        val tieoff_lat_fifo_depth = Input(UInt(9.W))
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
    /////////////////// pipe1 /////////////////////
    val pipe_p1 = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_DMA_RD_REQ))
    pipe_p1.io.clk  := io.nvdla_core_clk

    pipe_p1.io.vi := io.dma2bpt_req_pd.valid
    io.dma2bpt_req_pd.ready := pipe_p1.io.ro
    pipe_p1.io.di := io.dma2bpt_req_pd.bits
    
    val in_vld_p = pipe_p1.io.vo
    val in_pd_p  = pipe_p1.io.dout

    //////////////////// pipe2 ////////////////////
    val in_rdy = Wire(Bool())
    val pipe_p2 = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_DMA_RD_REQ))
    pipe_p2.io.clk  := io.nvdla_core_clk

    pipe_p2.io.vi := in_vld_p
    pipe_p1.io.ri := pipe_p2.io.ro
    pipe_p2.io.di := in_pd_p

    val in_vld = pipe_p2.io.vo
    pipe_p2.io.ri := in_rdy
    val in_pd = pipe_p2.io.dout

    val is_ltran = Wire(Bool())
    val req_rdy = Wire(Bool())
    in_rdy := req_rdy & is_ltran
    val in_vld_pd = Fill(conf.NVDLA_DMA_RD_REQ, in_vld) & in_pd

    // PKT_UNPACK_WIRE( dma_read_cmd , in_ , in_vld_pd )
    val in_addr = in_vld_pd(conf.NVDLA_MEM_ADDRESS_WIDTH-1, 0)
    val in_size = in_vld_pd(conf.NVDLA_DMA_RD_REQ-1, conf.NVDLA_MEM_ADDRESS_WIDTH)

    val stt_offset = if(conf.NVDLA_MEMORY_ATOMIC_SIZE == log2Ceil(conf.NVDLA_PRIMARY_MEMIF_WIDTH)) in_addr(log2Ceil(conf.NVDLA_MEMORY_ATOMIC_SIZE)+1, log2Ceil(conf.NVDLA_MEMORY_ATOMIC_SIZE)) 
                     else in_addr(log2Ceil(conf.NVDLA_MEMORY_ATOMIC_SIZE)+2, log2Ceil(conf.NVDLA_MEMORY_ATOMIC_SIZE)) 
    val size_offset = if(conf.NVDLA_MEMORY_ATOMIC_SIZE == log2Ceil(conf.NVDLA_PRIMARY_MEMIF_WIDTH)) in_size(1, 0)
                     else in_size(2, 0) 

    val end_offset = stt_offset + size_offset

    val is_single_tran = (stt_offset +& in_size) < (conf.NVDLA_MEM_MASK_BIT*4).U
    val ftran_size = Mux(is_single_tran , size_offset , (conf.NVDLA_MEM_MASK_BIT*4).U -& stt_offset)
    val ftran_num = ftran_size +& 1.U

    val ltran_size = Mux(is_single_tran, 0.U, end_offset)
    val mtran_num = Mux(is_single_tran, 0.U, end_offset +& 1.U)

    //================
    // check the empty entry of lat.fifo
    //================
    val is_ftran = Wire(Bool())
    val slot_needed = Wire(UInt(3.W))
    val out_size = Wire(UInt(3.W))
    val out_swizzle = Wire(Bool())

    if(conf.NVDLA_MEMORY_ATOMIC_LOG2 == conf.NVDLA_PRIMARY_MEMIF_WIDTH_LOG2){
        slot_needed := 1.U
    }
    else{
        when(is_single_tran){
            slot_needed := (out_size >> 1.U) +& 1.U
        }
        .elsewhen(is_ltran){
            slot_needed := ((out_size +& out_swizzle) >> 1.U) +& 1.U
        }
        .elsewhen(is_ftran){
            slot_needed := (out_size +& 1.U) >> 1.U
        }
        .otherwise{
            slot_needed := 4.U
        }
    }
    

    val lat_fifo_stall_enable = (io.tieoff_lat_fifo_depth =/= 0.U)
    val lat_count_dec = RegInit(false.B)
    lat_count_dec := io.dma2bpt_cdt_lat_fifo_pop
    val bpt2arb_accept = Wire(Bool())
    val lat_count_inc = Mux(bpt2arb_accept && lat_fifo_stall_enable , slot_needed, 0.U)

    val perf_lat = Module(new NV_COUNTER_STAGE_lat)
    perf_lat.io.clk := io.nvdla_core_clk
    perf_lat.io.lat_cnt_inc := lat_count_inc
    perf_lat.io.lat_cnt_dec := lat_count_dec
    val lat_count_cnt = perf_lat.io.lat_cnt_cur

    val lat_fifo_free_slot = io.tieoff_lat_fifo_depth - lat_count_cnt
    val req_enable = (!lat_fifo_stall_enable) || (slot_needed <= lat_fifo_free_slot)
    
    //================
    // bsp out: swizzle
    //================
    val out_odd = Wire(Bool())

    if(conf.NVDLA_MEMORY_ATOMIC_LOG2 == conf.NVDLA_PRIMARY_MEMIF_WIDTH_LOG2){
        out_swizzle := false.B
        out_odd := false.B
    }
    else{
        out_swizzle:= (stt_offset(0) === true.B)
        out_odd := (in_size(0) === false.B)
    }

    //================
    // bsp out: size
    //================
    val is_mtran = Wire(Bool())
    if(conf.NVDLA_MEMORY_ATOMIC_LOG2 == conf.NVDLA_PRIMARY_MEMIF_WIDTH_LOG2){
        out_size := 0.U(3.W)
    }
    else{
        val out_size_tmp = Wire(UInt(3.W))
        when(is_ftran){
            out_size_tmp := ftran_size
        } .elsewhen(is_mtran) {
            out_size_tmp := conf.NVDLA_MCIF_BURST_SIZE.U - 1.U
        } .elsewhen(is_ltran) {
            out_size_tmp := ltran_size
        }

        out_size := out_size_tmp
    }

    //================
    // bsp out: USER: SIZE
    //================
    val out_inc = is_ftran & is_ltran & out_swizzle && !out_odd
    val beat_size_NC = out_size(2, 1) + out_inc

    //================
    // bpt2arb: addr
    //================
    val out_addr = Reg(UInt(conf.NVDLA_MEM_ADDRESS_WIDTH.W))
    when(bpt2arb_accept){
        when(is_ftran){
            if(conf.NVDLA_MEMORY_ATOMIC_LOG2 == conf.NVDLA_PRIMARY_MEMIF_WIDTH_LOG2){
                out_addr := in_addr +& (1.U << conf.NVDLA_MEMORY_ATOMIC_LOG2.U)
            }
            else{
                out_addr := in_addr +& ((ftran_size +& 1.U) << conf.NVDLA_MEMORY_ATOMIC_LOG2.U)
            }
        }
        .otherwise{
            if(conf.NVDLA_MEMORY_ATOMIC_LOG2 == conf.NVDLA_PRIMARY_MEMIF_WIDTH_LOG2){
                out_addr := out_addr +& (1.U << conf.NVDLA_MEMORY_ATOMIC_LOG2.U)
            }
            else{
                out_addr := out_addr +& (8.U << (conf.NVDLA_MEMORY_ATOMIC_LOG2-1).U)
            }
        }
    }

    //================
    // tran count
    //================
    val req_num = Wire(UInt(16.W))
    if(conf.NVDLA_MEMORY_ATOMIC_LOG2 == conf.NVDLA_PRIMARY_MEMIF_WIDTH_LOG2){
        req_num := in_size +& 1.U
    }
    else{
        when(is_single_tran){
            req_num := 1.U
        }
        .elsewhen(mtran_num === 0.U){
            req_num := 2.U
        }
        .otherwise{
            req_num := 2.U +& mtran_num(14, 3)
        }
    }


    val count_req = RegInit(0.U(14.W))
    when(bpt2arb_accept){
        when(is_ltran){
            count_req := 0.U
        }
        .otherwise{
            count_req := count_req + 1.U
        }
    }

    is_ftran := (count_req === 0.U)
    is_mtran := (count_req > 0.U && count_req < (req_num -& 1.U))
    is_ltran := (count_req === (req_num -& 1.U))

    val bpt2arb_addr = Mux(is_ftran, in_addr, out_addr)
    val bpt2arb_size = out_size
    val bpt2arb_swizzle = out_swizzle
    val bpt2arb_odd = out_odd
    val bpt2arb_ltran = is_ltran
    val bpt2arb_ftran = is_ftran
    val bpt2arb_axid  = io.tieoff_axid

    req_rdy := req_enable & io.bpt2arb_req_pd.ready
    val req_vld = req_enable & in_vld

    io.bpt2arb_req_pd.valid := req_vld
    bpt2arb_accept := io.bpt2arb_req_pd.valid & req_rdy
    io.bpt2arb_req_pd.bits := Cat(bpt2arb_ftran, bpt2arb_ltran, bpt2arb_odd, bpt2arb_swizzle, bpt2arb_size, bpt2arb_addr, bpt2arb_axid)
}}

object NV_NVDLA_NOCIF_DRAM_READ_IG_bptDriver extends App {
    implicit val conf: nvdlaConfig = new nvdlaConfig
    chisel3.Driver.execute(args, () => new NV_NVDLA_NOCIF_DRAM_READ_IG_bpt())
}
