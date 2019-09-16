package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

//Implementation overview of ping-pong register file.

class NV_NVDLA_MCIF_CSB_reg extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())      

        // Register control interface
        val reg = new reg_control_if

        // Writable register flop/trigger outputs
        val rd_os_cnt = Output(UInt(8.W))
        val wr_os_cnt = Output(UInt(8.W))
        val rd_weight_bdma = Output(UInt(8.W))
        val rd_weight_cdp = Output(UInt(8.W))
        val rd_weight_pdp = Output(UInt(8.W))
        val rd_weight_sdp = Output(UInt(8.W))
        val rd_weight_cdma_dat = Output(UInt(8.W))
        val rd_weight_sdp_b = Output(UInt(8.W))
        val rd_weight_sdp_e = Output(UInt(8.W))
        val rd_weight_sdp_n = Output(UInt(8.W))
        val rd_weight_cdma_wt = Output(UInt(8.W))
        val rd_weight_rbk = Output(UInt(8.W))
        val rd_weight_rsv_0 = Output(UInt(8.W))
        val rd_weight_rsv_1 = Output(UInt(8.W))
        val wr_weight_bdma = Output(UInt(8.W))
        val wr_weight_cdp = Output(UInt(8.W))
        val wr_weight_pdp = Output(UInt(8.W))
        val wr_weight_sdp = Output(UInt(8.W))
        val wr_weight_rbk = Output(UInt(8.W))
        val wr_weight_rsv_0 = Output(UInt(8.W))
        val wr_weight_rsv_1 = Output(UInt(8.W))
        val wr_weight_rsv_2 = Output(UInt(8.W))

        // Read-only register inputs
        val idle = Input(Bool())
    })
