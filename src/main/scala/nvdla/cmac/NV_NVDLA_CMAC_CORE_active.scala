package nvdla

import chisel3._
import chisel3.experimental._



//this module is to active dat and wt

class NV_NVDLA_CMAC_CORE_active(implicit val conf: cmacConfiguration) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

        //: for(my $i=0; $i<CMAC_INPUT_NUM; $i++){
        //: print qq(
        //: input [CMAC_BPE-1:0] in_dat_data${i};)
        //: }
        val in_dat_data = Input(Vec(conf.CMAC_INPUT_NUM, UInt((conf.CMAC_BPE).W)))

        val in_dat_mask = Input(UInt((conf.CMAC_ATOMC).W))
        val in_dat_pvld = Input(Bool())
        val in_dat_stripe_end = Input(Bool())
        val in_dat_stripe_st = Input(Bool())

        //: for(my $i=0; $i<CMAC_INPUT_NUM; $i++){
        //: print qq(
        //: input [CMAC_BPE-1:0] in_wt_data${i};)
        val in_wt_data = Input(Vec(conf.CMAC_INPUT_NUM, UInt((conf.CMAC_BPE).W)))

        val in_wt_mask = Input(UInt(conf.CMAC_ATOMC.W))
        val in_wt_pvld = Input(Bool())
        val in_wt_sel = Input(UInt((conf.CMAC_ATOMK_HALF).W))

        //: for(my $i=0; $i<CMAC_ATOMK_HALF; $i++){
        //: print qq(
        //:  output [CMAC_BPE*CMAC_ATOMC-1:0] dat${i}_actv_data;
        //:  output [CMAC_ATOMC-1:0] dat${i}_actv_nz;
        //:  output [CMAC_ATOMC-1:0] dat${i}_actv_pvld;
        //:  output [CMAC_ATOMC-1:0] dat${i}_pre_mask;
        //:  output dat${i}_pre_pvld;
        //:  output dat${i}_pre_stripe_end;
        //:  output dat${i}_pre_stripe_st;
        //: )
        //: } 
        val dat_actv_data = Output(Vec(conf.CMAC_ATOMK_HALF, UInt((conf.CMAC_BPE*conf.CMAC_ATOMC).W)))
        val dat_actv_nz = Output(Vec(conf.CMAC_ATOMK_HALF, UInt((conf.CMAC_ATOMC).W)))
        val dat_actv_pvld = Output(Vec(conf.CMAC_ATOMK_HALF, UInt((conf.CMAC_ATOMC).W)))
        val dat_pre_mask = Output(Vec(conf.CMAC_ATOMK_HALF, UInt((conf.CMAC_ATOMC).W)))
        val dat_pre_pvld = Output(Vec(conf.CMAC_ATOMK_HALF, Bool()))
        val dat_pre_stripe_end = Output(Vec(conf.CMAC_ATOMK_HALF, Bool()))   
        val dat_pre_stripe_st = Output(Vec(conf.CMAC_ATOMK_HALF, Bool()))   

        //: for(my $i=0; $i<CMAC_ATOMK_HALF; $i++){
        //: print qq(
        //: output [CMAC_BPE*CMAC_ATOMC-1:0] wt${i}_actv_data;
        //: output [CMAC_ATOMC-1:0] wt${i}_actv_nz;
        //: output [CMAC_ATOMC-1:0] wt${i}_actv_pvld;
        //: output [CMAC_ATOMC-1:0] wt${i}_sd_mask;
        //: output wt${i}_sd_pvld;
        //: )
        //: } 
        val wt_actv_data = Output(Vec(conf.CMAC_ATOMK_HALF, UInt((conf.CMAC_BPE*conf.CMAC_ATOMC).W)))   
        val wt_actv_nz = Output(Vec(conf.CMAC_ATOMK_HALF, UInt((conf.CMAC_ATOMC).W)))
        val wt_actv_pvld = Output(Vec(conf.CMAC_ATOMK_HALF, UInt((conf.CMAC_ATOMC).W)))
        val wt_sd_mask = Output(Vec(conf.CMAC_ATOMK_HALF, UInt((conf.CMAC_ATOMC).W)))        
        val wt_sd_pvld = Output(Vec(conf.CMAC_ATOMK_HALF, Bool()))    
       
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
                





    //: for(my $i=0; $i<CMAC_ATOMK_HALF; $i++){
    //: print qq(
    //: reg  [CMAC_BPE*CMAC_ATOMC-1:0]  dat_actv_data_reg${i};
    //: )
    val dat_actv_data_reg = Reg(Vec(conf.CMAC_ATOMK_HALF, UInt((conf.CMAC_BPE*conf.CMAC_ATOMC).W)))
    val dat_pre_data_w = Wire(UInt((conf.CMAC_BPE*conf.CMAC_ATOMC).W))
    val dat_pre_mask_w = Wire(UInt((conf.CMAC_ATOMC).W))
    val dat_pre_nz_w = Reg(UInt((conf.CMAC_ATOMC).W)) 
    io.dat_pre_stripe_end := Reg(Bool()) 
    io.dat_pre_stripe_st := Reg(Bool())
    val wt_pre_data = Reg(UInt((conf.CMAC_BPE*conf.CMAC_ATOMC).W)) 
    val wt_pre_data_w = Wire(UInt((conf.CMAC_BPE*conf.CMAC_ATOMC).W))
    val wt_pre_mask = Reg(UInt((conf.CMAC_ATOMC).W)) 
    val wt_pre_mask_w = Wire(UInt((conf.CMAC_ATOMC).W))
    val wt_pre_nz_w = Reg(UInt((conf.CMAC_ATOMC).W)) 

    //: my $kk=CMAC_ATOMC;
    //: for(my $i=0; $i<CMAC_ATOMK_HALF; $i++){
    //: print qq(
    //: wire [${kk}-1:0] wt${i}_sd_mask={${kk}{1'b0}};
    //: wire [${kk}-1:0] dat${i}_pre_mask={${kk}{1'b0}};
    //: )
    //: }
    io.wt_sd_mask := Wire(Vec(conf.CMAC_ATOMK_HALF, "b0".asUInt((conf.CMAC_ATOMC).W)))        
    io.dat_pre_mask := Wire(Vec(conf.CMAC_ATOMK_HALF, "b0".asUInt((conf.CMAC_ATOMC).W))) 



