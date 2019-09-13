package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._
import chisel3.iotesters.Driver


class NV_NVDLA_CDMA_shared_buffer(implicit conf: nvdlaConfig) extends Module {

    val io = IO(new Bundle {
        //nvdla core clock
        val nvdla_core_clk = Input(Clock())
        val pwrbus_ram_pd = Input(UInt(32.W))

        //write port
        //dc2sbuf
        val dc2sbuf_p_wr = Vec(2, Flipped(new nvdla_wr_if(8, conf.CDMA_SBUF_SDATA_BITS)))
        //img2sbuf
        val img2sbuf_p_wr = Vec(2, Flipped(new nvdla_wr_if(8, conf.CDMA_SBUF_SDATA_BITS)))
        //read port
        //dc2sbuf
        val dc2sbuf_p_rd = Vec(2, Flipped(new nvdla_rd_if(8, conf.CDMA_SBUF_SDATA_BITS)))
        //img2sbuf
        val img2sbuf_p_rd = Vec(2, Flipped(new nvdla_rd_if(8, conf.CDMA_SBUF_SDATA_BITS)))      
 
    })
    
    //      ┌─┐       ┌─┐
    //   ┌──┘ ┴───────┘ ┴──┐
    //   │                 │
    //   │       ───       │
    //   │  ─┬┘       └┬─  │
    //   │                 │
    //   │       ─┴─       │
    //   │                 │
    //   └───┐         ┌───┘
    //       │         │    
    //       │         │
    //       │         │
    //       │         └──────────────┐
    //       │                        │
    //       │                        ├─┐
    //       │                        ┌─┘    
    //       │                        │
    //       └─┐  ┐  ┌───────┬──┐  ┌──┘         
    //         │ ─┤ ─┤       │ ─┤ ─┤         
    //         └──┴──┘       └──┴──┘ 
withClock(io.nvdla_core_clk){
////////////////////////////////////////////////////////////////////////
// Input port to RAMS                                                 //
////////////////////////////////////////////////////////////////////////
val b1 = log2Ceil(conf.CDMA_SBUF_DEPTH)
val b0 = log2Ceil(conf.CDMA_SBUF_DEPTH) - log2Ceil(conf.CDMA_SBUF_NUMBER)

val dc2sbuf_p0_wr_bsel = io.dc2sbuf_p_wr(0).addr.bits(b1-1, b0)
val img2sbuf_p0_wr_bsel = io.img2sbuf_p_wr(0).addr.bits(b1-1, b0)
val dc2sbuf_p1_wr_bsel = io.dc2sbuf_p_wr(1).addr.bits(b1-1, b0)
val img2sbuf_p1_wr_bsel = io.img2sbuf_p_wr(1).addr.bits(b1-1, b0)

val dc2sbuf_p0_wr_sel = VecInit((0 to conf.CDMA_SBUF_NUMBER-1) map {i => (dc2sbuf_p0_wr_bsel === i.U)&(io.dc2sbuf_p_wr(0).addr.valid)})
val dc2sbuf_p1_wr_sel = VecInit((0 to conf.CDMA_SBUF_NUMBER-1) map {i => (dc2sbuf_p1_wr_bsel === i.U)&(io.dc2sbuf_p_wr(1).addr.valid)})
val img2sbuf_p0_wr_sel = VecInit((0 to conf.CDMA_SBUF_NUMBER-1) map {i => (img2sbuf_p0_wr_bsel === i.U)&(io.img2sbuf_p_wr(0).addr.valid)})
val img2sbuf_p1_wr_sel = VecInit((0 to conf.CDMA_SBUF_NUMBER-1) map {i => (img2sbuf_p1_wr_bsel === i.U)&(io.img2sbuf_p_wr(1).addr.valid)})

val sbuf_we = VecInit((0 to conf.CDMA_SBUF_NUMBER-1) map {i => dc2sbuf_p0_wr_sel(i)|dc2sbuf_p1_wr_sel(i)|img2sbuf_p0_wr_sel(i)|img2sbuf_p1_wr_sel(i) })
val sbuf_wa = VecInit((0 to conf.CDMA_SBUF_NUMBER-1) map {
                    i => (Fill(b1, dc2sbuf_p0_wr_sel(i))&io.dc2sbuf_p_wr(0).addr.bits(b0-1, 0))|
                         (Fill(b1, dc2sbuf_p1_wr_sel(i))&io.dc2sbuf_p_wr(1).addr.bits(b0-1, 0))|
                         (Fill(b1, img2sbuf_p0_wr_sel(i))&io.img2sbuf_p_wr(0).addr.bits(b0-1, 0))|
                         (Fill(b1, img2sbuf_p1_wr_sel(i))&io.img2sbuf_p_wr(1).addr.bits(b0-1, 0)) 
             })
val sbuf_wdat = VecInit((0 to conf.CDMA_SBUF_NUMBER-1) map {
                    i => (Fill(conf.CDMA_SBUF_SDATA_BITS, dc2sbuf_p0_wr_sel(i))&io.dc2sbuf_p_wr(0).data)|
                         (Fill(conf.CDMA_SBUF_SDATA_BITS, dc2sbuf_p1_wr_sel(i))&io.dc2sbuf_p_wr(1).data)|
                         (Fill(conf.CDMA_SBUF_SDATA_BITS, img2sbuf_p0_wr_sel(i))&io.img2sbuf_p_wr(0).data)|
                         (Fill(conf.CDMA_SBUF_SDATA_BITS, img2sbuf_p1_wr_sel(i))&io.img2sbuf_p_wr(1).data) 
             })

val dc2sbuf_p0_rd_bsel = io.dc2sbuf_p_rd(0).addr.bits(b1-1, b0)
val img2sbuf_p0_rd_bsel = io.img2sbuf_p_rd(0).addr.bits(b1-1, b0)
val dc2sbuf_p1_rd_bsel = io.dc2sbuf_p_rd(1).addr.bits(b1-1, b0)
val img2sbuf_p1_rd_bsel = io.img2sbuf_p_rd(1).addr.bits(b1-1, b0)

val dc2sbuf_p0_rd_sel = VecInit((0 to conf.CDMA_SBUF_NUMBER-1) map {i => (dc2sbuf_p0_rd_bsel === i.U)&(io.dc2sbuf_p_rd(0).addr.valid)})
val dc2sbuf_p1_rd_sel = VecInit((0 to conf.CDMA_SBUF_NUMBER-1) map {i => (dc2sbuf_p1_rd_bsel === i.U)&(io.dc2sbuf_p_rd(1).addr.valid)})
val img2sbuf_p0_rd_sel = VecInit((0 to conf.CDMA_SBUF_NUMBER-1) map {i => (img2sbuf_p0_rd_bsel === i.U)&(io.img2sbuf_p_rd(0).addr.valid)})
val img2sbuf_p1_rd_sel = VecInit((0 to conf.CDMA_SBUF_NUMBER-1) map {i => (img2sbuf_p1_rd_bsel === i.U)&(io.img2sbuf_p_rd(1).addr.valid)})

val sbuf_p0_re = VecInit((0 to conf.CDMA_SBUF_NUMBER-1) map {i => dc2sbuf_p0_rd_sel(i)|img2sbuf_p0_rd_sel(i)})
val sbuf_p1_re = VecInit((0 to conf.CDMA_SBUF_NUMBER-1) map {i => dc2sbuf_p1_rd_sel(i)|img2sbuf_p1_rd_sel(i)})
val sbuf_re = VecInit((0 to conf.CDMA_SBUF_NUMBER-1) map {i => sbuf_p0_re(i)|sbuf_p1_re(i)})

val sbuf_ra = VecInit((0 to conf.CDMA_SBUF_NUMBER-1) map {
                    i => (Fill(b1, dc2sbuf_p0_rd_sel(i))&io.dc2sbuf_p_rd(0).addr.bits(b0-1, 0))|
                         (Fill(b1, dc2sbuf_p1_rd_sel(i))&io.dc2sbuf_p_rd(1).addr.bits(b0-1, 0))|
                         (Fill(b1, img2sbuf_p0_rd_sel(i))&io.img2sbuf_p_rd(0).addr.bits(b0-1, 0))|
                         (Fill(b1, img2sbuf_p1_rd_sel(i))&io.img2sbuf_p_rd(1).addr.bits(b0-1, 0)) 
             })

////////////////////////////////////////////////////////////////////////\n";
// Instance 16 256bx8 RAMs as local shared buffers                    //\n";
////////////////////////////////////////////////////////////////////////\n";

val u_shared_buffer = Array.fill(conf.CDMA_SBUF_NUMBER){Module(new nv_ram_rws(conf.CDMA_SBUF_DEPTH/conf.CDMA_SBUF_NUMBER, conf.CDMA_SBUF_SDATA_BITS))}
val sbuf_rdat = Wire(Vec(conf.CDMA_SBUF_NUMBER, UInt(conf.CDMA_SBUF_SDATA_BITS.W)))
for(i <- 0 to conf.CDMA_SBUF_NUMBER-1){
    u_shared_buffer(i).io.clk := io.nvdla_core_clk
    u_shared_buffer(i).io.ra := sbuf_ra(i)
    u_shared_buffer(i).io.re := sbuf_re(i)
    u_shared_buffer(i).io.wa := sbuf_wa(i)
    u_shared_buffer(i).io.we := sbuf_we(i)
    u_shared_buffer(i).io.di := sbuf_wdat(i)
    sbuf_rdat(i) := u_shared_buffer(i).io.dout
}

////////////////////////////////////////////////////////////////////////\n";
// RAMs to output port: stage 1                                       //\n";
////////////////////////////////////////////////////////////////////////\n";
val sbuf_p0_re_norm_d1 = RegInit(VecInit(Seq.fill(conf.CDMA_SBUF_NUMBER)(false.B)))
val sbuf_p1_re_norm_d1 = RegInit(VecInit(Seq.fill(conf.CDMA_SBUF_NUMBER)(false.B)))
for(i <- 0 to conf.CDMA_SBUF_NUMBER-1){
    sbuf_p0_re_norm_d1(i) := sbuf_p0_re(i)
    sbuf_p1_re_norm_d1(i) := sbuf_p1_re(i)
}

val sbuf_p0_rd_en_d1 = RegNext(io.dc2sbuf_p_rd(0).addr.valid | io.img2sbuf_p_rd(0).addr.valid, false.B)
val sbuf_p1_rd_en_d1 = RegNext(io.dc2sbuf_p_rd(1).addr.valid | io.img2sbuf_p_rd(1).addr.valid, false.B)

////////////////////////////////////////////////////////////////////////\n";
// RAMs to output port: stage2                                        //\n";
////////////////////////////////////////////////////////////////////////\n";

val sbuf_p0_rdat = VecInit((0 to conf.CDMA_SBUF_NUMBER-1) map {
                        i => Fill(conf.CDMA_SBUF_NUMBER, sbuf_p0_re_norm_d1(i))&sbuf_rdat(i)}).reduce(_|_)
val sbuf_p1_rdat = VecInit((0 to conf.CDMA_SBUF_NUMBER-1) map {
                        i => Fill(conf.CDMA_SBUF_NUMBER, sbuf_p1_re_norm_d1(i))&sbuf_rdat(i)}).reduce(_|_)
////////////////////////////////////////////////////////////////////////\n";
// RAMs to output port: stage2 register //\n";
////////////////////////////////////////////////////////////////////////\n";
val sbuf_p0_rdat_d2 = RegEnable(sbuf_p0_rdat, sbuf_p0_rd_en_d1)
val sbuf_p1_rdat_d2 = RegEnable(sbuf_p1_rdat, sbuf_p1_rd_en_d1)

io.dc2sbuf_p_rd(0).data := sbuf_p0_rdat_d2
io.img2sbuf_p_rd(0).data := sbuf_p0_rdat_d2;
io.dc2sbuf_p_rd(1).data := sbuf_p1_rdat_d2;
io.img2sbuf_p_rd(1).data := sbuf_p1_rdat_d2;


}}

object NV_NVDLA_CDMA_shared_bufferDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_CDMA_shared_buffer())
}

