package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_SDP_RDMA_EG_ro(implicit val conf: sdpConfiguration) extends Module {
   val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        val pwrbus_ram_pd = Input(UInt(32.W))

        val sdp_rdma2dp_valid = Output(Bool())
        val sdp_rdma2dp_ready = Input(Bool())
        val sdp_rdma2dp_pd = Output(UInt((conf.AM_DW2 + 1).W))

        val rod0_wr_pd = Input(UInt(conf.AM_DW.W))
        val rod1_wr_pd = Input(UInt(conf.AM_DW.W))
        val rod2_wr_pd = Input(UInt(conf.AM_DW.W))
        val rod3_wr_pd = Input(UInt(conf.AM_DW.W))

        val rod_wr_mask = Input(UInt(4.W))
        val rod_wr_vld = Input(Bool())
        val rod_wr_rdy = Output(Bool())
        val roc_wr_pd = Input(UInt(2.W))
        val roc_wr_vld = Input(Bool())
        val roc_wr_rdy = Output(Bool())
        val cfg_dp_8 = Input(Bool())
        val cfg_dp_size_1byte = Input(Bool())
        val cfg_mode_per_element = Input(Bool())
// #ifdef NVDLA_BATCH_ENABLE
//...
        // val cfg_mode_multi_batch = Input(Bool())
        // val reg2dp_batch_number = Input(UInt(5.W))

        val reg2dp_channel = Input(UInt(13.W))
        val reg2dp_height = Input(UInt(13.W))
        val reg2dp_width = Input(UInt(13.W))
        val layer_end = Output(Bool())
    })
    //     
    //          ┌─┐       ┌─┐
    //       ┌──┘ ┴───────┘ ┴──┐
    //       │                 │
    //       │       ───       │          
    //       │  ─┬┘       └┬─  │
    //       │                 │
    //       │       ─┴─       │
    //       │                 │
    //       └───┐         ┌───┘
    //           │         │
    //           │         │
    //           │         │
    //           │         └──────────────┐
    //           │                        │
    //           │                        ├─┐
    //           │                        ┌─┘    
    //           │                        │
    //           └─┐  ┐  ┌───────┬──┐  ┌──┘         
    //             │ ─┤ ─┤       │ ─┤ ─┤         
    //             └──┴──┘       └──┴──┘ 
