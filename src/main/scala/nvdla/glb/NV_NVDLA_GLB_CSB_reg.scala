package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_GLB_CSB_reg extends Module {
    val io = IO(new Bundle {
        //clock
        val nvdla_core_clk = Input(Clock())

        // Register control interface
        val reg_rd_data = Output(UInt(32.W))
        val reg_offset = Input(UInt(12.W))
        val reg_wr_data = Input(UInt(32.W))
        val reg_wr_en = Input(Bool())

        // Writable register flop/trigger outputs
        val bdma_done_mask0 = Output(Bool())
        val bdma_done_mask1 = Output(Bool())       
        val cacc_done_mask0 = Output(Bool())
        val cacc_done_mask1 = Output(Bool())
        val cdma_dat_done_mask0 = Output(Bool())
        val cdma_dat_done_mask1 = Output(Bool())
        val cdma_wt_done_mask0 = Output(Bool())
        val cdma_wt_done_mask1 = Output(Bool())
        val cdp_done_mask0 = Output(Bool())
        val cdp_done_mask1 = Output(Bool())
        val pdp_done_mask0 = Output(Bool())
        val pdp_done_mask1 = Output(Bool())
        val rubik_done_mask0 = Output(Bool())
        val rubik_done_mask1 = Output(Bool())
        val sdp_done_mask0 = Output(Bool()) 
        val sdp_done_mask1 = Output(Bool())
        val sdp_done_set0_trigger = Output(Bool())
        val sdp_done_status0_trigger = Output(Bool())

        // Read-only register inputs
        val bdma_done_set0 = Input(Bool()) 
        val bdma_done_set1 = Input(Bool())
        val cacc_done_set0 = Input(Bool())
        val cacc_done_set1 = Input(Bool())
        val cdma_dat_done_set0 = Input(Bool())
        val cdma_dat_done_set1 = Input(Bool())
        val cdma_wt_done_set0 = Input(Bool())
        val cdma_wt_done_set1 = Input(Bool())
        val cdp_done_set0 = Input(Bool())
        val cdp_done_set1 = Input(Bool())
        val pdp_done_set0 = Input(Bool())
        val pdp_done_set1 = Input(Bool())
        val rubik_done_set0 = Input(Bool())
        val rubik_done_set1 = Input(Bool())
        val sdp_done_set0 = Input(Bool())
        val sdp_done_set1 = Input(Bool())
        val bdma_done_status0 = Input(Bool())
        val bdma_done_status1 = Input(Bool())
        val cacc_done_status0 = Input(Bool())
        val cacc_done_status1 = Input(Bool())
        val cdma_dat_done_status0 = Input(Bool())
        val cdma_dat_done_status1 = Input(Bool())
        val cdma_wt_done_status0 = Input(Bool())
        val cdma_wt_done_status1 = Input(Bool())
        val cdp_done_status0 = Input(Bool())
        val cdp_done_status1 = Input(Bool())
        val pdp_done_status0 = Input(Bool())
        val pdp_done_status1 = Input(Bool())
        val rubik_done_status0 = Input(Bool())
        val rubik_done_status1 = Input(Bool())
        val sdp_done_status0 = Input(Bool())
        val sdp_done_status1 = Input(Bool())

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
withClock(io.nvdla_core_clk){

    // Address decode
    val nvdla_glb_s_intr_mask_0_wren = (io.reg_offset === "h4".asUInt(32.W))&io.reg_wr_en
    val nvdla_glb_s_intr_set_0_wren = (io.reg_offset === "h8".asUInt(32.W))&io.reg_wr_en
    val nvdla_glb_s_intr_status_0_wren = (io.reg_offset === "hc".asUInt(32.W))&io.reg_wr_en
    val nvdla_glb_s_nvdla_hw_version_0_wren = (io.reg_offset === "h0".asUInt(32.W))&io.reg_wr_en

    val major = "h31".asUInt(8.W)
    val minor = "h3030".asUInt(16.W)

    io.sdp_done_set0_trigger := nvdla_glb_s_intr_set_0_wren  
    io.sdp_done_status0_trigger := nvdla_glb_s_intr_status_0_wren   
              
    //Output mux

    io.reg_rd_data := MuxLookup(io.reg_offset, "b0".asUInt(32.W), 
    Seq(  
    //nvdla_glb_s_intr_mask_0_out    
    "h4".asUInt(32.W)  -> Cat("b0".asUInt(10.W), io.cacc_done_mask1, io.cacc_done_mask0, 
                              io.cdma_wt_done_mask1, io.cdma_wt_done_mask0, io.cdma_dat_done_mask1, 
                              io.cdma_dat_done_mask0, "b0".asUInt(6.W), io.rubik_done_mask1, 
                              io.rubik_done_mask0, io.bdma_done_mask1, io.bdma_done_mask0, 
                              io.pdp_done_mask1, io.pdp_done_mask0, io.cdp_done_mask1, 
                              io.cdp_done_mask0, io.sdp_done_mask1, io.sdp_done_mask0),
    //nvdla_glb_s_intr_set_0_out
    "h8".asUInt(32.W)  -> Cat("b0".asUInt(10.W), io.cacc_done_set1, io.reg_offset, 
                              io.cdma_wt_done_set1, io.cdma_wt_done_set0, io.cdma_dat_done_set1, 
                              io.cdma_dat_done_set0, "b0".asUInt(6.W), io.rubik_done_set1, 
                              io.rubik_done_set0, io.bdma_done_set1, io.bdma_done_set0, 
                              io.pdp_done_set1, io.pdp_done_set0, io.cdp_done_set1, 
                              io.cdp_done_set0, io.sdp_done_set1, io.sdp_done_set0),
    //nvdla_glb_s_intr_status_0_out
    "hc".asUInt(32.W)  -> Cat("b0".asUInt(10.W), io.cacc_done_status1, io.cacc_done_status0, 
                              io.cdma_wt_done_status1, io.cdma_wt_done_status0, io.cdma_dat_done_status1, 
                              io.cdma_dat_done_status0, "b0".asUInt(6.W), io.rubik_done_status1, 
                              io.rubik_done_status0, io.bdma_done_status1, io.bdma_done_status0, 
                              io.pdp_done_status1, io.pdp_done_status0, io.cdp_done_status1, 
                              io.cdp_done_status0, io.sdp_done_status1, io.sdp_done_status0),
    //nvdla_glb_s_nvdla_hw_version_0_out
    "h0".asUInt(32.W)  -> Cat("b0".asUInt(8.W), minor, major),                                                                             
    ))

    //Register flop declarations

    val bdma_done_mask0_out = RegInit(false.B)
    val bdma_done_mask1_out = RegInit(false.B)
    val cacc_done_mask0_out = RegInit(false.B)
    val cacc_done_mask1_out = RegInit(false.B)
    val cdma_dat_done_mask0_out = RegInit(false.B)
    val cdma_dat_done_mask1_out = RegInit(false.B)
    val cdma_wt_done_mask0_out = RegInit(false.B)
    val cdma_wt_done_mask1_out = RegInit(false.B)
    val cdp_done_mask0_out = RegInit(false.B)
    val cdp_done_mask1_out = RegInit(false.B)
    val pdp_done_mask0_out = RegInit(false.B)
    val pdp_done_mask1_out = RegInit(false.B)
    val rubik_done_mask0_out = RegInit(false.B)
    val rubik_done_mask1_out = RegInit(false.B)
    val sdp_done_mask0_out = RegInit(false.B)
    val sdp_done_mask1_out = RegInit(false.B)

    when(nvdla_glb_s_intr_mask_0_wren){
        bdma_done_mask0_out := io.reg_wr_data(6)
        bdma_done_mask1_out := io.reg_wr_data(7)
        cacc_done_mask0_out := io.reg_wr_data(20)
        cacc_done_mask1_out := io.reg_wr_data(21)
        cdma_dat_done_mask0_out := io.reg_wr_data(16)
        cdma_dat_done_mask1_out := io.reg_wr_data(17)
        cdma_wt_done_mask0_out := io.reg_wr_data(18)
        cdma_wt_done_mask1_out := io.reg_wr_data(19)
        cdp_done_mask0_out := io.reg_wr_data(2)
        cdp_done_mask1_out := io.reg_wr_data(3)
        pdp_done_mask0_out := io.reg_wr_data(4)
        pdp_done_mask1_out := io.reg_wr_data(5)
        rubik_done_mask0_out := io.reg_wr_data(8)
        rubik_done_mask1_out := io.reg_wr_data(9)
        sdp_done_mask0_out := io.reg_wr_data(0)
        sdp_done_mask1_out := io.reg_wr_data(1)       
    }  

    io.bdma_done_mask0 := bdma_done_mask0_out
    io.bdma_done_mask1 := bdma_done_mask1_out
    io.cacc_done_mask0 := cacc_done_mask0_out
    io.cacc_done_mask1 := cacc_done_mask1_out
    io.cdma_dat_done_mask0 := cdma_dat_done_mask0_out 
    io.cdma_dat_done_mask1 := cdma_dat_done_mask1_out 
    io.cdma_wt_done_mask0 := cdma_wt_done_mask0_out
    io.cdma_wt_done_mask1 := cdma_wt_done_mask1_out
    io.cdp_done_mask0 := cdp_done_mask0_out
    io.cdp_done_mask1 := cdp_done_mask1_out
    io.pdp_done_mask0 := pdp_done_mask0_out
    io.pdp_done_mask1 := pdp_done_mask1_out
    io.rubik_done_mask0 := rubik_done_mask0_out
    io.rubik_done_mask1 := rubik_done_mask1_out
    io.sdp_done_mask0 := sdp_done_mask0_out
    io.sdp_done_mask1 := sdp_done_mask1_out 

}}
    
    

    



    















    



 

