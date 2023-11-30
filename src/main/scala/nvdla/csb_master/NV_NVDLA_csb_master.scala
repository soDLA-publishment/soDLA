package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver

@chiselName
class NV_NVDLA_csb_master(implicit val conf: nvdlaConfig)  extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_falcon_clk = Input(Clock())

        val nvdla_core_rstn = Input(Bool())
        val nvdla_falcon_rstn = Input(Bool())

        //pwr_ram
        val pwrbus_ram_pd = Input(UInt(32.W))

        //csb2nvdla
        val csb2nvdla = Flipped(DecoupledIO(new csb2nvdla_if))
        val nvdla2csb = ValidIO(new nvdla2csb_if)
        val nvdla2csb_wr_complete = Output(Bool())

        //cfgrom
        val csb2cfgrom = Flipped(new csb2dp_if)

        //csb2glb
        val csb2glb = Flipped(new csb2dp_if)

        //mcif
        val csb2mcif = Flipped(new csb2dp_if)

        //memif
        val csb2cvif = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(new csb2dp_if)) else None

        //bdma
        val csb2bdma = if(conf.NVDLA_BDMA_ENABLE) Some(Flipped(new csb2dp_if)) else None

        //cdma
        val csb2cdma = Flipped(new csb2dp_if)

        //csc
        val csb2csc = Flipped(new csb2dp_if)

        //cmac
        val csb2cmac_a = Flipped(new csb2dp_if)
        val csb2cmac_b = Flipped(new csb2dp_if)

        //cacc
        val csb2cacc = Flipped(new csb2dp_if)

        //sdp
        val csb2sdp_rdma = Flipped(new csb2dp_if)
        val csb2sdp = Flipped(new csb2dp_if)

        //pdp
        val csb2pdp_rdma = if(conf.NVDLA_PDP_ENABLE) Some(Flipped(new csb2dp_if)) else None 
        val csb2pdp = if(conf.NVDLA_PDP_ENABLE) Some(Flipped(new csb2dp_if)) else None 

        //cdp
        val csb2cdp_rdma = if(conf.NVDLA_CDP_ENABLE) Some(Flipped(new csb2dp_if)) else None 
        val csb2cdp = if(conf.NVDLA_CDP_ENABLE) Some(Flipped(new csb2dp_if)) else None 

        //rubik
        val csb2rbk = if(conf.NVDLA_RUBIK_ENABLE) Some(Flipped(new csb2dp_if)) else None 

    })

