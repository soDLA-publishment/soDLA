// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._
// import chisel3.iotesters.Driver

// class NV_NVDLA_csb_master(implicit val conf: csbMasterConfiguration)  extends Module {
//     val io = IO(new Bundle {
//         //general clock
//         val nvdla_core_clk = Input(Clock())
//         val nvdla_falcon_clk = Input(Clock())

//         //pwr_ram
//         val pwrbus_ram_pd = Input(UInt(32.W))

//         //csb2nvdla
//         val csb2nvdla_valid = Input(Bool()) /* data valid */
//         val csb2nvdla_ready = Output(Bool())   /* data return handshake */ 
//         val csb2nvdla_addr = Input(UInt(16.W))
//         val csb2nvdla_wdat = Input(UInt(32.W))
//         val csb2nvdla_write = Input(Bool())
//         val csb2nvdla_nposted = Input(Bool())

//         val nvdla2csb_valid = Output(Bool())    /* data valid */
//         val nvdla2csb_data = Output(UInt(32.W))
//         val nvdla2csb_wr_complete = Output(Bool())

//         //cfgrom
//         val csb2cfgrom = Flipped(new csb2dp_if)

//         //csb2glb
//         val csb2glb = Flipped(new csb2dp_if)

//         //mcif
//         val csb2mcif = Flipped(new csb2dp_if)

//         //memif
//         val csb2cvif = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Flipped(new csb2dp_if)) else None

//         //bdma
//         val csb2bdma = if(conf.NVDLA_BDMA_ENABLE) Some(Flipped(new csb2dp_if)) else None

//         //cdma
//         val csb2cdma = Flipped(new csb2dp_if)

//         //csc
//         val csb2csc = Flipped(new csb2dp_if)

//         //cmac
//         val csb2cmac_a = Flipped(new csb2dp_if)
//         val csb2cmac_b = Flipped(new csb2dp_if)

//         //cacc
//         val csb2cacc = Flipped(new csb2dp_if)

//         //sdp
//         val csb2sdp_rdma = Flipped(new csb2dp_if)
//         val csb2sdp = Flipped(new csb2dp_if)

//         //pdp
//         val csb2pdp_rdma = if(conf.NVDLA_PDP_ENABLE) Some(Flipped(new csb2dp_if)) else None 
//         val csb2pdp = if(conf.NVDLA_PDP_ENABLE) Some(Flipped(new csb2dp_if)) else None 

//         //cdp
//         val csb2cdp_rdma = if(conf.NVDLA_CDP_ENABLE) Some(Flipped(new csb2dp_if)) else None 
//         val csb2cdp = if(conf.NVDLA_CDP_ENABLE) Some(Flipped(new csb2dp_if)) else None 

//         //rubik
//         val csb2rbk = if(conf.NVDLA_RUBIK_ENABLE) Some(Flipped(new csb2dp_if)) else None 

//     })

// //     
// //          ┌─┐       ┌─┐
// //       ┌──┘ ┴───────┘ ┴──┐
// //       │                 │
// //       │       ───       │
// //       │  ─┬┘       └┬─  │
// //       │                 │
// //       │       ─┴─       │
// //       │                 │
// //       └───┐         ┌───┘
// //           │         │
// //           │         │
// //           │         │
// //           │         └──────────────┐
// //           │                        │
// //           │                        ├─┐
// //           │                        ┌─┘    
// //           │                        │
// //           └─┐  ┐  ┌───────┬──┐  ┌──┘         
// //             │ ─┤ ─┤       │ ─┤ ─┤         
// //             └──┴──┘       └──┴──┘ 
// withClock(io.nvdla_core_clk){
// ////////////////////////////////////////////////////////////////////////
// // CSB interface to async FIFO                                        //
// ////////////////////////////////////////////////////////////////////////
// val csb2nvdla_pd = Cat(io.csb2nvdla_nposted,io.csb2nvdla_write,io.csb2nvdla_wdat,io.csb2nvdla_addr)
// val core_req_prdy = true.B

// val u_fifo_csb2nvdla = Module(new NV_NVDLA_CSB_MASTER_falcon2csb_fifo)
// //clk&pwr_ram
// u_fifo_csb2nvdla.io.wr_clk := io.nvdla_falcon_clk
// u_fifo_csb2nvdla.io.rd_clk := io.nvdla_core_clk
// u_fifo_csb2nvdla.io.pwrbus_ram_pd := io.pwrbus_ram_pd
// //wr
// u_fifo_csb2nvdla.io.wr_req := io.csb2nvdla_valid
// u_fifo_csb2nvdla.io.wr_data := csb2nvdla_pd
// io.csb2nvdla_ready := u_fifo_csb2nvdla.io.wr_ready
// //rd
// val core_req_pvld = u_fifo_csb2nvdla.io.rd_req
// val core_req_pd = u_fifo_csb2nvdla.io.rd_data
// u_fifo_csb2nvdla.io.rd_ready := core_req_prdy

// val core_resp_pvld = Wire(Bool())
// val core_resp_pd = Wire(UInt(34.W))
// val u_fifo_nvdla2csb = Module(new NV_NVDLA_CSB_MASTER_csb2falcon_fifo)
// //clk&pwr_ram
// u_fifo_nvdla2csb.io.wr_clk := io.nvdla_core_clk
// u_fifo_nvdla2csb.io.rd_clk := io.nvdla_falcon_clk
// u_fifo_nvdla2csb.io.pwrbus_ram_pd := io.pwrbus_ram_pd
// //wr
// u_fifo_nvdla2csb.io.wr_req := core_resp_pvld
// u_fifo_nvdla2csb.io.wr_data := core_resp_pd
// val core_resp_prdy = u_fifo_nvdla2csb.io.wr_ready
// //rd
// val nvdla2csb_resp_pvld = u_fifo_nvdla2csb.io.rd_req
// val nvdla2csb_resp_pd = u_fifo_nvdla2csb.io.rd_data
// u_fifo_nvdla2csb.io.rd_ready := true.B

