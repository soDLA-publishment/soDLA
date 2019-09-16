package nvdla
import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_MCIF_READ_IG_cvt(implicit conf: xxifConfiguration) extends Module {
    val io = IO(new Bundle{
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())
        val reg2dp_rd_os_cnt = Input(UInt(8.W))

        //eg2ig
        val eg2ig_axi_vld = Input(Bool())

        //spt2cvt
        val spt2cvt_req_valid = Input(Bool())
        val spt2cvt_req_ready = Output(Bool())
        val spt2cvt_req_pd = Input(UInt((conf.NVDLA_MEM_ADDRESS_WIDTH+11).W))

        //mcif2noc
        val mcif2noc_axi_ar_arvalid = Output(Bool())
        val mcif2noc_axi_ar_arready = Input(Bool())
        val mcif2noc_axi_ar_arid = Output(UInt(8.W))
        val mcif2noc_axi_ar_arlen = Output(UInt(4.W))
        val mcif2noc_axi_ar_araddr = Output(UInt(conf.NVDLA_MEM_ADDRESS_WIDTH.W))
    })

    withClock(io.nvdla_core_clk){
        val cmd_vld = io.spt2cvt_req_valid
        val cmd_rdy = Wire(Bool())
        io.spt2cvt_req_ready := cmd_rdy

        val cmd_axid = io.spt2cvt_req_pd(3,0)
        val cmd_addr = io.spt2cvt_req_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+3,4)
        val cmd_size = io.spt2cvt_req_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+6, conf.NVDLA_MEM_ADDRESS_WIDTH+4);
        val cmd_swizzle = io.spt2cvt_req_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+7)
        val cmd_odd  =  io.spt2cvt_req_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+8)
        val cmd_ltran = io.spt2cvt_req_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+9)
        val cmd_ftran = io.spt2cvt_req_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+10)

        val eg2ig_axi_vld_d = RegInit(true.B)

