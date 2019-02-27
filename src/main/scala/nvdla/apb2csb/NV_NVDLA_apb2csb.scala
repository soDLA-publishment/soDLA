package nvdla

import chisel3._
import chisel3.experimental._

class NV_NVDLA_apb2csb extends Module {
 
  //csb interface  
  val io = IO(new Bundle {

    //clock
    val pclk = Input(Clock())

    //apb interface
    val psel= Input(Bool())
    val penable = Input(Bool())
    val pwrite  = Input(Bool())
    val paddr = Input(UInt(32.W))
    val pwdata  = Input(UInt(32.W))
    val prdata = Output(UInt(32.W))
    val pready = Output(Bool())

    //csb interface 
    val csb2nvdla_ready = Input(Bool())
    val csb2nvdla_valid = Output(Bool())
    val csb2nvdla_addr  = Output(UInt(16.W))
    val csb2nvdla_wdat  = Output(UInt(32.W))
    val csb2nvdla_write  = Output(Bool())
    val csb2nvdla_nposted = Output(Bool())

    val nvdla2csb_valid = Input(Bool())
    val nvdla2csb_data = Input(UInt(32.W))

  })

  //input  nvdla2csb_wr_complete
  withClock(io.pclk){

  val rd_trans_low = RegInit(Bool())
  val rd_trans_vld = Wire(Bool())
  val wr_trans_vld = Wire(Bool())

  wr_trans_vld := io.psel & io.penable & io.pwrite
  rd_trans_vld := io.psel & io.penable & !io.pwrite 


  when(io.nvdla2csb_valid & rd_trans_low){
    rd_trans_low := false.B
  } 
  .elsewhen(io.csb2nvdla_ready & rd_trans_vld){
    rd_trans_low := true.B
  }    

  io.csb2nvdla_valid := wr_trans_vld | rd_trans_vld & !rd_trans_low
  io.csb2nvdla_addr := io.paddr(17,2)
  io.csb2nvdla_wdat := io.pwdata(31,0)
  io.csb2nvdla_write := io.pwrite
  io.csb2nvdla_nposted := false.B

  io.prdata := io.nvdla2csb_data(31,0)
  io.pready := !(wr_trans_vld&(!io.csb2nvdla_ready)|rd_trans_vld&(!io.nvdla2csb_valid))

  }

}

  