// val nvdla2csb_rresp_rdat = nvdla2csb_resp_pd
// val nvdla2csb_rresp_is_valid = nvdla2csb_resp_pvld  && (nvdla2csb_resp_pd(33) === false.B)
// val nvdla2csb_wresp_is_valid = nvdla2csb_resp_pvld && (nvdla2csb_resp_pd(33) === true.B)

// io.nvdla2csb_valid := withClock(io.nvdla_falcon_clk){RegNext(nvdla2csb_rresp_is_valid, false.B)}
// io.nvdla2csb_data := withClock(io.nvdla_falcon_clk){RegEnable(nvdla2csb_rresp_rdat, "b0".asUInt(32.W), nvdla2csb_rresp_is_valid)}
// io.nvdla2csb_wr_complete := withClock(io.nvdla_falcon_clk){RegNext(nvdla2csb_wresp_is_valid, false.B)}

// ////////////////////////////////////////////////////////////////////////
// // Distribute request and gather response                             //
// ////////////////////////////////////////////////////////////////////////
// val core_req_addr = core_req_pd(15, 0)
// val core_req_write = core_req_pd(48)
// val core_req_nposted   = core_req_pd(49)
// val core_req_pop_valid = core_req_pvld & core_req_prdy

// //core_req_addr is word aligned while address from arnvdla is byte aligned.
// val core_byte_addr = Cat(core_req_addr, "b0".asUInt(2.W))
// val core_req_pd_d1 = RegEnable(core_req_pd, core_req_pvld & core_req_prdy)

// val addr_mask = Cat(Fill(6, true.B), Fill(12, false.B))
// //////////////// for CFGROM ////////////////
// val cfgrom_req_pvld = RegInit(false.B)
// val csb2cfgrom_req_pvld_out = RegInit(false.B)
// val csb2cfgrom_req_pd_tmp = Reg(UInt(50.W))
// val cfgrom_resp_valid = RegInit(false.B)
// val cfgrom_resp_pd = Reg(UInt(34.W))

// val select_cfgrom = ((core_byte_addr & addr_mask) === "h00000000".asUInt(32.W))
// val cfgrom_req_pvld_w = Mux(core_req_pop_valid & select_cfgrom, true.B,
//                         Mux((io.csb2cfgrom_req_prdy | ~csb2cfgrom_req_pvld_out), false.B,
//                         cfgrom_req_pvld))
// val csb2cfgrom_req_pvld_w = Mux(cfgrom_req_pvld, true.B, 
//                             Mux(io.csb2cfgrom_req_prdy, false.B,
//                             csb2cfgrom_req_pvld_out))
// val csb2cfgrom_req_en = cfgrom_req_pvld & (io.csb2cfgrom_req_prdy | ~csb2cfgrom_req_pvld_out)    

// cfgrom_req_pvld := cfgrom_req_pvld_w
// csb2cfgrom_req_pvld_out := csb2cfgrom_req_pvld_w
// when(csb2cfgrom_req_en){
//     csb2cfgrom_req_pd_tmp := core_req_pd_d1
// }
// io.csb2cfgrom_req_pd := Cat("b0".asUInt(7.W), csb2cfgrom_req_pd_tmp(49, 16), "b0".asUInt(6.W), csb2cfgrom_req_pd_tmp(15, 0))
// cfgrom_resp_valid := io.cfgrom2csb_resp_valid
// when(io.cfgrom2csb_resp_valid){
//     cfgrom_resp_pd := io.cfgrom2csb_resp_pd
// }

// io.csb2cfgrom_req_pvld := csb2cfgrom_req_pvld_out

// //////////////// for GLB ////////////////
// val glb_req_pvld = RegInit(false.B)
// val csb2glb_req_pvld_out = RegInit(false.B)
// val csb2glb_req_pd_tmp = Reg(UInt(50.W))
// val glb_resp_valid = RegInit(false.B)
// val glb_resp_pd = Reg(UInt(34.W))

// val select_glb = ((core_byte_addr & addr_mask) === "h00001000".asUInt(32.W))
// val glb_req_pvld_w = Mux(core_req_pop_valid & select_glb, true.B,
//                         Mux((io.csb2glb_req_prdy | ~csb2glb_req_pvld_out), false.B,
//                         glb_req_pvld))
// val csb2glb_req_pvld_w = Mux(glb_req_pvld, true.B, 
//                             Mux(io.csb2glb_req_prdy, false.B,
//                             csb2glb_req_pvld_out))
// val csb2glb_req_en = glb_req_pvld & (io.csb2glb_req_prdy | ~csb2glb_req_pvld_out)    

// glb_req_pvld := glb_req_pvld_w
// csb2glb_req_pvld_out := csb2glb_req_pvld_w
// when(csb2glb_req_en){
//     csb2glb_req_pd_tmp := core_req_pd_d1
// }
// io.csb2glb_req_pd := Cat("b0".asUInt(7.W), csb2glb_req_pd_tmp(49, 16), "b0".asUInt(6.W), csb2glb_req_pd_tmp(15, 0))
// glb_resp_valid := io.glb2csb_resp_valid
// when(io.glb2csb_resp_valid){
//     glb_resp_pd := io.glb2csb_resp_pd
// }

// io.csb2glb_req_pvld := csb2glb_req_pvld_out

// //////////////// for MCIF ////////////////
// val mcif_req_pvld = RegInit(false.B)
// val csb2mcif_req_pvld_out = RegInit(false.B)
// val csb2mcif_req_pd_tmp = Reg(UInt(50.W))
// val mcif_resp_valid = RegInit(false.B)
// val mcif_resp_pd = Reg(UInt(34.W))

// val select_mcif = ((core_byte_addr & addr_mask) === "h00002000".asUInt(32.W))
// val mcif_req_pvld_w = Mux(core_req_pop_valid & select_mcif, true.B,
//                         Mux((io.csb2mcif_req_prdy | ~csb2mcif_req_pvld_out), false.B,
//                         mcif_req_pvld))
// val csb2mcif_req_pvld_w = Mux(mcif_req_pvld, true.B, 
//                             Mux(io.csb2mcif_req_prdy, false.B,
//                             csb2mcif_req_pvld_out))
// val csb2mcif_req_en = mcif_req_pvld & (io.csb2mcif_req_prdy | ~csb2mcif_req_pvld_out)    

