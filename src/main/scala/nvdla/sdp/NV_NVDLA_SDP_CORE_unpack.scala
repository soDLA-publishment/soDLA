package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver

class NV_NVDLA_SDP_CORE_unpack(IW: Int = 128, OW: Int = 512) extends Module {
   val RATIO = OW/IW
   val io = IO(new Bundle {
        //in clock
        val nvdla_core_clk = Input(Clock())

        val inp_data = Flipped(DecoupledIO(UInt(IW.W)))
        val out_data = DecoupledIO(UInt(OW.W))

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

    val pack_prdy = io.out_data.ready
    io.out_data.valid := pack_pvld
    io.inp_data.bits := (!pack_pvld) | pack_prdy

    val is_pack_last = Wire(Bool())
    when(io.inp_data.ready){
        pack_pvld := io.inp_data.valid & is_pack_last
    }
    val inp_acc = io.inp_data.valid & io.inp_data.ready

    val pack_cnt = RegInit("b0".asUInt(4.W))
    val pack_seg = Reg(Vec(RATIO, UInt(IW.W)))
    when(inp_acc){
        when(is_pack_last){
            pack_cnt := 0.U
        }
        .otherwise{
            pack_cnt := pack_cnt + 1.U
        }

        for(i <- 0 to RATIO-1){
            when(pack_cnt === i.U){
                pack_seg(i) := io.inp_data.bits
            }
        }
    }

    is_pack_last := pack_cnt === (RATIO-1).U
    io.out_data.bits := pack_seg.asUInt

}}


object NV_NVDLA_SDP_CORE_unpackDriver extends App {
  chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_CORE_unpack())
}
