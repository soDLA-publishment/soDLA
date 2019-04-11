package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

//bubble collapse
class NV_NVDLA_BC_pipe(WIDTH:Int) extends Module {
    val io = IO(new Bundle {  
        val clk = Input(Clock()) 

        val di = Input(UInt(WIDTH.W)) 
        val vi = Input(Bool())
        val ri = Input(Bool())

        val dout = Output(UInt(WIDTH.W))
        val vo = Output(Bool()) 
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
        
    // SKID
    //
    //  pipe -bc                          
    //   ----------------------------- 
    //    di,vi,ro      
    //    v     ^        
    //    |     |       
    //   _|_    |        
    //  |>__|   |      
    //    | - >(*)        
    //    |     |      
    //    |     |       
    //    do,vo,ri     
 
 withClock(io.clk){    
//## pipe skid buffer 
    val skid_valid = RegInit(false.B)
    val skid_ready_flop = RegInit(true.B)
    val skid_data = Reg(UInt(WIDTH.W))
    val ro_out = RegInit(true.B)
    val skid_pipe_ready = Wire(Bool())
    val skid_catch = Wire(Bool())
    val skid_ready = Wire(Bool())
    val skid_pipe_valid = Wire(Bool())
    val skid_pipe_data = Wire(UInt(WIDTH.W))

    skid_catch := io.vo && skid_ready_flop && !skid_pipe_ready
    skid_ready := Mux(skid_valid, skid_pipe_ready, !skid_catch)
    skid_valid := Mux(skid_valid, !skid_pipe_ready, skid_catch)

    skid_ready_flop := skid_ready
    ro_out := skid_ready
    skid_data := Mux(skid_catch, io.di, skid_data)

    skid_pipe_valid := Mux(skid_ready_flop, io.vo, skid_valid)
    skid_pipe_data := Mux(skid_ready_flop, io.di, skid_data)

//## pipe valid-ready-bubble-collapse
    val pipe_ready_bc = Wire(Bool())
    val pipe_valid = RegInit(false.B)
    val pipe_data = Reg(UInt(WIDTH.W))
    val pipe_ready = Wire(Bool())

    pipe_ready_bc := pipe_ready || !pipe_valid
    pipe_valid := Mux(pipe_ready_bc, skid_pipe_valid, true.B)
    pipe_data := Mux(pipe_ready_bc&&skid_pipe_valid, skid_pipe_data, pipe_data)
    skid_pipe_ready := pipe_ready_bc

//## pipe (2) output
    io.dout := pipe_data
    pipe_ready := io.ri
    io.vo := pipe_valid
    io.ro := ro_out
          
}}


//bubble collapse for vector
class NV_NVDLA_BC_VEC_pipe(DIM:Int, WIDTH:Int) extends Module {
    val io = IO(new Bundle {  
        val clk = Input(Clock()) 

        val di = Input(Vec(DIM, UInt(WIDTH.W)))
        val vi = Input(Bool())
        val ri = Input(Bool())

        val dout = Output(Vec(DIM, UInt(WIDTH.W)))
        val vo = Output(Bool()) 
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
        
    // SKID
    //
    //  pipe -bc                          
    //   ----------------------------- 
    //    di,vi,ro      
    //    v     ^        
    //    |     |       
    //   _|_    |        
    //  |>__|   |      
    //    | - >(*)        
    //    |     |      
    //    |     |       
    //    do,vo,ri     
 
 withClock(io.clk){    
//## pipe skid buffer 
    val skid_valid = RegInit(false.B)
    val skid_ready_flop = RegInit(true.B)
    val skid_data = Reg(Vec(DIM, UInt(WIDTH.W)))
    val ro_out = RegInit(true.B)
    val skid_pipe_ready = Wire(Bool())
    val skid_catch = Wire(Bool())
    val skid_ready = Wire(Bool())
    val skid_pipe_valid = Wire(Bool())
    val skid_pipe_data = Wire(Vec(DIM, UInt(WIDTH.W)))

    skid_catch := io.vo && skid_ready_flop && !skid_pipe_ready
    skid_ready := Mux(skid_valid, skid_pipe_ready, !skid_catch)
    skid_valid := Mux(skid_valid, !skid_pipe_ready, skid_catch)

    skid_ready_flop := skid_ready
    ro_out := skid_ready
    skid_data := Mux(skid_catch, io.di, skid_data)

    skid_pipe_valid := Mux(skid_ready_flop, io.vo, skid_valid)
    skid_pipe_data := Mux(skid_ready_flop, io.di, skid_data)

//## pipe valid-ready-bubble-collapse
    val pipe_ready_bc = Wire(Bool())
    val pipe_valid = RegInit(false.B)
    val pipe_data = Reg(Vec(DIM, UInt(WIDTH.W)))
    val pipe_ready = Wire(Bool())

    pipe_ready_bc := pipe_ready || !pipe_valid
    pipe_valid := Mux(pipe_ready_bc, skid_pipe_valid, true.B)
    pipe_data := Mux(pipe_ready_bc&&skid_pipe_valid, skid_pipe_data, pipe_data)
    skid_pipe_ready := pipe_ready_bc

//## pipe (2) output
    io.dout := pipe_data
    pipe_ready := io.ri
    io.vo := pipe_valid
    io.ro := ro_out
          
}}
