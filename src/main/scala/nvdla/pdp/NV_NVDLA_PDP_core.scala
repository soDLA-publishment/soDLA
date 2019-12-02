package nvdla

import chisel3._
import chisel3.util._

class NV_NVDLA_PDP_core(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())
        val pwrbus_ram_pd = Input(UInt(32.W))

        //pdp_rdma2dp
        val pdp_rdma2dp_pd = Flipped(DecoupledIO(UInt((conf.PDPBW+14).W)))
        //sdp2pdp
        val sdp2pdp_pd = Flipped(DecoupledIO(UInt((conf.NVDLA_PDP_ONFLY_INPUT_BW).W)))
        //pdp_dp2wdma
        val pdp_dp2wdma_pd = DecoupledIO(UInt((conf.NVDLA_PDP_THROUGHPUT*(conf.NVDLA_BPE)).W))

        //config 
        val datin_src_cfg = Input(Bool()) 
        val dp2reg_done = Input(Bool()) 
        val padding_h_cfg = Input(UInt(3.W))
        val padding_v_cfg = Input(UInt(3.W))
        val pooling_channel_cfg = Input(UInt(13.W))
        val pooling_fwidth_cfg = Input(UInt(10.W))
        val pooling_lwidth_cfg = Input(UInt(10.W))
        val pooling_mwidth_cfg = Input(UInt(10.W))
        val pooling_out_fwidth_cfg = Input(UInt(10.W))
        val pooling_out_lwidth_cfg = Input(UInt(10.W))
        val pooling_out_mwidth_cfg = Input(UInt(10.W))
        val pooling_size_h_cfg = Input(UInt(3.W))
        val pooling_size_v_cfg = Input(UInt(3.W))
        val pooling_splitw_num_cfg = Input(UInt(8.W))
        val pooling_stride_h_cfg = Input(UInt(4.W))
        val pooling_stride_v_cfg = Input(UInt(4.W))
        val pooling_type_cfg = Input(UInt(2.W))
        val reg2dp_cube_in_channel = Input(UInt(13.W))
        val reg2dp_cube_in_height = Input(UInt(13.W))
        val reg2dp_cube_in_width = Input(UInt(13.W))
        val reg2dp_cube_out_width = Input(UInt(13.W))
        val reg2dp_flying_mode = Input(Bool())
        val reg2dp_kernel_height = Input(UInt(3.W))
        val reg2dp_kernel_stride_width = Input(UInt(4.W))
        val reg2dp_kernel_width = Input(UInt(3.W))
        val reg2dp_op_en = Input(Bool())
        val reg2dp_pad_bottom_cfg = Input(UInt(3.W))
        val reg2dp_pad_left = Input(UInt(3.W))
        val reg2dp_pad_right = Input(UInt(3.W))
        val reg2dp_pad_right_cfg = Input(UInt(3.W))
        val reg2dp_pad_top = Input(UInt(3.W))
        val reg2dp_pad_value_1x_cfg = Input(UInt(19.W))
        val reg2dp_pad_value_2x_cfg = Input(UInt(19.W))
        val reg2dp_pad_value_3x_cfg = Input(UInt(19.W))
        val reg2dp_pad_value_4x_cfg = Input(UInt(19.W))
        val reg2dp_pad_value_5x_cfg = Input(UInt(19.W))
        val reg2dp_pad_value_6x_cfg = Input(UInt(19.W))
        val reg2dp_pad_value_7x_cfg = Input(UInt(19.W))
        val reg2dp_partial_width_out_first = Input(UInt(10.W))
        val reg2dp_partial_width_out_last = Input(UInt(10.W))
        val reg2dp_partial_width_out_mid = Input(UInt(10.W))
        val reg2dp_recip_height_cfg= Input(UInt(17.W))
        val reg2dp_recip_width_cfg= Input(UInt(17.W))
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
    //====================================================================
    //Instance--pooling 1D
    //
    //--------------------------------------------------------------------
    val u_preproc = Module(new NV_NVDLA_PDP_CORE_preproc)
    u_preproc.io.nvdla_core_clk := io.nvdla_core_clk
    u_preproc.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    u_preproc.io.sdp2pdp_pd <> io.sdp2pdp_pd

    u_preproc.io.reg2dp_cube_in_channel := io.reg2dp_cube_in_channel
    u_preproc.io.reg2dp_cube_in_height := io.reg2dp_cube_in_height
    u_preproc.io.reg2dp_cube_in_width := io.reg2dp_cube_in_width
    u_preproc.io.reg2dp_flying_mode := io.reg2dp_flying_mode
    u_preproc.io.reg2dp_op_en := io.reg2dp_op_en

    //====================================================================
    //Instance--pooling 1D
    //
    //--------------------------------------------------------------------
    val u_cal1d = Module(new NV_NVDLA_PDP_CORE_cal1d)
    u_cal1d.io.nvdla_core_clk := io.nvdla_core_clk
    u_cal1d.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    //pdp_rdma2dp
    u_cal1d.io.pdp_rdma2dp_pd <> io.pdp_rdma2dp_pd 
    //sdp2pdp
    u_cal1d.io.sdp2pdp_pd <> u_preproc.io.pre2cal1d_pd

    //config 
    u_cal1d.io.pooling_channel_cfg := io.pooling_channel_cfg
    u_cal1d.io.pooling_fwidth_cfg := io.pooling_fwidth_cfg
    u_cal1d.io.pooling_lwidth_cfg := io.pooling_lwidth_cfg
    u_cal1d.io.pooling_mwidth_cfg := io.pooling_mwidth_cfg
    u_cal1d.io.pooling_out_fwidth_cfg := io.pooling_out_fwidth_cfg
    u_cal1d.io.pooling_out_lwidth_cfg := io.pooling_out_lwidth_cfg
    u_cal1d.io.pooling_out_mwidth_cfg := io.pooling_out_mwidth_cfg
    u_cal1d.io.pooling_size_h_cfg := io.pooling_size_h_cfg
    u_cal1d.io.pooling_splitw_num_cfg := io.pooling_splitw_num_cfg 
    u_cal1d.io.pooling_stride_h_cfg := io.pooling_stride_h_cfg
    u_cal1d.io.pooling_type_cfg := io.pooling_type_cfg
    u_cal1d.io.reg2dp_cube_in_height := io.reg2dp_cube_in_height
    u_cal1d.io.reg2dp_cube_in_width := io.reg2dp_cube_in_width
    u_cal1d.io.reg2dp_cube_out_width := io.reg2dp_cube_out_width
    u_cal1d.io.reg2dp_kernel_stride_width := io.reg2dp_kernel_stride_width
    u_cal1d.io.reg2dp_kernel_width := io.reg2dp_kernel_width
    u_cal1d.io.reg2dp_op_en := io.reg2dp_op_en
    u_cal1d.io.reg2dp_pad_left := io.reg2dp_pad_left 
    u_cal1d.io.reg2dp_pad_right := io.reg2dp_pad_right 
    u_cal1d.io.reg2dp_pad_right_cfg := io.reg2dp_pad_right_cfg
    u_cal1d.io.reg2dp_pad_value_1x_cfg := io.reg2dp_pad_value_1x_cfg
    u_cal1d.io.reg2dp_pad_value_2x_cfg := io.reg2dp_pad_value_2x_cfg
    u_cal1d.io.reg2dp_pad_value_3x_cfg := io.reg2dp_pad_value_3x_cfg
    u_cal1d.io.reg2dp_pad_value_4x_cfg := io.reg2dp_pad_value_4x_cfg
    u_cal1d.io.reg2dp_pad_value_5x_cfg := io.reg2dp_pad_value_5x_cfg
    u_cal1d.io.reg2dp_pad_value_6x_cfg := io.reg2dp_pad_value_6x_cfg
    u_cal1d.io.reg2dp_pad_value_7x_cfg := io.reg2dp_pad_value_7x_cfg
    u_cal1d.io.dp2reg_done := io.dp2reg_done
    u_cal1d.io.datin_src_cfg := io.datin_src_cfg
    u_cal1d.io.padding_h_cfg := io.padding_h_cfg

    //====================================================================
    //Instanfce--pooling 2D
    //
    //--------------------------------------------------------------------
    val u_cal2d = Module(new NV_NVDLA_PDP_CORE_cal2d)
    u_cal2d.io.nvdla_core_clk := io.nvdla_core_clk
    u_cal2d.io.pwrbus_ram_pd := io.pwrbus_ram_pd

    //pdp_dp2wdma
    io.pdp_dp2wdma_pd <> u_cal2d.io.pdp_dp2wdma_pd
    //pooling
    u_cal2d.io.pooling1d_pd <> u_cal1d.io.pooling1d_pd

    //config 
    u_cal2d.io.padding_v_cfg := io.padding_v_cfg
    u_cal2d.io.pdp_op_start := u_cal1d.io.pdp_op_start
    u_cal2d.io.pooling_channel_cfg := io.pooling_channel_cfg
    u_cal2d.io.pooling_out_fwidth_cfg := io.pooling_out_fwidth_cfg
    u_cal2d.io.pooling_out_lwidth_cfg := io.pooling_out_lwidth_cfg
    u_cal2d.io.pooling_out_mwidth_cfg := io.pooling_out_mwidth_cfg
    u_cal2d.io.pooling_size_v_cfg := io.pooling_size_v_cfg
    u_cal2d.io.pooling_splitw_num_cfg := io.pooling_splitw_num_cfg
    u_cal2d.io.pooling_stride_v_cfg := io.pooling_stride_v_cfg
    u_cal2d.io.pooling_type_cfg := io.pooling_type_cfg
    u_cal2d.io.reg2dp_cube_in_height := io.reg2dp_cube_in_height
    u_cal2d.io.reg2dp_cube_out_width := io.reg2dp_cube_out_width
    u_cal2d.io.reg2dp_kernel_height := io.reg2dp_kernel_height
    u_cal2d.io.reg2dp_kernel_width := io.reg2dp_kernel_width
    u_cal2d.io.reg2dp_pad_bottom_cfg := io.reg2dp_pad_bottom_cfg
    u_cal2d.io.reg2dp_pad_top := io.reg2dp_pad_top
    u_cal2d.io.reg2dp_pad_value_1x_cfg := io.reg2dp_pad_value_1x_cfg
    u_cal2d.io.reg2dp_pad_value_2x_cfg := io.reg2dp_pad_value_2x_cfg
    u_cal2d.io.reg2dp_pad_value_3x_cfg := io.reg2dp_pad_value_3x_cfg
    u_cal2d.io.reg2dp_pad_value_4x_cfg := io.reg2dp_pad_value_4x_cfg
    u_cal2d.io.reg2dp_pad_value_5x_cfg := io.reg2dp_pad_value_5x_cfg
    u_cal2d.io.reg2dp_pad_value_6x_cfg := io.reg2dp_pad_value_6x_cfg
    u_cal2d.io.reg2dp_pad_value_7x_cfg := io.reg2dp_pad_value_7x_cfg
    u_cal2d.io.reg2dp_partial_width_out_first := io.reg2dp_partial_width_out_first
    u_cal2d.io.reg2dp_partial_width_out_last := io.reg2dp_partial_width_out_last
    u_cal2d.io.reg2dp_partial_width_out_mid := io.reg2dp_partial_width_out_mid
    u_cal2d.io.reg2dp_recip_height_cfg := io.reg2dp_recip_height_cfg
    u_cal2d.io.reg2dp_recip_width_cfg := io.reg2dp_recip_width_cfg


}}


object NV_NVDLA_PDP_coreDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_PDP_core())
}