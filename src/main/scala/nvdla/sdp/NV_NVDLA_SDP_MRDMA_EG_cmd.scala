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
    u_sfifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    u_sfifo.io.spt_fifo_pvld := spt_fifo_pvld
    spt_fifo_prdy := u_sfifo.io.spt_fifo_prdy
    u_sfifo.io.spt_fifo_pd := spt_fifo_pd

    io.cmd2dat_spt_pvld := u_sfifo.io.cmd2dat_spt_pvld
    u_sfifo.io.cmd2dat_spt_prdy := io.cmd2dat_spt_prdy
    io.cmd2dat_spt_pd := u_sfifo.io.cmd2dat_spt_pd
    
    val u_dfifo = Module{new NV_NVDLA_SDP_MRDMA_EG_CMD_dfifo}

    u_dfifo.io.nvdla_core_clk := io.nvdla_core_clk 
    u_dfifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    u_dfifo.io.dma_fifo_pvld := dma_fifo_pvld
    dma_fifo_prdy := u_dfifo.io.dma_fifo_prdy
    u_dfifo.io.dma_fifo_pd := dma_fifo_pd

    io.cmd2dat_dma_pvld := u_dfifo.io.cmd2dat_dma_pvld
    u_dfifo.io.cmd2dat_dma_prdy := io.cmd2dat_dma_prdy
    io.cmd2dat_dma_pd := u_dfifo.io.cmd2dat_dma_pd
    


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
    val spt_fifo_busy_next = wr_count_next_is_4 ||(wr_limit_reg =/= 0.U && (wr_count_next >= wr_limit_reg))

    spt_fifo_busy_int := spt_fifo_busy_next
    when(wr_reserving ^ wr_popping){
        spt_fifo_count := wr_count_next
    }

    val wr_pushing = wr_reserving // data pushed same cycle as wr_req_in

    //
    // RAM
    //  

    val spt_fifo_adr = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt(2.W))}
    val spt_fifo_adr_next = spt_fifo_adr + 1.U
    when(wr_pushing){
        spt_fifo_adr := spt_fifo_adr_next
    }
    val rd_popping = Wire(Bool())

    val cmd2dat_spt_adr = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt(2.W))}   // read address this cycle
    val ram_we = wr_pushing && (spt_fifo_count > 0.U || !rd_popping)      // note: write occurs next cycle
    

    // Adding parameter for fifogen to disable wr/rd contention assertion in ramgen.
    // Fifogen handles this by ignoring the data on the ram data out for that cycle.
    val ram = Module(new NV_NVDLA_SDP_MRDMA_EG_CMD_sfifo_flopram_rwsa_4x13())
    ram.io.clk := nvdla_core_clk_mgated
    ram.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    ram.io.di := io.spt_fifo_pd
    ram.io.we := ram_we
    ram.io.wa := spt_fifo_adr
    ram.io.ra := Mux(spt_fifo_count === 0.U, 4.U, Cat(false.B, cmd2dat_spt_adr))
    io.cmd2dat_spt_pd := ram.io.dout
    

    val rd_adr_next_popping = cmd2dat_spt_adr + 1.U
    when(rd_popping){
        cmd2dat_spt_adr := rd_adr_next_popping
    }

    //
    // SYNCHRONOUS BOUNDARY
    //
    wr_popping := rd_popping    // let it be seen immediately
    val rd_pushing = wr_pushing // let it be seen immediately

    //
    // READ SIDE
    //
    rd_popping := io.cmd2dat_spt_pvld && io.cmd2dat_spt_prdy

    val cmd2dat_spt_count = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt(3.W))} //read-side fifo count
    val rd_count_next_rd_popping = Mux(rd_pushing, cmd2dat_spt_count, cmd2dat_spt_count - 1.U)
    val rd_count_next_no_rd_popping = Mux(rd_pushing, cmd2dat_spt_count + 1.U, cmd2dat_spt_count)
    val rd_count_next = Mux(rd_popping, rd_count_next_rd_popping, rd_count_next_no_rd_popping)
    io.cmd2dat_spt_pvld := cmd2dat_spt_count =/= 0.U || rd_pushing;
    when(rd_pushing || rd_popping){
        cmd2dat_spt_count := rd_count_next
    }

    nvdla_core_clk_mgated_enable := ((wr_reserving || wr_pushing || wr_popping || 
                                    (io.spt_fifo_pvld && !spt_fifo_busy_int) || (spt_fifo_busy_int =/= spt_fifo_busy_next)) || 
                                    (rd_pushing || rd_popping || (io.cmd2dat_spt_pvld && io.cmd2dat_spt_prdy)) || (wr_pushing))

    wr_limit_muxed := "d0".asUInt(3.W)

}}

