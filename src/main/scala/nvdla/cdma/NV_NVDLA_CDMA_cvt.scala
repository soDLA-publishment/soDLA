package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver


class NV_NVDLA_CDMA_cvt(implicit conf: nvdlaConfig) extends Module {

    val io = IO(new Bundle {
        //nvdla core clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_ng_clk = Input(Clock())
        val nvdla_hls_clk = Input(Clock())

        //dc2cvt
        val dc2cvt_dat_wr_sel = if(conf.DMAIF<conf.ATMC) Some(Input(UInt(log2Ceil(conf.ATMC/conf.DMAIF).W))) else None
        val dc2cvt_dat_wr = Flipped(new nvdla_wr_if(17, conf.DMAIF))
        val dc2cvt_dat_wr_info_pd = Input(UInt(12.W))

        //img2cvt
        val img2cvt_dat_wr_sel = if(conf.DMAIF<conf.ATMC) Some(Input(UInt(log2Ceil(conf.ATMC/conf.DMAIF).W))) else None
        val img2cvt_dat_wr = Flipped(new nvdla_wr_if(17, conf.DMAIF))
        val img2cvt_mn_wr_data = Input(UInt((conf.BNUM*16).W))
        val img2cvt_dat_wr_pad_mask = Input(UInt((conf.BNUM).W))
        val img2cvt_dat_wr_info_pd = Input(UInt(12.W))

        //cdma2buf
        val cdma2buf_dat_wr_sel = if(conf.DMAIF<conf.ATMC) Some(Output(UInt((conf.ATMC/conf.DMAIF).W))) else None
        val cdma2buf_dat_wr = new nvdla_wr_if(17, conf.DMAIF)

        val slcg_hls_en = Output(Bool())
        
        val reg2dp_op_en = Input(Bool())
        val reg2dp_in_precision = Input(UInt(2.W))
        val reg2dp_proc_precision = Input(UInt(2.W))
        val reg2dp_cvt_en = Input(Bool())
        val reg2dp_cvt_truncate = Input(UInt(6.W))
        val reg2dp_cvt_offset = Input(UInt(16.W))
        val reg2dp_cvt_scale = Input(UInt(16.W))
        val reg2dp_nan_to_zero = Input(Bool())
        val reg2dp_pad_value = Input(UInt(16.W))
        val dp2reg_done = Input(Bool())

        val dp2reg_nan_data_num = Output(UInt(32.W))
        val dp2reg_inf_data_num = Output(UInt(32.W))
        val dp2reg_dat_flush_done = Output(Bool())

    })
//     .
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

////////////////////////////////////////////////////////////////////////
//  prepare input signals                                             //
////////////////////////////////////////////////////////////////////////
val op_en = RegInit(false.B)
val cfg_proc_precision = RegInit("b0".asUInt(2.W))
val cfg_scale = RegInit("b0".asUInt(16.W))
val cfg_truncate = RegInit("b0".asUInt(6.W))
val cfg_offset = RegInit("b0".asUInt(16.W))
val cfg_cvt_en = RegInit("b0".asUInt(6.W))
val cfg_in_int8 = RegInit(false.B)
val cfg_out_int8 = RegInit(false.B)
val cfg_pad_value = RegInit("b0".asUInt(16.W))
val is_input_int8 = RegInit(false.B)
val is_input_fp16 = RegInit(false.B)
val is_data_expand = RegInit(false.B)
val is_data_normal = RegInit(false.B)

val op_en_w = ~io.dp2reg_done & io.reg2dp_op_en
val cfg_reg_en = op_en_w & ~op_en
val is_input_int8_w = true.B
val is_input_fp16_w = false.B
val is_output_int8_w = true.B

val is_data_expand_w = is_input_int8_w & (~is_output_int8_w)
val is_data_normal_w = ~(is_input_int8_w ^ is_output_int8_w)
val cfg_pad_value_w = io.reg2dp_pad_value

op_en := op_en_w
when(cfg_reg_en){
    cfg_proc_precision := io.reg2dp_proc_precision
    cfg_scale := io.reg2dp_cvt_scale
    cfg_truncate := io.reg2dp_cvt_truncate
    cfg_offset := io.reg2dp_cvt_offset
    cfg_cvt_en := Fill(6, io.reg2dp_cvt_en)
    cfg_in_int8 := is_input_int8_w
    cfg_out_int8 := is_output_int8_w
    cfg_pad_value := cfg_pad_value_w
    is_input_int8 := is_input_int8_w
    is_input_fp16 := is_input_fp16_w
    is_data_expand := is_data_expand_w
    is_data_normal := is_data_normal_w
}

////////////////////////////////////////////////////////////////////////
//  SLCG control signal                                               //
////////////////////////////////////////////////////////////////////////

val slcg_hls_en_w = io.reg2dp_op_en & io.reg2dp_cvt_en
io.slcg_hls_en := ShiftRegister(slcg_hls_en_w, 3, false.B, true.B)

////////////////////////////////////////////////////////////////////////
//  Input signals                                                     //
////////////////////////////////////////////////////////////////////////
val cvt_wr_info_pd = (Fill(12, io.dc2cvt_dat_wr.addr.valid) & io.dc2cvt_dat_wr_info_pd)|
                     (Fill(12, io.img2cvt_dat_wr.addr.valid) & io.img2cvt_dat_wr_info_pd)

val cvt_wr_mask = cvt_wr_info_pd(3, 0)
val cvt_wr_mean = cvt_wr_info_pd(7)
val cvt_wr_uint = cvt_wr_info_pd(8)
val cvt_wr_sub_h = cvt_wr_info_pd(11, 9)

val cvt_wr_en = (io.dc2cvt_dat_wr.addr.valid | io.img2cvt_dat_wr.addr.valid)
val cvt_wr_sel = if(conf.DMAIF<conf.ATMC) 
                  Some(Mux(io.dc2cvt_dat_wr.addr.valid, io.dc2cvt_dat_wr_sel.get,
                       Mux(io.img2cvt_dat_wr.addr.valid, io.img2cvt_dat_wr_sel.get, 0.U)))
                 else None
val cvt_wr_pad_mask = Mux(io.img2cvt_dat_wr.addr.valid, io.img2cvt_dat_wr_pad_mask, 0.U)
val cvt_wr_addr = (Fill(17, io.dc2cvt_dat_wr.addr.valid)&io.dc2cvt_dat_wr.addr.bits) | 
                  (Fill(17, io.img2cvt_dat_wr.addr.valid)&io.img2cvt_dat_wr.addr.bits)
val cvt_wr_data = (Fill(conf.NVDLA_CDMA_DMAIF_BW, io.dc2cvt_dat_wr.addr.valid)&io.dc2cvt_dat_wr.data) | 
                  (Fill(conf.NVDLA_CDMA_DMAIF_BW, io.img2cvt_dat_wr.addr.valid)&io.img2cvt_dat_wr.data)
val cvt_wr_mean_data = io.img2cvt_mn_wr_data

////////////////////////////////////////////////////////////////////////
//  generator mux control signals                                     //
////////////////////////////////////////////////////////////////////////

val cvt_out_sel = if(conf.DMAIF<conf.ATMC) Some(cvt_wr_sel.get) else None
val cvt_out_reg_en = if(conf.DMAIF<conf.ATMC) Mux(cvt_wr_en, cvt_out_sel.get, 0.U) else false.B
val cvt_out_addr = cvt_wr_addr
val cvt_out_vld = cvt_wr_en
val cvt_cell_en = Mux(cvt_wr_en & cfg_cvt_en(0),  Fill(conf.NVDLA_MEMORY_ATOMIC_SIZE, cvt_wr_mask(0)), "b0".asUInt(conf.BNUM.W))

////////////////////////////////////////////////////////////////////////
//  One pipeline stage for retiming                                   //
////////////////////////////////////////////////////////////////////////
val cvt_wr_en_d1 = RegInit(false.B)
val cvt_wr_mean_d1 = RegInit(false.B)
val cvt_wr_uint_d1 = RegInit(false.B)
val cvt_wr_mean_data_d1 = Reg(UInt((conf.NVDLA_CDMA_DMAIF_BW/conf.NVDLA_BPE*16).W))
val cvt_wr_data_d1 = Reg(UInt(conf.NVDLA_CDMA_DMAIF_BW.W))
val cvt_cell_en_d1 = RegInit("b0".asUInt(conf.BNUM.W))
val cvt_out_vld_d1 = RegInit(false.B)
val cvt_out_pad_vld_d1 = RegInit(false.B)
val cvt_out_addr_d1 = RegInit("b0".asUInt(17.W))
val cvt_out_nz_mask_d1 = RegInit("b0".asUInt(4.W))
val cvt_out_pad_mask_d1 = RegInit("b0".asUInt(conf.BNUM.W))
val cvt_out_sel_d1 = if(conf.DMAIF<conf.ATMC) 
                     Some(RegInit("b0".asUInt(2.W)))
                     else None
val cvt_out_reg_en_d1 = RegInit("b0".asUInt(log2Ceil(conf.ATMC/conf.DMAIF).W))

cvt_wr_en_d1 :=  cvt_wr_en
when(cvt_wr_en){
    cvt_wr_mean_d1 := cvt_wr_mean
    cvt_wr_uint_d1 := cvt_wr_uint
    cvt_out_addr_d1 := cvt_out_addr
    cvt_out_nz_mask_d1 := cvt_wr_mask
    when(cvt_wr_mean){
        cvt_wr_mean_data_d1 := cvt_wr_mean_data
    }
    cvt_wr_data_d1 := cvt_wr_data
}
when(cvt_wr_en | cvt_wr_en_d1){
    cvt_cell_en_d1 := cvt_cell_en
}
cvt_out_vld_d1 := cvt_out_vld
cvt_out_pad_vld_d1 := io.img2cvt_dat_wr.addr.valid
when(io.img2cvt_dat_wr.addr.valid){
    cvt_out_pad_mask_d1 := cvt_wr_pad_mask
}
if(conf.DMAIF<conf.ATMC){
    when(cvt_wr_en){
        cvt_out_sel_d1.get := cvt_out_sel.get
    }
}
cvt_out_reg_en_d1 := cvt_out_reg_en

////////////////////////////////////////////////////////////////////////
//  generate input signals for convertor cells                        //
////////////////////////////////////////////////////////////////////////
val oprand_0_d0 = Reg(Vec(conf.BNUM, UInt(17.W)))
val oprand_1_d0 = Reg(Vec(conf.BNUM, UInt(16.W)))
val oprand_0_8b_sign = Wire(Vec(conf.BNUM, Bool()))
val oprand_0_ori = Wire(Vec(conf.BNUM, UInt(17.W)))
val oprand_1_ori = Wire(Vec(conf.BNUM, UInt(16.W)))

for(i <- 0 to conf.BNUM-1){
    oprand_0_8b_sign(i) := cvt_wr_data_d1((i+1)*conf.NVDLA_BPE-1) & ~cvt_wr_uint_d1
    oprand_0_ori(i) := Cat(Fill(17 - conf.NVDLA_BPE, oprand_0_8b_sign(i)), cvt_wr_data_d1((i+1)*conf.NVDLA_BPE-1, i*conf.NVDLA_BPE))
    oprand_1_ori(i) := Mux(cvt_wr_mean_d1, cvt_wr_mean_data_d1((i+1)*16-1, i*16), cfg_offset(15, 0))
    when(cvt_cell_en_d1(i)){
        oprand_0_d0(i) := oprand_0_ori(i)
        oprand_1_d0(i) := oprand_1_ori(i)
    }
}
////////////////////////////////////////////////////////////////////////
val op_en_d0 = RegInit(false.B)
val cell_en_d0 = RegInit("b0".asUInt(conf.BNUM.W))

op_en_d0 := cvt_wr_en_d1
when(cvt_wr_en_d1 | op_en_d0){
    cell_en_d0 := cvt_cell_en_d1
}

////////////////////////////////////////////////////////////////////////
//  instance of convert cells                                         //
////////////////////////////////////////////////////////////////////////
val cellout = Wire(Vec(conf.BNUM, UInt(16.W)))

val u_cell = Array.fill(conf.BNUM){Module(new NV_NVDLA_CDMA_CVT_cell)}
for(i <- 0 to conf.BNUM-1){
    u_cell(i).io.nvdla_core_clk := io.nvdla_hls_clk

    u_cell(i).io.chn_data_in_rsc.bits := oprand_0_d0(i)
    u_cell(i).io.chn_data_in_rsc.valid := cell_en_d0(i)

    u_cell(i).io.chn_alu_in_rsc.bits := oprand_1_d0(i)
    u_cell(i).io.chn_alu_in_rsc.valid := cell_en_d0(i)

    u_cell(i).io.cfg_mul_in_rsc := cfg_scale
    u_cell(i).io.cfg_out_precision := cfg_proc_precision
    u_cell(i).io.cfg_truncate := cfg_truncate

    u_cell(i).io.chn_data_out_rsc.ready := true.B
    cellout(i) := u_cell(i).io.chn_data_out_rsc.bits
}

val cvt_data_cell = VecInit((0 to conf.BNUM-1) map { i => cellout(i)(conf.NVDLA_BPE-1, 0)}).asUInt

////////////////////////////////////////////////////////////////////////
//  stage 2: pipeline to match latency of conver cells                //
////////////////////////////////////////////////////////////////////////

val cvt_out_vld_d1_d = Wire(Bool()) +: 
                    Seq.fill(conf.NVDLA_HLS_CDMA_CVT_LATENCY+1)(RegInit(false.B))
val cvt_out_pad_vld_d1_d = Wire(Bool()) +: 
                        Seq.fill(conf.NVDLA_HLS_CDMA_CVT_LATENCY+1)(RegInit(false.B))
val cvt_out_sel_d1_d = if (conf.DMAIF < conf.ATMC) Some(Wire(UInt(log2Ceil(conf.ATMC/conf.DMAIF).W)) +: 
                    Seq.fill(conf.NVDLA_HLS_CDMA_CVT_LATENCY+1)(RegInit("b0".asUInt(log2Ceil(conf.ATMC/conf.DMAIF).W))))
                    else None
val cvt_out_reg_en_d1_d = Wire(UInt(log2Ceil(conf.ATMC/conf.DMAIF).W)) +: 
                       Seq.fill(conf.NVDLA_HLS_CDMA_CVT_LATENCY+1)(RegInit("b0".asUInt(log2Ceil(conf.ATMC/conf.DMAIF).W)))
val cvt_out_addr_d1_d = Wire(UInt(17.W)) +: 
                     Seq.fill(conf.NVDLA_HLS_CDMA_CVT_LATENCY+1)(RegInit("b0".asUInt(17.W)))
val cvt_out_nz_mask_d1_d = Wire(UInt(4.W)) +: 
                        Seq.fill(conf.NVDLA_HLS_CDMA_CVT_LATENCY+1)(RegInit("b0".asUInt(4.W)))
val cvt_out_pad_mask_d1_d = Wire(UInt(conf.BNUM.W)) +: 
                         Seq.fill(conf.NVDLA_HLS_CDMA_CVT_LATENCY+1)(RegInit("b0".asUInt(conf.BNUM.W)))

cvt_out_vld_d1_d(0) := cvt_out_vld_d1
cvt_out_pad_vld_d1_d(0) := cvt_out_pad_vld_d1
if (conf.DMAIF < conf.ATMC){
    cvt_out_sel_d1_d.get(0) := cvt_out_sel_d1.get
}
cvt_out_reg_en_d1_d(0) := cvt_out_reg_en_d1
cvt_out_addr_d1_d(0) := cvt_out_addr_d1
cvt_out_nz_mask_d1_d(0) := cvt_out_nz_mask_d1
cvt_out_pad_mask_d1_d(0) := cvt_out_pad_mask_d1

for (t <- 0 to conf.NVDLA_HLS_CDMA_CVT_LATENCY){
    cvt_out_vld_d1_d(t+1) := cvt_out_vld_d1_d(t)
    cvt_out_pad_vld_d1_d(t+1) := cvt_out_pad_vld_d1_d(t)
    if (conf.DMAIF < conf.ATMC){
        when(cvt_out_vld_d1_d(t)){
            cvt_out_sel_d1_d.get(t+1) := cvt_out_sel_d1_d.get(t)
        }
    }
    when(cvt_out_vld_d1_d(t)|cvt_out_vld_d1_d(1)){
        cvt_out_reg_en_d1_d(t+1) := cvt_out_reg_en_d1_d(t)
    }
    cvt_out_addr_d1_d(t+1) := cvt_out_addr_d1_d(t)
    cvt_out_nz_mask_d1_d(t+1) := cvt_out_nz_mask_d1_d(t)
    cvt_out_pad_mask_d1_d(t+1) := cvt_out_pad_mask_d1_d(t)
}

val cvt_out_sel_bp = if (conf.DMAIF < conf.ATMC) 
                     Some(Mux(cfg_cvt_en(1), cvt_out_sel_d1_d.get(conf.NVDLA_HLS_CDMA_CVT_LATENCY + 1), cvt_out_sel_d1.get)) 
                     else None
val cvt_out_vld_bp = Mux(cfg_cvt_en(1), cvt_out_pad_vld_d1_d(conf.NVDLA_HLS_CDMA_CVT_LATENCY+1), cvt_out_vld_d1)
val cvt_out_addr_bp = Mux(cfg_cvt_en(1), cvt_out_addr_d1_d(conf.NVDLA_HLS_CDMA_CVT_LATENCY+1), cvt_out_addr_d1)
val cvt_out_nz_mask_bp = Mux(cfg_cvt_en(2), cvt_out_nz_mask_d1_d(conf.NVDLA_HLS_CDMA_CVT_LATENCY+1), cvt_out_nz_mask_d1)
val cvt_out_pad_vld_bp = Mux(cfg_cvt_en(3), cvt_out_pad_vld_d1_d(conf.NVDLA_HLS_CDMA_CVT_LATENCY+1), cvt_out_pad_vld_d1)
val cvt_out_pad_mask_bp = Mux(~cvt_out_pad_vld_bp, "b0".asUInt(conf.BNUM.W), 
                          Mux(cfg_cvt_en(3), cvt_out_pad_mask_d1_d(conf.NVDLA_HLS_CDMA_CVT_LATENCY + 1),
                          cvt_out_pad_mask_d1))
val cvt_out_reg_en_bp = Mux(cfg_cvt_en(4), cvt_out_reg_en_d1_d(conf.NVDLA_HLS_CDMA_CVT_LATENCY+1), cvt_out_reg_en_d1)

////////////////////////////////////////////////////////////////////////
//  stage 3: final pipeline stage                                    //
////////////////////////////////////////////////////////////////////////
val dat_cbuf_flush_vld_w = Wire(Bool())
val dat_cbuf_flush_idx = withClock(io.nvdla_core_ng_clk){RegInit("b0".asUInt(18.W))}

val cvt_out_data_mix = Mux(cfg_cvt_en(5), cvt_data_cell, cvt_wr_data_d1)
val cvt_out_data_masked = VecInit((0 to conf.BNUM-1) map 
{ i => Mux(cvt_out_pad_mask_bp(i), cfg_pad_value(conf.NVDLA_BPE-1, 0), cvt_out_data_mix((i+1)*conf.NVDLA_BPE-1, i*conf.NVDLA_BPE))}).asUInt
val cvt_out_data_p0 = Mux(cvt_out_nz_mask_bp(0), cvt_out_data_masked(conf.ATMM-1, 0), 0.U)
val cvt_out_data_p0_reg = RegNext(cvt_out_data_p0, "b0".asUInt((conf.ATMM).W))
val cvt_out_vld_reg_w = cvt_out_vld_bp | dat_cbuf_flush_vld_w;
val cvt_out_addr_reg_w = Mux(dat_cbuf_flush_vld_w, dat_cbuf_flush_idx(16+log2Ceil(conf.ATMC/conf.DMAIF), log2Ceil(conf.ATMC/conf.DMAIF)), cvt_out_addr_bp)
val cvt_out_sel_reg_w = if (conf.DMAIF < conf.ATMC) 
                        Some(Mux(dat_cbuf_flush_vld_w, Cat(dat_cbuf_flush_idx(0), ~dat_cbuf_flush_idx(0)), 
                        Cat(cvt_out_sel_bp.get(0), ~cvt_out_sel_bp.get(0)))) 
                        else None
val cvt_out_sel_reg = if (conf.DMAIF < conf.ATMC) 
                      Some(withClock(io.nvdla_core_ng_clk){RegNext(cvt_out_sel_reg_w.get, "b0".asUInt((conf.ATMC/conf.DMAIF).W))})
                      else None
//================  Non-SLCG clock domain ================//
val cvt_out_vld_reg = withClock(io.nvdla_core_ng_clk){RegNext(cvt_out_vld_reg_w, false.B)}
val cvt_out_addr_reg = withClock(io.nvdla_core_ng_clk){RegEnable(cvt_out_addr_reg_w, "b0".asUInt(17.W), cvt_out_vld_reg_w)}

////////////////////////////////////////////////////////////////////////
//  Data buffer flush logic                                           //
////////////////////////////////////////////////////////////////////////

val dat_cbuf_flush_idx_w = dat_cbuf_flush_idx + 1.U
dat_cbuf_flush_vld_w := ~dat_cbuf_flush_idx(log2Ceil(conf.NVDLA_CBUF_BANK_NUMBER * conf.NVDLA_CBUF_BANK_DEPTH) + log2Ceil(conf.ATMC/conf.DMAIF) - 1)//max value = half bank entry * 2^$k
io.dp2reg_dat_flush_done := dat_cbuf_flush_idx(log2Ceil(conf.NVDLA_CBUF_BANK_NUMBER * conf.NVDLA_CBUF_BANK_DEPTH) + log2Ceil(conf.ATMC/conf.DMAIF) - 1)
when(dat_cbuf_flush_vld_w){
    dat_cbuf_flush_idx := dat_cbuf_flush_idx_w
}
//================  Non-SLCG clock domain end ================//

////////////////////////////////////////////////////////////////////////
//  output ports                                                      //
////////////////////////////////////////////////////////////////////////

io.cdma2buf_dat_wr.addr.valid := cvt_out_vld_reg
io.cdma2buf_dat_wr.addr.bits := cvt_out_addr_reg
if(conf.DMAIF<conf.ATMC){
    io.cdma2buf_dat_wr_sel.get := cvt_out_sel_reg.get
}
io.cdma2buf_dat_wr.data := cvt_out_data_p0_reg

io.dp2reg_nan_data_num := "b0".asUInt(32.W)
io.dp2reg_inf_data_num := "b0".asUInt(32.W)


}}

object NV_NVDLA_CDMA_cvtDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_CDMA_cvt())
}

