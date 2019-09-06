package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver

//this module is to process dat

class NV_NVDLA_CACC_CALC_int8 extends Module {

    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())

        //input
        val cfg_truncate = Input(UInt(5.W))
        val in_data = Input(UInt(22.W))
        val in_op = Input(UInt(34.W))
        val in_op_valid = Input(Bool())   
        val in_sel = Input(Bool())
        val in_valid = Input(Bool())    

        //output
        val out_final_data = Output(UInt(32.W))
        val out_final_sat = Output(Bool())
        val out_final_valid = Output(Bool())
        val out_partial_data = Output(UInt(34.W))
        val out_partial_valid = Output(Bool())
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
    //====================
    // Addition
    //====================
    val i_sat_vld = RegInit(false.B)
    val i_sat_sel  = RegInit(false.B)
    val i_sum_pd = Reg(UInt(35.W))

    i_sat_vld := io.in_valid
    when(io.in_valid){
        i_sat_sel := io.in_sel
        i_sum_pd := (io.in_data.asSInt +& Mux(io.in_op_valid, io.in_op, 0.asUInt(34.W)).asSInt).asUInt
    } 

    //====================
    // narrow down to 34bit, and need satuation only
    //====================
    val i_sum_sign = i_sum_pd(34)
    val i_sum_msb = i_sum_pd(33)
    val i_sat_sign = i_sum_sign
    val i_sat_bits = WireInit("b0".asUInt(33.W))

    when(i_sum_sign^i_sum_msb){//saturation condition, sign and msb
        i_sat_bits := Fill(33, ~i_sum_sign) // overflow, need satuation
    }
    .otherwise{
        i_sat_bits := i_sum_pd(32, 0)
    }

    val i_sat_pd = Cat(i_sat_sign, i_sat_bits)
    val i_partial_result = i_sat_pd

    //====================
    // narrow down to 32bit, and need rounding and saturation 
    //====================  
    val i_point5 = Wire(Bool())
    val i_pre_sft_pd = Mux(i_sat_sel, i_sat_pd(33, 0), Fill(34, false.B))
    val i_pre_sft_pd_pack = Cat(i_pre_sft_pd, "b0".asUInt(16.W)).asSInt >> io.cfg_truncate
    val i_sft_pd = Cat(i_pre_sft_pd_pack)(49, 16)
    val i_guide = Cat(i_pre_sft_pd_pack)(15)
    val i_stick = Cat(i_pre_sft_pd_pack)(14, 0)

    val i_sft_need_sat = (i_sat_sign & (~(i_sft_pd(32, 31).andR)))|
                         (~i_sat_sign & (i_sft_pd(32, 31).orR)) |
                         (~i_sat_sign & Cat(i_sft_pd(30,0), i_point5).andR) 
    val i_sft_max = Mux(i_sat_sign, Cat(true.B, "b0".asUInt(31.W)), ~Cat(true.B, "b0".asUInt(31.W)))

    i_point5 := i_sat_sel & i_guide & (~i_sat_sign|(i_stick.orR))
    val i_pos_pd = i_sft_pd(31, 0) + i_point5
    val i_tru_pd = i_pos_pd

    val i_final_result = Mux(i_sft_need_sat, i_sft_max, i_tru_pd)

    val i_partial_vld = i_sat_vld & ~i_sat_sel
    val i_final_vld = i_sat_vld&i_sat_sel

    io.out_partial_valid := RegNext(i_partial_vld, false.B)
    io.out_partial_data := RegEnable(i_partial_result, i_partial_vld)
    io.out_final_valid := RegNext(i_final_vld, false.B)
    io.out_final_sat := RegNext(i_final_vld & i_sft_need_sat, false.B)
    io.out_final_data := RegEnable(i_final_result, i_final_vld)

}}

object NV_NVDLA_CACC_CALC_int8Driver extends App {
  chisel3.Driver.execute(args, () => new NV_NVDLA_CACC_CALC_int8())
}


