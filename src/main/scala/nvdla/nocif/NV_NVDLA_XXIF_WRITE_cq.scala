package nvdla

import scala.collection.mutable.ListBuffer
import chisel3._
import chisel3.experimental._
import chisel3.util._

@chiselName
class NV_NVDLA_XXIF_WRITE_cq(vec_num: Int, width: Int)(implicit conf:nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //general clock
        val clk = Input(Clock())      

        //cq_wr
        val cq_wr_pd = Flipped(DecoupledIO(UInt(width.W)))
        val cq_wr_thread_id = Input(UInt(log2Ceil(vec_num).W))
        
        //cq_rd
        val cq_rd_pd = Vec(vec_num, DecoupledIO(UInt(width.W)))

        val pwrbus_ram_pd = Input(UInt(32.W))
    })

withClock(io.clk){
    // -rd_take_to_rd_busy internal credit/take/data signals (which would have been ports)
    val cq_rd_take = Wire(Bool())
    val cq_rd_pd_p = Wire(UInt(width.W))
    val cq_rd_take_thread_id = Wire(UInt(log2Ceil(vec_num).W))

    // We also declare some per-thread flags that indicate whether to have the write bypass the internal fifo.
    // These per-class wr_bypassing* flags are set by the take-side logic.  We basically pretend that we never pushed the fifo,
    // but make sure we return a credit to the sender.
    val wr_bypassing = Wire(Bool()) // any thread bypassed

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
    val clk_mgated_skid_enable = Wire(Bool())
    val clk_rd_mgate_skid = Module(new NV_CLK_gate_power)
    clk_rd_mgate_skid.io.clk := io.clk
    clk_rd_mgate_skid.io.clk_en := clk_mgated_skid_enable
    val clk_mgated_skid = clk_rd_mgate_skid.io.clk_gated

    val clk_mgated_enable = Wire(Bool())
    val clk_mgate = Module(new NV_CLK_gate_power)
    clk_mgate.io.clk := io.clk
    clk_mgate.io.clk_en := clk_mgated_enable
    val clk_mgated = clk_mgate.io.clk_gated

    // 
    // WRITE SIDE
    //
    val wr_reserving = Wire(Bool())
    val wr_popping = Wire(Bool())
    val cq_wr_busy_int = withClock(clk_mgated){RegInit(false.B)}  // copy for internal use
   
    io.cq_wr_pd.ready := ~cq_wr_busy_int
    wr_reserving := io.cq_wr_pd.valid && ~cq_wr_busy_int   // reserving write space?

    val cq_wr_count = withClock(clk_mgated){RegInit("b0".asUInt(9.W))} // write-side count
    val wr_reserving_and_not_bypassing = wr_reserving && ~wr_bypassing

    val wr_count_next_wr_popping = Mux(wr_reserving_and_not_bypassing, cq_wr_count, cq_wr_count-1.U)
    val wr_count_next_no_wr_popping = Mux(wr_reserving_and_not_bypassing, cq_wr_count+1.U, cq_wr_count)
    val wr_count_next = Mux(wr_popping, wr_count_next_wr_popping, wr_count_next_no_wr_popping)

    val wr_count_next_no_wr_popping_is_full = (wr_count_next_no_wr_popping === 256.U)
    val wr_count_next_is_full = Mux(wr_popping, false.B, wr_count_next_no_wr_popping_is_full)

    val wr_limit_muxed = Wire(UInt(9.W))    // muxed with simulation/emulation overrides
    val wr_limit_reg = wr_limit_muxed
    val cq_wr_busy_next = wr_count_next_is_full ||(wr_limit_reg =/= 0.U && (wr_count_next >= wr_limit_reg))

    cq_wr_busy_int := cq_wr_busy_next
    when(wr_reserving_and_not_bypassing ^ wr_popping){
        cq_wr_count := wr_count_next
    }

    val wr_pushing = wr_reserving && ~wr_bypassing // data pushed same cycle as cq_wr_pvld
    val wr_pushing_thread_id = io.cq_wr_thread_id; // thread being written

    //
    // RAM
    //
    val wr_adr_popping = wr_pushing;	// pop free list when wr_pushing=1

    val cq_wr_adr = Wire(UInt(8.W))			// current write address
    val cq_rd_adr = Wire(UInt(8.W))	
    val cq_rd_adr_p = cq_rd_adr;		// read address to use for ram

    val rd_enable = Wire(Bool())

    // Adding parameter for fifogen to disable wr/rd contention assertion in ramgen.
    // Fifogen handles this by ignoring the data on the ram data out for that cycle.

    val ram = Module(new nv_ram_rws(256, width))
    ram.io.clk := io.clk
    ram.io.wa := cq_wr_adr
    ram.io.we := wr_pushing
    ram.io.di := io.cq_wr_pd.bits
    ram.io.ra := cq_rd_adr_p  
    ram.io.re := rd_enable
    cq_rd_pd_p := ram.io.dout

    val rd_popping = Wire(Bool())   // read side doing pop this cycle?

    //
    // SYNCHRONOUS BOUNDARY
    //
    val rd_pushing = wr_pushing;		// let it be seen immediately
    val rd_pushing_thread_id = wr_pushing_thread_id
    val rd_pushing_adr = cq_wr_adr

    //
    // MULTITHREADED FREE LIST FIFO
    //
    // free list of cq_wr_adr's from read side to write side
    // these are passed in a ff fifo when the fifo is popped
    //
    // there's an extra mux of the internal flops that is
    // used to determine which address to use when 
    // rd_pushing is 1 if the fifo is async.  
    //
    val rd_popping_adr = Wire(UInt(8.W))
    val free_adr_index = Wire(UInt(8.W))
    val free_adr_mask_next = Wire(Vec(255, Bool()))
    val free_adr_mask = withClock(clk_mgated){RegInit(VecInit(Seq.fill(255)(true.B)))}

    cq_wr_adr := free_adr_index

    when(rd_popping || wr_adr_popping){
        free_adr_mask := free_adr_mask_next
    }

    for(i <- 0 to 254){
        free_adr_mask_next(i) := free_adr_mask(i)
        when(rd_popping && (rd_popping_adr === i.U)){
            free_adr_mask_next(i) := true.B
        }
        .elsewhen(wr_adr_popping && (free_adr_index === i.U)){
            free_adr_mask_next(i) := false.B
        }
    }

    //generate flag
    val flag_l0 = VecInit((0 to 126) map { i => free_adr_mask(2*i+1)|free_adr_mask(2*i)})
    val flag_l1 = VecInit((0 to 62) map { i => flag_l0(2*i+1)|flag_l0(2*i)})
    val flag_l2 = VecInit((0 to 30) map { i => flag_l1(2*i+1)|flag_l1(2*i)})
    val flag_l3 = VecInit((0 to 14) map { i => flag_l2(2*i+1)|flag_l2(2*i)})
    val flag_l4 = VecInit((0 to 6) map { i => flag_l3(2*i+1)|flag_l3(2*i)})
    val flag_l5 = VecInit((0 to 2) map { i => flag_l4(2*i+1)|flag_l4(2*i)})
    val flag_l6_0 = flag_l5(1) | flag_l5(0)
    //index
    val index_l0 = VecInit((0 to 127) map { i => ~free_adr_mask(2*i)})
    val index_l1 = VecInit((0 to 63) map { i => Cat(~flag_l0(2*i),Mux(flag_l0(2*i), index_l0(2*i), index_l0(2*i+1)))})
    val index_l2 = VecInit((0 to 31) map { i => Cat(~flag_l1(2*i),Mux(flag_l1(2*i), index_l1(2*i), index_l1(2*i+1)))})
    val index_l3 = VecInit((0 to 15) map { i => Cat(~flag_l2(2*i),Mux(flag_l2(2*i), index_l2(2*i), index_l2(2*i+1)))})
    val index_l4 = VecInit((0 to 7) map { i => Cat(~flag_l3(2*i),Mux(flag_l3(2*i), index_l3(2*i), index_l3(2*i+1)))})
    val index_l5 = VecInit((0 to 3) map { i => Cat(~flag_l4(2*i),Mux(flag_l4(2*i), index_l4(2*i), index_l4(2*i+1)))})
    val index_l6 = VecInit((0 to 1) map { i => Cat(~flag_l5(2*i),Mux(flag_l5(2*i), index_l5(2*i), index_l5(2*i+1)))})
    val index_l7_0 = Cat(~flag_l6_0,Mux(flag_l6_0, index_l6(0), index_l6(1)))
    free_adr_index := index_l7_0
    wr_popping := rd_popping 

    //
    // READ SIDE
    //

    //
    // credits for taker are simply rd_pushing*
    //
    val cq_rd_credit = withClock(clk_mgated){RegInit(VecInit(Seq.fill(vec_num)(false.B)))}
    val rd_pushing_q = withClock(clk_mgated){RegInit(false.B)}
    when(rd_pushing || rd_pushing_q){
        for(i <- 0 to vec_num-1){
            cq_rd_credit(i) :=  rd_pushing && (rd_pushing_thread_id === i.U)
        }
        rd_pushing_q := rd_pushing
    }

    val rd_pushing_vec = VecInit((0 to vec_num-1) map { i => (rd_pushing && (rd_pushing_thread_id === i.U))})
    val rd_take = VecInit((0 to vec_num-1) map { i => (cq_rd_take && (cq_rd_take_thread_id === i.U))})

    val head = withClock(clk_mgated){Reg(Vec(vec_num, UInt(8.W)))} // thread head pointer
    val tail = withClock(clk_mgated){Reg(Vec(vec_num, UInt(8.W)))} // thread tail pointer

    val rd_take_n_dly = withClock(clk_mgated){RegInit(VecInit(Seq.fill(vec_num)(false.B)))}
    val rd_take_dly_cg = withClock(clk_mgated){RegInit(false.B)}
    val update_rd_take_n_dly = cq_rd_take || rd_take_dly_cg

    rd_take_dly_cg := cq_rd_take
    when(update_rd_take_n_dly){
        rd_take_n_dly := rd_take
    }

    val adr_ram_wr_adr = Wire(UInt(8.W))
    val adr_ram_wr_data = Wire(UInt(8.W))
    val adr_ram_wr_enable = Wire(Bool())
    val adr_ram_rd_adr = Wire(UInt(8.W))
    val adr_ram_rd_data = Wire(UInt(8.W))
    val adr_ram_rd_enable = Wire(Bool())

    val cq_rd_count = withClock(clk_mgated){RegInit(VecInit(Seq.fill(vec_num)("b0".asUInt(9.W))))}
    val rd_count_next = VecInit((0 to vec_num-1) map { i => Mux(rd_pushing_vec(i), Mux(rd_take(i), cq_rd_count(i), cq_rd_count(i) +& 1.U),
                                                                Mux(rd_take(i), cq_rd_count(i) -& 1.U, cq_rd_count(i)))})
    for(i <- 0 to vec_num-1){
        when(rd_pushing_vec(i)^rd_take(i)){
            cq_rd_count(i) := rd_count_next(i)
        }
    }

    val update_head = withClock(clk_mgated){RegInit(VecInit(Seq.fill(vec_num)(false.B)))}
    val update_head_next = VecInit((0 to vec_num-1) map { i => Mux(rd_take(i) && (cq_rd_count(i) > 1.U), true.B, false.B)})

    when(rd_pushing || cq_rd_take){
        update_head := update_head_next
    }

    for(i <- 0 to vec_num-1){
        when(rd_pushing_vec(i)){
            tail(i) := rd_pushing_adr
        }
        when((rd_pushing_vec(i) && (cq_rd_count(i) === 0.U)) || 
            (rd_pushing_vec(i) && rd_take(i) && (cq_rd_count(i) === 1.U))){
            head(i) := rd_pushing_adr
        }
        .elsewhen(update_head(i)){
            head(i) := adr_ram_rd_data
        }
    }

    val adr_ram = Module(new nv_ram_rws(256, 8))
    adr_ram.io.clk := io.clk
    adr_ram.io.wa := adr_ram_wr_adr
    adr_ram.io.we := adr_ram_wr_enable
    adr_ram.io.di := adr_ram_wr_data
    adr_ram.io.ra := adr_ram_rd_adr
    adr_ram.io.re := adr_ram_rd_enable
    adr_ram_rd_data := adr_ram.io.dout

    adr_ram_wr_data := rd_pushing_adr

    adr_ram_wr_adr := MuxLookup(rd_pushing_thread_id, "b0".asUInt(8.W),
                                (0 to vec_num-1) map{ i=> i.U -> tail(i)})
    adr_ram_wr_enable := MuxLookup(rd_pushing_thread_id, false.B,
                                  (0 to vec_num-1) map{ i=> i.U -> Mux(rd_pushing && (cq_rd_count(i) =/= 0.U), true.B, false.B)})
    adr_ram_rd_enable := MuxLookup(cq_rd_take_thread_id, false.B,
                                  (0 to vec_num-1) map{ i=> i.U -> Mux(cq_rd_take && (cq_rd_count(i) =/= 0.U), true.B, false.B)})
    adr_ram_rd_adr := MuxLookup(cq_rd_take_thread_id, false.B,
                               (0 to vec_num-1) map{ i=> i.U -> Mux(rd_take_n_dly(i)&&update_head(i), adr_ram_rd_data, head(i))})
    cq_rd_adr := MuxLookup(cq_rd_take_thread_id, false.B,
                          (0 to vec_num-1) map{ i=> i.U -> Mux(rd_take_n_dly(i)&&update_head(i), adr_ram_rd_data, head(i))})

    //
    // take data comes out next cycle for non-ff rams.
    //

    val rd_take_dly = withClock(clk_mgated){RegInit(false.B)}
    rd_popping := rd_take_dly

    val rd_adr_dly = withClock(clk_mgated){Reg(UInt(8.W))}
    rd_popping_adr := rd_adr_dly
    rd_enable := cq_rd_take

    rd_take_dly := cq_rd_take
    when(cq_rd_take){
        rd_adr_dly := cq_rd_adr
    }

    //
    // -rd_take_to_rd_busy conversion (conceptually outside the fifo except for ra2 bypass)
    //
    val cq_rd_take_elig = Wire(Vec(vec_num, Bool()))
    val rd_pre_bypassing = Wire(Vec(vec_num, Bool()))    // bypassing is split up into two parts to avoid combinatorial loop
    val rd_bypassing = Wire(Vec(vec_num, Bool()))        // between cq_rd0_pvld and cq_rd0_prdy when doing full bypass

    val rd_skid_0 = withClock(clk_mgated_skid){Reg(Vec(vec_num, UInt(width.W)))}    // head   skid reg
    val rd_skid_1 = withClock(clk_mgated_skid){Reg(Vec(vec_num, UInt(width.W)))}    // head+1   skid reg
    val rd_skid_2 = withClock(clk_mgated_skid){Reg(Vec(vec_num, UInt(width.W)))}    // head+2   skid reg (for -rd_take_reg)

    val rd_skid_0_vld = withClock(clk_mgated_skid){RegInit(VecInit(Seq.fill(vec_num)(false.B)))}    // head   skid reg has valid data
    val rd_skid_1_vld = withClock(clk_mgated_skid){RegInit(VecInit(Seq.fill(vec_num)(false.B)))}    // head+1   skid reg has valid data
    val rd_skid_2_vld = withClock(clk_mgated_skid){RegInit(VecInit(Seq.fill(vec_num)(false.B)))}    // head+2   skid reg has valid data (for -rd_take_reg)
   
    val cq_rd_prdy_d = RegInit(VecInit(Seq.fill(vec_num)(true.B)))
    for(i <- 0 to vec_num-1){
        cq_rd_prdy_d(i) := io.cq_rd_pd(i).ready
        io.cq_rd_pd(i).valid := rd_skid_0_vld(i) || rd_pre_bypassing(i)
        io.cq_rd_pd(i).bits := Mux(rd_skid_0_vld(i), rd_skid_0(i), io.cq_wr_pd.bits)

        when((rd_bypassing(i)||rd_take_n_dly(i)) && (~rd_skid_0_vld(i)||(io.cq_rd_pd(i).valid && io.cq_rd_pd(i).ready && ~rd_skid_1_vld(i)))){
            rd_skid_0(i) := Mux(rd_take_n_dly(i), cq_rd_pd_p, io.cq_wr_pd.bits)
        }
        .elsewhen(io.cq_rd_pd(i).valid && io.cq_rd_pd(i).ready && rd_skid_1_vld(i)){
            rd_skid_0(i) := rd_skid_1(i)
        }

        when((rd_bypassing(i)||rd_take_n_dly(i)) && (~rd_skid_1_vld(i)||(io.cq_rd_pd(i).valid && io.cq_rd_pd(i).ready && ~rd_skid_2_vld(i)))){
            rd_skid_1(i) := Mux(rd_bypassing(i), io.cq_wr_pd.bits, cq_rd_pd_p)
        }
        .elsewhen(io.cq_rd_pd(i).valid && io.cq_rd_pd(i).ready && rd_skid_2_vld(i)){
            rd_skid_1(i) := rd_skid_2(i)
        }

        when((rd_bypassing(i) || rd_take_n_dly(i)) && rd_skid_0_vld(i) && rd_skid_1_vld(i) && (rd_skid_2_vld(i) || ~(io.cq_rd_pd(i).valid && io.cq_rd_pd(i).ready))){
            rd_skid_2(i) := Mux(rd_bypassing(i), io.cq_wr_pd.bits, cq_rd_pd_p)
        }

        rd_skid_0_vld(i) := Mux(io.cq_rd_pd(i).valid && io.cq_rd_pd(i).ready, 
                                (rd_skid_1_vld(i) || (rd_bypassing(i) && rd_skid_0_vld(i)) || rd_take_n_dly(i)),  
                                (rd_skid_0_vld(i) || rd_bypassing(i) || rd_take_n_dly(i)))
        rd_skid_1_vld(i) := Mux(io.cq_rd_pd(i).valid && io.cq_rd_pd(i).ready, 
                                rd_skid_2_vld(i) || (rd_skid_1_vld(i) && (rd_bypassing(i) || rd_take_n_dly(i))),
                                rd_skid_1_vld(i) || (rd_skid_0_vld(i) && (rd_bypassing(i) || rd_take_n_dly(i))))
        rd_skid_2_vld(i) := Mux(io.cq_rd_pd(i).valid && io.cq_rd_pd(i).ready, 
                                rd_skid_2_vld(i) && (rd_bypassing(i) || rd_take_n_dly(i)),
                                rd_skid_2_vld(i) || (rd_skid_1_vld(i) && (rd_bypassing(i) || rd_take_n_dly(i))))
    }

    val cq_rd_credits = withClock(clk_mgated_skid){RegInit(VecInit(Seq.fill(vec_num)("b0".asUInt(9.W))))}
    val cq_rd_credits_ne0 = withClock(clk_mgated_skid){RegInit(VecInit(Seq.fill(vec_num)(false.B)))}

    val cq_rd_credits_w_take_next = Wire(Vec(vec_num, UInt(9.W)))
    val cq_rd_credits_wo_take_next = Wire(Vec(vec_num, UInt(9.W)))
    val cq_rd_credits_next = Wire(Vec(vec_num, UInt(9.W)))

    for(i <- 0 to vec_num-1){
        cq_rd_credits_w_take_next(i) := cq_rd_credits(i) +& cq_rd_credit(i) -& 1.U
        cq_rd_credits_wo_take_next(i) := cq_rd_credits(i) +& cq_rd_credit(i)
        cq_rd_credits_next(i) := Mux(rd_take(i), cq_rd_credits_w_take_next(i), cq_rd_credits_wo_take_next(i))

        cq_rd_take_elig(i) := (cq_rd_prdy_d(i) || ~rd_skid_0_vld(i) || ~rd_skid_1_vld(i) || (~rd_skid_2_vld(i) && ~rd_take_n_dly(i))) && (cq_rd_credit(i) || cq_rd_credits_ne0(i))

        rd_pre_bypassing(i) := io.cq_wr_pd.valid && ~cq_wr_busy_int && (io.cq_wr_thread_id === i.U) && cq_rd_credits(i) === 0.U && ~cq_rd_credit(i) && (~rd_take_n_dly(i) || rd_skid_0_vld(i)); // split this up to avoid combinatorial loop when full bypass is in effect
        rd_bypassing(i) := rd_pre_bypassing(i) && (~rd_skid_2_vld(i) || ~rd_skid_1_vld(i) || ~(~cq_rd_prdy_d(i) && rd_skid_0_vld(i) && rd_skid_1_vld(i))) && ~rd_take_n_dly(i)

        when(cq_rd_credit(i) | rd_take(i)){
            cq_rd_credits(i) := cq_rd_credits_next(i)
            cq_rd_credits_ne0(i) := Mux(rd_take(i), cq_rd_credits_w_take_next(i) =/= 0.U, cq_rd_credits_wo_take_next(i) =/= 0.U)
        }
    }

    // rd_take round-robin arbiter (similar to arbgen output)
    //
    cq_rd_take := cq_rd_take_elig.asUInt.orR    // any thread is eligible to take, so issue take

    val cq_rd_take_thread_id_last = withClock(clk_mgated_skid){RegInit("b0".asUInt(log2Ceil(vec_num).W))}

    val cq_rd_take_thread_id_is = Wire(Vec(vec_num, Vec(vec_num, Bool())))

    for(i <- 0 to vec_num-1){
        cq_rd_take_thread_id_is(0)(i) := 0.U
    }
    
    for(i <- 1 to vec_num-1){
        var std_num = 0
        //find the standard number
        for(j <- 0 to vec_num-1){
            if(i == (j+1)%vec_num){
                std_num = j
                cq_rd_take_thread_id_is(i)(j) := 
                cq_rd_take_elig(i) && (cq_rd_take_thread_id_last === j.U)
            }
        } 

        cq_rd_take_thread_id_is(i)((std_num+vec_num-1)%vec_num) := 
        cq_rd_take_elig(i) && (cq_rd_take_thread_id_last === ((std_num+vec_num-1)%vec_num).U) && ~cq_rd_take_elig(std_num)

        if(vec_num > 2){
            for(j <- 0 to vec_num-3){
                cq_rd_take_thread_id_is(i)((std_num+vec_num-2-j)%vec_num) := 
                cq_rd_take_elig(i) && (cq_rd_take_thread_id_last === ((std_num+vec_num-2-j)%vec_num).U) &&
                ~cq_rd_take_elig(std_num) &&
                (VecInit((0 to j) map{k => ~cq_rd_take_elig((std_num+vec_num-1-k)%vec_num)}).asUInt.andR)                                                                                             
            }  
        }
    }

    val cq_rd_take_thread_id_vec = Wire(Vec(log2Ceil(vec_num), Bool()))
    for(i <- 0 to log2Ceil(vec_num)-1){
        var cq_rd_take_thread_id_list = List.fill(vec_num)(0.U).toBuffer // new ListBuffer[UInt]()
        for(j <- 1 to vec_num-1){
               if((j & (1 << i)) != 0){
                   cq_rd_take_thread_id_list(j) = cq_rd_take_thread_id_is(j).asUInt
               }
        }

        cq_rd_take_thread_id_vec(i) := Cat(cq_rd_take_thread_id_list.toList).orR
    }

    cq_rd_take_thread_id := cq_rd_take_thread_id_vec.asUInt
    when(cq_rd_take){
        cq_rd_take_thread_id_last := cq_rd_take_thread_id
    }

    wr_bypassing := rd_bypassing.asUInt.orR

    clk_mgated_enable := ((wr_reserving || wr_pushing || wr_popping || (io.cq_wr_pd.valid && ~cq_wr_busy_int) || (cq_wr_busy_int =/= cq_wr_busy_next) || rd_popping) || (rd_pushing || cq_rd_take || (cq_rd_credit.asUInt =/= 0.U) || rd_take_dly))

    clk_mgated_skid_enable := clk_mgated_enable || 
                              VecInit((0 to vec_num-1) map {i => (io.cq_rd_pd(i).valid && io.cq_rd_pd(i).ready)||rd_bypassing(i)}).asUInt.orR

    wr_limit_muxed := 0.U
}}
 

 object NV_NVDLA_XXIF_WRITE_cqDriver extends App {
    implicit val conf: nvdlaConfig = new nvdlaConfig
    chisel3.Driver.execute(args, () => new NV_NVDLA_XXIF_WRITE_cq(5, 3))
}


