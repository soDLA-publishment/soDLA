package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class cdp_dp_intp_lut2intp_in_if(implicit val conf: nvdlaConfig) extends Bundle{
    val x_data_0 = Output(UInt(32.W))
    val x_data_0_17b = Output(UInt(17.W))
    val x_data_1 = Output(UInt(32.W))
    val x_info = Output(UInt(20.W))
    val x_sel = Output(Bool())
    val y_sel = Output(Bool())
}

class cdp_dp_intp_reg2dp_lut_if extends Bundle{
    val le_end_high = Output(UInt(6.W))
    val le_end_low = Output(UInt(32.W))
    val le_function = Output(Bool())
    val le_index_offset = Output(UInt(8.W))
    val le_slope_oflow_scale = Output(UInt(16.W))
    val le_slope_oflow_shift = Output(UInt(5.W))
    val le_slope_uflow_scale = Output(UInt(16.W))
    val le_slope_uflow_shift = Output(UInt(5.W))
    val le_start_high = Output(UInt(6.W))
    val le_start_low = Output(UInt(32.W))
    val lo_end_high = Output(UInt(6.W))
    val lo_end_low = Output(UInt(32.W))
    val lo_slope_oflow_scale = Output(UInt(16.W))
    val lo_slope_oflow_shift = Output(UInt(5.W))
    val lo_slope_uflow_scale = Output(UInt(16.W))
    val lo_slope_uflow_shift = Output(UInt(5.W))
    val lo_start_high = Output(UInt(6.W))
    val lo_start_low = Output(UInt(32.W))
}


class cdp_dp_intp_dp2reg_perf_lut_if extends Bundle{
    val d0_perf_lut_hybrid = Output(UInt(32.W))
    val d0_perf_lut_le_hit = Output(UInt(32.W))
    val d0_perf_lut_lo_hit = Output(UInt(32.W))
    val d0_perf_lut_oflow = Output(UInt(32.W))
    val d0_perf_lut_uflow = Output(UInt(32.W))
    val d1_perf_lut_hybrid = Output(UInt(32.W))
    val d1_perf_lut_le_hit = Output(UInt(32.W))
    val d1_perf_lut_lo_hit = Output(UInt(32.W))
    val d1_perf_lut_oflow = Output(UInt(32.W))
    val d1_perf_lut_uflow = Output(UInt(32.W))
}


class NV_NVDLA_CDP_DP_intp(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())
        val pwrbus_ram_pd = Input(UInt(32.W))

        val intp2mul_pd = DecoupledIO(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(17.W)))
        val lut2intp = Flipped(DecoupledIO(Vec(conf.NVDLA_CDP_THROUGHPUT, new cdp_dp_intp_lut2intp_in_if)))
        val sync2itp_pd = Flipped(DecoupledIO(UInt((conf.NVDLA_CDP_THROUGHPUT*(conf.NVDLA_CDP_ICVTO_BWPE*2+3)).W)))
        
        val reg2dp_lut = Flipped(new cdp_dp_intp_reg2dp_lut_if)
        val reg2dp_sqsum_bypass = Input(Bool())

        val dp2reg_perf = new cdp_dp_intp_dp2reg_perf_lut_if
        val dp2reg_done = Input(Bool())
    })

