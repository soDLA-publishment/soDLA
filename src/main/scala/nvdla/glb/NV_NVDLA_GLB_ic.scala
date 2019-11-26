// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_GLB_ic(implicit val conf: nvdlaConfig) extends Module {
//     val io = IO(new Bundle {
//         //clock
//         val nvdla_core_clk = Input(Clock())
//         val nvdla_falcon_clk = Input(Clock())
 
//         //bdma
//         val bdma2glb_done_intr_pd = if(conf.NVDLA_BDMA_ENABLE) Some(Input(UInt(2.W))) else None
//         val bdma_done_mask0 = if(conf.NVDLA_BDMA_ENABLE) Some(Input(Bool())) else None
//         val bdma_done_mask1 = if(conf.NVDLA_BDMA_ENABLE) Some(Input(Bool())) else None  
//         val bdma_done_status0 = if(conf.NVDLA_BDMA_ENABLE) Some(Output(Bool())) else None  
//         val bdma_done_status1 = if(conf.NVDLA_BDMA_ENABLE) Some(Output(Bool())) else None 

//         //cdp
//         val cdp2glb_done_intr_pd = if(conf.NVDLA_CDP_ENABLE) Some(Input(UInt(2.W))) else None
//         val cdp_done_mask0 = if(conf.NVDLA_CDP_ENABLE) Some(Input(Bool())) else None
//         val cdp_done_mask1 = if(conf.NVDLA_CDP_ENABLE) Some(Input(Bool())) else None  
//         val cdp_done_status0 = if(conf.NVDLA_CDP_ENABLE) Some(Output(Bool())) else None  
//         val cdp_done_status1 = if(conf.NVDLA_CDP_ENABLE) Some(Output(Bool())) else None      

//         //pdp
//         val pdp2glb_done_intr_pd = if(conf.NVDLA_PDP_ENABLE) Some(Input(UInt(2.W))) else None
//         val pdp_done_mask0 = if(conf.NVDLA_PDP_ENABLE) Some(Input(Bool())) else None
//         val pdp_done_mask1 = if(conf.NVDLA_PDP_ENABLE) Some(Input(Bool())) else None  
//         val pdp_done_status0 = if(conf.NVDLA_PDP_ENABLE) Some(Output(Bool())) else None  
//         val pdp_done_status1 = if(conf.NVDLA_PDP_ENABLE) Some(Output(Bool())) else None        

//         //rubik
//         val rubik2glb_done_intr_pd = if(conf.NVDLA_RUBIK_ENABLE) Some(Input(UInt(2.W))) else None
//         val rubik_done_mask0 = if(conf.NVDLA_RUBIK_ENABLE) Some(Input(Bool())) else None
//         val rubik_done_mask1 = if(conf.NVDLA_RUBIK_ENABLE) Some(Input(Bool())) else None  
//         val rubik_done_status0 = if(conf.NVDLA_RUBIK_ENABLE) Some(Output(Bool())) else None  
//         val rubik_done_status1 = if(conf.NVDLA_RUBIK_ENABLE) Some(Output(Bool())) else None              

//         //cacc
//         val cacc2glb_done_intr_pd = Input(UInt(2.W))
//         val cacc_done_mask0 = Input(Bool())
//         val cacc_done_mask1 = Input(Bool())
//         val cacc_done_status0 = Output(Bool())
//         val cacc_done_status1 = Output(Bool())

//         //cdma
//         val cdma_dat2glb_done_intr_pd = Input(UInt(2.W))
//         val cdma_dat_done_mask0 = Input(Bool())
//         val cdma_dat_done_mask1 = Input(Bool())
//         val cdma_dat_done_status0 = Output(Bool())
//         val cdma_dat_done_status1 = Output(Bool())

//         val cdma_wt2glb_done_intr_pd = Input(UInt(2.W))
//         val cdma_wt_done_mask0 = Input(Bool())
//         val cdma_wt_done_mask1 = Input(Bool())
//         val cdma_wt_done_status0 = Output(Bool())
//         val cdma_wt_done_status1 = Output(Bool())

