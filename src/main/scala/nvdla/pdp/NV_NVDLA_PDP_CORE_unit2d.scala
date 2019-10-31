package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_PDP_CORE_CAL2D_pnum_flush(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())

        val unit2d_cnt_pooling = Input(UInt(3.W))
        val unit2d_cnt_pooling_max = Input(UInt(3.W))
        val last_line_in = Input(Bool())

        val pnum_flush = Output(Vec(7, Bool()))
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
    val unit2d_cnt_pooling_a = VecInit((0 to 7) 
                    map {i => io.unit2d_cnt_pooling +& i.U})
    val pnum_flush_reg = Reg(Vec(7, UInt(3.W)))
    //pooling No. in flush time
    when(io.last_line_in){
        when(unit2d_cnt_pooling_a(0) === io.unit2d_cnt_pooling_max){
            pnum_flush_reg(0) := 0.U
            pnum_flush_reg(1) := 1.U
            pnum_flush_reg(2) := 2.U
            pnum_flush_reg(3) := 3.U
            pnum_flush_reg(4) := 4.U
            pnum_flush_reg(5) := 5.U
            pnum_flush_reg(6) := 6.U
        }
        .elsewhen(unit2d_cnt_pooling_a(1) === io.unit2d_cnt_pooling_max){
            pnum_flush_reg(0) := io.unit2d_cnt_pooling_max
            pnum_flush_reg(1) := 0.U
            pnum_flush_reg(2) := 1.U
            pnum_flush_reg(3) := 2.U
            pnum_flush_reg(4) := 3.U
            pnum_flush_reg(5) := 4.U
            pnum_flush_reg(6) := 5.U
        }
        .elsewhen(unit2d_cnt_pooling_a(2) === io.unit2d_cnt_pooling_max){
            pnum_flush_reg(0) := io.unit2d_cnt_pooling +& 1.U
            pnum_flush_reg(1) := io.unit2d_cnt_pooling_max
            pnum_flush_reg(2) := 0.U
            pnum_flush_reg(3) := 1.U
            pnum_flush_reg(4) := 2.U
            pnum_flush_reg(5) := 3.U
            pnum_flush_reg(6) := 4.U
        }
        .elsewhen(unit2d_cnt_pooling_a(3) === io.unit2d_cnt_pooling_max){
            pnum_flush_reg(0) := io.unit2d_cnt_pooling +& 1.U
            pnum_flush_reg(1) := io.unit2d_cnt_pooling +& 2.U
            pnum_flush_reg(2) := io.unit2d_cnt_pooling_max
            pnum_flush_reg(3) := 0.U
            pnum_flush_reg(4) := 1.U
            pnum_flush_reg(5) := 2.U
            pnum_flush_reg(6) := 3.U
        }
        .elsewhen(unit2d_cnt_pooling_a(4) === io.unit2d_cnt_pooling_max){
            pnum_flush_reg(0) := io.unit2d_cnt_pooling +& 1.U
            pnum_flush_reg(1) := io.unit2d_cnt_pooling +& 2.U
            pnum_flush_reg(2) := io.unit2d_cnt_pooling +& 3.U
            pnum_flush_reg(3) := io.unit2d_cnt_pooling_max
            pnum_flush_reg(4) := 0.U
            pnum_flush_reg(5) := 1.U
            pnum_flush_reg(6) := 2.U
        }
        .elsewhen(unit2d_cnt_pooling_a(5) ===io. unit2d_cnt_pooling_max){
            pnum_flush_reg(0) := io.unit2d_cnt_pooling +& 1.U
            pnum_flush_reg(1) := io.unit2d_cnt_pooling +& 2.U
            pnum_flush_reg(2) := io.unit2d_cnt_pooling +& 3.U
            pnum_flush_reg(3) := io.unit2d_cnt_pooling +& 4.U
            pnum_flush_reg(4) := io.unit2d_cnt_pooling_max
            pnum_flush_reg(5) := 0.U
            pnum_flush_reg(6) := 1.U
        }
        .elsewhen(unit2d_cnt_pooling_a(6) === io.unit2d_cnt_pooling_max){
            pnum_flush_reg(0) := io.unit2d_cnt_pooling +& 1.U
            pnum_flush_reg(1) := io.unit2d_cnt_pooling +& 2.U
            pnum_flush_reg(2) := io.unit2d_cnt_pooling +& 3.U
            pnum_flush_reg(3) := io.unit2d_cnt_pooling +& 4.U
            pnum_flush_reg(4) := io.unit2d_cnt_pooling +& 5.U
            pnum_flush_reg(5) := io.unit2d_cnt_pooling_max
            pnum_flush_reg(6) := 0.U
        }
        .elsewhen(unit2d_cnt_pooling_a(7) === io.unit2d_cnt_pooling_max){
            pnum_flush_reg(0) := io.unit2d_cnt_pooling +& 1.U
            pnum_flush_reg(1) := io.unit2d_cnt_pooling +& 2.U
            pnum_flush_reg(2) := io.unit2d_cnt_pooling +& 3.U
            pnum_flush_reg(3) := io.unit2d_cnt_pooling +& 4.U
            pnum_flush_reg(4) := io.unit2d_cnt_pooling +& 5.U
            pnum_flush_reg(5) := io.unit2d_cnt_pooling +& 6.U
            pnum_flush_reg(6) := io.unit2d_cnt_pooling_max
        }

    }

    //assign output
    for(i <- 0 to 6){
        io.pnum_flush(i) := pnum_flush_reg(i)
    }

}}



