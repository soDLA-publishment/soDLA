// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._
// import chisel3.iotesters.Driver

// class NV_NVDLA_BDMA_load extends Module {
//     val io = IO(new Bundle {
//         //clk
//         val nvdla_core_clk = Input(Clock())

//         val bdma2mcif_rd_req_valid = Output(Bool())    /* data valid */
//         val bdma2mcif_rd_req_ready = Input(Bool())      /* data return handshake */
//         val bdma2mcif_rd_req_pd = Output(UInt(79.W))

//         val bdma2cvif_rd_req_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None   /* data valid */
//         val bdma2mcif_rd_req_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None    /* data return handshake */
//         val bdma2mcif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(UInt(79.W))) else None

//         val ld2st_wr_pvld = Output(Bool())  /* data valid */
//         val ld2st_wr_prdy = Input(Bool())   /* data return handshake */ 
//         val ld2st_wr_pd = Output(UInt(161.W))

//         //&Ports /^obs_bus/;
//         val reg2dp_cmd_dst_ram_type = Input(Bool())
//         val reg2dp_cmd_interrupt = Input(Bool())
//         val reg2dp_cmd_interrupt_ptr = Input(Bool())
//         val reg2dp_cmd_src_ram_type = Input(Bool())
//         val reg2dp_dst_addr_high_v8 = Input(UInt(32.W))
//         val reg2dp_dst_addr_low_v32 = Input(UInt(27.W))
//         val reg2dp_dst_line_stride = Input(UInt(27.W))
//         val reg2dp_dst_surf_stride = Input(UInt(27.W))
//         val reg2dp_line_repeat_number = Input(UInt(24.W))
//         val reg2dp_line_size = Input(UInt(13.W))
//         val reg2dp_src_addr_high_v8 = Input(UInt(32.W))
//         val reg2dp_src_addr_low_v32 = Input(UInt(27.W)) 
//         val reg2dp_src_line_stride = Input(UInt(27.W))
//         val reg2dp_surf_repeat_number = Input(UInt(24.W))

//         val csb2ld_vld = Input(Bool())
//         val csb2ld_rdy = Output(Bool())

//         val ld2csb_grp0_dma_stall_inc = Output(Bool())
//         val ld2csb_grp1_dma_stall_inc = Output(Bool())

//         val ld2csb_idle = Output(Bool())        
//         val ld2st_wr_idle = Input(Bool())
//         val st2ld_load_idle = Input(Bool())

//         val ld2gate_slcg_en = Output(Bool())
//     })
//     //     
//     //          ┌─┐       ┌─┐
//     //       ┌──┘ ┴───────┘ ┴──┐
//     //       │                 │
//     //       │       ───       │          
//     //       │  ─┬┘       └┬─  │
//     //       │                 │
//     //       │       ─┴─       │
//     //       │                 │
//     //       └───┐         ┌───┘
//     //           │         │
//     //           │         │
//     //           │         │
//     //           │         └──────────────┐
//     //           │                        │
//     //           │                        ├─┐
//     //           │                        ┌─┘    
//     //           │                        │
//     //           └─┐  ┐  ┌───────┬──┐  ┌──┘         
//     //             │ ─┤ ─┤       │ ─┤ ─┤         
//     //             └──┴──┘       └──┴──┘
// withClock(io.nvdla_core_clk){
//     // synoff nets

//     // monitor nets

//     // debug nets

//     // tie high nets

//     // tie low nets

//     // no connect nets

//     // not all bits used nets

//     // todo nets

        
//     //==============
//     // LOAD: PROCESS
//     //==============
//     // when Load process is idle or finished the execution of last command, it will pop the next command from CSB cmd FIFO

//     // input rename

//     // FLAG: processing 
//     // downstreams are dmaif and context-Q
//     // after the csb cmd queue, will have one more layer to buffer the command when processing
//     // will load when cmd layer is empty, or when reach the last transaction and will be accepted by downstream(dma and CQ)
//     //  cmd_valid        cmd_ready
//     //     _|_______________|_  
//     //    |        pipe       | 
//     //    |___________________| 
//     //      v               ^   
//     //  tran_valid       tran_ready
//     val cmd_ready = Wire(Bool())
//     val load_cmd = Wire(Bool())
//     val load_cmd_en = Wire(Bool())
//     val tran_accept = Wire(Bool())
//     val is_cube_end = Wire(Bool())
//     val dma_rd_req_rdy = Wire(Bool())
//     val ld_idle = Wire(Bool())
//     val tran_valid = RegInit(false.B)
//     val reg_cmd_src_ram_type = RegInit(false.B)

