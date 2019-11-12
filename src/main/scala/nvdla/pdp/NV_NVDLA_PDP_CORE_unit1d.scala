package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

//////////////////////////
//=========================================================
//POOLING FUNCTION DEFINITION
//
object pooling_MIN{
    // returns the minimum
    def apply(data0:UInt, data1:UInt, data0_valid:Bool) = {
        Mux((data1.asSInt>data0.asSInt)&data0_valid, data0, data1)
    }
}

object pooling_MAX{
    // returns the maxinum
    def apply(data0:UInt, data1:UInt, data0_valid:Bool) = {
        Mux((data0.asSInt>data1.asSInt)&data0_valid, data0, data1)
    }
}

object pooling_SUM{
    // returns the sum
    def apply(data0:UInt, data1:UInt) = {
        (data0.asSInt + data1.asSInt).asUInt
    }
}



class NV_NVDLA_PDP_CORE_unit1d(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //clk
        val nvdla_core_clk = Input(Clock())

        //pdma2pdp
        val pdma2pdp_pd = Flipped(DecoupledIO(UInt((conf.PDP_UNIT1D_BW+9).W)))

        //pooling
        val pooling_out = DecoupledIO(UInt((conf.PDP_UNIT1D_BW+4).W))

        //config  
        val average_pooling_en = Input(Bool())
        val cur_datin_disable = Input(Bool())
        val last_out_en = Input(Bool())
        val pdp_din_lc_f = Input(Bool())
        val pooling_din_1st = Input(Bool())
        val pooling_din_last = Input(Bool())
        val pooling_type_cfg = Input(UInt(2.W))
        val pooling_unit_en = Input(Bool())

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
//=======================================================
//1D pooling unit
//-------------------------------------------------------

// interface
    val pdp_din_wpos = io.pdma2pdp_pd.bits(conf.PDP_UNIT1D_BW+3, conf.PDP_UNIT1D_BW)
    val pdp_din_cpos = io.pdma2pdp_pd.bits(conf.PDP_UNIT1D_BW+8, conf.PDP_UNIT1D_BW+4)
    val buf_sel       = pdp_din_cpos
  
    val pipe_in_rdy = Wire(Bool())
    val pdma2pdp_prdy_f = Wire(Bool())
    val load_din = io.pdma2pdp_pd.valid & pdma2pdp_prdy_f & (~io.cur_datin_disable) & io.pooling_unit_en;
    pdma2pdp_prdy_f := pipe_in_rdy
    io.pdma2pdp_pd.ready := pdma2pdp_prdy_f

//=========================================================
// pooling real size
//
val pooling_size = RegInit("b0".asUInt(3.W))
when(load_din & io.pdp_din_lc_f){
    when(io.pooling_din_last){
        pooling_size := 0.U
    }
    .otherwise{
        pooling_size := pooling_size + 1.U
    }
}

val pooling_out_size = pooling_size

////====================================================================
//// pooling data 
////
val data_buf = Wire(Vec(conf.BATCH_PDP_NUM, UInt((conf.PDP_UNIT1D_BW).W)))
val datain_ext = io.pdma2pdp_pd.bits(conf.PDP_UNIT1D_BW-1, 0)
val cur_pooling_dat = MuxLookup(buf_sel, 0.U,
                      (0 to conf.BATCH_PDP_NUM-1 ) map { i => i.U -> data_buf(i) }
                      )
//delay chain
val pipe_vld_d = retiming(Bool(), conf.NVDLA_HLS_ADD17_LATENCY)
val pipe_rdy_d = Wire(Vec((conf.NVDLA_HLS_ADD17_LATENCY+1), Bool()))
val pipe_dp_d = retiming(UInt((conf.PDP_UNIT1D_BW*2 + 12).W), conf.NVDLA_HLS_ADD17_LATENCY)

val int_pooling = Wire(UInt((conf.PDP_UNIT1D_BW).W))
//assign input port
pipe_vld_d(0) := io.pdma2pdp_pd.valid
pipe_dp_d(0) := Cat(io.pooling_din_last, pooling_out_size, io.cur_datin_disable, buf_sel, io.pdp_din_lc_f, io.pooling_din_1st, datain_ext, int_pooling)
 
//data flight
for(t <- 0 to conf.NVDLA_HLS_ADD17_LATENCY-1){
    pipe_rdy_d(t) :=  ~pipe_vld_d(t+1) || pipe_rdy_d(t+1)
    when(pipe_vld_d(t)){
        pipe_vld_d(t+1) := true.B
    }
    .elsewhen(pipe_rdy_d(t+1)){
        pipe_vld_d(t+1) := false.B
    }

    when(pipe_vld_d(t)&pipe_rdy_d(t)){
        pipe_dp_d(t+1) := pipe_dp_d(t)
    }
}  

//output assignment
val pipe_out_rdy = Wire(Bool())
val pooling_out_vld = Wire(Bool())
val add_out_rdy = Wire(Bool())

val pipe_out_vld = pipe_vld_d(conf.NVDLA_HLS_ADD17_LATENCY) 
pipe_rdy_d(conf.NVDLA_HLS_ADD17_LATENCY) := pipe_out_rdy
val pipe_out_pd = pipe_dp_d(conf.NVDLA_HLS_ADD17_LATENCY)

pipe_in_rdy := pipe_rdy_d(0)
pipe_out_rdy := add_out_rdy

val add_out_vld =  pipe_out_vld
add_out_rdy := ~pooling_out_vld | io.pooling_out.ready

////////////////////

val int_pooling_sync = pipe_out_pd(conf.PDP_UNIT1D_BW-1, 0)
val datain_ext_sync = pipe_out_pd(conf.PDP_UNIT1D_BW*2-1, conf.PDP_UNIT1D_BW)
val pooling_din_1st_sync = pipe_out_pd(conf.PDP_UNIT1D_BW*2)
val pdp_din_lc_f_sync = pipe_out_pd(conf.PDP_UNIT1D_BW*2+1)
val buf_sel_sync = pipe_out_pd(conf.PDP_UNIT1D_BW*2+6, conf.PDP_UNIT1D_BW*2+2)
val cur_datin_disable_sync = pipe_out_pd(conf.PDP_UNIT1D_BW*2+7)
val pooling_out_size_sync = pipe_out_pd(conf.PDP_UNIT1D_BW*2+10, conf.PDP_UNIT1D_BW*2+8)
val pooling_din_last_sync = pipe_out_pd(conf.PDP_UNIT1D_BW*2+11)


///
val pool_fun_vld = load_din;
val int_pool_datin_ext = Mux(pool_fun_vld, datain_ext, 0.U)
val int_pool_cur_dat = Mux(pool_fun_vld, cur_pooling_dat, 0.U)
int_pooling := VecInit((0 to conf.NVDLA_PDP_THROUGHPUT - 1) map 
{ i => 
Mux(io.pooling_type_cfg===2.U, pooling_SUM(int_pool_cur_dat(conf.NVDLA_PDP_UNIT1D_BWPE*i+conf.NVDLA_PDP_UNIT1D_BWPE-1, conf.NVDLA_PDP_UNIT1D_BWPE*i), int_pool_datin_ext(conf.NVDLA_PDP_UNIT1D_BWPE*i+conf.NVDLA_PDP_UNIT1D_BWPE-1, conf.NVDLA_PDP_UNIT1D_BWPE*i)),
Mux(io.pooling_type_cfg===1.U, pooling_MIN(int_pool_cur_dat(conf.NVDLA_PDP_UNIT1D_BWPE*i+conf.NVDLA_PDP_UNIT1D_BWPE-1, conf.NVDLA_PDP_UNIT1D_BWPE*i), int_pool_datin_ext(conf.NVDLA_PDP_UNIT1D_BWPE*i+conf.NVDLA_PDP_UNIT1D_BWPE-1, conf.NVDLA_PDP_UNIT1D_BWPE*i), true.B),
Mux(io.pooling_type_cfg===0.U, pooling_MAX(int_pool_cur_dat(conf.NVDLA_PDP_UNIT1D_BWPE*i+conf.NVDLA_PDP_UNIT1D_BWPE-1, conf.NVDLA_PDP_UNIT1D_BWPE*i), int_pool_datin_ext(conf.NVDLA_PDP_UNIT1D_BWPE*i+conf.NVDLA_PDP_UNIT1D_BWPE-1, conf.NVDLA_PDP_UNIT1D_BWPE*i), true.B), 
0.U)))}).asUInt

    

val pooling_result = Mux(pooling_din_1st_sync, datain_ext_sync, int_pooling_sync)
//--------------------------------------------------------------------
//for NVDLA_HLS_ADD17_LATENCY==3
val latch_result_d3 = RegInit(VecInit(Seq.fill(conf.BATCH_PDP_NUM)(0.U)))
when(add_out_vld & add_out_rdy){
    for(i <- 0 to conf.BATCH_PDP_NUM-1){
        when( buf_sel_sync === i.U){
            latch_result_d3(i) := pooling_result
        }
    }
}

//--------------------------------------------------------------------
//for NVDLA_HLS_ADD17_LATENCY==4
val latch_result_d4 = Wire(Vec(conf.BATCH_PDP_NUM, UInt((conf.PDP_UNIT1D_BW).W)))
val latch_result = Wire(Vec(conf.BATCH_PDP_NUM, UInt((conf.PDP_UNIT1D_BW).W)))
val flush_out = RegInit(VecInit(Seq.fill(conf.BATCH_PDP_NUM)(0.U)))

val pooling_out_size_sync_use_d4 = pooling_out_size_sync
val pooling_din_last_sync_use_d4 = pooling_din_last_sync
val buf_sel_sync_use_d4 = buf_sel_sync
val cur_datin_disable_sync_use_d4 = cur_datin_disable_sync
val data_buf_lc_d4 = pdp_din_lc_f_sync

for(i <- 0 to conf.BATCH_PDP_NUM-1){
    latch_result_d4(i) := Mux(buf_sel_sync === i.U, pooling_result, latch_result_d3(i))
    latch_result(i) := latch_result_d4(i)
    data_buf(i) := latch_result(i)
}

//==========
//info select
val pooling_out_size_sync_use = pooling_out_size_sync_use_d4
val pooling_din_last_sync_use = pooling_din_last_sync_use_d4
val buf_sel_sync_use = buf_sel_sync_use_d4
val cur_datin_disable_sync_use = cur_datin_disable_sync_use_d4
val data_buf_lc = data_buf_lc_d4

//============================================================
//pooling send out
//
pooling_out_vld := add_out_vld
io.pooling_out.valid := pooling_out_vld

val pooling_cnt = RegInit(0.U)
when(pooling_out_vld & io.pooling_out.ready & ((pooling_din_last_sync_use & (~cur_datin_disable_sync_use)) | io.last_out_en)){
    when(pooling_cnt === (conf.BATCH_PDP_NUM-1).U){
        pooling_cnt := 0.U
    }
    .otherwise{
        pooling_cnt := pooling_cnt + 1.U
    }
}

when(io.last_out_en){
    io.pooling_out.bits := MuxLookup(pooling_cnt, 0.U, 
                      (0 to conf.BATCH_PDP_NUM-1 ) map { i => i.U -> flush_out(i) }
                        )
}
.otherwise{
    io.pooling_out.bits := MuxLookup(pooling_cnt, 0.U, 
                      (0 to conf.BATCH_PDP_NUM-1 ) map { i => i.U -> Cat(data_buf_lc,pooling_out_size_sync_use,data_buf(i)) }
                      )
}

//////////////////////////////////////////////
//output latch in line end for flush
when(pooling_din_last_sync_use & (~cur_datin_disable_sync_use)){
    for(i <- 0 to conf.BATCH_PDP_NUM-1){
        when( buf_sel_sync === i.U){
            flush_out(i) := Cat(data_buf_lc,pooling_out_size_sync_use,data_buf(i))
        }
    }
}


}}


object NV_NVDLA_PDP_CORE_unit1dDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_PDP_CORE_unit1d())
}