withClock(io.nvdla_core_clk){


//=======================================================
// DATA FIFO: WRITE SIDE
//=======================================================

    val rod0_wr_prdy = Wire(Bool())
    val rod1_wr_prdy = Wire(Bool())
    val rod2_wr_prdy = Wire(Bool())
    val rod3_wr_prdy = Wire(Bool())

    io.rod_wr_rdy := !( io.rod_wr_mask(0) & !rod0_wr_prdy |
                        io.rod_wr_mask(1) & !rod1_wr_prdy | 
                        io.rod_wr_mask(2) & !rod2_wr_prdy | 
                        io.rod_wr_mask(3) & !rod3_wr_prdy )
    val rod0_wr_pvld = io.rod_wr_vld & io.rod_wr_mask(0) & 
                        !(  io.rod_wr_mask(1) & !rod1_wr_prdy | 
                            io.rod_wr_mask(2) & !rod2_wr_prdy | 
                            io.rod_wr_mask(3) & !rod3_wr_prdy )
    val rod1_wr_pvld = io.rod_wr_vld & io.rod_wr_mask(1) & 
                        !(  io.rod_wr_mask(0) & !rod1_wr_prdy | 
                            io.rod_wr_mask(2) & !rod2_wr_prdy | 
                            io.rod_wr_mask(3) & !rod3_wr_prdy )
    val rod2_wr_pvld = io.rod_wr_vld & io.rod_wr_mask(2) & 
                        !(  io.rod_wr_mask(0) & !rod1_wr_prdy | 
                            io.rod_wr_mask(1) & !rod2_wr_prdy | 
                            io.rod_wr_mask(3) & !rod3_wr_prdy ) 
    val rod3_wr_pvld = io.rod_wr_vld & io.rod_wr_mask(3) & 
                        !(  io.rod_wr_mask(0) & !rod1_wr_prdy | 
                            io.rod_wr_mask(1) & !rod2_wr_prdy | 
                            io.rod_wr_mask(2) & !rod3_wr_prdy )  
    
    val rod0_rd_prdy = Wire(Bool())
    val rod1_rd_prdy = Wire(Bool())
    val rod2_rd_prdy = Wire(Bool())
    val rod3_rd_prdy = Wire(Bool())

    val u_rod0 = Module(new NV_NVDLA_IS_pipe(conf.AM_DW))  //is pipe
    u_rod0.io.clk    := io.nvdla_core_clk
    rod0_wr_prdy     := u_rod0.io.ro
    u_rod0.io.vi     := rod0_wr_pvld
    u_rod0.io.di     := io.rod0_wr_pd
    u_rod0.io.ri     := rod0_rd_prdy
    val rod0_rd_pvld  = u_rod0.io.vo
    val rod0_rd_pd    = u_rod0.io.dout
    //No pwrbus_ram_pd

    val u_rod1 = Module(new NV_NVDLA_IS_pipe(conf.AM_DW))  
    u_rod1.io.clk    := io.nvdla_core_clk
    rod1_wr_prdy     := u_rod1.io.ro
    u_rod1.io.vi     := rod1_wr_pvld
    u_rod1.io.di     := io.rod1_wr_pd
    u_rod1.io.ri     := rod1_rd_prdy
    val rod1_rd_pvld  = u_rod1.io.vo
    val rod1_rd_pd    = u_rod1.io.dout

    val u_rod2 = Module(new NV_NVDLA_IS_pipe(conf.AM_DW))  
    u_rod2.io.clk    := io.nvdla_core_clk
    rod2_wr_prdy     := u_rod2.io.ro
    u_rod2.io.vi     := rod2_wr_pvld
    u_rod2.io.di     := io.rod2_wr_pd
    u_rod2.io.ri     := rod2_rd_prdy
    val rod2_rd_pvld  = u_rod2.io.vo
    val rod2_rd_pd    = u_rod2.io.dout

    val u_rod3 = Module(new NV_NVDLA_IS_pipe(conf.AM_DW))  
    u_rod3.io.clk    := io.nvdla_core_clk
    rod3_wr_prdy     := u_rod3.io.ro
    u_rod3.io.vi     := rod3_wr_pvld
    u_rod3.io.di     := io.rod3_wr_pd
    u_rod3.io.ri     := rod3_rd_prdy
    val rod3_rd_pvld  = u_rod3.io.vo
    val rod3_rd_pd    = u_rod3.io.dout


//=======================================================
// DATA FIFO: READ SIDE
//=======================================================

    val is_last_h = Wire(Bool())
    val is_last_w = Wire(Bool())
    val rodx_rd_en = Wire(Bool())
    when(io.cfg_mode_per_element){
        rodx_rd_en := true.B
    }.otherwise{
        rodx_rd_en := is_last_h & is_last_w
    }  
    val out_rdy = Wire(Bool())
    val rod0_sel = Wire(Bool())
    val rod1_sel = Wire(Bool())
    val rod2_sel = Wire(Bool())
    val rod3_sel = Wire(Bool())

    rod0_rd_prdy := out_rdy & rodx_rd_en & rod0_sel & !(rod1_sel & !rod1_rd_pvld)
    rod1_rd_prdy := out_rdy & rodx_rd_en & rod1_sel & !(rod0_sel & !rod0_rd_pvld)
    rod2_rd_prdy := out_rdy & rodx_rd_en & rod2_sel & !(rod3_sel & !rod3_rd_pvld)
    rod3_rd_prdy := out_rdy & rodx_rd_en & rod3_sel & !(rod2_sel & !rod2_rd_pvld)

//==============
// CMD FIFO
//==============
    
    val roc_rd_prdy = Wire(Bool())
    val u_roc = Module(new NV_NVDLA_SDP_RDMA_EG_RO_cfifo)
    u_roc.io.nvdla_core_clk     := io.nvdla_core_clk
    u_roc.io.pwrbus_ram_pd      := io.pwrbus_ram_pd
    io.roc_wr_rdy               := u_roc.io.roc_wr_prdy
    u_roc.io.roc_wr_pvld        := io.roc_wr_vld
    u_roc.io.roc_wr_pd          := io.roc_wr_pd
    u_roc.io.roc_rd_prdy        := roc_rd_prdy
    val roc_rd_pvld             = u_roc.io.roc_rd_pvld
    val roc_rd_pd               = u_roc.io.roc_rd_pd

    val is_last_beat = Wire(Bool())
    val is_surf_end = Wire(Bool())
    val roc_rd_en = is_last_beat & (is_surf_end | io.cfg_mode_per_element)
    val out_accept = Wire(Bool())
    roc_rd_prdy := roc_rd_en & out_accept
    val size_of_beat = Mux(roc_rd_pvld, (roc_rd_pd + 1.U), 0.U(3.W))

//==============
// END
//==============

    val is_line_end = is_last_w
    is_surf_end := is_line_end & is_last_h
    val is_last_c = Wire(Bool())
    val is_cube_end = is_surf_end & is_last_c

// #ifdef NVDLA_BATCH_ENABLE
//==============
//Batch Count
//==============
// ...

//==============
// Width Count
//==============

    val count_w = RegInit(0.U(13.W))
    when(out_accept){
        when(is_line_end){
            count_w := 0.U
        }.otherwise{
            count_w := count_w + 1.U
        }
    }
    is_last_w := (count_w === io.reg2dp_width)

//==============
// HEIGHT Count
//==============

    val count_h = RegInit(0.U(13.W))
    when(out_accept){
        when(is_surf_end){
            count_h := 0.U
        }.elsewhen(is_line_end){
            count_h := count_h + 1.U
        }
    }
    is_last_h := (count_h === io.reg2dp_height)

//==============
// SURF Count
//==============

    val size_of_surf = Mux(io.cfg_dp_8, 
                            Cat(false.B, io.reg2dp_channel(12, conf.AM_AW)),
                            io.reg2dp_channel(12, conf.AM_AW2))
    val count_c = RegInit(0.U((14-conf.AM_AW).W))   
    when(out_accept){
        when(is_cube_end){
            count_c := 0.U
        }.elsewhen(is_surf_end){
            count_c := count_c + 1.U
        }
    }
    is_last_c := (count_c === size_of_surf)

//==============
// BEAT CNT: used to foreach 1~4 16E rod FIFOs
//==============

    val size_of_elem = Mux((io.cfg_dp_size_1byte | !io.cfg_dp_8), 
                            1.U(2.W), 
                            2.U(2.W))
    val beat_cnt = RegInit(0.U(2.W))                      
    val beat_cnt_nxt = beat_cnt + size_of_elem

    when(out_accept){
        when(io.cfg_mode_per_element){
            when(is_last_beat){
                beat_cnt := 0.U
            }.otherwise{
                beat_cnt := beat_cnt_nxt
            }
        }.elsewhen(is_surf_end){
            when(is_last_beat){
                beat_cnt := 0.U
            }.otherwise{
                beat_cnt := beat_cnt_nxt
            }
        }
    }

    is_last_beat := (beat_cnt_nxt === size_of_beat)
    val rod_sel = beat_cnt
    rod0_sel := (beat_cnt === 0.U(2.W))
    rod1_sel := Mux((io.cfg_dp_size_1byte | !io.cfg_dp_8), 
                    (beat_cnt === 1.U(2.W)), 
                    (beat_cnt === 0.U(2.W))) 
    rod2_sel := (beat_cnt === 2.U(2.W))
    rod3_sel := Mux((io.cfg_dp_size_1byte | !io.cfg_dp_8), 
                    (beat_cnt === 3.U(2.W)), 
                    (beat_cnt === 2.U(2.W))) 


////dp int8 one byte per element or int16 two bytes per elment/////////// 

    val out_data_1bpe = Reg(UInt(conf.AM_DW.W))
    out_data_1bpe := MuxCase(
        Fill(conf.AM_DW, false.B),
        Array(
            (rod_sel === 0.U) -> rod0_rd_pd,
            (rod_sel === 1.U) -> rod1_rd_pd,
            (rod_sel === 2.U) -> rod2_rd_pd,
            (rod_sel === 3.U) -> rod3_rd_pd
        ))

    val out_vld_1bpe = Reg(Bool())
    out_vld_1bpe := MuxCase(
        false.B,
        Array(
            (rod_sel === 0.U) -> rod0_rd_pvld,
            (rod_sel === 1.U) -> rod1_rd_pvld,
            (rod_sel === 2.U) -> rod2_rd_pvld,
            (rod_sel === 3.U) -> rod3_rd_pvld
        ))


////dp int8 two byte per element/////////// 

    val out_data_2bpe = Reg(UInt(conf.AM_DW2.W))
    out_data_2bpe := MuxCase(
        Fill(conf.AM_DW2, false.B),
        Array(
            (rod_sel === 0.U) -> Cat(rod1_rd_pd, rod0_rd_pd),
            (rod_sel === 2.U) -> Cat(rod3_rd_pd, rod2_rd_pd)
        ))

    val out_vld_2bpe = Reg(Bool())
    out_vld_2bpe := MuxCase(
        false.B,
        Array(
            (rod_sel === 0.U) -> (rod0_rd_pvld & rod1_rd_pvld),
            (rod_sel === 2.U) -> (rod2_rd_pvld & rod3_rd_pvld)
        ))

////mux out data ////

    val out_vld = Mux((io.cfg_dp_size_1byte | !io.cfg_dp_8), 
                        out_vld_1bpe, 
                        out_vld_2bpe)
    val out_pd = Cat(is_cube_end, 
                        Mux(!(io.cfg_dp_8),
                            Cat(0.U(conf.AM_DW.W), out_data_1bpe),
                            Mux(io.cfg_dp_size_1byte, 
                                out_data_1bpe, //source: out_data_1bpe_ext (ut_data_1bpe_ext[16*${i}+15:16*${i}] = {{8{out_data_1bpe[8*${i}+7]}}, out_data_1bpe[8*${i}+7:8*${i}]})
                                out_data_2bpe)
                            )
                        )
    
    out_accept := out_vld & out_rdy

    val pipe_p1 = Module{new NV_NVDLA_BC_pipe(conf.AM_DW2+1)}
    pipe_p1.io.clk := io.nvdla_core_clk
    pipe_p1.io.vi := out_vld
    out_rdy := pipe_p1.io.ro
    pipe_p1.io.di := out_pd
    io.sdp_rdma2dp_valid := pipe_p1.io.vo
    pipe_p1.io.ri := io.sdp_rdma2dp_ready
    io.sdp_rdma2dp_pd := pipe_p1.io.dout

    io.layer_end := io.sdp_rdma2dp_valid & io.sdp_rdma2dp_ready & io.sdp_rdma2dp_pd(conf.AM_DW2)
}
}

