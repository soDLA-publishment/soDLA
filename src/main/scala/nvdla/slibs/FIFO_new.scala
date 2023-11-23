package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

@chiselName
class NV_NVDLA_fifo_new(depth: Int, width: Int,
                    ram_type: Int = 0, 
                    ram_bypass: Boolean = false, 
                    wr_reg: Boolean = false,
                    rd_reg: Boolean = false,
                    rd_skid: Boolean = false,
                    io_wr_empty: Boolean = false, 
                    io_wr_idle: Boolean = false,
                    io_wr_count: Boolean = false,
                    io_rd_idle: Boolean = false,
                    useRealClock: Boolean = false)(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //clk
        val clk = Input(Clock())

        val wr_pvld = Input(Bool())
        val wr_prdy = Output(Bool())
        val wr_pd = Input(UInt(width.W))

        val wr_count =  if(io_wr_count) Some(Output(UInt(log2Ceil(depth+1).W))) else None
        val wr_empty = if(io_wr_empty) Some(Output(Bool())) else None
        val wr_idle = if(io_wr_idle) Some(Output(Bool())) else None
        
        val rd_pvld = Output(Bool())
        val rd_prdy = Input(Bool())  
        val rd_pd = Output(UInt(width.W))

        val rd_idle = if(io_rd_idle) Some(Output(Bool())) else None

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
    val clk_mgated_enable = Wire(Bool())
    val clk_mgate = Module(new NV_CLK_gate_power)
    clk_mgate.io.clk := io.clk
    clk_mgate.io.clk_en := clk_mgated_enable
    val clk_mgated = if (useRealClock) clk_mgate.io.clk_gated else clock

withClock(if (useRealClock) io.clk else clock){
  
    if(depth == 0){
        // 
        // WRITE SIDE
        //  
        //          
        // NOTE: 0-depth fifo has no write side
        //          

        //
        // RAM
        //
        //
        // NOTE: 0-depth fifo has no ram.
        //

        val rd_pd_p = io.wr_pd

        //
        // SYNCHRONOUS BOUNDARY
        //

        //
        // NOTE: 0-depth fifo has no real boundary between write and read sides
        //
        val rd_prdy_d = RegInit(true.B)  // rd_prdy registered in cleanly

        rd_prdy_d := io.rd_prdy

        val rd_prdy_d_o = Wire(Bool())            // combinatorial rd_busy

        val rd_pvld_int = RegInit(false.B)        // internal copy of rd_pvld----------
        io.rd_pvld := rd_pvld_int
        val rd_pvld_p = io.wr_pvld      // no real fifo, take from write-side input
        val rd_pvld_int_o = withClock(clk_mgated){RegInit(false.B)}    // internal copy of rd_pvld_o
        val rd_pvld_o = rd_pvld_int_o
        val rd_popping = rd_pvld_p && ~(rd_pvld_int_o && ~rd_prdy_d_o);

        // 
        // SKID for -rd_busy_reg
        //
        val rd_pd_o = if(conf.REGINIT_DATA) withClock(clk_mgated){RegInit("b0".asUInt(width.W))} else withClock(clk_mgated){Reg(UInt(width.W))}// output data register 
        val rd_pvld_next_o = (rd_pvld_p || (rd_pvld_int_o && ~rd_prdy_d_o))

        rd_pvld_int_o := rd_pvld_next_o
        when(rd_pvld_int && rd_pvld_next_o && rd_popping){
            rd_pd_o := rd_pd_p
        }
        

        //
        // FINAL OUTPUT
        //
        val rd_pd_out = if(conf.REGINIT_DATA) RegInit("b0".asUInt(width.W)) else Reg(UInt(width.W))  // output data register
        val rd_pvld_int_d = RegInit(false.B)    // so we can bubble-collapse rd_prdy_d
        rd_prdy_d_o := ~((rd_pvld_o && rd_pvld_int_d && ~rd_prdy_d))
        val rd_pvld_next = Mux(~rd_prdy_d_o,  rd_pvld_o, rd_pvld_p)  

        when(~rd_pvld_int || io.rd_prdy ){
            rd_pvld_int := rd_pvld_next
        }
        rd_pvld_int_d := rd_pvld_int

        when(rd_pvld_next && (~rd_pvld_int || io.rd_prdy)){
            rd_pd_out := MuxLookup(~rd_prdy_d_o, Fill(width, false.B),
                    Array(
                    0.U -> rd_pd_p,
                    1.U -> rd_pd_o
                    ))
        }
        io.rd_pd := rd_pd_out
        clk_mgated_enable := (false.B || (io.wr_pvld || 
                            (rd_pvld_int && rd_prdy_d) || 
                            (rd_pvld_int_o && rd_prdy_d_o)))  
        io.wr_prdy := false.B  
    }

    else{

        ////////////////////////////////////////////////////////////////////////
        // WRITE SIDE                                                        //
        ////////////////////////////////////////////////////////////////////////
        val wr_reserving = Wire(Bool())
        val wr_busy_int = withClock(clk_mgated){RegInit(false.B)}  // copy for internal use

        val wr_pvld_in = if(wr_reg) RegInit(false.B) else io.wr_pvld    // registered wr_pvld

        val wr_pd_in = if(wr_reg & (ram_type == 2) & conf.REGINIT_DATA) RegInit("b0".asUInt(width.W)) 
                       else if(wr_reg & (ram_type == 2) & !conf.REGINIT_DATA) Reg(UInt(width.W)) 
                       else io.wr_pd   // registered wr_pd

        val wr_busy_in = if(wr_reg) RegInit(false.B) else wr_busy_int    // inputs being held this cycle?
        val wr_busy_next = Wire(Bool())     // fwd: fifo busy next?

        // factor for better timing with distant wr_pvld signal
        if(wr_reg){
            val wr_busy_in_next_wr_pvld_eq_1 = wr_busy_next
            val wr_busy_in_next_wr_pvld_eq_0 = (wr_pvld_in && wr_busy_next) && ~wr_reserving
            val wr_busy_in_next = Mux(io.wr_pvld, wr_busy_in_next_wr_pvld_eq_1, wr_busy_in_next_wr_pvld_eq_0)
            val wr_busy_in_int = wr_pvld_in && wr_busy_int

            wr_busy_in := wr_busy_in_next
            when(~wr_busy_in_int){
                wr_pvld_in := io.wr_pvld && ~wr_busy_in
            }

            if(ram_type == 2){
                when(~wr_busy_in && io.wr_pvld){
                    wr_pd_in := io.wr_pd
                }
            }
        }

        io.wr_prdy := ~wr_busy_in
        wr_reserving := wr_pvld_in && ~wr_busy_int   // reserving write space?

        val wr_popping = if((ram_type == 0) | (ram_type == 1) | ((ram_type == 2) & (ram_bypass == true))) Wire(Bool()) else withClock(clk_mgated){RegInit(false.B)}      // fwd: write side sees pop?
        val wr_count = withClock(clk_mgated){RegInit("b0".asUInt(log2Ceil(depth+1).W))} // write-side count
        if(io_wr_count){
            io.wr_count.get := wr_count
        }
        val wr_count_next_wr_popping = Mux(wr_reserving, wr_count, wr_count-1.U)
        val wr_count_next_no_wr_popping = Mux(wr_reserving, wr_count+1.U, wr_count)
        val wr_count_next = Mux(wr_popping, wr_count_next_wr_popping, wr_count_next_no_wr_popping)

        val wr_count_next_no_wr_popping_is_full = (wr_count_next_no_wr_popping === depth.U)
        val wr_count_next_is_full = Mux(wr_popping, false.B, wr_count_next_no_wr_popping_is_full)

        val wr_limit_muxed = Wire(UInt(log2Ceil(depth+1).W))    // muxed with simulation/emulation overrides
        val wr_limit_reg = wr_limit_muxed
        wr_busy_next := wr_count_next_is_full ||(wr_limit_reg =/= 0.U && (wr_count_next >= wr_limit_reg))

        wr_busy_int := wr_busy_next
        when(wr_reserving ^ wr_popping){
            wr_count := wr_count_next
        }

        if(io_wr_empty){
            io.wr_empty.get := RegNext(wr_count_next === 0.U && ~io.wr_pvld, true.B)
        }
        val wr_pushing = wr_reserving // data pushed same cycle as wr_pvld_in
        
        ////////////////////////////////////////////////////////////////////////
        // STORAGE TYPE                                                       //
        ////////////////////////////////////////////////////////////////////////
        val wr_adr = if(depth>1) Some(withClock(clk_mgated){RegInit("b0".asUInt(log2Ceil(depth).W))}) else None // current write address
        if(depth>1){
            val wr_adr_next = if((ram_type == 2) & (ram_bypass == true)) Mux(wr_adr.get === (depth-1).U, 0.U, wr_adr.get+1.U)
                              else wr_adr.get + 1.U
            when(wr_pushing){
                    wr_adr.get := wr_adr_next
            }
        }
        val rd_popping = Wire(Bool())
        val rd_adr = if(depth>1) withClock(clk_mgated){RegInit("b0".asUInt(log2Ceil(depth).W))} else 0.U   // read address this cycle
        val rd_adr_next = if((depth>1) & (ram_type == 2) & (ram_bypass == true)) Some(Wire(UInt(log2Ceil(depth).W))) else None
        val rd_adr_next_popping = rd_adr + 1.U
        if(depth>1){
            if((ram_type == 2) & (ram_bypass == true)){
                rd_adr_next.get := Mux(rd_adr === (depth-1).U, 0.U, rd_adr+1.U)
                when(rd_popping){
                    rd_adr := rd_adr_next.get
                }
            }
            else{
                when(rd_popping){
                    rd_adr := rd_adr_next_popping
                }
            }
        }
        val rd_pd_p = Wire(UInt(width.W))
        val ram_we = if(ram_bypass & ((ram_type == 0)|(ram_type ==1))) wr_pushing && (wr_count > 0.U || ~rd_popping) else wr_pushing
        // Adding parameter for fifogen to disable wr/rd contention assertion in ramgen.
        // Fifogen handles this by ignoring the data on the ram data out for that cycle.
        if(ram_type == 0){
            val ram = Module(new nv_flopram(depth, width, false))
            ram.io.clk := clk_mgated
            ram.io.pwrbus_ram_pd := io.pwrbus_ram_pd
            if(depth > 1){
                ram.io.wa.get := wr_adr.get
            }
            ram.io.we := ram_we
            ram.io.di := wr_pd_in
            if(ram_bypass){
                ram.io.ra := Mux(wr_count === 0.U, depth.U, rd_adr)
            }
            else{
                ram.io.ra := rd_adr
            }
            rd_pd_p := ram.io.dout
        }
        if(ram_type == 1){
            val ram_iwe = ~wr_busy_in && io.wr_pvld
        
            val ram = Module(new nv_flopram(depth, width, true))
            ram.io.clk := io.clk
            ram.io.clk_mgated.get := clk_mgated
            ram.io.pwrbus_ram_pd := io.pwrbus_ram_pd
            ram.io.di := wr_pd_in
            ram.io.iwe.get := ram_iwe
            ram.io.we := ram_we
            if(depth > 1){
                ram.io.wa.get := wr_adr.get
            }            
            if(ram_bypass){
                ram.io.ra := Mux(wr_count === 0.U, depth.U, rd_adr)
            }
            else{
                ram.io.ra := rd_adr
            }
            rd_pd_p := ram.io.dout
        }
        val rd_enable = if(ram_type == 2) Some(Wire(Bool())) else None
        val ore = if((ram_type == 2)&(ram_bypass == true)) Some(Wire(Bool())) else None
        val do_bypass = if((ram_type == 2)&(ram_bypass == true)) Some(Wire(Bool())) else None
        val comb_bypass = if((ram_type == 2)&(ram_bypass == true)) Some(Wire(Bool())) else None
        val rd_pd_p_byp_ram = if((ram_type == 2)&(ram_bypass == true)) Some(Wire(UInt(width.W))) else None

        if(ram_type == 2){
            //need two cycles
            if (ram_bypass == true){
                val ram = Module(new nv_ram_rwsthp(depth, width))
                ram.io.clk := io.clk
                ram.io.pwrbus_ram_pd := io.pwrbus_ram_pd
                if(depth > 1){
                    ram.io.wa := wr_adr.get
                }
                ram.io.we := wr_pushing && ((wr_count =/= 0.U) || ~rd_popping)
                ram.io.di := wr_pd_in
                ram.io.ra := Mux(rd_popping, rd_adr_next_popping, rd_adr)   // for ram
                ram.io.re := (do_bypass.get && wr_pushing) || rd_enable.get
                rd_pd_p_byp_ram.get := ram.io.dout
                ram.io.byp_sel := comb_bypass.get
                ram.io.dbyp := wr_pd_in
                ram.io.ore := ore.get

            }
            else{
                val ram = Module(new nv_ram_rwsp(depth, width))
                ram.io.clk := io.clk
                ram.io.pwrbus_ram_pd := io.pwrbus_ram_pd
                if(depth>1){
                    ram.io.wa := wr_adr.get
                }
                ram.io.we := ram_we
                ram.io.di := wr_pd_in
                ram.io.ra := Mux(rd_popping, rd_adr_next_popping, rd_adr)   // for ram
                ram.io.re := rd_enable.get
                ram.io.ore := rd_popping
                rd_pd_p := ram.io.dout
            }
        }


        //
        // SYNCHRONOUS BOUNDARY
        //
        wr_popping := rd_popping    // let it be seen immediately
        val rd_pushing = if((ram_type == 2) & (ram_bypass == false)) withClock(clk_mgated){RegNext(wr_pushing, false.B)} else wr_pushing // let data go into ram first or let it be seen immediately

        ////////////////////////////////////////////////////////////////////////
        // READ SIDE                                                          //
        ////////////////////////////////////////////////////////////////////////

        val rd_pvld_p = if((ram_type == 2)&(ram_bypass == false) | (( ram_type == 0 )&( rd_reg == false))) withClock(clk_mgated){RegInit(false.B)}
                        else Wire(Bool()) // data out of fifo is valid
        val rd_pvld_int = if(rd_reg | (ram_type == 2) | (ram_type == 0)) Some(withClock(clk_mgated){RegInit(false.B)}) 
                          else None // internal copy of rd_req
        val rd_count = withClock(clk_mgated){RegInit("b0".asUInt(log2Ceil(depth+1).W))}

        val rd_count_next_rd_popping = Mux(rd_pushing, rd_count, rd_count-1.U)
        val rd_count_next_no_rd_popping = Mux(rd_pushing, rd_count + 1.U, rd_count)
        val rd_count_next = Mux(rd_popping, rd_count_next_rd_popping, rd_count_next_no_rd_popping)

        when(rd_pushing || rd_popping){
            rd_count := rd_count_next
        }

        if(ram_type == 0){        

            if(rd_reg){ 

                rd_pvld_p := rd_count =/= 0.U|rd_pushing
                rd_popping := rd_pvld_p && ~(rd_pvld_int.get && ~io.rd_prdy)

                val rd_pd_reg = if(conf.REGINIT_DATA) withClock(clk_mgated){RegInit("b0".asUInt(width.W))} else withClock(clk_mgated){Reg(UInt(width.W))} // output data register
                val rd_pvld_next = (rd_pvld_p || (rd_pvld_int.get && ~io.rd_prdy))
                rd_pvld_int.get := rd_pvld_next
                rd_pd_reg := rd_pd_p

                io.rd_pd := rd_pd_reg
                io.rd_pvld := rd_pvld_int.get

            }
            else{
                rd_popping := rd_pvld_int.get && io.rd_prdy

                val rd_count_p_next_rd_popping_not_0 = rd_count_next_rd_popping =/= 0.U
                val rd_count_p_next_no_rd_popping_not_0 = rd_count_next_no_rd_popping =/= 0.U
                val rd_count_p_next_not_0 = Mux(rd_popping, rd_count_p_next_rd_popping_not_0, rd_count_p_next_no_rd_popping_not_0)

                when(rd_pushing || rd_popping){
                    rd_pvld_p := rd_count_p_next_not_0
                    rd_pvld_int.get := rd_count_p_next_not_0
                }

                io.rd_pd := rd_pd_p
                io.rd_pvld := rd_pvld_p

            }      
        }
        if(ram_type == 1){
            rd_pvld_p := rd_count =/= 0.U|rd_pushing

            if(rd_reg){
                rd_popping := rd_pvld_p && (rd_pvld_int.get && ~io.rd_prdy)

                val rd_pd_reg = if(conf.REGINIT_DATA) withClock(clk_mgated){RegInit("b0".asUInt(width.W))} else withClock(clk_mgated){Reg(UInt(width.W))} // output data register
                val rd_pvld_next = (rd_pvld_p || (rd_pvld_int.get && ~io.rd_prdy))
                rd_pvld_int.get := rd_pvld_next
                rd_pd_reg := rd_pd_p

                io.rd_pvld := rd_pvld_p
                io.rd_pd := rd_pd_reg

            }
            else{
                rd_popping := rd_pvld_p && io.rd_prdy


                io.rd_pvld := rd_pvld_p
                io.rd_pd := rd_pd_p

            } 
        }
        if(ram_type == 2){

                rd_popping := rd_pvld_p && ~(rd_pvld_int.get && ~io.rd_prdy)

                val rd_count_p_next_rd_popping_not_0 = rd_count_next_rd_popping =/= 0.U
                val rd_count_p_next_no_rd_popping_not_0 = rd_count_next_no_rd_popping =/= 0.U
                val rd_count_p_next_not_0 = Mux(rd_popping, rd_count_p_next_rd_popping_not_0, rd_count_p_next_no_rd_popping_not_0)

                rd_enable.get := ((rd_count_p_next_not_0) && ((~rd_pvld_p).asBool() | rd_popping)); // anytime data's there and not stalled

                val rd_pvld_next = (rd_pvld_p || (rd_pvld_int.get && ~io.rd_prdy))
                rd_pvld_int.get := rd_pvld_next

                io.rd_pvld := rd_pvld_int.get
                io.rd_pd := rd_pd_p

                if(ram_bypass){

                    ore.get := rd_popping
                    do_bypass.get := Mux(rd_popping, wr_adr.get === rd_adr_next.get, wr_adr.get === rd_adr)
                    val rd_pd_p_byp = rd_pd_p_byp_ram.get
                    //
                    // Combinatorial Bypass
                    //
                    // If we're pushing an empty fifo, mux the wr_data directly.
                    //
                    comb_bypass.get := wr_count === 0.U
                    rd_pd_p := rd_pd_p_byp

                    rd_pvld_p := (rd_count =/= 0.U) || rd_pushing
                }
                else{
                    when(rd_pushing || rd_popping){
                        rd_pvld_p := rd_count_p_next_not_0
                    }
                }
            
        }



        //
        // Read-side Idle Calculation
        //

        if(io_rd_idle){
            io.rd_idle.get := ~io.rd_pvld && ~rd_pushing && rd_count === 0.U
        }
        
        //
        // Write-Side Idle Calculation
        //

        if(io_wr_idle){
            val rd_idle = ~io.rd_pvld && ~rd_pushing && rd_count === 0.U
            io.wr_idle.get := ~wr_pvld_in && rd_idle && ~wr_pushing && wr_count === 0.U
        }

        
        // Master Clock Gating (SLCG) Enables
        //

        clk_mgated_enable := ((wr_reserving || wr_pushing || wr_popping ||
                            (wr_pvld_in && ~wr_busy_int) || (wr_busy_int =/= wr_busy_next)) || 
                            (rd_pushing || rd_popping || (io.rd_pvld && io.rd_prdy)) || 
                            (wr_pushing))

        wr_limit_muxed := "d0".asUInt(log2Ceil(depth+1).W)

    }
}}

