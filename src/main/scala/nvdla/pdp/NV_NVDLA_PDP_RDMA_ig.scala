// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// //NV_NVDLA_PDP_RDMA_ig.v

// class NV_NVDLA_PDP_RDMA_ig(implicit conf: nvdlaConfig) extends Module {
//    val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())

//         val pdp2mcif_rd_req_pd = DecoupledIO(UInt(conf.NVDLA_PDP_MEM_RD_REQ.W))
//         val pdp2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(DecoupledIO(UInt(conf.NVDLA_PDP_MEM_RD_REQ.W))) else None
//         val ig2cq_pd = DecoupledIO(UInt(18.W))

//         val reg2dp_cube_in_channel = Input(UInt(13.W))
//         val reg2dp_cube_in_height = Input(UInt(13.W))
//         val reg2dp_cube_in_width = Input(UInt(13.W))
//         val reg2dp_dma_en = Input(Bool())
//         val reg2dp_kernel_stride_width = Input(UInt(4.W))
//         val reg2dp_kernel_width = Input(UInt(4.W))
//         val reg2dp_op_en = Input(Bool())
//         val reg2dp_partial_width_in_first = Input(UInt(10.W))
//         val reg2dp_partial_width_in_last = Input(UInt(10.W))
//         val reg2dp_partial_width_in_mid = Input(UInt(10.W))
//         val reg2dp_split_num = Input(UInt(8.W))
//         val reg2dp_src_base_addr_high = Input(UInt(32.W))
//         val reg2dp_src_base_addr_low = Input(UInt(32.W))
//         val reg2dp_src_line_strid = Input(UInt(32.W))
//         val reg2dp_src_ram_type = Input(Bool())
//         val reg2dp_src_surface_stride = Input(UInt(32.W))
//         val dp2reg_d0_perf_read_stall = Input(UInt(32.W))
//         val dp2reg_d1_perf_read_stall = Input(UInt(32.W))
//         val reg2dp_surf_stride = Input(UInt(32.W))
//         val eg2ig_done = Input(Bool())

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
//     // Work Processing
//     //==============
//     // one bubble between operation on two layers to let ARREG to switch to the next configration group

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
//     val cfg_width = io.reg2dp_cube_in_width +& 1.U
//     val cfg_fspt_width = io.reg2dp_partial_width_in_first
//     val cfg_mspt_width = io.reg2dp_partial_width_in_mid
//     val cfg_lspt_width = io.reg2dp_partial_width_in_last
//     val cfg_fspt_width_use = io.reg2dp_partial_width_in_first +& 1.U
//     val cfg_mspt_width_use = io.reg2dp_partial_width_in_mid +& 1.U
//     val cfg_lspt_width_use = io.reg2dp_partial_width_in_last +& 1.U
//     val cfg_mode_split = (io.reg2dp_split_num =/= 0.U) 
//     val cfg_split_num = io.reg2dp_split_num +& 1.U

//     //==============
//     // CHANNEL Direction
//     // calculate how many 32x8 blocks in channel direction
//     //==============
//     val number_of_block_in_c = io.reg2dp_cube_in_channel(12, log2Ceil(conf.NVDLA_MEMORY_ATOMIC_SIZE))

//     //==============
//     // WIDTH calculation
//     // Always has FTRAN with size 0~7
//     // then will LTRAN with size 0~7
//     // then will have MTEAN with fixed size 7
//     //==============
//     val is_fspt = Wire(Bool())
//     val is_lspt = Wire(Bool())
//     val width_stride = Wire(UInt(14.W))
//     when(io.cfg_mode_split){
//         when(is_fspt){
//             width_stride := io.cfg_fspt_width_use
//         }
//         .elsewhen(is_lspt){
//             width_stride := io.cfg_lspt_width_use
//         }
//         .otherwise{
//             width_stride := io.cfg_mspt_width_use
//         }
//     }
//     .otherwise{
//         width_stride := io.cfg_width
//     }

//     //==============
//     // ENDing of line/surf/split/cube
//     //==============
//     val is_last_h = Wire(Bool())
//     val is_line_end = true.B    //is_last_w;
//     val is_surf_end = is_line_end & is_last_h
//     val is_split_end = is_surf_end & is_last_c
//     val is_cube_end = Mux(io.cfg_mode_split, is_split_end & is_lspt, is_split_end)

//     //==============
//     // WGROUP Count: width group: number of window after split-w. equal to 1 in non-split-w mode
//     //==============
//     val count_wg = RegInit("b0".asUInt(10.W))
//     val wg_num = Wire(UInt(9.W))
//     when(cmd_accept & is_split_end & io.cfg_mode_split){
//         when(count_wg === wg_num -& 1.U){
//             count_wg := 0.U
//         }
//         .otherwise{
//             count_wg := count_wg + 1.U
//         }
//     }

//     wg_num := Mux(io.cfg_mode_split, io.cfg_split_num, 1.U)
//     is_fspt := cfg_mode_split & (count_wg === 0.U)
//     is_lspt := cfg_mode_split & (count_wg === (wg_num -& 1.U))

//     //==============
//     // CHANNEL Count: with inital value of total number in C direction, and will count-- when moving in chn direction
//     //==============
//     val count_c = RegInit("b0".asUInt((13-conf.ATMMBW).W))

//     when(cmd_accept){
//         when(is_split_end){
//             count_c := 0.U
//         }
//         .elsewhen(is_surf_end){
//             count_c := count_c + 1.U
//         }
//     }

//     is_last_c := (count_c === number_of_block_in_c)

//     //==============
//     // HEIGHT Count: move to next line after one line is done
//     //==============
//     val count_h = RegInit("b0".asUInt(13.W))

//     when(op_load){
//         count_h := 0.U
//     }
//     .elsewhen(cmd_accept){
//         when(is_surf_end){
//             count_h := 0.U
//         }
//         .elsewhen(is_line_end){
//             count_h := count_h + 1.U
//         }
//     }
    
//     is_last_h := (count_h === io.reg2dp_cube_in_height);

//     //==========================================
//     // DMA Req : ADDR
//     //==========================================
//     val reg2dp_base_addr = io.reg2dp_src_base_addr
//     val reg2dp_line_stride = io.reg2dp_src_line_stride
//     val reg2dp_surf_stride = io.reg2dp_src_surface_stride
//     val reg2dp_esurf_stride = io.reg2dp_src_surface_stride

//     //==============
//     // DMA Req : ADDR : Prepration
//     // DMA Req: go through the CUBE: W8->C->H
//     //==============
//     // ELEMENT
//     val base_addr_width = RegInit("b0".asUInt(64.W))
//     when(op_load){
//         base_addr_width := io.reg2dp_base_addr
//     }
//     .elsewhen(cmd_accept){
//         when(is_split_end & (~is_cube_end)){
//             when(is_fspt){
//                 when(io.reg2dp_kernel_width < io.reg2dp_kernel_stride_width){
//                     base_addr_width := base_addr_split + Cat(width_stride, "b0".asUInt((conf.ATMMBW).W))
//                 }
//             }
//         }
//     }




// }}


// object NV_NVDLA_SDP_RDMA_igDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_RDMA_ig)
// }




