package nvdla

import chisel3._
import chisel3.experimental._




class NV_NVDLA_CMAC_CORE_cfg(implicit val conf: cmacConfiguration) extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())      
        val nvdla_core_rstn = Input(Bool())

        //input
        val dp2reg_done = Input(Bool())
        val reg2dp_conv_mode = Input(Bool())
        val reg2dp_op_en = Input(Bool())

        //config
        val cfg_is_wg = Output(Bool())
        val cfg_reg_en = Output(Bool())     
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
                
    val cfg_is_wg_w = Wire(Bool())
    val cfg_reg_en_w = Wire(Bool())

    //: &eperl::flop(" -q  op_en_d1  -d \"reg2dp_op_en\" -clk nvdla_core_clk -rst nvdla_core_rstn "); 
    //: &eperl::flop(" -q  op_done_d1  -d \"dp2reg_done\" -clk nvdla_core_clk -rst nvdla_core_rstn "); 
    //: &eperl::flop(" -q  cfg_reg_en  -d \"cfg_reg_en_w\" -clk nvdla_core_clk -rst nvdla_core_rstn "); 
    //: &eperl::flop(" -q  cfg_is_wg  -d \"cfg_is_wg_w\" -clk nvdla_core_clk -rst nvdla_core_rstn "); 
    //: &eperl::flop(" -q  cfg_reg_en_d1  -d \"cfg_reg_en\" -clk nvdla_core_clk -rst nvdla_core_rstn "); 

    val op_en_d1 = Reg(Bool())
    val op_done_d1 = Reg(Bool())
    val cfg_reg_en = Reg(Bool())
    val cfg_is_wg = Reg(Bool())
    val cfg_reg_en_d1 = Reg(Bool())

    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn) {
        op_en_d1 := io.reg2dp_op_en
        op_done_d1 := io.dp2reg_done
        io.cfg_reg_en := cfg_reg_en_w
        io.cfg_is_wg := cfg_is_wg_w
        cfg_reg_en_d1 := io.cfg_reg_en        
    }   

    cfg_reg_en_w := (~op_en_d1|op_done_d1) & io.reg2dp_op_en
    cfg_is_wg_w := false.B//wg is not completed by nvdla yet

    }