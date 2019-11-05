package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_PDP_CORE_CAL2D_pad_v_and_div_kernel(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())

        val pout_mem_data = Input(Vec(conf.NVDLA_PDP_THROUGHPUT, UInt((conf.NVDLA_PDP_BWPE+6).W)))
        val pout_mem_size_v = Input(UInt(3.W))
        val pooling_size_v_cfg = Input(UInt(3.W))
        val average_pooling_en = Input(Bool())
        val reg2dp_kernel_width = Input(UInt(3.W))
        val rd_pout_data_stage0 = Input(Bool())
        val rd_pout_data_stage1 = Input(Bool())
        val reg2dp_recip_width_cfg = Input(UInt(17.W))
        val reg2dp_recip_height_cfg = Input(UInt(17.W))
        val reg2dp_pad_value_1x_cfg = Input(UInt(19.W))
        val reg2dp_pad_value_2x_cfg = Input(UInt(19.W))
        val reg2dp_pad_value_3x_cfg = Input(UInt(19.W))
        val reg2dp_pad_value_4x_cfg = Input(UInt(19.W))
        val reg2dp_pad_value_5x_cfg = Input(UInt(19.W))
        val reg2dp_pad_value_6x_cfg = Input(UInt(19.W))
        val reg2dp_pad_value_7x_cfg = Input(UInt(19.W))

        val pout_data = Output(Vec(conf.NVDLA_PDP_THROUGHPUT, UInt((conf.NVDLA_PDP_BWPE).W)))

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
    //===========================================================
    //adding pad value in v direction
    //-----------------------------------------------------------
    //padding value 1x,2x,3x,4x,5x,6x,7x table
    val pout_mem_size_v_use =  io.pout_mem_size_v
    val padding_here = io.average_pooling_en & (pout_mem_size_v_use =/= io.pooling_size_v_cfg)
    val pad_table_index = io.pooling_size_v_cfg - pout_mem_size_v_use

    val pad_table_out = MuxLookup(pad_table_index, "b0".asUInt(19.W),
                        Array(
                            1.U -> io.reg2dp_pad_value_1x_cfg,
                            2.U -> io.reg2dp_pad_value_2x_cfg,
                            3.U -> io.reg2dp_pad_value_3x_cfg,
                            4.U -> io.reg2dp_pad_value_4x_cfg,
                            5.U -> io.reg2dp_pad_value_5x_cfg,
                            6.U -> io.reg2dp_pad_value_6x_cfg,
                            7.U -> io.reg2dp_pad_value_7x_cfg,
                        ))
    
    val kernel_width_cfg = io.reg2dp_kernel_width +& 1.U

    val pad_value = (pad_table_out.asSInt * Cat(false.B, kernel_width_cfg).asSInt).asUInt

    val u_pad = Module(new NV_NVDLA_VEC_padder(vector_len = conf.NVDLA_PDP_THROUGHPUT, data_width = conf.NVDLA_BPE+7))
    u_pad.io.vec_in := io.pout_mem_data
    u_pad.io.pad_value := pad_value
    u_pad.io.padding := padding_here
    val data_8bit = u_pad.io.vec_out

    val pout_data_0 = RegInit(VecInit(Seq.fill(conf.NVDLA_PDP_THROUGHPUT)("b0".asUInt((conf.NVDLA_PDP_BWPE+6).W))))
    when(io.average_pooling_en){
        when(io.rd_pout_data_stage0){
            for(i <- 0 to conf.NVDLA_PDP_THROUGHPUT-1){
                pout_data_0(i) := data_8bit(i)
            }
        }
    }
    .elsewhen(io.rd_pout_data_stage0){
        for(i <- 0 to conf.NVDLA_PDP_THROUGHPUT-1){
            pout_data_0(i) := Cat(io.pout_mem_data(i)(conf.NVDLA_PDP_BWPE+5), io.pout_mem_data(i))
        }
    }

    //===========================================================
    //stage1: (* /kernel_width)
    //stage1 : calcate pooling data based on real pooling size --- (* 1/kernel_width)
    //-----------------------------------------------------------
    val reg2dp_recip_width_use = RegInit("b0".asUInt(17.W))
    reg2dp_recip_width_use := io.reg2dp_recip_width_cfg

    val reg2dp_recip_height_use = RegInit("b0".asUInt(17.W))
    reg2dp_recip_height_use := io.reg2dp_recip_height_cfg

    val u_div_kwidth = Module(new NV_NVDLA_VEC_DIV_kernel(vector_len = conf.NVDLA_PDP_THROUGHPUT, 
                             data_width = conf.NVDLA_PDP_BWPE+6))
    for(i <- 0 to conf.NVDLA_PDP_THROUGHPUT-1){
        u_div_kwidth.io.vec_in(i) := pout_data_0(i)
    }
    u_div_kwidth.io.reg2dp_recip_width_or_height_use := reg2dp_recip_width_use
    u_div_kwidth.io.average_pooling_en := io.average_pooling_en
    val data_mult_stage0 = u_div_kwidth.io.vec_out

    //load data to stage0
    val pout_data_stage0 = RegInit(VecInit(Seq.fill(conf.NVDLA_PDP_THROUGHPUT)("b0".asUInt((conf.NVDLA_PDP_BWPE+3).W))))
    when(io.average_pooling_en){
        when(io.rd_pout_data_stage1){
            pout_data_stage0 := data_mult_stage0
        }
    }
    .elsewhen(io.rd_pout_data_stage1){
        pout_data_stage0 := pout_data_0
    }

    //===========================================================
    //stage1: (* /kernel_height)
    val u_div_kheight = Module(new NV_NVDLA_VEC_DIV_kernel(vector_len = conf.NVDLA_PDP_THROUGHPUT, 
                             data_width = conf.NVDLA_PDP_BWPE+3))
    for(i <- 0 to conf.NVDLA_PDP_THROUGHPUT-1){
        u_div_kheight.io.vec_in(i) := pout_data_stage0(i)
    }
    u_div_kheight.io.reg2dp_recip_width_or_height_use := reg2dp_recip_height_use
    u_div_kheight.io.average_pooling_en := io.average_pooling_en
    val data_mult_stage1 = u_div_kheight.io.vec_out

    //load data to stage1
    val pout_data_stage1 = RegInit(VecInit(Seq.fill(conf.NVDLA_PDP_THROUGHPUT)("b0".asUInt((conf.NVDLA_PDP_BWPE).W))))
    when(io.average_pooling_en){
        when(io.rd_pout_data_stage1){
            pout_data_stage1 := data_mult_stage1
        }
    }
    .elsewhen(io.rd_pout_data_stage1){
        pout_data_stage1 := pout_data_stage0
    }
  
    for(i <- 0 to conf.NVDLA_PDP_THROUGHPUT-1){
        io.pout_data(i) := pout_data_stage1(i)
    }
    

 
}}


