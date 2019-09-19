// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// //NV_NVDLA_SDP_RDMA_ig.v

// class NV_NVDLA_SDP_RDMA_ig(implicit conf: nvdlaConfig) extends Module {
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val dma_rd_req_pd = DecoupledIO(UInt(conf.NVDLA_DMA_RD_REQ.W))
//         val ig2cq_pd = DecoupledIO(UInt(16.W))

//         val op_load = Input(Bool())

//         val reg2dp_op_en = Input(Bool())
//         val reg2dp_winograd = Input(Bool())
//         val reg2dp_channel = Input(UInt(13.W))
//         val reg2dp_height = Input(UInt(13.W))
//         val reg2dp_width = Input(UInt(13.W))
//         val reg2dp_proc_precision = Input(UInt(2.W))
//         val reg2dp_rdma_data_mode = Input(Bool())
//         val reg2dp_rdma_data_size = Input(Bool())
//         val reg2dp_rdma_data_use = Input(UInt(2.W))
//         val reg2dp_base_addr_high = Input(UInt(32.W))
//         val reg2dp_base_addr_low = Input(UInt((32-conf.AM_AW).W))
//         val reg2dp_line_stride = Input(UInt((32-conf.AM_AW).W))
//         val reg2dp_surface_stride = Input(UInt((32-conf.AM_AW).W))
//         val reg2dp_perf_dma_en = Input(Bool())
//         val dp2reg_rdma_stall = Output(UInt(32.W))

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

//     //==============
//     // Reg Configure
//     //==============
//     // get the width of all regs
//     //==============
//     // Work Processing
//     //==============
//     val cmd_accept = Wire(Bool())
//     val is_cube_end = Wire(Bool())
//     val cmd_process = RegInit(false.B)

//     val op_done = cmd_accept & is_cube_end

//     when(io.op_load){
//         cmd_process := true.B
//     }.elsewhen(op_done){
//         cmd_process := false.B
//     }

//     //==============
//     // Address catenate and offset calc
//     //==============

//     val cfg_base_addr = if(conf.NVDLA_MEM_ADDRESS_WIDTH > 32) 
//                         Cat(io.reg2dp_base_addr_high, io.reg2dp_base_addr_low)
//                         else 
//                         io.reg2dp_base_addr_low
//     val cfg_surf_stride = io.reg2dp_surface_stride
//     val cfg_line_stride = io.reg2dp_line_stride
//     val cfg_data_size_1byte = io.reg2dp_rdma_data_size === 0.U
//     val cfg_data_use_both = io.reg2dp_rdma_data_use === 2.U
//     val cfg_data_mode_per_kernel = io.reg2dp_rdma_data_mode === 0.U

//     //==============
//     // WIDTH Direction
//     //==============

//     val cfg_proc_int8 = io.reg2dp_proc_precision === 0.U
//     val cfg_proc_int16 = io.reg2dp_proc_precision === 1.U
//     val cfg_mode_1x1_pack = (io.reg2dp_width === 0.U) & (io.reg2dp_height === 0.U)  

//     //=================================================
//     // Cube Shape
//     //=================================================
//     val is_last_h = Wire(Bool())
//     val is_last_c = Wire(Bool())

//     val is_line_end = true.B
//     val is_surf_end = cfg_mode_1x1_pack | cfg_data_mode_per_kernel | (is_line_end & is_last_h)
//     is_cube_end := cfg_mode_1x1_pack | cfg_data_mode_per_kernel | (is_surf_end & is_last_c)

//     //==============
//     // CHANNEL Count:
//     //==============
//     val size_of_surf = Wire(UInt((14-conf.AM_AW).W))
//     val count_c = RegInit(0.U)

//     when(cfg_proc_int8){
//         size_of_surf := Cat(false.B, io.reg2dp_channel(12, conf.AM_AW))
//     }.elsewhen(cfg_proc_int16){
//         size_of_surf := io.reg2dp_channel(12, conf.AM_AW2)
//     }.otherwise{
//         size_of_surf := io.reg2dp_channel(12, conf.AM_AW2)
//     }

//     when(cmd_accept){
//         when(is_cube_end){
//             count_c := 0.U
//         }.elsewhen(is_surf_end){
//             count_c := count_c + 1.U
//         }
//     }
//     is_last_c := (count_c === size_of_surf)

//     //==============
//     // HEIGHT Count:
//     //==============
//     val size_of_height = io.reg2dp_height
//     val count_h = RegInit(0.U)

//     when(cmd_accept){
//         when(is_surf_end){
//             count_h := 0.U
//         }.elsewhen(is_line_end){
//             count_h := count_h + 1.U
//         }
//     }

//     is_last_h := (count_h === size_of_height)

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
//     // DMA Req : Addr
//     //==========================================

//     val dma_req_addr = Cat(base_addr_line, "b0".asUInt(conf.AM_AW.W))

//     // Size_Of_Width: As each element is 1B or 2B, the width of cube will be resized accordingly
//     val size_of_width = Wire(UInt(15.W))

