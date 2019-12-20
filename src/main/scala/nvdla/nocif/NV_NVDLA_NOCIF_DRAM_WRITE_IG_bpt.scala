package nvdla

import chisel3._
import chisel3.util._


class NV_NVDLA_NOCIF_DRAM_WRITE_IG_bpt(implicit conf:nvdlaConfig)  extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val pwrbus_ram_pd = Input(UInt(32.W))

        val axid = Input(UInt(4.W))
        
        //dma2bpt
        val dma2bpt_req_pd = Flipped(DecoupledIO(UInt((conf.NVDLA_MEMIF_WIDTH+conf.NVDLA_MEM_MASK_BIT+1).W)))

        //bpt2arb
        val bpt2arb_cmd_pd = DecoupledIO(UInt(conf.NVDLA_DMA_WR_IG_PW.W))
        val bpt2arb_dat_pd = DecoupledIO(UInt((conf.NVDLA_MEMIF_WIDTH+2).W))
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

    val pipe_p1 = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_MEMIF_WIDTH+conf.NVDLA_MEM_MASK_BIT+1))
    pipe_p1.io.clk := io.nvdla_core_clk
    pipe_p1.io.vi := io.dma2bpt_req_pd.valid
    io.dma2bpt_req_pd.ready := pipe_p1.io.ro
    pipe_p1.io.di := io.dma2bpt_req_pd.bits

    val pipe_p2 = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_MEMIF_WIDTH+conf.NVDLA_MEM_MASK_BIT+1))
    pipe_p2.io.clk := io.nvdla_core_clk

    pipe_p2.io.vi := pipe_p1.io.vo
    pipe_p1.io.ri := pipe_p2.io.ro
    pipe_p2.io.di := pipe_p1.io.dout

    val ipipe_vld = pipe_p2.io.vo
    pipe_p2.io.ri := ipipe_rdy
    val ipipe_pd = pipe_p2.io.dout

    val ipipe_cmd_vld = ipipe_vld && (ipipe_pd(conf.NVDLA_MEMIF_WIDTH+conf.NVDLA_MEM_MASK_BIT) === 0.U)
    val dfifo_wr_vld = ipipe_vld && (ipipe_pd(conf.NVDLA_MEMIF_WIDTH+conf.NVDLA_MEM_MASK_BIT) === 1.U)

    val dfifo_wr_mask = Wire(UInt(2.W))
    if(conf.NVDLA_MEM_MASK_BIT == 1) {
        dfifo_wr_mask := 3.U
    } else {
        dfifo_wr_mask := ipipe_pd(conf.NVDLA_MEMIF_WIDTH+conf.NVDLA_MEM_MASK_BIT-1, conf.NVDLA_MEMIF_WIDTH)
    }
    
    val ipipe_cmd_rdy = Wire(Bool())
    val dfifo_wr_rdy = Wire(Bool())
    ipipe_rdy := (ipipe_cmd_vld & ipipe_cmd_rdy) || (dfifo_wr_vld & dfifo_wr_rdy)

//==================
// 2nd Stage: CMD PIPE

    val ipipe_cmd_pd = ipipe_pd(conf.NVDLA_DMA_WR_IG_PW, 0)

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
    val in_cmd_vld_pd = Fill(conf.NVDLA_MEM_ADDRESS_WIDTH+14, in_cmd_vld) & in_cmd_pd

    val in_cmd_addr = in_cmd_vld_pd(conf.NVDLA_MEM_ADDRESS_WIDTH-1, 0)
    val in_cmd_size = in_cmd_vld_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+12, conf.NVDLA_MEM_ADDRESS_WIDTH)
    val in_cmd_require_ack  = in_cmd_vld_pd(conf.NVDLA_DMA_WR_IG_PW)

