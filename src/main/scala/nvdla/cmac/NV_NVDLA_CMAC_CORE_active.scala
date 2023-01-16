package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._


//this module is to active dat and wt
@chiselName
class NV_NVDLA_CMAC_CORE_active(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

        // input_dat
        val in_dat = Flipped(ValidIO(new csc2cmac_data_if))  /* data valid */
        val in_dat_stripe_st = Input(Bool())
        val in_dat_stripe_end = Input(Bool())

        //odif
        // input_wt
        val in_wt = Flipped(ValidIO(new csc2cmac_wt_if))  /* data valid */

        // atomk, atomc, data&wt
        val dat_actv = Vec(conf.CMAC_ATOMK_HALF, Vec(conf.CMAC_ATOMC, ValidIO(new cmac_core_actv)))
        val wt_actv = Vec(conf.CMAC_ATOMK_HALF, Vec(conf.CMAC_ATOMC, ValidIO(new cmac_core_actv)))
            
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
//           │                        │        wt : in --> pre --> sd --> actv 
//           │                        ├─┐      dat: in --> pre ---------> actv
//           │                        ┌─┘    
//           │                        │
//           └─┐  ┐  ┌───────┬──┐  ┌──┘         
//             │ ─┤ ─┤       │ ─┤ ─┤         
//             └──┴──┘       └──┴──┘ 
withClockAndReset(io.nvdla_core_clk, ~io.nvdla_core_rstn){
//==========================================================
// wt&dat:in --> pre
//==========================================================   
    // wt
    val wt_pre_nz = RegInit(VecInit(Seq.fill(conf.CMAC_ATOMC)(false.B)))
    val wt_pre_data = Reg(Vec(conf.CMAC_ATOMC, UInt(conf.CMAC_BPE.W)))
    val wt_pre_sel = RegInit(VecInit(Seq.fill(conf.CMAC_ATOMK_HALF)(false.B)))

    when(io.in_wt.valid){
        wt_pre_nz := io.in_wt.bits.mask
        for(i <- 0 to conf.CMAC_ATOMC-1){
            when(io.in_wt.bits.mask(i)){
                wt_pre_data(i) := io.in_wt.bits.data(i)
            }
        }   
    } 
    for(i <- 0 to conf.CMAC_ATOMK_HALF-1){
        wt_pre_sel(i) := io.in_wt.bits.sel(i) & io.in_wt.valid
    }

    //dat
    val dat_pre_nz = RegInit(VecInit(Seq.fill(conf.CMAC_ATOMC)(false.B)))
    val dat_pre_data = Reg(Vec(conf.CMAC_ATOMC, UInt(conf.CMAC_BPE.W)))
    val dat_pre_pvld = RegInit(false.B) 
    val dat_pre_stripe_st = RegInit(false.B) 
    val dat_pre_stripe_end = RegInit(false.B) 

    dat_pre_pvld := io.in_dat.valid
    when(io.in_dat.valid){
        dat_pre_nz := io.in_dat.bits.mask
        for(i <- 0 to conf.CMAC_ATOMC-1){
            when(io.in_dat.bits.mask(i)){
                dat_pre_data(i):=io.in_dat.bits.data(i)
            }
        } 
    }

    dat_pre_stripe_st := io.in_dat_stripe_st & io.in_dat.valid//strip start
    dat_pre_stripe_end := io.in_dat_stripe_end & io.in_dat.valid //strip end

//==========================================================
// wt:pre --> sd  this is a push and pop, when strip end, push weight, when strip start, pop weight
//==========================================================  
    //push input weight into shadow

    val wt_sd_pvld = RegInit(VecInit(Seq.fill(conf.CMAC_ATOMK_HALF)(false.B)))
    val wt_sd_nz = RegInit(VecInit(Seq.fill(conf.CMAC_ATOMK_HALF)(VecInit(Seq.fill(conf.CMAC_ATOMC)(false.B)))))
    val wt_sd_data = Reg(Vec(conf.CMAC_ATOMK_HALF, Vec(conf.CMAC_ATOMC, UInt(conf.CMAC_BPE.W))))
    val dat_actv_stripe_end = RegInit(false.B)
        
    for(i <- 0 to conf.CMAC_ATOMK_HALF-1){
        wt_sd_pvld(i) := Mux(wt_pre_sel(i), true.B, Mux(dat_pre_stripe_st, false.B, wt_sd_pvld(i)))
        when(wt_pre_sel(i)){
            wt_sd_nz(i) := wt_pre_nz
            for (j <- 0 to conf.CMAC_ATOMC-1){
                when(wt_pre_nz(j)){
                    wt_sd_data(i)(j) := wt_pre_data(j)
                }
            }
        }
    } 

    dat_actv_stripe_end := dat_pre_stripe_end

//==========================================================
// wt:sd --> actv  this is a push and pop, when strip end, push weight, when strip start, pop weight
//==========================================================  
    //pop weight from shadow when new stripe begin.

    val wt_actv_vld = RegInit(VecInit(Seq.fill(conf.CMAC_ATOMK_HALF)(false.B)))
    val wt_actv_pvld = RegInit(VecInit(Seq.fill(conf.CMAC_ATOMK_HALF)(VecInit(Seq.fill(conf.CMAC_ATOMC)(false.B)))))
    val wt_actv_pvld_w = Wire(Vec(conf.CMAC_ATOMK_HALF,Bool()))
    val wt_actv_nz = RegInit(VecInit(Seq.fill(conf.CMAC_ATOMK_HALF)(VecInit(Seq.fill(conf.CMAC_ATOMC)(false.B)))))
    val wt_actv_data = Reg(Vec(conf.CMAC_ATOMK_HALF, Vec(conf.CMAC_ATOMC, UInt(conf.CMAC_BPE.W))))
        
    for(i <- 0 to conf.CMAC_ATOMK_HALF-1){
        wt_actv_pvld_w(i) := Mux(dat_pre_stripe_st, wt_sd_pvld(i), Mux(dat_actv_stripe_end, false.B, wt_actv_vld(i)))
        wt_actv_vld(i) := wt_actv_pvld_w(i)
        for (j <- 0 to conf.CMAC_ATOMC-1){
            wt_actv_pvld(i)(j) := wt_actv_pvld_w(i)
            when(dat_pre_stripe_st & wt_actv_pvld_w(i)){
                wt_actv_nz(i)(j) := wt_sd_nz(i)(j)
                wt_actv_data(i)(j) := Fill(conf.CMAC_BPE, wt_sd_nz(i)(j)) & wt_sd_data(i)(j)
            }
        }
    } 

//==========================================================
// dat:pre --> actv
//==========================================================  
    val dat_actv_pvld = RegInit(VecInit(Seq.fill(conf.CMAC_ATOMK_HALF)(VecInit(Seq.fill(conf.CMAC_ATOMC)(false.B)))))
    val dat_actv_nz = RegInit(VecInit(Seq.fill(conf.CMAC_ATOMK_HALF)(VecInit(Seq.fill(conf.CMAC_ATOMC)(false.B)))))
    val dat_actv_data = Reg(Vec(conf.CMAC_ATOMK_HALF, Vec(conf.CMAC_ATOMC, UInt(conf.CMAC_BPE.W))))

    for(i <- 0 to conf.CMAC_ATOMK_HALF-1){
        for (j <- 0 to conf.CMAC_ATOMC-1){
            dat_actv_pvld(i)(j) := dat_pre_pvld
            when(dat_pre_pvld){
                dat_actv_nz(i)(j) := dat_pre_nz(j)   
                when(dat_pre_nz(j)){
                    dat_actv_data(i)(j) := dat_pre_data(j)    
                }
            }
        }
    }

//assign output  
    for(i <- 0 to conf.CMAC_ATOMK_HALF-1){
        for (j <- 0 to conf.CMAC_ATOMC-1){
            io.dat_actv(i)(j).valid := dat_actv_pvld(i)(j)
            io.dat_actv(i)(j).bits.nz := dat_actv_nz(i)(j)
            io.dat_actv(i)(j).bits.data := dat_actv_data(i)(j)

            io.wt_actv(i)(j).valid := wt_actv_pvld(i)(j)
            io.wt_actv(i)(j).bits.nz := wt_actv_nz(i)(j)
            io.wt_actv(i)(j).bits.data := wt_actv_data(i)(j)
        }
    }


}}



object NV_NVDLA_CMAC_CORE_activeDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_CMAC_CORE_active)
}