// mcif_req_pvld := mcif_req_pvld_w
// csb2mcif_req_pvld_out := csb2mcif_req_pvld_w
// when(csb2mcif_req_en){
//     csb2mcif_req_pd_tmp := core_req_pd_d1
// }
// io.csb2mcif_req_pd := Cat("b0".asUInt(7.W), csb2mcif_req_pd_tmp(49, 16), "b0".asUInt(6.W), csb2mcif_req_pd_tmp(15, 0))
// mcif_resp_valid := io.mcif2csb_resp_valid
// when(io.mcif2csb_resp_valid){
//     mcif_resp_pd := io.mcif2csb_resp_pd
// }

// io.csb2mcif_req_pvld :=  csb2mcif_req_pvld_out

// //////////////// for CVIF ////////////////
// val cvif_req_pvld = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){Some(RegInit(false.B))} else None
// val cvif_resp_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){Some(RegInit(false.B))} else None
// val cvif_resp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){Some(Reg(UInt(34.W)))} else None
// val select_cvif = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE)
//                  ((core_byte_addr & addr_mask) === "h0000f000".asUInt(32.W))
//                  else
//                  false.B
// if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
// val csb2cvif_req_pvld_out = RegInit(false.B)
// val csb2cvif_req_pd_tmp = Reg(UInt(50.W))

// val cvif_req_pvld_w = Mux(core_req_pop_valid & select_cvif, true.B,
//                         Mux((io.csb2cvif_req_prdy.get | ~csb2cvif_req_pvld_out), false.B,
//                         cvif_req_pvld.get))
// val csb2cvif_req_pvld_w = Mux(cvif_req_pvld.get, true.B, 
//                             Mux(io.csb2cvif_req_prdy.get, false.B,
//                             csb2cvif_req_pvld_out))
// val csb2cvif_req_en = cvif_req_pvld.get & (io.csb2cvif_req_prdy.get | ~csb2cvif_req_pvld_out)    

// cvif_req_pvld.get := cvif_req_pvld_w
// csb2cvif_req_pvld_out := csb2cvif_req_pvld_w
// when(csb2cvif_req_en){
//     csb2cvif_req_pd_tmp := core_req_pd_d1
// }
// io.csb2cvif_req_pd.get := Cat("b0".asUInt(7.W), csb2cvif_req_pd_tmp(49, 16), "b0".asUInt(6.W), csb2cvif_req_pd_tmp(15, 0))
// cvif_resp_valid.get := io.cvif2csb_resp_valid.get
// when(io.cvif2csb_resp_valid.get){
//     cvif_resp_pd.get := io.cvif2csb_resp_pd.get
// }

// io.csb2cvif_req_pvld.get := csb2cvif_req_pvld_out
// }

// //////////////// for BDMA ////////////////
// val bdma_req_pvld = if(conf.NVDLA_BDMA_ENABLE){Some(RegInit(false.B))} else None
// val bdma_resp_valid = if(conf.NVDLA_BDMA_ENABLE){Some(RegInit(false.B))} else None
// val bdma_resp_pd = if(conf.NVDLA_BDMA_ENABLE){Some(Reg(UInt(34.W)))} else None
// val select_bdma = if(conf.NVDLA_BDMA_ENABLE)
//                   (core_byte_addr & addr_mask) === "h00010000".asUInt(32.W)
//                   else
//                   false.B

// if(conf.NVDLA_BDMA_ENABLE){
// val csb2bdma_req_pvld_out = RegInit(false.B)
// val csb2bdma_req_pd_tmp = Reg(UInt(50.W))

// val bdma_req_pvld_w = Mux(core_req_pop_valid & select_bdma, true.B,
//                         Mux((io.csb2bdma_req_prdy.get | ~csb2bdma_req_pvld_out), false.B,
//                         bdma_req_pvld.get))
// val csb2bdma_req_pvld_w = Mux(bdma_req_pvld.get, true.B, 
//                             Mux(io.csb2bdma_req_prdy.get, false.B,
//                             csb2bdma_req_pvld_out))
// val csb2bdma_req_en = bdma_req_pvld.get & (io.csb2bdma_req_prdy.get | ~csb2bdma_req_pvld_out)    

// bdma_req_pvld.get := bdma_req_pvld_w
// csb2bdma_req_pvld_out := csb2bdma_req_pvld_w
// when(csb2bdma_req_en){
//     csb2bdma_req_pd_tmp := core_req_pd_d1
// }
// io.csb2bdma_req_pd.get := Cat("b0".asUInt(7.W), csb2bdma_req_pd_tmp(49, 16), "b0".asUInt(6.W), csb2bdma_req_pd_tmp(15, 0))
// bdma_resp_valid.get := io.bdma2csb_resp_valid.get
// when(io.bdma2csb_resp_valid.get){
//     bdma_resp_pd.get := io.bdma2csb_resp_pd.get
// }
// }

// //////////////// for CDMA ////////////////
// val cdma_req_pvld = RegInit(false.B)
// val csb2cdma_req_pvld_out = RegInit(false.B)
// val csb2cdma_req_pd_tmp = Reg(UInt(50.W))
// val cdma_resp_valid = RegInit(false.B)
// val cdma_resp_pd = Reg(UInt(34.W))

// val select_cdma = ((core_byte_addr & addr_mask) === "h00003000".asUInt(32.W))
// val cdma_req_pvld_w = Mux(core_req_pop_valid & select_cdma, true.B,
//                         Mux((io.csb2cdma_req_prdy | ~csb2cdma_req_pvld_out), false.B,
//                         cdma_req_pvld))
// val csb2cdma_req_pvld_w = Mux(cdma_req_pvld, true.B, 
//                             Mux(io.csb2cdma_req_prdy, false.B,
//                             csb2cdma_req_pvld_out))
// val csb2cdma_req_en = cdma_req_pvld & (io.csb2cdma_req_prdy | ~csb2cdma_req_pvld_out)    

