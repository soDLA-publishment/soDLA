package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import scala.math._
import chisel3.iotesters.Driver

class NV_NVDLA_CDMA_IMG_pack(implicit conf: nvdlaConfig) extends Module {

    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())

        //img2sbuf
        val img2sbuf_p0_rd = new nvdla_rd_if(8, conf.ATMM)
        
        val is_running = Input(Bool())
        val layer_st = Input(Bool())

        val pixel_bank = Input(UInt(6.W))
        val pixel_data_expand = Input(Bool())
        val pixel_data_shrink = Input(Bool())
        val pixel_early_end = Input(Bool())
        val pixel_packed_10b = Input(Bool())
        val pixel_planar = Input(Bool())
        val pixel_planar0_sft = Input(UInt(3.W))
        val pixel_planar1_sft = Input(UInt(3.W))
        val pixel_precision = Input(UInt(2.W))
        val pixel_uint = Input(Bool())

        val sg2pack_img_pd = Flipped(Decoupled(UInt(11.W)))
        val sg2pack_data_entries = Input(UInt(15.W))
        val sg2pack_entry_end = Input(UInt(15.W))
        val sg2pack_entry_mid = Input(UInt(15.W))
        val sg2pack_entry_st = Input(UInt(15.W))
        val sg2pack_height_total = Input(UInt(13.W))
        val sg2pack_mn_enable = Input(Bool())
        val sg2pack_sub_h_end = Input(UInt(4.W))
        val sg2pack_sub_h_mid = Input(UInt(4.W))
        val sg2pack_sub_h_st = Input(UInt(4.W))

        val status2dma_wr_idx = Input(UInt(15.W))

        val img2cvt_dat_wr_sel = if(conf.DMAIF < conf.ATMC) Some(Output(UInt(log2Ceil(conf.ATMC/conf.DMAIF).W))) 
                                  else None
        val img2cvt_dat_wr = new nvdla_wr_if(17, conf.DMAIF)
        val img2cvt_mn_wr_data = Output(UInt((conf.BNUM*16).W))
        val img2cvt_dat_wr_pad_mask = Output(UInt(conf.BNUM.W))
        val img2cvt_dat_wr_info_pd = Output(UInt(12.W))

        val img2status_dat_updt = ValidIO(new updt_entries_slices_if)

        val pack_is_done = Output(Bool())

        val reg2dp_datain_width = Input(UInt(13.W))
        val reg2dp_datain_channel = Input(UInt(13.W))
        val reg2dp_mean_ry = Input(UInt(16.W))
        val reg2dp_mean_gu = Input(UInt(16.W))
        val reg2dp_mean_bv = Input(UInt(16.W))
        val reg2dp_mean_ax = Input(UInt(16.W))
        val reg2dp_pad_left = Input(UInt(5.W))
        val reg2dp_pad_right = Input(UInt(6.W))

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
////////////////////////////////////////////////////////////////////////
// signals from other modules                                         //
////////////////////////////////////////////////////////////////////////
val is_running_d1 = RegInit(false.B)

val img_pd = Mux(io.sg2pack_img_pd.valid,  io.sg2pack_img_pd.bits, 0.U)

val img_p0_burst = img_pd(3, 0)
val img_p1_burst = img_pd(8, 4)
val img_line_end = img_pd(9)
val img_layer_end = img_pd(10)

val is_first_running = ~is_running_d1 & io.is_running

is_running_d1 := io.is_running
////////////////////////////////////////////////////////////////////////
// general signals                                                    //
////////////////////////////////////////////////////////////////////////
val data_width_mark_0 = RegInit("b0".asUInt(14.W))
val data_width_mark_1 = RegInit("b0".asUInt(14.W))
val data_width_mark_2 = RegInit("b0".asUInt(14.W))

when(io.layer_st){
    data_width_mark_0 := io.reg2dp_pad_left
    data_width_mark_1 := io.reg2dp_pad_left +& io.reg2dp_datain_width +& 1.U
    data_width_mark_2 := io.reg2dp_pad_left +& io.reg2dp_datain_width +& 1.U +& io.reg2dp_pad_right
}
//sft
val lp_planar0_mask_sft = RegInit("b0".asUInt(conf.ATMMBW.W))
val lp_planar1_mask_sft = RegInit("b0".asUInt(conf.ATMMBW.W))
val rp_planar0_mask_sft = RegInit("b0".asUInt(conf.ATMMBW.W))
val rp_planar1_mask_sft = RegInit("b0".asUInt(conf.ATMMBW.W))
val zero_planar0_mask_sft = RegInit("b0".asUInt(conf.ATMMBW.W))
val zero_planar1_mask_sft = RegInit("b0".asUInt(conf.ATMMBW.W))
val data_planar0_add = RegInit("b0".asUInt(6.W))
val data_planar1_add = RegInit("b0".asUInt(6.W))

when(is_first_running){
    lp_planar0_mask_sft := Cat(data_width_mark_0(conf.ATMMBW-1, 0), Fill(conf.ATMMBW, false.B)) >> io.pixel_planar0_sft
    lp_planar1_mask_sft := Cat(data_width_mark_0(conf.ATMMBW-1, 0), Fill(conf.ATMMBW, false.B)) >> io.pixel_planar1_sft
    rp_planar0_mask_sft := Cat(data_width_mark_1(conf.ATMMBW-1, 0), Fill(conf.ATMMBW, false.B)) >> io.pixel_planar0_sft
    rp_planar1_mask_sft := Cat(data_width_mark_1(conf.ATMMBW-1, 0), Fill(conf.ATMMBW, false.B)) >> io.pixel_planar1_sft
    zero_planar0_mask_sft := Cat(data_width_mark_2(conf.ATMMBW-1, 0), Fill(conf.ATMMBW, false.B)) >> io.pixel_planar0_sft
    zero_planar1_mask_sft := Cat(data_width_mark_2(conf.ATMMBW-1, 0), Fill(conf.ATMMBW, false.B)) >> io.pixel_planar1_sft
    data_planar0_add := 1.U << io.pixel_planar0_sft
    data_planar1_add := 1.U << io.pixel_planar1_sft
}

////////////////////////////////////////////////////////////////////////
// Shared buffer read sequnce generator                               //
////////////////////////////////////////////////////////////////////////
val rd_height_cnt = RegInit("b0".asUInt(13.W))
val rd_height_en = Wire(Bool())