class NV_NVDLA_PDP_CORE_CAL2D_calculate_real_pooling_size(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())

        val wr_line_dat_done = Input(Bool())
        val mem_re_sel = Input(Vec(4, Bool()))
        val unit2d_set = Input(Vec(8, Bool()))
        val unit2d_en = Input(Vec(8, Bool()))

        val unit2d_vsize_cnt_d = Output(Vec(8, UInt(3.W)))

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

    val unit2d_vsize_cnt = RegInit(VecInit(Seq.fill(8)("b0".asUInt(3.W))))
    for(i <- 0 to 7){
        when(io.unit2d_set(i)){
            unit2d_vsize_cnt(i) := 0.U
        }
        .elsewhen(io.unit2d_en(i)&io.wr_line_dat_done){
            unit2d_vsize_cnt(i) := unit2d_vsize_cnt(i) + 1.U
        }
    }

    //line buffer number 1
    val unit2d_vsize0 = Wire(Vec(8, Bool()))
    for(i <- 0 to 7){
        unit2d_vsize0(i) := Mux(io.mem_re_sel(0), unit2d_vsize_cnt(0), 0.U)
    }

    //line buffer number 2
    val unit2d_vsize1 = Wire(Vec(8, Bool()))
    for(i <- 0 to 3){
        unit2d_vsize1(i) := Mux(io.mem_re_sel(1), unit2d_vsize_cnt(0), 0.U)
    }
    for(i <- 4 to 7){
        unit2d_vsize1(i) := Mux(io.mem_re_sel(1), unit2d_vsize_cnt(1), 0.U)
    }

    //line buffer number 3 4
    val unit2d_vsize2 = Wire(Vec(8, Bool()))
    for(i <- 0 to 1){
        unit2d_vsize2(i) := Mux(io.mem_re_sel(2), unit2d_vsize_cnt(0), 0.U)
    }
    for(i <- 2 to 3){
        unit2d_vsize2(i) := Mux(io.mem_re_sel(2), unit2d_vsize_cnt(1), 0.U)
    }
    for(i <- 4 to 5){
        unit2d_vsize2(i) := Mux(io.mem_re_sel(2), unit2d_vsize_cnt(2), 0.U)
    }
    for(i <- 6 to 7){
        unit2d_vsize2(i) := Mux(io.mem_re_sel(2), unit2d_vsize_cnt(3), 0.U)
    }

    //line buffer 5 6 7 8
    val unit2d_vsize3 = Wire(Vec(8, Bool()))
    for(i <- 0 to 7){
        unit2d_vsize3(i) := Mux(io.mem_re_sel(3), unit2d_vsize_cnt(i), 0.U)
    }


    val unit2d_vsize = Wire(Vec(8, Bool()))
    for(i <- 0 to 7){
        unit2d_vsize(i) := unit2d_vsize0(i)|unit2d_vsize1(i)|unit2d_vsize2(i)|unit2d_vsize3(i)
    }

    //one pipe
    val unit2d_vsize_cnt_d_reg = RegInit(VecInit(Seq.fill(8)("b0".asUInt(3.W))))
    for(i <- 0 to 7){
        unit2d_vsize_cnt_d_reg(i) := unit2d_vsize(i)
    }

    io.unit2d_vsize_cnt_d := unit2d_vsize_cnt_d_reg

 
}}


