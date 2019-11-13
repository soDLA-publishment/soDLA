package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class cdp_dp_lut_reg2dp_if extends Bundle{
    val access_type = Output(Bool())
    val addr = Output(UInt(10.W))
    val data = Output(UInt(16.W))
    val data_trigger = Output(Bool())
    val hybrid_priority = Output(Bool())
    val oflow_priority = Output(Bool())
    val table_id = Output(Bool())
    val uflow_priority = Output(Bool())

}

class NV_NVDLA_CDP_DP_lut(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_clk_orig = Input(Clock())

        val dp2lut = Flipped(DecoupledIO(Vec(conf.NVDLA_CDP_THROUGHPUT, new cdp_dp_lut_ctrl_dp2lut_if)))
        val reg2dp_lut = Flipped(new cdp_dp_lut_reg2dp_if)  
        val dp2reg_lut_data = Output(UInt(16.W))

        val lut2intp = DecoupledIO(Vec(conf.NVDLA_CDP_THROUGHPUT, new cdp_dp_intp_lut2intp_in_if))
    })
////////////////////////////////////////////////////////////////////////////
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
    //==============
    // Work Processing
    //==============
    val lut_wr_en = (io.reg2dp_lut.access_type === 1.U) && io.reg2dp_lut.data_trigger
    val raw_select = (io.reg2dp_lut.table_id === 0.U)

    //==========================================
    //LUT write 
    //------------------------------------------
    //need update foreach value if LUT depth update
    val raw_reg_temp = withClock(io.nvdla_core_clk_orig){RegInit(VecInit(Seq.fill(65)(0.U(16.W))))}
    val raw_reg = Wire(Vec(66, UInt(16.W)))
    for(i <- 0 to 64){
        when(lut_wr_en & raw_select){
            when(io.reg2dp_lut.addr === i.U){
                raw_reg_temp(i) := io.reg2dp_lut.data
            }
        }
        raw_reg(i) := raw_reg_temp(i)
    }
    raw_reg(65) := raw_reg_temp(64)

    val density_reg_temp = withClock(io.nvdla_core_clk_orig){RegInit(VecInit(Seq.fill(257)(0.U(16.W))))}
    val density_reg = Wire(Vec(258, UInt(16.W)))
    for(i <- 0 to 256){
        when(lut_wr_en & (~raw_select)){
            when(io.reg2dp_lut.addr === i.U){
                density_reg_temp(i) := io.reg2dp_lut.data
            }
        }
        density_reg(i) := density_reg_temp(i)
    }
    density_reg(257) := density_reg_temp(256)

    //==========================================
    //LUT read
    //------------------------------------------
    val raw_out = withClock(io.nvdla_core_clk_orig){RegInit(0.U(16.W))}
    for(i <- 0 to 64){
        when(io.reg2dp_lut.addr === i.U){
            raw_out := raw_reg(i)
        }
    }

    val density_out = withClock(io.nvdla_core_clk_orig){RegInit(0.U(16.W))}
    for(i <- 0 to 256){
        when(io.reg2dp_lut.addr === i.U){
            density_out := density_reg(i)
        }
    }
    io.dp2reg_lut_data := Mux(raw_select, raw_out, density_out)

    //==========================================
    //data to DP
    //------------------------------------------
    val dp2lut_prdy_f = ~io.lut2intp.valid | io.lut2intp.ready
    val load_din = io.dp2lut.valid & dp2lut_prdy_f
    io.dp2lut.ready := dp2lut_prdy_f

    /////////////////////////////////
    //lut look up select control
    /////////////////////////////////
    val both_hybrid_sel = (io.reg2dp_lut.hybrid_priority === 1.U)
    val both_of_sel = (io.reg2dp_lut.oflow_priority === 1.U)
    val both_uf_sel = (io.reg2dp_lut.uflow_priority === 1.U)

    val lut_x_sel = withClock(io.nvdla_core_clk_orig){Reg(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))}
   
    for(i <- 0 until conf.NVDLA_CDP_THROUGHPUT){
        lut_x_sel(i) := MuxLookup(Cat(io.dp2lut.bits(i).x_info(17,16), io.dp2lut.bits(i).y_info(17,16)), false.B,
            Array(
                "b0000".asUInt(4.W) -> ~both_hybrid_sel, //both hit, or one uflow and the other oflow
                "b0110".asUInt(4.W) -> ~both_hybrid_sel, //both hit, or one uflow and the other oflow
                "b1001".asUInt(4.W) -> ~both_hybrid_sel, //both hit, or one uflow and the other oflow
                "b0001".asUInt(4.W) -> true.B, //X hit, Y uflow/oflow
                "b0010".asUInt(4.W) -> true.B, //X hit, Y uflow/oflow
                "b0100".asUInt(4.W) -> false.B, //X uflow/oflow, Y hit 
                "b1000".asUInt(4.W) -> false.B, //X uflow/oflow, Y hit 
                "b0101".asUInt(4.W) -> ~both_uf_sel, //both uflow 
                "b1010".asUInt(4.W) -> ~both_of_sel //both oflow 
            )
        )
    }

    val lut_y_sel = withClock(io.nvdla_core_clk_orig){Reg(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))}
   
    for(i <- 0 until conf.NVDLA_CDP_THROUGHPUT){
        lut_y_sel(i) := MuxLookup(Cat(io.dp2lut.bits(i).x_info(17,16), io.dp2lut.bits(i).y_info(17,16)), false.B,
            Array(
                "b0000".asUInt(4.W) -> both_hybrid_sel, //both hit, or one uflow and the other oflow
                "b0110".asUInt(4.W) -> both_hybrid_sel, //both hit, or one uflow and the other oflow
                "b1001".asUInt(4.W) -> both_hybrid_sel, //both hit, or one uflow and the other oflow
                "b0001".asUInt(4.W) -> false.B, //X hit, Y uflow/oflow
                "b0010".asUInt(4.W) -> false.B, //X hit, Y uflow/oflow
                "b0100".asUInt(4.W) -> true.B, //X uflow/oflow, Y hit 
                "b1000".asUInt(4.W) -> true.B, //X uflow/oflow, Y hit 
                "b0101".asUInt(4.W) -> both_uf_sel, //both uflow 
                "b1010".asUInt(4.W) -> both_of_sel //both oflow 
            )
        )
    }

    val lut_x_data_0 = RegInit(VecInit(Seq.fill(conf.NVDLA_CDP_THROUGHPUT)(0.U(16.W))))
    val lut_x_data_1 = RegInit(VecInit(Seq.fill(conf.NVDLA_CDP_THROUGHPUT)(0.U(16.W))))
    for(i <- 0 until conf.NVDLA_CDP_THROUGHPUT){
        when(load_din & lut_x_sel(i)){
            when(io.dp2lut.bits(i).x_info(16)){
                lut_x_data_0(i) := raw_reg(0)
                lut_x_data_1(i) := raw_reg(0)
            }.elsewhen(io.dp2lut.bits(i).x_info(17)){
                lut_x_data_0(i) := raw_reg(64)
                lut_x_data_1(i) := raw_reg(64)
            }.otherwise{
                lut_x_data_0(i) := MuxLookup(io.dp2lut.bits(i).x_entry, raw_reg(0),
                (0 to 64) map {j => j.U -> raw_reg(j)})
                lut_x_data_1(i) := MuxLookup(io.dp2lut.bits(i).x_entry, raw_reg(0),
                (0 to 64) map {j => j.U -> raw_reg(j+1)})                       
            }
        }
    }

    val lut_y_data_0 = RegInit(VecInit(Seq.fill(conf.NVDLA_CDP_THROUGHPUT)(0.U(16.W))))
    val lut_y_data_1 = RegInit(VecInit(Seq.fill(conf.NVDLA_CDP_THROUGHPUT)(0.U(16.W))))
    for(i <- 0 until conf.NVDLA_CDP_THROUGHPUT){
        when(load_din & lut_y_sel(i)){
            when(io.dp2lut.bits(i).y_info(16)){
                lut_y_data_0(i) := density_reg(0)
                lut_y_data_1(i) := density_reg(0)
            }.elsewhen(io.dp2lut.bits(i).y_info(17)){
                lut_y_data_0(i) := density_reg(256)
                lut_y_data_1(i) := density_reg(256)
            }.otherwise{
                lut_y_data_0(i) := MuxLookup(io.dp2lut.bits(i).y_entry, density_reg(0),
                (0 to 256) map {j => j.U -> density_reg(j)})
                lut_y_data_1(i) := MuxLookup(io.dp2lut.bits(i).y_entry, density_reg(0),
                (0 to 256) map {j => j.U -> density_reg(j+1)})
            }
        }
    }

