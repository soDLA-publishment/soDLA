package nvdla

import chisel3._
import chisel3.util._

class sync2data_out_if(DATA1_WIDTH:Int, DATA2_WIDTH:Int) extends Bundle{
    val data1 = Output(UInt(DATA1_WIDTH.W))
    val data2 = Output(UInt(DATA2_WIDTH.W))
    override def cloneType: this.type =
    new sync2data_out_if(DATA1_WIDTH:Int, DATA2_WIDTH:Int).asInstanceOf[this.type]
}

class NV_NVDLA_HLS_sync2data(DATA1_WIDTH:Int, DATA2_WIDTH:Int) extends Module {
   val io = IO(new Bundle {
        val chn1_en = Input(Bool())
        val chn1_in = Flipped(DecoupledIO(UInt(DATA1_WIDTH.W)))
        
        val chn2_en = Input(Bool())
        val chn2_in = Flipped(DecoupledIO(UInt(DATA2_WIDTH.W)))
        
        val chn_out = DecoupledIO(new sync2data_out_if(DATA1_WIDTH, DATA2_WIDTH))
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

    io.chn_out.valid := Mux(io.chn1_en & io.chn2_en, io.chn1_in.valid & io.chn2_in.valid, 
                        Mux(io.chn2_en, io.chn2_in.valid,
                        Mux(io.chn1_en, io.chn1_in.valid, 
                        false.B)))
    io.chn1_in.ready := Mux(io.chn1_en & io.chn2_en, io.chn_out.ready & io.chn2_in.valid,
                        Mux(io.chn2_en, true.B, 
                        io.chn_out.ready))
    io.chn2_in.ready := Mux(io.chn1_en & io.chn2_en, io.chn_out.ready & io.chn1_in.valid,
                        Mux(io.chn2_en, io.chn_out.ready, 
                        true.B))

    io.chn_out.bits.data1 := Mux(io.chn1_en, io.chn1_in.bits, 0.asUInt)
    io.chn_out.bits.data2 := Mux(io.chn2_en, io.chn2_in.bits, 0.asUInt)

}