//        val os_cnt = Wire(UInt(9.W))
        val os_cnt_cur = RegInit(UInt(9.W), 0.U)
        val os_cnt_ext = Wire(UInt(11.W))
        val os_cnt_mod = Wire(UInt(11.W))
        val os_cnt_new = Wire(UInt(11.W))
        val os_cnt_nxt = Wire(UInt(11.W))
        val os_cnt_full = Wire(Bool())

        val axi_cmd_rdy = Wire(Bool())
        val axi_cmd_vld = Wire(Bool())
        val axi_len = Wire(UInt(2.W))
        val axi_axid = cmd_axid;
        val axi_addr = Cat(cmd_addr(conf.NVDLA_MEM_ADDRESS_WIDTH-1, conf.NVDLA_MEMORY_ATOMIC_LOG2), Fill(conf.NVDLA_MEMORY_ATOMIC_LOG2, 0.U))
        val os_inp_add_nxt = Mux(cmd_vld, (axi_len + 1.U), 0.U(3.W))
        val os_inp_sub_nxt = Mux(eg2ig_axi_vld_d,  1.U, 0.U)
        val os_inp_nxt = os_cnt_cur + os_inp_add_nxt - os_inp_sub_nxt
        val os_cnt_add_en = axi_cmd_vld & axi_cmd_rdy
        val os_cnt_sub_en = eg2ig_axi_vld_d
        val os_cnt_cen = os_cnt_add_en | os_cnt_sub_en
        val os_cnt_add = Mux(os_cnt_add_en, (axi_len + 1.U), 0.U(3.W))
        val os_cnt_sub = Mux(os_cnt_sub_en,  1.U(1.W), 0.U(1.W))
        val cfg_rd_os_cnt = io.reg2dp_rd_os_cnt
        val rd_os_cnt_ext = Cat(0.U, cfg_rd_os_cnt)


        os_cnt_full := os_inp_nxt > (rd_os_cnt_ext + 1.U);

        if(conf.NVDLA_PRIMARY_MEMIF_WIDTH > conf.NVDLA_MEMORY_ATOMIC_WIDTH) {
//            val cmd_swizzle = io.spt2cvt_req_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+7)
//            val cmd_odd = io.spt2cvt_req_pd(conf.NVDLA_MEM_ADDRESS_WIDTH +8)
//            val cmd_ltran = io.spt2cvt_req_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+9)
//            val cmd_ftran = io.spt2cvt_req_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+10)
//            val stt_offset = cmd_addr(7, 5)
//
//            val mon_end_offset_c = Wire(UInt(1.W))
//            val end_offset       = Wire(UInt(3.W))
//            Cat(mon_end_offset_c, end_offset) = stt_offset + cmd_size;
//            val stt_addr_is_32_align = (stt_offset(0) === true.B )
//            val end_addr_is_32_align = (end_offset(0) === false.B )
//
//            val mon_axi_len_c = Wire(UInt(1.W))
//            val inc = cmd_ftran & cmd_ltran & (cmd_size(0)===1) & cmd_swizzle;
//            Cat(mon_axi_len_c, axi_len)  = cmd_size(2, 1) + inc;
//
//            val cq_wr_pvld = cmd_vld & axi_cmd_rdy & !os_cnt_full;
//            val cq_wr_pd = Cat(cmd_ltran & end_addr_is_32_align, cmd_ftran & stt_addr_is_32_align,
//                cmd_ltran, cmd_odd, cmd_swizzle, axi_len)
//            val cq_wr_thread_id = cmd_axid;
//
//            val axi_cmd_vld = cmd_vld & cq_wr_prdy & !os_cnt_full;
//            val cmd_rdy = axi_cmd_rdy & cq_wr_prdy & !os_cnt_full;
//
        } else {
            axi_cmd_vld := cmd_vld & !os_cnt_full
            cmd_rdy := axi_cmd_rdy & !os_cnt_full
            axi_len := cmd_size
        }

        eg2ig_axi_vld_d := io.eg2ig_axi_vld
        when(os_cnt_cen) {
            os_cnt_cur := os_cnt_nxt
        }

        val os_adv = os_cnt_add =/= Cat(0.U(2.W), os_cnt_sub)
        os_cnt_ext := Cat(0.U(2.W), os_cnt_cur)
        os_cnt_mod := os_cnt_cur + os_cnt_add - os_cnt_sub
        os_cnt_new := Mux(os_adv, os_cnt_mod, os_cnt_ext)
        os_cnt_nxt := os_cnt_new

        val axi_cmd_pd = Cat(axi_axid,axi_addr,axi_len)

        val opipe_axi_addr = Wire(UInt(conf.NVDLA_MEM_ADDRESS_WIDTH.W))
        val opipe_axi_axid = Wire(UInt(4.W))
        val opipe_axi_len = Wire(UInt(2.W))
        val opipe_axi_pd  = Wire(UInt((conf.NVDLA_MEM_ADDRESS_WIDTH+6).W))
        val opipe_axi_rdy = io.mcif2noc_axi_ar_arready
        val opipe_axi_vld = Wire(Bool())


        val pipe_p1 = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_MEM_ADDRESS_WIDTH+6))
        pipe_p1.io.clk := io.nvdla_core_clk
        pipe_p1.io.di := axi_cmd_pd
        pipe_p1.io.vi := axi_cmd_vld
        axi_cmd_rdy := pipe_p1.io.ro

        opipe_axi_pd := pipe_p1.io.dout
        opipe_axi_vld := pipe_p1.io.vo
        pipe_p1.io.ri := opipe_axi_rdy

        opipe_axi_axid := opipe_axi_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+5, conf.NVDLA_MEM_ADDRESS_WIDTH+2)
        opipe_axi_addr := opipe_axi_pd(conf.NVDLA_MEM_ADDRESS_WIDTH+1, 2)
        opipe_axi_len  := opipe_axi_pd(1, 0)

        io.mcif2noc_axi_ar_arid    := Cat(0.U(4.W), opipe_axi_axid)
        io.mcif2noc_axi_ar_araddr  := opipe_axi_addr
        io.mcif2noc_axi_ar_arlen   := Cat(0.U(2.W), opipe_axi_len)
        io.mcif2noc_axi_ar_arvalid := opipe_axi_vld
    }
}

object NV_NVDLA_MCIF_READ_IG_cvtDriver extends App {
    implicit val conf: xxifConfiguration = new xxifConfiguration
    chisel3.Driver.execute(args, () => new NV_NVDLA_MCIF_READ_IG_cvt())
}