class NV_NVDLA_PDP_CORE_CAL2D_pnum_updt(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())

        val padding_v_cfg = Input(UInt(3.W))
        val stride = Input(UInt(5.W))

        val up_pnum = Output(Vec(6, Bool()))
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

    //-------------------------
    //update pooling No. in line2 of next surface
    //-------------------------
    val up_pnum_reg = Reg(Vec(6, Bool()))
    up_pnum_reg(0) := 0.U
    when(io.padding_v_cfg === 0.U){
        up_pnum_reg(1) := 0.U
        up_pnum_reg(2) := 0.U
        up_pnum_reg(3) := 0.U
        up_pnum_reg(4) := 0.U
        up_pnum_reg(5) := 0.U
    }
    when(io.padding_v_cfg === 1.U){
        when(io.stride === 1.U){
            up_pnum_reg(1) := 1.U
            up_pnum_reg(2) := 0.U
            up_pnum_reg(3) := 0.U
            up_pnum_reg(4) := 0.U
            up_pnum_reg(5) := 0.U
        }
        .otherwise{
            up_pnum_reg(1) := 0.U
            up_pnum_reg(2) := 0.U
            up_pnum_reg(3) := 0.U
            up_pnum_reg(4) := 0.U
            up_pnum_reg(5) := 0.U            
        }
    }
    when(io.padding_v_cfg === 2.U){
        when(io.stride === 1.U){
            up_pnum_reg(1) := 1.U
            up_pnum_reg(2) := 2.U
            up_pnum_reg(3) := 0.U
            up_pnum_reg(4) := 0.U
            up_pnum_reg(5) := 0.U
        }
        .elsewhen(io.stride === 2.U){
            up_pnum_reg(1) := 1.U
            up_pnum_reg(2) := 0.U
            up_pnum_reg(3) := 0.U
            up_pnum_reg(4) := 0.U
            up_pnum_reg(5) := 0.U
        }
        .otherwise{
            up_pnum_reg(1) := 0.U
            up_pnum_reg(2) := 0.U
            up_pnum_reg(3) := 0.U
            up_pnum_reg(4) := 0.U
            up_pnum_reg(5) := 0.U            
        }
    }
    when(io.padding_v_cfg === 3.U){
        when(io.stride === 1.U){
            up_pnum_reg(1) := 1.U
            up_pnum_reg(2) := 2.U
            up_pnum_reg(3) := 3.U
            up_pnum_reg(4) := 0.U
            up_pnum_reg(5) := 0.U
        }
        .elsewhen((io.stride === 2.U) | (io.stride === 3.U)){
            up_pnum_reg(1) := 1.U
            up_pnum_reg(2) := 0.U
            up_pnum_reg(3) := 0.U
            up_pnum_reg(4) := 0.U
            up_pnum_reg(5) := 0.U
        }
        .otherwise{
            up_pnum_reg(1) := 0.U
            up_pnum_reg(2) := 0.U
            up_pnum_reg(3) := 0.U
            up_pnum_reg(4) := 0.U
            up_pnum_reg(5) := 0.U            
        }
    }
    when(io.padding_v_cfg === 4.U){
        when(io.stride === 1.U){
            up_pnum_reg(1) := 1.U
            up_pnum_reg(2) := 2.U
            up_pnum_reg(3) := 3.U
            up_pnum_reg(4) := 4.U
            up_pnum_reg(5) := 0.U
        }
        .elsewhen(io.stride === 2.U){
            up_pnum_reg(1) := 1.U
            up_pnum_reg(2) := 2.U
            up_pnum_reg(3) := 0.U
            up_pnum_reg(4) := 0.U
            up_pnum_reg(5) := 0.U
        }
        .elsewhen((io.stride === 3.U) | (io.stride === 4.U)){
            up_pnum_reg(1) := 1.U
            up_pnum_reg(2) := 0.U
            up_pnum_reg(3) := 0.U
            up_pnum_reg(4) := 0.U
            up_pnum_reg(5) := 0.U            
        }
        .otherwise{
            up_pnum_reg(1) := 0.U
            up_pnum_reg(2) := 0.U
            up_pnum_reg(3) := 0.U
            up_pnum_reg(4) := 0.U
            up_pnum_reg(5) := 0.U            
        }
    }
    when(io.padding_v_cfg === 5.U){
        when(io.stride === 1.U){
            up_pnum_reg(1) := 1.U
            up_pnum_reg(2) := 2.U
            up_pnum_reg(3) := 3.U
            up_pnum_reg(4) := 4.U
            up_pnum_reg(5) := 5.U
        }
        .elsewhen(io.stride === 2.U){
            up_pnum_reg(1) := 1.U
            up_pnum_reg(2) := 2.U
            up_pnum_reg(3) := 0.U
            up_pnum_reg(4) := 0.U
            up_pnum_reg(5) := 0.U
        }
        .elsewhen((io.stride === 3.U) | (io.stride === 4.U) | (io.stride === 5.U)){
            up_pnum_reg(1) := 1.U
            up_pnum_reg(2) := 0.U
            up_pnum_reg(3) := 0.U
            up_pnum_reg(4) := 0.U
            up_pnum_reg(5) := 0.U            
        }
        .otherwise{
            up_pnum_reg(1) := 0.U
            up_pnum_reg(2) := 0.U
            up_pnum_reg(3) := 0.U
            up_pnum_reg(4) := 0.U
            up_pnum_reg(5) := 0.U            
        }
    }

    for(i <- 0 to 5){
        io.up_pnum := up_pnum_reg(i)
    }

}}