//     when(cfg_proc_int8){
//         when(cfg_data_use_both){
//             when(cfg_data_size_1byte){
//                 size_of_width := (io.reg2dp_width << 1.U) +& 1.U
//             }.otherwise{
//                 size_of_width := (io.reg2dp_width << 2.U) +& 3.U
//             }
//         }.otherwise{
//             when(cfg_data_size_1byte){
//                 size_of_width := io.reg2dp_width
//             }.otherwise{
//                 size_of_width := (io.reg2dp_width << 1.U) +& 1.U
//             }
//         }
//     }.otherwise{
//         when(cfg_data_use_both){
//             size_of_width := (io.reg2dp_width << 1.U) +& 1.U
//         }.otherwise{
//             size_of_width := io.reg2dp_width
//         }
//     }
    
//     //==========================================
//     // DMA Req : SIZE
//     //==========================================
//     // in 1x1_pack mode, only send one request out 
//     //assign mode_1x1_req_size = size_of_surf;
//     // PRECISION: 2byte both
//     //  8:1byte:single - 1B/elem -  32B/surf - 1 x surf
//     //  8:2byte:single - 2B/elem -  64B/surf - 2 x surf
//     //  8:1byte:both   - 2B/elem -  64B/surf - 2 x surf
//     //  8:2byte:both   - 4B/elem - 128B/surf - 4 x surf
//     // 16:2byte:single - 2B/elem -  32B/surf - 1 x surf
//     // 16:2byte:both   - 4B/elem -  64B/surf - 2 x surf
//     val size_of_straight = Wire(UInt(15.W))

//     when(cfg_proc_int8){
//         when(cfg_data_use_both){
//             when(cfg_data_size_1byte){
//                 size_of_straight := (size_of_surf << 1.U) +& 1.U
//             }.otherwise{
//                 size_of_straight := (size_of_surf << 2.U) +& 3.U
//             }
//         }.otherwise{
//              when(cfg_data_size_1byte){
//                 size_of_straight := (size_of_surf << 0.U) +& 0.U
//             }.otherwise{
//                 size_of_straight := (size_of_surf << 1.U) +& 1.U
//             }           
//         }
//     }.otherwise{
//         when(cfg_data_use_both){
//             when(cfg_data_size_1byte){
//                 size_of_straight := (size_of_surf << 1.U) +& 0.U   // illegal
//             }.otherwise{
//                 size_of_straight := (size_of_surf << 1.U) +& 1.U
//             }
//         }.otherwise{
//              when(cfg_data_size_1byte){
//                 size_of_straight := (size_of_surf << 1.U) +& 0.U   // illegal
//             }.otherwise{
//                 size_of_straight := (size_of_surf << 0.U) +& 0.U
//             }           
//         }        
//     }

//     //dma_req_size
//     val dma_req_size = Wire(UInt(15.W))

//     when(cfg_data_mode_per_kernel || cfg_mode_1x1_pack){
//         dma_req_size := size_of_straight
//     }.otherwise{
//         dma_req_size := size_of_width
//     }

//     //==========================================
//     // Context Queue Interface
//     // size,cube_end
//     //==========================================
//     val ig2eg_size = dma_req_size
//     val ig2eg_cube_end = is_cube_end    

//     // PKT_PACK_WIRE( sdp_brdma_ig2eg ,  ig2eg_ ,  ig2cq_pd )
//     io.ig2cq_pd := Cat(ig2eg_cube_end, ig2eg_size)
//     io.ig2cq_pvld := cmd_process & io.dma_rd_req_rdy

//     //==============
//     // DMA Req : PIPE
//     //==============
//     // VALID: clamp when when cq is not ready
//     io.dma_rd_req_pd.valid := cmd_process & io.ig2cq_prdy
//     io.dma_rd_req_pd.bits := Cat(dma_req_size, dma_req_addr)

//     // Accept
//     cmd_accept := io.dma_rd_req_pd.valid & io.dma_rd_req_pd.ready


//     //==============
//     // PERF STATISTIC

//     val rdma_stall_cnt_inc = io.dma_rd_req_pd.valid & !io.dma_rd_req_pd.ready
//     val rdma_stall_cnt_clr = io.op_load
//     val rdma_stall_cnt_cen = io.reg2dp_op_en & io.reg2dp_perf_dma_en

//     val dp2reg_rdma_stall_dec = false.B

//     // stl adv logic
//     val stl_adv = rdma_stall_cnt_inc ^ dp2reg_rdma_stall_dec

//     val stl_cnt_cur = RegInit("b0".asUInt(32.W))
//     val stl_cnt_ext = Cat("b0".asUInt(2.W), stl_cnt_cur)
//     val stl_cnt_inc = stl_cnt_cur +& 1.U
//     val stl_cnt_dec = stl_cnt_cur -& 1.U
//     val stl_cnt_mod = Mux(rdma_stall_cnt_inc && !dp2reg_rdma_stall_dec, stl_cnt_inc, 
//                       Mux(!rdma_stall_cnt_inc && dp2reg_rdma_stall_dec, stl_cnt_dec, stl_cnt_ext))
//     val stl_cnt_new = Mux(stl_adv, stl_cnt_mod, stl_cnt_ext)
//     val stl_cnt_nxt = Mux(rdma_stall_cnt_clr, 0.U(34.W), stl_cnt_new)

//     when(rdma_stall_cnt_cen){
//         stl_cnt_cur := stl_cnt_nxt(31,0)
//     }

//     io.dp2reg_rdma_stall := stl_cnt_cur

// }}


// object NV_NVDLA_SDP_RDMA_igDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_RDMA_ig)
// }




