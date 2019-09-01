// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_WDMA_cmd(implicit val conf: nvdlaConfig) extends Module {
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())
//         val pwrbus_ram_pd = Input(UInt(32.W))
//         val op_load = Input(Bool())

//         val cmd2dat_spt_pvld = Output(Bool())
//         val cmd2dat_spt_prdy = Input(Bool())
//         val cmd2dat_spt_pd = Output(UInt(15.W))

//         val cmd2dat_dma_pvld = Output(Bool())
//         val cmd2dat_dma_prdy = Input(Bool())
//         val cmd2dat_dma_pd = Output(UInt((conf.SDP_WR_CMD_DW+2).W))

//         val reg2dp_batch_number = Input(UInt(5.W))
//         val reg2dp_winograd = Input(Bool())
//         val reg2dp_channel = Input(UInt(13.W))
//         val reg2dp_height = Input(UInt(13.W))
//         val reg2dp_width = Input(UInt(13.W))
//         val reg2dp_output_dst = Input(Bool())
//         val reg2dp_out_precision = Input(UInt(2.W))
//         val reg2dp_proc_precision = Input(UInt(2.W))
//         val reg2dp_dst_base_addr_high = Input(UInt(32.W))
//         val reg2dp_dst_base_addr_low = Input(UInt((32-conf.AM_AW).W))
//         val reg2dp_dst_batch_stride = Input(UInt((32-conf.AM_AW).W))
//         val reg2dp_dst_line_stride = Input(UInt((32-conf.AM_AW).W))
//         val reg2dp_dst_surface_stride = Input(UInt((32-conf.AM_AW).W))
//         val reg2dp_ew_alu_algo = Input(UInt(2.W))
//         val reg2dp_ew_alu_bypass = Input(Bool())
//         val reg2dp_ew_bypass = Input(Bool())

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

// withClock(io.nvdla_core_clk){

// ////////cfg reg////////////    
//     val cfg_dst_addr = if(conf.NVDLA_MEM_ADDRESS_WIDTH > 31) Cat(io.reg2dp_dst_base_addr_high, io.reg2dp_dst_base_addr_low)
//                        else io.reg2dp_dst_base_addr_low

//     val cfg_dst_surf_stride = io.reg2dp_dst_surface_stride
//     val cfg_dst_line_stride = io.reg2dp_dst_line_stride
//     val cfg_mode_batch = false.B
//     val cfg_mode_winog = false.B

//     val cfg_di_int8 = io.reg2dp_proc_precision === 0.U
//     val cfg_di_int16 = io.reg2dp_proc_precision === 1.U
//     val cfg_do_int8 = io.reg2dp_out_precision === 0.U
//     val cfg_do_int16 = io.reg2dp_out_precision === 1.U

//     val cfg_mode_8to16 = false.B
//     val cfg_mode_norml = !(cfg_mode_batch | cfg_mode_winog | cfg_mode_8to16)
     
//     val cfg_mode_1x1_pack = (io.reg2dp_width === 0.U) & (io.reg2dp_height === 1.U)
//     val cfg_mode_1x1_nbatch = cfg_mode_1x1_pack & !cfg_mode_batch

//     val cfg_mode_eql = (io.reg2dp_ew_bypass === 0.U) & 
//                         (io.reg2dp_ew_alu_bypass === 0.U) &
//                         (io.reg2dp_ew_alu_algo === 3.U)
//     val cfg_mode_pdp = io.reg2dp_output_dst === 1.U
//     val cfg_mode_quite = cfg_mode_eql | cfg_mode_pdp
//     val cfg_addr_en = !cfg_mode_quite

//     //==============
//     // Surf is always in unit of ATOMIC (1x1x32B)

//     val size_of_surf = Wire(UInt((13-conf.AM_AW).W))
//     when(cfg_di_int8){
//         size_of_surf := Cat(false.B, io.reg2dp_channel(12, conf.AM_AW))
//     }.elsewhen(cfg_di_int16){
//         size_of_surf := io.reg2dp_channel(12, conf.AM_AW2)
//     }.otherwise{
//         size_of_surf := io.reg2dp_channel(12, conf.AM_AW2)
//     }

//     //=================================================
//     // Cube Shape
//     //=================================================
//     val is_last_wg = true.B
//     val is_last_e = Wire(Bool())
//     val is_last_batch = Wire(Bool())
//     val is_last_w = Wire(Bool())
//     val is_last_h = Wire(Bool())
//     val is_last_c = Wire(Bool())