/////////////////////////////////////// handle weight ///////////////////////
    // weight pack
    //:    print "assign    wt_pre_data_w  = {";
    //:    for(my $i = CMAC_ATOMC-1; $i >= 0; $i --) {
    //:        print "in_wt_data${i}";
    //:        if($i == 0) {
    //:            print "};\n";
    //:        } elsif ($i % 8 == 0) {
    //:            print ",\n                       ";
    //:        } else {
    //:            print ", ";
    //:        }
    //:    }
    for(i <- 0 to conf.CMAC_ATOMC-1){
            wt_pre_data_w(i*conf.CMAC_BPE+conf.CMAC_BPE-1, i*conf.CMAC_BPE) := io.in_wt_data(i)
    }

    // weight mask pack
    //:    print "assign    wt_pre_mask_w = {";
    //:    for(my $i = CMAC_ATOMC-1; $i >= 0; $i --) {
    //:        print "in_wt_mask[${i}]";
    //:        if($i == 0) {
    //:            print "};\n";
    //:        } elsif ($i % 8 == 0) {
    //:            print ",\n                       ";
    //:        } else {
    //:            print ", ";
    //:        }
    //:    
    for(i <- 0 to conf.CMAC_ATOMC-1){
            wt_pre_mask_w(i) := io.in_wt_mask(i)
    }
    

    // 1 pipe for input
    //: my $i=CMAC_ATOMC;
    //: my $j=CMAC_ATOMK_HALF;
    //: &eperl::flop(" -q  wt_pre_nz    -en in_wt_pvld -d  wt_pre_mask_w -wid ${i}  -clk nvdla_core_clk -rst nvdla_core_rstn"); 
    //: &eperl::flop(" -q wt_pre_sel -d \"in_wt_sel&{${j}{in_wt_pvld}}\" -wid ${j} -clk nvdla_core_clk -rst nvdla_core_rstn");
    //: 
    //:     for (my $i = 0; $i < CMAC_ATOMC; $i ++) {
    //:         my $b0 = $i * 8;
    //:         my $b1 = $i * 8 + 7;
    //:  &eperl::flop("-nodeclare -norst -q  wt_pre_data[${b1}:${b0}]  -en \"in_wt_pvld & wt_pre_mask_w[${i}]\" -d  \"wt_pre_data_w[${b1}:${b0}]\" -clk nvdla_core_clk"); 
    //:  }

    val wt_pre_nz = Reg(UInt(conf.CMAC_ATOMC.W))
    val wt_pre_sel = Reg(UInt(conf.CMAC_ATOMK_HALF.W))

    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn) {
        when(io.in_wt_pvld) {wt_pre_nz := wt_pre_mask_w}
        wt_pre_sel := io.in_wt_sel&fill(conf.CMAC_ATOMK_HALF, io.in_wt_pvld)
    } 


    withClock(io.nvdla_core_clk) {
        for(i <- 0 to (conf.CMAC_ATOMC-1)){
            when (io.in_wt_pvld & wt_pre_mask_w(i)) {
                wt_pre_data(i*8+7,i*8) := wt_pre_data_w(i*8+7,i*8)}
        }}

