set_fml_appmode SEQ

# Compile the two designs
analyze -format sverilog -library spec {../../small_gold/nvdla_small.preprocessed.v}
                                        
analyze -format sverilog -library impl {../../small_gate/vmod/nvdla/nocif/NV_NVDLA_MCIF_csb_wrapper.v 
                                        ../../small_gate/vmod/nvdla/nocif/NV_NVDLA_MCIF_csb.v}

elaborate_seq -spectop NV_NVDLA_MCIF_csb -impltop NV_NVDLA_MCIF_csb_wrapper

# Map inputs, outputs and blackboxes of the two design
map_by_name

## Create clock and reset signals
create_clock -period 100 spec.nvdla_core_clk
create_reset spec.nvdla_core_rstn -sense low

## Run reset simulation
sim_run -stable
sim_save_reset

#use SEQ config to map uninitialized registers
seq_config -extended_mapping -map_uninit -map_x zero 

# Run check command
check_fv 