val is_1st_height = ~(rd_height_cnt.orR)
val is_last_height = (rd_height_cnt === io.sg2pack_height_total)
val rd_height_cnt_inc = rd_height_cnt + 1.U
val rd_height_cnt_w = Mux(is_first_running, 0.U, rd_height_cnt_inc)

when(rd_height_en){
    rd_height_cnt := rd_height_cnt_w
}

//////// sub height counter ////////
val is_last_sub_h = true.B
val rd_sub_h_cnt = "b0".asUInt(3.W)

//////// loop cnt ////////
// img_p0_burst[3:1],means img_p0_burst/2, 2 means atmm_num/per_dmaif
val rd_loop_cnt = RegInit("b0".asUInt(4.W))
val rd_loop_en = Wire(Bool())

val rd_loop_cnt_limit = img_p0_burst

val rd_loop_cnt_inc = rd_loop_cnt + 1.U
val is_last_loop = (rd_loop_cnt_inc >= rd_loop_cnt_limit)

when(rd_loop_en){   
    rd_loop_cnt := Mux(is_first_running | is_last_loop, 0.U, rd_loop_cnt_inc)
}

//////// planar cnt ////////
val rd_planar_cnt = RegInit(false.B)
val rd_planar_en = Wire(Bool())
val is_last_planar = ~io.pixel_planar | rd_planar_cnt

when(rd_planar_en){
    rd_planar_cnt := Mux(is_first_running | is_last_planar, false.B, ~rd_planar_cnt)
}

//////// partial burst cnt ////////
val rd_pburst_cnt = RegInit("b0".asUInt(2.W))
val rd_pburst_en = Wire(Bool())

val rd_pburst_limit = Mux((rd_planar_cnt & (~is_last_loop | ~img_p1_burst(0))), "b1".asUInt(2.W), "b0".asUInt(2.W))

val is_last_pburst = (rd_pburst_cnt === rd_pburst_limit)

when(rd_pburst_en){
    rd_pburst_cnt := Mux(is_first_running | is_last_pburst, "b0".asUInt(2.W), rd_pburst_cnt + 1.U)
}

//////// control logic ////////
val rd_vld = Wire(Bool())
val rd_sub_h_end = Wire(Bool())
val rd_local_vld = RegInit(false.B)

io.sg2pack_img_pd.ready := rd_vld & rd_sub_h_end
rd_vld := (io.sg2pack_img_pd.valid | rd_local_vld)
val rd_local_vld_w = Mux(~io.is_running, false.B,
                     Mux(rd_sub_h_end, false.B,
                     Mux(io.sg2pack_img_pd.valid, true.B, rd_local_vld)))

val rd_pburst_end = rd_vld & is_last_pburst
val rd_planar_end = rd_vld & is_last_pburst & is_last_planar
val rd_loop_end   = rd_vld & is_last_pburst & is_last_planar & is_last_loop
rd_sub_h_end     := rd_vld & is_last_pburst & is_last_planar & is_last_loop & is_last_sub_h
val rd_line_end   = rd_vld & is_last_pburst & is_last_planar & is_last_loop & is_last_sub_h & img_line_end
val rd_height_end = rd_vld & is_last_pburst & is_last_planar & is_last_loop & is_last_sub_h & img_line_end & is_last_height
rd_pburst_en     := is_first_running | rd_vld
rd_planar_en     := is_first_running | (rd_pburst_end & io.pixel_planar)
rd_loop_en       := is_first_running | rd_planar_end
rd_height_en     := is_first_running | rd_line_end

val rd_planar0_burst_end = rd_vld & is_last_pburst & ~rd_planar_cnt & is_last_loop
val rd_planar1_burst_end = rd_vld & is_last_pburst & rd_planar_cnt & is_last_loop

val rd_planar0_line_end = rd_vld & is_last_pburst & ~rd_planar_cnt & is_last_loop & is_last_sub_h & img_line_end
val rd_planar1_line_end = rd_vld & is_last_pburst & rd_planar_cnt & is_last_loop & is_last_sub_h & img_line_end

rd_local_vld := rd_local_vld_w
val rd_vld_d1 = RegNext(rd_vld, false.B)

////////////////////////////////////////////////////////////////////////
// read control logic generator                                       //
////////////////////////////////////////////////////////////////////////
//////// read enalbe mask ////////
val rd_planar0_rd_mask = Wire(UInt(conf.ATMM_NUM.W))
val rd_planar1_rd_mask = Wire(UInt(conf.ATMM_NUM.W))
val rd_rd_mask = Wire(UInt(conf.ATMM_NUM.W))
val rd_p0_vld = Wire(Bool())
val rd_idx_add = Wire(UInt(3.W))

rd_planar0_rd_mask := 1.U
rd_planar1_rd_mask := 1.U
rd_p0_vld := rd_vld & rd_rd_mask
rd_idx_add := 1.U


val rd_p0_vld_d1 = RegNext(rd_p0_vld, false.B)

rd_rd_mask := Mux(rd_planar_cnt, rd_planar1_rd_mask, rd_planar0_rd_mask)

//////// read address ////////
val rd_p0_planar0_idx = RegInit("b0".asUInt(7.W))
val rd_p0_planar1_idx = RegInit("b0".asUInt(7.W))
val rd_p0_planar0_ori_idx = RegInit("b0".asUInt(7.W))
val rd_p0_planar1_ori_idx = RegInit("b0".asUInt(7.W))
val rd_p0_addr = RegInit("b0".asUInt(8.W))
val rd_p0_addr_d1 = RegInit("b0".asUInt(8.W))

val rd_planar0_en = Wire(Bool())
val rd_planar1_en = Wire(Bool())
val rd_planar0_ori_en = Wire(Bool())
val rd_planar1_ori_en = Wire(Bool())

val rd_p0_planar0_idx_inc = rd_p0_planar0_idx + rd_idx_add
val rd_p0_planar1_idx_inc = rd_p0_planar1_idx + rd_idx_add
val rd_p0_planar0_idx_w = Mux(is_first_running, "b0".asUInt(7.W), rd_p0_planar0_idx_inc)
val rd_p0_planar1_idx_w = Mux(is_first_running, "b0".asUInt(7.W), rd_p0_planar1_idx_inc)
rd_p0_addr := Mux(~rd_planar_cnt, Cat(false.B, rd_p0_planar0_idx(0), rd_p0_planar0_idx(6, 1)), 
             Cat(true.B, rd_p0_planar1_idx(0), rd_p0_planar1_idx(6, 1)))