//     
//          ┌─┐       ┌─┐
//       ┌──┘ ┴───────┘ ┴──┐
//       │                 │
//       │       ───       │
//       │  ─┬┘       └┬─  │
//       │                 │
//       │       ─┴─       │
//       │                 │
//       └───┐         ┌───┘
//           │         │
//           │         │
//           │         │
//           │         └──────────────┐
//           │                        │
//           │                        ├─┐
//           │                        ┌─┘    
//           │                        │
//           └─┐  ┐  ┌───────┬──┐  ┌──┘         
//             │ ─┤ ─┤       │ ─┤ ─┤         
//             └──┴──┘       └──┴──┘ 
withClockAndReset(io.nvdla_core_clk, ~io.nvdla_core_rstn){
////////////////////////////////////////////////////////////////////////
// CSB interface to async FIFO                                        //
////////////////////////////////////////////////////////////////////////
val csb2nvdla_pd = Cat(io.csb2nvdla.bits.nposted,io.csb2nvdla.bits.write,io.csb2nvdla.bits.wdat,io.csb2nvdla.bits.addr)
val core_req_prdy = true.B

val u_fifo_csb2nvdla = Module(new NV_NVDLA_CSB_MASTER_falcon2csb_fifo)
//clk&pwr_ram
u_fifo_csb2nvdla.io.wr_clk := io.nvdla_falcon_clk
u_fifo_csb2nvdla.io.rd_clk := io.nvdla_core_clk
u_fifo_csb2nvdla.io.wr_reset_ := io.nvdla_falcon_rstn
u_fifo_csb2nvdla.io.rd_reset_ := io.nvdla_core_rstn

u_fifo_csb2nvdla.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//wr
u_fifo_csb2nvdla.io.wr_req := io.csb2nvdla.valid
u_fifo_csb2nvdla.io.wr_data := csb2nvdla_pd
io.csb2nvdla.ready := u_fifo_csb2nvdla.io.wr_ready
//rd
val core_req_pvld = u_fifo_csb2nvdla.io.rd_req
val core_req_pd = u_fifo_csb2nvdla.io.rd_data
u_fifo_csb2nvdla.io.rd_ready := core_req_prdy

val core_resp_pvld = Wire(Bool())
val core_resp_pd = Wire(UInt(34.W))
val u_fifo_nvdla2csb = Module(new NV_NVDLA_CSB_MASTER_csb2falcon_fifo)
//clk&pwr_ram
u_fifo_nvdla2csb.io.wr_clk := io.nvdla_core_clk
u_fifo_nvdla2csb.io.rd_clk := io.nvdla_falcon_clk
u_fifo_nvdla2csb.io.wr_reset_ := io.nvdla_core_rstn
u_fifo_nvdla2csb.io.rd_reset_ := io.nvdla_falcon_rstn
u_fifo_nvdla2csb.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//wr
u_fifo_nvdla2csb.io.wr_req := core_resp_pvld
u_fifo_nvdla2csb.io.wr_data := core_resp_pd
val core_resp_prdy = u_fifo_nvdla2csb.io.wr_ready
//rd
val nvdla2csb_resp_pvld = u_fifo_nvdla2csb.io.rd_req
val nvdla2csb_resp_pd = u_fifo_nvdla2csb.io.rd_data
u_fifo_nvdla2csb.io.rd_ready := true.B

val nvdla2csb_rresp_rdat = nvdla2csb_resp_pd
val nvdla2csb_rresp_is_valid = nvdla2csb_resp_pvld  && (nvdla2csb_resp_pd(33) === false.B)
val nvdla2csb_wresp_is_valid = nvdla2csb_resp_pvld && (nvdla2csb_resp_pd(33) === true.B)

io.nvdla2csb.valid := withClockAndReset(io.nvdla_falcon_clk, !io.nvdla_falcon_rstn){RegNext(nvdla2csb_rresp_is_valid, false.B)}
io.nvdla2csb.bits.data := withClockAndReset(io.nvdla_falcon_clk, !io.nvdla_falcon_rstn){RegEnable(nvdla2csb_rresp_rdat, "b0".asUInt(32.W), nvdla2csb_rresp_is_valid)}
io.nvdla2csb_wr_complete := withClockAndReset(io.nvdla_falcon_clk, !io.nvdla_falcon_rstn){RegNext(nvdla2csb_wresp_is_valid, false.B)}

////////////////////////////////////////////////////////////////////////
// Distribute request and gather response                             //
////////////////////////////////////////////////////////////////////////
val core_req_addr = core_req_pd(15, 0)
val core_req_write = core_req_pd(48)
val core_req_nposted   = core_req_pd(49)
val core_req_pop_valid = core_req_pvld & core_req_prdy

//core_req_addr is word aligned while address from arnvdla is byte aligned.
val core_byte_addr = Cat(core_req_addr, "b0".asUInt(2.W))
val core_req_pd_d1 = RegEnable(core_req_pd, core_req_pvld & core_req_prdy)

val addr_mask = Cat(Fill(6, true.B), Fill(12, false.B))
//////////////// for CFGROM ////////////////
val u_client_cfgrom = Module{new NV_NVDLA_CSB_MASTER_for_client(address_space = "h00000000")}
u_client_cfgrom.io.nvdla_core_clk := io.nvdla_core_clk
u_client_cfgrom.io.core_req_pop_valid := core_req_pop_valid
u_client_cfgrom.io.core_byte_addr := core_byte_addr
u_client_cfgrom.io.addr_mask := addr_mask
u_client_cfgrom.io.core_req_pd_d1 := core_req_pd_d1
u_client_cfgrom.io.csb2client <> io.csb2cfgrom
val cfgrom_resp_pd = u_client_cfgrom.io.client_resp_pd
val select_cfgrom = u_client_cfgrom.io.select_client

//////////////// for GLB ////////////////
val u_client_glb = Module{new NV_NVDLA_CSB_MASTER_for_client(address_space = "h00001000")}
u_client_glb.io.nvdla_core_clk := io.nvdla_core_clk
u_client_glb.io.core_req_pop_valid := core_req_pop_valid
u_client_glb.io.core_byte_addr := core_byte_addr
u_client_glb.io.addr_mask := addr_mask
u_client_glb.io.core_req_pd_d1 := core_req_pd_d1
u_client_glb.io.csb2client <> io.csb2glb
val glb_resp_pd = u_client_glb.io.client_resp_pd
val select_glb = u_client_glb.io.select_client

//////////////// for MCIF ////////////////
val u_client_mcif = Module{new NV_NVDLA_CSB_MASTER_for_client(address_space = "h00002000")}
u_client_mcif.io.nvdla_core_clk := io.nvdla_core_clk
u_client_mcif.io.core_req_pop_valid := core_req_pop_valid
u_client_mcif.io.core_byte_addr := core_byte_addr
u_client_mcif.io.addr_mask := addr_mask
u_client_mcif.io.core_req_pd_d1 := core_req_pd_d1
u_client_mcif.io.csb2client <> io.csb2mcif
val mcif_resp_pd = u_client_mcif.io.client_resp_pd
val select_mcif = u_client_mcif.io.select_client

//////////////// for CVIF ////////////////
val u_client_cvif = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Module{new NV_NVDLA_CSB_MASTER_for_client(address_space = "h0000f000")}) else None
if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
    u_client_cvif.get.io.nvdla_core_clk := io.nvdla_core_clk
    u_client_cvif.get.io.core_req_pop_valid := core_req_pop_valid
    u_client_cvif.get.io.core_byte_addr := core_byte_addr
    u_client_cvif.get.io.addr_mask := addr_mask
    u_client_cvif.get.io.core_req_pd_d1 := core_req_pd_d1
    u_client_cvif.get.io.csb2client <> io.csb2cvif.get
}
val cvif_resp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(u_client_cvif.get.io.client_resp_pd) else None
val select_cvif = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) u_client_cvif.get.io.select_client else false.B

