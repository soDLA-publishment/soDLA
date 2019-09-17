package nvdla

import chisel3._
import chisel3.util._
import chisel3.experimental._

class int_sum_block_tp1 extends Module {
    val pINT8_BW = 9
    val io = IO(new Bundle {
        //nvdla core clock
        val nvdla_core_clk = Input(Clock())

        //control signal
        val int8_en = Input(Bool())
        val len5 = Input(Bool())
        val len7 = Input(Bool())
        val len9 = Input(Bool())
        val load_din_d = Input(Bool())
        val load_din_2d = Input(Bool())
        val reg2dp_normalz_len = Input(UInt(2.W))

        //sq_pd as a input
        val sq_pd_int8 = Input(Vec(9, UInt((2*pINT8_BW-1).W)))

        //output signal
        val int8_sum = Output(UInt((2*pINT8_BW+3).W))
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
    //load_din_d 1st cycle
    //add from double sides
    val int8_sum_0_8 = RegInit(0.U((pINT8_BW*2).W))
    val int8_sum_1_7 = RegInit(0.U((pINT8_BW*2).W))
    val int8_sum_2_6 = RegInit(0.U((pINT8_BW*2).W))
    val int8_sum_3_5 = RegInit(0.U((pINT8_BW*2).W))
    val sq_pd_int8_4_d = RegInit(0.U((pINT8_BW*2-1).W))


    when(io.load_din_d) {
        int8_sum_3_5 := io.sq_pd_int8(3) +& io.sq_pd_int8(5)
        sq_pd_int8_4_d := io.sq_pd_int8(4)
        when(io.len9){
            int8_sum_0_8 := io.sq_pd_int8(0) +& io.sq_pd_int8(8)
        }
        when(io.len7|io.len9){
            int8_sum_1_7 := io.sq_pd_int8(1) +& io.sq_pd_int8(7)
        }
        when(io.len5|io.len7|io.len9){
            int8_sum_2_6 := io.sq_pd_int8(2) +& io.sq_pd_int8(6)
        }
    }

    //2nd cycle
    val int8_sum3 = RegInit(0.U((pINT8_BW*2+1).W))
    val int8_sum5 = RegInit(0.U((pINT8_BW*2+2).W))
    val int8_sum7 = RegInit(0.U((pINT8_BW*2+2).W))
    val int8_sum9 = RegInit(0.U((pINT8_BW*2+3).W))

    //load_din_2d
    when(io.load_din_2d){
        int8_sum3 := int8_sum_3_5  +& Cat("b0".U, sq_pd_int8_4_d)
        when(io.len5|io.len7|io.len9){
            int8_sum5 := (int8_sum_3_5  +& Cat("b0".U, sq_pd_int8_4_d)) +& Cat("b0".U, int8_sum_2_6)
        }
        when(io.len7|io.len9){
            int8_sum7 := (int8_sum_3_5  +& Cat("b0".U, sq_pd_int8_4_d)) +& (int8_sum_2_6 +& int8_sum_1_7)
        }
        when(io.len9){
            int8_sum9 := (int8_sum_3_5  +& Cat("b0".U, sq_pd_int8_4_d)) +& ((int8_sum_2_6 +& int8_sum_1_7) +& Cat("b0".U, int8_sum_0_8))
        }
    }
    //config
    io.int8_sum := MuxLookup(io.reg2dp_normalz_len, int8_sum9,
        Array(
            "h0".asUInt(2.W) -> Cat("d0".asUInt(2.W), int8_sum3),
            "h1".asUInt(2.W) -> Cat("d1".asUInt(2.W), int8_sum5),
            "h2".asUInt(2.W) -> Cat("d2".asUInt(2.W), int8_sum7)
        )
    )
}}


object int_sum_block_tp1Driver extends App {
    chisel3.Driver.execute(args, () => new int_sum_block_tp1())
}
