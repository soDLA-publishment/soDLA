// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_partition_a(implicit val conf: cdmaConfiguration) extends Module {
//     val io = IO(new Bundle {
//         //general clock
//         val test_mode = Input(Bool())

//         val global_clk_ovr_on = Input(Clock())
//         val nvdla_clk_ovr_on = Input(Clock())
//         val nvdla_core_clk = Input(Clock())
  
//         val direct_reset_ = Input(Bool())
//         val dla_reset_rstn = Input(Bool())

//         val tmc2slcg_disable_clock_gating = Input(Bool())

//         val accu2sc_credit_vld = Output(Bool()) /* data valid */
//         val accu2sc_credit_size = Output(UInt(3.W))

//         val cacc2csb_resp_valid = Output(Bool())  /* data valid */
//         val cacc2csb_resp_pd = Output(UInt(34.W))    /* pkt_id_width=1 pkt_widths=33,33  */
//         val cacc2glb_done_intr_pd = Output(UInt(2.W))
//         val csb2cacc_req_pvld = Input(Bool())  /* data valid */
//         val csb2cacc_req_prdy = Output(Bool())  /* data return handshake */
//         val csb2cacc_req_pd = Input(UInt(63.W)) 

//         val cacc2sdp_valid= Output(Bool())  /* data valid */
//         val cacc2sdp_ready = Input(Bool())  /* data return handshake */
//         val cacc2sdp_pd = Output(UInt(conf.CACC_SDP_WIDTH.W))

//         val mac_a2accu_pvld = Input(Bool())  /* data valid */ 
//         val mac_a2accu_mode = Input(Bool())
//         val mac_a2accu_mask = Input(Vec(conf.CMAC_ATOMK_HALF, Bool()))  
//         val mac_a2accu_data = Input(Vec(conf.CMAC_ATOMK_HALF, UInt(conf.CACC_IN_WIDTH.W)))  /* data valid */
//         val mac_a2accu_pd = Input(UInt(9.W))  /* data return handshake */

//         val mac_b2accu_pvld = Input(Bool())  /* data valid */ 
//         val mac_b2accu_mode = Input(Bool())
//         val mac_b2accu_mask = Input(Vec(conf.CMAC_ATOMK_HALF, Bool()))  
//         val mac_b2accu_data = Input(Vec(conf.CMAC_ATOMK_HALF, UInt(conf.CACC_IN_WIDTH.W)))  /* data valid */
//         val mac_b2accu_pd = Input(UInt(9.W))  /* data return handshake */

//         val pwrbus_ram_pd = Input(UInt(32.W))

           
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

// ////////////////////////////////////////////////////////////////////////
// //  NVDLA Partition M:    Reset Syncer                                //
// ////////////////////////////////////////////////////////////////////////
//     val u_partition_a_reset = Module(new NV_NVDLA_reset)
//     u_partition_a_reset.io.nvdla_clk  := io.nvdla_core_clk
//     u_partition_a_reset.io.dla_reset_rstn := io.dla_reset_rstn
//     u_partition_a_reset.io.direct_reset_ := io.direct_reset_
//     u_partition_a_reset.io.test_mode := io.test_mode
//     val nvdla_core_rstn = u_partition_a_reset.io.synced_rstn
// ////////////////////////////////////////////////////////////////////////
// // SLCG override
// ////////////////////////////////////////////////////////////////////////
//     val u_dla_clk_ovr_on_sync = Module(new NV_NVDLA_sync3d)
//     u_dla_clk_ovr_on_sync.io.clk := io.nvdla_core_clk
//     u_dla_clk_ovr_on_sync.io.sync_i := io.nvdla_clk_ovr_on
//     val dla_clk_ovr_on_sync = u_dla_clk_ovr_on_sync.io.sync_o 