//         //sdp
//         val sdp2glb_done_intr_pd = Input(UInt(2.W))
//         val sdp_done_mask0 = Input(Bool())
//         val sdp_done_mask1 = Input(Bool())
//         val sdp_done_status0 = Output(Bool())
//         val sdp_done_status1 = Output(Bool())
//         val sdp_done_set0_trigger = Input(Bool())
//         val sdp_done_status0_trigger = Input(Bool())

//         val req_wdat = Input(UInt(22.W))
//         val core_intr = Output(Bool())
    
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

//     val done_wr_clr = Mux(io.sdp_done_status0_trigger, Cat(io.req_wdat(21, 16), io.req_wdat(9, 0)), "b0".asUInt(14.W))
//     val done_set = Mux(io.sdp_done_set0_trigger, Cat(io.req_wdat(21, 16), io.req_wdat(9, 0)), "b0".asUInt(14.W))

//     val rubik2glb_done_intr_pd_w = if(conf.NVDLA_RUBIK_ENABLE) io.rubik2glb_done_intr_pd.get else "b0".asUInt(2.W)
//     val bdma2glb_done_intr_pd_w = if(conf.NVDLA_BDMA_ENABLE) io.bdma2glb_done_intr_pd.get else "b0".asUInt(2.W)
//     val pdp2glb_done_intr_pd_w = if(conf.NVDLA_PDP_ENABLE) io.pdp2glb_done_intr_pd.get else "b0".asUInt(2.W)
//     val cdp2glb_done_intr_pd_w = if(conf.NVDLA_CDP_ENABLE) io.cdp2glb_done_intr_pd.get else "b0".asUInt(2.W)

//     val done_source = RegNext(Cat(io.cacc2glb_done_intr_pd, io.cdma_wt2glb_done_intr_pd, io.cdma_dat2glb_done_intr_pd,
//                             rubik2glb_done_intr_pd_w, bdma2glb_done_intr_pd_w, pdp2glb_done_intr_pd_w,
//                             cdp2glb_done_intr_pd_w, io.sdp2glb_done_intr_pd), "b0".asUInt(16.W))

//     //////// interrrupt status 0 for sdp ////////
//     val sdp_done_status0_reg = RegInit(false.B)

//     val sdp_done_status0_w = Mux(done_set(0) | done_source(0), true.B, 
//                              Mux(done_wr_clr(0), false.B,
//                              sdp_done_status0_reg))
    
//     sdp_done_status0_reg := sdp_done_status0_w
//     io.sdp_done_status0 := sdp_done_status0_reg

//     //////// interrrupt status 1 for sdp ////////
//     val sdp_done_status1_reg = RegInit(false.B)

//     val sdp_done_status1_w = Mux(done_set(1) | done_source(1), true.B, 
//                              Mux(done_wr_clr(1), false.B,
//                              sdp_done_status1_reg))

//     sdp_done_status1_reg := sdp_done_status1_w
//     io.sdp_done_status1 := sdp_done_status1_reg

//     if(conf.NVDLA_CDP_ENABLE){
//     //////// interrrupt status 0 for cdp ////////
//     val cdp_done_status0_reg = RegInit(false.B)

//     val cdp_done_status0_w = Mux(done_set(2) | done_source(2), true.B, 
//                              Mux(done_wr_clr(2), false.B,
//                              cdp_done_status0_reg))
    
//     cdp_done_status0_reg := cdp_done_status0_w
//     io.cdp_done_status0.get := cdp_done_status0_reg

//     //////// interrrupt status 1 for cdp ////////
//     val cdp_done_status1_reg = RegInit(false.B)

//     val cdp_done_status1_w = Mux(done_set(3) | done_source(3), true.B, 
//                              Mux(done_wr_clr(3), false.B,
//                              cdp_done_status1_reg))

//     cdp_done_status1_reg := cdp_done_status1_w
//     io.cdp_done_status1.get := cdp_done_status1_reg