// 
// Flop-Based RAM 
//

class NV_NVDLA_SDP_MRDMA_EG_CMD_sfifo_flopram_rwsa_4x13 extends Module{
  val io = IO(new Bundle{
        val clk = Input(Clock())    // write clock

        val di = Input(UInt(13.W))
        val we = Input(Bool())
        val wa = Input(UInt(2.W))
        val ra = Input(UInt(3.W))
        val dout = Output(UInt(13.W))

        val pwrbus_ram_pd = Input(UInt(32.W))

  })  
withClock(io.clk){
    val ram_ff = Seq.fill(4)(Reg(UInt(13.W))) :+ Wire(UInt(13.W))
    when(io.we){
        for(i <- 0 to 3){
            when(io.wa === i.U){
                ram_ff(i) := io.di
            }
        } 
    }   
    ram_ff(4) := io.di
    io.dout := MuxLookup(io.ra, "b0".asUInt(13.W), 
        (0 to 4) map { i => i.U -> ram_ff(i)} )
}}


class NV_NVDLA_SDP_MRDMA_EG_CMD_dfifo extends Module {
   val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        val dma_fifo_prdy = Output(Bool())
        val dma_fifo_pvld = Input(Bool())
        val dma_fifo_pd = Input(UInt(15.W))
        val cmd2dat_dma_prdy = Input(Bool())
        val cmd2dat_dma_pvld = Output(Bool())
        val cmd2dat_dma_pd = Output(UInt(15.W))

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
    val dma_fifo_busy_int = withClock(nvdla_core_clk_mgated){RegInit(false.B)}  // copy for internal use
    io.dma_fifo_prdy := !dma_fifo_busy_int
    wr_reserving := io.dma_fifo_pvld && !dma_fifo_busy_int   // reserving write space?