class NV_NVDLA_PDP_CORE_CAL2D_bank_merge_num(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        val pdp_op_start = Input(Bool())
        val pooling_size_v = Input(UInt(4.W))
        val pooling_stride_v_cfg = Input(UInt(4.W))
        val pooling_size_v_cfg = Input(UInt(3.W))

        val buffer_lines_num = Output(UInt(4.W))
        val bank_merge_num = Output(UInt(4.W))
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
    //maximum pooling output lines  need to be  buffer
    //stride 1
    val buffer_lines_0 = io.pooling_size_v  
    //stride 2
    val buffer_lines_1 = io.pooling_size_v(3, 1) +& io.pooling_size_v(0)
    //stride 3
    val buffer_lines_2 = Mux(5.U >= io.pooling_size_v_cfg, 2.U, 3.U)
    //stride 4 5 6 7
    val buffer_lines_3 = 2.U

    val pooling_stride_big =  (io.pooling_stride_v_cfg >= io.pooling_size_v_cfg)

    val buffer_lines_num_reg = RegInit("b0".asUInt(4.W))

    when(io.pdp_op_start){
        when(pooling_stride_big){
            buffer_lines_num_reg := 1.U
        }
        .otherwise{
            buffer_lines_num_reg := MuxLookup(io.pooling_stride_v_cfg, buffer_lines_3,
                                    Array(0.U -> buffer_lines_0,
                                          1.U -> buffer_lines_1,
                                          2.U -> buffer_lines_2))
        }
    }

    io.buffer_lines_num := buffer_lines_num_reg

    //memory bank merge num
    io.bank_merge_num := MuxLookup(buffer_lines_num_reg, 1.U,
                             Array(1.U -> 8.U,
                                   2.U -> 4.U,
                                   3.U -> 2.U,
                                   4.U -> 3.U))

 
}}

