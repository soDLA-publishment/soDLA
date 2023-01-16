
EXECUTABLE := ape_single_conv3_sdp4_revise.riscv
INCLUDES := ./include
SRC_DIR := ./src  ./    


TOOLCHAIN_PREFIX:=riscv64-unknown-elf-
GCC:=$(TOOLCHAIN_PREFIX)gcc
OBJDUMP=riscv64-unknown-elf-objdump
CFLAGS := -std=gnu99 -O2 -fno-common -fno-builtin-printf -Wall
CPPFLAGS := $(CFLAGS)
LDFLAGS = -static 
LDFLAGS += -Wl,--section-start=.config_addr=0x80051000
LDFLAGS += -Wl,--section-start=.config_addr1=0x80057300
CPPFLAGS += $(addprefix -I,$(INCLUDES))

include libgloss.mk
RM-F := rm -rf
SRCS += $(wildcard $(addsuffix  /*.c, $(SRC_DIR)))		



lite.dump : $(EXECUTABLE)
	$(OBJDUMP)  -D $(EXECUTABLE) > lite.dump

$(EXECUTABLE): $(SRCS) $(libgloss)
	$(GCC) $(LDFLAGS)  $(CPPFLAGS)  $(SRCS) -o $(EXECUTABLE)
.Phony : clean
clean :
	$(RM-F) $(EXECUTABLE) lite.dump

