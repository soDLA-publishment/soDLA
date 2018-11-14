package nvdla

import chisel3._


class p_STRICTSYNC3DOTM_C_PPP extends Module {
  val io = IO(new Bundle {
    val SRC_D_NEXT = Input(Bool())
    val SRC_CLK = Input(Clock())
    val SRC_CLRN = Input(Bool())
    val DST_CLK = Input(Clock())
    val DST_CLRN = Input(Bool())

    val SRC_D = Output(Bool())
    val DST_Q = Output(Bool())

    val ATPG_CTL = Input(Bool())
    val TEST_MODE = Input(Bool())


  })

  val src_sel = Wire(Bool())
  val dst_sel = Wire(Bool())
  val src_d_f = Reg(Bool())

  //reg dst_d2,dst_d1,dst_d0;

  val dst_d2 = Reg(Bool())
  val dst_d1 = Reg(Bool())
  val dst_d0 = Reg(Bool())

  src_sel := io.SRC_D_NEXT

  withClockAndReset(io.SRC_CLK, !io.SRC_CLRN) {
    src_d_f:= src_sel
  }

  io.SRC_D := src_d_f

  io.dst_sel := src_d_f

  //always @(posedge DST_CLK or negedge DST_CLRN)
  //begin
  //    if(~DST_CLRN)
  //        {dst_d2,dst_d1,dst_d0} <= 3'd0;
  //    else
  //        {dst_d2,dst_d1,dst_d0} <= {dst_d1,dst_d0,dst_sel};
  //end
  //assign DST_Q = dst_d2;

  withClockAndReset(io.DST_CLK, !io.DST_CLRN) {
    dst_d0:= dst_sel
    dst_d1:= dst_d0    
    dst_d2:= dst_d1
  }

  io.DST_Q := dst_d2

  val sync3d = Module(new p_SSYNC3DO_C_PPP)

  io.DST_CLK := sync3d.io.clk
  io.dst_sel:=sync3d.io.d 
  io.DST_CLRN:=sync3d.io.clr_ 
  io.DST_Q:=sync3d.io.q 

}
