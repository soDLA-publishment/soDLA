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
            bank_ram_wr_en_d0(j)(k) := bank_ram_wr_en_d0_t(j)(k).reduce(_ | _) //Bool OR-reduce p on all elts
            withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){
                bank_ram_wr_en_d1(j)(k) := bank_ram_wr_en_d0(j)(k) 
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
            bank_wr_en_d0(j)(i) := bank_ram_wr_en_d0_t(j)(i).reduce(_ | _) //Bool OR-reduce p on all elts
            withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){
                bank_wr_en_d1(j)(i) := bank_wr_en_d0(j)(i) 
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
    val cdma2buf_wr_data_with_en = Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_WR_PORT_NUMBER, UInt(conf.CBUF_WR_PORT_WIDTH.W))))
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
    if((conf.CBUF_BANK_RAM_CASE==0)||(conf.CBUF_BANK_RAM_CASE==2)||(conf.CBUF_BANK_RAM_CASE==4)){
        val bank_ram_data_rd_en_even_case = Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Bool())))
    }
    if((conf.CBUF_BANK_RAM_CASE==0)||(conf.CBUF_BANK_RAM_CASE==2)||(conf.CBUF_BANK_RAM_CASE==4)){
        val bank_ram_data_rd0_en_odd_case = Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Bool())))
        val bank_ram_data_rd1_en_odd_case = Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Bool())))
    }
    
    val sc2buf_dat_rd_en0 =  io.sc2buf_dat_rd_en
    val sc2buf_dat_rd_en1 =  io.sc2buf_dat_rd_en & io.sc2buf_dat_rd_next1_en
    val sc2buf_dat_rd_addr0 = io.sc2buf_dat_rd_addr
    val sc2buf_dat_rd_addr1 = io.sc2buf_dat_rd_next1_addr

    for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
        for(k <- 0 to conf.CBUF_RAM_PER_BANK-1){
            var kdiv2 = k/2
            var kdiv4 = k/4
            if((conf.CBUF_BANK_RAM_CASE==0)||(conf.CBUF_BANK_RAM_CASE==2)||(conf.CBUF_BANK_RAM_CASE==4)){
                bank_ram_data_rd_en_even_case(j)(k) := (io.sc2buf_dat_rd_en)&&(io.sc2buf_dat_rd_addr(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)=== j.U)
            }

            if(conf.CBUF_BANK_RAM_CASE==1){
                bank_ram_data_rd0_en_odd_case(j)(k) := (sc2buf_dat_rd_en0)&&(sc2buf_dat_rd_addr0(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)=== j.U)&&(sc2buf_dat_rd_addr0(0)=== k.U)
                bank_ram_data_rd1_en_odd_case(j)(k) := (sc2buf_dat_rd_en1)&&(sc2buf_dat_rd_addr1(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)=== j.U)&&(sc2buf_dat_rd_addr1(0)=== k.U)
            }
            if(conf.CBUF_BANK_RAM_CASE==3){
                bank_ram_data_rd0_en_odd_case(j)(k) := (sc2buf_dat_rd_en0)&&(sc2buf_dat_rd_addr0(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)=== j.U)&&(sc2buf_dat_rd_addr0(0)=== kdiv2.U)
                bank_ram_data_rd1_en_odd_case(j)(k) := (sc2buf_dat_rd_en1)&&(sc2buf_dat_rd_addr1(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)=== j.U)&&(sc2buf_dat_rd_addr1(0)=== kdiv2.U)
            }   
            if(conf.CBUF_BANK_RAM_CASE==5){
                bank_ram_data_rd0_en_odd_case(j)(k) := (sc2buf_dat_rd_en0)&&(sc2buf_dat_rd_addr0(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)=== j.U)&&(sc2buf_dat_rd_addr0(0)=== kdiv4.U)
                bank_ram_data_rd1_en_odd_case(j)(k) := (sc2buf_dat_rd_en1)&&(sc2buf_dat_rd_addr1(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)=== j.U)&&(sc2buf_dat_rd_addr1(0)=== kdiv4.U)
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
    if((conf.CBUF_BANK_RAM_CASE==0)||(conf.CBUF_BANK_RAM_CASE==2)||(conf.CBUF_BANK_RAM_CASE==4)){
        val bank_ram_data_rd_addr_even_case = Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, UInt(conf.CBUF_RAM_DEPTH_BITS.W))))
    }
    if((conf.CBUF_BANK_RAM_CASE==1)||(conf.CBUF_BANK_RAM_CASE==3)||(conf.CBUF_BANK_RAM_CASE==5)){
        val bank_ram_data_rd0_addr_odd_case = Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, UInt(conf.CBUF_RAM_DEPTH_BITS.W))))
        val bank_ram_data_rd1_addr_odd_case = Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, UInt(conf.CBUF_RAM_DEPTH_BITS.W))))
    }

    for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
        for(k <- 0 to conf.CBUF_RAM_PER_BANK-1){
            if((conf.CBUF_BANK_RAM_CASE==0)||(conf.CBUF_BANK_RAM_CASE==2)||(conf.CBUF_BANK_RAM_CASE==4)){
                bank_ram_data_rd_addr_even_case(j)(k) := Fill(conf.CBUF_RAM_DEPTH_BITS, bank_ram_data_rd_en_even_case(j)(k))&(io.sc2buf_dat_rd_addr(conf.CBUF_RAM_DEPTH_BITS-1, 0))
            }
            if((conf.CBUF_BANK_RAM_CASE==1)||(conf.CBUF_BANK_RAM_CASE==3)||(conf.CBUF_BANK_RAM_CASE==5)){
                bank_ram_data_rd0_addr_odd_case(j)(k) := Fill(conf.CBUF_RAM_DEPTH_BITS, bank_ram_data_rd0_en_odd_case(j)(k))&(io.sc2buf_dat_rd_addr(conf.CBUF_RAM_DEPTH_BITS, 1))
                bank_ram_data_rd1_addr_odd_case(j)(k) := Fill(conf.CBUF_RAM_DEPTH_BITS, bank_ram_data_rd1_en_odd_case(j)(k))&(io.sc2buf_dat_rd_addr(conf.CBUF_RAM_DEPTH_BITS, 1))
            }       
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

    if((conf.CBUF_BANK_RAM_CASE==0)||(conf.CBUF_BANK_RAM_CASE==2)||(conf.CBUF_BANK_RAM_CASE==4)){
        val bank_ram_data_rd_en_even_case_d1 = Reg(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Bool())))
        val bank_ram_data_rd_en_even_case_d2 = Reg(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Bool())))
    }
    if((conf.CBUF_BANK_RAM_CASE==1)||(conf.CBUF_BANK_RAM_CASE==3)||(conf.CBUF_BANK_RAM_CASE==5)){
        val bank_ram_data_rd0_en_odd_case_d1 = Reg(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Bool())))
        val bank_ram_data_rd1_en_odd_case_d1 = Reg(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Bool())))    
        val bank_ram_data_rd0_en_odd_case_d2 = Reg(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Bool())))
        val bank_ram_data_rd1_en_odd_case_d2 = Reg(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Bool())))        
    }

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
                    bank_ram_data_rd0_en_odd_case_d1(j)(k):= bank_ram_data_rd0_en_odd_case(j)(k)
                    bank_ram_data_rd1_en_odd_case_d1(j)(k):= bank_ram_data_rd1_en_odd_case(j)(k)
                    bank_ram_data_rd0_en_odd_case_d2(j)(k):= bank_ram_data_rd0_en_odd_case_d1(j)(k)
                    bank_ram_data_rd1_en_odd_case_d2(j)(k):= bank_ram_data_rd1_en_odd_case_d1(j)(k)
                }
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

    if((conf.CBUF_BANK_RAM_CASE==0)||(conf.CBUF_BANK_RAM_CASE==2)||(conf.CBUF_BANK_RAM_CASE==4)){
        val bank_ram_data_rd_valid_even_case = Reg(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Bool())))
    }
    if((conf.CBUF_BANK_RAM_CASE==1)||(conf.CBUF_BANK_RAM_CASE==3)||(conf.CBUF_BANK_RAM_CASE==5)){
        val bank_ram_data_rd0_valid_odd_case = Reg(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Bool())))
        val bank_ram_data_rd1_valid_odd_case = Reg(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Bool())))
    }

    for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
        for(k <- 0 to conf.CBUF_RAM_PER_BANK-1){
            if((conf.CBUF_BANK_RAM_CASE==0)||(conf.CBUF_BANK_RAM_CASE==2)||(conf.CBUF_BANK_RAM_CASE==4)){
                bank_ram_data_rd_valid_even_case(j)(k) := bank_ram_data_rd_en_even_case_d2(j)(k)
            }
            if((conf.CBUF_BANK_RAM_CASE==1)||(conf.CBUF_BANK_RAM_CASE==3)||(conf.CBUF_BANK_RAM_CASE==5)){
                bank_ram_data_rd0_valid_odd_case(j)(k) := bank_ram_data_rd0_en_odd_case_d2(j)(k)
                bank_ram_data_rd1_valid_odd_case(j)(k) := bank_ram_data_rd1_en_odd_case_d2(j)(k)
            }
        }       
    } 

    //get sc data read valid.
    //: my $t1="";
    //: my $t2="";
    //: if((CBUF_BANK_RAM_CASE==0)||(CBUF_BANK_RAM_CASE==2)||(CBUF_BANK_RAM_CASE==4)){
    //:     for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    //:         for(my $k=0; $k<CBUF_RAM_PER_BANK ; $k++){
    //:         $t1 .= "bank${j}_ram${k}_data_rd_valid|";
    //:         }
    //:     }
    //: print "wire [0:0] sc2buf_dat_rd_valid_w = $t1"."1'b0; \n";
    //: }
    //: if((CBUF_BANK_RAM_CASE==1)||(CBUF_BANK_RAM_CASE==3)||(CBUF_BANK_RAM_CASE==5)){
    //:     for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    //:         for(my $k=0; $k<CBUF_RAM_PER_BANK ; $k++){
    //:         $t1 .= "bank${j}_ram${k}_data_rd0_valid|"; 
    //:         $t2 .= "bank${j}_ram${k}_data_rd1_valid|"; 
    //:         }
    //:     }
    //: print "wire sc2buf_dat_rd_valid0 = ${t1}"."1'b0; \n";
    //: print "wire sc2buf_dat_rd_valid1 = ${t2}"."1'b0; \n";
    //: print "wire [0:0] sc2buf_dat_rd_valid_w = sc2buf_dat_rd_valid0 || sc2buf_dat_rd_valid1; \n";
    //: }
    //: &eperl::retime("-O sc2buf_dat_rd_valid -i sc2buf_dat_rd_valid_w -stage 4 -clk nvdla_core_clk");

    //: for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    //:     for(my $k=0; $k<CBUF_RAM_PER_BANK ; $k++){
    //: print qq(
    //: wire [CBUF_RAM_WIDTH-1:0] bank${j}_ram${k}_rd_data; );
    //:     }
    //: } 

    if((conf.CBUF_BANK_RAM_CASE==0)||(conf.CBUF_BANK_RAM_CASE==2)||(conf.CBUF_BANK_RAM_CASE==4)){
        val sc2buf_dat_rd_valid_w = bank_ram_data_rd_valid_even_case.map(_.reduce(_ | _)).reduce(_ | _)
    } 

    if((conf.CBUF_BANK_RAM_CASE==1)||(conf.CBUF_BANK_RAM_CASE==3)||(conf.CBUF_BANK_RAM_CASE==5)){
        val sc2buf_dat_rd_valid0 = bank_ram_data_rd0_valid_odd_case.map(_.reduce(_ | _)).reduce(_ | _)
        val sc2buf_dat_rd_valid1 = bank_ram_data_rd1_valid_odd_case.map(_.reduce(_ | _)).reduce(_ | _)
        val sc2buf_dat_rd_valid_w = sc2buf_dat_rd_valid0|sc2buf_dat_rd_valid1
    }  

    withClock(io.nvdla_core_clk){
        io.sc2buf_dat_rd_valid := ShiftRegister(sc2buf_dat_rd_valid_w, 4)
    }

    val bank_ram_rd_data = Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, UInt(conf.CBUF_RAM_WIDTH.W))))

    //get sc data read bank output data. 
    //: my $t1="";
    //: my $kk=CBUF_RD_PORT_WIDTH;
    //: if(CBUF_BANK_RAM_CASE==0){
    //: for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    //: print qq(
    //:  wire [${kk}-1:0] bank${j}_data_rd_data = bank${j}_ram0_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram0_data_rd_valid}}; );
    //:     }
    //: }
    //: if(CBUF_BANK_RAM_CASE==1){
    //: for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    //: print qq(
    //:  wire [${kk}-1:0] bank${j}_data_rd0_data = (bank${j}_ram1_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram1_data_rd0_valid}})|
    //:                                             (bank${j}_ram0_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram0_data_rd0_valid}});
    //:  wire [${kk}-1:0] bank${j}_data_rd1_data = (bank${j}_ram1_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram1_data_rd1_valid}})|
    //:                                             (bank${j}_ram0_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram0_data_rd1_valid}});
    //:     );
    //:     }
    //: }
    //: if(CBUF_BANK_RAM_CASE==2){
    //: for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    //: print qq(
    //:  wire [${kk}-1:0] bank${j}_data_rd_data = {bank${j}_ram1_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram1_data_rd_valid}},
    //:                                             bank${j}_ram0_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram0_data_rd_valid}}};
    //:     );
    //:     }
    //: }
    //: if(CBUF_BANK_RAM_CASE==3){
    //: for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    //: print qq(
    //:  wire [${kk}-1:0] bank${j}_data_rd0_data = {bank${j}_ram1_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram1_data_rd0_valid}},
    //:                                             bank${j}_ram0_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram0_data_rd0_valid}}}|
    //:                                             {bank${j}_ram3_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram3_data_rd0_valid}},
    //:                                             bank${j}_ram2_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram2_data_rd0_valid}}};
    //: wire [${kk}-1:0]  bank${j}_data_rd1_data = {bank${j}_ram1_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram1_data_rd1_valid}},
    //:                                             bank${j}_ram0_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram0_data_rd1_valid}}}|
    //:                                             {bank${j}_ram3_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram3_data_rd1_valid}},
    //:                                             bank${j}_ram2_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram2_data_rd1_valid}}};
    //:     );
    //:     }
    //: }
    //: if(CBUF_BANK_RAM_CASE==4){
    //: for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    //: print qq(
    //:  wire [${kk}-1:0] bank${j}_data_rd_data = {bank${j}_ram3_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram3_data_rd_valid}},
    //:                                             bank${j}_ram2_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram2_data_rd_valid}},
    //:                                             bank${j}_ram1_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram1_data_rd_valid}},
    //:                                             bank${j}_ram0_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram0_data_rd_valid}}};
    //:     );
    //:     }
    //: }
    //: if(CBUF_BANK_RAM_CASE==5){
    //: for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    //: print qq(
    //:  wire [${kk}-1:0] bank${j}_data_rd0_data = {
    //:                                             bank${j}_ram3_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram3_data_rd0_valid}},
    //:                                             bank${j}_ram2_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram2_data_rd0_valid}},
    //:                                             bank${j}_ram1_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram1_data_rd0_valid}},
    //:                                             bank${j}_ram0_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram0_data_rd0_valid}}}|
    //:                                             {bank${j}_ram7_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram7_data_rd0_valid}},
    //:                                             bank${j}_ram6_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram6_data_rd0_valid}},
    //:                                             bank${j}_ram5_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram5_data_rd0_valid}},
    //:                                             bank${j}_ram4_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram4_data_rd0_valid}}};
    //:  wire [${kk}-1:0] bank${j}_data_rd1_data = {
    //:                                             bank${j}_ram3_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram3_data_rd1_valid}},
    //:                                             bank${j}_ram2_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram2_data_rd1_valid}},
    //:                                             bank${j}_ram1_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram1_data_rd1_valid}},
    //:                                             bank${j}_ram0_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram0_data_rd1_valid}}}|
    //:                                             {bank${j}_ram7_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram7_data_rd1_valid}},
    //:                                             bank${j}_ram6_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram6_data_rd1_valid}},
    //:                                             bank${j}_ram5_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram5_data_rd1_valid}},
    //:                                             bank${j}_ram4_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram4_data_rd1_valid}}};
    //:     );
    //:     }
    //: }


    if(conf.CBUF_BANK_RAM_CASE==0){
        val bank_data_rd_data = Wire(Vec(conf.CBUF_BANK_NUMBER, UInt(conf.CBUF_RD_PORT_WIDTH.W)))
        for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
            bank_data_rd_data(j) := ((bank_ram_rd_data(j)(0))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd_valid_even_case(j)(0)))(conf.CBUF_RD_PORT_WIDTH-1,0)   
        }          
    }
    if(conf.CBUF_BANK_RAM_CASE==1){
        val bank_data_rd0_data = Wire(Vec(conf.CBUF_BANK_NUMBER, UInt(conf.CBUF_RD_PORT_WIDTH.W)))
        val bank_data_rd1_data = Wire(Vec(conf.CBUF_BANK_NUMBER, UInt(conf.CBUF_RD_PORT_WIDTH.W)))       
        for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
            bank_data_rd0_data(j) := (((bank_ram_rd_data(j)(0))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd0_valid_odd_case(j)(0)))|((bank_ram_rd_data(j)(1))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd0_valid_odd_case(j)(1))))(conf.CBUF_BANK_NUMBER-1,0)  
            bank_data_rd1_data(j) := (((bank_ram_rd_data(j)(0))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd1_valid_odd_case(j)(0)))|((bank_ram_rd_data(j)(1))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd1_valid_odd_case(j)(1))))(conf.CBUF_BANK_NUMBER-1,0)  
        }          
    }
    if(conf.CBUF_BANK_RAM_CASE==2){
        val bank_data_rd_data = Wire(Vec(conf.CBUF_BANK_NUMBER, UInt(conf.CBUF_RD_PORT_WIDTH.W)))
        for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
            bank_data_rd_data(j) := Cat((bank_ram_rd_data(j)(0))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd_valid_even_case(j)(0)), (bank_ram_rd_data(j)(1))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd_valid_even_case(j)(1)))(conf.CBUF_RD_PORT_WIDTH-1,0)   
        }          
    }
    if(conf.CBUF_BANK_RAM_CASE==3){
        val bank_data_rd0_data = Wire(Vec(conf.CBUF_BANK_NUMBER, UInt(conf.CBUF_RD_PORT_WIDTH.W)))
        val bank_data_rd1_data = Wire(Vec(conf.CBUF_BANK_NUMBER, UInt(conf.CBUF_RD_PORT_WIDTH.W)))       
        for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
            bank_data_rd0_data(j) := (Cat(((bank_ram_rd_data(j)(1))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd0_valid_odd_case(j)(1))), ((bank_ram_rd_data(j)(0))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd0_valid_odd_case(j)(0))))|Cat(((bank_ram_rd_data(j)(3))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd0_valid_odd_case(j)(3))), ((bank_ram_rd_data(j)(2))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd0_valid_odd_case(j)(2)))))(conf.CBUF_RD_PORT_WIDTH-1,0)   
            bank_data_rd1_data(j) := (Cat(((bank_ram_rd_data(j)(1))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd1_valid_odd_case(j)(1))), ((bank_ram_rd_data(j)(0))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd1_valid_odd_case(j)(0))))|Cat(((bank_ram_rd_data(j)(3))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd1_valid_odd_case(j)(3))), ((bank_ram_rd_data(j)(2))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd1_valid_odd_case(j)(2)))))(conf.CBUF_RD_PORT_WIDTH-1,0)   
        }          
    }
    if(conf.CBUF_BANK_RAM_CASE==4){
        val bank_data_rd_data = Wire(Vec(conf.CBUF_BANK_NUMBER, UInt(conf.CBUF_RD_PORT_WIDTH.W)))
        for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
            bank_data_rd_data(j) := Cat((bank_ram_rd_data(j)(3))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd_valid_even_case(j)(3)), (bank_ram_rd_data(j)(2))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd_valid_even_case(j)(2)), (bank_ram_rd_data(j)(1))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd_valid_even_case(j)(1)),(bank_ram_rd_data(j)(0))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd_valid_even_case(j)(0)))(conf.CBUF_RD_PORT_WIDTH-1,0)   
        }          
    }
    if(conf.CBUF_BANK_RAM_CASE==5){
        val bank_data_rd0_data = Wire(Vec(conf.CBUF_BANK_NUMBER, UInt(conf.CBUF_RD_PORT_WIDTH.W)))
        val bank_data_rd1_data = Wire(Vec(conf.CBUF_BANK_NUMBER, UInt(conf.CBUF_RD_PORT_WIDTH.W)))       
        for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
            bank_data_rd0_data(j) := (Cat(((bank_ram_rd_data(j)(7))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd0_valid_odd_case(j)(7))), ((bank_ram_rd_data(j)(6))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd0_valid_odd_case(j)(6))), ((bank_ram_rd_data(j)(5))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd0_valid_odd_case(j)(5))), ((bank_ram_rd_data(j)(4))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd0_valid_odd_case(j)(4))))|Cat(((bank_ram_rd_data(j)(3))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd0_valid_odd_case(j)(3))), ((bank_ram_rd_data(j)(2))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd0_valid_odd_case(j)(2))), ((bank_ram_rd_data(j)(1))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd0_valid_odd_case(j)(1))), ((bank_ram_rd_data(j)(0))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd0_valid_odd_case(j)(0)))))(conf.CBUF_RD_PORT_WIDTH-1,0)   
            bank_data_rd1_data(j) := (Cat(((bank_ram_rd_data(j)(7))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd1_valid_odd_case(j)(7))), ((bank_ram_rd_data(j)(6))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd1_valid_odd_case(j)(6))), ((bank_ram_rd_data(j)(5))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd1_valid_odd_case(j)(5))), ((bank_ram_rd_data(j)(4))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd1_valid_odd_case(j)(4))))|Cat(((bank_ram_rd_data(j)(3))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd1_valid_odd_case(j)(3))), ((bank_ram_rd_data(j)(2))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd1_valid_odd_case(j)(2))), ((bank_ram_rd_data(j)(1))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd1_valid_odd_case(j)(1))), ((bank_ram_rd_data(j)(0))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd1_valid_odd_case(j)(0)))))(conf.CBUF_RD_PORT_WIDTH-1,0)    
        }          
    }

    //: my $kk=CBUF_RD_DATA_SHIFT_WIDTH;
    //: &eperl::retime("-O sc2buf_dat_rd_shift_5T -i sc2buf_dat_rd_shift -wid ${kk} -stage 5 -clk nvdla_core_clk");
    withClock(io.nvdla_core_clk){
        val sc2buf_dat_rd_shift_5T = ShiftRegister(io.sc2buf_dat_rd_shift, 5)
    }

    // pipe solution. for timing concern, 4 level pipe. 
    //: my $kk=CBUF_RD_PORT_WIDTH;
    //: if((CBUF_BANK_RAM_CASE==0)||(CBUF_BANK_RAM_CASE==2)||(CBUF_BANK_RAM_CASE==4)){
    //: for (my $i=0; $i<CBUF_BANK_NUMBER; $i++){
    //: &eperl::flop("-wid ${kk} -norst -q l1group${i}_data_rd_data   -d bank${i}_data_rd_data");
    //: }
    //: 
    //: for (my $i=0; $i<CBUF_BANK_NUMBER/4; $i++){
    //: my $ni=$i*4;
    //: my $nii=$i*4+1;
    //: my $niii=$i*4+2;
    //: my $niiii=$i*4+3;
    //: print qq(
    //: wire [${kk}-1:0] l2group${i}_data_rd_data_w = l1group${ni}_data_rd_data | l1group${nii}_data_rd_data | l1group${niii}_data_rd_data | l1group${niiii}_data_rd_data;
    //: );
    //: &eperl::flop("-wid ${kk} -norst -q l2group${i}_data_rd_data   -d l2group${i}_data_rd_data_w");
    //: }
    //:
    //: for (my $i=0; $i<CBUF_BANK_NUMBER/16; $i++){
    //: my $ni=$i*4;
    //: my $nii=$i*4+1;
    //: my $niii=$i*4+2;
    //: my $niiii=$i*4+3;
    //: print qq(
    //: wire [${kk}-1:0] l3group${i}_data_rd_data_w = l2group${ni}_data_rd_data | l2group${nii}_data_rd_data | l2group${niii}_data_rd_data | l2group${niiii}_data_rd_data;
    //: );
    //: &eperl::flop("-wid ${kk} -norst -q l3group${i}_data_rd_data   -d l3group${i}_data_rd_data_w");
    //: }
    //: 
    //: if(CBUF_BANK_NUMBER==16){
    //: &eperl::flop("-wid ${kk} -norst -q l4group_data_rd_data   -d l3group0_data_rd_data"); 
    //: }
    //: if(CBUF_BANK_NUMBER==32) {
    //: print qq(
    //: wire [${kk}-1:0] l4group_data_rd_data_w = l3group0_data_rd_data | l3group1_data_rd_data;
    //: );
    //: &eperl::flop("-wid ${kk} -norst -q l4group_data_rd_data   -d l4group_data_rd_data_w");
    //: }
    //: print "wire[${kk}-1:0] sc2buf_dat_rd_data = l4group_data_rd_data[${kk}-1:0]; \n";
    //: }

    if((conf.CBUF_BANK_RAM_CASE==0)||(conf.CBUF_BANK_RAM_CASE==2)||(conf.CBUF_BANK_RAM_CASE==4)){
        val l1group_data_rd_data = Reg(Vec(conf.CBUF_BANK_NUMBER, UInt(conf.CBUF_RD_PORT_WIDTH.W)))
        for(i <- 0 to conf.CBUF_BANK_NUMBER-1){
            withClock(io.nvdla_core_clk){
                l1group_data_rd_data(i) := bank_data_rd_data(i)
            }
        }
        val l2group_data_rd_data = Reg(Vec(conf.CBUF_BANK_NUMBER/4, UInt(conf.CBUF_RD_PORT_WIDTH.W)))
        val l2group_data_rd_data_w = Wire(Vec(conf.CBUF_BANK_NUMBER/4, UInt(conf.CBUF_RD_PORT_WIDTH.W)))        
        for(i <- 0 to conf.CBUF_BANK_NUMBER/4-1){
            l2group_data_rd_data_w(i) := l1group_data_rd_data(i*4)|l1group_data_rd_data(i*4+1)|l1group_data_rd_data(i*4+2)|l1group_data_rd_data(i*4+3)
            withClock(io.nvdla_core_clk){
                l2group_data_rd_data(i) := l2group_data_rd_data_w(i)
            }            
        }
        val l3group_data_rd_data = Reg(Vec(conf.CBUF_BANK_NUMBER/16, UInt(conf.CBUF_RD_PORT_WIDTH.W)))
        val l3group_data_rd_data_w = Wire(Vec(conf.CBUF_BANK_NUMBER/16, UInt(conf.CBUF_RD_PORT_WIDTH.W)))                
        for(i <- 0 to conf.CBUF_BANK_NUMBER/16-1){
            l3group_data_rd_data_w(i) := l2group_data_rd_data(i*4)|l2group_data_rd_data(i*4+1)|l2group_data_rd_data(i*4+2)|l2group_data_rd_data(i*4+3)
            withClock(io.nvdla_core_clk){
                l3group_data_rd_data(i) := l3group_data_rd_data_w(i)
            }            
        }
        if(conf.CBUF_BANK_NUMBER==16){
            val l4group_data_rd_data = Reg(UInt(conf.CBUF_RD_PORT_WIDTH.W))
             withClock(io.nvdla_core_clk){
                l4group_data_rd_data := l3group_data_rd_data(0)
            }            
        } 
        if(conf.CBUF_BANK_NUMBER==32){
            val l4group_data_rd_data_w = l3group_data_rd_data(0)|l3group_data_rd_data(1)
            val l4group_data_rd_data = Reg(UInt(conf.CBUF_RD_PORT_WIDTH.W))
             withClock(io.nvdla_core_clk){
                l4group_data_rd_data := l4group_data_rd_data_w
            }            
        }
        io.sc2buf_dat_rd_data := l4group_data_rd_data
    }
    //: my $kk=CBUF_RD_PORT_WIDTH;
    //: if((CBUF_BANK_RAM_CASE==1)||(CBUF_BANK_RAM_CASE==3)||(CBUF_BANK_RAM_CASE==5)){
    //: for (my $i=0; $i<CBUF_BANK_NUMBER; $i++){
    //: &eperl::flop("-wid ${kk} -norst -q l1group${i}_data_rd0_data   -d bank${i}_data_rd0_data");
    //: &eperl::flop("-wid ${kk} -norst -q l1group${i}_data_rd1_data   -d bank${i}_data_rd1_data");
    //: }
    //: 
    //: for (my $i=0; $i<CBUF_BANK_NUMBER/4; $i++){
    //: my $ni=$i*4;
    //: my $nii=$i*4+1;
    //: my $niii=$i*4+2;
    //: my $niiii=$i*4+3;
    //: print qq(
    //: wire [${kk}-1:0] l2group${i}_data_rd0_data_w = l1group${ni}_data_rd0_data | l1group${nii}_data_rd0_data | l1group${niii}_data_rd0_data | l1group${niiii}_data_rd0_data;
    //: wire [${kk}-1:0] l2group${i}_data_rd1_data_w = l1group${ni}_data_rd1_data | l1group${nii}_data_rd1_data | l1group${niii}_data_rd1_data | l1group${niiii}_data_rd1_data;
    //: );
    //: &eperl::flop("-wid ${kk} -norst -q l2group${i}_data_rd0_data   -d l2group${i}_data_rd0_data_w");
    //: &eperl::flop("-wid ${kk} -norst -q l2group${i}_data_rd1_data   -d l2group${i}_data_rd1_data_w");
    //: }
    //:
    //: for (my $i=0; $i<CBUF_BANK_NUMBER/16; $i++){
    //: my $ni=$i*4;
    //: my $nii=$i*4+1;
    //: my $niii=$i*4+2;
    //: my $niiii=$i*4+3;
    //: print qq(
    //: wire [${kk}-1:0] l3group${i}_data_rd0_data_w = l2group${ni}_data_rd0_data | l2group${nii}_data_rd0_data | l2group${niii}_data_rd0_data | l2group${niiii}_data_rd0_data;
    //: wire [${kk}-1:0] l3group${i}_data_rd1_data_w = l2group${ni}_data_rd1_data | l2group${nii}_data_rd1_data | l2group${niii}_data_rd1_data | l2group${niiii}_data_rd1_data;
    //: );
    //: &eperl::flop("-wid ${kk} -norst -q l3group${i}_data_rd0_data   -d l3group${i}_data_rd0_data_w");
    //: &eperl::flop("-wid ${kk} -norst -q l3group${i}_data_rd1_data   -d l3group${i}_data_rd1_data_w");
    //: }
    //: 
    //: if(CBUF_BANK_NUMBER==16){
    //: print qq(
    //: wire [${kk}-1:0] l4group_data_rd0_data = l3group0_data_rd0_data;
    //: wire [${kk}-1:0] l4group_data_rd1_data = l3group0_data_rd1_data;
    //: );
    //: }
    //: if(CBUF_BANK_NUMBER==32) {
    //: print qq(
    //: wire [${kk}-1:0] l4group_data_rd0_data = l3group0_data_rd0_data | l3group1_data_rd0_data;
    //: wire [${kk}-1:0] l4group_data_rd1_data = l3group0_data_rd1_data | l3group1_data_rd1_data;
    //: );
    //: }
    //: print qq(
    //: wire [${kk}*2-1:0] l4group_data_rd_data_w = {l4group_data_rd1_data,l4group_data_rd0_data}>>{sc2buf_dat_rd_shift_5T,3'b0};
    //: );
    //: &eperl::flop("-wid ${kk} -norst -q l4group_data_rd_data   -d l4group_data_rd_data_w[${kk}-1:0]");
    //: print "wire[${kk}-1:0] sc2buf_dat_rd_data = l4group_data_rd_data[${kk}-1:0]; \n";
    //: }
    if((conf.CBUF_BANK_RAM_CASE==1)||(conf.CBUF_BANK_RAM_CASE==3)||(conf.CBUF_BANK_RAM_CASE==5)){
        val l1group_data_rd0_data = Reg(Vec(conf.CBUF_BANK_NUMBER, UInt(conf.CBUF_RD_PORT_WIDTH.W)))
        val l1group_data_rd1_data = Reg(Vec(conf.CBUF_BANK_NUMBER, UInt(conf.CBUF_RD_PORT_WIDTH.W)))
        for(i <- 0 to conf.CBUF_BANK_NUMBER-1){
            withClock(io.nvdla_core_clk){
                l1group_data_rd0_data(i) := bank_data_rd0_data(i)
                l1group_data_rd1_data(i) := bank_data_rd1_data(i)                
            }
        }
        val l2group_data_rd0_data = Reg(Vec(conf.CBUF_BANK_NUMBER/4, UInt(conf.CBUF_RD_PORT_WIDTH.W)))
        val l2group_data_rd1_data = Reg(Vec(conf.CBUF_BANK_NUMBER/4, UInt(conf.CBUF_RD_PORT_WIDTH.W)))       
        val l2group_data_rd0_data_w = Wire(Vec(conf.CBUF_BANK_NUMBER/4, UInt(conf.CBUF_RD_PORT_WIDTH.W)))
        val l2group_data_rd1_data_w = Wire(Vec(conf.CBUF_BANK_NUMBER/4, UInt(conf.CBUF_RD_PORT_WIDTH.W)))                
        for(i <- 0 to conf.CBUF_BANK_NUMBER/4-1){
            l2group_data_rd0_data_w(i) := l1group_data_rd0_data(i*4)|l1group_data_rd0_data(i*4+1)|l1group_data_rd0_data(i*4+2)|l1group_data_rd0_data(i*4+3)
            l2group_data_rd1_data_w(i) := l1group_data_rd1_data(i*4)|l1group_data_rd1_data(i*4+1)|l1group_data_rd1_data(i*4+2)|l1group_data_rd1_data(i*4+3)
            withClock(io.nvdla_core_clk){
                l2group_data_rd0_data(i) := l2group_data_rd0_data_w(i)
                l2group_data_rd1_data(i) := l2group_data_rd1_data_w(i)
            }            
        }
        val l3group_data_rd0_data = Reg(Vec(conf.CBUF_BANK_NUMBER/16, UInt(conf.CBUF_RD_PORT_WIDTH.W)))
        val l3group_data_rd1_data = Reg(Vec(conf.CBUF_BANK_NUMBER/16, UInt(conf.CBUF_RD_PORT_WIDTH.W)))        
        val l3group_data_rd0_data_w = Wire(Vec(conf.CBUF_BANK_NUMBER/16, UInt(conf.CBUF_RD_PORT_WIDTH.W)))
        val l3group_data_rd1_data_w = Wire(Vec(conf.CBUF_BANK_NUMBER/16, UInt(conf.CBUF_RD_PORT_WIDTH.W)))                        
        for(i <- 0 to conf.CBUF_BANK_NUMBER/16-1){
            l3group_data_rd0_data_w(i) := l2group_data_rd0_data(i*4)|l2group_data_rd0_data(i*4+1)|l2group_data_rd0_data(i*4+2)|l2group_data_rd0_data(i*4+3)
            l3group_data_rd1_data_w(i) := l2group_data_rd1_data(i*4)|l2group_data_rd1_data(i*4+1)|l2group_data_rd1_data(i*4+2)|l2group_data_rd1_data(i*4+3)        
            withClock(io.nvdla_core_clk){
                l3group_data_rd0_data(i) := l3group_data_rd0_data_w(i)
                l3group_data_rd1_data(i) := l3group_data_rd1_data_w(i)
            }            
        }
        if(conf.CBUF_BANK_NUMBER==16){
            val l4group_data_rd0_data = Reg(UInt(conf.CBUF_RD_PORT_WIDTH.W)) 
            val l4group_data_rd1_data = Reg(UInt(conf.CBUF_RD_PORT_WIDTH.W))                       
             withClock(io.nvdla_core_clk){
                l4group_data_rd0_data := l3group_data_rd0_data(0)
                l4group_data_rd1_data := l3group_data_rd1_data(0)
            }            
        } 
        if(conf.CBUF_BANK_NUMBER==32){
            val l4group_data_rd0_data = l3group_data_rd0_data(0)|l3group_data_rd1_data(1)
            val l4group_data_rd1_data = l3group_data_rd0_data(0)|l3group_data_rd1_data(1)         
        }
        val l4group_data_rd_data = Reg(UInt(conf.CBUF_RD_PORT_WIDTH.W))
        val l4group_data_rd_data_w = Cat(l4group_data_rd1_data, l4group_data_rd0_data) >> Cat(sc2buf_dat_rd_shift_5T, "b0".UInt(3.W))
        withClock(io.nvdla_core_clk){
            l4group_data_rd_data:=l4group_data_rd_data_w
        }  
        io.sc2buf_dat_rd_data := l4group_data_rd_data
    }

    ////get sc data read data. no pipe
    ////: my $t1="";
    ////: my $t2="";
    ////: my $kk=CBUF_RD_PORT_WIDTH;
    ////: if((CBUF_BANK_RAM_CASE==0)||(CBUF_BANK_RAM_CASE==2)||(CBUF_BANK_RAM_CASE==4)){
    ////:     for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    ////:         $t1 .= "bank${j}_data_rd_data|";    
    ////:     }
    ////: print "wire[${kk}-1:0] sc2buf_dat_rd_data =".${t1}."{${kk}{1'b0}}; \n";
    ////: }
    ////:     
    ////: if((CBUF_BANK_RAM_CASE==1)|(CBUF_BANK_RAM_CASE==3)||(CBUF_BANK_RAM_CASE==5)){
    ////:     for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    ////:         $t1 .= "bank${j}_data_rd0_data|";    
    ////:         $t2 .= "bank${j}_data_rd1_data|";    
    ////:     }
    ////: print "wire[${kk}-1:0] sc2buf_dat_rd_data0 =".${t1}."{${kk}{1'b0}}; \n";
    ////: print "wire[${kk}-1:0] sc2buf_dat_rd_data1 =".${t2}."{${kk}{1'b0}}; \n";
    ////: }
    ////:
    //wire[CBUF_RD_PORT_WIDTH*2-1:0] sc2buf_dat_rd_data_temp = {sc2buf_dat_rd_data1,sc2buf_dat_rd_data0} >> {sc2buf_dat_rd_shift_5T,3'b0};
    //wire[CBUF_RD_PORT_WIDTH-1:0] sc2buf_dat_rd_data = sc2buf_dat_rd_data_temp[CBUF_RD_PORT_WIDTH-1:0];

    //if((conf.CBUF_BANK_RAM_CASE==0)||(conf.CBUF_BANK_RAM_CASE==2)||(conf.CBUF_BANK_RAM_CASE==4)){
    //    io.sc2buf_dat_rd_data:= bank_data_rd_data.reduce(_ | _) 
    //}
    //if((conf.CBUF_BANK_RAM_CASE==1)||(conf.CBUF_BANK_RAM_CASE==3)||(conf.CBUF_BANK_RAM_CASE==5)){
    //    val sc2buf_dat_rd_data0:= bank_data_rd0_data.reduce(_ | _) 
    //    val sc2buf_dat_rd_data1:= bank_data_rd0_data.reduce(_ | _)     
    //}
    //val sc2buf_dat_rd_data_temp = Cat(sc2buf_dat_rd_data1, sc2buf_dat_rd_data0) >> Cat(sc2buf_dat_rd_shift_5T, "b0".UInt(3.W))
    //io.sc2buf_dat_rd_data := sc2buf_dat_rd_data_temp(conf.CBUF_RD_PORT_WIDTH-1, 0)

    /////////////////////step3: read weight handle
    //decode read weight address to sram.
    //: my $bank_slice= CBUF_BANK_SLICE;  #address part for select bank
    //: for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    //:     for(my $k=0; $k<CBUF_RAM_PER_BANK ; $k++){
    //:         my $kdiv2 = int($k/2);
    //:         my $kdiv4 = int($k/4);
    //:         if((CBUF_BANK_RAM_CASE==0)||(CBUF_BANK_RAM_CASE==2)||(CBUF_BANK_RAM_CASE==4)){
    //:         print qq(
    //:     wire  bank${j}_ram${k}_wt_rd_en = sc2buf_wt_rd_en&&(sc2buf_wt_rd_addr[${bank_slice}]==${j}); )
    //:         }
    //:         if(CBUF_BANK_RAM_CASE==1){
    //:         print qq(
    //:     wire  bank${j}_ram${k}_wt_rd_en = sc2buf_wt_rd_en&&(sc2buf_wt_rd_addr[${bank_slice}]==${j})&&(sc2buf_wt_rd_addr[0]==${k}); )
    //:         }
    //:         if(CBUF_BANK_RAM_CASE==3){
    //:         print qq(
    //:     wire  bank${j}_ram${k}_wt_rd_en = sc2buf_wt_rd_en&&(sc2buf_wt_rd_addr[${bank_slice}]==${j})&&(sc2buf_wt_rd_addr[0]==${kdiv2}); )
    //:         }
    //:         if(CBUF_BANK_RAM_CASE==5){
    //:         print qq(
    //:     wire  bank${j}_ram${k}_wt_rd_en = sc2buf_wt_rd_en&&(sc2buf_wt_rd_addr[${bank_slice}]==${j})&&(sc2buf_wt_rd_addr[0]==${kdiv4}); )
    //:         }
    //:     }
    //: }

    val bank_ram_wt_rd_en = Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Bool())))

    for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
        for(k <- 0 to conf.CBUF_RAM_PER_BANK-1){
            kdiv2 = k/2
            kdiv4 = k/4
            if((conf.CBUF_BANK_RAM_CASE==0)||(conf.CBUF_BANK_RAM_CASE==2)||(conf.CBUF_BANK_RAM_CASE==4)){
                bank_ram_wt_rd_en(j)(k) := (io.sc2buf_wt_rd_en)&&(io.sc2buf_wt_rd_addr(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)=== j.U)
            }
            if(conf.CBUF_BANK_RAM_CASE==1){
                bank_ram_wt_rd_en(j)(k) := (io.sc2buf_wt_rd_en)&&(io.sc2buf_wt_rd_addr(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)=== j.U)&&(io.sc2buf_wt_rd_addr(0)=== k.U)
            }
            if(conf.CBUF_BANK_RAM_CASE==3){
                bank_ram_wt_rd_en(j)(k) := (io.sc2buf_wt_rd_en)&&(io.sc2buf_wt_rd_addr(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)=== j.U)&&(io.sc2buf_wt_rd_addr(0)=== kdiv2.U)
            }   
            if(conf.CBUF_BANK_RAM_CASE==5){
                bank_ram_wt_rd_en(j)(k) := (io.sc2buf_wt_rd_en)&&(io.sc2buf_wt_rd_addr(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)=== j.U)&&(io.sc2buf_wt_rd_addr(0)=== kdiv4.U)
            }
        }       
    } 

    //get sram weight read address.
    //: for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    //:     for(my $k=0; $k<CBUF_RAM_PER_BANK ; $k++){
    //:         if((CBUF_BANK_RAM_CASE==0)||(CBUF_BANK_RAM_CASE==2)||(CBUF_BANK_RAM_CASE==4)){
    //:         print qq(
    //:     wire [CBUF_RAM_DEPTH_BITS-1:0] bank${j}_ram${k}_wt_rd_addr = {CBUF_RAM_DEPTH_BITS{bank${j}_ram${k}_wt_rd_en}}&(sc2buf_wt_rd_addr[CBUF_RAM_DEPTH_BITS-1:0]); )
    //:             }
    //:         if((CBUF_BANK_RAM_CASE==1)||(CBUF_BANK_RAM_CASE==3)||(CBUF_BANK_RAM_CASE==5)){
    //:         print qq(
    //:     wire [CBUF_RAM_DEPTH_BITS-1:0] bank${j}_ram${k}_wt_rd_addr = {CBUF_RAM_DEPTH_BITS{bank${j}_ram${k}_wt_rd_en}}&(sc2buf_wt_rd_addr[CBUF_RAM_DEPTH_BITS:1]); )
    //:         }
    //:     }
    //: }

    val bank_ram_wt_rd_addr = Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, UInt(conf.CBUF_RAM_DEPTH_BITS.W))))

    for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
        for(k <- 0 to conf.CBUF_RAM_PER_BANK-1){
            if((conf.CBUF_BANK_RAM_CASE==0)||(conf.CBUF_BANK_RAM_CASE==2)||(conf.CBUF_BANK_RAM_CASE==4)){
                bank_ram_wt_rd_addr(j)(k) := Fill(conf.CBUF_RAM_DEPTH_BITS, bank_ram_wt_rd_en(j)(k))&(io.sc2buf_wt_rd_addr(conf.CBUF_RAM_DEPTH_BITS-1, 0))
            }
            if((conf.CBUF_BANK_RAM_CASE==1)||(conf.CBUF_BANK_RAM_CASE==3)||(conf.CBUF_BANK_RAM_CASE==5)){
                bank_ram_wt_rd_addr(j)(k) := Fill(conf.CBUF_RAM_DEPTH_BITS, bank_ram_wt_rd_en(j)(k))&(io.sc2buf_wt_rd_addr(conf.CBUF_RAM_DEPTH_BITS, 1))
            }
        }       
    }

    //add flop for sram weight read en
    //: for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    //:     for(my $k=0; $k<CBUF_RAM_PER_BANK ; $k++){
    //: &eperl::flop("-q bank${j}_ram${k}_wt_rd_en_d1 -d  bank${j}_ram${k}_wt_rd_en"); 
    //: &eperl::flop("-q bank${j}_ram${k}_wt_rd_en_d2 -d  bank${j}_ram${k}_wt_rd_en_d1"); 
    //:         }
    //: }

    val bank_ram_wt_rd_en_d1 = Reg(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Bool())))
    val bank_ram_wt_rd_en_d2 = Reg(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Bool()))) 
    
    for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
        for(k <- 0 to conf.CBUF_RAM_PER_BANK-1){
            withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){
                bank_ram_wt_rd_en_d1(j)(k):= bank_ram_wt_rd_en(j)(k)
                bank_ram_wt_rd_en_d2(j)(k):= bank_ram_wt_rd_en_d1(j)(k)
            }               
        }       
    }

    //get sram weight read valid.
    //: for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    //:     for(my $k=0; $k<CBUF_RAM_PER_BANK ; $k++){
    //:     print qq(
    //:     wire  bank${j}_ram${k}_wt_rd_valid = bank${j}_ram${k}_wt_rd_en_d2; )
    //:     }
    //: }

    val bank_ram_wt_rd_valid = bank_ram_wt_rd_en_d2

    //get sc weight read valid.
    //: my $t1="";
    //: for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    //:     for(my $k=0; $k<CBUF_RAM_PER_BANK ; $k++){
    //:     $t1 .= "bank${j}_ram${k}_wt_rd_valid|";
    //:     }
    //: }
    //: print "wire [0:0] sc2buf_wt_rd_valid_w ="."${t1}"."1'b0 ;\n";
    //: &eperl::retime("-O sc2buf_wt_rd_valid -i sc2buf_wt_rd_valid_w -stage 4 -clk nvdla_core_clk");

    val sc2buf_wt_rd_valid_w = bank_ram_wt_rd_valid.map(_.reduce(_ | _)).reduce(_ | _)
    withClock(io.nvdla_core_clk){
        io.sc2buf_wt_rd_valid := ShiftRegister(sc2buf_dat_wt_valid_w, 4)
    }

    //get sc weight read bank output data. 
    //: my $t1="";
    //: my $kk=CBUF_RD_PORT_WIDTH;
    //: if(CBUF_BANK_RAM_CASE==0){
    //: for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    //: print qq(
    //: wire [${kk}-1:0] bank${j}_wt_rd_data = bank${j}_ram0_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram0_wt_rd_valid}}; );
    //:     }
    //: }
    //: if(CBUF_BANK_RAM_CASE==1){
    //: for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    //: print qq(
    //: wire [${kk}-1:0]  bank${j}_wt_rd_data = (bank${j}_ram1_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram1_wt_rd_valid}})|
    //:                                                         (bank${j}_ram0_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram0_wt_rd_valid}});
    //:     );
    //:     }
    //: }
    //: if(CBUF_BANK_RAM_CASE==2){
    //: for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    //: print qq(
    //: wire [${kk}-1:0]  bank${j}_wt_rd_data = {bank${j}_ram1_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram1_wt_rd_valid}},
    //:                                                         bank${j}_ram0_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram0_wt_rd_valid}}}; );
    //:     }
    //: }
    //: if(CBUF_BANK_RAM_CASE==3){
    //: for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    //: print qq(
    //: wire [${kk}-1:0]  bank${j}_wt_rd_data = {bank${j}_ram1_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram1_wt_rd_valid}},
    //:                                                         bank${j}_ram0_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram0_wt_rd_valid}}}|
    //:                                                         {bank${j}_ram3_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram3_wt_rd_valid}},
    //:                                                         bank${j}_ram2_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram2_wt_rd_valid}}};
    //:     );
    //:     }
    //: }
    //: if(CBUF_BANK_RAM_CASE==4){
    //: for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    //: print qq(
    //: wire [${kk}-1:0]  bank${j}_wt_rd_data = {bank${j}_ram3_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram3_wt_rd_valid}},
    //:                                                         bank${j}_ram2_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram2_wt_rd_valid}},
    //:                                                         bank${j}_ram1_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram1_wt_rd_valid}},
    //:                                                         bank${j}_ram0_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram0_wt_rd_valid}}};
    //:     );
    //:     }
    //: }
    //: if(CBUF_BANK_RAM_CASE==5){
    //: for(my $j=0; $j<CBUF_BANK_NUMBER ; $j++){
    //: print qq(
    //: wire [${kk}-1:0]  bank${j}_wt_rd_data = {bank${j}_ram7_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram7_wt_rd_valid}},
    //:                                                         bank${j}_ram6_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram6_wt_rd_valid}},
    //:                                                         bank${j}_ram5_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram5_wt_rd_valid}},
    //:                                                         bank${j}_ram4_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram4_wt_rd_valid}}}|
    //:                                                         {bank${j}_ram3_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram3_wt_rd_valid}},
    //:                                                         bank${j}_ram2_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram2_wt_rd_valid}},
    //:                                                         bank${j}_ram1_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram1_wt_rd_valid}},
    //:                                                         bank${j}_ram0_rd_data&{CBUF_RAM_WIDTH{bank${j}_ram0_wt_rd_valid}}};
    //:     );
    //:     }
    //: }

    if(conf.CBUF_BANK_RAM_CASE==0){
        val bank_wt_rd_data = Wire(Vec(conf.CBUF_BANK_NUMBER, UInt(conf.CBUF_RD_PORT_WIDTH.W)))
        for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
            bank_wt_rd_data(j) := ((bank_ram_rd_data(j)(0))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(0)))(conf.CBUF_RD_PORT_WIDTH-1,0)   
        }          
    }
    if(conf.CBUF_BANK_RAM_CASE==1){
        val bank_wt_rd_data = Wire(Vec(conf.CBUF_BANK_NUMBER, UInt(conf.CBUF_RD_PORT_WIDTH.W)))
        for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
            bank_wt_rd_data(j) := (((bank_ram_rd_data(j)(0))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(0)))|((bank_ram_rd_data(j)(1))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(1))))(conf.CBUF_BANK_NUMBER-1,0)  
        }          
    }
    if(conf.CBUF_BANK_RAM_CASE==2){
        val bank_wt_rd_data = Wire(Vec(conf.CBUF_BANK_NUMBER, UInt(conf.CBUF_RD_PORT_WIDTH.W)))
        for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
            bank_wt_rd_data(j) := Cat((bank_ram_rd_data(j)(0))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(0)), (bank_ram_rd_data(j)(1))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(1)))(conf.CBUF_RD_PORT_WIDTH-1,0)   
        }          
    }
    if(conf.CBUF_BANK_RAM_CASE==3){
        val bank_wt_rd_data = Wire(Vec(conf.CBUF_BANK_NUMBER, UInt(conf.CBUF_RD_PORT_WIDTH.W)))      
        for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
            bank_wt_rd_data(j) := (Cat(((bank_ram_rd_data(j)(1))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(1))), ((bank_ram_rd_data(j)(0))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(0))))|Cat(((bank_ram_rd_data(j)(3))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(3)(0))), ((bank_ram_rd_data(j)(2))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(2)(0)))))(conf.CBUF_RD_PORT_WIDTH-1,0)   
        }          
    }
    if(conf.CBUF_BANK_RAM_CASE==4){
        val bank_wt_rd_data= Wire(Vec(conf.CBUF_BANK_NUMBER, UInt(conf.CBUF_RD_PORT_WIDTH.W)))
        for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
            bank_wt_rd_data(j) := Cat((bank_ram_rd_data(j)(3))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(3)), (bank_ram_rd_data(j)(2))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(2)), (bank_ram_rd_data(j)(1))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(1)),(bank_ram_rd_data(j)(0))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(0)))(conf.CBUF_RD_PORT_WIDTH-1,0)   
        }          
    }
    if(conf.CBUF_BANK_RAM_CASE==5){
        val bank_wt_rd_data = Wire(Vec(conf.CBUF_BANK_NUMBER, UInt(conf.CBUF_RD_PORT_WIDTH.W)))      
        for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
            bank_wt_rd_data(j) := (Cat(((bank_ram_rd_data(j)(7))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(7))), ((bank_ram_rd_data(j)(6))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(6))), ((bank_ram_rd_data(j)(5))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(5)(0))), ((bank_ram_rd_data(j)(4))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(4)(0))))|Cat(((bank_ram_rd_data(j)(3))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(3)(0))), ((bank_ram_rd_data(j)(2))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(2)(0))), ((bank_ram_rd_data(j)(1))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(1)(0))), ((bank_ram_rd_data(j)(0))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(0)))))(conf.CBUF_RD_PORT_WIDTH-1,0)   
        }          
    }


    // pipe solution. for timing concern, 4 level pipe. 
    //: my $kk=CBUF_RD_PORT_WIDTH;
    //: for (my $i=0; $i<CBUF_BANK_NUMBER; $i++){
    //: &eperl::flop("-wid ${kk} -norst -q l1group${i}_wt_rd_data   -d bank${i}_wt_rd_data");
    //: }
    //: 
    //: for (my $i=0; $i<CBUF_BANK_NUMBER/4; $i++){
    //: my $ni=$i*4;
    //: my $nii=$i*4+1;
    //: my $niii=$i*4+2;
    //: my $niiii=$i*4+3;
    //: print qq(
    //: wire [${kk}-1:0] l2group${i}_wt_rd_data_w = l1group${ni}_wt_rd_data | l1group${nii}_wt_rd_data | l1group${niii}_wt_rd_data | l1group${niiii}_wt_rd_data;
    //: );
    //: &eperl::flop("-wid ${kk} -norst -q l2group${i}_wt_rd_data   -d l2group${i}_wt_rd_data_w");
    //: }
    //:
    //: for (my $i=0; $i<CBUF_BANK_NUMBER/16; $i++){
    //: my $ni=$i*4;
    //: my $nii=$i*4+1;
    //: my $niii=$i*4+2;
    //: my $niiii=$i*4+3;
    //: print qq(
    //: wire [${kk}-1:0] l3group${i}_wt_rd_data_w = l2group${ni}_wt_rd_data | l2group${nii}_wt_rd_data | l2group${niii}_wt_rd_data | l2group${niiii}_wt_rd_data;
    //: );
    //: &eperl::flop("-wid ${kk} -norst -q l3group${i}_wt_rd_data   -d l3group${i}_wt_rd_data_w");
    //: }
    //: 
    //: if(CBUF_BANK_NUMBER==16){
    //: &eperl::flop("-wid ${kk} -norst -q l4group_wt_rd_data   -d l3group0_wt_rd_data"); 
    //: }
    //: if(CBUF_BANK_NUMBER==32) {
    //: print qq(
    //: wire [${kk}-1:0] l4group_wt_rd_data_w = l3group0_wt_rd_data | l3group1_wt_rd_data;
    //: );
    //: &eperl::flop("-wid ${kk} -norst -q l4group_wt_rd_data   -d l4group_wt_rd_data_w");
    //: }
    //wire[CBUF_RD_PORT_WIDTH-1:0] sc2buf_wt_rd_data = l4group_wt_rd_data[CBUF_RD_PORT_WIDTH-1:0];

    val l1group_wt_rd_data = Reg(Vec(conf.CBUF_BANK_NUMBER, UInt(conf.CBUF_RD_PORT_WIDTH.W)))

    for(i <- 0 to conf.CBUF_BANK_NUMBER-1){
        withClock(io.nvdla_core_clk){
            l1group_wt_rd_data(i) := bank_wt_rd_data(i)
        }
    }

    val l2group_wt_rd_data = Reg(Vec(conf.CBUF_BANK_NUMBER/4, UInt(conf.CBUF_RD_PORT_WIDTH.W)))
    val l2group_wt_rd_data_w = Wire(Vec(conf.CBUF_BANK_NUMBER/4, UInt(conf.CBUF_RD_PORT_WIDTH.W)))

    for(i <- 0 to conf.CBUF_BANK_NUMBER/4-1){
        l2group_wt_rd_data_w(i) := l1group_wt_rd_data(i*4)|l1group_wt_rd_data(i*4+1)|l1group_wt_rd_data(i*4+2)|l1group_wt_rd_data(i*4+3)
        withClock(io.nvdla_core_clk){
            l2group_wt_rd_data(i) := l2group_wt_rd_data_w(i)
        }            
    }

    val l3group_wt_rd_data = Reg(Vec(conf.CBUF_BANK_NUMBER/16, UInt(conf.CBUF_RD_PORT_WIDTH.W)))
    val l3group_wt_rd_data_w = Wire(Vec(conf.CBUF_BANK_NUMBER/16, UInt(conf.CBUF_RD_PORT_WIDTH.W)))                
    for(i <- 0 to conf.CBUF_BANK_NUMBER/16-1){
        l3group_wt_rd_data_w(i) := l2group_wt_rd_data(i*4)|l2group_wt_rd_data(i*4+1)|l2group_wt_rd_data(i*4+2)|l2group_wt_rd_data(i*4+3)
        withClock(io.nvdla_core_clk){
            l3group_wt_rd_data(i) := l3group_wt_rd_data_w(i)
        }            
    }
    if(conf.CBUF_BANK_NUMBER==16){
        val l4group_wt_rd_data = Reg(UInt(conf.CBUF_RD_PORT_WIDTH.W))
        withClock(io.nvdla_core_clk){
            l4group_wt_rd_data := l3group_wt_rd_data(0)
        }            
    } 
    if(conf.CBUF_BANK_NUMBER==32){
        val l4group_wt_rd_data_w = l3group_wt_rd_data(0)|l3group_wt_rd_data(1)
        val l4group_wt_rd_data = Reg(UInt(conf.CBUF_RD_PORT_WIDTH.W))
        withClock(io.nvdla_core_clk){
            l4group_wt_rd_data := l4group_wt_rd_data_w
        }            
    }
    io.sc2buf_wt_rd_data := l4group_wt_rd_data

    //get sram read en, data_rd0/data_rd1/weight/wmb
    //: if ((CBUF_BANK_RAM_CASE==0)|(CBUF_BANK_RAM_CASE==2)|(CBUF_BANK_RAM_CASE==4)){
    //: for (my $i=0; $i<CBUF_BANK_NUMBER-1; $i++){
    //:     for (my $j=0; $j<CBUF_RAM_PER_BANK; $j++){
    //: print qq(
    //:     wire bank${i}_ram${j}_rd_en = bank${i}_ram${j}_data_rd_en|bank${i}_ram${j}_wt_rd_en;
    //: );
    //: }
    //: }
    //: my $i=CBUF_BANK_NUMBER-1;
    //:     for (my $j=0; $j<CBUF_RAM_PER_BANK; $j++){
    //: print  "`ifdef  CBUF_WEIGHT_COMPRESSED";
    //: print qq(
    //: wire bank${i}_ram${j}_rd_en = bank${i}_ram${j}_data_rd_en|bank${i}_ram${j}_wt_rd_en|bank${i}_ram${j}_wmb_rd_en;
    //: `else
    //: wire bank${i}_ram${j}_rd_en = bank${i}_ram${j}_data_rd_en|bank${i}_ram${j}_wt_rd_en;
    //: `endif
    //: );
    //: }
    //: }

    if((conf.CBUF_BANK_RAM_CASE==0)||(conf.CBUF_BANK_RAM_CASE==2)||(conf.CBUF_BANK_RAM_CASE==4)){
        val bank_ram_rd_en = Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Bool())))
        for(i<- 0 to conf.CBUF_BANK_NUMBER-1){
            for(j<- 0 to conf.CBUF_RAM_PER_BANK-1){
                bank_ram_rd_en(i)(j) := bank_ram_data_rd_en_even_case(i)(j)|bank_ram_wt_rd_en(i)(j)
            }
        }       
        val bank_ram_rd_addr = Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, UInt(conf.CBUF_RAM_DEPTH_BITS.W))))
        for(i<- 0 to conf.CBUF_BANK_NUMBER-1){
            for(j<- 0 to conf.CBUF_RAM_PER_BANK-1){
                bank_ram_rd_addr(i)(j) := (Fill(conf.CBUF_RAM_DEPTH_BITS, bank_ram_data_rd_en_even_case(i)(j))&bank_ram_data_rd_addr_even_case(i)(j))|(Fill(conf.CBUF_RAM_DEPTH_BITS, bank_ram_wt_rd_en(i)(j))&bank_ram_wt_rd_addr(i)(j))
            }
        }
    }
    //: if ((CBUF_BANK_RAM_CASE==1)||(CBUF_BANK_RAM_CASE==3)||(CBUF_BANK_RAM_CASE==5)){
    //: for (my $i=0; $i<CBUF_BANK_NUMBER-1; $i++){
    //:     for (my $j=0; $j<CBUF_RAM_PER_BANK; $j++){
    //: print qq(
    //:     wire bank${i}_ram${j}_rd_en = bank${i}_ram${j}_data_rd0_en|bank${i}_ram${j}_data_rd1_en|bank${i}_ram${j}_wt_rd_en;
    //: );
    //: }
    //: }
    //: my $i=CBUF_BANK_NUMBER-1;
    //:     for (my $j=0; $j<CBUF_RAM_PER_BANK; $j++){
    //: print  "`ifdef  CBUF_WEIGHT_COMPRESSED";
    //: print qq(
    //: wire bank${i}_ram${j}_rd_en = bank${i}_ram${j}_data_rd0_en|bank${i}_ram${j}_data_rd1_en|bank${i}_ram${j}_wt_rd_en|bank${i}_ram${j}_wmb_rd_en;
    //: `else
    //: wire bank${i}_ram${j}_rd_en = bank${i}_ram${j}_data_rd0_en|bank${i}_ram${j}_data_rd1_en|bank${i}_ram${j}_wt_rd_en;
    //: `endif
    //: );
    //: }
    //: }

    if((conf.CBUF_BANK_RAM_CASE==1)||(conf.CBUF_BANK_RAM_CASE==3)||(conf.CBUF_BANK_RAM_CASE==5)){
        val bank_ram_rd_en = Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Bool())))
        for(i<- 0 to conf.CBUF_BANK_NUMBER-1){
            for(j<- 0 to conf.CBUF_RAM_PER_BANK-1){
                bank_ram_rd_en(i)(j) := bank_ram_data_rd0_en_odd_case(i)(j)|bank_ram_data_rd1_en_odd_case(i)(j)|bank_ram_wt_rd_en(i)(j)
            }
        }       
        val bank_ram_rd_addr = Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, UInt(conf.CBUF_RAM_DEPTH_BITS.W))))
        for(i<- 0 to conf.CBUF_BANK_NUMBER-1){
            for(j<- 0 to conf.CBUF_RAM_PER_BANK-1){
                bank_ram_rd_addr(i)(j) := (Fill(conf.CBUF_RAM_DEPTH_BITS, bank_ram_data_rd0_en_odd_case(i)(j))&bank_ram_data_rd0_addr_odd_case(i)(j))|(Fill(conf.CBUF_RAM_DEPTH_BITS, bank_ram_data_rd1_en_odd_case(i)(j))&bank_ram_data_rd_addr_odd_case(i)(j))|(Fill(conf.CBUF_RAM_DEPTH_BITS, bank_ram_wt_rd_en(i)(j))&bank_ram_wt_rd_addr(i)(j))
            }
        }
    }

    // add 1 pipe for sram read control signal.
    //: my $kk=CBUF_RAM_DEPTH_BITS;
    //: for(my $i=0; $i<CBUF_BANK_NUMBER ; $i++){
    //:     for(my $j=0; $j<CBUF_RAM_PER_BANK ; $j++){
    //: &eperl::flop("-q bank${i}_ram${j}_rd_en_d1 -d bank${i}_ram${j}_rd_en");
    //: &eperl::flop("-wid ${kk} -q bank${i}_ram${j}_rd_addr_d1 -d bank${i}_ram${j}_rd_addr");
    //:     }
    //: }
    val bank_ram_rd_en_d1 = Reg(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Bool())))
    val bank_ram_rd_addr_d1 = Reg(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, UInt(conf.CBUF_RAM_DEPTH_BITS.W))))
    for(i<- 0 to conf.CBUF_BANK_NUMBER-1){
        for(j<- 0 to conf.CBUF_RAM_PER_BANK-1){
            withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn){
                bank_ram_rd_en_d1(i)(j) := bank_ram_rd_en(i)(j)
                bank_ram_rd_addr_d1(i)(j) := bank_ram_rd_addr(i)(j)
            }
        }
    }  



    //instance SRAM
      
    val u_cbuf_ram_bank_ram = Vec.fill(conf.CBUF_BANK_NUMBER) {Vec.fill(conf.CBUF_RAM_PER_BANK){Module(new nv_ram_rws(conf.CBUF_RAM_DEPTH, conf.CBUF_RAM_WIDTH))}}
     
    for(i<- 0 to conf.CBUF_BANK_NUMBER-1){
        for(j<- 0 to conf.CBUF_RAM_PER_BANK-1){
            io.nvdla_core_clk:=u_cbuf_ram_bank_ram(i)(j).io.clk
            bank_ram_rd_addr_d1(i)(j)(conf.CBUF_RAM_DEPTH_BITS-1,0):=u_cbuf_ram_bank_ram(i)(j).io.ra
            bank_ram_rd_en_d1(i)(j):=u_cbuf_ram_bank_ram(i)(j).io.re   
            bank_ram_rd_data(i)(j):=u_cbuf_ram_bank_ram(i)(j).io.dout
            bank_ram_wr_addr_d2(i)(j)(conf.CBUF_RAM_DEPTH_BITS-1,0):=u_cbuf_ram_bank_ram(i)(j).io.wa
            bank_ram_wr_en_d2(i)(j):=u_cbuf_ram_bank_ram(i)(j).io.we
            bank_ram_wr_data_d2(i)(j):=u_cbuf_ram_bank_ram(i)(j).io.di  
            io.pwrbus_ram_pd(i)(j)(31,0):=u_cbuf_ram_bank_ram(i)(j).io.pwrbus_ram_pd                    
        }
    }

    





 }

  
        



    










    

























            










    
































}


  