package slibs

import chisel3.iotesters.PeekPokeTester

// Problem:
//
// Implement test with PeekPokeTester
//
class OR2D1Tests(c: OR2D1) extends PeekPokeTester(c) {
  for (i <- 0 until 10) {
    // FILL THIS IN HERE    
    val A1 = rnd.nextInt(1)
    val A2 = rnd.nextInt(1)
    poke(c.io.A1, A1)
    poke(c.io.A2, A2)
    // FILL THIS IN HERE
    step(1)
    expect(c.io.Z, A1||A2)
  }
}

