package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

//bubble collapse
@chiselName
class NV_NVDLA_BC_pipe(WIDTH:Int) extends Module {
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
     
    //## pipe valid-ready-bubble-collapse
    val pipe_valid = RegInit(false.B)
    val pipe_data = Reg(UInt(WIDTH.W))
    val pipe_ready = Wire(Bool())

    pipe_valid := Mux(io.ro, io.vi, true.B)
    io.ro := pipe_ready || ~pipe_valid;
    pipe_data := Mux(io.ro && io.vi, io.di, pipe_data)

    //## pipe (2) output
    pipe_ready := io.ri
    io.vo := pipe_valid
    io.dout := pipe_data
          
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
     
    //## pipe valid-ready-bubble-collapse
    val pipe_valid = RegInit(false.B)
    val pipe_data = Reg(UInt(WIDTH.W))
    val pipe_ready = Wire(Bool())

    pipe_valid := Mux(io.ro, io.vi, true.B)
    io.ro := pipe_ready || ~pipe_valid;
    pipe_data := Mux(io.ro && io.vi, io.di, pipe_data)

    //## pipe (2) output
    pipe_ready := io.ri
    io.vo := pipe_valid
    io.dout := pipe_data
          
}}
