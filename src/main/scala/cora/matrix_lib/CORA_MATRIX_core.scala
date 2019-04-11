// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_MATRIX_core(implicit val conf: matrixConfiguration) extends Module {
//     val io = IO(new Bundle {

//         val sc2mac_dat_pvld = Input(Bool())  /* data valid */
//         val sc2mac_dat_mask = Input(Vec(conf.CMAC_ATOMC, Bool()))
//         val sc2mac_dat_data = Input(Vec(conf.CMAC_ATOMC, conf.CMAC_TYPE(conf.CMAC_BPE.W)))
//         val sc2mac_dat_pd = Input(UInt(9.W))

//         val sc2mac_wt_pvld = Input(Bool())
//         val sc2mac_wt_mask = Input(Vec(conf.CMAC_ATOMC, Bool()))
//         val sc2mac_wt_data = Input(Vec(conf.CMAC_INPUT_NUM, conf.CMAC_TYPE(conf.CMAC_BPE.W)))
//         val sc2mac_wt_sel = Input(Vec(conf.CMAC_ATOMK_HALF, Bool()))

//         val mac2accu_pvld = Output(Bool()) /* data valid */
//         val mac2accu_mask = Output(Vec(conf.CMAC_ATOMK_HALF, Bool()))
//         val mac2accu_data = Output(Vec(conf.CMAC_ATOMK_HALF, conf.CMAC_TYPE(conf.CMAC_RESULT_WIDTH.W)))
//         val mac2accu_pd = Output(UInt(9.W))

//         val dp2reg_done = Output(Bool())

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
//     //==========================================================
//     // input retiming logic            
//     //==========================================================
//     val u_rt_in = Module(new NV_NVDLA_CMAC_CORE_rt_in)

//     u_rt_in.io.sc2mac_dat_pvld := io.sc2mac_dat_pvld              //|< i
//     u_rt_in.io.sc2mac_dat_mask := io.sc2mac_dat_mask        //|< i    
//     u_rt_in.io.sc2mac_dat_data := io.sc2mac_dat_data        //|< i )
//     u_rt_in.io.sc2mac_dat_pd := io.sc2mac_dat_pd            //|< i

//     u_rt_in.io.sc2mac_wt_pvld := io.sc2mac_wt_pvld                //|< i
//     u_rt_in.io.sc2mac_wt_mask := io.sc2mac_wt_mask         //|< i    
//     u_rt_in.io.sc2mac_wt_data := io.sc2mac_wt_data         //|< i )
//     u_rt_in.io.sc2mac_wt_sel := io.sc2mac_wt_sel            //|< i

//     val in_dat_pvld = u_rt_in.io.in_dat_pvld                   //|> w 
//     val in_dat_mask = u_rt_in.io.in_dat_mask             //|> w    
//     val in_dat_data = u_rt_in.io.in_dat_data          //|< i 
//     val in_dat_pd = u_rt_in.io.in_dat_pd                 //|> w
//     val in_dat_stripe_st = u_rt_in.io.in_dat_stripe_st             //|> w
//     val in_dat_stripe_end = u_rt_in.io.in_dat_stripe_end             //|> w

//     val in_wt_pvld = u_rt_in.io.in_wt_pvld                    //|> w
//     val in_wt_mask = u_rt_in.io.in_wt_mask             //|> w   
//     val in_wt_data = u_rt_in.io.in_wt_data         //|< i )
//     val in_wt_sel = u_rt_in.io.in_wt_sel                //|> w

//     val out_pvld = ShiftRegister(in_dat_pvld, conf.MAC_PD_LATENCY)
//     val out_pd = ShiftRegister(in_dat_pd, conf.MAC_PD_LATENCY, in_dat_pvld)
        
//     //==========================================================
//     // input shadow and active pipeline
//     //==========================================================
//     val u_active = Module(new NV_NVDLA_CMAC_CORE_active)

//     u_active.io.in_dat_pvld := in_dat_pvld                   //|< w
//     u_active.io.in_dat_mask := in_dat_mask            //|< w
//     u_active.io.in_dat_data := in_dat_data     //|< i )  
//     u_active.io.in_dat_stripe_end := in_dat_stripe_end             //|< w
//     u_active.io.in_dat_stripe_st := in_dat_stripe_st              //|< w

//     u_active.io.in_wt_pvld := in_wt_pvld                    //|< w
//     u_active.io.in_wt_mask := in_wt_mask             //|< w
//     u_active.io.in_wt_data := in_wt_data         //|< i )    
//     u_active.io.in_wt_sel := in_wt_sel                //|< w

//     val dat_actv_pvld = u_active.io.dat_actv_pvld         //|> w
//     val dat_actv_nz = u_active.io.dat_actv_nz           //|> w
//     val dat_actv_data = u_active.io.dat_actv_data        //|> w   
//     val dat_pre_stripe_st = u_active.io.dat_pre_stripe_st            //|> w
//     val dat_pre_stripe_end = u_active.io.dat_pre_stripe_end           //|> w
    
//     val wt_actv_pvld = u_active.io.wt_actv_pvld          //|> w
//     val wt_actv_nz = u_active.io.wt_actv_nz            //|> w
//     val wt_actv_data = u_active.io.wt_actv_data         //|> w
    
//     //==========================================================
//     // MAC CELLs
//     //==========================================================
//     val u_mac = Array.fill(conf.KF_STAT){Module(new C_CORA_MATRIX_mac)}

//     val out_data = Wire(Vec(conf.KF_STAT, conf.KF_TYPE(conf.KF_RESULT_WIDTH.W)))
//     val out_mask = Wire(Vec(conf.KF_STAT, Bool()))

//     for(i<- 0 to conf.CMAC_ATOMK_HALF-1){

//         u_mac(i).io.stat_actv_pvld := stat_actv_pvld(i)
//         u_mac(i).io.stat_actv_nz := stat_actv_nz(i)
//         u_mac(i).io.stat_actv_data := stat_actv_data(i)

//         u_mac(i).io.tr_actv_pvld := tr_actv_pvld(i)
//         u_mac(i).io.tr_actv_nz := tr_actv_nz(i) 
//         u_mac(i).io.tr_actv_data := tr_actv_data(i)

//         stat_out_pvld(i) := u_mac(i).io.stat_out_pvld
//         stat_out_nz(i) := u_mac(i).io.stat_out_nz    
//         stat_out_data(i) := u_mac(i).io.stat_out_data                                                                                                  
//     }

//     //==========================================================
//     // output retiming logic            
//     //==========================================================
//     val u_rt_out = Module(new NV_NVDLA_CMAC_CORE_rt_out)  // use seq

//     u_rt_out.io.out_pvld := out_pvld                      //|< w
//     u_rt_out.io.out_mask := out_mask                 //|< w
//     u_rt_out.io.out_data := out_data              //|< w )
//     u_rt_out.io.out_pd := out_pd                   //|< w

//     io.dp2reg_done := u_rt_out.io.dp2reg_done                   //|> o
//     io.mac2accu_data := u_rt_out.io.mac2accu_data         //|> o )
//     io.mac2accu_mask := u_rt_out.io.mac2accu_mask            //|> o
//     io.mac2accu_pd := u_rt_out.io.mac2accu_pd              //|> o
//     io.mac2accu_pvld := u_rt_out.io.mac2accu_pvld                 //|> o  


// }