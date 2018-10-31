package slibs

import chisel3._


class NV_CLK_gate_powers(c: NV_CLK_gate_power) extends PeekPokeTester(c) {
  for (i <- 0 until 10) {
    // FILL THIS IN HERE    
    val I0 = rnd.nextInt(1)
    val I1 = rnd.nextInt(1)
    val S = rnd.nextInt(1)
    poke(c.io.I0, I0)
    poke(c.io.I1, I1)
    poke(c.io.S, S)
    // FILL THIS IN HERE
    step(1)
    expect(c.io.Z, if (S) I0 else I1)
  }
}


