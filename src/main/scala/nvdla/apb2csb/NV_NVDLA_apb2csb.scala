package nvdla

import chisel3._

class NV_NVDLA_apb2csb extends Module {

  //csb interface  
  val csb2_io = IO(new Bundle {
    val csb2nvdla_ready = Input(Bool())
    val csb2nvdla_valid = Output(Bool())
    val csb2nvdla_addr  = Output(UInt(16.W))
    val csb2nvdla_wdat  = Output(UInt(32.W))
    val csb2nvdla_write  = Output(Bool())
    val csb2nvdla_nposted = Output(Bool())
  })

  //apb interface  
  val apb_io = IO(new Bundle {
    val psel= Input(Bool())
    val penable = Input(Bool())
    val pwrite  = Input(Bool())
    val paddr = Input(UInt(32.W))
    val pwdata  = Input(UInt(32.W))
    val prdata = Output(UInt(32.W))
    val pready = Output(Bool())
  })

  
