#########################################################################################
# pre-process nvdla into a single blackbox file
#########################################################################################
# either "large" or "small"
SODLA_TYPE ?= small
SODLA_NAME = SO_$(SODLA_TYPE)

# name of output pre-processed verilog file
PREPROC_VERILOG = $(PREPROC_VERILOG_DIR)/$(SODLA_NAME).v

.PHONY: default $(PREPROC_VERILOG)
default: $(PREPROC_VERILOG)

#########################################################################################
$(PREPROC_VERILOG):
	sbt "runMain nvdla.SO_$(SODLA_TYPE)Driver"
	mkdir -p ../sodla-wrapper/src/main/resources
	cp sodla.v ../sodla-wrapper/src/main/resources/$(SODLA_NAME).v

clean:
	rm -rf *.v *.json *.fir
