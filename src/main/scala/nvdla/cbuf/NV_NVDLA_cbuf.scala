package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_cbuf(useRealClock:Boolean = true)(implicit val conf: cbufConfiguration) extends Module {
 
  val io = IO(new Bundle {
    //clock
    val nvdla_core_clk = Input(Clock())
    val nvdla_core_rstn = Input(Bool())
    val pwrbus_ram_pd = Input(UInt(32.W))

    //cdma2buf
    //port 0 for data, 1 for weight
    val cdma2buf_wr_en = Input(Vec(conf.CBUF_WR_PORT_NUMBER, Bool()))
    val cdma2buf_wr_sel = Input(Vec(conf.CBUF_WR_PORT_NUMBER, UInt(conf.CBUF_WR_BANK_SEL_WIDTH.W)))
    val cdma2buf_wr_addr = Input(Vec(conf.CBUF_WR_PORT_NUMBER, UInt(conf.CBUF_ADDR_WIDTH.W))) 
    val cdma2buf_wr_data = Input(Vec(conf.CBUF_WR_PORT_NUMBER, UInt(conf.CBUF_WR_PORT_WIDTH.W)))

    //sc2buf
    val sc2buf_dat_rd_en = Input(Bool())     /* data valid */
    val sc2buf_dat_rd_addr = Input(UInt(conf.CBUF_ADDR_WIDTH.W))
    val sc2buf_dat_rd_shift = Input(UInt(conf.CBUF_RD_DATA_SHIFT_WIDTH.W))
    val sc2buf_dat_rd_next1_en = Input(Bool())
    val sc2buf_dat_rd_next1_addr = Input(UInt(conf.CBUF_ADDR_WIDTH.W))
    val sc2buf_dat_rd_valid = Output(Bool())               /* data valid */
    val sc2buf_dat_rd_data = Output(UInt(conf.CBUF_RD_PORT_WIDTH.W))

    val sc2buf_wt_rd_en = Input(Bool()) /* data valid */
    val sc2buf_wt_rd_addr = Input(UInt(conf.CBUF_ADDR_WIDTH.W))
    val sc2buf_wt_rd_valid = Output(Bool())
    val sc2buf_wt_rd_data = Output(UInt(conf.CBUF_RD_PORT_WIDTH.W))

  })


   val internal_clock = if(useRealClock) io.nvdla_core_clk else clock


class cbufImpl{
//////////step1:write handle  
    val bank_ram_wr_en_d0 = Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Vec(conf.CBUF_WR_PORT_NUMBER, Bool()))))
   
    for(j <- 0 to conf.CBUF_BANK_NUMBER-1){ 
        for(k <- 0 to conf.CBUF_RAM_PER_BANK-1){
            for(i <- 0 to conf.CBUF_WR_PORT_NUMBER-1){
                if((conf.CBUF_BANK_RAM_CASE==0)|(conf.CBUF_BANK_RAM_CASE==2)|(conf.CBUF_BANK_RAM_CASE==4)){
                    bank_ram_wr_en_d0(j)(k)(i):=io.cdma2buf_wr_en(i)&&
                                                (io.cdma2buf_wr_addr(i)(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)===j.U)&&
                                                (io.cdma2buf_wr_sel(i)(k)===true.B)
                }
                if(conf.CBUF_BANK_RAM_CASE==1){
                    bank_ram_wr_en_d0(j)(k)(i):=io.cdma2buf_wr_en(i)&&
                                                (io.cdma2buf_wr_addr(i)(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)===j.U)&&
                                                (io.cdma2buf_wr_addr(i)(0)===k.U)
                }
                if(conf.CBUF_BANK_RAM_CASE==3){
                    bank_ram_wr_en_d0(j)(k)(i):=io.cdma2buf_wr_en(i)&&
                                                (io.cdma2buf_wr_addr(i)(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)===j.U)&& 
                                                (io.cdma2buf_wr_addr(i)(0)===k.U)&&
                                                (io.cdma2buf_wr_sel(i)(k%2)===true.B)
                }
                if(conf.CBUF_BANK_RAM_CASE==5){
                    bank_ram_wr_en_d0(j)(k)(i):=io.cdma2buf_wr_en(i)&&
                                                (io.cdma2buf_wr_addr(i)(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)===j.U)&& 
                                                (io.cdma2buf_wr_addr(i)(0)===k.U)&&(io.cdma2buf_wr_sel(i)(k%4)===true.B )
                }
            }
        }
    }

    //reorder 
    val bank_wr_ram_en_d0 = Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_WR_PORT_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Bool()))))
    
    for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
        for(k <- 0 to conf.CBUF_RAM_PER_BANK-1){
            for(i <- 0 to conf.CBUF_WR_PORT_NUMBER-1){
                bank_wr_ram_en_d0(j)(i)(k) := bank_ram_wr_en_d0(j)(k)(i)
            }
        }
    }