//==================
// data will be pushed into dFIFO first in dma format
// 2nd Stage: DATA FIFO: WRITE side

    val dfifo_wr_pd = ipipe_pd(conf.NVDLA_MEMIF_WIDTH-1, 0)

    val dfifo1_wr_prdy = Wire(Bool())
    val dfifo0_wr_prdy = Wire(Bool())
    val dfifo0_wr_pvld = dfifo_wr_vld && dfifo_wr_mask(0) && dfifo1_wr_prdy
    val dfifo1_wr_pvld = dfifo_wr_vld && dfifo_wr_mask(1) && dfifo0_wr_prdy
    val dfifo0_wr_pd = dfifo_wr_pd(conf.NVDLA_MEMIF_WIDTH/2-1, 0)
    val dfifo1_wr_pd = dfifo_wr_pd(conf.NVDLA_MEMIF_WIDTH-1, (conf.NVDLA_MEMIF_WIDTH/2))
    val dfifo_wr_rdy = dfifo0_wr_prdy & dfifo1_wr_prdy;

//    val mon_dfifo0_wr_pd = dfifo0_wr_pvld & (^dfifo0_wr_pd);
//    val mon_dfifo1_wr_pd = dfifo1_wr_pvld & (^dfifo1_wr_pd);


    val dfifo0_rd_prdy = Wire(Bool())
    val dfifo1_rd_prdy = Wire(Bool())
    val u_dfifo0 = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_MEMIF_WIDTH))
    u_dfifo0.io.clk := io.nvdla_core_clk
    u_dfifo0.io.vi := dfifo0_wr_pvld
    dfifo0_wr_prdy := u_dfifo0.io.ro
    u_dfifo0.io.di := dfifo0_wr_pd
    val dfifo0_rd_pvld = u_dfifo0.io.vo
    u_dfifo0.io.ri := dfifo0_rd_prdy
    val dfifo0_rd_pd = u_dfifo0.io.dout

    val u_dfifo1 = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_MEMIF_WIDTH))
    u_dfifo1.io.clk := io.nvdla_core_clk
    u_dfifo1.io.vi := dfifo1_wr_pvld
    dfifo1_wr_prdy := u_dfifo1.io.ro
    u_dfifo1.io.di := dfifo1_wr_pd
    val dfifo1_rd_pvld = u_dfifo1.io.vo
    u_dfifo1.io.ri := dfifo1_rd_prdy
    val dfifo1_rd_pd = u_dfifo1.io.dout

    val stt_offset = Wire(UInt(3.W))
    val size_offset = Wire(UInt(3.W))
    val is_stt_addr_32_aligned = Wire(Bool())
    val in_size_is_odd = Wire(Bool())

    if(conf.NVDLA_MEMORY_ATOMIC_LOG2 == conf.NVDLA_PRIMARY_MEMIF_WIDTH_LOG2){
        stt_offset := in_cmd_addr(conf.NVDLA_MEMORY_ATOMIC_LOG2+1, conf.NVDLA_MEMORY_ATOMIC_LOG2)
        size_offset := in_cmd_size(1,0)
        is_stt_addr_32_aligned := false.B
        in_size_is_odd := false.B
    } else {
        stt_offset := in_cmd_addr(conf.NVDLA_MEMORY_ATOMIC_LOG2+2, conf.NVDLA_MEMORY_ATOMIC_LOG2)
        size_offset := in_cmd_size(2, 0)
        is_stt_addr_32_aligned := (stt_offset(0) === true.B)
        in_size_is_odd := (in_cmd_size(0) === false.B)
    }

    val end_offset = stt_offset +& size_offset
    val is_swizzle = is_stt_addr_32_aligned

    val in_size_is_even = Mux((conf.NVDLA_MEMORY_ATOMIC_LOG2.U === conf.NVDLA_PRIMARY_MEMIF_WIDTH_LOG2.U), true.B, (in_cmd_size(0) === true.B))
    val large_req_grow = is_swizzle & in_size_is_even

