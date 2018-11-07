package nvdla

import chisel3._

class NV_NVDLA_cbuf(implicit val conf: cbufConfiguration) extends Module {
 
  //csb interface  
  val io = IO(new Bundle {

    //clock
    val nvdla_core_clk = Input(Clock())
    val nvdla_core_rstn = Input(Bool())

    val pwrbus_ram_pd = Input(UInt(32.W))

    //: for(my $i=0; $i<CBUF_WR_PORT_NUMBER ; $i++) {
    //: print qq(
    //: input[CBUF_ADDR_WIDTH-1:0] cdma2buf_wr_addr${i}; //|< i
    //: input[CBUF_WR_PORT_WIDTH-1:0] cdma2buf_wr_data${i}; //|< i
    //: input cdma2buf_wr_en${i};   //|< i
    //: input[CBUF_WR_BANK_SEL_WIDTH-1:0] cdma2buf_wr_sel${i}; //|< i
    //: )
    //: }


    val cdma2buf_wr_addr = Input(Vec(conf.CBUF_WR_PORT_NUMBER, UInt(conf.CBUF_ADDR_WIDTH.W)))
    val cdma2buf_wr_data = Input(Vec(conf.CBUF_WR_PORT_NUMBER, UInt(conf.CBUF_WR_PORT_WIDTH.W)))
    val cdma2buf_wr_en = Input(Vec(conf.CBUF_WR_PORT_NUMBER, Bool()))
    val cdma2buf_wr_sel = Input(Vec(conf.CBUF_WR_PORT_NUMBER, UInt(conf.CBUF_WR_BANK_SEL_WIDTH.W))) 

    val sc2buf_dat_rd_en = Input(Bool())     /* data valid */
    val sc2buf_dat_rd_addr = Input(UInt(conf.CBUF_ADDR_WIDTH.W))
    val sc2buf_dat_rd_shift = Input(UInt(conf.CBUF_RD_DATA_SHIFT_WIDTH.W))
    val sc2buf_dat_rd_next1_en = Input(Bool())
    val sc2buf_dat_rd_next1_addr = Input(UInt(conf.CBUF_ADDR_WIDTH.W))
    val sc2buf_dat_rd_valid = Output(Bool())               /* data valid */
    val sc2buf_dat_rd_data = Output(UInt(CBUF_RD_PORT_WIDTH.W))

    val sc2buf_wt_rd_en = Input(Bool()) /* data valid */
    val sc2buf_wt_rd_addr = Input(UInt(conf.CBUF_ADDR_WIDTH.W))
    val sc2buf_wt_rd_valid = Output(Bool())
    val sc2buf_wt_rd_data = Output(UInt(CBUF_RD_PORT_WIDTH.W))

    if(conf.CBUF_WEIGHT_COMPRESSED){
        val sc2buf_wmb_rd_en = Input(Bool())
        val sc2buf_wmb_rd_addr = Input(UInt(conf.CBUF_ADDR_WIDTH.W))
        val sc2buf_wmb_rd_valid = Output(Bool())
        val sc2buf_wmb_rd_data = Output(UInt(CBUF_RD_PORT_WIDTH.W))
    }
  })