//generate sram write en (reduce port number)
    //reduce port 
    val bank_ram_wr_en_d1 = RegInit(VecInit(Seq.fill(conf.CBUF_BANK_NUMBER)(VecInit(Seq.fill(conf.CBUF_RAM_PER_BANK)(false.B)))))      
    for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
        for(k <- 0 to conf.CBUF_RAM_PER_BANK-1){          
            bank_ram_wr_en_d1(j)(k) := bank_ram_wr_en_d0(j)(k).reduce(_|_)//OR-reduce from vector to bool                    
        }
    }
    
    //1 pipe for timing
    val cdma2buf_wr_addr_d1 = RegInit(VecInit(Seq.fill(conf.CBUF_WR_PORT_NUMBER)("b0".asUInt(conf.CBUF_ADDR_WIDTH.W))))
    val cdma2buf_wr_data_d1 = Reg(Vec(conf.CBUF_WR_PORT_NUMBER, UInt(conf.CBUF_WR_PORT_WIDTH.W)))
        
    cdma2buf_wr_addr_d1 := io.cdma2buf_wr_addr
    cdma2buf_wr_data_d1 := io.cdma2buf_wr_data

//generate bank write en (reduce ram per bank)
    val bank_wr_en_d1 = RegInit(VecInit(Seq.fill(conf.CBUF_BANK_NUMBER)(VecInit(Seq.fill(conf.CBUF_WR_PORT_NUMBER)(false.B)))))
    for(i <- 0 to conf.CBUF_WR_PORT_NUMBER-1){
        for(j <- 0 to conf.CBUF_BANK_NUMBER-1){          
            bank_wr_en_d1(j)(i) := bank_wr_ram_en_d0(j)(i).reduce(_|_)
        }
    }
    
//generate bank write addr/data
    val cdma2buf_wr_addr_with_en = Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_WR_PORT_NUMBER, UInt(conf.CBUF_ADDR_WIDTH.W))))
    val cdma2buf_wr_data_with_en = Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_WR_PORT_NUMBER, UInt(conf.CBUF_WR_PORT_WIDTH.W))))
    val bank_wr_addr_d1 = Wire(Vec(conf.CBUF_BANK_NUMBER, UInt(conf.CBUF_ADDR_WIDTH.W)))
    val bank_wr_data_d1 = Wire(Vec(conf.CBUF_BANK_NUMBER, UInt(conf.CBUF_WR_PORT_WIDTH.W)))   

    for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
        for(i <- 0 to conf.CBUF_WR_PORT_NUMBER-1){
            cdma2buf_wr_addr_with_en(j)(i) := Fill(conf.CBUF_ADDR_WIDTH, bank_wr_en_d1(j)(i))&cdma2buf_wr_addr_d1(i)
            cdma2buf_wr_data_with_en(j)(i) := Fill(conf.CBUF_WR_PORT_WIDTH, bank_wr_en_d1(j)(i))&cdma2buf_wr_data_d1(i)
        }
        bank_wr_addr_d1(j) := cdma2buf_wr_addr_with_en(j).reduce(_ | _) 
        bank_wr_data_d1(j) := cdma2buf_wr_data_with_en(j).reduce(_ | _)        
    }

//map bank to sram.
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
    val bank_ram_wr_addr_d2 = RegInit(VecInit(Seq.fill(conf.CBUF_BANK_NUMBER)(VecInit(Seq.fill(conf.CBUF_RAM_PER_BANK)("b0".asUInt(conf.CBUF_RAM_DEPTH_BITS.W))))))
    val bank_ram_wr_data_d2 = Reg(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, UInt(conf.CBUF_WR_PORT_WIDTH.W))))
    val bank_ram_wr_en_d2 = RegInit(VecInit(Seq.fill(conf.CBUF_BANK_NUMBER)(VecInit(Seq.fill(conf.CBUF_RAM_PER_BANK)(false.B)))))
                
    bank_ram_wr_en_d2 := bank_ram_wr_en_d1 
    bank_ram_wr_addr_d2 := bank_ram_wr_addr_d1 
    bank_ram_wr_data_d2 := bank_ram_wr_data_d1 

