package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_partition_c(implicit val conf: cdmaConfiguration) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val dla_reset_rstn = Input(Bool())
        val test_mode = Input(Bool())
        val direct_reset_ = Input(Bool())
        val nvdla_clk_ovr_on = Input(Clock())
        val global_clk_ovr_on = Input(Clock())

        val tmc2slcg_disable_clock_gating = Input(Bool())

        val accu2sc_credit_vld = Input(Bool()) /* data valid */
        val accu2sc_credit_size = Input(UInt(3.W))

        val cdma2csb_resp_valid = Output(Bool())  /* data valid */
        val cdma2csb_resp_pd = Output(UInt(34.W))    /* pkt_id_width=1 pkt_widths=33,33  */

        val cdma_dat2glb_done_intr_pd = Output(UInt(2.W))

        val cdma_dat2mcif_rd_req_valid = Output(Bool()) /* data valid */
        val cdma_dat2mcif_rd_req_ready = Input(Bool())  /* data return handshake */
        val cdma_dat2mcif_rd_req_pd = Output(UInt((conf.NVDLA_MEM_ADDRESS_WIDTH+15).W))

        val cdma_wt2glb_done_intr_pd = Output(UInt(2.W))

        val cdma_wt2mcif_rd_req_valid = Output(Bool())  /* data valid */
        val cdma_wt2mcif_rd_req_ready = Input(Bool())
        val cdma_wt2mcif_rd_req_pd = Output(UInt((conf.NVDLA_MEM_ADDRESS_WIDTH+15).W))

        val csb2cdma_req_pvld = Input(Bool())    /* data valid */
        val csb2cdma_req_prdy = Output(Bool())  /* data return handshake */
        val csb2cdma_req_pd = Input(UInt(63.W))

        val csb2csc_req_pvld = Input(Bool())    /* data valid */
        val csb2csc_req_prdy = Output(Bool())
        val csb2csc_req_pd = Input(UInt(63.W))

        val csc2csb_resp_valid = Output(Bool()) /* data valid */
        val csc2csb_resp_pd = Output(UInt(34.W))    /* pkt_id_width=1 pkt_widths=33,33  */

        //ifdef NVDLA_SECONDARY_MEMIF_ENABLE
        val cdma_dat2cvif_rd_req_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
        val cdma_dat2cvif_rd_req_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
        val cdma_dat2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))) else None
        val cvif2cdma_dat_rd_rsp_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
        val cvif2cdma_dat_rd_rsp_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
        val cvif2cdma_dat_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))) else None

        val cdma_wt2cvif_rd_req_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
        val cdma_wt2cvif_rd_req_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
        val cdma_wt2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))) else None
        val cvif2cdma_wt_rd_rsp_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
        val cvif2cdma_wt_rd_rsp_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
        val cvif2cdma_wt_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))) else None


        val mcif2cdma_dat_rd_rsp_valid = Input(Bool())     /* data valid */
        val mcif2cdma_dat_rd_rsp_ready = Output(Bool())      /* data return handshake */
        val mcif2cdma_dat_rd_rsp_pd = Input(UInt((conf.NVDLA_MEMIF_WIDTH+conf.NVDLA_CDMA_MEM_MASK_BIT).W))

        val mcif2cdma_wt_rd_rsp_valid = Input(Bool())     /* data valid */
        val mcif2cdma_wt_rd_rsp_ready = Output(Bool())      /* data return handshake */
        val mcif2cdma_wt_rd_rsp_pd = Input(UInt((conf.NVDLA_MEMIF_WIDTH+conf.NVDLA_CDMA_MEM_MASK_BIT).W))

        val pwrbus_ram_pd = Input(UInt(32.W))

        //mac_dat
        val sc2mac_dat_a_pvld = Output(Bool())
        val sc2mac_dat_a_mask = Output(Vec(conf.CSC_ATOMC, Bool()))
        val sc2mac_dat_a_data = Output(Vec(conf.CSC_ATOMC, UInt(conf.CSC_BPE.W)))
        val sc2mac_dat_a_pd = Output(UInt(9.W))

        val sc2mac_dat_b_pvld = Output(Bool())
        val sc2mac_dat_b_mask = Output(Vec(conf.CSC_ATOMC, Bool()))
        val sc2mac_dat_b_data = Output(Vec(conf.CSC_ATOMC, UInt(conf.CSC_BPE.W)))
        val sc2mac_dat_b_pd = Output(UInt(9.W))   

        //mac_wt
        val sc2mac_wt_a_pvld = Output(Bool())
        val sc2mac_wt_a_mask = Output(Vec(conf.CSC_ATOMC, Bool()))
        val sc2mac_wt_a_data = Output(Vec(conf.CSC_ATOMC, UInt(conf.CSC_BPE.W)))
        val sc2mac_wt_a_sel = Output(Vec(conf.CSC_ATOMK_HF, Bool()))

        val sc2mac_wt_b_pvld = Output(Bool())
        val sc2mac_wt_b_mask = Output(Vec(conf.CSC_ATOMC, Bool()))
        val sc2mac_wt_b_data = Output(Vec(conf.CSC_ATOMC, UInt(conf.CSC_BPE.W)))
        val sc2mac_wt_b_sel = Output(Vec(conf.CSC_ATOMK_HF, Bool()))

           
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