// cdma_req_pvld := cdma_req_pvld_w
// csb2cdma_req_pvld_out := csb2cdma_req_pvld_w
// when(csb2cdma_req_en){
//     csb2cdma_req_pd_tmp := core_req_pd_d1
// }
// io.csb2cdma_req_pd := Cat("b0".asUInt(7.W), csb2cdma_req_pd_tmp(49, 16), "b0".asUInt(6.W), csb2cdma_req_pd_tmp(15, 0))
// cdma_resp_valid := io.cdma2csb_resp_valid
// when(io.cdma2csb_resp_valid){
//     cdma_resp_pd := io.cdma2csb_resp_pd
// }

// io.csb2cdma_req_pvld := csb2cdma_req_pvld_out

// //////////////// for CSC ////////////////
// val csc_req_pvld = RegInit(false.B)
// val csb2csc_req_pvld_out = RegInit(false.B)
// val csb2csc_req_pd_tmp = Reg(UInt(50.W))
// val csc_resp_valid = RegInit(false.B)
// val csc_resp_pd = Reg(UInt(34.W))

// val select_csc = ((core_byte_addr & addr_mask) === "h00004000".asUInt(32.W))
// val csc_req_pvld_w = Mux(core_req_pop_valid & select_csc, true.B,
//                         Mux((io.csb2csc_req_prdy | ~csb2csc_req_pvld_out), false.B,
//                         csc_req_pvld))
// val csb2csc_req_pvld_w = Mux(csc_req_pvld, true.B, 
//                             Mux(io.csb2csc_req_prdy, false.B,
//                             csb2csc_req_pvld_out))
// val csb2csc_req_en = csc_req_pvld & (io.csb2csc_req_prdy | ~csb2csc_req_pvld_out)    

// csc_req_pvld := csc_req_pvld_w
// csb2csc_req_pvld_out := csb2csc_req_pvld_w
// when(csb2csc_req_en){
//     csb2csc_req_pd_tmp := core_req_pd_d1
// }
// io.csb2csc_req_pd := Cat("b0".asUInt(7.W), csb2csc_req_pd_tmp(49, 16), "b0".asUInt(6.W), csb2csc_req_pd_tmp(15, 0))
// csc_resp_valid := io.csc2csb_resp_valid
// when(io.csc2csb_resp_valid){
//     csc_resp_pd := io.csc2csb_resp_pd
// }

// io.csb2csc_req_pvld := csb2csc_req_pvld_out

// //////////////// for CMAC_A ////////////////
// val cmac_a_req_pvld = RegInit(false.B)
// val csb2cmac_a_req_pvld_out = RegInit(false.B)
// val csb2cmac_a_req_pd_tmp = Reg(UInt(50.W))
// val cmac_a_resp_valid = RegInit(false.B)
// val cmac_a_resp_pd = Reg(UInt(34.W))

// val select_cmac_a = ((core_byte_addr & addr_mask) === "h00005000".asUInt(32.W))
// val cmac_a_req_pvld_w = Mux(core_req_pop_valid & select_cmac_a, true.B,
//                         Mux((io.csb2cmac_a_req_prdy | ~csb2cmac_a_req_pvld_out), false.B,
//                         cmac_a_req_pvld))
// val csb2cmac_a_req_pvld_w = Mux(cmac_a_req_pvld, true.B, 
//                             Mux(io.csb2cmac_a_req_prdy, false.B,
//                             csb2cmac_a_req_pvld_out))
// val csb2cmac_a_req_en = cmac_a_req_pvld & (io.csb2cmac_a_req_prdy | ~csb2cmac_a_req_pvld_out)    

// cmac_a_req_pvld := cmac_a_req_pvld_w
// csb2cmac_a_req_pvld_out := csb2cmac_a_req_pvld_w
// when(csb2cmac_a_req_en){
//     csb2cmac_a_req_pd_tmp := core_req_pd_d1
// }
// io.csb2cmac_a_req_pd := Cat("b0".asUInt(7.W), csb2cmac_a_req_pd_tmp(49, 16), "b0".asUInt(6.W), csb2cmac_a_req_pd_tmp(15, 0))
// cmac_a_resp_valid := io.cmac_a2csb_resp_valid
// when(io.cmac_a2csb_resp_valid){
//     cmac_a_resp_pd := io.cmac_a2csb_resp_pd
// }

// io.csb2cmac_a_req_pvld := csb2cmac_a_req_pvld_out

// //////////////// for CMAC_B ////////////////
// val cmac_b_req_pvld = RegInit(false.B)
// val csb2cmac_b_req_pvld_out = RegInit(false.B)
// val csb2cmac_b_req_pd_tmp = Reg(UInt(50.W))
// val cmac_b_resp_valid = RegInit(false.B)
// val cmac_b_resp_pd = Reg(UInt(34.W))

// val select_cmac_b = ((core_byte_addr & addr_mask) === "h00006000".asUInt(32.W))
// val cmac_b_req_pvld_w = Mux(core_req_pop_valid & select_cmac_b, true.B,
//                         Mux((io.csb2cmac_b_req_prdy | ~csb2cmac_b_req_pvld_out), false.B,
//                         cmac_b_req_pvld))
// val csb2cmac_b_req_pvld_w = Mux(cmac_b_req_pvld, true.B, 
//                             Mux(io.csb2cmac_b_req_prdy, false.B,
//                             csb2cmac_b_req_pvld_out))
// val csb2cmac_b_req_en = cmac_b_req_pvld & (io.csb2cmac_b_req_prdy | ~csb2cmac_b_req_pvld_out)    