//==================
// dFIFO popping in AXI format
//==================

    val in_dat_beats = in_cmd_size(12, 1) +& large_req_grow
    val in_dat_cnt = RegInit(0.U(13.W))
    val in_dat_last = (in_dat_cnt === in_dat_beats)
    when(bpt2arb_dat_accept){
        when(in_dat_last){
            in_dat_cnt := 0.U
        }.otherwise{
            in_dat_cnt := in_dat_cnt + 1.U
        }
    }    

    val in_dat_first = (in_dat_cnt === 0.U)

    val in_dat0_dis = Mux((in_size_is_even & is_swizzle), in_dat_last, false.B)
    val in_dat1_dis = Reg(Bool())

    when(in_size_is_even){
        in_dat1_dis := Mux(is_swizzle, in_dat_first, false.B)
    }.otherwise{
        in_dat1_dis := Mux(is_swizzle, in_dat_first, in_dat_last)
    }

    val in_dat0_en = !in_dat0_dis
    val in_dat1_en = !in_dat1_dis 

    val dat_en = RegInit(false.B)
    val cmd_en = RegInit(true.B)
    val bpt2arb_cmd_accept = Wire(Bool())

    when(bpt2arb_cmd_accept){
        cmd_en := false.B
        dat_en := true.B
    }
    .elsewhen(bpt2arb_dat_accept & is_last_beat){
        cmd_en := true.B
        dat_en := false.B
    }

// vld & data & mask
    val in_dat0_pvld = dfifo0_rd_pvld & in_dat0_en
    val in_dat0_data = dfifo0_rd_pd
    val in_dat0_mask = in_dat0_en

    val in_dat1_pvld = dfifo1_rd_pvld & in_dat1_en
    val in_dat1_data = dfifo1_rd_pd
    val in_dat1_mask = in_dat1_en

    val in_dat_en = Cat(in_dat1_en, in_dat0_en)
    val in_dat_vld = MuxLookup(in_dat_en, false.B, 
                                Array(  0.U -> false.B,
                                        1.U -> in_dat0_pvld,
                                        2.U -> in_dat1_pvld,
                                        3.U -> in_dat0_pvld & in_dat1_pvld))
    
// dFIFO read side: ready
    val dfifo_rd_prdy = dat_en & io.bpt2arb_dat_pd.ready;
    dfifo0_rd_prdy := in_dat0_en & dfifo_rd_prdy & (in_dat1_dis || in_dat1_pvld)
    dfifo1_rd_prdy := in_dat1_en & dfifo_rd_prdy & (in_dat0_dis || in_dat0_pvld)

// Swizzle: vld and data
    val swizzle_dat0_data = Mux(is_swizzle, in_dat1_data, in_dat0_data)
    val swizzle_dat0_mask = Mux(is_swizzle, in_dat1_mask, in_dat0_mask)

    val swizzle_dat1_data = Mux(is_swizzle, in_dat0_data, in_dat1_data)
    val swizzle_dat1_mask = Mux(is_swizzle, in_dat0_mask, in_dat1_mask)

// DATA FIFO read side: valid
    io.bpt2arb_dat_pd.valid := dat_en & in_dat_vld
    val out_dat_data = Cat(swizzle_dat1_data,swizzle_dat0_data)
    val out_dat_mask = Cat(swizzle_dat1_mask,swizzle_dat0_mask)