// put input weight into shadow.
//:     for(my $i = 0; $i < CMAC_ATOMK_HALF; $i ++) {
//:         print qq (
//:     reg wt${i}_sd_pvld;
//:     wire    wt${i}_sd_pvld_w = wt_pre_sel[${i}] ? 1'b1 : dat_pre_stripe_st ? 1'b0 : wt${i}_sd_pvld; ); 
//: my $kk=CMAC_ATOMC;
//: &eperl::flop("-nodeclare -q  wt${i}_sd_pvld  -d \"wt${i}_sd_pvld_w\" -clk nvdla_core_clk -rst nvdla_core_rstn "); 
//: &eperl::flop(" -q  wt${i}_sd_nz -en wt_pre_sel[${i}] -d  \"wt_pre_nz\" -wid ${kk} -clk nvdla_core_clk -rst nvdla_core_rstn"); 
//: 

//: print qq(
//: reg [CMAC_BPE*CMAC_ATOMC-1:0] wt${i}_sd_data; );
//:  for(my $k = 0; $k < CMAC_ATOMC; $k ++) {
//:     my $b0 = $k * 8;
//:     my $b1 = $k * 8 + 7;
//:     &eperl::flop("-nodeclare -norst -q  wt${i}_sd_data[${b1}:${b0}]  -en \"wt_pre_sel[${i}] & wt_pre_nz[${k}]\" -d  \"wt_pre_data[${b1}:${b0}] \" -clk nvdla_core_clk"); 
//:     }
//: }
//: &eperl::flop(" -q  dat_actv_stripe_end  -d \"dat_pre_stripe_end\" -clk nvdla_core_clk -rst nvdla_core_rstn "); 

    val wt_sd_pvld_w = Wire(Vec(conf.CMAC_ATOMK_HALF, Bool()))
    val wt_sd_nz = Reg(Vec(conf.CMAC_ATOMK_HALF, UInt(conf.CMAC_ATOMC.W)))
    val wt_sd_data = Reg(Vec(conf.CMAC_ATOMK_HALF, UInt((conf.CMAC_ATOMC *conf.CMAC_BPE).W)))
    for(i <- 0 to conf.CMAC_ATOMK_HALF-1){
        io.wt_sd_pvld(i) := Reg(Bool())
        wt_sd_pvld_w(i) := Mux(wt_pre_sel(i).asBool, true.B, Mux(io.dat_pre_stripe_st, false.B, io.wt_sd_pvld(i)))
        withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn) { 
            io.wt_sd_pvld(i) := wt_sd_pvld_w(i)
            when(wt_pre_sel(i).asBool()) {wt_sd_nz(i) := wt_pre_nz}
        }
        for(j <- 0 to conf.CMAC_ATOMC-1){
            withClock(io.nvdla_core_clk) { 
                when(wt_pre_sel(i).asBool()&wt_pre_nz(j).asBool()) {wt_sd_data(i)(j*8+7, j*8) := wt_pre_data(j*8+7, j*8)}
            }   
        }      
    }

 

    val dat_actv_stripe_end = Reg(Bool())
    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn) {
        dat_actv_stripe_end := io.dat_pre_stripe_end
    } 

 // pop weight from shadow when new stripe begin.
