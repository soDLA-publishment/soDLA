 package nvdla

 import chisel3._
 import chisel3.experimental._
 import chisel3.util._

class NV_NVDLA_CSC_WL_dec(implicit val conf: nvdlaConfig) extends Module {
    val io = IO(new Bundle {
        //clock
        val nvdla_core_clk = Input(Clock()) 

        //input 
        val input = Flipped(ValidIO(new csc_wl_dec_if))
        val input_mask_en = Input(UInt(10.W))

        //output
        val output = ValidIO(new csc_wl_dec_if) 

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
    val input_mask_gated = Mux(io.input_mask_en(8), io.input.bits.mask, VecInit(Seq.fill(conf.CSC_ATOMC)(false.B)))
    val vec_sum = Wire(MixedVec((0 to conf.CSC_ATOMC-1) map { i => UInt((log2Ceil(i+2)).W) }))

    for(i <- 0 to conf.CSC_ATOMC-1){     
        vec_sum(i) := PopCount(Cat(input_mask_gated.asUInt)(i, 0))        
    }  

    ////////////////////////////////// phase I: registers //////////////////////////////////
    val valid_d1 = RegInit(false.B)
    val data_d1 = Reg(Vec(conf.CSC_ATOMC, UInt(conf.CSC_BPE.W)))
    val mask_d1 = Reg(Vec(conf.CSC_ATOMC, Bool()))
    val sel_d1 = RegInit(VecInit(Seq.fill(conf.CSC_ATOMK)(false.B)))
    val vec_sum_d1 = RegInit(MixedVecInit((0 to conf.CSC_ATOMC-1) map { i => Fill(log2Ceil(i+2), false.B) }))

    valid_d1 := io.input.valid
    when(io.input.valid){
        data_d1 := io.input.bits.data
        mask_d1 := io.input.bits.mask
        sel_d1 := io.input.bits.sel       
    }

    for(i <- 0 to conf.CSC_ATOMC-1){
        when(io.input.valid & io.input_mask_en(i/8))
        {
            vec_sum_d1(i) := vec_sum(i)
        }
    }  

    ////////////////////////////////// phase II: mux //////////////////////////////////
    val vec_data = Wire(Vec(conf.CSC_ATOMC, UInt(conf.CSC_BPE.W)))

    for(j <- 0 to conf.CSC_ATOMC-1){
        vec_data(j) := MuxLookup(vec_sum_d1(j), 0.asUInt(conf.CSC_BPE.W),            
        (0 to j) map { i => (i+1).U -> data_d1(i) }
        )
    }     

    ////////////////////////////////// phase II: registers //////////////////////////////////
    val valid_d2 = RegInit(false.B)
    val sel_d2 = RegInit(VecInit(Seq.fill(conf.CSC_ATOMK)(false.B)))
    val vec_data_d2 = Reg(Vec(conf.CSC_ATOMC, UInt(conf.CSC_BPE.W)))

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
                vec_data_d2(i) := 0.asUInt(conf.CSC_BPE.W)
            }
                
        }
    }     

    ////////////////////////////////// phase III: registers //////////////////////////////////
    val mask_d2_int8_w = Wire(Vec(conf.CSC_ATOMC, Bool()))
    for(i <- 0 to conf.CSC_ATOMC-1){
        mask_d2_int8_w(i) := vec_data(i).asUInt.orR
    }

    val mask_d2_w = mask_d2_int8_w //only for int8

    val valid_d3 = RegInit(false.B)
    val mask_d3 = Reg(Vec(conf.CSC_ATOMC, Bool()))
    val sel_d3 = RegInit(VecInit(Seq.fill(conf.CSC_ATOMK)(false.B)))
    val vec_data_d3 = Reg(Vec(conf.CSC_ATOMC, UInt(conf.CSC_BPE.W)))

    valid_d3 := valid_d2
    when(valid_d2){
        mask_d3 := mask_d2_w
        sel_d3 := sel_d2
        vec_data_d3 := vec_data_d2
    }

    ////////////////////////////////// output: rename //////////////////////////////////    
    io.output.valid := valid_d3
    io.output.bits.mask := mask_d3
    io.output.bits.sel := sel_d3
    io.output.bits.data := vec_data_d3
    
}}

object NV_NVDLA_CSC_WL_decDriver extends App {
  implicit val conf: nvdlaConfig = new nvdlaConfig
  chisel3.Driver.execute(args, () => new NV_NVDLA_CSC_WL_dec)
}