////////////////////////////////////////////////////////////////////////
//  NVDLA Partition C:    Reset Sync                                  //
////////////////////////////////////////////////////////////////////////
val u_partition_c_reset = Module(new NV_NVDLA_reset)
u_partition_c_reset.io.nvdla_clk  := io.nvdla_core_clk
u_partition_c_reset.io.dla_reset_rstn := io.dla_reset_rstn
u_partition_c_reset.io.direct_reset_ := io.direct_reset_
u_partition_c_reset.io.test_mode := io.test_mode
val nvdla_core_rstn = u_partition_c_reset.io.synced_rstn

////////////////////////////////////////////////////////////////////////
// SLCG override
////////////////////////////////////////////////////////////////////////
val u_csc_dla_clk_ovr_on_sync = Module(new NV_NVDLA_sync3d)
u_csc_dla_clk_ovr_on_sync.io.clk := io.nvdla_core_clk
u_csc_dla_clk_ovr_on_sync.io.sync_i := io.nvdla_clk_ovr_on
val csc_dla_clk_ovr_on_sync = u_csc_dla_clk_ovr_on_sync.io.sync_o 

val u_cdma_dla_clk_ovr_on_sync = Module(new NV_NVDLA_sync3d)
u_cdma_dla_clk_ovr_on_sync.io.clk := io.nvdla_core_clk
u_cdma_dla_clk_ovr_on_sync.io.sync_i := io.nvdla_clk_ovr_on
val cdma_dla_clk_ovr_on_sync = u_cdma_dla_clk_ovr_on_sync.io.sync_o 

val u_global_csc_clk_ovr_on_sync = Module(new NV_NVDLA_sync3d_s)
u_global_csc_clk_ovr_on_sync.io.clk := io.nvdla_core_clk
u_global_csc_clk_ovr_on_sync.io.prst := nvdla_core_rstn
u_global_csc_clk_ovr_on_sync.io.sync_i := io.global_clk_ovr_on
val csc_global_clk_ovr_on_sync = u_global_csc_clk_ovr_on_sync.io.sync_o 

val u_global_cdma_clk_ovr_on_sync = Module(new NV_NVDLA_sync3d_s)
u_global_cdma_clk_ovr_on_sync.io.clk := io.nvdla_core_clk
u_global_cdma_clk_ovr_on_sync.io.prst := nvdla_core_rstn
u_global_cdma_clk_ovr_on_sync.io.sync_i := io.global_clk_ovr_on
val cdma_global_clk_ovr_on_sync = u_global_cdma_clk_ovr_on_sync.io.sync_o 

////////////////////////////////////////////////////////////////////////
//  NVDLA Partition C:    Convolution DMA                             //
////////////////////////////////////////////////////////////////////////
val u_NV_NVDLA_cdma = Module(new NV_NVDLA_cdma)

