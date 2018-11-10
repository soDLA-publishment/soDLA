package nvdla

import chisel3._




class NV_NVDLA_CMAC_CORE_rt_in(implicit val conf: cmacConfiguration) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock()) 
        val nvdla_wg_clk = Input(Clock())          
        val nvdla_core_rstn = Input(Bool())

        val cfg_is_wg = Input(Bool())
        val cfg_reg_en = Input(Bool())

        //: for(my $i=0; $i<CMAC_ATOMK_HALF; $i++){
        //: print qq(
        //: input[CMAC_RESULT_WIDTH-1:0] out_data${i}; )
        //: }

        val out_data = Input(Vec(conf.CMAC_ATOMK_HALF, UInt(conf.CMAC_RESULT_WIDTH.W)))

        val out_mask = Input(UInt(conf.CMAC_ATOMK_HALF.W))
        val out_pd = Input(UInt(9.W))
        val out_pvld = Input(Bool())
        val dp2reg_done = Output(Bool())

        //: for(my $i=0; $i<CMAC_ATOMK_HALF; $i++){
        //: print qq(
        //: output[CMAC_RESULT_WIDTH-1:0] mac2accu_data${i}; )
        //: }

        val mac2accu_data = Output(Vec(conf.CMAC_ATOMK_HALF.W, UInt(conf.CMAC_RESULT_WIDTH.W)))
        val mac2accu_mask = Output(UInt(conf.CMAC_ATOMK_HALF.W))
        val mac2accu_pd = Output(UInt(9.W))
        val mac2accu_pvld = Output(Bool())
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
                
    val out_layer_done = Wire(Bool())

    val out_rt_done_d_wire = Wire(Bool())
    val out_rt_done_d_regs = Seq.fill(conf.CMAC_OUT_RT_LATENCY+1)(Reg(Bool()))  
    val out_rt_done_d = VecInit(out_rt_done_d_wire +: out_rt_done_d_regs)     
    

    //==========================================================
    // Config logic
    //==========================================================

    //: &eperl::flop(" -q  \"cfg_reg_en_d1\"  -d \"cfg_reg_en\" -clk nvdla_core_clk -rst nvdla_core_rstn ");
    //: &eperl::flop(" -q  \"cfg_is_wg_d1\"  -en \"cfg_reg_en\" -d  \"cfg_is_wg\" -clk nvdla_core_clk -rst nvdla_core_rstn ");

    val cfg_reg_en_d1 = Reg(Bool())
    val cfg_is_wg_d1 = Reg(Bool())

    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){
        cfg_reg_en_d1 := io.cfg_reg_en
        when(io.cfg_reg_en){
            cfg_is_wg_d1:= io.cfg_is_wg
        }
    }

    //==========================================================
    // Output retiming
    //==========================================================

    out_layer_done := out_pd(conf.PKT_nvdla_stripe_info_layer_end_FIELD) &out_pd(conf.PKT_nvdla_stripe_info_stripe_end_FIELD)& io.out_pvld

    //:     my $kk = CMAC_ATOMK_HALF;
    //:     my $jj = CMAC_RESULT_WIDTH;
    //:     print "wire             out_rt_pvld_d0 = out_pvld;\n";
    //:     print "wire [$kk-1:0]   out_rt_mask_d0 = out_mask;\n";
    //:     print "wire [8:0]       out_rt_pd_d0   = out_pd;\n";
    //:     for(my $k = 0; $k < $kk; $k ++) {
    //:     print "wire [${jj}-1:0]    out_rt_data${k}_d0 =  out_data${k};\n";
    //:     }

    val out_rt_pvld_d_wire = Wire(Bool())
    val out_rt_pvld_d_regs = Seq.fill(conf.CMAC_OUT_RT_LATENCY)(Reg(Bool()))  
    val out_rt_pvld_d = VecInit(out_rt_pvld_d_wire +: out_rt_pvld_d_regs)  

    val out_rt_mask_d_wire = Wire(UInt(conf.CMAC_RESULT_WIDTH.W))
    val out_rt_mask_d_regs = Seq.fill(conf.CMAC_OUT_RT_LATENCY)(Reg(UInt(conf.CMAC_RESULT_WIDTH.W)))  
    val out_rt_mask_d = VecInit(out_rt_mask_d_wire +: out_rt_mask_d_regs)    

    val out_rt_pd_d_wire = Wire(UInt(9.W))
    val out_rt_pd_d_regs = Seq.fill(conf.CMAC_OUT_RT_LATENCY)(Reg(UInt(9.W)))  
    val out_rt_pd_d = VecInit(out_rt_pd_d_wire +: out_rt_pd_d_regs)  

    val out_rt_data_d_wire = Wire(Vec(conf.CMAC_ATOMK_HALF, UInt(conf.CMAC_RESULT_WIDTH.W)))
    val out_rt_data_d_regs = Seq.fill(conf.CMAC_OUT_RT_LATENCY)(Reg(Vec(conf.CMAC_ATOMK_HALF, UInt(conf.CMAC_RESULT_WIDTH.W))))  
    val out_rt_data_d = VecInit(out_rt_data_d_wire +: out_rt_data_d_regs)        

    out_rt_pvld_d(0) := io.out_pvld
    out_rt_mask_d(0) := io.out_mask
    out_rt_pd_d(0) := io.out_pd
    out_rt_data_d(0) := io.out_data

    //:     my $latency = CMAC_OUT_RT_LATENCY;
    //:     my $kk = CMAC_ATOMK_HALF;
    //:     my $res_width = CMAC_RESULT_WIDTH;
    //:     for(my $i = 0; $i < $latency; $i ++) {
    //:         my $j = $i + 1;
    //: &eperl::flop(" -q  out_rt_pvld_d${j}  -d \"out_rt_pvld_d${i}\" -clk nvdla_core_clk -rst nvdla_core_rstn "); 
    //: &eperl::flop(" -q  out_rt_mask_d${j}  -d \"out_rt_mask_d${i}\" -wid $kk -clk nvdla_core_clk -rst nvdla_core_rstn "); 
    //: &eperl::flop("-wid 9 -q  out_rt_pd_d${j}  -en \"out_rt_pvld_d${i}\" -d  \"out_rt_pd_d${i}\" -clk nvdla_core_clk -rst nvdla_core_rstn");
    //:     for(my $k = 0; $k < $kk; $k ++) {
    //: &eperl::flop("-norst -wid $res_width  -q out_rt_data${k}_d${j} -en \"out_rt_mask_d${i}[${k}]\" -d  \"out_rt_data${k}_d${i}\" -clk nvdla_core_clk");  
    //:         }
    //:     }
    //:
    //:     my $i = $latency;
    //:     print "assign    mac2accu_pvld = out_rt_pvld_d${i};\n";
    //:     print "assign    mac2accu_mask = out_rt_mask_d${i};\n";
    //:     print "assign    mac2accu_pd = out_rt_pd_d${i};\n";
    //:     my $kk = CMAC_ATOMK_HALF;
    //:     for(my $k = 0; $k < $kk; $k ++) {
    //:         print "assign    mac2accu_data${k} = out_rt_data${k}_d${i};\n";
    //:     }
    //:

    for(t <- 0 to conf.CMAC_OUT_RT_LATENCY-1){
        withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){
            out_rt_pvld_d(t+1) := out_rt_pvld_d(t)
            out_rt_mask_d(t+1) := out_rt_mask_d(t)
            when(out_rt_pvld_d(t)){
                out_rt_pd_d(t+1) := out_rt_pd_d(t)
            }
        }    
        withClock(io.nvdla_core_clk){
            out_rt_data_d(t+1) := out_rt_data_d(t)
        }        
    } 

    io.mac2accu_pvld := out_rt_pvld_d(conf.CMAC_OUT_RT_LATENCY)
    io.mac2accu_mask := out_rt_mask_d(conf.CMAC_OUT_RT_LATENCY)
    io.mac2accu_pd := out_rt_pd_d(conf.CMAC_OUT_RT_LATENCY)
    io.mac2accu_data := out_rt_data_d(conf.CMAC_OUT_RT_LATENCY)

    // get layer done signal
    out_rt_done_d(0) := out_layer_done

    //: my $latency = CMAC_OUT_RT_LATENCY + 1;
    //: for(my $i = 0; $i < $latency; $i ++) {
    //:     my $j = $i + 1;
    //:  &eperl::flop(" -q  out_rt_done_d${j}  -d \"out_rt_done_d${i}\" -clk nvdla_core_clk -rst nvdla_core_rstn "); 
    //: };
    //: my $h = $latency;
    //: print "assign dp2reg_done = out_rt_done_d${h};\n";

    for(t <- 0 to conf.CMAC_OUT_RT_LATENCY){
        withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){
            out_rt_done_d(t+1) := out_rt_done_d(t)
        }       
    } 

    io.dp2reg_done := out_rt_done_d(conf.CMAC_OUT_RT_LATENCY+1)






  
}
    
    

    



    















    



 