//////////////////////step2: read data handle
//decode read data address to sram.
    val bank_ram_data_rd_en =  if((conf.CBUF_BANK_RAM_CASE==0)||(conf.CBUF_BANK_RAM_CASE==2)||(conf.CBUF_BANK_RAM_CASE==4))
                               Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Vec(1, Bool())))) 
                               else 
                               Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Vec(2, Bool()))))


    for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
        for(k <- 0 to conf.CBUF_RAM_PER_BANK-1){
            if((conf.CBUF_BANK_RAM_CASE==0)||(conf.CBUF_BANK_RAM_CASE==2)||(conf.CBUF_BANK_RAM_CASE==4)){
                bank_ram_data_rd_en(j)(k)(0) := (io.sc2buf_dat_rd_en)&&(io.sc2buf_dat_rd_addr(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)=== j.U)
            }
            if(conf.CBUF_BANK_RAM_CASE==1){
                bank_ram_data_rd_en(j)(k)(0) := (io.sc2buf_dat_rd_en)&&(io.sc2buf_dat_rd_addr(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)=== j.U)&&(io.sc2buf_dat_rd_addr(0)=== k.U)
                bank_ram_data_rd_en(j)(k)(1) := (io.sc2buf_dat_rd_en&io.sc2buf_dat_rd_next1_en)&&(io.sc2buf_dat_rd_next1_addr(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)=== j.U)&&(io.sc2buf_dat_rd_next1_addr(0)=== k.U)
            }
            if(conf.CBUF_BANK_RAM_CASE==3){
                bank_ram_data_rd_en(j)(k)(0) := (io.sc2buf_dat_rd_en)&&(io.sc2buf_dat_rd_addr(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)=== j.U)&&(io.sc2buf_dat_rd_addr(0)=== (k/2).U)
                bank_ram_data_rd_en(j)(k)(1) := (io.sc2buf_dat_rd_en&io.sc2buf_dat_rd_next1_en)&&(io.sc2buf_dat_rd_next1_addr(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)=== j.U)&&(io.sc2buf_dat_rd_next1_addr(0)=== (k/2).U)
            }   
            if(conf.CBUF_BANK_RAM_CASE==5){
                bank_ram_data_rd_en(j)(k)(0) := (io.sc2buf_dat_rd_en)&&(io.sc2buf_dat_rd_addr(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)=== j.U)&&(io.sc2buf_dat_rd_addr(0)=== (k/4).U)
                bank_ram_data_rd_en(j)(k)(1) := (io.sc2buf_dat_rd_en&io.sc2buf_dat_rd_next1_en)&&(io.sc2buf_dat_rd_next1_addr(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)=== j.U)&&(io.sc2buf_dat_rd_next1_addr(0)=== (k/4).U)
            }
        }       
    }

//get sram data read address
    val bank_ram_data_rd_addr = if((conf.CBUF_BANK_RAM_CASE==0)||(conf.CBUF_BANK_RAM_CASE==2)||(conf.CBUF_BANK_RAM_CASE==4))
                                Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Vec(1, UInt(conf.CBUF_RAM_DEPTH_BITS.W)))))
                                else
                                Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Vec(2, UInt(conf.CBUF_RAM_DEPTH_BITS.W)))))

    for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
        for(k <- 0 to conf.CBUF_RAM_PER_BANK-1){
            if((conf.CBUF_BANK_RAM_CASE==0)||(conf.CBUF_BANK_RAM_CASE==2)||(conf.CBUF_BANK_RAM_CASE==4)){
                bank_ram_data_rd_addr(j)(k)(0) := Fill(conf.CBUF_RAM_DEPTH_BITS, bank_ram_data_rd_en(j)(k)(0))&(io.sc2buf_dat_rd_addr(conf.CBUF_RAM_DEPTH_BITS-1, 0))
            }
            if((conf.CBUF_BANK_RAM_CASE==1)||(conf.CBUF_BANK_RAM_CASE==3)||(conf.CBUF_BANK_RAM_CASE==5)){
                bank_ram_data_rd_addr(j)(k)(0) := Fill(conf.CBUF_RAM_DEPTH_BITS, bank_ram_data_rd_en(j)(k)(0))&(io.sc2buf_dat_rd_addr(conf.CBUF_RAM_DEPTH_BITS, 1))
                bank_ram_data_rd_addr(j)(k)(1) := Fill(conf.CBUF_RAM_DEPTH_BITS, bank_ram_data_rd_en(j)(k)(1))&(io.sc2buf_dat_rd_next1_addr(conf.CBUF_RAM_DEPTH_BITS, 1))
            }       
        }
    }

