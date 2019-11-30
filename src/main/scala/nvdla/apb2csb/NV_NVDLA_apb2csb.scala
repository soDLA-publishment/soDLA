package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver


class csb2nvdla_if extends Bundle{
    val addr = Output(UInt(16.W))
    val wdat = Output(UInt(32.W))
    val write = Output(Bool())
    val nposted = Output(Bool())
}

class nvdla2csb_if extends Bundle{
    val data = Output(UInt(32.W))
}

class NV_NVDLA_apb2csb extends Module {
 
    //csb interface  
    val io = IO(new Bundle {

      //clock
      val pclk = Input(Clock())

      // Flow control signals from the master
      val psel= Input(Bool())
      val penable = Input(Bool())
      val pwrite  = Input(Bool())
      val paddr = Input(UInt(32.W))
      val pwdata  = Input(UInt(32.W))
      val prdata = Output(UInt(32.W))
      val pready = Output(Bool())

      //csb interface 
      val csb2nvdla = DecoupledIO(new csb2nvdla_if)
      val nvdla2csb = Flipped(ValidIO(new nvdla2csb_if))
    
    })

  //input  nvdla2csb_wr_complete
  withClock(io.pclk){

    val rd_trans_low = RegInit(false.B)

    val wr_trans_vld = io.psel & io.penable & io.pwrite
    val rd_trans_vld = io.psel & io.penable & !io.pwrite 

    when(io.nvdla2csb.valid & rd_trans_low){
      rd_trans_low := false.B
    } 
    .elsewhen(io.csb2nvdla.ready & rd_trans_vld){
      rd_trans_low := true.B
    }    

    io.csb2nvdla.valid := wr_trans_vld | rd_trans_vld & !rd_trans_low
    io.csb2nvdla.bits.addr := io.paddr(17,2)
    io.csb2nvdla.bits.wdat := io.pwdata(31,0)
    io.csb2nvdla.bits.write := io.pwrite
    io.csb2nvdla.bits.nposted := false.B

    io.prdata := io.nvdla2csb.bits.data
    io.pready := !(wr_trans_vld&(!io.csb2nvdla.ready)|rd_trans_vld&(!io.nvdla2csb.valid))

}}

object NV_NVDLA_apb2csbDriver extends App {
  chisel3.Driver.execute(args, () => new NV_NVDLA_apb2csb())
}

  