u_NV_NVDLA_cdma.io.nvdla_core_clk := io.nvdla_core_clk
u_NV_NVDLA_cdma.io.dla_clk_ovr_on_sync := cdma_dla_clk_ovr_on_sync
u_NV_NVDLA_cdma.io.global_clk_ovr_on_sync := cdma_global_clk_ovr_on_sync
u_NV_NVDLA_cdma.io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating     

//csb
io.cdma2csb_resp_valid := u_NV_NVDLA_cdma.io.cdma2csb_resp_valid
io.cdma2csb_resp_pd := u_NV_NVDLA_cdma.io.cdma2csb_resp_pd
u_NV_NVDLA_cdma.io.csb2cdma_req_pvld := io.csb2cdma_req_pvld
io.csb2cdma_req_prdy := u_NV_NVDLA_cdma.io.csb2cdma_req_prdy 
u_NV_NVDLA_cdma.io.csb2cdma_req_pd := io.csb2cdma_req_pd

//buf dat
val cdma2buf_dat_wr_en = u_NV_NVDLA_cdma.io.cdma2buf_dat_wr_en
val cdma2buf_dat_wr_addr = u_NV_NVDLA_cdma.io.cdma2buf_dat_wr_addr 
val cdma2buf_dat_wr_data = u_NV_NVDLA_cdma.io.cdma2buf_dat_wr_data

//buf wt
val cdma2buf_wt_wr_en = u_NV_NVDLA_cdma.io.cdma2buf_wt_wr_en
val cdma2buf_wt_wr_addr = u_NV_NVDLA_cdma.io.cdma2buf_wt_wr_addr 
val cdma2buf_wt_wr_data = u_NV_NVDLA_cdma.io.cdma2buf_wt_wr_data

//glb
io.cdma_dat2glb_done_intr_pd := u_NV_NVDLA_cdma.io.cdma_dat2glb_done_intr_pd
io.cdma_wt2glb_done_intr_pd := u_NV_NVDLA_cdma.io.cdma_wt2glb_done_intr_pd

if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
//cvif
io.cdma_dat2cvif_rd_req_valid.get := u_NV_NVDLA_cdma.io.cdma_dat2cvif_rd_req_valid.get
u_NV_NVDLA_cdma.io.cdma_dat2cvif_rd_req_ready.get := io.cdma_dat2cvif_rd_req_ready.get
io.cdma_dat2cvif_rd_req_pd.get := u_NV_NVDLA_cdma.io.cdma_dat2cvif_rd_req_pd.get
u_NV_NVDLA_cdma.io.cvif2cdma_dat_rd_rsp_valid.get := io.cvif2cdma_dat_rd_rsp_valid.get
io.cvif2cdma_dat_rd_rsp_ready.get := u_NV_NVDLA_cdma.io.cvif2cdma_dat_rd_rsp_ready.get
u_NV_NVDLA_cdma.io.cvif2cdma_dat_rd_rsp_pd.get := io.cvif2cdma_dat_rd_rsp_pd.get

io.cdma_wt2cvif_rd_req_valid.get := u_NV_NVDLA_cdma.io.cdma_wt2cvif_rd_req_valid.get
u_NV_NVDLA_cdma.io.cdma_wt2cvif_rd_req_ready.get := io.cdma_wt2cvif_rd_req_ready.get
io.cdma_wt2cvif_rd_req_pd.get := u_NV_NVDLA_cdma.io.cdma_wt2cvif_rd_req_pd.get
u_NV_NVDLA_cdma.io.cvif2cdma_wt_rd_rsp_valid.get := io.cvif2cdma_wt_rd_rsp_valid.get
io.cvif2cdma_wt_rd_rsp_ready.get := u_NV_NVDLA_cdma.io.cvif2cdma_wt_rd_rsp_ready.get
u_NV_NVDLA_cdma.io.cvif2cdma_wt_rd_rsp_pd.get := io.cvif2cdma_wt_rd_rsp_pd.get

}