//     }

//     if(conf.NVDLA_PDP_ENABLE){
//     //////// interrrupt status 0 for pdp ////////
//     val pdp_done_status0_reg = RegInit(false.B)

//     val pdp_done_status0_w = Mux(done_set(4) | done_source(4), true.B, 
//                              Mux(done_wr_clr(4), false.B,
//                              pdp_done_status0_reg))

//     pdp_done_status0_reg := pdp_done_status0_w
//     io.pdp_done_status0.get := pdp_done_status0_reg

//     //////// interrrupt status 1 for pdp ////////
//     val pdp_done_status1_reg = RegInit(false.B)

//     val pdp_done_status1_w = Mux(done_set(5) | done_source(5), true.B, 
//                              Mux(done_wr_clr(5), false.B,
//                              pdp_done_status1_reg))

//     pdp_done_status1_reg := pdp_done_status1_w
//     io.pdp_done_status1.get := pdp_done_status1_reg

//     }

//     if(conf.NVDLA_BDMA_ENABLE){
//     //////// interrrupt status 0 for bdma ////////
//     val bdma_done_status0_reg = RegInit(false.B)

//     val bdma_done_status0_w = Mux(done_set(6) | done_source(6), true.B, 
//                              Mux(done_wr_clr(6), false.B,
//                              bdma_done_status0_reg))
    
//     bdma_done_status0_reg := bdma_done_status0_w
//     io.bdma_done_status0.get := bdma_done_status0_reg

//     //////// interrrupt status 1 for bdma ////////
//     val bdma_done_status1_reg = RegInit(false.B)

//     val bdma_done_status1_w = Mux(done_set(7) | done_source(7), true.B, 
//                              Mux(done_wr_clr(7), false.B,
//                              bdma_done_status1_reg))

//     bdma_done_status1_reg := bdma_done_status1_w
//     io.bdma_done_status1.get := bdma_done_status1_reg

//     }

//     if(conf.NVDLA_RUBIK_ENABLE){
//     //////// interrrupt status 0 for rubik ////////
//     val rubik_done_status0_reg = RegInit(false.B)

//     val rubik_done_status0_w = Mux(done_set(8) | done_source(8), true.B, 
//                              Mux(done_wr_clr(8), false.B,
//                              rubik_done_status0_reg))
    
//     rubik_done_status0_reg := rubik_done_status0_w
//     io.rubik_done_status0.get := rubik_done_status0_reg

//     //////// interrrupt status 1 for rubik ////////
//     val rubik_done_status1_reg = RegInit(false.B)

//     val rubik_done_status1_w = Mux(done_set(9) | done_source(9), true.B, 
//                              Mux(done_wr_clr(9), false.B,
//                              rubik_done_status1_reg))

//     rubik_done_status1_reg := rubik_done_status1_w
//     io.rubik_done_status1.get := rubik_done_status1_reg

//     }

//     //////// interrrupt status 0 for cdma_dat ////////
//     val cdma_dat_done_status0_reg = RegInit(false.B)

//     val cdma_dat_done_status0_w = Mux(done_set(10) | done_source(10), true.B, 
//                              Mux(done_wr_clr(10), false.B,
//                              cdma_dat_done_status0_reg))
    
//     cdma_dat_done_status0_reg := cdma_dat_done_status0_w
//     io.cdma_dat_done_status0 := cdma_dat_done_status0_reg

//     //////// interrrupt status 1 for cdma_dat ////////
//     val cdma_dat_done_status1_reg = RegInit(false.B)

//     val cdma_dat_done_status1_w = Mux(done_set(11) | done_source(11), true.B, 
//                              Mux(done_wr_clr(11), false.B,
//                              cdma_dat_done_status1_reg))
    
//     cdma_dat_done_status1_reg := cdma_dat_done_status1_w
//     io.cdma_dat_done_status1 := cdma_dat_done_status1_reg

//     //////// interrrupt status 0 for cdma_wt ////////
//     val cdma_wt_done_status0_reg = RegInit(false.B)

