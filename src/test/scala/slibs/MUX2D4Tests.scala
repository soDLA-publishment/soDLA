// See LICENSE.txt for license details.
package slibs

import chisel3.iotesters.PeekPokeTester

// Problem:
//
// Implement test with PeekPokeTester
//
class MUX2D4Tests(c: MUX2D4) extends PeekPokeTester(c) {
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
