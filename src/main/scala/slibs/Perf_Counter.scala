package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
 
class PERF_COUNTER extends Module{

    val io = IO(new Bundle{
        val rd_req_vld = Input(Bool())
        val rd_req_rdy = Input(Bool())

        val rsp_fifo_ready = Input(Bool())
        val reg2dp_dma_en = Input(Bool())
        val reg2dp_op_en = Input(Bool())
        val status2dma_fsm_switch = Input(Bool())

        val dp2reg_rd_latency = Output(UInt(32.W))
        val dp2reg_rd_stall = Output(UInt(32.W))

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
    val img_rd_stall_inc = RegInit(false.B)
    val img_rd_stall_clr = RegInit(false.B)
    val img_rd_stall_cen = RegInit(false.B)

    img_rd_stall_inc := io.rd_req_vld & ~io.rd_req_rdy & io.reg2dp_dma_en
    img_rd_stall_clr := io.status2dma_fsm_switch & io.reg2dp_dma_en
    img_rd_stall_cen := io.reg2dp_op_en & io.reg2dp_dma_en

    val dp2reg_rd_stall_dec = false.B

    // stl adv logic
    val stl_adv = img_rd_stall_inc ^ dp2reg_rd_stall_dec

    // stl cnt logic
    val stl_cnt_cur = RegInit("b0".asUInt(32.W))
    val stl_cnt_ext = Wire(UInt(34.W))
    val stl_cnt_inc = Wire(UInt(34.W))
    val stl_cnt_dec = Wire(UInt(34.W))
    val stl_cnt_mod = Wire(UInt(34.W))
    val stl_cnt_new = Wire(UInt(34.W))
    val stl_cnt_nxt = Wire(UInt(34.W))

    stl_cnt_ext := stl_cnt_cur
    stl_cnt_inc := stl_cnt_cur +& 1.U
    stl_cnt_dec := stl_cnt_cur -& 1.U
    stl_cnt_mod := Mux(img_rd_stall_inc && !dp2reg_rd_stall_dec, stl_cnt_inc, 
                   Mux(!img_rd_stall_inc && dp2reg_rd_stall_dec, stl_cnt_dec,
                   stl_cnt_ext))
    stl_cnt_new := Mux(stl_adv, stl_cnt_mod, stl_cnt_ext)
    stl_cnt_nxt := Mux(img_rd_stall_clr, 0.U, stl_cnt_new)

    // stl flops
    when(img_rd_stall_cen){
        stl_cnt_cur := stl_cnt_nxt
    }

    // stl output logic
    val dc_rd_latency_inc = RegInit(false.B)
    val dc_rd_latency_dec = RegInit(false.B)
    val dc_rd_latency_clr = RegInit(false.B)
    val dc_rd_latency_cen = RegInit(false.B)

    io.dp2reg_rd_stall := stl_cnt_cur

    dc_rd_latency_inc := io.rd_req_vld & io.rd_req_rdy & io.reg2dp_dma_en
    dc_rd_latency_dec := io.rsp_fifo_ready & io.reg2dp_dma_en
    dc_rd_latency_clr := io.status2dma_fsm_switch
    dc_rd_latency_cen := io.reg2dp_op_en & io.reg2dp_dma_en

    val outs_dp2reg_rd_latency = Wire(UInt(9.W))

    val ltc_1_inc = (outs_dp2reg_rd_latency =/=511.U) & dc_rd_latency_inc
    val ltc_1_dec = (outs_dp2reg_rd_latency =/=511.U) & dc_rd_latency_dec

    // ltc_1 adv logic
    val ltc_1_adv = ltc_1_inc ^ ltc_1_dec

    // ltc_1 cnt logic
    val ltc_1_cnt_cur = RegInit("b0".asUInt(9.W))
    val ltc_1_cnt_ext = Wire(UInt(11.W))
    val ltc_1_cnt_inc = Wire(UInt(11.W))
    val ltc_1_cnt_dec = Wire(UInt(11.W))
    val ltc_1_cnt_mod = Wire(UInt(11.W))
    val ltc_1_cnt_new = Wire(UInt(11.W))
    val ltc_1_cnt_nxt = Wire(UInt(11.W))

    ltc_1_cnt_ext := ltc_1_cnt_cur
    ltc_1_cnt_inc := ltc_1_cnt_cur +& 1.U
    ltc_1_cnt_dec := ltc_1_cnt_cur -& 1.U
    ltc_1_cnt_mod := Mux(ltc_1_inc && !ltc_1_dec, ltc_1_cnt_inc, 
                     Mux((!ltc_1_inc && ltc_1_dec), ltc_1_cnt_dec,
                     ltc_1_cnt_ext))
    ltc_1_cnt_new := Mux(ltc_1_adv, ltc_1_cnt_mod, ltc_1_cnt_ext)
    ltc_1_cnt_nxt := Mux(dc_rd_latency_clr, 0.U, ltc_1_cnt_new)

    // ltc_1 flops
    when(dc_rd_latency_cen){
        ltc_1_cnt_cur := ltc_1_cnt_nxt
    }

    // ltc_1 output logic
    outs_dp2reg_rd_latency := ltc_1_cnt_cur

    val ltc_2_dec = false.B
    val ltc_2_inc = (~ (io.dp2reg_rd_latency.andR)) & (outs_dp2reg_rd_latency.orR)

    // ltc_2 adv logic
    val ltc_2_adv = ltc_2_inc ^ ltc_2_dec

    // ltc_2 cnt logic
    val ltc_2_cnt_cur = RegInit("b0".asUInt(32.W))
    val ltc_2_cnt_ext = Wire(UInt(34.W))
    val ltc_2_cnt_inc = Wire(UInt(34.W))
    val ltc_2_cnt_dec = Wire(UInt(34.W))
    val ltc_2_cnt_mod = Wire(UInt(34.W))
    val ltc_2_cnt_new = Wire(UInt(34.W))
    val ltc_2_cnt_nxt = Wire(UInt(34.W))

    ltc_2_cnt_ext := ltc_2_cnt_cur
    ltc_2_cnt_inc := ltc_2_cnt_cur +& 1.U
    ltc_2_cnt_dec := ltc_2_cnt_cur -& 1.U
    ltc_2_cnt_mod := Mux(ltc_2_inc && !ltc_2_dec, ltc_2_cnt_inc, 
                     Mux((!ltc_2_inc && ltc_2_dec), ltc_2_cnt_dec,
                     ltc_2_cnt_ext))
    ltc_2_cnt_new := Mux(ltc_2_adv, ltc_2_cnt_mod, ltc_2_cnt_ext)
    ltc_2_cnt_nxt := Mux(dc_rd_latency_clr, 0.U, ltc_2_cnt_new)

    // ltc_2 flops
    when(dc_rd_latency_cen){
        ltc_2_cnt_cur := ltc_2_cnt_nxt
    }

    // ltc_2 output logic
    io.dp2reg_rd_latency := ltc_2_cnt_cur

}


object NV_NVDLA_PERF_COUNTERDriver extends App {
  chisel3.Driver.execute(args, () => new PERF_COUNTER)
}