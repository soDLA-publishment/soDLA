package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class cdp_dp_lut_ctrl_dp2lut_if extends Bundle{
    val x_info = Output(UInt(18.W))
    val x_entry = Output(UInt(10.W))
    val y_info = Output(UInt(18.W))
    val y_entry = Output(UInt(10.W))
}

class cdp_dp_lut_ctrl_reg2dp_lut_if extends Bundle{
    val le_function = Output(Bool())
    val le_index_offset = Output(UInt(8.W))
    val le_index_select = Output(UInt(8.W))
    val le_start_high = Output(UInt(6.W))
    val le_start_low = Output(UInt(32.W))
    val lo_index_select = Output(UInt(8.W))
    val lo_start_high = Output(UInt(6.W))
    val lo_start_low = Output(UInt(32.W))
}



class NV_NVDLA_CDP_DP_LUT_CTRL_unit(implicit val conf: nvdlaConfig) extends Module {
    val pINT8_BW = conf.NVDLA_BPE + 1   //int8 bitwidth after icvt
    val pPP_BW = (pINT8_BW + pINT8_BW) - 1 + 4  //(pINT8_BW * pINT8_BW) -1 is for int8 mode x^2, +4 is after 9 lrn

    val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())

        val sum2itp_pd = Flipped(DecoupledIO(UInt(pPP_BW.W)))
        val dp2lut = DecoupledIO(new cdp_dp_lut_ctrl_dp2lut_if)

        val reg2dp_lut = Flipped(new cdp_dp_lut_ctrl_reg2dp_lut_if)
        val reg2dp_sqsum_bypass = Input(Bool())
        
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
    ///////////////////////////////////////////////////
    //==============
    // Work Processing
    //==============
    val x_exp = RegInit(false.B)
    val y_shift_bits = RegInit(0.U(8.W))
    val sqsum_bypass_enable = RegInit(false.B)

    x_exp := (io.reg2dp_lut.le_function === "h0".asUInt(1.W))
    y_shift_bits := io.reg2dp_lut.lo_index_select
    sqsum_bypass_enable := (io.reg2dp_sqsum_bypass === "h1".asUInt(1.W))

    ///////////////////////////////////////
    val int_y_datin_prdy = Wire(Bool())
    val int_x_datin_prdy = Wire(Bool())

    io.sum2itp_pd.ready := int_y_datin_prdy & int_y_datin_prdy
    val datin_int8 = io.sum2itp_pd.bits

    ///////////////////////////////////////////////////////////////////////////////////////
    //int x y table input interlock
    val int_x_proc_in_vld = io.sum2itp_pd.valid & int_y_datin_prdy;
    val int_y_proc_in_vld = io.sum2itp_pd.valid & int_x_datin_prdy;

    //////////////////////////////////////////////////////////////////////
    // index calculation of x table
    //////////////////////////////////////////////////////////////////////
    //=================================================
    //offset minus
    //=================================================
    val reg2dp_x_offset = Cat(io.reg2dp_lut.le_start_high, io.reg2dp_lut.le_start_low)
    val load_in_intx = int_x_proc_in_vld & int_y_datin_prdy
    val dec_offset_datin_msb_f0 = Cat(false.B, datin_int8)
    val dec_offset_datin_msb_f1 = Cat(datin_int8(pPP_BW-1), datin_int8)
    val dec_offset_datin_msb = Mux(sqsum_bypass_enable, dec_offset_datin_msb_f1, dec_offset_datin_msb_f0)

    val less_than_win_s = MuxLookup(Cat(dec_offset_datin_msb(pPP_BW), reg2dp_x_offset(pPP_BW)),
        dec_offset_datin_msb < reg2dp_x_offset | dec_offset_datin_msb === reg2dp_x_offset,
        Array(  "b01".asUInt(2.W) -> false.B,
                "b10".asUInt(2.W) -> true.B)
        )
    
    val dec_offset_msb = RegInit(0.U((pPP_BW+1).W))
    val int_x_input_uflow_msb = RegInit(false.B)
    when(load_in_intx){
        when(less_than_win_s){
            dec_offset_msb := 0.U((pPP_BW+1).W)
            int_x_input_uflow_msb := true.B
        }.otherwise{
            dec_offset_msb := (dec_offset_datin_msb.asSInt - reg2dp_x_offset(pPP_BW,0).asSInt).asUInt
            int_x_input_uflow_msb := false.B
        }
    }

    val int_stage0_pvld = RegInit(false.B)
    val int_stage0_prdy = Wire(Bool())
    when(int_x_proc_in_vld){
        int_stage0_pvld := true.B
    }.elsewhen(int_stage0_prdy){
        int_stage0_pvld := false.B
    }

    int_x_datin_prdy := ~int_stage0_pvld | int_stage0_prdy
    
    val int_stage1_pvld = RegInit(false.B)
    val int_stage1_prdy = Wire(Bool())
    int_stage0_prdy := ~int_stage1_pvld | int_stage1_prdy

    val load_int_stage0 = int_stage0_pvld & int_stage0_prdy
    //===================================================================
    //log2 logic , bypassed when x is a linear table
    val log2_datin_msb = dec_offset_msb
    val log2_datin_vld = load_int_stage0  

    val log2_datout_msb = RegInit(0.U((pPP_BW+1).W))
    val log2_frac_msb = RegInit(0.U(pPP_BW.W))

    when(log2_datin_vld){
        when(int_x_input_uflow_msb){
            log2_datout_msb := 0.U
            log2_frac_msb := 0.U
        }.otherwise{
            when(x_exp){
                for(i <- 0 to pPP_BW){
                    //compare the msb
                    when((log2_datin_msb >> i.U) === 1.U){
                        log2_datout_msb := i.U((pPP_BW+1).W)
                        log2_frac_msb := (log2_datin_msb << i.U)(pPP_BW-1, 0)
                    }
                }
            }
            .otherwise{
                log2_datout_msb := log2_datin_msb
                log2_frac_msb := (pPP_BW-1).U
            }
        }
    }

    val x_exp_frac_msb = log2_frac_msb(pPP_BW-1, pPP_BW-16)

    val int_x_input_uflow_d = RegInit(false.B)
    when(log2_datin_vld){
        int_x_input_uflow_d := int_x_input_uflow_msb
    }

    val dat_info = Cat(int_x_input_uflow_d,x_exp_frac_msb)

    when(int_stage0_pvld){
        int_stage1_pvld := true.B
    }.elsewhen(int_stage1_prdy){
        int_stage1_pvld := false.B
    }

    val int_stage2_pvld = RegInit(false.B)
    val int_stage2_prdy = Wire(Bool())
    int_stage1_prdy := ~int_stage2_pvld | int_stage2_prdy
    //===================================================================
    //exp index offset , only valid for exponent table
    val reg2dp_x_index_offset = io.reg2dp_lut.le_index_offset
    val load_int_stage1 = int_stage1_pvld & int_stage1_prdy
    val int_stage2_in_vld = int_stage1_pvld
    val dec_xindex_datin_msb = log2_datout_msb

    val dec_xindex_msb = RegInit(0.U((pPP_BW+2).W))
    val int_x_index_uflow_msb = RegInit(false.B)
    when(load_int_stage1){
        when(dat_info(16)){
            dec_xindex_msb := 0.U
            int_x_index_uflow_msb := false.B
        }.elsewhen(x_exp){
            when((dec_xindex_datin_msb < Cat(Fill(pPP_BW-6, false.B), reg2dp_x_index_offset(6,0))) & (~reg2dp_x_index_offset(7))){
                dec_xindex_msb := 0.U
                int_x_index_uflow_msb := true.B
            }.otherwise{
                dec_xindex_msb := ((Cat(false.B, dec_xindex_datin_msb)).asSInt - (Cat(Fill(pPP_BW-6, reg2dp_x_index_offset(7)), reg2dp_x_index_offset)).asSInt).asUInt
                int_x_index_uflow_msb := false.B
            }
        }.otherwise{
            dec_xindex_msb := Cat(0.U(1.W), dec_xindex_datin_msb)
            int_x_index_uflow_msb := false.B
        }
    }

    val dat_info_d = RegInit(0.U(17.W))
    when(load_int_stage1 === true.B){
        dat_info_d := dat_info
    }
    val dat_info_index_sub = Cat((dat_info_d(16) | int_x_index_uflow_msb), dat_info_d(15,0))
    when(int_stage2_in_vld){
        int_stage2_pvld := true.B
    }.elsewhen(int_stage2_prdy){
        int_stage2_pvld := false.B
    }

    val int_stage3_pvld = RegInit(false.B)
    val int_stage3_prdy = Wire(Bool())
    int_stage2_prdy := ~int_stage3_pvld | int_stage3_prdy

    val load_int_stage2 = int_stage2_pvld & int_stage2_prdy
    //===================================================================
    //shift process for int8/int16, linear only, shift "0" when exponent x
    val shift_bits = Mux(x_exp, 0.U, io.reg2dp_lut.le_index_select)
