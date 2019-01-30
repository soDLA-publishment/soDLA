
#include "VNV_NVDLA_CMAC_core.h"
#include "verilated.h"
#include "veri_api.h"
#if VM_TRACE
#include "verilated_vcd_c.h"
#endif
#include <iostream>
class NV_NVDLA_CMAC_core_api_t: public sim_api_t<VerilatorDataWrapper*> {
    public:
    NV_NVDLA_CMAC_core_api_t(VNV_NVDLA_CMAC_core* _dut) {
        dut = _dut;
        main_time = 0L;
        is_exit = false;
#if VM_TRACE
        tfp = NULL;
#endif
    }
    void init_sim_data() {
        sim_data.inputs.clear();
        sim_data.outputs.clear();
        sim_data.signals.clear();

        sim_data.inputs.push_back(new VerilatorCData(&(dut->clock)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->reset)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_sel_0)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_0)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_1)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_2)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_3)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_4)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_5)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_6)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_7)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_8)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_9)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_10)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_11)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_12)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_13)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_14)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_15)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_16)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_17)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_18)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_19)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_20)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_21)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_22)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_23)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_24)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_25)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_26)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_27)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_28)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_29)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_30)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_31)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_32)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_33)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_34)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_35)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_36)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_37)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_38)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_39)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_40)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_41)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_42)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_43)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_44)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_45)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_46)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_47)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_48)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_49)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_50)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_51)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_52)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_53)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_54)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_55)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_56)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_57)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_58)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_59)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_60)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_61)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_62)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_63)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_64)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_65)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_66)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_67)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_68)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_69)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_70)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_71)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_72)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_73)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_74)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_75)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_76)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_77)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_78)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_79)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_80)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_81)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_82)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_83)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_84)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_85)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_86)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_87)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_88)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_89)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_90)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_91)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_92)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_93)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_94)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_95)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_96)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_97)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_98)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_99)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_100)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_101)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_102)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_103)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_104)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_105)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_106)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_107)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_108)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_109)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_110)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_111)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_112)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_113)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_114)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_115)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_116)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_117)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_118)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_119)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_120)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_121)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_122)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_123)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_124)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_125)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_126)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_data_127)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_0)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_1)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_2)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_3)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_4)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_5)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_6)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_7)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_8)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_9)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_10)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_11)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_12)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_13)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_14)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_15)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_16)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_17)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_18)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_19)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_20)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_21)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_22)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_23)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_24)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_25)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_26)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_27)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_28)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_29)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_30)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_31)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_32)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_33)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_34)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_35)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_36)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_37)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_38)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_39)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_40)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_41)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_42)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_43)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_44)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_45)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_46)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_47)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_48)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_49)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_50)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_51)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_52)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_53)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_54)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_55)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_56)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_57)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_58)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_59)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_60)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_61)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_62)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_63)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_64)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_65)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_66)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_67)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_68)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_69)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_70)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_71)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_72)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_73)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_74)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_75)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_76)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_77)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_78)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_79)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_80)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_81)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_82)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_83)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_84)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_85)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_86)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_87)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_88)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_89)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_90)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_91)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_92)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_93)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_94)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_95)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_96)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_97)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_98)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_99)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_100)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_101)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_102)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_103)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_104)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_105)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_106)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_107)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_108)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_109)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_110)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_111)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_112)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_113)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_114)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_115)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_116)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_117)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_118)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_119)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_120)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_121)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_122)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_123)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_124)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_125)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_126)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_mask_127)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_wt_pvld)));
        sim_data.inputs.push_back(new VerilatorSData(&(dut->io_sc2mac_dat_pd)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_0)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_1)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_2)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_3)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_4)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_5)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_6)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_7)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_8)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_9)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_10)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_11)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_12)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_13)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_14)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_15)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_16)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_17)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_18)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_19)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_20)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_21)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_22)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_23)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_24)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_25)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_26)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_27)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_28)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_29)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_30)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_31)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_32)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_33)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_34)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_35)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_36)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_37)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_38)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_39)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_40)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_41)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_42)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_43)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_44)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_45)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_46)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_47)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_48)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_49)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_50)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_51)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_52)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_53)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_54)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_55)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_56)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_57)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_58)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_59)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_60)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_61)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_62)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_63)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_64)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_65)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_66)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_67)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_68)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_69)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_70)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_71)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_72)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_73)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_74)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_75)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_76)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_77)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_78)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_79)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_80)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_81)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_82)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_83)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_84)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_85)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_86)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_87)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_88)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_89)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_90)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_91)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_92)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_93)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_94)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_95)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_96)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_97)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_98)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_99)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_100)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_101)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_102)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_103)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_104)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_105)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_106)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_107)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_108)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_109)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_110)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_111)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_112)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_113)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_114)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_115)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_116)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_117)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_118)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_119)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_120)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_121)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_122)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_123)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_124)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_125)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_126)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_data_127)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_0)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_1)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_2)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_3)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_4)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_5)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_6)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_7)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_8)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_9)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_10)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_11)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_12)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_13)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_14)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_15)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_16)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_17)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_18)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_19)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_20)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_21)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_22)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_23)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_24)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_25)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_26)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_27)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_28)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_29)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_30)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_31)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_32)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_33)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_34)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_35)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_36)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_37)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_38)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_39)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_40)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_41)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_42)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_43)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_44)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_45)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_46)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_47)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_48)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_49)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_50)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_51)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_52)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_53)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_54)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_55)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_56)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_57)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_58)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_59)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_60)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_61)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_62)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_63)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_64)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_65)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_66)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_67)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_68)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_69)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_70)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_71)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_72)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_73)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_74)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_75)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_76)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_77)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_78)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_79)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_80)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_81)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_82)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_83)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_84)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_85)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_86)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_87)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_88)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_89)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_90)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_91)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_92)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_93)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_94)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_95)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_96)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_97)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_98)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_99)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_100)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_101)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_102)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_103)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_104)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_105)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_106)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_107)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_108)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_109)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_110)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_111)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_112)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_113)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_114)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_115)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_116)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_117)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_118)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_119)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_120)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_121)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_122)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_123)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_124)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_125)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_126)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_mask_127)));
        sim_data.inputs.push_back(new VerilatorCData(&(dut->io_sc2mac_dat_pvld)));
        sim_data.outputs.push_back(new VerilatorCData(&(dut->io_dp2reg_done)));
        sim_data.outputs.push_back(new VerilatorSData(&(dut->io_mac2accu_pd)));
        sim_data.outputs.push_back(new VerilatorIData(&(dut->io_mac2accu_data_0)));
        sim_data.outputs.push_back(new VerilatorCData(&(dut->io_mac2accu_mask_0)));
        sim_data.outputs.push_back(new VerilatorCData(&(dut->io_mac2accu_pvld)));
        sim_data.signals.push_back(new VerilatorCData(&(dut->reset)));
        sim_data.signal_map["NV_NVDLA_CMAC_core.reset"] = 0;
    }