    //////////step1:write handle
    //decode write address to sram
    //: my $bank_slice= CBUF_BANK_SLICE;  #address part for select bank
    //: for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    //:     for(my $k=0; $k<CBUF_RAM_PER_BANK ; $k++){
    //:         my $kmod2 = $k%2;
    //:         my $kmod4 = $k%4; 
    //:         for(my $i=0; $i<CBUF_WR_PORT_NUMBER ; $i++){
    //:         if((CBUF_BANK_RAM_CASE==0)||(CBUF_BANK_RAM_CASE==2)||(CBUF_BANK_RAM_CASE==4)){
    //:         print qq(
    //:     wire  bank${j}_ram${k}_wr${i}_en_d0 = cdma2buf_wr_en${i}&&(cdma2buf_wr_addr${i}[${bank_slice}]==${j}) &&(cdma2buf_wr_sel${i}[${k}]==1'b1);  );
    //:             }
    //:         if(CBUF_BANK_RAM_CASE==1){
    //:         print qq(
    //:     wire  bank${j}_ram${k}_wr${i}_en_d0 = cdma2buf_wr_en${i}&&(cdma2buf_wr_addr${i}[${bank_slice}]==${j})&&(cdma2buf_wr_addr${i}[0]==${k});  );
    //:             }
    //:         if(CBUF_BANK_RAM_CASE==3){
    //:         print qq(
    //:     wire  bank${j}_ram${k}_wr${i}_en_d0 = cdma2buf_wr_en${i}&&(cdma2buf_wr_addr${i}[${bank_slice}]==${j})&&(cdma2buf_wr_addr${i}[0]==${k})&&(cdma2buf_wr_sel${i}[${kmod2}]==1'b1 );  );
    //:             }
    //:         if(CBUF_BANK_RAM_CASE==5){
    //:         print qq(
    //:     wire  bank${j}_ram${k}_wr${i}_en_d0 = cdma2buf_wr_en${i}&&(cdma2buf_wr_addr${i}[${bank_slice}]==${j})&&(cdma2buf_wr_addr${i}[0]==${k})&&(cdma2buf_wr_sel${i}[${kmod4}]==1'b1 );  );
    //:             }
    //:         }
    //:     }
    //: }

