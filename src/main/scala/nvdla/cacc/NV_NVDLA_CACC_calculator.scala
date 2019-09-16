package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

//calculate accumulate data

class NV_NVDLA_CACC_calculator(implicit conf: caccConfiguration) extends Module {

    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())
        val nvdla_cell_clk = Input(Clock())

        //abuf
        val abuf_rd_data = Input(UInt(conf.CACC_ABUF_WIDTH.W))
        val abuf_wr = new nvdla_wr_if(conf.CACC_ABUF_AWIDTH, conf.CACC_ABUF_WIDTH)
    
        //dlv buf
        val dlv_valid = Output(Bool())
        val dlv_mask = Output(Bool()) 
        val dlv_data = Output(Vec(conf.CACC_ATOMK, UInt(conf.CACC_FINAL_WIDTH.W)))
        val dlv_pd = Output(UInt(2.W))  

        //control
        val accu_ctrl_pd = Flipped(ValidIO(UInt(13.W)))
        val accu_ctrl_ram_valid = Input(Bool())

        //cfg
        val cfg_in_en_mask = Input(Bool())
        val cfg_truncate = Input(UInt(5.W))

        //mac2cacc
        val mac_a2accu_data = Input(Vec(conf.CACC_ATOMK/2, UInt(conf.CACC_IN_WIDTH.W)))
        val mac_a2accu_mask = Input(Vec(conf.CACC_ATOMK/2, Bool()))
        val mac_a2accu_pvld = Input(Bool())

        val mac_b2accu_data = Input(Vec(conf.CACC_ATOMK/2, UInt(conf.CACC_IN_WIDTH.W)))
        val mac_b2accu_mask = Input(Vec(conf.CACC_ATOMK/2, Bool()))
        val mac_b2accu_pvld = Input(Bool())

        //reg
        val dp2reg_sat_count = Output(UInt(32.W))

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
    // unpack abuffer read data
    val abuf_in_data = VecInit((0 to conf.CACC_ATOMK-1) 
                        map { i => io.abuf_rd_data(conf.CACC_PARSUM_WIDTH*(i+1)-1, conf.CACC_PARSUM_WIDTH*i)})
    //1T delay, the same T with data/mask
    val accu_ctrl_pd_d1 = RegEnable(io.accu_ctrl_pd.bits, "b0".asUInt(13.W), io.accu_ctrl_pd.valid)
    val calc_valid_in = (io.mac_b2accu_pvld | io.mac_a2accu_pvld)

    val calc_valid = ShiftRegister(calc_valid_in, 3, false.B, true.B)

    // unpack pd form abuffer control
    val calc_addr = accu_ctrl_pd_d1(5, 0)
    val calc_mode = accu_ctrl_pd_d1(8, 6)
    val calc_stripe_end = accu_ctrl_pd_d1(9)
    val calc_channel_end = accu_ctrl_pd_d1(10)
    val calc_layer_end = accu_ctrl_pd_d1(11)
    val calc_dlv_elem_mask = accu_ctrl_pd_d1(12)

    val calc_elem = Wire(Vec(conf.CACC_ATOMK, UInt(conf.CACC_IN_WIDTH.W)))
    val calc_in_mask = Wire(Vec(conf.CACC_ATOMK, Bool()))
    for(i <- 0 to conf.CACC_ATOMK/2-1){
        calc_elem(i) := io.mac_a2accu_data(i)
        calc_elem(i + conf.CACC_ATOMK/2) := io.mac_b2accu_data(i)
        calc_in_mask(i) := io.mac_a2accu_mask(i)
        calc_in_mask(i + conf.CACC_ATOMK/2) := io.mac_b2accu_mask(i)
    }

    val calc_op_en = VecInit((0 to conf.CACC_ATOMK-1) map { i => calc_in_mask(i)&io.cfg_in_en_mask})
    val calc_op1_vld = VecInit((0 to conf.CACC_ATOMK-1) map { i => calc_in_mask(i)&io.cfg_in_en_mask&io.accu_ctrl_ram_valid})
    val calc_dlv_valid = calc_valid & calc_channel_end
    val calc_wr_en = calc_valid & (~calc_channel_end)

    //if CACC_IN_WIDTH is not 22, this can be solved by auto width inference in chisel
    val calc_op0 = calc_elem
    val calc_op1 = abuf_in_data

    // instance int8 adders
    val calc_fout_sat = Wire(Vec(conf.CACC_ATOMK, Bool()))
    val calc_pout_vld = Wire(Vec(conf.CACC_ATOMK, Bool()))
    val calc_fout_vld = Wire(Vec(conf.CACC_ATOMK, Bool()))
    val calc_pout_sum = Wire(Vec(conf.CACC_ATOMK, UInt(conf.CACC_PARSUM_WIDTH.W)))
    val calc_fout_sum = Wire(Vec(conf.CACC_ATOMK, UInt(conf.CACC_FINAL_WIDTH.W)))

    val u_cell_int8 = Array.fill(conf.CACC_ATOMK){Module(new NV_NVDLA_CACC_CALC_int8)}

    for(i <- 0 to conf.CACC_ATOMK-1){
        u_cell_int8(i).io.nvdla_core_clk := io.nvdla_cell_clk
        u_cell_int8(i).io.cfg_truncate := io.cfg_truncate
        u_cell_int8(i).io.in_data := calc_op0(i)
        u_cell_int8(i).io.in_op := calc_op1(i)
        u_cell_int8(i).io.in_op_valid := calc_op1_vld(i)
        u_cell_int8(i).io.in_sel := calc_dlv_valid
        u_cell_int8(i).io.in_valid := calc_op_en(i)
        calc_fout_sum(i) := u_cell_int8(i).io.out_final_data
        calc_fout_sat(i) := u_cell_int8(i).io.out_final_sat
        calc_fout_vld(i) := u_cell_int8(i).io.out_final_valid
        calc_pout_sum(i) := u_cell_int8(i).io.out_partial_data
        calc_pout_vld(i) := u_cell_int8(i).io.out_partial_valid
    }

    // Latency pipeline to balance with calc cells, signal for both abuffer & dbuffer

    val calc_valid_d = Wire(Bool()) +: 
                       Seq.fill(conf.CACC_CELL_PARTIAL_LATENCY)(RegInit(false.B)) 
    val calc_wr_en_d = Wire(Bool()) +: 
                       Seq.fill(conf.CACC_CELL_PARTIAL_LATENCY)(RegInit(false.B)) 
    val calc_addr_d = Wire(UInt(6.W))+:
                      Seq.fill(conf.CACC_CELL_PARTIAL_LATENCY)(RegInit("b0".asUInt(6.W)))
    
    calc_valid_d(0) := calc_valid
    calc_wr_en_d(0) := calc_wr_en
    calc_addr_d(0) := calc_addr

    for (t <- 0 to conf.CACC_CELL_PARTIAL_LATENCY-1){
        calc_valid_d(t+1) := calc_valid_d(t)
        calc_wr_en_d(t+1) := calc_wr_en_d(t)
        when(calc_valid_d(t)){
            calc_addr_d(t+1) := calc_addr_d(t)
        }
    }  

    val calc_valid_out = calc_valid_d(conf.CACC_CELL_PARTIAL_LATENCY) 
    val calc_wr_en_out = calc_wr_en_d(conf.CACC_CELL_PARTIAL_LATENCY) 
    val calc_addr_out = calc_addr_d(conf.CACC_CELL_PARTIAL_LATENCY)

    val calc_dlv_valid_d = Wire(Bool()) +: 
                           Seq.fill(conf.CACC_CELL_FINAL_LATENCY)(RegInit(false.B)) 
    val calc_stripe_end_d = Wire(Bool()) +: 
                            Seq.fill(conf.CACC_CELL_FINAL_LATENCY)(RegInit(false.B))
    val calc_layer_end_d = Wire(Bool()) +: 
                           Seq.fill(conf.CACC_CELL_FINAL_LATENCY)(RegInit(false.B))
    
    calc_dlv_valid_d(0) := calc_dlv_valid
    calc_stripe_end_d(0) := calc_stripe_end
    calc_layer_end_d(0) := calc_layer_end

    for (t <- 0 to conf.CACC_CELL_FINAL_LATENCY-1){
        calc_dlv_valid_d(t+1) := calc_dlv_valid_d(t)
        when(calc_dlv_valid_d(t)){
            calc_stripe_end_d(t+1) := calc_stripe_end_d(t)
            calc_layer_end_d(t+1) := calc_layer_end_d(t)
        }
    } 

    val calc_dlv_valid_out = calc_dlv_valid_d(conf.CACC_CELL_FINAL_LATENCY)
    val calc_stripe_end_out = calc_stripe_end_d(conf.CACC_CELL_FINAL_LATENCY)
    val calc_layer_end_out = calc_layer_end_d(conf.CACC_CELL_FINAL_LATENCY)

    // Gather of accumulator result   
    val calc_pout = Wire(Vec(conf.CACC_ATOMK, UInt(conf.CACC_PARSUM_WIDTH.W)))
    for (i <- 0 to conf.CACC_ATOMK-1){
        when(calc_pout_vld(i)){
            calc_pout(i) := calc_pout_sum(i)
        }
        .otherwise{
            calc_pout(i) := 0.U
        }
    }
    val calc_fout = Wire(Vec(conf.CACC_ATOMK, UInt(conf.CACC_FINAL_WIDTH.W)))
    for (i <- 0 to conf.CACC_ATOMK-1){
        when(calc_fout_vld(i)){
            calc_fout(i) := calc_fout_sum(i)
        }
        .otherwise{
            calc_fout(i) := 0.U
        }
    }  

    // to abuffer, 1 pipe

    io.abuf_wr.addr.valid := RegNext(calc_wr_en_out, false.B)
    io.abuf_wr.addr.bits := RegEnable(calc_addr_out, calc_wr_en_out)
    io.abuf_wr.data := RegEnable(calc_pout.asUInt, calc_wr_en_out)

    // to dbuffer, 1 pipe.
    io.dlv_data := RegEnable(calc_fout, calc_dlv_valid_out)
    io.dlv_valid := RegNext(calc_dlv_valid_out, false.B)
    io.dlv_mask := RegNext(calc_dlv_valid_out, false.B)
    val dlv_stripe_end = RegEnable(calc_stripe_end_out, false.B, calc_dlv_valid_out)
    val dlv_layer_end = RegEnable(calc_layer_end_out, false.B, calc_dlv_valid_out)
    io.dlv_pd := Cat(dlv_layer_end, dlv_stripe_end)

    // overflow count  
    val dlv_sat_vld_d1 = RegInit(false.B)
    val dlv_sat_end_d1 = RegInit(true.B)
    val dlv_sat_bit_d1 = RegInit(VecInit(Seq.fill(conf.CACC_ATOMK)(false.B)))
    val dlv_sat_clr_d1 = RegInit(false.B)
    
    val dlv_sat_bit = calc_fout_sat
    val dlv_sat_end = calc_layer_end_out & calc_stripe_end_out
    val dlv_sat_clr = calc_dlv_valid_out & ~dlv_sat_end & dlv_sat_end_d1

    dlv_sat_vld_d1 := calc_dlv_valid_out
    when(calc_dlv_valid_out){
        dlv_sat_end_d1 := dlv_sat_end
        dlv_sat_bit_d1 := dlv_sat_bit
    }
    dlv_sat_clr_d1 := dlv_sat_clr
    val sat_sum = VecInit((0 to conf.CACC_ATOMK-1) map { i => dlv_sat_bit_d1(i).asUInt}).reduce(_+&_)

    val sat_count = RegInit("b0".asUInt(32.W))
    val sat_count_inc = (sat_count +& sat_sum)(31, 0)
    val sat_carry = (sat_count +& sat_sum)(32)
    val sat_count_w = Mux(dlv_sat_clr_d1, Cat("b0".asUInt(24.W), sat_sum),
                      Mux(sat_carry, Fill(32, true.B), sat_count_inc))
    val sat_reg_en = dlv_sat_vld_d1 & ((sat_sum.orR) | dlv_sat_clr_d1);
    when(sat_reg_en){
        sat_count := sat_count_w
    }

    io.dp2reg_sat_count := sat_count

}}