//     io.csb2ld_rdy := io.ld2st_wr_prdy & cmd_ready & load_cmd_en
//     io.ld2st_wr_pvld := io.csb2ld_vld & cmd_ready & load_cmd_en
//     val cmd_valid = io.csb2ld_vld & io.ld2st_wr_prdy & load_cmd_en
//     val cmd_ready = (!tran_valid) | (tran_accept & is_cube_end)

//     when(cmd_ready){
//         tran_valid := cmd_valid
//     }
//     tran_ready := dma_rd_req_rdy
//     tran_accept := tran_valid & tran_ready
//     load_cmd = cmd_valid & cmd_ready

//     val is_src_ram_type_switching = (io.reg2dp_cmd_src_ram_type =/= reg_cmd_src_ram_type)
//     load_cmd_en := io.csb2ld_vld & ((ld_idle & io.st2ld_load_idle) || !is_src_ram_type_switching);

//     //==============================
//     // IDLE
//     ld_idle := io.ld2st_wr_idle & !tran_valid
//     io.ld2csb_idle := ld_idle

//     //==============================
//     // SLCG
//     io.ld2gate_slcg_en := RegNext(!ld_idle, false.B)

//     // Constant Reg on each COMMAND
//     val reg2dp_dst_addr = Cat(io.reg2dp_dst_addr_high_v8, io.reg2dp_dst_addr_low_v32, "b0".arUInt(5.W))
//     val reg2dp_addr = Cat(io.reg2dp_src_addr_high_v8, io.reg2dp_src_addr_low_v32, "b0".arUInt(5.W))
//     val reg2dp_src_line_stride_ext = Cat(io.reg2dp_src_line_stride, "h0".asUInt(5.W))
//     val reg2dp_src_surf_stride_ext = Cat(io.reg2dp_src_surf_stride, "h0".asUInt(5.W))

//     val reg_line_size = RegInit("b0".asUInt(13.W))
//     val reg_line_stride = RegInit("b0".asUInt(32.W))
//     val reg_surf_stride = RegInit("b0".asUInt(32.W))
//     val reg_line_repeat_number = RegInit("b0".asUInt(24.W))
//     val reg_surf_repeat_number = RegInit("b0".asUInt(24.W))

//     when(load_cmd){
//         reg_line_size := io.reg2dp_line_size
//         reg_cmd_src_ram_type := io.reg2dp_cmd_src_ram_type;
//         reg_line_stride := reg2dp_src_line_stride_ext;
//         reg_surf_stride := reg2dp_src_surf_stride_ext;
//         //reg_cmd_dst_ram_type       <= reg2dp_cmd_dst_ram_type;
//         //reg_cmd_interrupt      <= reg2dp_cmd_interrupt;
//         reg_line_repeat_number := io.reg2dp_line_repeat_number;
//         //reg_dst_line_stride <= reg2dp_dst_line_stride;
//         reg_surf_repeat_number := io.reg2dp_surf_repeat_number;
//         //reg_dst_surf_stride <= reg2dp_dst_surf_stride;

//         //reg_src_addr <= reg2dp_src_addr;
//         //reg_dst_addr <= reg2dp_dst_addr;
//     }

// // ===================================
// // Context Queue Write
// // ===================================
// // below are required cmd information needed in store side to reassemble the return data, 
// // and format the DMA write request, one emtry is needed for each MOMERY COPY COMMAND
//     val ld2st_addr = reg2dp_dst_addr
//     val ld2st_line_size = reg2dp_line_size
//     val ld2st_cmd_src_ram_type = io.reg2dp_cmd_src_ram_type
//     val ld2st_cmd_dst_ram_type = io.reg2dp_cmd_dst_ram_type
//     val ld2st_cmd_interrupt = io.reg2dp_cmd_interrupt
//     val ld2st_cmd_interrupt_ptr = io.reg2dp_cmd_interrupt_ptr
//     val ld2st_line_stride = io.reg2dp_dst_line_stride
//     val ld2st_surf_stride = io.reg2dp_dst_surf_stride
//     val ld2st_line_repeat_number = io.reg2dp_line_repeat_number
//     val ld2st_surf_repeat_number = io.reg2dp_surf_repeat_number


