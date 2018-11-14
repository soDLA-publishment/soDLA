package nvdla

import chisel3._
import chisel3.experimental._


class NV_NVDLA_RT_cmac_b2cacc(implicit val conf: cmacConfiguration) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

        //control signal
        val mac2accu_src_pvld = Input(Bool())
        val mac2accu_src_mask = Input(UInt((conf.CMAC_ATOMK_HALF).W))
        val mac2accu_src_mode = Input(UInt((conf.CMAC_ATOMK_HALF).W))
        val mac2accu_src_pd = Input(UInt(9.W))//magic number

        val mac2accu_dst_pvld = Output(Bool())
        val mac2accu_dst_mask = Output(UInt((conf.CMAC_ATOMK_HALF).W))
        val mac2accu_dst_mode = Output(UInt((conf.CMAC_ATOMK_HALF).W))
        val mac2accu_dst_pd = Output(UInt(9.W))//magic number


        //data signal
        val mac2accu_src_data = Input(Vec(conf.CMAC_ATOMK_HALF, UInt((conf.CMAC_RESULT_WIDTH).W)))
        val mac2accu_dst_data = Output(Vec(conf.CMAC_ATOMK_HALF, UInt((conf.CMAC_RESULT_WIDTH).W)))


    })


    //fix input wire
    val max2accu_data_d_wire = Wire(Vec(conf.CMAC_ATOMK_HALF, UInt((conf.CMAC_RESULT_WIDTH).W)))
    val max2accu_data_d_regs = Seq.fill(conf.RT_CMAC_A2CACC_LATENCY)(Reg(Vec(conf.CMAC_ATOMK_HALF, UInt((conf.CMAC_RESULT_WIDTH).W))))
    val mac2accu_data_d = VecInit(max2accu_data_d_wire +: max2accu_data_d_regs)

    val mac2accu_mask_d_wire = Wire(UInt((conf.CMAC_ATOMK_HALF).W))
    val mac2accu_mask_d_regs = Seq.fill(conf.RT_CMAC_A2CACC_LATENCY)(Reg(UInt((conf.CMAC_ATOMK_HALF).W)))
    val mac2accu_data_d = VecInit(mac2accu_mask_d_wire +: mac2accu_mask_d_regs)

    val mac2accu_mode_d_wire = Wire(UInt((conf.CMAC_ATOMK_HALF).W))
    val mac2accu_mode_d_regs = Seq.fill(conf.RT_CMAC_A2CACC_LATENCY)(Reg(UInt((conf.CMAC_ATOMK_HALF).W)))
    val mac2accu_mode_d = VecInit(mac2accu_mode_d_wire +: mac2accu_mode_d_regs)  

    val mac2accu_pd_d_wire = Wire(UInt(9.W))
    val mac2accu_pd_d_regs = Seq.fill(conf.RT_CMAC_A2CACC_LATENCY)(Reg(UInt(9.W)))   
    val mac2accu_pd_d = VecInit(mac2accu_pd_d_wire +: mac2accu_pd_d_regs)  

    val mac2accu_pvld_d_wire = Wire(Bool())
    val mac2accu_pvld_d_regs = Seq.fill(conf.RT_CMAC_A2CACC_LATENCY)(Reg(Bool()))   
    val mac2accu_pvld_d = VecInit(mac2accu_pd_d_wire +: mac2accu_pd_d_regs)  
    
    //:    my $delay = RT_CMAC_A2CACC_LATENCY;
    //:    my $i;
    //:    my $j;
    //:    my $k;
    //:    my $kk=CMAC_ATOMK_HALF;
    //:    my $jj=CMAC_RESULT_WIDTH;
    //:    for($k = 0; $k <CMAC_ATOMK_HALF; $k ++) {
    //:        print "assign mac2accu_data${k}_d0 = mac2accu_src_data${k};\n";
    //:    }
    //:

    //assign input port
    mac2accu_pvld_d(0) := io.mac2accu_src_pvld
    mac2accu_mask_d(0) := io.mac2accu_src_mask
    mac2accu_mode_d(0) := io.mac2accu_src_mode
    mac2accu_pd_d(0) := io.mac2accu_src_pd
    mac2accu_data_d(0) := io.mac2accu_src_data


    //:    for($i = 0; $i < $delay; $i ++) {
    //:        $j = $i + 1;
    //:        &eperl::flop("-q mac2accu_pvld_d${j} -d mac2accu_pvld_d${i}");
    //:        &eperl::flop("-wid 9 -q mac2accu_pd_d${j} -en mac2accu_pvld_d${i} -d  mac2accu_pd_d${i}");
    //:        &eperl::flop("-q mac2accu_mode_d${j} -en mac2accu_pvld_d${i} -d  mac2accu_mode_d${i}");
    //:        &eperl::flop("-wid ${kk} -q mac2accu_mask_d${j} -d mac2accu_mask_d${i}");
    //:        for($k = 0; $k < CMAC_ATOMK_HALF; $k ++) {
    //:            &eperl::flop("-wid ${jj} -q mac2accu_data${k}_d${j} -en mac2accu_mask_d${i}[${k}] -d  mac2accu_data${k}_d${i}");
    //:        }
    //:    }
    //:


    //data flight

    
    withClock(io.nvdla_core_clk) {
        for(t <- 0 to conf.RT_CMAC_A2CACC_LATENCY-1){

            mac2accu_pvld_d(t+1) := mac2accu_pvld_d(t)
            mac2accu_mask_d(t+1) := mac2accu_mask_d(t)

            when(mac2accu_pvld_d(t)){
                mac2accu_pd_d(t+1) := mac2accu_pd_d(t)
                mac2accu_mode_d(t+1) := mac2accu_mode_d(t) 
            }
            
            for(i <- 0 to conf.CMAC_ATOMK_HALF-1){

                when (mac2accu_mask_d(t)(i)){
                    mac2accu_data_d(t+1)(i):= mac2accu_data_d(t)(i)
                }               
            }
        }  
    }  

    //output assignment

    io.mac2accu_dst_pvld := mac2accu_pvld_d(conf.RT_CMAC_A2CACC_LATENCY)
    io.mac2accu_dst_mask := mac2accu_dst_mask_d(conf.RT_CMAC_A2CACC_LATENCY) 
    io.mac2accu_dst_mode := mac2accu_dst_mode_d(conf.RT_CMAC_A2CACC_LATENCY)
    io.mac2accu_dst_pd := mac2accu_dst_pd_d(conf.RT_CMAC_A2CACC_LATENCY)
    io.mac2accu_dst_data := mac2accu_dst_data_d(conf.RT_CMAC_A2CACC_LATENCY)



  }