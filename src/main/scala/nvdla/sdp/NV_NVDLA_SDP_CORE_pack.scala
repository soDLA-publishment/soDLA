package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver

class NV_NVDLA_SDP_CORE_pack(IW: Int = 512, OW: Int = 128) extends Module {
   val RATIO = IW/OW
   val io = IO(new Bundle {
        //in clock
        val nvdla_core_clk = Input(Clock())

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

    val is_pack_last = Wire(Bool())

    val pack_pvld = RegInit(false.B)
    val pack_prdy = io.out.ready 
    io.out.valid := pack_pvld
    io.inp.ready := (!pack_pvld) | (pack_prdy & is_pack_last)

    when(io.inp.ready){
        pack_pvld := io.inp.valid
    }

    val inp_acc = io.inp.valid & io.inp.ready
    val out_acc = io.out.valid & io.out.ready

    //push data 
    val pack_data = Reg(UInt(IW.W))
    when(inp_acc){
        pack_data := io.inp.bits
    }

    val pack_cnt = RegInit("h0".asUInt(4.W))
    when(out_acc){
        when(is_pack_last){
            pack_cnt := "h0".asUInt(4.W)
        }
        .otherwise{
            pack_cnt := pack_cnt + 1.U
        }
    }

    is_pack_last := (pack_cnt === (RATIO-1).U)

    val pack_seg = VecInit((0 to RATIO-1) map { i => pack_data(i*OW + OW -1, i*OW)})

    io.out.bits := MuxLookup(pack_cnt, "b0".asUInt(OW.W),
                   (0 to RATIO-1) map { i => i.U -> pack_seg(i)})



}}


object NV_NVDLA_SDP_CORE_packDriver extends App {
  chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_CORE_pack())
}