//mcif
io.cdma_dat2mcif_rd_req_valid := u_NV_NVDLA_cdma.io.cdma_dat2mcif_rd_req_valid
u_NV_NVDLA_cdma.io.cdma_dat2mcif_rd_req_ready := io.cdma_dat2mcif_rd_req_ready
io.cdma_dat2mcif_rd_req_pd := u_NV_NVDLA_cdma.io.cdma_dat2mcif_rd_req_pd
u_NV_NVDLA_cdma.io.mcif2cdma_dat_rd_rsp_valid := io.mcif2cdma_dat_rd_rsp_valid
io.mcif2cdma_dat_rd_rsp_ready := u_NV_NVDLA_cdma.io.mcif2cdma_dat_rd_rsp_ready
u_NV_NVDLA_cdma.io.mcif2cdma_dat_rd_rsp_pd := io.mcif2cdma_dat_rd_rsp_pd

io.cdma_wt2mcif_rd_req_valid := u_NV_NVDLA_cdma.io.cdma_wt2mcif_rd_req_valid
u_NV_NVDLA_cdma.io.cdma_wt2mcif_rd_req_ready := io.cdma_wt2mcif_rd_req_ready
io.cdma_wt2mcif_rd_req_pd := u_NV_NVDLA_cdma.io.cdma_wt2mcif_rd_req_pd
u_NV_NVDLA_cdma.io.mcif2cdma_wt_rd_rsp_valid := io.mcif2cdma_wt_rd_rsp_valid
io.mcif2cdma_wt_rd_rsp_ready := u_NV_NVDLA_cdma.io.mcif2cdma_wt_rd_rsp_ready
u_NV_NVDLA_cdma.io.mcif2cdma_wt_rd_rsp_pd := io.mcif2cdma_wt_rd_rsp_pd

//sc
val sc2cdma_dat_pending_req = Wire(Bool())
val sc2cdma_wt_pending_req = Wire(Bool())

val sc2cdma_dat_updt = Wire(Bool())
val sc2cdma_dat_entries = Wire(UInt(15.W))
val sc2cdma_dat_slices = Wire(UInt(14.W))

val sc2cdma_wt_updt = Wire(Bool())
val sc2cdma_wt_kernels = Wire(UInt(14.W))
val sc2cdma_wt_entries = Wire(UInt(15.W))
val sc2cdma_wmb_entries = Wire(UInt(9.W))

u_NV_NVDLA_cdma.io.sc2cdma_dat_pending_req := sc2cdma_dat_pending_req
u_NV_NVDLA_cdma.io.sc2cdma_wt_pending_req := sc2cdma_wt_pending_req

val cdma2sc_dat_pending_ack = u_NV_NVDLA_cdma.io.cdma2sc_dat_pending_ack
val cdma2sc_wt_pending_ack = u_NV_NVDLA_cdma.io.cdma2sc_wt_pending_ack

val cdma2sc_dat_updt = u_NV_NVDLA_cdma.io.cdma2sc_dat_updt
val cdma2sc_dat_entries = u_NV_NVDLA_cdma.io.cdma2sc_dat_entries
val cdma2sc_dat_slices = u_NV_NVDLA_cdma.io.cdma2sc_dat_slices

u_NV_NVDLA_cdma.io.sc2cdma_dat_updt := sc2cdma_dat_updt 
u_NV_NVDLA_cdma.io.sc2cdma_dat_entries := sc2cdma_dat_entries
u_NV_NVDLA_cdma.io.sc2cdma_dat_slices := sc2cdma_dat_slices

val cdma2sc_wt_updt = u_NV_NVDLA_cdma.io.cdma2sc_wt_updt
val cdma2sc_wt_kernels = u_NV_NVDLA_cdma.io.cdma2sc_wt_kernels
val cdma2sc_wt_entries = u_NV_NVDLA_cdma.io.cdma2sc_wt_entries
val cdma2sc_wmb_entries = u_NV_NVDLA_cdma.io.cdma2sc_wmb_entries