//////////////// for BDMA ////////////////
val u_client_bdma = if(conf.NVDLA_BDMA_ENABLE) Some(Module{new NV_NVDLA_CSB_MASTER_for_client(address_space = "h00010000")}) else None
if(conf.NVDLA_BDMA_ENABLE){
    u_client_bdma.get.io.nvdla_core_clk := io.nvdla_core_clk
    u_client_bdma.get.io.core_req_pop_valid := core_req_pop_valid
    u_client_bdma.get.io.core_byte_addr := core_byte_addr
    u_client_bdma.get.io.addr_mask := addr_mask
    u_client_bdma.get.io.core_req_pd_d1 := core_req_pd_d1
    u_client_bdma.get.io.csb2client <> io.csb2bdma.get
}

val bdma_resp_pd = if(conf.NVDLA_BDMA_ENABLE) Some(u_client_bdma.get.io.client_resp_pd) else None
val select_bdma = if(conf.NVDLA_BDMA_ENABLE) u_client_bdma.get.io.select_client else false.B


//////////////// for CDMA ////////////////
val u_client_cdma = Module{new NV_NVDLA_CSB_MASTER_for_client(address_space = "h00003000")}
u_client_cdma.io.nvdla_core_clk := io.nvdla_core_clk
u_client_cdma.io.core_req_pop_valid := core_req_pop_valid
u_client_cdma.io.core_byte_addr := core_byte_addr
u_client_cdma.io.addr_mask := addr_mask
u_client_cdma.io.core_req_pd_d1 := core_req_pd_d1
u_client_cdma.io.csb2client <> io.csb2cdma
val cdma_resp_pd = u_client_cdma.io.client_resp_pd
val select_cdma = u_client_cdma.io.select_client