//note for int16 should be: assign shift_bits_inv1[5:0] = ~shift_bits[5:0];
//note for int16 should be: assign shift_bits_int16_abs[6:0] = shift_bits[6]? (shift_bits_inv1[5:0]+1) : shift_bits[5:0];
//note for int16 should be: assign {shift_int16_s[63:0],   shift_int16_f[38:0]   } = shift_bits[6]? ({64'd0,dec_xindex_lsb[38:0]}<<shift_bits_int16_abs) : ({25'd0,dec_xindex_lsb[38:0],39'd0}>>shift_bits_int16_abs);
    val shift_bits_inv = ~shift_bits(4,0)
    val shift_bits_int8_abs = Mux(shift_bits(5), (shift_bits_inv +& 1.U), shift_bits(4,0))
    val shift_msb_int8_s_and_f= Mux(
        shift_bits(5), 
        (Cat(0.U(32.W), dec_xindex_msb) << shift_bits_int8_abs), 
        (Cat(0.U(9.W), dec_xindex_msb, 0.U(23.W)) >> shift_bits_int8_abs)
        )
    val shift_msb_int8_f = shift_msb_int8_s_and_f(pPP_BW+1, 0)
    val shift_msb_int8_s = shift_msb_int8_s_and_f(pPP_BW+33, pPP_BW+2)

