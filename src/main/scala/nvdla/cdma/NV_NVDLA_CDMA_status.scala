package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_CDMA_status(implicit conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        // clk
        val nvdla_core_clk = Input(Clock())

        // dc2status
        val dc2status_dat_updt = Flipped(ValidIO(new updt_entries_slices_if))

        //img2status
        val img2status_dat_updt = Flipped(ValidIO(new updt_entries_slices_if))
    
        //sc2cdma
        val sc2cdma_dat_updt = Flipped(ValidIO(new updt_entries_slices_if))
        val cdma2sc_dat_updt = ValidIO(new updt_entries_slices_if)

        //status2dma
        val status2dma_valid_slices = Output(UInt(14.W))
        val status2dma_free_entries = Output(UInt(15.W))
        val status2dma_wr_idx = Output(UInt(15.W))

        //state
        val dc2status_state = Input(UInt(2.W))
        val img2status_state = Input(UInt(2.W))
        val wt2status_state = Input(UInt(2.W))

        val dp2reg_consumer = Input(Bool())
        val dp2reg_done = Output(Bool())
        val reg2dp_op_en = Input(Bool())
        val reg2dp_data_bank = Input(UInt(5.W))

        val cdma_wt2glb_done_intr_pd = Output(UInt(2.W))
        val cdma_dat2glb_done_intr_pd = Output(UInt(2.W))

        val sc2cdma_dat_pending_req = Input(UInt(2.W))
        val cdma2sc_dat_pending_ack = Output(UInt(2.W))

        val status2dma_fsm_switch = Output(Bool())


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
////////////////////////////////////////////////////////////////////////
//  control CDMA working status                                       //
////////////////////////////////////////////////////////////////////////
    val status2dma_fsm_switch_out = RegInit(false.B)
    val wt2status_done_d1 = RegInit(false.B)
    val dat2status_done_d1 = RegInit(false.B)
    val wt_done_intr = RegInit("b0".asUInt(2.W))
    val dat_done_intr = RegInit("b0".asUInt(2.W))

    val wt2status_done = (io.wt2status_state === 3.U )
    val dc2status_done = (io.dc2status_state === 3.U )
    val dc2status_pend = (io.dc2status_state === 1.U )
    val img2status_done = (io.img2status_state === 3.U )
    val img2status_pend = (io.img2status_state === 1.U )
    val dat2status_done = (dc2status_done | img2status_done)
    val status2dma_fsm_switch_w = io.reg2dp_op_en & ~status2dma_fsm_switch_out & wt2status_done & dat2status_done

    val wt_done_intr_w = Cat(io.reg2dp_op_en & io.dp2reg_consumer & ~wt2status_done_d1 & wt2status_done, 
                             io.reg2dp_op_en & ~io.dp2reg_consumer & ~wt2status_done_d1 & wt2status_done)

    val dat_done_intr_w = Cat(io.reg2dp_op_en & io.dp2reg_consumer & ~dat2status_done_d1 & dat2status_done, 
                             io.reg2dp_op_en & ~io.dp2reg_consumer & ~dat2status_done_d1 & dat2status_done)

    status2dma_fsm_switch_out := status2dma_fsm_switch_w
    wt2status_done_d1 := io.reg2dp_op_en & wt2status_done
    dat2status_done_d1 := io.reg2dp_op_en & dat2status_done
    wt_done_intr := wt_done_intr_w
    dat_done_intr := dat_done_intr_w

    io.status2dma_fsm_switch := status2dma_fsm_switch_out
    io.dp2reg_done := io.status2dma_fsm_switch
    io.cdma_wt2glb_done_intr_pd := wt_done_intr
    io.cdma_dat2glb_done_intr_pd := dat_done_intr

////////////////////////////////////////////////////////////////////////
//  manage data bank status                                           //
////////////////////////////////////////////////////////////////////////
    val layer_end = RegInit(true.B)
    val status2dma_valid_entries = RegInit("b0".asUInt(15.W))
    val status2dma_valid_slices_out = RegInit("b0".asUInt(14.W))
    val status2dma_free_entries_out = RegInit("b0".asUInt(15.W))
    val status2dma_wr_idx_out = RegInit("b0".asUInt(15.W))
    val real_bank = RegInit("b0".asUInt(6.W))
    val pending_ack = RegInit(false.B)
    val pending_req = RegInit(false.B)

    val layer_end_w = Mux(status2dma_fsm_switch_out, true.B,
                      Mux(io.reg2dp_op_en, false.B,
                      layer_end))
    val real_bank_w = io.reg2dp_data_bank +& 1.U
    val real_bank_reg_en = io.reg2dp_op_en && (real_bank_w =/= real_bank)

// ///// Register flop declarations
    val pending_ack_w = (io.reg2dp_op_en & (dc2status_pend | img2status_pend))
    val update_dma = io.dc2status_dat_updt.valid | io.img2status_dat_updt.valid
    val update_all = update_dma | io.sc2cdma_dat_updt.valid | (pending_ack & pending_req)
    val entries_add = (Fill(15, io.dc2status_dat_updt.valid) & io.dc2status_dat_updt.bits.entries)|
                      (Fill(15, io.img2status_dat_updt.valid) & io.img2status_dat_updt.bits.entries)
    val entries_sub = Mux(io.sc2cdma_dat_updt.valid, io.sc2cdma_dat_updt.bits.entries, "b0".asUInt(15.W))
    val status2dma_valid_entries_w = Mux(pending_ack & pending_req, "b0".asUInt(15.W), status2dma_valid_entries + entries_add - entries_sub)
    val slices_add = (Fill(14, io.dc2status_dat_updt.valid) & io.dc2status_dat_updt.bits.slices)|
                     (Fill(14, io.img2status_dat_updt.valid) & io.img2status_dat_updt.bits.slices)
    val slices_sub = Mux(io.sc2cdma_dat_updt.valid, io.sc2cdma_dat_updt.bits.slices, "b0".asUInt(14.W))
    val status2dma_valid_slices_w = Mux(pending_ack & pending_req, "b0".asUInt(14.W), status2dma_valid_slices_out + slices_add - slices_sub)
    val status2dma_free_entries_w = Cat(real_bank, "b0".asUInt(log2Ceil(conf.NVDLA_CBUF_BANK_DEPTH).W)) - status2dma_valid_entries_w
    val entries_reg_en = (status2dma_free_entries_w =/= status2dma_free_entries_out)
    val status2dma_wr_idx_inc = status2dma_wr_idx_out + entries_add
    val status2dma_wr_idx_inc_wrap = status2dma_wr_idx_out + entries_add - Cat(real_bank, "b0".asUInt(log2Ceil(conf.NVDLA_CBUF_BANK_DEPTH).W))
    val status2dma_wr_idx_overflow = (status2dma_wr_idx_inc >= Cat(real_bank, "b0".asUInt(log2Ceil(conf.NVDLA_CBUF_BANK_DEPTH).W)))
    val status2dma_wr_idx_w = Mux(pending_ack & pending_req, "b0".asUInt(15.W),
                              Mux(~update_dma, status2dma_wr_idx_out,
                              Mux(status2dma_wr_idx_overflow, status2dma_wr_idx_inc_wrap,
                              status2dma_wr_idx_inc)))

    layer_end := layer_end_w
    when(update_all){
        status2dma_valid_entries := status2dma_valid_entries_w
        status2dma_valid_slices_out := status2dma_valid_slices_w
        status2dma_wr_idx_out := status2dma_wr_idx_w
    }
    when(entries_reg_en){
        status2dma_free_entries_out := status2dma_free_entries_w
    }
    when(real_bank_reg_en){
        real_bank := real_bank_w
    }
    pending_ack := pending_ack_w
    pending_req := io.sc2cdma_dat_pending_req
    io.cdma2sc_dat_pending_ack := pending_ack

    io.status2dma_free_entries := status2dma_free_entries_out
    io.status2dma_valid_slices := status2dma_valid_slices_out
    io.status2dma_wr_idx := status2dma_wr_idx_out

    val dat_updt_d = Wire(Bool()) +: 
                     Seq.fill(conf.CDMA_STATUS_LATENCY)(RegInit(false.B))
    val dat_entries_d = Wire(UInt(15.W)) +: 
                        Seq.fill(conf.CDMA_STATUS_LATENCY)(RegInit("b0".asUInt(15.W)))
    val dat_slices_d = Wire(UInt(14.W)) +: 
                       Seq.fill(conf.CDMA_STATUS_LATENCY)(RegInit("b0".asUInt(14.W)))
    dat_updt_d(0) := update_dma
    dat_entries_d(0) := entries_add
    dat_slices_d(0) := slices_add

    for(t <- 0 to conf.CDMA_STATUS_LATENCY-1){
        dat_updt_d(t+1) := dat_updt_d(t)
        when(dat_updt_d(t)){
            dat_entries_d(t+1) := dat_entries_d(t)
            dat_slices_d(t+1) := dat_slices_d(t)
        }
    }  

    io.cdma2sc_dat_updt.valid := dat_updt_d(conf.CDMA_STATUS_LATENCY)
    io.cdma2sc_dat_updt.bits.entries := dat_entries_d(conf.CDMA_STATUS_LATENCY)
    io.cdma2sc_dat_updt.bits.slices := dat_slices_d(conf.CDMA_STATUS_LATENCY)
 
}}


object NV_NVDLA_CDMA_statusDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_CDMA_status())
}