//     // PKT_PACK_WIRE( bdma_ld2st , ld2st_ , ld2st_wr_pd )

//     io.ld2st_wr_pd := Cat(ld2st_surf_repeat_number, ld2st_surf_stride, ld2st_line_repeat_number,
//                          ld2st_line_stride, ld2st_cmd_interrupt_ptr, ld2st_cmd_interrupt, 
//                          ld2st_cmd_dst_ram_type, ld2st_cmd_src_ram_type, ld2st_line_size,
//                          ld2st_addr)

//     // Variable Reg on each COMMAND
//     // 3-D support tran->line->surf->cube
//     // cube consists of multiple surfaces(surf)
//     // surf consists of multiple lines
//     // line consists of multiple transaction(tran)

//     val is_last_req_in_line = true.B
//     val line_count = RegInit("b0".asUInt(24.W))
//     val surf_count = RegInit("b0".asUInt(24.W))
//     val is_surf_end = is_last_req_in_line & (line_count === reg_line_repeat_number)
//     is_cube_end := is_surf_end & (surf_count === reg_surf_repeat_number)

//     // tran_addr is the start address of each DMA request
//     // will load a new one from CSB FIFO for every mem copy command
//     // will change every time one DMA request is aacpetted by xxif
//     val line_addr = Reg(UInt(64.W))
//     val tran_addr = line_addr

//     // Line_addr is the start address of every line
//     // load a new one from CSB FIFO for every mem copy command
//     // will change every time one a block is done and jump to the next line
//     val surf_addr = RegInit(0.U(64.W))

//     when(load_cmd){
//         line_addr := reg2dp_addr
//     }.elsewhen(tran_accept){
//         when(is_surf_end){
//             line_addr := surf_addr + reg_surf_stride
//         }.otherwise{
//             line_addr := line_addr + reg_line_stride
//         }
//     } 

//     // Surf_addr is the base address of each surface
//     // load a new one from CSB FIFO for every mem copy command
//     // will change every time one a block is done and jump to the next surface
//     when(load_cmd){
//         surf_addr := reg2dp_addr
//     }.elsewhen(tran_accept){
//         when(is_surf_end){
//             surf_addr := surf_addr + reg_surf_stride
//         }
//     }

//     //===TRAN SIZE
//     // for each DMA request, tran_size is to tell how many 32B DATA block indicated
//     tran_size := Cat(0.U(2.W), reg_line_size)

//     // ===LINE COUNT
//     // count++ when just to next line
//     when(tran_accept){
//         when(is_surf_end){
//             line_count := 0.U
//         }.otherwise{
//             line_count := line_count + 1.U
//         }
//     }

//     // SURF COUNT
//     // count++ when just to next surf
//     when(tran_accept){
//         when(is_cube_end){
//             surf_count := 0.U
//         }.elsewhen(is_surf_end){
//             surf_count := surf_count + 1.U
//         }
//     }

//     //==============
//     // LOAD: DMA OUT
//     //==============
//     // ===DMA Request: ADDR/SIZE/INTR
//     val dma_rd_req_vld  = tran_valid
//     val dma_rd_req_addr = tran_addr
//     val dma_rd_req_type = reg_cmd_src_ram_type
//     val dma_rd_req_size = tran_size

//     // PKT_PACK_WIRE( dma_read_cmd , dma_rd_req_ , dma_rd_req_pd )
//     val dma_rd_req_pd = Cat(dma_rd_req_size, dma_rd_req_addr)


// }}


// object NV_NVDLA_BDMA_loadDriver extends App {
//   chisel3.Driver.execute(args, () => new NV_NVDLA_BDMA_load())
// }