//add flop for sram data read en
//get sram data read valid.
    var bank_ram_data_rd_en_d1 = if((conf.CBUF_BANK_RAM_CASE==0)||(conf.CBUF_BANK_RAM_CASE==2)||(conf.CBUF_BANK_RAM_CASE==4))
                                 RegInit(VecInit(Seq.fill(conf.CBUF_BANK_NUMBER)(VecInit(Seq.fill(conf.CBUF_RAM_PER_BANK)(VecInit(Seq.fill(1)(false.B)))))))
                                 else
                                 RegInit(VecInit(Seq.fill(conf.CBUF_BANK_NUMBER)(VecInit(Seq.fill(conf.CBUF_RAM_PER_BANK)(VecInit(Seq.fill(2)(false.B)))))))

    var bank_ram_data_rd_valid = if((conf.CBUF_BANK_RAM_CASE==0)||(conf.CBUF_BANK_RAM_CASE==2)||(conf.CBUF_BANK_RAM_CASE==4))
                                 RegInit(VecInit(Seq.fill(conf.CBUF_BANK_NUMBER)(VecInit(Seq.fill(conf.CBUF_RAM_PER_BANK)(VecInit(Seq.fill(1)(false.B)))))))
                                 else
                                 RegInit(VecInit(Seq.fill(conf.CBUF_BANK_NUMBER)(VecInit(Seq.fill(conf.CBUF_RAM_PER_BANK)(VecInit(Seq.fill(2)(false.B)))))))

    for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
        for(k <- 0 to conf.CBUF_RAM_PER_BANK-1){
            if((conf.CBUF_BANK_RAM_CASE==0)||(conf.CBUF_BANK_RAM_CASE==2)||(conf.CBUF_BANK_RAM_CASE==4)){
                bank_ram_data_rd_en_d1(j)(k)(0) := bank_ram_data_rd_en(j)(k)(0)
                bank_ram_data_rd_valid(j)(k)(0) := bank_ram_data_rd_en_d1(j)(k)(0)
            }
            if((conf.CBUF_BANK_RAM_CASE==1)||(conf.CBUF_BANK_RAM_CASE==3)||(conf.CBUF_BANK_RAM_CASE==5)){
                for(i <- 0 to 1){
                    bank_ram_data_rd_en_d1(j)(k)(i) := bank_ram_data_rd_en(j)(k)(i)
                    bank_ram_data_rd_valid(j)(k)(i) := bank_ram_data_rd_en_d1(j)(k)(i)
                }
            }       
        }
    }

//get sc data read valid
    io.sc2buf_dat_rd_valid := ShiftRegister(bank_ram_data_rd_valid.asUInt.orR, 4)
    
    val bank_ram_rd_data = Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, UInt(conf.CBUF_RAM_WIDTH.W))))

