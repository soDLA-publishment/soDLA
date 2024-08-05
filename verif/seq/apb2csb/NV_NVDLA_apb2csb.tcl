set_fml_appmode SEQ

# Compile the two designs
analyze -format sverilog -library spec {../../small_gold/nvdla_small.preprocessed.v}
                                        
analyze -format sverilog -library impl {../../small_gate/vmod/nvdla/apb2csb/NV_NVDLA_apb2csb_wrapper.v 
                                        ../../small_gate/vmod/nvdla/apb2csb/NV_NVDLA_apb2csb.v}

elaborate_seq -spectop NV_NVDLA_apb2csb -impltop NV_NVDLA_apb2csb_wrapper

# Map inputs, outputs and blackboxes of the two design
map_by_name

## Create clock and reset signals
create_clock -period 100 spec.pclk
create_reset spec.prstn -sense low

## Run reset simulation
sim_run -stable
sim_save_reset

#use SEQ config to map uninitialized registers
seq_config -extended_mapping -map_uninit -map_x zero 

# Run check command
check_fv 

