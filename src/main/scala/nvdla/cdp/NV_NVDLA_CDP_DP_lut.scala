// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_CDP_DP_lut(implicit val conf: nvdlaConfig) extends Module {
//     val io = IO(new Bundle {
//         val nvdla_core_clk = Input(Clock())
//         val nvdla_core_clk_orig = Input(Clock())

//         val dp2lut_X_entry = Input(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(10.W)))
//         val dp2lut_Xinfo = Input(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(18.W)))
//         val dp2lut_Y_entry = Input(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(10.W)))
//         val dp2lut_Yinfo = Input(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(18.W)))
//         val dp2lut_pvld = Input(Bool())
//         val lut2intp_prdy = Input(Bool())
//         val reg2dp_lut_access_type = Input(Bool())
//         val reg2dp_lut_addr = Input(UInt(10.W))
//         val reg2dp_lut_data = Input(UInt(16.W))
//         val reg2dp_lut_data_trigger = Input(Bool())
//         val reg2dp_lut_hybrid_priority = Input(Bool())
//         val reg2dp_lut_oflow_priority = Input(Bool())
//         val reg2dp_lut_table_id = Input(Bool())
//         val reg2dp_lut_uflow_priority = Input(Bool())
//         val dp2lut_prdy = Output(Bool())
//         val dp2reg_lut_data = Output(UInt(16.W))
//         val lut2intp_X_data_0 = Output(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(32.W)))
//         val lut2intp_X_data_0_17b = Output(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(17.W)))
//         val lut2intp_X_data_1 = Output(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(32.W)))
//         val lut2intp_X_info = Output(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(20.W)))
//         val lut2intp_X_sel = Output(UInt(conf.NVDLA_CDP_THROUGHPUT.W))
//         val lut2intp_Y_sel = Output(UInt(conf.NVDLA_CDP_THROUGHPUT.W))
//         val lut2intp_pvld = Output(Bool())
//     })
// ////////////////////////////////////////////////////////////////////////////
// //==============
// // Work Processing
// //==============
//     val lut_wr_en = io.reg2dp_lut_access_type && io.reg2dp_lut_data_trigger
//     val raw_select = (io.reg2dp_lut_table_id === false.B)

// //==========================================
// //LUT write 
// //------------------------------------------
// withClock(io.nvdla_core_clk){
//     val raw_reg = withClock(io.nvdla_core_clk_orig){RegInit(VecInit(Seq.fill(65)(0.U(16.W))))}

    
//     for(i <- 0 until 65){
//         when(lut_wr_en & raw_select){
//             when(io.reg2dp_lut_addr === i.asUInt){
//                 raw_reg(i) := io.reg2dp_lut_data
//             }
//         }
//     }

//     val density_reg = withClock(io.nvdla_core_clk_orig){RegInit(VecInit(Seq.fill(257)(0.U(16.W))))}
//     for(i <- 0 until 257){
//         when(lut_wr_en & (~raw_select)){
//             when(io.reg2dp_lut_addr === i.asUInt){
//                 density_reg(i) := io.reg2dp_lut_data
//             }
//         }
//     }

// //==========================================
// //LUT read
// //------------------------------------------
//     val raw_out = withClock(io.nvdla_core_clk_orig){RegInit(0.U(16.W))}
//     for(i <- 0 until 65){
//         when(io.reg2dp_lut_addr === i.asUInt){
//             raw_out := raw_reg(i)
//         }
//     }

//     val density_out = withClock(io.nvdla_core_clk_orig){RegInit(0.U(16.W))}
//     for(i <- 0 until 257){
//         when(io.reg2dp_lut_addr === i.asUInt){
//             density_out := density_reg(i)
//         }
//     }
//     io.dp2reg_lut_data := Mux(raw_select, raw_out, density_out)

// //==========================================
// //data to DP
// //------------------------------------------
//     val dp2lut_prdy_f = ~io.lut2intp_pvld | io.lut2intp_prdy
//     val load_din = io.dp2lut_pvld & dp2lut_prdy_f
//     io.dp2lut_prdy := dp2lut_prdy_f

// /////////////////////////////////
// //lut look up select control
// /////////////////////////////////
//     val both_hybrid_sel = (io.reg2dp_lut_hybrid_priority === true.B)
//     val both_of_sel = (io.reg2dp_lut_oflow_priority === true.B)
//     val both_uf_sel = (io.reg2dp_lut_uflow_priority === true.B)

//     val lut_X_sel = withClock(io.nvdla_core_clk_orig){Reg(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))}
   