#if VM_TRACE
     void init_dump(VerilatedVcdC* _tfp) { tfp = _tfp; }
#endif
    inline bool exit() { return is_exit; }

    // required for sc_time_stamp()
    virtual inline double get_time_stamp() {
        return main_time;
    }

    private:
    VNV_NVDLA_CMAC_core* dut;
    bool is_exit;
    vluint64_t main_time;
#if VM_TRACE
    VerilatedVcdC* tfp;
#endif
    virtual inline size_t put_value(VerilatorDataWrapper* &sig, uint64_t* data, bool force=false) {
        return sig->put_value(data);
    }
    virtual inline size_t get_value(VerilatorDataWrapper* &sig, uint64_t* data) {
        return sig->get_value(data);
    }
    virtual inline size_t get_chunk(VerilatorDataWrapper* &sig) {
        return sig->get_num_words();
    }
    virtual inline void reset() {
        dut->reset = 1;
        step();
    }
    virtual inline void start() {
        dut->reset = 0;
    }
    virtual inline void finish() {
        dut->eval();
        is_exit = true;
    }
    virtual inline void step() {
        dut->clock = 0;
        dut->eval();
#if VM_TRACE
        if (tfp) tfp->dump(main_time);
#endif
        main_time++;
        dut->clock = 1;
        dut->eval();
#if VM_TRACE
        if (tfp) tfp->dump(main_time);
#endif
        main_time++;
    }
    virtual inline void update() {
        dut->_eval_settle(dut->__VlSymsp);
    }
};

// The following isn't strictly required unless we emit (possibly indirectly) something
// requiring a time-stamp (such as an assert).
static NV_NVDLA_CMAC_core_api_t * _Top_api;
double sc_time_stamp () { return _Top_api->get_time_stamp(); }

// Override Verilator definition so first $finish ends simulation
// Note: VL_USER_FINISH needs to be defined when compiling Verilator code
void vl_finish(const char* filename, int linenum, const char* hier) {
  Verilated::flushCall();
  exit(0);
}

int main(int argc, char **argv, char **env) {
    Verilated::commandArgs(argc, argv);
    VNV_NVDLA_CMAC_core* top = new VNV_NVDLA_CMAC_core;
    std::string vcdfile = "test_run_dir/cmac/NV_NVDLA_CMAC_core/NV_NVDLA_CMAC_core.vcd";
    std::vector<std::string> args(argv+1, argv+argc);
    std::vector<std::string>::const_iterator it;
    for (it = args.begin() ; it != args.end() ; it++) {
        if (it->find("+waveform=") == 0) vcdfile = it->c_str()+10;
    }
#if VM_TRACE
    Verilated::traceEverOn(true);
    VL_PRINTF("Enabling waves..");
    VerilatedVcdC* tfp = new VerilatedVcdC;
    top->trace(tfp, 99);
    tfp->open(vcdfile.c_str());
#endif
    NV_NVDLA_CMAC_core_api_t api(top);
    _Top_api = &api; /* required for sc_time_stamp() */
    api.init_sim_data();
    api.init_channels();
#if VM_TRACE
    api.init_dump(tfp);
#endif
    while(!api.exit()) api.tick();
#if VM_TRACE
    if (tfp) tfp->close();
    delete tfp;
#endif
    delete top;
    exit(0);
}