//shift_int16...

    val shift_msb_int8 = RegInit(0.U(10.W))
    val x_int8_oflow_msb = RegInit(false.B)
    when(load_int_stage2){
        when(dat_info_index_sub(16)){
            shift_msb_int8 := 0.U
            x_int8_oflow_msb := false.B
        }.elsewhen(shift_bits(5)){
            when(Cat(shift_msb_int8_s, shift_msb_int8_f) >= 64.U){
                shift_msb_int8 := 64.U
                x_int8_oflow_msb := true.B
            }.otherwise{
                shift_msb_int8 := shift_msb_int8_f(9,0)
                x_int8_oflow_msb := false.B
            }
        }.otherwise{
            when(shift_msb_int8_s >= 64.U){
                shift_msb_int8 := 64.U
                x_int8_oflow_msb := true.B
            }.otherwise{
                shift_msb_int8 := shift_msb_int8_s(9,0)
                x_int8_oflow_msb := false.B
            }
        }
    }

    val x_oflow_int_msb = x_int8_oflow_msb;
    val x_index_msb = shift_msb_int8;

//x_lin_frac_int16...

    val x_lin_frac_int8_msb = RegInit(0.U(16.W))
    when(load_int_stage2){
        when(shift_bits(5)){
            x_lin_frac_int8_msb := 0.U
        }.otherwise{
            x_lin_frac_int8_msb := shift_msb_int8_f(pPP_BW+1, pPP_BW-14)
        }
    }

    val x_lin_frac_msb = x_lin_frac_int8_msb

    val dat_info_shift = RegInit(0.U(17.W))
    when(load_int_stage2){
        dat_info_shift := dat_info_index_sub
    }

    val x_frac_msb = Mux(x_exp, dat_info_shift(15,0), x_lin_frac_msb)
    val x_dat_info = Cat(x_oflow_int_msb, dat_info_shift(16), x_frac_msb)

    when(int_stage2_pvld){
        int_stage3_pvld := true.B
    }.elsewhen(int_stage3_prdy){
        int_stage3_pvld := false.B
    }

//assign int_stage3_prdy = ~int_stage4_pvld | int_stage4_prdy;

//assign load_int_stage3 = int_stage3_pvld & int_stage3_prdy;

//////////////////////////////////////////////////////////////////////
//index calculation of y table
//////////////////////////////////////////////////////////////////////

