package nvdla

import chisel3._

class NV_NVDLA_sdp(addressWidth: Int) extends Module {

  //nvdla_core interface  
  val nvdla_core_clk = IO(new Bundle {
    val clk  = Input(Clock())
    val rstn = Input(Bool())
  })

  //lut2inp interface  
  val lut2inp = IO(new Bundle {
    val pvld  = Output(Bool()) /* data valid */
    val prdy = Input(Bool()) /* data return handshake */
    val pd = Input(UInt(740.W))
  })

  //idx2lut interface  
  val idx2lut = IO(new Bundle {
    val pvld  = Input(Bool()) /* data valid */
    val prdy = Output(Bool()) /* data return handshake */
    val pd = Input(UInt(324.W))
  })

  //reg2dp interface

  val reg2dp = IO(new Bundle {
    val lut_int_access_type = Input(Bool())
    val lut_int_addr = Input(UInt(10.W)) 
    val lut_int_data = Input(UInt(16.W))
    val lut_int_data_wr = Input(Bool())
    val lut_int_table_id = Input(Bool())
    val lut_le_end = Input(UInt(32.W))
    val lut_le_function = Input(Bool())
    val lut_le_index_offset = Input(UInt(8.W))
    val lut_le_slope_oflow_scale = Input(UInt(16.W))
    val lut_le_slope_oflow_shift = Input(UInt(5.W))
    val lut_le_slope_uflow_scale = Input(UInt(16.W))
    val lut_le_slope_uflow_shift = Input(UInt(5.W))
    val lut_le_start = Input(UInt(32.W))
    val lut_lo_end = Input(UInt(32.W))
    val lut_lo_slope_oflow_scale = Input(UInt(16.W))
    val lut_lo_slope_oflow_shift = Input(UInt(5.W))
    val lut_lo_slope_uflow_scale = Input(UInt(16.W))
    val lut_lo_slope_uflow_shift = Input(UInt(5.W))
    val lut_lo_start = Input(UInt(32.W))
    val perf_lut_en = Input(Bool())
    val proc_precision = Input(UInt(2.W))
  })

  //dp2reg interface

  val dp2reg = IO(new Bundle {
    val lut_hybrid = Output(UInt(32.W))
    val lut_int_data = Output(UInt(16.W))
    val lut_le_hit = Output(UInt(32.W))
    val lut_lo_hit = Output(UInt(32.W))
    val lut_oflow = Output(UInt(32.W))
    val lut_uflow = Output(UInt(32.W))
  })

  //pwrbus interface
  val pwrbus = IO(new Bundle {
    val ram_pd = Input(UInt(32.W))
  })

  //op interface
  val op = IO(new Bundle {
    val en_load = Input(Bool())
  })

//declaring registers

  
  // mixed vec Reg_le of Vec of 32-bit UInts without initialization
  class REG_le extends Bundle {
      val reg = Reg(Vec(65, UInt(16.W)))
      val wire = Wire(Vec(257, UInt(16.W)))
  }
 

  // Reg_lo of Vec of 32-bit UInts without initialization
  val REG_lo = Reg(Vec(257, UInt(16.W)))

  // idx2lut_prdy of Bool
  val idx2lut.prdy = Reg(Bool())

  // le_data0 of Vec of 32-bit UInts without initialization

  val le_data0 = Reg(Vec(4, UInt(16.W)))

  // le_data1 of Vec of 32-bit UInts without initialization

  val le_data1 = Reg(Vec(4, UInt(16.W)))

  // le_lut_data of 16-bit UInts

  val le_lut_data = Reg(UInt(16.W))

  // lut2inp_pd and pvld as Reg

  val lut2inp.pd = Reg(UInt(740.W))

  val lut2inp.pvld = Reg(Bool())

  val lut_hybrid.


  // out_offset

  val out_offset = Reg(Vec(4, UInt(32.W)))
  val out_scale  = Reg(Vec(4, UInt(16.W)))
  val out_shift  = Reg(Vec(4, UInt(5.W)))


 // Reg_le of Vec of 32-bit UInts without initialization
  val Reg_le = Wire(Vec(257, UInt(16.W)))


// le_wr_en of Vec without initialization

  val le_wr_en = Wire(Vec(65, Bool())

  
// lo_wr_en of Vec without initialization

  val lo_wr_en = Wire(Vec(257, Bool())
  

  val lut_access_type = Bool()




  //LUT programming

  val lut_addr = 



  














  

