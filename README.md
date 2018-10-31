soDLA (Building)
================

This is a suite of packages for working with nvdla in chisel
.
These are the tutorials for [chisel3](https://chisel.eecs.berkeley.edu/index.html#getstarted) and [nvdla](http://nvdla.org/hw/v1/hwarch.html)



Getting the Repo
----------------

    $ git clone https://github.com/redpanda3/soDLA.git
    $ cd soDLA


Executing Chisel
----------------

####Testing Your System
First make sure that you have sbt (the scala build tool) installed. See details
in [sbt](http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html).

    $ sbt run

This will generate and test a simple block (`Hello`) that always outputs the
number 42 (aka 0x2a). You should see `[success]` on the last line of output (from sbt) and
`PASSED` on the line before indicating the block passed the testcase. If you
are doing this for the first time, sbt will automatically download the
appropriate versions of Chisel3, the Chisel Testers harness
and Scala and cache them (usually in `~/.ivy2`).

