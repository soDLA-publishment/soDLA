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

CMAC

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
    
CMAC_core(untested, but you can generate fir):

    $ test:runMain nvdla.cmacLauncher NV_NVDLA_CMAC_core
    


Discussion
----------------

gitter: [link](https://gitter.im/NVDLA_chisel/Lobby)


Coffee
----------------
Thanks for buying me a coffee.


Wechat: ![](wechat_QR.jpg)

Payme: [link](https://paypal.me/redpanda3)