    val wr_popping = Wire(Bool())// fwd: write side sees pop?
    val dma_fifo_count = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt(3.W))} // write-side count
    val wr_count_next_wr_popping = Mux(wr_reserving, dma_fifo_count, dma_fifo_count-1.U)
    val wr_count_next_no_wr_popping = Mux(wr_reserving, dma_fifo_count+1.U, dma_fifo_count)
    val wr_count_next = Mux(wr_popping, wr_count_next_wr_popping, wr_count_next_no_wr_popping)

    val wr_count_next_no_wr_popping_is_4 = (wr_count_next_no_wr_popping === 4.U)
    val wr_count_next_is_4 = Mux(wr_popping, false.B, wr_count_next_no_wr_popping_is_4)
    val wr_limit_muxed = Wire(UInt(3.W))    // muxed with simulation/emulation overrides
    val wr_limit_reg = wr_limit_muxed
    val dma_fifo_busy_next = wr_count_next_is_4 ||(wr_limit_reg =/= 0.U && (wr_count_next >= wr_limit_reg))

    dma_fifo_busy_int := dma_fifo_busy_next
    when(wr_reserving ^ wr_popping){
        dma_fifo_count := wr_count_next
    }

    val wr_pushing = wr_reserving // data pushed same cycle as wr_req_in

    //
    // RAM
    //  

    val dma_fifo_adr = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt(2.W))}
    val dma_fifo_adr_next = dma_fifo_adr + 1.U
    when(wr_pushing){
        dma_fifo_adr := dma_fifo_adr_next
    }
    val rd_popping = Wire(Bool())

    val cmd2dat_dma_adr = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt(2.W))}   // read address this cycle
    val ram_we = wr_pushing && (dma_fifo_count > 0.U || !rd_popping)      // note: write occurs next cycle
    

    // Adding parameter for fifogen to disable wr/rd contention assertion in ramgen.
    // Fifogen handles this by ignoring the data on the ram data out for that cycle.
    val ram = Module(new NV_NVDLA_SDP_MRDMA_EG_CMD_dfifo_flopram_rwsa_4x15())
    ram.io.clk := nvdla_core_clk_mgated
    ram.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    ram.io.di := io.dma_fifo_pd
    ram.io.we := ram_we
    ram.io.wa := dma_fifo_adr
    ram.io.ra := Mux(dma_fifo_count === 0.U, 4.U, Cat(false.B, cmd2dat_dma_adr))
    io.cmd2dat_dma_pd := ram.io.dout
    

    val rd_adr_next_popping = cmd2dat_dma_adr + 1.U
    when(rd_popping){
        cmd2dat_dma_adr := rd_adr_next_popping
    }

    //
    // SYNCHRONOUS BOUNDARY
    //
    wr_popping := rd_popping    // let it be seen immediately
    val rd_pushing = wr_pushing // let it be seen immediately

    //
    // READ SIDE
    //
    rd_popping := io.cmd2dat_dma_pvld && io.cmd2dat_dma_prdy

    val cmd2dat_dma_count = withClock(nvdla_core_clk_mgated){RegInit("b0".asUInt(3.W))} //read-side fifo count
    val rd_count_next_rd_popping = Mux(rd_pushing, cmd2dat_dma_count, cmd2dat_dma_count - 1.U)
    val rd_count_next_no_rd_popping = Mux(rd_pushing, cmd2dat_dma_count + 1.U, cmd2dat_dma_count)
    val rd_count_next = Mux(rd_popping, rd_count_next_rd_popping, rd_count_next_no_rd_popping)
    io.cmd2dat_dma_pvld := cmd2dat_dma_count =/= 0.U || rd_pushing;
    when(rd_pushing || rd_popping){
        cmd2dat_dma_count := rd_count_next
    }

    nvdla_core_clk_mgated_enable := ((wr_reserving || wr_pushing || wr_popping || 
                                    (io.dma_fifo_pvld && !dma_fifo_busy_int) || (dma_fifo_busy_int =/= dma_fifo_busy_next)) || 
                                    (rd_pushing || rd_popping || (io.cmd2dat_dma_pvld && io.cmd2dat_dma_prdy)) || (wr_pushing))

    wr_limit_muxed := "d0".asUInt(3.W)

}}

// 
// Flop-Based RAM 
//

class NV_NVDLA_SDP_MRDMA_EG_CMD_dfifo_flopram_rwsa_4x15 extends Module{
  val io = IO(new Bundle{
        val clk = Input(Clock())    // write clock

        val di = Input(UInt(15.W))
        val we = Input(Bool())
        val wa = Input(UInt(2.W))
        val ra = Input(UInt(3.W))
        val dout = Output(UInt(15.W))

        val pwrbus_ram_pd = Input(UInt(32.W))

  })  
withClock(io.clk){
    val ram_ff = Seq.fill(4)(Reg(UInt(15.W))) :+ Wire(UInt(15.W))
    when(io.we){
        for(i <- 0 to 3){
            when(io.wa === i.U){
                ram_ff(i) := io.di
            }
        } 
    }   
    ram_ff(4) := io.di
    io.dout := MuxLookup(io.ra, "b0".asUInt(15.W), 
        (0 to 4) map { i => i.U -> ram_ff(i)} )
}}


object NV_NVDLA_SDP_MRDMA_EG_cmdDriver extends App {
  chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_MRDMA_EG_cmd)
}