//     val is_winog_end = is_last_wg
//     val is_elem_end = cfg_mode_1x1_nbatch | is_last_e
//     val is_line_end = cfg_mode_1x1_nbatch | cfg_mode_norml | (is_last_batch & is_elem_end & is_last_w & is_winog_end)
//     val is_surf_end = cfg_mode_1x1_nbatch | is_line_end & is_last_h
//     val is_cube_end = cfg_mode_1x1_nbatch | is_surf_end & is_last_c

//     //==============
//     // Width Count;
//     //==============
//     // Norml Mode
//     val odd = false.B

//     //================================
//     // SIZE of Trans
//     //================================
//     val size_of_width = Cat(false.B, io.reg2dp_width)

//     val count_w = RegInit(0.U(14.W))
//     val cmd_accept = Wire(Bool())

//     when(cmd_accept){
//         when(is_line_end){
//             count_w := 0.U
//         }.elsewhen(is_last_batch & is_winog_end){
//             count_w := count_w + 1.U
//         }
//     }

//     val is_ltrans = (count_w === size_of_width)
//     val is_ftrans = (count_w === 0.U)

//     is_last_w := is_ltrans
//     is_last_e := true.B

//     //==============
//     // HEIGHT Count:
//     //==============
//     val size_of_height = io.reg2dp_height
//     val count_h = RegInit(0.U(13.W))

//     when(cmd_accept){
//         when(is_last_batch){
//             when(is_surf_end){
//                 count_h := 0.U
//             }.elsewhen(is_line_end){
//                 count_h := count_h + 1.U
//             }
//         }
//     }
//     is_last_h := (count_h === size_of_height)

//     //==============
//     // CHANNEL Count
//     //==============
//     val count_c = RegInit(0.U(9.W))

//     when(cmd_accept){
//         when(is_last_batch){
//             when(is_cube_end){
//                 count_c := 0.U
//             }.elsewhen(is_surf_end){
//                 count_c := count_c + 1.U
//             }
//         }
//     }

//     is_last_c := (count_c === size_of_surf)

// // //==============
// // // BATCH Count: 
// // //==============
//     is_last_batch := true.B

//     //==========================================
//     // DMA Req : ADDR PREPARE
//     //==========================================

//     val base_addr_width = RegInit(0.U((conf.NVDLA_MEM_ADDRESS_WIDTH - conf.AM_AW).W))
//     val base_addr_surf = RegInit(0.U((conf.NVDLA_MEM_ADDRESS_WIDTH - conf.AM_AW).W))
//     val base_addr_line = RegInit(0.U((conf.NVDLA_MEM_ADDRESS_WIDTH - conf.AM_AW).W))

//     // WIDTH
//     when(cfg_addr_en){
//         when(io.op_load){
//             base_addr_width := cfg_dst_addr
//         }.elsewhen(cmd_accept){
//             // #ifdef NVDLA_SDP_DATA_TYPE_INT8TO16
//             // #ifdef NVDLA_WINOGRAD_ENABLE 
//             // #ifdef NVDLA_BATCH_ENABLE 
//             when(is_surf_end){
//                 base_addr_width := base_addr_surf + cfg_dst_surf_stride
//             }.elsewhen(is_line_end){
//                 base_addr_width := base_addr_line + cfg_dst_line_stride
//             }
//         }
//     }

//     // LINE
//     when(cfg_addr_en){
//         when(io.op_load){
//             base_addr_line := cfg_dst_addr
//         }.elsewhen(cmd_accept){
//             // #ifdef NVDLA_SDP_DATA_TYPE_INT8TO16
//             // #ifdef NVDLA_WINOGRAD_ENABLE 
//             // #ifdef NVDLA_BATCH_ENABLE 
//             when(is_surf_end){
//                 base_addr_line := base_addr_surf + cfg_dst_surf_stride
//             }.elsewhen(is_line_end){
//                 base_addr_line := base_addr_line + cfg_dst_line_stride            
//             }
//         }
//     }