//                             
//          ┌─┐       ┌─┐
//       ┌──┘ ┴───────┘ ┴──┐
//       │                 │              |-------------|
//       │       ───       │              |     CSB     |
//       │  ─┬┘       └┬─  │              |-------------|
//       │                 │                    ||
//       │       ─┴─       │                    reg   
//       │                 │                    ||
//       └───┐         ┌───┘              |-------------|
//           │         │                  |     MCIF    |
//           │         │                  |-------------|
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
    val nvdla_mcif_cfg_outstanding_cnt_0_wren = (io.reg.offset === "h14".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_mcif_cfg_rd_weight_0_0_wren = (io.reg.offset === "h00".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_mcif_cfg_rd_weight_1_0_wren = (io.reg.offset === "h04".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_mcif_cfg_rd_weight_2_0_wren = (io.reg.offset === "h08".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_mcif_cfg_wr_weight_0_0_wren = (io.reg.offset === "h0c".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_mcif_cfg_wr_weight_1_0_wren = (io.reg.offset === "h10".asUInt(32.W)) & io.reg.wr_en 
    val nvdla_mcif_status_0_wren = (io.reg.offset === "h18".asUInt(32.W)) & io.reg.wr_en 

    // Output mux
    io.reg.rd_data := MuxLookup(io.reg.offset, "b0".asUInt(32.W), 
    Seq(  
    //nvdla_mcif_cfg_outstanding_cnt_0_out    
    "h14".asUInt(32.W)  -> Cat("b0".asUInt(16.W), io.wr_os_cnt, io.rd_os_cnt),
    //nvdla_mcif_cfg_rd_weight_0_0_out
    "h00".asUInt(32.W)  -> Cat(io.rd_weight_cdp, io.rd_weight_pdp, io.rd_weight_sdp, io.rd_weight_bdma),
    //nvdla_mcif_cfg_rd_weight_1_0_out
    "h04".asUInt(32.W)  -> Cat(io.rd_weight_cdma_dat, io.rd_weight_sdp_e, io.rd_weight_sdp_n, io.rd_weight_sdp_b),
    //nvdla_mcif_cfg_rd_weight_2_0_out
    "h08".asUInt(32.W)  -> Cat(io.rd_weight_rsv_0, io.rd_weight_rsv_1, io.rd_weight_rbk, io.rd_weight_cdma_wt),
    //nvdla_mcif_cfg_wr_weight_0_0_out
    "h0c".asUInt(32.W)  -> Cat(io.wr_weight_cdp, io.wr_weight_pdp, io.wr_weight_sdp, io.wr_weight_bdma),
    //nvdla_mcif_cfg_wr_weight_1_0_out
    "h10".asUInt(32.W)  -> Cat(io.wr_weight_rsv_0, io.wr_weight_rsv_1, io.wr_weight_rsv_2, io.wr_weight_rbk),
    //nvdla_mcif_status_0_out
    "h18".asUInt(32.W)  -> Cat("b0".asUInt(23.W), io.idle, "b0".asUInt(8.W))
    ))

    // Register flop declarations

    // Register: NVDLA_MCIF_CFG_OUTSTANDING_CNT_0    Field: rd_os_cnt
    io.rd_os_cnt := RegEnable(io.reg.wr_data(7, 0), "b11111111".asUInt(8.W), nvdla_mcif_cfg_outstanding_cnt_0_wren)
    // Register: NVDLA_MCIF_CFG_OUTSTANDING_CNT_0    Field: wr_os_cnt
    io.wr_os_cnt := RegEnable(io.reg.wr_data(15, 8), "b11111111".asUInt(8.W), nvdla_mcif_cfg_outstanding_cnt_0_wren)
    // Register: NVDLA_MCIF_CFG_RD_WEIGHT_0_0    Field: rd_weight_bdma
    io.rd_weight_bdma := RegEnable(io.reg.wr_data(7, 0), "b00000001".asUInt(8.W), nvdla_mcif_cfg_rd_weight_0_0_wren)
    // Register: NVDLA_MCIF_CFG_RD_WEIGHT_0_0    Field: rd_weight_cdp
    io.rd_weight_cdp := RegEnable(io.reg.wr_data(31, 24), "b00000001".asUInt(8.W), nvdla_mcif_cfg_rd_weight_0_0_wren)
    // Register: NVDLA_MCIF_CFG_RD_WEIGHT_0_0    Field: rd_weight_pdp
    io.rd_weight_pdp := RegEnable(io.reg.wr_data(23, 16), "b00000001".asUInt(8.W), nvdla_mcif_cfg_rd_weight_0_0_wren)
    // Register: NVDLA_MCIF_CFG_RD_WEIGHT_0_0    Field: rd_weight_sdp
    io.rd_weight_sdp := RegEnable(io.reg.wr_data(15, 8), "b00000001".asUInt(8.W), nvdla_mcif_cfg_rd_weight_0_0_wren)
    // Register: NVDLA_MCIF_CFG_RD_WEIGHT_1_0    Field: rd_weight_cdma_dat
    io.rd_weight_cdma_dat := RegEnable(io.reg.wr_data(31, 24), "b00000001".asUInt(8.W), nvdla_mcif_cfg_rd_weight_1_0_wren)
    // Register: NVDLA_MCIF_CFG_RD_WEIGHT_1_0    Field: rd_weight_sdp_b
    io.rd_weight_sdp_b := RegEnable(io.reg.wr_data(7, 0), "b00000001".asUInt(8.W), nvdla_mcif_cfg_rd_weight_1_0_wren)
    // Register: NVDLA_MCIF_CFG_RD_WEIGHT_1_0    Field: rd_weight_sdp_e
    io.rd_weight_sdp_e := RegEnable(io.reg.wr_data(23, 16), "b00000001".asUInt(8.W), nvdla_mcif_cfg_rd_weight_1_0_wren)
    // Register: NVDLA_MCIF_CFG_RD_WEIGHT_1_0    Field: rd_weight_sdp_n
    io.rd_weight_sdp_n := RegEnable(io.reg.wr_data(15, 8), "b00000001".asUInt(8.W), nvdla_mcif_cfg_rd_weight_1_0_wren)
    // Register: NVDLA_MCIF_CFG_RD_WEIGHT_2_0    Field: rd_weight_cdma_wt
    io.rd_weight_cdma_wt := RegEnable(io.reg.wr_data(7, 0), "b00000001".asUInt(8.W), nvdla_mcif_cfg_rd_weight_2_0_wren)
    // Register: NVDLA_MCIF_CFG_RD_WEIGHT_2_0    Field: rd_weight_rbk
    io.rd_weight_rbk := RegEnable(io.reg.wr_data(15, 8), "b00000001".asUInt(8.W), nvdla_mcif_cfg_rd_weight_2_0_wren)
    // Register: NVDLA_MCIF_CFG_RD_WEIGHT_2_0    Field: rd_weight_rsv_0
    io.rd_weight_rsv_0 := RegEnable(io.reg.wr_data(31, 24), "b00000001".asUInt(8.W), nvdla_mcif_cfg_rd_weight_2_0_wren)
    // Register: NVDLA_MCIF_CFG_RD_WEIGHT_2_0    Field: rd_weight_rsv_1
    io.rd_weight_rsv_1 := RegEnable(io.reg.wr_data(23, 16), "b00000001".asUInt(8.W), nvdla_mcif_cfg_rd_weight_2_0_wren)
    // Register: NVDLA_MCIF_CFG_WR_WEIGHT_0_0    Field: wr_weight_bdma
    io.wr_weight_bdma := RegEnable(io.reg.wr_data(7, 0), "b00000001".asUInt(8.W), nvdla_mcif_cfg_wr_weight_0_0_wren)
    // Register: NVDLA_MCIF_CFG_WR_WEIGHT_0_0    Field: wr_weight_cdp
    io.wr_weight_cdp := RegEnable(io.reg.wr_data(31, 24), "b00000001".asUInt(8.W), nvdla_mcif_cfg_wr_weight_0_0_wren)
    // Register: NVDLA_MCIF_CFG_WR_WEIGHT_0_0    Field: wr_weight_pdp
    io.wr_weight_pdp := RegEnable(io.reg.wr_data(23, 16), "b00000001".asUInt(8.W), nvdla_mcif_cfg_wr_weight_0_0_wren)
    // Register: NVDLA_MCIF_CFG_WR_WEIGHT_0_0    Field: wr_weight_sdp
    io.wr_weight_sdp := RegEnable(io.reg.wr_data(15, 8), "b00000001".asUInt(8.W), nvdla_mcif_cfg_wr_weight_0_0_wren)
    // Register: NVDLA_MCIF_CFG_WR_WEIGHT_1_0    Field: wr_weight_rbk
    io.wr_weight_rbk := RegEnable(io.reg.wr_data(7, 0), "b00000001".asUInt(8.W), nvdla_mcif_cfg_wr_weight_1_0_wren)
    // Register: NVDLA_MCIF_CFG_WR_WEIGHT_1_0    Field: wr_weight_rsv_0
    io.wr_weight_rsv_0 := RegEnable(io.reg.wr_data(31, 24), "b00000001".asUInt(8.W), nvdla_mcif_cfg_wr_weight_1_0_wren)
    // Register: NVDLA_MCIF_CFG_WR_WEIGHT_1_0    Field: wr_weight_rsv_1
    io.wr_weight_rsv_1 := RegEnable(io.reg.wr_data(23, 16), "b00000001".asUInt(8.W), nvdla_mcif_cfg_wr_weight_1_0_wren)
    // Register: NVDLA_MCIF_CFG_WR_WEIGHT_1_0    Field: wr_weight_rsv_2
    io.wr_weight_rsv_2 := RegEnable(io.reg.wr_data(15, 8), "b00000001".asUInt(8.W), nvdla_mcif_cfg_wr_weight_1_0_wren)
}}