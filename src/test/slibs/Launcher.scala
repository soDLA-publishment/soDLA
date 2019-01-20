package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.iotesters.{PeekPokeTester, Driver}

object slibsDriver extends App {
  chisel3.Driver.execute(args, () => new AN2D4PO4)
  
}