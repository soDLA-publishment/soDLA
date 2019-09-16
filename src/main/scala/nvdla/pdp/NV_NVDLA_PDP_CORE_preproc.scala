// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_PDP_CORE_preproc(implicit val conf: nvdlaConfig) extends Module {
//     val io = IO(new Bundle {
//         // clk
//         val nvdla_core_clk = Input(Clock())
//         val pwrbus_ram_pd = Input(UInt(32.W))
//         //sdp2pdp
//         val sdp2pdp_pd = Flipped(DecoupledIO(UInt(((conf.NVDLA_PDP_BWPE*conf.NVDLA_PDP_THROUGHPUT)+14).W)))
//         //pre2cal1d
//         val pre2cal1d_pd = DecoupledIO(UInt(((conf.NVDLA_PDP_BWPE*conf.NVDLA_PDP_THROUGHPUT)+14).W))
//         //config  
//         val reg2dp_cube_in_channel = Input(UInt(13.W))
//         val reg2dp_cube_in_height = Input(UInt(13.W))
//         val reg2dp_cube_in_width = Input(UInt(13.W))
//         val reg2dp_flying_mode = Input(Bool())
//         val reg2dp_op_en = Output(Bool())

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
//     /////////////////////////////////////////////////////////////////
//     //Data path pre process
//     //--------------------------------------------------------------
//     val onfly_en = (io.reg2dp_flying_mode === false.B );
//     //////////////////////////////
//     //sdp to pdp layer end info
//     //////////////////////////////
//     val load_din = Wire(Bool())
//     val sdp2pdp_c_end = Wire(Bool())
//     val sdp2pdp_line_end = Wire(Bool())
//     val sdp2pdp_surf_end = Wire(Bool())
//     val sdp2pdp_cube_end = Wire(Bool())
//     val sdp2pdp_c_cnt = RegInit("b0".asUInt(5.W))
//     val sdp2pdp_width_cnt = RegInit("b0".asUInt(13.W))
//     val sdp2pdp_height_cnt = RegInit("b0".asUInt(13.W))
//     val sdp2pdp_surf_cnt = RegInit("b0".asUInt((13-conf.ATMMBW).W))
    
//     when(load_din){
//        when(sdp2pdp_c_end){
//            sdp2pdp_c_cnt := "b0".asUInt(5.W)
//        }
//        .otherwise{
//            sdp2pdp_c_cnt := sdp2pdp_c_cnt + 1.U
//        }
//     }

//     when(sdp2pdp_c_end){
//         when(sdp2pdp_line_end){
//             sdp2pdp_width_cnt := "b0".asUInt(13.W)
//         }
//         .otherwise{
//             sdp2pdp_width_cnt := sdp2pdp_width_cnt + 1.U
//         }
//     }

//     when(sdp2pdp_line_end){
//         when(sdp2pdp_surf_end){
//             sdp2pdp_height_cnt := 0.U
//         }
//         .otherwise{
//             sdp2pdp_height_cnt := sdp2pdp_height_cnt + 1.U
//         }
//     }

//     when(sdp2pdp_surf_end){
//         when(sdp2pdp_cube_end){
//             sdp2pdp_surf_cnt := 0.U
//         }
//         .otherwise{
//             sdp2pdp_surf_cnt := sdp2pdp_surf_cnt + 1.U
//         }
//     }

//     sdp2pdp_c_end := (load_din & (sdp2pdp_c_cnt === (conf.NVDLA_MEMORY_ATOMIC_SIZE/conf.SDP_THROUGHPUT-1).U))
//     sdp2pdp_line_end := sdp2pdp_c_end & (sdp2pdp_width_cnt === io.reg2dp_cube_in_width(12, 0))
//     sdp2pdp_surf_end := sdp2pdp_line_end & (sdp2pdp_height_cnt === io.reg2dp_cube_in_height(12, 0))
//     sdp2pdp_cube_end := sdp2pdp_surf_end & (sdp2pdp_height_cnt === io.reg2dp_cube_in_channel(12, conf.ATMMBW))

//     //////////////////////////////////////////////////////////////////////
//     //waiting for op_en
//     //////////////////////////////////////////////////////////////////////
//     val op_en_d1 = RegInit(false.B)

//     op_en_d1 := io.reg2dp_op_en

//     val op_en_load = io.reg2dp_op_en & (~op_en_d1);
//     val layer_end = sdp2pdp_cube_end;

//     val waiting_for_op_en = RegInit(true.B)
//     when(layer_end & onfly_en){
//         waiting_for_op_en := true.B
//     }
//     .elsewhen(op_en_load){
//         when(~onfly_en){
//             waiting_for_op_en := true.B
//         }
//         .elsewhen(onfly_en){
//             waiting_for_op_en := false.B
//         }
//     }

//     ///////////////////////////
//     val sdp2pdp_en = (onfly_en & (~waiting_for_op_en));
//     val pipe0_i = Cat(io.sdp2pdp_pd, sdp2pdp_en)
//     val sdp2pdp_ready_use = Wire(Bool())

//     val is_pipe0 = Module{new NV_NVDLA_IS_pipe(conf.NVDLA_PDP_ONFLY_INPUT_BW + 1)}
//     is_pipe0.io.clk := io.nvdla_core_clk
//     is_pipe0.io.vi := io.sdp2pdp_valid
//     val sdp2pdp_ready_f = is_pipe0.io.ro
//     is_pipe0.io.di := pipe0_i
//     val sdp2pdp_valid_use_f = is_pipe0.io.vo
//     is_pipe0.io.ri := sdp2pdp_ready_use
//     val pipe0_o = is_pipe0.io.dout

//     val sdp2pdp_pd_use = pipe0_o(conf.NVDLA_PDP_ONFLY_INPUT_BW, 1)
//     val sdp2pdp_en_sync = pipe0_o(0)

//     val load_din = (io.sdp2pdp_valid & sdp2pdp_ready_f & sdp2pdp_en)
//     io.sdp2pdp_ready := sdp2pdp_ready_f & sdp2pdp_en;
//     val sdp2pdp_valid_use = sdp2pdp_valid_use_f & sdp2pdp_en_sync

//     ////////////////////////////////////////////////////////////////
//     ////////////////////////////////////////////////////////////////
//     if(conf.)






// }}


// object NV_NVDLA_PDP_CORE_preprocDriver extends App {
//   implicit val conf: nvdlaConfig = new nvdlaConfig
//   chisel3.Driver.execute(args, () => new NV_NVDLA_PDP_CORE_preproc())
// }