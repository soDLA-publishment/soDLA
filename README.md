soDLA (Building)
================

This is a suite of packages for working with nvdla in chisel
.
These are the tutorials for [chisel3](https://chisel.eecs.berkeley.edu/index.html#getstarted) and [nvdla](http://nvdla.org/hw/v1/hwarch.html)

Getting Started
----------------

    $ git clone https://github.com/redpanda3/soDLA.git
    $ cd soDLA
    $ sbt
    

Executing Test
----------------

The following modules are suitable for peekpoke tester.

CMAC

CMAC_core component test in Peekpoke Tester:

CMAC_CORE_mac for unsigned data, see nvdla/common/configurations for detail configurations:

    $ test:runMain nvdla.cmacLauncher NV_NVDLA_CMAC_CORE_mac
    
CMAC_CORE_mac for signed data: 
 
    $ test:runMain nvdla.cmacSINTLauncher NV_NVDLA_CMAC_CORE_macSINT
    
CMAC_CORE_rt_in:

    $ test:runMain nvdla.cmacLauncher NV_NVDLA_CMAC_CORE_rt_in
    
CMAC_CORE_rt_out:

    $ test:runMain nvdla.cmacLauncher NV_NVDLA_CMAC_CORE_rt_out
    
CMAC_CORE_active:

    $ test:runMain nvdla.cmacLauncher NV_NVDLA_CMAC_CORE_active

Some modules are suitable for VCS. I will post a scipt for generating vsrc later.


    
About the buiding cora package
----------------

This is a accelerator of self-driving car with following features:

1. 4-d or 6-d floating point matrix operations.

2. cordic

3. A pipeline of kalman-filter