    val bank_ram_wr_en_d0_t = Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Vec(conf.CBUF_WR_PORT_NUMBER, Bool()))))

    for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
        for(k <- 0 to conf.CBUF_RAM_PER_BANK-1){
            val kmod2 = k%2
            val kmod4 = k%4
            for(i <- 0 to conf.CBUF_WR_PORT_NUMBER-1){
                if((conf.CBUF_BANK_RAM_CASE==0)||(conf.CBUF_BANK_RAM_CASE==2)||(conf.CBUF_BANK_RAM_CASE==4)){
                    bank_ram_wr_en_d0_t(j)(k)(i):= io.cdma2buf_wr_en(i)&&(io.cdma2buf_wr_addr(i)(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)===j.U) &&(io.cdma2buf_wr_sel(j)(k)==="b1".U)
                }
                if(conf.CBUF_BANK_RAM_CASE==1){
                    bank_ram_wr_en_d0_t(j)(k)(i):= io.cdma2buf_wr_en(i)&&(io.cdma2buf_wr_addr(i)(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)===j.U) &&(io.cdma2buf_wr_sel(j)(0)=== k.U)
                }
                if(conf.CBUF_BANK_RAM_CASE==3){
                    bank_ram_wr_en_d0_t(j)(k)(i):= io.cdma2buf_wr_en(i)&&(io.cdma2buf_wr_addr(i)(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)===j.U) &&(io.cdma2buf_wr_sel(j)(0)===k.U)&&(io.cdma2buf_wr_sel(i)(kmod2)==="b1".U )
                }
                if(conf.CBUF_BANK_RAM_CASE==5){
                    bank_ram_wr_en_d0_t(j)(k)(i):= io.cdma2buf_wr_en(i)&&(io.cdma2buf_wr_addr(i)(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)===j.U) &&(io.cdma2buf_wr_sel(j)(0)===k.U)&&(io.cdma2buf_wr_sel(i)(kmod2)==="b1".U )
                }
            }
        }
    }

    //generate sram write en
    //: my $t1="";
    //: for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    //:     for(my $k=0; $k<CBUF_RAM_PER_BANK ; $k++){
    //:         for(my $i=0; $i<CBUF_WR_PORT_NUMBER; $i++){
    //:             ${t1} .= "bank${j}_ram${k}_wr${i}_en_d0 |";
    //:         }
    //:         print  "wire bank${j}_ram${k}_wr_en_d0  = ${t1}"."1'b0; \n";
    //:         $t1="";
    //:     &eperl::flop("-q bank${j}_ram${k}_wr_en_d1 -d bank${j}_ram${k}_wr_en_d0");
    //:     }
    //: }

    val bank_ram_wr_en_d0 = Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Bool())))
    val bank_ram_wr_en_d1 = Reg(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Bool())))
    for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
        for(k <- 0 to conf.CBUF_RAM_PER_BANK-1){          
            bank_ram_wr_en_d0(j)(k) := bank_ram_wr_en_d0_t(j)(k).exists //Bool OR-reduce p on all elts
            withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){
                bank_ram_wr_en_d1(j)(k) := bank_ram_wr_en_d0(j)(k) 
            }
            }
        }
    }

    // 1 pipe for timing
    //: my $kk=CBUF_ADDR_WIDTH;
    //: my $jj=CBUF_WR_PORT_WIDTH;
    //: for(my $i=0; $i<CBUF_WR_PORT_NUMBER ; $i++){
    //: &eperl::flop("-wid ${kk} -q cdma2buf_wr_addr${i}_d1 -d cdma2buf_wr_addr${i}");
    //: &eperl::flop("-wid ${jj} -norst -q cdma2buf_wr_data${i}_d1 -d cdma2buf_wr_data${i}");
    //: }

    val cdma2buf_wr_addr_d1 = Reg(Vec(conf.CBUF_WR_PORT_NUMBER, UInt(conf.CBUF_ADDR_WIDTH.W)))
    val cdma2buf_wr_data_d1 = Reg(Vec(conf.CBUF_WR_PORT_NUMBER, UInt(conf.CBUF_WR_PORT_WIDTH.W)))
    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){
        cdma2buf_wr_addr_d1 := io.cdma2buf_wr_addr
    }
    withClock(io.nvdla_core_clk){
        cdma2buf_wr_data_d1 := io.cdma2buf_wr_data
    }

    //generate bank write en
    //: my $t1="";
    //: for(my $i=0; $i<CBUF_WR_PORT_NUMBER; $i++){
    //:     for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    //:         for(my $k=0; $k<CBUF_RAM_PER_BANK; $k++){
    //:             $t1 .= "bank${j}_ram${k}_wr${i}_en_d0 |";
    //:         }
    //:         print "wire bank${j}_wr${i}_en_d0 = ${t1}"."1'b0; \n";
    //: &eperl::flop("-q bank${j}_wr${i}_en_d1 -d bank${j}_wr${i}_en_d0");
    //:         $t1="";
    //:     }
    //: }

    val bank_wr_en_d0 = Wire(Vec(conf.CBUF_WR_PORT_NUMBER, Vec(conf.CBUF_BANK_NUMBER, Bool())))
    val bank_wr_en_d1 = Reg(Vec(conf.CBUF_WR_PORT_NUMBER, Vec(conf.CBUF_BANK_NUMBER, Bool())))
    for(i <- 0 to conf.CBUF_WR_PORT_NUMBER-1){
        for(j <- 0 to conf.CBUF_BANK_NUMBER-1){          
            bank_wr_en_d0(j)(i) := bank_ram_wr_en_d0_t(j)(i).exists //Bool OR-reduce p on all elts
            withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){
                bank_wr_en_d1(j)(i) := bank_wr_en_d0(j)(i) 
            }
            }
        }
    }

    //generate bank write addr/data
    //: my $t1="";
    //: my $d1="";
    //: my $kk= CBUF_ADDR_WIDTH;
    //: my $jj= CBUF_WR_PORT_WIDTH;
    //: for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    //:         for(my $i=0; $i<CBUF_WR_PORT_NUMBER; $i++){
    //:         $t1 .="({${kk}{bank${j}_wr${i}_en_d1}}&cdma2buf_wr_addr${i}_d1)|";
    //:         $d1 .="({${jj}{bank${j}_wr${i}_en_d1}}&cdma2buf_wr_data${i}_d1)|";
    //:         }
    //:         my $t2 .="{${kk}{1'b0}}";
    //:         my $d2 .="{${jj}{1'b0}}";
    //:         print "wire [${kk}-1:0] bank${j}_wr_addr_d1 = ${t1}${t2}; \n";
    //:         print "wire [${jj}-1:0] bank${j}_wr_data_d1 = ${d1}${d2}; \n";
    //:         $t1="";
    //:         $d1="";
    //: }

    val cdma2buf_wr_addr_with_en = Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_WR_PORT_NUMBER, UInt(conf.CBUF_ADDR_WIDTH.W))))
    val cdma2buf_wr_data_with_en = Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_WR_PORT_NUMBER, UInt(conf.CBUF_WR_PORT_WIDTH;.W))))
    val bank_wr_addr_d1 = Wire(Vec(conf.CBUF_BANK_NUMBER, UInt(conf.CBUF_ADDR_WIDTH.W)))
    val bank_wr_data_d1 = Wire(Vec(conf.CBUF_BANK_NUMBER, UInt(conf.CBUF_WR_PORT_WIDTH.W)))   

    for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
        for(i <- 0 to conf.CBUF_BANK_NUMBER-1){
            cdma2buf_wr_addr_with_en(j)(i) := bank_wr_en_d1.asUInt(conf.CBUF_ADDR_WIDTH.W)&cdma2buf_wr_addr_d1(i)
            cdma2buf_wr_data_with_en(j)(i) := bank_wr_en_d1.asUInt(conf.CBUF_WR_PORT_WIDTH.W)&cdma2buf_wr_data_d1(i)
        }
        bank_wr_addr_d1(j) := cdma2buf_wr_addr_with_en(j).reduce(_ | _) 
        bank_wr_data_d1(j) := cdma2buf_wr_data_with_en(j).reduce(_ | _)        
    }

    //map bank to sram.
    //: for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    //:     for(my $k=0; $k<CBUF_RAM_PER_BANK ; $k++){
    //:         if((CBUF_BANK_RAM_CASE==0)||(CBUF_BANK_RAM_CASE==2)||(CBUF_BANK_RAM_CASE==4)){
    //:                print qq(
    //:        wire[CBUF_RAM_DEPTH_BITS-1:0]   bank${j}_ram${k}_wr_addr_d1 = bank${j}_wr_addr_d1[CBUF_RAM_DEPTH_BITS-1:0];
    //:        wire[CBUF_WR_PORT_WIDTH-1:0]    bank${j}_ram${k}_wr_data_d1 = bank${j}_wr_data_d1;
    //:        )
    //:         }
    //:         if((CBUF_BANK_RAM_CASE==1)||(CBUF_BANK_RAM_CASE==3)||(CBUF_BANK_RAM_CASE==5)){
    //:                print qq(
    //:        wire[CBUF_RAM_DEPTH_BITS-1:0]   bank${j}_ram${k}_wr_addr_d1 = bank${j}_wr_addr_d1[CBUF_RAM_DEPTH_BITS:1];
    //:        wire[CBUF_WR_PORT_WIDTH-1:0]    bank${j}_ram${k}_wr_data_d1 = bank${j}_wr_data_d1;
    //:        )
    //:         }
    //:     }
    //: }

    val bank_ram_wr_addr_d1 = Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, UInt(conf.CBUF_RAM_DEPTH_BITS.W))))
    val bank_ram_wr_data_d1 = Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, UInt(conf.CBUF_WR_PORT_WIDTH.W))))
    for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
        for(k <- 0 to conf.CBUF_RAM_PER_BANK-1){
            if((conf.CBUF_BANK_RAM_CASE==0)||(conf.CBUF_BANK_RAM_CASE==2)||(conf.CBUF_BANK_RAM_CASE==4)){
                bank_ram_wr_addr_d1(j)(k):= bank_wr_addr_d1(j)(conf.CBUF_RAM_DEPTH_BITS-1, 0)
                bank_ram_wr_data_d1(j)(k):= bank_wr_data_d1(j)
            }
            if((conf.CBUF_BANK_RAM_CASE==1)||(conf.CBUF_BANK_RAM_CASE==3)||(conf.CBUF_BANK_RAM_CASE==5)){
                bank_ram_wr_addr_d1(j)(k):= bank_wr_addr_d1(j)(conf.CBUF_RAM_DEPTH_BITS, 1)
                bank_ram_wr_data_d1(j)(k):= bank_wr_data_d1(j)
            }
        }       
    }

    // 1 pipe before write to sram, for timing
    //: my $kk=CBUF_RAM_DEPTH_BITS;
    //: my $jj=CBUF_WR_PORT_WIDTH;
    //: for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    //:     for(my $k=0; $k<CBUF_RAM_PER_BANK ; $k++){
    //:     &eperl::flop("-q bank${j}_ram${k}_wr_en_d2 -d bank${j}_ram${k}_wr_en_d1"); 
    //:     &eperl::flop("-wid ${kk} -q bank${j}_ram${k}_wr_addr_d2 -d bank${j}_ram${k}_wr_addr_d1");
    //:     &eperl::flop("-wid ${jj} -norst -q bank${j}_ram${k}_wr_data_d2 -d bank${j}_ram${k}_wr_data_d1");
    //:     }
    //: }

    val bank_ram_wr_addr_d2 = Reg(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, UInt(conf.CBUF_RAM_DEPTH_BITS.W))))
    val bank_ram_wr_data_d2 = Reg(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, UInt(conf.CBUF_WR_PORT_WIDTH.W))))
    val bank_ram_wr_en_d2 = Reg(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Bool())))
    for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
        for(k <- 0 to conf.CBUF_RAM_PER_BANK-1){
            withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){
                bank_ram_wr_en_d2(j)(k) := bank_ram_wr_en_d1(j)(k) 
                bank_ram_wr_addr_d2(j)(k) := bank_ram_wr_addr_d1(j)(k) 
            }
            withClock(io.nvdla_core_clk){
                bank_ram_wr_data_d2(j)(k) := bank_ram_wr_data_d1(j)(k) 
            }

        }       
    }

    //////////////////////step2: read data handle
    //decode read data address to sram.

    //: my $bank_slice= CBUF_BANK_SLICE;  #address part for select bank
    //: for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    //:     for(my $k=0; $k<CBUF_RAM_PER_BANK ; $k++){
    //:         my $kdiv2 = int($k/2);
    //:         my $kdiv4 = int($k/4);
    //:         if((CBUF_BANK_RAM_CASE==0)||(CBUF_BANK_RAM_CASE==2)||(CBUF_BANK_RAM_CASE==4)){
    //:         print qq(
    //:     wire  bank${j}_ram${k}_data_rd_en = sc2buf_dat_rd_en&&(sc2buf_dat_rd_addr[${bank_slice}]==${j}); );
    //:         }
    //:     for(my $i=0; $i<2; $i++){
    //:         if(CBUF_BANK_RAM_CASE==1){
    //:         print qq(
    //:     wire  bank${j}_ram${k}_data_rd${i}_en = sc2buf_dat_rd_en${i}&&(sc2buf_dat_rd_addr${i}[${bank_slice}]==${j})&&(sc2buf_dat_rd_addr${i}[0]==${k}); );
    //:             }
    //:         if(CBUF_BANK_RAM_CASE==3){
    //:         print qq(
    //:     wire  bank${j}_ram${k}_data_rd${i}_en = sc2buf_dat_rd_en${i}&&(sc2buf_dat_rd_addr${i}[${bank_slice}]==${j})&&(sc2buf_dat_rd_addr${i}[0]==${kdiv2}); );
    //:             }
    //:         if(CBUF_BANK_RAM_CASE==5){
    //:         print qq(
    //:     wire  bank${j}_ram${k}_data_rd${i}_en = sc2buf_dat_rd_en${i}&&(sc2buf_dat_rd_addr${i}[${bank_slice}]==${j})&&(sc2buf_dat_rd_addr${i}[0]==${kdiv4}); );
    //:             }
    //:         }
    //:     }
    //: }

    val bank_ram_data_rd_en_even_case = Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Bool())))
    val bank_ram_data_rd_en_odd_case = Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Vec(2, Bool()))))
    val sc2buf_dat_rd_en0 =  io.sc2buf_dat_rd_en
    val sc2buf_dat_rd_en1 =  io.sc2buf_dat_rd_en & io.sc2buf_dat_rd_next1_en
    val sc2buf_dat_rd_addr0 = io.sc2buf_dat_rd_addr
    val sc2buf_dat_rd_addr1 = io.sc2buf_dat_rd_next1_addr

    for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
        for(k <- 0 to conf.CBUF_RAM_PER_BANK-1){
            val kdiv2 = k/2
            val kdiv4 = k/4
            if((conf.CBUF_BANK_RAM_CASE==0)||(conf.CBUF_BANK_RAM_CASE==2)||(conf.CBUF_BANK_RAM_CASE==4)){
                bank_ram_data_rd_en_even_case(j)(k) := (io.sc2buf_dat_rd_en)&&(io.sc2buf_dat_rd_addr(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)=== j.U)
            }

            if(conf.CBUF_BANK_RAM_CASE==1){
                bank_ram_data_rd_en_odd_case(j)(k)(0) := (sc2buf_dat_rd_en0)&&(sc2buf_dat_rd_addr0(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)=== j.U)&&(sc2buf_dat_rd_addr0(0)=== k.U)
                bank_ram_data_rd_en_odd_case(j)(k)(1) := (sc2buf_dat_rd_en1)&&(sc2buf_dat_rd_addr1(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)=== j.U)&&(sc2buf_dat_rd_addr1(0)=== k.U)
            }
            if(conf.CBUF_BANK_RAM_CASE==3){
                bank_ram_data_rd_en_odd_case(j)(k)(0) := (sc2buf_dat_rd_en0)&&(sc2buf_dat_rd_addr0(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)=== j.U)&&(sc2buf_dat_rd_addr0(0)=== kdiv2.U)
                bank_ram_data_rd_en_odd_case(j)(k)(1) := (sc2buf_dat_rd_en1)&&(sc2buf_dat_rd_addr1(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)=== j.U)&&(sc2buf_dat_rd_addr1(0)=== kdiv2.U)
            }   
            if(conf.CBUF_BANK_RAM_CASE==5){
                bank_ram_data_rd_en_odd_case(j)(k)(0) := (sc2buf_dat_rd_en0)&&(sc2buf_dat_rd_addr0(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)=== j.U)&&(sc2buf_dat_rd_addr0(0)=== kdiv4.U)
                bank_ram_data_rd_en_odd_case(j)(k)(1) := (sc2buf_dat_rd_en1)&&(sc2buf_dat_rd_addr1(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)=== j.U)&&(sc2buf_dat_rd_addr1(0)=== kdiv4.U)
            }
        }       
    }

    //get sram data read address.
    //: for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    //:     for(my $k=0; $k<CBUF_RAM_PER_BANK ; $k++){
    //:         if((CBUF_BANK_RAM_CASE==0)||(CBUF_BANK_RAM_CASE==2)||(CBUF_BANK_RAM_CASE==4)){
    //:         print qq(
    //:     wire [CBUF_RAM_DEPTH_BITS-1:0] bank${j}_ram${k}_data_rd_addr = {CBUF_RAM_DEPTH_BITS{bank${j}_ram${k}_data_rd_en}}&(sc2buf_dat_rd_addr[CBUF_RAM_DEPTH_BITS-1:0]); );
    //:             }
    //:         for(my $i=0; $i<2; $i++){
    //:             if((CBUF_BANK_RAM_CASE==1)||(CBUF_BANK_RAM_CASE==3)||(CBUF_BANK_RAM_CASE==5)){
    //:         print qq(
    //:     wire [CBUF_RAM_DEPTH_BITS-1:0] bank${j}_ram${k}_data_rd${i}_addr = {CBUF_RAM_DEPTH_BITS{bank${j}_ram${k}_data_rd${i}_en}}&(sc2buf_dat_rd_addr${i}[CBUF_RAM_DEPTH_BITS:1]); );
    //:             }
    //:         }
    //:     }
    //: }

    val bank_ram_data_rd_addr_even_case = Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, UInt(conf.CBUF_RAM_DEPTH_BITS.W))))
    val bank_ram_data_rd_addr_odd_case = Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Vec(2, UInt(conf.CBUF_RAM_DEPTH_BITS.W)))))
 

    for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
        for(k <- 0 to conf.CBUF_RAM_PER_BANK-1){
            if((conf.CBUF_BANK_RAM_CASE==0)||(conf.CBUF_BANK_RAM_CASE==2)||(conf.CBUF_BANK_RAM_CASE==4)){
                bank_ram_data_rd_addr_even_case(j)(k) := (Fill(conf.CBUF_RAM_DEPTH_BITS, bank_ram_data_rd_en_even_case(j)(k)))&(io.sc2buf_dat_rd_addr(conf.CBUF_RAM_DEPTH_BITS-1, 0))
            }
            if((conf.CBUF_BANK_RAM_CASE==1)||(conf.CBUF_BANK_RAM_CASE==3)||(conf.CBUF_BANK_RAM_CASE==5)){
                bank_ram_data_rd_addr_odd_case(j)(k)(0) := (Fill(conf.CBUF_RAM_DEPTH_BITS, bank_ram_data_rd_en_odd_case(j)(k)(0))&(io.sc2buf_dat_rd_addr(conf.CBUF_RAM_DEPTH_BITS, 1))
                bank_ram_data_rd_addr_odd_case(j)(k)(1) := (Fill(conf.CBUF_RAM_DEPTH_BITS, bank_ram_data_rd_en_odd_case(j)(k)(1))&(io.sc2buf_dat_rd_addr(conf.CBUF_RAM_DEPTH_BITS, 1))
        }       
    }

    //add flop for sram data read en
    //: for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    //:     for(my $k=0; $k<CBUF_RAM_PER_BANK ; $k++){
    //:         if((CBUF_BANK_RAM_CASE==0)||(CBUF_BANK_RAM_CASE==2)||(CBUF_BANK_RAM_CASE==4)){
    //: &eperl::flop("-q bank${j}_ram${k}_data_rd_en_d1 -d  bank${j}_ram${k}_data_rd_en"); 
    //: &eperl::flop("-q bank${j}_ram${k}_data_rd_en_d2 -d  bank${j}_ram${k}_data_rd_en_d1"); 
    //:         }
    //:         for(my $i=0; $i<2; $i++){
    //:             if((CBUF_BANK_RAM_CASE==1)||(CBUF_BANK_RAM_CASE==3)||(CBUF_BANK_RAM_CASE==5)){
    //: &eperl::flop("-q bank${j}_ram${k}_data_rd${i}_en_d1 -d bank${j}_ram${k}_data_rd${i}_en"); 
    //: &eperl::flop("-q bank${j}_ram${k}_data_rd${i}_en_d2 -d bank${j}_ram${k}_data_rd${i}_en_d1"); 
    //:             }
    //:         }
    //:     }
    //: }

    val bank_ram_data_rd_en_even_case_d1 = Reg(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Bool())))
    val bank_ram_data_rd_en_even_case_d2 = Reg(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Bool())))
    val bank_ram_data_rd_en_odd_case_d1 = Reg(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Vec(2, Bool()))))
    val bank_ram_data_rd_en_odd_case_d2 = Reg(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Vec(2, Bool()))))

    for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
        for(k <- 0 to conf.CBUF_RAM_PER_BANK-1){
            if((conf.CBUF_BANK_RAM_CASE==0)||(conf.CBUF_BANK_RAM_CASE==2)||(conf.CBUF_BANK_RAM_CASE==4)){
                withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){
                    bank_ram_data_rd_en_even_case_d1(j)(k):= bank_ram_data_rd_en_even_case(j)(k)
                    bank_ram_data_rd_en_even_case_d2(j)(k):= bank_ram_data_rd_en_even_case_d1(j)(k)
                }
                
            }
            if((conf.CBUF_BANK_RAM_CASE==1)||(conf.CBUF_BANK_RAM_CASE==3)||(conf.CBUF_BANK_RAM_CASE==5)){
                withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){
                    bank_ram_data_rd_en_odd_case_d1(j)(k)(0):= bank_ram_data_rd_en_odd_case(j)(k)(0)
                    bank_ram_data_rd_en_odd_case_d1(j)(k)(1):= bank_ram_data_rd_en_odd_case(j)(k)(1)
                    bank_ram_data_rd_en_odd_case_d2(j)(k)(0):= bank_ram_data_rd_en_odd_case_d1(j)(k)(0)
                    bank_ram_data_rd_en_odd_case_d2(j)(k)(1):= bank_ram_data_rd_en_odd_case_d1(j)(k)(1)
                }
        }       
    }

    //get sram data read valid.
    //: for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    //:     for(my $k=0; $k<CBUF_RAM_PER_BANK ; $k++){
    //:         if((CBUF_BANK_RAM_CASE==0)||(CBUF_BANK_RAM_CASE==2)||(CBUF_BANK_RAM_CASE==4)){
    //:         print qq(
    //:     wire  bank${j}_ram${k}_data_rd_valid = bank${j}_ram${k}_data_rd_en_d2; )
    //:             }
    //:         for(my $i=0; $i<2; $i++){
    //:             if((CBUF_BANK_RAM_CASE==1)||(CBUF_BANK_RAM_CASE==3)||(CBUF_BANK_RAM_CASE==5)){
    //:         print qq(
    //:     wire  bank${j}_ram${k}_data_rd${i}_valid = bank${j}_ram${k}_data_rd${i}_en_d2; )
    //:             }
    //:         }
    //:     }
    //: }































}


  