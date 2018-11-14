package nvdla

import chisel3._
import chisel3.experimental._




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

    //: for(my $i=1; $i<CMAC_IN_RT_LATENCY+1; $i++){
    //: print qq.
    //: reg     in_rt_dat_pvld_d${i};
    //: reg     [CMAC_ATOMC-1:0]   in_rt_dat_mask_d${i};
    //: reg     [8:0]              in_rt_dat_pd_d${i};
    //: reg     [CMAC_BPE*CMAC_ATOMC-1:0] in_rt_dat_data_d${i};
    //: );
    //: }

    val in_rt_dat_pvld_d_wire = Wire(Bool())
    val in_rt_dat_pvld_d_regs = Seq.fill(conf.CMAC_IN_RT_LATENCY)(Reg(Bool()))
    val in_rt_dat_pvld_d = VecInit(in_rt_dat_pvld_d_wire +: in_rt_dat_pvld_d_regs)

    val in_rt_dat_mask_d_wire = Wire(UInt(conf.CMAC_ATOMC.W))
    val in_rt_dat_mask_d_regs = Seq.fill(conf.CMAC_IN_RT_LATENCY)(Reg(UInt(conf.CMAC_ATOMC.W)))
    val in_rt_dat_mask_d = VecInit(in_rt_dat_mask_d_wire +: in_rt_dat_mask_d_regs)

    val in_rt_dat_pd_d_wire = Wire(UInt(9.W))
    val in_rt_dat_pd_d_regs = Seq.fill(conf.CMAC_IN_RT_LATENCY)(Reg(UInt(9.W)))
    val in_rt_dat_pd_d = VecInit(in_rt_dat_pd_d_wire +: in_rt_dat_pd_d_regs)

    val in_rt_dat_data_d_wire = Wire(Vec(conf.CMAC_ATOMC, UInt((conf.CMAC_BPE*conf.CMAC_ATOMC).W)))
    val in_rt_dat_data_d_regs = Seq.fill(conf.CMAC_IN_RT_LATENCY)(Reg(Vec(conf.CMAC_ATOMC, UInt((conf.CMAC_BPE*conf.CMAC_ATOMC).W))))  
    val in_rt_dat_data_d = VecInit(in_rt_dat_data_d_wire +: in_rt_dat_data_d_regs)


    //: for(my $i=1; $i<CMAC_IN_RT_LATENCY+1; $i++){
    //: print qq(
    //: reg    [CMAC_BPE*CMAC_ATOMC-1:0]    in_rt_wt_data_d${i};
    //: reg    [CMAC_ATOMC-1:0]             in_rt_wt_mask_d${i};
    //: reg                                 in_rt_wt_pvld_d${i};
    //: reg    [CMAC_ATOMK_HALF-1:0]        in_rt_wt_sel_d${i};
    //: )
    //: }

    val in_rt_wt_data_d_wire = Wire(Vec(conf.CMAC_ATOMC, UInt((conf.CMAC_BPE*conf.CMAC_ATOMC).W)))
    val in_rt_wt_data_d_regs = Seq.fill(conf.CMAC_IN_RT_LATENCY)(Reg(Vec(conf.CMAC_ATOMC, UInt((conf.CMAC_BPE*conf.CMAC_ATOMC).W))))  
    val in_rt_wt_data_d = VecInit(in_rt_wt_data_d_wire +: in_rt_wt_data_d_regs)

    val in_rt_wt_mask_d_wire = Wire(UInt(conf.CMAC_ATOMC.W))
    val in_rt_wt_mask_d_regs = Seq.fill(conf.CMAC_IN_RT_LATENCY)(Reg(UInt(conf.CMAC_ATOMC.W)))
    val in_rt_wt_mask_d = VecInit(in_rt_wt_mask_d_wire +: in_rt_wt_mask_d_regs)

    val in_rt_wt_pvld_d_wire = Wire(Bool())
    val in_rt_wt_pvld_d_regs = Seq.fill(conf.CMAC_IN_RT_LATENCY)(Reg(Bool()))
    val in_rt_wt_pvld_d = VecInit(in_rt_wt_pvld_d_wire +: in_rt_wt_pvld_d_regs)

    val in_rt_wt_sel_d_wire = Wire(UInt((conf.CMAC_ATOMK_HALF).W))
    val in_rt_wt_sel_d_regs = Seq.fill(conf.CMAC_IN_RT_LATENCY)(Reg(UInt((conf.CMAC_ATOMK_HALF).W)))  
    val in_rt_wt_sel_d = VecInit(in_rt_wt_sel_d_wire +: in_rt_wt_sel_d_regs)

    // assign

    in_rt_dat_pvld_d(0) := io.sc2mac_dat_pvld
    in_rt_dat_mask_d(0) := io.sc2mac_dat_mask
    in_rt_dat_pd_d(0) := io.sc2mac_dat_pd
    in_rt_wt_pvld_d(0) := io.sc2mac_wt_pvld
    in_rt_wt_mask_d(0) := io.sc2mac_wt_mask
    in_rt_wt_sel_d(0) := io.sc2mac_wt_sel

    //:     my $kk=CMAC_BPE;
    //:     for(my $k = 0; $k <CMAC_ATOMC; $k ++) {
    //:         print "wire [$kk-1:0]  in_rt_dat_data${k}_d0 = sc2mac_dat_data${k}; \n"; 
    //:     }
    //:     for(my $k = 0; $k <CMAC_ATOMC; $k ++) {
    //:         print "wire [$kk-1:0]  in_rt_wt_data${k}_d0 = sc2mac_wt_data${k}; \n"; 
    //:     }

    in_rt_dat_data_d(0) := io.sc2mac_dat_data
    in_rt_wt_data_d(0) := io.sc2mac_wt_data

    //==========================================================
    // Retiming flops,add latency.
    //==========================================================
    //: my $latency = CMAC_IN_RT_LATENCY;
    //: my $bpe=CMAC_BPE;
    //: for(my $i = 0; $i < $latency; $i ++) {
    //:     my $j = $i + 1;
    //:     &eperl::flop("-nodeclare -q  in_rt_dat_pvld_d${j}  -d \"in_rt_dat_pvld_d${i}\" -clk nvdla_core_clk -rst nvdla_core_rstn "); 
    //:     &eperl::flop("-nodeclare -q  in_rt_dat_mask_d${j}  -en \"in_rt_dat_pvld_d${i} | in_rt_dat_pvld_d${j}\" -d  \"in_rt_dat_mask_d${i}\" -clk nvdla_core_clk -rst nvdla_core_rstn "); 
    //:     &eperl::flop("-nodeclare -q  in_rt_dat_pd_d${j}    -en \"in_rt_dat_pvld_d${i} | in_rt_dat_pvld_d${j}\" -d  \"in_rt_dat_pd_d${i}\" -clk nvdla_core_clk -rst nvdla_core_rstn ");
    //:     for(my $k = 0; $k <CMAC_ATOMC; $k ++) {
    //:         &eperl::flop("-norst -wid $bpe -q  in_rt_dat_data${k}_d${j}  -en \"in_rt_dat_mask_d${i}[${k}]\" -d  \"in_rt_dat_data${k}_d${i}\" -clk nvdla_core_clk"); 
    //:     }
    //:
    //:     &eperl::flop("-nodeclare -q  in_rt_wt_pvld_d${j}  -d \"in_rt_wt_pvld_d${i}\" -clk nvdla_core_clk -rst nvdla_core_rstn "); 
    //:     &eperl::flop("-nodeclare -q  in_rt_wt_mask_d${j}  -en \"in_rt_wt_pvld_d${i} | in_rt_wt_pvld_d${j}\" -d  \"in_rt_wt_mask_d${i}\" -clk nvdla_core_clk -rst nvdla_core_rstn ");
    //:     &eperl::flop("-nodeclare -q  in_rt_wt_sel_d${j} -en \"in_rt_wt_pvld_d${i} | in_rt_wt_pvld_d${j}\" -d  \"in_rt_wt_sel_d${i}\" -clk nvdla_core_clk -rst nvdla_core_rstn ");
    //: 
    //:     my $bpe = CMAC_BPE;
    //:     for(my $k = 0; $k <CMAC_ATOMC; $k ++) {
    //:         &eperl::flop("-norst -wid $bpe -q  in_rt_wt_data${k}_d${j}  -en \"in_rt_wt_mask_d${i}[${k}]\" -d  \"in_rt_wt_data${k}_d${i}\" -clk nvdla_core_clk"); 
    //:     }
    //: }  
    
    for(t <- 0 to conf.CMAC_IN_RT_LATENCY-1){
        withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){
            in_rt_dat_pvld_d(t+1) := in_rt_dat_pvld_d(t)
            when(in_rt_dat_pvld_d(t)|in_rt_dat_pvld_d(t+1)){
                in_rt_dat_mask_d(t+1) := in_rt_dat_mask_d(t)
                in_rt_dat_pd_d(t+1) := in_rt_dat_pd_d(t)
            }

            in_rt_wt_pvld_d(t+1) := in_rt_wt_pvld_d(t)
            when(in_rt_wt_pvld_d(t)|in_rt_wt_pvld_d(t+1)){
                in_rt_wt_mask_d(t+1) := in_rt_wt_mask_d(t)
                in_rt_wt_sel_d(t+1) := in_rt_wt_sel_d(t)           
            }
        }    
        withClock(io.nvdla_core_clk){
            for(i <- 0 to conf.CMAC_ATOMC-1){
                when(in_rt_dat_mask_d(t)(i)){
                    in_rt_dat_data_d(t+1)(i) := in_rt_dat_data_d(t)(i)                   
                }
                when(in_rt_wt_mask_d(t)(i)){
                    in_rt_wt_data_d(t+1)(i) := in_rt_wt_data_d(t)(i) 
                }  

            }
        }        
    }     
    
    //: my $i = $latency;
    //: print "assign    in_dat_pvld = in_rt_dat_pvld_d${i};\n";
    //: print "assign    in_dat_mask = in_rt_dat_mask_d${i};\n";
    //: print "assign    in_dat_pd   = in_rt_dat_pd_d${i};\n";
    //: print "assign    in_wt_pvld = in_rt_wt_pvld_d${i};\n";
    //: print "assign    in_wt_mask = in_rt_wt_mask_d${i};\n";
    //: print "assign    in_wt_sel = in_rt_wt_sel_d${i};\n";
    //: 
    io.in_dat_pvld := in_rt_dat_pvld_d(conf.CMAC_IN_RT_LATENCY)
    io.in_dat_mask := in_rt_dat_mask_d(conf.CMAC_IN_RT_LATENCY)
    io.in_dat_pd := in_rt_dat_pd_d(conf.CMAC_IN_RT_LATENCY)
    io.in_wt_pvld := in_rt_wt_pvld_d(conf.CMAC_IN_RT_LATENCY)
    io.in_wt_mask := in_rt_wt_mask_d(conf.CMAC_IN_RT_LATENCY)
    io.in_wt_sel := in_rt_wt_sel_d(conf.CMAC_IN_RT_LATENCY)

    //: my $k=$latency;
    //: for(my $i=0; $i<CMAC_ATOMC; $i++){
    //: print qq(
    //: assign in_dat_data${i} = in_rt_dat_data${i}_d${k}; )
    //: }
    //: for(my $i=0; $i<CMAC_ATOMC; $i++){
    //: print qq(
    //: assign in_wt_data${i} = in_rt_wt_data${i}_d${k};)
    //: }

    io.in_dat_data := in_rt_dat_data_d(conf.CMAC_IN_RT_LATENCY)
    io.in_wt_data := in_rt_wt_data_d(conf.CMAC_IN_RT_LATENCY)

    //: my $i= PKT_nvdla_stripe_info_stripe_st_FIELD;
    //: my $j= PKT_nvdla_stripe_info_stripe_end_FIELD;
    //: print qq(
    //: assign    in_dat_stripe_st  = in_dat_pd[${i}];
    //: assign    in_dat_stripe_end = in_dat_pd[${j}]; );

    io.in_dat_stripe_st := io.in_dat_pd(conf.PKT_nvdla_stripe_info_stripe_st_FIELD)
    io.in_dat_stripe_end := io.in_dat_pd(conf.PKT_nvdla_stripe_info_stripe_end_FIELD)

}
    
    

    



    















    



 

