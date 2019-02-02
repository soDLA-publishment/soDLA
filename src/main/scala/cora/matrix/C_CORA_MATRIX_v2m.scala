package cora


import chisel3._
import chisel3.experimental._
import chisel3.util._
import hardfloat._

//this module is to mac tr and stat

class C_CORA_MATRIX_v2m(implicit val conf: matrixConfiguration) extends Module {

    val io = IO(new Bundle {
        //input
        val roundingMode = Input(UInt(3.W))
        val detectTininess = Input(UInt(1.W))

        val stat_actv_data = Input(Vec(conf.KF_STAT, UInt(conf.KF_BPE.W)))
        val stat_actv_pvld = Input(Vec(conf.KF_STAT, Bool()))

        val tr_actv_data = Input(Vec(conf.KF_STAT, Vec(conf.KF_STAT, UInt(conf.KF_BPE.W))))
        val tr_actv_pvld = Input(Vec(conf.KF_STAT, Vec(conf.KF_STAT, Bool())))

        //output
        val stat_out_data = Output(Vec(conf.KF_STAT, UInt(conf.KF_BPE.W)))
        val stat_out_pvld = Output(Vec(conf.KF_STAT, Bool()))
       
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
//                                                        
//           │                        ├─┐                   
//           │                        ┌─┘             
//           │                        │                            
//           └─┐  ┐  ┌───────┬──┐  ┌──┘                            
//             │ ─┤ ─┤       │ ─┤ ─┤            
//             └──┴──┘       └──┴──┘ 


    //==========================================================
    // MulAddRecFNPipe CELLs
    //==========================================================

    val u_v2v = Array.fill(conf.KF_STAT)(Module(new C_CORA_MATRIX_v2v_fp_noshareFMA()))
    
    //setup config
    for (i <- 0 to conf.KF_STAT-1){

        u_v2v(i).io.roundingMode := io.roundingMode
        u_v2v(i).io.detectTininess := io.detectTininess   
        u_v2v(i).io.stat_actv_data := io.stat_actv_data
        u_v2v(i).io.stat_actv_pvld := io.stat_actv_pvld
        u_v2v(i).io.tr_actv_data := io.tr_actv_data(i)
        u_v2v(i).io.tr_actv_pvld := io.tr_actv_pvld(i)

        io.stat_out_data(i) := u_v2v(i).io.stat_out_data
        io.stat_out_pvld(i) := u_v2v(i).io.stat_out_pvld

    }



}