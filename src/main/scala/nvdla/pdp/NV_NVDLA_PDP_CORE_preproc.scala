package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_PDP_CORE_preproc(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        // clk
        val nvdla_core_clk = Input(Clock())
        val pwrbus_ram_pd = Input(UInt(32.W))
        //sdp2pdp
        val sdp2pdp_pd = Flipped(DecoupledIO(UInt(conf.NVDLA_PDP_ONFLY_INPUT_BW.W)))
        //pre2cal1d
        val pre2cal1d_pd = DecoupledIO(UInt((conf.PDPBW+14).W))
        //config  
        val reg2dp_cube_in_channel = Input(UInt(13.W))
        val reg2dp_cube_in_height = Input(UInt(13.W))
        val reg2dp_cube_in_width = Input(UInt(13.W))
        val reg2dp_flying_mode = Input(Bool())
        val reg2dp_op_en = Output(Bool())

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
    /////////////////////////////////////////////////////////////////
    //Data path pre process
    //--------------------------------------------------------------
    val onfly_en = (io.reg2dp_flying_mode === false.B );
    //////////////////////////////
    //sdp to pdp layer end info
    //////////////////////////////
    val load_din = Wire(Bool())
    val sdp2pdp_c_end = Wire(Bool())
    val sdp2pdp_line_end = Wire(Bool())
    val sdp2pdp_surf_end = Wire(Bool())
    val sdp2pdp_cube_end = Wire(Bool())
    val sdp2pdp_c_cnt = RegInit("b0".asUInt(5.W))
    val sdp2pdp_width_cnt = RegInit("b0".asUInt(13.W))
    val sdp2pdp_height_cnt = RegInit("b0".asUInt(13.W))
    val sdp2pdp_surf_cnt = RegInit("b0".asUInt((13-conf.ATMMBW).W))
    
    when(load_din){
       when(sdp2pdp_c_end){
           sdp2pdp_c_cnt := 0.U
       }
       .otherwise{
           sdp2pdp_c_cnt := sdp2pdp_c_cnt + 1.U
       }
    }

    when(sdp2pdp_c_end){
        when(sdp2pdp_line_end){
            sdp2pdp_width_cnt := 0.U
        }
        .otherwise{
            sdp2pdp_width_cnt := sdp2pdp_width_cnt + 1.U
        }
    }

    when(sdp2pdp_line_end){
        when(sdp2pdp_surf_end){
            sdp2pdp_height_cnt := 0.U
        }
        .otherwise{
            sdp2pdp_height_cnt := sdp2pdp_height_cnt + 1.U
        }
    }

    when(sdp2pdp_surf_end){
        when(sdp2pdp_cube_end){
            sdp2pdp_surf_cnt := 0.U
        }
        .otherwise{
            sdp2pdp_surf_cnt := sdp2pdp_surf_cnt + 1.U
        }
    }

    sdp2pdp_c_end := (load_din & (sdp2pdp_c_cnt === (conf.NVDLA_MEMORY_ATOMIC_SIZE/conf.SDP_THROUGHPUT-1).U))
    sdp2pdp_line_end := sdp2pdp_c_end & (sdp2pdp_width_cnt === io.reg2dp_cube_in_width(12, 0))
    sdp2pdp_surf_end := sdp2pdp_line_end & (sdp2pdp_height_cnt === io.reg2dp_cube_in_height(12, 0))
    sdp2pdp_cube_end := sdp2pdp_surf_end & (sdp2pdp_height_cnt === io.reg2dp_cube_in_channel(12, conf.ATMMBW))

    //////////////////////////////////////////////////////////////////////
    //waiting for op_en
    //////////////////////////////////////////////////////////////////////
    val op_en_d1 = RegInit(false.B)
    op_en_d1 := io.reg2dp_op_en
    val op_en_load = io.reg2dp_op_en & (~op_en_d1);
    val layer_end = sdp2pdp_cube_end;

    val waiting_for_op_en = RegInit(true.B)
    when(layer_end & onfly_en){
        waiting_for_op_en := true.B
    }
    .elsewhen(op_en_load){
        when(~onfly_en){
            waiting_for_op_en := true.B
        }
        .elsewhen(onfly_en){
            waiting_for_op_en := false.B
        }
    }

    ///////////////////////////
    val sdp2pdp_en = (onfly_en & (~waiting_for_op_en));
    val pipe0_i = Cat(io.sdp2pdp_pd.bits, sdp2pdp_en)
    val sdp2pdp_ready_use = Wire(Bool())

    val is_pipe0 = Module{new NV_NVDLA_IS_pipe(conf.NVDLA_PDP_ONFLY_INPUT_BW + 1)}
    is_pipe0.io.clk := io.nvdla_core_clk
    is_pipe0.io.vi := io.sdp2pdp_pd.valid
    val sdp2pdp_ready_f = is_pipe0.io.ro
    is_pipe0.io.di := pipe0_i
    val sdp2pdp_valid_use_f = is_pipe0.io.vo
    is_pipe0.io.ri := sdp2pdp_ready_use
    val pipe0_o = is_pipe0.io.dout

    val sdp2pdp_pd_use = pipe0_o(conf.NVDLA_PDP_ONFLY_INPUT_BW, 1)
    val sdp2pdp_en_sync = pipe0_o(0)

    load_din := (io.sdp2pdp_pd.valid & sdp2pdp_ready_f & sdp2pdp_en)
    io.sdp2pdp_pd.ready := sdp2pdp_ready_f & sdp2pdp_en;
    val sdp2pdp_valid_use = sdp2pdp_valid_use_f & sdp2pdp_en_sync

    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////
    val pre2cal1d_data = Wire(UInt(conf.PDPBW.W))

    if(conf.SDP_THROUGHPUT > conf.NVDLA_PDP_THROUGHPUT){
        val k = conf.SDP_THROUGHPUT/conf.NVDLA_PDP_THROUGHPUT
        val selbw = log2Ceil(k)

        val ro_wr_vld = Wire(Vec(k, Bool()))
        val ro_wr_rdy = Wire(Vec(k, Bool()))
        val ro_wr_pd = VecInit((0 to k-1) 
                    map {i => sdp2pdp_pd_use(conf.PDPBW*i+conf.PDPBW-1, conf.PDPBW*i)})
        val ro_rd_vld = Wire(Vec(k, Bool()))
        val ro_rd_rdy = Wire(Vec(k, Bool()))
        val ro_rd_pd = Wire(Vec(k, UInt(conf.PDPBW.W)))
        val fifo_sel_cnt = RegInit("b0".asUInt(selbw.W))

        val u_ro_fifo = Array.fill(k){Module(new NV_NVDLA_fifo(
                               depth = 4,
                               width = conf.PDPBW,
                               ram_type = 0,
                               distant_wr_req = false))}
        for(i <- 0 to k-1){
            ro_wr_vld(i) := sdp2pdp_valid_use

            u_ro_fifo(i).io.clk := io.nvdla_core_clk
            u_ro_fifo(i).io.pwrbus_ram_pd := io.pwrbus_ram_pd

            u_ro_fifo(i).io.wr_pvld := ro_wr_vld(i)
            ro_wr_rdy(i) := u_ro_fifo(i).io.wr_prdy
            u_ro_fifo(i).io.wr_pd := ro_wr_pd(i)

            ro_rd_vld(i) := u_ro_fifo(i).io.rd_pvld
            u_ro_fifo(i).io.rd_prdy := ro_rd_rdy(i)
            ro_rd_pd(i) := u_ro_fifo(i).io.rd_pd

            ro_rd_rdy(i) := io.pre2cal1d_pd.ready & (fifo_sel_cnt === i.U)
        }
        val pre2cal1d_pvld_f = ro_rd_vld.asUInt.orR

        when(pre2cal1d_pvld_f){
            when(io.pre2cal1d_pd.ready){
                when(fifo_sel_cnt === (k - 1).U){
                    fifo_sel_cnt := 0.U
                }
                .otherwise{
                    fifo_sel_cnt := fifo_sel_cnt + 1.U
                }
            }
        }

        pre2cal1d_data := MuxLookup(fifo_sel_cnt, "b0".asUInt(conf.PDPBW.W),
                                         (0 to (k - 1)) map { i => i.U -> ro_rd_pd(i) })
        io.pre2cal1d_pd.valid := pre2cal1d_pvld_f
    }
    else if(conf.SDP_THROUGHPUT < conf.NVDLA_PDP_THROUGHPUT){
        val k = conf.NVDLA_PDP_THROUGHPUT/conf.SDP_THROUGHPUT
        val selbw = log2Ceil(k)
        val input_sel_cnt = RegInit("b0".asUInt(selbw.W))

        when(sdp2pdp_valid_use & sdp2pdp_ready_use){
            when(input_sel_cnt === (k-1).U){
                input_sel_cnt := 0.U
            }
            .otherwise{
                input_sel_cnt := input_sel_cnt + 1.U
            }
        }

        val sdp2pdp_dp = RegInit(VecInit(Seq.fill(k)("b0".asUInt(selbw.W))))
        for(i <- 0 to k-1){
            when((sdp2pdp_valid_use & sdp2pdp_ready_use)&(input_sel_cnt === i.U)){
                sdp2pdp_dp(i) := sdp2pdp_pd_use
            }    
        }

        val pre2cal1d_data_reg = RegInit("b0".asUInt(conf.PDPBW.W))

        when((sdp2pdp_valid_use & sdp2pdp_ready_use)&(input_sel_cnt === (k-1).U)){
            pre2cal1d_data_reg := sdp2pdp_dp.asUInt
        }

        val sdp2pdp_vld_f = RegInit(false.B)

        when((sdp2pdp_valid_use & sdp2pdp_ready_use)&(input_sel_cnt === (k-1).U)){
            sdp2pdp_vld_f := true.B
        }
        .elsewhen(io.pre2cal1d_pd.ready){
            sdp2pdp_vld_f := false.B
        }

        io.pre2cal1d_pd.valid := sdp2pdp_vld_f
        pre2cal1d_data := pre2cal1d_data_reg 
    }
    else if(conf.SDP_THROUGHPUT == conf.NVDLA_PDP_THROUGHPUT){
        sdp2pdp_ready_use := io.pre2cal1d_pd.ready
        io.pre2cal1d_pd.valid := sdp2pdp_valid_use
        pre2cal1d_data := sdp2pdp_pd_use
    }

    //==============================================================
    //Data info path pre process
    //--------------------------------------------------------------
    val pre2cal1d_load = io.pre2cal1d_pd.valid & io.pre2cal1d_pd.ready

    val last_c = Wire(Bool())
    //pos_c, 8B data position within 32B
    val pos_c = RegInit("b0".asUInt(5.W))
    when(pre2cal1d_load){
        when(last_c){
            pos_c := 0.U
        }
        .otherwise{
            pos_c := pos_c + 1.U
        }
    }

    last_c := pre2cal1d_load & (pos_c === (conf.BATCH_PDP_NUM - 1).U)

    //width direction
    val line_end = Wire(Bool())
    val w_cnt = RegInit("b0".asUInt(13.W))
    when(last_c){
        when(line_end){
            w_cnt := 0.U
        }
        .otherwise{
            w_cnt := w_cnt + 1.U
        }
    }

    line_end := last_c & (w_cnt === io.reg2dp_cube_in_width)

    //height direction
    val surf_end = Wire(Bool())
    val line_cnt = RegInit("b0".asUInt(13.W))
    when(line_end){
        when(surf_end){
            line_cnt := 0.U
        }
        .otherwise{
            line_cnt := line_cnt + 1.U
        }
    }

    surf_end := line_end & (w_cnt === io.reg2dp_cube_in_height)

    //surface/Channel direction
    val split_end = Wire(Bool())
    val surf_cnt = RegInit("b0".asUInt((13-conf.ATMMBW).W))
    when(surf_end){
        when(split_end){
            surf_cnt := 0.U
        }
        .otherwise{
            surf_cnt := surf_cnt + 1.U
        }
    }

    split_end := surf_end & (surf_cnt === io.reg2dp_cube_in_channel(12, conf.ATMMBW))
    val cube_end = split_end
    val b_sync = line_end

    val pre2cal1d_info = Cat(cube_end,split_end,surf_end,line_end,b_sync,pos_c, "b0".asUInt(4.W))
    io.pre2cal1d_pd.bits := Cat(pre2cal1d_info, pre2cal1d_data)





}}


object NV_NVDLA_PDP_CORE_preprocDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_PDP_CORE_preproc())
}