//////////////// for CSC ////////////////
val u_client_csc = Module{new NV_NVDLA_CSB_MASTER_for_client(address_space = "h00004000")}
u_client_csc.io.nvdla_core_clk := io.nvdla_core_clk
u_client_csc.io.core_req_pop_valid := core_req_pop_valid
u_client_csc.io.core_byte_addr := core_byte_addr
u_client_csc.io.addr_mask := addr_mask
u_client_csc.io.core_req_pd_d1 := core_req_pd_d1
u_client_csc.io.csb2client <> io.csb2csc
val csc_resp_pd = u_client_csc.io.client_resp_pd
val select_csc = u_client_csc.io.select_client

//////////////// for CMAC_A ////////////////
val u_client_cmac_a = Module{new NV_NVDLA_CSB_MASTER_for_client(address_space = "h00005000")}
u_client_cmac_a.io.nvdla_core_clk := io.nvdla_core_clk
u_client_cmac_a.io.core_req_pop_valid := core_req_pop_valid
u_client_cmac_a.io.core_byte_addr := core_byte_addr
u_client_cmac_a.io.addr_mask := addr_mask
u_client_cmac_a.io.core_req_pd_d1 := core_req_pd_d1
u_client_cmac_a.io.csb2client <> io.csb2cmac_a
val cmac_a_resp_pd = u_client_cmac_a.io.client_resp_pd
val select_cmac_a = u_client_cmac_a.io.select_client

//////////////// for CMAC_B ////////////////
val u_client_cmac_b = Module{new NV_NVDLA_CSB_MASTER_for_client(address_space = "h00006000")}
u_client_cmac_b.io.nvdla_core_clk := io.nvdla_core_clk
u_client_cmac_b.io.core_req_pop_valid := core_req_pop_valid
u_client_cmac_b.io.core_byte_addr := core_byte_addr
u_client_cmac_b.io.addr_mask := addr_mask
u_client_cmac_b.io.core_req_pd_d1 := core_req_pd_d1
u_client_cmac_b.io.csb2client <> io.csb2cmac_b
val cmac_b_resp_pd = u_client_cmac_b.io.client_resp_pd
val select_cmac_b = u_client_cmac_b.io.select_client

//////////////// for CACC////////////////
val u_client_cacc = Module{new NV_NVDLA_CSB_MASTER_for_client(address_space = "h00007000")}
u_client_cacc.io.nvdla_core_clk := io.nvdla_core_clk
u_client_cacc.io.core_req_pop_valid := core_req_pop_valid
u_client_cacc.io.core_byte_addr := core_byte_addr
u_client_cacc.io.addr_mask := addr_mask
u_client_cacc.io.core_req_pd_d1 := core_req_pd_d1
u_client_cacc.io.csb2client <> io.csb2cacc
val cacc_resp_pd = u_client_cacc.io.client_resp_pd
val select_cacc = u_client_cacc.io.select_client

//////////////// for SDP_RDMA////////////////
val u_client_sdp_rdma = Module{new NV_NVDLA_CSB_MASTER_for_client(address_space = "h00008000")}
u_client_sdp_rdma.io.nvdla_core_clk := io.nvdla_core_clk
u_client_sdp_rdma.io.core_req_pop_valid := core_req_pop_valid
u_client_sdp_rdma.io.core_byte_addr := core_byte_addr
u_client_sdp_rdma.io.addr_mask := addr_mask
u_client_sdp_rdma.io.core_req_pd_d1 := core_req_pd_d1
u_client_sdp_rdma.io.csb2client <> io.csb2sdp_rdma
val sdp_rdma_resp_pd = u_client_sdp_rdma.io.client_resp_pd
val select_sdp_rdma = u_client_sdp_rdma.io.select_client

//////////////// for SDP ////////////////
val u_client_sdp = Module{new NV_NVDLA_CSB_MASTER_for_client(address_space = "h00009000")}
u_client_sdp.io.nvdla_core_clk := io.nvdla_core_clk
u_client_sdp.io.core_req_pop_valid := core_req_pop_valid
u_client_sdp.io.core_byte_addr := core_byte_addr
u_client_sdp.io.addr_mask := addr_mask
u_client_sdp.io.core_req_pd_d1 := core_req_pd_d1
u_client_sdp.io.csb2client <> io.csb2sdp
val sdp_resp_pd = u_client_sdp.io.client_resp_pd
val select_sdp = u_client_sdp.io.select_client