// calculate how many trans to be split
    val is_single_tran = ((stt_offset + in_cmd_size) < (conf.NVDLA_MEM_MASK_BIT*4).U)

    val ftran_size = Mux(is_single_tran, size_offset, (conf.NVDLA_MEM_MASK_BIT*4-1).asUInt(3.W) - stt_offset)

    val ltran_size = Mux(is_single_tran, size_offset, end_offset)
    val mtran_num = in_cmd_size - ftran_size - ltran_size - 1.U

    //================
    // Beat Count: to count data per split req
    //================

    val out_size = RegInit(0.U(3.W))
    val out_cmd_inc = Wire(Bool())

    val beat_size = out_size(2, 1) +& out_cmd_inc

    val beat_count = RegInit("b0".asUInt(2.W))
    when(bpt2arb_dat_accept){
        when(is_last_beat){
            beat_count := 0.U
        }
        .otherwise{
            beat_count := beat_count + 1.U
        }
    }

    is_last_beat := Mux(
        (conf.NVDLA_MEMORY_ATOMIC_LOG2.U === conf.NVDLA_PRIMARY_MEMIF_WIDTH_LOG2.U), 
        (beat_count === out_size),
        (beat_count === beat_size)
        )

    // in AXI format
    //================
    // bsp out: size: this is in unit of 64B, including masked 32B data
    //================
    val is_ftran = Wire(Bool())
    val is_mtran = Wire(Bool())

    if(conf.NVDLA_MEMORY_ATOMIC_LOG2 == conf.NVDLA_PRIMARY_MEMIF_WIDTH_LOG2){
        out_size := 0.U
    }
    else{
        when(is_ftran){
            out_size := ftran_size
        }
        .elsewhen(is_mtran){
            out_size := 7.U
        }
        .elsewhen(is_ltran){
            out_size := ltran_size
        }
    }

    //================
    // bpt2arb: addr
    //================
    val out_addr = Reg(UInt(conf.NVDLA_MEM_ADDRESS_WIDTH.W))
    when(bpt2arb_cmd_accept){
        when(is_ftran){
            if(conf.NVDLA_MEMORY_ATOMIC_LOG2 == conf.NVDLA_PRIMARY_MEMIF_WIDTH_LOG2){
                out_addr := in_cmd_addr +& (1.U << conf.NVDLA_MEMORY_ATOMIC_LOG2.U)
            } else {
                out_addr := in_cmd_addr +& ((ftran_size+1.U) << 5.U)
            }
        }.otherwise{
            if(conf.NVDLA_MEMORY_ATOMIC_LOG2 == conf.NVDLA_PRIMARY_MEMIF_WIDTH_LOG2){
                out_addr := out_addr +& (1.U << conf.NVDLA_MEMORY_ATOMIC_LOG2.U)
            } else {
                out_addr := out_addr +& 256.U
            }
        }
    }

    //================
    // tran count
    //================
    val req_num = Reg(UInt(14.W))

    if(conf.NVDLA_MEMORY_ATOMIC_LOG2 == conf.NVDLA_PRIMARY_MEMIF_WIDTH_LOG2){
        req_num := in_cmd_size +& 1.U
    } else {
        when(is_single_tran){
            req_num := 1.U
        }.elsewhen(mtran_num === 0.U){
            req_num := 2.U
        }.otherwise{
            req_num := 2.U +& mtran_num(12, 3)
        }
    }

    val req_count = RegInit("b0".asUInt(11.W))
    when(bpt2arb_dat_accept & is_last_beat){
        when(is_ltran){
            req_count := 0.U
        }.otherwise{
            req_count := req_count + 1.U
        }
    } 

    is_ftran := (req_count === 0.U)
    is_mtran := (req_count > 0.U && req_count < req_num - 1.U)
    is_ltran := (req_count === req_num - 1.U)

// CMD PATH
    val out_cmd_addr = Mux(is_ftran, in_cmd_addr, out_addr)
    val out_cmd_inc = is_ltran & is_ftran & large_req_grow
    val out_cmd_require_ack = in_cmd_require_ack & is_ltran
    io.bpt2arb_cmd_pd.valid := cmd_en & in_cmd_vld

    io.bpt2arb_cmd_pd.bits := Cat(is_ftran, is_ltran, out_cmd_inc, in_size_is_odd, 
                                  is_swizzle, out_size, out_cmd_addr, out_cmd_require_ack,
                                  io.axid)
    bpt2arb_cmd_accept := io.bpt2arb_cmd_pd.valid & io.bpt2arb_cmd_pd.ready

    io.bpt2arb_dat_pd.bits := Cat(out_dat_mask, out_dat_data)
    bpt2arb_dat_accept := io.bpt2arb_dat_pd.valid & io.bpt2arb_dat_pd.ready

}}   