when(rd_planar0_en){
    rd_p0_planar0_idx := rd_p0_planar0_idx_w
}
when(rd_planar1_en){
    rd_p0_planar1_idx := rd_p0_planar1_idx_w
}
when(rd_planar0_ori_en){
    rd_p0_planar0_ori_idx := rd_p0_planar0_idx_w
}
when(rd_planar1_ori_en){
    rd_p0_planar1_ori_idx := rd_p0_planar1_idx_w
}
when(rd_p0_vld){
    rd_p0_addr_d1 := rd_p0_addr
}


rd_planar0_en := is_first_running | (rd_vld & ~rd_planar_cnt)
rd_planar1_en := is_first_running | (rd_vld & rd_planar_cnt)

rd_planar0_ori_en := is_first_running
rd_planar1_ori_en := is_first_running

//////// status logic /////////
val data_planar0_cur_cnt = RegInit("b0".asUInt(14.W))
val data_planar1_cur_cnt = RegInit("b0".asUInt(14.W)) 

val data_planar0_p0_cnt_w = data_planar0_cur_cnt + data_planar0_add 
val data_planar1_p0_cnt_w = data_planar1_cur_cnt + data_planar1_add 
val data_planar0_p0_cur_flag = Cat(data_planar0_p0_cnt_w>data_width_mark_2, data_planar0_p0_cnt_w>data_width_mark_1, data_planar0_p0_cnt_w>data_width_mark_0)
val data_planar1_p0_cur_flag = Cat(data_planar1_p0_cnt_w>data_width_mark_2, data_planar1_p0_cnt_w>data_width_mark_1, data_planar1_p0_cnt_w>data_width_mark_0)

val data_planar0_en = Wire(Bool())
val data_planar1_en = Wire(Bool())

when(data_planar0_en){
    data_planar0_cur_cnt := Mux(is_first_running | rd_planar0_line_end, "b0".asUInt(14.W), data_planar0_p0_cnt_w)
}
when(data_planar1_en){
    data_planar1_cur_cnt := Mux(is_first_running | rd_planar1_line_end, "b0".asUInt(14.W), data_planar1_p0_cnt_w)
}


///////////////////////////////
val data_planar0_p0_zero_mask = Wire(UInt(conf.NVDLA_MEMORY_ATOMIC_SIZE.W))
val data_planar0_p0_pad_mask = Wire(UInt(conf.NVDLA_MEMORY_ATOMIC_SIZE.W))
val data_planar1_p0_zero_mask = Wire(UInt(conf.NVDLA_MEMORY_ATOMIC_SIZE.W))
val data_planar1_p0_pad_mask = Wire(UInt(conf.NVDLA_MEMORY_ATOMIC_SIZE.W))
val rd_p0_zero_mask = Wire(UInt(conf.NVDLA_MEMORY_ATOMIC_SIZE.W))
val rd_p0_pad_mask = Wire(UInt(conf.NVDLA_MEMORY_ATOMIC_SIZE.W))
val rd_p0_zero_mask_d1 = Reg(UInt(conf.NVDLA_MEMORY_ATOMIC_SIZE.W))
val rd_p0_pad_mask_d1 = Reg(UInt(conf.NVDLA_MEMORY_ATOMIC_SIZE.W))

//planar0
val data_planar0_cnt_sub = data_planar0_p0_cnt_w - data_planar0_add
val data_planar0_p0_flag_nex = Cat(data_planar0_cnt_sub > data_width_mark_1, data_planar0_cnt_sub > data_width_mark_0)
val data_planar0_p0_lp_mask = Mux(~data_planar0_p0_cur_flag(0), Fill(conf.ATMM, true.B),
                                Mux(~data_planar0_p0_flag_nex(0), ~(Fill(conf.ATMM, true.B) << lp_planar0_mask_sft),
                                Fill(conf.ATMM, false.B)))
val data_planar0_p0_rp_mask = Mux(~data_planar0_p0_cur_flag(1), Fill(conf.ATMM, false.B),
                                Mux(~data_planar0_p0_flag_nex(1), (Fill(conf.ATMM, true.B) << rp_planar0_mask_sft),
                                Fill(conf.ATMM, true.B))) 
data_planar0_p0_zero_mask := Mux(~data_planar0_p0_cur_flag(2), Fill(conf.ATMM, false.B),
                                (Fill(conf.ATMM, true.B) << zero_planar0_mask_sft))    
data_planar0_p0_pad_mask :=  (data_planar0_p0_lp_mask | data_planar0_p0_rp_mask) & ~data_planar0_p0_zero_mask   
//planar1
val data_planar1_cnt_sub = data_planar1_p0_cnt_w - data_planar1_add
val data_planar1_p0_flag_nex = Cat(data_planar1_cnt_sub > data_width_mark_1, data_planar1_cnt_sub > data_width_mark_0)
val data_planar1_p0_lp_mask = Mux(~data_planar1_p0_cur_flag(0), Fill(conf.ATMM, true.B),
                                Mux(~data_planar1_p0_flag_nex(0), ~(Fill(conf.ATMM, true.B) << lp_planar1_mask_sft),
                                Fill(conf.ATMM, false.B)))
val data_planar1_p0_rp_mask = Mux(~data_planar1_p0_cur_flag(1), Fill(conf.ATMM, false.B),
                                Mux(~data_planar1_p0_flag_nex(1), (Fill(conf.ATMM, true.B) << rp_planar1_mask_sft),
                                Fill(conf.ATMM, true.B))) 
data_planar1_p0_zero_mask := Mux(~data_planar1_p0_cur_flag(2), Fill(conf.ATMM, false.B),
                                (Fill(conf.ATMM, true.B) << zero_planar1_mask_sft))    
data_planar1_p0_pad_mask :=  (data_planar1_p0_lp_mask | data_planar1_p0_rp_mask) & ~data_planar1_p0_zero_mask   

rd_p0_pad_mask := Mux(~rd_planar_cnt, data_planar0_p0_pad_mask, data_planar1_p0_pad_mask)
rd_p0_zero_mask := Mux(~rd_planar_cnt, data_planar0_p0_zero_mask, data_planar1_p0_zero_mask)

when(rd_vld){
    rd_p0_pad_mask_d1 := Mux(~rd_planar_cnt, data_planar0_p0_pad_mask, data_planar1_p0_pad_mask)
    rd_p0_zero_mask_d1 := Mux(~rd_planar_cnt, data_planar0_p0_pad_mask, data_planar1_p0_pad_mask)
}

