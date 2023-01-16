

package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

@chiselName
class NV_NVDLA_CMAC_core(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //clock
        val nvdla_clock = Flipped(new nvdla_clock_if)
        val nvdla_core_rstn = Input(Bool())
        val slcg_op_en = Input(UInt(conf.CMAC_SLCG_NUM.W))

        //odif
        val sc2mac_dat = Flipped(ValidIO(new csc2cmac_data_if))  /* data valid */
        val sc2mac_wt = Flipped(ValidIO(new csc2cmac_wt_if))  /* data valid */
        val mac2accu = ValidIO(new cmac2cacc_if) /* data valid */

        //reg
        val dp2reg_done = Output(Bool())

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
withClockAndReset(io.nvdla_clock.nvdla_core_clk, ~io.nvdla_core_rstn){

    //==========================================================
    // interface with register config
    //==========================================================
    val nvdla_op_gated_clk = Wire(Vec(conf.CMAC_ATOMK_HALF+3, Clock()))

    //==========================================================
    // input retiming logic
    //==========================================================
    val u_rt_in = Module(new NV_NVDLA_CMAC_CORE_rt_in)

    u_rt_in.io.nvdla_core_clk := nvdla_op_gated_clk(conf.CMAC_ATOMK_HALF)
    u_rt_in.io.nvdla_core_rstn := io.nvdla_core_rstn
    u_rt_in.io.sc2mac_dat <> io.sc2mac_dat
    u_rt_in.io.sc2mac_wt <> io.sc2mac_wt

    //==========================================================
    // input shadow and active pipeline
    //==========================================================
    val u_active = Module(new NV_NVDLA_CMAC_CORE_active)

    u_active.io.nvdla_core_clk := nvdla_op_gated_clk(conf.CMAC_ATOMK_HALF+1)
    u_active.io.nvdla_core_rstn := io.nvdla_core_rstn
    u_active.io.in_dat <> u_rt_in.io.in_dat
    u_active.io.in_dat_stripe_st := u_rt_in.io.in_dat.bits.pd(conf.PKT_nvdla_stripe_info_stripe_st_FIELD)                 //|< w
    u_active.io.in_dat_stripe_end := u_rt_in.io.in_dat.bits.pd(conf.PKT_nvdla_stripe_info_stripe_end_FIELD)               //|< w
    u_active.io.in_wt <> u_rt_in.io.in_wt

    //==========================================================
    // MAC CELLs
    //==========================================================
    val u_mac = Array.fill(conf.CMAC_ATOMK_HALF){Module(new NV_NVDLA_CMAC_CORE_mac)}
    val u_rt_out = Module(new NV_NVDLA_CMAC_CORE_rt_out)  // use seq

    for(i<- 0 to conf.CMAC_ATOMK_HALF-1){

        u_mac(i).io.nvdla_core_clk := nvdla_op_gated_clk(i)
        u_mac(i).io.nvdla_core_rstn := io.nvdla_core_rstn

        u_mac(i).io.dat_actv <> u_active.io.dat_actv(i)
        u_mac(i).io.wt_actv <> u_active.io.wt_actv(i)

        u_rt_out.io.out.bits.mask(i) := u_mac(i).io.mac_out.valid
        u_rt_out.io.out.bits.data(i) := u_mac(i).io.mac_out.bits
    }

    //==========================================================
    // output retiming logic
    //==========================================================
    u_rt_out.io.nvdla_core_clk := nvdla_op_gated_clk(conf.CMAC_ATOMK_HALF+2)
    u_rt_out.io.nvdla_core_rstn := io.nvdla_core_rstn

    val u_rt_valid_d = retiming(Bool(), conf.MAC_PD_LATENCY)
    val u_rt_pd_d = retiming(UInt(conf.CMAC_RESULT_WIDTH.W), conf.MAC_PD_LATENCY)

    u_rt_valid_d(0) := u_rt_in.io.in_dat.valid
    u_rt_pd_d(0) := u_rt_in.io.in_dat.bits.pd

    for(t <- 0 to conf.MAC_PD_LATENCY-1){
        u_rt_valid_d(t+1) := u_rt_valid_d(t)
        when(u_rt_valid_d(t)){
            u_rt_pd_d(t+1) := u_rt_pd_d(t)
        }
    }

    u_rt_out.io.out.valid := u_rt_valid_d(conf.MAC_PD_LATENCY)
    u_rt_out.io.out.bits.pd := u_rt_pd_d(conf.MAC_PD_LATENCY)

    io.dp2reg_done := u_rt_out.io.dp2reg_done                   //|> o
    io.mac2accu <> u_rt_out.io.mac2accu         //|> o )
    //==========================================================
    // SLCG groups
    //==========================================================
    val u_slcg_op = Array.fill(conf.CMAC_ATOMK_HALF+3){Module(new NV_NVDLA_slcg(1, false))}

    for(i<- 0 to conf.CMAC_ATOMK_HALF+2){
        u_slcg_op(i).io.nvdla_clock := io.nvdla_clock
        u_slcg_op(i).io.slcg_en(0) := io.slcg_op_en(i)
        nvdla_op_gated_clk(i) := u_slcg_op(i).io.nvdla_core_gated_clk
    }

}}

object NV_NVDLA_CMAC_coreDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_CMAC_core)
}