////////////////
    val lut_x_info = RegInit(VecInit(Seq.fill(conf.NVDLA_CDP_THROUGHPUT)(0.U(18.W))))
    for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT-1)){
        when(load_din){
            lut_x_info(i) := io.dp2lut.bits(i).x_info
        }
    }
    
    val lutx_sel = RegInit(0.U(conf.NVDLA_CDP_THROUGHPUT.W))
    when(load_din){
        lutx_sel := lut_x_sel.asUInt
    }


    val lut_y_info = RegInit(VecInit(Seq.fill(conf.NVDLA_CDP_THROUGHPUT)(0.U(18.W))))
    for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT-1)){
        when(load_din){
            lut_y_info(i) := io.dp2lut.bits(i).y_info
        }
    }

    val luty_sel = RegInit(0.U(conf.NVDLA_CDP_THROUGHPUT.W))
    when(load_din){
        luty_sel := lut_y_sel.asUInt
    }

////////////////
    val lutx_data_0 = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(16.W)))
    val lutx_data_1 = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(16.W)))
    val lutx_info = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(16.W)))

    for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT-1)){
        lutx_data_0(i) := Mux(lutx_sel(i), lut_x_data_0(i), Mux(luty_sel(i), lut_y_data_0(i), 0.U))
        lutx_data_1(i) := Mux(lutx_sel(i), lut_x_data_1(i), Mux(luty_sel(i), lut_y_data_1(i), 0.U))
        lutx_info(i) := Mux(lutx_sel(i), lut_x_info(i), Mux(luty_sel(i), lut_y_info(i), 0.U))
    }

    val lut2intp_pvld_out = RegInit(false.B)
    when(io.dp2lut.valid){
        lut2intp_pvld_out := true.B
    }.elsewhen(io.lut2intp.ready){
        lut2intp_pvld_out := false.B
    }
    io.lut2intp.valid := lut2intp_pvld_out

///////////////////////////////////////////////////////////////
//output data
///////////////////////////////////////////////////////////////

    for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT-1)){
        io.lut2intp.bits(i).x_data_0 := Cat(Fill(16, lutx_data_0(i)(15)), lutx_data_0(i))
        io.lut2intp.bits(i).x_data_1 := Cat(Fill(16, lutx_data_1(i)(15)), lutx_data_1(i))
        io.lut2intp.bits(i).x_data_0_17b := Cat(lutx_data_0(i)(15), lutx_data_0(i))
        io.lut2intp.bits(i).x_info := Cat(lut_y_info(i)(17,16), lut_x_info(i)(17,16), lutx_info(i))
        io.lut2intp.bits(i).x_sel := lutx_sel(i)
        io.lut2intp.bits(i).y_sel := luty_sel(i)
    }

}}


object NV_NVDLA_CDP_DP_lutDriver extends App {
    implicit val conf: nvdlaConfig = new nvdlaConfig
    chisel3.Driver.execute(args, () => new NV_NVDLA_CDP_DP_lut())
}
