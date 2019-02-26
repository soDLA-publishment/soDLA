// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// //this module is to mac dat and wt

// class NV_NVDLA_CACC_calculator(implicit conf: caccConfiguration) extends Module {

//     val io = IO(new Bundle {
//         //clk
//         val nvdla_core_clk = Input(Clock())
//         val nvdla_cell_clk = Input(Clock())

//         //abuf
//         val abuf_rd_data = Input(Vec(conf.CACC_ATOMK, UInt(CACC_PARSUM_WIDTH.W)))
//         val abuf_wr_addr = Output(UInt(CACC_ABUF_AWIDTH.W))
//         val abuf_wr_data = Output(Vec(conf.CACC_ATOMK, UInt(CACC_PARSUM_WIDTH.W)))
//         val abuf_wr_en = Output(Bool())

//         //dlv
//         val dlv_data = Output(Vec(conf.CACC_ATOMK, UInt(CACC_FINAL_WIDTH.W)))
//         val dlv_mask = Output(Bool())
//         val dlv_pd = Output(UInt(2.W))
//         val dlv_valid = Output(Bool())
//         val dp2reg_sat_count = Output(UInt(32.W))

//         //control
//         val accu_ctrl_pd = Input(UInt(13.W))
//         val accu_ctrl_ram_valid = Input(Bool())
//         val accu_ctrl_valid = Input(Bool())

//         //cfg
//         val cfg_in_en_mask = Input(Bool())
//         val cfg_is_wg = Input(Bool())
//         val cfg_truncate = Input(UInt(5.W))

//         //mac2cacc
//         val mac_a2accu_data = Input(Vec(conf.CACC_ATOMK/2, conf.CACC_TYPE(conf.CACC_IN_WIDTH.W)))
//         val mac_a2accu_mask = Input(Vec(conf.CACC_ATOMK/2, Bool()))
//         val mac_a2accu_mode = Input(Bool())
//         val mac_a2accu_pvld = Input(Bool())

//         val mac_b2accu_data = Input(Vec(conf.CACC_ATOMK/2, conf.CACC_TYPE(conf.CACC_IN_WIDTH.W)))
//         val mac_b2accu_mask = Input(Vec(conf.CACC_ATOMK/2, Bool()))
//         val mac_b2accu_mode = Input(Bool())
//         val mac_b2accu_pvld = Input(Bool())

//         //output
//         val mac_out_data = Output(conf.CMAC_TYPE(conf.CMAC_RESULT_WIDTH.W))
//         val mac_out_pvld = Output(Bool())         
//     })

// //     
// //          ┌─┐       ┌─┐
// //       ┌──┘ ┴───────┘ ┴──┐
// //       │                 │
// //       │       ───       │
// //       │  ─┬┘       └┬─  │
// //       │                 │
// //       │       ─┴─       │
// //       │                 │
// //       └───┐         ┌───┘
// //           │         │
// //           │         │
// //           │         │
// //           │         └──────────────┐
// //           │                        │
// //           │                        ├─┐
// //           │                        ┌─┘    
// //           │                        │
// //           └─┐  ┐  ┌───────┬──┐  ┌──┘         
// //             │ ─┤ ─┤       │ ─┤ ─┤         
// //             └──┴──┘       └──┴──┘ 
                
//     val mout = VecInit(Seq.fill(conf.CMAC_ATOMC)(conf.CMAC_TYPE(0, (2*conf.CMAC_BPE).W)))

//     for(i <- 0 to conf.CMAC_ATOMC-1){
//         when(io.wt_actv_pvld(i)&io.wt_actv_nz(i)&io.dat_actv_pvld(i)&io.dat_actv_nz(i)){                       
//              mout(i) := io.wt_actv_data(i)*io.dat_actv_data(i)
//         }
//         .otherwise{
//              mout(i) := conf.CMAC_TYPE(0, conf.CMAC_RESULT_WIDTH)
//         }
//     }  

//     val sum_out = mout.reduce(_+&_)
    
//     //add retiming
//     val pp_pvld_d0 = io.dat_actv_pvld(0)&io.wt_actv_pvld(0)

//     io.mac_out_data := ShiftRegister(sum_out, conf.CMAC_OUT_RETIMING, pp_pvld_d0)
//     io.mac_out_pvld := ShiftRegister(pp_pvld_d0, conf.CMAC_OUT_RETIMING, pp_pvld_d0)


// }