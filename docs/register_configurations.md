## Register Configurations

The register is configurated by csb bus, from software side. reference is the nvdla.org/hw/v1/hwarch.html.
As a practice, the example would be used from the test case of dc_1x1x8_1x1x8x1_int_0

### SDP Group 

#### S_STATUS

address: 0xa000

Idle status of two register groups

#### S_POINTER

address: 0xa004

Pointer for CSB master and data path to access groups. 

#### D_OP_ENABLE

address: 0xa008

Set it to 1 to kick off operation for current register group.

#### D_DATA_CUBE_WIDTH

address: 0xa00c

Input cube’s width, in the example, this is 1, so set it to zero.

#### D_DATA_CUBE_HEIGHT

address: 0xa010

Input cube’s height, in the example, this is 1, so set it to zero.

### D_DATA_CUBE_CHANNEL

address: 0xa014

Input cube’s channel, for sdp, the output cube channel should be 1(k is 1), so set it to zero 

### D_SRC_BASE_ADDR_LOW

address: 0xa018

Lower 32bits of input data address, in the example, the input address is 0x80050200, which is the initial data input address from memory. 

### D_SRC_BASE_ADDR_HIGH

address: 0xa01c

Higher 32bits of input data address when axi araddr is 64bits, is zero.

### D_SRC_LINE_STRIDE

address: 0xa020

Line stride of input cube

D_SRC_SURFACE_STRIDE

0xa024

Surface stride of input cube

D_BRDMA_CFG

0xa028

Configuration of BRDMA: enable/disable, data size, Ram type, etc.

D_BS_BASE_ADDR_LOW

0xa02c

Lower 32bits address of the bias data cube

D_BS_BASE_ADDR_HIGH

0xa030

Higher 32bits address of the bias data cube when axi araddr is 64bits

D_BS_LINE_STRIDE

0xa034

Line stride of bias data cube

D_BS_SURFACE_STRIDE

0xa038

Surface stride of bias data cube

D_BS_BATCH_STRIDE

0xa03c

Stride of bias data cube in batch mode

D_NRDMA_CFG

0xa040

Configuration of NRDMA: enable/disable, data size, Ram type, etc.

D_BN_BASE_ADDR_LOW

0xa044

Lower 32bits address of the bias data cube

D_BN_BASE_ADDR_HIGH

0xa048

Higher 32bits address of the bias data cube when axi araddr is 64bits

D_BN_LINE_STRIDE

0xa04c

Line stride of bias data cube

D_BN_SURFACE_STRIDE

0xa050

Surface stride of bias data cube

D_BN_BATCH_STRIDE

0xa054

Stride of bias data cube in batch mode

D_ERDMA_CFG

0xa058

Configuration of ERDMA: enable/disable, data size, Ram type, etc.

D_EW_BASE_ADDR_LOW

0xa05c

Lower 32bits address of the bias data cube

D_EW_BASE_ADDR_HIGH

0xa060

Higher 32bits address of the bias data cube when axi araddr is 64bits

D_EW_LINE_STRIDE

0xa064

Line stride of bias data cube

D_EW_SURFACE_STRIDE

0xa068

Surface stride of bias data cube

D_EW_BATCH_STRIDE

0xa06c

Stride of bias data cube in batch mode

D_FEATURE_MODE_CFG

0xa070

Operation configuration: flying mode, output destination, Direct or Winograd mode, flush NaN to zero, batch number.

D_SRC_DMA_CFG

0xa074

RAM type of input data cube

D_STATUS_NAN_INPUT_NUM

0xa078

Input NaN element number

D_STATUS_INF_INPUT_NUM

0xa07c

Input Infinity element number

D_PERF_ENABLE

0xa080

Enable/Disable performance counting

D_PERF_MRDMA_READ_STALL

0xa084

Count stall cycles of M read DMA for one layer

D_PERF_BRDMA_READ_STALL

0xa088

Count stall cycles of B read DMA for one layer

D_PERF_NRDMA_READ_STALL

0xa08c

Count stall cycles of N read DMA for one layer

D_PERF_ERDMA_READ_STALL

0xa090

Count stall cycles of E read DMA for one layer


