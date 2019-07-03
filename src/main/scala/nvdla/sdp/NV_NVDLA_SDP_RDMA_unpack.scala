package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver

class NV_NVDLA_SDP_RDMA_unpack(implicit val conf: sdpConfiguration) extends Module {
   val RATIO = 4*conf.AM_DW/conf.NVDLA_MEMIF_WIDTH
   val io = IO(new Bundle {
        //in clock
        val nvdla_core_clk = Input(Clock())

        // val cfg_dp_8 = Input(Bool())

        val inp_end = Input(Bool())
        
        val inp_pvld = Input(Bool())
        val inp_prdy = Output(Bool())
        val inp_data = Input(UInt(conf.NVDLA_DMA_RD_RSP.W))

        val out_pvld = Output(Bool())
        val out_prdy = Input(Bool())
        val out_data = Output(UInt((4*conf.AM_DW+4).W))

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

    val pack_pvld = RegInit(false.B)

    val pack_prdy = io.out_prdy
    io.out_pvld := pack_pvld
    io.inp_prdy := (!pack_pvld) | pack_prdy

    val is_pack_last = Wire(Bool())
    when(io.inp_prdy){
        pack_pvld := io.inp_pvld & is_pack_last
    }
    val inp_acc = io.inp_pvld & io.inp_prdy

    val data_mask = Cat(Fill(4-conf.NVDLA_DMA_MASK_BIT, false.B), io.inp_data)
    val data_size = data_mask(0) + data_mask(1) + data_mask(2) + data_mask(3)
    val pack_cnt = RegInit("b0".asUInt(2.W))
    val pack_cnt_nxt = pack_cnt + data_size
    when(inp_acc){
        when(is_pack_last){
            pack_cnt := 0.U
        }
        .otherwise{
            pack_cnt := pack_cnt_nxt
        }
    }
    is_pack_last := (pack_cnt_nxt === 4.U) | io.inp_end;

    val pack_mask = RegInit("b0".asUInt(4.W))
    when(inp_acc & is_pack_last){
        pack_mask := Mux(pack_cnt_nxt === 4.U, "hf".asUInt(4.W),
                     Mux(pack_cnt_nxt === 3.U, "h7".asUInt(4.W),
                     Mux(pack_cnt_nxt === 2.U, "h3".asUInt(4.W),
                     pack_cnt_nxt)))
    }

    val pack_seq = Reg(Vec(RATIO, UInt(conf.AM_DW.W)))
        
    when(inp_acc){
        for(i <- 0 to RATIO-1){
            pack_seq(i) := io.inp_data(4/RATIO*conf.AM_DW-1, 0)
        }
    }

    val pack_total = pack_seq.asUInt
    io.out_data := Cat(pack_mask, pack_total)

}}


object NV_NVDLA_SDP_RDMA_unpackDriver extends App {
  implicit val conf: sdpConfiguration = new sdpConfiguration
  chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_RDMA_unpack())
}