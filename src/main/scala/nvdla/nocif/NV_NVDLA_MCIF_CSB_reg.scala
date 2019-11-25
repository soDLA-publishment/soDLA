package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._


class NV_NVDLA_MCIF_CSB_reg(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())      

        // Register control interface
        val reg = new reg_control_if

        // Writable register flop/trigger outputs
        val field = new mcif_reg_flop_outputs

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
    "h14".asUInt(32.W)  -> Cat("b0".asUInt(16.W), io.field.wr_os_cnt, io.field.rd_os_cnt),
    //nvdla_mcif_cfg_rd_weight_0_0_out
    "h00".asUInt(32.W)  -> Cat(io.field.rd_weight_client(conf.tieoff_axid_cdp), io.field.rd_weight_client(conf.tieoff_axid_pdp), io.field.rd_weight_client(conf.tieoff_axid_sdp), io.field.rd_weight_client(conf.tieoff_axid_bdma)),
    //nvdla_mcif_cfg_rd_weight_1_0_out
    "h04".asUInt(32.W)  -> Cat(io.field.rd_weight_client(conf.tieoff_axid_cdma_dat), io.field.rd_weight_client(conf.tieoff_axid_sdp_e), io.field.rd_weight_client(conf.tieoff_axid_sdp_n), io.field.rd_weight_client(conf.tieoff_axid_sdp_b)),
    //nvdla_mcif_cfg_rd_weight_2_0_out
    "h08".asUInt(32.W)  -> Cat(io.field.rd_weight_rsv_0, io.field.rd_weight_rsv_1, io.field.rd_weight_client(conf.tieoff_axid_rbk), io.field.rd_weight_client(conf.tieoff_axid_cdma_wt)),
    //nvdla_mcif_cfg_wr_weight_0_0_out
    "h0c".asUInt(32.W)  -> Cat(io.field.wr_weight_client(conf.tieoff_axid_cdp), io.field.wr_weight_client(conf.tieoff_axid_pdp), io.field.wr_weight_client(conf.tieoff_axid_sdp), io.field.wr_weight_client(conf.tieoff_axid_bdma)),
    //nvdla_mcif_cfg_wr_weight_1_0_out
    "h10".asUInt(32.W)  -> Cat(io.field.wr_weight_rsv_0, io.field.wr_weight_rsv_1, io.field.wr_weight_rsv_2, io.field.wr_weight_client(conf.tieoff_axid_rbk)),
    //nvdla_mcif_status_0_out
    "h18".asUInt(32.W)  -> Cat("b0".asUInt(23.W), io.idle, "b0".asUInt(8.W))
    ))

    // Register flop declarations

    // Register: NVDLA_MCIF_CFG_OUTSTANDING_CNT_0    Field: rd_os_cnt
    io.field.rd_os_cnt := RegEnable(io.reg.wr_data(7, 0), "b11111111".asUInt(8.W), nvdla_mcif_cfg_outstanding_cnt_0_wren)
    // Register: NVDLA_MCIF_CFG_OUTSTANDING_CNT_0    Field: wr_os_cnt
    io.field.wr_os_cnt := RegEnable(io.reg.wr_data(15, 8), "b11111111".asUInt(8.W), nvdla_mcif_cfg_outstanding_cnt_0_wren)
    // Register: NVDLA_MCIF_CFG_RD_WEIGHT_0_0    Field: rd_weight_bdma
    io.field.rd_weight_client(conf.tieoff_axid_bdma) := RegEnable(io.reg.wr_data(7, 0), "b00000001".asUInt(8.W), nvdla_mcif_cfg_rd_weight_0_0_wren)
    // Register: NVDLA_MCIF_CFG_RD_WEIGHT_0_0    Field: rd_weight_cdp
    io.field.rd_weight_client(conf.tieoff_axid_cdp) := RegEnable(io.reg.wr_data(31, 24), "b00000001".asUInt(8.W), nvdla_mcif_cfg_rd_weight_0_0_wren)
    // Register: NVDLA_MCIF_CFG_RD_WEIGHT_0_0    Field: rd_weight_pdp
    io.field.rd_weight_client(conf.tieoff_axid_pdp) := RegEnable(io.reg.wr_data(23, 16), "b00000001".asUInt(8.W), nvdla_mcif_cfg_rd_weight_0_0_wren)
    // Register: NVDLA_MCIF_CFG_RD_WEIGHT_0_0    Field: rd_weight_sdp
    io.field.rd_weight_client(conf.tieoff_axid_sdp) := RegEnable(io.reg.wr_data(15, 8), "b00000001".asUInt(8.W), nvdla_mcif_cfg_rd_weight_0_0_wren)
    // Register: NVDLA_MCIF_CFG_RD_WEIGHT_1_0    Field: rd_weight_cdma_dat
    io.field.rd_weight_client(conf.tieoff_axid_cdma_dat) := RegEnable(io.reg.wr_data(31, 24), "b00000001".asUInt(8.W), nvdla_mcif_cfg_rd_weight_1_0_wren)
    // Register: NVDLA_MCIF_CFG_RD_WEIGHT_1_0    Field: rd_weight_sdp_b
    io.field.rd_weight_client(conf.tieoff_axid_sdp_b) := RegEnable(io.reg.wr_data(7, 0), "b00000001".asUInt(8.W), nvdla_mcif_cfg_rd_weight_1_0_wren)
    // Register: NVDLA_MCIF_CFG_RD_WEIGHT_1_0    Field: rd_weight_sdp_e
    io.field.rd_weight_client(conf.tieoff_axid_sdp_e) := RegEnable(io.reg.wr_data(23, 16), "b00000001".asUInt(8.W), nvdla_mcif_cfg_rd_weight_1_0_wren)
    // Register: NVDLA_MCIF_CFG_RD_WEIGHT_1_0    Field: rd_weight_sdp_n
    io.field.rd_weight_client(conf.tieoff_axid_sdp_n) := RegEnable(io.reg.wr_data(15, 8), "b00000001".asUInt(8.W), nvdla_mcif_cfg_rd_weight_1_0_wren)
    // Register: NVDLA_MCIF_CFG_RD_WEIGHT_2_0    Field: rd_weight_cdma_wt
    io.field.rd_weight_client(conf.tieoff_axid_cdma_wt) := RegEnable(io.reg.wr_data(7, 0), "b00000001".asUInt(8.W), nvdla_mcif_cfg_rd_weight_2_0_wren)
    // Register: NVDLA_MCIF_CFG_RD_WEIGHT_2_0    Field: rd_weight_rbk
    io.field.rd_weight_client(conf.tieoff_axid_rbk) := RegEnable(io.reg.wr_data(15, 8), "b00000001".asUInt(8.W), nvdla_mcif_cfg_rd_weight_2_0_wren)
    // Register: NVDLA_MCIF_CFG_RD_WEIGHT_2_0    Field: rd_weight_rsv_0
    io.field.rd_weight_rsv_0 := RegEnable(io.reg.wr_data(31, 24), "b00000001".asUInt(8.W), nvdla_mcif_cfg_rd_weight_2_0_wren)
    // Register: NVDLA_MCIF_CFG_RD_WEIGHT_2_0    Field: rd_weight_rsv_1
    io.field.rd_weight_rsv_1 := RegEnable(io.reg.wr_data(23, 16), "b00000001".asUInt(8.W), nvdla_mcif_cfg_rd_weight_2_0_wren)
    // Register: NVDLA_MCIF_CFG_WR_WEIGHT_0_0    Field: wr_weight_bdma
    io.field.wr_weight_client(conf.tieoff_axid_bdma) := RegEnable(io.reg.wr_data(7, 0), "b00000001".asUInt(8.W), nvdla_mcif_cfg_wr_weight_0_0_wren)
    // Register: NVDLA_MCIF_CFG_WR_WEIGHT_0_0    Field: wr_weight_cdp
    io.field.wr_weight_client(conf.tieoff_axid_cdp) := RegEnable(io.reg.wr_data(31, 24), "b00000001".asUInt(8.W), nvdla_mcif_cfg_wr_weight_0_0_wren)
    // Register: NVDLA_MCIF_CFG_WR_WEIGHT_0_0    Field: wr_weight_pdp
    io.field.wr_weight_client(conf.tieoff_axid_pdp) := RegEnable(io.reg.wr_data(23, 16), "b00000001".asUInt(8.W), nvdla_mcif_cfg_wr_weight_0_0_wren)
    // Register: NVDLA_MCIF_CFG_WR_WEIGHT_0_0    Field: wr_weight_sdp
    io.field.wr_weight_client(conf.tieoff_axid_sdp) := RegEnable(io.reg.wr_data(15, 8), "b00000001".asUInt(8.W), nvdla_mcif_cfg_wr_weight_0_0_wren)
    // Register: NVDLA_MCIF_CFG_WR_WEIGHT_1_0    Field: wr_weight_rbk
    io.field.wr_weight_client(conf.tieoff_axid_rbk) := RegEnable(io.reg.wr_data(7, 0), "b00000001".asUInt(8.W), nvdla_mcif_cfg_wr_weight_1_0_wren)
    // Register: NVDLA_MCIF_CFG_WR_WEIGHT_1_0    Field: wr_weight_rsv_0
    io.field.wr_weight_rsv_0 := RegEnable(io.reg.wr_data(31, 24), "b00000001".asUInt(8.W), nvdla_mcif_cfg_wr_weight_1_0_wren)
    // Register: NVDLA_MCIF_CFG_WR_WEIGHT_1_0    Field: wr_weight_rsv_1
    io.field.wr_weight_rsv_1 := RegEnable(io.reg.wr_data(23, 16), "b00000001".asUInt(8.W), nvdla_mcif_cfg_wr_weight_1_0_wren)
    // Register: NVDLA_MCIF_CFG_WR_WEIGHT_1_0    Field: wr_weight_rsv_2
    io.field.wr_weight_rsv_2 := RegEnable(io.reg.wr_data(15, 8), "b00000001".asUInt(8.W), nvdla_mcif_cfg_wr_weight_1_0_wren)
}}