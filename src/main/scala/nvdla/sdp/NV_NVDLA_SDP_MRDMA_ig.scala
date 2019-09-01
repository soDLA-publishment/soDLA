// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// //NV_NVDLA_SDP_RDMA_ig.v

// class NV_NVDLA_SDP_MRDMA_ig(implicit conf: nvdlaConfig) extends Module {
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val dma_rd_req_vld = Output(Bool())
//         val dma_rd_req_rdy = Input(Bool())
//         val dma_rd_req_pd = Output(UInt(conf.NVDLA_DMA_RD_REQ.W))
//         val dma_rd_req_ram_type = Output(Bool())
        
//         val ig2cq_pvld = Output(Bool())
//         val ig2cq_prdy = Input(Bool())
//         val ig2cq_pd = Output(UInt(14.W))

//         val op_load = Input(Bool())

//         val reg2dp_src_ram_type = Input(Bool())
//         val reg2dp_batch_number = Input(UInt(5.W))
//         val reg2dp_channel = Input(UInt(13.W))
//         val reg2dp_height = Input(UInt(13.W))
//         val reg2dp_width = Input(UInt(13.W))
//         val reg2dp_in_precision  = Input(UInt(2.W))
//         val reg2dp_proc_precision = Input(UInt(2.W))
//         val reg2dp_src_base_addr_high = Input(UInt(32.W))
//         val reg2dp_src_base_addr_low = Input(UInt((32-conf.AM_AW).W))
//         val reg2dp_src_line_stride = Input(UInt((32-conf.AM_AW).W))
//         val reg2dp_src_surface_stride = Input(UInt((32-conf.AM_AW).W))
//         val reg2dp_perf_dma_en = Input(Bool())
//         val dp2reg_mrdma_stall = Output(UInt(32.W))

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
//     val cmd_accept = Wire(Bool())
//     val is_cube_end = Wire(Bool())
//     val cmd_process = RegInit(false.B)

//     val cmd_done = cmd_accept & is_cube_end
//     when(io.op_load){
//         cmd_process := true.B
//     }.elsewhen(cmd_done){
//         cmd_process := false.B
//     }

//     //==============
//     // Address catenate and offset calc
//     //==============
//     //==============
//     // CFG value calculation 
//     //==============
//     val cfg_base_addr = if(conf.NVDLA_MEM_ADDRESS_WIDTH > 32) 
//                         Cat(io.reg2dp_src_base_addr_high, io.reg2dp_src_base_addr_low)
//                         else 
//                         io.reg2dp_src_base_addr_low
//     val cfg_line_stride = io.reg2dp_src_line_stride
//     val cfg_surf_stride = io.reg2dp_src_surface_stride
//     val cfg_di_int8 = io.reg2dp_in_precision === 0.U
//     val cfg_di_int16 = io.reg2dp_in_precision === 1.U
//     val cfg_do_int8 = io.reg2dp_proc_precision === 1.U
//     val cfg_mode_1x1_pack = (io.reg2dp_width === 0.U) & (io.reg2dp_height === 0.U) 
//     val cfg_mode_multi_batch = io.reg2dp_batch_number =/= 0.U

//     //==============
//     // CHANNEL Direction
//     // calculate how many 32x8 blocks in channel direction
//     //==============
//     val size_of_surf = Wire(UInt((14-conf.AM_AW).W))

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
//     val is_last_b = Wire(Bool())
//     val is_last_h = Wire(Bool())
//     val is_last_c = Wire(Bool())
//     val is_batch_end = is_last_b
//     val is_elem_end  = is_batch_end
//     val is_line_end  = is_elem_end
//     val is_surf_end  = is_line_end  & ( (cfg_mode_1x1_pack) || (is_last_h) )
//     is_cube_end  := is_surf_end  & ( (cfg_mode_1x1_pack) || (is_last_c) )

//     //=================================================
//     // Batch Count:
//     //=================================================
//     is_last_b := true.B

//     //==============
//     // CHANNEL Count:
//     //==============
//     val count_c = RegInit("b0".asUInt((14-conf.AM_AW).W))
    
//     when(cmd_accept){
//         when(is_cube_end){
//             count_c := 0.U
//         }
//         .elsewhen(is_surf_end){
//             count_c := count_c + 1.U
//         }
//     }

//     is_last_c := (count_c===size_of_surf)

//     //==============
//     // LINE Count:
//     //==============
//     val count_h = RegInit("b0".asUInt(13.W))

