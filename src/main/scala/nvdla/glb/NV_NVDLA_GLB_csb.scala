// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_GLB_csb(implicit val conf: nvdlaConfig) extends Module {
//     val io = IO(new Bundle {

//         //clock
//         val nvdla_core_clk = Input(Clock())
 
//         //bdma
//         val bdma_done_status0 = if(conf.NVDLA_BDMA_ENABLE) Some(Input(Bool())) else None
//         val bdma_done_status1 = if(conf.NVDLA_BDMA_ENABLE) Some(Input(Bool())) else None
//         val bdma_done_mask0 = if(conf.NVDLA_BDMA_ENABLE) Some(Output(Bool())) else None
//         val bdma_done_mask1 = if(conf.NVDLA_BDMA_ENABLE) Some(Output(Bool())) else None           

//         //cdp
//         val cdp_done_status0 = if(conf.NVDLA_CDP_ENABLE) Some(Input(Bool())) else None
//         val cdp_done_status1 = if(conf.NVDLA_CDP_ENABLE) Some(Input(Bool())) else None
//         val cdp_done_mask0 = if(conf.NVDLA_CDP_ENABLE) Some(Output(Bool())) else None
//         val cdp_done_mask1 = if(conf.NVDLA_CDP_ENABLE) Some(Output(Bool())) else None         

//         //pdp
//         val pdp_done_status0 = if(conf.NVDLA_PDP_ENABLE) Some(Input(Bool())) else None
//         val pdp_done_status1 = if(conf.NVDLA_PDP_ENABLE) Some(Input(Bool())) else None
//         val pdp_done_mask0 = if(conf.NVDLA_PDP_ENABLE) Some(Output(Bool())) else None
//         val pdp_done_mask1 = if(conf.NVDLA_PDP_ENABLE) Some(Output(Bool())) else None        

//         //rubik
//         val rubik_done_status0 = if(conf.NVDLA_RUBIK_ENABLE) Some(Input(Bool())) else None
//         val rubik_done_status1 = if(conf.NVDLA_RUBIK_ENABLE) Some(Input(Bool())) else None
//         val rubik_done_mask0 = if(conf.NVDLA_RUBIK_ENABLE) Some(Output(Bool())) else None
//         val rubik_done_mask1 = if(conf.NVDLA_RUBIK_ENABLE) Some(Output(Bool())) else None         

//         //cacc
//         val cacc_done_status0 = Input(Bool())
//         val cacc_done_status1 = Input(Bool())
//         val cacc_done_mask0 = Output(Bool())
//         val cacc_done_mask1 = Output(Bool())

//         //cdma
//         val cdma_dat_done_status0 = Input(Bool())
//         val cdma_dat_done_status1 = Input(Bool())
//         val cdma_dat_done_mask0 = Output(Bool())
//         val cdma_dat_done_mask1 = Output(Bool())

//         val cdma_wt_done_status0 = Input(Bool())
//         val cdma_wt_done_status1 = Input(Bool())
//         val cdma_wt_done_mask0 = Output(Bool())
//         val cdma_wt_done_mask1 = Output(Bool())

//         //csb2glb
//         val csb2glb_req_pd = Input(UInt(63.W))
//         val csb2glb_req_pvld = Input(Bool())  
//         val csb2glb_req_prdy = Output(Bool())  
//         val glb2csb_resp_pd = Output(UInt(34.W))
//         val glb2csb_resp_valid = Output(Bool())

//         //sdp
//         val sdp_done_status0 = Input(Bool())
//         val sdp_done_status1 = Input(Bool())
//         val sdp_done_mask0 = Output(Bool())
//         val sdp_done_mask1 = Output(Bool())
//         val sdp_done_set0_trigger = Output(Bool())
//         val sdp_done_status0_trigger = Output(Bool())

//         val req_wdat = Output(UInt(32.W))

        
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

//     //////////////////////////////////////////////////////////
//     ////  register
//     //////////////////////////////////////////////////////////

