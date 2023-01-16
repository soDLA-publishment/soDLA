package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_CDP_DP_bufferin_tp1(implicit val conf: nvdlaConfig) extends Module {
    val buf2sq_data_bw = conf.NVDLA_CDP_ICVTO_BWPE*(conf.NVDLA_CDP_THROUGHPUT+8)
    val buf2sq_dp_bw = buf2sq_data_bw + 17
    val reg_num = 8/conf.NVDLA_CDP_THROUGHPUT + 1
    val reg_1stc_num = 4/conf.NVDLA_CDP_THROUGHPUT

    val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())
        val cdp_rdma2dp_pd = Flipped(DecoupledIO(UInt((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE+17).W)))
        val normalz_buf_data = DecoupledIO(UInt(buf2sq_dp_bw.W))
    })

withClock(io.nvdla_core_clk){
    /////////////////////////////////////////////////////////////
    //
    /////////////////////////////////////////////////////////////
    val nvdla_cdp_rdma2dp_ready = Wire(Bool())
    val nvdla_cdp_rdma2dp_valid = RegInit(false.B)
    io.cdp_rdma2dp_pd.ready := nvdla_cdp_rdma2dp_ready || (~nvdla_cdp_rdma2dp_valid)
    
    when(io.cdp_rdma2dp_pd.valid){
        nvdla_cdp_rdma2dp_valid := true.B
    }.elsewhen(nvdla_cdp_rdma2dp_ready){
        nvdla_cdp_rdma2dp_valid := false.B
    }

    val nvdla_cdp_rdma2dp_pd = RegInit(0.U((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE+17).W))
    when(io.cdp_rdma2dp_pd.valid & io.cdp_rdma2dp_pd.ready){
        nvdla_cdp_rdma2dp_pd := io.cdp_rdma2dp_pd.bits
    }

    /////////////////////////////////////////////////////////////
    //==============
    // INPUT UNPACK: from RDMA
    //==============
    val u_unpack = Module(new NV_NVDLA_CDP_DP_BUFFERIN_unpack)
    u_unpack.io.nvdla_cdp_rdma2dp_pd := nvdla_cdp_rdma2dp_pd
    val dp_data = u_unpack.io.dp_data
    val is_pos_w = u_unpack.io.is_pos_w
    val is_width = u_unpack.io.is_width
    val is_pos_c = u_unpack.io.is_pos_c
    val is_b_sync = u_unpack.io.is_b_sync
    val is_last_w = u_unpack.io.is_last_w
    val is_last_h = u_unpack.io.is_last_h
    val is_last_c = u_unpack.io.is_last_c

    ///////////////////////////////////////////////////
    val rdma2dp_ready_normal = Wire(Bool())
    val hold_here = RegInit(false.B)
    nvdla_cdp_rdma2dp_ready := rdma2dp_ready_normal & (~hold_here)
    val rdma2dp_valid_rebuild = nvdla_cdp_rdma2dp_valid | hold_here

    val vld = rdma2dp_valid_rebuild
    val load_din = vld & nvdla_cdp_rdma2dp_ready
    val load_din_full = rdma2dp_valid_rebuild & rdma2dp_ready_normal
    ///////////////////////////////////////////////////

    val is_4ele_here = is_pos_c === (4/conf.NVDLA_CDP_THROUGHPUT).toInt.U 
    val is_posc_end = is_pos_c === ((conf.NVDLA_MEMORY_ATOMIC_SIZE/conf.NVDLA_CDP_THROUGHPUT) - 1).U

    val wait :: normal_c :: first_c :: second_c :: cube_end :: Nil = Enum(5)

    val stat_cur = RegInit(wait)
    val stat_nex = WireInit(wait)
    val normalC2CubeEnd = WireInit(false.B)
    val more2less = Wire(Bool())
    val width_pre_cnt = RegInit(0.U(4.W))
    val width_pre = RegInit(0.U(4.W))
    val is_hold_4ele_done = Wire(Bool())
    val cube_done = Wire(Bool())

    switch (stat_cur) {
        is (wait) {
            when(is_b_sync & is_4ele_here & load_din){
                if(conf.NVDLA_MEMORY_ATOMIC_SIZE == 4){
                    when(is_posc_end & is_last_c & is_last_h & is_last_w){
                        normalC2CubeEnd := true.B
                        stat_nex := cube_end
                    }
                    .elsewhen(is_posc_end & is_last_c){
                        stat_nex := first_c
                    }
                    .otherwise{
                        stat_nex := normal_c
                    }
                }
                else{
                    stat_nex := normal_c 
                }
            }
        }
        is (normal_c) {
            when(is_b_sync & is_posc_end & is_last_c & is_last_h & is_last_w & load_din){
                normalC2CubeEnd := true.B
                stat_nex := cube_end
            }.elsewhen(is_b_sync & is_posc_end & is_last_c & load_din){
                stat_nex := first_c
            }
        }
        is (first_c) {
            when((is_4ele_here & (is_pos_w === is_width) & (~more2less) & load_din)
            || (more2less & (width_pre_cnt === width_pre) & is_hold_4ele_done & hold_here & rdma2dp_ready_normal)){
                    if(conf.NVDLA_MEMORY_ATOMIC_SIZE == 4){
                        when(is_posc_end & is_last_c & is_last_h & is_last_w){
                            stat_nex := cube_end
                        }
                        .elsewhen(is_posc_end & is_last_c){
                            stat_nex := first_c
                        }
                        .otherwise{
                            stat_nex := second_c
                        }
                    }
                    else if(conf.NVDLA_MEMORY_ATOMIC_SIZE > 4){
                        stat_nex := second_c
                    }
                }
        }
        is (second_c) {
            when(is_b_sync & load_din){
                stat_nex := normal_c
            }
        }
        is (cube_end) {
            when(cube_done){
                stat_nex := wait
            }
        }
    } 

    stat_cur := stat_nex
    /////////////////////////////////////////
    val data_shift_valid = RegInit(false.B)
    val data_shift_ready = Wire(Bool())
    val buf_dat_vld = RegInit(false.B)
    val buf_dat_rdy = Wire(Bool())
    val hold_here_dly = RegInit(false.B)
    val stat_cur_dly = RegInit(0.U(3.W))

    rdma2dp_ready_normal := (~data_shift_valid) | data_shift_ready
    when(vld){
        data_shift_valid := true.B
    }.elsewhen(data_shift_ready){
        data_shift_valid := false.B
    }

    data_shift_ready := (~buf_dat_vld | buf_dat_rdy)

    val data_shift_load_all = data_shift_ready & data_shift_valid
    val data_shift_load = data_shift_load_all & ((~hold_here_dly)  | (stat_cur_dly === cube_end))
    /////////////////////////////////

    val data_shift = RegInit(VecInit(Seq.fill(reg_num)(
        VecInit(Seq.fill(8)(0.U((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE).W)))))) 
        
    val data_1stC = if(conf.NVDLA_CDP_THROUGHPUT <= 4) Some(RegInit(VecInit(Seq.fill(reg_1stc_num)(
        VecInit(Seq.fill(8)(0.U((conf.NVDLA_CDP_THROUGHPUT*conf.NVDLA_CDP_ICVTO_BWPE).W)))))))
                    else None

    val cube_end_width_cnt = RegInit(0.U(4.W))

    switch (stat_cur) {
        is (wait) {
            when(load_din){
                for(m <- 0 to 7){
                    when(is_pos_w === m.U){
                        data_shift(0)(m) := dp_data
                    }
                    for(s <- 0 to reg_num -2){
                        data_shift(s+1)(m) := data_shift(s)(m)
                    }    
                }
                // reset all of un-used register in wait status
                if(conf.NVDLA_CDP_THROUGHPUT <= 4){
                    for(m <- 0 to 7){
                        for(k <- 0 to reg_1stc_num -1){
                            data_1stC.get(k)(m) := 0.U
                        }
                    }
                }
            }
        }
        is (normal_c) {
            when(load_din){
                for(i <- 0 to 7){
                    when(is_pos_w === i.U){
                        data_shift(0)(i) := dp_data
                        for(s <- 0 to reg_num -2){
                            data_shift(s+1)(i) := data_shift(s)(i)
                        }  
                    }
                }
            }
        }
        is (first_c) {
            for(i <- 0 to 7){
                when(hold_here & rdma2dp_ready_normal){
                    when(width_pre_cnt === i.U){
                        data_shift(0)(i) := 0.U
                        for(s <- 0 to reg_num -2){
                            data_shift(s+1)(i) := data_shift(s)(i)  
                        }
                    }
                }.elsewhen((is_pos_w === i.U) & load_din){
                    if(conf.NVDLA_CDP_THROUGHPUT <= 4){
                        data_1stC.get(0)(i) := dp_data
                    }
                    data_shift(0)(i) := 0.U
                    for(s <- 0 to reg_num -2 ){
                        data_shift(s+1)(i) := data_shift(s)(i)
                    }  
                    if(conf.NVDLA_CDP_THROUGHPUT <= 4){
                        val k = reg_1stc_num - 2
                        if(k >= 0){
                            for(s <- 0 to reg_1stc_num -2 ){
                                data_1stC.get(s+1)(i) := data_1stC.get(s)(i)
                            }  
                        }
                    }
                }
            }
        }
        is (second_c) {
            when(load_din){
                for(i <- 0 to 7){
                    when(is_pos_w === i.U){
                        data_shift(0)(i) := dp_data
                        if(conf.NVDLA_CDP_THROUGHPUT <= 4){
                            for(s <- 0 to reg_1stc_num - 1){
                                data_shift(s+1)(i) := data_1stC.get(s)(i)
                            }
                        }
                        for(s <- 0 to reg_1stc_num - 1){
                            data_shift(s+reg_1stc_num+1)(i) := 0.U
                        }
                    }
                }
            }
        }
        is (cube_end) {
            when(rdma2dp_ready_normal){
                for(i <- 0 to 7){
                    when(cube_end_width_cnt === i.U){
                        data_shift(0)(i) := 0.U
                        for(s <- 0 to reg_num - 2){
                            data_shift(s+1)(i) := data_shift(s)(i)
                        }
                    }
                }
            }
        }
        is (Nil){
            for(i <- 0 to 7){
                for(k <- 0 to reg_num -1){
                    data_shift(k)(i) := 0.U
                }
                for(k <- 0 to reg_1stc_num-1){
                    data_shift(k)(i) := 0.U
                }
            }
        }
    }

    when((stat_cur===normal_c) & is_last_c & is_b_sync & is_posc_end & load_din){
        width_pre := is_width
    }

    val width_cur_1 = Wire(UInt(4.W))
    when((stat_cur===first_c) & (is_pos_w === 0.U) & (is_pos_c === 0.U)){
        width_cur_1 := is_width 
    }.otherwise{
        width_cur_1 := 0.U
    }

    val width_cur_2 = RegInit(0.U(4.W))
    when((stat_cur===first_c) & (is_pos_w === 0.U) & load_din){
        width_cur_2 := is_width
    }

    val width_cur = Mux(((stat_cur===first_c) & (is_pos_w === 0.U)), width_cur_1, width_cur_2)

    more2less := (stat_cur===first_c) & (width_cur < width_pre)
    val less2more = (stat_cur===first_c) & (width_cur > width_pre)
    val l2m_1stC_vld = (stat_cur===first_c) & less2more & (is_pos_w <= width_pre)

    when((stat_cur===first_c) & more2less){
        when((is_pos_w===is_width) & load_din){
            hold_here := true.B
        }.elsewhen((width_pre_cnt === width_pre) & rdma2dp_ready_normal){
            hold_here := false.B
        }
    }.elsewhen(normalC2CubeEnd){
        hold_here := true.B
    }.elsewhen(cube_done){
        hold_here := false.B
    }

    when((stat_cur===first_c) & more2less){
        when((is_pos_w===is_width) & load_din){
            width_pre_cnt := is_width + 1.U
        }.elsewhen(hold_here & rdma2dp_ready_normal){
            width_pre_cnt := width_pre_cnt + 1.U
        }
    }.otherwise{
        width_pre_cnt := 0.U
    }

    val hold_4ele_cnt = RegInit(0.U(2.W))
    when((stat_cur===first_c) & more2less & hold_here & (width_pre_cnt === width_pre) & rdma2dp_ready_normal){
        when(is_hold_4ele_done){
            hold_4ele_cnt := 0.U
        }.otherwise{
            hold_4ele_cnt := hold_4ele_cnt + 1.U
        }
    }


    is_hold_4ele_done := hold_4ele_cnt === (4/conf.NVDLA_CDP_THROUGHPUT).U

    //the last block data need to be output in cube end
    val last_width = RegInit(0.U(4.W))
    val cube_end_c_cnt = RegInit(0.U(3.W))

    when(normalC2CubeEnd & load_din){
        last_width := is_width
    }

    when(stat_cur === cube_end){
        when(rdma2dp_ready_normal){
            when(cube_end_width_cnt === last_width){
                cube_end_width_cnt := 0.U
                cube_end_c_cnt := cube_end_c_cnt + 1.U
            }.otherwise{
                cube_end_width_cnt := cube_end_width_cnt + 1.U
            }
        }
    }.otherwise{
        cube_end_width_cnt := 0.U
        cube_end_c_cnt := 0.U
    } 

    
    if(conf.NVDLA_CDP_THROUGHPUT == 1){
        cube_done := (stat_cur === cube_end) & (cube_end_width_cnt === last_width) & (cube_end_c_cnt === 3.U) & rdma2dp_ready_normal
    }
    else if(conf.NVDLA_CDP_THROUGHPUT == 2){
         cube_done := (stat_cur === cube_end) & (cube_end_width_cnt === last_width) & (cube_end_c_cnt === 1.U) & rdma2dp_ready_normal
    }
    else if(conf.NVDLA_CDP_THROUGHPUT >= 4){
         cube_done := (stat_cur === cube_end) & (cube_end_width_cnt === last_width) & rdma2dp_ready_normal
    }   
    
    //1pipe delay for buffer data generation
    val more2less_dly = RegInit(false.B)
    val less2more_dly = RegInit(false.B)
    val is_pos_w_dly = RegInit(0.U(4.W))
    val width_pre_cnt_dly = RegInit(0.U(4.W))
    val width_pre_dly = RegInit(0.U(4.W))

    when(load_din_full === true.B){
        stat_cur_dly := stat_cur
        more2less_dly := more2less
        less2more_dly := less2more
        hold_here_dly := hold_here
        width_pre_cnt_dly := width_pre_cnt
        width_pre_dly := width_pre
    }

    when((stat_cur === cube_end) & rdma2dp_ready_normal){
        is_pos_w_dly := cube_end_width_cnt
    }.elsewhen(load_din){
        is_pos_w_dly := is_pos_w
    }

    /////////////////////////////
    //buffer data generation for output data
    val buffer_data = RegInit(0.U(buf2sq_data_bw.W))

    
    when((stat_cur_dly === normal_c) || (stat_cur_dly === second_c) || (stat_cur_dly === cube_end & data_shift_load)){
        if(conf.NVDLA_CDP_THROUGHPUT == 1){
            for(i <- 0 to 7){
                when(is_pos_w_dly === i.U){
                    buffer_data := Cat(data_shift(0)(i), data_shift(1)(i), data_shift(2)(i), data_shift(3)(i), 
                                       data_shift(4)(i), data_shift(5)(i), data_shift(6)(i), data_shift(7)(i), data_shift(8)(i))
                }
            }
        }
        else if(conf.NVDLA_CDP_THROUGHPUT == 2){
            for(i <- 0 to 7){
                when(is_pos_w_dly === i.U){
                    buffer_data := Cat(data_shift(0)(i), data_shift(1)(i), data_shift(2)(i), data_shift(3)(i), data_shift(4)(i))
                }
            }
        }
    } 
    .elsewhen(stat_cur_dly === first_c){
        when(more2less_dly){
            when(data_shift_load){
                if(conf.NVDLA_CDP_THROUGHPUT == 1){
                    for(i <- 0 to 7){
                        when(is_pos_w_dly === i.U){
                            buffer_data := Cat(data_shift(0)(i), data_shift(1)(i), data_shift(2)(i), data_shift(3)(i), 
                                            data_shift(4)(i), data_shift(5)(i), data_shift(6)(i), data_shift(7)(i), data_shift(8)(i))
                        }
                    }
                }
                else if(conf.NVDLA_CDP_THROUGHPUT == 2){
                    for(i <- 0 to 7){
                        when(is_pos_w_dly === i.U){
                            buffer_data := Cat(data_shift(0)(i), data_shift(1)(i), data_shift(2)(i), data_shift(3)(i), data_shift(4)(i))
                        }
                    }
                }               
            }
            .elsewhen(hold_here_dly & data_shift_ready){
                if(conf.NVDLA_CDP_THROUGHPUT == 1){
                    for(i <- 0 to 7){
                        when(width_pre_cnt_dly === i.U){
                            buffer_data := Cat(data_shift(0)(i), data_shift(1)(i), data_shift(2)(i), data_shift(3)(i), 
                                            data_shift(4)(i), data_shift(5)(i), data_shift(6)(i), data_shift(7)(i), data_shift(8)(i))
                        }
                    }
                }
                else if(conf.NVDLA_CDP_THROUGHPUT == 2){
                    for(i <- 0 to 7){
                        when(width_pre_cnt_dly === i.U){
                            buffer_data := Cat(data_shift(0)(i), data_shift(1)(i), data_shift(2)(i), data_shift(3)(i), data_shift(4)(i))
                        }
                    }
                }       
            }
        }
        .otherwise{
            when((is_pos_w_dly<=width_pre_dly) & data_shift_load){
                if(conf.NVDLA_CDP_THROUGHPUT == 1){
                    for(i <- 0 to 7){
                        when(width_pre_cnt_dly === i.U){
                            buffer_data := Cat(data_shift(0)(i), data_shift(1)(i), data_shift(2)(i), data_shift(3)(i), 
                                                data_shift(4)(i), data_shift(5)(i), data_shift(6)(i), data_shift(7)(i), data_shift(8)(i))
                        }
                    }
                }
                else if(conf.NVDLA_CDP_THROUGHPUT == 2){
                    for(i <- 0 to 7){
                        when(width_pre_cnt_dly === i.U){
                            buffer_data := Cat(data_shift(0)(i), data_shift(1)(i), data_shift(2)(i), data_shift(3)(i), data_shift(4)(i))
                        }
                    }
                }                      
            }
            .elsewhen(data_shift_load){
                buffer_data := 0.U
            }
        }
    }
    .elsewhen(data_shift_ready){
        buffer_data := 0.U
    }
 

    when(data_shift_valid){
        buf_dat_vld := true.B
    }.elsewhen(buf_dat_rdy){
        buf_dat_vld := false.B
    }

//assign buf_dat_rdy = buffer_ready;

    val stat_cur_dly2 = RegInit(0.U(3.W))
    val less2more_dly2 = RegInit(false.B)
    val is_pos_w_dly2 = RegInit(0.U(4.W))
    val width_pre_dly2 = RegInit(0.U(4.W))
    when(data_shift_load_all){
        stat_cur_dly2 := stat_cur_dly
        less2more_dly2 := less2more_dly
        is_pos_w_dly2 := is_pos_w_dly
        width_pre_dly2 := width_pre_dly
    }

    val buffer_data_vld = Wire(Bool())
    when(((stat_cur_dly2===first_c) & less2more_dly2 & (is_pos_w_dly2 > width_pre_dly2)) || (stat_cur_dly2===wait)){
        buffer_data_vld := false.B
    }.otherwise{
        buffer_data_vld := buf_dat_vld
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    //output data_info generation
    ///////////////////////////////////////////////////////////////////////////////////////////

    val first_c_end = ((stat_cur === first_c) & (width_pre_cnt === width_pre) & more2less & rdma2dp_ready_normal)
    val first_c_bf_end = ((stat_cur === first_c) & (width_pre_cnt < width_pre) & more2less)

    val width_align = RegInit(0.U(4.W))
    val last_w_align = RegInit(false.B)
    val last_h_align = RegInit(false.B)
    val last_c_align = RegInit(false.B)
    val pos_c_align = RegInit(0.U(5.W))
    val pos_w_align = Wire(UInt(4.W))
    val b_sync_align = Wire(Bool())

    when((is_b_sync & load_din & (~first_c_bf_end)) | first_c_end){
        width_align := is_width
        last_w_align := is_last_w
        last_h_align := is_last_h
        last_c_align := is_last_c
    }

    when(first_c_end){
        pos_c_align := 0.U
    }.elsewhen(is_b_sync & load_din & (~first_c_bf_end)){
        pos_c_align := is_pos_c
    }

    when(stat_cur === cube_end){
        pos_w_align := cube_end_width_cnt
    }.elsewhen(stat_cur === wait){
        pos_w_align := 0.U
    }.elsewhen(stat_cur === first_c){
        when(more2less){
            when(hold_here){
                pos_w_align := width_pre_cnt
            }.otherwise{
                pos_w_align := is_pos_w
            }
        }.elsewhen(less2more){
            when(is_pos_w <= width_pre){
                pos_w_align := is_pos_w
            }.otherwise{
                pos_w_align := 0.U
            }
        }.otherwise{
            pos_w_align := is_pos_w
        }
    }.otherwise{
        pos_w_align := is_pos_w
    }

    when(stat_cur === cube_end){
        b_sync_align := cube_done
    }.elsewhen(stat_cur === wait){
        b_sync_align := false.B
    }.elsewhen(stat_cur === first_c){
        when(more2less){
            b_sync_align := (width_pre_cnt === width_pre)
        }.elsewhen(less2more){
            b_sync_align := (is_pos_w === width_pre) & load_din
        }.otherwise{
            b_sync_align := (is_b_sync & load_din)
        }
    }.otherwise{
        b_sync_align := (is_b_sync & load_din)
    }

    ///////////////////
    //Two cycle delay
    ///////////////////
    val u_delay = Module(new NV_NVDLA_CDP_DP_BUFFERIN_two_cycle_delay)
    u_delay.io.nvdla_core_clk := io.nvdla_core_clk
    u_delay.io.load_din := load_din
    u_delay.io.stat_cur := stat_cur
    u_delay.io.first_c := first_c
    u_delay.io.normal_c := normal_c
    u_delay.io.second_c := second_c
    u_delay.io.cube_end := cube_end
    u_delay.io.rdma2dp_ready_normal := rdma2dp_ready_normal
    u_delay.io.l2m_1stC_vld := l2m_1stC_vld
    u_delay.io.data_shift_load_all := data_shift_load_all
    u_delay.io.more2less := more2less
    u_delay.io.less2more := less2more
    u_delay.io.hold_here := hold_here

    u_delay.io.pos_w_align := pos_w_align
    u_delay.io.width_align := width_align
    u_delay.io.pos_c_align := pos_c_align
    u_delay.io.b_sync_align := b_sync_align
    u_delay.io.last_w_align := last_w_align
    u_delay.io.last_h_align := last_h_align
    u_delay.io.last_c_align := last_c_align

    val buffer_pos_w = u_delay.io.buffer_pos_w
    val buffer_width = u_delay.io.buffer_width
    val buffer_pos_c = u_delay.io.buffer_pos_c
    val buffer_b_sync = u_delay.io.buffer_b_sync
    val buffer_last_w = u_delay.io.buffer_last_w
    val buffer_last_h = u_delay.io.buffer_last_h
    val buffer_last_c = u_delay.io.buffer_last_c

    /////////////////////////////////////////
    val buffer_pd = Cat(
        buffer_last_c, buffer_last_h, buffer_last_w, buffer_b_sync, 
        buffer_pos_c, buffer_width, buffer_pos_w, buffer_data
        )

    val buffer_valid = buffer_data_vld

    /////////////////////////////////////////
    val pipe_p1 = Module(new NV_NVDLA_IS_pipe((conf.NVDLA_CDP_THROUGHPUT+8)*conf.NVDLA_CDP_ICVTO_BWPE+17))
    pipe_p1.io.clk := io.nvdla_core_clk
    pipe_p1.io.vi := buffer_valid
    val buffer_ready = pipe_p1.io.ro
    pipe_p1.io.di := buffer_pd
    io.normalz_buf_data.valid := pipe_p1.io.vo
    pipe_p1.io.ri := io.normalz_buf_data.ready
    io.normalz_buf_data.bits := pipe_p1.io.dout

    buf_dat_rdy := buffer_ready
}}

object NV_NVDLA_CDP_DP_bufferin_tp1Driver extends App {
    implicit val conf: nvdlaConfig = new nvdlaConfig
    chisel3.Driver.execute(args, () => new NV_NVDLA_CDP_DP_bufferin_tp1())
}

