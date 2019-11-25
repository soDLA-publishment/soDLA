package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._


class NV_NVDLA_MCIF_WRITE_eg (implicit conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())      

        val mcif2client_wr_rsp_complete = Output(Vec(conf.WDMA_NUM, Bool()))
        //cq_rd
        val cq_rd_pd = Flipped(Vec(conf.WDMA_MAX_NUM, DecoupledIO(UInt(3.W))))
        //noc2mcif
        val noc2mcif_axi_b_id = Flipped(Decoupled(UInt(8.W)))
        //eq2ig
        val eq2ig_axi_len = ValidIO(UInt(2.W))
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

    val iflop_axi_rdy = Wire(Bool())
    val u_pipe = Module(new NV_NVDLA_IS_pipe(3))
    u_pipe.io.clk := io.nvdla_core_clk

    u_pipe.io.vi := io.noc2mcif_axi_b_id.valid
    io.noc2mcif_axi_b_id.ready := u_pipe.io.ro
    u_pipe.io.di := io.noc2mcif_axi_b_id.bits

    val iflop_axi_vld = u_pipe.io.vo
    u_pipe.io.ri := iflop_axi_rdy
    val iflop_axi_axid = u_pipe.io.dout

    val iflop_axi_rdy_vec = VecInit((0 to conf.WDMA_MAX_NUM-1) map{i => io.cq_rd_pd(i).valid & (u_pipe.io.dout === i.U)})
    val iflop_axi_vld_vec = VecInit((0 to conf.WDMA_MAX_NUM-1) map{i => iflop_axi_vld & (u_pipe.io.dout === i.U)})
    val cq_rd_len_vec = VecInit((0 to conf.WDMA_MAX_NUM-1) map{i => io.cq_rd_pd(i).bits(2, 1)})
    val cq_rd_prdy_vec = iflop_axi_vld_vec

    iflop_axi_rdy := iflop_axi_rdy_vec.asUInt.orR

    val eg2ig_axi_vld = iflop_axi_vld & iflop_axi_rdy 

    val eg2ig_axi_len = WireInit("b0".asUInt(2.W))
    for(i <- 0 to conf.WDMA_MAX_NUM-1){
        when(iflop_axi_vld(i)){
            eg2ig_axi_len := cq_rd_len_vec(i)
        }   
    }


    val client_cq_rd_pvld = Wire(Vec(conf.WDMA_NUM, Bool()))
    val client_cq_rd_ack = Wire(Vec(conf.WDMA_NUM, Bool()))
    val client_axi_vld = Wire(Vec(conf.WDMA_NUM, Bool()))
    for(i <- 0 to conf.WDMA_NUM-1){
        client_cq_rd_pvld(i) := MuxLookup(conf.arr_tieoff_axid(i).U, false.B,
                                  (0 to conf.WDMA_MAX_NUM-1) map{j => j.U -> io.cq_rd_pd(j).valid})
        client_cq_rd_ack(i) := MuxLookup(conf.arr_tieoff_axid(i).U, false.B,
                            (0 to conf.WDMA_MAX_NUM-1) map{j => j.U -> io.cq_rd_pd(j).bits(0)})
        client_axi_vld(i) := iflop_axi_vld & (iflop_axi_axid === conf.arr_tieoff_axid(i).U)
        io.mcif2client_wr_rsp_complete := RegNext(client_cq_rd_pvld(i)&client_cq_rd_ack(i)&client_axi_vld(i), false.B)
    }

}}