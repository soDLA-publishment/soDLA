// package nvdla

// import chisel3._
// import chisel3.experimental._

// class NV_NVDLA_SDP_fifo(depth: Int, width: Int, reg_wr_data: Boolean, reg_rd_data: Boolean) extends RawModule {
//     val io = IO(new Bundle {
//         //general clock
//         val clk = Input(Clock())

//         //wr pipeline 
//         val wr_ready = Output(Bool())
//         val wr_req = Input(Bool())  
//         val wr_data = Input(UInt(width.W))

//         //rd pipeline
//         val rd_ready = Input(Bool())
//         val rd_req = Output(Bool()) 
//         val rd_data = Output(UInt(width.W)) 

//         //
//         val pwrbus_ram_pd = Input(UInt(32.W))

//     })
// //     
// //          ┌─┐       ┌─┐
// //       ┌──┘ ┴───────┘ ┴──┐
// //       │                 │
// //       │       ───       │          
// //       │  ─┬┘       └┬─  │
// //       │                 │
// //       │       ─┴─       │
// //       │                 │
// //       └───┐         ┌───┘
// //           │         │
// //           │         │
// //           │         │
// //           │         └──────────────┐
// //           │                        │
// //           │                        ├─┐
// //           │                        ┌─┘    
// //           │                        │
// //           └─┐  ┐  ┌───────┬──┐  ┌──┘         
// //             │ ─┤ ─┤       │ ─┤ ─┤         
// //             └──┴──┘       └──┴──┘ 
// withClock(io.clk){    
//     // Master Clock Gating (SLCG)
//     //
//     // We gate the clock(s) when idle or stalled.
//     // This allows us to turn off numerous miscellaneous flops
//     // that don't get gated during synthesis for one reason or another.
//     //
//     // We gate write side and read side separately. 
//     // If the fifo is synchronous, we also gate the ram separately, but if
//     // -master_clk_gated_unified or -status_reg/-status_logic_reg is specified, 
//     // then we use one clk gate for write, ram, and read.
//     //

//     val clk_mgated_enable = Wire(Bool())  // assigned by code at end of this module
//     val clk_mgated = Wire(Clock())  // used only in synchronous fifos
//     val clk_mgate = Module(new NV_CLK_gate_power)

//     clk_mgate.io.clk := io.clk
//     clk_mgate.io.reset_ := io.reset_
//     clk_mgate.io.clk_en := clk_mgated_enable 
//     clk_mgated := clk_mgate.io.clk_gated

//     // 
//     // WRITE SIDE
//     //  
//     val wr_reserving = Wire(Bool()) 
//     val wr_req_in = if(reg_wr_data) Some(RegInit(false.B)) else None    // registered wr_req                     
//     var wr_data_in = if(reg_wr_data) Some(Reg(UInt(width.W))) else None // registered wr_data
//     val wr_busy_in = RegInit(false.B)    // inputs being held this cycle?  or  copy for internal use
//     io.wr_ready := !wr_busy_in

//     val wr_busy_next = Wire(Bool())     // fwd: fifo busy next?

//     // factor for better timing with distant wr_req signal
//     val wr_busy_in_next_wr_req_eq_1 = wr_busy_next
//     val wr_busy_in_next_wr_req_eq_0 = (wr_req_in && wr_busy_next) && !wr_reserving
//     val wr_busy_in_next = Mux(io.wr_req, wr_busy_in_next_wr_req_eq_1, wr_busy_in_next_wr_req_eq_0)

//     val wr_busy_in_int = Wire(Bool())
//     wr_busy_in := wr_busy_in_next
//     when(!wr_busy_in_int){
//         wr_req_in := io.wr_req && !wr_busy_in
//     }
//     if(reg_wr_data){ 
//     when(!wr_busy_in && io.wr_req){
//         wr_data_in := io.wr_data
//     }}}


//     wr_reserving := wr_req_in & !wr_busy_int    // reserving write space?

//     if(reg_wr_data){
//         val wr_popping = Reg(Bool())  // fwd: write side sees pop?
//     }
//     else{
//         val wr_popping = Wire(Bool())  // fwd: write side sees pop?
//     }
//     val wr_count = Reg("b0".asUInt(log2Ceil(depth).W))			// write-side 
    
//     val wr_count_next_wr_popping = Mux(wr_reserving, wr_count, (wr_count - "d1".asUInt(1.W)))
//     val wr_count_next_no_wr_popping = Mux(wr_reserving, wr_count + "d1".asUInt(1.W), wr_count)
//     val wr_count_next = Mux(wr_popping, wr_count_next_wr_popping, wr_count_next_no_wr_popping)

//     val wr_count_next_no_wr_popping_is_max = ( wr_count_next_no_wr_popping === depth.asUInt(8.W))
//     val wr_count_next_is_max = Mux(wr_popping, false.B, wr_count_next_no_wr_popping_is_max)

//     val wr_limit_muxed = Wire(UInt(8.W))  // muxed with simulation/emulation overrides
//     val wr_limit_reg = wr_limit_muxed

