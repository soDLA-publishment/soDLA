package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

//bubble collapse
@chiselName
class NV_NVDLA_BC_OS_pipe(WIDTH:Int) extends Module {
    val io = IO(new Bundle {  
        val clk = Input(Clock()) 

        val vi = Input(Bool())
        val ro = Output(Bool())
        val di = Input(UInt(WIDTH.W)) 
            
        val vo = Output(Bool()) 
        val ri = Input(Bool())
        val dout = Output(UInt(WIDTH.W))
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
    // SKID
    //
    //   -is                           
    //   ----------------------------- 
    //    di,vi              ro       
    //      v                ^     
    //      _|_______________|_       
    //     |      bc pipe      |      
    //     |___________________|        
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
    //## pipe valid-ready-bubble-collapse
    val pipe_ready_bc = Wire(Bool())
    val pipe_valid = RegInit(false.B)
    val pipe_data = Reg(UInt(WIDTH.W))
    val pipe_ready = RegInit(true.B)

    pipe_ready_bc := pipe_ready || ~pipe_valid
    pipe_valid := Mux(pipe_ready_bc, io.vi, true.B)
    pipe_data := Mux(pipe_ready_bc&&io.vi, io.di, pipe_data)
    io.ro := pipe_ready_bc

    //## pipe skid buffer 
    val skid_valid = RegInit(false.B)
    val skid_ready_flop = RegInit(true.B)
    val skid_data = Reg(UInt(WIDTH.W))
    val skid_catch = Wire(Bool())
    val skid_ready = Wire(Bool())
    val pipe_skid_valid = Wire(Bool())
    val pipe_skid_ready = Wire(Bool())
    val pipe_skid_data = Wire(UInt(WIDTH.W))
    dontTouch(pipe_skid_valid)
    dontTouch(pipe_skid_ready)
    dontTouch(pipe_skid_data)

    skid_catch := pipe_valid && skid_ready_flop && ~pipe_skid_ready
    skid_ready := Mux(skid_valid, pipe_skid_ready, ~skid_catch)
    skid_valid := Mux(skid_valid, ~pipe_skid_ready, skid_catch)

    skid_ready_flop := skid_ready
    pipe_ready := skid_ready
    skid_data := Mux(skid_catch, pipe_data, skid_data)

    pipe_skid_valid := Mux(skid_ready_flop, pipe_valid, skid_valid)
    pipe_skid_data := Mux(skid_ready_flop, pipe_data, skid_data)

    //assign to deq side
    io.vo := pipe_skid_valid
    pipe_skid_ready := io.ri
    io.dout := pipe_skid_data
          
}}