//////////////// for PDP_RDMA ////////////////
val u_client_pdp_rdma = if(conf.NVDLA_PDP_ENABLE) Some(Module{new NV_NVDLA_CSB_MASTER_for_client(address_space = "h0000a000")}) else None
if(conf.NVDLA_PDP_ENABLE){
    u_client_pdp_rdma.get.io.nvdla_core_clk := io.nvdla_core_clk
    u_client_pdp_rdma.get.io.core_req_pop_valid := core_req_pop_valid
    u_client_pdp_rdma.get.io.core_byte_addr := core_byte_addr
    u_client_pdp_rdma.get.io.addr_mask := addr_mask
    u_client_pdp_rdma.get.io.core_req_pd_d1 := core_req_pd_d1
    u_client_pdp_rdma.get.io.csb2client <> io.csb2pdp_rdma.get
}

val pdp_rdma_resp_pd = if(conf.NVDLA_PDP_ENABLE) Some(u_client_pdp_rdma.get.io.client_resp_pd) else None
val select_pdp_rdma = if(conf.NVDLA_PDP_ENABLE) u_client_pdp_rdma.get.io.select_client else false.B


//////////////// for PDP ////////////////
val u_client_pdp = if(conf.NVDLA_PDP_ENABLE) Some(Module{new NV_NVDLA_CSB_MASTER_for_client(address_space = "h0000b000")}) else None
if(conf.NVDLA_PDP_ENABLE){
    u_client_pdp.get.io.nvdla_core_clk := io.nvdla_core_clk
    u_client_pdp.get.io.core_req_pop_valid := core_req_pop_valid
    u_client_pdp.get.io.core_byte_addr := core_byte_addr
    u_client_pdp.get.io.addr_mask := addr_mask
    u_client_pdp.get.io.core_req_pd_d1 := core_req_pd_d1
    u_client_pdp.get.io.csb2client <> io.csb2pdp.get
}

val pdp_resp_pd = if(conf.NVDLA_PDP_ENABLE) Some(u_client_pdp.get.io.client_resp_pd) else None
val select_pdp = if(conf.NVDLA_PDP_ENABLE) u_client_pdp.get.io.select_client else false.B


//////////////// for CDP_RDMA ////////////////
val u_client_cdp_rdma = if(conf.NVDLA_CDP_ENABLE) Some(Module{new NV_NVDLA_CSB_MASTER_for_client(address_space = "h0000c000")}) else None
if(conf.NVDLA_CDP_ENABLE){
    u_client_cdp_rdma.get.io.nvdla_core_clk := io.nvdla_core_clk
    u_client_cdp_rdma.get.io.core_req_pop_valid := core_req_pop_valid
    u_client_cdp_rdma.get.io.core_byte_addr := core_byte_addr
    u_client_cdp_rdma.get.io.addr_mask := addr_mask
    u_client_cdp_rdma.get.io.core_req_pd_d1 := core_req_pd_d1
    u_client_cdp_rdma.get.io.csb2client <> io.csb2cdp_rdma.get
}

val cdp_rdma_resp_pd = if(conf.NVDLA_CDP_ENABLE) Some(u_client_cdp_rdma.get.io.client_resp_pd) else None
val select_cdp_rdma = if(conf.NVDLA_CDP_ENABLE) u_client_cdp_rdma.get.io.select_client else false.B

//////////////// for CDP ////////////////
val u_client_cdp = if(conf.NVDLA_CDP_ENABLE) Some(Module{new NV_NVDLA_CSB_MASTER_for_client(address_space = "h0000d000")}) else None
if(conf.NVDLA_CDP_ENABLE){
    u_client_cdp.get.io.nvdla_core_clk := io.nvdla_core_clk
    u_client_cdp.get.io.core_req_pop_valid := core_req_pop_valid
    u_client_cdp.get.io.core_byte_addr := core_byte_addr
    u_client_cdp.get.io.addr_mask := addr_mask
    u_client_cdp.get.io.core_req_pd_d1 := core_req_pd_d1
    u_client_cdp.get.io.csb2client <> io.csb2cdp.get
}

