package nvdla
import chisel3._
import chisel3.experimental._
import chisel3.util._

class xxif_read_ig_cq_wr_out_if extends Bundle{
    val pd = Output(UInt(7.W))
    val thread_id = Output(UInt(4.W))
}

@chiselName
class NV_NVDLA_XXIF_READ_IG_cvt(cq_enabled: Boolean)(implicit conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle{
        //general clock
        val nvdla_core_clk = Input(Clock())

        val reg2dp_rd_os_cnt = Input(UInt(8.W))

        //if cq enabled
        val cq_wr = if(cq_enabled) Some(DecoupledIO(new xxif_read_ig_cq_wr_out_if)) else None

        //eg2ig
        val eg2ig_axi_vld = Input(Bool())
        //spt2cvt
        val spt2cvt_req_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_DMA_RD_IG_PW.W)))
        //mcif2noc
        val mcif2noc_axi_ar = DecoupledIO(new nocif_axi_rd_address_if)
    })

withClock(io.nvdla_core_clk){

    val cmd_vld = io.spt2cvt_req_pd.valid
    val cmd_rdy = Wire(Bool())
    io.spt2cvt_req_pd.ready := cmd_rdy

    val cmd_axid = io.spt2cvt_req_pd.bits(3,0)
    val cmd_addr = io.spt2cvt_req_pd.bits(conf.NVDLA_MEM_ADDRESS_WIDTH+3,4)
    val cmd_size = io.spt2cvt_req_pd.bits(conf.NVDLA_MEM_ADDRESS_WIDTH+6, conf.NVDLA_MEM_ADDRESS_WIDTH+4);
    val cmd_swizzle = io.spt2cvt_req_pd.bits(conf.NVDLA_MEM_ADDRESS_WIDTH+7)
    val cmd_odd  =  io.spt2cvt_req_pd.bits(conf.NVDLA_MEM_ADDRESS_WIDTH+8)
    val cmd_ltran = io.spt2cvt_req_pd.bits(conf.NVDLA_MEM_ADDRESS_WIDTH+9)
    val cmd_ftran = io.spt2cvt_req_pd.bits(conf.NVDLA_MEM_ADDRESS_WIDTH+10)

    val axi_cmd_vld = Wire(Bool())
    val axi_cmd_rdy = Wire(Bool())
    val axi_len = Wire(UInt(2.W))
    val os_cnt_full = Wire(Bool())

    if(cq_enabled){
        val stt_offset = cmd_addr(7, 5)
        val end_offset = stt_offset + cmd_size
        val stt_addr_is_32_align = stt_offset(0) === true.B
        val end_addr_is_32_align = end_offset(0) === false.B
        val inc = cmd_ftran & cmd_ltran & (cmd_size(0) === true.B) & cmd_swizzle
        axi_len := cmd_size(2, 1) + inc
        io.cq_wr.get.valid := cmd_vld & axi_cmd_rdy & ~os_cnt_full
        io.cq_wr.get.bits.pd := Cat(cmd_ltran & end_addr_is_32_align, cmd_ftran & stt_addr_is_32_align, cmd_ltran,
                           cmd_odd, cmd_swizzle, axi_len)
        io.cq_wr.get.bits.thread_id := cmd_axid
        axi_cmd_vld := cmd_vld & io.cq_wr.get.valid & ~os_cnt_full
        cmd_rdy := axi_cmd_rdy & io.cq_wr.get.valid & ~os_cnt_full
    }
    else{
        axi_cmd_vld := cmd_vld & ~os_cnt_full
        cmd_rdy := axi_cmd_rdy & ~os_cnt_full      
        axi_len := cmd_size 
    }
    
    val axi_axid = cmd_axid 
    val axi_addr = Cat(cmd_addr(conf.NVDLA_MEM_ADDRESS_WIDTH-1, conf.NVDLA_MEMORY_ATOMIC_LOG2), "b0".asUInt(conf.NVDLA_MEMORY_ATOMIC_LOG2.W))

    val os_cnt = Wire(UInt(9.W))
    val os_inp_add_nxt = Mux(cmd_vld, axi_len +& 1.U, 0.U)
    val eg2ig_axi_vld_d = RegInit(false.B)
    eg2ig_axi_vld_d := io.eg2ig_axi_vld
    val os_inp_sub_nxt = Mux(eg2ig_axi_vld_d, true.B, false.B)
    val os_inp_nxt = os_cnt +& os_inp_add_nxt -& os_inp_sub_nxt

    // 256 outstanding trans
    val os_cnt_add_en = axi_cmd_vld & axi_cmd_rdy
    val os_cnt_sub_en = eg2ig_axi_vld_d
    val os_cnt_cen = os_cnt_add_en | os_cnt_sub_en
    val os_cnt_add = Mux(os_cnt_add_en, axi_len +& 1.U, 0.U)
    val os_cnt_sub = Mux(os_cnt_sub_en, true.B, false.B)
    val cfg_rd_os_cnt = io.reg2dp_rd_os_cnt
    val rd_os_cnt_ext = Cat(false.B, cfg_rd_os_cnt)
    os_cnt_full := os_inp_nxt > (rd_os_cnt_ext +& 1.U)

    val perf_os = Module(new NV_COUNTER_STAGE_os)
    perf_os.io.clk := io.nvdla_core_clk
    perf_os.io.os_cnt_add := os_cnt_add
    perf_os.io.os_cnt_sub := os_cnt_sub
    perf_os.io.os_cnt_cen := os_cnt_cen
    os_cnt := perf_os.io.os_cnt_cur

    val axi_cmd_pd = Cat(axi_axid,axi_addr,axi_len)

    val pipe_p1 = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_MEM_ADDRESS_WIDTH+6))
    pipe_p1.io.clk := io.nvdla_core_clk
    pipe_p1.io.vi := axi_cmd_vld
    axi_cmd_rdy := pipe_p1.io.ro
    pipe_p1.io.di := axi_cmd_pd

    io.mcif2noc_axi_ar.valid := pipe_p1.io.vo
    pipe_p1.io.ri := io.mcif2noc_axi_ar.ready

    val opipe_axi_axid = pipe_p1.io.dout(conf.NVDLA_MEM_ADDRESS_WIDTH+5, conf.NVDLA_MEM_ADDRESS_WIDTH+2)
    val opipe_axi_addr = pipe_p1.io.dout(conf.NVDLA_MEM_ADDRESS_WIDTH+1, 2)
    val opipe_axi_len  = pipe_p1.io.dout(1, 0)

    io.mcif2noc_axi_ar.bits.id    := Cat(0.U(4.W), opipe_axi_axid)
    io.mcif2noc_axi_ar.bits.addr  := Cat(0.U(32.W), opipe_axi_addr)
    io.mcif2noc_axi_ar.bits.len   := Cat(0.U(2.W), opipe_axi_len)

}}

object NV_NVDLA_XXIF_READ_IG_cvtDriver extends App {
    implicit val conf: nvdlaConfig = new nvdlaConfig
    chisel3.Driver.execute(args, () => new NV_NVDLA_XXIF_READ_IG_cvt(true))
}