//get sc data read bank output data. 
    val bank_data_rd_data = if((conf.CBUF_BANK_RAM_CASE==0)||(conf.CBUF_BANK_RAM_CASE==2)||(conf.CBUF_BANK_RAM_CASE==4))
                            Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(1 ,UInt(conf.CBUF_RD_PORT_WIDTH.W))))
                            else
                            Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(2 ,UInt(conf.CBUF_RD_PORT_WIDTH.W))))


    if(conf.CBUF_BANK_RAM_CASE==0){
        for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
            bank_data_rd_data(j)(0) := (bank_ram_rd_data(j)(0))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd_valid(j)(0)(0)) 
        }          
    }
    if(conf.CBUF_BANK_RAM_CASE==1){
        for(j <- 0 to conf.CBUF_BANK_NUMBER-1){for(i <- 0 to 1){
            bank_data_rd_data(j)(i) := ((bank_ram_rd_data(j)(0))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd_valid(j)(0)(i)))|
                                        ((bank_ram_rd_data(j)(1))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd_valid(j)(1)(i))) 
        }}          
    }
    if(conf.CBUF_BANK_RAM_CASE==2){
        for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
            bank_data_rd_data(j)(0) := Cat((bank_ram_rd_data(j)(1))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd_valid(j)(1)(0)), 
                                        (bank_ram_rd_data(j)(0))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd_valid(j)(0)(0)))
        }          
    }
    if(conf.CBUF_BANK_RAM_CASE==3){   
        for(j <- 0 to conf.CBUF_BANK_NUMBER-1){for(i <- 0 to 1){
            bank_data_rd_data(j)(i) := (Cat(((bank_ram_rd_data(j)(3))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd_valid(j)(3)(i))), 
                                        ((bank_ram_rd_data(j)(2))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd_valid(j)(2)(i))))
                                        |Cat(((bank_ram_rd_data(j)(1))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd_valid(j)(1)(i))), 
                                        ((bank_ram_rd_data(j)(0))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd_valid(j)(0)(i)))))  
        }}          
    }
    if(conf.CBUF_BANK_RAM_CASE==4){
        for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
            bank_data_rd_data(j)(0) := Cat((bank_ram_rd_data(j)(3))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd_valid(j)(3)(0)), 
                                            (bank_ram_rd_data(j)(2))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd_valid(j)(3)(0)), 
                                            (bank_ram_rd_data(j)(1))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd_valid(j)(3)(0)),
                                            (bank_ram_rd_data(j)(0))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd_valid(j)(3)(0)))
        }          
    }
    if(conf.CBUF_BANK_RAM_CASE==5){   
        for(j <- 0 to conf.CBUF_BANK_NUMBER-1){for(i <- 0 to 1){
            bank_data_rd_data(j)(i) := (Cat(((bank_ram_rd_data(j)(7))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd_valid(j)(7)(i))),
                                         ((bank_ram_rd_data(j)(6))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd_valid(j)(6)(i))), 
                                         ((bank_ram_rd_data(j)(5))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd_valid(j)(5)(i))), 
                                         ((bank_ram_rd_data(j)(4))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd_valid(j)(4)(i))))
                                         |Cat(((bank_ram_rd_data(j)(3))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd_valid(j)(3)(i))), 
                                         ((bank_ram_rd_data(j)(2))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd_valid(j)(2)(i))), 
                                         ((bank_ram_rd_data(j)(1))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd_valid(j)(1)(i))), 
                                         ((bank_ram_rd_data(j)(0))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_data_rd_valid(j)(0)(i))))) 
        }}          
    }

    val sc2buf_dat_rd_shift_5T = ShiftRegister(io.sc2buf_dat_rd_shift, 5)
    
