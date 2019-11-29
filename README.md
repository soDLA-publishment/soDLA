soDLA (beta)
================

Ownership has been trasnfered to [soDLA-publishment](https://github.com/soDLA-publishment/soDLA)

This is a suite of packages for working with nvdla in chisel.

These are the tutorials for [chisel3](https://chisel.eecs.berkeley.edu/index.html#getstarted) and [nvdla](http://nvdla.org/hw/v1/hwarch.html). 



Generate Verilog Modules
----------------
    $ sbt run
    
Most verilog sources are not verified yet, but welcome to test on firesim and send me the issues. Before you generate nv_large as a whole, you will need to increase heap space in java first, or 

    $ env JAVA_OPTS="-Xmx4g" sbt run
    

New Update
----------------
1. Package data with valid/ready interface, data with valid interface, data bundle with valid/ready interface into DecoupledIO(UInt), ValidIO(UInt) and Decoupled(Bundle).

2. Package configuration data from ping-pong register into 'field' Bundle.

3. Wrap the basic reg_single in ping-pong register into NV_NVDLA_BASIC_REG_single, csb logic in ping-pong register into NV_NVDLA_CSB_LOGIC, input-skid pipe into NV_NVDLA_IS_pipe, bubble-collapse pipe into NV_NVDLA_BC_pipe, fifo generator(from ness) into NV_NVDLA_fifo, they are under slibs folder.

4. ODIF(open deep learning interface) is defined under nvdla/spec.

//updt 11/27/2019

1. nv_large suport 

//updt 11/28/2019

1. nv_small without DRAM support




TODO
----------------
1. PeekPokeTester under chisel, and verification with formal verification tools.
2. FPGA test(Thanks to [Professor Di Zhao's Group](http://sourcedb.ict.cas.cn/cn/jssrck/201803/t20180309_4971421.html)).
3. soDLA doc page.
4. Resume cora package.
5. Chisel3.2 full support(soDLA_beta will face "OutofMemory" issue in the latest Chisel3.2). 


cora package
----------------

This is a accelerator of self-driving car with following features:

1. 4-d or 6-d floating point matrix operations.

2. cordic

3. A pipeline of kalman-filter