//     for(i <- 0 until conf.NVDLA_CDP_THROUGHPUT){
//         lut_X_sel(i) := MuxLookup(
//             Cat(io.dp2lut_Xinfo(i)(17,16), io.dp2lut_Yinfo(i)(17,16)),
//             false.B,
//             Array(
//                 "b0000".asUInt(4.W) -> ~both_hybrid_sel,
//                 "b0110".asUInt(4.W) -> ~both_hybrid_sel,
//                 "b1001".asUInt(4.W) -> ~both_hybrid_sel, //both hit, or one uflow and the other oflow
//                 "b0001".asUInt(4.W) -> true.B,
//                 "b0010".asUInt(4.W) -> true.B, //X hit, Y uflow/oflow
//                 "b0100".asUInt(4.W) -> false.B,
//                 "b1000".asUInt(4.W) -> false.B, //X uflow/oflow, Y hit 
//                 "b0101".asUInt(4.W) -> ~both_uf_sel, //both uflow 
//                 "b1010".asUInt(4.W) -> ~both_of_sel //both oflow 
//             )
//         )
//     }

//     val lut_Y_sel = withClock(io.nvdla_core_clk_orig){Reg(Vec(conf.NVDLA_CDP_THROUGHPUT, Bool()))}
   
//     for(i <- 0 until conf.NVDLA_CDP_THROUGHPUT){
//         lut_Y_sel(i) := MuxLookup(
//             Cat(io.dp2lut_Xinfo(i)(17,16), io.dp2lut_Yinfo(i)(17,16)),
//             false.B,
//             Array(
//                 "b0000".asUInt(4.W) -> both_hybrid_sel,
//                 "b0110".asUInt(4.W) -> both_hybrid_sel,
//                 "b1001".asUInt(4.W) -> both_hybrid_sel, //both hit, or one uflow and the other oflow
//                 "b0001".asUInt(4.W) -> false.B,
//                 "b0010".asUInt(4.W) -> false.B, //X hit, Y uflow/oflow
//                 "b0100".asUInt(4.W) -> true.B,
//                 "b1000".asUInt(4.W) -> true.B, //X uflow/oflow, Y hit 
//                 "b0101".asUInt(4.W) -> both_uf_sel, //both uflow 
//                 "b1010".asUInt(4.W) -> both_of_sel //both oflow 
//             )
//         )
//     }