data_planar0_en := is_first_running | (rd_vld & ~rd_planar_cnt)
data_planar1_en := is_first_running | (rd_vld & rd_planar_cnt)

val rd_planar_d1 = RegInit(false.B)
val rd_sub_h_d1 = RegInit("b0".asUInt(3.W))
val rd_sub_h_end_d1 = RegInit(false.B)
val rd_loop_end_d1 = RegInit(false.B)
val rd_one_line_end_d1 = RegInit(false.B)
val rd_1st_height_d1 = RegInit(false.B)
val rd_layer_end_d1 = RegInit(false.B)

when(rd_vld){
   rd_planar_d1 := rd_planar_cnt
   rd_sub_h_d1 := rd_sub_h_cnt
   rd_sub_h_end_d1 := rd_sub_h_end
   rd_loop_end_d1 := rd_loop_end
   rd_one_line_end_d1 := is_last_pburst & is_last_planar & is_last_loop & img_line_end
   rd_1st_height_d1 := is_1st_height
   rd_layer_end_d1 := img_layer_end & rd_height_end
}
////////////////////////////////////////////////////////////////////////
// connect to shared buffer                                           //
////////////////////////////////////////////////////////////////////////

io.img2sbuf_p0_rd.addr.valid := rd_p0_vld_d1
io.img2sbuf_p0_rd.addr.bits := rd_p0_addr_d1


////////////////////////////////////////////////////////////////////////
// pipeline register for shared buffer read latency                   //
////////////////////////////////////////////////////////////////////////
val rd_vld_d1_d = Wire(Bool()) +: 
               Seq.fill(conf.CDMA_SBUF_RD_LATENCY)(RegInit(false.B)) 
val rd_planar_d1_d = Wire(Bool()) +: 
                  Seq.fill(conf.CDMA_SBUF_RD_LATENCY)(RegInit(false.B)) 
val rd_sub_h_d1_d = Wire(UInt(3.W)) +: 
                  Seq.fill(conf.CDMA_SBUF_RD_LATENCY)(RegInit("b0".asUInt(3.W))) 
val rd_sub_h_end_d1_d = Wire(Bool()) +: 
               Seq.fill(conf.CDMA_SBUF_RD_LATENCY)(RegInit(false.B)) 
val rd_loop_end_d1_d = Wire(Bool()) +: 
                  Seq.fill(conf.CDMA_SBUF_RD_LATENCY)(RegInit(false.B)) 
val rd_one_line_end_d1_d = Wire(Bool()) +: 
               Seq.fill(conf.CDMA_SBUF_RD_LATENCY)(RegInit(false.B)) 
val rd_1st_height_d1_d = Wire(Bool()) +: 
                  Seq.fill(conf.CDMA_SBUF_RD_LATENCY)(RegInit(false.B)) 
val rd_layer_end_d1_d = Wire(Bool()) +: 
               Seq.fill(conf.CDMA_SBUF_RD_LATENCY)(RegInit(false.B)) 

val rd_p0_pad_mask_d1_d = Wire(UInt(conf.NVDLA_MEMORY_ATOMIC_SIZE.W)) +: 
                        Seq.fill(conf.CDMA_SBUF_RD_LATENCY)(Reg(UInt(conf.NVDLA_MEMORY_ATOMIC_SIZE.W))) 
val rd_p0_zero_mask_d1_d = Wire(UInt(conf.NVDLA_MEMORY_ATOMIC_SIZE.W)) +: 
                        Seq.fill(conf.CDMA_SBUF_RD_LATENCY)(Reg(UInt(conf.NVDLA_MEMORY_ATOMIC_SIZE.W))) 
rd_vld_d1_d(0) := rd_vld_d1
rd_planar_d1_d(0) := rd_planar_d1
rd_sub_h_d1_d(0) := rd_sub_h_d1 
rd_sub_h_end_d1_d(0) := rd_sub_h_end_d1
rd_loop_end_d1_d(0) := rd_loop_end_d1
rd_one_line_end_d1_d(0) := rd_one_line_end_d1
rd_1st_height_d1_d(0) := rd_1st_height_d1
rd_layer_end_d1_d(0) := rd_layer_end_d1
rd_p0_pad_mask_d1_d(0) := rd_p0_pad_mask_d1 
rd_p0_zero_mask_d1_d(0) := rd_p0_zero_mask_d1 

for(t <- 0 to conf.CDMA_SBUF_RD_LATENCY-1){
    rd_vld_d1_d(t+1) := rd_vld_d1_d(t)
    when(rd_vld_d1_d(t)){
        rd_vld_d1_d(t+1) := rd_vld_d1_d(t)
        rd_planar_d1_d(t+1) := rd_planar_d1_d(t)
        rd_sub_h_d1_d(t+1) := rd_sub_h_d1_d(t)
        rd_sub_h_end_d1_d(t+1) := rd_sub_h_end_d1_d(t)
        rd_loop_end_d1_d(t+1) := rd_loop_end_d1_d(t)
        rd_one_line_end_d1_d(t+1) := rd_one_line_end_d1_d(t)
        rd_1st_height_d1_d(t+1) := rd_1st_height_d1_d(t)
        rd_layer_end_d1_d(t+1) := rd_layer_end_d1_d(t)
        rd_p0_pad_mask_d1_d(t+1) := rd_p0_pad_mask_d1_d(t) 
        rd_p0_zero_mask_d1_d(t+1) := rd_p0_zero_mask_d1_d(t) 
    }
}
val pk_rsp_vld           = rd_vld_d1_d(conf.CDMA_SBUF_RD_LATENCY)
val pk_rsp_planar        = rd_planar_d1_d(conf.CDMA_SBUF_RD_LATENCY)
val pk_rsp_sub_h         = rd_sub_h_d1_d(conf.CDMA_SBUF_RD_LATENCY)
val pk_rsp_sub_h_end     = rd_sub_h_end_d1_d(conf.CDMA_SBUF_RD_LATENCY)
val pk_rsp_loop_end      = rd_loop_end_d1_d(conf.CDMA_SBUF_RD_LATENCY)
val pk_rsp_one_line_end  = rd_one_line_end_d1_d(conf.CDMA_SBUF_RD_LATENCY)
val pk_rsp_1st_height    = rd_1st_height_d1_d(conf.CDMA_SBUF_RD_LATENCY)
val pk_rsp_layer_end     = rd_layer_end_d1_d(conf.CDMA_SBUF_RD_LATENCY)
val pk_rsp_p0_pad_mask   = rd_p0_pad_mask_d1_d(conf.CDMA_SBUF_RD_LATENCY)
val pk_rsp_p0_zero_mask  = rd_p0_zero_mask_d1_d(conf.CDMA_SBUF_RD_LATENCY)