// pipe solution. for timing concern, 4 level pipe.
    if((conf.CBUF_BANK_RAM_CASE==0)||(conf.CBUF_BANK_RAM_CASE==2)||(conf.CBUF_BANK_RAM_CASE==4)){
        val l1group_data_rd_data = RegNext(bank_data_rd_data)//first pipe

        val l2group_data_rd_data = Reg(Vec(conf.CBUF_BANK_NUMBER/4, Vec(1 ,UInt(conf.CBUF_RD_PORT_WIDTH.W))))//second pipe      
        for(i <- 0 to conf.CBUF_BANK_NUMBER/4-1){
            l2group_data_rd_data(i)(0) := l1group_data_rd_data(i*4)(0)|l1group_data_rd_data(i*4+1)(0)|l1group_data_rd_data(i*4+2)(0)|l1group_data_rd_data(i*4+3)(0)           
        }

        val l3group_data_rd_data = Reg(Vec(conf.CBUF_BANK_NUMBER/16, Vec(1 ,UInt(conf.CBUF_RD_PORT_WIDTH.W))))//third pipe            
        for(i <- 0 to conf.CBUF_BANK_NUMBER/16-1){
            l3group_data_rd_data(i)(0) := l2group_data_rd_data(i*4)(0)|l2group_data_rd_data(i*4+1)(0)|l2group_data_rd_data(i*4+2)(0)|l2group_data_rd_data(i*4+3)(0)
                        
        }

        val l4group_data_rd_data = Reg(UInt(conf.CBUF_RD_PORT_WIDTH.W))//fourth pipe
        if(conf.CBUF_BANK_NUMBER==16){     
            l4group_data_rd_data := l3group_data_rd_data(0)(0)            
        } 
        if(conf.CBUF_BANK_NUMBER==32){
            l4group_data_rd_data := l3group_data_rd_data(0)(0)|l3group_data_rd_data(1)(0)                    
        }
    
        io.sc2buf_dat_rd_data := l4group_data_rd_data
    }
    if((conf.CBUF_BANK_RAM_CASE==1)||(conf.CBUF_BANK_RAM_CASE==3)||(conf.CBUF_BANK_RAM_CASE==5)){
        val l1group_data_rd_data = RegNext(bank_data_rd_data)//first pipe

        val l2group_data_rd_data = Reg(Vec(conf.CBUF_BANK_NUMBER/4, Vec(2 ,UInt(conf.CBUF_RD_PORT_WIDTH.W))))//second pipe      
        for(i <- 0 to conf.CBUF_BANK_NUMBER/4-1){
            l2group_data_rd_data(i)(0) := l1group_data_rd_data(i*4)(0)|l1group_data_rd_data(i*4+1)(0)|l1group_data_rd_data(i*4+2)(0)|l1group_data_rd_data(i*4+3)(0)
            l2group_data_rd_data(i)(1) := l1group_data_rd_data(i*4)(1)|l1group_data_rd_data(i*4+1)(1)|l1group_data_rd_data(i*4+2)(1)|l1group_data_rd_data(i*4+3)(1)           
        }

        val l3group_data_rd_data = Reg(Vec(conf.CBUF_BANK_NUMBER/16, Vec(2 ,UInt(conf.CBUF_RD_PORT_WIDTH.W))))//third pipe            
        for(i <- 0 to conf.CBUF_BANK_NUMBER/16-1){
            l3group_data_rd_data(i)(0) := l2group_data_rd_data(i*4)(0)|l2group_data_rd_data(i*4+1)(0)|l2group_data_rd_data(i*4+2)(0)|l2group_data_rd_data(i*4+3)(0)
            l3group_data_rd_data(i)(1) := l2group_data_rd_data(i*4)(1)|l2group_data_rd_data(i*4+1)(1)|l2group_data_rd_data(i*4+2)(1)|l2group_data_rd_data(i*4+3)(1)          
        }

        val l4group_data_rd_data = Reg(Vec(2, UInt(conf.CBUF_RD_PORT_WIDTH.W)))//fourth pipe
        if(conf.CBUF_BANK_NUMBER==16){     
            l4group_data_rd_data(0) := l3group_data_rd_data(0)(0)
            l4group_data_rd_data(1) := l3group_data_rd_data(0)(1)          
        } 

        if(conf.CBUF_BANK_NUMBER==32){
            l4group_data_rd_data(0) := l3group_data_rd_data(0)(0)|l3group_data_rd_data(1)(0)
            l4group_data_rd_data(1) := l3group_data_rd_data(0)(1)|l3group_data_rd_data(1)(1)         
        }

        io.sc2buf_dat_rd_data := RegNext((Cat(l4group_data_rd_data(1), l4group_data_rd_data(0)) >> Cat(sc2buf_dat_rd_shift_5T, "b0".asUInt(3.W)))(conf.CBUF_RD_PORT_WIDTH-1, 0))
    }

    /////////////////////step3: read weight handle
    //decode read weight address to sram.

    val bank_ram_wt_rd_en = Wire(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, Bool())))

    for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
        for(k <- 0 to conf.CBUF_RAM_PER_BANK-1){
            if((conf.CBUF_BANK_RAM_CASE==0)||(conf.CBUF_BANK_RAM_CASE==2)||(conf.CBUF_BANK_RAM_CASE==4)){
                bank_ram_wt_rd_en(j)(k) := (io.sc2buf_wt_rd_en)&&(io.sc2buf_wt_rd_addr(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)=== j.U)
            }
            if(conf.CBUF_BANK_RAM_CASE==1){
                bank_ram_wt_rd_en(j)(k) := (io.sc2buf_wt_rd_en)&&(io.sc2buf_wt_rd_addr(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)=== j.U)&&(io.sc2buf_wt_rd_addr(0)=== k.U)
            }
            if(conf.CBUF_BANK_RAM_CASE==3){
                bank_ram_wt_rd_en(j)(k) := (io.sc2buf_wt_rd_en)&&(io.sc2buf_wt_rd_addr(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)=== j.U)&&(io.sc2buf_wt_rd_addr(0)=== (k/2).U)
            }   
            if(conf.CBUF_BANK_RAM_CASE==5){
                bank_ram_wt_rd_en(j)(k) := (io.sc2buf_wt_rd_en)&&(io.sc2buf_wt_rd_addr(conf.CBUF_BANK_SLICE_max,conf.CBUF_BANK_SLICE_min)=== j.U)&&(io.sc2buf_wt_rd_addr(0)=== (k/4).U)
            }
        }       
    } 

    //get sram weight read address
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
    //get sram weight read valid.
    val bank_ram_wt_rd_valid = ShiftRegister(bank_ram_wt_rd_en, 2)

    //get sc weight read valid.
    io.sc2buf_wt_rd_valid := ShiftRegister(bank_ram_wt_rd_valid.asUInt.orR, 4)

    //get sc weight read bank output data. 
    val bank_wt_rd_data = Wire(Vec(conf.CBUF_BANK_NUMBER, UInt(conf.CBUF_RD_PORT_WIDTH.W)))
    if(conf.CBUF_BANK_RAM_CASE==0){
        for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
            bank_wt_rd_data(j) := (bank_ram_rd_data(j)(0))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(0))
        }          
    }
    if(conf.CBUF_BANK_RAM_CASE==1){
        for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
            bank_wt_rd_data(j) := ((bank_ram_rd_data(j)(0))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(0)))|
                                    ((bank_ram_rd_data(j)(1))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(1))) 
        }          
    }
    if(conf.CBUF_BANK_RAM_CASE==2){
        for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
            bank_wt_rd_data(j) := Cat((bank_ram_rd_data(j)(1))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(1)), 
                                    (bank_ram_rd_data(j)(0))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(0)))  
        }          
    }
    if(conf.CBUF_BANK_RAM_CASE==3){     
        for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
            bank_wt_rd_data(j) := Cat(((bank_ram_rd_data(j)(1))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(1))), 
                                    ((bank_ram_rd_data(j)(0))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(0))))|
                                    Cat(((bank_ram_rd_data(j)(3))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(3))), 
                                    ((bank_ram_rd_data(j)(2))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(2))))
        }          
    }
    if(conf.CBUF_BANK_RAM_CASE==4){
        for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
            bank_wt_rd_data(j) := Cat((bank_ram_rd_data(j)(3))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(3)), 
                                    (bank_ram_rd_data(j)(2))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(2)), 
                                    (bank_ram_rd_data(j)(1))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(1)),
                                    (bank_ram_rd_data(j)(0))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(0)))  
        }          
    }
    if(conf.CBUF_BANK_RAM_CASE==5){     
        for(j <- 0 to conf.CBUF_BANK_NUMBER-1){
            bank_wt_rd_data(j) := Cat(((bank_ram_rd_data(j)(7))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(7))), 
                                    ((bank_ram_rd_data(j)(6))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(6))), 
                                    ((bank_ram_rd_data(j)(5))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(5))), 
                                    ((bank_ram_rd_data(j)(4))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(4))))|
                                    Cat(((bank_ram_rd_data(j)(3))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(3))), 
                                    ((bank_ram_rd_data(j)(2))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(2))), 
                                    ((bank_ram_rd_data(j)(1))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(1))), 
                                    ((bank_ram_rd_data(j)(0))&Fill(conf.CBUF_RAM_WIDTH, bank_ram_wt_rd_valid(j)(0))))
        }          
    }


    // pipe solution. for timing concern, 4 level pipe. 
    val l1group_wt_rd_data = RegNext(bank_wt_rd_data) //first pipe

    val l2group_wt_rd_data = Reg(Vec(conf.CBUF_BANK_NUMBER/4, UInt(conf.CBUF_RD_PORT_WIDTH.W)))//second pipe
    for(i <- 0 to conf.CBUF_BANK_NUMBER/4-1){
        l2group_wt_rd_data(i) := l1group_wt_rd_data(i*4)|l1group_wt_rd_data(i*4+1)|l1group_wt_rd_data(i*4+2)|l1group_wt_rd_data(i*4+3)          
    }

    val l3group_wt_rd_data = Reg(Vec(conf.CBUF_BANK_NUMBER/16, UInt(conf.CBUF_RD_PORT_WIDTH.W)))//third pipe           
    for(i <- 0 to conf.CBUF_BANK_NUMBER/16-1){
        l3group_wt_rd_data(i) := l2group_wt_rd_data(i*4)|l2group_wt_rd_data(i*4+1)|l2group_wt_rd_data(i*4+2)|l2group_wt_rd_data(i*4+3)            
    }

    val l4group_wt_rd_data = Reg(UInt(conf.CBUF_RD_PORT_WIDTH.W))//fourth pipe
    if(conf.CBUF_BANK_NUMBER==16){
        l4group_wt_rd_data := l3group_wt_rd_data(0)            
    } 
    if(conf.CBUF_BANK_NUMBER==32){
        l4group_wt_rd_data := l3group_wt_rd_data(0)|l3group_wt_rd_data(1)          
    }

    io.sc2buf_wt_rd_data := l4group_wt_rd_data

