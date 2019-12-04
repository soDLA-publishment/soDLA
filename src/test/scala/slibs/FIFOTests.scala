package slibs

import nvdla.NV_NVDLA_fifo
import chisel3._
import chisel3.iotesters.{PeekPokeTester, Driver, ChiselFlatSpec}

class NV_NVDLA_fifoTest(c: NV_NVDLA_fifo, ram_type: Int) extends PeekPokeTester(c) {
  def wr_data(wdata: Int): Unit = {
    poke(c.io.wr_pvld, 1)
    poke(c.io.wr_pd, wdata)
    step(1)
    poke(c.io.wr_pvld, 0)
  }

  def rd_data(rdata: Int, delay: Int = 1): Unit = {
    poke(c.io.rd_prdy, 1)
    step(delay)
    if (peek(c.io.rd_pvld) == 1) {
      expect(c.io.rd_pd, rdata)
    } else {
      println("no data out")
    }
    poke(c.io.rd_prdy, 0)
  }

  ram_type match {
    case 0 => {
      //===========================================
      // write 8 number and read 8 number at the same time
      //===========================================
      for (i <- 0 until 8) {
        wr_data(i + 0xf0)
        rd_data(i + 0xf0)
      }
      //===========================================
      // write 8 number then read 8
      //===========================================
      for (i <- 0 until 8) {
        wr_data(i + 0xf0)
      }
      for (i <- 0 until 8) {
        rd_data(i + 0xf0)
      }
    }
    case 1 => {
      //===========================================
      // write 8 number and read 8 number at the same time
      //===========================================
      for (i <- 0 until 8) {
        wr_data(i + 0xf0)
        rd_data(i + 0xf0)
      }
      //===========================================
      // write 8 number then read 8
      //===========================================
      for (i <- 0 until 8) {
        wr_data(i + 0xf0)
      }
      for (i <- 0 until 8) {
        rd_data(i + 0xf0, 2)
      }
    }
    case 2 => {
      //===========================================
      // write 8 number and read 8 number at the same time
      //===========================================
      for (i <- 0 until 8) {
        wr_data(i + 0xf0)
        rd_data(i + 0xf0)
      }
      //===========================================
      // write 8 number then read 8
      //===========================================
      for (i <- 0 until 8) {
        wr_data(i + 0xf0)
      }
      for (i <- 0 until 8) {
        rd_data(i + 0xf0)
      }
    }
    case _ => {
      println("no match")
    }
  }
}

class NV_NVDLA_fifoTester extends ChiselFlatSpec {
  for (i <- 0 until 3) {
    val ram_type = i
    val dirname = "make_fifo_type" + ram_type.toString + "_vcd"
    s"running with --generate-vcd-output on and type ${ram_type}" should "create a vcd file from your test" in {
      iotesters.Driver.execute(
        Array(
          "--generate-vcd-output", "on",
          "--target-dir", s"test_run_dir/make_fifo_type_${ram_type}_vcd",
          "--top-name", s"make_fifo_type_${ram_type}_vcd",
          "--backend-name", "verilator",
          // "-tmvf", "-full64 -cpp g++-4.8 -cc gcc-4.8 -LDFLAGS -Wl,-no-as-needed +memcbk  +vcs+dumparrays -debug_all"
        ),
        () => new NV_NVDLA_fifo(8, 8, ram_type)
      ) {
        c => new NV_NVDLA_fifoTest(c, ram_type)
      } should be(true)
    }
  }
}