val cdp_resp_pd = if(conf.NVDLA_CDP_ENABLE) Some(u_client_cdp.get.io.client_resp_pd) else None
val select_cdp = if(conf.NVDLA_CDP_ENABLE) u_client_cdp.get.io.select_client else false.B

//////////////// for RUBIK ////////////////
val u_client_rubik = if(conf.NVDLA_RUBIK_ENABLE) Some(Module{new NV_NVDLA_CSB_MASTER_for_client(address_space = "h00011000")}) else None
if(conf.NVDLA_RUBIK_ENABLE){
    u_client_rubik.get.io.nvdla_core_clk := io.nvdla_core_clk
    u_client_rubik.get.io.core_req_pop_valid := core_req_pop_valid
    u_client_rubik.get.io.core_byte_addr := core_byte_addr
    u_client_rubik.get.io.addr_mask := addr_mask
    u_client_rubik.get.io.core_req_pd_d1 := core_req_pd_d1
    u_client_rubik.get.io.csb2client <> io.csb2rbk.get
}

val rbk_resp_pd = if(conf.NVDLA_RUBIK_ENABLE) Some(u_client_rubik.get.io.client_resp_pd) else None
val select_rbk = if(conf.NVDLA_RUBIK_ENABLE) u_client_rubik.get.io.select_client else false.B

//////////////// for DUMMY ////////////////
////////////////// dummy client //////////////////////
val csb2dummy_req_pvld = RegInit(false.B)
val csb2dummy_req_nposted = Reg(Bool())
val csb2dummy_req_read = Reg(Bool())

val select_dummy = ~(select_cfgrom
                      | select_glb
                      | select_mcif
                      | select_cvif
                      | select_bdma
                      | select_cdma
                      | select_csc
                      | select_cmac_a
                      | select_cmac_b
                      | select_cacc
                      | select_sdp_rdma
                      | select_sdp
                      | select_pdp_rdma
                      | select_pdp
                      | select_cdp
                      | select_cdp_rdma
                      | select_rbk)

val dummy_req_pvld_w = (core_req_pop_valid & select_dummy)
csb2dummy_req_pvld := dummy_req_pvld_w
when(dummy_req_pvld_w){
    csb2dummy_req_nposted := core_req_nposted
}
when(dummy_req_pvld_w){
    csb2dummy_req_read := ~core_req_write
}

val dummy_resp_rdat = Fill(32, false.B)
val dummy_resp_error = false.B
val dummy_rresp_pd = Cat(false.B, dummy_resp_error, dummy_resp_rdat(31,0))    /* PKT_nvdla_xx2csb_resp_dla_xx2csb_rd_erpt_ID  */
val dummy_wresp_pd = Cat(true.B, dummy_resp_error, dummy_resp_rdat(31,0))    /* PKT_nvdla_xx2csb_resp_dla_xx2csb_wr_erpt_ID  */

val dummy_resp_valid_w = csb2dummy_req_pvld & (csb2dummy_req_nposted | csb2dummy_req_read)
val dummy_resp_type_w = ~csb2dummy_req_read & csb2dummy_req_nposted
val dummy_resp_type = RegEnable(dummy_resp_type_w, false.B, dummy_resp_valid_w)
val dummy_resp_pd = Mux(dummy_resp_type, dummy_wresp_pd, dummy_rresp_pd)

val dummy_resp_valid = RegNext(dummy_resp_valid_w, false.B)


//////////////// ass3mble ////////////////

val cvif_resp_valid_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) cvif_resp_pd.get.bits else Fill(34, false.B)                     
val cvif_resp_valid_get = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) cvif_resp_pd.get.valid else false.B

val bdma_resp_valid_pd = if(conf.NVDLA_BDMA_ENABLE) bdma_resp_pd.get.bits else Fill(34, false.B)
val bdma_resp_valid_get = if(conf.NVDLA_BDMA_ENABLE) bdma_resp_pd.get.valid else false.B

