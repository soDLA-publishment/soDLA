module NV_NVDLA_CSC_dl( // @[:@3.2]
  input         clock, // @[:@4.4]
  input         reset, // @[:@5.4]
  input         io_nvdla_core_clk, // @[:@6.4]
  input         io_nvdla_core_ng_clk, // @[:@6.4]
  input  [1:0]  io_sc_state, // @[:@6.4]
  input         io_sg2dl_pd_valid, // @[:@6.4]
  input  [30:0] io_sg2dl_pd_bits, // @[:@6.4]
  input         io_sg2dl_reuse_rls, // @[:@6.4]
  input         io_cdma2sc_dat_updt_valid, // @[:@6.4]
  input  [14:0] io_cdma2sc_dat_updt_bits_entries, // @[:@6.4]
  input  [13:0] io_cdma2sc_dat_updt_bits_slices, // @[:@6.4]
  input         io_sc2cdma_dat_pending_req, // @[:@6.4]
  output        io_sc2cdma_dat_updt_valid, // @[:@6.4]
  output [14:0] io_sc2cdma_dat_updt_bits_entries, // @[:@6.4]
  output [13:0] io_sc2cdma_dat_updt_bits_slices, // @[:@6.4]
  output        io_sc2buf_dat_rd_addr_valid, // @[:@6.4]
  output [13:0] io_sc2buf_dat_rd_addr_bits, // @[:@6.4]
  input         io_sc2buf_dat_rd_data_valid, // @[:@6.4]
  input  [63:0] io_sc2buf_dat_rd_data_bits, // @[:@6.4]
  output        io_sc2mac_dat_a_valid, // @[:@6.4]
  output        io_sc2mac_dat_a_bits_mask_0, // @[:@6.4]
  output        io_sc2mac_dat_a_bits_mask_1, // @[:@6.4]
  output        io_sc2mac_dat_a_bits_mask_2, // @[:@6.4]
  output        io_sc2mac_dat_a_bits_mask_3, // @[:@6.4]
  output        io_sc2mac_dat_a_bits_mask_4, // @[:@6.4]
  output        io_sc2mac_dat_a_bits_mask_5, // @[:@6.4]
  output        io_sc2mac_dat_a_bits_mask_6, // @[:@6.4]
  output        io_sc2mac_dat_a_bits_mask_7, // @[:@6.4]
  output [7:0]  io_sc2mac_dat_a_bits_data_0, // @[:@6.4]
  output [7:0]  io_sc2mac_dat_a_bits_data_1, // @[:@6.4]
  output [7:0]  io_sc2mac_dat_a_bits_data_2, // @[:@6.4]
  output [7:0]  io_sc2mac_dat_a_bits_data_3, // @[:@6.4]
  output [7:0]  io_sc2mac_dat_a_bits_data_4, // @[:@6.4]
  output [7:0]  io_sc2mac_dat_a_bits_data_5, // @[:@6.4]
  output [7:0]  io_sc2mac_dat_a_bits_data_6, // @[:@6.4]
  output [7:0]  io_sc2mac_dat_a_bits_data_7, // @[:@6.4]
  output [8:0]  io_sc2mac_dat_a_bits_pd, // @[:@6.4]
  output        io_sc2mac_dat_b_valid, // @[:@6.4]
  output        io_sc2mac_dat_b_bits_mask_0, // @[:@6.4]
  output        io_sc2mac_dat_b_bits_mask_1, // @[:@6.4]
  output        io_sc2mac_dat_b_bits_mask_2, // @[:@6.4]
  output        io_sc2mac_dat_b_bits_mask_3, // @[:@6.4]
  output        io_sc2mac_dat_b_bits_mask_4, // @[:@6.4]
  output        io_sc2mac_dat_b_bits_mask_5, // @[:@6.4]
  output        io_sc2mac_dat_b_bits_mask_6, // @[:@6.4]
  output        io_sc2mac_dat_b_bits_mask_7, // @[:@6.4]
  output [7:0]  io_sc2mac_dat_b_bits_data_0, // @[:@6.4]
  output [7:0]  io_sc2mac_dat_b_bits_data_1, // @[:@6.4]
  output [7:0]  io_sc2mac_dat_b_bits_data_2, // @[:@6.4]
  output [7:0]  io_sc2mac_dat_b_bits_data_3, // @[:@6.4]
  output [7:0]  io_sc2mac_dat_b_bits_data_4, // @[:@6.4]
  output [7:0]  io_sc2mac_dat_b_bits_data_5, // @[:@6.4]
  output [7:0]  io_sc2mac_dat_b_bits_data_6, // @[:@6.4]
  output [7:0]  io_sc2mac_dat_b_bits_data_7, // @[:@6.4]
  output [8:0]  io_sc2mac_dat_b_bits_pd, // @[:@6.4]
  input         io_reg2dp_op_en, // @[:@6.4]
  input         io_reg2dp_conv_mode, // @[:@6.4]
  input  [4:0]  io_reg2dp_batches, // @[:@6.4]
  input  [1:0]  io_reg2dp_proc_precision, // @[:@6.4]
  input         io_reg2dp_datain_format, // @[:@6.4]
  input         io_reg2dp_skip_data_rls, // @[:@6.4]
  input  [12:0] io_reg2dp_datain_channel_ext, // @[:@6.4]
  input  [12:0] io_reg2dp_datain_height_ext, // @[:@6.4]
  input  [12:0] io_reg2dp_datain_width_ext, // @[:@6.4]
  input  [1:0]  io_reg2dp_y_extension, // @[:@6.4]
  input  [12:0] io_reg2dp_weight_channel_ext, // @[:@6.4]
  input  [13:0] io_reg2dp_entries, // @[:@6.4]
  input  [12:0] io_reg2dp_dataout_width, // @[:@6.4]
  input  [11:0] io_reg2dp_rls_slices, // @[:@6.4]
  input  [2:0]  io_reg2dp_conv_x_stride_ext, // @[:@6.4]
  input  [2:0]  io_reg2dp_conv_y_stride_ext, // @[:@6.4]
  input  [4:0]  io_reg2dp_x_dilation_ext, // @[:@6.4]
  input  [4:0]  io_reg2dp_y_dilation_ext, // @[:@6.4]
  input  [4:0]  io_reg2dp_pad_left, // @[:@6.4]
  input  [4:0]  io_reg2dp_pad_top, // @[:@6.4]
  input  [15:0] io_reg2dp_pad_value, // @[:@6.4]
  input  [4:0]  io_reg2dp_data_bank, // @[:@6.4]
  input  [1:0]  io_reg2dp_pra_truncate, // @[:@6.4]
  output        io_slcg_wg_en // @[:@6.4]
);
  wire  is_sg_idle; // @[NV_NVDLA_CSC_dl.scala 77:31:@8.4]
  wire  is_sg_done; // @[NV_NVDLA_CSC_dl.scala 79:31:@10.4]
  wire  layer_st; // @[NV_NVDLA_CSC_dl.scala 86:32:@13.4]
  wire  is_conv; // @[NV_NVDLA_CSC_dl.scala 88:35:@15.4]
  wire  is_img; // @[NV_NVDLA_CSC_dl.scala 89:22:@16.4]
  wire [6:0] _T_181; // @[NV_NVDLA_CSC_dl.scala 96:53:@17.4]
  wire [6:0] _T_183; // @[NV_NVDLA_CSC_dl.scala 96:24:@18.4]
  wire [2:0] sub_h_total_w; // @[NV_NVDLA_CSC_dl.scala 96:100:@19.4]
  wire [2:0] sub_h_cmp_w; // @[NV_NVDLA_CSC_dl.scala 97:22:@20.4]
  wire [3:0] _T_186; // @[NV_NVDLA_CSC_dl.scala 98:34:@21.4]
  wire [3:0] dataout_w_init; // @[NV_NVDLA_CSC_dl.scala 98:34:@22.4]
  wire [3:0] conv_x_stride_w; // @[NV_NVDLA_CSC_dl.scala 99:51:@23.4]
  wire [1:0] _T_188; // @[NV_NVDLA_CSC_dl.scala 100:62:@24.4]
  wire [5:0] _T_191; // @[Cat.scala 30:58:@25.4]
  wire [4:0] _T_194; // @[Cat.scala 30:58:@26.4]
  wire [4:0] _GEN_223; // @[NV_NVDLA_CSC_dl.scala 102:74:@27.4]
  wire [5:0] _T_195; // @[NV_NVDLA_CSC_dl.scala 102:74:@27.4]
  wire [4:0] _T_196; // @[NV_NVDLA_CSC_dl.scala 102:74:@28.4]
  wire  _T_197; // @[Mux.scala 46:19:@29.4]
  wire [4:0] _T_198; // @[Mux.scala 46:16:@30.4]
  wire  _T_199; // @[Mux.scala 46:19:@31.4]
  wire [5:0] pixel_x_stride_w; // @[Mux.scala 46:16:@32.4]
  wire  _T_201; // @[NV_NVDLA_CSC_dl.scala 104:88:@33.4]
  wire [2:0] _T_207; // @[NV_NVDLA_CSC_dl.scala 104:172:@35.4]
  wire [2:0] _T_208; // @[NV_NVDLA_CSC_dl.scala 104:58:@36.4]
  wire [6:0] _T_211; // @[Cat.scala 30:58:@37.4]
  wire [6:0] _GEN_224; // @[NV_NVDLA_CSC_dl.scala 105:81:@38.4]
  wire [7:0] _T_212; // @[NV_NVDLA_CSC_dl.scala 105:81:@38.4]
  wire [6:0] _T_213; // @[NV_NVDLA_CSC_dl.scala 105:81:@39.4]
  wire [5:0] _T_214; // @[NV_NVDLA_CSC_dl.scala 105:130:@40.4]
  wire [6:0] _GEN_225; // @[NV_NVDLA_CSC_dl.scala 105:100:@41.4]
  wire [7:0] _T_215; // @[NV_NVDLA_CSC_dl.scala 105:100:@41.4]
  wire [6:0] _T_216; // @[NV_NVDLA_CSC_dl.scala 105:100:@42.4]
  wire [6:0] _T_219; // @[NV_NVDLA_CSC_dl.scala 106:58:@44.4]
  wire [5:0] _T_220; // @[NV_NVDLA_CSC_dl.scala 106:58:@45.4]
  wire  _T_221; // @[Mux.scala 46:19:@46.4]
  wire [5:0] _T_222; // @[Mux.scala 46:16:@47.4]
  wire  _T_223; // @[Mux.scala 46:19:@48.4]
  wire [6:0] pixel_x_init_w; // @[Mux.scala 46:16:@49.4]
  wire [3:0] pixel_x_init_offset_w; // @[NV_NVDLA_CSC_dl.scala 107:80:@51.4]
  wire [7:0] _T_228; // @[Cat.scala 30:58:@52.4]
  wire [6:0] _T_233; // @[Mux.scala 46:16:@55.4]
  wire [7:0] pixel_x_add_w; // @[Mux.scala 46:16:@57.4]
  wire [9:0] pixel_ch_stride_w; // @[Cat.scala 30:58:@58.4]
  wire [3:0] conv_y_stride_w; // @[NV_NVDLA_CSC_dl.scala 117:52:@59.4]
  wire [5:0] _T_239; // @[NV_NVDLA_CSC_dl.scala 118:60:@60.4]
  wire [5:0] x_dilate_w; // @[NV_NVDLA_CSC_dl.scala 118:21:@61.4]
  wire [5:0] _T_242; // @[NV_NVDLA_CSC_dl.scala 119:60:@62.4]
  wire [5:0] y_dilate_w; // @[NV_NVDLA_CSC_dl.scala 119:21:@63.4]
  reg  layer_st_d1; // @[NV_NVDLA_CSC_dl.scala 121:26:@64.4]
  reg [31:0] _RAND_0;
  reg [5:0] data_batch; // @[NV_NVDLA_CSC_dl.scala 122:25:@66.4]
  reg [31:0] _RAND_1;
  reg [13:0] rls_slices; // @[NV_NVDLA_CSC_dl.scala 123:25:@68.4]
  reg [31:0] _RAND_2;
  reg [13:0] h_offset_slice; // @[NV_NVDLA_CSC_dl.scala 124:29:@70.4]
  reg [31:0] _RAND_3;
  reg [14:0] entries; // @[NV_NVDLA_CSC_dl.scala 125:22:@72.4]
  reg [31:0] _RAND_4;
  reg [14:0] entries_batch; // @[NV_NVDLA_CSC_dl.scala 126:28:@74.4]
  reg [31:0] _RAND_5;
  reg [12:0] dataout_width_cmp; // @[NV_NVDLA_CSC_dl.scala 127:32:@76.4]
  reg [31:0] _RAND_6;
  reg [14:0] rls_entries; // @[NV_NVDLA_CSC_dl.scala 129:26:@80.4]
  reg [31:0] _RAND_7;
  reg [13:0] h_bias_0_stride; // @[NV_NVDLA_CSC_dl.scala 130:30:@82.4]
  reg [31:0] _RAND_8;
  reg [13:0] h_bias_1_stride; // @[NV_NVDLA_CSC_dl.scala 131:30:@84.4]
  reg [31:0] _RAND_9;
  reg [13:0] slice_left; // @[NV_NVDLA_CSC_dl.scala 132:25:@86.4]
  reg [31:0] _RAND_10;
  wire [14:0] entries_single_w; // @[NV_NVDLA_CSC_dl.scala 135:43:@87.4]
  wire [20:0] _T_313; // @[NV_NVDLA_CSC_dl.scala 136:41:@89.4]
  wire [14:0] entries_batch_w; // @[NV_NVDLA_CSC_dl.scala 136:56:@90.4]
  wire [11:0] h_offset_slice_w; // @[NV_NVDLA_CSC_dl.scala 138:37:@91.4]
  wire [14:0] _GEN_226; // @[NV_NVDLA_CSC_dl.scala 139:34:@92.4]
  wire [20:0] _T_314; // @[NV_NVDLA_CSC_dl.scala 139:34:@92.4]
  wire [13:0] h_bias_0_stride_w; // @[NV_NVDLA_CSC_dl.scala 139:47:@93.4]
  wire [14:0] _GEN_227; // @[NV_NVDLA_CSC_dl.scala 140:34:@94.4]
  wire [28:0] _T_315; // @[NV_NVDLA_CSC_dl.scala 140:34:@94.4]
  wire [13:0] h_bias_1_stride_w; // @[NV_NVDLA_CSC_dl.scala 140:51:@95.4]
  wire [12:0] rls_slices_w; // @[NV_NVDLA_CSC_dl.scala 141:41:@96.4]
  wire [13:0] _T_318; // @[NV_NVDLA_CSC_dl.scala 142:77:@97.4]
  wire [12:0] _GEN_228; // @[NV_NVDLA_CSC_dl.scala 142:113:@98.4]
  wire [13:0] _T_319; // @[NV_NVDLA_CSC_dl.scala 142:113:@98.4]
  wire [13:0] _T_320; // @[NV_NVDLA_CSC_dl.scala 142:113:@99.4]
  wire [13:0] slice_left_w; // @[NV_NVDLA_CSC_dl.scala 142:23:@100.4]
  wire [13:0] slices_oprand; // @[NV_NVDLA_CSC_dl.scala 143:24:@101.4]
  wire [14:0] _GEN_229; // @[NV_NVDLA_CSC_dl.scala 144:38:@102.4]
  wire [28:0] _T_321; // @[NV_NVDLA_CSC_dl.scala 144:38:@102.4]
  wire [14:0] slice_entries_w; // @[NV_NVDLA_CSC_dl.scala 144:54:@103.4]
  reg [33:0] is_img_d1; // @[NV_NVDLA_CSC_dl.scala 149:24:@109.4]
  reg [63:0] _RAND_11;
  reg [4:0] data_bank; // @[NV_NVDLA_CSC_dl.scala 150:24:@111.4]
  reg [31:0] _RAND_12;
  reg [13:0] datain_width; // @[NV_NVDLA_CSC_dl.scala 151:27:@113.4]
  reg [31:0] _RAND_13;
  reg [12:0] datain_width_cmp; // @[NV_NVDLA_CSC_dl.scala 152:31:@115.4]
  reg [31:0] _RAND_14;
  reg [12:0] datain_height_cmp; // @[NV_NVDLA_CSC_dl.scala 153:32:@117.4]
  reg [31:0] _RAND_15;
  reg [10:0] datain_channel_cmp; // @[NV_NVDLA_CSC_dl.scala 154:33:@119.4]
  reg [31:0] _RAND_16;
  reg [2:0] sub_h_total_g0; // @[NV_NVDLA_CSC_dl.scala 155:29:@120.4]
  reg [31:0] _RAND_17;
  reg [2:0] sub_h_total_g1; // @[NV_NVDLA_CSC_dl.scala 156:29:@121.4]
  reg [31:0] _RAND_18;
  reg [1:0] sub_h_total_g2; // @[NV_NVDLA_CSC_dl.scala 157:29:@122.4]
  reg [31:0] _RAND_19;
  reg [2:0] sub_h_total_g3; // @[NV_NVDLA_CSC_dl.scala 158:29:@123.4]
  reg [31:0] _RAND_20;
  reg [2:0] sub_h_total_g4; // @[NV_NVDLA_CSC_dl.scala 159:29:@124.4]
  reg [31:0] _RAND_21;
  reg [2:0] sub_h_total_g5; // @[NV_NVDLA_CSC_dl.scala 160:29:@125.4]
  reg [31:0] _RAND_22;
  reg [2:0] sub_h_total_g6; // @[NV_NVDLA_CSC_dl.scala 161:29:@126.4]
  reg [31:0] _RAND_23;
  reg [2:0] sub_h_total_g8; // @[NV_NVDLA_CSC_dl.scala 163:29:@128.4]
  reg [31:0] _RAND_24;
  reg [2:0] sub_h_total_g9; // @[NV_NVDLA_CSC_dl.scala 164:29:@129.4]
  reg [31:0] _RAND_25;
  reg [2:0] sub_h_total_g11; // @[NV_NVDLA_CSC_dl.scala 166:30:@131.4]
  reg [31:0] _RAND_26;
  reg [2:0] sub_h_cmp_g0; // @[NV_NVDLA_CSC_dl.scala 167:27:@132.4]
  reg [31:0] _RAND_27;
  reg [2:0] sub_h_cmp_g1; // @[NV_NVDLA_CSC_dl.scala 168:27:@133.4]
  reg [31:0] _RAND_28;
  reg [3:0] conv_x_stride; // @[NV_NVDLA_CSC_dl.scala 169:28:@135.4]
  reg [31:0] _RAND_29;
  reg [3:0] conv_y_stride; // @[NV_NVDLA_CSC_dl.scala 170:28:@137.4]
  reg [31:0] _RAND_30;
  reg [4:0] batch_cmp; // @[NV_NVDLA_CSC_dl.scala 172:24:@140.4]
  reg [31:0] _RAND_31;
  reg [5:0] pixel_x_init; // @[NV_NVDLA_CSC_dl.scala 173:27:@142.4]
  reg [31:0] _RAND_32;
  reg [6:0] pixel_x_init_offset; // @[NV_NVDLA_CSC_dl.scala 174:34:@144.4]
  reg [31:0] _RAND_33;
  reg [6:0] pixel_x_add; // @[NV_NVDLA_CSC_dl.scala 175:26:@146.4]
  reg [31:0] _RAND_34;
  reg [6:0] pixel_x_byte_stride; // @[NV_NVDLA_CSC_dl.scala 176:34:@148.4]
  reg [31:0] _RAND_35;
  reg [11:0] pixel_ch_stride; // @[NV_NVDLA_CSC_dl.scala 177:30:@150.4]
  reg [31:0] _RAND_36;
  reg [5:0] x_dilate; // @[NV_NVDLA_CSC_dl.scala 178:23:@152.4]
  reg [31:0] _RAND_37;
  reg [5:0] y_dilate; // @[NV_NVDLA_CSC_dl.scala 179:23:@154.4]
  reg [31:0] _RAND_38;
  reg [15:0] pad_value; // @[NV_NVDLA_CSC_dl.scala 180:24:@156.4]
  reg [31:0] _RAND_39;
  reg [14:0] entries_cmp; // @[NV_NVDLA_CSC_dl.scala 181:26:@158.4]
  reg [31:0] _RAND_40;
  reg [14:0] h_bias_2_stride; // @[NV_NVDLA_CSC_dl.scala 182:30:@160.4]
  reg [31:0] _RAND_41;
  reg [14:0] h_bias_3_stride; // @[NV_NVDLA_CSC_dl.scala 183:30:@162.4]
  reg [31:0] _RAND_42;
  reg [13:0] last_slices; // @[NV_NVDLA_CSC_dl.scala 185:26:@164.4]
  reg [31:0] _RAND_43;
  reg [14:0] last_entries; // @[NV_NVDLA_CSC_dl.scala 186:27:@166.4]
  reg [31:0] _RAND_44;
  wire [33:0] _T_506; // @[Bitwise.scala 72:12:@174.6]
  wire [5:0] _T_508; // @[NV_NVDLA_CSC_dl.scala 193:38:@176.6]
  wire [4:0] _T_509; // @[NV_NVDLA_CSC_dl.scala 193:38:@177.6]
  wire [13:0] _T_511; // @[NV_NVDLA_CSC_dl.scala 194:48:@179.6]
  wire [9:0] _T_513; // @[NV_NVDLA_CSC_dl.scala 197:93:@183.6]
  wire [10:0] _T_514; // @[Cat.scala 30:58:@184.6]
  wire [1:0] _T_515; // @[NV_NVDLA_CSC_dl.scala 200:36:@188.6]
  wire [5:0] _T_517; // @[NV_NVDLA_CSC_dl.scala 217:35:@207.6]
  wire [6:0] _T_518; // @[NV_NVDLA_CSC_dl.scala 219:33:@210.6]
  wire [14:0] _T_520; // @[Cat.scala 30:58:@219.6]
  wire [33:0] _GEN_1; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [4:0] _GEN_2; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [13:0] _GEN_3; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [12:0] _GEN_4; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [12:0] _GEN_5; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [10:0] _GEN_6; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [2:0] _GEN_7; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [2:0] _GEN_8; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [1:0] _GEN_9; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [2:0] _GEN_10; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [2:0] _GEN_11; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [2:0] _GEN_12; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [2:0] _GEN_13; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [2:0] _GEN_15; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [2:0] _GEN_16; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [2:0] _GEN_18; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [2:0] _GEN_19; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [2:0] _GEN_20; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [3:0] _GEN_21; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [3:0] _GEN_22; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [5:0] _GEN_24; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [4:0] _GEN_25; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [5:0] _GEN_26; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [6:0] _GEN_27; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [6:0] _GEN_28; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [6:0] _GEN_29; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [11:0] _GEN_30; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [5:0] _GEN_31; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [5:0] _GEN_32; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [15:0] _GEN_33; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [14:0] _GEN_34; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [14:0] _GEN_35; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [14:0] _GEN_36; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [13:0] _GEN_37; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [13:0] _GEN_38; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [13:0] _GEN_39; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [12:0] _GEN_40; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  wire [13:0] _GEN_43; // @[NV_NVDLA_CSC_dl.scala 235:18:@232.4]
  wire [13:0] _GEN_44; // @[NV_NVDLA_CSC_dl.scala 235:18:@232.4]
  wire [14:0] _GEN_45; // @[NV_NVDLA_CSC_dl.scala 235:18:@232.4]
  wire [14:0] _GEN_46; // @[NV_NVDLA_CSC_dl.scala 235:18:@232.4]
  wire [14:0] _GEN_47; // @[NV_NVDLA_CSC_dl.scala 235:18:@232.4]
  wire [13:0] _GEN_48; // @[NV_NVDLA_CSC_dl.scala 242:17:@239.4]
  wire [14:0] _GEN_49; // @[NV_NVDLA_CSC_dl.scala 242:17:@239.4]
  reg [14:0] dat_entry_st; // @[NV_NVDLA_CSC_dl.scala 265:59:@251.4]
  reg [31:0] _RAND_45;
  wire  _T_597; // @[NV_NVDLA_CSC_dl.scala 306:37:@314.4]
  wire  _T_598; // @[NV_NVDLA_CSC_dl.scala 306:23:@315.4]
  wire  _T_1471; // @[NV_NVDLA_CSC_dl.scala 900:32:@1234.4]
  reg  dat_rsp_l3_pvld; // @[NV_NVDLA_CSC_dl.scala 885:41:@1202.4]
  reg [31:0] _RAND_46;
  wire  _T_1472; // @[NV_NVDLA_CSC_dl.scala 900:36:@1235.4]
  wire  _T_1473; // @[NV_NVDLA_CSC_dl.scala 901:35:@1236.4]
  reg  dat_rsp_l1_pvld; // @[NV_NVDLA_CSC_dl.scala 885:41:@1200.4]
  reg [31:0] _RAND_47;
  wire  _T_1474; // @[NV_NVDLA_CSC_dl.scala 901:39:@1237.4]
  wire  _T_1475; // @[NV_NVDLA_CSC_dl.scala 900:57:@1238.4]
  wire  _T_1476; // @[NV_NVDLA_CSC_dl.scala 902:35:@1239.4]
  reg  dat_rsp_l0_pvld; // @[NV_NVDLA_CSC_dl.scala 885:41:@1199.4]
  reg [31:0] _RAND_48;
  wire  _T_1477; // @[NV_NVDLA_CSC_dl.scala 902:39:@1240.4]
  wire  dat_rsp_pvld; // @[NV_NVDLA_CSC_dl.scala 901:60:@1241.4]
  wire  _T_1479; // @[NV_NVDLA_CSC_dl.scala 909:42:@1243.4]
  wire [26:0] _T_1483; // @[Bitwise.scala 72:12:@1245.4]
  reg [26:0] _T_1461; // @[NV_NVDLA_CSC_dl.scala 887:41:@1207.4]
  reg [31:0] _RAND_49;
  wire [26:0] _T_1484; // @[NV_NVDLA_CSC_dl.scala 909:47:@1246.4]
  wire  _T_1485; // @[NV_NVDLA_CSC_dl.scala 910:42:@1247.4]
  wire [26:0] _T_1489; // @[Bitwise.scala 72:12:@1249.4]
  reg [26:0] _T_1455; // @[NV_NVDLA_CSC_dl.scala 887:41:@1205.4]
  reg [31:0] _RAND_50;
  wire [26:0] _T_1490; // @[NV_NVDLA_CSC_dl.scala 910:47:@1250.4]
  wire [26:0] _T_1491; // @[NV_NVDLA_CSC_dl.scala 909:66:@1251.4]
  wire  _T_1492; // @[NV_NVDLA_CSC_dl.scala 911:42:@1252.4]
  wire [26:0] _T_1496; // @[Bitwise.scala 72:12:@1254.4]
  reg [26:0] _T_1452; // @[NV_NVDLA_CSC_dl.scala 887:41:@1204.4]
  reg [31:0] _RAND_51;
  wire [26:0] _T_1497; // @[NV_NVDLA_CSC_dl.scala 911:47:@1255.4]
  wire [26:0] dat_rsp_pd; // @[NV_NVDLA_CSC_dl.scala 910:66:@1256.4]
  wire  dat_rsp_rls; // @[NV_NVDLA_CSC_dl.scala 935:26:@1275.4]
  wire  sub_rls; // @[NV_NVDLA_CSC_dl.scala 303:29:@313.4]
  wire  _T_600; // @[NV_NVDLA_CSC_dl.scala 306:66:@316.4]
  wire  _T_601; // @[NV_NVDLA_CSC_dl.scala 306:53:@317.4]
  wire  dat_rls; // @[NV_NVDLA_CSC_dl.scala 306:42:@318.4]
  wire [13:0] sc2cdma_dat_slices_w; // @[NV_NVDLA_CSC_dl.scala 307:28:@320.4]
  wire [14:0] sc2cdma_dat_entries_w; // @[NV_NVDLA_CSC_dl.scala 308:29:@322.4]
  wire [14:0] dat_entry_avl_sub; // @[NV_NVDLA_CSC_dl.scala 275:28:@262.4]
  wire [15:0] _T_554; // @[NV_NVDLA_CSC_dl.scala 280:37:@269.4]
  wire [14:0] dat_entry_st_inc; // @[NV_NVDLA_CSC_dl.scala 280:37:@270.4]
  wire [13:0] _T_560; // @[Cat.scala 30:58:@272.4]
  wire [14:0] _GEN_230; // @[NV_NVDLA_CSC_dl.scala 281:46:@273.4]
  wire [15:0] _T_561; // @[NV_NVDLA_CSC_dl.scala 281:46:@273.4]
  wire [15:0] _T_562; // @[NV_NVDLA_CSC_dl.scala 281:46:@274.4]
  wire [14:0] dat_entry_st_inc_wrap; // @[NV_NVDLA_CSC_dl.scala 281:46:@275.4]
  wire  is_dat_entry_st_wrap; // @[NV_NVDLA_CSC_dl.scala 282:45:@278.4]
  wire [14:0] _T_570; // @[NV_NVDLA_CSC_dl.scala 283:83:@279.4]
  wire [14:0] dat_entry_st_w; // @[NV_NVDLA_CSC_dl.scala 283:25:@280.4]
  wire  _T_592; // @[NV_NVDLA_CSC_dl.scala 294:13:@303.4]
  wire [14:0] _GEN_52; // @[NV_NVDLA_CSC_dl.scala 294:25:@304.4]
  reg  _T_607; // @[NV_NVDLA_CSC_dl.scala 310:37:@324.4]
  reg [31:0] _RAND_52;
  reg [13:0] _T_610; // @[Reg.scala 19:20:@327.4]
  reg [31:0] _RAND_53;
  wire [13:0] _GEN_54; // @[Reg.scala 20:19:@328.4]
  reg [14:0] _T_613; // @[Reg.scala 19:20:@332.4]
  reg [31:0] _RAND_54;
  wire [14:0] _GEN_55; // @[Reg.scala 20:19:@333.4]
  reg  _T_618; // @[NV_NVDLA_CSC_dl.scala 323:50:@338.4]
  reg [31:0] _RAND_55;
  reg  _T_621; // @[NV_NVDLA_CSC_dl.scala 323:50:@339.4]
  reg [31:0] _RAND_56;
  reg  _T_624; // @[NV_NVDLA_CSC_dl.scala 323:50:@340.4]
  reg [31:0] _RAND_57;
  reg  _T_627; // @[NV_NVDLA_CSC_dl.scala 323:50:@341.4]
  reg [31:0] _RAND_58;
  reg  dl_in_pvld; // @[NV_NVDLA_CSC_dl.scala 323:50:@342.4]
  reg [31:0] _RAND_59;
  reg [30:0] _T_634; // @[NV_NVDLA_CSC_dl.scala 325:47:@344.4]
  reg [31:0] _RAND_60;
  reg [30:0] _T_637; // @[NV_NVDLA_CSC_dl.scala 325:47:@345.4]
  reg [31:0] _RAND_61;
  reg [30:0] _T_640; // @[NV_NVDLA_CSC_dl.scala 325:47:@346.4]
  reg [31:0] _RAND_62;
  reg [30:0] _T_643; // @[NV_NVDLA_CSC_dl.scala 325:47:@347.4]
  reg [31:0] _RAND_63;
  reg [30:0] dl_in_pd; // @[NV_NVDLA_CSC_dl.scala 325:47:@348.4]
  reg [31:0] _RAND_64;
  wire [30:0] _GEN_56; // @[NV_NVDLA_CSC_dl.scala 332:26:@352.4]
  wire [30:0] _GEN_57; // @[NV_NVDLA_CSC_dl.scala 332:26:@356.4]
  wire [30:0] _GEN_58; // @[NV_NVDLA_CSC_dl.scala 332:26:@360.4]
  wire [30:0] _GEN_59; // @[NV_NVDLA_CSC_dl.scala 332:26:@364.4]
  wire [30:0] _GEN_60; // @[NV_NVDLA_CSC_dl.scala 332:26:@368.4]
  reg  _T_650; // @[NV_NVDLA_CSC_dl.scala 342:36:@372.4]
  reg [31:0] _RAND_65;
  reg  _T_653; // @[NV_NVDLA_CSC_dl.scala 342:36:@373.4]
  reg [31:0] _RAND_66;
  reg  _T_656; // @[NV_NVDLA_CSC_dl.scala 342:36:@374.4]
  reg [31:0] _RAND_67;
  reg  _T_659; // @[NV_NVDLA_CSC_dl.scala 342:36:@375.4]
  reg [31:0] _RAND_68;
  reg [30:0] _T_664; // @[NV_NVDLA_CSC_dl.scala 344:34:@377.4]
  reg [31:0] _RAND_69;
  reg [30:0] _T_667; // @[NV_NVDLA_CSC_dl.scala 344:34:@378.4]
  reg [31:0] _RAND_70;
  reg [30:0] _T_670; // @[NV_NVDLA_CSC_dl.scala 344:34:@379.4]
  reg [31:0] _RAND_71;
  reg [30:0] _T_673; // @[NV_NVDLA_CSC_dl.scala 344:34:@380.4]
  reg [31:0] _RAND_72;
  wire [30:0] _GEN_61; // @[NV_NVDLA_CSC_dl.scala 351:23:@384.4]
  wire [30:0] _GEN_62; // @[NV_NVDLA_CSC_dl.scala 351:23:@388.4]
  wire [30:0] _GEN_63; // @[NV_NVDLA_CSC_dl.scala 351:23:@392.4]
  wire [30:0] _GEN_64; // @[NV_NVDLA_CSC_dl.scala 351:23:@396.4]
  wire  _T_674; // @[NV_NVDLA_CSC_dl.scala 356:30:@399.4]
  wire  _T_675; // @[NV_NVDLA_CSC_dl.scala 356:34:@400.4]
  wire  _T_676; // @[NV_NVDLA_CSC_dl.scala 357:30:@401.4]
  wire  _T_677; // @[NV_NVDLA_CSC_dl.scala 357:34:@402.4]
  wire  _T_678; // @[NV_NVDLA_CSC_dl.scala 356:50:@403.4]
  wire  _T_679; // @[NV_NVDLA_CSC_dl.scala 358:30:@404.4]
  wire  _T_680; // @[NV_NVDLA_CSC_dl.scala 358:34:@405.4]
  wire  dl_pvld; // @[NV_NVDLA_CSC_dl.scala 357:50:@406.4]
  wire  _T_681; // @[NV_NVDLA_CSC_dl.scala 360:37:@407.4]
  wire [30:0] _T_685; // @[Bitwise.scala 72:12:@409.4]
  wire [30:0] _T_686; // @[NV_NVDLA_CSC_dl.scala 360:42:@410.4]
  wire  _T_687; // @[NV_NVDLA_CSC_dl.scala 361:37:@411.4]
  wire [30:0] _T_691; // @[Bitwise.scala 72:12:@413.4]
  wire [30:0] _T_692; // @[NV_NVDLA_CSC_dl.scala 361:42:@414.4]
  wire [30:0] _T_693; // @[NV_NVDLA_CSC_dl.scala 360:56:@415.4]
  wire  _T_694; // @[NV_NVDLA_CSC_dl.scala 362:37:@416.4]
  wire [30:0] _T_698; // @[Bitwise.scala 72:12:@418.4]
  wire [30:0] _T_699; // @[NV_NVDLA_CSC_dl.scala 362:42:@419.4]
  wire [30:0] dl_pd; // @[NV_NVDLA_CSC_dl.scala 361:56:@420.4]
  wire [4:0] dl_w_offset; // @[NV_NVDLA_CSC_dl.scala 365:24:@421.4]
  wire [4:0] dl_h_offset; // @[NV_NVDLA_CSC_dl.scala 366:24:@422.4]
  wire [6:0] dl_channel_size; // @[NV_NVDLA_CSC_dl.scala 367:28:@423.4]
  wire [6:0] dl_stripe_length; // @[NV_NVDLA_CSC_dl.scala 368:29:@424.4]
  wire [1:0] dl_cur_sub_h; // @[NV_NVDLA_CSC_dl.scala 369:25:@425.4]
  wire  dl_block_end; // @[NV_NVDLA_CSC_dl.scala 370:25:@426.4]
  wire  dl_channel_end; // @[NV_NVDLA_CSC_dl.scala 371:27:@427.4]
  wire  dl_group_end; // @[NV_NVDLA_CSC_dl.scala 372:25:@428.4]
  wire  dl_layer_end; // @[NV_NVDLA_CSC_dl.scala 373:25:@429.4]
  wire  dl_dat_release; // @[NV_NVDLA_CSC_dl.scala 374:27:@430.4]
  reg [4:0] batch_cnt; // @[NV_NVDLA_CSC_dl.scala 379:24:@433.4]
  reg [31:0] _RAND_73;
  wire [5:0] _T_707; // @[NV_NVDLA_CSC_dl.scala 383:24:@434.4]
  wire [4:0] _T_708; // @[NV_NVDLA_CSC_dl.scala 383:24:@435.4]
  wire  is_batch_end; // @[NV_NVDLA_CSC_dl.scala 385:27:@439.4]
  wire [4:0] _T_709; // @[NV_NVDLA_CSC_dl.scala 382:17:@436.4]
  wire [4:0] _T_710; // @[NV_NVDLA_CSC_dl.scala 381:17:@437.4]
  reg [1:0] sub_h_cnt; // @[NV_NVDLA_CSC_dl.scala 388:24:@441.4]
  reg [31:0] _RAND_74;
  wire [2:0] sub_h_cnt_inc; // @[NV_NVDLA_CSC_dl.scala 391:31:@443.4]
  wire  is_sub_h_end; // @[NV_NVDLA_CSC_dl.scala 392:32:@444.4]
  wire  _T_718; // @[NV_NVDLA_CSC_dl.scala 393:61:@446.4]
  reg [6:0] stripe_cnt; // @[NV_NVDLA_CSC_dl.scala 399:25:@454.4]
  reg [31:0] _RAND_75;
  wire  _T_754; // @[NV_NVDLA_CSC_dl.scala 426:37:@483.4]
  wire  _T_755; // @[NV_NVDLA_CSC_dl.scala 426:24:@484.4]
  wire  _T_757; // @[NV_NVDLA_CSC_dl.scala 426:56:@485.4]
  wire  _T_758; // @[NV_NVDLA_CSC_dl.scala 426:44:@486.4]
  wire  _T_759; // @[NV_NVDLA_CSC_dl.scala 426:42:@487.4]
  wire  _T_761; // @[NV_NVDLA_CSC_dl.scala 426:75:@488.4]
  wire  _T_762; // @[NV_NVDLA_CSC_dl.scala 426:63:@489.4]
  wire  _T_763; // @[NV_NVDLA_CSC_dl.scala 426:61:@490.4]
  reg  dat_exec_valid_d1; // @[NV_NVDLA_CSC_dl.scala 418:32:@476.4]
  reg [31:0] _RAND_76;
  wire  _T_765; // @[NV_NVDLA_CSC_dl.scala 426:22:@491.4]
  wire  dat_exec_valid; // @[NV_NVDLA_CSC_dl.scala 425:22:@492.4]
  wire  _T_719; // @[NV_NVDLA_CSC_dl.scala 393:66:@447.4]
  wire  sub_h_cnt_reg_en; // @[NV_NVDLA_CSC_dl.scala 393:33:@448.4]
  wire  _T_720; // @[NV_NVDLA_CSC_dl.scala 395:31:@450.6]
  wire [2:0] _T_722; // @[NV_NVDLA_CSC_dl.scala 395:21:@451.6]
  wire [2:0] _GEN_65; // @[NV_NVDLA_CSC_dl.scala 394:23:@449.4]
  wire [7:0] _T_728; // @[NV_NVDLA_CSC_dl.scala 403:33:@457.4]
  wire [6:0] stripe_cnt_inc; // @[NV_NVDLA_CSC_dl.scala 403:33:@458.4]
  wire  _T_729; // @[NV_NVDLA_CSC_dl.scala 404:51:@459.4]
  wire  is_stripe_equal; // @[NV_NVDLA_CSC_dl.scala 404:33:@460.4]
  wire  is_stripe_end; // @[NV_NVDLA_CSC_dl.scala 405:34:@462.4]
  wire  _T_732; // @[NV_NVDLA_CSC_dl.scala 406:52:@464.4]
  wire  stripe_cnt_reg_en; // @[NV_NVDLA_CSC_dl.scala 406:34:@465.4]
  wire  _T_734; // @[NV_NVDLA_CSC_dl.scala 410:41:@467.6]
  wire  _T_735; // @[NV_NVDLA_CSC_dl.scala 410:39:@468.6]
  wire [6:0] _T_737; // @[NV_NVDLA_CSC_dl.scala 411:22:@469.6]
  wire [6:0] _T_738; // @[NV_NVDLA_CSC_dl.scala 410:22:@470.6]
  wire [6:0] _T_739; // @[NV_NVDLA_CSC_dl.scala 409:22:@471.6]
  wire [6:0] _GEN_66; // @[NV_NVDLA_CSC_dl.scala 408:24:@466.4]
  reg  dat_pipe_local_valid; // @[NV_NVDLA_CSC_dl.scala 416:35:@474.4]
  reg [31:0] _RAND_77;
  reg  dat_pipe_valid_d1; // @[NV_NVDLA_CSC_dl.scala 417:32:@475.4]
  reg [31:0] _RAND_78;
  wire  dat_pipe_valid; // @[NV_NVDLA_CSC_dl.scala 424:27:@481.4]
  wire  _T_747; // @[NV_NVDLA_CSC_dl.scala 421:49:@478.4]
  wire  _T_750; // @[NV_NVDLA_CSC_dl.scala 422:32:@479.4]
  wire  dat_pipe_local_valid_w; // @[NV_NVDLA_CSC_dl.scala 421:33:@480.4]
  reg [7:0] dat_req_bytes_d1; // @[NV_NVDLA_CSC_dl.scala 434:31:@497.4]
  reg [31:0] _RAND_79;
  wire [7:0] dat_req_bytes; // @[Cat.scala 30:58:@498.4]
  wire [7:0] _GEN_67; // @[NV_NVDLA_CSC_dl.scala 436:21:@499.4]
  reg [12:0] dataout_w_cnt; // @[NV_NVDLA_CSC_dl.scala 442:28:@502.4]
  reg [31:0] _RAND_80;
  reg [12:0] dataout_w_ori; // @[NV_NVDLA_CSC_dl.scala 443:28:@503.4]
  reg [31:0] _RAND_81;
  wire [12:0] _GEN_234; // @[NV_NVDLA_CSC_dl.scala 446:39:@504.4]
  wire [13:0] _T_774; // @[NV_NVDLA_CSC_dl.scala 446:39:@504.4]
  wire [12:0] dataout_w_cnt_inc; // @[NV_NVDLA_CSC_dl.scala 446:39:@505.4]
  wire  _T_775; // @[NV_NVDLA_CSC_dl.scala 447:29:@506.4]
  wire  _T_776; // @[NV_NVDLA_CSC_dl.scala 447:61:@507.4]
  wire  is_w_end; // @[NV_NVDLA_CSC_dl.scala 447:44:@508.4]
  wire  _T_778; // @[NV_NVDLA_CSC_dl.scala 450:43:@511.4]
  wire  _T_779; // @[NV_NVDLA_CSC_dl.scala 450:41:@512.4]
  wire [12:0] _T_780; // @[NV_NVDLA_CSC_dl.scala 451:26:@513.4]
  wire [12:0] _T_781; // @[NV_NVDLA_CSC_dl.scala 450:26:@514.4]
  wire [12:0] dataout_w_cnt_w; // @[NV_NVDLA_CSC_dl.scala 449:26:@515.4]
  wire  _T_783; // @[NV_NVDLA_CSC_dl.scala 452:70:@517.4]
  wire  dataout_w_cnt_reg_en; // @[NV_NVDLA_CSC_dl.scala 452:37:@518.4]
  wire  _T_784; // @[NV_NVDLA_CSC_dl.scala 453:55:@519.4]
  wire  _T_785; // @[NV_NVDLA_CSC_dl.scala 453:71:@520.4]
  wire  dataout_w_ori_reg_en; // @[NV_NVDLA_CSC_dl.scala 453:37:@521.4]
  wire [12:0] _GEN_68; // @[NV_NVDLA_CSC_dl.scala 455:27:@522.4]
  wire [12:0] _GEN_69; // @[NV_NVDLA_CSC_dl.scala 458:27:@525.4]
  reg [10:0] datain_c_cnt; // @[NV_NVDLA_CSC_dl.scala 463:27:@528.4]
  reg [31:0] _RAND_82;
  wire  is_last_channel; // @[NV_NVDLA_CSC_dl.scala 465:37:@529.4]
  wire  _T_789; // @[NV_NVDLA_CSC_dl.scala 466:70:@531.4]
  wire  datain_c_cnt_reg_en; // @[NV_NVDLA_CSC_dl.scala 466:36:@532.4]
  wire [11:0] _T_793; // @[NV_NVDLA_CSC_dl.scala 471:34:@534.6]
  wire [10:0] _T_794; // @[NV_NVDLA_CSC_dl.scala 471:34:@535.6]
  wire [10:0] _T_795; // @[NV_NVDLA_CSC_dl.scala 470:24:@536.6]
  wire [10:0] _T_796; // @[NV_NVDLA_CSC_dl.scala 469:24:@537.6]
  wire [10:0] _GEN_70; // @[NV_NVDLA_CSC_dl.scala 468:26:@533.4]
  reg [13:0] datain_w_cnt; // @[NV_NVDLA_CSC_dl.scala 475:27:@540.4]
  reg [31:0] _RAND_83;
  reg [13:0] datain_w_ori; // @[NV_NVDLA_CSC_dl.scala 476:27:@541.4]
  reg [31:0] _RAND_84;
  reg [15:0] pixel_w_cnt; // @[NV_NVDLA_CSC_dl.scala 477:26:@542.4]
  reg [31:0] _RAND_85;
  reg [15:0] pixel_w_ori; // @[NV_NVDLA_CSC_dl.scala 478:26:@543.4]
  reg [31:0] _RAND_86;
  reg [15:0] pixel_w_ch_ori; // @[NV_NVDLA_CSC_dl.scala 479:29:@544.4]
  reg [31:0] _RAND_87;
  reg [12:0] channel_op_cnt; // @[NV_NVDLA_CSC_dl.scala 480:29:@545.4]
  reg [31:0] _RAND_88;
  reg  pixel_force_clr_d1; // @[NV_NVDLA_CSC_dl.scala 482:33:@547.4]
  reg [31:0] _RAND_89;
  reg  pixel_force_fetch_d1; // @[NV_NVDLA_CSC_dl.scala 483:35:@548.4]
  reg [31:0] _RAND_90;
  wire [12:0] _GEN_235; // @[NV_NVDLA_CSC_dl.scala 486:41:@549.4]
  wire [13:0] _T_816; // @[NV_NVDLA_CSC_dl.scala 486:41:@549.4]
  wire [13:0] _T_817; // @[NV_NVDLA_CSC_dl.scala 486:41:@550.4]
  wire [13:0] datain_w_cnt_st; // @[NV_NVDLA_CSC_dl.scala 485:26:@551.4]
  wire [13:0] _GEN_236; // @[NV_NVDLA_CSC_dl.scala 487:37:@552.4]
  wire [14:0] _T_818; // @[NV_NVDLA_CSC_dl.scala 487:37:@552.4]
  wire [13:0] datain_w_cnt_inc; // @[NV_NVDLA_CSC_dl.scala 487:37:@553.4]
  wire [13:0] _T_821; // @[NV_NVDLA_CSC_dl.scala 492:25:@556.4]
  wire [13:0] _T_822; // @[NV_NVDLA_CSC_dl.scala 491:25:@557.4]
  wire [13:0] datain_w_cnt_w; // @[NV_NVDLA_CSC_dl.scala 490:25:@558.4]
  wire [5:0] _GEN_237; // @[NV_NVDLA_CSC_dl.scala 494:35:@559.4]
  wire [10:0] dl_w_offset_ext; // @[NV_NVDLA_CSC_dl.scala 494:35:@559.4]
  wire [13:0] _GEN_238; // @[NV_NVDLA_CSC_dl.scala 495:33:@560.4]
  wire [14:0] _T_823; // @[NV_NVDLA_CSC_dl.scala 495:33:@560.4]
  wire [13:0] datain_w_cur; // @[NV_NVDLA_CSC_dl.scala 495:33:@561.4]
  wire  _T_826; // @[NV_NVDLA_CSC_dl.scala 496:96:@564.4]
  wire  _T_827; // @[NV_NVDLA_CSC_dl.scala 496:86:@565.4]
  wire  _T_828; // @[NV_NVDLA_CSC_dl.scala 496:84:@566.4]
  wire  datain_w_cnt_reg_en; // @[NV_NVDLA_CSC_dl.scala 496:36:@567.4]
  wire  _T_831; // @[NV_NVDLA_CSC_dl.scala 497:99:@570.4]
  wire  _T_832; // @[NV_NVDLA_CSC_dl.scala 497:89:@571.4]
  wire  _T_833; // @[NV_NVDLA_CSC_dl.scala 497:87:@572.4]
  wire  datain_w_ori_reg_en; // @[NV_NVDLA_CSC_dl.scala 497:36:@573.4]
  wire [6:0] pixel_x_cnt_add; // @[NV_NVDLA_CSC_dl.scala 500:26:@574.4]
  wire  _T_837; // @[NV_NVDLA_CSC_dl.scala 502:79:@576.4]
  wire [10:0] _T_841; // @[NV_NVDLA_CSC_dl.scala 503:74:@579.4]
  wire [10:0] total_channel_op; // @[NV_NVDLA_CSC_dl.scala 502:27:@580.4]
  wire  _T_842; // @[NV_NVDLA_CSC_dl.scala 504:37:@581.4]
  wire  _T_844; // @[NV_NVDLA_CSC_dl.scala 505:35:@582.4]
  wire [13:0] _T_846; // @[NV_NVDLA_CSC_dl.scala 505:66:@583.4]
  wire [12:0] _T_847; // @[NV_NVDLA_CSC_dl.scala 505:66:@584.4]
  wire [12:0] _T_848; // @[NV_NVDLA_CSC_dl.scala 505:22:@585.4]
  wire [12:0] _T_849; // @[NV_NVDLA_CSC_dl.scala 504:22:@586.4]
  wire [12:0] _GEN_239; // @[NV_NVDLA_CSC_dl.scala 507:44:@588.4]
  wire  next_is_last_channel; // @[NV_NVDLA_CSC_dl.scala 507:44:@588.4]
  wire  _T_850; // @[NV_NVDLA_CSC_dl.scala 511:39:@589.4]
  wire  _T_851; // @[NV_NVDLA_CSC_dl.scala 511:54:@590.4]
  wire  _T_852; // @[NV_NVDLA_CSC_dl.scala 511:71:@591.4]
  wire  _T_855; // @[NV_NVDLA_CSC_dl.scala 512:73:@594.4]
  wire  _T_856; // @[NV_NVDLA_CSC_dl.scala 512:71:@595.4]
  wire [15:0] _GEN_240; // @[NV_NVDLA_CSC_dl.scala 512:99:@596.4]
  wire [16:0] _T_857; // @[NV_NVDLA_CSC_dl.scala 512:99:@596.4]
  wire [15:0] _T_858; // @[NV_NVDLA_CSC_dl.scala 512:99:@597.4]
  wire  _T_860; // @[NV_NVDLA_CSC_dl.scala 513:54:@599.4]
  wire [15:0] _GEN_241; // @[NV_NVDLA_CSC_dl.scala 513:90:@600.4]
  wire [16:0] _T_861; // @[NV_NVDLA_CSC_dl.scala 513:90:@600.4]
  wire [15:0] _T_862; // @[NV_NVDLA_CSC_dl.scala 513:90:@601.4]
  wire  _T_864; // @[NV_NVDLA_CSC_dl.scala 514:56:@603.4]
  wire  _T_865; // @[NV_NVDLA_CSC_dl.scala 514:54:@604.4]
  wire [16:0] _T_867; // @[NV_NVDLA_CSC_dl.scala 514:91:@605.4]
  wire [15:0] _T_868; // @[NV_NVDLA_CSC_dl.scala 514:91:@606.4]
  wire  _T_869; // @[NV_NVDLA_CSC_dl.scala 515:41:@607.4]
  wire  _T_870; // @[NV_NVDLA_CSC_dl.scala 515:39:@608.4]
  wire [15:0] _GEN_242; // @[NV_NVDLA_CSC_dl.scala 515:81:@609.4]
  wire [16:0] _T_871; // @[NV_NVDLA_CSC_dl.scala 515:81:@609.4]
  wire [15:0] _T_872; // @[NV_NVDLA_CSC_dl.scala 515:81:@610.4]
  wire [15:0] _T_873; // @[NV_NVDLA_CSC_dl.scala 515:24:@611.4]
  wire [15:0] _T_874; // @[NV_NVDLA_CSC_dl.scala 514:24:@612.4]
  wire [15:0] _T_875; // @[NV_NVDLA_CSC_dl.scala 513:24:@613.4]
  wire [15:0] _T_876; // @[NV_NVDLA_CSC_dl.scala 512:24:@614.4]
  wire [15:0] _T_877; // @[NV_NVDLA_CSC_dl.scala 511:24:@615.4]
  wire [15:0] pixel_w_cnt_w; // @[NV_NVDLA_CSC_dl.scala 510:24:@616.4]
  wire [12:0] _T_883; // @[NV_NVDLA_CSC_dl.scala 517:68:@618.4]
  wire [14:0] pixel_w_cur; // @[Cat.scala 30:58:@619.4]
  wire  _T_884; // @[NV_NVDLA_CSC_dl.scala 518:67:@620.4]
  wire  _T_885; // @[NV_NVDLA_CSC_dl.scala 518:56:@621.4]
  wire  _T_886; // @[NV_NVDLA_CSC_dl.scala 518:87:@622.4]
  wire  _T_887; // @[NV_NVDLA_CSC_dl.scala 518:71:@623.4]
  wire  pixel_w_cnt_reg_en; // @[NV_NVDLA_CSC_dl.scala 518:38:@624.4]
  wire  _T_888; // @[NV_NVDLA_CSC_dl.scala 519:67:@625.4]
  wire  _T_889; // @[NV_NVDLA_CSC_dl.scala 519:56:@626.4]
  wire  _T_890; // @[NV_NVDLA_CSC_dl.scala 519:71:@627.4]
  wire  _T_891; // @[NV_NVDLA_CSC_dl.scala 519:87:@628.4]
  wire  pixel_w_ori_reg_en; // @[NV_NVDLA_CSC_dl.scala 519:38:@629.4]
  wire  _T_892; // @[NV_NVDLA_CSC_dl.scala 520:68:@630.4]
  wire  _T_893; // @[NV_NVDLA_CSC_dl.scala 520:57:@631.4]
  wire  _T_894; // @[NV_NVDLA_CSC_dl.scala 520:72:@632.4]
  wire  _T_895; // @[NV_NVDLA_CSC_dl.scala 520:88:@633.4]
  wire  _T_896; // @[NV_NVDLA_CSC_dl.scala 520:103:@634.4]
  wire  pixel_ch_ori_reg_en; // @[NV_NVDLA_CSC_dl.scala 520:39:@635.4]
  wire  _T_898; // @[NV_NVDLA_CSC_dl.scala 522:42:@637.4]
  wire  _T_901; // @[NV_NVDLA_CSC_dl.scala 522:74:@638.4]
  wire  pixel_force_fetch; // @[NV_NVDLA_CSC_dl.scala 522:28:@639.4]
  wire  _T_903; // @[NV_NVDLA_CSC_dl.scala 523:36:@641.4]
  wire  _T_904; // @[NV_NVDLA_CSC_dl.scala 523:72:@642.4]
  wire  pixel_force_clr; // @[NV_NVDLA_CSC_dl.scala 523:51:@643.4]
  wire [13:0] _GEN_71; // @[NV_NVDLA_CSC_dl.scala 525:26:@644.4]
  wire [13:0] _GEN_72; // @[NV_NVDLA_CSC_dl.scala 528:26:@647.4]
  wire [15:0] _GEN_73; // @[NV_NVDLA_CSC_dl.scala 531:25:@650.4]
  wire [15:0] _GEN_74; // @[NV_NVDLA_CSC_dl.scala 534:25:@653.4]
  wire [15:0] _GEN_75; // @[NV_NVDLA_CSC_dl.scala 537:26:@656.4]
  reg [13:0] datain_h_cnt; // @[NV_NVDLA_CSC_dl.scala 543:27:@659.4]
  reg [31:0] _RAND_91;
  reg [13:0] datain_h_ori; // @[NV_NVDLA_CSC_dl.scala 544:27:@660.4]
  reg [31:0] _RAND_92;
  wire [13:0] _GEN_243; // @[NV_NVDLA_CSC_dl.scala 546:41:@661.4]
  wire [14:0] _T_910; // @[NV_NVDLA_CSC_dl.scala 546:41:@661.4]
  wire [14:0] _T_911; // @[NV_NVDLA_CSC_dl.scala 546:41:@662.4]
  wire [13:0] datain_h_cnt_st; // @[NV_NVDLA_CSC_dl.scala 546:41:@663.4]
  wire [13:0] _GEN_244; // @[NV_NVDLA_CSC_dl.scala 547:37:@664.4]
  wire [14:0] _T_912; // @[NV_NVDLA_CSC_dl.scala 547:37:@664.4]
  wire [13:0] datain_h_cnt_inc; // @[NV_NVDLA_CSC_dl.scala 547:37:@665.4]
  wire  _T_913; // @[NV_NVDLA_CSC_dl.scala 548:52:@666.4]
  wire  _T_914; // @[NV_NVDLA_CSC_dl.scala 548:35:@667.4]
  wire [13:0] _T_917; // @[NV_NVDLA_CSC_dl.scala 550:25:@670.4]
  wire [13:0] _T_918; // @[NV_NVDLA_CSC_dl.scala 549:25:@671.4]
  wire [13:0] datain_h_cnt_w; // @[NV_NVDLA_CSC_dl.scala 548:25:@672.4]
  wire  _T_921; // @[NV_NVDLA_CSC_dl.scala 551:91:@675.4]
  wire  _T_922; // @[NV_NVDLA_CSC_dl.scala 551:54:@676.4]
  wire  datain_h_cnt_reg_en; // @[NV_NVDLA_CSC_dl.scala 551:36:@677.4]
  wire [5:0] _GEN_245; // @[NV_NVDLA_CSC_dl.scala 553:35:@681.4]
  wire [10:0] dl_h_offset_ext; // @[NV_NVDLA_CSC_dl.scala 553:35:@681.4]
  wire [13:0] _GEN_246; // @[NV_NVDLA_CSC_dl.scala 554:33:@682.4]
  wire [14:0] _T_925; // @[NV_NVDLA_CSC_dl.scala 554:33:@682.4]
  wire [13:0] _T_926; // @[NV_NVDLA_CSC_dl.scala 554:33:@683.4]
  wire [13:0] _GEN_247; // @[NV_NVDLA_CSC_dl.scala 554:51:@684.4]
  wire [14:0] _T_927; // @[NV_NVDLA_CSC_dl.scala 554:51:@684.4]
  wire [13:0] datain_h_cur; // @[NV_NVDLA_CSC_dl.scala 554:51:@685.4]
  wire [13:0] _GEN_76; // @[NV_NVDLA_CSC_dl.scala 556:26:@686.4]
  wire [13:0] _GEN_77; // @[NV_NVDLA_CSC_dl.scala 557:26:@689.4]
  wire  _T_928; // @[NV_NVDLA_CSC_dl.scala 560:39:@692.4]
  wire [13:0] _GEN_248; // @[NV_NVDLA_CSC_dl.scala 560:59:@693.4]
  wire  _T_929; // @[NV_NVDLA_CSC_dl.scala 560:59:@693.4]
  wire  _T_930; // @[NV_NVDLA_CSC_dl.scala 560:44:@694.4]
  wire  _T_931; // @[NV_NVDLA_CSC_dl.scala 560:92:@695.4]
  wire  _T_932; // @[NV_NVDLA_CSC_dl.scala 560:78:@696.4]
  wire [13:0] _GEN_249; // @[NV_NVDLA_CSC_dl.scala 560:112:@697.4]
  wire  _T_933; // @[NV_NVDLA_CSC_dl.scala 560:112:@697.4]
  wire  dat_conv_req_dummy; // @[NV_NVDLA_CSC_dl.scala 560:97:@698.4]
  wire  dat_img_req_dummy; // @[NV_NVDLA_CSC_dl.scala 563:42:@708.4]
  wire  _T_1016; // @[NV_NVDLA_CSC_dl.scala 643:33:@794.4]
  wire  _T_1017; // @[NV_NVDLA_CSC_dl.scala 644:24:@795.4]
  wire  _T_1018; // @[NV_NVDLA_CSC_dl.scala 644:55:@796.4]
  wire  _T_1019; // @[NV_NVDLA_CSC_dl.scala 644:41:@797.4]
  wire [12:0] _T_1021; // @[NV_NVDLA_CSC_dl.scala 644:95:@798.4]
  wire [14:0] _T_1022; // @[Cat.scala 30:58:@799.4]
  wire [14:0] _T_1026; // @[NV_NVDLA_CSC_dl.scala 644:23:@802.4]
  wire [14:0] w_bias_int8; // @[NV_NVDLA_CSC_dl.scala 643:23:@803.4]
  wire [13:0] w_bias_w; // @[NV_NVDLA_CSC_dl.scala 660:24:@805.4]
  wire [11:0] _T_945; // @[NV_NVDLA_CSC_dl.scala 567:32:@710.4]
  wire [14:0] _GEN_251; // @[NV_NVDLA_CSC_dl.scala 567:40:@711.4]
  wire  dat_img_req_skip; // @[NV_NVDLA_CSC_dl.scala 567:40:@711.4]
  wire  _T_946; // @[NV_NVDLA_CSC_dl.scala 568:34:@712.4]
  wire  dat_req_dummy; // @[NV_NVDLA_CSC_dl.scala 568:24:@713.4]
  wire  _T_947; // @[NV_NVDLA_CSC_dl.scala 569:29:@714.4]
  wire  dat_req_skip; // @[NV_NVDLA_CSC_dl.scala 569:33:@715.4]
  wire  _T_948; // @[NV_NVDLA_CSC_dl.scala 570:39:@716.4]
  wire  _T_949; // @[NV_NVDLA_CSC_dl.scala 570:37:@717.4]
  wire  _T_950; // @[NV_NVDLA_CSC_dl.scala 570:56:@718.4]
  wire  dat_req_valid; // @[NV_NVDLA_CSC_dl.scala 570:54:@719.4]
  wire  _T_951; // @[NV_NVDLA_CSC_dl.scala 573:37:@720.4]
  wire  _T_952; // @[NV_NVDLA_CSC_dl.scala 573:27:@721.4]
  wire  dat_req_sub_c_w; // @[NV_NVDLA_CSC_dl.scala 573:26:@723.4]
  wire [1:0] dat_req_sub_w_w; // @[NV_NVDLA_CSC_dl.scala 574:35:@724.4]
  wire  _T_955; // @[NV_NVDLA_CSC_dl.scala 575:55:@725.4]
  wire  dat_req_sub_w_st_en; // @[NV_NVDLA_CSC_dl.scala 575:42:@726.4]
  wire  dat_req_stripe_end; // @[NV_NVDLA_CSC_dl.scala 578:42:@728.4]
  wire [8:0] dat_req_flag_w; // @[Cat.scala 30:58:@732.4]
  reg  dat_req_valid_d1; // @[NV_NVDLA_CSC_dl.scala 585:31:@733.4]
  reg [31:0] _RAND_93;
  reg [1:0] dat_req_sub_w_d1; // @[NV_NVDLA_CSC_dl.scala 586:31:@734.4]
  reg [31:0] _RAND_94;
  reg [1:0] dat_req_sub_h_d1; // @[NV_NVDLA_CSC_dl.scala 587:31:@735.4]
  reg [31:0] _RAND_95;
  reg  dat_req_sub_c_d1; // @[NV_NVDLA_CSC_dl.scala 588:31:@736.4]
  reg [31:0] _RAND_96;
  reg  dat_req_ch_end_d1; // @[NV_NVDLA_CSC_dl.scala 589:32:@737.4]
  reg [31:0] _RAND_97;
  reg  dat_req_dummy_d1; // @[NV_NVDLA_CSC_dl.scala 590:31:@738.4]
  reg [31:0] _RAND_98;
  reg [1:0] dat_req_cur_sub_h_d1; // @[NV_NVDLA_CSC_dl.scala 591:35:@739.4]
  reg [31:0] _RAND_99;
  reg  dat_req_sub_w_st_d1; // @[NV_NVDLA_CSC_dl.scala 592:34:@740.4]
  reg [31:0] _RAND_100;
  reg [8:0] dat_req_flag_d1; // @[NV_NVDLA_CSC_dl.scala 593:30:@741.4]
  reg [31:0] _RAND_101;
  reg  dat_req_rls_d1; // @[NV_NVDLA_CSC_dl.scala 594:29:@742.4]
  reg [31:0] _RAND_102;
  wire  _T_979; // @[NV_NVDLA_CSC_dl.scala 605:38:@752.6]
  wire  _T_980; // @[NV_NVDLA_CSC_dl.scala 605:56:@753.6]
  wire [1:0] _GEN_78; // @[NV_NVDLA_CSC_dl.scala 597:21:@744.4]
  wire [1:0] _GEN_79; // @[NV_NVDLA_CSC_dl.scala 597:21:@744.4]
  wire  _GEN_80; // @[NV_NVDLA_CSC_dl.scala 597:21:@744.4]
  wire  _GEN_81; // @[NV_NVDLA_CSC_dl.scala 597:21:@744.4]
  wire  _GEN_82; // @[NV_NVDLA_CSC_dl.scala 597:21:@744.4]
  wire [1:0] _GEN_83; // @[NV_NVDLA_CSC_dl.scala 597:21:@744.4]
  wire [8:0] _GEN_84; // @[NV_NVDLA_CSC_dl.scala 597:21:@744.4]
  wire  _GEN_85; // @[NV_NVDLA_CSC_dl.scala 597:21:@744.4]
  wire  _GEN_86; // @[NV_NVDLA_CSC_dl.scala 597:21:@744.4]
  wire  _GEN_87; // @[NV_NVDLA_CSC_dl.scala 597:21:@744.4]
  wire  _GEN_88; // @[NV_NVDLA_CSC_dl.scala 609:26:@758.4]
  reg [13:0] c_bias; // @[NV_NVDLA_CSC_dl.scala 617:21:@761.4]
  reg [31:0] _RAND_103;
  reg [13:0] c_bias_d1; // @[NV_NVDLA_CSC_dl.scala 618:24:@762.4]
  reg [31:0] _RAND_104;
  reg [13:0] h_bias_0_d1; // @[NV_NVDLA_CSC_dl.scala 619:26:@763.4]
  reg [31:0] _RAND_105;
  reg [13:0] h_bias_1_d1; // @[NV_NVDLA_CSC_dl.scala 620:26:@764.4]
  reg [31:0] _RAND_106;
  reg [13:0] h_bias_2_d1; // @[NV_NVDLA_CSC_dl.scala 621:26:@765.4]
  reg [31:0] _RAND_107;
  reg [13:0] h_bias_3_d1; // @[NV_NVDLA_CSC_dl.scala 622:26:@766.4]
  reg [31:0] _RAND_108;
  reg [13:0] w_bias_d1; // @[NV_NVDLA_CSC_dl.scala 623:24:@767.4]
  reg [31:0] _RAND_109;
  wire  _T_995; // @[NV_NVDLA_CSC_dl.scala 626:32:@768.4]
  wire  _T_996; // @[NV_NVDLA_CSC_dl.scala 626:22:@769.4]
  wire [11:0] _T_997; // @[NV_NVDLA_CSC_dl.scala 626:49:@770.4]
  wire [11:0] c_bias_add; // @[NV_NVDLA_CSC_dl.scala 626:21:@771.4]
  wire  _T_1000; // @[NV_NVDLA_CSC_dl.scala 628:34:@772.4]
  wire [13:0] _GEN_252; // @[NV_NVDLA_CSC_dl.scala 628:64:@773.4]
  wire [14:0] _T_1002; // @[NV_NVDLA_CSC_dl.scala 628:64:@773.4]
  wire [13:0] _T_1003; // @[NV_NVDLA_CSC_dl.scala 628:64:@774.4]
  wire [13:0] _T_1004; // @[NV_NVDLA_CSC_dl.scala 628:19:@775.4]
  wire [13:0] c_bias_w; // @[NV_NVDLA_CSC_dl.scala 627:19:@776.4]
  wire  c_bias_d1_reg_en; // @[NV_NVDLA_CSC_dl.scala 630:31:@780.4]
  wire [27:0] _T_1007; // @[NV_NVDLA_CSC_dl.scala 633:32:@781.4]
  wire [13:0] h_bias_0_w; // @[NV_NVDLA_CSC_dl.scala 633:50:@782.4]
  wire [13:0] _GEN_253; // @[NV_NVDLA_CSC_dl.scala 634:31:@783.4]
  wire [18:0] _T_1008; // @[NV_NVDLA_CSC_dl.scala 634:31:@783.4]
  wire [13:0] h_bias_1_w; // @[NV_NVDLA_CSC_dl.scala 634:49:@784.4]
  wire [14:0] _GEN_254; // @[NV_NVDLA_CSC_dl.scala 635:29:@785.4]
  wire [19:0] _T_1009; // @[NV_NVDLA_CSC_dl.scala 635:29:@785.4]
  wire [13:0] h_bias_2_w; // @[NV_NVDLA_CSC_dl.scala 635:47:@786.4]
  wire [14:0] _GEN_255; // @[NV_NVDLA_CSC_dl.scala 636:47:@787.4]
  wire [16:0] _T_1011; // @[NV_NVDLA_CSC_dl.scala 636:47:@787.4]
  wire [16:0] _T_1012; // @[NV_NVDLA_CSC_dl.scala 636:21:@788.4]
  wire [13:0] h_bias_3_w; // @[NV_NVDLA_CSC_dl.scala 636:65:@789.4]
  wire  _T_1013; // @[NV_NVDLA_CSC_dl.scala 637:45:@790.4]
  wire  _T_1014; // @[NV_NVDLA_CSC_dl.scala 637:34:@791.4]
  wire [1:0] h_bias_reg_en; // @[Cat.scala 30:58:@792.4]
  wire [13:0] dat_req_base_d1; // @[NV_NVDLA_CSC_dl.scala 662:35:@807.4]
  wire [13:0] _GEN_89; // @[NV_NVDLA_CSC_dl.scala 664:20:@808.4]
  wire [13:0] _GEN_90; // @[NV_NVDLA_CSC_dl.scala 667:23:@811.4]
  wire  _T_1029; // @[NV_NVDLA_CSC_dl.scala 670:19:@814.4]
  wire [13:0] _GEN_91; // @[NV_NVDLA_CSC_dl.scala 670:23:@815.4]
  wire [13:0] _GEN_92; // @[NV_NVDLA_CSC_dl.scala 670:23:@815.4]
  wire [13:0] _GEN_93; // @[NV_NVDLA_CSC_dl.scala 670:23:@815.4]
  wire  _T_1030; // @[NV_NVDLA_CSC_dl.scala 675:19:@820.4]
  wire [13:0] _GEN_94; // @[NV_NVDLA_CSC_dl.scala 675:23:@821.4]
  wire [13:0] _GEN_95; // @[NV_NVDLA_CSC_dl.scala 678:20:@824.4]
  reg [13:0] dat_req_sub_h_addr_0; // @[NV_NVDLA_CSC_dl.scala 686:33:@836.4]
  reg [31:0] _RAND_110;
  reg [13:0] dat_req_sub_h_addr_1; // @[NV_NVDLA_CSC_dl.scala 686:33:@836.4]
  reg [31:0] _RAND_111;
  reg [13:0] dat_req_sub_h_addr_2; // @[NV_NVDLA_CSC_dl.scala 686:33:@836.4]
  reg [31:0] _RAND_112;
  reg [13:0] dat_req_sub_h_addr_3; // @[NV_NVDLA_CSC_dl.scala 686:33:@836.4]
  reg [31:0] _RAND_113;
  reg  sc2buf_dat_rd_en_out; // @[NV_NVDLA_CSC_dl.scala 687:35:@837.4]
  reg [31:0] _RAND_114;
  reg [13:0] sc2buf_dat_rd_addr_out; // @[NV_NVDLA_CSC_dl.scala 688:37:@839.4]
  reg [31:0] _RAND_115;
  reg [1:0] dat_req_pipe_sub_w; // @[NV_NVDLA_CSC_dl.scala 691:33:@842.4]
  reg [31:0] _RAND_116;
  reg [1:0] dat_req_pipe_sub_h; // @[NV_NVDLA_CSC_dl.scala 692:33:@843.4]
  reg [31:0] _RAND_117;
  reg  dat_req_pipe_sub_c; // @[NV_NVDLA_CSC_dl.scala 693:33:@844.4]
  reg [31:0] _RAND_118;
  reg  dat_req_pipe_ch_end; // @[NV_NVDLA_CSC_dl.scala 694:34:@845.4]
  reg [31:0] _RAND_119;
  reg [7:0] dat_req_pipe_bytes; // @[NV_NVDLA_CSC_dl.scala 695:33:@846.4]
  reg [31:0] _RAND_120;
  reg  dat_req_pipe_dummy; // @[NV_NVDLA_CSC_dl.scala 696:33:@847.4]
  reg [31:0] _RAND_121;
  reg [1:0] dat_req_pipe_cur_sub_h; // @[NV_NVDLA_CSC_dl.scala 697:37:@848.4]
  reg [31:0] _RAND_122;
  reg  dat_req_pipe_sub_w_st; // @[NV_NVDLA_CSC_dl.scala 698:36:@849.4]
  reg [31:0] _RAND_123;
  reg  dat_req_pipe_rls; // @[NV_NVDLA_CSC_dl.scala 699:31:@850.4]
  reg [31:0] _RAND_124;
  reg [8:0] dat_req_pipe_flag; // @[NV_NVDLA_CSC_dl.scala 700:32:@851.4]
  reg [31:0] _RAND_125;
  wire [14:0] _T_1119; // @[NV_NVDLA_CSC_dl.scala 702:30:@852.4]
  wire [13:0] _T_1120; // @[NV_NVDLA_CSC_dl.scala 702:30:@853.4]
  wire [14:0] _T_1121; // @[NV_NVDLA_CSC_dl.scala 702:44:@854.4]
  wire [13:0] _T_1122; // @[NV_NVDLA_CSC_dl.scala 702:44:@855.4]
  wire [14:0] _T_1123; // @[NV_NVDLA_CSC_dl.scala 702:58:@856.4]
  wire [13:0] h_bias_d1; // @[NV_NVDLA_CSC_dl.scala 702:58:@857.4]
  wire [14:0] _T_1125; // @[NV_NVDLA_CSC_dl.scala 703:41:@859.4]
  wire [14:0] _GEN_256; // @[NV_NVDLA_CSC_dl.scala 703:54:@860.4]
  wire [15:0] _T_1126; // @[NV_NVDLA_CSC_dl.scala 703:54:@860.4]
  wire [15:0] _GEN_257; // @[NV_NVDLA_CSC_dl.scala 703:67:@861.4]
  wire [16:0] _T_1127; // @[NV_NVDLA_CSC_dl.scala 703:67:@861.4]
  wire [14:0] dat_req_addr_sum; // @[NV_NVDLA_CSC_dl.scala 703:80:@862.4]
  wire  is_dat_req_addr_wrap; // @[NV_NVDLA_CSC_dl.scala 704:45:@865.4]
  wire [15:0] _T_1140; // @[NV_NVDLA_CSC_dl.scala 705:43:@868.4]
  wire [15:0] _T_1141; // @[NV_NVDLA_CSC_dl.scala 705:43:@869.4]
  wire [13:0] dat_req_addr_wrap; // @[NV_NVDLA_CSC_dl.scala 705:103:@870.4]
  wire  _T_1142; // @[NV_NVDLA_CSC_dl.scala 706:35:@871.4]
  wire [14:0] _T_1148; // @[NV_NVDLA_CSC_dl.scala 707:25:@873.4]
  wire [14:0] _T_1149; // @[NV_NVDLA_CSC_dl.scala 706:25:@874.4]
  wire [13:0] dat_req_addr_w; // @[NV_NVDLA_CSC_dl.scala 707:85:@875.4]
  wire  _T_1169; // @[Mux.scala 46:19:@885.4]
  wire [13:0] _T_1170; // @[Mux.scala 46:16:@886.4]
  wire  _T_1171; // @[Mux.scala 46:19:@887.4]
  wire [13:0] _T_1172; // @[Mux.scala 46:16:@888.4]
  wire  _T_1173; // @[Mux.scala 46:19:@889.4]
  wire [13:0] _T_1174; // @[Mux.scala 46:16:@890.4]
  wire  _T_1175; // @[Mux.scala 46:19:@891.4]
  wire [13:0] dat_req_addr_last; // @[Mux.scala 46:16:@892.4]
  wire  _T_1176; // @[NV_NVDLA_CSC_dl.scala 714:65:@893.4]
  wire  _T_1177; // @[NV_NVDLA_CSC_dl.scala 714:85:@894.4]
  wire  sc2buf_dat_rd_en_w; // @[NV_NVDLA_CSC_dl.scala 714:43:@895.4]
  wire  _T_1178; // @[NV_NVDLA_CSC_dl.scala 716:38:@896.4]
  wire  _T_1180; // @[NV_NVDLA_CSC_dl.scala 716:78:@897.4]
  wire  _T_1181; // @[NV_NVDLA_CSC_dl.scala 716:58:@898.4]
  wire  dat_req_sub_h_addr_en_0; // @[NV_NVDLA_CSC_dl.scala 716:17:@899.4]
  wire  _T_1185; // @[NV_NVDLA_CSC_dl.scala 716:78:@901.4]
  wire  _T_1186; // @[NV_NVDLA_CSC_dl.scala 716:58:@902.4]
  wire  dat_req_sub_h_addr_en_1; // @[NV_NVDLA_CSC_dl.scala 716:17:@903.4]
  wire  _T_1190; // @[NV_NVDLA_CSC_dl.scala 716:78:@905.4]
  wire  _T_1191; // @[NV_NVDLA_CSC_dl.scala 716:58:@906.4]
  wire  dat_req_sub_h_addr_en_2; // @[NV_NVDLA_CSC_dl.scala 716:17:@907.4]
  wire  _T_1195; // @[NV_NVDLA_CSC_dl.scala 716:78:@909.4]
  wire  _T_1196; // @[NV_NVDLA_CSC_dl.scala 716:58:@910.4]
  wire  dat_req_sub_h_addr_en_3; // @[NV_NVDLA_CSC_dl.scala 716:17:@911.4]
  wire [13:0] _GEN_96; // @[NV_NVDLA_CSC_dl.scala 723:35:@917.4]
  wire [13:0] _GEN_97; // @[NV_NVDLA_CSC_dl.scala 723:35:@920.4]
  wire [13:0] _GEN_98; // @[NV_NVDLA_CSC_dl.scala 723:35:@923.4]
  wire [13:0] _GEN_99; // @[NV_NVDLA_CSC_dl.scala 723:35:@926.4]
  wire  _T_1207; // @[NV_NVDLA_CSC_dl.scala 729:14:@930.4]
  wire [13:0] _GEN_100; // @[NV_NVDLA_CSC_dl.scala 729:34:@931.4]
  reg  dat_req_pipe_pvld; // @[NV_NVDLA_CSC_dl.scala 734:29:@936.4]
  reg [31:0] _RAND_126;
  reg  dat_req_exec_pvld; // @[NV_NVDLA_CSC_dl.scala 735:29:@939.4]
  reg [31:0] _RAND_127;
  wire [1:0] _GEN_101; // @[NV_NVDLA_CSC_dl.scala 736:24:@942.4]
  wire [1:0] _GEN_102; // @[NV_NVDLA_CSC_dl.scala 736:24:@942.4]
  wire  _GEN_103; // @[NV_NVDLA_CSC_dl.scala 736:24:@942.4]
  wire  _GEN_104; // @[NV_NVDLA_CSC_dl.scala 736:24:@942.4]
  wire [7:0] _GEN_105; // @[NV_NVDLA_CSC_dl.scala 736:24:@942.4]
  wire  _GEN_106; // @[NV_NVDLA_CSC_dl.scala 736:24:@942.4]
  wire [1:0] _GEN_107; // @[NV_NVDLA_CSC_dl.scala 736:24:@942.4]
  wire  _GEN_108; // @[NV_NVDLA_CSC_dl.scala 736:24:@942.4]
  wire  _GEN_109; // @[NV_NVDLA_CSC_dl.scala 736:24:@942.4]
  wire [8:0] _GEN_110; // @[NV_NVDLA_CSC_dl.scala 736:24:@942.4]
  wire [6:0] _T_1223; // @[Cat.scala 30:58:@962.4]
  wire [28:0] dat_req_pipe_pd; // @[Cat.scala 30:58:@968.4]
  reg  _T_1233; // @[NV_NVDLA_CSC_dl.scala 763:73:@970.4]
  reg [31:0] _RAND_128;
  reg  _T_1236; // @[NV_NVDLA_CSC_dl.scala 763:73:@971.4]
  reg [31:0] _RAND_129;
  reg  _T_1239; // @[NV_NVDLA_CSC_dl.scala 763:73:@972.4]
  reg [31:0] _RAND_130;
  reg  _T_1242; // @[NV_NVDLA_CSC_dl.scala 763:73:@973.4]
  reg [31:0] _RAND_131;
  reg  _T_1245; // @[NV_NVDLA_CSC_dl.scala 763:73:@974.4]
  reg [31:0] _RAND_132;
  reg  dat_rsp_pipe_pvld; // @[NV_NVDLA_CSC_dl.scala 763:73:@975.4]
  reg [31:0] _RAND_133;
  reg [28:0] _T_1252; // @[NV_NVDLA_CSC_dl.scala 765:71:@977.4]
  reg [31:0] _RAND_134;
  reg [28:0] _T_1255; // @[NV_NVDLA_CSC_dl.scala 765:71:@978.4]
  reg [31:0] _RAND_135;
  reg [28:0] _T_1258; // @[NV_NVDLA_CSC_dl.scala 765:71:@979.4]
  reg [31:0] _RAND_136;
  reg [28:0] _T_1261; // @[NV_NVDLA_CSC_dl.scala 765:71:@980.4]
  reg [31:0] _RAND_137;
  reg [28:0] _T_1264; // @[NV_NVDLA_CSC_dl.scala 765:71:@981.4]
  reg [31:0] _RAND_138;
  reg [28:0] dat_rsp_pipe_pd; // @[NV_NVDLA_CSC_dl.scala 765:71:@982.4]
  reg [31:0] _RAND_139;
  reg  _T_1271; // @[NV_NVDLA_CSC_dl.scala 767:73:@984.4]
  reg [31:0] _RAND_140;
  reg  _T_1274; // @[NV_NVDLA_CSC_dl.scala 767:73:@985.4]
  reg [31:0] _RAND_141;
  reg  _T_1277; // @[NV_NVDLA_CSC_dl.scala 767:73:@986.4]
  reg [31:0] _RAND_142;
  reg  _T_1280; // @[NV_NVDLA_CSC_dl.scala 767:73:@987.4]
  reg [31:0] _RAND_143;
  reg  _T_1283; // @[NV_NVDLA_CSC_dl.scala 767:73:@988.4]
  reg [31:0] _RAND_144;
  reg  dat_rsp_exec_pvld; // @[NV_NVDLA_CSC_dl.scala 767:73:@989.4]
  reg [31:0] _RAND_145;
  reg  _T_1290; // @[NV_NVDLA_CSC_dl.scala 769:73:@991.4]
  reg [31:0] _RAND_146;
  reg  _T_1293; // @[NV_NVDLA_CSC_dl.scala 769:73:@992.4]
  reg [31:0] _RAND_147;
  reg  _T_1296; // @[NV_NVDLA_CSC_dl.scala 769:73:@993.4]
  reg [31:0] _RAND_148;
  reg  _T_1299; // @[NV_NVDLA_CSC_dl.scala 769:73:@994.4]
  reg [31:0] _RAND_149;
  reg  _T_1302; // @[NV_NVDLA_CSC_dl.scala 769:73:@995.4]
  reg [31:0] _RAND_150;
  reg  dat_rsp_exec_dummy; // @[NV_NVDLA_CSC_dl.scala 769:73:@996.4]
  reg [31:0] _RAND_151;
  reg [1:0] _T_1309; // @[NV_NVDLA_CSC_dl.scala 771:74:@998.4]
  reg [31:0] _RAND_152;
  reg [1:0] _T_1312; // @[NV_NVDLA_CSC_dl.scala 771:74:@999.4]
  reg [31:0] _RAND_153;
  reg [1:0] _T_1315; // @[NV_NVDLA_CSC_dl.scala 771:74:@1000.4]
  reg [31:0] _RAND_154;
  reg [1:0] _T_1318; // @[NV_NVDLA_CSC_dl.scala 771:74:@1001.4]
  reg [31:0] _RAND_155;
  reg [1:0] _T_1321; // @[NV_NVDLA_CSC_dl.scala 771:74:@1002.4]
  reg [31:0] _RAND_156;
  reg [1:0] dat_rsp_exec_sub_h; // @[NV_NVDLA_CSC_dl.scala 771:74:@1003.4]
  reg [31:0] _RAND_157;
  wire [28:0] _GEN_111; // @[NV_NVDLA_CSC_dl.scala 781:33:@1010.4]
  wire  _GEN_112; // @[NV_NVDLA_CSC_dl.scala 785:33:@1014.4]
  wire [1:0] _GEN_113; // @[NV_NVDLA_CSC_dl.scala 785:33:@1014.4]
  wire [28:0] _GEN_114; // @[NV_NVDLA_CSC_dl.scala 781:33:@1019.4]
  wire  _GEN_115; // @[NV_NVDLA_CSC_dl.scala 785:33:@1023.4]
  wire [1:0] _GEN_116; // @[NV_NVDLA_CSC_dl.scala 785:33:@1023.4]
  wire [28:0] _GEN_117; // @[NV_NVDLA_CSC_dl.scala 781:33:@1028.4]
  wire  _GEN_118; // @[NV_NVDLA_CSC_dl.scala 785:33:@1032.4]
  wire [1:0] _GEN_119; // @[NV_NVDLA_CSC_dl.scala 785:33:@1032.4]
  wire [28:0] _GEN_120; // @[NV_NVDLA_CSC_dl.scala 781:33:@1037.4]
  wire  _GEN_121; // @[NV_NVDLA_CSC_dl.scala 785:33:@1041.4]
  wire [1:0] _GEN_122; // @[NV_NVDLA_CSC_dl.scala 785:33:@1041.4]
  wire [28:0] _GEN_123; // @[NV_NVDLA_CSC_dl.scala 781:33:@1046.4]
  wire  _GEN_124; // @[NV_NVDLA_CSC_dl.scala 785:33:@1050.4]
  wire [1:0] _GEN_125; // @[NV_NVDLA_CSC_dl.scala 785:33:@1050.4]
  wire [28:0] _GEN_126; // @[NV_NVDLA_CSC_dl.scala 781:33:@1055.4]
  wire  _GEN_127; // @[NV_NVDLA_CSC_dl.scala 785:33:@1059.4]
  wire [1:0] _GEN_128; // @[NV_NVDLA_CSC_dl.scala 785:33:@1059.4]
  wire [1:0] dat_rsp_pipe_sub_w; // @[NV_NVDLA_CSC_dl.scala 798:41:@1063.4]
  wire [1:0] dat_rsp_pipe_sub_h; // @[NV_NVDLA_CSC_dl.scala 799:41:@1064.4]
  wire  dat_rsp_pipe_sub_c; // @[NV_NVDLA_CSC_dl.scala 800:41:@1065.4]
  wire  dat_rsp_pipe_ch_end; // @[NV_NVDLA_CSC_dl.scala 801:42:@1066.4]
  wire [7:0] dat_rsp_pipe_bytes; // @[NV_NVDLA_CSC_dl.scala 802:41:@1067.4]
  wire [1:0] dat_rsp_pipe_cur_sub_h; // @[NV_NVDLA_CSC_dl.scala 803:45:@1068.4]
  wire  dat_rsp_pipe_rls; // @[NV_NVDLA_CSC_dl.scala 806:39:@1071.4]
  wire [8:0] dat_rsp_pipe_flag; // @[NV_NVDLA_CSC_dl.scala 807:40:@1072.4]
  reg  dat_l0c0_dummy; // @[NV_NVDLA_CSC_dl.scala 812:29:@1073.4]
  reg [31:0] _RAND_158;
  reg  dat_l1c0_dummy; // @[NV_NVDLA_CSC_dl.scala 813:29:@1074.4]
  reg [31:0] _RAND_159;
  reg  dat_l2c0_dummy; // @[NV_NVDLA_CSC_dl.scala 814:29:@1075.4]
  reg [31:0] _RAND_160;
  reg  dat_l3c0_dummy; // @[NV_NVDLA_CSC_dl.scala 815:29:@1076.4]
  reg [31:0] _RAND_161;
  reg  dat_l0c1_dummy; // @[NV_NVDLA_CSC_dl.scala 816:29:@1077.4]
  reg [31:0] _RAND_162;
  reg  dat_l1c1_dummy; // @[NV_NVDLA_CSC_dl.scala 817:29:@1078.4]
  reg [31:0] _RAND_163;
  reg  dat_l2c1_dummy; // @[NV_NVDLA_CSC_dl.scala 818:29:@1079.4]
  reg [31:0] _RAND_164;
  reg  dat_l3c1_dummy; // @[NV_NVDLA_CSC_dl.scala 819:29:@1080.4]
  reg [31:0] _RAND_165;
  reg [63:0] dat_l0c0; // @[NV_NVDLA_CSC_dl.scala 821:19:@1081.4]
  reg [63:0] _RAND_166;
  reg [63:0] dat_l1c0; // @[NV_NVDLA_CSC_dl.scala 822:19:@1082.4]
  reg [63:0] _RAND_167;
  reg [63:0] dat_l2c0; // @[NV_NVDLA_CSC_dl.scala 823:19:@1083.4]
  reg [63:0] _RAND_168;
  reg [63:0] dat_l3c0; // @[NV_NVDLA_CSC_dl.scala 824:19:@1084.4]
  reg [63:0] _RAND_169;
  reg [63:0] dat_l0c1; // @[NV_NVDLA_CSC_dl.scala 825:19:@1085.4]
  reg [63:0] _RAND_170;
  reg [63:0] dat_l1c1; // @[NV_NVDLA_CSC_dl.scala 826:19:@1086.4]
  reg [63:0] _RAND_171;
  reg [63:0] dat_l2c1; // @[NV_NVDLA_CSC_dl.scala 827:19:@1087.4]
  reg [63:0] _RAND_172;
  reg [63:0] dat_l3c1; // @[NV_NVDLA_CSC_dl.scala 828:19:@1088.4]
  reg [63:0] _RAND_173;
  wire  _T_1349; // @[NV_NVDLA_CSC_dl.scala 830:70:@1089.4]
  wire  dat_l0c0_en; // @[NV_NVDLA_CSC_dl.scala 830:48:@1090.4]
  wire  _T_1351; // @[NV_NVDLA_CSC_dl.scala 831:70:@1091.4]
  wire  dat_l1c0_en; // @[NV_NVDLA_CSC_dl.scala 831:48:@1092.4]
  wire  _T_1353; // @[NV_NVDLA_CSC_dl.scala 832:70:@1093.4]
  wire  dat_l2c0_en; // @[NV_NVDLA_CSC_dl.scala 832:48:@1094.4]
  wire  _T_1355; // @[NV_NVDLA_CSC_dl.scala 833:70:@1095.4]
  wire  dat_l3c0_en; // @[NV_NVDLA_CSC_dl.scala 833:48:@1096.4]
  wire  _T_1359; // @[NV_NVDLA_CSC_dl.scala 837:69:@1100.4]
  wire  _T_1360; // @[NV_NVDLA_CSC_dl.scala 837:74:@1101.4]
  wire  _T_1361; // @[NV_NVDLA_CSC_dl.scala 837:90:@1102.4]
  wire  dat_l0c1_en; // @[NV_NVDLA_CSC_dl.scala 837:88:@1103.4]
  wire  _T_1365; // @[NV_NVDLA_CSC_dl.scala 838:68:@1107.4]
  wire  _T_1366; // @[NV_NVDLA_CSC_dl.scala 838:73:@1108.4]
  wire  _T_1367; // @[NV_NVDLA_CSC_dl.scala 838:89:@1109.4]
  wire  dat_l1c1_en; // @[NV_NVDLA_CSC_dl.scala 838:87:@1110.4]
  wire  _T_1369; // @[NV_NVDLA_CSC_dl.scala 839:29:@1112.4]
  wire  _T_1370; // @[NV_NVDLA_CSC_dl.scala 839:34:@1113.4]
  wire  _T_1371; // @[NV_NVDLA_CSC_dl.scala 839:50:@1114.4]
  wire  dat_l2c1_en; // @[NV_NVDLA_CSC_dl.scala 839:48:@1115.4]
  wire  _T_1372; // @[NV_NVDLA_CSC_dl.scala 840:29:@1116.4]
  wire  _T_1373; // @[NV_NVDLA_CSC_dl.scala 840:34:@1117.4]
  wire  _T_1374; // @[NV_NVDLA_CSC_dl.scala 840:50:@1118.4]
  wire  dat_l3c1_en; // @[NV_NVDLA_CSC_dl.scala 840:48:@1119.4]
  wire  _T_1375; // @[NV_NVDLA_CSC_dl.scala 842:41:@1120.4]
  wire  dat_dummy_l0_en; // @[NV_NVDLA_CSC_dl.scala 842:62:@1122.4]
  wire  dat_dummy_l1_en; // @[NV_NVDLA_CSC_dl.scala 843:62:@1125.4]
  wire  dat_dummy_l2_en; // @[NV_NVDLA_CSC_dl.scala 844:62:@1128.4]
  wire  dat_dummy_l3_en; // @[NV_NVDLA_CSC_dl.scala 845:62:@1131.4]
  wire  dat_l0_set; // @[NV_NVDLA_CSC_dl.scala 847:30:@1132.4]
  wire  dat_l1_set; // @[NV_NVDLA_CSC_dl.scala 848:30:@1133.4]
  wire  dat_l2_set; // @[NV_NVDLA_CSC_dl.scala 849:30:@1134.4]
  wire  dat_l3_set; // @[NV_NVDLA_CSC_dl.scala 850:30:@1135.4]
  wire  _T_1389; // @[NV_NVDLA_CSC_dl.scala 852:48:@1136.4]
  wire  _T_1390; // @[NV_NVDLA_CSC_dl.scala 852:22:@1137.4]
  wire  _T_1393; // @[NV_NVDLA_CSC_dl.scala 853:48:@1139.4]
  wire  _T_1394; // @[NV_NVDLA_CSC_dl.scala 853:22:@1140.4]
  wire  _T_1397; // @[NV_NVDLA_CSC_dl.scala 854:48:@1142.4]
  wire  _T_1398; // @[NV_NVDLA_CSC_dl.scala 854:22:@1143.4]
  wire  _T_1401; // @[NV_NVDLA_CSC_dl.scala 855:48:@1145.4]
  wire  _T_1402; // @[NV_NVDLA_CSC_dl.scala 855:22:@1146.4]
  wire  _T_1404; // @[NV_NVDLA_CSC_dl.scala 856:48:@1148.4]
  wire  _T_1405; // @[NV_NVDLA_CSC_dl.scala 856:22:@1149.4]
  wire  _T_1408; // @[NV_NVDLA_CSC_dl.scala 857:77:@1151.4]
  wire  _T_1409; // @[NV_NVDLA_CSC_dl.scala 857:60:@1152.4]
  wire  _T_1410; // @[NV_NVDLA_CSC_dl.scala 857:48:@1153.4]
  wire  _T_1411; // @[NV_NVDLA_CSC_dl.scala 857:22:@1154.4]
  wire  _T_1413; // @[NV_NVDLA_CSC_dl.scala 858:76:@1156.4]
  wire  _T_1414; // @[NV_NVDLA_CSC_dl.scala 858:60:@1157.4]
  wire  _T_1415; // @[NV_NVDLA_CSC_dl.scala 858:48:@1158.4]
  wire  _T_1416; // @[NV_NVDLA_CSC_dl.scala 858:22:@1159.4]
  wire  _T_1419; // @[NV_NVDLA_CSC_dl.scala 859:60:@1162.4]
  wire  _T_1420; // @[NV_NVDLA_CSC_dl.scala 859:48:@1163.4]
  wire  _T_1421; // @[NV_NVDLA_CSC_dl.scala 859:22:@1164.4]
  reg [7:0] rsp_sft_cnt_l0; // @[NV_NVDLA_CSC_dl.scala 873:29:@1190.4]
  reg [31:0] _RAND_174;
  reg [7:0] rsp_sft_cnt_l1; // @[NV_NVDLA_CSC_dl.scala 874:29:@1191.4]
  reg [31:0] _RAND_175;
  reg [7:0] rsp_sft_cnt_l2; // @[NV_NVDLA_CSC_dl.scala 875:29:@1192.4]
  reg [31:0] _RAND_176;
  reg [7:0] rsp_sft_cnt_l3; // @[NV_NVDLA_CSC_dl.scala 876:29:@1193.4]
  reg [31:0] _RAND_177;
  reg [7:0] rsp_sft_cnt_l0_ori; // @[NV_NVDLA_CSC_dl.scala 877:33:@1194.4]
  reg [31:0] _RAND_178;
  reg [7:0] rsp_sft_cnt_l1_ori; // @[NV_NVDLA_CSC_dl.scala 878:33:@1195.4]
  reg [31:0] _RAND_179;
  reg [7:0] rsp_sft_cnt_l2_ori; // @[NV_NVDLA_CSC_dl.scala 879:33:@1196.4]
  reg [31:0] _RAND_180;
  reg [7:0] rsp_sft_cnt_l3_ori; // @[NV_NVDLA_CSC_dl.scala 880:33:@1197.4]
  reg [31:0] _RAND_181;
  reg  dat_rsp_l2_pvld; // @[NV_NVDLA_CSC_dl.scala 885:41:@1201.4]
  reg [31:0] _RAND_182;
  reg [26:0] _T_1458; // @[NV_NVDLA_CSC_dl.scala 887:41:@1206.4]
  reg [31:0] _RAND_183;
  wire [26:0] _T_1470; // @[Cat.scala 30:58:@1216.4]
  wire [26:0] _GEN_137; // @[NV_NVDLA_CSC_dl.scala 895:28:@1219.4]
  wire [26:0] _GEN_138; // @[NV_NVDLA_CSC_dl.scala 895:28:@1223.4]
  wire [26:0] _GEN_139; // @[NV_NVDLA_CSC_dl.scala 895:28:@1227.4]
  wire [26:0] _GEN_140; // @[NV_NVDLA_CSC_dl.scala 895:28:@1231.4]
  wire  dat_rsp_l0_sub_c; // @[NV_NVDLA_CSC_dl.scala 913:39:@1257.4]
  wire  dat_rsp_l1_sub_c; // @[NV_NVDLA_CSC_dl.scala 914:39:@1258.4]
  wire  dat_rsp_l2_sub_c; // @[NV_NVDLA_CSC_dl.scala 915:39:@1259.4]
  wire  dat_rsp_l3_sub_c; // @[NV_NVDLA_CSC_dl.scala 916:39:@1260.4]
  wire [8:0] dat_rsp_l0_flag; // @[NV_NVDLA_CSC_dl.scala 918:38:@1261.4]
  wire [8:0] dat_rsp_l1_flag; // @[NV_NVDLA_CSC_dl.scala 919:38:@1262.4]
  wire [8:0] dat_rsp_l2_flag; // @[NV_NVDLA_CSC_dl.scala 920:38:@1263.4]
  wire [8:0] dat_rsp_l3_flag; // @[NV_NVDLA_CSC_dl.scala 921:38:@1264.4]
  wire  dat_rsp_l0_stripe_end; // @[NV_NVDLA_CSC_dl.scala 923:44:@1265.4]
  wire  dat_rsp_l1_stripe_end; // @[NV_NVDLA_CSC_dl.scala 924:44:@1266.4]
  wire  dat_rsp_l2_stripe_end; // @[NV_NVDLA_CSC_dl.scala 925:44:@1267.4]
  wire  dat_rsp_l3_stripe_end; // @[NV_NVDLA_CSC_dl.scala 926:44:@1268.4]
  wire [7:0] dat_rsp_bytes; // @[NV_NVDLA_CSC_dl.scala 933:31:@1273.4]
  wire [1:0] dat_rsp_cur_sub_h; // @[NV_NVDLA_CSC_dl.scala 934:35:@1274.4]
  wire [8:0] dat_rsp_flag; // @[NV_NVDLA_CSC_dl.scala 936:30:@1277.4]
  wire [7:0] rsp_sft_cnt_l0_sub; // @[NV_NVDLA_CSC_dl.scala 945:29:@1283.4]
  wire [7:0] rsp_sft_cnt_l1_sub; // @[NV_NVDLA_CSC_dl.scala 946:29:@1284.4]
  wire [7:0] rsp_sft_cnt_l2_sub; // @[NV_NVDLA_CSC_dl.scala 947:29:@1285.4]
  wire [7:0] rsp_sft_cnt_l3_sub; // @[NV_NVDLA_CSC_dl.scala 948:29:@1286.4]
  wire  _T_1508; // @[NV_NVDLA_CSC_dl.scala 950:50:@1287.4]
  wire [7:0] _GEN_260; // @[NV_NVDLA_CSC_dl.scala 950:111:@1288.4]
  wire [8:0] _T_1510; // @[NV_NVDLA_CSC_dl.scala 950:111:@1288.4]
  wire [7:0] _T_1511; // @[NV_NVDLA_CSC_dl.scala 950:111:@1289.4]
  wire [8:0] _T_1512; // @[NV_NVDLA_CSC_dl.scala 950:133:@1290.4]
  wire [8:0] _T_1513; // @[NV_NVDLA_CSC_dl.scala 950:133:@1291.4]
  wire [7:0] _T_1514; // @[NV_NVDLA_CSC_dl.scala 950:133:@1292.4]
  wire [7:0] rsp_sft_cnt_l0_inc; // @[NV_NVDLA_CSC_dl.scala 950:29:@1293.4]
  wire [8:0] _T_1518; // @[NV_NVDLA_CSC_dl.scala 951:111:@1295.4]
  wire [7:0] _T_1519; // @[NV_NVDLA_CSC_dl.scala 951:111:@1296.4]
  wire [8:0] _T_1520; // @[NV_NVDLA_CSC_dl.scala 951:133:@1297.4]
  wire [8:0] _T_1521; // @[NV_NVDLA_CSC_dl.scala 951:133:@1298.4]
  wire [7:0] _T_1522; // @[NV_NVDLA_CSC_dl.scala 951:133:@1299.4]
  wire [7:0] rsp_sft_cnt_l1_inc; // @[NV_NVDLA_CSC_dl.scala 951:29:@1300.4]
  wire [8:0] _T_1526; // @[NV_NVDLA_CSC_dl.scala 952:111:@1302.4]
  wire [7:0] _T_1527; // @[NV_NVDLA_CSC_dl.scala 952:111:@1303.4]
  wire [8:0] _T_1528; // @[NV_NVDLA_CSC_dl.scala 952:133:@1304.4]
  wire [8:0] _T_1529; // @[NV_NVDLA_CSC_dl.scala 952:133:@1305.4]
  wire [7:0] _T_1530; // @[NV_NVDLA_CSC_dl.scala 952:133:@1306.4]
  wire [7:0] rsp_sft_cnt_l2_inc; // @[NV_NVDLA_CSC_dl.scala 952:29:@1307.4]
  wire [8:0] _T_1534; // @[NV_NVDLA_CSC_dl.scala 953:111:@1309.4]
  wire [7:0] _T_1535; // @[NV_NVDLA_CSC_dl.scala 953:111:@1310.4]
  wire [8:0] _T_1536; // @[NV_NVDLA_CSC_dl.scala 953:133:@1311.4]
  wire [8:0] _T_1537; // @[NV_NVDLA_CSC_dl.scala 953:133:@1312.4]
  wire [7:0] _T_1538; // @[NV_NVDLA_CSC_dl.scala 953:133:@1313.4]
  wire [7:0] rsp_sft_cnt_l3_inc; // @[NV_NVDLA_CSC_dl.scala 953:29:@1314.4]
  wire  _T_1540; // @[NV_NVDLA_CSC_dl.scala 962:52:@1315.4]
  wire  _T_1541; // @[NV_NVDLA_CSC_dl.scala 962:50:@1316.4]
  wire  _T_1542; // @[NV_NVDLA_CSC_dl.scala 963:50:@1317.4]
  wire [7:0] _T_1545; // @[NV_NVDLA_CSC_dl.scala 964:64:@1318.4]
  wire [7:0] _T_1546; // @[NV_NVDLA_CSC_dl.scala 964:27:@1319.4]
  wire [7:0] _T_1547; // @[NV_NVDLA_CSC_dl.scala 963:27:@1320.4]
  wire [7:0] _T_1548; // @[NV_NVDLA_CSC_dl.scala 962:27:@1321.4]
  wire [7:0] rsp_sft_cnt_l0_w; // @[NV_NVDLA_CSC_dl.scala 961:27:@1322.4]
  wire  _T_1550; // @[NV_NVDLA_CSC_dl.scala 967:52:@1323.4]
  wire  _T_1551; // @[NV_NVDLA_CSC_dl.scala 967:50:@1324.4]
  wire  _T_1552; // @[NV_NVDLA_CSC_dl.scala 968:50:@1325.4]
  wire [7:0] _T_1555; // @[NV_NVDLA_CSC_dl.scala 969:64:@1326.4]
  wire [7:0] _T_1556; // @[NV_NVDLA_CSC_dl.scala 969:27:@1327.4]
  wire [7:0] _T_1557; // @[NV_NVDLA_CSC_dl.scala 968:27:@1328.4]
  wire [7:0] _T_1558; // @[NV_NVDLA_CSC_dl.scala 967:27:@1329.4]
  wire [7:0] rsp_sft_cnt_l1_w; // @[NV_NVDLA_CSC_dl.scala 966:27:@1330.4]
  wire  _T_1560; // @[NV_NVDLA_CSC_dl.scala 972:52:@1331.4]
  wire  _T_1561; // @[NV_NVDLA_CSC_dl.scala 972:50:@1332.4]
  wire  _T_1562; // @[NV_NVDLA_CSC_dl.scala 973:50:@1333.4]
  wire [7:0] _T_1565; // @[NV_NVDLA_CSC_dl.scala 974:64:@1334.4]
  wire [7:0] _T_1566; // @[NV_NVDLA_CSC_dl.scala 974:27:@1335.4]
  wire [7:0] _T_1567; // @[NV_NVDLA_CSC_dl.scala 973:27:@1336.4]
  wire [7:0] _T_1568; // @[NV_NVDLA_CSC_dl.scala 972:27:@1337.4]
  wire [7:0] rsp_sft_cnt_l2_w; // @[NV_NVDLA_CSC_dl.scala 971:27:@1338.4]
  wire  _T_1570; // @[NV_NVDLA_CSC_dl.scala 977:52:@1339.4]
  wire  _T_1571; // @[NV_NVDLA_CSC_dl.scala 977:50:@1340.4]
  wire  _T_1572; // @[NV_NVDLA_CSC_dl.scala 978:50:@1341.4]
  wire [7:0] _T_1575; // @[NV_NVDLA_CSC_dl.scala 979:64:@1342.4]
  wire [7:0] _T_1576; // @[NV_NVDLA_CSC_dl.scala 979:27:@1343.4]
  wire [7:0] _T_1577; // @[NV_NVDLA_CSC_dl.scala 978:27:@1344.4]
  wire [7:0] _T_1578; // @[NV_NVDLA_CSC_dl.scala 977:27:@1345.4]
  wire [7:0] rsp_sft_cnt_l3_w; // @[NV_NVDLA_CSC_dl.scala 976:27:@1346.4]
  wire  _T_1579; // @[NV_NVDLA_CSC_dl.scala 982:46:@1347.4]
  wire  _T_1580; // @[NV_NVDLA_CSC_dl.scala 982:51:@1348.4]
  wire  rsp_sft_cnt_l0_en; // @[NV_NVDLA_CSC_dl.scala 982:34:@1349.4]
  wire  _T_1581; // @[NV_NVDLA_CSC_dl.scala 983:46:@1350.4]
  wire  _T_1582; // @[NV_NVDLA_CSC_dl.scala 983:51:@1351.4]
  wire  _T_1584; // @[NV_NVDLA_CSC_dl.scala 983:87:@1352.4]
  wire  _T_1585; // @[NV_NVDLA_CSC_dl.scala 983:69:@1353.4]
  wire  rsp_sft_cnt_l1_en; // @[NV_NVDLA_CSC_dl.scala 983:34:@1354.4]
  wire  _T_1586; // @[NV_NVDLA_CSC_dl.scala 984:46:@1355.4]
  wire  _T_1587; // @[NV_NVDLA_CSC_dl.scala 984:51:@1356.4]
  wire  _T_1589; // @[NV_NVDLA_CSC_dl.scala 984:87:@1357.4]
  wire  _T_1590; // @[NV_NVDLA_CSC_dl.scala 984:69:@1358.4]
  wire  rsp_sft_cnt_l2_en; // @[NV_NVDLA_CSC_dl.scala 984:34:@1359.4]
  wire  _T_1591; // @[NV_NVDLA_CSC_dl.scala 985:46:@1360.4]
  wire  _T_1592; // @[NV_NVDLA_CSC_dl.scala 985:51:@1361.4]
  wire  _T_1595; // @[NV_NVDLA_CSC_dl.scala 985:69:@1363.4]
  wire  rsp_sft_cnt_l3_en; // @[NV_NVDLA_CSC_dl.scala 985:34:@1364.4]
  wire  _T_1596; // @[NV_NVDLA_CSC_dl.scala 987:50:@1365.4]
  wire  _T_1597; // @[NV_NVDLA_CSC_dl.scala 987:55:@1366.4]
  wire  _T_1598; // @[NV_NVDLA_CSC_dl.scala 987:73:@1367.4]
  wire  _T_1599; // @[NV_NVDLA_CSC_dl.scala 987:97:@1368.4]
  wire  rsp_sft_cnt_l0_ori_en; // @[NV_NVDLA_CSC_dl.scala 987:38:@1369.4]
  wire  _T_1600; // @[NV_NVDLA_CSC_dl.scala 988:50:@1370.4]
  wire  _T_1601; // @[NV_NVDLA_CSC_dl.scala 988:55:@1371.4]
  wire  _T_1602; // @[NV_NVDLA_CSC_dl.scala 988:73:@1372.4]
  wire  _T_1603; // @[NV_NVDLA_CSC_dl.scala 988:97:@1373.4]
  wire  _T_1605; // @[NV_NVDLA_CSC_dl.scala 988:138:@1374.4]
  wire  _T_1606; // @[NV_NVDLA_CSC_dl.scala 988:120:@1375.4]
  wire  rsp_sft_cnt_l1_ori_en; // @[NV_NVDLA_CSC_dl.scala 988:38:@1376.4]
  wire  _T_1607; // @[NV_NVDLA_CSC_dl.scala 989:50:@1377.4]
  wire  _T_1608; // @[NV_NVDLA_CSC_dl.scala 989:55:@1378.4]
  wire  _T_1609; // @[NV_NVDLA_CSC_dl.scala 989:73:@1379.4]
  wire  _T_1610; // @[NV_NVDLA_CSC_dl.scala 989:97:@1380.4]
  wire  _T_1612; // @[NV_NVDLA_CSC_dl.scala 989:138:@1381.4]
  wire  _T_1613; // @[NV_NVDLA_CSC_dl.scala 989:120:@1382.4]
  wire  rsp_sft_cnt_l2_ori_en; // @[NV_NVDLA_CSC_dl.scala 989:38:@1383.4]
  wire  _T_1614; // @[NV_NVDLA_CSC_dl.scala 990:50:@1384.4]
  wire  _T_1615; // @[NV_NVDLA_CSC_dl.scala 990:55:@1385.4]
  wire  _T_1616; // @[NV_NVDLA_CSC_dl.scala 990:73:@1386.4]
  wire  _T_1617; // @[NV_NVDLA_CSC_dl.scala 990:97:@1387.4]
  wire  _T_1620; // @[NV_NVDLA_CSC_dl.scala 990:120:@1389.4]
  wire  rsp_sft_cnt_l3_ori_en; // @[NV_NVDLA_CSC_dl.scala 990:38:@1390.4]
  wire [7:0] _GEN_141; // @[NV_NVDLA_CSC_dl.scala 992:24:@1391.4]
  wire [7:0] _GEN_142; // @[NV_NVDLA_CSC_dl.scala 993:24:@1394.4]
  wire [7:0] _GEN_143; // @[NV_NVDLA_CSC_dl.scala 994:24:@1397.4]
  wire [7:0] _GEN_144; // @[NV_NVDLA_CSC_dl.scala 995:24:@1400.4]
  wire [7:0] _GEN_145; // @[NV_NVDLA_CSC_dl.scala 996:28:@1403.4]
  wire [7:0] _GEN_146; // @[NV_NVDLA_CSC_dl.scala 997:28:@1406.4]
  wire [7:0] _GEN_147; // @[NV_NVDLA_CSC_dl.scala 998:28:@1409.4]
  wire [7:0] _GEN_148; // @[NV_NVDLA_CSC_dl.scala 999:28:@1412.4]
  wire [7:0] _T_1621; // @[NV_NVDLA_CSC_dl.scala 1008:55:@1415.4]
  wire [63:0] dat_rsp_pad_value; // @[Cat.scala 30:58:@1418.4]
  wire [63:0] dat_rsp_l0c0; // @[NV_NVDLA_CSC_dl.scala 1010:23:@1419.4]
  wire [63:0] dat_rsp_l1c0; // @[NV_NVDLA_CSC_dl.scala 1011:23:@1420.4]
  wire [63:0] dat_rsp_l2c0; // @[NV_NVDLA_CSC_dl.scala 1012:23:@1421.4]
  wire [63:0] dat_rsp_l3c0; // @[NV_NVDLA_CSC_dl.scala 1013:23:@1422.4]
  wire [63:0] dat_rsp_l0c1; // @[NV_NVDLA_CSC_dl.scala 1015:23:@1423.4]
  wire [63:0] dat_rsp_l1c1; // @[NV_NVDLA_CSC_dl.scala 1016:23:@1424.4]
  wire [63:0] dat_rsp_l2c1; // @[NV_NVDLA_CSC_dl.scala 1017:23:@1425.4]
  wire [63:0] dat_rsp_l3c1; // @[NV_NVDLA_CSC_dl.scala 1018:23:@1426.4]
  wire  _T_1625; // @[NV_NVDLA_CSC_dl.scala 1024:37:@1428.4]
  wire [63:0] dat_rsp_conv_8b; // @[NV_NVDLA_CSC_dl.scala 1024:27:@1429.4]
  wire [7:0] dat_rsp_conv_0; // @[NV_NVDLA_CSC_dl.scala 1046:39:@1432.4]
  wire [7:0] dat_rsp_conv_1; // @[NV_NVDLA_CSC_dl.scala 1046:39:@1434.4]
  wire [7:0] dat_rsp_conv_2; // @[NV_NVDLA_CSC_dl.scala 1046:39:@1436.4]
  wire [7:0] dat_rsp_conv_3; // @[NV_NVDLA_CSC_dl.scala 1046:39:@1438.4]
  wire [7:0] dat_rsp_conv_4; // @[NV_NVDLA_CSC_dl.scala 1046:39:@1440.4]
  wire [7:0] dat_rsp_conv_5; // @[NV_NVDLA_CSC_dl.scala 1046:39:@1442.4]
  wire [7:0] dat_rsp_conv_6; // @[NV_NVDLA_CSC_dl.scala 1046:39:@1444.4]
  wire [7:0] dat_rsp_conv_7; // @[NV_NVDLA_CSC_dl.scala 1046:39:@1446.4]
  reg [31:0] dat_rsp_l0_sft_d1; // @[NV_NVDLA_CSC_dl.scala 1050:28:@1448.4]
  reg [31:0] _RAND_184;
  reg [15:0] dat_rsp_l0_sft_d2; // @[NV_NVDLA_CSC_dl.scala 1051:28:@1449.4]
  reg [31:0] _RAND_185;
  reg [15:0] dat_rsp_l0_sft_d3; // @[NV_NVDLA_CSC_dl.scala 1052:28:@1450.4]
  reg [31:0] _RAND_186;
  reg [15:0] dat_rsp_l1_sft_d2; // @[NV_NVDLA_CSC_dl.scala 1054:28:@1451.4]
  reg [31:0] _RAND_187;
  reg [15:0] dat_rsp_l1_sft_d3; // @[NV_NVDLA_CSC_dl.scala 1055:28:@1452.4]
  reg [31:0] _RAND_188;
  reg [15:0] dat_rsp_l2_sft_d3; // @[NV_NVDLA_CSC_dl.scala 1057:28:@1453.4]
  reg [31:0] _RAND_189;
  wire  _T_1655; // @[NV_NVDLA_CSC_dl.scala 1059:39:@1454.4]
  wire  _T_1656; // @[NV_NVDLA_CSC_dl.scala 1059:29:@1455.4]
  wire [127:0] _T_1658; // @[Cat.scala 30:58:@1456.4]
  wire [127:0] dat_rsp_l0_sft_in; // @[NV_NVDLA_CSC_dl.scala 1059:28:@1457.4]
  wire  _T_1659; // @[NV_NVDLA_CSC_dl.scala 1060:39:@1458.4]
  wire  _T_1660; // @[NV_NVDLA_CSC_dl.scala 1060:29:@1459.4]
  wire [127:0] _T_1662; // @[Cat.scala 30:58:@1460.4]
  wire [127:0] dat_rsp_l1_sft_in; // @[NV_NVDLA_CSC_dl.scala 1060:28:@1461.4]
  wire  _T_1663; // @[NV_NVDLA_CSC_dl.scala 1061:39:@1462.4]
  wire  _T_1664; // @[NV_NVDLA_CSC_dl.scala 1061:29:@1463.4]
  wire [127:0] _T_1666; // @[Cat.scala 30:58:@1464.4]
  wire [127:0] dat_rsp_l2_sft_in; // @[NV_NVDLA_CSC_dl.scala 1061:28:@1465.4]
  wire  _T_1667; // @[NV_NVDLA_CSC_dl.scala 1062:39:@1466.4]
  wire  _T_1668; // @[NV_NVDLA_CSC_dl.scala 1062:29:@1467.4]
  wire [127:0] _T_1670; // @[Cat.scala 30:58:@1468.4]
  wire [127:0] dat_rsp_l3_sft_in; // @[NV_NVDLA_CSC_dl.scala 1062:28:@1469.4]
  wire [10:0] _T_1672; // @[Cat.scala 30:58:@1470.4]
  wire [127:0] _T_1673; // @[NV_NVDLA_CSC_dl.scala 1064:41:@1471.4]
  wire [63:0] dat_rsp_l0_sft; // @[NV_NVDLA_CSC_dl.scala 1064:82:@1472.4]
  wire [10:0] _T_1675; // @[Cat.scala 30:58:@1473.4]
  wire [127:0] _T_1676; // @[NV_NVDLA_CSC_dl.scala 1065:41:@1474.4]
  wire [63:0] dat_rsp_l1_sft; // @[NV_NVDLA_CSC_dl.scala 1065:82:@1475.4]
  wire [10:0] _T_1678; // @[Cat.scala 30:58:@1476.4]
  wire [127:0] _T_1679; // @[NV_NVDLA_CSC_dl.scala 1066:41:@1477.4]
  wire [63:0] dat_rsp_l2_sft; // @[NV_NVDLA_CSC_dl.scala 1066:82:@1478.4]
  wire [10:0] _T_1681; // @[Cat.scala 30:58:@1479.4]
  wire [127:0] _T_1682; // @[NV_NVDLA_CSC_dl.scala 1067:41:@1480.4]
  wire [63:0] dat_rsp_l3_sft; // @[NV_NVDLA_CSC_dl.scala 1067:82:@1481.4]
  wire  _T_1683; // @[NV_NVDLA_CSC_dl.scala 1069:36:@1482.4]
  wire  _T_1684; // @[NV_NVDLA_CSC_dl.scala 1069:26:@1483.4]
  wire  _T_1687; // @[NV_NVDLA_CSC_dl.scala 1070:41:@1484.4]
  wire [15:0] _T_1688; // @[NV_NVDLA_CSC_dl.scala 1070:81:@1485.4]
  wire [63:0] _T_1694; // @[Cat.scala 30:58:@1491.4]
  wire  _T_1696; // @[NV_NVDLA_CSC_dl.scala 1071:41:@1492.4]
  wire [31:0] _T_1697; // @[NV_NVDLA_CSC_dl.scala 1071:81:@1493.4]
  wire [63:0] _T_1699; // @[Cat.scala 30:58:@1495.4]
  wire [63:0] _T_1701; // @[NV_NVDLA_CSC_dl.scala 1071:25:@1497.4]
  wire [63:0] _T_1702; // @[NV_NVDLA_CSC_dl.scala 1070:25:@1498.4]
  wire [63:0] dat_rsp_img_8b; // @[NV_NVDLA_CSC_dl.scala 1069:25:@1499.4]
  wire [7:0] dat_rsp_img_0; // @[NV_NVDLA_CSC_dl.scala 1077:37:@1501.4]
  wire [7:0] dat_rsp_img_1; // @[NV_NVDLA_CSC_dl.scala 1077:37:@1503.4]
  wire [7:0] dat_rsp_img_2; // @[NV_NVDLA_CSC_dl.scala 1077:37:@1505.4]
  wire [7:0] dat_rsp_img_3; // @[NV_NVDLA_CSC_dl.scala 1077:37:@1507.4]
  wire [7:0] dat_rsp_img_4; // @[NV_NVDLA_CSC_dl.scala 1077:37:@1509.4]
  wire [7:0] dat_rsp_img_5; // @[NV_NVDLA_CSC_dl.scala 1077:37:@1511.4]
  wire [7:0] dat_rsp_img_6; // @[NV_NVDLA_CSC_dl.scala 1077:37:@1513.4]
  wire [7:0] dat_rsp_img_7; // @[NV_NVDLA_CSC_dl.scala 1077:37:@1515.4]
  wire  _T_1725; // @[NV_NVDLA_CSC_dl.scala 1080:59:@1517.4]
  wire  dat_rsp_sft_d1_en; // @[NV_NVDLA_CSC_dl.scala 1080:41:@1518.4]
  wire  _T_1727; // @[NV_NVDLA_CSC_dl.scala 1081:59:@1519.4]
  wire  dat_rsp_sft_d2_en; // @[NV_NVDLA_CSC_dl.scala 1081:41:@1520.4]
  wire  dat_rsp_sft_d3_en; // @[NV_NVDLA_CSC_dl.scala 1082:41:@1522.4]
  wire [63:0] _GEN_149; // @[NV_NVDLA_CSC_dl.scala 1084:24:@1523.4]
  wire [31:0] _GEN_150; // @[NV_NVDLA_CSC_dl.scala 1087:24:@1526.4]
  wire [63:0] _GEN_151; // @[NV_NVDLA_CSC_dl.scala 1087:24:@1526.4]
  wire [63:0] _GEN_154; // @[NV_NVDLA_CSC_dl.scala 1091:24:@1530.4]
  wire [262:0] _T_1735; // @[NV_NVDLA_CSC_dl.scala 1100:56:@1536.4]
  wire [7:0] _T_1736; // @[NV_NVDLA_CSC_dl.scala 1100:73:@1537.4]
  wire [7:0] dat_rsp_ori_mask; // @[NV_NVDLA_CSC_dl.scala 1100:24:@1538.4]
  wire  _T_1738; // @[NV_NVDLA_CSC_dl.scala 1102:51:@1539.4]
  wire [7:0] dat_rsp_cur_h_mask_p1; // @[NV_NVDLA_CSC_dl.scala 1102:32:@1541.4]
  wire  _T_1746; // @[NV_NVDLA_CSC_dl.scala 1103:51:@1542.4]
  wire [3:0] dat_rsp_cur_h_mask_p2; // @[NV_NVDLA_CSC_dl.scala 1103:32:@1544.4]
  wire  _T_1754; // @[NV_NVDLA_CSC_dl.scala 1104:51:@1545.4]
  wire [3:0] dat_rsp_cur_h_mask_p3; // @[NV_NVDLA_CSC_dl.scala 1104:32:@1547.4]
  wire [3:0] _T_1761; // @[NV_NVDLA_CSC_dl.scala 1106:57:@1548.4]
  wire [7:0] dat_rsp_cur_h_e2_mask_8b; // @[Cat.scala 30:58:@1550.4]
  wire [1:0] _T_1767; // @[NV_NVDLA_CSC_dl.scala 1107:57:@1551.4]
  wire [1:0] _T_1768; // @[NV_NVDLA_CSC_dl.scala 1107:106:@1552.4]
  wire [1:0] _T_1769; // @[NV_NVDLA_CSC_dl.scala 1107:155:@1553.4]
  wire [7:0] dat_rsp_cur_h_e4_mask_8b; // @[Cat.scala 30:58:@1557.4]
  wire  _T_1778; // @[NV_NVDLA_CSC_dl.scala 1109:43:@1558.4]
  wire [1:0] _T_1779; // @[NV_NVDLA_CSC_dl.scala 1109:89:@1559.4]
  wire [7:0] _T_1781; // @[Cat.scala 30:58:@1561.4]
  wire [7:0] _T_1782; // @[NV_NVDLA_CSC_dl.scala 1109:116:@1562.4]
  wire  _T_1784; // @[NV_NVDLA_CSC_dl.scala 1110:43:@1563.4]
  wire [3:0] _T_1785; // @[NV_NVDLA_CSC_dl.scala 1110:89:@1564.4]
  wire [7:0] _T_1786; // @[Cat.scala 30:58:@1565.4]
  wire [7:0] _T_1787; // @[NV_NVDLA_CSC_dl.scala 1110:116:@1566.4]
  wire [7:0] _T_1788; // @[NV_NVDLA_CSC_dl.scala 1110:26:@1567.4]
  wire [7:0] dat_rsp_mask_8b; // @[NV_NVDLA_CSC_dl.scala 1109:26:@1568.4]
  wire  _T_1789; // @[NV_NVDLA_CSC_dl.scala 1114:35:@1569.4]
  wire [7:0] dat_rsp_data_w_0; // @[NV_NVDLA_CSC_dl.scala 1114:25:@1570.4]
  wire [7:0] dat_rsp_data_w_1; // @[NV_NVDLA_CSC_dl.scala 1114:25:@1570.4]
  wire [7:0] dat_rsp_data_w_2; // @[NV_NVDLA_CSC_dl.scala 1114:25:@1570.4]
  wire [7:0] dat_rsp_data_w_3; // @[NV_NVDLA_CSC_dl.scala 1114:25:@1570.4]
  wire [7:0] dat_rsp_data_w_4; // @[NV_NVDLA_CSC_dl.scala 1114:25:@1570.4]
  wire [7:0] dat_rsp_data_w_5; // @[NV_NVDLA_CSC_dl.scala 1114:25:@1570.4]
  wire [7:0] dat_rsp_data_w_6; // @[NV_NVDLA_CSC_dl.scala 1114:25:@1570.4]
  wire [7:0] dat_rsp_data_w_7; // @[NV_NVDLA_CSC_dl.scala 1114:25:@1570.4]
  wire  dat_rsp_mask_val_int8_0; // @[NV_NVDLA_CSC_dl.scala 1115:97:@1571.4]
  wire  dat_rsp_mask_val_int8_1; // @[NV_NVDLA_CSC_dl.scala 1115:97:@1572.4]
  wire  dat_rsp_mask_val_int8_2; // @[NV_NVDLA_CSC_dl.scala 1115:97:@1573.4]
  wire  dat_rsp_mask_val_int8_3; // @[NV_NVDLA_CSC_dl.scala 1115:97:@1574.4]
  wire  dat_rsp_mask_val_int8_4; // @[NV_NVDLA_CSC_dl.scala 1115:97:@1575.4]
  wire  dat_rsp_mask_val_int8_5; // @[NV_NVDLA_CSC_dl.scala 1115:97:@1576.4]
  wire  dat_rsp_mask_val_int8_6; // @[NV_NVDLA_CSC_dl.scala 1115:97:@1577.4]
  wire  dat_rsp_mask_val_int8_7; // @[NV_NVDLA_CSC_dl.scala 1115:97:@1578.4]
  wire  _T_1838; // @[NV_NVDLA_CSC_dl.scala 1116:80:@1588.4]
  wire  dat_rsp_mask_w_0; // @[NV_NVDLA_CSC_dl.scala 1116:83:@1589.4]
  wire  _T_1840; // @[NV_NVDLA_CSC_dl.scala 1116:80:@1590.4]
  wire  dat_rsp_mask_w_1; // @[NV_NVDLA_CSC_dl.scala 1116:83:@1591.4]
  wire  _T_1842; // @[NV_NVDLA_CSC_dl.scala 1116:80:@1592.4]
  wire  dat_rsp_mask_w_2; // @[NV_NVDLA_CSC_dl.scala 1116:83:@1593.4]
  wire  _T_1844; // @[NV_NVDLA_CSC_dl.scala 1116:80:@1594.4]
  wire  dat_rsp_mask_w_3; // @[NV_NVDLA_CSC_dl.scala 1116:83:@1595.4]
  wire  _T_1846; // @[NV_NVDLA_CSC_dl.scala 1116:80:@1596.4]
  wire  dat_rsp_mask_w_4; // @[NV_NVDLA_CSC_dl.scala 1116:83:@1597.4]
  wire  _T_1848; // @[NV_NVDLA_CSC_dl.scala 1116:80:@1598.4]
  wire  dat_rsp_mask_w_5; // @[NV_NVDLA_CSC_dl.scala 1116:83:@1599.4]
  wire  _T_1850; // @[NV_NVDLA_CSC_dl.scala 1116:80:@1600.4]
  wire  dat_rsp_mask_w_6; // @[NV_NVDLA_CSC_dl.scala 1116:83:@1601.4]
  wire  _T_1852; // @[NV_NVDLA_CSC_dl.scala 1116:80:@1602.4]
  wire  dat_rsp_mask_w_7; // @[NV_NVDLA_CSC_dl.scala 1116:83:@1603.4]
  reg  dat_out_pvld; // @[NV_NVDLA_CSC_dl.scala 1139:27:@1613.4]
  reg [31:0] _RAND_190;
  reg [8:0] dat_out_flag; // @[NV_NVDLA_CSC_dl.scala 1140:27:@1614.4]
  reg [31:0] _RAND_191;
  reg  dat_out_bypass_mask_0; // @[NV_NVDLA_CSC_dl.scala 1141:34:@1624.4]
  reg [31:0] _RAND_192;
  reg  dat_out_bypass_mask_1; // @[NV_NVDLA_CSC_dl.scala 1141:34:@1624.4]
  reg [31:0] _RAND_193;
  reg  dat_out_bypass_mask_2; // @[NV_NVDLA_CSC_dl.scala 1141:34:@1624.4]
  reg [31:0] _RAND_194;
  reg  dat_out_bypass_mask_3; // @[NV_NVDLA_CSC_dl.scala 1141:34:@1624.4]
  reg [31:0] _RAND_195;
  reg  dat_out_bypass_mask_4; // @[NV_NVDLA_CSC_dl.scala 1141:34:@1624.4]
  reg [31:0] _RAND_196;
  reg  dat_out_bypass_mask_5; // @[NV_NVDLA_CSC_dl.scala 1141:34:@1624.4]
  reg [31:0] _RAND_197;
  reg  dat_out_bypass_mask_6; // @[NV_NVDLA_CSC_dl.scala 1141:34:@1624.4]
  reg [31:0] _RAND_198;
  reg  dat_out_bypass_mask_7; // @[NV_NVDLA_CSC_dl.scala 1141:34:@1624.4]
  reg [31:0] _RAND_199;
  reg [7:0] _T_1943; // @[NV_NVDLA_CSC_dl.scala 1142:81:@1625.4]
  reg [31:0] _RAND_200;
  reg [7:0] _T_1946; // @[NV_NVDLA_CSC_dl.scala 1142:81:@1626.4]
  reg [31:0] _RAND_201;
  reg [7:0] _T_1949; // @[NV_NVDLA_CSC_dl.scala 1142:81:@1627.4]
  reg [31:0] _RAND_202;
  reg [7:0] _T_1952; // @[NV_NVDLA_CSC_dl.scala 1142:81:@1628.4]
  reg [31:0] _RAND_203;
  reg [7:0] _T_1955; // @[NV_NVDLA_CSC_dl.scala 1142:81:@1629.4]
  reg [31:0] _RAND_204;
  reg [7:0] _T_1958; // @[NV_NVDLA_CSC_dl.scala 1142:81:@1630.4]
  reg [31:0] _RAND_205;
  reg [7:0] _T_1961; // @[NV_NVDLA_CSC_dl.scala 1142:81:@1631.4]
  reg [31:0] _RAND_206;
  reg [7:0] _T_1964; // @[NV_NVDLA_CSC_dl.scala 1142:81:@1632.4]
  reg [31:0] _RAND_207;
  wire [8:0] _GEN_155; // @[NV_NVDLA_CSC_dl.scala 1153:21:@1634.4]
  wire  _GEN_156; // @[NV_NVDLA_CSC_dl.scala 1156:30:@1637.4]
  wire  _GEN_157; // @[NV_NVDLA_CSC_dl.scala 1156:30:@1637.4]
  wire  _GEN_158; // @[NV_NVDLA_CSC_dl.scala 1156:30:@1637.4]
  wire  _GEN_159; // @[NV_NVDLA_CSC_dl.scala 1156:30:@1637.4]
  wire  _GEN_160; // @[NV_NVDLA_CSC_dl.scala 1156:30:@1637.4]
  wire  _GEN_161; // @[NV_NVDLA_CSC_dl.scala 1156:30:@1637.4]
  wire  _GEN_162; // @[NV_NVDLA_CSC_dl.scala 1156:30:@1637.4]
  wire  _GEN_163; // @[NV_NVDLA_CSC_dl.scala 1156:30:@1637.4]
  wire  _T_1965; // @[NV_NVDLA_CSC_dl.scala 1160:34:@1647.4]
  wire [7:0] _GEN_164; // @[NV_NVDLA_CSC_dl.scala 1160:61:@1648.4]
  wire  _T_1966; // @[NV_NVDLA_CSC_dl.scala 1160:34:@1651.4]
  wire [7:0] _GEN_165; // @[NV_NVDLA_CSC_dl.scala 1160:61:@1652.4]
  wire  _T_1967; // @[NV_NVDLA_CSC_dl.scala 1160:34:@1655.4]
  wire [7:0] _GEN_166; // @[NV_NVDLA_CSC_dl.scala 1160:61:@1656.4]
  wire  _T_1968; // @[NV_NVDLA_CSC_dl.scala 1160:34:@1659.4]
  wire [7:0] _GEN_167; // @[NV_NVDLA_CSC_dl.scala 1160:61:@1660.4]
  wire  _T_1969; // @[NV_NVDLA_CSC_dl.scala 1160:34:@1663.4]
  wire [7:0] _GEN_168; // @[NV_NVDLA_CSC_dl.scala 1160:61:@1664.4]
  wire  _T_1970; // @[NV_NVDLA_CSC_dl.scala 1160:34:@1667.4]
  wire [7:0] _GEN_169; // @[NV_NVDLA_CSC_dl.scala 1160:61:@1668.4]
  wire  _T_1971; // @[NV_NVDLA_CSC_dl.scala 1160:34:@1671.4]
  wire [7:0] _GEN_170; // @[NV_NVDLA_CSC_dl.scala 1160:61:@1672.4]
  wire  _T_1972; // @[NV_NVDLA_CSC_dl.scala 1160:34:@1675.4]
  wire [7:0] _GEN_171; // @[NV_NVDLA_CSC_dl.scala 1160:61:@1676.4]
  reg  dl_out_pvld; // @[NV_NVDLA_CSC_dl.scala 1168:26:@1679.4]
  reg [31:0] _RAND_208;
  reg  dl_out_mask_0; // @[NV_NVDLA_CSC_dl.scala 1169:26:@1689.4]
  reg [31:0] _RAND_209;
  reg  dl_out_mask_1; // @[NV_NVDLA_CSC_dl.scala 1169:26:@1689.4]
  reg [31:0] _RAND_210;
  reg  dl_out_mask_2; // @[NV_NVDLA_CSC_dl.scala 1169:26:@1689.4]
  reg [31:0] _RAND_211;
  reg  dl_out_mask_3; // @[NV_NVDLA_CSC_dl.scala 1169:26:@1689.4]
  reg [31:0] _RAND_212;
  reg  dl_out_mask_4; // @[NV_NVDLA_CSC_dl.scala 1169:26:@1689.4]
  reg [31:0] _RAND_213;
  reg  dl_out_mask_5; // @[NV_NVDLA_CSC_dl.scala 1169:26:@1689.4]
  reg [31:0] _RAND_214;
  reg  dl_out_mask_6; // @[NV_NVDLA_CSC_dl.scala 1169:26:@1689.4]
  reg [31:0] _RAND_215;
  reg  dl_out_mask_7; // @[NV_NVDLA_CSC_dl.scala 1169:26:@1689.4]
  reg [31:0] _RAND_216;
  reg [8:0] dl_out_flag; // @[NV_NVDLA_CSC_dl.scala 1170:26:@1690.4]
  reg [31:0] _RAND_217;
  reg [7:0] _T_2049; // @[NV_NVDLA_CSC_dl.scala 1171:73:@1691.4]
  reg [31:0] _RAND_218;
  reg [7:0] _T_2052; // @[NV_NVDLA_CSC_dl.scala 1171:73:@1692.4]
  reg [31:0] _RAND_219;
  reg [7:0] _T_2055; // @[NV_NVDLA_CSC_dl.scala 1171:73:@1693.4]
  reg [31:0] _RAND_220;
  reg [7:0] _T_2058; // @[NV_NVDLA_CSC_dl.scala 1171:73:@1694.4]
  reg [31:0] _RAND_221;
  reg [7:0] _T_2061; // @[NV_NVDLA_CSC_dl.scala 1171:73:@1695.4]
  reg [31:0] _RAND_222;
  reg [7:0] _T_2064; // @[NV_NVDLA_CSC_dl.scala 1171:73:@1696.4]
  reg [31:0] _RAND_223;
  reg [7:0] _T_2067; // @[NV_NVDLA_CSC_dl.scala 1171:73:@1697.4]
  reg [31:0] _RAND_224;
  reg [7:0] _T_2070; // @[NV_NVDLA_CSC_dl.scala 1171:73:@1698.4]
  reg [31:0] _RAND_225;
  wire  _T_2071; // @[NV_NVDLA_CSC_dl.scala 1175:24:@1699.4]
  wire  dat_out_mask_0; // @[NV_NVDLA_CSC_dl.scala 1175:23:@1709.4]
  wire  dat_out_mask_1; // @[NV_NVDLA_CSC_dl.scala 1175:23:@1709.4]
  wire  dat_out_mask_2; // @[NV_NVDLA_CSC_dl.scala 1175:23:@1709.4]
  wire  dat_out_mask_3; // @[NV_NVDLA_CSC_dl.scala 1175:23:@1709.4]
  wire  dat_out_mask_4; // @[NV_NVDLA_CSC_dl.scala 1175:23:@1709.4]
  wire  dat_out_mask_5; // @[NV_NVDLA_CSC_dl.scala 1175:23:@1709.4]
  wire  dat_out_mask_6; // @[NV_NVDLA_CSC_dl.scala 1175:23:@1709.4]
  wire  dat_out_mask_7; // @[NV_NVDLA_CSC_dl.scala 1175:23:@1709.4]
  wire  _T_2113; // @[NV_NVDLA_CSC_dl.scala 1179:19:@1711.4]
  wire  _GEN_172; // @[NV_NVDLA_CSC_dl.scala 1179:33:@1712.4]
  wire  _GEN_173; // @[NV_NVDLA_CSC_dl.scala 1179:33:@1712.4]
  wire  _GEN_174; // @[NV_NVDLA_CSC_dl.scala 1179:33:@1712.4]
  wire  _GEN_175; // @[NV_NVDLA_CSC_dl.scala 1179:33:@1712.4]
  wire  _GEN_176; // @[NV_NVDLA_CSC_dl.scala 1179:33:@1712.4]
  wire  _GEN_177; // @[NV_NVDLA_CSC_dl.scala 1179:33:@1712.4]
  wire  _GEN_178; // @[NV_NVDLA_CSC_dl.scala 1179:33:@1712.4]
  wire  _GEN_179; // @[NV_NVDLA_CSC_dl.scala 1179:33:@1712.4]
  wire [8:0] _GEN_180; // @[NV_NVDLA_CSC_dl.scala 1182:19:@1722.4]
  wire [7:0] _GEN_181; // @[NV_NVDLA_CSC_dl.scala 1187:26:@1725.4]
  wire [7:0] _GEN_182; // @[NV_NVDLA_CSC_dl.scala 1187:26:@1728.4]
  wire [7:0] _GEN_183; // @[NV_NVDLA_CSC_dl.scala 1187:26:@1731.4]
  wire [7:0] _GEN_184; // @[NV_NVDLA_CSC_dl.scala 1187:26:@1734.4]
  wire [7:0] _GEN_185; // @[NV_NVDLA_CSC_dl.scala 1187:26:@1737.4]
  wire [7:0] _GEN_186; // @[NV_NVDLA_CSC_dl.scala 1187:26:@1740.4]
  wire [7:0] _GEN_187; // @[NV_NVDLA_CSC_dl.scala 1187:26:@1743.4]
  wire [7:0] _GEN_188; // @[NV_NVDLA_CSC_dl.scala 1187:26:@1746.4]
  reg  dl_out_pvld_d1; // @[NV_NVDLA_CSC_dl.scala 1195:29:@1749.4]
  reg [31:0] _RAND_226;
  wire  _T_2116; // @[NV_NVDLA_CSC_dl.scala 1196:27:@1751.4]
  wire [8:0] sc2mac_dat_pd_w; // @[NV_NVDLA_CSC_dl.scala 1196:26:@1752.4]
  reg  _T_2120; // @[NV_NVDLA_CSC_dl.scala 1198:33:@1753.4]
  reg [31:0] _RAND_227;
  reg  _T_2123; // @[NV_NVDLA_CSC_dl.scala 1199:33:@1756.4]
  reg [31:0] _RAND_228;
  wire  _T_2125; // @[NV_NVDLA_CSC_dl.scala 1200:85:@1759.4]
  reg [8:0] _T_2127; // @[Reg.scala 19:20:@1760.4]
  reg [31:0] _RAND_229;
  wire [8:0] _GEN_189; // @[Reg.scala 20:19:@1761.4]
  reg [8:0] _T_2131; // @[Reg.scala 19:20:@1766.4]
  reg [31:0] _RAND_230;
  wire [8:0] _GEN_190; // @[Reg.scala 20:19:@1767.4]
  reg  _T_2175_0; // @[Reg.scala 19:20:@1781.4]
  reg [31:0] _RAND_231;
  reg  _T_2175_1; // @[Reg.scala 19:20:@1781.4]
  reg [31:0] _RAND_232;
  reg  _T_2175_2; // @[Reg.scala 19:20:@1781.4]
  reg [31:0] _RAND_233;
  reg  _T_2175_3; // @[Reg.scala 19:20:@1781.4]
  reg [31:0] _RAND_234;
  reg  _T_2175_4; // @[Reg.scala 19:20:@1781.4]
  reg [31:0] _RAND_235;
  reg  _T_2175_5; // @[Reg.scala 19:20:@1781.4]
  reg [31:0] _RAND_236;
  reg  _T_2175_6; // @[Reg.scala 19:20:@1781.4]
  reg [31:0] _RAND_237;
  reg  _T_2175_7; // @[Reg.scala 19:20:@1781.4]
  reg [31:0] _RAND_238;
  wire  _GEN_191; // @[Reg.scala 20:19:@1782.4]
  wire  _GEN_192; // @[Reg.scala 20:19:@1782.4]
  wire  _GEN_193; // @[Reg.scala 20:19:@1782.4]
  wire  _GEN_194; // @[Reg.scala 20:19:@1782.4]
  wire  _GEN_195; // @[Reg.scala 20:19:@1782.4]
  wire  _GEN_196; // @[Reg.scala 20:19:@1782.4]
  wire  _GEN_197; // @[Reg.scala 20:19:@1782.4]
  wire  _GEN_198; // @[Reg.scala 20:19:@1782.4]
  reg  _T_2247_0; // @[Reg.scala 19:20:@1810.4]
  reg [31:0] _RAND_239;
  reg  _T_2247_1; // @[Reg.scala 19:20:@1810.4]
  reg [31:0] _RAND_240;
  reg  _T_2247_2; // @[Reg.scala 19:20:@1810.4]
  reg [31:0] _RAND_241;
  reg  _T_2247_3; // @[Reg.scala 19:20:@1810.4]
  reg [31:0] _RAND_242;
  reg  _T_2247_4; // @[Reg.scala 19:20:@1810.4]
  reg [31:0] _RAND_243;
  reg  _T_2247_5; // @[Reg.scala 19:20:@1810.4]
  reg [31:0] _RAND_244;
  reg  _T_2247_6; // @[Reg.scala 19:20:@1810.4]
  reg [31:0] _RAND_245;
  reg  _T_2247_7; // @[Reg.scala 19:20:@1810.4]
  reg [31:0] _RAND_246;
  wire  _GEN_199; // @[Reg.scala 20:19:@1811.4]
  wire  _GEN_200; // @[Reg.scala 20:19:@1811.4]
  wire  _GEN_201; // @[Reg.scala 20:19:@1811.4]
  wire  _GEN_202; // @[Reg.scala 20:19:@1811.4]
  wire  _GEN_203; // @[Reg.scala 20:19:@1811.4]
  wire  _GEN_204; // @[Reg.scala 20:19:@1811.4]
  wire  _GEN_205; // @[Reg.scala 20:19:@1811.4]
  wire  _GEN_206; // @[Reg.scala 20:19:@1811.4]
  reg [7:0] _T_2278; // @[Reg.scala 19:20:@1829.4]
  reg [31:0] _RAND_247;
  wire [7:0] _GEN_207; // @[Reg.scala 20:19:@1830.4]
  reg [7:0] _T_2281; // @[Reg.scala 19:20:@1834.4]
  reg [31:0] _RAND_248;
  wire [7:0] _GEN_208; // @[Reg.scala 20:19:@1835.4]
  reg [7:0] _T_2284; // @[Reg.scala 19:20:@1839.4]
  reg [31:0] _RAND_249;
  wire [7:0] _GEN_209; // @[Reg.scala 20:19:@1840.4]
  reg [7:0] _T_2287; // @[Reg.scala 19:20:@1844.4]
  reg [31:0] _RAND_250;
  wire [7:0] _GEN_210; // @[Reg.scala 20:19:@1845.4]
  reg [7:0] _T_2290; // @[Reg.scala 19:20:@1849.4]
  reg [31:0] _RAND_251;
  wire [7:0] _GEN_211; // @[Reg.scala 20:19:@1850.4]
  reg [7:0] _T_2293; // @[Reg.scala 19:20:@1854.4]
  reg [31:0] _RAND_252;
  wire [7:0] _GEN_212; // @[Reg.scala 20:19:@1855.4]
  reg [7:0] _T_2296; // @[Reg.scala 19:20:@1859.4]
  reg [31:0] _RAND_253;
  wire [7:0] _GEN_213; // @[Reg.scala 20:19:@1860.4]
  reg [7:0] _T_2299; // @[Reg.scala 19:20:@1864.4]
  reg [31:0] _RAND_254;
  wire [7:0] _GEN_214; // @[Reg.scala 20:19:@1865.4]
  reg [7:0] _T_2302; // @[Reg.scala 19:20:@1869.4]
  reg [31:0] _RAND_255;
  wire [7:0] _GEN_215; // @[Reg.scala 20:19:@1870.4]
  reg [7:0] _T_2305; // @[Reg.scala 19:20:@1874.4]
  reg [31:0] _RAND_256;
  wire [7:0] _GEN_216; // @[Reg.scala 20:19:@1875.4]
  reg [7:0] _T_2308; // @[Reg.scala 19:20:@1879.4]
  reg [31:0] _RAND_257;
  wire [7:0] _GEN_217; // @[Reg.scala 20:19:@1880.4]
  reg [7:0] _T_2311; // @[Reg.scala 19:20:@1884.4]
  reg [31:0] _RAND_258;
  wire [7:0] _GEN_218; // @[Reg.scala 20:19:@1885.4]
  reg [7:0] _T_2314; // @[Reg.scala 19:20:@1889.4]
  reg [31:0] _RAND_259;
  wire [7:0] _GEN_219; // @[Reg.scala 20:19:@1890.4]
  reg [7:0] _T_2317; // @[Reg.scala 19:20:@1894.4]
  reg [31:0] _RAND_260;
  wire [7:0] _GEN_220; // @[Reg.scala 20:19:@1895.4]
  reg [7:0] _T_2320; // @[Reg.scala 19:20:@1899.4]
  reg [31:0] _RAND_261;
  wire [7:0] _GEN_221; // @[Reg.scala 20:19:@1900.4]
  reg [7:0] _T_2323; // @[Reg.scala 19:20:@1904.4]
  reg [31:0] _RAND_262;
  wire [7:0] _GEN_222; // @[Reg.scala 20:19:@1905.4]
  assign is_sg_idle = io_sc_state == 2'h0; // @[NV_NVDLA_CSC_dl.scala 77:31:@8.4]
  assign is_sg_done = io_sc_state == 2'h3; // @[NV_NVDLA_CSC_dl.scala 79:31:@10.4]
  assign layer_st = io_reg2dp_op_en & is_sg_idle; // @[NV_NVDLA_CSC_dl.scala 86:32:@13.4]
  assign is_conv = io_reg2dp_conv_mode == 1'h0; // @[NV_NVDLA_CSC_dl.scala 88:35:@15.4]
  assign is_img = is_conv & io_reg2dp_datain_format; // @[NV_NVDLA_CSC_dl.scala 89:22:@16.4]
  assign _T_181 = 7'h9 << io_reg2dp_y_extension; // @[NV_NVDLA_CSC_dl.scala 96:53:@17.4]
  assign _T_183 = is_img ? _T_181 : 7'h8; // @[NV_NVDLA_CSC_dl.scala 96:24:@18.4]
  assign sub_h_total_w = _T_183[5:3]; // @[NV_NVDLA_CSC_dl.scala 96:100:@19.4]
  assign sub_h_cmp_w = is_img ? sub_h_total_w : 3'h1; // @[NV_NVDLA_CSC_dl.scala 97:22:@20.4]
  assign _T_186 = sub_h_cmp_w - 3'h1; // @[NV_NVDLA_CSC_dl.scala 98:34:@21.4]
  assign dataout_w_init = $unsigned(_T_186); // @[NV_NVDLA_CSC_dl.scala 98:34:@22.4]
  assign conv_x_stride_w = io_reg2dp_conv_x_stride_ext + 3'h1; // @[NV_NVDLA_CSC_dl.scala 99:51:@23.4]
  assign _T_188 = io_reg2dp_datain_channel_ext[1:0]; // @[NV_NVDLA_CSC_dl.scala 100:62:@24.4]
  assign _T_191 = {conv_x_stride_w,2'h0}; // @[Cat.scala 30:58:@25.4]
  assign _T_194 = {conv_x_stride_w,1'h0}; // @[Cat.scala 30:58:@26.4]
  assign _GEN_223 = {{1'd0}, conv_x_stride_w}; // @[NV_NVDLA_CSC_dl.scala 102:74:@27.4]
  assign _T_195 = _T_194 + _GEN_223; // @[NV_NVDLA_CSC_dl.scala 102:74:@27.4]
  assign _T_196 = _T_194 + _GEN_223; // @[NV_NVDLA_CSC_dl.scala 102:74:@28.4]
  assign _T_197 = 2'h2 == _T_188; // @[Mux.scala 46:19:@29.4]
  assign _T_198 = _T_197 ? _T_196 : {{1'd0}, conv_x_stride_w}; // @[Mux.scala 46:16:@30.4]
  assign _T_199 = 2'h3 == _T_188; // @[Mux.scala 46:19:@31.4]
  assign pixel_x_stride_w = _T_199 ? _T_191 : {{1'd0}, _T_198}; // @[Mux.scala 46:16:@32.4]
  assign _T_201 = io_reg2dp_weight_channel_ext >= 13'h8; // @[NV_NVDLA_CSC_dl.scala 104:88:@33.4]
  assign _T_207 = io_reg2dp_weight_channel_ext[2:0]; // @[NV_NVDLA_CSC_dl.scala 104:172:@35.4]
  assign _T_208 = _T_201 ? 3'h7 : _T_207; // @[NV_NVDLA_CSC_dl.scala 104:58:@36.4]
  assign _T_211 = {pixel_x_stride_w,1'h0}; // @[Cat.scala 30:58:@37.4]
  assign _GEN_224 = {{1'd0}, pixel_x_stride_w}; // @[NV_NVDLA_CSC_dl.scala 105:81:@38.4]
  assign _T_212 = _T_211 + _GEN_224; // @[NV_NVDLA_CSC_dl.scala 105:81:@38.4]
  assign _T_213 = _T_211 + _GEN_224; // @[NV_NVDLA_CSC_dl.scala 105:81:@39.4]
  assign _T_214 = io_reg2dp_weight_channel_ext[5:0]; // @[NV_NVDLA_CSC_dl.scala 105:130:@40.4]
  assign _GEN_225 = {{1'd0}, _T_214}; // @[NV_NVDLA_CSC_dl.scala 105:100:@41.4]
  assign _T_215 = _T_213 + _GEN_225; // @[NV_NVDLA_CSC_dl.scala 105:100:@41.4]
  assign _T_216 = _T_213 + _GEN_225; // @[NV_NVDLA_CSC_dl.scala 105:100:@42.4]
  assign _T_219 = pixel_x_stride_w + _T_214; // @[NV_NVDLA_CSC_dl.scala 106:58:@44.4]
  assign _T_220 = pixel_x_stride_w + _T_214; // @[NV_NVDLA_CSC_dl.scala 106:58:@45.4]
  assign _T_221 = 2'h1 == io_reg2dp_y_extension; // @[Mux.scala 46:19:@46.4]
  assign _T_222 = _T_221 ? _T_220 : {{3'd0}, _T_208}; // @[Mux.scala 46:16:@47.4]
  assign _T_223 = 2'h2 == io_reg2dp_y_extension; // @[Mux.scala 46:19:@48.4]
  assign pixel_x_init_w = _T_223 ? _T_216 : {{1'd0}, _T_222}; // @[Mux.scala 46:16:@49.4]
  assign pixel_x_init_offset_w = _T_207 + 3'h1; // @[NV_NVDLA_CSC_dl.scala 107:80:@51.4]
  assign _T_228 = {pixel_x_stride_w,2'h0}; // @[Cat.scala 30:58:@52.4]
  assign _T_233 = _T_221 ? _T_211 : {{1'd0}, pixel_x_stride_w}; // @[Mux.scala 46:16:@55.4]
  assign pixel_x_add_w = _T_223 ? _T_228 : {{1'd0}, _T_233}; // @[Mux.scala 46:16:@57.4]
  assign pixel_ch_stride_w = {pixel_x_stride_w,4'h0}; // @[Cat.scala 30:58:@58.4]
  assign conv_y_stride_w = io_reg2dp_conv_y_stride_ext + 3'h1; // @[NV_NVDLA_CSC_dl.scala 117:52:@59.4]
  assign _T_239 = io_reg2dp_x_dilation_ext + 5'h1; // @[NV_NVDLA_CSC_dl.scala 118:60:@60.4]
  assign x_dilate_w = is_img ? 6'h1 : _T_239; // @[NV_NVDLA_CSC_dl.scala 118:21:@61.4]
  assign _T_242 = io_reg2dp_y_dilation_ext + 5'h1; // @[NV_NVDLA_CSC_dl.scala 119:60:@62.4]
  assign y_dilate_w = is_img ? 6'h1 : _T_242; // @[NV_NVDLA_CSC_dl.scala 119:21:@63.4]
  assign entries_single_w = io_reg2dp_entries + 14'h1; // @[NV_NVDLA_CSC_dl.scala 135:43:@87.4]
  assign _T_313 = entries_single_w * 15'h1; // @[NV_NVDLA_CSC_dl.scala 136:41:@89.4]
  assign entries_batch_w = _T_313[14:0]; // @[NV_NVDLA_CSC_dl.scala 136:56:@90.4]
  assign h_offset_slice_w = 6'h1 * y_dilate_w; // @[NV_NVDLA_CSC_dl.scala 138:37:@91.4]
  assign _GEN_226 = {{9'd0}, data_batch}; // @[NV_NVDLA_CSC_dl.scala 139:34:@92.4]
  assign _T_314 = entries * _GEN_226; // @[NV_NVDLA_CSC_dl.scala 139:34:@92.4]
  assign h_bias_0_stride_w = _T_314[13:0]; // @[NV_NVDLA_CSC_dl.scala 139:47:@93.4]
  assign _GEN_227 = {{1'd0}, h_offset_slice}; // @[NV_NVDLA_CSC_dl.scala 140:34:@94.4]
  assign _T_315 = entries * _GEN_227; // @[NV_NVDLA_CSC_dl.scala 140:34:@94.4]
  assign h_bias_1_stride_w = _T_315[13:0]; // @[NV_NVDLA_CSC_dl.scala 140:51:@95.4]
  assign rls_slices_w = io_reg2dp_rls_slices + 12'h1; // @[NV_NVDLA_CSC_dl.scala 141:41:@96.4]
  assign _T_318 = io_reg2dp_datain_height_ext + 13'h1; // @[NV_NVDLA_CSC_dl.scala 142:77:@97.4]
  assign _GEN_228 = {{1'd0}, io_reg2dp_rls_slices}; // @[NV_NVDLA_CSC_dl.scala 142:113:@98.4]
  assign _T_319 = io_reg2dp_datain_height_ext - _GEN_228; // @[NV_NVDLA_CSC_dl.scala 142:113:@98.4]
  assign _T_320 = $unsigned(_T_319); // @[NV_NVDLA_CSC_dl.scala 142:113:@99.4]
  assign slice_left_w = io_reg2dp_skip_data_rls ? _T_318 : _T_320; // @[NV_NVDLA_CSC_dl.scala 142:23:@100.4]
  assign slices_oprand = layer_st_d1 ? rls_slices : slice_left; // @[NV_NVDLA_CSC_dl.scala 143:24:@101.4]
  assign _GEN_229 = {{1'd0}, slices_oprand}; // @[NV_NVDLA_CSC_dl.scala 144:38:@102.4]
  assign _T_321 = entries_batch * _GEN_229; // @[NV_NVDLA_CSC_dl.scala 144:38:@102.4]
  assign slice_entries_w = _T_321[14:0]; // @[NV_NVDLA_CSC_dl.scala 144:54:@103.4]
  assign _T_506 = is_img ? 34'h3ffffffff : 34'h0; // @[Bitwise.scala 72:12:@174.6]
  assign _T_508 = io_reg2dp_data_bank + 5'h1; // @[NV_NVDLA_CSC_dl.scala 193:38:@176.6]
  assign _T_509 = io_reg2dp_data_bank + 5'h1; // @[NV_NVDLA_CSC_dl.scala 193:38:@177.6]
  assign _T_511 = io_reg2dp_datain_width_ext + 13'h1; // @[NV_NVDLA_CSC_dl.scala 194:48:@179.6]
  assign _T_513 = io_reg2dp_weight_channel_ext[12:3]; // @[NV_NVDLA_CSC_dl.scala 197:93:@183.6]
  assign _T_514 = {1'h0,_T_513}; // @[Cat.scala 30:58:@184.6]
  assign _T_515 = sub_h_total_w[2:1]; // @[NV_NVDLA_CSC_dl.scala 200:36:@188.6]
  assign _T_517 = pixel_x_init_w[5:0]; // @[NV_NVDLA_CSC_dl.scala 217:35:@207.6]
  assign _T_518 = pixel_x_add_w[6:0]; // @[NV_NVDLA_CSC_dl.scala 219:33:@210.6]
  assign _T_520 = {1'h0,io_reg2dp_entries}; // @[Cat.scala 30:58:@219.6]
  assign _GEN_1 = layer_st ? _T_506 : is_img_d1; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_2 = layer_st ? _T_509 : data_bank; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_3 = layer_st ? _T_511 : datain_width; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_4 = layer_st ? io_reg2dp_datain_width_ext : datain_width_cmp; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_5 = layer_st ? io_reg2dp_datain_height_ext : datain_height_cmp; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_6 = layer_st ? _T_514 : datain_channel_cmp; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_7 = layer_st ? sub_h_total_w : sub_h_total_g0; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_8 = layer_st ? sub_h_total_w : sub_h_total_g1; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_9 = layer_st ? _T_515 : sub_h_total_g2; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_10 = layer_st ? sub_h_total_w : sub_h_total_g3; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_11 = layer_st ? sub_h_total_w : sub_h_total_g4; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_12 = layer_st ? sub_h_total_w : sub_h_total_g5; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_13 = layer_st ? sub_h_total_w : sub_h_total_g6; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_15 = layer_st ? sub_h_total_w : sub_h_total_g8; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_16 = layer_st ? sub_h_total_w : sub_h_total_g9; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_18 = layer_st ? sub_h_total_w : sub_h_total_g11; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_19 = layer_st ? sub_h_cmp_w : sub_h_cmp_g0; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_20 = layer_st ? sub_h_cmp_w : sub_h_cmp_g1; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_21 = layer_st ? conv_x_stride_w : conv_x_stride; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_22 = layer_st ? conv_y_stride_w : conv_y_stride; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_24 = layer_st ? 6'h1 : data_batch; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_25 = layer_st ? 5'h0 : batch_cmp; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_26 = layer_st ? _T_517 : pixel_x_init; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_27 = layer_st ? {{3'd0}, pixel_x_init_offset_w} : pixel_x_init_offset; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_28 = layer_st ? _T_518 : pixel_x_add; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_29 = layer_st ? {{1'd0}, pixel_x_stride_w} : pixel_x_byte_stride; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_30 = layer_st ? {{2'd0}, pixel_ch_stride_w} : pixel_ch_stride; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_31 = layer_st ? x_dilate_w : x_dilate; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_32 = layer_st ? y_dilate_w : y_dilate; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_33 = layer_st ? io_reg2dp_pad_value : pad_value; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_34 = layer_st ? entries_single_w : entries; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_35 = layer_st ? entries_batch_w : entries_batch; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_36 = layer_st ? _T_520 : entries_cmp; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_37 = layer_st ? {{2'd0}, h_offset_slice_w} : h_offset_slice; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_38 = layer_st ? {{1'd0}, rls_slices_w} : rls_slices; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_39 = layer_st ? slice_left_w : slice_left; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_40 = layer_st ? io_reg2dp_dataout_width : dataout_width_cmp; // @[NV_NVDLA_CSC_dl.scala 190:15:@170.4]
  assign _GEN_43 = layer_st_d1 ? h_bias_0_stride_w : h_bias_0_stride; // @[NV_NVDLA_CSC_dl.scala 235:18:@232.4]
  assign _GEN_44 = layer_st_d1 ? h_bias_1_stride_w : h_bias_1_stride; // @[NV_NVDLA_CSC_dl.scala 235:18:@232.4]
  assign _GEN_45 = layer_st_d1 ? entries : h_bias_2_stride; // @[NV_NVDLA_CSC_dl.scala 235:18:@232.4]
  assign _GEN_46 = layer_st_d1 ? entries : h_bias_3_stride; // @[NV_NVDLA_CSC_dl.scala 235:18:@232.4]
  assign _GEN_47 = layer_st_d1 ? slice_entries_w : rls_entries; // @[NV_NVDLA_CSC_dl.scala 235:18:@232.4]
  assign _GEN_48 = is_sg_done ? slice_left : last_slices; // @[NV_NVDLA_CSC_dl.scala 242:17:@239.4]
  assign _GEN_49 = is_sg_done ? slice_entries_w : last_entries; // @[NV_NVDLA_CSC_dl.scala 242:17:@239.4]
  assign _T_597 = last_slices != 14'h0; // @[NV_NVDLA_CSC_dl.scala 306:37:@314.4]
  assign _T_598 = io_sg2dl_reuse_rls & _T_597; // @[NV_NVDLA_CSC_dl.scala 306:23:@315.4]
  assign _T_1471 = sub_h_total_g3[2]; // @[NV_NVDLA_CSC_dl.scala 900:32:@1234.4]
  assign _T_1472 = _T_1471 & dat_rsp_l3_pvld; // @[NV_NVDLA_CSC_dl.scala 900:36:@1235.4]
  assign _T_1473 = sub_h_total_g3[1]; // @[NV_NVDLA_CSC_dl.scala 901:35:@1236.4]
  assign _T_1474 = _T_1473 & dat_rsp_l1_pvld; // @[NV_NVDLA_CSC_dl.scala 901:39:@1237.4]
  assign _T_1475 = _T_1472 | _T_1474; // @[NV_NVDLA_CSC_dl.scala 900:57:@1238.4]
  assign _T_1476 = sub_h_total_g3[0]; // @[NV_NVDLA_CSC_dl.scala 902:35:@1239.4]
  assign _T_1477 = _T_1476 & dat_rsp_l0_pvld; // @[NV_NVDLA_CSC_dl.scala 902:39:@1240.4]
  assign dat_rsp_pvld = _T_1475 | _T_1477; // @[NV_NVDLA_CSC_dl.scala 901:60:@1241.4]
  assign _T_1479 = sub_h_total_g4[2]; // @[NV_NVDLA_CSC_dl.scala 909:42:@1243.4]
  assign _T_1483 = _T_1479 ? 27'h7ffffff : 27'h0; // @[Bitwise.scala 72:12:@1245.4]
  assign _T_1484 = _T_1483 & _T_1461; // @[NV_NVDLA_CSC_dl.scala 909:47:@1246.4]
  assign _T_1485 = sub_h_total_g4[1]; // @[NV_NVDLA_CSC_dl.scala 910:42:@1247.4]
  assign _T_1489 = _T_1485 ? 27'h7ffffff : 27'h0; // @[Bitwise.scala 72:12:@1249.4]
  assign _T_1490 = _T_1489 & _T_1455; // @[NV_NVDLA_CSC_dl.scala 910:47:@1250.4]
  assign _T_1491 = _T_1484 | _T_1490; // @[NV_NVDLA_CSC_dl.scala 909:66:@1251.4]
  assign _T_1492 = sub_h_total_g4[0]; // @[NV_NVDLA_CSC_dl.scala 911:42:@1252.4]
  assign _T_1496 = _T_1492 ? 27'h7ffffff : 27'h0; // @[Bitwise.scala 72:12:@1254.4]
  assign _T_1497 = _T_1496 & _T_1452; // @[NV_NVDLA_CSC_dl.scala 911:47:@1255.4]
  assign dat_rsp_pd = _T_1491 | _T_1497; // @[NV_NVDLA_CSC_dl.scala 910:66:@1256.4]
  assign dat_rsp_rls = dat_rsp_pd[17]; // @[NV_NVDLA_CSC_dl.scala 935:26:@1275.4]
  assign sub_rls = dat_rsp_pvld & dat_rsp_rls; // @[NV_NVDLA_CSC_dl.scala 303:29:@313.4]
  assign _T_600 = rls_slices != 14'h0; // @[NV_NVDLA_CSC_dl.scala 306:66:@316.4]
  assign _T_601 = sub_rls & _T_600; // @[NV_NVDLA_CSC_dl.scala 306:53:@317.4]
  assign dat_rls = _T_598 | _T_601; // @[NV_NVDLA_CSC_dl.scala 306:42:@318.4]
  assign sc2cdma_dat_slices_w = sub_rls ? rls_slices : last_slices; // @[NV_NVDLA_CSC_dl.scala 307:28:@320.4]
  assign sc2cdma_dat_entries_w = sub_rls ? rls_entries : last_entries; // @[NV_NVDLA_CSC_dl.scala 308:29:@322.4]
  assign dat_entry_avl_sub = dat_rls ? sc2cdma_dat_entries_w : 15'h0; // @[NV_NVDLA_CSC_dl.scala 275:28:@262.4]
  assign _T_554 = dat_entry_st + dat_entry_avl_sub; // @[NV_NVDLA_CSC_dl.scala 280:37:@269.4]
  assign dat_entry_st_inc = dat_entry_st + dat_entry_avl_sub; // @[NV_NVDLA_CSC_dl.scala 280:37:@270.4]
  assign _T_560 = {data_bank,9'h0}; // @[Cat.scala 30:58:@272.4]
  assign _GEN_230 = {{1'd0}, _T_560}; // @[NV_NVDLA_CSC_dl.scala 281:46:@273.4]
  assign _T_561 = dat_entry_st_inc - _GEN_230; // @[NV_NVDLA_CSC_dl.scala 281:46:@273.4]
  assign _T_562 = $unsigned(_T_561); // @[NV_NVDLA_CSC_dl.scala 281:46:@274.4]
  assign dat_entry_st_inc_wrap = _T_562[14:0]; // @[NV_NVDLA_CSC_dl.scala 281:46:@275.4]
  assign is_dat_entry_st_wrap = dat_entry_st_inc >= _GEN_230; // @[NV_NVDLA_CSC_dl.scala 282:45:@278.4]
  assign _T_570 = is_dat_entry_st_wrap ? dat_entry_st_inc_wrap : dat_entry_st_inc; // @[NV_NVDLA_CSC_dl.scala 283:83:@279.4]
  assign dat_entry_st_w = io_sc2cdma_dat_pending_req ? 15'h0 : _T_570; // @[NV_NVDLA_CSC_dl.scala 283:25:@280.4]
  assign _T_592 = dat_rls | io_sc2cdma_dat_pending_req; // @[NV_NVDLA_CSC_dl.scala 294:13:@303.4]
  assign _GEN_52 = _T_592 ? dat_entry_st_w : dat_entry_st; // @[NV_NVDLA_CSC_dl.scala 294:25:@304.4]
  assign _GEN_54 = dat_rls ? sc2cdma_dat_slices_w : _T_610; // @[Reg.scala 20:19:@328.4]
  assign _GEN_55 = dat_rls ? sc2cdma_dat_entries_w : _T_613; // @[Reg.scala 20:19:@333.4]
  assign _GEN_56 = io_sg2dl_pd_valid ? io_sg2dl_pd_bits : _T_634; // @[NV_NVDLA_CSC_dl.scala 332:26:@352.4]
  assign _GEN_57 = _T_618 ? _T_634 : _T_637; // @[NV_NVDLA_CSC_dl.scala 332:26:@356.4]
  assign _GEN_58 = _T_621 ? _T_637 : _T_640; // @[NV_NVDLA_CSC_dl.scala 332:26:@360.4]
  assign _GEN_59 = _T_624 ? _T_640 : _T_643; // @[NV_NVDLA_CSC_dl.scala 332:26:@364.4]
  assign _GEN_60 = _T_627 ? _T_643 : dl_in_pd; // @[NV_NVDLA_CSC_dl.scala 332:26:@368.4]
  assign _GEN_61 = dl_in_pvld ? dl_in_pd : _T_664; // @[NV_NVDLA_CSC_dl.scala 351:23:@384.4]
  assign _GEN_62 = _T_650 ? _T_664 : _T_667; // @[NV_NVDLA_CSC_dl.scala 351:23:@388.4]
  assign _GEN_63 = _T_653 ? _T_667 : _T_670; // @[NV_NVDLA_CSC_dl.scala 351:23:@392.4]
  assign _GEN_64 = _T_656 ? _T_670 : _T_673; // @[NV_NVDLA_CSC_dl.scala 351:23:@396.4]
  assign _T_674 = sub_h_total_g0[2]; // @[NV_NVDLA_CSC_dl.scala 356:30:@399.4]
  assign _T_675 = _T_674 & _T_650; // @[NV_NVDLA_CSC_dl.scala 356:34:@400.4]
  assign _T_676 = sub_h_total_g0[1]; // @[NV_NVDLA_CSC_dl.scala 357:30:@401.4]
  assign _T_677 = _T_676 & _T_656; // @[NV_NVDLA_CSC_dl.scala 357:34:@402.4]
  assign _T_678 = _T_675 | _T_677; // @[NV_NVDLA_CSC_dl.scala 356:50:@403.4]
  assign _T_679 = sub_h_total_g0[0]; // @[NV_NVDLA_CSC_dl.scala 358:30:@404.4]
  assign _T_680 = _T_679 & _T_659; // @[NV_NVDLA_CSC_dl.scala 358:34:@405.4]
  assign dl_pvld = _T_678 | _T_680; // @[NV_NVDLA_CSC_dl.scala 357:50:@406.4]
  assign _T_681 = sub_h_total_g1[2]; // @[NV_NVDLA_CSC_dl.scala 360:37:@407.4]
  assign _T_685 = _T_681 ? 31'h7fffffff : 31'h0; // @[Bitwise.scala 72:12:@409.4]
  assign _T_686 = _T_685 & _T_664; // @[NV_NVDLA_CSC_dl.scala 360:42:@410.4]
  assign _T_687 = sub_h_total_g1[1]; // @[NV_NVDLA_CSC_dl.scala 361:37:@411.4]
  assign _T_691 = _T_687 ? 31'h7fffffff : 31'h0; // @[Bitwise.scala 72:12:@413.4]
  assign _T_692 = _T_691 & _T_670; // @[NV_NVDLA_CSC_dl.scala 361:42:@414.4]
  assign _T_693 = _T_686 | _T_692; // @[NV_NVDLA_CSC_dl.scala 360:56:@415.4]
  assign _T_694 = sub_h_total_g1[0]; // @[NV_NVDLA_CSC_dl.scala 362:37:@416.4]
  assign _T_698 = _T_694 ? 31'h7fffffff : 31'h0; // @[Bitwise.scala 72:12:@418.4]
  assign _T_699 = _T_698 & _T_673; // @[NV_NVDLA_CSC_dl.scala 362:42:@419.4]
  assign dl_pd = _T_693 | _T_699; // @[NV_NVDLA_CSC_dl.scala 361:56:@420.4]
  assign dl_w_offset = dl_pd[4:0]; // @[NV_NVDLA_CSC_dl.scala 365:24:@421.4]
  assign dl_h_offset = dl_pd[9:5]; // @[NV_NVDLA_CSC_dl.scala 366:24:@422.4]
  assign dl_channel_size = dl_pd[16:10]; // @[NV_NVDLA_CSC_dl.scala 367:28:@423.4]
  assign dl_stripe_length = dl_pd[23:17]; // @[NV_NVDLA_CSC_dl.scala 368:29:@424.4]
  assign dl_cur_sub_h = dl_pd[25:24]; // @[NV_NVDLA_CSC_dl.scala 369:25:@425.4]
  assign dl_block_end = dl_pd[26]; // @[NV_NVDLA_CSC_dl.scala 370:25:@426.4]
  assign dl_channel_end = dl_pd[27]; // @[NV_NVDLA_CSC_dl.scala 371:27:@427.4]
  assign dl_group_end = dl_pd[28]; // @[NV_NVDLA_CSC_dl.scala 372:25:@428.4]
  assign dl_layer_end = dl_pd[29]; // @[NV_NVDLA_CSC_dl.scala 373:25:@429.4]
  assign dl_dat_release = dl_pd[30]; // @[NV_NVDLA_CSC_dl.scala 374:27:@430.4]
  assign _T_707 = batch_cnt + 5'h1; // @[NV_NVDLA_CSC_dl.scala 383:24:@434.4]
  assign _T_708 = batch_cnt + 5'h1; // @[NV_NVDLA_CSC_dl.scala 383:24:@435.4]
  assign is_batch_end = batch_cnt == batch_cmp; // @[NV_NVDLA_CSC_dl.scala 385:27:@439.4]
  assign _T_709 = is_batch_end ? 5'h0 : _T_708; // @[NV_NVDLA_CSC_dl.scala 382:17:@436.4]
  assign _T_710 = layer_st ? 5'h0 : _T_709; // @[NV_NVDLA_CSC_dl.scala 381:17:@437.4]
  assign sub_h_cnt_inc = sub_h_cnt + 2'h1; // @[NV_NVDLA_CSC_dl.scala 391:31:@443.4]
  assign is_sub_h_end = sub_h_cnt_inc == sub_h_cmp_g0; // @[NV_NVDLA_CSC_dl.scala 392:32:@444.4]
  assign _T_718 = io_reg2dp_y_extension != 2'h0; // @[NV_NVDLA_CSC_dl.scala 393:61:@446.4]
  assign _T_754 = stripe_cnt != 7'h0; // @[NV_NVDLA_CSC_dl.scala 426:37:@483.4]
  assign _T_755 = ~ _T_754; // @[NV_NVDLA_CSC_dl.scala 426:24:@484.4]
  assign _T_757 = sub_h_cnt != 2'h0; // @[NV_NVDLA_CSC_dl.scala 426:56:@485.4]
  assign _T_758 = ~ _T_757; // @[NV_NVDLA_CSC_dl.scala 426:44:@486.4]
  assign _T_759 = _T_755 & _T_758; // @[NV_NVDLA_CSC_dl.scala 426:42:@487.4]
  assign _T_761 = batch_cnt != 5'h0; // @[NV_NVDLA_CSC_dl.scala 426:75:@488.4]
  assign _T_762 = ~ _T_761; // @[NV_NVDLA_CSC_dl.scala 426:63:@489.4]
  assign _T_763 = _T_759 & _T_762; // @[NV_NVDLA_CSC_dl.scala 426:61:@490.4]
  assign _T_765 = _T_763 ? 1'h0 : dat_exec_valid_d1; // @[NV_NVDLA_CSC_dl.scala 426:22:@491.4]
  assign dat_exec_valid = dl_pvld ? 1'h1 : _T_765; // @[NV_NVDLA_CSC_dl.scala 425:22:@492.4]
  assign _T_719 = _T_718 & dat_exec_valid; // @[NV_NVDLA_CSC_dl.scala 393:66:@447.4]
  assign sub_h_cnt_reg_en = layer_st | _T_719; // @[NV_NVDLA_CSC_dl.scala 393:33:@448.4]
  assign _T_720 = layer_st | is_sub_h_end; // @[NV_NVDLA_CSC_dl.scala 395:31:@450.6]
  assign _T_722 = _T_720 ? 3'h0 : sub_h_cnt_inc; // @[NV_NVDLA_CSC_dl.scala 395:21:@451.6]
  assign _GEN_65 = sub_h_cnt_reg_en ? _T_722 : {{1'd0}, sub_h_cnt}; // @[NV_NVDLA_CSC_dl.scala 394:23:@449.4]
  assign _T_728 = stripe_cnt + 7'h1; // @[NV_NVDLA_CSC_dl.scala 403:33:@457.4]
  assign stripe_cnt_inc = stripe_cnt + 7'h1; // @[NV_NVDLA_CSC_dl.scala 403:33:@458.4]
  assign _T_729 = stripe_cnt_inc == dl_stripe_length; // @[NV_NVDLA_CSC_dl.scala 404:51:@459.4]
  assign is_stripe_equal = is_batch_end & _T_729; // @[NV_NVDLA_CSC_dl.scala 404:33:@460.4]
  assign is_stripe_end = is_stripe_equal & is_sub_h_end; // @[NV_NVDLA_CSC_dl.scala 405:34:@462.4]
  assign _T_732 = dat_exec_valid & is_batch_end; // @[NV_NVDLA_CSC_dl.scala 406:52:@464.4]
  assign stripe_cnt_reg_en = layer_st | _T_732; // @[NV_NVDLA_CSC_dl.scala 406:34:@465.4]
  assign _T_734 = ~ is_sub_h_end; // @[NV_NVDLA_CSC_dl.scala 410:41:@467.6]
  assign _T_735 = is_stripe_equal & _T_734; // @[NV_NVDLA_CSC_dl.scala 410:39:@468.6]
  assign _T_737 = is_stripe_end ? 7'h0 : stripe_cnt_inc; // @[NV_NVDLA_CSC_dl.scala 411:22:@469.6]
  assign _T_738 = _T_735 ? stripe_cnt : _T_737; // @[NV_NVDLA_CSC_dl.scala 410:22:@470.6]
  assign _T_739 = layer_st ? 7'h0 : _T_738; // @[NV_NVDLA_CSC_dl.scala 409:22:@471.6]
  assign _GEN_66 = stripe_cnt_reg_en ? _T_739 : stripe_cnt; // @[NV_NVDLA_CSC_dl.scala 408:24:@466.4]
  assign dat_pipe_valid = dl_pvld | dat_pipe_local_valid; // @[NV_NVDLA_CSC_dl.scala 424:27:@481.4]
  assign _T_747 = dat_pipe_valid & is_stripe_equal; // @[NV_NVDLA_CSC_dl.scala 421:49:@478.4]
  assign _T_750 = dl_pvld ? 1'h1 : dat_pipe_local_valid; // @[NV_NVDLA_CSC_dl.scala 422:32:@479.4]
  assign dat_pipe_local_valid_w = _T_747 ? 1'h0 : _T_750; // @[NV_NVDLA_CSC_dl.scala 421:33:@480.4]
  assign dat_req_bytes = {1'h0,dl_channel_size}; // @[Cat.scala 30:58:@498.4]
  assign _GEN_67 = dat_exec_valid ? dat_req_bytes : dat_req_bytes_d1; // @[NV_NVDLA_CSC_dl.scala 436:21:@499.4]
  assign _GEN_234 = {{10'd0}, sub_h_cmp_g1}; // @[NV_NVDLA_CSC_dl.scala 446:39:@504.4]
  assign _T_774 = dataout_w_cnt + _GEN_234; // @[NV_NVDLA_CSC_dl.scala 446:39:@504.4]
  assign dataout_w_cnt_inc = dataout_w_cnt + _GEN_234; // @[NV_NVDLA_CSC_dl.scala 446:39:@505.4]
  assign _T_775 = is_batch_end & is_sub_h_end; // @[NV_NVDLA_CSC_dl.scala 447:29:@506.4]
  assign _T_776 = dataout_w_cnt >= dataout_width_cmp; // @[NV_NVDLA_CSC_dl.scala 447:61:@507.4]
  assign is_w_end = _T_775 & _T_776; // @[NV_NVDLA_CSC_dl.scala 447:44:@508.4]
  assign _T_778 = ~ dl_channel_end; // @[NV_NVDLA_CSC_dl.scala 450:43:@511.4]
  assign _T_779 = is_stripe_end & _T_778; // @[NV_NVDLA_CSC_dl.scala 450:41:@512.4]
  assign _T_780 = is_w_end ? {{9'd0}, dataout_w_init} : dataout_w_cnt_inc; // @[NV_NVDLA_CSC_dl.scala 451:26:@513.4]
  assign _T_781 = _T_779 ? dataout_w_ori : _T_780; // @[NV_NVDLA_CSC_dl.scala 450:26:@514.4]
  assign dataout_w_cnt_w = layer_st ? {{9'd0}, dataout_w_init} : _T_781; // @[NV_NVDLA_CSC_dl.scala 449:26:@515.4]
  assign _T_783 = _T_732 & is_sub_h_end; // @[NV_NVDLA_CSC_dl.scala 452:70:@517.4]
  assign dataout_w_cnt_reg_en = layer_st | _T_783; // @[NV_NVDLA_CSC_dl.scala 452:37:@518.4]
  assign _T_784 = dat_exec_valid & is_stripe_end; // @[NV_NVDLA_CSC_dl.scala 453:55:@519.4]
  assign _T_785 = _T_784 & dl_channel_end; // @[NV_NVDLA_CSC_dl.scala 453:71:@520.4]
  assign dataout_w_ori_reg_en = layer_st | _T_785; // @[NV_NVDLA_CSC_dl.scala 453:37:@521.4]
  assign _GEN_68 = dataout_w_cnt_reg_en ? dataout_w_cnt_w : dataout_w_cnt; // @[NV_NVDLA_CSC_dl.scala 455:27:@522.4]
  assign _GEN_69 = dataout_w_ori_reg_en ? dataout_w_cnt_w : dataout_w_ori; // @[NV_NVDLA_CSC_dl.scala 458:27:@525.4]
  assign is_last_channel = datain_c_cnt == datain_channel_cmp; // @[NV_NVDLA_CSC_dl.scala 465:37:@529.4]
  assign _T_789 = _T_784 & dl_block_end; // @[NV_NVDLA_CSC_dl.scala 466:70:@531.4]
  assign datain_c_cnt_reg_en = layer_st | _T_789; // @[NV_NVDLA_CSC_dl.scala 466:36:@532.4]
  assign _T_793 = datain_c_cnt + 11'h1; // @[NV_NVDLA_CSC_dl.scala 471:34:@534.6]
  assign _T_794 = datain_c_cnt + 11'h1; // @[NV_NVDLA_CSC_dl.scala 471:34:@535.6]
  assign _T_795 = dl_channel_end ? 11'h0 : _T_794; // @[NV_NVDLA_CSC_dl.scala 470:24:@536.6]
  assign _T_796 = layer_st ? 11'h0 : _T_795; // @[NV_NVDLA_CSC_dl.scala 469:24:@537.6]
  assign _GEN_70 = datain_c_cnt_reg_en ? _T_796 : datain_c_cnt; // @[NV_NVDLA_CSC_dl.scala 468:26:@533.4]
  assign _GEN_235 = {{8'd0}, io_reg2dp_pad_left}; // @[NV_NVDLA_CSC_dl.scala 486:41:@549.4]
  assign _T_816 = 13'h0 - _GEN_235; // @[NV_NVDLA_CSC_dl.scala 486:41:@549.4]
  assign _T_817 = $unsigned(_T_816); // @[NV_NVDLA_CSC_dl.scala 486:41:@550.4]
  assign datain_w_cnt_st = is_img ? 14'h0 : _T_817; // @[NV_NVDLA_CSC_dl.scala 485:26:@551.4]
  assign _GEN_236 = {{10'd0}, conv_x_stride}; // @[NV_NVDLA_CSC_dl.scala 487:37:@552.4]
  assign _T_818 = datain_w_cnt + _GEN_236; // @[NV_NVDLA_CSC_dl.scala 487:37:@552.4]
  assign datain_w_cnt_inc = datain_w_cnt + _GEN_236; // @[NV_NVDLA_CSC_dl.scala 487:37:@553.4]
  assign _T_821 = is_w_end ? datain_w_cnt_st : datain_w_cnt_inc; // @[NV_NVDLA_CSC_dl.scala 492:25:@556.4]
  assign _T_822 = _T_779 ? datain_w_ori : _T_821; // @[NV_NVDLA_CSC_dl.scala 491:25:@557.4]
  assign datain_w_cnt_w = layer_st ? datain_w_cnt_st : _T_822; // @[NV_NVDLA_CSC_dl.scala 490:25:@558.4]
  assign _GEN_237 = {{1'd0}, dl_w_offset}; // @[NV_NVDLA_CSC_dl.scala 494:35:@559.4]
  assign dl_w_offset_ext = _GEN_237 * x_dilate; // @[NV_NVDLA_CSC_dl.scala 494:35:@559.4]
  assign _GEN_238 = {{3'd0}, dl_w_offset_ext}; // @[NV_NVDLA_CSC_dl.scala 495:33:@560.4]
  assign _T_823 = datain_w_cnt + _GEN_238; // @[NV_NVDLA_CSC_dl.scala 495:33:@560.4]
  assign datain_w_cur = datain_w_cnt + _GEN_238; // @[NV_NVDLA_CSC_dl.scala 495:33:@561.4]
  assign _T_826 = is_img_d1[0]; // @[NV_NVDLA_CSC_dl.scala 496:96:@564.4]
  assign _T_827 = ~ _T_826; // @[NV_NVDLA_CSC_dl.scala 496:86:@565.4]
  assign _T_828 = _T_783 & _T_827; // @[NV_NVDLA_CSC_dl.scala 496:84:@566.4]
  assign datain_w_cnt_reg_en = layer_st | _T_828; // @[NV_NVDLA_CSC_dl.scala 496:36:@567.4]
  assign _T_831 = is_img_d1[1]; // @[NV_NVDLA_CSC_dl.scala 497:99:@570.4]
  assign _T_832 = ~ _T_831; // @[NV_NVDLA_CSC_dl.scala 497:89:@571.4]
  assign _T_833 = _T_785 & _T_832; // @[NV_NVDLA_CSC_dl.scala 497:87:@572.4]
  assign datain_w_ori_reg_en = layer_st | _T_833; // @[NV_NVDLA_CSC_dl.scala 497:36:@573.4]
  assign pixel_x_cnt_add = is_sub_h_end ? pixel_x_add : 7'h0; // @[NV_NVDLA_CSC_dl.scala 500:26:@574.4]
  assign _T_837 = _T_207 == 3'h0; // @[NV_NVDLA_CSC_dl.scala 502:79:@576.4]
  assign _T_841 = _T_513 + 10'h1; // @[NV_NVDLA_CSC_dl.scala 503:74:@579.4]
  assign total_channel_op = _T_837 ? {{1'd0}, _T_513} : _T_841; // @[NV_NVDLA_CSC_dl.scala 502:27:@580.4]
  assign _T_842 = dl_channel_end & is_stripe_end; // @[NV_NVDLA_CSC_dl.scala 504:37:@581.4]
  assign _T_844 = dl_block_end & is_stripe_end; // @[NV_NVDLA_CSC_dl.scala 505:35:@582.4]
  assign _T_846 = channel_op_cnt + 13'h1; // @[NV_NVDLA_CSC_dl.scala 505:66:@583.4]
  assign _T_847 = channel_op_cnt + 13'h1; // @[NV_NVDLA_CSC_dl.scala 505:66:@584.4]
  assign _T_848 = _T_844 ? _T_847 : channel_op_cnt; // @[NV_NVDLA_CSC_dl.scala 505:22:@585.4]
  assign _T_849 = _T_842 ? 13'h2 : _T_848; // @[NV_NVDLA_CSC_dl.scala 504:22:@586.4]
  assign _GEN_239 = {{2'd0}, total_channel_op}; // @[NV_NVDLA_CSC_dl.scala 507:44:@588.4]
  assign next_is_last_channel = channel_op_cnt >= _GEN_239; // @[NV_NVDLA_CSC_dl.scala 507:44:@588.4]
  assign _T_850 = is_stripe_end & dl_block_end; // @[NV_NVDLA_CSC_dl.scala 511:39:@589.4]
  assign _T_851 = _T_850 & dl_channel_end; // @[NV_NVDLA_CSC_dl.scala 511:54:@590.4]
  assign _T_852 = _T_851 & is_w_end; // @[NV_NVDLA_CSC_dl.scala 511:71:@591.4]
  assign _T_855 = ~ is_w_end; // @[NV_NVDLA_CSC_dl.scala 512:73:@594.4]
  assign _T_856 = _T_851 & _T_855; // @[NV_NVDLA_CSC_dl.scala 512:71:@595.4]
  assign _GEN_240 = {{4'd0}, pixel_ch_stride}; // @[NV_NVDLA_CSC_dl.scala 512:99:@596.4]
  assign _T_857 = pixel_w_ch_ori + _GEN_240; // @[NV_NVDLA_CSC_dl.scala 512:99:@596.4]
  assign _T_858 = pixel_w_ch_ori + _GEN_240; // @[NV_NVDLA_CSC_dl.scala 512:99:@597.4]
  assign _T_860 = _T_850 & next_is_last_channel; // @[NV_NVDLA_CSC_dl.scala 513:54:@599.4]
  assign _GEN_241 = {{9'd0}, pixel_x_init_offset}; // @[NV_NVDLA_CSC_dl.scala 513:90:@600.4]
  assign _T_861 = pixel_w_ori + _GEN_241; // @[NV_NVDLA_CSC_dl.scala 513:90:@600.4]
  assign _T_862 = pixel_w_ori + _GEN_241; // @[NV_NVDLA_CSC_dl.scala 513:90:@601.4]
  assign _T_864 = ~ next_is_last_channel; // @[NV_NVDLA_CSC_dl.scala 514:56:@603.4]
  assign _T_865 = _T_850 & _T_864; // @[NV_NVDLA_CSC_dl.scala 514:54:@604.4]
  assign _T_867 = pixel_w_ori + 16'h8; // @[NV_NVDLA_CSC_dl.scala 514:91:@605.4]
  assign _T_868 = pixel_w_ori + 16'h8; // @[NV_NVDLA_CSC_dl.scala 514:91:@606.4]
  assign _T_869 = ~ dl_block_end; // @[NV_NVDLA_CSC_dl.scala 515:41:@607.4]
  assign _T_870 = is_stripe_end & _T_869; // @[NV_NVDLA_CSC_dl.scala 515:39:@608.4]
  assign _GEN_242 = {{9'd0}, pixel_x_cnt_add}; // @[NV_NVDLA_CSC_dl.scala 515:81:@609.4]
  assign _T_871 = pixel_w_cnt + _GEN_242; // @[NV_NVDLA_CSC_dl.scala 515:81:@609.4]
  assign _T_872 = pixel_w_cnt + _GEN_242; // @[NV_NVDLA_CSC_dl.scala 515:81:@610.4]
  assign _T_873 = _T_870 ? pixel_w_ori : _T_872; // @[NV_NVDLA_CSC_dl.scala 515:24:@611.4]
  assign _T_874 = _T_865 ? _T_868 : _T_873; // @[NV_NVDLA_CSC_dl.scala 514:24:@612.4]
  assign _T_875 = _T_860 ? _T_862 : _T_874; // @[NV_NVDLA_CSC_dl.scala 513:24:@613.4]
  assign _T_876 = _T_856 ? _T_858 : _T_875; // @[NV_NVDLA_CSC_dl.scala 512:24:@614.4]
  assign _T_877 = _T_852 ? {{10'd0}, pixel_x_init} : _T_876; // @[NV_NVDLA_CSC_dl.scala 511:24:@615.4]
  assign pixel_w_cnt_w = layer_st_d1 ? {{10'd0}, pixel_x_init} : _T_877; // @[NV_NVDLA_CSC_dl.scala 510:24:@616.4]
  assign _T_883 = pixel_w_cnt[15:3]; // @[NV_NVDLA_CSC_dl.scala 517:68:@618.4]
  assign pixel_w_cur = {2'h0,_T_883}; // @[Cat.scala 30:58:@619.4]
  assign _T_884 = is_img_d1[2]; // @[NV_NVDLA_CSC_dl.scala 518:67:@620.4]
  assign _T_885 = dat_exec_valid & _T_884; // @[NV_NVDLA_CSC_dl.scala 518:56:@621.4]
  assign _T_886 = is_sub_h_end | is_w_end; // @[NV_NVDLA_CSC_dl.scala 518:87:@622.4]
  assign _T_887 = _T_885 & _T_886; // @[NV_NVDLA_CSC_dl.scala 518:71:@623.4]
  assign pixel_w_cnt_reg_en = layer_st_d1 | _T_887; // @[NV_NVDLA_CSC_dl.scala 518:38:@624.4]
  assign _T_888 = is_img_d1[3]; // @[NV_NVDLA_CSC_dl.scala 519:67:@625.4]
  assign _T_889 = dat_exec_valid & _T_888; // @[NV_NVDLA_CSC_dl.scala 519:56:@626.4]
  assign _T_890 = _T_889 & is_stripe_end; // @[NV_NVDLA_CSC_dl.scala 519:71:@627.4]
  assign _T_891 = _T_890 & dl_block_end; // @[NV_NVDLA_CSC_dl.scala 519:87:@628.4]
  assign pixel_w_ori_reg_en = layer_st_d1 | _T_891; // @[NV_NVDLA_CSC_dl.scala 519:38:@629.4]
  assign _T_892 = is_img_d1[4]; // @[NV_NVDLA_CSC_dl.scala 520:68:@630.4]
  assign _T_893 = dat_exec_valid & _T_892; // @[NV_NVDLA_CSC_dl.scala 520:57:@631.4]
  assign _T_894 = _T_893 & is_stripe_end; // @[NV_NVDLA_CSC_dl.scala 520:72:@632.4]
  assign _T_895 = _T_894 & dl_block_end; // @[NV_NVDLA_CSC_dl.scala 520:88:@633.4]
  assign _T_896 = _T_895 & dl_channel_end; // @[NV_NVDLA_CSC_dl.scala 520:103:@634.4]
  assign pixel_ch_ori_reg_en = layer_st_d1 | _T_896; // @[NV_NVDLA_CSC_dl.scala 520:39:@635.4]
  assign _T_898 = _T_826 & dl_pvld; // @[NV_NVDLA_CSC_dl.scala 522:42:@637.4]
  assign _T_901 = pixel_force_clr_d1 ? 1'h0 : pixel_force_fetch_d1; // @[NV_NVDLA_CSC_dl.scala 522:74:@638.4]
  assign pixel_force_fetch = _T_898 ? 1'h1 : _T_901; // @[NV_NVDLA_CSC_dl.scala 522:28:@639.4]
  assign _T_903 = _T_826 & is_sub_h_end; // @[NV_NVDLA_CSC_dl.scala 523:36:@641.4]
  assign _T_904 = pixel_force_fetch | pixel_force_fetch_d1; // @[NV_NVDLA_CSC_dl.scala 523:72:@642.4]
  assign pixel_force_clr = _T_903 & _T_904; // @[NV_NVDLA_CSC_dl.scala 523:51:@643.4]
  assign _GEN_71 = datain_w_cnt_reg_en ? datain_w_cnt_w : datain_w_cnt; // @[NV_NVDLA_CSC_dl.scala 525:26:@644.4]
  assign _GEN_72 = datain_w_ori_reg_en ? datain_w_cnt_w : datain_w_ori; // @[NV_NVDLA_CSC_dl.scala 528:26:@647.4]
  assign _GEN_73 = pixel_w_cnt_reg_en ? pixel_w_cnt_w : pixel_w_cnt; // @[NV_NVDLA_CSC_dl.scala 531:25:@650.4]
  assign _GEN_74 = pixel_w_ori_reg_en ? pixel_w_cnt_w : pixel_w_ori; // @[NV_NVDLA_CSC_dl.scala 534:25:@653.4]
  assign _GEN_75 = pixel_ch_ori_reg_en ? pixel_w_cnt_w : pixel_w_ch_ori; // @[NV_NVDLA_CSC_dl.scala 537:26:@656.4]
  assign _GEN_243 = {{9'd0}, io_reg2dp_pad_top}; // @[NV_NVDLA_CSC_dl.scala 546:41:@661.4]
  assign _T_910 = 14'h0 - _GEN_243; // @[NV_NVDLA_CSC_dl.scala 546:41:@661.4]
  assign _T_911 = $unsigned(_T_910); // @[NV_NVDLA_CSC_dl.scala 546:41:@662.4]
  assign datain_h_cnt_st = _T_911[13:0]; // @[NV_NVDLA_CSC_dl.scala 546:41:@663.4]
  assign _GEN_244 = {{10'd0}, conv_y_stride}; // @[NV_NVDLA_CSC_dl.scala 547:37:@664.4]
  assign _T_912 = datain_h_cnt + _GEN_244; // @[NV_NVDLA_CSC_dl.scala 547:37:@664.4]
  assign datain_h_cnt_inc = datain_h_cnt + _GEN_244; // @[NV_NVDLA_CSC_dl.scala 547:37:@665.4]
  assign _T_913 = is_stripe_end & dl_group_end; // @[NV_NVDLA_CSC_dl.scala 548:52:@666.4]
  assign _T_914 = layer_st | _T_913; // @[NV_NVDLA_CSC_dl.scala 548:35:@667.4]
  assign _T_917 = is_w_end ? datain_h_cnt_inc : datain_h_cnt; // @[NV_NVDLA_CSC_dl.scala 550:25:@670.4]
  assign _T_918 = _T_779 ? datain_h_ori : _T_917; // @[NV_NVDLA_CSC_dl.scala 549:25:@671.4]
  assign datain_h_cnt_w = _T_914 ? datain_h_cnt_st : _T_918; // @[NV_NVDLA_CSC_dl.scala 548:25:@672.4]
  assign _T_921 = _T_779 | is_w_end; // @[NV_NVDLA_CSC_dl.scala 551:91:@675.4]
  assign _T_922 = dat_exec_valid & _T_921; // @[NV_NVDLA_CSC_dl.scala 551:54:@676.4]
  assign datain_h_cnt_reg_en = layer_st | _T_922; // @[NV_NVDLA_CSC_dl.scala 551:36:@677.4]
  assign _GEN_245 = {{1'd0}, dl_h_offset}; // @[NV_NVDLA_CSC_dl.scala 553:35:@681.4]
  assign dl_h_offset_ext = _GEN_245 * y_dilate; // @[NV_NVDLA_CSC_dl.scala 553:35:@681.4]
  assign _GEN_246 = {{3'd0}, dl_h_offset_ext}; // @[NV_NVDLA_CSC_dl.scala 554:33:@682.4]
  assign _T_925 = datain_h_cnt + _GEN_246; // @[NV_NVDLA_CSC_dl.scala 554:33:@682.4]
  assign _T_926 = datain_h_cnt + _GEN_246; // @[NV_NVDLA_CSC_dl.scala 554:33:@683.4]
  assign _GEN_247 = {{12'd0}, sub_h_cnt}; // @[NV_NVDLA_CSC_dl.scala 554:51:@684.4]
  assign _T_927 = _T_926 + _GEN_247; // @[NV_NVDLA_CSC_dl.scala 554:51:@684.4]
  assign datain_h_cur = _T_926 + _GEN_247; // @[NV_NVDLA_CSC_dl.scala 554:51:@685.4]
  assign _GEN_76 = datain_h_cnt_reg_en ? datain_h_cnt_w : datain_h_cnt; // @[NV_NVDLA_CSC_dl.scala 556:26:@686.4]
  assign _GEN_77 = dataout_w_ori_reg_en ? datain_h_cnt_w : datain_h_ori; // @[NV_NVDLA_CSC_dl.scala 557:26:@689.4]
  assign _T_928 = datain_w_cur[13]; // @[NV_NVDLA_CSC_dl.scala 560:39:@692.4]
  assign _GEN_248 = {{1'd0}, datain_width_cmp}; // @[NV_NVDLA_CSC_dl.scala 560:59:@693.4]
  assign _T_929 = datain_w_cur > _GEN_248; // @[NV_NVDLA_CSC_dl.scala 560:59:@693.4]
  assign _T_930 = _T_928 | _T_929; // @[NV_NVDLA_CSC_dl.scala 560:44:@694.4]
  assign _T_931 = datain_h_cur[13]; // @[NV_NVDLA_CSC_dl.scala 560:92:@695.4]
  assign _T_932 = _T_930 | _T_931; // @[NV_NVDLA_CSC_dl.scala 560:78:@696.4]
  assign _GEN_249 = {{1'd0}, datain_height_cmp}; // @[NV_NVDLA_CSC_dl.scala 560:112:@697.4]
  assign _T_933 = datain_h_cur > _GEN_249; // @[NV_NVDLA_CSC_dl.scala 560:112:@697.4]
  assign dat_conv_req_dummy = _T_932 | _T_933; // @[NV_NVDLA_CSC_dl.scala 560:97:@698.4]
  assign dat_img_req_dummy = _T_931 | _T_933; // @[NV_NVDLA_CSC_dl.scala 563:42:@708.4]
  assign _T_1016 = is_img_d1[10]; // @[NV_NVDLA_CSC_dl.scala 643:33:@794.4]
  assign _T_1017 = ~ is_last_channel; // @[NV_NVDLA_CSC_dl.scala 644:24:@795.4]
  assign _T_1018 = datain_c_cnt[0]; // @[NV_NVDLA_CSC_dl.scala 644:55:@796.4]
  assign _T_1019 = _T_1017 | _T_1018; // @[NV_NVDLA_CSC_dl.scala 644:41:@797.4]
  assign _T_1021 = datain_w_cur[12:0]; // @[NV_NVDLA_CSC_dl.scala 644:95:@798.4]
  assign _T_1022 = {2'h0,_T_1021}; // @[Cat.scala 30:58:@799.4]
  assign _T_1026 = _T_1019 ? _T_1022 : _T_1022; // @[NV_NVDLA_CSC_dl.scala 644:23:@802.4]
  assign w_bias_int8 = _T_1016 ? pixel_w_cur : _T_1026; // @[NV_NVDLA_CSC_dl.scala 643:23:@803.4]
  assign w_bias_w = w_bias_int8[13:0]; // @[NV_NVDLA_CSC_dl.scala 660:24:@805.4]
  assign _T_945 = w_bias_w[13:2]; // @[NV_NVDLA_CSC_dl.scala 567:32:@710.4]
  assign _GEN_251 = {{3'd0}, _T_945}; // @[NV_NVDLA_CSC_dl.scala 567:40:@711.4]
  assign dat_img_req_skip = _GEN_251 > entries_cmp; // @[NV_NVDLA_CSC_dl.scala 567:40:@711.4]
  assign _T_946 = is_img_d1[5]; // @[NV_NVDLA_CSC_dl.scala 568:34:@712.4]
  assign dat_req_dummy = _T_946 ? dat_img_req_dummy : dat_conv_req_dummy; // @[NV_NVDLA_CSC_dl.scala 568:24:@713.4]
  assign _T_947 = is_img_d1[6]; // @[NV_NVDLA_CSC_dl.scala 569:29:@714.4]
  assign dat_req_skip = _T_947 & dat_img_req_skip; // @[NV_NVDLA_CSC_dl.scala 569:33:@715.4]
  assign _T_948 = ~ dat_req_dummy; // @[NV_NVDLA_CSC_dl.scala 570:39:@716.4]
  assign _T_949 = dat_exec_valid & _T_948; // @[NV_NVDLA_CSC_dl.scala 570:37:@717.4]
  assign _T_950 = ~ dat_req_skip; // @[NV_NVDLA_CSC_dl.scala 570:56:@718.4]
  assign dat_req_valid = _T_949 & _T_950; // @[NV_NVDLA_CSC_dl.scala 570:54:@719.4]
  assign _T_951 = is_img_d1[7]; // @[NV_NVDLA_CSC_dl.scala 573:37:@720.4]
  assign _T_952 = ~ _T_951; // @[NV_NVDLA_CSC_dl.scala 573:27:@721.4]
  assign dat_req_sub_c_w = _T_952 ? _T_1018 : dl_block_end; // @[NV_NVDLA_CSC_dl.scala 573:26:@723.4]
  assign dat_req_sub_w_w = datain_w_cur[1:0]; // @[NV_NVDLA_CSC_dl.scala 574:35:@724.4]
  assign _T_955 = sub_h_cnt == 2'h0; // @[NV_NVDLA_CSC_dl.scala 575:55:@725.4]
  assign dat_req_sub_w_st_en = dat_exec_valid & _T_955; // @[NV_NVDLA_CSC_dl.scala 575:42:@726.4]
  assign dat_req_stripe_end = is_stripe_equal & dat_pipe_valid; // @[NV_NVDLA_CSC_dl.scala 578:42:@728.4]
  assign dat_req_flag_w = {dl_layer_end,dl_channel_end,dat_req_stripe_end,dl_pvld,batch_cnt}; // @[Cat.scala 30:58:@732.4]
  assign _T_979 = dl_dat_release & is_stripe_equal; // @[NV_NVDLA_CSC_dl.scala 605:38:@752.6]
  assign _T_980 = _T_979 & dat_pipe_valid; // @[NV_NVDLA_CSC_dl.scala 605:56:@753.6]
  assign _GEN_78 = dat_exec_valid ? dat_req_sub_w_w : dat_req_sub_w_d1; // @[NV_NVDLA_CSC_dl.scala 597:21:@744.4]
  assign _GEN_79 = dat_exec_valid ? sub_h_cnt : dat_req_sub_h_d1; // @[NV_NVDLA_CSC_dl.scala 597:21:@744.4]
  assign _GEN_80 = dat_exec_valid ? dat_req_sub_c_w : dat_req_sub_c_d1; // @[NV_NVDLA_CSC_dl.scala 597:21:@744.4]
  assign _GEN_81 = dat_exec_valid ? is_last_channel : dat_req_ch_end_d1; // @[NV_NVDLA_CSC_dl.scala 597:21:@744.4]
  assign _GEN_82 = dat_exec_valid ? dat_req_dummy : dat_req_dummy_d1; // @[NV_NVDLA_CSC_dl.scala 597:21:@744.4]
  assign _GEN_83 = dat_exec_valid ? dl_cur_sub_h : dat_req_cur_sub_h_d1; // @[NV_NVDLA_CSC_dl.scala 597:21:@744.4]
  assign _GEN_84 = dat_exec_valid ? dat_req_flag_w : dat_req_flag_d1; // @[NV_NVDLA_CSC_dl.scala 597:21:@744.4]
  assign _GEN_85 = dat_exec_valid ? _T_980 : dat_req_rls_d1; // @[NV_NVDLA_CSC_dl.scala 597:21:@744.4]
  assign _GEN_86 = dat_exec_valid ? pixel_force_fetch : pixel_force_fetch_d1; // @[NV_NVDLA_CSC_dl.scala 597:21:@744.4]
  assign _GEN_87 = dat_exec_valid ? pixel_force_clr : pixel_force_clr_d1; // @[NV_NVDLA_CSC_dl.scala 597:21:@744.4]
  assign _GEN_88 = dat_req_sub_w_st_en ? dl_pvld : dat_req_sub_w_st_d1; // @[NV_NVDLA_CSC_dl.scala 609:26:@758.4]
  assign _T_995 = is_img_d1[8]; // @[NV_NVDLA_CSC_dl.scala 626:32:@768.4]
  assign _T_996 = ~ _T_995; // @[NV_NVDLA_CSC_dl.scala 626:22:@769.4]
  assign _T_997 = datain_width[11:0]; // @[NV_NVDLA_CSC_dl.scala 626:49:@770.4]
  assign c_bias_add = _T_996 ? _T_997 : 12'h0; // @[NV_NVDLA_CSC_dl.scala 626:21:@771.4]
  assign _T_1000 = is_stripe_end & dl_channel_end; // @[NV_NVDLA_CSC_dl.scala 628:34:@772.4]
  assign _GEN_252 = {{2'd0}, c_bias_add}; // @[NV_NVDLA_CSC_dl.scala 628:64:@773.4]
  assign _T_1002 = c_bias + _GEN_252; // @[NV_NVDLA_CSC_dl.scala 628:64:@773.4]
  assign _T_1003 = c_bias + _GEN_252; // @[NV_NVDLA_CSC_dl.scala 628:64:@774.4]
  assign _T_1004 = _T_1000 ? 14'h0 : _T_1003; // @[NV_NVDLA_CSC_dl.scala 628:19:@775.4]
  assign c_bias_w = layer_st ? 14'h0 : _T_1004; // @[NV_NVDLA_CSC_dl.scala 627:19:@776.4]
  assign c_bias_d1_reg_en = c_bias != c_bias_d1; // @[NV_NVDLA_CSC_dl.scala 630:31:@780.4]
  assign _T_1007 = datain_h_cnt * h_bias_0_stride; // @[NV_NVDLA_CSC_dl.scala 633:32:@781.4]
  assign h_bias_0_w = _T_1007[13:0]; // @[NV_NVDLA_CSC_dl.scala 633:50:@782.4]
  assign _GEN_253 = {{9'd0}, dl_h_offset}; // @[NV_NVDLA_CSC_dl.scala 634:31:@783.4]
  assign _T_1008 = _GEN_253 * h_bias_1_stride; // @[NV_NVDLA_CSC_dl.scala 634:31:@783.4]
  assign h_bias_1_w = _T_1008[13:0]; // @[NV_NVDLA_CSC_dl.scala 634:49:@784.4]
  assign _GEN_254 = {{10'd0}, batch_cnt}; // @[NV_NVDLA_CSC_dl.scala 635:29:@785.4]
  assign _T_1009 = _GEN_254 * h_bias_2_stride; // @[NV_NVDLA_CSC_dl.scala 635:29:@785.4]
  assign h_bias_2_w = _T_1009[13:0]; // @[NV_NVDLA_CSC_dl.scala 635:47:@786.4]
  assign _GEN_255 = {{13'd0}, sub_h_cnt}; // @[NV_NVDLA_CSC_dl.scala 636:47:@787.4]
  assign _T_1011 = _GEN_255 * h_bias_3_stride; // @[NV_NVDLA_CSC_dl.scala 636:47:@787.4]
  assign _T_1012 = layer_st ? 17'h0 : _T_1011; // @[NV_NVDLA_CSC_dl.scala 636:21:@788.4]
  assign h_bias_3_w = _T_1012[13:0]; // @[NV_NVDLA_CSC_dl.scala 636:65:@789.4]
  assign _T_1013 = is_img_d1[9]; // @[NV_NVDLA_CSC_dl.scala 637:45:@790.4]
  assign _T_1014 = layer_st | _T_1013; // @[NV_NVDLA_CSC_dl.scala 637:34:@791.4]
  assign h_bias_reg_en = {_T_1014,dat_exec_valid}; // @[Cat.scala 30:58:@792.4]
  assign dat_req_base_d1 = dat_entry_st[13:0]; // @[NV_NVDLA_CSC_dl.scala 662:35:@807.4]
  assign _GEN_89 = datain_c_cnt_reg_en ? c_bias_w : c_bias; // @[NV_NVDLA_CSC_dl.scala 664:20:@808.4]
  assign _GEN_90 = c_bias_d1_reg_en ? c_bias : c_bias_d1; // @[NV_NVDLA_CSC_dl.scala 667:23:@811.4]
  assign _T_1029 = h_bias_reg_en[0]; // @[NV_NVDLA_CSC_dl.scala 670:19:@814.4]
  assign _GEN_91 = _T_1029 ? h_bias_0_w : h_bias_0_d1; // @[NV_NVDLA_CSC_dl.scala 670:23:@815.4]
  assign _GEN_92 = _T_1029 ? h_bias_1_w : h_bias_1_d1; // @[NV_NVDLA_CSC_dl.scala 670:23:@815.4]
  assign _GEN_93 = _T_1029 ? h_bias_2_w : h_bias_2_d1; // @[NV_NVDLA_CSC_dl.scala 670:23:@815.4]
  assign _T_1030 = h_bias_reg_en[1]; // @[NV_NVDLA_CSC_dl.scala 675:19:@820.4]
  assign _GEN_94 = _T_1030 ? h_bias_3_w : h_bias_3_d1; // @[NV_NVDLA_CSC_dl.scala 675:23:@821.4]
  assign _GEN_95 = dat_exec_valid ? w_bias_w : w_bias_d1; // @[NV_NVDLA_CSC_dl.scala 678:20:@824.4]
  assign _T_1119 = h_bias_0_d1 + h_bias_1_d1; // @[NV_NVDLA_CSC_dl.scala 702:30:@852.4]
  assign _T_1120 = h_bias_0_d1 + h_bias_1_d1; // @[NV_NVDLA_CSC_dl.scala 702:30:@853.4]
  assign _T_1121 = _T_1120 + h_bias_2_d1; // @[NV_NVDLA_CSC_dl.scala 702:44:@854.4]
  assign _T_1122 = _T_1120 + h_bias_2_d1; // @[NV_NVDLA_CSC_dl.scala 702:44:@855.4]
  assign _T_1123 = _T_1122 + h_bias_3_d1; // @[NV_NVDLA_CSC_dl.scala 702:58:@856.4]
  assign h_bias_d1 = _T_1122 + h_bias_3_d1; // @[NV_NVDLA_CSC_dl.scala 702:58:@857.4]
  assign _T_1125 = dat_req_base_d1 + c_bias_d1; // @[NV_NVDLA_CSC_dl.scala 703:41:@859.4]
  assign _GEN_256 = {{1'd0}, h_bias_d1}; // @[NV_NVDLA_CSC_dl.scala 703:54:@860.4]
  assign _T_1126 = _T_1125 + _GEN_256; // @[NV_NVDLA_CSC_dl.scala 703:54:@860.4]
  assign _GEN_257 = {{2'd0}, w_bias_d1}; // @[NV_NVDLA_CSC_dl.scala 703:67:@861.4]
  assign _T_1127 = _T_1126 + _GEN_257; // @[NV_NVDLA_CSC_dl.scala 703:67:@861.4]
  assign dat_req_addr_sum = _T_1127[14:0]; // @[NV_NVDLA_CSC_dl.scala 703:80:@862.4]
  assign is_dat_req_addr_wrap = dat_req_addr_sum >= _GEN_230; // @[NV_NVDLA_CSC_dl.scala 704:45:@865.4]
  assign _T_1140 = dat_req_addr_sum - _GEN_230; // @[NV_NVDLA_CSC_dl.scala 705:43:@868.4]
  assign _T_1141 = $unsigned(_T_1140); // @[NV_NVDLA_CSC_dl.scala 705:43:@869.4]
  assign dat_req_addr_wrap = _T_1141[13:0]; // @[NV_NVDLA_CSC_dl.scala 705:103:@870.4]
  assign _T_1142 = layer_st | dat_req_dummy_d1; // @[NV_NVDLA_CSC_dl.scala 706:35:@871.4]
  assign _T_1148 = is_dat_req_addr_wrap ? {{1'd0}, dat_req_addr_wrap} : dat_req_addr_sum; // @[NV_NVDLA_CSC_dl.scala 707:25:@873.4]
  assign _T_1149 = _T_1142 ? 15'h3fff : _T_1148; // @[NV_NVDLA_CSC_dl.scala 706:25:@874.4]
  assign dat_req_addr_w = _T_1149[13:0]; // @[NV_NVDLA_CSC_dl.scala 707:85:@875.4]
  assign _T_1169 = 2'h3 == dat_req_sub_h_d1; // @[Mux.scala 46:19:@885.4]
  assign _T_1170 = _T_1169 ? dat_req_sub_h_addr_3 : 14'h0; // @[Mux.scala 46:16:@886.4]
  assign _T_1171 = 2'h2 == dat_req_sub_h_d1; // @[Mux.scala 46:19:@887.4]
  assign _T_1172 = _T_1171 ? dat_req_sub_h_addr_2 : _T_1170; // @[Mux.scala 46:16:@888.4]
  assign _T_1173 = 2'h1 == dat_req_sub_h_d1; // @[Mux.scala 46:19:@889.4]
  assign _T_1174 = _T_1173 ? dat_req_sub_h_addr_1 : _T_1172; // @[Mux.scala 46:16:@890.4]
  assign _T_1175 = 2'h0 == dat_req_sub_h_d1; // @[Mux.scala 46:19:@891.4]
  assign dat_req_addr_last = _T_1175 ? dat_req_sub_h_addr_0 : _T_1174; // @[Mux.scala 46:16:@892.4]
  assign _T_1176 = dat_req_addr_last != dat_req_addr_w; // @[NV_NVDLA_CSC_dl.scala 714:65:@893.4]
  assign _T_1177 = _T_1176 | pixel_force_fetch_d1; // @[NV_NVDLA_CSC_dl.scala 714:85:@894.4]
  assign sc2buf_dat_rd_en_w = dat_req_valid_d1 & _T_1177; // @[NV_NVDLA_CSC_dl.scala 714:43:@895.4]
  assign _T_1178 = dat_req_valid_d1 | dat_req_dummy_d1; // @[NV_NVDLA_CSC_dl.scala 716:38:@896.4]
  assign _T_1180 = dat_req_sub_h_d1 == 2'h0; // @[NV_NVDLA_CSC_dl.scala 716:78:@897.4]
  assign _T_1181 = _T_1178 & _T_1180; // @[NV_NVDLA_CSC_dl.scala 716:58:@898.4]
  assign dat_req_sub_h_addr_en_0 = layer_st | _T_1181; // @[NV_NVDLA_CSC_dl.scala 716:17:@899.4]
  assign _T_1185 = dat_req_sub_h_d1 == 2'h1; // @[NV_NVDLA_CSC_dl.scala 716:78:@901.4]
  assign _T_1186 = _T_1178 & _T_1185; // @[NV_NVDLA_CSC_dl.scala 716:58:@902.4]
  assign dat_req_sub_h_addr_en_1 = layer_st | _T_1186; // @[NV_NVDLA_CSC_dl.scala 716:17:@903.4]
  assign _T_1190 = dat_req_sub_h_d1 == 2'h2; // @[NV_NVDLA_CSC_dl.scala 716:78:@905.4]
  assign _T_1191 = _T_1178 & _T_1190; // @[NV_NVDLA_CSC_dl.scala 716:58:@906.4]
  assign dat_req_sub_h_addr_en_2 = layer_st | _T_1191; // @[NV_NVDLA_CSC_dl.scala 716:17:@907.4]
  assign _T_1195 = dat_req_sub_h_d1 == 2'h3; // @[NV_NVDLA_CSC_dl.scala 716:78:@909.4]
  assign _T_1196 = _T_1178 & _T_1195; // @[NV_NVDLA_CSC_dl.scala 716:58:@910.4]
  assign dat_req_sub_h_addr_en_3 = layer_st | _T_1196; // @[NV_NVDLA_CSC_dl.scala 716:17:@911.4]
  assign _GEN_96 = dat_req_sub_h_addr_en_0 ? dat_req_addr_w : dat_req_sub_h_addr_0; // @[NV_NVDLA_CSC_dl.scala 723:35:@917.4]
  assign _GEN_97 = dat_req_sub_h_addr_en_1 ? dat_req_addr_w : dat_req_sub_h_addr_1; // @[NV_NVDLA_CSC_dl.scala 723:35:@920.4]
  assign _GEN_98 = dat_req_sub_h_addr_en_2 ? dat_req_addr_w : dat_req_sub_h_addr_2; // @[NV_NVDLA_CSC_dl.scala 723:35:@923.4]
  assign _GEN_99 = dat_req_sub_h_addr_en_3 ? dat_req_addr_w : dat_req_sub_h_addr_3; // @[NV_NVDLA_CSC_dl.scala 723:35:@926.4]
  assign _T_1207 = layer_st | sc2buf_dat_rd_en_w; // @[NV_NVDLA_CSC_dl.scala 729:14:@930.4]
  assign _GEN_100 = _T_1207 ? dat_req_addr_w : sc2buf_dat_rd_addr_out; // @[NV_NVDLA_CSC_dl.scala 729:34:@931.4]
  assign _GEN_101 = dat_exec_valid_d1 ? dat_req_sub_w_d1 : dat_req_pipe_sub_w; // @[NV_NVDLA_CSC_dl.scala 736:24:@942.4]
  assign _GEN_102 = dat_exec_valid_d1 ? dat_req_sub_h_d1 : dat_req_pipe_sub_h; // @[NV_NVDLA_CSC_dl.scala 736:24:@942.4]
  assign _GEN_103 = dat_exec_valid_d1 ? dat_req_sub_c_d1 : dat_req_pipe_sub_c; // @[NV_NVDLA_CSC_dl.scala 736:24:@942.4]
  assign _GEN_104 = dat_exec_valid_d1 ? dat_req_ch_end_d1 : dat_req_pipe_ch_end; // @[NV_NVDLA_CSC_dl.scala 736:24:@942.4]
  assign _GEN_105 = dat_exec_valid_d1 ? dat_req_bytes_d1 : dat_req_pipe_bytes; // @[NV_NVDLA_CSC_dl.scala 736:24:@942.4]
  assign _GEN_106 = dat_exec_valid_d1 ? dat_req_dummy_d1 : dat_req_pipe_dummy; // @[NV_NVDLA_CSC_dl.scala 736:24:@942.4]
  assign _GEN_107 = dat_exec_valid_d1 ? dat_req_cur_sub_h_d1 : dat_req_pipe_cur_sub_h; // @[NV_NVDLA_CSC_dl.scala 736:24:@942.4]
  assign _GEN_108 = dat_exec_valid_d1 ? dat_req_sub_w_st_d1 : dat_req_pipe_sub_w_st; // @[NV_NVDLA_CSC_dl.scala 736:24:@942.4]
  assign _GEN_109 = dat_exec_valid_d1 ? dat_req_rls_d1 : dat_req_pipe_rls; // @[NV_NVDLA_CSC_dl.scala 736:24:@942.4]
  assign _GEN_110 = dat_exec_valid_d1 ? dat_req_flag_d1 : dat_req_pipe_flag; // @[NV_NVDLA_CSC_dl.scala 736:24:@942.4]
  assign _T_1223 = {1'h0,dat_req_pipe_ch_end,dat_req_pipe_sub_c,dat_req_pipe_sub_h,dat_req_pipe_sub_w}; // @[Cat.scala 30:58:@962.4]
  assign dat_req_pipe_pd = {dat_req_pipe_flag,dat_req_pipe_rls,dat_req_pipe_sub_w_st,dat_req_pipe_dummy,dat_req_pipe_cur_sub_h,dat_req_pipe_bytes,_T_1223}; // @[Cat.scala 30:58:@968.4]
  assign _GEN_111 = dat_req_pipe_pvld ? dat_req_pipe_pd : _T_1252; // @[NV_NVDLA_CSC_dl.scala 781:33:@1010.4]
  assign _GEN_112 = dat_req_exec_pvld ? dat_req_pipe_dummy : _T_1290; // @[NV_NVDLA_CSC_dl.scala 785:33:@1014.4]
  assign _GEN_113 = dat_req_exec_pvld ? dat_req_pipe_sub_h : _T_1309; // @[NV_NVDLA_CSC_dl.scala 785:33:@1014.4]
  assign _GEN_114 = _T_1233 ? _T_1252 : _T_1255; // @[NV_NVDLA_CSC_dl.scala 781:33:@1019.4]
  assign _GEN_115 = _T_1271 ? _T_1290 : _T_1293; // @[NV_NVDLA_CSC_dl.scala 785:33:@1023.4]
  assign _GEN_116 = _T_1271 ? _T_1309 : _T_1312; // @[NV_NVDLA_CSC_dl.scala 785:33:@1023.4]
  assign _GEN_117 = _T_1236 ? _T_1255 : _T_1258; // @[NV_NVDLA_CSC_dl.scala 781:33:@1028.4]
  assign _GEN_118 = _T_1274 ? _T_1293 : _T_1296; // @[NV_NVDLA_CSC_dl.scala 785:33:@1032.4]
  assign _GEN_119 = _T_1274 ? _T_1312 : _T_1315; // @[NV_NVDLA_CSC_dl.scala 785:33:@1032.4]
  assign _GEN_120 = _T_1239 ? _T_1258 : _T_1261; // @[NV_NVDLA_CSC_dl.scala 781:33:@1037.4]
  assign _GEN_121 = _T_1277 ? _T_1296 : _T_1299; // @[NV_NVDLA_CSC_dl.scala 785:33:@1041.4]
  assign _GEN_122 = _T_1277 ? _T_1315 : _T_1318; // @[NV_NVDLA_CSC_dl.scala 785:33:@1041.4]
  assign _GEN_123 = _T_1242 ? _T_1261 : _T_1264; // @[NV_NVDLA_CSC_dl.scala 781:33:@1046.4]
  assign _GEN_124 = _T_1280 ? _T_1299 : _T_1302; // @[NV_NVDLA_CSC_dl.scala 785:33:@1050.4]
  assign _GEN_125 = _T_1280 ? _T_1318 : _T_1321; // @[NV_NVDLA_CSC_dl.scala 785:33:@1050.4]
  assign _GEN_126 = _T_1245 ? _T_1264 : dat_rsp_pipe_pd; // @[NV_NVDLA_CSC_dl.scala 781:33:@1055.4]
  assign _GEN_127 = _T_1283 ? _T_1302 : dat_rsp_exec_dummy; // @[NV_NVDLA_CSC_dl.scala 785:33:@1059.4]
  assign _GEN_128 = _T_1283 ? _T_1321 : dat_rsp_exec_sub_h; // @[NV_NVDLA_CSC_dl.scala 785:33:@1059.4]
  assign dat_rsp_pipe_sub_w = dat_rsp_pipe_pd[1:0]; // @[NV_NVDLA_CSC_dl.scala 798:41:@1063.4]
  assign dat_rsp_pipe_sub_h = dat_rsp_pipe_pd[3:2]; // @[NV_NVDLA_CSC_dl.scala 799:41:@1064.4]
  assign dat_rsp_pipe_sub_c = dat_rsp_pipe_pd[4]; // @[NV_NVDLA_CSC_dl.scala 800:41:@1065.4]
  assign dat_rsp_pipe_ch_end = dat_rsp_pipe_pd[5]; // @[NV_NVDLA_CSC_dl.scala 801:42:@1066.4]
  assign dat_rsp_pipe_bytes = dat_rsp_pipe_pd[14:7]; // @[NV_NVDLA_CSC_dl.scala 802:41:@1067.4]
  assign dat_rsp_pipe_cur_sub_h = dat_rsp_pipe_pd[16:15]; // @[NV_NVDLA_CSC_dl.scala 803:45:@1068.4]
  assign dat_rsp_pipe_rls = dat_rsp_pipe_pd[19]; // @[NV_NVDLA_CSC_dl.scala 806:39:@1071.4]
  assign dat_rsp_pipe_flag = dat_rsp_pipe_pd[28:20]; // @[NV_NVDLA_CSC_dl.scala 807:40:@1072.4]
  assign _T_1349 = dat_rsp_exec_sub_h == 2'h0; // @[NV_NVDLA_CSC_dl.scala 830:70:@1089.4]
  assign dat_l0c0_en = io_sc2buf_dat_rd_data_valid & _T_1349; // @[NV_NVDLA_CSC_dl.scala 830:48:@1090.4]
  assign _T_1351 = dat_rsp_exec_sub_h == 2'h1; // @[NV_NVDLA_CSC_dl.scala 831:70:@1091.4]
  assign dat_l1c0_en = io_sc2buf_dat_rd_data_valid & _T_1351; // @[NV_NVDLA_CSC_dl.scala 831:48:@1092.4]
  assign _T_1353 = dat_rsp_exec_sub_h == 2'h2; // @[NV_NVDLA_CSC_dl.scala 832:70:@1093.4]
  assign dat_l2c0_en = io_sc2buf_dat_rd_data_valid & _T_1353; // @[NV_NVDLA_CSC_dl.scala 832:48:@1094.4]
  assign _T_1355 = dat_rsp_exec_sub_h == 2'h3; // @[NV_NVDLA_CSC_dl.scala 833:70:@1095.4]
  assign dat_l3c0_en = io_sc2buf_dat_rd_data_valid & _T_1355; // @[NV_NVDLA_CSC_dl.scala 833:48:@1096.4]
  assign _T_1359 = is_img_d1[12]; // @[NV_NVDLA_CSC_dl.scala 837:69:@1100.4]
  assign _T_1360 = _T_1359 & dat_l0c0_en; // @[NV_NVDLA_CSC_dl.scala 837:74:@1101.4]
  assign _T_1361 = ~ dat_l0c0_dummy; // @[NV_NVDLA_CSC_dl.scala 837:90:@1102.4]
  assign dat_l0c1_en = _T_1360 & _T_1361; // @[NV_NVDLA_CSC_dl.scala 837:88:@1103.4]
  assign _T_1365 = is_img_d1[13]; // @[NV_NVDLA_CSC_dl.scala 838:68:@1107.4]
  assign _T_1366 = _T_1365 & dat_l1c0_en; // @[NV_NVDLA_CSC_dl.scala 838:73:@1108.4]
  assign _T_1367 = ~ dat_l1c0_dummy; // @[NV_NVDLA_CSC_dl.scala 838:89:@1109.4]
  assign dat_l1c1_en = _T_1366 & _T_1367; // @[NV_NVDLA_CSC_dl.scala 838:87:@1110.4]
  assign _T_1369 = is_img_d1[15]; // @[NV_NVDLA_CSC_dl.scala 839:29:@1112.4]
  assign _T_1370 = _T_1369 & dat_l2c0_en; // @[NV_NVDLA_CSC_dl.scala 839:34:@1113.4]
  assign _T_1371 = ~ dat_l2c0_dummy; // @[NV_NVDLA_CSC_dl.scala 839:50:@1114.4]
  assign dat_l2c1_en = _T_1370 & _T_1371; // @[NV_NVDLA_CSC_dl.scala 839:48:@1115.4]
  assign _T_1372 = is_img_d1[16]; // @[NV_NVDLA_CSC_dl.scala 840:29:@1116.4]
  assign _T_1373 = _T_1372 & dat_l3c0_en; // @[NV_NVDLA_CSC_dl.scala 840:34:@1117.4]
  assign _T_1374 = ~ dat_l3c0_dummy; // @[NV_NVDLA_CSC_dl.scala 840:50:@1118.4]
  assign dat_l3c1_en = _T_1373 & _T_1374; // @[NV_NVDLA_CSC_dl.scala 840:48:@1119.4]
  assign _T_1375 = dat_rsp_exec_pvld & dat_rsp_exec_dummy; // @[NV_NVDLA_CSC_dl.scala 842:41:@1120.4]
  assign dat_dummy_l0_en = _T_1375 & _T_1349; // @[NV_NVDLA_CSC_dl.scala 842:62:@1122.4]
  assign dat_dummy_l1_en = _T_1375 & _T_1351; // @[NV_NVDLA_CSC_dl.scala 843:62:@1125.4]
  assign dat_dummy_l2_en = _T_1375 & _T_1353; // @[NV_NVDLA_CSC_dl.scala 844:62:@1128.4]
  assign dat_dummy_l3_en = _T_1375 & _T_1355; // @[NV_NVDLA_CSC_dl.scala 845:62:@1131.4]
  assign dat_l0_set = dat_l0c0_en | dat_dummy_l0_en; // @[NV_NVDLA_CSC_dl.scala 847:30:@1132.4]
  assign dat_l1_set = dat_l1c0_en | dat_dummy_l1_en; // @[NV_NVDLA_CSC_dl.scala 848:30:@1133.4]
  assign dat_l2_set = dat_l2c0_en | dat_dummy_l2_en; // @[NV_NVDLA_CSC_dl.scala 849:30:@1134.4]
  assign dat_l3_set = dat_l3c0_en | dat_dummy_l3_en; // @[NV_NVDLA_CSC_dl.scala 850:30:@1135.4]
  assign _T_1389 = dat_dummy_l0_en ? 1'h1 : dat_l0c0_dummy; // @[NV_NVDLA_CSC_dl.scala 852:48:@1136.4]
  assign _T_1390 = dat_l0c0_en ? 1'h0 : _T_1389; // @[NV_NVDLA_CSC_dl.scala 852:22:@1137.4]
  assign _T_1393 = dat_dummy_l1_en ? 1'h1 : dat_l1c0_dummy; // @[NV_NVDLA_CSC_dl.scala 853:48:@1139.4]
  assign _T_1394 = dat_l1c0_en ? 1'h0 : _T_1393; // @[NV_NVDLA_CSC_dl.scala 853:22:@1140.4]
  assign _T_1397 = dat_dummy_l2_en ? 1'h1 : dat_l2c0_dummy; // @[NV_NVDLA_CSC_dl.scala 854:48:@1142.4]
  assign _T_1398 = dat_l2c0_en ? 1'h0 : _T_1397; // @[NV_NVDLA_CSC_dl.scala 854:22:@1143.4]
  assign _T_1401 = dat_dummy_l3_en ? 1'h1 : dat_l3c0_dummy; // @[NV_NVDLA_CSC_dl.scala 855:48:@1145.4]
  assign _T_1402 = dat_l3c0_en ? 1'h0 : _T_1401; // @[NV_NVDLA_CSC_dl.scala 855:22:@1146.4]
  assign _T_1404 = dat_l0_set ? dat_l0c0_dummy : dat_l0c1_dummy; // @[NV_NVDLA_CSC_dl.scala 856:48:@1148.4]
  assign _T_1405 = dat_l0c1_en ? 1'h0 : _T_1404; // @[NV_NVDLA_CSC_dl.scala 856:22:@1149.4]
  assign _T_1408 = sub_h_total_g2 != 2'h0; // @[NV_NVDLA_CSC_dl.scala 857:77:@1151.4]
  assign _T_1409 = dat_l1_set & _T_1408; // @[NV_NVDLA_CSC_dl.scala 857:60:@1152.4]
  assign _T_1410 = _T_1409 ? dat_l1c0_dummy : dat_l1c1_dummy; // @[NV_NVDLA_CSC_dl.scala 857:48:@1153.4]
  assign _T_1411 = dat_l1c1_en ? 1'h0 : _T_1410; // @[NV_NVDLA_CSC_dl.scala 857:22:@1154.4]
  assign _T_1413 = sub_h_total_g2[1]; // @[NV_NVDLA_CSC_dl.scala 858:76:@1156.4]
  assign _T_1414 = dat_l2_set & _T_1413; // @[NV_NVDLA_CSC_dl.scala 858:60:@1157.4]
  assign _T_1415 = _T_1414 ? dat_l2c0_dummy : dat_l2c1_dummy; // @[NV_NVDLA_CSC_dl.scala 858:48:@1158.4]
  assign _T_1416 = dat_l2c1_en ? 1'h0 : _T_1415; // @[NV_NVDLA_CSC_dl.scala 858:22:@1159.4]
  assign _T_1419 = dat_l3_set & _T_1413; // @[NV_NVDLA_CSC_dl.scala 859:60:@1162.4]
  assign _T_1420 = _T_1419 ? dat_l3c0_dummy : dat_l3c1_dummy; // @[NV_NVDLA_CSC_dl.scala 859:48:@1163.4]
  assign _T_1421 = dat_l3c1_en ? 1'h0 : _T_1420; // @[NV_NVDLA_CSC_dl.scala 859:22:@1164.4]
  assign _T_1470 = {dat_rsp_pipe_flag,dat_rsp_pipe_rls,dat_rsp_pipe_cur_sub_h,dat_rsp_pipe_bytes,1'h0,dat_rsp_pipe_ch_end,dat_rsp_pipe_sub_c,dat_rsp_pipe_sub_h,dat_rsp_pipe_sub_w}; // @[Cat.scala 30:58:@1216.4]
  assign _GEN_137 = dat_rsp_pipe_pvld ? _T_1470 : _T_1452; // @[NV_NVDLA_CSC_dl.scala 895:28:@1219.4]
  assign _GEN_138 = dat_rsp_l0_pvld ? _T_1452 : _T_1455; // @[NV_NVDLA_CSC_dl.scala 895:28:@1223.4]
  assign _GEN_139 = dat_rsp_l1_pvld ? _T_1455 : _T_1458; // @[NV_NVDLA_CSC_dl.scala 895:28:@1227.4]
  assign _GEN_140 = dat_rsp_l2_pvld ? _T_1458 : _T_1461; // @[NV_NVDLA_CSC_dl.scala 895:28:@1231.4]
  assign dat_rsp_l0_sub_c = _T_1452[4]; // @[NV_NVDLA_CSC_dl.scala 913:39:@1257.4]
  assign dat_rsp_l1_sub_c = _T_1455[4]; // @[NV_NVDLA_CSC_dl.scala 914:39:@1258.4]
  assign dat_rsp_l2_sub_c = _T_1458[4]; // @[NV_NVDLA_CSC_dl.scala 915:39:@1259.4]
  assign dat_rsp_l3_sub_c = _T_1461[4]; // @[NV_NVDLA_CSC_dl.scala 916:39:@1260.4]
  assign dat_rsp_l0_flag = _T_1452[26:18]; // @[NV_NVDLA_CSC_dl.scala 918:38:@1261.4]
  assign dat_rsp_l1_flag = _T_1455[26:18]; // @[NV_NVDLA_CSC_dl.scala 919:38:@1262.4]
  assign dat_rsp_l2_flag = _T_1458[26:18]; // @[NV_NVDLA_CSC_dl.scala 920:38:@1263.4]
  assign dat_rsp_l3_flag = _T_1461[26:18]; // @[NV_NVDLA_CSC_dl.scala 921:38:@1264.4]
  assign dat_rsp_l0_stripe_end = dat_rsp_l0_flag[6]; // @[NV_NVDLA_CSC_dl.scala 923:44:@1265.4]
  assign dat_rsp_l1_stripe_end = dat_rsp_l1_flag[6]; // @[NV_NVDLA_CSC_dl.scala 924:44:@1266.4]
  assign dat_rsp_l2_stripe_end = dat_rsp_l2_flag[6]; // @[NV_NVDLA_CSC_dl.scala 925:44:@1267.4]
  assign dat_rsp_l3_stripe_end = dat_rsp_l3_flag[6]; // @[NV_NVDLA_CSC_dl.scala 926:44:@1268.4]
  assign dat_rsp_bytes = dat_rsp_pd[14:7]; // @[NV_NVDLA_CSC_dl.scala 933:31:@1273.4]
  assign dat_rsp_cur_sub_h = dat_rsp_pd[16:15]; // @[NV_NVDLA_CSC_dl.scala 934:35:@1274.4]
  assign dat_rsp_flag = dat_rsp_pd[26:18]; // @[NV_NVDLA_CSC_dl.scala 936:30:@1277.4]
  assign rsp_sft_cnt_l0_sub = dat_l0c0_en ? 8'h8 : 8'h0; // @[NV_NVDLA_CSC_dl.scala 945:29:@1283.4]
  assign rsp_sft_cnt_l1_sub = dat_l1c0_en ? 8'h8 : 8'h0; // @[NV_NVDLA_CSC_dl.scala 946:29:@1284.4]
  assign rsp_sft_cnt_l2_sub = dat_l2c0_en ? 8'h8 : 8'h0; // @[NV_NVDLA_CSC_dl.scala 947:29:@1285.4]
  assign rsp_sft_cnt_l3_sub = dat_l3c0_en ? 8'h8 : 8'h0; // @[NV_NVDLA_CSC_dl.scala 948:29:@1286.4]
  assign _T_1508 = pixel_x_byte_stride > 7'h8; // @[NV_NVDLA_CSC_dl.scala 950:50:@1287.4]
  assign _GEN_260 = {{1'd0}, pixel_x_byte_stride}; // @[NV_NVDLA_CSC_dl.scala 950:111:@1288.4]
  assign _T_1510 = rsp_sft_cnt_l0 + _GEN_260; // @[NV_NVDLA_CSC_dl.scala 950:111:@1288.4]
  assign _T_1511 = rsp_sft_cnt_l0 + _GEN_260; // @[NV_NVDLA_CSC_dl.scala 950:111:@1289.4]
  assign _T_1512 = _T_1511 - rsp_sft_cnt_l0_sub; // @[NV_NVDLA_CSC_dl.scala 950:133:@1290.4]
  assign _T_1513 = $unsigned(_T_1512); // @[NV_NVDLA_CSC_dl.scala 950:133:@1291.4]
  assign _T_1514 = _T_1513[7:0]; // @[NV_NVDLA_CSC_dl.scala 950:133:@1292.4]
  assign rsp_sft_cnt_l0_inc = _T_1508 ? 8'h8 : _T_1514; // @[NV_NVDLA_CSC_dl.scala 950:29:@1293.4]
  assign _T_1518 = rsp_sft_cnt_l1 + _GEN_260; // @[NV_NVDLA_CSC_dl.scala 951:111:@1295.4]
  assign _T_1519 = rsp_sft_cnt_l1 + _GEN_260; // @[NV_NVDLA_CSC_dl.scala 951:111:@1296.4]
  assign _T_1520 = _T_1519 - rsp_sft_cnt_l1_sub; // @[NV_NVDLA_CSC_dl.scala 951:133:@1297.4]
  assign _T_1521 = $unsigned(_T_1520); // @[NV_NVDLA_CSC_dl.scala 951:133:@1298.4]
  assign _T_1522 = _T_1521[7:0]; // @[NV_NVDLA_CSC_dl.scala 951:133:@1299.4]
  assign rsp_sft_cnt_l1_inc = _T_1508 ? 8'h8 : _T_1522; // @[NV_NVDLA_CSC_dl.scala 951:29:@1300.4]
  assign _T_1526 = rsp_sft_cnt_l2 + _GEN_260; // @[NV_NVDLA_CSC_dl.scala 952:111:@1302.4]
  assign _T_1527 = rsp_sft_cnt_l2 + _GEN_260; // @[NV_NVDLA_CSC_dl.scala 952:111:@1303.4]
  assign _T_1528 = _T_1527 - rsp_sft_cnt_l2_sub; // @[NV_NVDLA_CSC_dl.scala 952:133:@1304.4]
  assign _T_1529 = $unsigned(_T_1528); // @[NV_NVDLA_CSC_dl.scala 952:133:@1305.4]
  assign _T_1530 = _T_1529[7:0]; // @[NV_NVDLA_CSC_dl.scala 952:133:@1306.4]
  assign rsp_sft_cnt_l2_inc = _T_1508 ? 8'h8 : _T_1530; // @[NV_NVDLA_CSC_dl.scala 952:29:@1307.4]
  assign _T_1534 = rsp_sft_cnt_l3 + _GEN_260; // @[NV_NVDLA_CSC_dl.scala 953:111:@1309.4]
  assign _T_1535 = rsp_sft_cnt_l3 + _GEN_260; // @[NV_NVDLA_CSC_dl.scala 953:111:@1310.4]
  assign _T_1536 = _T_1535 - rsp_sft_cnt_l3_sub; // @[NV_NVDLA_CSC_dl.scala 953:133:@1311.4]
  assign _T_1537 = $unsigned(_T_1536); // @[NV_NVDLA_CSC_dl.scala 953:133:@1312.4]
  assign _T_1538 = _T_1537[7:0]; // @[NV_NVDLA_CSC_dl.scala 953:133:@1313.4]
  assign rsp_sft_cnt_l3_inc = _T_1508 ? 8'h8 : _T_1538; // @[NV_NVDLA_CSC_dl.scala 953:29:@1314.4]
  assign _T_1540 = ~ dat_rsp_l0_sub_c; // @[NV_NVDLA_CSC_dl.scala 962:52:@1315.4]
  assign _T_1541 = dat_rsp_l0_stripe_end & _T_1540; // @[NV_NVDLA_CSC_dl.scala 962:50:@1316.4]
  assign _T_1542 = dat_rsp_l0_stripe_end & dat_rsp_l0_sub_c; // @[NV_NVDLA_CSC_dl.scala 963:50:@1317.4]
  assign _T_1545 = rsp_sft_cnt_l0_inc & 8'h7; // @[NV_NVDLA_CSC_dl.scala 964:64:@1318.4]
  assign _T_1546 = dat_dummy_l0_en ? _T_1545 : rsp_sft_cnt_l0_inc; // @[NV_NVDLA_CSC_dl.scala 964:27:@1319.4]
  assign _T_1547 = _T_1542 ? 8'h8 : _T_1546; // @[NV_NVDLA_CSC_dl.scala 963:27:@1320.4]
  assign _T_1548 = _T_1541 ? rsp_sft_cnt_l0_ori : _T_1547; // @[NV_NVDLA_CSC_dl.scala 962:27:@1321.4]
  assign rsp_sft_cnt_l0_w = layer_st ? 8'h8 : _T_1548; // @[NV_NVDLA_CSC_dl.scala 961:27:@1322.4]
  assign _T_1550 = ~ dat_rsp_l1_sub_c; // @[NV_NVDLA_CSC_dl.scala 967:52:@1323.4]
  assign _T_1551 = dat_rsp_l1_stripe_end & _T_1550; // @[NV_NVDLA_CSC_dl.scala 967:50:@1324.4]
  assign _T_1552 = dat_rsp_l1_stripe_end & dat_rsp_l1_sub_c; // @[NV_NVDLA_CSC_dl.scala 968:50:@1325.4]
  assign _T_1555 = rsp_sft_cnt_l1_inc & 8'h7; // @[NV_NVDLA_CSC_dl.scala 969:64:@1326.4]
  assign _T_1556 = dat_dummy_l1_en ? _T_1555 : rsp_sft_cnt_l1_inc; // @[NV_NVDLA_CSC_dl.scala 969:27:@1327.4]
  assign _T_1557 = _T_1552 ? 8'h8 : _T_1556; // @[NV_NVDLA_CSC_dl.scala 968:27:@1328.4]
  assign _T_1558 = _T_1551 ? rsp_sft_cnt_l1_ori : _T_1557; // @[NV_NVDLA_CSC_dl.scala 967:27:@1329.4]
  assign rsp_sft_cnt_l1_w = layer_st ? 8'h8 : _T_1558; // @[NV_NVDLA_CSC_dl.scala 966:27:@1330.4]
  assign _T_1560 = ~ dat_rsp_l2_sub_c; // @[NV_NVDLA_CSC_dl.scala 972:52:@1331.4]
  assign _T_1561 = dat_rsp_l2_stripe_end & _T_1560; // @[NV_NVDLA_CSC_dl.scala 972:50:@1332.4]
  assign _T_1562 = dat_rsp_l2_stripe_end & dat_rsp_l2_sub_c; // @[NV_NVDLA_CSC_dl.scala 973:50:@1333.4]
  assign _T_1565 = rsp_sft_cnt_l2_inc & 8'h7; // @[NV_NVDLA_CSC_dl.scala 974:64:@1334.4]
  assign _T_1566 = dat_dummy_l2_en ? _T_1565 : rsp_sft_cnt_l2_inc; // @[NV_NVDLA_CSC_dl.scala 974:27:@1335.4]
  assign _T_1567 = _T_1562 ? 8'h8 : _T_1566; // @[NV_NVDLA_CSC_dl.scala 973:27:@1336.4]
  assign _T_1568 = _T_1561 ? rsp_sft_cnt_l2_ori : _T_1567; // @[NV_NVDLA_CSC_dl.scala 972:27:@1337.4]
  assign rsp_sft_cnt_l2_w = layer_st ? 8'h8 : _T_1568; // @[NV_NVDLA_CSC_dl.scala 971:27:@1338.4]
  assign _T_1570 = ~ dat_rsp_l3_sub_c; // @[NV_NVDLA_CSC_dl.scala 977:52:@1339.4]
  assign _T_1571 = dat_rsp_l3_stripe_end & _T_1570; // @[NV_NVDLA_CSC_dl.scala 977:50:@1340.4]
  assign _T_1572 = dat_rsp_l3_stripe_end & dat_rsp_l3_sub_c; // @[NV_NVDLA_CSC_dl.scala 978:50:@1341.4]
  assign _T_1575 = rsp_sft_cnt_l3_inc & 8'h7; // @[NV_NVDLA_CSC_dl.scala 979:64:@1342.4]
  assign _T_1576 = dat_dummy_l3_en ? _T_1575 : rsp_sft_cnt_l3_inc; // @[NV_NVDLA_CSC_dl.scala 979:27:@1343.4]
  assign _T_1577 = _T_1572 ? 8'h8 : _T_1576; // @[NV_NVDLA_CSC_dl.scala 978:27:@1344.4]
  assign _T_1578 = _T_1571 ? rsp_sft_cnt_l3_ori : _T_1577; // @[NV_NVDLA_CSC_dl.scala 977:27:@1345.4]
  assign rsp_sft_cnt_l3_w = layer_st ? 8'h8 : _T_1578; // @[NV_NVDLA_CSC_dl.scala 976:27:@1346.4]
  assign _T_1579 = is_img_d1[17]; // @[NV_NVDLA_CSC_dl.scala 982:46:@1347.4]
  assign _T_1580 = _T_1579 & dat_rsp_l0_pvld; // @[NV_NVDLA_CSC_dl.scala 982:51:@1348.4]
  assign rsp_sft_cnt_l0_en = layer_st | _T_1580; // @[NV_NVDLA_CSC_dl.scala 982:34:@1349.4]
  assign _T_1581 = is_img_d1[18]; // @[NV_NVDLA_CSC_dl.scala 983:46:@1350.4]
  assign _T_1582 = _T_1581 & dat_rsp_l1_pvld; // @[NV_NVDLA_CSC_dl.scala 983:51:@1351.4]
  assign _T_1584 = sub_h_total_g5 != 3'h1; // @[NV_NVDLA_CSC_dl.scala 983:87:@1352.4]
  assign _T_1585 = _T_1582 & _T_1584; // @[NV_NVDLA_CSC_dl.scala 983:69:@1353.4]
  assign rsp_sft_cnt_l1_en = layer_st | _T_1585; // @[NV_NVDLA_CSC_dl.scala 983:34:@1354.4]
  assign _T_1586 = is_img_d1[19]; // @[NV_NVDLA_CSC_dl.scala 984:46:@1355.4]
  assign _T_1587 = _T_1586 & dat_rsp_l2_pvld; // @[NV_NVDLA_CSC_dl.scala 984:51:@1356.4]
  assign _T_1589 = sub_h_total_g5 == 3'h4; // @[NV_NVDLA_CSC_dl.scala 984:87:@1357.4]
  assign _T_1590 = _T_1587 & _T_1589; // @[NV_NVDLA_CSC_dl.scala 984:69:@1358.4]
  assign rsp_sft_cnt_l2_en = layer_st | _T_1590; // @[NV_NVDLA_CSC_dl.scala 984:34:@1359.4]
  assign _T_1591 = is_img_d1[20]; // @[NV_NVDLA_CSC_dl.scala 985:46:@1360.4]
  assign _T_1592 = _T_1591 & dat_rsp_l3_pvld; // @[NV_NVDLA_CSC_dl.scala 985:51:@1361.4]
  assign _T_1595 = _T_1592 & _T_1589; // @[NV_NVDLA_CSC_dl.scala 985:69:@1363.4]
  assign rsp_sft_cnt_l3_en = layer_st | _T_1595; // @[NV_NVDLA_CSC_dl.scala 985:34:@1364.4]
  assign _T_1596 = is_img_d1[21]; // @[NV_NVDLA_CSC_dl.scala 987:50:@1365.4]
  assign _T_1597 = _T_1596 & dat_rsp_l0_pvld; // @[NV_NVDLA_CSC_dl.scala 987:55:@1366.4]
  assign _T_1598 = _T_1597 & dat_rsp_l0_stripe_end; // @[NV_NVDLA_CSC_dl.scala 987:73:@1367.4]
  assign _T_1599 = _T_1598 & dat_rsp_l0_sub_c; // @[NV_NVDLA_CSC_dl.scala 987:97:@1368.4]
  assign rsp_sft_cnt_l0_ori_en = layer_st | _T_1599; // @[NV_NVDLA_CSC_dl.scala 987:38:@1369.4]
  assign _T_1600 = is_img_d1[22]; // @[NV_NVDLA_CSC_dl.scala 988:50:@1370.4]
  assign _T_1601 = _T_1600 & dat_rsp_l1_pvld; // @[NV_NVDLA_CSC_dl.scala 988:55:@1371.4]
  assign _T_1602 = _T_1601 & dat_rsp_l1_stripe_end; // @[NV_NVDLA_CSC_dl.scala 988:73:@1372.4]
  assign _T_1603 = _T_1602 & dat_rsp_l1_sub_c; // @[NV_NVDLA_CSC_dl.scala 988:97:@1373.4]
  assign _T_1605 = sub_h_total_g6 != 3'h1; // @[NV_NVDLA_CSC_dl.scala 988:138:@1374.4]
  assign _T_1606 = _T_1603 & _T_1605; // @[NV_NVDLA_CSC_dl.scala 988:120:@1375.4]
  assign rsp_sft_cnt_l1_ori_en = layer_st | _T_1606; // @[NV_NVDLA_CSC_dl.scala 988:38:@1376.4]
  assign _T_1607 = is_img_d1[23]; // @[NV_NVDLA_CSC_dl.scala 989:50:@1377.4]
  assign _T_1608 = _T_1607 & dat_rsp_l2_pvld; // @[NV_NVDLA_CSC_dl.scala 989:55:@1378.4]
  assign _T_1609 = _T_1608 & dat_rsp_l2_stripe_end; // @[NV_NVDLA_CSC_dl.scala 989:73:@1379.4]
  assign _T_1610 = _T_1609 & dat_rsp_l2_sub_c; // @[NV_NVDLA_CSC_dl.scala 989:97:@1380.4]
  assign _T_1612 = sub_h_total_g6 == 3'h4; // @[NV_NVDLA_CSC_dl.scala 989:138:@1381.4]
  assign _T_1613 = _T_1610 & _T_1612; // @[NV_NVDLA_CSC_dl.scala 989:120:@1382.4]
  assign rsp_sft_cnt_l2_ori_en = layer_st | _T_1613; // @[NV_NVDLA_CSC_dl.scala 989:38:@1383.4]
  assign _T_1614 = is_img_d1[24]; // @[NV_NVDLA_CSC_dl.scala 990:50:@1384.4]
  assign _T_1615 = _T_1614 & dat_rsp_l3_pvld; // @[NV_NVDLA_CSC_dl.scala 990:55:@1385.4]
  assign _T_1616 = _T_1615 & dat_rsp_l3_stripe_end; // @[NV_NVDLA_CSC_dl.scala 990:73:@1386.4]
  assign _T_1617 = _T_1616 & dat_rsp_l3_sub_c; // @[NV_NVDLA_CSC_dl.scala 990:97:@1387.4]
  assign _T_1620 = _T_1617 & _T_1612; // @[NV_NVDLA_CSC_dl.scala 990:120:@1389.4]
  assign rsp_sft_cnt_l3_ori_en = layer_st | _T_1620; // @[NV_NVDLA_CSC_dl.scala 990:38:@1390.4]
  assign _GEN_141 = rsp_sft_cnt_l0_en ? rsp_sft_cnt_l0_w : rsp_sft_cnt_l0; // @[NV_NVDLA_CSC_dl.scala 992:24:@1391.4]
  assign _GEN_142 = rsp_sft_cnt_l1_en ? rsp_sft_cnt_l1_w : rsp_sft_cnt_l1; // @[NV_NVDLA_CSC_dl.scala 993:24:@1394.4]
  assign _GEN_143 = rsp_sft_cnt_l2_en ? rsp_sft_cnt_l2_w : rsp_sft_cnt_l2; // @[NV_NVDLA_CSC_dl.scala 994:24:@1397.4]
  assign _GEN_144 = rsp_sft_cnt_l3_en ? rsp_sft_cnt_l3_w : rsp_sft_cnt_l3; // @[NV_NVDLA_CSC_dl.scala 995:24:@1400.4]
  assign _GEN_145 = rsp_sft_cnt_l0_ori_en ? rsp_sft_cnt_l0_w : rsp_sft_cnt_l0_ori; // @[NV_NVDLA_CSC_dl.scala 996:28:@1403.4]
  assign _GEN_146 = rsp_sft_cnt_l1_ori_en ? rsp_sft_cnt_l1_w : rsp_sft_cnt_l1_ori; // @[NV_NVDLA_CSC_dl.scala 997:28:@1406.4]
  assign _GEN_147 = rsp_sft_cnt_l2_ori_en ? rsp_sft_cnt_l2_w : rsp_sft_cnt_l2_ori; // @[NV_NVDLA_CSC_dl.scala 998:28:@1409.4]
  assign _GEN_148 = rsp_sft_cnt_l3_ori_en ? rsp_sft_cnt_l3_w : rsp_sft_cnt_l3_ori; // @[NV_NVDLA_CSC_dl.scala 999:28:@1412.4]
  assign _T_1621 = pad_value[7:0]; // @[NV_NVDLA_CSC_dl.scala 1008:55:@1415.4]
  assign dat_rsp_pad_value = {_T_1621,_T_1621,_T_1621,_T_1621,_T_1621,_T_1621,_T_1621,_T_1621}; // @[Cat.scala 30:58:@1418.4]
  assign dat_rsp_l0c0 = dat_l0c0_dummy ? dat_rsp_pad_value : dat_l0c0; // @[NV_NVDLA_CSC_dl.scala 1010:23:@1419.4]
  assign dat_rsp_l1c0 = dat_l1c0_dummy ? dat_rsp_pad_value : dat_l1c0; // @[NV_NVDLA_CSC_dl.scala 1011:23:@1420.4]
  assign dat_rsp_l2c0 = dat_l2c0_dummy ? dat_rsp_pad_value : dat_l2c0; // @[NV_NVDLA_CSC_dl.scala 1012:23:@1421.4]
  assign dat_rsp_l3c0 = dat_l3c0_dummy ? dat_rsp_pad_value : dat_l3c0; // @[NV_NVDLA_CSC_dl.scala 1013:23:@1422.4]
  assign dat_rsp_l0c1 = dat_l0c1_dummy ? dat_rsp_pad_value : dat_l0c1; // @[NV_NVDLA_CSC_dl.scala 1015:23:@1423.4]
  assign dat_rsp_l1c1 = dat_l1c1_dummy ? dat_rsp_pad_value : dat_l1c1; // @[NV_NVDLA_CSC_dl.scala 1016:23:@1424.4]
  assign dat_rsp_l2c1 = dat_l2c1_dummy ? dat_rsp_pad_value : dat_l2c1; // @[NV_NVDLA_CSC_dl.scala 1017:23:@1425.4]
  assign dat_rsp_l3c1 = dat_l3c1_dummy ? dat_rsp_pad_value : dat_l3c1; // @[NV_NVDLA_CSC_dl.scala 1018:23:@1426.4]
  assign _T_1625 = is_img_d1[26]; // @[NV_NVDLA_CSC_dl.scala 1024:37:@1428.4]
  assign dat_rsp_conv_8b = _T_1625 ? 64'h0 : dat_rsp_l0c0; // @[NV_NVDLA_CSC_dl.scala 1024:27:@1429.4]
  assign dat_rsp_conv_0 = dat_rsp_conv_8b[7:0]; // @[NV_NVDLA_CSC_dl.scala 1046:39:@1432.4]
  assign dat_rsp_conv_1 = dat_rsp_conv_8b[15:8]; // @[NV_NVDLA_CSC_dl.scala 1046:39:@1434.4]
  assign dat_rsp_conv_2 = dat_rsp_conv_8b[23:16]; // @[NV_NVDLA_CSC_dl.scala 1046:39:@1436.4]
  assign dat_rsp_conv_3 = dat_rsp_conv_8b[31:24]; // @[NV_NVDLA_CSC_dl.scala 1046:39:@1438.4]
  assign dat_rsp_conv_4 = dat_rsp_conv_8b[39:32]; // @[NV_NVDLA_CSC_dl.scala 1046:39:@1440.4]
  assign dat_rsp_conv_5 = dat_rsp_conv_8b[47:40]; // @[NV_NVDLA_CSC_dl.scala 1046:39:@1442.4]
  assign dat_rsp_conv_6 = dat_rsp_conv_8b[55:48]; // @[NV_NVDLA_CSC_dl.scala 1046:39:@1444.4]
  assign dat_rsp_conv_7 = dat_rsp_conv_8b[63:56]; // @[NV_NVDLA_CSC_dl.scala 1046:39:@1446.4]
  assign _T_1655 = is_img_d1[27]; // @[NV_NVDLA_CSC_dl.scala 1059:39:@1454.4]
  assign _T_1656 = ~ _T_1655; // @[NV_NVDLA_CSC_dl.scala 1059:29:@1455.4]
  assign _T_1658 = {dat_rsp_l0c0,dat_rsp_l0c1}; // @[Cat.scala 30:58:@1456.4]
  assign dat_rsp_l0_sft_in = _T_1656 ? 128'h0 : _T_1658; // @[NV_NVDLA_CSC_dl.scala 1059:28:@1457.4]
  assign _T_1659 = is_img_d1[28]; // @[NV_NVDLA_CSC_dl.scala 1060:39:@1458.4]
  assign _T_1660 = ~ _T_1659; // @[NV_NVDLA_CSC_dl.scala 1060:29:@1459.4]
  assign _T_1662 = {dat_rsp_l1c0,dat_rsp_l1c1}; // @[Cat.scala 30:58:@1460.4]
  assign dat_rsp_l1_sft_in = _T_1660 ? 128'h0 : _T_1662; // @[NV_NVDLA_CSC_dl.scala 1060:28:@1461.4]
  assign _T_1663 = is_img_d1[29]; // @[NV_NVDLA_CSC_dl.scala 1061:39:@1462.4]
  assign _T_1664 = ~ _T_1663; // @[NV_NVDLA_CSC_dl.scala 1061:29:@1463.4]
  assign _T_1666 = {dat_rsp_l2c0,dat_rsp_l2c1}; // @[Cat.scala 30:58:@1464.4]
  assign dat_rsp_l2_sft_in = _T_1664 ? 128'h0 : _T_1666; // @[NV_NVDLA_CSC_dl.scala 1061:28:@1465.4]
  assign _T_1667 = is_img_d1[30]; // @[NV_NVDLA_CSC_dl.scala 1062:39:@1466.4]
  assign _T_1668 = ~ _T_1667; // @[NV_NVDLA_CSC_dl.scala 1062:29:@1467.4]
  assign _T_1670 = {dat_rsp_l3c0,dat_rsp_l3c1}; // @[Cat.scala 30:58:@1468.4]
  assign dat_rsp_l3_sft_in = _T_1668 ? 128'h0 : _T_1670; // @[NV_NVDLA_CSC_dl.scala 1062:28:@1469.4]
  assign _T_1672 = {rsp_sft_cnt_l0,3'h0}; // @[Cat.scala 30:58:@1470.4]
  assign _T_1673 = dat_rsp_l0_sft_in >> _T_1672; // @[NV_NVDLA_CSC_dl.scala 1064:41:@1471.4]
  assign dat_rsp_l0_sft = _T_1673[63:0]; // @[NV_NVDLA_CSC_dl.scala 1064:82:@1472.4]
  assign _T_1675 = {rsp_sft_cnt_l1,3'h0}; // @[Cat.scala 30:58:@1473.4]
  assign _T_1676 = dat_rsp_l1_sft_in >> _T_1675; // @[NV_NVDLA_CSC_dl.scala 1065:41:@1474.4]
  assign dat_rsp_l1_sft = _T_1676[63:0]; // @[NV_NVDLA_CSC_dl.scala 1065:82:@1475.4]
  assign _T_1678 = {rsp_sft_cnt_l2,3'h0}; // @[Cat.scala 30:58:@1476.4]
  assign _T_1679 = dat_rsp_l2_sft_in >> _T_1678; // @[NV_NVDLA_CSC_dl.scala 1066:41:@1477.4]
  assign dat_rsp_l2_sft = _T_1679[63:0]; // @[NV_NVDLA_CSC_dl.scala 1066:82:@1478.4]
  assign _T_1681 = {rsp_sft_cnt_l3,3'h0}; // @[Cat.scala 30:58:@1479.4]
  assign _T_1682 = dat_rsp_l3_sft_in >> _T_1681; // @[NV_NVDLA_CSC_dl.scala 1067:41:@1480.4]
  assign dat_rsp_l3_sft = _T_1682[63:0]; // @[NV_NVDLA_CSC_dl.scala 1067:82:@1481.4]
  assign _T_1683 = is_img_d1[32]; // @[NV_NVDLA_CSC_dl.scala 1069:36:@1482.4]
  assign _T_1684 = ~ _T_1683; // @[NV_NVDLA_CSC_dl.scala 1069:26:@1483.4]
  assign _T_1687 = sub_h_total_g8 == 3'h4; // @[NV_NVDLA_CSC_dl.scala 1070:41:@1484.4]
  assign _T_1688 = dat_rsp_l3_sft[15:0]; // @[NV_NVDLA_CSC_dl.scala 1070:81:@1485.4]
  assign _T_1694 = {_T_1688,dat_rsp_l2_sft_d3,dat_rsp_l1_sft_d3,dat_rsp_l0_sft_d3}; // @[Cat.scala 30:58:@1491.4]
  assign _T_1696 = sub_h_total_g8 == 3'h2; // @[NV_NVDLA_CSC_dl.scala 1071:41:@1492.4]
  assign _T_1697 = dat_rsp_l1_sft[31:0]; // @[NV_NVDLA_CSC_dl.scala 1071:81:@1493.4]
  assign _T_1699 = {_T_1697,dat_rsp_l0_sft_d1}; // @[Cat.scala 30:58:@1495.4]
  assign _T_1701 = _T_1696 ? _T_1699 : dat_rsp_l0_sft; // @[NV_NVDLA_CSC_dl.scala 1071:25:@1497.4]
  assign _T_1702 = _T_1687 ? _T_1694 : _T_1701; // @[NV_NVDLA_CSC_dl.scala 1070:25:@1498.4]
  assign dat_rsp_img_8b = _T_1684 ? 64'h0 : _T_1702; // @[NV_NVDLA_CSC_dl.scala 1069:25:@1499.4]
  assign dat_rsp_img_0 = dat_rsp_img_8b[7:0]; // @[NV_NVDLA_CSC_dl.scala 1077:37:@1501.4]
  assign dat_rsp_img_1 = dat_rsp_img_8b[15:8]; // @[NV_NVDLA_CSC_dl.scala 1077:37:@1503.4]
  assign dat_rsp_img_2 = dat_rsp_img_8b[23:16]; // @[NV_NVDLA_CSC_dl.scala 1077:37:@1505.4]
  assign dat_rsp_img_3 = dat_rsp_img_8b[31:24]; // @[NV_NVDLA_CSC_dl.scala 1077:37:@1507.4]
  assign dat_rsp_img_4 = dat_rsp_img_8b[39:32]; // @[NV_NVDLA_CSC_dl.scala 1077:37:@1509.4]
  assign dat_rsp_img_5 = dat_rsp_img_8b[47:40]; // @[NV_NVDLA_CSC_dl.scala 1077:37:@1511.4]
  assign dat_rsp_img_6 = dat_rsp_img_8b[55:48]; // @[NV_NVDLA_CSC_dl.scala 1077:37:@1513.4]
  assign dat_rsp_img_7 = dat_rsp_img_8b[63:56]; // @[NV_NVDLA_CSC_dl.scala 1077:37:@1515.4]
  assign _T_1725 = sub_h_total_g9 != 3'h1; // @[NV_NVDLA_CSC_dl.scala 1080:59:@1517.4]
  assign dat_rsp_sft_d1_en = dat_rsp_l0_pvld & _T_1725; // @[NV_NVDLA_CSC_dl.scala 1080:41:@1518.4]
  assign _T_1727 = sub_h_total_g9 == 3'h4; // @[NV_NVDLA_CSC_dl.scala 1081:59:@1519.4]
  assign dat_rsp_sft_d2_en = dat_rsp_l1_pvld & _T_1727; // @[NV_NVDLA_CSC_dl.scala 1081:41:@1520.4]
  assign dat_rsp_sft_d3_en = dat_rsp_l2_pvld & _T_1727; // @[NV_NVDLA_CSC_dl.scala 1082:41:@1522.4]
  assign _GEN_149 = dat_rsp_sft_d1_en ? dat_rsp_l0_sft : {{32'd0}, dat_rsp_l0_sft_d1}; // @[NV_NVDLA_CSC_dl.scala 1084:24:@1523.4]
  assign _GEN_150 = dat_rsp_sft_d2_en ? dat_rsp_l0_sft_d1 : {{16'd0}, dat_rsp_l0_sft_d2}; // @[NV_NVDLA_CSC_dl.scala 1087:24:@1526.4]
  assign _GEN_151 = dat_rsp_sft_d2_en ? dat_rsp_l1_sft : {{48'd0}, dat_rsp_l1_sft_d2}; // @[NV_NVDLA_CSC_dl.scala 1087:24:@1526.4]
  assign _GEN_154 = dat_rsp_sft_d3_en ? dat_rsp_l2_sft : {{48'd0}, dat_rsp_l2_sft_d3}; // @[NV_NVDLA_CSC_dl.scala 1091:24:@1530.4]
  assign _T_1735 = 263'hff << dat_rsp_bytes; // @[NV_NVDLA_CSC_dl.scala 1100:56:@1536.4]
  assign _T_1736 = _T_1735[7:0]; // @[NV_NVDLA_CSC_dl.scala 1100:73:@1537.4]
  assign dat_rsp_ori_mask = ~ _T_1736; // @[NV_NVDLA_CSC_dl.scala 1100:24:@1538.4]
  assign _T_1738 = dat_rsp_cur_sub_h >= 2'h1; // @[NV_NVDLA_CSC_dl.scala 1102:51:@1539.4]
  assign dat_rsp_cur_h_mask_p1 = _T_1738 ? 8'hff : 8'h0; // @[NV_NVDLA_CSC_dl.scala 1102:32:@1541.4]
  assign _T_1746 = dat_rsp_cur_sub_h >= 2'h2; // @[NV_NVDLA_CSC_dl.scala 1103:51:@1542.4]
  assign dat_rsp_cur_h_mask_p2 = _T_1746 ? 4'hf : 4'h0; // @[NV_NVDLA_CSC_dl.scala 1103:32:@1544.4]
  assign _T_1754 = dat_rsp_cur_sub_h == 2'h3; // @[NV_NVDLA_CSC_dl.scala 1104:51:@1545.4]
  assign dat_rsp_cur_h_mask_p3 = _T_1754 ? 4'hf : 4'h0; // @[NV_NVDLA_CSC_dl.scala 1104:32:@1547.4]
  assign _T_1761 = dat_rsp_cur_h_mask_p1[3:0]; // @[NV_NVDLA_CSC_dl.scala 1106:57:@1548.4]
  assign dat_rsp_cur_h_e2_mask_8b = {_T_1761,4'hf}; // @[Cat.scala 30:58:@1550.4]
  assign _T_1767 = dat_rsp_cur_h_mask_p3[1:0]; // @[NV_NVDLA_CSC_dl.scala 1107:57:@1551.4]
  assign _T_1768 = dat_rsp_cur_h_mask_p2[1:0]; // @[NV_NVDLA_CSC_dl.scala 1107:106:@1552.4]
  assign _T_1769 = dat_rsp_cur_h_mask_p1[1:0]; // @[NV_NVDLA_CSC_dl.scala 1107:155:@1553.4]
  assign dat_rsp_cur_h_e4_mask_8b = {_T_1767,_T_1768,_T_1769,2'h3}; // @[Cat.scala 30:58:@1557.4]
  assign _T_1778 = sub_h_total_g11 == 3'h4; // @[NV_NVDLA_CSC_dl.scala 1109:43:@1558.4]
  assign _T_1779 = dat_rsp_ori_mask[1:0]; // @[NV_NVDLA_CSC_dl.scala 1109:89:@1559.4]
  assign _T_1781 = {_T_1779,_T_1779,_T_1779,_T_1779}; // @[Cat.scala 30:58:@1561.4]
  assign _T_1782 = _T_1781 & dat_rsp_cur_h_e4_mask_8b; // @[NV_NVDLA_CSC_dl.scala 1109:116:@1562.4]
  assign _T_1784 = sub_h_total_g11 == 3'h2; // @[NV_NVDLA_CSC_dl.scala 1110:43:@1563.4]
  assign _T_1785 = dat_rsp_ori_mask[3:0]; // @[NV_NVDLA_CSC_dl.scala 1110:89:@1564.4]
  assign _T_1786 = {_T_1785,_T_1785}; // @[Cat.scala 30:58:@1565.4]
  assign _T_1787 = _T_1786 & dat_rsp_cur_h_e2_mask_8b; // @[NV_NVDLA_CSC_dl.scala 1110:116:@1566.4]
  assign _T_1788 = _T_1784 ? _T_1787 : dat_rsp_ori_mask; // @[NV_NVDLA_CSC_dl.scala 1110:26:@1567.4]
  assign dat_rsp_mask_8b = _T_1778 ? _T_1782 : _T_1788; // @[NV_NVDLA_CSC_dl.scala 1109:26:@1568.4]
  assign _T_1789 = is_img_d1[33]; // @[NV_NVDLA_CSC_dl.scala 1114:35:@1569.4]
  assign dat_rsp_data_w_0 = _T_1789 ? dat_rsp_img_0 : dat_rsp_conv_0; // @[NV_NVDLA_CSC_dl.scala 1114:25:@1570.4]
  assign dat_rsp_data_w_1 = _T_1789 ? dat_rsp_img_1 : dat_rsp_conv_1; // @[NV_NVDLA_CSC_dl.scala 1114:25:@1570.4]
  assign dat_rsp_data_w_2 = _T_1789 ? dat_rsp_img_2 : dat_rsp_conv_2; // @[NV_NVDLA_CSC_dl.scala 1114:25:@1570.4]
  assign dat_rsp_data_w_3 = _T_1789 ? dat_rsp_img_3 : dat_rsp_conv_3; // @[NV_NVDLA_CSC_dl.scala 1114:25:@1570.4]
  assign dat_rsp_data_w_4 = _T_1789 ? dat_rsp_img_4 : dat_rsp_conv_4; // @[NV_NVDLA_CSC_dl.scala 1114:25:@1570.4]
  assign dat_rsp_data_w_5 = _T_1789 ? dat_rsp_img_5 : dat_rsp_conv_5; // @[NV_NVDLA_CSC_dl.scala 1114:25:@1570.4]
  assign dat_rsp_data_w_6 = _T_1789 ? dat_rsp_img_6 : dat_rsp_conv_6; // @[NV_NVDLA_CSC_dl.scala 1114:25:@1570.4]
  assign dat_rsp_data_w_7 = _T_1789 ? dat_rsp_img_7 : dat_rsp_conv_7; // @[NV_NVDLA_CSC_dl.scala 1114:25:@1570.4]
  assign dat_rsp_mask_val_int8_0 = dat_rsp_data_w_0 != 8'h0; // @[NV_NVDLA_CSC_dl.scala 1115:97:@1571.4]
  assign dat_rsp_mask_val_int8_1 = dat_rsp_data_w_1 != 8'h0; // @[NV_NVDLA_CSC_dl.scala 1115:97:@1572.4]
  assign dat_rsp_mask_val_int8_2 = dat_rsp_data_w_2 != 8'h0; // @[NV_NVDLA_CSC_dl.scala 1115:97:@1573.4]
  assign dat_rsp_mask_val_int8_3 = dat_rsp_data_w_3 != 8'h0; // @[NV_NVDLA_CSC_dl.scala 1115:97:@1574.4]
  assign dat_rsp_mask_val_int8_4 = dat_rsp_data_w_4 != 8'h0; // @[NV_NVDLA_CSC_dl.scala 1115:97:@1575.4]
  assign dat_rsp_mask_val_int8_5 = dat_rsp_data_w_5 != 8'h0; // @[NV_NVDLA_CSC_dl.scala 1115:97:@1576.4]
  assign dat_rsp_mask_val_int8_6 = dat_rsp_data_w_6 != 8'h0; // @[NV_NVDLA_CSC_dl.scala 1115:97:@1577.4]
  assign dat_rsp_mask_val_int8_7 = dat_rsp_data_w_7 != 8'h0; // @[NV_NVDLA_CSC_dl.scala 1115:97:@1578.4]
  assign _T_1838 = dat_rsp_mask_8b[0]; // @[NV_NVDLA_CSC_dl.scala 1116:80:@1588.4]
  assign dat_rsp_mask_w_0 = _T_1838 & dat_rsp_mask_val_int8_0; // @[NV_NVDLA_CSC_dl.scala 1116:83:@1589.4]
  assign _T_1840 = dat_rsp_mask_8b[1]; // @[NV_NVDLA_CSC_dl.scala 1116:80:@1590.4]
  assign dat_rsp_mask_w_1 = _T_1840 & dat_rsp_mask_val_int8_1; // @[NV_NVDLA_CSC_dl.scala 1116:83:@1591.4]
  assign _T_1842 = dat_rsp_mask_8b[2]; // @[NV_NVDLA_CSC_dl.scala 1116:80:@1592.4]
  assign dat_rsp_mask_w_2 = _T_1842 & dat_rsp_mask_val_int8_2; // @[NV_NVDLA_CSC_dl.scala 1116:83:@1593.4]
  assign _T_1844 = dat_rsp_mask_8b[3]; // @[NV_NVDLA_CSC_dl.scala 1116:80:@1594.4]
  assign dat_rsp_mask_w_3 = _T_1844 & dat_rsp_mask_val_int8_3; // @[NV_NVDLA_CSC_dl.scala 1116:83:@1595.4]
  assign _T_1846 = dat_rsp_mask_8b[4]; // @[NV_NVDLA_CSC_dl.scala 1116:80:@1596.4]
  assign dat_rsp_mask_w_4 = _T_1846 & dat_rsp_mask_val_int8_4; // @[NV_NVDLA_CSC_dl.scala 1116:83:@1597.4]
  assign _T_1848 = dat_rsp_mask_8b[5]; // @[NV_NVDLA_CSC_dl.scala 1116:80:@1598.4]
  assign dat_rsp_mask_w_5 = _T_1848 & dat_rsp_mask_val_int8_5; // @[NV_NVDLA_CSC_dl.scala 1116:83:@1599.4]
  assign _T_1850 = dat_rsp_mask_8b[6]; // @[NV_NVDLA_CSC_dl.scala 1116:80:@1600.4]
  assign dat_rsp_mask_w_6 = _T_1850 & dat_rsp_mask_val_int8_6; // @[NV_NVDLA_CSC_dl.scala 1116:83:@1601.4]
  assign _T_1852 = dat_rsp_mask_8b[7]; // @[NV_NVDLA_CSC_dl.scala 1116:80:@1602.4]
  assign dat_rsp_mask_w_7 = _T_1852 & dat_rsp_mask_val_int8_7; // @[NV_NVDLA_CSC_dl.scala 1116:83:@1603.4]
  assign _GEN_155 = dat_rsp_pvld ? dat_rsp_flag : dat_out_flag; // @[NV_NVDLA_CSC_dl.scala 1153:21:@1634.4]
  assign _GEN_156 = dat_rsp_pvld ? dat_rsp_mask_w_0 : dat_out_bypass_mask_0; // @[NV_NVDLA_CSC_dl.scala 1156:30:@1637.4]
  assign _GEN_157 = dat_rsp_pvld ? dat_rsp_mask_w_1 : dat_out_bypass_mask_1; // @[NV_NVDLA_CSC_dl.scala 1156:30:@1637.4]
  assign _GEN_158 = dat_rsp_pvld ? dat_rsp_mask_w_2 : dat_out_bypass_mask_2; // @[NV_NVDLA_CSC_dl.scala 1156:30:@1637.4]
  assign _GEN_159 = dat_rsp_pvld ? dat_rsp_mask_w_3 : dat_out_bypass_mask_3; // @[NV_NVDLA_CSC_dl.scala 1156:30:@1637.4]
  assign _GEN_160 = dat_rsp_pvld ? dat_rsp_mask_w_4 : dat_out_bypass_mask_4; // @[NV_NVDLA_CSC_dl.scala 1156:30:@1637.4]
  assign _GEN_161 = dat_rsp_pvld ? dat_rsp_mask_w_5 : dat_out_bypass_mask_5; // @[NV_NVDLA_CSC_dl.scala 1156:30:@1637.4]
  assign _GEN_162 = dat_rsp_pvld ? dat_rsp_mask_w_6 : dat_out_bypass_mask_6; // @[NV_NVDLA_CSC_dl.scala 1156:30:@1637.4]
  assign _GEN_163 = dat_rsp_pvld ? dat_rsp_mask_w_7 : dat_out_bypass_mask_7; // @[NV_NVDLA_CSC_dl.scala 1156:30:@1637.4]
  assign _T_1965 = dat_rsp_pvld & dat_rsp_mask_w_0; // @[NV_NVDLA_CSC_dl.scala 1160:34:@1647.4]
  assign _GEN_164 = _T_1965 ? dat_rsp_data_w_0 : _T_1943; // @[NV_NVDLA_CSC_dl.scala 1160:61:@1648.4]
  assign _T_1966 = dat_rsp_pvld & dat_rsp_mask_w_1; // @[NV_NVDLA_CSC_dl.scala 1160:34:@1651.4]
  assign _GEN_165 = _T_1966 ? dat_rsp_data_w_1 : _T_1946; // @[NV_NVDLA_CSC_dl.scala 1160:61:@1652.4]
  assign _T_1967 = dat_rsp_pvld & dat_rsp_mask_w_2; // @[NV_NVDLA_CSC_dl.scala 1160:34:@1655.4]
  assign _GEN_166 = _T_1967 ? dat_rsp_data_w_2 : _T_1949; // @[NV_NVDLA_CSC_dl.scala 1160:61:@1656.4]
  assign _T_1968 = dat_rsp_pvld & dat_rsp_mask_w_3; // @[NV_NVDLA_CSC_dl.scala 1160:34:@1659.4]
  assign _GEN_167 = _T_1968 ? dat_rsp_data_w_3 : _T_1952; // @[NV_NVDLA_CSC_dl.scala 1160:61:@1660.4]
  assign _T_1969 = dat_rsp_pvld & dat_rsp_mask_w_4; // @[NV_NVDLA_CSC_dl.scala 1160:34:@1663.4]
  assign _GEN_168 = _T_1969 ? dat_rsp_data_w_4 : _T_1955; // @[NV_NVDLA_CSC_dl.scala 1160:61:@1664.4]
  assign _T_1970 = dat_rsp_pvld & dat_rsp_mask_w_5; // @[NV_NVDLA_CSC_dl.scala 1160:34:@1667.4]
  assign _GEN_169 = _T_1970 ? dat_rsp_data_w_5 : _T_1958; // @[NV_NVDLA_CSC_dl.scala 1160:61:@1668.4]
  assign _T_1971 = dat_rsp_pvld & dat_rsp_mask_w_6; // @[NV_NVDLA_CSC_dl.scala 1160:34:@1671.4]
  assign _GEN_170 = _T_1971 ? dat_rsp_data_w_6 : _T_1961; // @[NV_NVDLA_CSC_dl.scala 1160:61:@1672.4]
  assign _T_1972 = dat_rsp_pvld & dat_rsp_mask_w_7; // @[NV_NVDLA_CSC_dl.scala 1160:34:@1675.4]
  assign _GEN_171 = _T_1972 ? dat_rsp_data_w_7 : _T_1964; // @[NV_NVDLA_CSC_dl.scala 1160:61:@1676.4]
  assign _T_2071 = ~ dat_out_pvld; // @[NV_NVDLA_CSC_dl.scala 1175:24:@1699.4]
  assign dat_out_mask_0 = _T_2071 ? 1'h0 : dat_out_bypass_mask_0; // @[NV_NVDLA_CSC_dl.scala 1175:23:@1709.4]
  assign dat_out_mask_1 = _T_2071 ? 1'h0 : dat_out_bypass_mask_1; // @[NV_NVDLA_CSC_dl.scala 1175:23:@1709.4]
  assign dat_out_mask_2 = _T_2071 ? 1'h0 : dat_out_bypass_mask_2; // @[NV_NVDLA_CSC_dl.scala 1175:23:@1709.4]
  assign dat_out_mask_3 = _T_2071 ? 1'h0 : dat_out_bypass_mask_3; // @[NV_NVDLA_CSC_dl.scala 1175:23:@1709.4]
  assign dat_out_mask_4 = _T_2071 ? 1'h0 : dat_out_bypass_mask_4; // @[NV_NVDLA_CSC_dl.scala 1175:23:@1709.4]
  assign dat_out_mask_5 = _T_2071 ? 1'h0 : dat_out_bypass_mask_5; // @[NV_NVDLA_CSC_dl.scala 1175:23:@1709.4]
  assign dat_out_mask_6 = _T_2071 ? 1'h0 : dat_out_bypass_mask_6; // @[NV_NVDLA_CSC_dl.scala 1175:23:@1709.4]
  assign dat_out_mask_7 = _T_2071 ? 1'h0 : dat_out_bypass_mask_7; // @[NV_NVDLA_CSC_dl.scala 1175:23:@1709.4]
  assign _T_2113 = dat_out_pvld | dl_out_pvld; // @[NV_NVDLA_CSC_dl.scala 1179:19:@1711.4]
  assign _GEN_172 = _T_2113 ? dat_out_mask_0 : dl_out_mask_0; // @[NV_NVDLA_CSC_dl.scala 1179:33:@1712.4]
  assign _GEN_173 = _T_2113 ? dat_out_mask_1 : dl_out_mask_1; // @[NV_NVDLA_CSC_dl.scala 1179:33:@1712.4]
  assign _GEN_174 = _T_2113 ? dat_out_mask_2 : dl_out_mask_2; // @[NV_NVDLA_CSC_dl.scala 1179:33:@1712.4]
  assign _GEN_175 = _T_2113 ? dat_out_mask_3 : dl_out_mask_3; // @[NV_NVDLA_CSC_dl.scala 1179:33:@1712.4]
  assign _GEN_176 = _T_2113 ? dat_out_mask_4 : dl_out_mask_4; // @[NV_NVDLA_CSC_dl.scala 1179:33:@1712.4]
  assign _GEN_177 = _T_2113 ? dat_out_mask_5 : dl_out_mask_5; // @[NV_NVDLA_CSC_dl.scala 1179:33:@1712.4]
  assign _GEN_178 = _T_2113 ? dat_out_mask_6 : dl_out_mask_6; // @[NV_NVDLA_CSC_dl.scala 1179:33:@1712.4]
  assign _GEN_179 = _T_2113 ? dat_out_mask_7 : dl_out_mask_7; // @[NV_NVDLA_CSC_dl.scala 1179:33:@1712.4]
  assign _GEN_180 = dat_out_pvld ? dat_out_flag : dl_out_flag; // @[NV_NVDLA_CSC_dl.scala 1182:19:@1722.4]
  assign _GEN_181 = dat_out_mask_0 ? _T_1943 : _T_2049; // @[NV_NVDLA_CSC_dl.scala 1187:26:@1725.4]
  assign _GEN_182 = dat_out_mask_1 ? _T_1946 : _T_2052; // @[NV_NVDLA_CSC_dl.scala 1187:26:@1728.4]
  assign _GEN_183 = dat_out_mask_2 ? _T_1949 : _T_2055; // @[NV_NVDLA_CSC_dl.scala 1187:26:@1731.4]
  assign _GEN_184 = dat_out_mask_3 ? _T_1952 : _T_2058; // @[NV_NVDLA_CSC_dl.scala 1187:26:@1734.4]
  assign _GEN_185 = dat_out_mask_4 ? _T_1955 : _T_2061; // @[NV_NVDLA_CSC_dl.scala 1187:26:@1737.4]
  assign _GEN_186 = dat_out_mask_5 ? _T_1958 : _T_2064; // @[NV_NVDLA_CSC_dl.scala 1187:26:@1740.4]
  assign _GEN_187 = dat_out_mask_6 ? _T_1961 : _T_2067; // @[NV_NVDLA_CSC_dl.scala 1187:26:@1743.4]
  assign _GEN_188 = dat_out_mask_7 ? _T_1964 : _T_2070; // @[NV_NVDLA_CSC_dl.scala 1187:26:@1746.4]
  assign _T_2116 = ~ dl_out_pvld; // @[NV_NVDLA_CSC_dl.scala 1196:27:@1751.4]
  assign sc2mac_dat_pd_w = _T_2116 ? 9'h0 : dl_out_flag; // @[NV_NVDLA_CSC_dl.scala 1196:26:@1752.4]
  assign _T_2125 = dl_out_pvld | dl_out_pvld_d1; // @[NV_NVDLA_CSC_dl.scala 1200:85:@1759.4]
  assign _GEN_189 = _T_2125 ? sc2mac_dat_pd_w : _T_2127; // @[Reg.scala 20:19:@1761.4]
  assign _GEN_190 = _T_2125 ? sc2mac_dat_pd_w : _T_2131; // @[Reg.scala 20:19:@1767.4]
  assign _GEN_191 = _T_2125 ? dl_out_mask_0 : _T_2175_0; // @[Reg.scala 20:19:@1782.4]
  assign _GEN_192 = _T_2125 ? dl_out_mask_1 : _T_2175_1; // @[Reg.scala 20:19:@1782.4]
  assign _GEN_193 = _T_2125 ? dl_out_mask_2 : _T_2175_2; // @[Reg.scala 20:19:@1782.4]
  assign _GEN_194 = _T_2125 ? dl_out_mask_3 : _T_2175_3; // @[Reg.scala 20:19:@1782.4]
  assign _GEN_195 = _T_2125 ? dl_out_mask_4 : _T_2175_4; // @[Reg.scala 20:19:@1782.4]
  assign _GEN_196 = _T_2125 ? dl_out_mask_5 : _T_2175_5; // @[Reg.scala 20:19:@1782.4]
  assign _GEN_197 = _T_2125 ? dl_out_mask_6 : _T_2175_6; // @[Reg.scala 20:19:@1782.4]
  assign _GEN_198 = _T_2125 ? dl_out_mask_7 : _T_2175_7; // @[Reg.scala 20:19:@1782.4]
  assign _GEN_199 = _T_2125 ? dl_out_mask_0 : _T_2247_0; // @[Reg.scala 20:19:@1811.4]
  assign _GEN_200 = _T_2125 ? dl_out_mask_1 : _T_2247_1; // @[Reg.scala 20:19:@1811.4]
  assign _GEN_201 = _T_2125 ? dl_out_mask_2 : _T_2247_2; // @[Reg.scala 20:19:@1811.4]
  assign _GEN_202 = _T_2125 ? dl_out_mask_3 : _T_2247_3; // @[Reg.scala 20:19:@1811.4]
  assign _GEN_203 = _T_2125 ? dl_out_mask_4 : _T_2247_4; // @[Reg.scala 20:19:@1811.4]
  assign _GEN_204 = _T_2125 ? dl_out_mask_5 : _T_2247_5; // @[Reg.scala 20:19:@1811.4]
  assign _GEN_205 = _T_2125 ? dl_out_mask_6 : _T_2247_6; // @[Reg.scala 20:19:@1811.4]
  assign _GEN_206 = _T_2125 ? dl_out_mask_7 : _T_2247_7; // @[Reg.scala 20:19:@1811.4]
  assign _GEN_207 = dl_out_mask_0 ? _T_2049 : _T_2278; // @[Reg.scala 20:19:@1830.4]
  assign _GEN_208 = dl_out_mask_0 ? _T_2049 : _T_2281; // @[Reg.scala 20:19:@1835.4]
  assign _GEN_209 = dl_out_mask_1 ? _T_2052 : _T_2284; // @[Reg.scala 20:19:@1840.4]
  assign _GEN_210 = dl_out_mask_1 ? _T_2052 : _T_2287; // @[Reg.scala 20:19:@1845.4]
  assign _GEN_211 = dl_out_mask_2 ? _T_2055 : _T_2290; // @[Reg.scala 20:19:@1850.4]
  assign _GEN_212 = dl_out_mask_2 ? _T_2055 : _T_2293; // @[Reg.scala 20:19:@1855.4]
  assign _GEN_213 = dl_out_mask_3 ? _T_2058 : _T_2296; // @[Reg.scala 20:19:@1860.4]
  assign _GEN_214 = dl_out_mask_3 ? _T_2058 : _T_2299; // @[Reg.scala 20:19:@1865.4]
  assign _GEN_215 = dl_out_mask_4 ? _T_2061 : _T_2302; // @[Reg.scala 20:19:@1870.4]
  assign _GEN_216 = dl_out_mask_4 ? _T_2061 : _T_2305; // @[Reg.scala 20:19:@1875.4]
  assign _GEN_217 = dl_out_mask_5 ? _T_2064 : _T_2308; // @[Reg.scala 20:19:@1880.4]
  assign _GEN_218 = dl_out_mask_5 ? _T_2064 : _T_2311; // @[Reg.scala 20:19:@1885.4]
  assign _GEN_219 = dl_out_mask_6 ? _T_2067 : _T_2314; // @[Reg.scala 20:19:@1890.4]
  assign _GEN_220 = dl_out_mask_6 ? _T_2067 : _T_2317; // @[Reg.scala 20:19:@1895.4]
  assign _GEN_221 = dl_out_mask_7 ? _T_2070 : _T_2320; // @[Reg.scala 20:19:@1900.4]
  assign _GEN_222 = dl_out_mask_7 ? _T_2070 : _T_2323; // @[Reg.scala 20:19:@1905.4]
  assign io_sc2cdma_dat_updt_valid = _T_607; // @[NV_NVDLA_CSC_dl.scala 310:27:@326.4]
  assign io_sc2cdma_dat_updt_bits_entries = _T_613; // @[NV_NVDLA_CSC_dl.scala 312:34:@336.4]
  assign io_sc2cdma_dat_updt_bits_slices = _T_610; // @[NV_NVDLA_CSC_dl.scala 311:33:@331.4]
  assign io_sc2buf_dat_rd_addr_valid = sc2buf_dat_rd_en_out; // @[NV_NVDLA_CSC_dl.scala 731:29:@934.4]
  assign io_sc2buf_dat_rd_addr_bits = sc2buf_dat_rd_addr_out; // @[NV_NVDLA_CSC_dl.scala 732:28:@935.4]
  assign io_sc2mac_dat_a_valid = _T_2120; // @[NV_NVDLA_CSC_dl.scala 1198:23:@1755.4]
  assign io_sc2mac_dat_a_bits_mask_0 = _T_2175_0; // @[NV_NVDLA_CSC_dl.scala 1202:27:@1792.4]
  assign io_sc2mac_dat_a_bits_mask_1 = _T_2175_1; // @[NV_NVDLA_CSC_dl.scala 1202:27:@1793.4]
  assign io_sc2mac_dat_a_bits_mask_2 = _T_2175_2; // @[NV_NVDLA_CSC_dl.scala 1202:27:@1794.4]
  assign io_sc2mac_dat_a_bits_mask_3 = _T_2175_3; // @[NV_NVDLA_CSC_dl.scala 1202:27:@1795.4]
  assign io_sc2mac_dat_a_bits_mask_4 = _T_2175_4; // @[NV_NVDLA_CSC_dl.scala 1202:27:@1796.4]
  assign io_sc2mac_dat_a_bits_mask_5 = _T_2175_5; // @[NV_NVDLA_CSC_dl.scala 1202:27:@1797.4]
  assign io_sc2mac_dat_a_bits_mask_6 = _T_2175_6; // @[NV_NVDLA_CSC_dl.scala 1202:27:@1798.4]
  assign io_sc2mac_dat_a_bits_mask_7 = _T_2175_7; // @[NV_NVDLA_CSC_dl.scala 1202:27:@1799.4]
  assign io_sc2mac_dat_a_bits_data_0 = _T_2278; // @[NV_NVDLA_CSC_dl.scala 1206:38:@1833.4]
  assign io_sc2mac_dat_a_bits_data_1 = _T_2284; // @[NV_NVDLA_CSC_dl.scala 1206:38:@1843.4]
  assign io_sc2mac_dat_a_bits_data_2 = _T_2290; // @[NV_NVDLA_CSC_dl.scala 1206:38:@1853.4]
  assign io_sc2mac_dat_a_bits_data_3 = _T_2296; // @[NV_NVDLA_CSC_dl.scala 1206:38:@1863.4]
  assign io_sc2mac_dat_a_bits_data_4 = _T_2302; // @[NV_NVDLA_CSC_dl.scala 1206:38:@1873.4]
  assign io_sc2mac_dat_a_bits_data_5 = _T_2308; // @[NV_NVDLA_CSC_dl.scala 1206:38:@1883.4]
  assign io_sc2mac_dat_a_bits_data_6 = _T_2314; // @[NV_NVDLA_CSC_dl.scala 1206:38:@1893.4]
  assign io_sc2mac_dat_a_bits_data_7 = _T_2320; // @[NV_NVDLA_CSC_dl.scala 1206:38:@1903.4]
  assign io_sc2mac_dat_a_bits_pd = _T_2127; // @[NV_NVDLA_CSC_dl.scala 1200:25:@1764.4]
  assign io_sc2mac_dat_b_valid = _T_2123; // @[NV_NVDLA_CSC_dl.scala 1199:23:@1758.4]
  assign io_sc2mac_dat_b_bits_mask_0 = _T_2247_0; // @[NV_NVDLA_CSC_dl.scala 1203:27:@1821.4]
  assign io_sc2mac_dat_b_bits_mask_1 = _T_2247_1; // @[NV_NVDLA_CSC_dl.scala 1203:27:@1822.4]
  assign io_sc2mac_dat_b_bits_mask_2 = _T_2247_2; // @[NV_NVDLA_CSC_dl.scala 1203:27:@1823.4]
  assign io_sc2mac_dat_b_bits_mask_3 = _T_2247_3; // @[NV_NVDLA_CSC_dl.scala 1203:27:@1824.4]
  assign io_sc2mac_dat_b_bits_mask_4 = _T_2247_4; // @[NV_NVDLA_CSC_dl.scala 1203:27:@1825.4]
  assign io_sc2mac_dat_b_bits_mask_5 = _T_2247_5; // @[NV_NVDLA_CSC_dl.scala 1203:27:@1826.4]
  assign io_sc2mac_dat_b_bits_mask_6 = _T_2247_6; // @[NV_NVDLA_CSC_dl.scala 1203:27:@1827.4]
  assign io_sc2mac_dat_b_bits_mask_7 = _T_2247_7; // @[NV_NVDLA_CSC_dl.scala 1203:27:@1828.4]
  assign io_sc2mac_dat_b_bits_data_0 = _T_2281; // @[NV_NVDLA_CSC_dl.scala 1207:38:@1838.4]
  assign io_sc2mac_dat_b_bits_data_1 = _T_2287; // @[NV_NVDLA_CSC_dl.scala 1207:38:@1848.4]
  assign io_sc2mac_dat_b_bits_data_2 = _T_2293; // @[NV_NVDLA_CSC_dl.scala 1207:38:@1858.4]
  assign io_sc2mac_dat_b_bits_data_3 = _T_2299; // @[NV_NVDLA_CSC_dl.scala 1207:38:@1868.4]
  assign io_sc2mac_dat_b_bits_data_4 = _T_2305; // @[NV_NVDLA_CSC_dl.scala 1207:38:@1878.4]
  assign io_sc2mac_dat_b_bits_data_5 = _T_2311; // @[NV_NVDLA_CSC_dl.scala 1207:38:@1888.4]
  assign io_sc2mac_dat_b_bits_data_6 = _T_2317; // @[NV_NVDLA_CSC_dl.scala 1207:38:@1898.4]
  assign io_sc2mac_dat_b_bits_data_7 = _T_2323; // @[NV_NVDLA_CSC_dl.scala 1207:38:@1908.4]
  assign io_sc2mac_dat_b_bits_pd = _T_2131; // @[NV_NVDLA_CSC_dl.scala 1201:25:@1770.4]
  assign io_slcg_wg_en = 1'h0; // @[NV_NVDLA_CSC_dl.scala 251:15:@243.4]
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE
  integer initvar;
  initial begin
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      #0.002 begin end
    `endif
  `ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  layer_st_d1 = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  data_batch = _RAND_1[5:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  rls_slices = _RAND_2[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  h_offset_slice = _RAND_3[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  entries = _RAND_4[14:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  entries_batch = _RAND_5[14:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  dataout_width_cmp = _RAND_6[12:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_7 = {1{`RANDOM}};
  rls_entries = _RAND_7[14:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_8 = {1{`RANDOM}};
  h_bias_0_stride = _RAND_8[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_9 = {1{`RANDOM}};
  h_bias_1_stride = _RAND_9[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_10 = {1{`RANDOM}};
  slice_left = _RAND_10[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_11 = {2{`RANDOM}};
  is_img_d1 = _RAND_11[33:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_12 = {1{`RANDOM}};
  data_bank = _RAND_12[4:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_13 = {1{`RANDOM}};
  datain_width = _RAND_13[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_14 = {1{`RANDOM}};
  datain_width_cmp = _RAND_14[12:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_15 = {1{`RANDOM}};
  datain_height_cmp = _RAND_15[12:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_16 = {1{`RANDOM}};
  datain_channel_cmp = _RAND_16[10:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_17 = {1{`RANDOM}};
  sub_h_total_g0 = _RAND_17[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_18 = {1{`RANDOM}};
  sub_h_total_g1 = _RAND_18[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_19 = {1{`RANDOM}};
  sub_h_total_g2 = _RAND_19[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_20 = {1{`RANDOM}};
  sub_h_total_g3 = _RAND_20[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_21 = {1{`RANDOM}};
  sub_h_total_g4 = _RAND_21[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_22 = {1{`RANDOM}};
  sub_h_total_g5 = _RAND_22[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_23 = {1{`RANDOM}};
  sub_h_total_g6 = _RAND_23[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_24 = {1{`RANDOM}};
  sub_h_total_g8 = _RAND_24[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_25 = {1{`RANDOM}};
  sub_h_total_g9 = _RAND_25[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_26 = {1{`RANDOM}};
  sub_h_total_g11 = _RAND_26[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_27 = {1{`RANDOM}};
  sub_h_cmp_g0 = _RAND_27[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_28 = {1{`RANDOM}};
  sub_h_cmp_g1 = _RAND_28[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_29 = {1{`RANDOM}};
  conv_x_stride = _RAND_29[3:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_30 = {1{`RANDOM}};
  conv_y_stride = _RAND_30[3:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_31 = {1{`RANDOM}};
  batch_cmp = _RAND_31[4:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_32 = {1{`RANDOM}};
  pixel_x_init = _RAND_32[5:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_33 = {1{`RANDOM}};
  pixel_x_init_offset = _RAND_33[6:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_34 = {1{`RANDOM}};
  pixel_x_add = _RAND_34[6:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_35 = {1{`RANDOM}};
  pixel_x_byte_stride = _RAND_35[6:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_36 = {1{`RANDOM}};
  pixel_ch_stride = _RAND_36[11:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_37 = {1{`RANDOM}};
  x_dilate = _RAND_37[5:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_38 = {1{`RANDOM}};
  y_dilate = _RAND_38[5:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_39 = {1{`RANDOM}};
  pad_value = _RAND_39[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_40 = {1{`RANDOM}};
  entries_cmp = _RAND_40[14:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_41 = {1{`RANDOM}};
  h_bias_2_stride = _RAND_41[14:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_42 = {1{`RANDOM}};
  h_bias_3_stride = _RAND_42[14:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_43 = {1{`RANDOM}};
  last_slices = _RAND_43[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_44 = {1{`RANDOM}};
  last_entries = _RAND_44[14:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_45 = {1{`RANDOM}};
  dat_entry_st = _RAND_45[14:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_46 = {1{`RANDOM}};
  dat_rsp_l3_pvld = _RAND_46[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_47 = {1{`RANDOM}};
  dat_rsp_l1_pvld = _RAND_47[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_48 = {1{`RANDOM}};
  dat_rsp_l0_pvld = _RAND_48[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_49 = {1{`RANDOM}};
  _T_1461 = _RAND_49[26:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_50 = {1{`RANDOM}};
  _T_1455 = _RAND_50[26:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_51 = {1{`RANDOM}};
  _T_1452 = _RAND_51[26:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_52 = {1{`RANDOM}};
  _T_607 = _RAND_52[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_53 = {1{`RANDOM}};
  _T_610 = _RAND_53[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_54 = {1{`RANDOM}};
  _T_613 = _RAND_54[14:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_55 = {1{`RANDOM}};
  _T_618 = _RAND_55[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_56 = {1{`RANDOM}};
  _T_621 = _RAND_56[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_57 = {1{`RANDOM}};
  _T_624 = _RAND_57[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_58 = {1{`RANDOM}};
  _T_627 = _RAND_58[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_59 = {1{`RANDOM}};
  dl_in_pvld = _RAND_59[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_60 = {1{`RANDOM}};
  _T_634 = _RAND_60[30:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_61 = {1{`RANDOM}};
  _T_637 = _RAND_61[30:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_62 = {1{`RANDOM}};
  _T_640 = _RAND_62[30:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_63 = {1{`RANDOM}};
  _T_643 = _RAND_63[30:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_64 = {1{`RANDOM}};
  dl_in_pd = _RAND_64[30:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_65 = {1{`RANDOM}};
  _T_650 = _RAND_65[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_66 = {1{`RANDOM}};
  _T_653 = _RAND_66[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_67 = {1{`RANDOM}};
  _T_656 = _RAND_67[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_68 = {1{`RANDOM}};
  _T_659 = _RAND_68[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_69 = {1{`RANDOM}};
  _T_664 = _RAND_69[30:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_70 = {1{`RANDOM}};
  _T_667 = _RAND_70[30:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_71 = {1{`RANDOM}};
  _T_670 = _RAND_71[30:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_72 = {1{`RANDOM}};
  _T_673 = _RAND_72[30:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_73 = {1{`RANDOM}};
  batch_cnt = _RAND_73[4:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_74 = {1{`RANDOM}};
  sub_h_cnt = _RAND_74[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_75 = {1{`RANDOM}};
  stripe_cnt = _RAND_75[6:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_76 = {1{`RANDOM}};
  dat_exec_valid_d1 = _RAND_76[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_77 = {1{`RANDOM}};
  dat_pipe_local_valid = _RAND_77[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_78 = {1{`RANDOM}};
  dat_pipe_valid_d1 = _RAND_78[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_79 = {1{`RANDOM}};
  dat_req_bytes_d1 = _RAND_79[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_80 = {1{`RANDOM}};
  dataout_w_cnt = _RAND_80[12:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_81 = {1{`RANDOM}};
  dataout_w_ori = _RAND_81[12:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_82 = {1{`RANDOM}};
  datain_c_cnt = _RAND_82[10:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_83 = {1{`RANDOM}};
  datain_w_cnt = _RAND_83[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_84 = {1{`RANDOM}};
  datain_w_ori = _RAND_84[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_85 = {1{`RANDOM}};
  pixel_w_cnt = _RAND_85[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_86 = {1{`RANDOM}};
  pixel_w_ori = _RAND_86[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_87 = {1{`RANDOM}};
  pixel_w_ch_ori = _RAND_87[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_88 = {1{`RANDOM}};
  channel_op_cnt = _RAND_88[12:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_89 = {1{`RANDOM}};
  pixel_force_clr_d1 = _RAND_89[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_90 = {1{`RANDOM}};
  pixel_force_fetch_d1 = _RAND_90[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_91 = {1{`RANDOM}};
  datain_h_cnt = _RAND_91[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_92 = {1{`RANDOM}};
  datain_h_ori = _RAND_92[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_93 = {1{`RANDOM}};
  dat_req_valid_d1 = _RAND_93[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_94 = {1{`RANDOM}};
  dat_req_sub_w_d1 = _RAND_94[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_95 = {1{`RANDOM}};
  dat_req_sub_h_d1 = _RAND_95[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_96 = {1{`RANDOM}};
  dat_req_sub_c_d1 = _RAND_96[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_97 = {1{`RANDOM}};
  dat_req_ch_end_d1 = _RAND_97[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_98 = {1{`RANDOM}};
  dat_req_dummy_d1 = _RAND_98[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_99 = {1{`RANDOM}};
  dat_req_cur_sub_h_d1 = _RAND_99[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_100 = {1{`RANDOM}};
  dat_req_sub_w_st_d1 = _RAND_100[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_101 = {1{`RANDOM}};
  dat_req_flag_d1 = _RAND_101[8:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_102 = {1{`RANDOM}};
  dat_req_rls_d1 = _RAND_102[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_103 = {1{`RANDOM}};
  c_bias = _RAND_103[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_104 = {1{`RANDOM}};
  c_bias_d1 = _RAND_104[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_105 = {1{`RANDOM}};
  h_bias_0_d1 = _RAND_105[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_106 = {1{`RANDOM}};
  h_bias_1_d1 = _RAND_106[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_107 = {1{`RANDOM}};
  h_bias_2_d1 = _RAND_107[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_108 = {1{`RANDOM}};
  h_bias_3_d1 = _RAND_108[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_109 = {1{`RANDOM}};
  w_bias_d1 = _RAND_109[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_110 = {1{`RANDOM}};
  dat_req_sub_h_addr_0 = _RAND_110[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_111 = {1{`RANDOM}};
  dat_req_sub_h_addr_1 = _RAND_111[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_112 = {1{`RANDOM}};
  dat_req_sub_h_addr_2 = _RAND_112[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_113 = {1{`RANDOM}};
  dat_req_sub_h_addr_3 = _RAND_113[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_114 = {1{`RANDOM}};
  sc2buf_dat_rd_en_out = _RAND_114[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_115 = {1{`RANDOM}};
  sc2buf_dat_rd_addr_out = _RAND_115[13:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_116 = {1{`RANDOM}};
  dat_req_pipe_sub_w = _RAND_116[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_117 = {1{`RANDOM}};
  dat_req_pipe_sub_h = _RAND_117[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_118 = {1{`RANDOM}};
  dat_req_pipe_sub_c = _RAND_118[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_119 = {1{`RANDOM}};
  dat_req_pipe_ch_end = _RAND_119[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_120 = {1{`RANDOM}};
  dat_req_pipe_bytes = _RAND_120[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_121 = {1{`RANDOM}};
  dat_req_pipe_dummy = _RAND_121[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_122 = {1{`RANDOM}};
  dat_req_pipe_cur_sub_h = _RAND_122[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_123 = {1{`RANDOM}};
  dat_req_pipe_sub_w_st = _RAND_123[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_124 = {1{`RANDOM}};
  dat_req_pipe_rls = _RAND_124[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_125 = {1{`RANDOM}};
  dat_req_pipe_flag = _RAND_125[8:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_126 = {1{`RANDOM}};
  dat_req_pipe_pvld = _RAND_126[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_127 = {1{`RANDOM}};
  dat_req_exec_pvld = _RAND_127[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_128 = {1{`RANDOM}};
  _T_1233 = _RAND_128[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_129 = {1{`RANDOM}};
  _T_1236 = _RAND_129[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_130 = {1{`RANDOM}};
  _T_1239 = _RAND_130[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_131 = {1{`RANDOM}};
  _T_1242 = _RAND_131[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_132 = {1{`RANDOM}};
  _T_1245 = _RAND_132[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_133 = {1{`RANDOM}};
  dat_rsp_pipe_pvld = _RAND_133[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_134 = {1{`RANDOM}};
  _T_1252 = _RAND_134[28:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_135 = {1{`RANDOM}};
  _T_1255 = _RAND_135[28:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_136 = {1{`RANDOM}};
  _T_1258 = _RAND_136[28:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_137 = {1{`RANDOM}};
  _T_1261 = _RAND_137[28:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_138 = {1{`RANDOM}};
  _T_1264 = _RAND_138[28:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_139 = {1{`RANDOM}};
  dat_rsp_pipe_pd = _RAND_139[28:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_140 = {1{`RANDOM}};
  _T_1271 = _RAND_140[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_141 = {1{`RANDOM}};
  _T_1274 = _RAND_141[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_142 = {1{`RANDOM}};
  _T_1277 = _RAND_142[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_143 = {1{`RANDOM}};
  _T_1280 = _RAND_143[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_144 = {1{`RANDOM}};
  _T_1283 = _RAND_144[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_145 = {1{`RANDOM}};
  dat_rsp_exec_pvld = _RAND_145[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_146 = {1{`RANDOM}};
  _T_1290 = _RAND_146[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_147 = {1{`RANDOM}};
  _T_1293 = _RAND_147[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_148 = {1{`RANDOM}};
  _T_1296 = _RAND_148[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_149 = {1{`RANDOM}};
  _T_1299 = _RAND_149[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_150 = {1{`RANDOM}};
  _T_1302 = _RAND_150[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_151 = {1{`RANDOM}};
  dat_rsp_exec_dummy = _RAND_151[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_152 = {1{`RANDOM}};
  _T_1309 = _RAND_152[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_153 = {1{`RANDOM}};
  _T_1312 = _RAND_153[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_154 = {1{`RANDOM}};
  _T_1315 = _RAND_154[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_155 = {1{`RANDOM}};
  _T_1318 = _RAND_155[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_156 = {1{`RANDOM}};
  _T_1321 = _RAND_156[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_157 = {1{`RANDOM}};
  dat_rsp_exec_sub_h = _RAND_157[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_158 = {1{`RANDOM}};
  dat_l0c0_dummy = _RAND_158[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_159 = {1{`RANDOM}};
  dat_l1c0_dummy = _RAND_159[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_160 = {1{`RANDOM}};
  dat_l2c0_dummy = _RAND_160[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_161 = {1{`RANDOM}};
  dat_l3c0_dummy = _RAND_161[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_162 = {1{`RANDOM}};
  dat_l0c1_dummy = _RAND_162[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_163 = {1{`RANDOM}};
  dat_l1c1_dummy = _RAND_163[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_164 = {1{`RANDOM}};
  dat_l2c1_dummy = _RAND_164[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_165 = {1{`RANDOM}};
  dat_l3c1_dummy = _RAND_165[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_166 = {2{`RANDOM}};
  dat_l0c0 = _RAND_166[63:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_167 = {2{`RANDOM}};
  dat_l1c0 = _RAND_167[63:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_168 = {2{`RANDOM}};
  dat_l2c0 = _RAND_168[63:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_169 = {2{`RANDOM}};
  dat_l3c0 = _RAND_169[63:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_170 = {2{`RANDOM}};
  dat_l0c1 = _RAND_170[63:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_171 = {2{`RANDOM}};
  dat_l1c1 = _RAND_171[63:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_172 = {2{`RANDOM}};
  dat_l2c1 = _RAND_172[63:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_173 = {2{`RANDOM}};
  dat_l3c1 = _RAND_173[63:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_174 = {1{`RANDOM}};
  rsp_sft_cnt_l0 = _RAND_174[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_175 = {1{`RANDOM}};
  rsp_sft_cnt_l1 = _RAND_175[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_176 = {1{`RANDOM}};
  rsp_sft_cnt_l2 = _RAND_176[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_177 = {1{`RANDOM}};
  rsp_sft_cnt_l3 = _RAND_177[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_178 = {1{`RANDOM}};
  rsp_sft_cnt_l0_ori = _RAND_178[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_179 = {1{`RANDOM}};
  rsp_sft_cnt_l1_ori = _RAND_179[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_180 = {1{`RANDOM}};
  rsp_sft_cnt_l2_ori = _RAND_180[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_181 = {1{`RANDOM}};
  rsp_sft_cnt_l3_ori = _RAND_181[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_182 = {1{`RANDOM}};
  dat_rsp_l2_pvld = _RAND_182[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_183 = {1{`RANDOM}};
  _T_1458 = _RAND_183[26:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_184 = {1{`RANDOM}};
  dat_rsp_l0_sft_d1 = _RAND_184[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_185 = {1{`RANDOM}};
  dat_rsp_l0_sft_d2 = _RAND_185[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_186 = {1{`RANDOM}};
  dat_rsp_l0_sft_d3 = _RAND_186[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_187 = {1{`RANDOM}};
  dat_rsp_l1_sft_d2 = _RAND_187[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_188 = {1{`RANDOM}};
  dat_rsp_l1_sft_d3 = _RAND_188[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_189 = {1{`RANDOM}};
  dat_rsp_l2_sft_d3 = _RAND_189[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_190 = {1{`RANDOM}};
  dat_out_pvld = _RAND_190[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_191 = {1{`RANDOM}};
  dat_out_flag = _RAND_191[8:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_192 = {1{`RANDOM}};
  dat_out_bypass_mask_0 = _RAND_192[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_193 = {1{`RANDOM}};
  dat_out_bypass_mask_1 = _RAND_193[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_194 = {1{`RANDOM}};
  dat_out_bypass_mask_2 = _RAND_194[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_195 = {1{`RANDOM}};
  dat_out_bypass_mask_3 = _RAND_195[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_196 = {1{`RANDOM}};
  dat_out_bypass_mask_4 = _RAND_196[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_197 = {1{`RANDOM}};
  dat_out_bypass_mask_5 = _RAND_197[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_198 = {1{`RANDOM}};
  dat_out_bypass_mask_6 = _RAND_198[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_199 = {1{`RANDOM}};
  dat_out_bypass_mask_7 = _RAND_199[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_200 = {1{`RANDOM}};
  _T_1943 = _RAND_200[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_201 = {1{`RANDOM}};
  _T_1946 = _RAND_201[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_202 = {1{`RANDOM}};
  _T_1949 = _RAND_202[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_203 = {1{`RANDOM}};
  _T_1952 = _RAND_203[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_204 = {1{`RANDOM}};
  _T_1955 = _RAND_204[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_205 = {1{`RANDOM}};
  _T_1958 = _RAND_205[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_206 = {1{`RANDOM}};
  _T_1961 = _RAND_206[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_207 = {1{`RANDOM}};
  _T_1964 = _RAND_207[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_208 = {1{`RANDOM}};
  dl_out_pvld = _RAND_208[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_209 = {1{`RANDOM}};
  dl_out_mask_0 = _RAND_209[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_210 = {1{`RANDOM}};
  dl_out_mask_1 = _RAND_210[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_211 = {1{`RANDOM}};
  dl_out_mask_2 = _RAND_211[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_212 = {1{`RANDOM}};
  dl_out_mask_3 = _RAND_212[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_213 = {1{`RANDOM}};
  dl_out_mask_4 = _RAND_213[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_214 = {1{`RANDOM}};
  dl_out_mask_5 = _RAND_214[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_215 = {1{`RANDOM}};
  dl_out_mask_6 = _RAND_215[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_216 = {1{`RANDOM}};
  dl_out_mask_7 = _RAND_216[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_217 = {1{`RANDOM}};
  dl_out_flag = _RAND_217[8:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_218 = {1{`RANDOM}};
  _T_2049 = _RAND_218[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_219 = {1{`RANDOM}};
  _T_2052 = _RAND_219[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_220 = {1{`RANDOM}};
  _T_2055 = _RAND_220[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_221 = {1{`RANDOM}};
  _T_2058 = _RAND_221[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_222 = {1{`RANDOM}};
  _T_2061 = _RAND_222[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_223 = {1{`RANDOM}};
  _T_2064 = _RAND_223[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_224 = {1{`RANDOM}};
  _T_2067 = _RAND_224[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_225 = {1{`RANDOM}};
  _T_2070 = _RAND_225[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_226 = {1{`RANDOM}};
  dl_out_pvld_d1 = _RAND_226[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_227 = {1{`RANDOM}};
  _T_2120 = _RAND_227[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_228 = {1{`RANDOM}};
  _T_2123 = _RAND_228[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_229 = {1{`RANDOM}};
  _T_2127 = _RAND_229[8:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_230 = {1{`RANDOM}};
  _T_2131 = _RAND_230[8:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_231 = {1{`RANDOM}};
  _T_2175_0 = _RAND_231[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_232 = {1{`RANDOM}};
  _T_2175_1 = _RAND_232[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_233 = {1{`RANDOM}};
  _T_2175_2 = _RAND_233[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_234 = {1{`RANDOM}};
  _T_2175_3 = _RAND_234[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_235 = {1{`RANDOM}};
  _T_2175_4 = _RAND_235[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_236 = {1{`RANDOM}};
  _T_2175_5 = _RAND_236[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_237 = {1{`RANDOM}};
  _T_2175_6 = _RAND_237[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_238 = {1{`RANDOM}};
  _T_2175_7 = _RAND_238[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_239 = {1{`RANDOM}};
  _T_2247_0 = _RAND_239[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_240 = {1{`RANDOM}};
  _T_2247_1 = _RAND_240[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_241 = {1{`RANDOM}};
  _T_2247_2 = _RAND_241[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_242 = {1{`RANDOM}};
  _T_2247_3 = _RAND_242[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_243 = {1{`RANDOM}};
  _T_2247_4 = _RAND_243[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_244 = {1{`RANDOM}};
  _T_2247_5 = _RAND_244[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_245 = {1{`RANDOM}};
  _T_2247_6 = _RAND_245[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_246 = {1{`RANDOM}};
  _T_2247_7 = _RAND_246[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_247 = {1{`RANDOM}};
  _T_2278 = _RAND_247[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_248 = {1{`RANDOM}};
  _T_2281 = _RAND_248[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_249 = {1{`RANDOM}};
  _T_2284 = _RAND_249[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_250 = {1{`RANDOM}};
  _T_2287 = _RAND_250[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_251 = {1{`RANDOM}};
  _T_2290 = _RAND_251[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_252 = {1{`RANDOM}};
  _T_2293 = _RAND_252[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_253 = {1{`RANDOM}};
  _T_2296 = _RAND_253[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_254 = {1{`RANDOM}};
  _T_2299 = _RAND_254[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_255 = {1{`RANDOM}};
  _T_2302 = _RAND_255[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_256 = {1{`RANDOM}};
  _T_2305 = _RAND_256[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_257 = {1{`RANDOM}};
  _T_2308 = _RAND_257[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_258 = {1{`RANDOM}};
  _T_2311 = _RAND_258[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_259 = {1{`RANDOM}};
  _T_2314 = _RAND_259[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_260 = {1{`RANDOM}};
  _T_2317 = _RAND_260[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_261 = {1{`RANDOM}};
  _T_2320 = _RAND_261[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_262 = {1{`RANDOM}};
  _T_2323 = _RAND_262[7:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge io_nvdla_core_clk) begin
    if (reset) begin
      layer_st_d1 <= 1'h0;
    end else begin
      layer_st_d1 <= layer_st;
    end
    if (reset) begin
      data_batch <= 6'h0;
    end else begin
      if (layer_st) begin
        data_batch <= 6'h1;
      end
    end
    if (reset) begin
      rls_slices <= 14'h0;
    end else begin
      if (layer_st) begin
        rls_slices <= {{1'd0}, rls_slices_w};
      end
    end
    if (reset) begin
      h_offset_slice <= 14'h0;
    end else begin
      if (layer_st) begin
        h_offset_slice <= {{2'd0}, h_offset_slice_w};
      end
    end
    if (reset) begin
      entries <= 15'h0;
    end else begin
      if (layer_st) begin
        entries <= entries_single_w;
      end
    end
    if (reset) begin
      entries_batch <= 15'h0;
    end else begin
      if (layer_st) begin
        entries_batch <= entries_batch_w;
      end
    end
    if (reset) begin
      dataout_width_cmp <= 13'h0;
    end else begin
      if (layer_st) begin
        dataout_width_cmp <= io_reg2dp_dataout_width;
      end
    end
    if (reset) begin
      rls_entries <= 15'h0;
    end else begin
      if (layer_st_d1) begin
        rls_entries <= slice_entries_w;
      end
    end
    if (reset) begin
      h_bias_0_stride <= 14'h0;
    end else begin
      if (layer_st_d1) begin
        h_bias_0_stride <= h_bias_0_stride_w;
      end
    end
    if (reset) begin
      h_bias_1_stride <= 14'h0;
    end else begin
      if (layer_st_d1) begin
        h_bias_1_stride <= h_bias_1_stride_w;
      end
    end
    if (reset) begin
      slice_left <= 14'h0;
    end else begin
      if (layer_st) begin
        if (io_reg2dp_skip_data_rls) begin
          slice_left <= _T_318;
        end else begin
          slice_left <= _T_320;
        end
      end
    end
    if (reset) begin
      is_img_d1 <= 34'h0;
    end else begin
      if (layer_st) begin
        if (is_img) begin
          is_img_d1 <= 34'h3ffffffff;
        end else begin
          is_img_d1 <= 34'h0;
        end
      end
    end
    if (reset) begin
      data_bank <= 5'h0;
    end else begin
      if (layer_st) begin
        data_bank <= _T_509;
      end
    end
    if (reset) begin
      datain_width <= 14'h0;
    end else begin
      if (layer_st) begin
        datain_width <= _T_511;
      end
    end
    if (reset) begin
      datain_width_cmp <= 13'h0;
    end else begin
      if (layer_st) begin
        datain_width_cmp <= io_reg2dp_datain_width_ext;
      end
    end
    if (reset) begin
      datain_height_cmp <= 13'h0;
    end else begin
      if (layer_st) begin
        datain_height_cmp <= io_reg2dp_datain_height_ext;
      end
    end
    if (reset) begin
      datain_channel_cmp <= 11'h0;
    end else begin
      if (layer_st) begin
        datain_channel_cmp <= _T_514;
      end
    end
    if (reset) begin
      sub_h_total_g0 <= 3'h1;
    end else begin
      if (layer_st) begin
        sub_h_total_g0 <= sub_h_total_w;
      end
    end
    if (reset) begin
      sub_h_total_g1 <= 3'h1;
    end else begin
      if (layer_st) begin
        sub_h_total_g1 <= sub_h_total_w;
      end
    end
    if (reset) begin
      sub_h_total_g2 <= 2'h1;
    end else begin
      if (layer_st) begin
        sub_h_total_g2 <= _T_515;
      end
    end
    if (reset) begin
      sub_h_total_g3 <= 3'h1;
    end else begin
      if (layer_st) begin
        sub_h_total_g3 <= sub_h_total_w;
      end
    end
    if (reset) begin
      sub_h_total_g4 <= 3'h1;
    end else begin
      if (layer_st) begin
        sub_h_total_g4 <= sub_h_total_w;
      end
    end
    if (reset) begin
      sub_h_total_g5 <= 3'h1;
    end else begin
      if (layer_st) begin
        sub_h_total_g5 <= sub_h_total_w;
      end
    end
    if (reset) begin
      sub_h_total_g6 <= 3'h1;
    end else begin
      if (layer_st) begin
        sub_h_total_g6 <= sub_h_total_w;
      end
    end
    if (reset) begin
      sub_h_total_g8 <= 3'h1;
    end else begin
      if (layer_st) begin
        sub_h_total_g8 <= sub_h_total_w;
      end
    end
    if (reset) begin
      sub_h_total_g9 <= 3'h1;
    end else begin
      if (layer_st) begin
        sub_h_total_g9 <= sub_h_total_w;
      end
    end
    if (reset) begin
      sub_h_total_g11 <= 3'h1;
    end else begin
      if (layer_st) begin
        sub_h_total_g11 <= sub_h_total_w;
      end
    end
    if (reset) begin
      sub_h_cmp_g0 <= 3'h1;
    end else begin
      if (layer_st) begin
        if (is_img) begin
          sub_h_cmp_g0 <= sub_h_total_w;
        end else begin
          sub_h_cmp_g0 <= 3'h1;
        end
      end
    end
    if (reset) begin
      sub_h_cmp_g1 <= 3'h1;
    end else begin
      if (layer_st) begin
        if (is_img) begin
          sub_h_cmp_g1 <= sub_h_total_w;
        end else begin
          sub_h_cmp_g1 <= 3'h1;
        end
      end
    end
    if (reset) begin
      conv_x_stride <= 4'h0;
    end else begin
      if (layer_st) begin
        conv_x_stride <= conv_x_stride_w;
      end
    end
    if (reset) begin
      conv_y_stride <= 4'h0;
    end else begin
      if (layer_st) begin
        conv_y_stride <= conv_y_stride_w;
      end
    end
    if (reset) begin
      batch_cmp <= 5'h0;
    end else begin
      if (layer_st) begin
        batch_cmp <= 5'h0;
      end
    end
    if (reset) begin
      pixel_x_init <= 6'h0;
    end else begin
      if (layer_st) begin
        pixel_x_init <= _T_517;
      end
    end
    if (reset) begin
      pixel_x_init_offset <= 7'h0;
    end else begin
      if (layer_st) begin
        pixel_x_init_offset <= {{3'd0}, pixel_x_init_offset_w};
      end
    end
    if (reset) begin
      pixel_x_add <= 7'h0;
    end else begin
      if (layer_st) begin
        pixel_x_add <= _T_518;
      end
    end
    if (reset) begin
      pixel_x_byte_stride <= 7'h0;
    end else begin
      if (layer_st) begin
        pixel_x_byte_stride <= {{1'd0}, pixel_x_stride_w};
      end
    end
    if (reset) begin
      pixel_ch_stride <= 12'h0;
    end else begin
      if (layer_st) begin
        pixel_ch_stride <= {{2'd0}, pixel_ch_stride_w};
      end
    end
    if (reset) begin
      x_dilate <= 6'h0;
    end else begin
      if (layer_st) begin
        if (is_img) begin
          x_dilate <= 6'h1;
        end else begin
          x_dilate <= _T_239;
        end
      end
    end
    if (reset) begin
      y_dilate <= 6'h0;
    end else begin
      if (layer_st) begin
        if (is_img) begin
          y_dilate <= 6'h1;
        end else begin
          y_dilate <= _T_242;
        end
      end
    end
    if (reset) begin
      pad_value <= 16'h0;
    end else begin
      if (layer_st) begin
        pad_value <= io_reg2dp_pad_value;
      end
    end
    if (reset) begin
      entries_cmp <= 15'h0;
    end else begin
      if (layer_st) begin
        entries_cmp <= _T_520;
      end
    end
    if (reset) begin
      h_bias_2_stride <= 15'h0;
    end else begin
      if (layer_st_d1) begin
        h_bias_2_stride <= entries;
      end
    end
    if (reset) begin
      h_bias_3_stride <= 15'h0;
    end else begin
      if (layer_st_d1) begin
        h_bias_3_stride <= entries;
      end
    end
    if (reset) begin
      last_slices <= 14'h0;
    end else begin
      if (is_sg_done) begin
        last_slices <= slice_left;
      end
    end
    if (reset) begin
      last_entries <= 15'h0;
    end else begin
      if (is_sg_done) begin
        last_entries <= slice_entries_w;
      end
    end
    if (reset) begin
      dat_rsp_l3_pvld <= 1'h0;
    end else begin
      dat_rsp_l3_pvld <= dat_rsp_l2_pvld;
    end
    if (reset) begin
      dat_rsp_l1_pvld <= 1'h0;
    end else begin
      dat_rsp_l1_pvld <= dat_rsp_l0_pvld;
    end
    if (reset) begin
      dat_rsp_l0_pvld <= 1'h0;
    end else begin
      dat_rsp_l0_pvld <= dat_rsp_pipe_pvld;
    end
    if (reset) begin
      _T_1461 <= 27'h0;
    end else begin
      if (dat_rsp_l2_pvld) begin
        _T_1461 <= _T_1458;
      end
    end
    if (reset) begin
      _T_1455 <= 27'h0;
    end else begin
      if (dat_rsp_l0_pvld) begin
        _T_1455 <= _T_1452;
      end
    end
    if (reset) begin
      _T_1452 <= 27'h0;
    end else begin
      if (dat_rsp_pipe_pvld) begin
        _T_1452 <= _T_1470;
      end
    end
    if (reset) begin
      _T_607 <= 1'h0;
    end else begin
      _T_607 <= dat_rls;
    end
    if (reset) begin
      _T_610 <= 14'h0;
    end else begin
      if (dat_rls) begin
        if (sub_rls) begin
          _T_610 <= rls_slices;
        end else begin
          _T_610 <= last_slices;
        end
      end
    end
    if (reset) begin
      _T_613 <= 15'h0;
    end else begin
      if (dat_rls) begin
        if (sub_rls) begin
          _T_613 <= rls_entries;
        end else begin
          _T_613 <= last_entries;
        end
      end
    end
    if (reset) begin
      _T_618 <= 1'h0;
    end else begin
      _T_618 <= io_sg2dl_pd_valid;
    end
    if (reset) begin
      _T_621 <= 1'h0;
    end else begin
      _T_621 <= _T_618;
    end
    if (reset) begin
      _T_624 <= 1'h0;
    end else begin
      _T_624 <= _T_621;
    end
    if (reset) begin
      _T_627 <= 1'h0;
    end else begin
      _T_627 <= _T_624;
    end
    if (reset) begin
      dl_in_pvld <= 1'h0;
    end else begin
      dl_in_pvld <= _T_627;
    end
    if (reset) begin
      _T_634 <= 31'h0;
    end else begin
      if (io_sg2dl_pd_valid) begin
        _T_634 <= io_sg2dl_pd_bits;
      end
    end
    if (reset) begin
      _T_637 <= 31'h0;
    end else begin
      if (_T_618) begin
        _T_637 <= _T_634;
      end
    end
    if (reset) begin
      _T_640 <= 31'h0;
    end else begin
      if (_T_621) begin
        _T_640 <= _T_637;
      end
    end
    if (reset) begin
      _T_643 <= 31'h0;
    end else begin
      if (_T_624) begin
        _T_643 <= _T_640;
      end
    end
    if (reset) begin
      dl_in_pd <= 31'h0;
    end else begin
      if (_T_627) begin
        dl_in_pd <= _T_643;
      end
    end
    if (reset) begin
      _T_650 <= 1'h0;
    end else begin
      _T_650 <= dl_in_pvld;
    end
    if (reset) begin
      _T_653 <= 1'h0;
    end else begin
      _T_653 <= _T_650;
    end
    if (reset) begin
      _T_656 <= 1'h0;
    end else begin
      _T_656 <= _T_653;
    end
    if (reset) begin
      _T_659 <= 1'h0;
    end else begin
      _T_659 <= _T_656;
    end
    if (reset) begin
      _T_664 <= 31'h0;
    end else begin
      if (dl_in_pvld) begin
        _T_664 <= dl_in_pd;
      end
    end
    if (reset) begin
      _T_667 <= 31'h0;
    end else begin
      if (_T_650) begin
        _T_667 <= _T_664;
      end
    end
    if (reset) begin
      _T_670 <= 31'h0;
    end else begin
      if (_T_653) begin
        _T_670 <= _T_667;
      end
    end
    if (reset) begin
      _T_673 <= 31'h0;
    end else begin
      if (_T_656) begin
        _T_673 <= _T_670;
      end
    end
    if (reset) begin
      batch_cnt <= 5'h0;
    end else begin
      if (layer_st) begin
        batch_cnt <= 5'h0;
      end else begin
        if (is_batch_end) begin
          batch_cnt <= 5'h0;
        end else begin
          batch_cnt <= _T_708;
        end
      end
    end
    if (reset) begin
      sub_h_cnt <= 2'h0;
    end else begin
      sub_h_cnt <= _GEN_65[1:0];
    end
    if (reset) begin
      stripe_cnt <= 7'h0;
    end else begin
      if (stripe_cnt_reg_en) begin
        if (layer_st) begin
          stripe_cnt <= 7'h0;
        end else begin
          if (!(_T_735)) begin
            if (is_stripe_end) begin
              stripe_cnt <= 7'h0;
            end else begin
              stripe_cnt <= stripe_cnt_inc;
            end
          end
        end
      end
    end
    if (reset) begin
      dat_exec_valid_d1 <= 1'h0;
    end else begin
      if (dl_pvld) begin
        dat_exec_valid_d1 <= 1'h1;
      end else begin
        if (_T_763) begin
          dat_exec_valid_d1 <= 1'h0;
        end
      end
    end
    if (reset) begin
      dat_pipe_local_valid <= 1'h0;
    end else begin
      if (_T_747) begin
        dat_pipe_local_valid <= 1'h0;
      end else begin
        if (dl_pvld) begin
          dat_pipe_local_valid <= 1'h1;
        end
      end
    end
    if (reset) begin
      dat_pipe_valid_d1 <= 1'h0;
    end else begin
      dat_pipe_valid_d1 <= dat_pipe_valid;
    end
    if (reset) begin
      dat_req_bytes_d1 <= 8'h0;
    end else begin
      if (dat_exec_valid) begin
        dat_req_bytes_d1 <= dat_req_bytes;
      end
    end
    if (reset) begin
      dataout_w_cnt <= 13'h0;
    end else begin
      if (dataout_w_cnt_reg_en) begin
        if (layer_st) begin
          dataout_w_cnt <= {{9'd0}, dataout_w_init};
        end else begin
          if (_T_779) begin
            dataout_w_cnt <= dataout_w_ori;
          end else begin
            if (is_w_end) begin
              dataout_w_cnt <= {{9'd0}, dataout_w_init};
            end else begin
              dataout_w_cnt <= dataout_w_cnt_inc;
            end
          end
        end
      end
    end
    if (reset) begin
      dataout_w_ori <= 13'h0;
    end else begin
      if (dataout_w_ori_reg_en) begin
        if (layer_st) begin
          dataout_w_ori <= {{9'd0}, dataout_w_init};
        end else begin
          if (!(_T_779)) begin
            if (is_w_end) begin
              dataout_w_ori <= {{9'd0}, dataout_w_init};
            end else begin
              dataout_w_ori <= dataout_w_cnt_inc;
            end
          end
        end
      end
    end
    if (reset) begin
      datain_c_cnt <= 11'h0;
    end else begin
      if (datain_c_cnt_reg_en) begin
        if (layer_st) begin
          datain_c_cnt <= 11'h0;
        end else begin
          if (dl_channel_end) begin
            datain_c_cnt <= 11'h0;
          end else begin
            datain_c_cnt <= _T_794;
          end
        end
      end
    end
    if (reset) begin
      datain_w_cnt <= 14'h0;
    end else begin
      if (datain_w_cnt_reg_en) begin
        if (layer_st) begin
          if (is_img) begin
            datain_w_cnt <= 14'h0;
          end else begin
            datain_w_cnt <= _T_817;
          end
        end else begin
          if (_T_779) begin
            datain_w_cnt <= datain_w_ori;
          end else begin
            if (is_w_end) begin
              if (is_img) begin
                datain_w_cnt <= 14'h0;
              end else begin
                datain_w_cnt <= _T_817;
              end
            end else begin
              datain_w_cnt <= datain_w_cnt_inc;
            end
          end
        end
      end
    end
    if (reset) begin
      datain_w_ori <= 14'h0;
    end else begin
      if (datain_w_ori_reg_en) begin
        if (layer_st) begin
          if (is_img) begin
            datain_w_ori <= 14'h0;
          end else begin
            datain_w_ori <= _T_817;
          end
        end else begin
          if (!(_T_779)) begin
            if (is_w_end) begin
              if (is_img) begin
                datain_w_ori <= 14'h0;
              end else begin
                datain_w_ori <= _T_817;
              end
            end else begin
              datain_w_ori <= datain_w_cnt_inc;
            end
          end
        end
      end
    end
    if (reset) begin
      pixel_w_cnt <= 16'h0;
    end else begin
      if (pixel_w_cnt_reg_en) begin
        if (layer_st_d1) begin
          pixel_w_cnt <= {{10'd0}, pixel_x_init};
        end else begin
          if (_T_852) begin
            pixel_w_cnt <= {{10'd0}, pixel_x_init};
          end else begin
            if (_T_856) begin
              pixel_w_cnt <= _T_858;
            end else begin
              if (_T_860) begin
                pixel_w_cnt <= _T_862;
              end else begin
                if (_T_865) begin
                  pixel_w_cnt <= _T_868;
                end else begin
                  if (_T_870) begin
                    pixel_w_cnt <= pixel_w_ori;
                  end else begin
                    pixel_w_cnt <= _T_872;
                  end
                end
              end
            end
          end
        end
      end
    end
    if (reset) begin
      pixel_w_ori <= 16'h0;
    end else begin
      if (pixel_w_ori_reg_en) begin
        if (layer_st_d1) begin
          pixel_w_ori <= {{10'd0}, pixel_x_init};
        end else begin
          if (_T_852) begin
            pixel_w_ori <= {{10'd0}, pixel_x_init};
          end else begin
            if (_T_856) begin
              pixel_w_ori <= _T_858;
            end else begin
              if (_T_860) begin
                pixel_w_ori <= _T_862;
              end else begin
                if (_T_865) begin
                  pixel_w_ori <= _T_868;
                end else begin
                  if (!(_T_870)) begin
                    pixel_w_ori <= _T_872;
                  end
                end
              end
            end
          end
        end
      end
    end
    if (reset) begin
      pixel_w_ch_ori <= 16'h0;
    end else begin
      if (pixel_ch_ori_reg_en) begin
        if (layer_st_d1) begin
          pixel_w_ch_ori <= {{10'd0}, pixel_x_init};
        end else begin
          if (_T_852) begin
            pixel_w_ch_ori <= {{10'd0}, pixel_x_init};
          end else begin
            if (_T_856) begin
              pixel_w_ch_ori <= _T_858;
            end else begin
              if (_T_860) begin
                pixel_w_ch_ori <= _T_862;
              end else begin
                if (_T_865) begin
                  pixel_w_ch_ori <= _T_868;
                end else begin
                  if (_T_870) begin
                    pixel_w_ch_ori <= pixel_w_ori;
                  end else begin
                    pixel_w_ch_ori <= _T_872;
                  end
                end
              end
            end
          end
        end
      end
    end
    if (reset) begin
      channel_op_cnt <= 13'h2;
    end else begin
      if (_T_842) begin
        channel_op_cnt <= 13'h2;
      end else begin
        if (_T_844) begin
          channel_op_cnt <= _T_847;
        end
      end
    end
    if (reset) begin
      pixel_force_clr_d1 <= 1'h0;
    end else begin
      if (dat_exec_valid) begin
        pixel_force_clr_d1 <= pixel_force_clr;
      end
    end
    if (reset) begin
      pixel_force_fetch_d1 <= 1'h0;
    end else begin
      if (dat_exec_valid) begin
        if (_T_898) begin
          pixel_force_fetch_d1 <= 1'h1;
        end else begin
          if (pixel_force_clr_d1) begin
            pixel_force_fetch_d1 <= 1'h0;
          end
        end
      end
    end
    if (reset) begin
      datain_h_cnt <= 14'h0;
    end else begin
      if (datain_h_cnt_reg_en) begin
        if (_T_914) begin
          datain_h_cnt <= datain_h_cnt_st;
        end else begin
          if (_T_779) begin
            datain_h_cnt <= datain_h_ori;
          end else begin
            if (is_w_end) begin
              datain_h_cnt <= datain_h_cnt_inc;
            end
          end
        end
      end
    end
    if (reset) begin
      datain_h_ori <= 14'h0;
    end else begin
      if (dataout_w_ori_reg_en) begin
        if (_T_914) begin
          datain_h_ori <= datain_h_cnt_st;
        end else begin
          if (!(_T_779)) begin
            if (is_w_end) begin
              datain_h_ori <= datain_h_cnt_inc;
            end else begin
              datain_h_ori <= datain_h_cnt;
            end
          end
        end
      end
    end
    if (reset) begin
      dat_req_valid_d1 <= 1'h0;
    end else begin
      dat_req_valid_d1 <= dat_req_valid;
    end
    if (reset) begin
      dat_req_sub_w_d1 <= 2'h0;
    end else begin
      if (dat_exec_valid) begin
        dat_req_sub_w_d1 <= dat_req_sub_w_w;
      end
    end
    if (reset) begin
      dat_req_sub_h_d1 <= 2'h0;
    end else begin
      if (dat_exec_valid) begin
        dat_req_sub_h_d1 <= sub_h_cnt;
      end
    end
    if (reset) begin
      dat_req_sub_c_d1 <= 1'h0;
    end else begin
      if (dat_exec_valid) begin
        if (_T_952) begin
          dat_req_sub_c_d1 <= _T_1018;
        end else begin
          dat_req_sub_c_d1 <= dl_block_end;
        end
      end
    end
    if (reset) begin
      dat_req_ch_end_d1 <= 1'h0;
    end else begin
      if (dat_exec_valid) begin
        dat_req_ch_end_d1 <= is_last_channel;
      end
    end
    if (reset) begin
      dat_req_dummy_d1 <= 1'h0;
    end else begin
      if (dat_exec_valid) begin
        if (_T_946) begin
          dat_req_dummy_d1 <= dat_img_req_dummy;
        end else begin
          dat_req_dummy_d1 <= dat_conv_req_dummy;
        end
      end
    end
    if (reset) begin
      dat_req_cur_sub_h_d1 <= 2'h0;
    end else begin
      if (dat_exec_valid) begin
        dat_req_cur_sub_h_d1 <= dl_cur_sub_h;
      end
    end
    if (reset) begin
      dat_req_sub_w_st_d1 <= 1'h0;
    end else begin
      if (dat_req_sub_w_st_en) begin
        dat_req_sub_w_st_d1 <= dl_pvld;
      end
    end
    if (reset) begin
      dat_req_flag_d1 <= 9'h0;
    end else begin
      if (dat_exec_valid) begin
        dat_req_flag_d1 <= dat_req_flag_w;
      end
    end
    if (reset) begin
      dat_req_rls_d1 <= 1'h0;
    end else begin
      if (dat_exec_valid) begin
        dat_req_rls_d1 <= _T_980;
      end
    end
    if (reset) begin
      c_bias <= 14'h0;
    end else begin
      if (datain_c_cnt_reg_en) begin
        if (layer_st) begin
          c_bias <= 14'h0;
        end else begin
          if (_T_1000) begin
            c_bias <= 14'h0;
          end else begin
            c_bias <= _T_1003;
          end
        end
      end
    end
    if (reset) begin
      c_bias_d1 <= 14'h0;
    end else begin
      if (c_bias_d1_reg_en) begin
        c_bias_d1 <= c_bias;
      end
    end
    if (reset) begin
      h_bias_0_d1 <= 14'h0;
    end else begin
      if (_T_1029) begin
        h_bias_0_d1 <= h_bias_0_w;
      end
    end
    if (reset) begin
      h_bias_1_d1 <= 14'h0;
    end else begin
      if (_T_1029) begin
        h_bias_1_d1 <= h_bias_1_w;
      end
    end
    if (reset) begin
      h_bias_2_d1 <= 14'h0;
    end else begin
      if (_T_1029) begin
        h_bias_2_d1 <= h_bias_2_w;
      end
    end
    if (reset) begin
      h_bias_3_d1 <= 14'h0;
    end else begin
      if (_T_1030) begin
        h_bias_3_d1 <= h_bias_3_w;
      end
    end
    if (reset) begin
      w_bias_d1 <= 14'h0;
    end else begin
      if (dat_exec_valid) begin
        w_bias_d1 <= w_bias_w;
      end
    end
    if (reset) begin
      dat_req_sub_h_addr_0 <= 14'h3fff;
    end else begin
      if (dat_req_sub_h_addr_en_0) begin
        dat_req_sub_h_addr_0 <= dat_req_addr_w;
      end
    end
    if (reset) begin
      dat_req_sub_h_addr_1 <= 14'h3fff;
    end else begin
      if (dat_req_sub_h_addr_en_1) begin
        dat_req_sub_h_addr_1 <= dat_req_addr_w;
      end
    end
    if (reset) begin
      dat_req_sub_h_addr_2 <= 14'h3fff;
    end else begin
      if (dat_req_sub_h_addr_en_2) begin
        dat_req_sub_h_addr_2 <= dat_req_addr_w;
      end
    end
    if (reset) begin
      dat_req_sub_h_addr_3 <= 14'h3fff;
    end else begin
      if (dat_req_sub_h_addr_en_3) begin
        dat_req_sub_h_addr_3 <= dat_req_addr_w;
      end
    end
    if (reset) begin
      sc2buf_dat_rd_en_out <= 1'h0;
    end else begin
      sc2buf_dat_rd_en_out <= sc2buf_dat_rd_en_w;
    end
    if (reset) begin
      sc2buf_dat_rd_addr_out <= 14'h3fff;
    end else begin
      if (_T_1207) begin
        sc2buf_dat_rd_addr_out <= dat_req_addr_w;
      end
    end
    if (reset) begin
      dat_req_pipe_sub_w <= 2'h0;
    end else begin
      if (dat_exec_valid_d1) begin
        dat_req_pipe_sub_w <= dat_req_sub_w_d1;
      end
    end
    if (reset) begin
      dat_req_pipe_sub_h <= 2'h0;
    end else begin
      if (dat_exec_valid_d1) begin
        dat_req_pipe_sub_h <= dat_req_sub_h_d1;
      end
    end
    if (reset) begin
      dat_req_pipe_sub_c <= 1'h0;
    end else begin
      if (dat_exec_valid_d1) begin
        dat_req_pipe_sub_c <= dat_req_sub_c_d1;
      end
    end
    if (reset) begin
      dat_req_pipe_ch_end <= 1'h0;
    end else begin
      if (dat_exec_valid_d1) begin
        dat_req_pipe_ch_end <= dat_req_ch_end_d1;
      end
    end
    if (reset) begin
      dat_req_pipe_bytes <= 8'h0;
    end else begin
      if (dat_exec_valid_d1) begin
        dat_req_pipe_bytes <= dat_req_bytes_d1;
      end
    end
    if (reset) begin
      dat_req_pipe_dummy <= 1'h0;
    end else begin
      if (dat_exec_valid_d1) begin
        dat_req_pipe_dummy <= dat_req_dummy_d1;
      end
    end
    if (reset) begin
      dat_req_pipe_cur_sub_h <= 2'h0;
    end else begin
      if (dat_exec_valid_d1) begin
        dat_req_pipe_cur_sub_h <= dat_req_cur_sub_h_d1;
      end
    end
    if (reset) begin
      dat_req_pipe_sub_w_st <= 1'h0;
    end else begin
      if (dat_exec_valid_d1) begin
        dat_req_pipe_sub_w_st <= dat_req_sub_w_st_d1;
      end
    end
    if (reset) begin
      dat_req_pipe_rls <= 1'h0;
    end else begin
      if (dat_exec_valid_d1) begin
        dat_req_pipe_rls <= dat_req_rls_d1;
      end
    end
    if (reset) begin
      dat_req_pipe_flag <= 9'h0;
    end else begin
      if (dat_exec_valid_d1) begin
        dat_req_pipe_flag <= dat_req_flag_d1;
      end
    end
    if (reset) begin
      dat_req_pipe_pvld <= 1'h0;
    end else begin
      dat_req_pipe_pvld <= dat_pipe_valid_d1;
    end
    if (reset) begin
      dat_req_exec_pvld <= 1'h0;
    end else begin
      dat_req_exec_pvld <= dat_exec_valid_d1;
    end
    if (reset) begin
      _T_1233 <= 1'h0;
    end else begin
      _T_1233 <= dat_req_pipe_pvld;
    end
    if (reset) begin
      _T_1236 <= 1'h0;
    end else begin
      _T_1236 <= _T_1233;
    end
    if (reset) begin
      _T_1239 <= 1'h0;
    end else begin
      _T_1239 <= _T_1236;
    end
    if (reset) begin
      _T_1242 <= 1'h0;
    end else begin
      _T_1242 <= _T_1239;
    end
    if (reset) begin
      _T_1245 <= 1'h0;
    end else begin
      _T_1245 <= _T_1242;
    end
    if (reset) begin
      dat_rsp_pipe_pvld <= 1'h0;
    end else begin
      dat_rsp_pipe_pvld <= _T_1245;
    end
    if (reset) begin
      _T_1252 <= 29'h0;
    end else begin
      if (dat_req_pipe_pvld) begin
        _T_1252 <= dat_req_pipe_pd;
      end
    end
    if (reset) begin
      _T_1255 <= 29'h0;
    end else begin
      if (_T_1233) begin
        _T_1255 <= _T_1252;
      end
    end
    if (reset) begin
      _T_1258 <= 29'h0;
    end else begin
      if (_T_1236) begin
        _T_1258 <= _T_1255;
      end
    end
    if (reset) begin
      _T_1261 <= 29'h0;
    end else begin
      if (_T_1239) begin
        _T_1261 <= _T_1258;
      end
    end
    if (reset) begin
      _T_1264 <= 29'h0;
    end else begin
      if (_T_1242) begin
        _T_1264 <= _T_1261;
      end
    end
    if (reset) begin
      dat_rsp_pipe_pd <= 29'h0;
    end else begin
      if (_T_1245) begin
        dat_rsp_pipe_pd <= _T_1264;
      end
    end
    if (reset) begin
      _T_1271 <= 1'h0;
    end else begin
      _T_1271 <= dat_req_exec_pvld;
    end
    if (reset) begin
      _T_1274 <= 1'h0;
    end else begin
      _T_1274 <= _T_1271;
    end
    if (reset) begin
      _T_1277 <= 1'h0;
    end else begin
      _T_1277 <= _T_1274;
    end
    if (reset) begin
      _T_1280 <= 1'h0;
    end else begin
      _T_1280 <= _T_1277;
    end
    if (reset) begin
      _T_1283 <= 1'h0;
    end else begin
      _T_1283 <= _T_1280;
    end
    if (reset) begin
      dat_rsp_exec_pvld <= 1'h0;
    end else begin
      dat_rsp_exec_pvld <= _T_1283;
    end
    if (reset) begin
      _T_1290 <= 1'h0;
    end else begin
      if (dat_req_exec_pvld) begin
        _T_1290 <= dat_req_pipe_dummy;
      end
    end
    if (reset) begin
      _T_1293 <= 1'h0;
    end else begin
      if (_T_1271) begin
        _T_1293 <= _T_1290;
      end
    end
    if (reset) begin
      _T_1296 <= 1'h0;
    end else begin
      if (_T_1274) begin
        _T_1296 <= _T_1293;
      end
    end
    if (reset) begin
      _T_1299 <= 1'h0;
    end else begin
      if (_T_1277) begin
        _T_1299 <= _T_1296;
      end
    end
    if (reset) begin
      _T_1302 <= 1'h0;
    end else begin
      if (_T_1280) begin
        _T_1302 <= _T_1299;
      end
    end
    if (reset) begin
      dat_rsp_exec_dummy <= 1'h0;
    end else begin
      if (_T_1283) begin
        dat_rsp_exec_dummy <= _T_1302;
      end
    end
    if (reset) begin
      _T_1309 <= 2'h0;
    end else begin
      if (dat_req_exec_pvld) begin
        _T_1309 <= dat_req_pipe_sub_h;
      end
    end
    if (reset) begin
      _T_1312 <= 2'h0;
    end else begin
      if (_T_1271) begin
        _T_1312 <= _T_1309;
      end
    end
    if (reset) begin
      _T_1315 <= 2'h0;
    end else begin
      if (_T_1274) begin
        _T_1315 <= _T_1312;
      end
    end
    if (reset) begin
      _T_1318 <= 2'h0;
    end else begin
      if (_T_1277) begin
        _T_1318 <= _T_1315;
      end
    end
    if (reset) begin
      _T_1321 <= 2'h0;
    end else begin
      if (_T_1280) begin
        _T_1321 <= _T_1318;
      end
    end
    if (reset) begin
      dat_rsp_exec_sub_h <= 2'h0;
    end else begin
      if (_T_1283) begin
        dat_rsp_exec_sub_h <= _T_1321;
      end
    end
    if (reset) begin
      dat_l0c0_dummy <= 1'h1;
    end else begin
      if (dat_l0c0_en) begin
        dat_l0c0_dummy <= 1'h0;
      end else begin
        if (dat_dummy_l0_en) begin
          dat_l0c0_dummy <= 1'h1;
        end
      end
    end
    if (reset) begin
      dat_l1c0_dummy <= 1'h1;
    end else begin
      if (dat_l1c0_en) begin
        dat_l1c0_dummy <= 1'h0;
      end else begin
        if (dat_dummy_l1_en) begin
          dat_l1c0_dummy <= 1'h1;
        end
      end
    end
    if (reset) begin
      dat_l2c0_dummy <= 1'h1;
    end else begin
      if (dat_l2c0_en) begin
        dat_l2c0_dummy <= 1'h0;
      end else begin
        if (dat_dummy_l2_en) begin
          dat_l2c0_dummy <= 1'h1;
        end
      end
    end
    if (reset) begin
      dat_l3c0_dummy <= 1'h1;
    end else begin
      if (dat_l3c0_en) begin
        dat_l3c0_dummy <= 1'h0;
      end else begin
        if (dat_dummy_l3_en) begin
          dat_l3c0_dummy <= 1'h1;
        end
      end
    end
    if (reset) begin
      dat_l0c1_dummy <= 1'h1;
    end else begin
      if (dat_l0c1_en) begin
        dat_l0c1_dummy <= 1'h0;
      end else begin
        if (dat_l0_set) begin
          dat_l0c1_dummy <= dat_l0c0_dummy;
        end
      end
    end
    if (reset) begin
      dat_l1c1_dummy <= 1'h1;
    end else begin
      if (dat_l1c1_en) begin
        dat_l1c1_dummy <= 1'h0;
      end else begin
        if (_T_1409) begin
          dat_l1c1_dummy <= dat_l1c0_dummy;
        end
      end
    end
    if (reset) begin
      dat_l2c1_dummy <= 1'h1;
    end else begin
      if (dat_l2c1_en) begin
        dat_l2c1_dummy <= 1'h0;
      end else begin
        if (_T_1414) begin
          dat_l2c1_dummy <= dat_l2c0_dummy;
        end
      end
    end
    if (reset) begin
      dat_l3c1_dummy <= 1'h1;
    end else begin
      if (dat_l3c1_en) begin
        dat_l3c1_dummy <= 1'h0;
      end else begin
        if (_T_1419) begin
          dat_l3c1_dummy <= dat_l3c0_dummy;
        end
      end
    end
    if (dat_l0c0_en) begin
      dat_l0c0 <= io_sc2buf_dat_rd_data_bits;
    end
    if (dat_l1c0_en) begin
      dat_l1c0 <= io_sc2buf_dat_rd_data_bits;
    end
    if (dat_l2c0_en) begin
      dat_l2c0 <= io_sc2buf_dat_rd_data_bits;
    end
    if (dat_l3c0_en) begin
      dat_l3c0 <= io_sc2buf_dat_rd_data_bits;
    end
    if (dat_l0c1_en) begin
      dat_l0c1 <= dat_l0c0;
    end
    if (dat_l1c1_en) begin
      dat_l1c1 <= dat_l1c0;
    end
    if (dat_l2c1_en) begin
      dat_l2c1 <= dat_l2c0;
    end
    if (dat_l3c1_en) begin
      dat_l3c1 <= dat_l3c0;
    end
    if (reset) begin
      rsp_sft_cnt_l0 <= 8'h0;
    end else begin
      if (rsp_sft_cnt_l0_en) begin
        if (layer_st) begin
          rsp_sft_cnt_l0 <= 8'h8;
        end else begin
          if (_T_1541) begin
            rsp_sft_cnt_l0 <= rsp_sft_cnt_l0_ori;
          end else begin
            if (_T_1542) begin
              rsp_sft_cnt_l0 <= 8'h8;
            end else begin
              if (dat_dummy_l0_en) begin
                rsp_sft_cnt_l0 <= _T_1545;
              end else begin
                if (_T_1508) begin
                  rsp_sft_cnt_l0 <= 8'h8;
                end else begin
                  rsp_sft_cnt_l0 <= _T_1514;
                end
              end
            end
          end
        end
      end
    end
    if (reset) begin
      rsp_sft_cnt_l1 <= 8'h0;
    end else begin
      if (rsp_sft_cnt_l1_en) begin
        if (layer_st) begin
          rsp_sft_cnt_l1 <= 8'h8;
        end else begin
          if (_T_1551) begin
            rsp_sft_cnt_l1 <= rsp_sft_cnt_l1_ori;
          end else begin
            if (_T_1552) begin
              rsp_sft_cnt_l1 <= 8'h8;
            end else begin
              if (dat_dummy_l1_en) begin
                rsp_sft_cnt_l1 <= _T_1555;
              end else begin
                if (_T_1508) begin
                  rsp_sft_cnt_l1 <= 8'h8;
                end else begin
                  rsp_sft_cnt_l1 <= _T_1522;
                end
              end
            end
          end
        end
      end
    end
    if (reset) begin
      rsp_sft_cnt_l2 <= 8'h0;
    end else begin
      if (rsp_sft_cnt_l2_en) begin
        if (layer_st) begin
          rsp_sft_cnt_l2 <= 8'h8;
        end else begin
          if (_T_1561) begin
            rsp_sft_cnt_l2 <= rsp_sft_cnt_l2_ori;
          end else begin
            if (_T_1562) begin
              rsp_sft_cnt_l2 <= 8'h8;
            end else begin
              if (dat_dummy_l2_en) begin
                rsp_sft_cnt_l2 <= _T_1565;
              end else begin
                if (_T_1508) begin
                  rsp_sft_cnt_l2 <= 8'h8;
                end else begin
                  rsp_sft_cnt_l2 <= _T_1530;
                end
              end
            end
          end
        end
      end
    end
    if (reset) begin
      rsp_sft_cnt_l3 <= 8'h0;
    end else begin
      if (rsp_sft_cnt_l3_en) begin
        if (layer_st) begin
          rsp_sft_cnt_l3 <= 8'h8;
        end else begin
          if (_T_1571) begin
            rsp_sft_cnt_l3 <= rsp_sft_cnt_l3_ori;
          end else begin
            if (_T_1572) begin
              rsp_sft_cnt_l3 <= 8'h8;
            end else begin
              if (dat_dummy_l3_en) begin
                rsp_sft_cnt_l3 <= _T_1575;
              end else begin
                if (_T_1508) begin
                  rsp_sft_cnt_l3 <= 8'h8;
                end else begin
                  rsp_sft_cnt_l3 <= _T_1538;
                end
              end
            end
          end
        end
      end
    end
    if (reset) begin
      rsp_sft_cnt_l0_ori <= 8'h0;
    end else begin
      if (rsp_sft_cnt_l0_ori_en) begin
        if (layer_st) begin
          rsp_sft_cnt_l0_ori <= 8'h8;
        end else begin
          if (!(_T_1541)) begin
            if (_T_1542) begin
              rsp_sft_cnt_l0_ori <= 8'h8;
            end else begin
              if (dat_dummy_l0_en) begin
                rsp_sft_cnt_l0_ori <= _T_1545;
              end else begin
                if (_T_1508) begin
                  rsp_sft_cnt_l0_ori <= 8'h8;
                end else begin
                  rsp_sft_cnt_l0_ori <= _T_1514;
                end
              end
            end
          end
        end
      end
    end
    if (reset) begin
      rsp_sft_cnt_l1_ori <= 8'h0;
    end else begin
      if (rsp_sft_cnt_l1_ori_en) begin
        if (layer_st) begin
          rsp_sft_cnt_l1_ori <= 8'h8;
        end else begin
          if (!(_T_1551)) begin
            if (_T_1552) begin
              rsp_sft_cnt_l1_ori <= 8'h8;
            end else begin
              if (dat_dummy_l1_en) begin
                rsp_sft_cnt_l1_ori <= _T_1555;
              end else begin
                if (_T_1508) begin
                  rsp_sft_cnt_l1_ori <= 8'h8;
                end else begin
                  rsp_sft_cnt_l1_ori <= _T_1522;
                end
              end
            end
          end
        end
      end
    end
    if (reset) begin
      rsp_sft_cnt_l2_ori <= 8'h0;
    end else begin
      if (rsp_sft_cnt_l2_ori_en) begin
        if (layer_st) begin
          rsp_sft_cnt_l2_ori <= 8'h8;
        end else begin
          if (!(_T_1561)) begin
            if (_T_1562) begin
              rsp_sft_cnt_l2_ori <= 8'h8;
            end else begin
              if (dat_dummy_l2_en) begin
                rsp_sft_cnt_l2_ori <= _T_1565;
              end else begin
                if (_T_1508) begin
                  rsp_sft_cnt_l2_ori <= 8'h8;
                end else begin
                  rsp_sft_cnt_l2_ori <= _T_1530;
                end
              end
            end
          end
        end
      end
    end
    if (reset) begin
      rsp_sft_cnt_l3_ori <= 8'h0;
    end else begin
      if (rsp_sft_cnt_l3_ori_en) begin
        if (layer_st) begin
          rsp_sft_cnt_l3_ori <= 8'h8;
        end else begin
          if (!(_T_1571)) begin
            if (_T_1572) begin
              rsp_sft_cnt_l3_ori <= 8'h8;
            end else begin
              if (dat_dummy_l3_en) begin
                rsp_sft_cnt_l3_ori <= _T_1575;
              end else begin
                if (_T_1508) begin
                  rsp_sft_cnt_l3_ori <= 8'h8;
                end else begin
                  rsp_sft_cnt_l3_ori <= _T_1538;
                end
              end
            end
          end
        end
      end
    end
    if (reset) begin
      dat_rsp_l2_pvld <= 1'h0;
    end else begin
      dat_rsp_l2_pvld <= dat_rsp_l1_pvld;
    end
    if (reset) begin
      _T_1458 <= 27'h0;
    end else begin
      if (dat_rsp_l1_pvld) begin
        _T_1458 <= _T_1455;
      end
    end
    dat_rsp_l0_sft_d1 <= _GEN_149[31:0];
    dat_rsp_l0_sft_d2 <= _GEN_150[15:0];
    if (dat_rsp_sft_d3_en) begin
      dat_rsp_l0_sft_d3 <= dat_rsp_l0_sft_d2;
    end
    dat_rsp_l1_sft_d2 <= _GEN_151[15:0];
    if (dat_rsp_sft_d3_en) begin
      dat_rsp_l1_sft_d3 <= dat_rsp_l1_sft_d2;
    end
    dat_rsp_l2_sft_d3 <= _GEN_154[15:0];
    if (reset) begin
      dat_out_pvld <= 1'h0;
    end else begin
      dat_out_pvld <= dat_rsp_pvld;
    end
    if (reset) begin
      dat_out_flag <= 9'h0;
    end else begin
      if (dat_rsp_pvld) begin
        dat_out_flag <= dat_rsp_flag;
      end
    end
    if (reset) begin
      dat_out_bypass_mask_0 <= 1'h0;
    end else begin
      if (dat_rsp_pvld) begin
        dat_out_bypass_mask_0 <= dat_rsp_mask_w_0;
      end
    end
    if (reset) begin
      dat_out_bypass_mask_1 <= 1'h0;
    end else begin
      if (dat_rsp_pvld) begin
        dat_out_bypass_mask_1 <= dat_rsp_mask_w_1;
      end
    end
    if (reset) begin
      dat_out_bypass_mask_2 <= 1'h0;
    end else begin
      if (dat_rsp_pvld) begin
        dat_out_bypass_mask_2 <= dat_rsp_mask_w_2;
      end
    end
    if (reset) begin
      dat_out_bypass_mask_3 <= 1'h0;
    end else begin
      if (dat_rsp_pvld) begin
        dat_out_bypass_mask_3 <= dat_rsp_mask_w_3;
      end
    end
    if (reset) begin
      dat_out_bypass_mask_4 <= 1'h0;
    end else begin
      if (dat_rsp_pvld) begin
        dat_out_bypass_mask_4 <= dat_rsp_mask_w_4;
      end
    end
    if (reset) begin
      dat_out_bypass_mask_5 <= 1'h0;
    end else begin
      if (dat_rsp_pvld) begin
        dat_out_bypass_mask_5 <= dat_rsp_mask_w_5;
      end
    end
    if (reset) begin
      dat_out_bypass_mask_6 <= 1'h0;
    end else begin
      if (dat_rsp_pvld) begin
        dat_out_bypass_mask_6 <= dat_rsp_mask_w_6;
      end
    end
    if (reset) begin
      dat_out_bypass_mask_7 <= 1'h0;
    end else begin
      if (dat_rsp_pvld) begin
        dat_out_bypass_mask_7 <= dat_rsp_mask_w_7;
      end
    end
    if (reset) begin
      _T_1943 <= 8'h0;
    end else begin
      if (_T_1965) begin
        if (_T_1789) begin
          _T_1943 <= dat_rsp_img_0;
        end else begin
          _T_1943 <= dat_rsp_conv_0;
        end
      end
    end
    if (reset) begin
      _T_1946 <= 8'h0;
    end else begin
      if (_T_1966) begin
        if (_T_1789) begin
          _T_1946 <= dat_rsp_img_1;
        end else begin
          _T_1946 <= dat_rsp_conv_1;
        end
      end
    end
    if (reset) begin
      _T_1949 <= 8'h0;
    end else begin
      if (_T_1967) begin
        if (_T_1789) begin
          _T_1949 <= dat_rsp_img_2;
        end else begin
          _T_1949 <= dat_rsp_conv_2;
        end
      end
    end
    if (reset) begin
      _T_1952 <= 8'h0;
    end else begin
      if (_T_1968) begin
        if (_T_1789) begin
          _T_1952 <= dat_rsp_img_3;
        end else begin
          _T_1952 <= dat_rsp_conv_3;
        end
      end
    end
    if (reset) begin
      _T_1955 <= 8'h0;
    end else begin
      if (_T_1969) begin
        if (_T_1789) begin
          _T_1955 <= dat_rsp_img_4;
        end else begin
          _T_1955 <= dat_rsp_conv_4;
        end
      end
    end
    if (reset) begin
      _T_1958 <= 8'h0;
    end else begin
      if (_T_1970) begin
        if (_T_1789) begin
          _T_1958 <= dat_rsp_img_5;
        end else begin
          _T_1958 <= dat_rsp_conv_5;
        end
      end
    end
    if (reset) begin
      _T_1961 <= 8'h0;
    end else begin
      if (_T_1971) begin
        if (_T_1789) begin
          _T_1961 <= dat_rsp_img_6;
        end else begin
          _T_1961 <= dat_rsp_conv_6;
        end
      end
    end
    if (reset) begin
      _T_1964 <= 8'h0;
    end else begin
      if (_T_1972) begin
        if (_T_1789) begin
          _T_1964 <= dat_rsp_img_7;
        end else begin
          _T_1964 <= dat_rsp_conv_7;
        end
      end
    end
    if (reset) begin
      dl_out_pvld <= 1'h0;
    end else begin
      dl_out_pvld <= dat_out_pvld;
    end
    if (reset) begin
      dl_out_mask_0 <= 1'h0;
    end else begin
      if (_T_2113) begin
        if (_T_2071) begin
          dl_out_mask_0 <= 1'h0;
        end else begin
          dl_out_mask_0 <= dat_out_bypass_mask_0;
        end
      end
    end
    if (reset) begin
      dl_out_mask_1 <= 1'h0;
    end else begin
      if (_T_2113) begin
        if (_T_2071) begin
          dl_out_mask_1 <= 1'h0;
        end else begin
          dl_out_mask_1 <= dat_out_bypass_mask_1;
        end
      end
    end
    if (reset) begin
      dl_out_mask_2 <= 1'h0;
    end else begin
      if (_T_2113) begin
        if (_T_2071) begin
          dl_out_mask_2 <= 1'h0;
        end else begin
          dl_out_mask_2 <= dat_out_bypass_mask_2;
        end
      end
    end
    if (reset) begin
      dl_out_mask_3 <= 1'h0;
    end else begin
      if (_T_2113) begin
        if (_T_2071) begin
          dl_out_mask_3 <= 1'h0;
        end else begin
          dl_out_mask_3 <= dat_out_bypass_mask_3;
        end
      end
    end
    if (reset) begin
      dl_out_mask_4 <= 1'h0;
    end else begin
      if (_T_2113) begin
        if (_T_2071) begin
          dl_out_mask_4 <= 1'h0;
        end else begin
          dl_out_mask_4 <= dat_out_bypass_mask_4;
        end
      end
    end
    if (reset) begin
      dl_out_mask_5 <= 1'h0;
    end else begin
      if (_T_2113) begin
        if (_T_2071) begin
          dl_out_mask_5 <= 1'h0;
        end else begin
          dl_out_mask_5 <= dat_out_bypass_mask_5;
        end
      end
    end
    if (reset) begin
      dl_out_mask_6 <= 1'h0;
    end else begin
      if (_T_2113) begin
        if (_T_2071) begin
          dl_out_mask_6 <= 1'h0;
        end else begin
          dl_out_mask_6 <= dat_out_bypass_mask_6;
        end
      end
    end
    if (reset) begin
      dl_out_mask_7 <= 1'h0;
    end else begin
      if (_T_2113) begin
        if (_T_2071) begin
          dl_out_mask_7 <= 1'h0;
        end else begin
          dl_out_mask_7 <= dat_out_bypass_mask_7;
        end
      end
    end
    if (reset) begin
      dl_out_flag <= 9'h0;
    end else begin
      if (dat_out_pvld) begin
        dl_out_flag <= dat_out_flag;
      end
    end
    if (reset) begin
      _T_2049 <= 8'h0;
    end else begin
      if (dat_out_mask_0) begin
        _T_2049 <= _T_1943;
      end
    end
    if (reset) begin
      _T_2052 <= 8'h0;
    end else begin
      if (dat_out_mask_1) begin
        _T_2052 <= _T_1946;
      end
    end
    if (reset) begin
      _T_2055 <= 8'h0;
    end else begin
      if (dat_out_mask_2) begin
        _T_2055 <= _T_1949;
      end
    end
    if (reset) begin
      _T_2058 <= 8'h0;
    end else begin
      if (dat_out_mask_3) begin
        _T_2058 <= _T_1952;
      end
    end
    if (reset) begin
      _T_2061 <= 8'h0;
    end else begin
      if (dat_out_mask_4) begin
        _T_2061 <= _T_1955;
      end
    end
    if (reset) begin
      _T_2064 <= 8'h0;
    end else begin
      if (dat_out_mask_5) begin
        _T_2064 <= _T_1958;
      end
    end
    if (reset) begin
      _T_2067 <= 8'h0;
    end else begin
      if (dat_out_mask_6) begin
        _T_2067 <= _T_1961;
      end
    end
    if (reset) begin
      _T_2070 <= 8'h0;
    end else begin
      if (dat_out_mask_7) begin
        _T_2070 <= _T_1964;
      end
    end
    if (reset) begin
      dl_out_pvld_d1 <= 1'h0;
    end else begin
      dl_out_pvld_d1 <= dl_out_pvld;
    end
    if (reset) begin
      _T_2120 <= 1'h0;
    end else begin
      _T_2120 <= dl_out_pvld;
    end
    if (reset) begin
      _T_2123 <= 1'h0;
    end else begin
      _T_2123 <= dl_out_pvld;
    end
    if (reset) begin
      _T_2127 <= 9'h0;
    end else begin
      if (_T_2125) begin
        if (_T_2116) begin
          _T_2127 <= 9'h0;
        end else begin
          _T_2127 <= dl_out_flag;
        end
      end
    end
    if (reset) begin
      _T_2131 <= 9'h0;
    end else begin
      if (_T_2125) begin
        if (_T_2116) begin
          _T_2131 <= 9'h0;
        end else begin
          _T_2131 <= dl_out_flag;
        end
      end
    end
    if (reset) begin
      _T_2175_0 <= 1'h0;
    end else begin
      if (_T_2125) begin
        _T_2175_0 <= dl_out_mask_0;
      end
    end
    if (reset) begin
      _T_2175_1 <= 1'h0;
    end else begin
      if (_T_2125) begin
        _T_2175_1 <= dl_out_mask_1;
      end
    end
    if (reset) begin
      _T_2175_2 <= 1'h0;
    end else begin
      if (_T_2125) begin
        _T_2175_2 <= dl_out_mask_2;
      end
    end
    if (reset) begin
      _T_2175_3 <= 1'h0;
    end else begin
      if (_T_2125) begin
        _T_2175_3 <= dl_out_mask_3;
      end
    end
    if (reset) begin
      _T_2175_4 <= 1'h0;
    end else begin
      if (_T_2125) begin
        _T_2175_4 <= dl_out_mask_4;
      end
    end
    if (reset) begin
      _T_2175_5 <= 1'h0;
    end else begin
      if (_T_2125) begin
        _T_2175_5 <= dl_out_mask_5;
      end
    end
    if (reset) begin
      _T_2175_6 <= 1'h0;
    end else begin
      if (_T_2125) begin
        _T_2175_6 <= dl_out_mask_6;
      end
    end
    if (reset) begin
      _T_2175_7 <= 1'h0;
    end else begin
      if (_T_2125) begin
        _T_2175_7 <= dl_out_mask_7;
      end
    end
    if (reset) begin
      _T_2247_0 <= 1'h0;
    end else begin
      if (_T_2125) begin
        _T_2247_0 <= dl_out_mask_0;
      end
    end
    if (reset) begin
      _T_2247_1 <= 1'h0;
    end else begin
      if (_T_2125) begin
        _T_2247_1 <= dl_out_mask_1;
      end
    end
    if (reset) begin
      _T_2247_2 <= 1'h0;
    end else begin
      if (_T_2125) begin
        _T_2247_2 <= dl_out_mask_2;
      end
    end
    if (reset) begin
      _T_2247_3 <= 1'h0;
    end else begin
      if (_T_2125) begin
        _T_2247_3 <= dl_out_mask_3;
      end
    end
    if (reset) begin
      _T_2247_4 <= 1'h0;
    end else begin
      if (_T_2125) begin
        _T_2247_4 <= dl_out_mask_4;
      end
    end
    if (reset) begin
      _T_2247_5 <= 1'h0;
    end else begin
      if (_T_2125) begin
        _T_2247_5 <= dl_out_mask_5;
      end
    end
    if (reset) begin
      _T_2247_6 <= 1'h0;
    end else begin
      if (_T_2125) begin
        _T_2247_6 <= dl_out_mask_6;
      end
    end
    if (reset) begin
      _T_2247_7 <= 1'h0;
    end else begin
      if (_T_2125) begin
        _T_2247_7 <= dl_out_mask_7;
      end
    end
    if (reset) begin
      _T_2278 <= 8'h0;
    end else begin
      if (dl_out_mask_0) begin
        _T_2278 <= _T_2049;
      end
    end
    if (reset) begin
      _T_2281 <= 8'h0;
    end else begin
      if (dl_out_mask_0) begin
        _T_2281 <= _T_2049;
      end
    end
    if (reset) begin
      _T_2284 <= 8'h0;
    end else begin
      if (dl_out_mask_1) begin
        _T_2284 <= _T_2052;
      end
    end
    if (reset) begin
      _T_2287 <= 8'h0;
    end else begin
      if (dl_out_mask_1) begin
        _T_2287 <= _T_2052;
      end
    end
    if (reset) begin
      _T_2290 <= 8'h0;
    end else begin
      if (dl_out_mask_2) begin
        _T_2290 <= _T_2055;
      end
    end
    if (reset) begin
      _T_2293 <= 8'h0;
    end else begin
      if (dl_out_mask_2) begin
        _T_2293 <= _T_2055;
      end
    end
    if (reset) begin
      _T_2296 <= 8'h0;
    end else begin
      if (dl_out_mask_3) begin
        _T_2296 <= _T_2058;
      end
    end
    if (reset) begin
      _T_2299 <= 8'h0;
    end else begin
      if (dl_out_mask_3) begin
        _T_2299 <= _T_2058;
      end
    end
    if (reset) begin
      _T_2302 <= 8'h0;
    end else begin
      if (dl_out_mask_4) begin
        _T_2302 <= _T_2061;
      end
    end
    if (reset) begin
      _T_2305 <= 8'h0;
    end else begin
      if (dl_out_mask_4) begin
        _T_2305 <= _T_2061;
      end
    end
    if (reset) begin
      _T_2308 <= 8'h0;
    end else begin
      if (dl_out_mask_5) begin
        _T_2308 <= _T_2064;
      end
    end
    if (reset) begin
      _T_2311 <= 8'h0;
    end else begin
      if (dl_out_mask_5) begin
        _T_2311 <= _T_2064;
      end
    end
    if (reset) begin
      _T_2314 <= 8'h0;
    end else begin
      if (dl_out_mask_6) begin
        _T_2314 <= _T_2067;
      end
    end
    if (reset) begin
      _T_2317 <= 8'h0;
    end else begin
      if (dl_out_mask_6) begin
        _T_2317 <= _T_2067;
      end
    end
    if (reset) begin
      _T_2320 <= 8'h0;
    end else begin
      if (dl_out_mask_7) begin
        _T_2320 <= _T_2070;
      end
    end
    if (reset) begin
      _T_2323 <= 8'h0;
    end else begin
      if (dl_out_mask_7) begin
        _T_2323 <= _T_2070;
      end
    end
  end
  always @(posedge io_nvdla_core_ng_clk) begin
    if (reset) begin
      dat_entry_st <= 15'h0;
    end else begin
      if (_T_592) begin
        if (io_sc2cdma_dat_pending_req) begin
          dat_entry_st <= 15'h0;
        end else begin
          if (is_dat_entry_st_wrap) begin
            dat_entry_st <= dat_entry_st_inc_wrap;
          end else begin
            dat_entry_st <= dat_entry_st_inc;
          end
        end
      end
    end
  end
endmodule