u_NV_NVDLA_cdma.io.sc2cdma_wt_updt := sc2cdma_wt_updt
u_NV_NVDLA_cdma.io.sc2cdma_wt_kernels := sc2cdma_wt_kernels
u_NV_NVDLA_cdma.io.sc2cdma_wt_entries := sc2cdma_wt_entries
u_NV_NVDLA_cdma.io.sc2cdma_wmb_entries := sc2cdma_wmb_entries

//pwrbus
u_NV_NVDLA_cdma.io.pwrbus_ram_pd := io.pwrbus_ram_pd

////////////////////////////////////////////////////////////////////////
//  NVDLA Partition C:    Convolution Buffer                         //
////////////////////////////////////////////////////////////////////////
val u_NV_NVDLA_cbuf = Module(new NV_NVDLA_cbuf)

u_NV_NVDLA_cbuf.io.nvdla_core_clk := io.nvdla_core_clk
u_NV_NVDLA_cbuf.io.nvdla_core_rstn := nvdla_core_rstn
u_NV_NVDLA_cbuf.io.pwrbus_ram_pd := io.pwrbus_ram_pd

u_NV_NVDLA_cbuf.io.cdma2buf_wr_en(0) := cdma2buf_dat_wr_en
u_NV_NVDLA_cbuf.io.cdma2buf_wr_addr(0) := cdma2buf_dat_wr_addr(conf.CBUF_ADDR_WIDTH-1, 0)
u_NV_NVDLA_cbuf.io.cdma2buf_wr_data(0) := cdma2buf_dat_wr_data

u_NV_NVDLA_cbuf.io.cdma2buf_wr_en(1) := cdma2buf_wt_wr_en
u_NV_NVDLA_cbuf.io.cdma2buf_wr_addr(1) := cdma2buf_wt_wr_addr(conf.CBUF_ADDR_WIDTH-1, 0)
u_NV_NVDLA_cbuf.io.cdma2buf_wr_data(1) := cdma2buf_wt_wr_data

if(conf.NVDLA_CC_ATOMC_DIV_ATOMK == 1){
    u_NV_NVDLA_cbuf.io.cdma2buf_wr_sel(0) := Fill(conf.CBUF_WR_BANK_SEL_WIDTH, true.B)
    u_NV_NVDLA_cbuf.io.cdma2buf_wr_sel(1) := Fill(conf.CBUF_WR_BANK_SEL_WIDTH, true.B)
}

if(conf.NVDLA_CC_ATOMC_DIV_ATOMK == 2){
    u_NV_NVDLA_cbuf.io.cdma2buf_wr_sel(0) := u_NV_NVDLA_cdma.io.cdma2buf_dat_wr_sel.get
    u_NV_NVDLA_cbuf.io.cdma2buf_wr_sel(1) := u_NV_NVDLA_cdma.io.cdma2buf_wt_wr_sel.get
}

if(conf.NVDLA_CC_ATOMC_DIV_ATOMK == 4){
    u_NV_NVDLA_cbuf.io.cdma2buf_wr_sel(0) := u_NV_NVDLA_cdma.io.cdma2buf_dat_wr_sel.get
    u_NV_NVDLA_cbuf.io.cdma2buf_wr_sel(1) := u_NV_NVDLA_cdma.io.cdma2buf_wt_wr_sel.get
}

val sc2buf_dat_rd_en = Wire(Bool())
val sc2buf_dat_rd_addr = Wire(UInt(conf.CBUF_ADDR_WIDTH.W))
val sc2buf_dat_rd_shift = Wire(UInt(conf.CBUF_RD_DATA_SHIFT_WIDTH.W))
val sc2buf_dat_rd_next1_en = Wire(Bool())
val sc2buf_dat_rd_next1_addr = Wire(UInt(conf.CBUF_ADDR_WIDTH.W))

val sc2buf_wt_rd_en = Wire(Bool())
val sc2buf_wt_rd_addr = Wire(UInt(conf.CBUF_ADDR_WIDTH.W))