// cmac_b_req_pvld := cmac_b_req_pvld_w
// csb2cmac_b_req_pvld_out := csb2cmac_b_req_pvld_w
// when(csb2cmac_b_req_en){
//     csb2cmac_b_req_pd_tmp := core_req_pd_d1
// }
// io.csb2cmac_b_req_pd := Cat("b0".asUInt(7.W), csb2cmac_b_req_pd_tmp(49, 16), "b0".asUInt(6.W), csb2cmac_b_req_pd_tmp(15, 0))
// cmac_b_resp_valid := io.cmac_b2csb_resp_valid
// when(io.cmac_b2csb_resp_valid){
//     cmac_b_resp_pd := io.cmac_b2csb_resp_pd
// }

// io.csb2cmac_b_req_pvld := csb2cmac_b_req_pvld_out

// //////////////// for CACC////////////////
// val cacc_req_pvld = RegInit(false.B)
// val csb2cacc_req_pvld_out = RegInit(false.B)
// val csb2cacc_req_pd_tmp = Reg(UInt(50.W))
// val cacc_resp_valid = RegInit(false.B)
// val cacc_resp_pd = Reg(UInt(34.W))

// val select_cacc = ((core_byte_addr & addr_mask) === "h00007000".asUInt(32.W))
// val cacc_req_pvld_w = Mux(core_req_pop_valid & select_cacc, true.B,
//                         Mux((io.csb2cacc_req_prdy | ~csb2cacc_req_pvld_out), false.B,
//                         cacc_req_pvld))
// val csb2cacc_req_pvld_w = Mux(cacc_req_pvld, true.B, 
//                             Mux(io.csb2cacc_req_prdy, false.B,
//                             csb2cacc_req_pvld_out))
// val csb2cacc_req_en = cacc_req_pvld & (io.csb2cacc_req_prdy | ~csb2cacc_req_pvld_out)    

// cacc_req_pvld := cacc_req_pvld_w
// csb2cacc_req_pvld_out := csb2cacc_req_pvld_w
// when(csb2cacc_req_en){
//     csb2cacc_req_pd_tmp := core_req_pd_d1
// }
// io.csb2cacc_req_pd := Cat("b0".asUInt(7.W), csb2cacc_req_pd_tmp(49, 16), "b0".asUInt(6.W), csb2cacc_req_pd_tmp(15, 0))
// cacc_resp_valid := io.cacc2csb_resp_valid
// when(io.cacc2csb_resp_valid){
//     cacc_resp_pd := io.cacc2csb_resp_pd
// }

// io.csb2cacc_req_pvld := csb2cacc_req_pvld_out

// //////////////// for SDP_RDMA////////////////
// val sdp_rdma_req_pvld = RegInit(false.B)
// val csb2sdp_rdma_req_pvld_out = RegInit(false.B)
// val csb2sdp_rdma_req_pd_tmp = Reg(UInt(50.W))
// val sdp_rdma_resp_valid = RegInit(false.B)
// val sdp_rdma_resp_pd = Reg(UInt(34.W))

// val select_sdp_rdma = ((core_byte_addr & addr_mask) === "h00008000".asUInt(32.W))
// val sdp_rdma_req_pvld_w = Mux(core_req_pop_valid & select_sdp_rdma, true.B,
//                         Mux((io.csb2sdp_rdma_req_prdy | ~csb2sdp_rdma_req_pvld_out), false.B,
//                         sdp_rdma_req_pvld))
// val csb2sdp_rdma_req_pvld_w = Mux(sdp_rdma_req_pvld, true.B, 
//                             Mux(io.csb2sdp_rdma_req_prdy, false.B,
//                             csb2sdp_rdma_req_pvld_out))
// val csb2sdp_rdma_req_en = sdp_rdma_req_pvld & (io.csb2sdp_rdma_req_prdy | ~csb2sdp_rdma_req_pvld_out)    

// sdp_rdma_req_pvld := sdp_rdma_req_pvld_w
// csb2sdp_rdma_req_pvld_out := csb2sdp_rdma_req_pvld_w
// when(csb2sdp_rdma_req_en){
//     csb2sdp_rdma_req_pd_tmp := core_req_pd_d1
// }
// io.csb2sdp_rdma_req_pd := Cat("b0".asUInt(7.W), csb2sdp_rdma_req_pd_tmp(49, 16), "b0".asUInt(6.W), csb2sdp_rdma_req_pd_tmp(15, 0))
// sdp_rdma_resp_valid := io.sdp_rdma2csb_resp_valid
// when(io.sdp_rdma2csb_resp_valid){
//     sdp_rdma_resp_pd := io.sdp_rdma2csb_resp_pd
// }

// io.csb2sdp_rdma_req_pvld := csb2sdp_rdma_req_pvld_out

// //////////////// for SDP ////////////////
// val sdp_req_pvld = RegInit(false.B)
// val csb2sdp_req_pvld_out = RegInit(false.B)
// val csb2sdp_req_pd_tmp = Reg(UInt(50.W))
// val sdp_resp_valid = RegInit(false.B)
// val sdp_resp_pd = Reg(UInt(34.W))

// val select_sdp = ((core_byte_addr & addr_mask) === "h00009000".asUInt(32.W))
// val sdp_req_pvld_w = Mux(core_req_pop_valid & select_sdp, true.B,
//                         Mux((io.csb2sdp_req_prdy | ~csb2sdp_req_pvld_out), false.B,
//                         sdp_req_pvld))
// val csb2sdp_req_pvld_w = Mux(sdp_req_pvld, true.B, 
//                             Mux(io.csb2sdp_req_prdy, false.B,
//                             csb2sdp_req_pvld_out))
// val csb2sdp_req_en = sdp_req_pvld & (io.csb2sdp_req_prdy | ~csb2sdp_req_pvld_out)    

// sdp_req_pvld := sdp_req_pvld_w
// csb2sdp_req_pvld_out := csb2sdp_req_pvld_w
// when(csb2sdp_req_en){
//     csb2sdp_req_pd_tmp := core_req_pd_d1
// }
// io.csb2sdp_req_pd := Cat("b0".asUInt(7.W), csb2sdp_req_pd_tmp(49, 16), "b0".asUInt(6.W), csb2sdp_req_pd_tmp(15, 0))
// sdp_resp_valid := io.sdp2csb_resp_valid
// when(io.sdp2csb_resp_valid){
//     sdp_resp_pd := io.sdp2csb_resp_pd
// }

