package nvdla

import chisel3._




class NV_NVDLA_CMAC_CORE_rt_in(implicit val conf: cmacConfiguration) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())      
        val nvdla_core_rstn = Input(Bool())

        //: for(my $i=0; $i<CMAC_INPUT_NUM; $i++){
        //: print qq(
        //: input   [CMAC_BPE-1:0]  sc2mac_dat_data${i};);
        //: }
        //: for(my $i=0; $i<CMAC_INPUT_NUM; $i++){
        //: print qq(
        //: output  [CMAC_BPE-1:0]  in_dat_data${i};);
        //: }

        val sc2mac_dat_data = Input(Vec(conf.CMAC_INPUT_NUM, UInt((conf.CMAC_BPE).W)))
        val in_dat_data = Input(Vec(conf.CMAC_INPUT_NUM, UInt((conf.CMAC_BPE).W)))

        //: for(my $i=0; $i<CMAC_INPUT_NUM; $i++){
        //: print qq(
        //: input   [CMAC_BPE-1:0] sc2mac_wt_data${i};);
        //: }
        //: for(my $i=0; $i<CMAC_INPUT_NUM; $i++){
        //: print qq(
        //: output  [CMAC_BPE-1:0] in_wt_data${i};);
        //: }

        val sc2mac_wt_data = Input(Vec(conf.CMAC_INPUT_NUM, UInt((conf.CMAC_BPE).W)))
        val in_wt_data = Input(Vec(conf.CMAC_INPUT_NUM, UInt((conf.CMAC_BPE).W)))
       
        val sc2mac_dat_mask = Input(UInt((conf.CMAC_ATOMC).W))
        val sc2mac_dat_pd = Input(UInt(9.W))
        val sc2mac_dat_pvld = Input(Bool())
        val sc2mac_wt_mask = Input(UInt((conf.CMAC_ATOMC).W))
        val sc2mac_wt_pvld = Input(Bool())
        val sc2mac_wt_sel = Input(UInt((conf.CMAC_ATOMK_HALF).W))

        val in_dat_mask = Output(UInt((conf.CMAC_ATOMC).W))
        val in_dat_pd = Output(UInt(9.W))
        val in_dat_pvld = Output(Bool())
        val in_dat_stripe_end = Output(Bool())
        val in_dat_stripe_st = Output(Bool())
        val in_wt_mask = Output(UInt((conf.CMAC_ATOMC).W))
        val in_wt_pvld = Output(Bool())
        val in_wt_sel = Output(UInt((conf.CMAC_ATOMK_HALF).W))

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
                
    //: for(my $i=0; $i<CMAC_INPUT_NUM; $i++){
    //: print qq(
    //: wire [CMAC_BPE-1:0] in_dat_data${i};
    //: wire [CMAC_BPE-1:0] in_wt_data${i};)
    //: }
    io.in_dat_data := Wire(Vec(conf.CMAC_INPUT_NUM, UInt((conf.CMAC_BPE).W)))
    io.in_wt_data := Wire(Vec(conf.CMAC_INPUT_NUM, UInt((conf.CMAC_BPE).W)))
    io.in_dat_mask := Wire(UInt((conf.CMAC_ATOMC).W))
    io.in_dat_pd := Wire(UInt(9.W))  
    io.in_dat_pvld := Wire(Bool()) 
    io.in_dat_stripe_end := Wire(Bool())
    io.in_dat_stripe_st := Wire(Bool())
    io.in_wt_mask := Wire(UInt((conf.CMAC_ATOMC).W))
    io.in_wt_pvld := Wire(Bool())
    io.in_wt_sel := Wire(UInt((conf.CMAC_ATOMK_HALF).W))
    val in_rt_dat_pvld_d0 = Wire(Bool())
    val in_rt_dat_mask_d0 = Wire(UInt((conf.CMAC_ATOMC).W))
    val in_rt_dat_pd_d0 = Wire(UInt(9.W))
    val in_rt_dat_data_d0 = Wire(UInt((conf.CMAC_BPE*conf*CMAC_ATOMC).W)

    //: for(my $i=1; $i<CMAC_IN_RT_LATENCY+1; $i++){
    //: print qq(
    //: reg     in_rt_dat_pvld_d${i};
    //: reg     [CMAC_ATOMC-1:0]   in_rt_dat_mask_d${i};
    //: reg     [8:0]              in_rt_dat_pd_d${i};
    //: reg     [CMAC_BPE*CMAC_ATOMC-1:0] in_rt_dat_data_d${i};
    //: );
    //: }

    val in_rt_dat_pvld_d = Reg()
    



 

