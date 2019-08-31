package nvdla
import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_MCIF_READ_IG_bpt(implicit conf: xxifConfiguration) extends Module {
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
        val tieoff_lat_fifo_depth = Input(UInt(9.W))
    })


    val in_rdy_p = Wire(Bool())
    val in_rdy  = Wire(Bool())

    //////////////////////////////////////// pipe1
    val pipe_p1 = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_DMA_RD_REQ))
    pipe_p1.io.clk  := io.nvdla_core_clk
    pipe_p1.io.vi   := io.dma2bpt_req_valid
    pipe_p1.io.di   := io.dma2bpt_req_pd
    io.dma2bpt_req_ready := pipe_p1.io.ro

    pipe_p1.io.ri   := in_rdy_p
    val in_pd_p  = pipe_p1.io.dout
    val in_vld_p = pipe_p1.io.vo


    //////////////////////////////////////// pipe2
    val pipe_p2 = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_DMA_RD_REQ))
    pipe_p2.io.clk  := io.nvdla_core_clk
    pipe_p2.io.di   := in_pd_p
    pipe_p2.io.vi   := in_vld_p
    in_rdy_p        := pipe_p2.io.ro

    pipe_p2.io.ri   := in_rdy
    val in_pd   = pipe_p2.io.dout
    val in_vld  = pipe_p2.io.vo


    withClock(io.nvdla_core_clk) {
        val out_addr = Reg(UInt(conf.NVDLA_MEM_ADDRESS_WIDTH.W))
        val out_size = Wire(UInt(3.W))
        val out_size_tmp = Reg(UInt(3.W))

        val bpt2arb_accept = Wire(Bool())

        val ftran_size = Wire(UInt(3.W))
        val ltran_size = Wire(UInt(3.W))
        val mtran_num = Wire(UInt(conf.NVDLA_DMA_RD_SIZE.W))

        val out_odd = Wire(Bool())
        val out_swizzle = Wire(Bool())

        val count_req = RegInit(UInt(conf.NVDLA_DMA_RD_SIZE.W), 0.U)
        val req_num = Reg(UInt(conf.NVDLA_DMA_RD_SIZE.W))
        val lat_adv = Reg(Bool())
        val lat_cnt_cur = RegInit(UInt(9.W), 0.U)
        val lat_count_cnt = RegInit(UInt(9.W), 0.U)
        val lat_count_dec = RegInit(UInt(1.W), 0.U)

        val lat_fifo_free_slot = Wire(UInt(9.W))
        val mon_lat_fifo_free_slot_c = Wire(Bool())

        val slot_needed = Reg(UInt(3.W))


        val in_vld_pd = Fill(conf.NVDLA_DMA_RD_REQ, in_vld) & in_pd
        val in_addr = in_vld_pd(conf.NVDLA_MEM_ADDRESS_WIDTH-1, 0)
        val in_size = in_vld_pd(conf.NVDLA_DMA_RD_REQ-1, conf.NVDLA_MEM_ADDRESS_WIDTH)

        val is_ftran = (count_req===0.U)
        val is_mtran = (count_req>0.U && count_req<req_num)
        val is_ltran = (count_req===req_num)

        if(conf.NVDLA_MCIF_BURST_SIZE > 1) {
            val stt_offset = Wire(UInt(conf.NVDLA_MCIF_BURST_SIZE_LOG2.W))
            val end_offset = Wire(UInt(conf.NVDLA_MCIF_BURST_SIZE_LOG2.W))

            stt_offset  := in_addr(conf.NVDLA_MEMORY_ATOMIC_LOG2+conf.NVDLA_MCIF_BURST_SIZE_LOG2-1, conf.NVDLA_MEMORY_ATOMIC_LOG2)
            val size_offset = in_size(conf.NVDLA_MCIF_BURST_SIZE_LOG2-1, 0)
            val temp_result = stt_offset +& size_offset
            val mon_end_offset_c = temp_result(conf.NVDLA_MCIF_BURST_SIZE_LOG2)
            end_offset := temp_result(conf.NVDLA_MCIF_BURST_SIZE_LOG2-1, 0)

            val is_single_tran = (stt_offset + in_size) < conf.NVDLA_MCIF_BURST_SIZE.U
            val ftran_size_tmp = Mux(is_single_tran , size_offset ,(conf.NVDLA_MCIF_BURST_SIZE-1).U - stt_offset)
            val ltran_size_tmp = Mux(is_single_tran , 0.U, end_offset)

            ftran_size := Cat (Fill(3-conf.NVDLA_MCIF_BURST_SIZE_LOG2, 0.U), ftran_size_tmp)
            ltran_size := Cat (Fill(3-conf.NVDLA_MCIF_BURST_SIZE_LOG2, 0.U), ltran_size_tmp)

            mtran_num := in_size - ftran_size - ltran_size - 1.U

            when(is_ftran | is_ltran){
                slot_needed := out_size + 1.U
            } .otherwise {
                slot_needed := conf.NVDLA_PRIMARY_MEMIF_MAX_BURST_LENGTH.U
            }

            //================
            // bsp out: swizzle
            //================
            out_swizzle := (stt_offset(0)===1.U)
            out_odd := (in_size(0)===0.U)

            //================
            // tran count
            //================
            when(is_single_tran) {
                req_num := 0.U
            }.otherwise {
                req_num := 1.U + mtran_num(14, conf.NVDLA_MCIF_BURST_SIZE_LOG2)
            }
        } else {
            ftran_size := 0.U(3.W)
            ltran_size := 0.U(3.W)
            mtran_num := in_size - 1.U

            slot_needed := 1.U(3.W)

            //================
            // bsp out: swizzle
            //================
            out_swizzle := 0.U
            out_odd := 0.U

            //================
            // tran count
            //================
            req_num := in_size
        }


        val lat_fifo_stall_enable = (io.tieoff_lat_fifo_depth =/=0.U).asBool()
        lat_count_dec := io.dma2bpt_cdt_lat_fifo_pop
        val lat_count_inc = Mux(bpt2arb_accept && lat_fifo_stall_enable , slot_needed, 0.U)
        lat_adv := lat_count_inc =/= Cat(0.U(2.W), lat_count_dec)

        // lat cnt logic
        val lat_cnt_ext = Cat(0.U(2.W), lat_cnt_cur)
        val lat_cnt_mod = lat_cnt_cur + lat_count_inc - lat_count_dec
        val lat_cnt_new = Mux(lat_adv, lat_cnt_mod, lat_cnt_ext)
        val lat_cnt_nxt = lat_cnt_new

        // lat flops
        lat_cnt_cur := lat_cnt_nxt
        // lat output logic
        lat_count_cnt := lat_cnt_cur

//        Cat(mon_lat_fifo_free_slot_c, lat_fifo_free_slot) := io.tieoff_lat_fifo_depth - lat_count_cnt
        val tmp = io.tieoff_lat_fifo_depth - lat_count_cnt
        mon_lat_fifo_free_slot_c := tmp(8)
        lat_fifo_free_slot := tmp(7,0)
        val req_enable = (!lat_fifo_stall_enable) || (Cat(0.U(6.W), slot_needed) <= lat_fifo_free_slot)


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
//            val out_inc = is_ftran & is_ltran & out_swizzle && !out_odd
//            val beat_size = Wire(UInt(2.W))
//            val mon_out_beats_c = Wire(Bool())
//            Cat(mon_out_beats_c, beat_size) := out_size(2, 1) + out_inc
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


        val bpt2arb_addr = Mux(is_ftran, in_addr, out_addr)
        val bpt2arb_size = out_size
        val bpt2arb_swizzle = out_swizzle
        val bpt2arb_odd   = out_odd
        val bpt2arb_ltran = is_ltran
        val bpt2arb_ftran = is_ftran
        val bpt2arb_axid  = io.tieoff_axid

        val req_rdy = req_enable & io.bpt2arb_req_ready
        val req_vld = req_enable & in_vld;

        in_rdy := req_rdy & is_ltran;

        io.bpt2arb_req_valid := req_vld
        bpt2arb_accept := io.bpt2arb_req_valid & req_rdy
        io.bpt2arb_req_pd := Cat(bpt2arb_ftran, bpt2arb_ltran, bpt2arb_odd, bpt2arb_swizzle, bpt2arb_size, bpt2arb_addr, bpt2arb_axid)
    }
}

object NV_NVDLA_MCIF_READ_IG_bptDriver extends App {
    implicit val conf: xxifConfiguration = new xxifConfiguration
    chisel3.Driver.execute(args, () => new NV_NVDLA_MCIF_READ_IG_bpt())
}
