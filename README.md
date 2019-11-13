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
| cdma| Done | Not yet | Not yet |
| sdp | Done | Not yet | Not yet |
| cdp | Done | Not yet | Not yet |
| pdp | Done | Not yet | Not yet |
| cora(self-driving package) | Not yet | Not yet | Not yet |

Generate Verilog Modules
----------------
    $ sbt run
    
Most verilog sources are not verified yet, but welcome to test on firesim and send me the issues. 
    

New Update
----------------
1. Package data with valid/ready interface, data with valid interface, data bundle with valid/ready interface into DecoupledIO(UInt), ValidIO(UInt) and Decoupled(Bundle).
2. Package configuration data from ping-pong register into 'field' Bundle.
3. Wrap the basic reg_single in ping-pong register into NV_NVDLA_BASIC_REG_single, csb logic in ping-pong register into NV_NVDLA_CSB_LOGIC, input-skid pipe into NV_NVDLA_IS_pipe, bubble-collapse pipe into NV_NVDLA_BC_pipe, fifo generator(from ness) into NV_NVDLA_fifo, they are under slibs folder.
4. ODIF(open deep learning interface) is defined under nvdla/spec.

TODO
----------------
1. nocif rewrite.
2. PeekPokeTester under chisel, and verification with formal verification tools.
3. FPGA test(Thanks to [Professor Di Zhao's Group](http://sourcedb.ict.cas.cn/cn/jssrck/201803/t20180309_4971421.html)).
4. soDLA doc page.
5. Resume cora package.


cora package
----------------

This is a accelerator of self-driving car with following features:

1. 4-d or 6-d floating point matrix operations.

2. cordic

3. A pipeline of kalman-filter