// io.csb2sdp_req_pvld := csb2sdp_req_pvld_out

// //////////////// for PDP_RDMA ////////////////
// val pdp_rdma_req_pvld = if(conf.NVDLA_PDP_ENABLE){Some(RegInit(false.B))} else None
// val pdp_rdma_resp_valid = if(conf.NVDLA_PDP_ENABLE){Some(RegInit(false.B))} else None
// val pdp_rdma_resp_pd = if(conf.NVDLA_PDP_ENABLE){Some(Reg(UInt(34.W)))} else None
// val select_pdp_rdma = false.B
// val select_pdp = false.B

// if(conf.NVDLA_PDP_ENABLE){
// val csb2pdp_rdma_req_pvld_out = RegInit(false.B)
// val csb2pdp_rdma_req_pd_tmp = Reg(UInt(50.W))

// val pdp_rdma_req_pvld_w = Mux(core_req_pop_valid & select_pdp_rdma, true.B,
//                         Mux((io.csb2pdp_rdma_req_prdy.get | ~csb2pdp_rdma_req_pvld_out), false.B,
//                         pdp_rdma_req_pvld.get))
// val csb2pdp_rdma_req_pvld_w = Mux(pdp_rdma_req_pvld.get, true.B, 
//                             Mux(io.csb2pdp_rdma_req_prdy.get, false.B,
//                             csb2pdp_rdma_req_pvld_out))
// val csb2pdp_rdma_req_en = pdp_rdma_req_pvld.get & (io.csb2pdp_rdma_req_prdy.get | ~csb2pdp_rdma_req_pvld_out)    

// pdp_rdma_req_pvld.get := pdp_rdma_req_pvld_w
// csb2pdp_rdma_req_pvld_out := csb2pdp_rdma_req_pvld_w
// when(csb2pdp_rdma_req_en){
//     csb2pdp_rdma_req_pd_tmp := core_req_pd_d1
// }
// io.csb2pdp_rdma_req_pd.get := Cat("b0".asUInt(7.W), csb2pdp_rdma_req_pd_tmp(49, 16), "b0".asUInt(6.W), csb2pdp_rdma_req_pd_tmp(15, 0))
// pdp_rdma_resp_valid.get := io.pdp_rdma2csb_resp_valid.get
// when(io.pdp_rdma2csb_resp_valid.get){
//     pdp_rdma_resp_pd.get := io.pdp_rdma2csb_resp_pd.get
// }
// io.csb2pdp_rdma_req_pvld.get := csb2pdp_rdma_req_pvld_out
// }

// //////////////// for PDP ////////////////
// val pdp_req_pvld = if(conf.NVDLA_PDP_ENABLE){Some(RegInit(false.B))} else None
// val pdp_resp_valid = if(conf.NVDLA_PDP_ENABLE){Some(RegInit(false.B))} else None
// val pdp_resp_pd = if(conf.NVDLA_PDP_ENABLE){Some(Reg(UInt(34.W)))} else None

// if(conf.NVDLA_PDP_ENABLE){
// val csb2pdp_req_pvld_out = RegInit(false.B)
// val csb2pdp_req_pd_tmp = Reg(UInt(50.W))

// val pdp_req_pvld_w = Mux(core_req_pop_valid & select_pdp, true.B,
//                         Mux(io.csb2pdp_req_prdy.get | ~csb2pdp_req_pvld_out, false.B,
//                         pdp_req_pvld.get))
// val csb2pdp_req_pvld_w = Mux(pdp_req_pvld.get, true.B, 
//                             Mux(io.csb2pdp_req_prdy.get, false.B,
//                             csb2pdp_req_pvld_out))
// val csb2pdp_req_en = pdp_req_pvld.get & (io.csb2pdp_req_prdy.get | ~csb2pdp_req_pvld_out)    

// pdp_req_pvld.get := pdp_req_pvld_w
// csb2pdp_req_pvld_out := csb2pdp_req_pvld_w
// when(csb2pdp_req_en){
//     csb2pdp_req_pd_tmp := core_req_pd_d1
// }
// io.csb2pdp_req_pd.get := Cat("b0".asUInt(7.W), csb2pdp_req_pd_tmp(49, 16), "b0".asUInt(6.W), csb2pdp_req_pd_tmp(15, 0))
// pdp_resp_valid.get := io.pdp2csb_resp_valid.get
// when(io.pdp2csb_resp_valid.get){
//     pdp_resp_pd.get := io.pdp2csb_resp_pd.get
// }
// io.csb2pdp_req_pvld.get := csb2pdp_req_pvld_out
// }

// //////////////// for CDP_RDMA ////////////////
// val cdp_rdma_req_pvld = if(conf.NVDLA_CDP_ENABLE){Some(RegInit(false.B))} else None
// val cdp_rdma_resp_valid = if(conf.NVDLA_CDP_ENABLE){Some(RegInit(false.B))} else None
// val cdp_rdma_resp_pd = if(conf.NVDLA_CDP_ENABLE){Some(Reg(UInt(34.W)))} else None
// val select_cdp_rdma = false.B
// val select_cdp = false.B

// if(conf.NVDLA_CDP_ENABLE){
// val csb2cdp_rdma_req_pvld_out = RegInit(false.B)
// val csb2cdp_rdma_req_pd_tmp = Reg(UInt(50.W))

// val cdp_rdma_req_pvld_w = Mux(core_req_pop_valid & select_cdp_rdma, true.B,
//                         Mux((io.csb2cdp_rdma_req_prdy.get | ~csb2cdp_rdma_req_pvld_out), false.B,
//                         cdp_rdma_req_pvld.get))
// val csb2cdp_rdma_req_pvld_w = Mux(cdp_rdma_req_pvld.get, true.B, 
//                             Mux(io.csb2cdp_rdma_req_prdy.get, false.B,
//                             csb2cdp_rdma_req_pvld_out))
// val csb2cdp_rdma_req_en = cdp_rdma_req_pvld.get & (io.csb2cdp_rdma_req_prdy.get | ~csb2cdp_rdma_req_pvld_out)    

