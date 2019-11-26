package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._


class NV_NVDLA_CACC_assembly_buffer(implicit conf: nvdlaConfig) extends Module {

    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())

        //abuf
        val abuf_wr = Flipped(new nvdla_wr_if(conf.CACC_ABUF_AWIDTH, conf.CACC_ABUF_WIDTH))
        val abuf_rd = Flipped(new nvdla_rd_if(conf.CACC_ABUF_AWIDTH, conf.CACC_ABUF_WIDTH))

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
    u_accu_abuf_0.io.ra := io.abuf_rd.addr.bits
    u_accu_abuf_0.io.re := io.abuf_rd.addr.valid
    u_accu_abuf_0.io.we := io.abuf_wr.addr.valid
    u_accu_abuf_0.io.wa := io.abuf_wr.addr.bits
    u_accu_abuf_0.io.di := io.abuf_wr.data
    val abuf_rd_raw_data = u_accu_abuf_0.io.dout

    val abuf_rd_en_d1 = RegNext(io.abuf_rd.addr.valid, false.B)

    io.abuf_rd.data := RegEnable(abuf_rd_raw_data, abuf_rd_en_d1)

}}