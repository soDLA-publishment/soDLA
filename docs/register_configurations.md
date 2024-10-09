## Register Configurations

The register is configurated by csb bus, from software side. reference is the nvdla.org/hw/v1/hwarch.html. D_*** , means the register infomation is in the dual register group, S_*** means the register infomation is in the single register group.
As a practice, the example would be used from the test case of dc_1x1x8_1x1x8x1_int_0

### SDP RDMA Group 

#### S_STATUS

address: 0xa000

format: Cat("b0".asUInt(14.W), io.status_1, "b0".asUInt(14.W), io.status_0)

default value: initial state depends on dp0_op_en or dp1_op_en

Idle status of two register groups, to indicate which register group is idle. 

#### S_POINTER

address: 0xa004

format: Cat("b0".asUInt(15.W), io.consumer, "b0".asUInt(15.W), io.producer)

default value: consumer is initially 0, producer is initially 0. 

Pointer for CSB master and data path to access groups. consumer means the which dual register is consuming the data returned from data processor, pointer is a 1-bit tag, for selecting which dual register. 

#### D_OP_ENABLE

address: 0xa008

format: internal logic, unreadable

Set it to 1 to kick off operation for current register group. 

#### D_DATA_CUBE_WIDTH

address: 0xa00c

format: Cat("b0".asUInt(19.W), io.field.width_a[12:0])

default value: 0

Input cube’s width, in the example, width after convolution is still 1, so set it to zero.

#### D_DATA_CUBE_HEIGHT

address: 0xa010

format: Cat("b0".asUInt(19.W), io.field.height[12:0])

default value: 0

Input cube’s height, in the example, height after convolution is still 1, so set it to zero.

#### D_DATA_CUBE_CHANNEL

address: 0xa014

format: Cat("b0".asUInt(19.W), io.field.channel[12:0])

default value: 0

Input cube’s channel, for sdp, the output cube channel should be 1(k is 1), so set it to zero 

#### D_SRC_BASE_ADDR_LOW

address: 0xa018

format: io.field.src_base_addr_low[31:0]

default value: 0

Lower 32bits of input data address

#### D_SRC_BASE_ADDR_HIGH

address: 0xa01c

format: io.field.dst_base_addr_low[31:0]

default value: 0

Higher 32bits of input data address when axi araddr is 64bits, is zero. 

#### D_SRC_LINE_STRIDE

address: 0xa020

format: io.field.src_line_stride[31:0]

default value: 0

Line stride of input cube, is the distance in bytes from one line to another(according to a nvdla issue).  Line stride refers to the stride within one surface.

### D_SRC_SURFACE_STRIDE

address: 0xa024

format: io.field.src_surface_stride[31:0]

default value: 0

Surface stride of input cube, is the distance in bytes from one surface to the next(according to a nvdla issue).  Surface stride refers to the stride between surfaces.

### D_BRDMA_CFG

address: 0xa028

format: Cat("b0".asUInt(26.W), io.field.brdma_ram_type[0], io.field.brdma_data_mode[0], io.field.brdma_data_size[0], io.field.brdma_data_use[1:0], io.field.brdma_disable[0])

default value: 0

Configuration of BRDMA: enable/disable, data size, Ram type, etc, default to be zero(to be implemented deeper later)

### D_BS_BASE_ADDR_LOW

address: 0xa02c

format: io.field.bs_base_addr_low[31:0]

defaut value: 0

Lower 32bits address of the bias data cube. 

### D_BS_BASE_ADDR_HIGH

address: 0xa030

format: io.field.bs_base_addr_high[31:0]

default value: 0

Higher 32bits address of the bias data cube when axi araddr is 64bits

#### D_BS_LINE_STRIDE

address: 0xa034

format: io.field.bs_line_stride[31:0]

default: 0

Line stride of bias data cube.

#### D_BS_SURFACE_STRIDE

address: 0xa038

format: io.field.bs_surface_stride[31:0]

Surface stride of bias data cube.

#### D_BS_BATCH_STRIDE

address: 0xa03c

format: io.field.bs_batch_stride[31:0]

Stride of bias data cube in batch mode(mentioned in NVDLA programming guide).

#### D_NRDMA_CFG

address: 0xa040

format: Cat("b0".asUInt(26.W), io.field.nrdma_ram_type, io.field.nrdma_data_mode, io.field.nrdma_data_size, io.field.nrdma_data_use[1:0], io.field.nrdma_disable)

default value: all 0.

Configuration of NRDMA: enable/disable, data size, Ram type, etc.

#### D_BN_BASE_ADDR_LOW

address: 0xa044

format: io.field.bn_base_addr_low[31:0]

default value: 0

Lower 32bits address of the bias data cube, BN is for batch normalization. 

#### D_BN_BASE_ADDR_HIGH

address: 0xa048

format: io.field.bn_base_addr_high[31:0]

default value: 0

Higher 32bits address of the bias data cube when axi araddr is 64bits.

#### D_BN_LINE_STRIDE