class NV_NVDLA_PDP_CORE_CAL2D_bubble_control(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())

        val pdp_op_start = Input(Bool())
        val flush_num = Input(UInt(3.W))
        val first_out_num = Input(UInt(3.W))
        val up_pnum = Input(Vec(6, Bool()))

        val bubble_add = Output(UInt(3.W))

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

    val bubble_num = RegInit("b0".asUInt(3.W))
    when(io.pdp_op_start){
        when(io.flush_num >= io.first_out_num){
            bubble_num := io.flush_num -& io.first_out_num +& 1.U
        }
        .otherwise{
            bubble_num := 0.U
        }
    }

    val flush_in_next_surf = io.flush_num - bubble_num
    ///////////////
    val pnum_flush = Wire(Vec(7, UInt(3.W)))
    val next_pnum = Wire(Vec((0 to 7) map { i => Vec(i, UInt(3.W))}))
    //set next_pnum(0)(x) and  next_pnum(1)(x) to zero
    for(i <- 0 to 1){
        for(j <- 0 to i){
            next_pnum(i)(j) := 0.U
        }  
    }
    //set next_pnum(x)(x) to zero
    for(i <- 0 to 7){
        next_pnum(i)(i) := 0.U
    }

    //begin
    when(flush_in_next_surf === 2.U){
        when(bubble_num === 0.U){
            next_pnum(2)(1) := pnum_flush(1)
            next_pnum(2)(0) := pnum_flush(0)
        }
        .elsewhen(bubble_num === 1.U){
            next_pnum(2)(1) := pnum_flush(2)
            next_pnum(2)(0) := pnum_flush(1)           
        }
        .elsewhen(bubble_num === 2.U){
            next_pnum(2)(1) := pnum_flush(3)
            next_pnum(2)(0) := pnum_flush(2)           
        }
        .elsewhen(bubble_num === 3.U){
            next_pnum(2)(1) := pnum_flush(4)
            next_pnum(2)(0) := pnum_flush(3)           
        }
        .elsewhen(bubble_num === 4.U){
            next_pnum(2)(1) := pnum_flush(5)
            next_pnum(2)(0) := pnum_flush(4)           
        }
        .otherwise{
            next_pnum(2)(1) := pnum_flush(6)
            next_pnum(2)(0) := pnum_flush(5)           
        }
    }
    .otherwise{
        next_pnum(2)(1) := 0.U
        next_pnum(2)(0) := 0.U       
    }

    when(flush_in_next_surf === 3.U){
        when(bubble_num === 0.U){
            next_pnum(3)(2) := pnum_flush(2)
            next_pnum(3)(1) := pnum_flush(1)
            next_pnum(3)(0) := pnum_flush(0)
        }
        .elsewhen(bubble_num === 1.U){
            next_pnum(3)(2) := pnum_flush(3)
            next_pnum(3)(1) := pnum_flush(2)
            next_pnum(3)(0) := pnum_flush(1)       
        }
        .elsewhen(bubble_num === 2.U){
            next_pnum(3)(2) := pnum_flush(4)
            next_pnum(3)(1) := pnum_flush(3)
            next_pnum(3)(0) := pnum_flush(2)   
        }
        .elsewhen(bubble_num === 3.U){
            next_pnum(3)(2) := pnum_flush(5)
            next_pnum(3)(1) := pnum_flush(4)
            next_pnum(3)(0) := pnum_flush(3)           
        }
        .otherwise{
            next_pnum(3)(2) := pnum_flush(6)
            next_pnum(3)(1) := pnum_flush(5)
            next_pnum(3)(0) := pnum_flush(4)            
        }
    }
    .otherwise{
        next_pnum(3)(2) := 0.U
        next_pnum(3)(1) := 0.U
        next_pnum(3)(0) := 0.U     
    }

    when(flush_in_next_surf === 4.U){
        when(bubble_num === 0.U){
            next_pnum(4)(3) := pnum_flush(3)
            next_pnum(4)(2) := pnum_flush(2)
            next_pnum(4)(1) := pnum_flush(1)
            next_pnum(4)(0) := pnum_flush(0)
        }
        .elsewhen(bubble_num === 1.U){
            next_pnum(4)(3) := pnum_flush(4)
            next_pnum(4)(2) := pnum_flush(3)
            next_pnum(4)(1) := pnum_flush(2)
            next_pnum(4)(0) := pnum_flush(1)    
        }
        .elsewhen(bubble_num === 2.U){
            next_pnum(4)(3) := pnum_flush(5)
            next_pnum(4)(2) := pnum_flush(4)
            next_pnum(4)(1) := pnum_flush(3)
            next_pnum(4)(0) := pnum_flush(2) 
        }
        .otherwise{
            next_pnum(4)(3) := pnum_flush(6)
            next_pnum(4)(2) := pnum_flush(5)
            next_pnum(4)(1) := pnum_flush(4)
            next_pnum(4)(0) := pnum_flush(3)        
        }
    }
    .otherwise{
        next_pnum(4)(3) := 0.U
        next_pnum(4)(2) := 0.U
        next_pnum(4)(1) := 0.U
        next_pnum(4)(0) := 0.U  
    }

    when(flush_in_next_surf === 5.U){
        when(bubble_num === 0.U){
            next_pnum(5)(4) := pnum_flush(4)
            next_pnum(5)(3) := pnum_flush(3)
            next_pnum(5)(2) := pnum_flush(2)
            next_pnum(5)(1) := pnum_flush(1)
            next_pnum(5)(0) := pnum_flush(0)
        }
        .elsewhen(bubble_num === 1.U){
            next_pnum(5)(4) := pnum_flush(5)
            next_pnum(5)(3) := pnum_flush(4)
            next_pnum(5)(2) := pnum_flush(3)
            next_pnum(5)(1) := pnum_flush(2)
            next_pnum(5)(0) := pnum_flush(1) 
        }
        .otherwise{
            next_pnum(5)(4) := pnum_flush(6)
            next_pnum(5)(3) := pnum_flush(5)
            next_pnum(5)(2) := pnum_flush(4)
            next_pnum(5)(1) := pnum_flush(3)
            next_pnum(5)(0) := pnum_flush(2)    
        }
    }
    .otherwise{
        next_pnum(5)(4) := 0.U
        next_pnum(5)(3) := 0.U
        next_pnum(5)(2) := 0.U
        next_pnum(5)(1) := 0.U
        next_pnum(5)(0) := 0.U
    }

    when(flush_in_next_surf === 6.U){
        when(bubble_num === 0.U){
            next_pnum(6)(5) := pnum_flush(5)
            next_pnum(6)(4) := pnum_flush(4)
            next_pnum(6)(3) := pnum_flush(3)
            next_pnum(6)(2) := pnum_flush(2)
            next_pnum(6)(1) := pnum_flush(1)
            next_pnum(6)(0) := pnum_flush(0)
        }
        .otherwise{
            next_pnum(6)(5) := pnum_flush(6)
            next_pnum(6)(4) := pnum_flush(5)
            next_pnum(6)(3) := pnum_flush(4)
            next_pnum(6)(2) := pnum_flush(3)
            next_pnum(6)(1) := pnum_flush(2)
            next_pnum(6)(0) := pnum_flush(1)
        }
    }
    .otherwise{
        next_pnum(6)(5) := 0.U
        next_pnum(6)(4) := 0.U
        next_pnum(6)(3) := 0.U
        next_pnum(6)(2) := 0.U
        next_pnum(6)(1) := 0.U
        next_pnum(6)(0) := 0.U
    }

    when(flush_in_next_surf === 7.U){
        next_pnum(7)(6) := pnum_flush(6)
        next_pnum(7)(5) := pnum_flush(5)
        next_pnum(7)(4) := pnum_flush(4)
        next_pnum(7)(3) := pnum_flush(3)
        next_pnum(7)(2) := pnum_flush(2)
        next_pnum(7)(1) := pnum_flush(1)
        next_pnum(7)(0) := pnum_flush(0)
    }
    .otherwise{
        next_pnum(7)(6) := 0.U
        next_pnum(7)(5) := 0.U
        next_pnum(7)(4) := 0.U
        next_pnum(7)(3) := 0.U
        next_pnum(7)(2) := 0.U
        next_pnum(7)(1) := 0.U
        next_pnum(7)(0) := 0.U
    }

    val bubble_add_reg = RegInit("b0".asUInt(3.W))
    //default case
    bubble_add_reg := 0.U
    //begin
    for(i <- 2 to 7){
        when(flush_in_next_surf === i.U){
            for(j <- 0 to i-1){
                when(VecInit((0 to 5) map { k => (up_pnum(k) === next_pnum(i)(j))}).asUInt.orR){
                    bubble_add_reg := (j+1).U
                }     
            }   
        }
    }

    io.bubble_add := bubble_add_reg


}}


