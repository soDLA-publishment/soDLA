// package nvdla

// import chisel3._


// class p_STRICTSYNC3DOTM_C_PPP extends Module {
//   val io = IO(new Bundle {
//     val SRC_D_NEXT = Input(Bool())
//     val SRC_CLK = Input(Clock())
//     val SRC_CLRN = Input(Bool())
//     val DST_CLK = Input(Clock())
//     val DST_CLRN = Input(Bool())

//     val SRC_D = Output(Bool())
//     val DST_Q = Output(Bool())

//     val ATPG_CTL = Input(Bool())
//     val TEST_MODE = Input(Bool())


//   })

//   withClockAndReset(io.SRC_CLK, !io.SRC_CLRN) {

//   io.SRC_D = RegInit(false.B)
//   io.SRC_D := ShiftRegister(io.SRC_D_NEXT, 1)

//   }

//   withClockAndReset(io.DST_CLK, !io.DST_CLRN) {

//   io.DST_Q = RegInit(false.B)
//   io.DST_Q := ShiftRegister(io.SRC_D_NEXT, 3)

//   }


//   val sync3d = Module(new p_SSYNC3DO_C_PPP)

//   io.DST_CLK := sync3d.io.clk
//   io.dst_sel:=sync3d.io.d 
//   io.DST_CLRN:=sync3d.io.clr_ 
//   io.DST_Q:=sync3d.io.q 

// }}
