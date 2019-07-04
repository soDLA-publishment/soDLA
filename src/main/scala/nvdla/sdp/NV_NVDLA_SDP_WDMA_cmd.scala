// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_WDMA_cmd(implicit val conf: sdpConfiguration) extends Module {
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

// withClock(io.nvdla_core_clk){

// ////////cfg reg////////////    

// // #if (NVDLA_MEM_ADDRESS_WIDTH > 32)
// // #else

//     val cfg_dst_addr = io.reg2dp_dst_base_addr_low

//     val cfg_dst_surf_stride = io.reg2dp_dst_surface_stride
//     val cfg_dst_line_stride = io.reg2dp_dst_line_stride

// // #ifdef NVDLA_BATCH_ENABLE
// // #else

//     val cfg_mode_batch = false.B

// // #ifdef NVDLA_WINOGRAD_ENABLE
// // #else

//     val cfg_mode_winog = false.B

//     val cfg_di_int8 = io.reg2dp_proc_precision === 0.U
//     val cfg_di_int16 = io.reg2dp_proc_precision === 1.U
//     val cfg_do_int8 = io.reg2dp_out_precision === 0.U
//     val cfg_do_int16 = io.reg2dp_out_precision === 1.U

// // #ifdef NVDLA_SDP_DATA_TYPE_INT8TO16
// // #else

//     val cfg_mode_8to16 = false.B

//     val cfg_mode_norml = !(cfg_mode_batch | cfg_mode_winog | cfg_mode_8to16)
     
//     val cfg_mode_1x1_pack = (io.reg2dp_width === 0.U) & (io.reg2dp_height === 1.U)
//     val cfg_mode_1x1_nbatch = cfg_mode_1x1_pack & !cfg_mode_batch

//     val cfg_mode_eql = (io.reg2dp_ew_bypass === false.B) & 
//                         (io.reg2dp_ew_alu_bypass === false.B) &
//                         (io.reg2dp_ew_alu_algo === 3.U)
//     val cfg_mode_pdp = io.reg2dp_output_dst === true.B

//     val cfg_mode_quite = cfg_mode_eql | cfg_mode_pdp
//     val cfg_addr_en = !cfg_mode_quite

// //==============
// // Surf is always in unit of ATOMIC (1x1x32B)

//     val size_of_surf = Reg(UInt((13-conf.AM_AW).W))
//     when(cfg_di_int8){
//         size_of_surf := Cat(false.B, io.reg2dp_channel(12, conf.AM_AW))
//     }.elsewhen(cfg_di_int16){
//         size_of_surf := io.reg2dp_channel(12, conf.AM_AW2)
//     }.otherwise{
//         size_of_surf := io.reg2dp_channel(12, conf.AM_AW2)
//     }

// //=================================================
// // Cube Shape
// //=================================================

// // #ifdef NVDLA_WINOGRAD_ENABLE 
// // #else
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

// //==============
// // Width Count;
// //==============
// // Norml Mode
// // #ifndef NVDLA_SDP_BATCH_1ATOM   
// // #else

//     val odd = false.B

// // #ifdef NVDLA_SDP_DATA_TYPE_INT8TO16
// // #endif    
// // #ifdef NVDLA_WINOGRAD_ENABLE 
// // #endif
// // #ifdef NVDLA_BATCH_ENABLE
// // #endif

// //================================
// // SIZE of Trans
// //================================

// // #ifdef NVDLA_SDP_DATA_TYPE_INT8TO16
// // #endif
// // #ifdef NVDLA_WINOGRAD_ENABLE 
// // #endif
// // #ifdef NVDLA_BATCH_ENABLE
// // #endif
// // #ifdef NVDLA_WINOGRAD_ENABLE 
// // #endif

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

// // #ifdef NVDLA_SDP_DATA_TYPE_INT8TO16
// // #else

//     val is_ltrans = (count_w === size_of_width)
//     val is_ftrans = (count_w === 0.U)

//     is_last_w := is_ltrans

// // #ifdef NVDLA_SDP_DATA_TYPE_INT8TO16
// // //==============
// // // Element Count: for 8to16 only
// // //==============
// // #else    

//     is_last_e := true.B

// //==============
// // HEIGHT Count:
// //==============

// // #ifdef NVDLA_WINOGRAD_ENABLE 
// // #else

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

// //==============
// // CHANNEL Count
// //==============

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

// // #ifdef NVDLA_BATCH_ENABLE
// // //==============
// // // BATCH Count: 
// // //==============
// // #else

//     is_last_batch := true.B

// //==========================================
// // DMA Req : ADDR PREPARE
// //==========================================

// // #ifdef NVDLA_BATCH_ENABLE
// // #endif
// // #ifdef NVDLA_SDP_DATA_TYPE_INT8TO16
// // #endif
// // #ifdef NVDLA_WINOGRAD_ENABLE 
// // #endif


//     val base_addr_width = RegInit(0.U((conf.NVDLA_MEM_ADDRESS_WIDTH - conf.AM_AW).W))
//     val base_addr_surf = RegInit(0.U((conf.NVDLA_MEM_ADDRESS_WIDTH - conf.AM_AW).W))
//     val base_addr_line = RegInit(0.U((conf.NVDLA_MEM_ADDRESS_WIDTH - conf.AM_AW).W))

// // WIDTH

//     when(cfg_addr_en){
//         when(io.op_load){
//             base_addr_width := Cat(false.B, cfg_dst_addr)
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