//     //tie 0 for wo type register read
//     val  bdma_done_set0 = false.B
//     val  bdma_done_set1 = false.B
//     val  cdp_done_set0  = false.B
//     val  cdp_done_set1  = false.B
//     val  pdp_done_set0  = false.B
//     val  pdp_done_set1  = false.B
//     val  sdp_done_set0  = false.B
//     val  sdp_done_set1  = false.B
//     val  rubik_done_set0  = false.B
//     val  rubik_done_set1  = false.B
//     val  cdma_dat_done_set0  = false.B
//     val  cdma_dat_done_set1  = false.B
//     val  cdma_wt_done_set0  = false.B
//     val  cdma_wt_done_set1  = false.B
//     val  cacc_done_set0  = false.B
//     val  cacc_done_set1  = false.B

//     val req_vld = RegNext(io.csb2glb_req_pvld, false.B)
//     val req_pd = RegEnable(io.csb2glb_req_pd, io.csb2glb_req_pvld)  
//     io.csb2glb_req_prdy := true.B

//     // ========
//     // REQUEST
//     // ========
//     // flow=pvld_prdy 
//     val req_level_NC = req_pd(62, 61)
//     val req_nposted = req_pd(55)
//     val req_addr = req_pd(21, 0)
//     val req_wrbe_NC = req_pd(60, 57)
//     val req_srcpriv_NC = req_pd(56)
//     val req_write = req_pd(54)
//     io.req_wdat := req_pd(53, 22)

//     // ========
//     // RESPONSE
//     // ========
//     // flow=valid 
//     val reg.rd_data = Wire(UInt(32.W))


//     val rsp_rd_vld  = req_vld & ~req_write;
//     val rsp_rd_rdat = Fill(32, rsp_rd_vld) & reg.rd_data;
//     val rsp_rd_error  = false.B

//     val rsp_wr_vld  = req_vld & req_write & req_nposted;
//     val rsp_wr_rdat = "b0".asUInt(32.W)
//     val rsp_wr_error = false.B

//     val rsp_rd_pd = Cat(rsp_rd_error, rsp_rd_rdat)
//     val rsp_wr_pd = Cat(rsp_wr_error, rsp_wr_rdat)

//     // ========
//     // REQUEST
//     // ========

//     val rsp_vld = rsp_rd_vld | rsp_wr_vld
//     val rsp_pd = Cat((rsp_rd_vld&false.B)|(rsp_wr_vld&true.B), 
//                  (Fill(33, rsp_rd_vld)&rsp_rd_pd)|(Fill(33, rsp_wr_vld)&rsp_wr_pd))
//     io.glb2csb_resp_valid := RegNext(rsp_vld, false.B)
//     io.glb2csb_resp_pd := RegEnable(rsp_pd, rsp_vld)
//     val reg.offset = Cat(req_addr(9, 0), "b0".asUInt(2.W))
//     val reg.wr_en = req_vld & req_write
//     val reg.wr_data = io.req_wdat

//     val u_reg = Module(new NV_NVDLA_GLB_CSB_reg)

//     u_reg.io.nvdla_core_clk := io.nvdla_core_clk

//     u_reg.io.reg.offset := reg.offset
//     u_reg.io.reg.wr_data := reg.wr_data
//     u_reg.io.reg.wr_en := reg.wr_en
//     reg.rd_data := u_reg.io.reg.rd_data

//     //bdma
//     if(conf.NVDLA_BDMA_ENABLE){
//         u_reg.io.bdma_done_status0 := io.bdma_done_status0.get
//         u_reg.io.bdma_done_status1 := io.bdma_done_status1.get
//         io.bdma_done_mask0.get := u_reg.io.bdma_done_mask0
//         io.bdma_done_mask1.get := u_reg.io.bdma_done_mask1
//     }
//     else{
//         u_reg.io.bdma_done_status0 := false.B
//         u_reg.io.bdma_done_status1 := false.B

//     }
//     u_reg.io.bdma_done_set0 := bdma_done_set0
//     u_reg.io.bdma_done_set1 := bdma_done_set1

