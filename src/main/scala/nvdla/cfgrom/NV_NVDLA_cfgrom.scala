// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._



// //this module is to active dat and wt

// class NV_NVDLA_cfgrom extends RawModule {

//     val io = IO(new Bundle {
//         //general clock
//         val nvdla_core_clk = Input(Clock())     
//         val nvdla_core_rstn = Input(Bool())

//         val csb2cfgrom_req_pd = Input(UInt(63.W))
//         val csb2cfgrom_req_pvld = Input(Bool())

//         val csb2cfgrom_req_prdy = Output(Bool())
//         val cfgrom2csb_resp_pd = Output(UInt(34.W))
//         val cfgrom2csb_resp_valid = Output(Bool())


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
                
// withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn) { 

// /////////////////////////////////////////////
//     val csb_rresp_error = Wire(Bool())
//     val csb_rresp_pd_w = Wire(UInt(34.W))
//     val csb_rresp_rdat = Wire(UInt(32.W)) 
//     val csb_wresp_error = Wire(Bool())
//     val csb_wresp_pd_w = Wire(UInt(34.W))
//     val csb_wresp_rdat = Wire(UInt(32.W)) 
//     val reg_offset = Wire(UInt(24.W)) 
//     val reg_rd_data = Wire(UInt(32.W)) 
//     val reg_rd_en = Wire(Bool())
//     val reg_wr_data = Wire(UInt(32.W)) 
//     val reg_wr_en = Wire(Bool())
//     val req_addr = Wire(UInt(22.W)) 
//     val req_level = Wire(UInt(2.W)) 
//     val req_nposted = Wire(Bool())
//     val req_srcpriv = Wire(Bool())
//     val req_wdat = Wire(UInt(32.W)) 
//     val req_wrbe = Wire(UInt(4.W)) 
//     val req_write = Wire(Bool())
//     val cfgrom2csb_resp_pd_out = RegInit("b0".asUInt(34.W))
//     val cfgrom2csb_resp_valid_out = Wire(false.B)
//     val req_pd = Wire(UInt(63.W))
//     val req_pvld = RegInit(false.B)
    
// ////////////////////////////////////////////////////////////////////////
//     val u_NV_NVDLA_CFGROM_rom = Module(new NV_NVDLA_CFGROM_rom)

//     u_dual_reg_d0.io.nvdla_core_clk := io.nvdla_core_clk
//     u_dual_reg_d0.io.nvdla_core_rstn := io.nvdla_core_rstn

//     u_NV_NVDLA_CFGROM_rom.io.reg_offset := reg_offset
//     u_NV_NVDLA_CFGROM_rom.io.reg_wr_data := reg_wr_data
//     u_NV_NVDLA_CFGROM_rom.io.reg_wr_en := reg_wr_en

//     reg_rd_data := u_NV_NVDLA_CFGROM_rom.io.reg_rd_data 

// ////////////////////////////////////////////////////////////////////////
// //                                                                    //
// // GENERATE CSB TO REGISTER CONNECTION LOGIC                          //
// //                                                                    //
// ////////////////////////////////////////////////////////////////////////
//     req_pvld := io.csb2cfgrom_req_pvld
//     req_pd := io.csb2cfgrom_req_pd

// // PKT_UNPACK_WIRE( csb2xx_16m_be_lvl ,  req_ ,  req_pd )
//     req_addr := req_pd(21, 0)
//     req_wdat := req_pd(53, 22)
//     req_write := req_pd(54)
//     req_nposted := req_pd(55)
//     req_srcpriv := req_pd(56)
//     req_wrbe := req_pd(60, 57)
//     req_level := req_pd(62, 61)
//     io.csb2cfgrom_req_prdy := true.B

// //Address in CSB master is word aligned while address in regfile is byte aligned.
//     reg_offset := Cat(req_addr, "b0".asUInt(2.W))
//     reg_wr_data := req_wdat
//     reg_wr_en := req_pvld & req_write
//     reg_rd_en := req_pvld & ~req_write

//     csb_rresp_pd_w := Cat(false.B, csb_rresp_error, csb_rresp_rdat) /* PKT_nvdla_xx2csb_resp_dla_xx2csb_rd_erpt_ID  */ 
//     csb_wresp_pd_w := Cat(true.B, csb_wresp_error, csb_wresp_rdat) /* PKT_nvdla_xx2csb_resp_dla_xx2csb_wr_erpt_ID  */

//     csb_rresp_rdat := reg_rd_data
//     csb_rresp_error := fasle.B
//     csb_wresp_rdat := Fill(32, false.B) 
//     csb_wresp_error := false.B

//     when(reg_rd_en){
//         cfgrom2csb_resp_pd_out := csb_rresp_pd_w
//     }
//     .elsewhen(reg_wr_en & req_nposted){
//         cfgrom2csb_resp_pd_out := csb_wresp_pd_w
//     }

//     io.cfgrom2csb_resp_pd := cfgrom2csb_resp_pd_out


//     cfgrom2csb_resp_valid_out := (reg_wr_en & req_nposted) | reg_rd_en
//     io.cfgrom2csb_resp_valid := cfgrom2csb_resp_valid_out


// }}