val pk_rsp_early_end = io.pixel_early_end & pk_rsp_one_line_end;
val pk_rsp_vld_d1_w = pk_rsp_vld & io.pixel_planar & ~(pk_rsp_early_end)

val pk_rsp_vld_d1 = RegInit(false.B)
val pk_rsp_sub_h_d1 = RegInit("b0".asUInt(3.W))
val pk_rsp_sub_h_end_d1 = RegInit(false.B)
val pk_rsp_loop_end_d1 = RegInit(false.B)
val pk_rsp_one_line_end_d1 = RegInit(false.B)
val pk_rsp_1st_height_d1 = RegInit(false.B)
val pk_rsp_layer_end_d1 = RegInit(false.B)

pk_rsp_vld_d1 := pk_rsp_vld_d1_w
when(pk_rsp_vld_d1_w){
    pk_rsp_sub_h_d1 := pk_rsp_sub_h
    pk_rsp_sub_h_end_d1 := pk_rsp_sub_h_end
    pk_rsp_loop_end_d1 := pk_rsp_loop_end
    pk_rsp_one_line_end_d1 := pk_rsp_one_line_end
    pk_rsp_1st_height_d1 := pk_rsp_1st_height
    pk_rsp_layer_end_d1 := pk_rsp_layer_end
}

////////////////////////////////////////////////////////////////////////
//  connect to sbuf ram input                                         //
////////////////////////////////////////////////////////////////////////
val pk_rsp_p0_data = io.img2sbuf_p0_rd.data
////////////////////////////////////////////////////////////////////////
// data write logic                                                   //
////////////////////////////////////////////////////////////////////////
//////// control and status logic ////////
val pk_rsp_wr_cnt = RegInit("b0".asUInt(2.W))

val pk_rsp_pipe_sel = (~io.pixel_planar | (pk_rsp_vld & pk_rsp_early_end));

val pk_rsp_cur_vld = Mux(pk_rsp_pipe_sel , pk_rsp_vld , pk_rsp_vld_d1)
val pk_rsp_cur_sub_h = Mux(pk_rsp_pipe_sel , pk_rsp_sub_h , pk_rsp_sub_h_d1)
val pk_rsp_cur_sub_h_end = Mux(pk_rsp_pipe_sel , pk_rsp_sub_h_end , pk_rsp_sub_h_end_d1)
val pk_rsp_cur_loop_end = Mux(pk_rsp_pipe_sel , pk_rsp_loop_end , pk_rsp_loop_end_d1)
val pk_rsp_cur_one_line_end = Mux(pk_rsp_pipe_sel , pk_rsp_one_line_end , pk_rsp_one_line_end_d1)
val pk_rsp_cur_1st_height = Mux(pk_rsp_pipe_sel , pk_rsp_1st_height , pk_rsp_1st_height_d1)
val pk_rsp_cur_layer_end = Mux(pk_rsp_pipe_sel , pk_rsp_layer_end , pk_rsp_layer_end_d1)

val pk_rsp_wr_vld = pk_rsp_cur_vld
val pk_rsp_wr_cnt_w = Mux(is_first_running | ~pk_rsp_planar, "b0".asUInt(2.W), pk_rsp_wr_cnt + 1.U)

when(pk_rsp_vld){
    pk_rsp_wr_cnt := pk_rsp_wr_cnt_w
}

val pk_rsp_wr_size_ori = conf.ATMM_NUM.asUInt(3.W)
val pk_rsp_wr_mask = Cat(Fill(4 -conf.ATMM_NUM, false.B), Fill(conf.ATMM_NUM, true.B))

val pk_out_vld = RegInit(false.B)
val pk_out_sub_h = RegInit("b0".asUInt(3.W))
val pk_out_mask = RegInit("b0".asUInt(4.W))
val pk_out_mean = RegInit(false.B)
val pk_out_uint = RegInit(false.B)

pk_out_vld := pk_rsp_wr_vld
when(pk_rsp_wr_vld){
    pk_out_sub_h := pk_rsp_cur_sub_h
}
when(is_first_running){
    pk_out_mask := pk_rsp_wr_mask
    pk_out_mean := io.sg2pack_mn_enable
    pk_out_uint := io.pixel_uint
}

val pk_out_info_pd = Cat(pk_out_sub_h, pk_out_uint, pk_out_mean, false.B, false.B, false.B, pk_out_mask)

////////////////////////////////////////////////////////////////////////
// data output logic                                                  //
////////////////////////////////////////////////////////////////////////
val pk_rsp_planar0_c0_d1 = RegInit("b0".asUInt(conf.NVDLA_CDMA_DMAIF_BW.W))
val pk_rsp_planar1_c0_d1 = RegInit("b0".asUInt(conf.NVDLA_CDMA_DMAIF_BW.W))
val pk_rsp_planar1_c1_d1 = RegInit("b0".asUInt(conf.NVDLA_CDMA_DMAIF_BW.W))
val mask_pad_planar0_c0_d1 = RegInit("b0".asUInt((conf.NVDLA_CDMA_DMAIF_BW/conf.NVDLA_BPE).W))
val mask_pad_planar1_c0_d1 = RegInit("b0".asUInt((conf.NVDLA_CDMA_DMAIF_BW/conf.NVDLA_BPE).W))
val mask_pad_planar1_c1_d1 = RegInit("b0".asUInt((conf.NVDLA_CDMA_DMAIF_BW/conf.NVDLA_BPE).W))
val pk_out_data_h0 = Reg(UInt(conf.NVDLA_CDMA_DMAIF_BW.W))
val pk_out_pad_mask_h0 = RegInit("b0".asUInt((conf.NVDLA_CDMA_DMAIF_BW/conf.NVDLA_BPE).W))

val rdat = Wire(UInt(conf.NVDLA_CDMA_DMAIF_BW.W))
val mask_pad = Wire(UInt((conf.NVDLA_CDMA_DMAIF_BW/conf.NVDLA_BPE).W))
val mask_zero = Wire(UInt((conf.NVDLA_CDMA_DMAIF_BW/conf.NVDLA_BPE).W))
val pk_rsp_planar1_c0_en = Wire(Bool())
val pk_rsp_planar1_c1_en = Wire(Bool())

