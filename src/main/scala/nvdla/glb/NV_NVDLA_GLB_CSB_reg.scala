package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_GLB_CSB_reg extends Module {
    val io = IO(new Bundle {
        //clock
        val nvdla_core_clk = Input(Clock())

        // Register control interface
        val reg = new reg_control_if

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
    val nvdla_glb_s_intr_mask_0_wren = (io.reg.offset === "h4".asUInt(32.W))&io.reg.wr_en
    val nvdla_glb_s_intr_set_0_wren = (io.reg.offset === "h8".asUInt(32.W))&io.reg.wr_en
    val nvdla_glb_s_intr_status_0_wren = (io.reg.offset === "hc".asUInt(32.W))&io.reg.wr_en
    val nvdla_glb_s_nvdla_hw_version_0_wren = (io.reg.offset === "h0".asUInt(32.W))&io.reg.wr_en

    val major = "h31".asUInt(8.W)
    val minor = "h3030".asUInt(16.W)

    io.sdp_done_set0_trigger := nvdla_glb_s_intr_set_0_wren  
    io.sdp_done_status0_trigger := nvdla_glb_s_intr_status_0_wren   
              
    //Output mux

    io.reg.rd_data := MuxLookup(io.reg.offset, "b0".asUInt(32.W), 
    Seq(  
    //nvdla_glb_s_intr_mask_0    
    "h4".asUInt(32.W)  -> Cat("b0".asUInt(10.W), io.cacc_done_mask1, io.cacc_done_mask0, 
                              io.cdma_wt_done_mask1, io.cdma_wt_done_mask0, io.cdma_dat_done_mask1, 
                              io.cdma_dat_done_mask0, "b0".asUInt(6.W), io.rubik_done_mask1, 
                              io.rubik_done_mask0, io.bdma_done_mask1, io.bdma_done_mask0, 
                              io.pdp_done_mask1, io.pdp_done_mask0, io.cdp_done_mask1, 
                              io.cdp_done_mask0, io.sdp_done_mask1, io.sdp_done_mask0),
    //nvdla_glb_s_intr_set_0
    "h8".asUInt(32.W)  -> Cat("b0".asUInt(10.W), io.cacc_done_set1, io.reg.offset, 
                              io.cdma_wt_done_set1, io.cdma_wt_done_set0, io.cdma_dat_done_set1, 
                              io.cdma_dat_done_set0, "b0".asUInt(6.W), io.rubik_done_set1, 
                              io.rubik_done_set0, io.bdma_done_set1, io.bdma_done_set0, 
                              io.pdp_done_set1, io.pdp_done_set0, io.cdp_done_set1, 
                              io.cdp_done_set0, io.sdp_done_set1, io.sdp_done_set0),
    //nvdla_glb_s_intr_status_0
    "hc".asUInt(32.W)  -> Cat("b0".asUInt(10.W), io.cacc_done_status1, io.cacc_done_status0, 
                              io.cdma_wt_done_status1, io.cdma_wt_done_status0, io.cdma_dat_done_status1, 
                              io.cdma_dat_done_status0, "b0".asUInt(6.W), io.rubik_done_status1, 
                              io.rubik_done_status0, io.bdma_done_status1, io.bdma_done_status0, 
                              io.pdp_done_status1, io.pdp_done_status0, io.cdp_done_status1, 
                              io.cdp_done_status0, io.sdp_done_status1, io.sdp_done_status0),
    //nvdla_glb_s_nvdla_hw_version_0
    "h0".asUInt(32.W)  -> Cat("b0".asUInt(8.W), minor, major),                                                                             
    ))

    //Register flop declarations

    io.bdma_done_mask0 := RegEnable(io.reg.wr_data(6), false.B, nvdla_glb_s_intr_mask_0_wren)
    io.bdma_done_mask1 := RegEnable(io.reg.wr_data(7), false.B, nvdla_glb_s_intr_mask_0_wren)
    io.cacc_done_mask0 := RegEnable(io.reg.wr_data(20), false.B, nvdla_glb_s_intr_mask_0_wren)
    io.cacc_done_mask1 := RegEnable(io.reg.wr_data(21), false.B, nvdla_glb_s_intr_mask_0_wren)
    io.cdma_dat_done_mask0 := RegEnable(io.reg.wr_data(16), false.B, nvdla_glb_s_intr_mask_0_wren)
    io.cdma_dat_done_mask1 := RegEnable(io.reg.wr_data(17), false.B, nvdla_glb_s_intr_mask_0_wren)
    io.cdma_wt_done_mask0 := RegEnable(io.reg.wr_data(18), false.B, nvdla_glb_s_intr_mask_0_wren)
    io.cdma_wt_done_mask1 := RegEnable(io.reg.wr_data(19), false.B, nvdla_glb_s_intr_mask_0_wren)
    io.cdp_done_mask0 := RegEnable(io.reg.wr_data(2), false.B, nvdla_glb_s_intr_mask_0_wren)
    io.cdp_done_mask1 := RegEnable(io.reg.wr_data(3), false.B, nvdla_glb_s_intr_mask_0_wren)
    io.pdp_done_mask0 := RegEnable(io.reg.wr_data(4), false.B, nvdla_glb_s_intr_mask_0_wren)
    io.pdp_done_mask1 := RegEnable(io.reg.wr_data(5), false.B, nvdla_glb_s_intr_mask_0_wren)
    io.rubik_done_mask0 := RegEnable(io.reg.wr_data(8), false.B, nvdla_glb_s_intr_mask_0_wren)
    io.rubik_done_mask1 := RegEnable(io.reg.wr_data(9), false.B, nvdla_glb_s_intr_mask_0_wren)
    io.sdp_done_mask0 := RegEnable(io.reg.wr_data(0), false.B, nvdla_glb_s_intr_mask_0_wren)
    io.sdp_done_mask1 := RegEnable(io.reg.wr_data(1), false.B, nvdla_glb_s_intr_mask_0_wren)


}}
    
    

    



    















    



 