//     val u_global_clk_ovr_on_sync = Module(new NV_NVDLA_sync3d_s)
//     u_global_clk_ovr_on_sync.io.clk := io.nvdla_core_clk
//     u_global_clk_ovr_on_sync.io.prst := nvdla_core_rstn
//     u_global_clk_ovr_on_sync.io.sync_i := io.global_clk_ovr_on
//     val global_clk_ovr_on_sync = u_global_clk_ovr_on_sync.io.sync_o 

// ////////////////////////////////////////////////////////////////////////
// //  NVDLA Partition A:     Convolution Accumulator                    //
// ////////////////////////////////////////////////////////////////////////
// //stepheng, modify for cacc verification
//     val u_NV_NVDLA_cacc = Module(new NV_NVDLA_cacc)
//     u_NV_NVDLA_cacc.io.nvdla_core_clk := io.nvdla_core_clk
//     u_NV_NVDLA_cacc.io.nvdla_core_rstn := nvdla_core_rstn
//     u_NV_NVDLA_cacc.io.pwrbus_ram_pd := io.pwrbus_ram_pd

//     u_NV_NVDLA_cacc.io.csb2cacc_req_pvld := io.csb2cacc_req_pvld
//     io.csb2cacc_req_prdy := u_NV_NVDLA_cacc.io.csb2cacc_req_prdy
//     u_NV_NVDLA_cacc.io.csb2cacc_req_pd := io.csb2cacc_req_pd
//     io.cacc2csb_resp_valid := u_NV_NVDLA_cacc.io.cacc2csb_resp_valid
//     io.cacc2csb_resp_pd := u_NV_NVDLA_cacc.io.cacc2csb_resp_pd

//     u_NV_NVDLA_cacc.io.mac_a2accu_pvld := io.mac_a2accu_pvld  /* data valid */ 
//     u_NV_NVDLA_cacc.io.mac_a2accu_mode := io.mac_a2accu_mode
//     u_NV_NVDLA_cacc.io.mac_a2accu_mask := io.mac_a2accu_mask  
//     u_NV_NVDLA_cacc.io.mac_a2accu_data := io.mac_a2accu_data  /* data valid */
//     u_NV_NVDLA_cacc.io.mac_a2accu_pd := io.mac_a2accu_pd  /* data return handshake */

//     u_NV_NVDLA_cacc.io.mac_b2accu_pvld := io.mac_b2accu_pvld  /* data valid */ 
//     u_NV_NVDLA_cacc.io.mac_b2accu_mode := io.mac_b2accu_mode
//     u_NV_NVDLA_cacc.io.mac_b2accu_mask := io.mac_b2accu_mask 
//     u_NV_NVDLA_cacc.io.mac_b2accu_data := io.mac_b2accu_data  /* data valid */
//     u_NV_NVDLA_cacc.io.mac_b2accu_pd := io.mac_b2accu_pd  /* data return handshake */

//     io.cacc2sdp_valid := u_NV_NVDLA_cacc.io.cacc2sdp_valid   /* data valid */
//     u_NV_NVDLA_cacc.io.cacc2sdp_ready := io.cacc2sdp_valid  /* data return handshake */
//     io.cacc2sdp_pd := u_NV_NVDLA_cacc.io.cacc2sdp_pd

//     io.accu2sc_credit_vld := u_NV_NVDLA_cacc.io.accu2sc_credit_vld  /* data valid */
//     io.accu2sc_credit_size := u_NV_NVDLA_cacc.io.accu2sc_credit_size
//     io.cacc2glb_done_intr_pd := u_NV_NVDLA_cacc.io.cacc2glb_done_intr_pd

//     //Port for SLCG
//     u_NV_NVDLA_cacc.io.dla_clk_ovr_on_sync := dla_clk_ovr_on_sync
//     u_NV_NVDLA_cacc.io.global_clk_ovr_on_sync := global_clk_ovr_on_sync
//     u_NV_NVDLA_cacc.io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating




// }


// object NV_NVDLA_partition_aDriver extends App {
//   implicit val conf: cdmaConfiguration = new cdmaConfiguration
//   chisel3.Driver.execute(args, () => new NV_NVDLA_partition_a())
// }
