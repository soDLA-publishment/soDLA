package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._


class NV_NVDLA_CACC_assembly_buffer(implicit conf: nvdlaConfig) extends Module {

    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())

        //abuf
        val abuf_wr_en = Input(Bool())
        val abuf_wr_addr = Input(UInt(conf.CACC_ABUF_AWIDTH.W))
        val abuf_wr_data = Input(UInt(conf.CACC_ABUF_WIDTH.W))

        val abuf_rd_en = Input(Bool())
        val abuf_rd_addr = Input(UInt(conf.CACC_ABUF_AWIDTH.W))                
        val abuf_rd_data = Output(UInt(conf.CACC_ABUF_WIDTH.W))

        val pwrbus_ram_pd = Input(UInt(32.W))
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

val u_accu_abuf_0 = Module(new nv_ram_rws(conf.CACC_ABUF_DEPTH, conf.CACC_ABUF_WIDTH))

u_accu_abuf_0.io.clk := io.nvdla_core_clk
u_accu_abuf_0.io.ra := io.abuf_rd_addr
u_accu_abuf_0.io.re := io.abuf_rd_en
u_accu_abuf_0.io.we := io.abuf_wr_en
u_accu_abuf_0.io.wa := io.abuf_wr_addr
u_accu_abuf_0.io.di := io.abuf_wr_data
val abuf_rd_raw_data = u_accu_abuf_0.io.dout

val abuf_rd_en_d1 = RegNext(io.abuf_rd_en, false.B)

io.abuf_rd_data := RegEnable(abuf_rd_raw_data, abuf_rd_en_d1)

}}