//     //cdp
//     if(conf.NVDLA_CDP_ENABLE){
//         u_reg.io.cdp_done_status0 := io.cdp_done_status0.get
//         u_reg.io.cdp_done_status1 := io.cdp_done_status1.get
//         io.cdp_done_mask0.get := u_reg.io.cdp_done_mask0
//         io.cdp_done_mask1.get := u_reg.io.cdp_done_mask1
//     }
//     else{
//         u_reg.io.cdp_done_status0 := false.B
//         u_reg.io.cdp_done_status1 := false.B
//     }
//     u_reg.io.cdp_done_set0 := cdp_done_set0
//     u_reg.io.cdp_done_set1 := cdp_done_set1

//     //pdp
//     if(conf.NVDLA_PDP_ENABLE){
//         u_reg.io.pdp_done_status0 := io.pdp_done_status0.get
//         u_reg.io.pdp_done_status1 := io.pdp_done_status1.get
//         io.pdp_done_mask0.get := u_reg.io.pdp_done_mask0
//         io.pdp_done_mask1.get := u_reg.io.pdp_done_mask1
//     }
//     else{
//         u_reg.io.pdp_done_status0 := false.B
//         u_reg.io.pdp_done_status1 := false.B
//     }
//     u_reg.io.pdp_done_set0 := pdp_done_set0
//     u_reg.io.pdp_done_set1 := pdp_done_set1

//     //rubik
//     if(conf.NVDLA_RUBIK_ENABLE){
//         u_reg.io.rubik_done_status0 := io.rubik_done_status0.get
//         u_reg.io.rubik_done_status1 := io.rubik_done_status1.get
//         io.rubik_done_mask0.get := u_reg.io.rubik_done_mask0
//         io.rubik_done_mask1.get := u_reg.io.rubik_done_mask1
//     }
//     else{
//         u_reg.io.rubik_done_status0 := false.B
//         u_reg.io.rubik_done_status1 := false.B
//     }
//     u_reg.io.rubik_done_set0 := rubik_done_set0
//     u_reg.io.rubik_done_set1 := rubik_done_set1

//     //cacc
//     u_reg.io.cacc_done_set0 := cacc_done_set0 
//     u_reg.io.cacc_done_set1 := cacc_done_set1
//     u_reg.io.cacc_done_status0 := io.cacc_done_status0
//     u_reg.io.cacc_done_status1 := io.cacc_done_status1
//     io.cacc_done_mask0 := u_reg.io.cacc_done_mask0
//     io.cacc_done_mask1 := u_reg.io.cacc_done_mask1

//     //cdma_dat
//     u_reg.io.cdma_dat_done_set0 := cdma_dat_done_set0
//     u_reg.io.cdma_dat_done_set1 := cdma_dat_done_set1
//     u_reg.io.cdma_dat_done_status0 := io.cdma_dat_done_status0
//     u_reg.io.cdma_dat_done_status1 := io.cdma_dat_done_status1
//     io.cdma_dat_done_mask0 := u_reg.io.cdma_dat_done_mask0
//     io.cdma_dat_done_mask1 := u_reg.io.cdma_dat_done_mask1

//     //cdma_wt
//     u_reg.io.cdma_wt_done_set0 := cdma_wt_done_set0
//     u_reg.io.cdma_wt_done_set1 := cdma_wt_done_set1
//     u_reg.io.cdma_wt_done_status0 := io.cdma_wt_done_status0
//     u_reg.io.cdma_wt_done_status1 := io.cdma_wt_done_status1
//     io.cdma_wt_done_mask0 := u_reg.io.cdma_wt_done_mask0
//     io.cdma_wt_done_mask1 := u_reg.io.cdma_wt_done_mask1

//     //sdp
//     u_reg.io.sdp_done_status0:= io.sdp_done_status0
//     u_reg.io.sdp_done_status1 := io.sdp_done_status1
//     u_reg.io.sdp_done_set0 := sdp_done_set0 
//     u_reg.io.sdp_done_set1 := sdp_done_set1
//     io.sdp_done_mask0 := u_reg.io.sdp_done_mask0
//     io.sdp_done_mask1 := u_reg.io.sdp_done_mask1  
//     io.sdp_done_set0_trigger := u_reg.io.sdp_done_set0_trigger
//     io.sdp_done_status0_trigger := u_reg.io.sdp_done_status0_trigger


// }}
    
    

    



    















    



 

