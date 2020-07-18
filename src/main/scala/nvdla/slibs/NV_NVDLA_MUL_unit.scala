package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_MUL_unit(pINA_BW: Int, pINB_BW: Int) extends Module {

    class mul_inp_if extends Bundle{
        val ina = Output(UInt(pINA_BW.W))
        val inb = Output(UInt(pINB_BW.W))
    }

    val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        val mul_in_pd = Flipped(DecoupledIO(new mul_inp_if))
        val mul_unit_pd = DecoupledIO(UInt((pINA_BW+pINB_BW).W))
    })

withClock(io.nvdla_core_clk){

    io.mul_in_pd.ready := ~io.mul_unit_pd.valid | io.mul_unit_pd.ready
    val mul_unit_pd_out = RegInit(0.U((pINA_BW+pINB_BW).W))
    val mul_unit_vld_out = RegInit(false.B)

    when(io.mul_in_pd.valid & io.mul_in_pd.ready){
        mul_unit_pd_out := (io.mul_in_pd.bits.ina.asSInt * io.mul_in_pd.bits.inb.asSInt).asUInt
    }
    
    when(io.mul_in_pd.valid){
        mul_unit_vld_out := true.B
    }.elsewhen(io.mul_unit_pd.ready){
        mul_unit_vld_out := false.B
    }

    io.mul_unit_pd.bits := mul_unit_pd_out
    io.mul_unit_pd.valid := mul_unit_vld_out

}}


object NV_NVDLA_MUL_unitDriver extends App {
    chisel3.Driver.execute(args, () => new NV_NVDLA_MUL_unit(9, 16))
}