//:     for(my $i = 0; $i < CMAC_ATOMK_HALF; $i ++) {
//:         print qq {
//:     reg wt${i}_actv_vld; 
//:     reg [CMAC_BPE*CMAC_ATOMC-1:0] wt${i}_actv_data;
//:     wire wt${i}_actv_pvld_w = dat_pre_stripe_st ? wt${i}_sd_pvld : dat_actv_stripe_end ? 1'b0 : wt${i}_actv_vld;
//: };
//: my $cmac_atomc = CMAC_ATOMC;
//: &eperl::flop(" -q  wt${i}_actv_vld   -d \"wt${i}_actv_pvld_w\" -clk nvdla_core_clk -rst nvdla_core_rstn -nodeclare"); 
//: &eperl::flop(" -q  wt${i}_actv_pvld  -d \"{${cmac_atomc}{wt${i}_actv_pvld_w}}\" -clk nvdla_core_clk -rst nvdla_core_rstn -wid ${cmac_atomc}"); 
//: &eperl::flop(" -q  wt${i}_actv_nz    -en \"dat_pre_stripe_st & wt${i}_actv_pvld_w\" -d  \"wt${i}_sd_nz\" -clk nvdla_core_clk -rst nvdla_core_rstn -wid ${cmac_atomc}"); 
//: 
//:         for(my $k = 0; $k < CMAC_ATOMC; $k ++) {
//:             my $b0 = $k * 8;
//:             my $b1 = $k * 8 + 7;
//: &eperl::flop("-nodeclare -norst -q  wt${i}_actv_data[${b1}:${b0}]  -en \"dat_pre_stripe_st & wt${i}_actv_pvld_w\" -d  \"{8{wt${i}_sd_nz[${k}]}} & wt${i}_sd_data[${b1}:${b0}]\" -clk nvdla_core_clk"); 
//:     }
//: }   

    val wt_actv_vld = Reg(Vec(conf.CMAC_ATOMK_HALF, Bool()))
    io.wt_actv_data := Reg(Vec(conf.CMAC_ATOMK_HALF, UInt((conf.CMAC_BPE*conf.CMAC_ATOMC).W)))
    val wt_actv_pvld_w = Wire(conf.CMAC_ATOMK_HALF, Bool())
    for(i <- 0 to conf.CMAC_ATOMK_HALF-1){
        wt_actv_pvld_w(i) := Mux(io.dat_pre_stripe_st, io.wt_sd_pvld(i), Mux(dat_actv_stripe_end, false.B, wt_actv_vld(i)))
        withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn) { 
            wt_actv_vld(i) := wt_actv_pvld_w(i)
            io.wt_actv_pvld(i) := fill(conf.CMAC_ATOMC, wt_sd_pvld_w(i))
            when(io.dat_pre_stripe_st& wt_actv_pvld_w(i)) {io.wt_actv_nz(i) := RegNext(wt_sd_nz(i))}
        }
        for(j <- 0 to conf.CMAC_ATOMC-1){
            withClock(io.nvdla_core_clk){
                when(io.dat_pre_stripe_st&(wt_actv_pvld_w(i))){
                    io.wt_actv_data(i)(j*8+7, j*8) := Fill(8, wt_sd_nz(i)(j))&(wt_sd_data(i)(j*8+7, j*8))
                }
            }
        }         
    }

    ////////////////////////////////// handle data ///////////////
    // data pack
    //:    print "assign    dat_pre_data_w  = {";
    //:    for(my $i = CMAC_INPUT_NUM-1; $i >= 0; $i --) {
    //:        print "in_dat_data${i}";
    //:        if($i == 0) {
    //:            print "};\n";
    //:        } elsif ($i % 8 == 0) {
    //:            print ",\n                       ";
    //:        } else {

    //:            print ", ";
    //:        }
    //:    }
    // data mask pack
    //:    print "assign    dat_pre_mask_w = {";
    //:    for(my $i = CMAC_INPUT_NUM-1; $i >= 0; $i --) {
    //:        print "in_dat_mask[${i}]";
    //:        if($i == 0) {
    //:            print "};\n";
    //:        } elsif ($i % 8 == 0) {
    //:            print ",\n                       ";
    //:        } else {
    //:            print ", ";
    //:        }
    //:    }
    for(i <- 0 to conf.CMAC_INPUT_NUM-1){
        dat_pre_data_w(i*8+7, i*8) := io.in_dat_data(i)
    }

    for(i <- 0 to conf.CMAC_INPUT_NUM-1){
        dat_pre_mask_w(i) := io.in_dat_mask(i)
    }


    // 1 pipe for input data
    //: my $kk= CMAC_ATOMC; 
    //: &eperl::flop(" -q  dat_pre_pvld   -d \"in_dat_pvld\"  -clk nvdla_core_clk -rst nvdla_core_rstn "); 
    //: &eperl::flop(" -q  dat_pre_nz     -en \"in_dat_pvld\" -d  \"dat_pre_mask_w\" -wid ${kk} -clk nvdla_core_clk -rst nvdla_core_rstn"); 
    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn) {
        io.dat_pre_pvld := io.in_dat_pvld
        when(io.in_dat_pvld) {val dat_pre_nz = dat_pre_mask_w }
    }    