//     val cdma_wt_done_status0_w = Mux(done_set(12) | done_source(12), true.B, 
//                              Mux(done_wr_clr(12), false.B,
//                              cdma_wt_done_status0_reg))
    
//     cdma_wt_done_status0_reg := cdma_wt_done_status0_w
//     io.cdma_wt_done_status0 := cdma_wt_done_status0_reg

//     //////// interrrupt status 1 for cdma_wt ////////
//     val cdma_wt_done_status1_reg = RegInit(false.B)

//     val cdma_wt_done_status1_w = Mux(done_set(13) | done_source(13), true.B, 
//                              Mux(done_wr_clr(13), false.B,
//                              cdma_wt_done_status1_reg))
    
//     cdma_wt_done_status1_reg := cdma_wt_done_status1_w
//     io.cdma_wt_done_status1 := cdma_wt_done_status1_reg 

//     //////// interrrupt status 0 for cacc ////////
//     val cacc_done_status0_reg = RegInit(false.B)

//     val cacc_done_status0_w = Mux(done_set(14) | done_source(14), true.B, 
//                              Mux(done_wr_clr(14), false.B,
//                              cacc_done_status0_reg))
    
//     cacc_done_status0_reg := cacc_done_status0_w
//     io.cacc_done_status0 := cacc_done_status0_reg     

//     //////// interrrupt status 1 for cacc ////////
//     val cacc_done_status1_reg = RegInit(false.B)

//     val cacc_done_status1_w = Mux(done_set(15) | done_source(15), true.B, 
//                              Mux(done_wr_clr(15), false.B,
//                              cacc_done_status1_reg))
    
//     cacc_done_status1_reg := cacc_done_status1_w
//     io.cacc_done_status1 := cacc_done_status1_reg  

//     val cdp_intr = if(conf.NVDLA_CDP_ENABLE) 
//                    (~io.cdp_done_mask0.get & io.cdp_done_status0.get)|(~io.cdp_done_mask1.get & io.cdp_done_status1.get) 
//                    else
//                    false.B
//     val pdp_intr = if(conf.NVDLA_PDP_ENABLE) 
//                    (~io.pdp_done_mask0.get & io.pdp_done_status0.get)|(~io.pdp_done_mask1.get & io.pdp_done_status1.get) 
//                    else
//                    false.B
//     val bdma_intr = if(conf.NVDLA_BDMA_ENABLE) 
//                    (~io.bdma_done_mask0.get & io.bdma_done_status0.get)|(~io.bdma_done_mask1.get & io.bdma_done_status1.get) 
//                    else
//                    false.B
//     val rubik_intr = if(conf.NVDLA_RUBIK_ENABLE) 
//                    (~io.rubik_done_mask0.get & io.rubik_done_status0.get)|(~io.rubik_done_mask1.get & io.rubik_done_status1.get) 
//                    else
//                    false.B  

//     val core_intr_w = (~io.sdp_done_mask0 & io.sdp_done_status0) |
//                       (~io.sdp_done_mask1 & io.sdp_done_status1) |  
//                        cdp_intr|
//                        pdp_intr|
//                        bdma_intr|          
//                        rubik_intr|
//                      (~io.cdma_dat_done_mask0 & io.cdma_dat_done_status0) |
//                      (~io.cdma_dat_done_mask1 & io.cdma_dat_done_status1) |
//                      (~io.cdma_wt_done_mask0 & io.cdma_wt_done_status0) |
//                      (~io.cdma_wt_done_mask1 & io.cdma_wt_done_status1) |
//                      (~io.cacc_done_mask0 & io.cacc_done_status0) |
//                      (~io.cacc_done_mask1 & io.cacc_done_status1)    
    
//     val core_intr_d = RegNext(core_intr_w, false.B)

//     io.core_intr := withClock(io.nvdla_falcon_clk){ShiftRegister(core_intr_d, 3, false.B)}




// }}
    
    

    



    















    



 

