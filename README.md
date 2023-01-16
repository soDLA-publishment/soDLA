soDLA 
================

Sorry for the long waiting, redpanda3 has recently busy working on some other projects.

Thanks for [HuiX](xuehui.hf@gmail.com), [rgb000000](rgb000000black@gmail.com), [CristinaZN], [Leway](https://github.com/colin4124) for contribution, and also, chisel creators for help.


Setup
----------------
The whole environment is accomplished by chipyard. 

Paste soDLA and sodla-wrapper to chipyard/generators.

In RocketConfigs.scala under chipyard/generators/chipyard/scala/config, add

```
class SmallSODLARocketConfig extends Config(
  new np.devices.sodla.WithSODLA("small") ++               // add a small NVDLA
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)
```


Smoke test
----------------

Prepare software program
In chipyard/generators/soDLA/test

    $ make

In chipyard/sims/vcs or chipyard/sims/verilator
    
    $ rm -rf ../../generators/sodla-wrapper/target
    $ make debug -j16 CONFIG=SmallSODLARocketConfig
    $ ./simv-chipyard-SmallSODLARocketConfig-debug ../../generators/soDLA/test/dc_1x1x8_1x1x8x1_int8_0/dc_1x1x8_1x1x8x1_int8_0.riscv 


Verification Plan
---------------- 

Not yet.
