  module NV_NVDLA_apb2csb_wrapper (
  ,pclk
  ,prstn
  ,csb2nvdla_ready
  ,nvdla2csb_data
  ,nvdla2csb_valid
  ,paddr
  ,penable
  ,psel
  ,pwdata
  ,pwrite
  ,csb2nvdla_addr
  ,csb2nvdla_nposted
  ,csb2nvdla_valid
  ,csb2nvdla_wdat
  ,csb2nvdla_write
  ,prdata
  ,pready
);

input pclk;
input prstn;
//apb interface
input psel;
input penable;
input pwrite;
input [31:0] paddr;
input [31:0] pwdata;
output [31:0] prdata;
output pready;
//csb interface
output csb2nvdla_valid;
input csb2nvdla_ready;
output [15:0] csb2nvdla_addr;
output [31:0] csb2nvdla_wdat;
output csb2nvdla_write;
output csb2nvdla_nposted;
input nvdla2csb_valid;
input [31:0] nvdla2csb_data;

NV_NVDLA_apb2csb (
.io_pclk (pclk)
,.io_prstn (prstn)
,.io_psel (psel)
,.io_penable (penable)
,.io_pwrite (pwrite)
,.io_paddr (paddr)
,.io_pwdata (pwdata)
,.io_prdata (prdata)
,.io_pready (pready)
,.io_csb2nvdla_ready (csb2nvdla_ready)
,.io_csb2nvdla_valid (csb2nvdla_valid)
,.io_csb2nvdla_bits_addr (csb2nvdla_addr)
,.io_csb2nvdla_bits_wdat (csb2nvdla_wdat)
,.io_csb2nvdla_bits_write (csb2nvdla_write)
,.io_csb2nvdla_bits_nposted (csb2nvdla_nposted)
,.io_nvdla2csb_valid (nvdla2csb_valid)
,.io_nvdla2csb_bits_data (nvdla2csb_data) 
);


endmodule
