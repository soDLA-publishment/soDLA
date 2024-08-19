## SODLA Verification Environment

The whole verification environment in soDLA is accomplished by chipyard as follows

![image info](./imgs/verif0.PNG)

riscv-toolchain as a compiler, will compile the testing program. And soDLA rtl is compiled by vcs/verilator. 

In the test/test_case/include/ape_small_single.h, provides the register address(or the KMD information). Application program is the ape_single.c 