//     when(cmd_accept){
//         when(is_surf_end){
//             count_h := 0.U
//         }.elsewhen(is_line_end){
//             count_h := count_h + 1.U
//         }
//     }

//     is_last_h := (count_h === io.reg2dp_height)

//     //==========================================
//     // DMA Req : ADDR PREPARE
//     //==========================================
//     // LINE
//     val base_addr_line = RegInit("b0".asUInt((conf.NVDLA_MEM_ADDRESS_WIDTH - conf.AM_AW).W))
//     val base_addr_surf = RegInit("b0".asUInt((conf.NVDLA_MEM_ADDRESS_WIDTH - conf.AM_AW).W))

//     when(io.op_load){
//         base_addr_line := cfg_base_addr
//     }
//     .elsewhen(cmd_accept){
//         when(is_surf_end){
//             base_addr_line := base_addr_surf + cfg_surf_stride
//         }.elsewhen(is_line_end){
//             base_addr_line := base_addr_line + cfg_line_stride
//         }
//     }

//     // SURF
//     when(io.op_load){
//         base_addr_surf := cfg_base_addr
//     }.elsewhen(cmd_accept){
//         when(is_surf_end){
//             base_addr_surf := base_addr_surf + cfg_surf_stride
//         }
//     }

//     //==========================================
//     // DMA Req : Addr : Generation
//     //==========================================
//     val req_addr = base_addr_line
//     val dma_req_addr = Cat(base_addr_line, "b0".asUInt(conf.AM_AW.W))
    
//     //==============
//     // DMA Req : SIZE : Generation
//     //==============
//     // in 1x1_pack mode, only send one request out 
//     val mode_1x1_req_size = size_of_surf

//     val dma_req_size = Wire(UInt(15.W))

//     when(cfg_mode_1x1_pack){
//         dma_req_size := Cat(Fill(conf.AM_AW+1, false.B), mode_1x1_req_size)
//     }.otherwise{
//         dma_req_size := Cat(Fill(2, false.B), io.reg2dp_width)
//     }

//     val ig2eg_size = dma_req_size
//     val ig2eg_cube_end = is_cube_end    

//     io.ig2cq_pd := Cat(ig2eg_cube_end, ig2eg_size)
//     io.ig2cq_pvld := cmd_process & io.dma_rd_req_rdy

//     //==============
//     // DMA Req : PIPE
//     //==============
//     // VALID: clamp when when cq is not ready
//     io.dma_rd_req_vld := cmd_process & io.ig2cq_prdy
//     io.dma_rd_req_pd := Cat(dma_req_size, dma_req_addr)
//     io.dma_rd_req_ram_type := io.reg2dp_src_ram_type

//     // Accept
//     cmd_accept := io.dma_rd_req_vld & io.dma_rd_req_rdy

//     //==============
//     // PERF STATISTIC
//     val mrdma_rd_stall_cnt_inc = io.dma_rd_req_vld & !io.dma_rd_req_rdy
//     val mrdma_rd_stall_cnt_clr = io.op_load
//     val mrdma_rd_stall_cnt_cen = RegInit(false.B)
//     mrdma_rd_stall_cnt_cen := io.reg2dp_perf_dma_en

//     val dp2reg_mrdma_stall_dec = false.B

//     // stl adv logic
//     val stl_adv = mrdma_rd_stall_cnt_inc ^ dp2reg_mrdma_stall_dec

//     val stl_cnt_cur = RegInit("b0".asUInt(32.W))
//     val stl_cnt_ext = Cat("b0".asUInt(2.W), stl_cnt_cur)
//     val stl_cnt_inc = stl_cnt_cur +& 1.U
//     val stl_cnt_dec = stl_cnt_cur -& 1.U
//     val stl_cnt_mod = Mux(mrdma_rd_stall_cnt_inc && !dp2reg_mrdma_stall_dec, stl_cnt_inc, 
//                       Mux(!mrdma_rd_stall_cnt_inc && dp2reg_mrdma_stall_dec, stl_cnt_dec, stl_cnt_ext))
//     val stl_cnt_new = Mux(stl_adv, stl_cnt_mod, stl_cnt_ext)
//     val stl_cnt_nxt = Mux(mrdma_rd_stall_cnt_clr, 0.U(34.W), stl_cnt_new)

//     when(mrdma_rd_stall_cnt_cen){
//         stl_cnt_cur := stl_cnt_nxt(31,0)
//     }

//     io.dp2reg_mrdma_stall := stl_cnt_cur

// }}


// object NV_NVDLA_SDP_MRDMA_igDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_MRDMA_ig)
// }




