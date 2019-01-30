# Verilated -*- Makefile -*-
# DESCRIPTION: Verilator output: Makefile for building Verilated archive or executable
#
# Execute this makefile from the object directory:
#    make -f VNV_NVDLA_CMAC_core.mk

default: VNV_NVDLA_CMAC_core

### Constants...
# Perl executable (from $PERL)
PERL = perl
# Path to Verilator kit (from $VERILATOR_ROOT)
VERILATOR_ROOT = /usr/local/share/verilator
# SystemC include directory with systemc.h (from $SYSTEMC_INCLUDE)
SYSTEMC_INCLUDE ?= 
# SystemC library directory with libsystemc.a (from $SYSTEMC_LIBDIR)
SYSTEMC_LIBDIR ?= 

### Switches...
# SystemC output mode?  0/1 (from --sc)
VM_SC = 0
# Legacy or SystemC output mode?  0/1 (from --sc)
VM_SP_OR_SC = $(VM_SC)
# Deprecated
VM_PCLI = 1
# Deprecated: SystemC architecture to find link library path (from $SYSTEMC_ARCH)
VM_SC_TARGET_ARCH = linux

### Vars...
# Design prefix (from --prefix)
VM_PREFIX = VNV_NVDLA_CMAC_core
# Module prefix (from --prefix)
VM_MODPREFIX = VNV_NVDLA_CMAC_core
# User CFLAGS (from -CFLAGS on Verilator command line)
VM_USER_CFLAGS = \
	-Wno-undefined-bool-conversion -O1 -DTOP_TYPE=VNV_NVDLA_CMAC_core -DVL_USER_FINISH -include VNV_NVDLA_CMAC_core.h \

# User LDLIBS (from -LDFLAGS on Verilator command line)
VM_USER_LDLIBS = \

# User .cpp files (from .cpp's on Verilator command line)
VM_USER_CLASSES = \
	NV_NVDLA_CMAC_core-harness \

# User .cpp directories (from .cpp's on Verilator command line)
VM_USER_DIR = \
	/home/yuda/soDLA/test_run_dir/cmac/NV_NVDLA_CMAC_core \


### Default rules...
# Include list of all generated classes
include VNV_NVDLA_CMAC_core_classes.mk
# Include global rules
include $(VERILATOR_ROOT)/include/verilated.mk

### Executable rules... (from --exe)
VPATH += $(VM_USER_DIR)

NV_NVDLA_CMAC_core-harness.o: /home/yuda/soDLA/test_run_dir/cmac/NV_NVDLA_CMAC_core/NV_NVDLA_CMAC_core-harness.cpp
	$(CXX) $(CXXFLAGS) $(CPPFLAGS) $(OPT_FAST) -c -o $@ $<

### Link rules... (from --exe)
VNV_NVDLA_CMAC_core: $(VK_USER_OBJS) $(VK_GLOBAL_OBJS) $(VM_PREFIX)__ALL.a
	$(LINK) $(LDFLAGS) $^ $(LOADLIBES) $(LDLIBS) -o $@ $(LIBS) $(SC_LIBS)


# Verilated -*- Makefile -*-