u_NV_NVDLA_cbuf.io.sc2buf_dat_rd_en := sc2buf_dat_rd_en
u_NV_NVDLA_cbuf.io.sc2buf_dat_rd_addr := sc2buf_dat_rd_addr
val sc2buf_dat_rd_valid = u_NV_NVDLA_cbuf.io.sc2buf_dat_rd_valid
u_NV_NVDLA_cbuf.io.sc2buf_dat_rd_shift := sc2buf_dat_rd_shift
u_NV_NVDLA_cbuf.io.sc2buf_dat_rd_next1_en := sc2buf_dat_rd_next1_en
u_NV_NVDLA_cbuf.io.sc2buf_dat_rd_next1_addr := sc2buf_dat_rd_next1_addr
val sc2buf_dat_rd_data = u_NV_NVDLA_cbuf.io.sc2buf_dat_rd_data

u_NV_NVDLA_cbuf.io.sc2buf_wt_rd_en := sc2buf_wt_rd_en
u_NV_NVDLA_cbuf.io.sc2buf_wt_rd_addr := sc2buf_wt_rd_addr
val sc2buf_wt_rd_valid = u_NV_NVDLA_cbuf.io.sc2buf_wt_rd_valid
val sc2buf_wt_rd_data = u_NV_NVDLA_cbuf.io.sc2buf_wt_rd_data

////////////////////////////////////////////////////////////////////////
//  NVDLA Partition C:    Convolution Sequence Controller             //
////////////////////////////////////////////////////////////////////////

val u_NV_NVDLA_csc = Module(new NV_NVDLA_csc)
u_NV_NVDLA_csc.io.nvdla_core_clk := io.nvdla_core_clk
u_NV_NVDLA_csc.io.nvdla_core_rstn := nvdla_core_rstn

sc2cdma_dat_pending_req := u_NV_NVDLA_csc.io.sc2cdma_dat_pending_req
sc2cdma_wt_pending_req := u_NV_NVDLA_csc.io.sc2cdma_wt_pending_req

u_NV_NVDLA_csc.io.accu2sc_credit_vld := io.accu2sc_credit_vld
u_NV_NVDLA_csc.io.accu2sc_credit_size := io.accu2sc_credit_size

u_NV_NVDLA_csc.io.cdma2sc_dat_pending_ack := cdma2sc_dat_pending_ack
u_NV_NVDLA_csc.io.cdma2sc_wt_pending_ack := cdma2sc_wt_pending_ack

u_NV_NVDLA_csc.io.csb2csc_req_pvld := io.csb2csc_req_pvld
io.csb2csc_req_prdy := u_NV_NVDLA_csc.io.csb2csc_req_prdy
u_NV_NVDLA_csc.io.csb2csc_req_pd := io.csb2csc_req_pd
io.csc2csb_resp_valid := u_NV_NVDLA_csc.io.csc2csb_resp_valid
io.csc2csb_resp_pd := u_NV_NVDLA_csc.io.csc2csb_resp_pd

u_NV_NVDLA_csc.io.cdma2sc_dat_updt := cdma2sc_dat_updt
u_NV_NVDLA_csc.io.cdma2sc_dat_entries := cdma2sc_dat_entries
u_NV_NVDLA_csc.io.cdma2sc_dat_slices := cdma2sc_dat_slices

sc2cdma_dat_updt := u_NV_NVDLA_csc.io.sc2cdma_dat_updt
sc2cdma_dat_entries := u_NV_NVDLA_csc.io.sc2cdma_dat_entries
sc2cdma_dat_slices := u_NV_NVDLA_csc.io.sc2cdma_dat_slices

u_NV_NVDLA_csc.io.pwrbus_ram_pd := io.pwrbus_ram_pd

sc2buf_dat_rd_en := u_NV_NVDLA_csc.io.sc2buf_dat_rd_en               //|> w
sc2buf_dat_rd_addr := u_NV_NVDLA_csc.io.sc2buf_dat_rd_addr      //|> w
u_NV_NVDLA_csc.io.sc2buf_dat_rd_valid := sc2buf_dat_rd_valid            //|< w
u_NV_NVDLA_csc.io.sc2buf_dat_rd_data := sc2buf_dat_rd_data     //|< w
sc2buf_dat_rd_shift := u_NV_NVDLA_csc.io.sc2buf_dat_rd_shift
sc2buf_dat_rd_next1_en := u_NV_NVDLA_csc.io.sc2buf_dat_rd_next1_en
sc2buf_dat_rd_next1_addr := u_NV_NVDLA_csc.io.sc2buf_dat_rd_next1_addr

