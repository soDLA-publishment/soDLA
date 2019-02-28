package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_CSC_WL_dec(implicit val conf: cscConfiguration) extends Module {
    val io = IO(new Bundle {
        //input 
        val input_data = Input(Vec(conf.CSC_ATOMC, SInt(conf.CSC_BPE.W)))
        val input_mask = Input(Vec(conf.CSC_ATOMC, Bool()))
        val input_mask_en = Input(Vec(10, Bool()))
        val input_pipe_valid = Input(Bool())
        val input_sel = Input(Vec(conf.CSC_ATOMK, Bool()))

        //output
        val output_data = Output(Vec(conf.CSC_ATOMC, SInt(conf.CSC_BPE.W)))
        val output_mask = Output(Vec(conf.CSC_ATOMC, Bool()))
        val output_pvld = Output(Bool())
        val output_sel = Output(Vec(conf.CSC_ATOMK, Bool()))    

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

    /////////////////////////////////////////////////////////////////////////////////////////////
    // Decoder of compressed weight                                                  
    //
    //            data_mask             input_data     mac_sel
    //                |                     |            |
    //            sums_for_sel           register     register
    //                |                     |            |
    //                ------------------>  mux        register
    //                                      |            |
    //                                   output_data  output_sel
    //
    /////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////// phase I: calculate sums for mux //////////////////////////////////
    val input_mask_gated = Mux(io.input_mask_en(8), io.input_mask, Vec(conf.CSC_ATOMC, false.B))

    val vec_sum = MixedVec((0 to conf.CSC_ATOMC-1) map { i => UInt((log2Ceil(i+2)).W) })
    for(i <- 0 to conf.CSC_ATOMC-1){
        for(j <- 0 to i){
            if(j==0){
                vec_sum(i) := input_mask_gated(0)
            }
            else{
                vec_sum(i) := vec_sum(i) + input_mask_gated(j)
            }
        }
    }  

    ////////////////////////////////// phase I: registers //////////////////////////////////
    val valid_d1 = RegInit(false.B)
    val data_d1 = Reg(Vec(conf.CSC_ATOMC, SInt(conf.CSC_BPE.W)))
    val mask_d1 = Reg(Vec(conf.CSC_ATOMC, Bool()))
    val sel_d1 = RegInit(VecInit(Seq.fill(conf.CSC_ATOMK)(false.B)))
    val vec_sum_d1 = RegInit(MixedVecInit((0 to conf.CSC_ATOMC-1) map { i => Fill(log2Ceil(i+2), false.B) }))

    valid_d1 := io.input_pipe_valid
    when(io.input_pipe_valid){
        data_d1 := io.input_data
        mask_d1 := io.input_mask
        sel_d1 := io.input_sel       
    }

    for(i <- 0 to conf.CSC_ATOMC-1){
        when(io.input_pipe_valid & io.input_mask_en(i/8))
        {
            vec_sum_d1(i) := vec_sum(i)
        }
    }  

    ////////////////////////////////// phase II: mux //////////////////////////////////
    val vec_data = Vec(conf.CSC_ATOMC, SInt(conf.CSC_BPE.W))

    for(j <- 0 to conf.CSC_ATOMC-1){
        vec_data(j) := MuxLookup(vec_sum_d1(j), 0.asSInt(conf.CSC_BPE.W),
        Seq(      
        (1 to j) map { i => ( (i+1).U -> data_d1(i) ) }
        ))
    }     

    ////////////////////////////////// phase II: registers //////////////////////////////////
    val valid_d2 = RegInit(false.B)
    val sel_d2 = RegInit(VecInit(Seq.fill(conf.CSC_ATOMK)(false.B)))
    val vec_data_d2 = Reg(Vec(conf.CSC_ATOMC, SInt(conf.CSC_BPE.W)))

    valid_d2 := valid_d1
    when(valid_d1){
        sel_d2 := sel_d1
    }
    for(i <- 0 to conf.CSC_ATOMC-1){
        when(valid_d1){
            when(mask_d1(i)){
                vec_data_d2(i) := vec_data(i)
            }
            .otherwise{
                vec_data_d2(i) := 0.asSInt(conf.CSC_BPE.W)
            }
                
        }
    }     

    ////////////////////////////////// phase III: registers //////////////////////////////////
    val mask_d2_int8_w = Vec(conf.CSC_ATOMC, Bool())
    for(i <- 0 to conf.CSC_ATOMC-1){
        mask_d2_int8_w(i) := vec_data(i).asUInt.orR
    }

    val mask_d2_w = mask_d2_int8_w //only for int8

    val valid_d3 = RegInit(false.B)
    val mask_d3 = Reg(Vec(conf.CSC_ATOMC, Bool()))
    val sel_d3 = RegInit(VecInit(Seq.fill(conf.CSC_ATOMK)(false.B)))
    val vec_data_d3 = Reg(Vec(conf.CSC_ATOMC, SInt(conf.CSC_BPE.W)))

    valid_d3 := valid_d2
    when(valid_d2){
        mask_d3 := mask_d2_w
        sel_d3 := sel_d2
        vec_data_d3 := vec_data_d2
    }

    ////////////////////////////////// output: rename //////////////////////////////////    
    io.output_pvld := valid_d3
    io.output_mask := mask_d3
    io.output_sel := sel_d3
    io.output_data := vec_data_d3
    
}

