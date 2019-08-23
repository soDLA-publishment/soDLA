package nvdla
import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_MCIF_READ_IG_bpt(implicit conf: nocifConfiguration) extends Module {
    val io = IO(new Bundle{
        //general clock
        val nvdla_core_clk = Input(Clock())

        //dma2bpt
        val dma2bpt_req_valid = Input(Bool())
        val dma2bpt_req_ready = Output(Bool())
        val dma2bpt_req_pd = Input(UInt(conf.NVDLA_DMA_RD_REQ.W))
        val dma2bpt_cdt_lat_fifo_pop = Input(Bool())

        //bpt2arb
        val bpt2arb_req_valid = Output(Bool())
        val bpt2arb_req_ready = Input(Bool())
        val bpt2arb_req_pd = Output(UInt(conf.NVDLA_DMA_RD_IG_PW.W))

        val tieoff_axid = Input(UInt(4.W))
        val tieoff_lat_fifo_depth = Input(UInt(8.W))
    })


    val in_pd_p  = Wire(UInt(conf.NVDLA_DMA_RD_REQ.W))
    val in_vld_p = Wire(Bool())
    val in_rdy_p = Wire(Bool())

    val in_pd   = Wire(UInt(conf.NVDLA_DMA_RD_REQ.W))
    val in_vld  = Wire(Bool())
    val in_rdy  = Wire(Bool())

    //////////////////////////////////////// pipe1
    val pipe_p1 = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_DMA_RD_REQ))
    pipe_p1.io.clk  := io.nvdla_core_clk
    pipe_p1.io.di   := io.dma2bpt_req_pd
    pipe_p1.io.vi   := io.dma2bpt_req_valid
    io.dma2bpt_req_ready := pipe_p1.io.ri

    in_pd_p     := pipe_p1.io.dout
    in_vld_p    := pipe_p1.io.vo
    pipe_p1.io.ro := in_rdy_p


    //////////////////////////////////////// pipe2
    val pipe_p2 = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_DMA_RD_REQ))
    pipe_p2.io.clk  := io.nvdla_core_clk
    pipe_p2.io.di   := in_pd_p
    pipe_p2.io.vi   := in_vld_p
    in_rdy_p        := pipe_p2.io.ri

    in_pd  := pipe_p2.io.dout
    in_vld := pipe_p2.io.vo
    pipe_p2.io.ro := in_rdy


    withClock(io.nvdla_core_clk) {
        val out_addr = Reg(UInt(conf.NVDLA_MEM_ADDRESS_WIDTH.W))
        val out_size = Wire(UInt(3.W))
        val out_size_tmp = Reg(UInt(3.W))
        val beat_size = Wire(UInt(2.W))
        val bpt2arb_accept = Wire(Bool())
        val bpt2arb_addr = Wire(UInt(conf.NVDLA_MEM_ADDRESS_WIDTH.W))
        val bpt2arb_axid = Wire(UInt(4.W))
        val bpt2arb_ftran = Wire(Bool())
        val bpt2arb_ltran = Wire(Bool())
        val bpt2arb_odd = Wire(Bool())
        val bpt2arb_size = Wire(UInt(3.W))
        val bpt2arb_swizzle = Wire(Bool())

        val stt_offset = Wire(UInt(conf.NVDLA_MCIF_BURST_SIZE_LOG2.W))
        val end_offset = Wire(UInt(conf.NVDLA_MCIF_BURST_SIZE_LOG2.W))
        val size_offset = Wire(UInt(conf.NVDLA_MCIF_BURST_SIZE_LOG2.W))
        val ftran_size_tmp = Wire(UInt(conf.NVDLA_MCIF_BURST_SIZE_LOG2.W))
        val ltran_size_tmp = Wire(UInt(conf.NVDLA_MCIF_BURST_SIZE_LOG2.W))
        val mon_end_offset_c = Wire(Bool())

        val ftran_size = Wire(UInt(3.W))
        val ltran_size = Wire(UInt(3.W))
        val mtran_num = Wire(UInt(conf.NVDLA_DMA_RD_SIZE.W))

        val in_addr = Wire(UInt(conf.NVDLA_MEM_ADDRESS_WIDTH.W))
        val in_size = Wire(UInt(conf.NVDLA_DMA_RD_SIZE.W))

        val in_vld_pd  = Wire(UInt(conf.NVDLA_DMA_RD_REQ.W))

        val is_ftran = Wire(Bool())
        val is_ltran = Wire(Bool())
        val is_mtran = Wire(Bool())
        val is_single_tran = Wire(Bool())
        val mon_out_beats_c = Wire(Bool())

        val out_inc = Wire(Bool())
        val out_odd = Wire(Bool())
        val out_swizzle = Wire(Bool())
        val req_enable  = Wire(Bool())
        val req_rdy  = Wire(Bool())
        val req_vld  = Wire(Bool())

        in_rdy := req_rdy & is_ltran;

        in_vld_pd := Fill(conf.NVDLA_DMA_RD_REQ, in_vld) & in_pd
        in_addr(conf.NVDLA_MEM_ADDRESS_WIDTH -1, 0) := in_vld_pd(conf.NVDLA_MEM_ADDRESS_WIDTH-1, 0)
        in_size(conf.NVDLA_DMA_RD_SIZE-1, 0) := in_vld_pd(conf.NVDLA_DMA_RD_REQ-1, conf.NVDLA_MEM_ADDRESS_WIDTH)

        if(conf.NVDLA_MCIF_BURST_SIZE > 1) {
            stt_offset(conf.NVDLA_MCIF_BURST_SIZE_LOG2-1, 0) := in_addr(conf.NVDLA_MEMORY_ATOMIC_LOG2+conf.NVDLA_MCIF_BURST_SIZE_LOG2-1, conf.NVDLA_MEMORY_ATOMIC_LOG2)
            size_offset(conf.NVDLA_MCIF_BURST_SIZE_LOG2-1,0) := in_size(conf.NVDLA_MCIF_BURST_SIZE_LOG2-1, 0)
            Cat(mon_end_offset_c, end_offset(conf.NVDLA_MCIF_BURST_SIZE_LOG2-1, 0)) := Cat(stt_offset, size_offset)


            is_single_tran := Mux((stt_offset + in_size) < conf.NVDLA_MCIF_BURST_SIZE.U , true.B, false.B)

            ftran_size_tmp(conf.NVDLA_MCIF_BURST_SIZE_LOG2-1, 0) := Mux(is_single_tran , size_offset ,(conf.NVDLA_MCIF_BURST_SIZE-1).U - stt_offset)
            ltran_size_tmp(conf.NVDLA_MCIF_BURST_SIZE_LOG2-1, 0) := Mux(is_single_tran , 0.U, end_offset)

            ftran_size(2, 0) := Cat (Fill(3-conf.NVDLA_MCIF_BURST_SIZE_LOG2, 0.U), ftran_size_tmp)
            ltran_size(2, 0) := Cat (Fill(3-conf.NVDLA_MCIF_BURST_SIZE_LOG2, 0.U), ltran_size_tmp)

            mtran_num := in_size - ftran_size - ltran_size - 1.U
        } else {
            ftran_size := 0.U(3.W)
            ltran_size := 0.U(3.W)
            mtran_num := in_size - 1.U
        }


        val count_req = RegInit(UInt(conf.NVDLA_DMA_RD_SIZE.W), 0.U)
        val req_num = Reg(UInt(conf.NVDLA_DMA_RD_SIZE.W))
        val lat_fifo_stall_enable = Wire(Bool())
        val lat_adv = Reg(Bool())
        val lat_cnt_ext = Reg(UInt(11.W))
        val lat_cnt_mod = Reg(UInt(11.W))
        val lat_cnt_new = Reg(UInt(11.W))
        val lat_cnt_nxt = Reg(UInt(11.W))
        val lat_cnt_cur = RegInit(UInt(9.W), 0.U)
        val lat_count_cnt = RegInit(UInt(9.W), 0.U)
        val lat_count_dec = RegInit(UInt(1.W), 0.U)
        val lat_count_inc = Wire(UInt(3.W))
        val lat_fifo_free_slot = Wire(UInt(9.W))
        val mon_lat_fifo_free_slot_c = Wire(Bool())



        val slot_needed = Reg(UInt(3.W))


        if(conf.NVDLA_MCIF_BURST_SIZE > 1) {
            when(is_ftran | is_ltran){
                slot_needed := out_size + 1.U
            } .otherwise {
                slot_needed := conf.NVDLA_PRIMARY_MEMIF_MAX_BURST_LENGTH.U
            }
        } else {
            slot_needed := 1.U(3.W)
        }

        lat_fifo_stall_enable := (io.tieoff_lat_fifo_depth =/=0.U)
        lat_count_dec := io.dma2bpt_cdt_lat_fifo_pop
        lat_count_inc := Mux(bpt2arb_accept && lat_fifo_stall_enable , slot_needed, 0.U)
        lat_adv := lat_count_inc =/= Cat(0.U(2.W), lat_count_dec)

        // lat cnt logic
        lat_cnt_ext := Cat(0.U(2.W), lat_cnt_cur)
        lat_cnt_mod := lat_cnt_cur + lat_count_inc - lat_count_dec
        lat_cnt_new := Mux(lat_adv, lat_cnt_mod, lat_cnt_ext)
        lat_cnt_nxt := lat_cnt_new

        // lat flops
        lat_cnt_cur := lat_cnt_nxt
        // lat output logic
        lat_count_cnt := lat_cnt_cur

        Cat(mon_lat_fifo_free_slot_c, lat_fifo_free_slot) := io.tieoff_lat_fifo_depth - lat_count_cnt

        req_enable := (!lat_fifo_stall_enable) || (Cat(0.U(6.W), slot_needed) <= lat_fifo_free_slot)

        //================
        // bsp out: swizzle
        //================
        if(conf.NVDLA_DMA_MASK_BIT == 2) {
            out_swizzle := (stt_offset(0)===1.U)
            out_odd := (in_size(0)===0.U)
        } else {
            out_swizzle := 0.U
            out_odd := 0.U
        }

        //================
        // bsp out: size
        //================
        if(conf.NVDLA_MCIF_BURST_SIZE > 1) {
            out_size_tmp := 0.U
            when(is_ftran ){
                out_size_tmp := ftran_size
            } .elsewhen(is_mtran) {
                out_size_tmp := conf.NVDLA_MCIF_BURST_SIZE.U
            } .elsewhen(is_ltran) {
                out_size_tmp := ltran_size
            }

            out_size := out_size_tmp
        } else {
            out_size := 0.U(3.W)
        }

        if (conf.NVDLA_MEMIF_WIDTH > conf.NVDLA_MEMORY_ATOMIC_WIDTH) {
            //================
            // bsp out: USER: SIZE
            //================
            out_inc := is_ftran & is_ltran & out_swizzle && !out_odd
            Cat(mon_out_beats_c, beat_size) := out_size(2, 1) + out_inc
        }


        //================
        // bpt2arb: addr
        //================
        when(bpt2arb_accept) {
            when(is_ftran) {
                if(conf.NVDLA_MCIF_BURST_SIZE > 1) {
                    out_addr := in_addr +& ((ftran_size+&1.U) <<conf.NVDLA_MEMORY_ATOMIC_LOG2.U).asUInt()
                } else {
                    out_addr := in_addr +& (1.U <<conf.NVDLA_MEMORY_ATOMIC_LOG2.U).asUInt()
                }
            } .otherwise {
                out_addr := out_addr +& (conf.NVDLA_MCIF_BURST_SIZE.U << conf.NVDLA_MEMORY_ATOMIC_LOG2.U).asUInt()

            }

            when(is_ltran) {
                count_req := 0.U
            } .otherwise {
                count_req := count_req + 1.U
            }
        }

        //================
        // tran count
        //================
        if(conf.NVDLA_MCIF_BURST_SIZE > 1) {
            when(is_single_tran) {
                req_num := 0.U
            }.otherwise {
                req_num := 1.U + mtran_num(14, conf.NVDLA_MCIF_BURST_SIZE_LOG2)
            }
        } else {
            req_num := in_size
        }


        is_ftran := (count_req===0.U)
        is_mtran := (count_req>0.U && count_req<req_num)
        is_ltran := (count_req===req_num)

        bpt2arb_addr := Mux(is_ftran, in_addr, out_addr)
        bpt2arb_size := out_size
        bpt2arb_swizzle := out_swizzle
        bpt2arb_odd   := out_odd
        bpt2arb_ltran := is_ltran
        bpt2arb_ftran := is_ftran
        bpt2arb_axid  := io.tieoff_axid

        req_rdy := req_enable & io.bpt2arb_req_ready
        req_vld := req_enable & in_vld;

        io.bpt2arb_req_valid := req_vld
        bpt2arb_accept := io.bpt2arb_req_valid & req_rdy

        io.bpt2arb_req_pd(3, 0) :=  bpt2arb_axid
        io.bpt2arb_req_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+3, 4) := bpt2arb_addr(conf.NVDLA_MEM_ADDRESS_WIDTH-1, 0)
        io.bpt2arb_req_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+6, conf.NVDLA_MEM_ADDRESS_WIDTH+4) := bpt2arb_size(2, 0)
        io.bpt2arb_req_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+7)  :=    bpt2arb_swizzle
        io.bpt2arb_req_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+8)  :=    bpt2arb_odd
        io.bpt2arb_req_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+9)  :=    bpt2arb_ltran
        io.bpt2arb_req_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+10) :=    bpt2arb_ftran
    }

}