//:     for (my $i = 0; $i < CMAC_ATOMC; $i ++) {
//:         my $b0 = $i * 8;
//:         my $b1 = $i * 8 + 7;
//: &eperl::flop("-nodeclare -norst -q  dat_pre_data[${b1}:${b0}]  -en \"in_dat_pvld & dat_pre_mask_w[${i}]\" -d  \"dat_pre_data_w[${b1}:${b0}]\" -clk nvdla_core_clk"); 
//: }
//: &eperl::flop("-nodeclare -q  dat_pre_stripe_st   -d  \"in_dat_stripe_st & in_dat_pvld\" -clk nvdla_core_clk -rst nvdla_core_rstn "); 
//: &eperl::flop("-nodeclare -q  dat_pre_stripe_end  -d  \"in_dat_stripe_end & in_dat_pvld \" -clk nvdla_core_clk -rst nvdla_core_rstn "); 
//:     for(my $i = 0; $i < CMAC_ATOMK_HALF; $i ++) {
//:         print qq {
//: assign    dat${i}_pre_pvld       = dat_pre_pvld;
//: assign    dat${i}_pre_stripe_st  = dat_pre_stripe_st;
//: assign    dat${i}_pre_stripe_end = dat_pre_stripe_end;
//:     };
//: }

    val dat_pre_data = Reg(UInt((conf.CMAC_BPE*conf.CMAC_ATOMC).W))
    for(i <- 0 to conf.CMAC_ATOMC-1){        
        withClock(io.nvdla_core_clk) {
            when(io.in_dat_pvld & in_dat_mask_w(i)) { dat_pre_data(i*8+7,i*8) := dat_pre_data_w(i*8+7,i*8) }
        }
    }

    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn) {

        io.dat_pre_stripe_st := io.in_dat_stripe_st&io.in_dat_pvld
        io.dat_pre_stripe_end := io.in_dat_stripe_end&io.in_dat_pvld
    } 


// get data for cmac, 1 pipe.
//: my $atomc= CMAC_ATOMC; 
//: for(my $i = 0; $i < CMAC_ATOMK_HALF; $i ++) {
//:     my $l = $i + 8;
//:     &eperl::flop(" -q  dat_actv_pvld_reg${i}  -d \"{${atomc}{dat_pre_pvld}}\" -wid ${atomc} -clk nvdla_core_clk -rst nvdla_core_rstn "); 
//:     &eperl::flop(" -q  dat_actv_nz_reg${i}    -en dat_pre_pvld -d  dat_pre_nz -wid $atomc -clk nvdla_core_clk -rst nvdla_core_rstn"); 
//:     for(my $k = 0; $k < CMAC_ATOMC; $k ++) {
//:         my $j = int($k/2);
//:         my $b0 = $k * 8;
//:         my $b1 = $k * 8 + 7;
//:         &eperl::flop("-nodeclare -norst -q  dat_actv_data_reg${i}[${b1}:${b0}]  -en \"dat_pre_pvld & dat_pre_nz[${k}]\" -d  \"dat_pre_data[${b1}:${b0}]\" -clk nvdla_core_clk"); 
//:     }
//: }
    val dat_actv_pvld_reg = Reg(Vec(conf.CMAC_ATOMK_HALF, UInt((conf.CMAC_ATOMC).W)))
    val dat_actv_nz_reg = Reg(Vec(conf.CMAC_ATOMK_HALF, UInt((conf.CMAC_ATOMC).W)))

    for(i <- 0 to conf.CMAC_ATOMK_HALF-1){
        
        withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn) {

            dat_actv_pvld_reg(i) := Fill(conf.CMAC_ATOMC, io.dat_pre_pvld(i))
            when(io.dat_pre_pvld(i)){
                dat_actv_nz_reg(i) := dat_pre_nz
            }
            for(k <- 0 to conf.CMAC_ATOMC-1){                   
                withClock(io.nvdla_core_clk){
                    when(io.dat_pre_pvld(i) & dat_pre_nz(k)){
                        dat_actv_data_reg(i)(k*8+7, k*8):= dat_pre_data(k*8+7,k*8)
                    }
                }
            }
        }     
    }

//:     for(my $i = 0; $i < CMAC_ATOMK_HALF; $i ++) {
//:         print qq {
//: assign    dat${i}_actv_pvld = dat_actv_pvld_reg${i};
//: assign    dat${i}_actv_data = dat_actv_data_reg${i};
//: assign    dat${i}_actv_nz   = dat_actv_nz_reg${i};

    io.dat_actv_pvld := dat_actv_pvld_reg
    io.dat_actv_data := dat_actv_data_reg
    io.dat_actv_nz := dat_actv_nz_reg



  }