rdat := pk_rsp_p0_data
mask_zero := pk_rsp_p0_zero_mask
mask_pad := pk_rsp_p0_pad_mask


val pk_rsp_dat_normal = rdat
val pk_rsp_dat_mnorm = VecInit((0 to conf.NVDLA_CDMA_DMAIF_BW/conf.NVDLA_BPE-1) 
map{i => Mux(io.pixel_packed_10b|mask_zero(i)|mask_pad(i), 0.U, pk_rsp_dat_normal(conf.NVDLA_BPE*i+conf.NVDLA_BPE-1, conf.NVDLA_BPE*i))}).asUInt
val dat_l0 = pk_rsp_planar0_c0_d1
val dat_l1_lo = Mux(pk_rsp_planar1_c0_en, pk_rsp_dat_mnorm, pk_rsp_planar1_c0_d1)
val dat_l1_hi = Mux(pk_rsp_planar1_c1_en, pk_rsp_dat_mnorm, pk_rsp_planar1_c1_d1)
val dat_l1 = Cat(dat_l1_hi, dat_l1_lo)
val dat_8b_yuv = VecInit((0 to conf.NVDLA_CDMA_DMAIF_BW/conf.NVDLA_BPE - 1) map
{i => Cat(dat_l1(i*2*8+2*8-1, i*2*8), dat_l0(i*8+8-1, i*8))}).asUInt
val dat_yuv = dat_8b_yuv
val pk_rsp_out_sel = Cat(io.pixel_planar & (pk_rsp_wr_cnt === 2.U), io.pixel_planar & (pk_rsp_wr_cnt === 1.U), 
                        io.pixel_planar & (pk_rsp_wr_cnt === 0.U), ~io.pixel_planar & ~io.pixel_packed_10b, io.pixel_packed_10b)
val pk_rsp_data_h0 = (Fill(conf.NVDLA_CDMA_DMAIF_BW, pk_rsp_out_sel(1)) & pk_rsp_dat_mnorm) | 
                     (Fill(conf.NVDLA_CDMA_DMAIF_BW, pk_rsp_out_sel(2)) & dat_yuv(conf.NVDLA_CDMA_DMAIF_BW-1, 0)) |
                     (Fill(conf.NVDLA_CDMA_DMAIF_BW, pk_rsp_out_sel(3)) & dat_yuv(conf.NVDLA_CDMA_DMAIF_BW*2-1,conf.NVDLA_CDMA_DMAIF_BW)) |
                     (Fill(conf.NVDLA_CDMA_DMAIF_BW, pk_rsp_out_sel(4)) & dat_yuv(conf.NVDLA_CDMA_DMAIF_BW*3-1,conf.NVDLA_CDMA_DMAIF_BW*2)) 

val pk_rsp_pad_mask_norm = mask_pad
val pad_mask_l0 = mask_pad_planar0_c0_d1
val pad_mask_l1_lo = Mux(pk_rsp_planar1_c0_en, mask_pad, mask_pad_planar1_c0_d1)
val pad_mask_l1_hi = Mux(pk_rsp_planar1_c1_en, mask_pad, mask_pad_planar1_c1_d1)
val pad_mask_l1 = Cat(pad_mask_l1_hi, pad_mask_l1_lo)
val pad_mask_8b_yuv = VecInit((0 to conf.NVDLA_CDMA_DMAIF_BW/conf.NVDLA_BPE - 1) map
{i => Cat(pad_mask_l1(i*2+2-1, i*2), pad_mask_l0(i+1-1, i))}).asUInt
val pad_mask_yuv = pad_mask_8b_yuv
val pk_rsp_pad_mask_h0 = (Fill(conf.NVDLA_CDMA_DMAIF_BW/conf.NVDLA_BPE, pk_rsp_out_sel(1)) & pk_rsp_pad_mask_norm) | 
                     (Fill(conf.NVDLA_CDMA_DMAIF_BW/conf.NVDLA_BPE, pk_rsp_out_sel(2)) & pad_mask_yuv(conf.NVDLA_CDMA_DMAIF_BW/conf.NVDLA_BPE-1, 0)) |
                     (Fill(conf.NVDLA_CDMA_DMAIF_BW/conf.NVDLA_BPE, pk_rsp_out_sel(3)) & pad_mask_yuv(conf.NVDLA_CDMA_DMAIF_BW/conf.NVDLA_BPE*2-1,conf.NVDLA_CDMA_DMAIF_BW/conf.NVDLA_BPE)) |
                     (Fill(conf.NVDLA_CDMA_DMAIF_BW/conf.NVDLA_BPE, pk_rsp_out_sel(4)) & pad_mask_yuv(conf.NVDLA_CDMA_DMAIF_BW/conf.NVDLA_BPE*3-1,conf.NVDLA_CDMA_DMAIF_BW/conf.NVDLA_BPE*2)) 
val pk_rsp_planar0_c0_en = (pk_rsp_vld & io.pixel_planar & ~pk_rsp_planar);
pk_rsp_planar1_c0_en := pk_rsp_vld & io.pixel_planar & pk_rsp_planar & (pk_rsp_wr_cnt === 0.U)
pk_rsp_planar1_c1_en := pk_rsp_vld & io.pixel_planar & pk_rsp_planar & (pk_rsp_wr_cnt === 1.U)
val pk_rsp_data_h0_en = pk_rsp_wr_vld

