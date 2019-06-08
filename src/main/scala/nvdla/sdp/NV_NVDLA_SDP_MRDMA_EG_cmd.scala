package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_SDP_MRDMA_EG_cmd extends Module {
   val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        val pwrbus_ram_pd = Input(UInt(32.W))

        val eg_done = Input(Bool())

        val cq2eg_pvld = Input(Bool())
        val cq2eg_prdy = Output(Bool())
        val cq2eg_pd = Input(UInt(14.W))

        val cmd2dat_spt_pvld = Output(Bool())
        val cmd2dat_spt_prdy = Input(Bool())
        val cmd2dat_spt_pd = Output(UInt(13.W))

        val cmd2dat_dma_pvld = Output(Bool())
        val cmd2dat_dma_prdy = Input(Bool())
        val cmd2dat_dma_pd = Output(UInt(15.W))

        val reg2dp_height = Input(UInt(13.W))
        val reg2dp_in_precision = Input(UInt(2.W))
        val reg2dp_proc_precision = Input(UInt(2.W))
        val reg2dp_width = Input(UInt(13.W))


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
    val cfg_di_int16 = io.reg2dp_in_precision === 1.U
    val cfg_do_int8 = io.reg2dp_proc_precision === 0.U
    val cfg_do_int16 = io.reg2dp_proc_precision === 1.U
    val cfg_do_fp16 = io.reg2dp_proc_precision === 2.U
    val cfg_do_16 = cfg_do_int16 | cfg_do_fp16
    val cfg_mode_1x1_pack = (io.reg2dp_width === 0.U) & (io.reg2dp_height === 0.U)

    val ig2eg_size = io.cq2eg_pd(12, 0)
    val ig2eg_cube_end = io.cq2eg_pd(13)

    val cmd_vld = RegInit(false.B)
    val cmd_rdy = Wire(Bool())
    io.cq2eg_prdy := !cmd_vld || cmd_rdy
    when(io.cq2eg_prdy){
        cmd_vld := io.cq2eg_pvld
    }

    val cq2eg_accept = io.cq2eg_pvld & io.cq2eg_prdy

    //dma_size is in unit of atomic_m * 1B
    val ig2eg_spt_size = ig2eg_size
    val cmd_spt_size = RegInit("b0".asUInt(13.W))
    cmd_spt_size := ig2eg_spt_size

    //dma_size is in unit of 16B
    val ig2eg_dma_size = Cat(false.B, ig2eg_size)
    val cmd_dma_size = RegInit("b0".asUInt(14.W))
    when(cq2eg_accept){
        cmd_dma_size := ig2eg_dma_size
    }

    val cmd_cube_end = RegInit(false.B)
    when(cq2eg_accept){
        cmd_cube_end := ig2eg_cube_end
    }

    val dma_req_en = true.B
    val spt_size = cmd_spt_size
    val dma_size = cmd_dma_size
    val dma_cube_end = cmd_cube_end

    //==============
    // OUTPUT PACK and PIPE: To EG_DAT
    //==============
    val spt_fifo_pd = spt_size
    val dma_fifo_pd = Cat(dma_cube_end, dma_size)

    val dma_fifo_prdy = Wire(Bool())
    val spt_fifo_prdy = Wire(Bool())
    val spt_fifo_pvld = cmd_vld & dma_fifo_prdy
    val dma_fifo_pvld = cmd_vld & dma_req_en & spt_fifo_prdy
    cmd_rdy := spt_fifo_prdy & dma_fifo_prdy

    val u_sfifo = Module{new NV_NVDLA_SDP_MRDMA_EG_CMD_sfifo}
    u_sfifo.io.nvdla_core_clk := io.nvdla_core_clk 
    






}}

class NV_NVDLA_SDP_MRDMA_EG_CMD_sfifo extends Module {
   val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        val spt_fifo_prdy = Output(Bool())
        val spt_fifo_pvld = Input(Bool())
        val spt_fifo_pd = Input(UInt(13.W))
        val cmd2dat_spt_prdy = Input(Bool())
        val cmd2dat_spt_pvld = Output(Bool())
        val cmd2dat_spt_pd = Output(UInt(13.W))

        val pwrbus_ram_pd = Input(UInt(32.W))
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
    nvdla_core_clk_mgate.io.clk_en := nvdla_core_clk_mgated_enable
    val nvdla_core_clk_mgated = nvdla_core_clk_mgate.io.clk_gated

    ////////////////////////////////////////////////////////////////////////
    // WRITE SIDE                                                        //
    ////////////////////////////////////////////////////////////////////////
    val wr_reserving = Wire(Bool())
    val spt_fifo_busy_int = withClock(nvdla_core_clk_mgated){RegInit(false.B)}  // copy for internal use
    io.spt_fifo_prdy := !spt_fifo_busy_int
    wr_reserving := io.spt_fifo_pvld && !spt_fifo_busy_int   // reserving write space?

