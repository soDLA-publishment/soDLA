soDLA (Building)
================

This is a suite of packages for working with nvdla in chisel
.
These are the tutorials for [chisel3](https://chisel.eecs.berkeley.edu/index.html#getstarted) and [nvdla](http://nvdla.org/hw/v1/hwarch.html)


My Progress
----------------


| Unit | Chisel | Validation& Test| Backend |
| ------ | ------ | ------ |------ |
| cmac |  Done | Some Simple Peekpoke Test| Not yet |
| cbuf | Done | Not yet | Not yet |
| csc | Done | Not yet | Not yet |
| cacc | Done | Not yet | Not yet |
| cfgrom | Done | Not yet | Not yet |
| apb2csb | Done | Not yet | Not yet |
| glb | Done | Not yet | Not yet |
| csb_master | Done | Not yet | Not yet |
| cdma| In Progress | Not yet | Not yet |
| cora(self-driving package) | In Progress | Not yet | Not yet |

Generate Verilog Modules
----------------
    $ sbt run
    
Most verilog sources are not verified yet, but welcome to test on firesim and send me the issues. 

Executing Some Basic Test
----------------

The following modules are suitable for peekpoke tester.

CMAC_CORE_mac 

    $ test:runMain nvdla.cmacLauncher NV_NVDLA_CMAC_CORE_mac
    
CMAC_CORE_rt_in:

    $ test:runMain nvdla.cmacLauncher NV_NVDLA_CMAC_CORE_rt_in
    
CMAC_CORE_rt_out:

    $ test:runMain nvdla.cmacLauncher NV_NVDLA_CMAC_CORE_rt_out
    
CMAC_CORE_active:

    $ test:runMain nvdla.cmacLauncher NV_NVDLA_CMAC_CORE_active



    
About the buiding cora package
----------------

This is a accelerator of self-driving car with following features:

1. 4-d or 6-d floating point matrix operations.

2. cordic

3. A pipeline of kalman-filter