//==================================================
//input offset
//==================================================

    val reg2dp_y_offset = Cat(io.reg2dp_lut.lo_start_high, io.reg2dp_lut.lo_start_low)

    val load_din_inty = int_y_proc_in_vld & int_y_datin_prdy

    val y_less_than_win_s = MuxLookup(
        Cat(dec_offset_datin_msb(pPP_BW), reg2dp_y_offset(pPP_BW)),
        (dec_offset_datin_msb <= reg2dp_y_offset(pPP_BW,0)),
        Array(  "b01".asUInt(2.W) -> false.B,
                "b10".asUInt(2.W) -> true.B)        
    )
    
    val y_dec_offset_msb = RegInit(0.U((pPP_BW+1).W))
    val int_y_input_uflow_msb = RegInit(false.B)

    when(load_din_inty){
        when(y_less_than_win_s){
            y_dec_offset_msb := 0.U
            int_y_input_uflow_msb := true.B
        }.otherwise{
            y_dec_offset_msb := (dec_offset_datin_msb.asSInt - reg2dp_y_offset(pPP_BW,0).asSInt).asUInt
            int_y_input_uflow_msb := false.B
        }
    }

    val int_y_stage0_pvld = RegInit(false.B)
    val int_y_stage0_prdy = Wire(Bool())
    int_y_datin_prdy := ~int_y_stage0_pvld | int_y_stage0_prdy

    when(int_y_proc_in_vld){
        int_y_stage0_pvld := true.B
    }.elsewhen(int_y_stage0_prdy){
        int_y_stage0_pvld := false.B
    }

    val int_y_stage1_pvld = RegInit(false.B)
    val int_y_stage1_prdy = Wire(Bool())
    int_y_stage0_prdy := ~int_y_stage1_pvld | int_y_stage1_prdy

    val load_int_y_stage0 = int_y_stage0_pvld & int_y_stage0_prdy

//===================================================================
//shift process for y int8/int16, y is linear only
//===================================================================
    val dec_yindex_msb = y_dec_offset_msb
// note int16 should be this : assign y_shift_bits_inv1[5:0] = ~y_shift_bits[5:0];
// note int16 should be this : assign y_shift_bits_int16_abs = y_shift_bits[6]? (y_shift_bits_inv1[5:0]+1) : y_shift_bits[5:0];
// note int16 should be this : assign {y_shift_int16_s[63:0]   ,y_shift_int16_f[37:0]}    = y_shift_bits[6]? ({64'd0,dec_yindex_lsb[37:0]} << y_shift_bits_int16_abs) : ({26'd0,dec_yindex_lsb[37:0],38'd0} >> y_shift_bits_int16_abs);

    val y_shift_bits_inv  = ~y_shift_bits(4,0)
    val y_shift_bits_int8_abs  = Mux(y_shift_bits(5), (y_shift_bits_inv(4, 0) + 1.U), y_shift_bits(4, 0))
    val y_shift_msb_int8_s_and_f = Mux(
            y_shift_bits(5), 
            (Cat(Fill(32,false.B), dec_yindex_msb) << y_shift_bits_int8_abs ), 
            (Cat(Fill(10,false.B), dec_yindex_msb, Fill(22,false.B)) >> y_shift_bits_int8_abs)
    )
    val y_shift_msb_int8_f = y_shift_msb_int8_s_and_f(pPP_BW, 0)
    val y_shift_msb_int8_s = y_shift_msb_int8_s_and_f(33+pPP_BW, 1+pPP_BW)

