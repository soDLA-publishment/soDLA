package nvdla

import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_SDP_RDMA_ig extends Module {
    val io = IO(new Bundle {
        // clk
        val nvdla_core_clk = Input(Clock())

        val op_load = Input(Bool())
        val dma_rd_req_rdy = Input(Bool())
        val dma_rd_req_pd = Output(UInt(79.W))
        val dma_rd_req_vld = Output(Bool())
        val ig2cq_prdy = Input(Bool())
        val ig2cq_pd = Output(UInt(16.W))
        val ig2cq_pvld = Output(Bool())
        val reg2dp_op_en = Input(Bool())
        val reg2dp_winograd = Input(Bool())
        val reg2dp_channel = Input(UInt(13.W))
        val reg2dp_height = Input(UInt(13.W))
        val reg2dp_width = Input(UInt(13.W))
        val reg2dp_proc_precision = Input(UInt(2.W))
        val reg2dp_rdma_data_mode = Input(Bool())
        val reg2dp_rdma_data_size = Input(Bool())
        val reg2dp_rdma_data_use = Input(UInt(2.W))
//        val reg2dp_rdma_data_type = Input(Bool())
        val reg2dp_base_addr_high = Input(UInt(32.W))
        val reg2dp_base_addr_low = Input(UInt(27.W))
        val reg2dp_line_stride = Input(UInt(27.W))
        val reg2dp_surface_stride = Input(UInt(27.W))
        val reg2dp_perf_dma_en = Input(Bool())
        val dp2reg_rdma_stall = Output(UInt(32.W))
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
    withClock(io.nvdla_core_clk){

//==============
// Reg Configure
//==============
// get the width of all regs
//==============
// Work Processing
//==============
    val cmd_accept = Wire(Bool())
    val is_cube_end = Wire(Bool())
    val cmd_process = RegInit(false.B)

    val op_done = cmd_accept & is_cube_end

    when(io.op_load){
        cmd_process := true.B
    }.elsewhen(op_done){
        cmd_process := false.B
    }

//==============
// Address catenate and offset calc
//==============

    val cfg_base_addr = Cat(reg2dp_base_addr_high, reg2dp_base_addr_low)
    val cfg_surf_stride = reg2dp_surface_stride
    val cfg_line_stride = reg2dp_line_stride
    val cfg_data_size_1byte = reg2dp_rdma_data_size === false.B
    val cfg_data_use_both = reg2dp_rdma_data_use === 2.U
    val cfg_data_mode_per_kernel = reg2dp_rdma_data_mode === true.B

//==============
// WIDTH Direction
//==============

    val cfg_proc_int8 = reg2dp_proc_precision === false.B
    val cfg_proc_int16 = reg2dp_proc_precision === true.B 

    val cfg_mode_wino = reg2dp_winograd === true.B

    val cfg_mode_1x1_pack = (reg2dp_width === false.B) & (reg2dp_height === false.B)  

//=================================================
// Cube Shape
//=================================================

    val is_last_wg = Wire(Bool())
    val is_last_w = Wire(Bool())
    val is_last_h = Wire(Bool())
    val is_last_c = Wire(Bool())

    val is_wino_end = (!cfg_mode_wino) | cfg_data_mode_per_kernel | is_last_wg
    val is_line_end = (!cfg_mode_wino) | cfg_data_mode_per_kernel | is_wino_end & is_last_w
    val is_surf_end = cfg_mode_1x1_pack | cfg_data_mode_per_kernel | (is_line_end & is_last_h)
    val is_cube_end = cfg_mode_1x1_pack | cfg_data_mode_per_kernel | (is_surf_end & is_last_c)

//==============
// Winograd Count: size always==1, go height direction with 4 steps, then width direction
//==============

    val size_of_wino = Mux(cfg_mode_wino, 2.U, 0.U)
    val count_g = RegInit(0.U(2.W))

    when(cfg_mode_wino){
        when(cmd_accept){
            when(is_wino_end){
                count_g := 0.U
            }.otherwise{
                count_g := count_g + 1.U
            }
        }
    }
    is_last_wg := (count_g === size_of_wino)

//==============
// Width Count:
//==============

    val count_w = RegInit(0.U(15.W))   
    val size_of_width = Reg(UInt(15.W))

    when(cfg_mode_wino){
        when(cmd_accept){
            when(is_line_end){
                count_w := 0.U
            }.elsewhen(is_wino_end){
                count_w := count_w + 1.U
            }
        }
    } 

    is_last_w := (count_w === size_of_width)

//==============
// HEIGHT Count:
//==============

    val size_of_height = Mux(cfg_mode_wino, 
                            (Cat(0.U(2.W), reg2dp_height(12:2))), 
                            reg2dp_height)
    val count_h = RegInit(0.U(13.W))

    when(cmd_accept){
        when(is_surf_end){
            count_h := 0.U
        }.elsewhen(is_line_end){
            count_h := count_h + 1.U
        }
    }

    is_last_h := (count_h === size_of_height)

//==============
// CHANNEL Count:
//==============

    val size_of_surf = Reg(UInt(9.W))
    val count_c = RegInit(0.U(9.W))

    when(cfg_proc_int8){
        size_of_surf = Cat(false.B, reg2dp_channel(12:5))
    }.elsewhen(cfg_proc_int16){
        size_of_surf = reg2dp_channel(12:4)
    }.otherwise{
        size_of_surf = reg2dp_channel(12:4)
    }

    when(cmd_accept){
        when(is_cube_end){
            count_c := 0.U
        }.elsewhen(is_surf_end){
            count_c := count_c + 1.U
        }
    }
    is_last_c := (count_c === size_of_surf)

//==========================================
// DMA Req : ADDR PREPARE
//==========================================

    val base_addr_wino = RegInit(0.U(59.W))
    val base_addr_width = RegInit(0.U(59.W))
    val base_addr_line = RegInit(0.U(59.W))
    val base_addr_surf = RegInit(0.U(59.W))    
    val dma_req_size = Reg(UInt(15.W))

// ADDR WinoG

    when(cfg_mode_wino){
        when(op_load){
            base_addr_wino := cfg_base_addr
        }.elsewhen(cmd_accept){
            when(is_surf_end){
                base_addr_wino := base_addr_surf + cfg_surf_stride
            }.elsewhen(is_line_end){
                base_addr_wino := base_addr_line + (cfg_line_stride << 2)
            }.elsewhen(is_wino_end){
                base_addr_wino := base_addr_width + (dma_req_size + 1.U)
            }.otherwise{
                base_addr_wino := base_addr_wino + cfg_line_stride
            }
        }
    }

// ADDR width

    when(cfg_mode_wino){
        when(op_load){
            base_addr_width := cfg_base_addr
        }.elsewhen(cmd_accept){
            when(is_surf_end){
                base_addr_width := base_addr_surf + cfg_surf_stride
            }.elsewhen(is_line_end){
                base_addr_width := base_addr_line + (cfg_line_stride << 2)
            }.elsewhen(is_wino_end){
                base_addr_width := base_addr_width + (dma_req_size + 1.U)
            }
        }
    }

// LINE

    when(op_load){
        base_addr_line := cfg_base_addr
    }.elsewhen(cmd_accept){
        when(cfg_mode_wino){
            when(is_surf_end){
                base_addr_line := base_addr_surf + cfg_surf_stride
            }.elsewhen(is_line_end){
                base_addr_line := base_addr_line + (cfg_line_stride << 2)
            }
        }.otherwise{
            when(is_surf_end){
                base_addr_line := base_addr_surf + cfg_surf_stride
            }.elsewhen(is_line_end){
                base_addr_line := base_addr_line + cfg_line_stride
            }
        }
    }

// SURF

    when(op_load){
        base_addr_surf := cfg_base_addr
    }.elsewhen(cmd_accept){
//        when(cfg_mode_wino){
            when(is_surf_end){
                base_addr_surf := base_addr_surf + cfg_surf_stride
            }
//        }.otherwise{
//            base_addr_surf := base_addr_surf + cfg_surf_stride
//        }
    }

//==========================================
// DMA Req : Addr
//==========================================

    val dma_req_addr = Reg(UInt(64.W))

    when(cfg_mode_wino){
        dma_req_addr := Cat(base_addr_wino, 0.U(5.W))
    }.otherwise{
        dma_req_addr := Cat(base_addr_line, 0.U(5.W))
    }

// Size_Of_Width: As each element is 1B or 2B, the width of cube will be resized accordingly 

    when(cfg_mode_wino){
        size_of_width := Cat(0.U(3.W), reg2dp_width(12:1))
    }.elsewhen(cfg_proc_int8){
        when(cfg_data_use_both){
            when(cfg_data_size_1byte){
                size_of_width := (reg2dp_width << 1) + 1.U
            }.otherwise{
                size_of_width := (reg2dp_width << 2) + 3.U
            }
        }.otherwise{
            when(cfg_data_size_1byte){
                size_of_width := Cat(0.U(2.W), reg2dp_width)
            }.otherwise{
                size_of_width := (reg2dp_width << 1) + 1.U
            }
        }
    }.otherwise{
        when(cfg_data_use_both){
            size_of_width := (reg2dp_width << 1) + 1.U
        }.otherwise{
            size_of_width := Cat(0.U(2.W), reg2dp_width)
        }
    }
    
//==========================================
// DMA Req : SIZE
//==========================================
// winograd
    val mode_wino_req_size = Reg(UInt(3.W))
    when(cfg_proc_int8){
        when(cfg_data_use_both){
            when(cfg_data_size_1byte){
                mode_wino_req_size := 3.U
            }.otherwise{
                mode_wino_req_size := 7.U
            }
        }.otherwise{
            when(cfg_data_size_1byte){
                mode_wino_req_size := 1.U
            }.otherwise{
                mode_wino_req_size := 3.U
            }
        }
    }.otherwise{
        when(cfg_data_use_both){
            when(cfg_data_size_1byte){
                mode_wino_req_size := 1.U
            }.otherwise{
                mode_wino_req_size := 3.U
            }
        }.otherwise{
            when(cfg_data_size_1byte){
                mode_wino_req_size := 0.U
            }.otherwise{
                mode_wino_req_size := 1.U
            }
        }        
    }

// in 1x1_pack mode, only send one request out 
//assign mode_1x1_req_size = size_of_surf;
// PRECISION: 2byte both
//  8:1byte:single - 1B/elem -  32B/surf - 1 x surf
//  8:2byte:single - 2B/elem -  64B/surf - 2 x surf
//  8:1byte:both   - 2B/elem -  64B/surf - 2 x surf
//  8:2byte:both   - 4B/elem - 128B/surf - 4 x surf
// 16:2byte:single - 2B/elem -  32B/surf - 1 x surf
// 16:2byte:both   - 4B/elem -  64B/surf - 2 x surf

// Straight / Surf

    val size_of_straight = Reg(UInt(15.W))

    when(cfg_proc_int8){
        when(cfg_data_use_both){
            when(cfg_data_size_1byte){
                size_of_straight := (size_of_surf << 1) + 1.U
            }.otherwise{
                size_of_straight := (size_of_surf << 2) + 3.U
            }
        }.otherwise{
             when(cfg_data_size_1byte){
                size_of_straight := (size_of_surf << 0) + 0.U
            }.otherwise{
                size_of_straight := (size_of_surf << 1) + 1.U
            }           
        }
    }.otherwise{
        when(cfg_data_use_both){
            when(cfg_data_size_1byte){
                size_of_straight := (size_of_surf << 1) + 0.U   // illegal
            }.otherwise{
                size_of_straight := (size_of_surf << 1) + 1.U
            }
        }.otherwise{
             when(cfg_data_size_1byte){
                size_of_straight := (size_of_surf << 1) + 0.U   // illegal
            }.otherwise{
                size_of_straight := (size_of_surf << 0) + 0.U
            }           
        }        
    }

//dma_req_size
    when(cfg_data_mode_per_kernel || cfg_mode_1x1_pack){
        dma_req_size := size_of_straight
    }.otherwise{
        when(cfg_mode_wino){
            dma_req_size := Cat(0.U(12.W), mode_wino_req_size)
        }.otherwise{
            dma_req_size := size_of_width
        }
    }

//==========================================
// Context Queue Interface
// size,cube_end
//==========================================

    val ig2eg_size = dma_req_size
    val ig2eg_cube_end = is_cube_end    

// PKT_PACK_WIRE( sdp_brdma_ig2eg ,  ig2eg_ ,  ig2cq_pd )

    val ig2cq_pd = Cat(ig2eg_cube_end, ig2eg_size)
    val dma_rd_req_rdy_d := Wire(Bool())
    io.ig2cq_pvld := cmd_process & dma_rd_req_rdy_d

//==============
// DMA Req : PIPE
//==============
// VALID: clamp when when cq is not ready

    val dma_rd_req_vld_d := cmd_process & ig2cq_prdy


    val dma_rd_req_pd_d := Cat(dma_req_size, dma_req_addr)

    cmd_accept := dma_rd_req_vld_d & dma_rd_req_rdy_d

//==============
// DMA Interface
//==============

    val int_rd_req_ready = Wire(Bool())
    val pipe_p1 = Module{new NV_NVDLA_BC_pipe(79)}
    pipe_p1.io.clk := io.nvdla_core_clk
    pipe_p1.io.vi := dma_rd_req_vld_d
    dma_rd_req_rdy_d := pipe_p1.io.ro
    pipe_p1.io.di := dma_rd_req_pd_d
    val int_rd_req_valid = pipe_p1.io.vo
    pipe_p1.io.ri := int_rd_req_ready
    val int_rd_req_pd = pipe_p1.io.dout 

    val int_rd_req_valid_d0 = int_rd_req_valid
    val int_rd_req_ready = int_rd_req_ready_d0 
    val int_rd_req_pd_d0 = int_rd_req_pd  

    val pipe_p3 = Module{new NV_NVDLA_BC_pipe(79)}
    pipe_p3.io.clk := io.nvdla_core_clk
    pipe_p3.io.vi := int_rd_req_valid_d0
    val int_rd_req_ready_d1 := pipe_p3.io.ro
    pipe_p3.io.di := int_rd_req_pd_d0
    val int_rd_req_valid_d1 = pipe_p3.io.vo
    pipe_p3.io.ri := int_rd_req_ready_d0
    val int_rd_req_pd_d1 = pipe_p3.io.dout    

    io.dma_rd_req_vld := int_rd_req_valid_d1
    int_rd_req_ready_d1 := io.dma_rd_req_rdy
    io.dma_rd_req_pd := int_rd_req_pd_d1



    // // Address decode

    // val nvdla_sdp_rdma_s_pointer_0_wren = (io.reg_offset === "h4".asUInt(32.W))&io.reg_wr_en
    // val nvdla_sdp_rdma_s_status_0_wren = (io.reg_offset === "h0".asUInt(32.W))&io.reg_wr_en
    
    // val nvdla_sdp_rdma_s_pointer_0_out = Cat("b0".asUInt(15.W), io.consumer, "b0".asUInt(15.W), io.producer)
    // val nvdla_sdp_rdma_s_status_0_out = Cat("b0".asUInt(14.W), io.status_1, "b0".asUInt(14.W), io.status_0)

    // // Output mux
   
    // io.reg_rd_data := MuxLookup(io.reg_offset, "b0".asUInt(32.W), 
    // Seq(      
    // "h4".asUInt(32.W)  -> nvdla_sdp_rdma_s_pointer_0_out,
    // "h0".asUInt(32.W)  -> nvdla_sdp_rdma_s_status_0_out 
    // ))

    // // Register flop declarations

    // val producer_out = RegInit(false.B)

    // when(nvdla_sdp_rdma_s_pointer_0_wren){
    //     producer_out:= io.reg_wr_data(0)
    // }
        
    // io.producer := producer_out

}}

