package nvdla

import chisel3._



//this module is to active dat and wt

class NV_NVDLA_CMAC_REG_single(implicit val conf: cmacConfiguration) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())      
        val nvdla_core_rstn = Input(Bool())

        // Register control interface
        val reg_rd_data = Output(UInt(32.W))
        val reg_offset = Input(UInt(12.W))
        val reg_wr_data = Input(UInt(32.W))//(UNUSED_DEC)

        val reg_wr_en = Input(Bool())

        // Writable register flop/trigger outputs


         
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
                




    ////////////////// unpack data&nz //////////////
    //: for(my $i=0; $i<CMAC_ATOMC; $i++){
    //: my $bpe = CMAC_BPE;
    //: my $data_msb = ($i+1) * $bpe - 1;
    //: my $data_lsb = $i * $bpe;
    //: print qq(
    //: wire [${bpe}-1:0] wt_actv_data${i} = wt_actv_data[${data_msb}:${data_lsb}];
    //: wire [${bpe}-1:0] dat_actv_data${i} = dat_actv_data[${data_msb}:${data_lsb}];
    //: wire wt_actv_nz${i} = wt_actv_nz[${i}];
    //: wire dat_actv_nz${i} = dat_actv_nz[${i}];
    //: )
    //: }

    val wt_actv_data_wire = Wire(Vec(conf.CMAC_ATOMC, UInt((conf.CMAC_BPE).W)))
    val dat_actv_data_wire = Wire(Vec(conf.CMAC_ATOMC, UInt((conf.CMAC_BPE).W)))
    val wt_actv_nz_wire = Wire(Vec(conf.CMAC_ATOMC, Bool()))
    val dat_actv_nz_wire = Wire(Vec(conf.CMAC_ATOMC, Bool()))

    for(i <- 0 to conf.CMAC_ATOMC-1){
        wt_actv_data_wire(i) := io.wt_actv_data((i+1)*conf.CMAC_BPE-1, i*conf.CMAC_BPE)
        dat_actv_data_wire(i) := io.dat_actv_data((i+1)*conf.CMAC_BPE-1, i*conf.CMAC_BPE)
        wt_actv_nz_wire(i) := io.wt_actv_nz(i)
        dat_actv_nz_wire(i) := io.dat_actv_nz(i)
    }

    //`ifdef DESIGNWARE_NOEXIST
    //wire signed [CMAC_RESULT_WIDTH-1:0] sum_out;
    //wire [CMAC_ATOMC-1:0] op_out_pvld;
    //: my $mul_result_width = 18;
    //: my $bpe = CMAC_BPE; 
    //: my $rwidth = CMAC_RESULT_WIDTH; 
    //: my $result_width = $rwidth * CMAC_ATOMC * 2; 
    //: for (my $i=0; $i < CMAC_ATOMC; ++$i) {
    //:     print "assign op_out_pvld[${i}] = wt_actv_pvld[${i}] & dat_actv_pvld[${i}] & wt_actv_nz${i} & dat_actv_nz${i};\n";
    //:     print "wire signed [${mul_result_width}-1:0] mout_$i = (\$signed(wt_actv_data${i}) * \$signed(dat_actv_data${i})) & \$signed({${mul_result_width}{op_out_pvld[${i}]}});\n";
    //: }
    //:
    //: print "assign sum_out = \n";
    //: for (my $i=0; $i < CMAC_ATOMC; ++$i) {
    //:     print "    ";
    //:     print "+ " if ($i != 0);
    //:     print "mout_$i\n";
    //: }
    //: print "; \n";
    //`endif

    val sum_out = "b0".S((conf.CMAC_RESULT_WIDTH).W)
    val op_out_pvld = Wire(UInt(conf.CMAC_ATOMC).W)
    val mout = Wire(Vec(conf.CMAC_ATOMC, SInt(18.W)))
 
    for(i <- 0 to conf.CMAC_ATOMC-1){
        op_out_pvld(i) := io.wt_actv_pvld(i)&io.dat_actv_pvld(i)&wt_actv_nz_wire(i)&dat_actv_nz_wire(i)
        mout(i) := ((wt_actv_data_wire(i).zext*(dat_actv_data_wire(i).zext))&Fill(18, op_out_pvld(i)))
    }  

    for(i <- 0 to conf.CMAC_ATOMC-1){
        sum_out:=sum_out+mout(i)
    } 


    //add pipeline for retiming
    val pp_pvld_d0 = io.dat_actv_pvld(0)&io.wt_actv_pvld(0)
    //wire [CMAC_RESULT_WIDTH-1:0] sum_out_d0 = $unsigned(sum_out);
    val sum_out_d0 = sum_out.asUInt

    withClock(nvdla_core_clk){
        val sum_out_dd = ShiftRegister(sum_out_d0, conf.CMAC_OUT_RETIMING, pp_pvld_d0)
        val pp_pvld_dd = ShiftRegister(pp_pvld_d0, conf.CMAC_OUT_RETIMING, pp_pvld_d0)
    }

    io.mac_out_data := sum_out_dd
    io.mac_out_pvld := pp_pvld_d

  }