//     // SURF
//     when(cfg_addr_en){
//         when(io.op_load){
//             base_addr_surf := cfg_dst_addr
//         }.elsewhen(cmd_accept){
//             // #ifdef NVDLA_SDP_DATA_TYPE_INT8TO16
//             // #ifdef NVDLA_WINOGRAD_ENABLE 
//             // #ifdef NVDLA_BATCH_ENABLE 
//             when(is_surf_end){
//                 base_addr_surf := base_addr_surf + cfg_dst_surf_stride
//             }            
//         }
//     }
//     //==========================================
//     // DMA Req : SIZE
//     //==========================================
//     // #ifdef NVDLA_SDP_DATA_TYPE_INT8TO16
//     // #ifdef NVDLA_WINOGRAD_ENABLE 
//     // #ifdef NVDLA_BATCH_ENABLE 
//     val dma_addr = base_addr_line
//     //========================
//     // Output: one for data write spt_; and one for data read dma_
//     //========================
//     // spt_size is to tell how many data from dp2wdma for a corresponding DMA req to MC/CF if
//     // spt_size is in unit of cycle on dp2wdma
//     val mode_1x1_spt_size = Mux(cfg_do_int8 | cfg_di_int8, Cat(false.B, io.reg2dp_channel(12, conf.AM_AW)),
//                                 io.reg2dp_channel(12, conf.AM_AW2))
//     val mode_norml_spt_size = Cat(false.B, io.reg2dp_width)

//     val spt_size = Wire(UInt(14.W))
//     when(cfg_mode_1x1_nbatch){
//         spt_size := Cat(Fill(conf.AM_AW, false.B), mode_1x1_spt_size)
//     }.otherwise{
//         spt_size := mode_norml_spt_size
//     }

//     //========================
//     // Output: one for data write spt_; and one for data read dma_
//     //========================
//     val mode_1x1_dma_size = size_of_surf
//     val mode_norml_dma_size = io.reg2dp_width

//     val dma_size = Wire(UInt(13.W))
//     when(cfg_mode_1x1_nbatch){
//         dma_size := Cat(Fill(conf.AM_AW2, false.B), mode_1x1_dma_size)
//     }.otherwise{
//         dma_size := mode_norml_dma_size
//     }

//     //=================================================
//     // OUTPUT FIFO: SPT & DMA channel
//     //=================================================
//     val cmd_vld = RegInit(false.B)
//     when(io.op_load){
//         cmd_vld := true.B
//     }.elsewhen(cmd_accept){
//         when(is_cube_end){
//             cmd_vld := false.B
//         }
//     }

//     val dma_fifo_prdy = Wire(Bool())
//     val spt_fifo_prdy = Wire(Bool())

//     val spt_fifo_pvld = cmd_vld & dma_fifo_prdy
//     val dma_fifo_pvld = cmd_vld & spt_fifo_prdy

//     val cmd_rdy = dma_fifo_prdy & spt_fifo_prdy
//     cmd_accept := cmd_vld & cmd_rdy

//     val spt_fifo_pd = Cat(odd, spt_size)

//     val dma_fifo_pd = Cat(is_cube_end, odd, dma_size, dma_addr)
    
//     val u_sfifo = Module{new NV_NVDLA_SDP_fifo_flop_based(4, 15)}

//     u_sfifo.io.clk := io.nvdla_core_clk 
//     u_sfifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd

//     u_sfifo.io.wr_vld := spt_fifo_pvld
//     spt_fifo_prdy := u_sfifo.io.wr_rdy
//     u_sfifo.io.wr_data := spt_fifo_pd

//     io.cmd2dat_spt_pvld := u_sfifo.io.rd_vld
//     u_sfifo.io.rd_rdy := io.cmd2dat_spt_prdy
//     io.cmd2dat_spt_pd := u_sfifo.io.rd_data
    
//     val u_dfifo = Module{new NV_NVDLA_SDP_fifo_flop_based(4, 44)}

//     u_dfifo.io.clk := io.nvdla_core_clk 
//     u_dfifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd

//     u_dfifo.io.wr_vld := dma_fifo_pvld
//     dma_fifo_prdy := u_dfifo.io.wr_rdy
//     u_dfifo.io.wr_data := dma_fifo_pd

//     io.cmd2dat_dma_pvld := u_dfifo.io.rd_vld
//     u_dfifo.io.rd_rdy := io.cmd2dat_dma_prdy
//     io.cmd2dat_dma_pd := u_dfifo.io.rd_data

// }}

 
// object NV_NVDLA_SDP_WDMA_cmdDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_WDMA_cmd())
// }


