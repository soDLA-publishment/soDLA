package nvdla

import chisel3._
import chisel3.util._

class NV_NVDLA_SDP_RDMA_pack(IW: Int = 512, OW: Int = 256, CW: Int = 1)(implicit val conf: nvdlaConfig) extends Module {
   val RATIO = IW/OW
   val io = IO(new Bundle {
        //in clock
        val nvdla_core_clk = Input(Clock())

        val cfg_dp_8 = Input(Bool())
        
        val inp = Flipped(DecoupledIO(UInt((IW+CW).W)))
        val out = DecoupledIO(UInt((OW+CW).W))

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

    val ctrl_end = Wire(UInt(CW.W))
    val mux_data = Wire(UInt(OW.W))
    io.out.bits := Cat(ctrl_end, mux_data)

    val pack_prdy = io.out.ready

    val pack_pvld = RegInit(false.B)
    pack_pvld := io.inp.valid
    io.out.valid := pack_pvld

    val is_pack_last = Wire(Bool())
    io.inp.ready := (!pack_pvld) | (pack_prdy & is_pack_last)

    val inp_acc = io.inp.valid & io.inp.ready
    val out_acc = io.out.valid & io.out.ready

    val ctrl_done = Reg(UInt(CW.W))
    when(inp_acc){
        ctrl_done := io.inp.bits(IW+CW-1,IW)
    }.elsewhen(out_acc & is_pack_last){
        ctrl_done := 0.U
    }

    ctrl_end := ctrl_done & Fill(CW, is_pack_last)

    //push data 
    val pack_data = Reg(UInt(IW.W))
    when(inp_acc){
        pack_data := io.inp.bits(IW-1,0)
    }
    
    val pack_data_ext = Wire(UInt((OW*16).W))
    pack_data_ext := pack_data
    
    val pack_cnt = RegInit(0.U(4.W))
    when(out_acc){
        when(is_pack_last){
            pack_cnt := 0.U
        }.otherwise{
            pack_cnt := pack_cnt + 1.U
        }
    }
    
    is_pack_last := Mux(!io.cfg_dp_8, (pack_cnt===(RATIO/2-1).U), (pack_cnt===(RATIO-1).U))

    val pack_seg = VecInit((0 to RATIO-1) 
                    map {i => pack_data_ext((OW*i + OW - 1), OW*i)})

    mux_data := MuxLookup(pack_cnt, "b0".asUInt(OW.W),
                        (0 to RATIO-1) map {i => i.U -> pack_seg(i)})

    }
}


object NV_NVDLA_SDP_RDMA_packDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_RDMA_pack())
}
