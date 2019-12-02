package nvdla

import chisel3._
import chisel3.util._

class NV_NVDLA_SDP_WDMA_unpack extends Module {
   val IW = 256
   val IHW = IW/2
   val OW = 256
   val RATIO = OW/IW
   val io = IO(new Bundle {
        //in clock
        val nvdla_core_clk = Input(Clock())

        val cfg_dp_8 = Input(Bool())

        val inp = Flipped(DecoupledIO(UInt(IW.W)))
        val out = DecoupledIO(UInt(OW.W))

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

    val pack_prdy = io.out.ready
    io.out.valid := pack_pvld
    io.inp.ready := (!pack_pvld) | pack_prdy

    val is_pack_last = Wire(Bool())
    when(io.inp.ready){
        pack_pvld := io.inp.valid & is_pack_last
    }
    val inp_acc = io.inp.valid & io.inp.ready

    val pack_cnt = RegInit("b0".asUInt(4.W))
    when(inp_acc){
        when(is_pack_last){
            pack_cnt := 0.U
        }
        .otherwise{
            pack_cnt := pack_cnt + 1.U
        }
    }
    is_pack_last := Mux(io.cfg_dp_8, pack_cnt === (2*RATIO-1).U, pack_cnt === (RATIO-1).U)


    val pack_seg = Reg(Vec(2*RATIO, UInt(IW.W)))
    when(inp_acc){
        for(i <- 0 to 2*RATIO-1){
            when(pack_cnt === i.U){
                pack_seg(i) := io.inp.bits
            }
        }
    }

    val pack_total_8 = VecInit((0 to 2*RATIO-1) map
                      {i => pack_seg(i)(IHW -1, 0)}).asUInt
    val pack_total_16 = VecInit((0 to RATIO-1) map
                      {i => pack_seg(i)}).asUInt
    io.out.bits := Mux(io.cfg_dp_8, pack_total_8, pack_total_16)

}}

