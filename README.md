soDLA 
================

Sorry for the long waiting, redpanda3 has recently busy working on some other projects.

Thanks for [HuiX](xuehui.hf@gmail.com), [rgb000000](rgb000000black@gmail.com), [CristinaZN], [Leway](https://github.com/colin4124) for contribution, and also, chisel creators for help.


Smoke Test Setup
----------------
The whole environment is accomplished by chipyard. 
soDLA integration of latest chipyard(1.10.0) is now successful. 

Paste soDLA and sodla-wrapper to chipyard/generators.

In chipyard/build.sbt, add sodla-wrapper

```
lazy val sodla = (project in file("generators/sodla-wrapper"))
  .dependsOn(rocketchip)
  .settings(libraryDependencies ++= rocketLibDeps.value)
  .settings(commonSettings)
```

In chipyard/build.sbt, within the definition of chipyard,  add sodla

```
lazy val chipyard = (project in file("generators/chipyard"))
  .sourceDependency(testchipip, testchipipLib)
  .dependsOn(rocketchip, boom, hwacha, sifive_blocks, sifive_cache, utilities, iocell,
    sha3, // On separate line to allow for cleaner tutorial-setup patches
    dsptools, `rocket-dsptools`,
    gemmini, icenet, tracegen, cva6, sodla,nvdla, sodor)
  .settings(libraryDependencies ++= rocketLibDeps.value)
  .settings(commonSettings)
```

In DigitalTop.scala under chipyard/generators/chipyard/scala, insert

```
with np.blocks.dla.CanHavePeripherySODLA
```
within the option of DigitalTop.


In MMIOAcceleratorConfigs.scala under chipyard/generators/chipyard/scala/config, add a configuration of soDLA integrated soc

```
class SmallSODLARocketConfig extends Config(
  new np.devices.sodla.WithSODLA("small") ++               // add a small NVDLA
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)
```


Prepare software program

In chipyard/generators/soDLA/test, we prepare some testing programs. To compile it under riscv

    $ make
    
In chipyard/sims/vcs or chipyard/sims/verilator
    
    $ rm -rf ../../generators/sodla-wrapper/target
    $ make debug -j16 CONFIG=SmallSODLARocketConfig
    $ ./simv-chipyard-SmallSODLARocketConfig-debug ../../generators/soDLA/test/dc_1x1x8_1x1x8x1_int8_0/dc_1x1x8_1x1x8x1_int8_0.riscv 


Verification Plan
---------------- 

Not yet.