// cdp_rdma_req_pvld.get := cdp_rdma_req_pvld_w
// csb2cdp_rdma_req_pvld_out := csb2cdp_rdma_req_pvld_w
// when(csb2cdp_rdma_req_en){
//     csb2cdp_rdma_req_pd_tmp := core_req_pd_d1
// }
// io.csb2cdp_rdma_req_pd.get := Cat("b0".asUInt(7.W), csb2cdp_rdma_req_pd_tmp(49, 16), "b0".asUInt(6.W), csb2cdp_rdma_req_pd_tmp(15, 0))
// when(io.cdp_rdma2csb_resp_valid.get){
//     cdp_rdma_resp_pd.get := io.cdp_rdma2csb_resp_pd.get
// }

// io.csb2cdp_rdma_req_pvld.get := csb2cdp_rdma_req_pvld_out
// }

// //////////////// for CDP ////////////////
// val cdp_req_pvld = if(conf.NVDLA_CDP_ENABLE){Some(RegInit(false.B))} else None
// val cdp_resp_valid = if(conf.NVDLA_CDP_ENABLE){Some(RegInit(false.B))} else None
// val cdp_resp_pd = if(conf.NVDLA_CDP_ENABLE){Some(Reg(UInt(34.W)))} else None



// if(conf.NVDLA_CDP_ENABLE){
// val csb2cdp_req_pvld_out = RegInit(false.B)
// val csb2cdp_req_pd_tmp = Reg(UInt(50.W))

// val cdp_req_pvld_w = Mux(core_req_pop_valid & select_cdp, true.B,
//                         Mux(io.csb2cdp_req_prdy.get | ~csb2cdp_req_pvld_out, false.B,
//                         cdp_req_pvld.get))
// val csb2cdp_req_pvld_w = Mux(cdp_req_pvld.get, true.B, 
//                             Mux(io.csb2cdp_req_prdy.get, false.B,
//                             csb2cdp_req_pvld_out))
// val csb2cdp_req_en = cdp_req_pvld.get & (io.csb2cdp_req_prdy.get | ~csb2cdp_req_pvld_out)    

// cdp_req_pvld.get := cdp_req_pvld_w
// csb2cdp_req_pvld_out := csb2cdp_req_pvld_w
// when(csb2cdp_req_en){
//     csb2cdp_req_pd_tmp := core_req_pd_d1
// }
// io.csb2cdp_req_pd.get := Cat("b0".asUInt(7.W), csb2cdp_req_pd_tmp(49, 16), "b0".asUInt(6.W), csb2cdp_req_pd_tmp(15, 0))
// cdp_resp_valid.get := io.cdp2csb_resp_valid.get
// when(io.cdp2csb_resp_valid.get){
//     cdp_resp_pd.get := io.cdp2csb_resp_pd.get
// }
// io.csb2cdp_req_pvld.get := csb2cdp_req_pvld_out
// }



// //////////////// for RUBIK ////////////////
// val rbk_req_pvld = if(conf.NVDLA_RUBIK_ENABLE){Some(RegInit(false.B))} else None
// val rbk_resp_valid = if(conf.NVDLA_RUBIK_ENABLE){Some(RegInit(false.B))} else None
// val rbk_resp_pd = if(conf.NVDLA_RUBIK_ENABLE){Some(Reg(UInt(34.W)))} else None
// val select_rbk = false.B

// if(conf.NVDLA_RUBIK_ENABLE){
// val csb2rbk_req_pvld_out = RegInit(false.B)
// val csb2rbk_req_pd_tmp = Reg(UInt(50.W))

// val rbk_req_pvld_w = Mux(core_req_pop_valid & select_rbk, true.B,
//                         Mux((io.csb2rbk_req_prdy.get | ~csb2rbk_req_pvld_out), false.B,
//                         rbk_req_pvld.get))
// val csb2rbk_req_pvld_w = Mux(rbk_req_pvld.get, true.B, 
//                             Mux(io.csb2rbk_req_prdy.get, false.B,
//                             csb2rbk_req_pvld_out))
// val csb2rbk_req_en = rbk_req_pvld.get & (io.csb2rbk_req_prdy.get | ~csb2rbk_req_pvld_out)    

// rbk_req_pvld.get := rbk_req_pvld_w
// csb2rbk_req_pvld_out := csb2rbk_req_pvld_w
// when(csb2rbk_req_en){
//     csb2rbk_req_pd_tmp := core_req_pd_d1
// }
// io.csb2rbk_req_pd.get := Cat("b0".asUInt(7.W), csb2rbk_req_pd_tmp(49, 16), "b0".asUInt(6.W), csb2rbk_req_pd_tmp(15, 0))
// rbk_resp_valid.get := io.rbk2csb_resp_valid.get
// when(io.rbk2csb_resp_valid.get){
//     rbk_resp_pd.get := io.rbk2csb_resp_pd.get
// }
// }

// //////////////// for DUMMY ////////////////
// ////////////////// dummy client //////////////////////
// val csb2dummy_req_pvld = RegInit(false.B)
// val csb2dummy_req_nposted = Reg(Bool())
// val csb2dummy_req_read = Reg(Bool())

// val select_dummy = ~(select_cfgrom
//                       | select_glb
//                       | select_mcif
//                       | select_cvif
//                       | select_bdma
//                       | select_cdma
//                       | select_csc
//                       | select_cmac_a
//                       | select_cmac_b
//                       | select_cacc
//                       | select_sdp_rdma
//                       | select_sdp
//                       | select_pdp_rdma
//                       | select_pdp
//                       | select_cdp
//                       | select_cdp_rdma
//                       | select_rbk)