when(pk_rsp_planar0_c0_en){
    pk_rsp_planar0_c0_d1 := pk_rsp_dat_mnorm
    mask_pad_planar0_c0_d1 := mask_pad
}
when(pk_rsp_planar1_c0_en){
    pk_rsp_planar1_c0_d1 := pk_rsp_dat_mnorm
    mask_pad_planar1_c0_d1 := mask_pad
}
when(pk_rsp_planar1_c1_en){
    pk_rsp_planar1_c1_d1 := pk_rsp_dat_mnorm
    mask_pad_planar1_c1_d1 := mask_pad
}
when(pk_rsp_data_h0_en){
    pk_out_data_h0 := pk_rsp_data_h0
    pk_out_pad_mask_h0 := pk_rsp_pad_mask_h0
}
val pk_out_data = pk_out_data_h0
val pk_out_pad_mask = pk_out_pad_mask_h0
////////////////////////////////////////////////////////////////////////
// mean data replacement and output logic                             //
////////////////////////////////////////////////////////////////////////
val mn_mask_y_d1 = RegInit("b0".asUInt((conf.NVDLA_CDMA_DMAIF_BW/conf.NVDLA_BPE).W))
val mn_mask_uv_lo_d1 = RegInit("b0".asUInt((conf.NVDLA_CDMA_DMAIF_BW/conf.NVDLA_BPE).W))
val mn_mask_uv_hi_d1 = RegInit("b0".asUInt((conf.NVDLA_CDMA_DMAIF_BW/conf.NVDLA_BPE).W))
val pk_mn_out_data_h0 = RegInit("b0".asUInt(conf.NVDLA_CDMA_DMAIF_BW.W))
val mn_mask_uv_0_en = Wire(Bool())
val mn_mask_uv_1_en = Wire(Bool())

val mn_mask_y = mn_mask_y_d1
val mn_mask_uv_lo = Mux(mn_mask_uv_0_en, mask_zero, mn_mask_uv_lo_d1)
val mn_mask_uv_hi = Mux(mn_mask_uv_1_en, mask_zero, mn_mask_uv_hi_d1)
val mn_mask_uv = Cat(mn_mask_uv_hi, mn_mask_uv_lo)
val mn_mask_yuv = VecInit((0 to conf.NVDLA_MEMORY_ATOMIC_SIZE - 1) 
map{i => Cat(pad_mask_l1(i*2*conf.ATMM_NUM+2*conf.ATMM_NUM-1, i*2*conf.ATMM_NUM), pad_mask_l0(i*conf.ATMM_NUM+conf.ATMM_NUM-1, i*conf.ATMM_NUM))}).asUInt
val mn_ch1 = Fill(conf.BNUM, io.reg2dp_mean_ry)
val mn_ch4 = Fill(conf.BNUM/4, Cat(io.reg2dp_mean_ax, io.reg2dp_mean_bv, io.reg2dp_mean_gu, io.reg2dp_mean_ry))
val mn_ch3 = Fill(conf.BNUM, Cat(io.reg2dp_mean_bv, io.reg2dp_mean_gu, io.reg2dp_mean_ry))
val mn_ch1_4 = Mux(~(io.reg2dp_datain_channel.orR), mn_ch1, mn_ch4)
val mn_8b_mnorm = VecInit((0 to conf.NVDLA_CDMA_DMAIF_BW/conf.NVDLA_BPE-1) 
map{i => Mux(mask_zero(i), "b0".asUInt(16.W), mn_ch1_4(16*i+15, 16*i))}).asUInt
val mn_8b_myuv = VecInit((0 to conf.NVDLA_CDMA_DMAIF_BW/conf.NVDLA_BPE*3-1) 
map{i => Mux(mn_mask_yuv(i), "b0".asUInt(16.W), mn_ch3(16*i+15, 16*i))}).asUInt

val pk_rsp_mn_sel = Cat(io.pixel_planar & (pk_rsp_wr_cnt ===2.U) & io.pixel_precision.orR,
                        io.pixel_planar & (pk_rsp_wr_cnt ===2.U) & ~io.pixel_precision.orR,
                        io.pixel_planar & (pk_rsp_wr_cnt ===1.U) & io.pixel_precision.orR,
                        io.pixel_planar & (pk_rsp_wr_cnt ===1.U) & ~io.pixel_precision.orR,
                        io.pixel_planar & (pk_rsp_wr_cnt ===0.U) & io.pixel_precision.orR,
                        io.pixel_planar & (pk_rsp_wr_cnt ===0.U) & ~io.pixel_precision.orR,
                        ~io.pixel_planar & ~io.pixel_packed_10b & io.pixel_precision.orR,
                        ~io.pixel_planar & (io.pixel_packed_10b | ~io.pixel_precision.orR),
                        )
val pk_rsp_mn_data_h0 = (Fill(conf.MN_BW, pk_rsp_mn_sel(0))&mn_8b_mnorm)|
                        (Fill(conf.MN_BW, pk_rsp_mn_sel(2))&mn_8b_myuv(conf.MN_BW-1, 0))|
                        (Fill(conf.MN_BW, pk_rsp_mn_sel(4))&mn_8b_myuv(2*conf.MN_BW-1, conf.MN_BW))|
                        (Fill(conf.MN_BW, pk_rsp_mn_sel(6))&mn_8b_myuv(3*conf.MN_BW-1, 2*conf.MN_BW))
val mn_mask_y_en = pk_rsp_planar0_c0_en
mn_mask_uv_0_en := pk_rsp_planar1_c0_en 
mn_mask_uv_1_en := pk_rsp_planar1_c1_en
val pk_rsp_mn_data_h0_en = pk_rsp_wr_vld;
val pk_rsp_mn_data_h1_en = (pk_rsp_wr_vld & (~(io.pixel_precision.orR) | io.pixel_packed_10b));

when(mn_mask_y_en){
    mn_mask_y_d1 := mask_zero
}
when(mn_mask_uv_0_en){
    mn_mask_uv_lo_d1 := mask_zero
}
when(mn_mask_uv_1_en){
    mn_mask_uv_hi_d1 := mask_zero
}
when(pk_rsp_mn_data_h1_en){
    pk_mn_out_data_h0 := pk_rsp_mn_data_h0
}

val pk_mn_out_data = pk_mn_out_data_h0

////////////////////////////////////////////////////////////////////////
// cbuf write addresss generator                                      //
////////////////////////////////////////////////////////////////////////
//////// address base ////////
val pk_rsp_wr_base = RegInit("b0".asUInt(15.W))

val pk_rsp_wr_entries = Mux(pk_rsp_cur_1st_height, io.sg2pack_entry_st,
                        Mux(pk_rsp_cur_layer_end, io.sg2pack_entry_end, io.sg2pack_entry_mid))
val pk_rsp_wr_slices = Mux(pk_rsp_cur_1st_height, io.sg2pack_sub_h_st,
                        Mux(pk_rsp_cur_layer_end, io.sg2pack_sub_h_end, io.sg2pack_sub_h_mid))
val pk_rsp_wr_base_inc = Mux(is_first_running, Cat(false.B, io.status2dma_wr_idx), pk_rsp_wr_base +& pk_rsp_wr_entries)

