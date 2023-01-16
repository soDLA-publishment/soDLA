package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_PDP_WDMA_dat(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        // clk
        val nvdla_core_clk = Input(Clock())
        val pwrbus_ram_pd = Input(UInt(32.W))

        // dp2wdma
        val dp2wdma_pd = Flipped(DecoupledIO(UInt((conf.NVDLA_PDP_THROUGHPUT*conf.NVDLA_PDP_BWPE).W)))

        // dat_fifo
        val dat_fifo_rd_pd = Vec(conf.ATMM_NUM, Vec(conf.BATCH_PDP_NUM, DecoupledIO(UInt(conf.PDPBW.W))))

        // config
        val reg2dp_cube_out_channel = Input(UInt(13.W))
        val reg2dp_cube_out_height = Input(UInt(13.W))
        val reg2dp_cube_out_width = Input(UInt(13.W))
        val reg2dp_partial_width_out_first = Input(UInt(10.W))
        val reg2dp_partial_width_out_last = Input(UInt(10.W))
        val reg2dp_partial_width_out_mid = Input(UInt(10.W))
        val reg2dp_split_num = Input(UInt(8.W))

       // Read-only register input
        val op_load = Input(Bool())
        val wdma_done = Input(Bool())
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
    val cfg_mode_split = (io.reg2dp_split_num =/= 0.U)
    //==============
    // CUBE DRAW
    //==============
    val is_last_b = Wire(Bool())
    val is_last_w = Wire(Bool())
    val is_last_h = Wire(Bool())
    val is_last_surf = Wire(Bool())
    val is_last_wg = Wire(Bool())
    val is_blk_end = is_last_b
    val is_line_end  = is_blk_end & is_last_w
    val is_surf_end = is_line_end & is_last_h
    val is_split_end = is_surf_end & is_last_surf
    val is_cube_end  = is_split_end & is_last_wg

    // WIDTH COUNT: in width direction, indidate one block 
    val is_fspt = Wire(Bool())
    val is_lspt = Wire(Bool())
    val is_mspt = Wire(Bool())
    val split_size_of_width = Mux(is_fspt, io.reg2dp_partial_width_out_first,
                              Mux(is_lspt, io.reg2dp_partial_width_out_last,
                              Mux(is_mspt, io.reg2dp_partial_width_out_mid, 
                              "b0".asUInt(10.W))))
    val size_of_width = Mux(cfg_mode_split, split_size_of_width, io.reg2dp_cube_out_width)

    // WG: WidthGroup, including one FSPT, one LSPT, and many MSPT
    val spt_dat_accept = Wire(Bool())
    val count_wg = RegInit("b0".asUInt(8.W))
    when(io.op_load){
        when(spt_dat_accept){
            when(is_cube_end){
                count_wg := 0.U
            }
            .elsewhen(is_split_end){
                count_wg := count_wg + 1.U
            }
        }
    }

    is_last_wg := count_wg === io.reg2dp_split_num
    val is_first_wg = count_wg === 0.U

    is_fspt := cfg_mode_split & is_first_wg
    is_lspt := cfg_mode_split & is_last_wg
    is_mspt := cfg_mode_split & ~is_fspt & ~is_lspt

    //================================================================
    // C direction: count_b + count_surf
    // count_b: in each W in line, will go 4 step in c first
    // count_surf: when one surf with 4c is done, will go to next surf
    //================================================================

    //==============
    // COUNT B
    //==============
    val count_b = RegInit(0.U(5.W))
    when(spt_dat_accept){
        when(is_blk_end){
            count_b := 0.U
        }
        .otherwise{
            count_b := count_b + 1.U
        }
    }

    is_last_b := count_b === (conf.BATCH_PDP_NUM - 1).U

    //==============
    // COUNT W
    //==============
    val count_w = RegInit(0.U(13.W))
    when(spt_dat_accept){
        when(is_line_end){
            count_w := 0.U
        }
        .elsewhen(is_blk_end){
            count_w := count_w + 1.U
        }
    }

    is_last_w := (count_w === size_of_width)

    //==============
    // COUNT SURF
    //==============
    val count_surf = RegInit(0.U((13-log2Ceil(conf.NVDLA_MEMORY_ATOMIC_SIZE)).W))
    when(spt_dat_accept){
        when(is_split_end){
            count_surf := 0.U
        }
        .elsewhen(is_surf_end){
            count_surf := count_surf + 1.U
        }
    }

    is_last_surf := count_surf === io.reg2dp_cube_out_channel(12, conf.ATMMBW)

    //==============
    // COUNT HEIGHT 
    //==============
    val count_h = RegInit(0.U(13.W))
    when(spt_dat_accept){
        when(is_surf_end){
            count_h := 0.U
        }
        .elsewhen(is_line_end){
            count_h := count_h + 1.U
        }
    }

    is_last_h := count_h === io.reg2dp_cube_out_height

    //==============
    // spt information gen
    //==============
    val spt_posb = count_b
    val spt_posw = if(conf.ATMM_NUM == 1) "b0".asUInt(2.W) else Cat("b0".asUInt((2 - log2Ceil(conf.ATMM_NUM)).W), count_w(log2Ceil(conf.ATMM_NUM)-1, 0))

    //==============
    // Data FIFO WRITE contrl
    //==============
    val u_dat_fifo = Array.fill(conf.ATMM_NUM){Array.fill(conf.BATCH_PDP_NUM){Module(new NV_NVDLA_fifo_new(depth = 3, width = conf.PDPBW, rd_reg = true, ram_type = 0, ram_bypass = true))}}    
    val u_dat_fifo_wr_prdy = Wire(Vec(conf.ATMM_NUM, Vec(conf.BATCH_PDP_NUM, Bool())))
    for(i <- 0 to conf.ATMM_NUM-1){
        for(j <- 0 to conf.BATCH_PDP_NUM-1){
            // DATA FIFO WRITE SIDE
            // is last_b, then fifo idx large than count_b will need a push to fill in fake data to make up a full atomic_m
            u_dat_fifo(i)(j).io.clk := io.nvdla_core_clk
            u_dat_fifo(i)(j).io.pwrbus_ram_pd := io.pwrbus_ram_pd

            u_dat_fifo(i)(j).io.wr_pvld := io.dp2wdma_pd.valid & (spt_posw === i.U) & (spt_posb === j.U)
            u_dat_fifo_wr_prdy(i)(j) := u_dat_fifo(i)(j).io.wr_prdy & (spt_posw === i.U) & (spt_posb === j.U)
            u_dat_fifo(i)(j).io.wr_pd := io.dp2wdma_pd.bits  

            io.dat_fifo_rd_pd(i)(j).valid := u_dat_fifo(i)(j).io.rd_pvld
            u_dat_fifo(i)(j).io.rd_prdy := io.dat_fifo_rd_pd(i)(j).ready
            io.dat_fifo_rd_pd(i)(j).bits := u_dat_fifo(i)(j).io.rd_pd
        }
    }

    io.dp2wdma_pd.ready := u_dat_fifo_wr_prdy.asUInt.orR
    spt_dat_accept := io.dp2wdma_pd.valid & io.dp2wdma_pd.ready
}}



object NV_NVDLA_PDP_WDMA_datDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_PDP_WDMA_dat())
}