class NV_NVDLA_PDP_CORE_CAL2D_mem_rd(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())

        val load_din = Input(Bool())
        val wr_line_dat_done = Input(Bool())
        val unit2d_en = Input(Vec(8, Bool()))
        val unit2d_set = Input(Vec(8, Bool()))

        val buffer_lines_num = Input(UInt(4.W))
        val wr_sub_lbuf_cnt = Input(UInt(3.W))

        val mem_re = Output(Vec(4, Vec(8, Bool())))
        val mem_re_1st = Output(Vec(4, Vec(8, Bool())))

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
    val mem_re_sel = RegInit(VecInit(Seq.fill(4)(false.B)))
    mem_re_sel(0) := io.buffer_lines_num === 1.U
    mem_re_sel(1) := io.buffer_lines_num === 2.U
    mem_re_sel(2) := (io.buffer_lines_num === 3.U) | (io.buffer_lines_num === 4.U)
    mem_re_sel(3) := io.buffer_lines_num === 5.U

    //memory read
    //mem bank0 enable
    //

    //memory first read
    val unit2d_mem_1strd = RegInit(VecInit(Seq.fill(8)(false.B)))

    for(i <- 0 to 7){
        unit2d_mem_1strd(i) := Mux(io.unit2d_set(i), true.B, 
                               Mux(io.wr_line_dat_done, false.B,
                               unit2d_mem_1strd(i)))
    }

    //line buffer number 1
    for(i <- 0 to 7){
        io.mem_re(0)(i) := io.unit2d_en(0) & io.load_din & (io.wr_sub_lbuf_cnt === i.U) & mem_re_sel(0)
        io.mem_re_1st(0)(i) := unit2d_mem_1strd(0) & mem_re_sel(0)
    }

    //line buffer number 2
    //4 bank read enable
    for(i <- 0 to 3){
        io.mem_re(1)(i) := io.unit2d_en(0) & io.load_din & (io.wr_sub_lbuf_cnt === i.U) & mem_re_sel(1)
        io.mem_re_1st(1)(i) := unit2d_mem_1strd(0) & mem_re_sel(1)
    }
    for(i <- 4 to 7){
        io.mem_re(1)(i) := io.unit2d_en(1) & io.load_din & (io.wr_sub_lbuf_cnt === (i-4).U) & mem_re_sel(1)
        io.mem_re_1st(1)(i) := unit2d_mem_1strd(1) & mem_re_sel(1)
    }

    //line buffer number 3 4
    for(i <- 0 to 1){
        io.mem_re(2)(i) := io.unit2d_en(0) & io.load_din & (io.wr_sub_lbuf_cnt === i.U) & mem_re_sel(2)
        io.mem_re_1st(2)(i) := unit2d_mem_1strd(0) & mem_re_sel(2)
    }
    for(i <- 2 to 3){
        io.mem_re(2)(i) := io.unit2d_en(1) & io.load_din & (io.wr_sub_lbuf_cnt === (i-2).U) & mem_re_sel(2)
        io.mem_re_1st(2)(i) := unit2d_mem_1strd(1) & mem_re_sel(2)
    }
    for(i <- 4 to 5){
        io.mem_re(2)(i) := io.unit2d_en(2) & io.load_din & (io.wr_sub_lbuf_cnt === (i-4).U) & mem_re_sel(2)
        io.mem_re_1st(2)(i) := unit2d_mem_1strd(2) & mem_re_sel(2)
    }
    for(i <- 6 to 7){
        io.mem_re(2)(i) := io.unit2d_en(3) & io.load_din & (io.wr_sub_lbuf_cnt === (i-6).U) & mem_re_sel(2)
        io.mem_re_1st(2)(i) := unit2d_mem_1strd(3) & mem_re_sel(2)
    }

    //line buffer 5 6 7 8
    for(i <- 0 to 7){
        io.mem_re(3)(i) := io.unit2d_en(i) & io.load_din & (io.wr_sub_lbuf_cnt === 0.U) & mem_re_sel(3)
        io.mem_re_1st(3)(i) := unit2d_mem_1strd(i) & mem_re_sel(3)
    }

}}