    val wr_popping = Wire(Bool())// fwd: write side sees pop?
    val spt_fifo_count = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt(3.W))} // write-side count
    val wr_count_next_wr_popping = Mux(wr_reserving, spt_fifo_count, spt_fifo_count-1.U)
    val wr_count_next_no_wr_popping = Mux(wr_reserving, spt_fifo_count+1.U, spt_fifo_count)
    val wr_count_next = Mux(wr_popping, wr_count_next_wr_popping, wr_count_next_no_wr_popping)

    val wr_count_next_no_wr_popping_is_4 = (wr_count_next_no_wr_popping === 4.U)
    val wr_count_next_is_4 = Mux(wr_popping, false.B, wr_count_next_no_wr_popping_is_4)
    val wr_limit_muxed = Wire(UInt(3.W))    // muxed with simulation/emulation overrides
    val wr_limit_reg = wr_limit_muxed
    spt_fifo_busy_next := wr_count_next_is_4 ||(wr_limit_reg =/= 0.U && (wr_count_next >= wr_limit_reg))

    spt_fifo_busy_int := spt_fifo_busy_next
    when(wr_reserving ^ wr_popping){
        wr_count := wr_count_next
    }

    val wr_pushing = wr_reserving // data pushed same cycle as wr_req_in

    //
    // RAM
    //  

    val wr_adr = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt(2.W))}
    val wr_adr_next = wr_adr + 1.U
    when(wr_pushing){
        wr_adr := wr_adr_next
    }
    val rd_popping = Wire(Bool())

    val rd_adr = withClock(clk_mgated){RegInit("b0".asUInt(7.W))}   // read address this cycle
    val ram_we = wr_pushing && (wr_count > 0.U || !rd_popping)      // note: write occurs next cycle
    val ram_iwe = !wr_busy_in && io.wr_req
    val rd_data_p = Wire(UInt(11.W))// read data out of ram
    

    // Adding parameter for fifogen to disable wr/rd contention assertion in ramgen.
    // Fifogen handles this by ignoring the data on the ram data out for that cycle.
    val ram = Module(new NV_NVDLA_CDMA_IMG_sg2pack_fifo_flopram_rwsa_128x11())
    ram.io.clk := io.clk
    ram.io.clk_mgated := clk_mgated
    ram.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    ram.io.di := io.wr_data
    ram.io.iwe := ram_iwe
    ram.io.we := ram_we
    ram.io.wa := wr_adr
    ram.io.ra := Mux(wr_count === 0.U, 128.U, Cat(false.B, rd_adr))
    rd_data_p := ram.io.dout
    

    val rd_adr_next_popping = rd_adr + 1.U
    when(rd_popping){
        rd_adr := rd_adr_next_popping
    }

    //
    // SYNCHRONOUS BOUNDARY
    //
    wr_popping := rd_popping    // let it be seen immediately
    val rd_pushing = wr_pushing // let it be seen immediately

    //
    // READ SIDE
    //
    val rd_req_p = Wire(Bool())  // data out of fifo is valid
    val rd_req_int = withClock(clk_mgated){RegInit(false.B)} // internal copy of rd_req
    io.rd_req := rd_req_int
    rd_popping := rd_req_p && !(rd_req_int && !io.rd_ready)

    val rd_count_p = withClock(clk_mgated){RegInit("b0".asUInt(8.W))} //read-side fifo count
    val rd_count_p_next_rd_popping = Mux(rd_pushing, rd_count_p, rd_count_p - 1.U)
    val rd_count_p_next_no_rd_popping = Mux(rd_pushing, rd_count_p + 1.U, rd_count_p)
    val rd_count_p_next = Mux(rd_popping, rd_count_p_next_rd_popping, rd_count_p_next_no_rd_popping)
    rd_req_p := rd_count_p =/= 0.U || rd_pushing;
    when(rd_pushing || rd_popping){
        rd_count_p := rd_count_p_next
    }

    val rd_data_out = withClock(clk_mgated){RegInit("b0".asUInt(11.W))} // output data register
    val rd_req_next = (rd_req_p || (rd_req_int && !io.rd_ready))

    rd_req_int := rd_req_next
    when(rd_popping){
        rd_data_out := rd_data_p
    }

    io.rd_data := rd_data_out

    clk_mgated_enable := ((wr_reserving || wr_pushing || wr_popping || 
                         (wr_req_in && !wr_busy_int) || (wr_busy_int =/= wr_busy_next)) || 
                         (rd_pushing || rd_popping || (rd_req_int && io.rd_ready)) || (wr_pushing))

    wr_limit_muxed := "d0".asUInt(8.W)



}