val is_base_wrap = pk_rsp_wr_base_inc(15, conf.NVDLA_CBUF_BANK_DEPTH_LOG2) >= io.pixel_bank
val pk_rsp_wr_base_wrap = (pk_rsp_wr_base_inc(15, 0) -& Cat(io.pixel_bank, Fill(conf.NVDLA_CBUF_BANK_DEPTH_LOG2, false.B)))(14, 0)
val pk_rsp_wr_base_w = Mux(is_base_wrap, pk_rsp_wr_base_wrap, pk_rsp_wr_base_inc(14, 0))

val pk_rsp_wr_base_en = is_first_running | (pk_rsp_wr_vld & pk_rsp_cur_one_line_end & pk_rsp_cur_sub_h_end)

when(pk_rsp_wr_base_en){
    pk_rsp_wr_base := pk_rsp_wr_base_w
}

//////// h_offset ////////
val pk_rsp_wr_h_offset = RegInit("b0".asUInt(15.W))

val pk_rsp_wr_h_offset_w = Mux(is_first_running | pk_rsp_cur_sub_h_end, "b0".asUInt(15.W), pk_rsp_wr_h_offset + io.sg2pack_data_entries)
val pk_rsp_wr_h_offset_en = is_first_running | (pk_rsp_wr_vld & pk_rsp_cur_loop_end)

when(pk_rsp_wr_h_offset_en){
    pk_rsp_wr_h_offset := pk_rsp_wr_h_offset_w
}
///////// w_offset ////////
val pk_rsp_wr_w_offset = RegInit("b0".asUInt(15.W))
val pk_rsp_wr_w_offset_ori = RegInit("b0".asUInt(15.W))

val pk_rsp_wr_w_add = pk_rsp_wr_size_ori
val pk_rsp_wr_w_offset_w = Mux((is_first_running | (pk_rsp_cur_one_line_end & pk_rsp_cur_sub_h_end)), "b0".asUInt(14.W),
                           Mux(pk_rsp_cur_loop_end & ~pk_rsp_cur_sub_h_end, pk_rsp_wr_w_offset_ori, pk_rsp_wr_w_offset + pk_rsp_wr_w_add))

val pk_rsp_wr_w_offset_en = is_first_running | pk_rsp_wr_vld
val pk_rsp_wr_w_offset_ori_en = is_first_running

when(pk_rsp_wr_w_offset_en){
    pk_rsp_wr_w_offset := pk_rsp_wr_w_offset_w
}
when(pk_rsp_wr_w_offset_ori_en){
    pk_rsp_wr_w_offset_ori := pk_rsp_wr_w_offset_w
}
///////// total_address ////////
val pk_out_hsel = RegInit(0.U)

val pk_rsp_wr_addr_inc = pk_rsp_wr_base +& pk_rsp_wr_h_offset +& pk_rsp_wr_w_offset(14, conf.SS)
val pk_rsp_wr_sub_addr = if(conf.SS>0) Some(pk_rsp_wr_w_offset(conf.SS-1, 0)) else None

if(conf.ATMC>conf.DMAIF){
    if(conf.ATMC/conf.DMAIF == 2){
        if(conf.ATMM_NUM == 1){
            when(pk_rsp_wr_vld){
                pk_out_hsel := pk_rsp_wr_sub_addr.get(0)
            }
        }
    }
    else if(conf.ATMC/conf.DMAIF == 4){
        if(conf.ATMM_NUM == 1){
            when(pk_rsp_wr_vld){
                pk_out_hsel := pk_rsp_wr_sub_addr.get(1, 0)
            }
        }    
    }
}
val pk_out_addr = RegInit("b0".asUInt(15.W))

val is_addr_wrap = pk_rsp_wr_addr_inc(16, conf.NVDLA_CBUF_BANK_DEPTH_LOG2) >= io.pixel_bank
val pk_rsp_wr_addr_wrap = (pk_rsp_wr_addr_inc(16, 0) -& Cat(io.pixel_bank, Fill(conf.NVDLA_CBUF_BANK_DEPTH_LOG2, false.B)))(14, 0)
val pk_rsp_wr_addr = Mux(is_addr_wrap, pk_rsp_wr_addr_wrap, pk_rsp_wr_addr_inc(14, 0))


when(pk_rsp_wr_vld){
    pk_out_addr := pk_rsp_wr_addr
}

////////////////////////////////////////////////////////////////////////
// update status                                                      //
////////////////////////////////////////////////////////////////////////
val pk_out_data_updt = RegInit(false.B)
val pk_out_data_entries = RegInit("b0".asUInt(15.W))
val pk_out_data_slices = RegInit("b0".asUInt(4.W))

val pk_rsp_data_updt = pk_rsp_wr_vld & pk_rsp_cur_one_line_end & pk_rsp_cur_sub_h_end

pk_out_data_updt := pk_rsp_data_updt
when(pk_rsp_data_updt){
    pk_out_data_entries := pk_rsp_wr_entries
    pk_out_data_slices := pk_rsp_wr_slices
}

////////////////////////////////////////////////////////////////////////
//  output connection                                                 //
////////////////////////////////////////////////////////////////////////
io.img2status_dat_updt.valid := pk_out_data_updt
io.img2status_dat_updt.bits.slices := Cat("b0".asUInt(10.W), pk_out_data_slices)
io.img2status_dat_updt.bits.entries := pk_out_data_entries

io.img2cvt_dat_wr.addr.valid := pk_out_vld
io.img2cvt_dat_wr_info_pd := pk_out_info_pd

if(conf.DMAIF < conf.ATMC){
    io.img2cvt_dat_wr_sel.get := pk_out_hsel
}
io.img2cvt_dat_wr.addr.bits := pk_out_addr
io.img2cvt_dat_wr.data := pk_out_data
io.img2cvt_mn_wr_data := pk_mn_out_data
io.img2cvt_dat_wr_pad_mask := pk_out_pad_mask

////////////////////////////////////////////////////////////////////////
// global status                                                      //
////////////////////////////////////////////////////////////////////////
val pack_is_done_out = RegInit(true.B)

val pack_is_done_w = Mux(is_first_running, false.B, 
                     Mux(pk_rsp_wr_vld & pk_rsp_cur_layer_end, true.B,
                     pack_is_done_out))

pack_is_done_out := pack_is_done_w
io.pack_is_done := pack_is_done_out

    
}}


    
object NV_NVDLA_CDMA_IMG_packDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_CDMA_IMG_pack())
}