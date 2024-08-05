#########################################################################################
# pre-process nvdla into a single blackbox file
#########################################################################################

nvdla_blocks_dir := $(patsubst %/,%,$(dir $(abspath $(lastword $(MAKEFILE_LIST)))))

# either "large" or "small"
NVDLA_TYPE ?= small
NVDLA_NAME = nvdla_$(NVDLA_TYPE)

# path to use synthesis or fpga rams
# either "fpga/model" or "synth"
NVDLA_RAMS ?= fpga/model

# name of output pre-processed verilog file
PREPROC_VERILOG_DIR = $(nvdla_blocks_dir)
PREPROC_VERILOG = $(PREPROC_VERILOG_DIR)/$(NVDLA_NAME).preprocessed.v

.PHONY: default $(PREPROC_VERILOG)
default: $(PREPROC_VERILOG)

#########################################################################################
# includes and vsrcs
#########################################################################################

lookup_srcs = $(shell find -L $(1)/ -name target -prune -o -iname "*.$(2)" -print 2> /dev/null)

VLOG_DIR = $(nvdla_blocks_dir)

include $(nvdla_blocks_dir)/vsrc.mk

ALL_VSRCS = \
	$(nvdla_blocks_dir)/defines/defs.v \
	$(nvdla_$(NVDLA_TYPE)_vsrcs) \
	$(VLOG_DIR)/$(NVDLA_NAME).v \
	$(nvdla_blocks_dir)/defines/undefs.v

INC_DIRS = $(sort $(dir $(call lookup_srcs,$(VLOG_DIR)/vmod,vh)))

#########################################################################################
# pre-process using custom script to replace the includes (but leave rest unaffected)
#########################################################################################

PREPROC_SCRIPT = $(nvdla_blocks_dir)/insert-includes.py

$(PREPROC_VERILOG): $(ALL_VSRCS)
	mkdir -p $(dir $(PREPROC_VERILOG))
	cat $(ALL_VSRCS) > combined.v
	$(PREPROC_SCRIPT) combined.v $@ $(INC_DIRS)
	rm -rf combined.v

clean:
	rm -rf $(nvdla_blocks_dir)/*.preprocessed.v
