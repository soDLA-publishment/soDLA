package nvdla

import chisel3._

class nvdla(addressWidth: Int) extends BlackBox {

    val nvdla_core_io = IO(new Bundle {
        val clockB = Input(Clock())
        val resetB = Input(Bool())
        val stuff = Input(Bool())
    })

   nvdla_core_clk                  //|< i
   nvdla_core_rstn                 //|< i
   idx2lut_pd                      //|< i
  ,idx2lut_pvld                    //|< i
  ,lut2inp_prdy                    //|< i
  ,op_en_load                      //|< i
  ,pwrbus_ram_pd                   //|< i
  ,reg2dp_lut_int_access_type      //|< i
  ,reg2dp_lut_int_addr             //|< i
  ,reg2dp_lut_int_data             //|< i
  ,reg2dp_lut_int_data_wr          //|< i
  ,reg2dp_lut_int_table_id         //|< i
  ,reg2dp_lut_le_end               //|< i
  ,reg2dp_lut_le_function          //|< i
  ,reg2dp_lut_le_index_offset      //|< i
  ,reg2dp_lut_le_slope_oflow_scale //|< i
  ,reg2dp_lut_le_slope_oflow_shift //|< i
  ,reg2dp_lut_le_slope_uflow_scale //|< i
  ,reg2dp_lut_le_slope_uflow_shift //|< i
  ,reg2dp_lut_le_start             //|< i
  ,reg2dp_lut_lo_end               //|< i
  ,reg2dp_lut_lo_slope_oflow_scale //|< i
  ,reg2dp_lut_lo_slope_oflow_shift //|< i
  ,reg2dp_lut_lo_slope_uflow_scale //|< i
  ,reg2dp_lut_lo_slope_uflow_shift //|< i
  ,reg2dp_lut_lo_start             //|< i
  ,reg2dp_perf_lut_en              //|< i
  ,reg2dp_proc_precision           //|< i
  ,dp2reg_lut_hybrid               //|> o
  ,dp2reg_lut_int_data             //|> o
  ,dp2reg_lut_le_hit               //|> o
  ,dp2reg_lut_lo_hit               //|> o
  ,dp2reg_lut_oflow                //|> o
  ,dp2reg_lut_uflow                //|> o
  ,idx2lut_prdy                    //|> o
  ,lut2inp_pd                      //|> o
  ,lut2inp_pvld                    //|> o
  );