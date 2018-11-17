package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._



//this module is to active dat and wt

class NV_NVDLA_CMAC_CORE_active(implicit val conf: cmacConfiguration) extends RawModule {

    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

        //input_num
        val in_dat_data = Input(Vec(conf.CMAC_ATOMC, UInt(conf.CMAC_BPE.W)))
        val in_dat_mask = Input(Vec(conf.CMAC_ATOMC, Bool()))
        val in_dat_pvld = Input(Bool())
        val in_dat_stripe_st = Input(Bool())
        val in_dat_stripe_end = Input(Bool())

        //input_num
        val in_wt_data = Input(Vec(conf.CMAC_ATOMC, UInt(conf.CMAC_BPE.W)))
        val in_wt_mask = Input(Vec(conf.CMAC_ATOMC, Bool()))
        val in_wt_pvld = Input(Bool())
        val in_wt_sel = Input(Vec(conf.CMAC_ATOMK_HALF, Bool()))

        // atomk, atomc, data
        val dat_actv_data = Output(Vec(conf.CMAC_ATOMK_HALF, Vec(conf.CMAC_ATOMC, UInt(conf.CMAC_BPE.W))))
        val dat_actv_nz = Output(Vec(conf.CMAC_ATOMK_HALF, Vec(conf.CMAC_ATOMC, Bool())))
        val dat_actv_pvld = Output(Vec(conf.CMAC_ATOMK_HALF, Vec(conf.CMAC_ATOMC, Bool())))
        val dat_pre_stripe_st = Output(Vec(conf.CMAC_ATOMK_HALF, Bool()))
        val dat_pre_stripe_end = Output(Vec(conf.CMAC_ATOMK_HALF,Bool()))