/////////////////step4: read WMB handle

    //get sram read en, data_rd0/data_rd1/weight/wmb 
    //get sram read addr, data_rd0/data_rd1/weight/wmb 
    // add 1 pipe for sram read control signal.
    val bank_ram_rd_en_d1 = RegInit(VecInit(Seq.fill(conf.CBUF_BANK_NUMBER)(VecInit(Seq.fill(conf.CBUF_RAM_PER_BANK)(false.B)))))
    val bank_ram_rd_addr_d1 = Reg(Vec(conf.CBUF_BANK_NUMBER, Vec(conf.CBUF_RAM_PER_BANK, UInt(conf.CBUF_RAM_DEPTH_BITS.W))))

    if((conf.CBUF_BANK_RAM_CASE==0)||(conf.CBUF_BANK_RAM_CASE==2)||(conf.CBUF_BANK_RAM_CASE==4)){        
        for(i<- 0 to conf.CBUF_BANK_NUMBER-1){
            for(j<- 0 to conf.CBUF_RAM_PER_BANK-1){
                bank_ram_rd_en_d1(i)(j) := bank_ram_data_rd_en(i)(j)(0)|bank_ram_wt_rd_en(i)(j)
                bank_ram_rd_addr_d1(i)(j) := (Fill(conf.CBUF_RAM_DEPTH_BITS, bank_ram_data_rd_en(i)(j)(0))&bank_ram_data_rd_addr(i)(j)(0))|
                                         (Fill(conf.CBUF_RAM_DEPTH_BITS, bank_ram_wt_rd_en(i)(j))&bank_ram_wt_rd_addr(i)(j))
            }
        }       
    }
    if((conf.CBUF_BANK_RAM_CASE==1)||(conf.CBUF_BANK_RAM_CASE==3)||(conf.CBUF_BANK_RAM_CASE==5)){
        for(i<- 0 to conf.CBUF_BANK_NUMBER-1){
            for(j<- 0 to conf.CBUF_RAM_PER_BANK-1){
                bank_ram_rd_en_d1(i)(j) := bank_ram_data_rd_en(i)(j)(0)|bank_ram_data_rd_en(i)(j)(1)|bank_ram_wt_rd_en(i)(j)
                bank_ram_rd_addr_d1(i)(j) := (Fill(conf.CBUF_RAM_DEPTH_BITS, bank_ram_data_rd_en(i)(j)(0))&bank_ram_data_rd_addr(i)(j)(0))|
                                         (Fill(conf.CBUF_RAM_DEPTH_BITS, bank_ram_data_rd_en(i)(j)(1))&bank_ram_data_rd_addr(i)(j)(1))|
                                         (Fill(conf.CBUF_RAM_DEPTH_BITS, bank_ram_wt_rd_en(i)(j))&bank_ram_wt_rd_addr(i)(j))
            }
        }       
    }
      
    //instance SRAM
    val u_cbuf_ram_bank_ram = Array.fill(conf.CBUF_BANK_NUMBER){Array.fill(conf.CBUF_RAM_PER_BANK){Module(new nv_ram_rws(conf.CBUF_RAM_DEPTH, conf.CBUF_RAM_WIDTH))}}
     
    for(i<- 0 to conf.CBUF_BANK_NUMBER-1){
        for(j<- 0 to conf.CBUF_RAM_PER_BANK-1){
            u_cbuf_ram_bank_ram(i)(j).io.clk := internal_clock
            u_cbuf_ram_bank_ram(i)(j).io.re := bank_ram_rd_en_d1(i)(j)
            u_cbuf_ram_bank_ram(i)(j).io.ra := bank_ram_rd_addr_d1(i)(j)(conf.CBUF_RAM_DEPTH_BITS-1,0)
            u_cbuf_ram_bank_ram(i)(j).io.we := bank_ram_wr_en_d2(i)(j)
            u_cbuf_ram_bank_ram(i)(j).io.wa := bank_ram_wr_addr_d2(i)(j)(conf.CBUF_RAM_DEPTH_BITS-1,0)
            u_cbuf_ram_bank_ram(i)(j).io.di := bank_ram_wr_data_d2(i)(j)
            bank_ram_rd_data(i)(j):=u_cbuf_ram_bank_ram(i)(j).io.dout

        }
    }

}

    val cbuf = withClockAndReset(internal_clock, !io.nvdla_core_rstn){new cbufImpl} 

}

object NV_NVDLA_cbufDriver extends App {
  implicit val conf: cbufConfiguration = new cbufConfiguration
  chisel3.Driver.execute(args, () => new NV_NVDLA_cbuf(useRealClock = true))
}