class NV_NVDLA_SDP_RDMA_EG_RO_cfifo extends Module {
    val io = IO(new Bundle{
        val nvdla_core_clk = Input(Clock())
        val roc_wr_prdy = Output(Bool())
        val roc_wr_pvld = Input(Bool())
        val roc_wr_pd = Input(UInt(2.W))
        val roc_rd_prdy = Input(Bool())
        val roc_rd_pvld = Output(Bool())
        val roc_rd_pd = Output(UInt(2.W))
        val pwrbus_ram_pd = Input(UInt(32.W))
    })
    withClock(io.nvdla_core_clk){
// Master Clock Gating (SLCG)
//
// We gate the clock(s) when idle or stalled.
// This allows us to turn off numerous miscellaneous flops
// that don't get gated during synthesis for one reason or another.
//
// We gate write side and read side separately. 
// If the fifo is synchronous, we also gate the ram separately, but if
// -master_clk_gated_unified or -status_reg/-status_logic_reg is specified, 
// then we use one clk gate for write, ram, and read.
//

    val nvdla_core_clk_mgated_enable = Wire(Bool())
    val nvdla_core_clk_mgate = Module(new NV_CLK_gate_power)
    nvdla_core_clk_mgate.io.clk := io.nvdla_core_clk
    nvdla_core_clk_mgate.io.clk_en := nvdla_core_clk_mgated_enable     // assigned by code at end of this module
    val nvdla_core_clk_mgated = nvdla_core_clk_mgate.io.clk_gated      // used only in synchronous fifos

// 
// WRITE SIDE
//
    val roc_wr_busy_int = withClock(nvdla_core_clk_mgated){RegInit(false.B)}      // copy for internal use

    io.roc_wr_prdy := !roc_wr_busy_int
    val wr_reserving = (io.roc_wr_pvld && !roc_wr_busy_int)     // reserving write space?

    val roc_wr_count = withClock(nvdla_core_clk_mgated){RegInit(0.U(3.W))}
    val wr_popping = Wire(Bool())
    
    val wr_count_next_wr_popping = Mux(wr_reserving, roc_wr_count, (roc_wr_count - 1.U))
    val wr_count_next_no_wr_popping = Mux(wr_reserving, (roc_wr_count + 1.U), roc_wr_count)
    val wr_count_next = Mux(wr_popping, wr_count_next_wr_popping, wr_count_next_no_wr_popping)

    val wr_count_next_no_wr_popping_is_4 = (wr_count_next_no_wr_popping === 4.U)
    val wr_count_next_is_4 = Mux(wr_popping, false.B, wr_count_next_no_wr_popping_is_4)

    val wr_limit_muxed = Wire(UInt(3.W))
    val wr_limit_reg = wr_limit_muxed

    val roc_wr_busy_next = (wr_count_next_is_4 || 
        ((wr_limit_reg =/= 0.U) && (wr_count_next >= wr_limit_reg)))
    
    roc_wr_busy_int := roc_wr_busy_next
    when(wr_reserving ^ wr_popping){
        roc_wr_count := wr_count_next
    }

    val wr_pushing = wr_reserving       // data pushed same cycle as roc_wr_pvld

//
// RAM
//

    val roc_wr_adr = withClock(nvdla_core_clk_mgated){RegInit(0.U(2.W))}
    when(wr_pushing){
        roc_wr_adr := roc_wr_adr + 1.U
    }

    val rd_popping = Wire(Bool())
    val roc_rd_adr = withClock(nvdla_core_clk_mgated){RegInit(0.U(2.W))}    // read address this cycle
    val ram_we = (wr_pushing && ((roc_wr_count > 0.U) || !rd_popping))      // note: write occurs next cycle

    val ram = Module(new NV_NVDLA_SDP_RDMA_EG_RO_cfifo_flopram_rwsa_4x2)
    ram.io.clk := nvdla_core_clk_mgated
    ram.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    ram.io.di := io.roc_wr_pd
    ram.io.we := ram_we
    ram.io.wa := roc_wr_adr
    ram.io.ra := Mux((roc_wr_count === 0.U), 4.U(3.W), Cat(false.B, roc_wr_adr))
    val roc_rd_pd_p = ram.io.dout       // read data out of ram

    val rd_adr_next_popping = roc_wr_adr + 1.U
    when(rd_popping){
        roc_rd_adr := rd_adr_next_popping
    }

    wr_popping := rd_popping        // let it be seen immediately
    val rd_pushing = wr_pushing     // let it be seen immediately

//
// READ SIDE
//

    val roc_rd_prdy_d = RegInit(true.B)     // roc_rd_prdy registered in cleanly
    roc_rd_prdy_d := io.roc_rd_prdy

    val roc_rd_pvld_int_o = withClock(nvdla_core_clk_mgated){RegInit(false.B)}  // internal copy of roc_rd_pvld_o
    val roc_rd_pvld_p = Wire(Bool())        // data out of fifo is valid
    val roc_rd_prdy_d_o = Wire(Bool())      // combinatorial rd_busy

    val roc_rd_pvld_o = roc_rd_pvld_int_o
    rd_popping := roc_rd_pvld_p && !(roc_rd_pvld_int_o && !roc_rd_prdy_d_o)

    val roc_rd_count_p = withClock(nvdla_core_clk_mgated){RegInit(0.U(3.W))}        // read-side fifo count
    val rd_count_p_next_rd_popping = Mux(rd_pushing, roc_rd_count_p, (roc_rd_count_p - true.B))
    val rd_count_p_next_no_rd_popping = Mux(rd_pushing, (roc_rd_count_p + 1.U), roc_rd_count_p)
    val rd_count_p_next = Mux(rd_popping, rd_count_p_next_rd_popping, rd_count_p_next_no_rd_popping)
    roc_rd_pvld_p := (roc_rd_count_p =/= 0.U) || rd_pushing

    when(rd_pushing || rd_popping){
        roc_rd_count_p := rd_count_p_next
    }

// 
// SKID for -rd_busy_reg
//

    val rd_req_next_o = (roc_rd_pvld_p || (roc_rd_pvld_int_o && !roc_rd_prdy_d_o))
    roc_rd_pvld_int_o := rd_req_next_o

    val roc_rd_pd_o = withClock(nvdla_core_clk_mgated){Reg(Bool())}
    roc_rd_pd_o := roc_rd_pd_p

//
// FINAL OUTPUT
//

    io.roc_rd_pd := Mux(!roc_rd_prdy_d_o, roc_rd_pd_o, roc_rd_pd_p)
    val roc_rd_pvld_d = withClock(nvdla_core_clk_mgated){RegInit(false.B)}
    roc_rd_prdy_d_o := !(roc_rd_pvld_d && !roc_rd_prdy_d)
    io.roc_rd_pvld := Mux(!roc_rd_prdy_d_o, roc_rd_pvld_o, roc_rd_pvld_p)   
    roc_rd_pvld_d := io.roc_rd_pvld

//
// Master Clock Gating (SLCG) Enables
//

    nvdla_core_clk_mgated_enable := (
        (wr_reserving || wr_pushing || wr_popping || 
            (io.roc_wr_pvld && !roc_wr_busy_int) || 
            (roc_wr_busy_int =/= roc_wr_busy_next)) || 
        (rd_pushing || rd_popping || 
            (io.roc_rd_pvld && roc_rd_prdy_d) || 
            (roc_rd_pvld_int_o && roc_rd_prdy_d_o)) || 
        (wr_pushing)
        )

    wr_limit_muxed := 0.U
}}