// //NV_NVDLA_CDMA_DC_fifo
// object NV_NVDLA_CDMA_DC_fifoDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_fifo(depth = 128, width = 6, wr_reg = true, ram_type = 2))
// }

// //NV_NVDLA_CDMA_IMG_fifo
// object NV_NVDLA_CDMA_IMG_fifoDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_fifo(depth = 128, width = 11, wr_reg = true, ram_type = 2))
// }

// //NV_NVDLA_CDMA_IMG_sg2pack_fifo
// object NV_NVDLA_CDMA_IMG_sg2pack_fifoDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_fifo(depth = 128, width = 11, wr_reg = true, ram_type = 1, ram_bypass = true))
// }

// // NV_NVDLA_8atmm_fifo
// object NV_NVDLA_8atmm_fifoDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_fifo(depth = 8*conf.ATMM/conf.DMAIF, width = conf.NVDLA_CDMA_MEM_RD_RSP, ram_type = 2))
// }

// // NV_NVDLA_intpinfo_sync_fifo
// object NV_NVDLA_intpinfo_sync_fifo extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_fifo(20, conf.NVDLA_CDP_THROUGHPUT*4, ram_type = 2, ram_bypass = true))
// }


object NV_NVDLA_MCIF_READ_eg_fifo extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_fifo_new(depth = 4,width = conf.NVDLA_PRIMARY_MEMIF_WIDTH, ram_type = 0))
}





// object NV_NVDLA_fifoDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_fifo(depth = 20, width = 80, wr_reg = true, ram_type = 2))
// }

