
EXECUTABLE := dc_1x1x8_1x1x8x1_int8_0.riscv
INCLUDES := ./include
SRC_DIR := ./src  ./     # 除了当前目录外，其他的源代码文件目录


TOOLCHAIN_PREFIX:=riscv64-unknown-elf-
GCC:=$(TOOLCHAIN_PREFIX)gcc
OBJDUMP=riscv64-unknown-elf-objdump
CFLAGS := -std=gnu99 -O2 -fno-common -fno-builtin-printf -Wall
CPPFLAGS := $(CFLAGS)
LDFLAGS = -static 
LDFLAGS += -Wl,--section-start=.config_addr=0x80050100
LDFLAGS += -Wl,--section-start=.config_addr1=0x80050200
CPPFLAGS += $(addprefix -I,$(INCLUDES))

include libgloss.mk
RM-F := rm -rf
SRCS += $(wildcard $(addsuffix  /*.c, $(SRC_DIR)))		#第一次定义变量不能用+=，否则编译不能通过



lite.dump : $(EXECUTABLE)
	$(OBJDUMP)  -D $(EXECUTABLE) > lite.dump

$(EXECUTABLE): $(SRCS) $(libgloss)
	$(GCC) $(LDFLAGS)  $(CPPFLAGS)  $(SRCS) -o $(EXECUTABLE)
.Phony : clean
clean :
	$(RM-F) $(EXECUTABLE) lite.dump

# INCLUDES := .
# SRC_DIR:=	.		# 除了当前目录外，其他的源代码文件目录

# C_SRCS += $(wildcard $(addsuffix /*.c, $(SRC_DIR)))		#第一次定义变量不能用+=，否则编译不能通过
# CFLAGS += $(addprefix -I,$(INCLUDES))

# $(EXECUTABLE):$(C_SRCS)
# 	@echo aaaaaaaaaaaaaaaaaaaaaaaaaa
# 	@echo $(C_SRCS)
# 	@echo $(bbb)
# 	echo $(EXECUTABLE)
# 	$(GCC) $(LDFLAGS)  $(CFLAGS)  $(C_SRCS) -o $(EXECUTABLE)
