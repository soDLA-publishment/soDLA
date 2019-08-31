package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._


class read_ig_arb extends Module {
    val io = IO(new Bundle() {
        val clk = Input(Clock())
        val req = Input(Vec(10, Bool()))
        val wt = Input(Vec(10, UInt(8.W)))
        val gnt_busy = Input(Bool())
        val gnt = Output(UInt(10.W))
    })

    val req = VecInit((0 to 9)
            map { i => io.req(i) & io.wt(i).orR })

    val new_wt_left = VecInit((0 to 9)
            map { i => io.wt(i) - 1.U })

    withClock(io.clk) {
        val gnt = Reg(UInt(10.W))
        val gnt_pre = RegInit(UInt(10.W), 0.U)
        val wrr_gnt = RegInit(UInt(10.W), 0.U)
        val wt_left = RegInit(UInt(8.W), 0.U)
        val wt_left_nxt = Reg(UInt(8.W))

        gnt := Fill(10, !io.gnt_busy) & gnt_pre
        io.gnt := gnt

        wt_left_nxt := wt_left
        when(wt_left === 0.U  | !((req.asUInt() & wrr_gnt).orR)) {
            for(i<-0 until 10) {
                when(i.asUInt(10.W) === wrr_gnt){
                    for(j <-i until (10+i)) {
                        var x = (10+i)%10
                        when(req(x)) {
                            gnt_pre := (1<<x).asUInt(10.W)
                            wt_left_nxt := new_wt_left(x)
                        }
                    }
                }
            }
        } .otherwise {
            gnt_pre := wrr_gnt
            wt_left_nxt := wt_left - 1.U
        }

        when((!io.gnt_busy) & (req.asUInt() =/= 0.U(10.W))) {
            wrr_gnt := gnt
            wt_left := wt_left_nxt
        }
    }
}

class read_eg_arb extends Module {
    val io = IO(new Bundle() {
        val clk = Input(Clock())
        val req = Input(Vec(10, Bool()))
        val wt = Input(Vec(10, UInt(8.W)))
        val gnt = Output(UInt(10.W))
    })

    val req = VecInit((0 to 9)
      map { i =>  io.req(i) & io.wt(i).orR})

    val new_wt_left = VecInit((0 to 9)
      map { i => io.wt(i) - 1.U })

    withClock(io.clk) {
        val gnt = Reg(UInt(10.W))
        val gnt_pre = RegInit(UInt(10.W), 0.U)
        val wrr_gnt = RegInit(UInt(10.W), 0.U)
        val wt_left = RegInit(UInt(8.W), 0.U)
        val wt_left_nxt = Reg(UInt(8.W))

        gnt := gnt_pre
        io.gnt := gnt

        wt_left_nxt := wt_left
        when(wt_left === 0.U  | !((req.asUInt() & wrr_gnt).orR)) {
            for(i<-0 until 10) {
                when(i.asUInt(10.W) === wrr_gnt){
                    for(j <-i until (10+i)) {
                        var x = (10+i)%10
                        when(req(x)) {
                            gnt_pre := (1<<x).asUInt(10.W)
                            wt_left_nxt := new_wt_left(x)
                        }
                    }
                }
            }
        } .otherwise {
            gnt_pre := wrr_gnt
            wt_left_nxt := wt_left - 1.U
        }

        when((req.asUInt() =/= 0.U(10.W))) {
            wrr_gnt := gnt
            wt_left := wt_left_nxt
        }
    }
}

object read_ig_arb_Driver extends App {
    chisel3.Driver.execute(args, () => new read_ig_arb())
}


object read_eg_arb_Driver extends App {
    chisel3.Driver.execute(args, () => new read_eg_arb())
}