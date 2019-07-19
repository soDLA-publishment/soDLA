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
        val reg_rd_data = Output(UInt(32.W))
        val reg_offset = Input(UInt(12.W))
        val reg_wr_data = Input(UInt(32.W))
        val reg_wr_en = Input(Bool())

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
    val nvdla_mcif_cfg_outstanding_cnt_0_wren = (io.reg_offset === "h14".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_mcif_cfg_rd_weight_0_0_wren = (io.reg_offset === "h00".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_mcif_cfg_rd_weight_1_0_wren = (io.reg_offset === "h04".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_mcif_cfg_rd_weight_2_0_wren = (io.reg_offset === "h08".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_mcif_cfg_wr_weight_0_0_wren = (io.reg_offset === "h0c".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_mcif_cfg_wr_weight_1_0_wren = (io.reg_offset === "h10".asUInt(32.W)) & io.reg_wr_en ;   
    val nvdla_mcif_status_0_wren = (io.reg_offset === "h18".asUInt(32.W)) & io.reg_wr_en ;   

    val nvdla_mcif_cfg_outstanding_cnt_0_out = Cat("b0".asUInt(16.W), io.wr_os_cnt, io.rd_os_cnt)
    val nvdla_mcif_cfg_rd_weight_0_0_out = Cat(io.rd_weight_cdp, io.rd_weight_pdp, io.rd_weight_sdp, io.rd_weight_bdma)
    val nvdla_mcif_cfg_rd_weight_1_0_out = Cat(io.rd_weight_cdma_dat, io.rd_weight_sdp_e, io.rd_weight_sdp_n, io.rd_weight_sdp_b)
    val nvdla_mcif_cfg_rd_weight_2_0_out = Cat(io.rd_weight_rsv_0, io.rd_weight_rsv_1, io.rd_weight_rbk, io.rd_weight_cdma_wt)
    val nvdla_mcif_cfg_wr_weight_0_0_out = Cat(io.wr_weight_cdp, io.wr_weight_pdp, io.wr_weight_sdp, io.wr_weight_bdma)
    val nvdla_mcif_cfg_wr_weight_1_0_out = Cat(io.wr_weight_rsv_0, io.wr_weight_rsv_1, io.wr_weight_rsv_2, io.wr_weight_rbk)
    val nvdla_mcif_status_0_out = Cat("b0".asUInt(23.W), io.idle, "b0".asUInt(8.W))

    // Output mux

    io.reg_rd_data := MuxLookup(io.reg_offset, "b0".asUInt(32.W), 
    Seq(      
    "h14".asUInt(32.W)  -> nvdla_mcif_cfg_outstanding_cnt_0_out,
    "h00".asUInt(32.W)  -> nvdla_mcif_cfg_rd_weight_0_0_out,
    "h04".asUInt(32.W)  -> nvdla_mcif_cfg_rd_weight_1_0_out,
    "h08".asUInt(32.W)  -> nvdla_mcif_cfg_rd_weight_2_0_out,
    "h0c".asUInt(32.W)  -> nvdla_mcif_cfg_wr_weight_0_0_out,
    "h10".asUInt(32.W)  -> nvdla_mcif_cfg_wr_weight_1_0_out,
    "h18".asUInt(32.W)  -> nvdla_mcif_status_0_out
    ))

    // Register flop declarations

    val rd_os_cnt_out = RegInit("b11111111".asUInt(8.W))
    val wr_os_cnt_out = RegInit("b11111111".asUInt(8.W))
    val rd_weight_bdma_out = RegInit("b00000001".asUInt(8.W))
    val rd_weight_cdp_out = RegInit("b00000001".asUInt(8.W))
    val rd_weight_pdp_out = RegInit("b00000001".asUInt(8.W))
    val rd_weight_sdp_out = RegInit("b00000001".asUInt(8.W))
    val rd_weight_cdma_dat_out = RegInit("b00000001".asUInt(8.W))
    val rd_weight_sdp_b_out = RegInit("b00000001".asUInt(8.W))
    val rd_weight_sdp_e_out = RegInit("b00000001".asUInt(8.W))
    val rd_weight_sdp_n_out = RegInit("b00000001".asUInt(8.W))
    val rd_weight_cdma_wt_out = RegInit("b00000001".asUInt(8.W))
    val rd_weight_rbk_out = RegInit("b00000001".asUInt(8.W))
    val rd_weight_rsv_0_out = RegInit("b00000001".asUInt(8.W))
    val rd_weight_rsv_1_out = RegInit("b00000001".asUInt(8.W))
    val wr_weight_bdma_out = RegInit("b00000001".asUInt(8.W))
    val wr_weight_cdp_out = RegInit("b00000001".asUInt(8.W))
    val wr_weight_pdp_out = RegInit("b00000001".asUInt(8.W))
    val wr_weight_sdp_out = RegInit("b00000001".asUInt(8.W))
    val wr_weight_rbk_out = RegInit("b00000001".asUInt(8.W))
    val wr_weight_rsv_0_out = RegInit("b00000001".asUInt(8.W))
    val wr_weight_rsv_1_out = RegInit("b00000001".asUInt(8.W))
    val wr_weight_rsv_2_out = RegInit("b00000001".asUInt(8.W))

// Register: NVDLA_MCIF_CFG_OUTSTANDING_CNT_0    Field: rd_os_cnt
    when(nvdla_mcif_cfg_outstanding_cnt_0_wren){
        rd_os_cnt_out := io.reg_wr_data(7, 0)
    }

  // Register: NVDLA_MCIF_CFG_OUTSTANDING_CNT_0    Field: wr_os_cnt
    when(nvdla_mcif_cfg_outstanding_cnt_0_wren){
        wr_os_cnt_out := io.reg_wr_data(15, 8)
    }

  // Register: NVDLA_MCIF_CFG_RD_WEIGHT_0_0    Field: rd_weight_bdma
    when(nvdla_mcif_cfg_rd_weight_0_0_wren){
        rd_weight_bdma_out := io.reg_wr_data(7, 0)
    }

  // Register: NVDLA_MCIF_CFG_RD_WEIGHT_0_0    Field: rd_weight_cdp
    when(nvdla_mcif_cfg_rd_weight_0_0_wren){
        rd_weight_cdp_out := io.reg_wr_data(31, 24)
    }

  // Register: NVDLA_MCIF_CFG_RD_WEIGHT_0_0    Field: rd_weight_pdp
    when(nvdla_mcif_cfg_rd_weight_0_0_wren){
        rd_weight_pdp_out := io.reg_wr_data(23, 16)
    }

  // Register: NVDLA_MCIF_CFG_RD_WEIGHT_0_0    Field: rd_weight_sdp
    when(nvdla_mcif_cfg_rd_weight_0_0_wren){
        rd_weight_sdp_out := io.reg_wr_data(15, 8)
    }

  // Register: NVDLA_MCIF_CFG_RD_WEIGHT_1_0    Field: rd_weight_cdma_dat
    when(nvdla_mcif_cfg_rd_weight_1_0_wren){
        rd_weight_cdma_dat_out := io.reg_wr_data(31, 24)
    }

  // Register: NVDLA_MCIF_CFG_RD_WEIGHT_1_0    Field: rd_weight_sdp_b
    when(nvdla_mcif_cfg_rd_weight_1_0_wren){
        rd_weight_sdp_b_out := io.reg_wr_data(7, 0)
    }

  // Register: NVDLA_MCIF_CFG_RD_WEIGHT_1_0    Field: rd_weight_sdp_e
    when(nvdla_mcif_cfg_rd_weight_1_0_wren){
        rd_weight_sdp_e_out := io.reg_wr_data(23, 16)
    }

  // Register: NVDLA_MCIF_CFG_RD_WEIGHT_1_0    Field: rd_weight_sdp_n
    when(nvdla_mcif_cfg_rd_weight_1_0_wren){
        rd_weight_sdp_n_out := io.reg_wr_data(15, 8)
    }

  // Register: NVDLA_MCIF_CFG_RD_WEIGHT_2_0    Field: rd_weight_cdma_wt
    when(nvdla_mcif_cfg_rd_weight_2_0_wren){
        rd_weight_cdma_wt_out := io.reg_wr_data(7, 0)
    }

  // Register: NVDLA_MCIF_CFG_RD_WEIGHT_2_0    Field: rd_weight_rbk
    when(nvdla_mcif_cfg_rd_weight_2_0_wren){
        rd_weight_rbk_out := io.reg_wr_data(15, 8)
    }

  // Register: NVDLA_MCIF_CFG_RD_WEIGHT_2_0    Field: rd_weight_rsv_0
    when(nvdla_mcif_cfg_rd_weight_2_0_wren){
        rd_weight_rsv_0_out := io.reg_wr_data(31, 24)
    }

  // Register: NVDLA_MCIF_CFG_RD_WEIGHT_2_0    Field: rd_weight_rsv_1
    when(nvdla_mcif_cfg_rd_weight_2_0_wren){
        rd_weight_rsv_1_out := io.reg_wr_data(23, 16)
    }

  // Register: NVDLA_MCIF_CFG_WR_WEIGHT_0_0    Field: wr_weight_bdma
    when(nvdla_mcif_cfg_wr_weight_0_0_wren){
        wr_weight_bdma_out := io.reg_wr_data(7, 0)
    }

  // Register: NVDLA_MCIF_CFG_WR_WEIGHT_0_0    Field: wr_weight_cdp
    when(nvdla_mcif_cfg_wr_weight_0_0_wren){
        wr_weight_cdp_out := io.reg_wr_data(31, 24)
    }

  // Register: NVDLA_MCIF_CFG_WR_WEIGHT_0_0    Field: wr_weight_pdp
    when(nvdla_mcif_cfg_wr_weight_0_0_wren){
        wr_weight_pdp_out := io.reg_wr_data(23, 16)
    }

  // Register: NVDLA_MCIF_CFG_WR_WEIGHT_0_0    Field: wr_weight_sdp
    when(nvdla_mcif_cfg_wr_weight_0_0_wren){
        wr_weight_sdp_out := io.reg_wr_data(15, 8)
    }

  // Register: NVDLA_MCIF_CFG_WR_WEIGHT_1_0    Field: wr_weight_rbk
    when(nvdla_mcif_cfg_wr_weight_1_0_wren){
        wr_weight_rbk_out := io.reg_wr_data(7, 0)
    }

  // Register: NVDLA_MCIF_CFG_WR_WEIGHT_1_0    Field: wr_weight_rsv_0
    when(nvdla_mcif_cfg_wr_weight_1_0_wren){
        wr_weight_rsv_0_out := io.reg_wr_data(31, 24)
    }

  // Register: NVDLA_MCIF_CFG_WR_WEIGHT_1_0    Field: wr_weight_rsv_1
    when(nvdla_mcif_cfg_wr_weight_1_0_wren){
        wr_weight_rsv_1_out := io.reg_wr_data(23, 16)
    }

  // Register: NVDLA_MCIF_CFG_WR_WEIGHT_1_0    Field: wr_weight_rsv_2
    when(nvdla_mcif_cfg_wr_weight_1_0_wren){
        wr_weight_rsv_2_out := io.reg_wr_data(15, 8)
    }

    io.rd_os_cnt := rd_os_cnt_out
    io.wr_os_cnt := wr_os_cnt_out
    io.rd_weight_bdma := rd_weight_bdma_out
    io.rd_weight_cdp := rd_weight_cdp_out
    io.rd_weight_pdp := rd_weight_pdp_out
    io.rd_weight_sdp := rd_weight_sdp_out
    io.rd_weight_cdma_dat := rd_weight_cdma_dat_out
    io.rd_weight_sdp_b := rd_weight_sdp_b_out
    io.rd_weight_sdp_e := rd_weight_sdp_e_out
    io.rd_weight_sdp_n := rd_weight_sdp_n_out
    io.rd_weight_cdma_wt := rd_weight_cdma_wt_out
    io.rd_weight_rbk := rd_weight_rbk_out
    io.rd_weight_rsv_0 := rd_weight_rsv_0_out
    io.rd_weight_rsv_1 := rd_weight_rsv_1_out
    io.wr_weight_bdma := wr_weight_bdma_out
    io.wr_weight_cdp := wr_weight_cdp_out
    io.wr_weight_pdp := wr_weight_pdp_out
    io.wr_weight_sdp := wr_weight_sdp_out
    io.wr_weight_rbk := wr_weight_rbk_out
    io.wr_weight_rsv_0 := wr_weight_rsv_0_out
    io.wr_weight_rsv_1 := wr_weight_rsv_1_out
    io.wr_weight_rsv_2 := wr_weight_rsv_2_out
}}