// // LINE

//     when(cfg_addr_en){
//         when(io.op_load){
//             base_addr_line := Cat(false.B, cfg_dst_addr)
//         }.elsewhen(cmd_accept){
//             // #ifdef NVDLA_SDP_DATA_TYPE_INT8TO16
//             // #ifdef NVDLA_WINOGRAD_ENABLE 
//             // #ifdef NVDLA_BATCH_ENABLE 
//             when(is_surf_end){
//                 base_addr_line := base_addr_surf + cfg_dst_surf_stride
//             }.elsewhen(is_line_end){
//                 base_addr_line := base_addr_line + cfg_dst_line_stride            
//         }
//     }}

// // SURF

//     when(cfg_addr_en){
//         when(io.op_load){
//             base_addr_surf := Cat(false.B, cfg_dst_addr)
//         }.elsewhen(cmd_accept){
//             // #ifdef NVDLA_SDP_DATA_TYPE_INT8TO16
//             // #ifdef NVDLA_WINOGRAD_ENABLE 
//             // #ifdef NVDLA_BATCH_ENABLE 
//             when(is_surf_end){
//                 base_addr_surf := base_addr_surf + cfg_dst_surf_stride
//             }            
//         }
//     }

// //==========================================
// // DMA Req : SIZE
// //==========================================

//             // #ifdef NVDLA_SDP_DATA_TYPE_INT8TO16
//             // #ifdef NVDLA_WINOGRAD_ENABLE 
//             // #ifdef NVDLA_BATCH_ENABLE 
    
//     val dma_addr = base_addr_line

// //========================
// // Output: one for data write spt_; and one for data read dma_
// //========================
// // spt_size is to tell how many data from dp2wdma for a corresponding DMA req to MC/CF if
// // spt_size is in unit of cycle on dp2wdma

//     val mode_1x1_spt_size = Mux(
//                                 (cfg_do_int8 | cfg_di_int8), 
//                                 Cat(false.B, io.reg2dp_channel(12, conf.AM_AW)),
//                                 io.reg2dp_channel(12, conf.AM_AW2))
    
//     val mode_norml_spt_size = Cat(false.B, io.reg2dp_width)

//     val spt_size = Reg(UInt(14.W))
//     when(cfg_mode_1x1_nbatch){
//         spt_size := Cat(Fill(conf.AM_AW, false.B), mode_1x1_spt_size)
//     }.otherwise{
//         spt_size := mode_norml_spt_size
//     }

// //========================
// // Output: one for data write spt_; and one for data read dma_
// //========================

//     val mode_1x1_dma_size = size_of_surf

//     val mode_norml_dma_size = io.reg2dp_width

//     val dma_size = Reg(UInt(13.W))
//     when(cfg_mode_1x1_nbatch){
//         dma_size := Cat(Fill(conf.AM_AW2, false.B), mode_1x1_dma_size)
//     }.otherwise{
//         dma_size := mode_norml_dma_size
//     }

// //=================================================
// // OUTPUT FIFO: SPT & DMA channel
// //=================================================

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

//     val cmd_rdy = dma_fifo_prdy & spt_fifo_prdy
//     cmd_accept := cmd_vld & cmd_rdy

//     val spt_fifo_pd = Cat(odd, spt_size)

//     val dma_fifo_pd = Cat(is_cube_end, odd, dma_size, dma_addr)



// // NV_NVDLA_SDP_WDMA_CMD_sfifo u_sfifo (
// //    .nvdla_core_clk   (nvdla_core_clk)       
// //   ,.nvdla_core_rstn  (nvdla_core_rstn)      
// //   ,.spt_fifo_prdy    (spt_fifo_prdy)        
// //   ,.spt_fifo_pvld    (spt_fifo_pvld)        
// //   ,.spt_fifo_pd      (spt_fifo_pd[14:0])    
// //   ,.cmd2dat_spt_prdy (cmd2dat_spt_prdy)     
// //   ,.cmd2dat_spt_pvld (cmd2dat_spt_pvld)     
// //   ,.cmd2dat_spt_pd   (cmd2dat_spt_pd[14:0]) 
// //   ,.pwrbus_ram_pd    (pwrbus_ram_pd[31:0])  
// //   );

// // NV_NVDLA_SDP_WDMA_CMD_dfifo u_dfifo (      
// //    .nvdla_core_clk   (nvdla_core_clk)       
// //   ,.nvdla_core_rstn  (nvdla_core_rstn)      
// //   ,.dma_fifo_prdy    (dma_fifo_prdy)        
// //   ,.dma_fifo_pvld    (dma_fifo_pvld)        
// //   ,.dma_fifo_pd      (dma_fifo_pd[SDP_WR_CMD_DW+1:0])    
// //   ,.cmd2dat_dma_prdy (cmd2dat_dma_prdy)     
// //   ,.cmd2dat_dma_pvld (cmd2dat_dma_pvld)     
// //   ,.cmd2dat_dma_pd   (cmd2dat_dma_pd[SDP_WR_CMD_DW+1:0]) 
// //   ,.pwrbus_ram_pd    (pwrbus_ram_pd[31:0])  
// //   );


// }
// }

 
// object NV_NVDLA_SDP_WDMA_cmdDriver extends App {
//   implicit val conf: sdpConfiguration = new sdpConfiguration
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_WDMA_cmd())
// }