val pdp_rdma_resp_valid_pd = if(conf.NVDLA_PDP_ENABLE) pdp_rdma_resp_pd.get.bits else Fill(34, false.B)
val pdp_rdma_resp_valid_get = if(conf.NVDLA_PDP_ENABLE)  pdp_rdma_resp_pd.get.valid else false.B

val pdp_resp_valid_pd = if(conf.NVDLA_PDP_ENABLE) pdp_resp_pd.get.bits else Fill(34, false.B)
val pdp_resp_valid_get = if(conf.NVDLA_PDP_ENABLE) pdp_resp_pd.get.valid else false.B

val cdp_rdma_resp_valid_pd = if(conf.NVDLA_CDP_ENABLE) cdp_rdma_resp_pd.get.bits else Fill(34, false.B)
val cdp_rdma_resp_valid_get = if(conf.NVDLA_CDP_ENABLE)  cdp_rdma_resp_pd.get.valid else false.B

val cdp_resp_valid_pd = if(conf.NVDLA_CDP_ENABLE) cdp_resp_pd.get.bits else Fill(34, false.B)
val cdp_resp_valid_get = if(conf.NVDLA_CDP_ENABLE) cdp_resp_pd.get.valid else false.B

val rbk_resp_valid_pd = if(conf.NVDLA_RUBIK_ENABLE) rbk_resp_pd.get.bits else Fill(34, false.B)
val rbk_resp_valid_get = if(conf.NVDLA_RUBIK_ENABLE) rbk_resp_pd.get.valid else false.B


core_resp_pd := (Fill(34, cfgrom_resp_pd.valid) & cfgrom_resp_pd.bits)|
                      (Fill(34, glb_resp_pd.valid) & glb_resp_pd.bits)|
                      (Fill(34, mcif_resp_pd.valid) & mcif_resp_pd.bits)|
                      (Fill(34, cvif_resp_valid_get) & cvif_resp_valid_pd)|
                      (Fill(34, bdma_resp_valid_get) & bdma_resp_valid_pd)|
                      (Fill(34, cdma_resp_pd.valid) & cdma_resp_pd.bits)|
                      (Fill(34, csc_resp_pd.valid) & csc_resp_pd.bits)|
                      (Fill(34, cmac_a_resp_pd.valid) & cmac_a_resp_pd.bits)|
                      (Fill(34, cmac_b_resp_pd.valid) & cmac_b_resp_pd.bits)|
                      (Fill(34, cacc_resp_pd.valid) & cacc_resp_pd.bits)|
                      (Fill(34, sdp_rdma_resp_pd.valid) & sdp_rdma_resp_pd.bits)|
                      (Fill(34, sdp_resp_pd.valid) & sdp_resp_pd.bits)|
                      (Fill(34, pdp_rdma_resp_valid_get) & pdp_rdma_resp_valid_pd)|
                      (Fill(34, pdp_resp_valid_get) & pdp_resp_valid_pd)|
                      (Fill(34, cdp_rdma_resp_valid_get) & cdp_rdma_resp_valid_pd)|
                      (Fill(34, cdp_resp_valid_get) & cdp_resp_valid_pd)|
                      (Fill(34, rbk_resp_valid_get) & rbk_resp_valid_pd)|
                      (Fill(34, dummy_resp_valid) & dummy_resp_pd)

core_resp_pvld := cfgrom_resp_pd.valid |
                        glb_resp_pd.valid |
                        mcif_resp_pd.valid |
                        cvif_resp_valid_get |
                        bdma_resp_valid_get |
                        cdma_resp_pd.valid |
                        csc_resp_pd.valid |
                        cmac_a_resp_pd.valid |
                        cmac_b_resp_pd.valid |
                        cacc_resp_pd.valid |
                        sdp_rdma_resp_pd.valid |
                        sdp_resp_pd.valid |
                        pdp_rdma_resp_valid_get |
                        pdp_resp_valid_get |
                        cdp_rdma_resp_valid_get |
                        cdp_resp_valid_get |
                        rbk_resp_valid_get |
                        dummy_resp_valid;
}}


object NV_NVDLA_csb_masterDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_csb_master())
}