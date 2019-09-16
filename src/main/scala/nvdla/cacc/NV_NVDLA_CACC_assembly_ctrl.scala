package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_CACC_assembly_ctrl(implicit conf: nvdlaConfig) extends Module {

    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())

        //abuf
        val abuf_rd_addr = ValidIO(UInt(conf.CACC_ABUF_AWIDTH.W))

        //mac2accu
        val mac_a2accu_pd = Flipped(ValidIO(UInt(9.W)))
        
        //accu_ctrl
        val accu_ctrl_pd = ValidIO(UInt(13.W))
        val accu_ctrl_ram_valid = Output(Bool())

        //cfg
        val cfg_in_en_mask = Output(Bool())
        val cfg_truncate = Output(UInt(5.W))

        //reg2dp
        val reg2dp_op_en = Input(Bool()) 
        val reg2dp_conv_mode = Input(Bool())
        val reg2dp_proc_precision = Input(UInt(2.W))
        val reg2dp_clip_truncate = Input(UInt(5.W))
        val dp2reg_done = Input(Bool())
        
        //slcg
        val slcg_cell_en = Output(Bool())

        //wait for op
        val wait_for_op_en = Output(Bool())
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

val accu_valid = RegNext(io.mac_a2accu_pd.valid, false.B)
val accu_pd = RegEnable(io.mac_a2accu_pd.bits, "b0".asUInt(9.W), io.mac_a2accu_pd.valid)

//////////////////////////////////////////////////////////////
///// generator input status signal                      /////
//////////////////////////////////////////////////////////////
val accu_stripe_st = accu_pd(5)
val accu_stripe_end = accu_pd(6)
val accu_channel_end = accu_pd(7)
val accu_layer_end = accu_pd(8)

val is_int8 = (io.reg2dp_proc_precision === conf.NVDLA_CACC_D_MISC_CFG_0_PROC_PRECISION_INT8.U)
val is_winograd = false.B

// SLCG
io.slcg_cell_en := ShiftRegister(io.reg2dp_op_en, 3, false.B, true.B)

// get layer operation begin
val wait_for_op_en_out = RegInit(true.B)
val wait_for_op_en_w = RegNext(Mux(io.dp2reg_done, true.B, Mux(io.reg2dp_op_en, false.B, wait_for_op_en_out)))
wait_for_op_en_out := wait_for_op_en_w 
io.wait_for_op_en := wait_for_op_en_out


// get address and other contrl
val cfg_winograd = RegInit(false.B)
val accu_cnt = RegInit("b0".asUInt(conf.CACC_ABUF_AWIDTH.W))
val accu_ram_valid = RegInit(false.B)
val accu_channel_st = RegInit(true.B)

val layer_st = io.wait_for_op_en & io.reg2dp_op_en
val accu_cnt_inc = accu_cnt + 1.U
val accu_cnt_w = Mux(layer_st | accu_stripe_end, "b0".asUInt(conf.CACC_ABUF_AWIDTH.W), accu_cnt_inc)
val accu_addr = accu_cnt
val accu_channel_st_w  = Mux(layer_st, true.B, Mux(accu_valid & accu_stripe_end, accu_channel_end, accu_channel_st))
val accu_rd_en = accu_valid & (~accu_channel_st)
val cfg_in_en_mask_w = true.B

accu_ram_valid := accu_rd_en
when(layer_st | accu_valid){
    accu_cnt := accu_cnt_w
    accu_channel_st := accu_channel_st_w
}
when(layer_st){
    cfg_winograd := is_winograd
}
io.cfg_truncate := RegEnable(io.reg2dp_clip_truncate, false.B, layer_st)
io.cfg_in_en_mask := RegEnable(cfg_in_en_mask_w, false.B, layer_st)

io.abuf_rd_addr.valid := accu_rd_en
io.abuf_rd_addr.bits := accu_addr

// regout

io.accu_ctrl_pd.valid := RegNext(accu_valid, false.B)
io.accu_ctrl_ram_valid := RegNext(accu_ram_valid, false.B)
val accu_ctrl_addr = RegInit("b0".asUInt(6.W));
when(accu_valid){
    accu_ctrl_addr := accu_addr
}
val accu_ctrl_stripe_end = RegEnable(accu_stripe_end, false.B, accu_valid)
val accu_ctrl_channel_end = RegEnable(accu_channel_end, false.B, accu_valid)
val accu_ctrl_layer_end = RegEnable(accu_layer_end, false.B, accu_valid)
val accu_ctrl_dlv_elem_mask = RegEnable(accu_layer_end, false.B, accu_valid)

io.accu_ctrl_pd.bits := Cat(accu_ctrl_dlv_elem_mask, accu_ctrl_layer_end, accu_ctrl_channel_end, 
                      accu_ctrl_stripe_end, "b1".asUInt(3.W), accu_ctrl_addr) //(8,6) digit is for reserve.


}}