        // atomk, atomc, data
        val wt_actv_data = Output(Vec(conf.CMAC_ATOMK_HALF, Vec(conf.CMAC_ATOMC, UInt(conf.CMAC_BPE.W))))   
        val wt_actv_nz = Output(Vec(conf.CMAC_ATOMK_HALF, Vec(conf.CMAC_ATOMC, Bool())))
        val wt_actv_pvld = Output(Vec(conf.CMAC_ATOMK_HALF, Vec(conf.CMAC_ATOMC, Bool())))                  
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
                
    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn) {

//==========================================================
// wt&dat:in --> pre
//==========================================================   
    // wt
    val wt_pre_nz = RegInit(Vec(conf.CMAC_ATOMC,false.B))
    val wt_pre_data = Reg(Vec(conf.CMAC_ATOMC, UInt(conf.CMAC_BPE.W)))
    val wt_pre_sel = RegInit(Vec(conf.CMAC_ATOMK_HALF,false.B))

    when(io.in_wt_pvld){

        wt_pre_nz := io.in_wt_mask
        wt_pre_sel := io.in_wt_sel

        for(i <- 0 to conf.CMAC_ATOMC-1){
            when(io.in_wt_mask(i)){
                wt_pre_data(i) := io.in_wt_data(i)
            }
        }   
    } 

    //dat
    val dat_pre_nz = RegInit(Vec(conf.CMAC_ATOMC,false.B))
    val dat_pre_data = Reg(Vec(conf.CMAC_ATOMC, UInt(conf.CMAC_BPE.W)))
    val dat_pre_pvld = RegInit(false.B) 
    val dat_pre_stripe_st = RegInit(Vec(conf.CMAC_ATOMK_HALF, false.B))
    val dat_pre_stripe_end = RegInit(Vec(conf.CMAC_ATOMK_HALF, false.B))

    dat_pre_pvld := io.in_dat_pvld 
    when(io.in_dat_pvld){
        dat_pre_nz := io.in_dat_mask 
        for(i <- 0 to conf.CMAC_ATOMC-1){
            when(io.in_dat_mask(i)){
                dat_pre_data(i):=io.in_dat_data(i)
            }
        } 
        for(i <- 0 to conf.CMAC_ATOMK_HALF-1){
            dat_pre_stripe_st(i) := io.in_dat_stripe_st //strip start
            dat_pre_stripe_end(i) := io.in_dat_stripe_end //strip end
        }
    }


//==========================================================
// wt:pre --> sd  this is a push and pop, when strip end, push weight, when strip start, pop weight
//==========================================================  
    //push input weight into shadow

    val wt_sd_pvld = RegInit(Vec(conf.CMAC_ATOMK_HALF,false.B))
    val wt_sd_pvld_w = Wire(Vec(conf.CMAC_ATOMK_HALF,Bool()))
    val wt_sd_nz = RegInit(Vec(conf.CMAC_ATOMK_HALF,Vec(conf.CMAC_ATOMC,false.B)))
    val wt_sd_data = Reg(Vec(conf.CMAC_ATOMK_HALF, Vec(conf.CMAC_ATOMC, UInt(conf.CMAC_BPE.W))))
    val dat_actv_stripe_end = RegInit(Vec(conf.CMAC_ATOMK_HALF, false.B))
        
    for(i <- 0 to conf.CMAC_ATOMK_HALF-1){
        wt_sd_pvld_w(i) := Mux(wt_pre_sel(i), true.B, Mux(dat_pre_stripe_st(i), false.B, wt_sd_pvld(i)))
        wt_sd_pvld(i) := wt_sd_pvld_w(i) 
        when(wt_pre_sel(i)){
            wt_sd_nz(i) := wt_pre_nz
            for (j <- 0 to conf.CMAC_ATOMC-1){
                when(wt_pre_nz(i)){
                    wt_sd_data(i)(j) := wt_pre_data(i)(j)
                }
            }
        }
    } 

    dat_actv_stripe_end := dat_pre_stripe_end 

//==========================================================
// wt:sd --> actv  this is a push and pop, when strip end, push weight, when strip start, pop weight
//==========================================================  
    //pop weight from shadow when new stripe begin.

    val wt_actv_vld = RegInit(Vec(conf.CMAC_ATOMK_HALF,false.B))
    val wt_actv_pvld = RegInit(Vec(conf.CMAC_ATOMK_HALF, Vec(conf.CMAC_ATOMC, false.B)))
    val wt_actv_pvld_w = Wire(Vec(conf.CMAC_ATOMK_HALF,Bool()))
    val wt_actv_nz = RegInit(Vec(conf.CMAC_ATOMK_HALF,Vec(conf.CMAC_ATOMC,false.B)))
    val wt_actv_data = Reg(Vec(conf.CMAC_ATOMK_HALF, Vec(conf.CMAC_ATOMC, UInt(conf.CMAC_BPE.W))))
        
    for(i <- 0 to conf.CMAC_ATOMK_HALF-1){
        wt_actv_pvld_w(i) := Mux(dat_pre_stripe_st(i), wt_sd_pvld(i), Mux(dat_actv_stripe_end(i), false.B, wt_actv_vld(i)))
        wt_actv_vld(i) := wt_actv_pvld_w(i)
        for (j <- 0 to conf.CMAC_ATOMC-1){
            wt_actv_pvld(i)(j) := wt_actv_pvld_w(i)
            when(dat_pre_stripe_st(i)&wt_actv_pvld_w(i)){
                wt_actv_nz(i)(j) := wt_sd_nz(i)(j)
                wt_actv_data(i)(j) := Fill(conf.CMAC_BPE, wt_actv_nz(i)(j))&(wt_sd_data(i)(j))
            }
        }
    } 

//==========================================================
// dat:pre --> actv
//==========================================================  

    val dat_actv_data_reg = Reg(Vec(conf.CMAC_ATOMK_HALF, Vec(conf.CMAC_ATOMC, UInt(conf.CMAC_BPE.W))))
    val dat_actv_nz_reg = RegInit(Vec(conf.CMAC_ATOMK_HALF, Vec(conf.CMAC_ATOMC, false.B)))
    val dat_actv_pvld_reg = RegInit(Vec(conf.CMAC_ATOMK_HALF, Vec(conf.CMAC_ATOMC, false.B)))

    for(i <- 0 to conf.CMAC_ATOMK_HALF-1){
        for (j <- 0 to conf.CMAC_ATOMC-1){
            dat_actv_pvld_reg(i)(j) := dat_pre_pvld
        }
        when(dat_pre_pvld){
            dat_actv_nz_reg(i) := dat_pre_nz
        }
        for (j <- 0 to conf.CMAC_ATOMC-1){
            when(dat_pre_pvld&dat_pre_nz(j)){
                dat_actv_data_reg(i)(j) := dat_pre_data(i)(j)
            }
        }
    }    

    //assign output

    io.dat_actv_pvld := dat_actv_pvld_reg
    io.dat_actv_data := dat_actv_data_reg
    io.dat_actv_nz := dat_actv_nz_reg

  }}