//     val lut_X_data_0 = RegInit(VecInit(Seq.fill(conf.NVDLA_CDP_THROUGHPUT)(0.U(16.W))))
//     val lut_X_data_1 = RegInit(VecInit(Seq.fill(conf.NVDLA_CDP_THROUGHPUT)(0.U(16.W))))
//     for(i <- 0 until conf.NVDLA_CDP_THROUGHPUT){
//         when(load_din & lut_X_sel(i)){
//             when(io.dp2lut_Xinfo(i)(16)){
//                 lut_X_data_0(i) := raw_reg(0)
//                 lut_X_data_1(i) := raw_reg(0)
//             }.elsewhen(io.dp2lut_Xinfo(i)(17)){
//                 lut_X_data_0(i) := raw_reg(64)
//                 lut_X_data_1(i) := raw_reg(64)
//             }.otherwise{
//                 lut_X_data_0(i) := MuxLookup(
//                                         io.dp2lut_X_entry(i),
//                                         raw_reg(0),
//                                         Array(
//                 0.U -> raw_reg(0),
//                1.U -> raw_reg(1),
//                2.U -> raw_reg(2),
//                3.U -> raw_reg(3),
//                4.U -> raw_reg(4),
//                5.U -> raw_reg(5),
//                6.U -> raw_reg(6),
//                7.U -> raw_reg(7),
//                8.U -> raw_reg(8),
//                9.U -> raw_reg(9),
//                10.U -> raw_reg(10),
//                11.U -> raw_reg(11),
//                12.U -> raw_reg(12),
//                13.U -> raw_reg(13),
//                14.U -> raw_reg(14),
//                15.U -> raw_reg(15),
//                16.U -> raw_reg(16),
//                17.U -> raw_reg(17),
//                18.U -> raw_reg(18),
//                19.U -> raw_reg(19),
//                20.U -> raw_reg(20),
//                21.U -> raw_reg(21),
//                22.U -> raw_reg(22),
//                23.U -> raw_reg(23),
//                24.U -> raw_reg(24),
//                25.U -> raw_reg(25),
//                26.U -> raw_reg(26),
//                27.U -> raw_reg(27),
//                28.U -> raw_reg(28),
//                29.U -> raw_reg(29),
//                30.U -> raw_reg(30),
//                31.U -> raw_reg(31),
//                32.U -> raw_reg(32),
//                33.U -> raw_reg(33),
//                34.U -> raw_reg(34),
//                35.U -> raw_reg(35),
//                36.U -> raw_reg(36),
//                37.U -> raw_reg(37),
//                38.U -> raw_reg(38),
//                39.U -> raw_reg(39),
//                40.U -> raw_reg(40),
//                41.U -> raw_reg(41),
//                42.U -> raw_reg(42),
//                43.U -> raw_reg(43),
//                44.U -> raw_reg(44),
//                45.U -> raw_reg(45),
//                46.U -> raw_reg(46),
//                47.U -> raw_reg(47),
//                48.U -> raw_reg(48),
//                49.U -> raw_reg(49),
//                50.U -> raw_reg(50),
//                51.U -> raw_reg(51),
//                52.U -> raw_reg(52),
//                53.U -> raw_reg(53),
//                54.U -> raw_reg(54),
//                55.U -> raw_reg(55),
//                56.U -> raw_reg(56),
//                57.U -> raw_reg(57),
//                58.U -> raw_reg(58),
//                59.U -> raw_reg(59),
//                60.U -> raw_reg(60),
//                61.U -> raw_reg(61),
//                62.U -> raw_reg(62),
//                63.U -> raw_reg(63),                                            
//                64.U -> raw_reg(64)                                            
//                                         ))
//                 lut_X_data_1(i) := MuxLookup(
//                                         io.dp2lut_X_entry(i),
//                                         raw_reg(0),
//                                         Array(
//                0.U -> raw_reg(1),
//                1.U -> raw_reg(2),
//                2.U -> raw_reg(3),
//                3.U -> raw_reg(4),
//                4.U -> raw_reg(5),
//                5.U -> raw_reg(6),
//                6.U -> raw_reg(7),
//                7.U -> raw_reg(8),
//                8.U -> raw_reg(9),
//                9.U -> raw_reg(10),
//                10.U -> raw_reg(11),
//                11.U -> raw_reg(12),
//                12.U -> raw_reg(13),
//                13.U -> raw_reg(14),
//                14.U -> raw_reg(15),
//                15.U -> raw_reg(16),
//                16.U -> raw_reg(17),
//                17.U -> raw_reg(18),
//                18.U -> raw_reg(19),
//                19.U -> raw_reg(20),
//                20.U -> raw_reg(21),
//                21.U -> raw_reg(22),
//                22.U -> raw_reg(23),
//                23.U -> raw_reg(24),
//                24.U -> raw_reg(25),
//                25.U -> raw_reg(26),
//                26.U -> raw_reg(27),
//                27.U -> raw_reg(28),
//                28.U -> raw_reg(29),
//                29.U -> raw_reg(30),
//                30.U -> raw_reg(31),
//                31.U -> raw_reg(32),
//                32.U -> raw_reg(33),
//                33.U -> raw_reg(34),
//                34.U -> raw_reg(35),
//                35.U -> raw_reg(36),
//                36.U -> raw_reg(37),
//                37.U -> raw_reg(38),
//                38.U -> raw_reg(39),
//                39.U -> raw_reg(40),
//                40.U -> raw_reg(41),
//                41.U -> raw_reg(42),
//                42.U -> raw_reg(43),
//                43.U -> raw_reg(44),
//                44.U -> raw_reg(45),
//                45.U -> raw_reg(46),
//                46.U -> raw_reg(47),
//                47.U -> raw_reg(48),
//                48.U -> raw_reg(49),
//                49.U -> raw_reg(50),
//                50.U -> raw_reg(51),
//                51.U -> raw_reg(52),
//                52.U -> raw_reg(53),
//                53.U -> raw_reg(54),
//                54.U -> raw_reg(55),
//                55.U -> raw_reg(56),
//                56.U -> raw_reg(57),
//                57.U -> raw_reg(58),
//                58.U -> raw_reg(59),
//                59.U -> raw_reg(60),
//                60.U -> raw_reg(61),
//                61.U -> raw_reg(62),
//                62.U -> raw_reg(63),
//                63.U -> raw_reg(64),
//                64.U -> raw_reg(64)
//                                ))                
//             }
//         }
//     }

