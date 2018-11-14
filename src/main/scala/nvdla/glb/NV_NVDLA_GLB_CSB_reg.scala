package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._




class NV_NVDLA_GLB_CSB_reg extends Module {
    val io = IO(new Bundle {

        //clock

        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

        // Register control interface

        val reg_rd_data = Output(UInt(32.W))
        val reg_offset = Input(UInt(12.W))
        val reg_wr_data = Input(UInt(32.W))
        val reg_wr_en = Input(UInt(1.W))

        // Writable register flop/trigger outputs

        val bdma_done_mask0 = Output(UInt(1.W))
        val bdma_done_mask1 = Output(UInt(1.W))       
        val cacc_done_mask0 = Output(UInt(1.W))
        val cacc_done_mask1 = Output(UInt(1.W))
        val cdma_dat_done_mask0 = Output(UInt(1.W))
        val cdma_dat_done_mask1 = Output(UInt(1.W))
        val cdma_wt_done_mask0 = Output(UInt(1.W))
        val cdma_wt_done_mask1 = Output(UInt(1.W))
        val cdp_done_mask0 = Output(UInt(1.W))
        val cdp_done_mask1 = Output(UInt(1.W))
        val pdp_done_mask0 = Output(UInt(1.W))
        val pdp_done_mask1 = Output(UInt(1.W))
        val rubik_done_mask0 = Output(UInt(1.W))
        val rubik_done_mask1 = Output(UInt(1.W))
        val sdp_done_mask0 = Output(UInt(1.W)) 
        val sdp_done_mask1 = Output(UInt(1.W))
        val sdp_done_set0_trigger = Output(UInt(1.W))
        val sdp_done_status0_trigger = Output(UInt(1.W))

        // Read-only register inputs

        val bdma_done_set0 = Input(UInt(1.W)) 
        val bdma_done_set1 = Input(UInt(1.W))
        val cacc_done_set0 = Input(UInt(1.W))
        val cacc_done_set1 = Input(UInt(1.W))
        val cdma_dat_done_set0 = Input(UInt(1.W))
        val cdma_dat_done_set1 = Input(UInt(1.W))
        val cdma_wt_done_set0 = Input(UInt(1.W))
        val cdma_wt_done_set1 = Input(UInt(1.W))
        val cdp_done_set0 = Input(UInt(1.W))
        val cdp_done_set1 = Input(UInt(1.W))
        val pdp_done_set0 = Input(UInt(1.W))
        val pdp_done_set1 = Input(UInt(1.W))
        val rubik_done_set0 = Input(UInt(1.W))
        val rubik_done_set1 = Input(UInt(1.W))
        val sdp_done_set0 = Input(UInt(1.W))
        val sdp_done_set1 = Input(UInt(1.W))
        val bdma_done_status0 = Input(UInt(1.W))
        val bdma_done_status1 = Input(UInt(1.W))
        val cacc_done_status0 = Input(UInt(1.W))
        val cacc_done_status1 = Input(UInt(1.W))
        val cdma_dat_done_status0 = Input(UInt(1.W))
        val cdma_dat_done_status1 = Input(UInt(1.W))
        val cdma_wt_done_status0 = Input(UInt(1.W))
        val cdma_wt_done_status1 = Input(UInt(1.W))
        val cdp_done_status0 = Input(UInt(1.W))
        val cdp_done_status1 = Input(UInt(1.W))
        val pdp_done_status0 = Input(UInt(1.W))
        val pdp_done_status1 = Input(UInt(1.W))
        val rubik_done_status0 = Input(UInt(1.W))
        val rubik_done_status1 = Input(UInt(1.W))
        val sdp_done_status0 = Input(UInt(1.W))
        val sdp_done_status1 = Input(UInt(1.W))

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

    val major = Wire(UInt(8.W))
    val minor = Wire(UInt(16.W))
    val nvdla_glb_s_intr_mask_0_out = Wire(UInt(32.W))
    val nvdla_glb_s_intr_set_0_out = Wire(UInt(32.W))
    val nvdla_glb_s_intr_status_0_out = Wire(UInt(32.W))
    val nvdla_glb_s_nvdla_hw_version_0_out = Wire(UInt(32.W))
    val reg_offset_rd_int = Wire(UInt(12.W))
    val reg_offset_wr = Wire(UInt(32.W))
              
    // leda FM_2_23 on

    io.bdma_done_mask0 := Reg(UInt(1.W))
    io.bdma_done_mask1 := Reg(UInt(1.W))
    io.cacc_done_mask0 := Reg(UInt(1.W))
    io.cacc_done_mask1 := Reg(UInt(1.W)) 
    io.cdma_dat_done_mask0 := Reg(UInt(1.W)) 
    io.cdma_dat_done_mask1 := Reg(UInt(1.W))
    io.cdma_wt_done_mask0 := Reg(UInt(1.W))
    io.cdma_wt_done_mask1 := Reg(UInt(1.W))
    io.cdp_done_mask0 := Reg(UInt(1.W))
    io.cdp_done_mask1:= Reg(UInt(1.W))
    io.pdp_done_mask0 := Reg(UInt(1.W))
    io.pdp_done_mask1 := Reg(UInt(1.W))
    io.reg_rd_data := Wire(UInt(32.W)) //Not really change when clock coming
    io.rubik_done_mask0 := Reg(UInt(1.W))
    io.rubik_done_mask1 := Reg(UInt(1.W))
    io.sdp_done_mask0 := Reg(UInt(1.W))
    io.sdp_done_mask1 := Reg(UInt(1.W))

    reg_offset_wr := Cat("b0".U(20.W), io.reg_offset)

    // SCR signals

    // Address decode

    val nvdla_glb_s_intr_mask_0_wren = (reg_offset_wr === ("h4".U(32.W) &"h00000fff".U(32.W)))&io.reg_wr_en
    val nvdla_glb_s_intr_set_0_wren = (reg_offset_wr === ("h8".U(32.W) &"h00000fff".U(32.W)))&io.reg_wr_en
    val nvdla_glb_s_intr_status_0_wren = (reg_offset_wr === ("hc".U(32.W) &"h00000fff".U(32.W)))&io.reg_wr_en
    val nvdla_glb_s_nvdla_hw_version_0_wren = (reg_offset_wr === ("h0".U(32.W) &"h00000fff".U(32.W)))&io.reg_wr_en

    major = "h31".U(8.W)
    minor = "h3030".U(16.W)

    nvdla_glb_s_intr_mask_0_out := Cat("b0".U(10.W), io.cacc_done_mask1, io.cacc_done_mask0, io.cdma_wt_done_mask1, io.cdma_wt_done_mask0, io.cdma_dat_done_mask1, io.cdma_dat_done_mask0, "b0".U(6.W), io.rubik_done_mask1, io.rubik_done_mask0, io.bdma_done_mask1, io.bdma_done_mask0, io.pdp_done_mask1, io.pdp_done_mask0, io.cdp_done_mask1, io.cdp_done_mask0, io.sdp_done_mask1, io.sdp_done_mask0 )
    nvdla_glb_s_intr_set_0_wren := Cat("b0".U(10.W), io.cacc_done_set1, io.reg_offset_rd_int, io.cdma_wt_done_set1, io.cdma_wt_done_set0, io.cdma_dat_done_set1, io.cdma_dat_done_set0, "b0".U(6.W), io.rubik_done_set1, io.rubik_done_set0, io.bdma_done_set1, io.bdma_done_set0, io.pdp_done_set1, io.pdp_done_set0, io.cdp_done_set1, io.cdp_done_set0, io.sdp_done_set1, io.sdp_done_set0)
    nvdla_glb_s_intr_status_0_out := Cat("b0".U(10.W), io.cacc_done_status1, io.cacc_done_status0, io.cdma_wt_done_status1, io.cdma_wt_done_status0, io.cdma_dat_done_status1, io.cdma_dat_done_status0, "b0".U(6.W), io.rubik_done_status1, io.rubik_done_status0, io.bdma_done_status1, io.bdma_done_status0, io.pdp_done_status1, io.pdp_done_status0, io.cdp_done_status1, io.cdp_done_status0, io.sdp_done_status1, io.sdp_done_status0)
    nvdla_glb_s_nvdla_hw_version_0_out := Cat("b0".U(8.W), minor, major)

    io.sdp_done_set0_trigger := nvdla_glb_s_intr_set_0_wren  //(W563)
    io.sdp_done_status0_trigger := nvdla_glb_s_intr_status_0_wren   //(W563)

    reg_offset_rd_int := io.reg_offset

    // Output mux
    //spyglass disable_block W338, W263 

    when(reg_offset_rd_int === ("h4".U(32.W) &"h00000fff".U(32.W))){
        io.reg_rd_data := nvdla_glb_s_intr_mask_0_out
    }.elsewhen(reg_offset_rd_int === ("h8".U(32.W) &"h00000fff".U(32.W))){
        io.reg_rd_data := nvdla_glb_s_intr_set_0_out
    }.elsewhen(reg_offset_rd_int === ("hc".U(32.W) &"h00000fff".U(32.W))){
        io.reg_rd_data := nvdla_glb_s_intr_status_0_out
    }.elsewhen(reg_offset_rd_int === ("h0".U(32.W) &"h00000fff".U(32.W))){
        io.reg_rd_data := nvdla_glb_s_nvdla_hw_version_0_out
    }.otherwise{
        io.reg_rd_data := "b0".U(32.W)
    }

    //spyglass enable_block W338, W263

    // spyglass disable_block STARC-2.10.1.6, NoConstWithXZ, W443

    // Register flop declarations

    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn) {
        when(nvdla_glb_s_intr_mask_0_wren === "b1".U(1.W)){
            io.bdma_done_mask0 := io.reg_wr_data(6)
            io.bdma_done_mask1 := io.reg_wr_data(7)
            io.cacc_done_mask0 := io.reg_wr_data(20)
            io.cacc_done_mask1 := io.reg_wr_data(21)
            io.cdma_dat_done_mask0 := io.reg_wr_data(16)
            io.cdma_dat_done_mask1 := io.reg_wr_data(17)
            io.cdma_wt_done_mask0 := io.reg_wr_data(18)
            io.cdma_wt_done_mask1 := io.reg_wr_data(19)
            io.cdp_done_mask0 := io.reg_wr_data(2)
            io.cdp_done_mask1 := io.reg_wr_data(3)
            io.pdp_done_mask0 := io.reg_wr_data(4)
            io.pdp_done_mask1 := io.reg_wr_data(5)
            io.rubik_done_mask0 := io.reg_wr_data(8)
            io.rubik_done_mask1 := io.reg_wr_data(9)
            io.sdp_done_mask0 := io.reg_wr_data(0)
            io.sdp_done_mask1 := io.reg_wr_data(1)
        }
    }   



}
    
    

    



    















    



 