class NV_NVDLA_SDP_RDMA_EG_RO_cfifo_flopram_rwsa_4x2 extends Module{
    val io = IO(new Bundle{
        val clk = Input(Clock())
        val pwrbus_ram_pd = Input(UInt(32.W))
        val di = Input(UInt(2.W))
        val we = Input(Bool())
        val wa = Input(UInt(2.W))
        val ra = Input(UInt(3.W))
        val dout = Output(UInt(2.W))
    })
    withClock(io.clk){

        // //`ifdef EMU
        // val wa0_vmw = io.wa
        // val we0_vmw = io.we
        // val di0_vmw = io.di

        // val emu_ram = Module(new vmw_NV_NVDLA_SDP_RDMA_EG_RO_cfifo_flopram_rwsa_4x2)
        // emu_ram.io.wa0 := wa0_vmw
        // emu_ram.io.we0 := we0_vmw
        // emu_ram.io.di0 := di0_vmw
        // emu_ram.io.ra0 := io.ra(1,0)
        // val dout_p = emu_ram.io.do0

        // io.dout := Mux((ra === 4.U), io.d1, dout_p)
        // //`else

        val ram_ff0 = Reg(UInt(2.W))
        val ram_ff1 = Reg(UInt(2.W))
        val ram_ff2 = Reg(UInt(2.W))
        val ram_ff3 = Reg(UInt(2.W))

        when(io.we){
        when(io.wa === 0.U){
            ram_ff0 := io.di
        }
        when(io.wa === 1.U){
            ram_ff1 := io.di
        }
        when(io.wa === 2.U){
            ram_ff2 := io.di
        }
        when(io.wa === 3.U){
            ram_ff3 := io.di
        }}.otherwise{
            ram_ff0 := io.di
        }

        io.dout := MuxCase(
            0.U(2.W),
            Array(
            (io.ra === 0.U) -> ram_ff0,
            (io.ra === 1.U) -> ram_ff1,
            (io.ra === 2.U) -> ram_ff2,
            (io.ra === 3.U) -> ram_ff3
        ))
    }    
}