//     val lut_Y_data_0 = RegInit(VecInit(Seq.fill(conf.NVDLA_CDP_THROUGHPUT)(0.U(16.W))))
//     val lut_Y_data_1 = RegInit(VecInit(Seq.fill(conf.NVDLA_CDP_THROUGHPUT)(0.U(16.W))))
//     for(i <- 0 until conf.NVDLA_CDP_THROUGHPUT){
//         when(load_din & lut_Y_sel(i)){
//             when(io.dp2lut_Yinfo(i)(16)){
//                 lut_Y_data_0(i) := density_reg(0)
//                 lut_Y_data_1(i) := density_reg(0)
//             }.elsewhen(io.dp2lut_Yinfo(i)(17)){
//                 lut_Y_data_0(i) := density_reg(256)
//                 lut_Y_data_1(i) := density_reg(256)
//             }.otherwise{
//                 lut_Y_data_0(i) := MuxLookup(
//                                         io.dp2lut_Y_entry(i),
//                                         density_reg(0),
//                                         Array(
//                0.U -> density_reg(0),
//                1.U -> density_reg(1),
//                2.U -> density_reg(2),
//                3.U -> density_reg(3),
//                4.U -> density_reg(4),
//                5.U -> density_reg(5),
//                6.U -> density_reg(6),
//                7.U -> density_reg(7),
//                8.U -> density_reg(8),
//                9.U -> density_reg(9),
//                10.U -> density_reg(10),
//                11.U -> density_reg(11),
//                12.U -> density_reg(12),
//                13.U -> density_reg(13),
//                14.U -> density_reg(14),
//                15.U -> density_reg(15),
//                16.U -> density_reg(16),
//                17.U -> density_reg(17),
//                18.U -> density_reg(18),
//                19.U -> density_reg(19),
//                20.U -> density_reg(20),
//                21.U -> density_reg(21),
//                22.U -> density_reg(22),
//                23.U -> density_reg(23),
//                24.U -> density_reg(24),
//                25.U -> density_reg(25),
//                26.U -> density_reg(26),
//                27.U -> density_reg(27),
//                28.U -> density_reg(28),
//                29.U -> density_reg(29),
//                30.U -> density_reg(30),
//                31.U -> density_reg(31),
//                32.U -> density_reg(32),
//                33.U -> density_reg(33),
//                34.U -> density_reg(34),
//                35.U -> density_reg(35),
//                36.U -> density_reg(36),
//                37.U -> density_reg(37),
//                38.U -> density_reg(38),
//                39.U -> density_reg(39),
//                40.U -> density_reg(40),
//                41.U -> density_reg(41),
//                42.U -> density_reg(42),
//                43.U -> density_reg(43),
//                44.U -> density_reg(44),
//                45.U -> density_reg(45),
//                46.U -> density_reg(46),
//                47.U -> density_reg(47),
//                48.U -> density_reg(48),
//                49.U -> density_reg(49),
//                50.U -> density_reg(50),
//                51.U -> density_reg(51),
//                52.U -> density_reg(52),
//                53.U -> density_reg(53),
//                54.U -> density_reg(54),
//                55.U -> density_reg(55),
//                56.U -> density_reg(56),
//                57.U -> density_reg(57),
//                58.U -> density_reg(58),
//                59.U -> density_reg(59),
//                60.U -> density_reg(60),
//                61.U -> density_reg(61),
//                62.U -> density_reg(62),
//                63.U -> density_reg(63),
//                64.U -> density_reg(64),
//                65.U -> density_reg(65),
//                66.U -> density_reg(66),
//                67.U -> density_reg(67),
//                68.U -> density_reg(68),
//                69.U -> density_reg(69),
//                70.U -> density_reg(70),
//                71.U -> density_reg(71),
//                72.U -> density_reg(72),
//                73.U -> density_reg(73),
//                74.U -> density_reg(74),
//                75.U -> density_reg(75),
//                76.U -> density_reg(76),
//                77.U -> density_reg(77),
//                78.U -> density_reg(78),
//                79.U -> density_reg(79),
//                80.U -> density_reg(80),
//                81.U -> density_reg(81),
//                82.U -> density_reg(82),
//                83.U -> density_reg(83),
//                84.U -> density_reg(84),
//                85.U -> density_reg(85),
//                86.U -> density_reg(86),
//                87.U -> density_reg(87),
//                88.U -> density_reg(88),
//                89.U -> density_reg(89),
//                90.U -> density_reg(90),
//                91.U -> density_reg(91),
//                92.U -> density_reg(92),
//                93.U -> density_reg(93),
//                94.U -> density_reg(94),
//                95.U -> density_reg(95),
//                96.U -> density_reg(96),
//                97.U -> density_reg(97),
//                98.U -> density_reg(98),
//                99.U -> density_reg(99),
//                100.U -> density_reg(100),
//                101.U -> density_reg(101),
//                102.U -> density_reg(102),
//                103.U -> density_reg(103),
//                104.U -> density_reg(104),
//                105.U -> density_reg(105),
//                106.U -> density_reg(106),
//                107.U -> density_reg(107),
//                108.U -> density_reg(108),
//                109.U -> density_reg(109),
//                110.U -> density_reg(110),
//                111.U -> density_reg(111),
//                112.U -> density_reg(112),
//                113.U -> density_reg(113),
//                114.U -> density_reg(114),
//                115.U -> density_reg(115),
//                116.U -> density_reg(116),
//                117.U -> density_reg(117),
//                118.U -> density_reg(118),
//                119.U -> density_reg(119),
//                120.U -> density_reg(120),
//                121.U -> density_reg(121),
//                122.U -> density_reg(122),
//                123.U -> density_reg(123),
//                124.U -> density_reg(124),
//                125.U -> density_reg(125),
//                126.U -> density_reg(126),
//                127.U -> density_reg(127),
//                128.U -> density_reg(128),
//                129.U -> density_reg(129),
//                130.U -> density_reg(130),
//                131.U -> density_reg(131),
//                132.U -> density_reg(132),
//                133.U -> density_reg(133),
//                134.U -> density_reg(134),
//                135.U -> density_reg(135),
//                136.U -> density_reg(136),
//                137.U -> density_reg(137),
//                138.U -> density_reg(138),
//                139.U -> density_reg(139),
//                140.U -> density_reg(140),
//                141.U -> density_reg(141),
//                142.U -> density_reg(142),
//                143.U -> density_reg(143),
//                144.U -> density_reg(144),
//                145.U -> density_reg(145),
//                146.U -> density_reg(146),
//                147.U -> density_reg(147),
//                148.U -> density_reg(148),
//                149.U -> density_reg(149),
//                150.U -> density_reg(150),
//                151.U -> density_reg(151),
//                152.U -> density_reg(152),
//                153.U -> density_reg(153),
//                154.U -> density_reg(154),
//                155.U -> density_reg(155),
//                156.U -> density_reg(156),
//                157.U -> density_reg(157),
//                158.U -> density_reg(158),
//                159.U -> density_reg(159),
//                160.U -> density_reg(160),
//                161.U -> density_reg(161),
//                162.U -> density_reg(162),
//                163.U -> density_reg(163),
//                164.U -> density_reg(164),
//                165.U -> density_reg(165),
//                166.U -> density_reg(166),
//                167.U -> density_reg(167),
//                168.U -> density_reg(168),
//                169.U -> density_reg(169),
//                170.U -> density_reg(170),
//                171.U -> density_reg(171),
//                172.U -> density_reg(172),
//                173.U -> density_reg(173),
//                174.U -> density_reg(174),
//                175.U -> density_reg(175),
//                176.U -> density_reg(176),
//                177.U -> density_reg(177),
//                178.U -> density_reg(178),
//                179.U -> density_reg(179),
//                180.U -> density_reg(180),
//                181.U -> density_reg(181),
//                182.U -> density_reg(182),
//                183.U -> density_reg(183),
//                184.U -> density_reg(184),
//                185.U -> density_reg(185),
//                186.U -> density_reg(186),
//                187.U -> density_reg(187),
//                188.U -> density_reg(188),
//                189.U -> density_reg(189),
//                190.U -> density_reg(190),
//                191.U -> density_reg(191),
//                192.U -> density_reg(192),
//                193.U -> density_reg(193),
//                194.U -> density_reg(194),
//                195.U -> density_reg(195),
//                196.U -> density_reg(196),
//                197.U -> density_reg(197),
//                198.U -> density_reg(198),
//                199.U -> density_reg(199),
//                200.U -> density_reg(200),
//                201.U -> density_reg(201),
//                202.U -> density_reg(202),
//                203.U -> density_reg(203),
//                204.U -> density_reg(204),
//                205.U -> density_reg(205),
//                206.U -> density_reg(206),
//                207.U -> density_reg(207),
//                208.U -> density_reg(208),
//                209.U -> density_reg(209),
//                210.U -> density_reg(210),
//                211.U -> density_reg(211),
//                212.U -> density_reg(212),
//                213.U -> density_reg(213),
//                214.U -> density_reg(214),
//                215.U -> density_reg(215),
//                216.U -> density_reg(216),
//                217.U -> density_reg(217),
//                218.U -> density_reg(218),
//                219.U -> density_reg(219),
//                220.U -> density_reg(220),
//                221.U -> density_reg(221),
//                222.U -> density_reg(222),
//                223.U -> density_reg(223),
//                224.U -> density_reg(224),
//                225.U -> density_reg(225),
//                226.U -> density_reg(226),
//                227.U -> density_reg(227),
//                228.U -> density_reg(228),
//                229.U -> density_reg(229),
//                230.U -> density_reg(230),
//                231.U -> density_reg(231),
//                232.U -> density_reg(232),
//                233.U -> density_reg(233),
//                234.U -> density_reg(234),
//                235.U -> density_reg(235),
//                236.U -> density_reg(236),
//                237.U -> density_reg(237),
//                238.U -> density_reg(238),
//                239.U -> density_reg(239),
//                240.U -> density_reg(240),
//                241.U -> density_reg(241),
//                242.U -> density_reg(242),
//                243.U -> density_reg(243),
//                244.U -> density_reg(244),
//                245.U -> density_reg(245),
//                246.U -> density_reg(246),
//                247.U -> density_reg(247),
//                248.U -> density_reg(248),
//                249.U -> density_reg(249),
//                250.U -> density_reg(250),
//                251.U -> density_reg(251),
//                252.U -> density_reg(252),
//                253.U -> density_reg(253),
//                254.U -> density_reg(254),
//                255.U -> density_reg(255),
//                256.U -> density_reg(256)
//                                         ))  
//                 lut_Y_data_1(i) := MuxLookup(
//                                         io.dp2lut_Y_entry(i),
//                                         density_reg(0),
//                                         Array(
//                0.U -> density_reg(1),
//                1.U -> density_reg(2),
//                2.U -> density_reg(3),
//                3.U -> density_reg(4),
//                4.U -> density_reg(5),
//                5.U -> density_reg(6),
//                6.U -> density_reg(7),
//                7.U -> density_reg(8),
//                8.U -> density_reg(9),
//                9.U -> density_reg(10),
//                10.U -> density_reg(11),
//                11.U -> density_reg(12),
//                12.U -> density_reg(13),
//                13.U -> density_reg(14),
//                14.U -> density_reg(15),
//                15.U -> density_reg(16),
//                16.U -> density_reg(17),
//                17.U -> density_reg(18),
//                18.U -> density_reg(19),
//                19.U -> density_reg(20),
//                20.U -> density_reg(21),
//                21.U -> density_reg(22),
//                22.U -> density_reg(23),
//                23.U -> density_reg(24),
//                24.U -> density_reg(25),
//                25.U -> density_reg(26),
//                26.U -> density_reg(27),
//                27.U -> density_reg(28),
//                28.U -> density_reg(29),
//                29.U -> density_reg(30),
//                30.U -> density_reg(31),
//                31.U -> density_reg(32),
//                32.U -> density_reg(33),
//                33.U -> density_reg(34),
//                34.U -> density_reg(35),
//                35.U -> density_reg(36),
//                36.U -> density_reg(37),
//                37.U -> density_reg(38),
//                38.U -> density_reg(39),
//                39.U -> density_reg(40),
//                40.U -> density_reg(41),
//                41.U -> density_reg(42),
//                42.U -> density_reg(43),
//                43.U -> density_reg(44),
//                44.U -> density_reg(45),
//                45.U -> density_reg(46),
//                46.U -> density_reg(47),
//                47.U -> density_reg(48),
//                48.U -> density_reg(49),
//                49.U -> density_reg(50),
//                50.U -> density_reg(51),
//                51.U -> density_reg(52),
//                52.U -> density_reg(53),
//                53.U -> density_reg(54),
//                54.U -> density_reg(55),
//                55.U -> density_reg(56),
//                56.U -> density_reg(57),
//                57.U -> density_reg(58),
//                58.U -> density_reg(59),
//                59.U -> density_reg(60),
//                60.U -> density_reg(61),
//                61.U -> density_reg(62),
//                62.U -> density_reg(63),
//                63.U -> density_reg(64),
//                64.U -> density_reg(65),
//                65.U -> density_reg(66),
//                66.U -> density_reg(67),
//                67.U -> density_reg(68),
//                68.U -> density_reg(69),
//                69.U -> density_reg(70),
//                70.U -> density_reg(71),
//                71.U -> density_reg(72),
//                72.U -> density_reg(73),
//                73.U -> density_reg(74),
//                74.U -> density_reg(75),
//                75.U -> density_reg(76),
//                76.U -> density_reg(77),
//                77.U -> density_reg(78),
//                78.U -> density_reg(79),
//                79.U -> density_reg(80),
//                80.U -> density_reg(81),
//                81.U -> density_reg(82),
//                82.U -> density_reg(83),
//                83.U -> density_reg(84),
//                84.U -> density_reg(85),
//                85.U -> density_reg(86),
//                86.U -> density_reg(87),
//                87.U -> density_reg(88),
//                88.U -> density_reg(89),
//                89.U -> density_reg(90),
//                90.U -> density_reg(91),
//                91.U -> density_reg(92),
//                92.U -> density_reg(93),
//                93.U -> density_reg(94),
//                94.U -> density_reg(95),
//                95.U -> density_reg(96),
//                96.U -> density_reg(97),
//                97.U -> density_reg(98),
//                98.U -> density_reg(99),
//                99.U -> density_reg(100),
//                100.U -> density_reg(101),
//                101.U -> density_reg(102),
//                102.U -> density_reg(103),
//                103.U -> density_reg(104),
//                104.U -> density_reg(105),
//                105.U -> density_reg(106),
//                106.U -> density_reg(107),
//                107.U -> density_reg(108),
//                108.U -> density_reg(109),
//                109.U -> density_reg(110),
//                110.U -> density_reg(111),
//                111.U -> density_reg(112),
//                112.U -> density_reg(113),
//                113.U -> density_reg(114),
//                114.U -> density_reg(115),
//                115.U -> density_reg(116),
//                116.U -> density_reg(117),
//                117.U -> density_reg(118),
//                118.U -> density_reg(119),
//                119.U -> density_reg(120),
//                120.U -> density_reg(121),
//                121.U -> density_reg(122),
//                122.U -> density_reg(123),
//                123.U -> density_reg(124),
//                124.U -> density_reg(125),
//                125.U -> density_reg(126),
//                126.U -> density_reg(127),
//                127.U -> density_reg(128),
//                128.U -> density_reg(129),
//                129.U -> density_reg(130),
//                130.U -> density_reg(131),
//                131.U -> density_reg(132),
//                132.U -> density_reg(133),
//                133.U -> density_reg(134),
//                134.U -> density_reg(135),
//                135.U -> density_reg(136),
//                136.U -> density_reg(137),
//                137.U -> density_reg(138),
//                138.U -> density_reg(139),
//                139.U -> density_reg(140),
//                140.U -> density_reg(141),
//                141.U -> density_reg(142),
//                142.U -> density_reg(143),
//                143.U -> density_reg(144),
//                144.U -> density_reg(145),
//                145.U -> density_reg(146),
//                146.U -> density_reg(147),
//                147.U -> density_reg(148),
//                148.U -> density_reg(149),
//                149.U -> density_reg(150),
//                150.U -> density_reg(151),
//                151.U -> density_reg(152),
//                152.U -> density_reg(153),
//                153.U -> density_reg(154),
//                154.U -> density_reg(155),
//                155.U -> density_reg(156),
//                156.U -> density_reg(157),
//                157.U -> density_reg(158),
//                158.U -> density_reg(159),
//                159.U -> density_reg(160),
//                160.U -> density_reg(161),
//                161.U -> density_reg(162),
//                162.U -> density_reg(163),
//                163.U -> density_reg(164),
//                164.U -> density_reg(165),
//                165.U -> density_reg(166),
//                166.U -> density_reg(167),
//                167.U -> density_reg(168),
//                168.U -> density_reg(169),
//                169.U -> density_reg(170),
//                170.U -> density_reg(171),
//                171.U -> density_reg(172),
//                172.U -> density_reg(173),
//                173.U -> density_reg(174),
//                174.U -> density_reg(175),
//                175.U -> density_reg(176),
//                176.U -> density_reg(177),
//                177.U -> density_reg(178),
//                178.U -> density_reg(179),
//                179.U -> density_reg(180),
//                180.U -> density_reg(181),
//                181.U -> density_reg(182),
//                182.U -> density_reg(183),
//                183.U -> density_reg(184),
//                184.U -> density_reg(185),
//                185.U -> density_reg(186),
//                186.U -> density_reg(187),
//                187.U -> density_reg(188),
//                188.U -> density_reg(189),
//                189.U -> density_reg(190),
//                190.U -> density_reg(191),
//                191.U -> density_reg(192),
//                192.U -> density_reg(193),
//                193.U -> density_reg(194),
//                194.U -> density_reg(195),
//                195.U -> density_reg(196),
//                196.U -> density_reg(197),
//                197.U -> density_reg(198),
//                198.U -> density_reg(199),
//                199.U -> density_reg(200),
//                200.U -> density_reg(201),
//                201.U -> density_reg(202),
//                202.U -> density_reg(203),
//                203.U -> density_reg(204),
//                204.U -> density_reg(205),
//                205.U -> density_reg(206),
//                206.U -> density_reg(207),
//                207.U -> density_reg(208),
//                208.U -> density_reg(209),
//                209.U -> density_reg(210),
//                210.U -> density_reg(211),
//                211.U -> density_reg(212),
//                212.U -> density_reg(213),
//                213.U -> density_reg(214),
//                214.U -> density_reg(215),
//                215.U -> density_reg(216),
//                216.U -> density_reg(217),
//                217.U -> density_reg(218),
//                218.U -> density_reg(219),
//                219.U -> density_reg(220),
//                220.U -> density_reg(221),
//                221.U -> density_reg(222),
//                222.U -> density_reg(223),
//                223.U -> density_reg(224),
//                224.U -> density_reg(225),
//                225.U -> density_reg(226),
//                226.U -> density_reg(227),
//                227.U -> density_reg(228),
//                228.U -> density_reg(229),
//                229.U -> density_reg(230),
//                230.U -> density_reg(231),
//                231.U -> density_reg(232),
//                232.U -> density_reg(233),
//                233.U -> density_reg(234),
//                234.U -> density_reg(235),
//                235.U -> density_reg(236),
//                236.U -> density_reg(237),
//                237.U -> density_reg(238),
//                238.U -> density_reg(239),
//                239.U -> density_reg(240),
//                240.U -> density_reg(241),
//                241.U -> density_reg(242),
//                242.U -> density_reg(243),
//                243.U -> density_reg(244),
//                244.U -> density_reg(245),
//                245.U -> density_reg(246),
//                246.U -> density_reg(247),
//                247.U -> density_reg(248),
//                248.U -> density_reg(249),
//                249.U -> density_reg(250),
//                250.U -> density_reg(251),
//                251.U -> density_reg(252),
//                252.U -> density_reg(253),
//                253.U -> density_reg(254),
//                254.U -> density_reg(255),
//                255.U -> density_reg(256),
//                256.U -> density_reg(256)
//                                         ))                
//             }
//         }
//     }