sc2buf_wt_rd_en := u_NV_NVDLA_csc.io.sc2buf_wt_rd_en
sc2buf_wt_rd_addr := u_NV_NVDLA_csc.io.sc2buf_wt_rd_addr
u_NV_NVDLA_csc.io.sc2buf_wt_rd_valid := sc2buf_wt_rd_valid
u_NV_NVDLA_csc.io.sc2buf_wt_rd_data := sc2buf_wt_rd_data

io.sc2mac_dat_a_pvld := u_NV_NVDLA_csc.io.sc2mac_dat_a_pvld
io.sc2mac_dat_a_mask := u_NV_NVDLA_csc.io.sc2mac_dat_a_mask
io.sc2mac_dat_a_data := u_NV_NVDLA_csc.io.sc2mac_dat_a_data
io.sc2mac_dat_a_pd := u_NV_NVDLA_csc.io.sc2mac_dat_a_pd

io.sc2mac_dat_b_pvld := u_NV_NVDLA_csc.io.sc2mac_dat_b_pvld
io.sc2mac_dat_b_mask := u_NV_NVDLA_csc.io.sc2mac_dat_b_mask
io.sc2mac_dat_b_data := u_NV_NVDLA_csc.io.sc2mac_dat_b_data
io.sc2mac_dat_b_pd := u_NV_NVDLA_csc.io.sc2mac_dat_b_pd

io.sc2mac_wt_a_pvld := u_NV_NVDLA_csc.io.sc2mac_wt_a_pvld
io.sc2mac_wt_a_mask := u_NV_NVDLA_csc.io.sc2mac_wt_a_mask
io.sc2mac_wt_a_data := u_NV_NVDLA_csc.io.sc2mac_wt_a_data
io.sc2mac_wt_a_sel := u_NV_NVDLA_csc.io.sc2mac_wt_a_sel

io.sc2mac_wt_b_pvld := u_NV_NVDLA_csc.io.sc2mac_wt_b_pvld
io.sc2mac_wt_b_mask := u_NV_NVDLA_csc.io.sc2mac_wt_b_mask
io.sc2mac_wt_b_data := u_NV_NVDLA_csc.io.sc2mac_wt_b_data
io.sc2mac_wt_b_sel := u_NV_NVDLA_csc.io.sc2mac_wt_b_sel

u_NV_NVDLA_csc.io.cdma2sc_wt_updt := cdma2sc_wt_updt         
u_NV_NVDLA_csc.io.cdma2sc_wt_kernels := cdma2sc_wt_kernels
u_NV_NVDLA_csc.io.cdma2sc_wt_entries := cdma2sc_wt_entries
u_NV_NVDLA_csc.io.cdma2sc_wmb_entries := cdma2sc_wmb_entries
sc2cdma_wt_updt := u_NV_NVDLA_csc.io.sc2cdma_wt_updt      
sc2cdma_wt_kernels := u_NV_NVDLA_csc.io.sc2cdma_wt_kernels
sc2cdma_wt_entries := u_NV_NVDLA_csc.io.sc2cdma_wt_entries
sc2cdma_wmb_entries := u_NV_NVDLA_csc.io.sc2cdma_wmb_entries
u_NV_NVDLA_csc.io.dla_clk_ovr_on_sync := csc_dla_clk_ovr_on_sync
u_NV_NVDLA_csc.io.global_clk_ovr_on_sync := csc_global_clk_ovr_on_sync   
u_NV_NVDLA_csc.io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating



}


object NV_NVDLA_partition_cDriver extends App {
  implicit val conf: cdmaConfiguration = new cdmaConfiguration
  chisel3.Driver.execute(args, () => new NV_NVDLA_partition_c())
}
