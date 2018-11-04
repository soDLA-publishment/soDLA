package nvdla

import chisel3._

class NV_NVDLA_CDMA_DC_fifo extends Module {
    val io = IO(new Bundle {
        //general clock
        val clk = Input(Clock())
        val reset_ = Input(Bool())

        //control signal
        val wr_ready = Output(Bool())
        val wr_req = Input(Bool())   
        val rd_ready = Input(Bool())
        val rd_req = Output(Bool())   

        //data signal

        val wr_data = Input(UInt(6.W))
        val rd_data = Output(UInt(6.W))
        val pwrbus_ram_pd = Input(UInt(32.W))

    })

    val clk_mgated_enable = Wire(Bool())
    val clk_mgated = Wire(Bool())

    val clk_mgate = Module(new CKLNQD12())
    io.clk := clk_mgate.io.clk 
    io.reset_ := clk_mgate.io.reset_ 
    clk_mgated_enable := clk_mgate.io.clk_en 
    clk_mgated := clk_mgate.io.clk_gated

    // 
    // WRITE SIDE
    //  
    val wr_reserving = Wire(Bool())
    val wr_req_in = Reg(Bool())
    val wr_data_in = Reg(UInt(6.W))
    val wr_busy_in = Reg(Bool())
    io.wr_ready := !wr_busy_in
    val wr_busy_next = Wire(Bool())

    // factor for better timing with distant wr_req signal
    val wr_busy_in_next_wr_req_eq_1 = wr_busy_next
    val wr_busy_in_next_wr_req_eq_0 = (wr_req_in && wr_busy_next) && !wr_reserving
    val wr_busy_in_next = Mux(io.wr_req, wr_busy_in_next_wr_req_eq_1, wr_busy_in_next_wr_req_eq_0)

    val wr_busy_in_int = Wire(Bool())

    withClockAndReset(io.clk, !io.reset_) {
        wr_busy_in := wr_busy_in_next
        when (!wr_busy_in_int) {
            wr_req_in := wr_req && !wr_busy_in
        }

    } 

    withClock(io.clk){
         when (!wr_busy_in&&wr_req) {
            wr_data_in = RegNext(wr_data)
        }       
    }

    val wr_busy_int = Reg(Bool())		        	// copy for internal use
    wr_reserving := wr_req_in && !wr_busy_int    // reserving write space?


    val wr_popping = Reg(Bool())	               // fwd: write side sees pop?
    val wr_count = Reg(UInt(8.W))			// write-side 
    
    val wr_count_next_wr_popping = Mux(wr_reserving, wr_count, (wr_count - "d1".U(1.W)))
    val wr_count_next_no_wr_popping = Mux(wr_reserving, wr_count + "d1".U(1.W), wr_count)
    val wr_count_next = Mux(wr_popping, wr_count_next_wr_popping, wr_count_next_no_wr_popping)

    val wr_count_next_no_wr_popping_is_128 = ( wr_count_next_no_wr_popping == "d128".U(8.W))
    val wr_count_next_is_128 = Mux(wr_popping, false, wr_count_next_no_wr_popping_is_128)

    val wr_limit_muxed = UInt(8.W)  // muxed with simulation/emulation overrides
    val wr_limit_reg = wr_limit_muxed

    val wr_busy_in_int = wr_req_in && wr_busy_int

    withClockAndReset(clk_mgated, !io.reset_) {
        wr_busy_in := wr_busy_in_next
        when (wr_reserving ^ wr_popping) {
            wr_count := wr_count_next
        }
    } 

    val  wr_pushing = wr_reserving

    //RAM

    val wr_adr = Reg(UInt(7.W)) 			// current write address

    

  
}