//    y_shift_int16 ...

    val y_shift_msb_int8 = RegInit(0.U(10.W))
    val y_int8_oflow_msb = RegInit(false.B)
    
    when(load_int_y_stage0){
        when(int_y_input_uflow_msb){
            y_shift_msb_int8 := 0.U
            y_int8_oflow_msb := false.B
        }.elsewhen(y_shift_bits(5)){
            when(y_shift_msb_int8_s_and_f >= 256.U){
                y_shift_msb_int8 := 256.U
                y_int8_oflow_msb := true.B
            }.otherwise{
                y_shift_msb_int8 := y_shift_msb_int8_f(10, 0)
                y_int8_oflow_msb := false.B
            }
        }.otherwise{
            when(y_shift_msb_int8_s >= (256.U)){
                y_shift_msb_int8 := 256.U
                y_int8_oflow_msb := true.B
            }.otherwise{
                y_shift_msb_int8 := y_shift_msb_int8_s(10, 0)
                y_int8_oflow_msb := false.B
            }
        }
    }

    val y_oflow_int_msb = y_int8_oflow_msb
    val y_index_msb_f = y_shift_msb_int8

    // y_lin_frac_int16 ...

    val y_lin_frac_int8_msb = RegInit(0.U(16.W))
    when(load_int_y_stage0){
        when(y_shift_bits(5)){
            y_lin_frac_int8_msb := 0.U
        }.otherwise{
            y_lin_frac_int8_msb := y_shift_msb_int8_f(pPP_BW, pPP_BW-15)
        }
    }

    val y_lin_frac_msb = y_lin_frac_int8_msb

    val y_dat_info_shift = RegInit(false.B)
    when(load_int_y_stage0){
        y_dat_info_shift := int_y_input_uflow_msb
    }

    val y_dat_info_f = Cat(y_oflow_int_msb, y_dat_info_shift, y_lin_frac_msb)

    when(int_y_stage0_pvld){
        int_y_stage1_pvld := true.B
    }.elsewhen(int_y_stage1_prdy){
        int_y_stage1_pvld := false.B
    }

/////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////
//pipe delay to sync with x table

/////////////////////////////////////////////////////////////////////////
    val y_stage1_in_rdy = Wire(Bool())
    int_y_stage1_prdy := y_stage1_in_rdy
/////////////////////////////////////////////////////////////////////////
    val y_stage1_in_pd = Cat(y_dat_info_f, y_index_msb_f)
    val y_stage1_in_vld = int_y_stage1_pvld
/////////////////////////////////
    val y_stage2_in_rdy = Wire(Bool())
    val y_stage2_in_vld = RegInit(false.B)
    y_stage1_in_rdy := y_stage2_in_rdy || (~y_stage2_in_vld)

    when(y_stage1_in_vld){
        y_stage2_in_vld := true.B
    }.elsewhen(y_stage2_in_rdy){
        y_stage2_in_vld := false.B
    }

    val y_stage3_out_rdy = Wire(Bool())
    val y_stage3_out_vld = RegInit(false.B)
    y_stage2_in_rdy := y_stage3_out_rdy || (~y_stage3_out_vld)
    
    when(y_stage2_in_vld){
        y_stage3_out_vld := true.B
    }.elsewhen(y_stage3_out_rdy){
        y_stage3_out_vld := false.B
    }

    val y_stage2_in_pd = RegInit(0.U(28.W))
    when(y_stage1_in_vld & y_stage1_in_rdy){
        y_stage2_in_pd := y_stage1_in_pd
    }

    val y_stage3_out_pd = RegInit(0.U(28.W))
    when(y_stage2_in_vld & y_stage2_in_rdy){
        y_stage3_out_pd := y_stage2_in_pd
    }
/////////////////////////////////

    val y_index_msb = y_stage3_out_pd(9, 0)
    val y_dat_info = y_stage3_out_pd(27, 10)
    val y_int_stage3_pvld = y_stage3_out_vld
    val y_int_stage3_prdy = Wire(Bool())
    y_stage3_out_rdy := y_int_stage3_prdy
////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////
//int x y tables control output interlock
    val int_out_rdy = io.dp2lut.ready

    int_stage3_prdy := int_out_rdy & y_int_stage3_pvld
    y_int_stage3_prdy := int_out_rdy & int_stage3_pvld
    val int_out_vld = int_stage3_pvld & y_int_stage3_pvld

////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////
    io.dp2lut.valid := int_out_vld

    io.dp2lut.bits.x_entry := x_index_msb
    io.dp2lut.bits.x_info := x_dat_info

    io.dp2lut.bits.y_entry := y_index_msb
    io.dp2lut.bits.y_info := y_dat_info

}}


object NV_NVDLA_CDP_DP_LUT_CTRL_unitDriver extends App {
    implicit val conf: nvdlaConfig = new nvdlaConfig  
    chisel3.Driver.execute(args, () => new NV_NVDLA_CDP_DP_LUT_CTRL_unit())
}
