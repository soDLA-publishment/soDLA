package nvdla



import chisel3._


import chisel3.experimental._

//https://github.com/freechipsproject/chisel3/wiki/Multiple-Clock-Domains



class int_sum_block_tp1(implicit val conf: cdpConfiguration) extends Module {
    val io = IO(new Bundle {
        //nvdla core clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

        //control signal
        val int8_en = Input(Bool())
        val len5 = Input(Bool())
        val len7 = Input(Bool())
        val len9 = Input(Bool())
        val load_din_d = Input(Bool())
        val load_din_2d = Input(Bool())
        val reg2dp_normalz_len = Input(UInt(2.W))

        //sq_pd as a input
        val sq_pd_int8 = Input(Vec(9, UInt((2*conf.pINT8_BW-1).W)))

        //output signal
        val int8_sum = Output(UInt((2*conf.pINT8_BW+3).W))
    })

    //Reg
    io.int8_sum := Reg(UInt(conf.pINT8_BW*2+3).W)
    val int8_sum3 = Reg(UInt(conf.pINT8_BW*2+1).W)
    val int8_sum5 = Reg(UInt(conf.pINT8_BW*2+2).W)
    val int8_sum7 = Reg(UInt(conf.pINT8_BW*2+2).W)
    val int8_sum9 = Reg(UInt(conf.pINT8_BW*2+2).W)

        //add from double sides
    val int8_sum_0_8 = Reg(UInt(conf.pINT8_BW*2).W)
    val int8_sum_1_7 = Reg(UInt(conf.pINT8_BW*2).W)
    val int8_sum_2_6 = Reg(UInt(conf.pINT8_BW*2).W)
    val int8_sum_3_5 = Reg(UInt(conf.pINT8_BW*2).W)
    val sq_pd_int8_4_d = Reg(UInt(conf.pINT8_BW*2-1).W)
    
    //wire
    val sq0 = io.sq_pd_int8(0)
    val sq1 = io.sq_pd_int8(1)
    val sq2 = io.sq_pd_int8(2)
    val sq3 = io.sq_pd_int8(3)
    val sq5 = io.sq_pd_int8(5)
    val sq6 = io.sq_pd_int8(6)
    val sq7 = io.sq_pd_int8(7)
    val sq8 = io.sq_pd_int8(8)
    
    
    //compute
    //为梦想灼伤了自己，也不要平庸的喘息

    //load_din_d
    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn) {
        when (load_din_d) {
            int8_sum_3_5 := sq3 + sq5
            sq_pd_int8_4_d := sq_pd_int8(4)
        }
    }
    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn) {
        when (load_din_d & (len5|len7|len9)) {
            int8_sum_2_6 := sq2 + sq6
        }
    }
    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn) {
        when (load_din_d & (len7|len9)) {
            int8_sum_1_7 := sq1 + sq7
        }
    }
    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn) {
        when (load_din_d & (len9)) {
            int8_sum_0_8 := sq0 + sq8
        }
    }

    //load_din_2d
    //此处感觉可优化
    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn) {
        when (load_din_2d) {
            int8_sum3 := (int8_sum_3_5  + Cat("b0".U, sq_pd_int8_4_d))
        }
    }
    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn) {
        when (load_din_2d & (len5|len7|len9)) {
            int8_sum5 := (int8_sum_3_5  + Cat("b0".U, sq_pd_int8_4_d)) + Cat("b0".U, sq_pd_int8_2_6)
        }
    }
    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn) {
        when (load_din_2d & (len7|len9)) {
            int8_sum7 := (int8_sum_3_5  + Cat("b0".U, sq_pd_int8_4_d)) + (int8_sum_2_6 + int8_sum_1_7)
        }
    }
    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn) {
        when (load_din_2d & (len9)) {
            int8_sum9 := (int8_sum_3_5  + Cat("b0".U, sq_pd_int8_4_d)) + ((int8_sum_2_6 + int8_sum_1_7) + Cat("b0".U, int8_sum_0_8))
        }
    }

    //direction
    when(reg2dp_normalz_len === "b00".U){
        int8_sum := Cat("b00".U, int8_sum3)
    }
    .elsewhen(reg2dp_normalz_len === "b01".U){
        int8_sum := Cat("b0".U, int8_sum5)   
    }
    .elsewhen(reg2dp_normalz_len === "b10".U){
        int8_sum := Cat("b0".U, int8_sum7)   
    }
    .otherwise{
        int8_sum := int8_sum9
    }
}