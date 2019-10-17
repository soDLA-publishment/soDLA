package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_PDP_WDMA_cmd(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        // clk
        val nvdla_core_clk = Input(Clock())
        val pwrbus_ram_pd = Input(UInt(32.W))

        // cmd rd
        val cmd_fifo_rd_pd = DecoupledIO(UInt(80.W))

        // config
        val reg2dp_cube_out_channel = Input(UInt(13.W))
        val reg2dp_cube_out_height = Input(UInt(13.W))
        val reg2dp_cube_out_width = Input(UInt(13.W))
        val reg2dp_dst_base_addr_high = Input(UInt(32.W))
        val reg2dp_dst_base_addr_low = Input(UInt(32.W))
        val reg2dp_dst_line_stride = Input(UInt(32.W))
        val reg2dp_dst_surface_stride = Input(UInt(32.W))
        val reg2dp_partial_width_out_first = Input(UInt(10.W))
        val reg2dp_partial_width_out_last = Input(UInt(10.W))
        val reg2dp_partial_width_out_mid = Input(UInt(10.W))
        val reg2dp_split_num = Input(UInt(8.W))

        // Read-only register input
        val op_load = Input(Bool())
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

    val cmd_fifo_wr_prdy = Wire(Bool())
    val is_cube_end = Wire(Bool())
    val op_prcess = RegInit(false.B)
    val op_done = op_prcess & cmd_fifo_wr_prdy & is_cube_end;

    when(io.op_load){
        op_prcess := true.B
    }
    .elsewhen(op_done){
        op_prcess := false.B
    }

    val cmd_fifo_wr_pvld = op_prcess
    val cmd_fifo_wr_accpet = cmd_fifo_wr_pvld & cmd_fifo_wr_prdy

    // SPLIT MODE
    val cfg_mode_split = (io.reg2dp_split_num =/= 0.U)
    val cfg_base_addr = Cat(io.reg2dp_dst_base_addr_high, io.reg2dp_dst_base_addr_low)

    //==============
    // CUBE DRAW
    //==============
    val is_last_wg = Wire(Bool())
    val is_last_h = Wire(Bool())
    val is_last_surf = Wire(Bool())

    val is_line_end  = true.B
    val is_surf_end  = is_line_end & is_last_h;
    val is_split_end = is_surf_end & is_last_surf;
    is_cube_end  := is_split_end & is_last_wg;

    // WIDTH COUNT: in width direction, indidate one block 
    val is_fspt = Wire(Bool())
    val is_lspt = Wire(Bool())
    val is_mspt = Wire(Bool())
    val split_size_of_width = Mux(is_fspt, io.reg2dp_partial_width_out_first,
                              Mux(is_lspt, io.reg2dp_partial_width_out_last,
                              Mux(is_mspt, io.reg2dp_partial_width_out_mid,
                              "b0".asUInt(10.W))))
    val size_of_width = Mux(cfg_mode_split, split_size_of_width, io.reg2dp_cube_out_width)
    val splitw_stride = (size_of_width +& 1.U) << conf.ATMMBW.U

    // WG: WidthGroup, including one FSPT, one LSPT, and many MSPT
    val count_wg = RegInit("b0".asUInt(8.W))
    when(io.op_load){
        count_wg := 0.U
    }
    .elsewhen(cmd_fifo_wr_accpet){
        when(is_split_end){
            count_wg := count_wg + 1.U
        }
    }

    is_last_wg := (count_wg === io.reg2dp_split_num)
    val is_first_wg = (count_wg === 0.U)

    is_fspt := cfg_mode_split & is_first_wg
    is_lspt := cfg_mode_split & is_last_wg
    is_mspt := cfg_mode_split & !is_fspt & !is_lspt

    //==============
    // COUNT SURF
    //==============
    val count_surf = RegInit("b0".asUInt((13-conf.ATMMBW).W))
    when(cmd_fifo_wr_accpet){
        when(is_split_end){
            count_surf := 0.U
        }
        .elsewhen(is_surf_end){
            count_surf := count_surf + 1.U
        }
    }

    is_last_surf := (count_surf === io.reg2dp_cube_out_channel(12, conf.ATMMBW))

    // per Surf
    val count_h = RegInit("b0".asUInt(13.W))
    when(cmd_fifo_wr_accpet){
        when(is_surf_end){
            count_h := 0.U
        }
        .elsewhen(is_line_end){
            count_h := count_h + 1.U
        }
    }

    is_last_h := (count_h === io.reg2dp_cube_out_height)

    //==============
    // ADDR
    //==============
    
    val base_addr_line = RegInit("b0".asUInt(64.W))
    val base_addr_surf = RegInit("b0".asUInt(64.W))
    val base_addr_split = RegInit("b0".asUInt(64.W))

    // LINE
    // SURF
    // SPLIT
    when(io.op_load){
        base_addr_line := cfg_base_addr
        base_addr_surf := cfg_base_addr
        base_addr_split := base_addr_split
    }
    .elsewhen(cmd_fifo_wr_accpet){
        when(is_split_end){
            base_addr_line := base_addr_split + splitw_stride
            base_addr_surf := base_addr_split + splitw_stride
            base_addr_split := base_addr_split + splitw_stride
        }
        .elsewhen(is_surf_end){
            base_addr_line := base_addr_surf + io.reg2dp_dst_surface_stride
            base_addr_surf := base_addr_surf + io.reg2dp_dst_surface_stride
        }
        .elsewhen(is_line_end){
            base_addr_line := base_addr_line + io.reg2dp_dst_line_stride
        }

    }

    //==============
    // CMD FIFO WRITE 
    //==============
    val cmd_fifo_wr_pd = Wire(UInt(80.W))
    val u_fifo = Module{new NV_NVDLA_fifo(depth = 1, width = 80, ram_type = 1, distant_wr_req = true)}
    u_fifo.io.clk := io.nvdla_core_clk
    u_fifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd
    u_fifo.io.wr_pvld := cmd_fifo_wr_pvld
    cmd_fifo_wr_prdy := u_fifo.io.wr_prdy
    u_fifo.io.wr_pd := cmd_fifo_wr_pd
    io.cmd_fifo_rd_pd.valid := u_fifo.io.rd_pvld
    u_fifo.io.rd_prdy := io.cmd_fifo_rd_pd.ready
    io.cmd_fifo_rd_pd.bits := u_fifo.io.rd_pd

    //==============
    // DMA Req : ADDR : Generation
    //==============
    val spt_cmd_addr = base_addr_line
    val spt_cmd_size = size_of_width
    val spt_cmd_lenb = "b0".asUInt(2.W)
    val spt_cmd_cube_end = is_cube_end

    cmd_fifo_wr_pd := Cat(spt_cmd_cube_end, spt_cmd_lenb, spt_cmd_size, spt_cmd_addr)

}}




object NV_NVDLA_PDP_WDMA_cmdDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_PDP_WDMA_cmd())
}