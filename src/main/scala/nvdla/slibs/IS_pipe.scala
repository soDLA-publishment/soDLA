package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

//input skid
@chiselName
class NV_NVDLA_IS_pipe(WIDTH:Int) extends Module {
    val io = IO(new Bundle {  
        val clk = Input(Clock())  

        val dout = Output(UInt(WIDTH.W))
        val vo = Output(Bool())
        val ri = Input(Bool())
        val di = Input(UInt(WIDTH.W))
        val vi = Input(Bool())
        val ro = Output(Bool())
    })
    // -vi  : input valid signal name
    // -di  : input data signal name
    // -ro  : output ready signal name
    // -vo  : output valid signal name
    // -do  : output data signal name
    // -ri  : input ready signal name

    // ----------
    // Basic Pipe
    // ----------
    // 
    // Input skid(is) adds comb logic on vi side, while make ro timing clean
        
    // SKID
    //
    //   -is                           
    //   ----------------------------- 
    //    di,vi              ro       
    //      v                ^        
    //    __|  __            |        
    //   | _|_|_ |           |        
    //   | \___/-|------     |        
    //   |  _|_  | |  _|_   _|_       
    //   | |>__| | | |>__| |>__|      
    //   |__ |___| |   |     |        
    //     _|_|_   |   ------|        
    //     \___/---          |           
    //       |               |        
    //       v               ^        
    //    do,vo               ri   
  
 withClock(io.clk){       
    //pipe skid buffer
    //reg
    val ro_out = RegInit(true.B)
    val skid_flop_ro = RegInit(true.B)
    val skid_flop_vi = RegInit(false.B)
    val skid_flop_di = Reg(UInt(WIDTH.W))
    val pipe_skid_vi = RegInit(false.B)
    val pipe_skid_di = Reg(UInt(WIDTH.W))
    //Wire
    val skid_vi = Wire(Bool())
    val skid_di = Wire(UInt(WIDTH.W))
    val skid_ro = Wire(Bool())
    val pipe_skid_ro = Wire(Bool())
    val vo_out = Wire(Bool())
    val do_out = Wire(UInt(WIDTH.W))
    //skid ready
    ro_out := skid_ro
    skid_flop_ro := skid_ro
    //skid valid
    when(skid_flop_ro){
        skid_flop_vi := io.vi
    }
    skid_vi := Mux(skid_flop_ro, io.vi, skid_flop_vi)
    //skid data
    when(skid_flop_ro&io.vi){
        skid_flop_di:= io.di
    }
    skid_di := Mux(skid_flop_ro, io.di, skid_flop_di)
    //pipe ready
    skid_ro := pipe_skid_ro || ~pipe_skid_vi
    //pipe valid
    when(skid_ro){
        pipe_skid_vi := skid_vi
    }
    //pipe data
    when(skid_ro && skid_vi){
        pipe_skid_di := skid_di
    }
    //pipe output
    pipe_skid_ro := io.ri
    vo_out := pipe_skid_vi
    do_out := pipe_skid_di

    //deliver output
    io.ro := ro_out
    io.vo := vo_out
    io.dout := do_out
          
}}