// val dummy_req_pvld_w = (core_req_pop_valid & select_dummy)
// csb2dummy_req_pvld := dummy_req_pvld_w
// when(dummy_req_pvld_w){
//     csb2dummy_req_nposted := core_req_nposted
// }
// when(dummy_req_pvld_w){
//     csb2dummy_req_read := ~core_req_write
// }

// val dummy_resp_rdat = Fill(32, false.B)
// val dummy_resp_error = false.B
// val dummy_rresp_pd = Cat(false.B, dummy_resp_rdat, dummy_resp_rdat(31,0))    /* PKT_nvdla_xx2csb_resp_dla_xx2csb_rd_erpt_ID  */
// val dummy_wresp_pd = Cat(true.B, dummy_resp_rdat, dummy_resp_rdat(31,0))    /* PKT_nvdla_xx2csb_resp_dla_xx2csb_wr_erpt_ID  */

// val dummy_resp_valid_w = csb2dummy_req_pvld & (csb2dummy_req_nposted | csb2dummy_req_read)
// val dummy_resp_type_w = ~csb2dummy_req_read & csb2dummy_req_nposted
// val dummy_resp_type = RegEnable(dummy_resp_type_w, false.B, dummy_resp_valid_w)
// val dummy_resp_pd = Mux(dummy_resp_type, dummy_wresp_pd, dummy_rresp_pd)

// val dummy_resp_valid = RegNext(dummy_resp_valid_w, false.B)


// //////////////// ass3mble ////////////////

// val cvif_resp_valid_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Fill(34, cvif_resp_valid.get)&cvif_resp_pd.get else Fill(34, false.B)                     
// val cvif_resp_valid_get = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) cvif_resp_valid.get else false.B

// val bdma_resp_valid_pd = if(conf.NVDLA_BDMA_ENABLE) Fill(34, bdma_resp_valid.get)&bdma_resp_pd.get else Fill(34, false.B)
// val bdma_resp_valid_get = if(conf.NVDLA_BDMA_ENABLE) bdma_resp_valid.get else false.B

// val pdp_rdma_resp_valid_pd = if(conf.NVDLA_PDP_ENABLE) Fill(34, pdp_rdma_resp_valid.get)&pdp_rdma_resp_pd.get else Fill(34, false.B)
// val pdp_rdma_resp_valid_get = if(conf.NVDLA_PDP_ENABLE)  pdp_rdma_resp_valid.get else false.B

// val pdp_resp_valid_pd = if(conf.NVDLA_PDP_ENABLE) Fill(34, pdp_resp_valid.get)&pdp_resp_pd.get else Fill(34, false.B)
// val pdp_resp_valid_get = if(conf.NVDLA_PDP_ENABLE) pdp_resp_valid.get else false.B

// val cdp_rdma_resp_valid_pd = if(conf.NVDLA_CDP_ENABLE) Fill(34, cdp_rdma_resp_valid.get)&cdp_rdma_resp_pd.get else Fill(34, false.B)
// val cdp_rdma_resp_valid_get = if(conf.NVDLA_CDP_ENABLE)  cdp_rdma_resp_valid.get else false.B

// val cdp_resp_valid_pd = if(conf.NVDLA_CDP_ENABLE) Fill(34, cdp_resp_valid.get)&cdp_resp_pd.get else Fill(34, false.B)
// val cdp_resp_valid_get = if(conf.NVDLA_CDP_ENABLE) cdp_resp_valid.get else false.B

// val rbk_resp_valid_pd = if(conf.NVDLA_RUBIK_ENABLE) Fill(34, rbk_resp_valid.get)&rbk_resp_pd.get else Fill(34, false.B)
// val rbk_resp_valid_get = if(conf.NVDLA_RUBIK_ENABLE) rbk_resp_valid.get else false.B


// core_resp_pd := (Fill(34, cfgrom_resp_valid) & cfgrom_resp_pd)|
//                       (Fill(34, glb_resp_valid) & glb_resp_pd)|
//                       (Fill(34, mcif_resp_valid) & mcif_resp_pd)|
//                       cvif_resp_valid_pd|
//                       bdma_resp_valid_pd|
//                       (Fill(34, cdma_resp_valid) & cdma_resp_pd)|
//                       (Fill(34, csc_resp_valid) & csc_resp_pd)|
//                       (Fill(34, cmac_a_resp_valid) & cmac_a_resp_pd)|
//                       (Fill(34, cmac_b_resp_valid) & cmac_b_resp_pd)|
//                       (Fill(34, cacc_resp_valid) & cacc_resp_pd)|
//                       (Fill(34, sdp_rdma_resp_valid) & sdp_rdma_resp_pd)|
//                       (Fill(34, sdp_resp_valid) & sdp_resp_pd)|
//                       pdp_rdma_resp_valid_pd|
//                       pdp_resp_valid_pd|
//                       cdp_rdma_resp_valid_pd|
//                       cdp_resp_valid_pd|
//                       rbk_resp_valid_pd|
//                       (Fill(34, dummy_resp_valid) & dummy_resp_pd)

// core_resp_pvld := cfgrom_resp_valid |
//                         glb_resp_valid |
//                         mcif_resp_valid |
//                         cvif_resp_valid_get |
//                         bdma_resp_valid_get |
//                         cdma_resp_valid |
//                         csc_resp_valid |
//                         cmac_a_resp_valid |
//                         cmac_b_resp_valid |
//                         cacc_resp_valid |
//                         sdp_rdma_resp_valid |
//                         sdp_resp_valid |
//                         pdp_rdma_resp_valid_get |
//                         pdp_resp_valid_get |
//                         cdp_rdma_resp_valid_get |
//                         cdp_resp_valid_get |
//                         rbk_resp_valid_get |
//                         dummy_resp_valid;
// }}


// object NV_NVDLA_csb_masterDriver extends App {
//   implicit val conf: csbMasterConfiguration = new csbMasterConfiguration
//   chisel3.Driver.execute(args, () => new NV_NVDLA_csb_master())
// }