// ////////////////
//     val lut_X_info = RegInit(VecInit(Seq.fill(conf.NVDLA_CDP_THROUGHPUT)(0.U(18.W))))
//     for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT-1)){
//         when(load_din){
//             lut_X_info(i) := io.dp2lut_Xinfo(i)
//         }
//     }
    
//     val lutX_sel = RegInit(0.U(conf.NVDLA_CDP_THROUGHPUT.W))
//         when(load_din){
//             lutX_sel := lut_X_sel.asUInt
//         }


//     val lut_Y_info = RegInit(VecInit(Seq.fill(conf.NVDLA_CDP_THROUGHPUT)(0.U(18.W))))
//     for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT-1)){
//         when(load_din){
//             lut_Y_info(i) := io.dp2lut_Yinfo(i)
//         }
//     }

//     val lutY_sel = RegInit(0.U(conf.NVDLA_CDP_THROUGHPUT.W))
//         when(load_din){
//             lutY_sel := lut_Y_sel.asUInt
//         }

// ////////////////
//     val lutX_data_0 = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(16.W)))
//     val lutX_data_1 = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(16.W)))
//     val lutX_info = Wire(Vec(conf.NVDLA_CDP_THROUGHPUT, UInt(16.W)))

//     for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT-1)){
//         lutX_data_0(i) := Mux(lutX_sel(i), lut_X_data_0(i), Mux(lutY_sel(i), lut_Y_data_0(i), 0.U))
//         lutX_data_1(i) := Mux(lutX_sel(i), lut_X_data_1(i), Mux(lutY_sel(i), lut_Y_data_1(i), 0.U))
//         lutX_info(i) := Mux(lutX_sel(i), lut_X_info(i), Mux(lutY_sel(i), lut_Y_info(i), 0.U))
//     }