withClock(io.nvdla_core_clk){
    ///////////////////////////////////////////
    val x_exp = RegInit(false.B)
    val sqsum_bypass_enable = RegInit(false.B)
    val reg2dp_lut_le_slope_uflow_shift_sync = RegInit(0.U(5.W))
    val reg2dp_lut_le_slope_oflow_shift_sync = RegInit(0.U(5.W))
    val reg2dp_lut_lo_slope_uflow_shift_sync = RegInit(0.U(5.W))
    val reg2dp_lut_lo_slope_oflow_shift_sync = RegInit(0.U(5.W))
    val reg2dp_lut_le_slope_uflow_scale_sync = RegInit(0.U(16.W))
    val reg2dp_lut_le_slope_oflow_scale_sync = RegInit(0.U(16.W))
    val reg2dp_lut_lo_slope_uflow_scale_sync = RegInit(0.U(16.W))
    val reg2dp_lut_lo_slope_oflow_scale_sync = RegInit(0.U(16.W))

    x_exp := (io.reg2dp_lut.le_function === 0.U)
    sqsum_bypass_enable := (io.reg2dp_sqsum_bypass === 1.U)
    reg2dp_lut_le_slope_uflow_shift_sync := io.reg2dp_lut.le_slope_uflow_shift
    reg2dp_lut_le_slope_oflow_shift_sync := io.reg2dp_lut.le_slope_oflow_shift
    reg2dp_lut_lo_slope_uflow_shift_sync := io.reg2dp_lut.lo_slope_uflow_shift
    reg2dp_lut_lo_slope_oflow_shift_sync := io.reg2dp_lut.lo_slope_oflow_shift
    reg2dp_lut_le_slope_uflow_scale_sync := io.reg2dp_lut.le_slope_uflow_scale
    reg2dp_lut_le_slope_oflow_scale_sync := io.reg2dp_lut.le_slope_oflow_scale
    reg2dp_lut_lo_slope_uflow_scale_sync := io.reg2dp_lut.lo_slope_uflow_scale
    reg2dp_lut_lo_slope_oflow_scale_sync := io.reg2dp_lut.lo_slope_oflow_scale

    ///////////////////////////////////////////
    val le_slope_uflow_scale = Cat(reg2dp_lut_le_slope_uflow_scale_sync(15), reg2dp_lut_le_slope_uflow_scale_sync(15,0))
    val le_slope_oflow_scale = Cat(reg2dp_lut_le_slope_oflow_scale_sync(15), reg2dp_lut_le_slope_oflow_scale_sync(15,0))
    val lo_slope_uflow_scale = Cat(reg2dp_lut_lo_slope_uflow_scale_sync(15), reg2dp_lut_lo_slope_uflow_scale_sync(15,0))
    val lo_slope_oflow_scale = Cat(reg2dp_lut_lo_slope_oflow_scale_sync(15), reg2dp_lut_lo_slope_oflow_scale_sync(15,0))

    ///////////////////////////////////////////
    //lut2intp pipe sync for timing
    //pack
    val lut2intp_pd_data = VecInit((0 to (conf.NVDLA_CDP_THROUGHPUT-1)) 
    map {i => Cat(io.lut2intp.bits(i).x_data_0, io.lut2intp.bits(i).x_data_0_17b, io.lut2intp.bits(i).x_data_1)}).asUInt
    val lut2intp_pd_info = VecInit((0 to (conf.NVDLA_CDP_THROUGHPUT-1)) 
    map {i => io.lut2intp.bits(i).x_info}).asUInt
    val lut2intp_x_sel = VecInit((0 to (conf.NVDLA_CDP_THROUGHPUT-1)) 
    map {i => io.lut2intp.bits(i).x_sel}).asUInt
    val lut2intp_y_sel = VecInit((0 to (conf.NVDLA_CDP_THROUGHPUT-1)) 
    map {i => io.lut2intp.bits(i).y_sel}).asUInt    
    val lut2intp_pd = Cat(lut2intp_pd_data, lut2intp_pd_info, lut2intp_x_sel, lut2intp_y_sel)
    //one fifo
    val lut2intp_ready = Wire(Bool())
    val pipe = Module(new NV_NVDLA_IS_pipe(conf.NVDLA_CDP_THROUGHPUT*103))
    pipe.io.clk := io.nvdla_core_clk
    pipe.io.vi := io.lut2intp.valid
    io.lut2intp.ready := pipe.io.ro
    pipe.io.di := lut2intp_pd
    val lut2intp_valid = pipe.io.vo
    pipe.io.ri := lut2intp_ready
    val lut2intp_data = pipe.io.dout

    //unpack
    val lut2ip_x_data_pd = lut2intp_data(103*conf.NVDLA_CDP_THROUGHPUT -1, 22*conf.NVDLA_CDP_THROUGHPUT)
    val lut2ip_x_info_pd = lut2intp_data(22*conf.NVDLA_CDP_THROUGHPUT - 1, 2*conf.NVDLA_CDP_THROUGHPUT)
    val lut2ip_x_sel = lut2intp_data(2*conf.NVDLA_CDP_THROUGHPUT-1, conf.NVDLA_CDP_THROUGHPUT)
    val lut2ip_y_sel = lut2intp_data(conf.NVDLA_CDP_THROUGHPUT-1, 0)

    val lut2ip_x_data_1 = VecInit((0 to (conf.NVDLA_CDP_THROUGHPUT-1)) 
    map {i => lut2ip_x_data_pd(32*i + 31, 32*i)})
    val lut2ip_x_data_0_17b = VecInit((0 to (conf.NVDLA_CDP_THROUGHPUT-1)) 
    map {i => lut2ip_x_data_pd(32*i + 48, 32*i + 32)})
    val lut2ip_x_data_0 = VecInit((0 to (conf.NVDLA_CDP_THROUGHPUT-1)) 
    map {i => lut2ip_x_data_pd(32*i + 80, 32*i + 49)})
    val lut2ip_x_info  = VecInit((0 to (conf.NVDLA_CDP_THROUGHPUT-1)) 
    map {i => lut2ip_x_info_pd(20*i + 19, 20*i)})

    
    ///////////////////////////////////////////
    //lock
    //from lut2int and sync2itp to intp_in
    val intp_in_prdy = Wire(Bool())
    lut2intp_ready := intp_in_prdy & io.sync2itp_pd.valid
    io.sync2itp_pd.ready := intp_in_prdy & lut2intp_valid
    val intp_in_pvld = io.sync2itp_pd.valid & lut2intp_valid

    ///////////////////////////////////////////
    val xinterp_in_rdy = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))
    val info_in_rdy = Wire(Bool())
    intp_in_prdy := (xinterp_in_rdy.asUInt.andR) & info_in_rdy

    val sqbw = conf.NVDLA_CDP_ICVTO_BWPE * 2 + 3
    val hit_in1_pd = VecInit(
        (0 to conf.NVDLA_CDP_THROUGHPUT-1) map {
            i => Mux(
                sqsum_bypass_enable, 
                Cat(Fill((38 - sqbw), io.sync2itp_pd.bits(sqbw * (i + 1) - 1)), io.sync2itp_pd.bits((sqbw * (i + 1) - 1), (sqbw * i))),
                Cat(Fill(17, false.B), io.sync2itp_pd.bits((sqbw * (i + 1) - 1), (sqbw * i)))
            )
            }
    )

    /////////////////////////////////////////////////
    //start/end prepare for out of range interpolation
    /////////////////////////////////////////////////

    val lut_le_end = Cat(io.reg2dp_lut.le_end_high, io.reg2dp_lut.le_end_low)
    val lut_le_start = Cat(io.reg2dp_lut.le_start_high, io.reg2dp_lut.le_start_low)
    val lut_lo_end = Cat(io.reg2dp_lut.lo_end_high, io.reg2dp_lut.lo_end_low)
    val lut_lo_start = Cat(io.reg2dp_lut.lo_start_high, io.reg2dp_lut.lo_start_low)

    val le_offset_use = io.reg2dp_lut.le_index_offset(6,0)
    val le_offset_exp = Mux(io.reg2dp_lut.le_index_offset(7), 0.U, (1.U << le_offset_use))

    val lut_le_min_int = Mux(x_exp, 
                        (Cat(Fill(91, lut_le_start(37)), lut_le_start).asSInt +& le_offset_exp.asSInt).asUInt, 
                        Cat(Fill(92, lut_le_start(37)), lut_le_start))(37, 0)

    val lut_le_max = RegInit(0.U(38.W))
    val lut_le_min = RegInit(0.U(39.W))
    val lut_lo_max = RegInit(0.U(38.W))
    val lut_lo_min = RegInit(0.U(38.W))

    lut_le_max := lut_le_end
    lut_le_min := lut_le_min_int
    lut_lo_max := lut_lo_end
    lut_lo_min := lut_lo_start

    /////////////////////////////////////////////////

    val x_uflow = VecInit(
        (0 to (conf.NVDLA_CDP_THROUGHPUT-1)) map {i => lut2ip_x_info(i)(16)}
    )
    val x_oflow = VecInit(
        (0 to (conf.NVDLA_CDP_THROUGHPUT-1)) map {i => lut2ip_x_info(i)(17)}
    )
    val y_uflow = VecInit(
        (0 to (conf.NVDLA_CDP_THROUGHPUT-1)) map {i => lut2ip_x_info(i)(18)}
    )
    val y_oflow = VecInit(
        (0 to (conf.NVDLA_CDP_THROUGHPUT-1)) map {i => lut2ip_x_info(i)(19)}
    )

    val xinterp_in0_pd = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(39.W)))

    for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT-1)){
        when(lut2ip_x_sel(i)){
            when(x_uflow(i)){
                xinterp_in0_pd(i) := lut_le_min
            }.elsewhen(x_oflow(i)){
                xinterp_in0_pd(i) := Cat(lut_le_max(37), lut_le_max(37,0))
            }.otherwise{
                xinterp_in0_pd(i) := Cat(Fill(7, lut2ip_x_data_0(i)(31)), lut2ip_x_data_0(i))
            }
        }.elsewhen(lut2ip_y_sel(i)){
            when(y_uflow(i)){
                xinterp_in0_pd(i) := Cat(lut_lo_min(37), lut_lo_min)
            }.elsewhen(y_oflow(i)){
                xinterp_in0_pd(i) := Cat(lut_lo_max(37), lut_lo_max)
            }.otherwise{
                xinterp_in0_pd(i) := Cat(Fill(7, lut2ip_x_data_0(i)(31)), lut2ip_x_data_0(i))
            }
        }.otherwise{
            xinterp_in0_pd(i) := 0.U
        }
    }

    val xinterp_in1_pd = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(38.W)))
    for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT-1)){
        when(lut2ip_x_sel(i)){
            when(x_uflow(i) | x_oflow(i)){
                xinterp_in1_pd(i) := hit_in1_pd(i)
            }.otherwise{
                xinterp_in1_pd(i) := Cat(Fill(6, lut2ip_x_data_1(i)(31)), lut2ip_x_data_1(i))
            }
        }.elsewhen(lut2ip_y_sel(i)){
            when(y_uflow(i) | y_oflow(i)){
                xinterp_in1_pd(i) := hit_in1_pd(i)
            }.otherwise{
                xinterp_in1_pd(i) := Cat(Fill(6, lut2ip_x_data_1(i)(31)), lut2ip_x_data_1(i))
            }
        }.otherwise{
            xinterp_in1_pd(i) := 0.U
        }
    }

    val xinterp_in_pd = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(17.W)))
    for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT-1)){
        when(lut2ip_x_sel(i) | lut2ip_y_sel(i)){
            xinterp_in_pd(i) := lut2ip_x_data_0_17b(i)
        }.otherwise{
            xinterp_in_pd(i) := 0.U
        }
    }

    val xinterp_in_scale = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(17.W)))
    for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT-1)){
        when(lut2ip_x_sel(i)){
            when(x_uflow(i)){
                xinterp_in_scale(i) := le_slope_uflow_scale
            }.elsewhen(x_oflow(i)){
                xinterp_in_scale(i) := le_slope_oflow_scale
            }.otherwise{
                xinterp_in_scale(i) := Cat(false.B, lut2ip_x_info(i)(15,0))
            }
        }.elsewhen(lut2ip_y_sel(i)){
            when(y_uflow(i)){
                xinterp_in_scale(i) := lo_slope_uflow_scale
            }.elsewhen(y_oflow(i)){
                xinterp_in_scale(i) := lo_slope_oflow_scale
            }.otherwise{
                xinterp_in_scale(i) := Cat(false.B, lut2ip_x_info(i)(15,0))
            }
        }.otherwise{
            xinterp_in_scale(i) := 0.U
        }
    }

    val xinterp_in_shift = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(6.W)))
    for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT-1)){
        when(lut2ip_x_sel(i)){
            when(x_uflow(i)){
                xinterp_in_shift(i) := Cat(reg2dp_lut_le_slope_uflow_shift_sync(4), reg2dp_lut_le_slope_uflow_shift_sync)
            }.elsewhen(x_oflow(i)){
                xinterp_in_shift(i) := Cat(reg2dp_lut_le_slope_oflow_shift_sync(4), reg2dp_lut_le_slope_oflow_shift_sync)
            }.otherwise{
                xinterp_in_shift(i) := Cat(false.B, 16.U(5.W))
            }
        }.elsewhen(lut2ip_y_sel(i)){
            when(y_uflow(i)){
                xinterp_in_shift(i) := Cat(reg2dp_lut_lo_slope_uflow_shift_sync(4), reg2dp_lut_lo_slope_uflow_shift_sync)
            }.elsewhen(y_oflow(i)){
                xinterp_in_shift(i) := Cat(reg2dp_lut_lo_slope_oflow_shift_sync(4), reg2dp_lut_lo_slope_oflow_shift_sync)
            }.otherwise{
                xinterp_in_shift(i) := Cat(false.B, 16.U(5.W))
            }
        }.otherwise{
            xinterp_in_shift(i) := 0.U
        } 
    }  

    val xinterp_in_vld = VecInit((0 to (conf.NVDLA_CDP_THROUGHPUT-1)) map 
            {i => intp_in_pvld & info_in_rdy & (xinterp_in_rdy.drop(i).reduce(_ && _))})
    val xinterp_out_rdy = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))
    val xinterp_out_pd = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(17.W)))
    val xinterp_out_vld = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))

    val u_interp_x = Array.fill(conf.NVDLA_CDP_THROUGHPUT){Module(new NV_NVDLA_CDP_DP_INTP_unit)}
    for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT-1)){
        u_interp_x(i).io.nvdla_core_clk := io.nvdla_core_clk

        u_interp_x(i).io.interp_in.valid := xinterp_in_vld(i)
        xinterp_in_rdy(i) := u_interp_x(i).io.interp_in.ready
        u_interp_x(i).io.interp_in.bits.pd0 := xinterp_in0_pd(i)
        u_interp_x(i).io.interp_in.bits.pd1 := xinterp_in1_pd(i)
        u_interp_x(i).io.interp_in.bits.pd := xinterp_in_pd(i)
        u_interp_x(i).io.interp_in.bits.scale := xinterp_in_scale(i)
        u_interp_x(i).io.interp_in.bits.shift := xinterp_in_shift(i)
        
        xinterp_out_vld(i) := u_interp_x(i).io.interp_out_pd.valid
        u_interp_x(i).io.interp_out_pd.ready := xinterp_out_rdy(i)
        xinterp_out_pd(i) := u_interp_x(i).io.interp_out_pd.bits
        
    }

    val intp_prdy = Wire(Bool())
    val info_o_vld = Wire(Bool())
    for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT-1)){
        xinterp_out_rdy(i) := intp_prdy & info_o_vld & (xinterp_out_vld.drop(i).reduce(_ && _))
    }

    val info_o_rdy = intp_prdy & xinterp_out_vld.asUInt.andR
    
    ///////////////////////////////////////////////
    //process for normal uflow/oflow info
    val info_in_vld = intp_in_pvld & (xinterp_in_rdy.asUInt.andR);

    val info_xin_pd = VecInit((0 to (conf.NVDLA_CDP_THROUGHPUT-1)) map 
            {i => lut2ip_x_info(i)(17, 16)}).asUInt
    val info_yin_pd = VecInit((0 to (conf.NVDLA_CDP_THROUGHPUT-1)) map 
            {i => lut2ip_x_info(i)(19, 18)}).asUInt
    val dat_info_in = Cat(info_xin_pd, info_yin_pd)
    val info_in_pd = dat_info_in

    val u_intpinfo_sync_fifo = Module(new NV_NVDLA_fifo(20, conf.NVDLA_CDP_THROUGHPUT*4))
    u_intpinfo_sync_fifo.io.clk := io.nvdla_core_clk
    u_intpinfo_sync_fifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    u_intpinfo_sync_fifo.io.wr_pvld := info_in_vld
    info_in_rdy := u_intpinfo_sync_fifo.io.wr_prdy
    u_intpinfo_sync_fifo.io.wr_pd := info_in_pd

    info_o_vld := u_intpinfo_sync_fifo.io.rd_pvld
    u_intpinfo_sync_fifo.io.rd_prdy := info_o_rdy
    val info_o_pd = u_intpinfo_sync_fifo.io.rd_pd

    val x_info = VecInit((0 to (conf.NVDLA_CDP_THROUGHPUT-1)) map 
                            {i => info_o_pd(i*2+1,i*2)})
    val y_info = VecInit((0 to (conf.NVDLA_CDP_THROUGHPUT-1)) map 
                            {i => info_o_pd((conf.NVDLA_CDP_THROUGHPUT*2+i*2+1),(conf.NVDLA_CDP_THROUGHPUT*2+i*2))})
    ////////////////////////////////////////////////
    val intp_pvld = info_o_vld & (xinterp_out_vld.asUInt.andR)
    val intp_pvld_d = RegInit(false.B)
    val intp_prdy_d = Wire(Bool())
    intp_prdy := ~intp_pvld_d | intp_prdy_d
    ////////

    when(intp_pvld){
        intp_pvld_d := true.B
    }.elsewhen(intp_prdy_d){
        intp_pvld_d := false.B
    }

    val ip2mul_pvld = intp_pvld_d

    val ip2mul_pd = RegInit(VecInit(Seq.fill(conf.NVDLA_CDP_THROUGHPUT)(0.U(17.W))))
    for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT-1)){
        when(intp_pvld & intp_prdy){
            ip2mul_pd(i) := xinterp_out_pd(i)
        }
    }

    ////////////////////////////////////////////////
    //LUT perf counters
    ////////////////////////////////////////////////

    val layer_done = io.dp2reg_done
    val both_hybrid_flag = RegInit(VecInit(Seq.fill(conf.NVDLA_CDP_THROUGHPUT)(false.B)))
    val both_of_flag = RegInit(VecInit(Seq.fill(conf.NVDLA_CDP_THROUGHPUT)(false.B)))
    val both_uf_flag = RegInit(VecInit(Seq.fill(conf.NVDLA_CDP_THROUGHPUT)(false.B)))
    val only_le_hit = RegInit(VecInit(Seq.fill(conf.NVDLA_CDP_THROUGHPUT)(false.B)))
    val only_lo_hit = RegInit(VecInit(Seq.fill(conf.NVDLA_CDP_THROUGHPUT)(false.B)))
    for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT-1)){
        when(intp_pvld & intp_prdy){
            both_hybrid_flag(i) :=  (Cat(x_info(i),y_info(i)) === "b0000".asUInt(4.W)) | 
                                    (Cat(x_info(i),y_info(i)) === "b0110".asUInt(4.W)) | 
                                    (Cat(x_info(i),y_info(i)) === "b1001".asUInt(4.W))
            both_of_flag(i) := (Cat(x_info(i),y_info(i)) === "b1010".asUInt(4.W))
            both_uf_flag(i) := (Cat(x_info(i),y_info(i)) === "b0101".asUInt(4.W))
            only_le_hit(i)  :=  (Cat(x_info(i),y_info(i)) === "b0001".asUInt(4.W)) | 
                                (Cat(x_info(i),y_info(i)) === "b0010".asUInt(4.W))
            only_lo_hit(i)  :=  (Cat(x_info(i),y_info(i)) === "b0100".asUInt(4.W)) | 
                                (Cat(x_info(i),y_info(i)) === "b1000".asUInt(4.W))
        }
    }

    def fun_bit_sum_8(idata: UInt): UInt = (idata(0) +& idata(1) +& idata(2)) +&
                                       (idata(3) +& idata(4) +& idata(5)) +&
                                       (idata(6) +& idata(7))

    val both_hybrid_ele = fun_bit_sum_8(both_hybrid_flag.asUInt)
    val both_of_ele = fun_bit_sum_8(both_of_flag.asUInt)
    val both_uf_ele = fun_bit_sum_8(both_uf_flag.asUInt)
    val only_le_hit_ele = fun_bit_sum_8(only_le_hit.asUInt)
    val only_lo_hit_ele = fun_bit_sum_8(only_lo_hit.asUInt)

    val both_hybrid_counter = RegInit(0.U(32.W))
    val both_of_counter = RegInit(0.U(32.W))
    val both_uf_counter = RegInit(0.U(32.W))
    val only_le_hit_counter = RegInit(0.U(32.W))
    val only_lo_hit_counter = RegInit(0.U(32.W))

    val both_hybrid_counter_nxt = both_hybrid_counter +& both_hybrid_ele
    val both_of_counter_nxt = both_of_counter +& both_of_ele
    val both_uf_counter_nxt = both_uf_counter +& both_uf_ele
    val only_le_hit_counter_nxt = only_le_hit_counter +& only_le_hit_ele
    val only_lo_hit_counter_nxt = only_lo_hit_counter +& only_lo_hit_ele

    when(layer_done){
        both_hybrid_counter := 0.U
        both_of_counter := 0.U
        both_uf_counter := 0.U
        only_le_hit_counter := 0.U
        only_lo_hit_counter := 0.U
    }.elsewhen(intp_pvld_d & intp_prdy_d){
        both_hybrid_counter := Mux(both_hybrid_counter_nxt(32), Fill(32, true.B), both_hybrid_counter_nxt(31, 0))
        both_of_counter := Mux(both_of_counter_nxt(32), Fill(32, true.B), both_of_counter_nxt(31, 0))
        both_uf_counter := Mux(both_uf_counter_nxt(32), Fill(32, true.B), both_uf_counter_nxt(31, 0))
        only_le_hit_counter := Mux(only_le_hit_counter_nxt(32), Fill(32, true.B), only_le_hit_counter_nxt(31, 0))
        only_lo_hit_counter := Mux(only_lo_hit_counter_nxt(32), Fill(32, true.B), only_lo_hit_counter_nxt(31, 0))
    }

    val layer_flg = RegInit(false.B)
    when(layer_done){
        layer_flg := ~ layer_flg
    }

    val dp2reg_d0_perf_lut_hybrid_reg = RegInit("b0".asUInt(32.W))
    val dp2reg_d0_perf_lut_oflow_reg = RegInit("b0".asUInt(32.W))
    val dp2reg_d0_perf_lut_uflow_reg = RegInit("b0".asUInt(32.W))
    val dp2reg_d0_perf_lut_le_hit_reg = RegInit("b0".asUInt(32.W))
    val dp2reg_d0_perf_lut_lo_hit_reg = RegInit("b0".asUInt(32.W))

    val dp2reg_d1_perf_lut_hybrid_reg = RegInit("b0".asUInt(32.W))
    val dp2reg_d1_perf_lut_oflow_reg = RegInit("b0".asUInt(32.W))
    val dp2reg_d1_perf_lut_uflow_reg = RegInit("b0".asUInt(32.W))
    val dp2reg_d1_perf_lut_le_hit_reg = RegInit("b0".asUInt(32.W))
    val dp2reg_d1_perf_lut_lo_hit_reg = RegInit("b0".asUInt(32.W))

    when(layer_done){
        when(~layer_flg){
            dp2reg_d0_perf_lut_hybrid_reg := both_hybrid_counter
            dp2reg_d0_perf_lut_oflow_reg := both_of_counter
            dp2reg_d0_perf_lut_uflow_reg := both_uf_counter
            dp2reg_d0_perf_lut_le_hit_reg := only_le_hit_counter
            dp2reg_d0_perf_lut_lo_hit_reg := only_lo_hit_counter
        }
        .elsewhen(layer_flg){
            dp2reg_d1_perf_lut_hybrid_reg := both_hybrid_counter
            dp2reg_d1_perf_lut_oflow_reg := both_of_counter
            dp2reg_d1_perf_lut_uflow_reg := both_uf_counter
            dp2reg_d1_perf_lut_le_hit_reg := only_le_hit_counter
            dp2reg_d1_perf_lut_lo_hit_reg := only_lo_hit_counter
        }
    }

    io.dp2reg_perf.d0_perf_lut_hybrid := dp2reg_d0_perf_lut_hybrid_reg
    io.dp2reg_perf.d0_perf_lut_oflow := dp2reg_d0_perf_lut_oflow_reg
    io.dp2reg_perf.d0_perf_lut_uflow := dp2reg_d0_perf_lut_uflow_reg
    io.dp2reg_perf.d0_perf_lut_le_hit := dp2reg_d0_perf_lut_le_hit_reg
    io.dp2reg_perf.d0_perf_lut_lo_hit := dp2reg_d0_perf_lut_lo_hit_reg

    io.dp2reg_perf.d1_perf_lut_hybrid := dp2reg_d1_perf_lut_hybrid_reg
    io.dp2reg_perf.d1_perf_lut_oflow := dp2reg_d1_perf_lut_oflow_reg
    io.dp2reg_perf.d1_perf_lut_uflow := dp2reg_d1_perf_lut_uflow_reg
    io.dp2reg_perf.d1_perf_lut_le_hit := dp2reg_d1_perf_lut_le_hit_reg
    io.dp2reg_perf.d1_perf_lut_lo_hit := dp2reg_d1_perf_lut_lo_hit_reg

    ////////////////////////////////////////////////
    //intp output pipe sync for timing
    ////////////////////////////////////////////////
    val ip2mul_pipe = Array.fill(conf.NVDLA_CDP_THROUGHPUT){Module(new NV_NVDLA_IS_pipe(17))}
    val ip2mul_prdy = Wire(Bool())
    for(i <- 0 to conf.NVDLA_CDP_THROUGHPUT-1){
        ip2mul_pipe(i).io.clk := io.nvdla_core_clk

        ip2mul_pipe(i).io.vi := ip2mul_pvld
        ip2mul_prdy := ip2mul_pipe(i).io.ro
        ip2mul_pipe(i).io.di := ip2mul_pd(i)

        io.intp2mul_pd.valid := ip2mul_pipe(i).io.vo
        ip2mul_pipe(i).io.ri := io.intp2mul_pd.ready
        io.intp2mul_pd.bits(i) := ip2mul_pipe(i).io.dout
    }

    intp_prdy_d := ip2mul_prdy
}}


object NV_NVDLA_CDP_DP_intpDriver extends App {
    implicit val conf: nvdlaConfig = new nvdlaConfig
    chisel3.Driver.execute(args, () => new NV_NVDLA_CDP_DP_intp())
}