// // `ifdef EMU

// class vmw_NV_NVDLA_SDP_RDMA_EG_RO_cfifo_flopram_rwsa_4x2 extends Module{
//     val io = IO(new Bundle{
//         val wa0 = Input(UInt(2.W))
//         val we0 = Input(Bool())
//         val di0 = Input(UInt(2.W))
//         val ra0 = Input(UInt(2.W))
//         val do0 = Output(UInt(2.W))
//     })
//     // // Only visible during Spyglass to avoid blackboxes.
//     // `ifdef SPYGLASS_FLOPRAM
//     // io.do0 := 0.U
//     // ...
//     // `endif

//     // // expand mem for debug ease
//     // `ifdef EMU_EXPAND_FLOPRAM_MEM
    
//     // val mem = SyncReadMem(2, UInt(4.W))
//     // mem.write(io.wa0, io.di0)
//     // io.do0 := mem.read(io.ra0, we0)

//     val mem = Reg(Vec(2, UInt(4.W)))
//     when(we0 === true.B){
//         // #0.1
//         mem(io.wa0) := io.di0
//     }
//     io.do0 := mem(io.ra0)
// }

// //  `endif

object NV_NVDLA_SDP_RDMA_EG_roDriver extends App {
    implicit val conf: sdpConfiguration = new sdpConfiguration
    chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_RDMA_EG_ro())
}

/////////////////////