//     val lut2intp_pvld_out = RegInit(false.B)
//     when(io.dp2lut_pvld){
//         lut2intp_pvld_out := true.B
//     }.elsewhen(io.lut2intp_prdy){
//         lut2intp_pvld_out := false.B
//     }
//     io.lut2intp_pvld := lut2intp_pvld_out

// ///////////////////////////////////////////////////////////////
// //output data
// ///////////////////////////////////////////////////////////////

//     for(i <- 0 to (conf.NVDLA_CDP_THROUGHPUT-1)){
//         io.lut2intp_X_data_0(i) := Cat(Fill(16, lutX_data_0(i)(15)), lutX_data_0(i))
//         io.lut2intp_X_data_1(i) := Cat(Fill(16, lutX_data_1(i)(15)), lutX_data_1(i))
//         io.lut2intp_X_data_0_17b(i) := Cat(lutX_data_0(i)(15), lutX_data_0(i))
//         io.lut2intp_X_info(i) := Cat(lut_Y_info(i)(17,16), lut_X_info(i)(17,16), lutX_info(i))
//         io.lut2intp_X_sel := lutX_sel
//         io.lut2intp_Y_sel := lutY_sel
//     }

// }}


// object NV_NVDLA_CDP_DP_lutDriver extends App {
//     implicit val conf: nvdlaConfig = new nvdlaConfig
//     chisel3.Driver.execute(args, () => new NV_NVDLA_CDP_DP_lut())
// }