address: 0xa04c

format: io.field.bn_line_stride[31:0]

default value: 0

Line stride of bias data cube for batch normalization

#### D_BN_SURFACE_STRIDE

address: 0xa050

format: io.field.bn_line_stride[31:0]

default value: 0

Surface stride of bias data cube for batch normalization

#### D_BN_BATCH_STRIDE

address: 0xa054

format: io.field.bn_batch_stride[31:0]

default value: 0

Stride of bias data cube for batch normalization in multi-batch mode

#### D_ERDMA_CFG

address: 0xa058

format: Cat("b0".asUInt(26.W), io.field.erdma_ram_type, io.field.erdma_data_mode, io.field.erdma_data_size, io.field.erdma_data_use[1:0], io.field.erdma_disable)

default value: 0

Configuration of ERDMA: enable/disable, data size, Ram type, etc. Those are the configurations in the EW mode(will implement more document later).

#### D_EW_BASE_ADDR_LOW

address: 0xa05c

format: io.field.ew_base_addr_low[31:0]

default value: 0

Lower 32bits address of the bias data cube

#### D_EW_BASE_ADDR_HIGH

address: 0xa060

format: io.field.ew_base_addr_high[31:0]

default value: 0

Higher 32bits address of the bias data cube when axi araddr is 64bits. 

#### D_EW_LINE_STRIDE

address: 0xa064

format: io.field.ew_line_stride[31:0]

default value: 0

Line stride of bias data cube for element-wise mode.

#### D_EW_SURFACE_STRIDE

address: 0xa068

format: io.field.ew_surface_stride[31:0]

default value: 0

Surface stride of bias data cube for element-wise mode.

#### D_EW_BATCH_STRIDE

address: 0xa06c

format: io.field.ew_batch_stride[31:0]

default value: -

Stride of bias data cube in batch mode for element-wise mode.

#### D_FEATURE_MODE_CFG

address: 0xa070

format: Cat("b0".asUInt(19.W), io.field.batch_number, io.field.out_precision, io.field.proc_precision, io.field.in_precision, io.field.winograd, io.field.flying_mode)

default value: 0

Operation configuration: flying mode, output destination, Direct or Winograd mode, flush NaN to zero, batch number.

#### D_SRC_DMA_CFG

address: 0xa074

format: Cat("b0".asUInt(31.W), io.field.src_ram_type)

default value: 0

RAM type of input data cube. There are two dma receivers in sdp rdma, one for the first memory, another one is for the secondary memory. src_ram_type = 1 means use the first receiver to get the rdma data. 

#### D_STATUS_NAN_INPUT_NUM

address: 0xa078

format: io.status_nan_input_num[31:0]

Input NaN element number. From dp side to reg side so it is unavailable for reg_write. This is a status signal. 

#### D_STATUS_INF_INPUT_NUM

address: 0xa07c

format: io.status_inf_input_num[31:0]

Input Infinity element number. From dp side to reg sideso it is unavailable for reg_write. This is a status signal.

#### D_PERF_ENABLE

address: 0xa080

format: Cat("b0".asUInt(30.W), io.field.perf_nan_inf_count_en, io.field.perf_dma_en)

default: 0

Enable/Disable performance counting. 

#### D_PERF_MRDMA_READ_STALL

address: 0xa084

format: io.mrdma_stall[31:0]

Count stall cycles of M read DMA for one layer, for MRDMA ig stage. MRDMA ig is to send request to external memory.  

#### D_PERF_BRDMA_READ_STALL

address: 0xa088

format: io.brdma_stall[31:0]

Count stall cycles of B read DMA for one layer, for BRDMA ig stage. BRDMA ig is to send requst to external memory in multi-batch mode. 

#### D_PERF_NRDMA_READ_STALL

address: 0xa08c

format: io.ndmma_stall[31:0]

Count stall cycles of N read DMA for one layer, for NRDMA ig stage, in batch normalization

#### D_PERF_ERDMA_READ_STALL

address: 0xa090

format: io.edmma_stall[31:0]

Count stall cycles of E read DMA for one layer, for ERDMA ig stage, in element-wise mode.


## SDP Group 

#### S_STATUS

address: 0xb000

format: Cat("b0".asUInt(14.W), io.status_1, "b0".asUInt(14.W), io.status_0)

default value: initial state depends on dp0_op_en or dp1_op_en

Idle status of two register groups, to indicate which register group is idle. 

#### S_POINTER

address: 0xb004

format: Cat("b0".asUInt(15.W), io.consumer, "b0".asUInt(15.W), io.producer)

default value: consumer is initially 0, producer is initially 0. 

Pointer for CSB master and data path to access groups. consumer means the which dual register is consuming the data returned from data processor, pointer is a 1-bit tag, for selecting which dual register. 

#### S_LUT_ACCESS_CFG

address: 0xb008

format: Cat("b0".asUInt(14.W), io.lut_access_type, io.lut_table_id, "b0".asUInt(6.W), io.lut_addr[9:0])

default value: all 0.