//     wr_busy_in_int := wr_req_in && wr_busy_int
    
//     wr_busy_int := wr_busy_next
//     when (wr_reserving ^ wr_popping) {
//         wr_count := wr_count_next
//     } 

//     val  wr_pushing = wr_reserving   // data pushed same cycle as wr_req_in

//     //RAM

//     val wr_adr = Reg(UInt(log2Ceil(depth).W)) 			// current write address
//     val rd_adr_p = Wire(UInt(log2Ceil(depth).W))  // read address to use for ram
//     val rd_data_p = Wire(UInt(width.W)) // read data directly out of ram

//     val rd_enable = Wire(Bool())

//     val ore = Wire(Bool())
//     io.pwrbus_ram_pd := Wire(UInt(32.W))

//     // Adding parameter for fifogen to disable wr/rd contention assertion in ramgen.
//     // Fifogen handles this by ignoring the data on the ram data out for that cycle.

//     val ram = Module(new nv_ram_rwsp(depth, width))
//     ram.io.clk := io.clk
//     ore 
//     ram.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//     ram.io.wa := wr_adr
//     ram.io.we := wr_pushing 
//     ram.io.di := wr_data_in
//     ram.io.ra := rd_adr_p 
//     ram.io.re := rd_enable 

//     rd_data_p := ram.io.dout
//     := ram.io.ore

//     // next wr_adr if wr_pushing=1
//     val wr_adr_next = wr_adr + "d1".asUInt(1.W)

//     // spyglass disable_block W484

//     withClockAndReset(clk_mgated, !io.reset_) {
//         when (wr_pushing) {
//             wr_adr := wr_adr_next
//         }
//     }  

//     // spyglass enable_block W484

//     val rd_popping = Wire(Bool())// read side doing pop this cycle?
//     val rd_adr = Reg(UInt(7.W))// current read address

//     // next    read address 

//     val rd_adr_next = rd_adr + "d1".asUInt(1.W)
//     rd_adr_p := Mux(rd_popping, rd_adr_next, rd_adr) //for ram

//     // spyglass disable_block W484

//     withClockAndReset(clk_mgated, !io.reset_) {
//         when (rd_popping) {
//             rd_adr := rd_adr_next
//         }
//     }  

//     // spyglass enable_block W484

//     //
//     // SYNCHRONOUS BOUNDARY
//     //

//     withClockAndReset(clk_mgated, !io.reset_) {
//         wr_popping := rd_popping
//     } 

//     val rd_pushing = Reg(Bool())

//     withClockAndReset(clk_mgated, !io.reset_) {
//         rd_pushing := wr_pushing// let data go into ram first
//     } 

//     //
//     // READ SIDE
//     //

//     val rd_req_p = Reg(Bool())// data out of fifo is valid
//     val rd_req_int = Reg(Bool())// internal copy of rd_req
//     io.rd_req := rd_req_int
//     rd_popping := rd_req_p && !(rd_req_int && !io.rd_ready)
//     val rd_count_p = Reg(UInt(8.W)) // read-side fifo count
//     // spyglass disable_block W164a W484

//     val rd_count_p_next_rd_popping = Mux(rd_pushing, rd_count_p, rd_count_p - "d1".asUInt(1.W))
//     val rd_count_p_next_no_rd_popping =  Mux(rd_pushing, rd_count_p  +"d1".asUInt(1.W), rd_count_p)

//     // spyglass enable_block W164a W484
//     val rd_count_p_next = Mux(rd_popping,  rd_count_p_next_rd_popping,  rd_count_p_next_no_rd_popping)
//     val rd_count_p_next_rd_popping_not_0= (rd_count_p_next_rd_popping != 0)
//     val rd_count_p_next_no_rd_popping_not_0 = (rd_count_p_next_no_rd_popping != 0)
//     val rd_count_p_next_n = Mux(rd_popping,  rd_count_p_next_rd_popping_not_0,   rd_count_p_next_no_rd_popping_not_0)


//     rd_enable := ((rd_count_p_next_not_0) && ((!rd_req_p) || rd_popping))

//     withClockAndReset(clk_mgated, !io.reset_) {
//         when(rd_pushing || rd_popping){
//             rd_count_p:=rd_count_p_next 
//         }
//     } 

//     val rd_req_next = (rd_req_p || (rd_req_int && !io.rd_ready))

//     withClockAndReset(clk_mgated, !io.reset_) {
//         rd_req_int:= rd_req_next
//     } 

//     io.rd_data := rd_data_p
//     ore := rd_popping

//     // Master Clock Gating (SLCG) Enables
//     //

//     clk_mgated_enable := ((wr_reserving || wr_pushing || rd_popping || wr_popping || (wr_req_in && !wr_busy_int) || (wr_busy_int != wr_busy_next)) || (rd_pushing || rd_popping || (rd_req_int && io.rd_ready) || wr_pushing))
 

  
// }