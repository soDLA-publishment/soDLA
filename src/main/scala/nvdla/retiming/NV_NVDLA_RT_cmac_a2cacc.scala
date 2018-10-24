package nvdla

import chisel3._


class NV_NVDLA_RT_cmac_a2cacc extends Module {
    val io = IO(new Bundle {
        //general clock
        val nvdla_core_clk = Input(Clock())
        val nvdla_core_rstn = Input(Bool())

        //control signal
        val mac2accu_src_pvld = Input(Bool())
        val mac2accu_src_mask = Input(UInt(8.W))
        val mac2accu_src_mode = Input(UInt(8.W))
        val mac2accu_src_pd = Input(UInt(9.W))

        val mac2accu_dst_pvld = Output(Bool())
        val mac2accu_dst_mask = Output(UInt(8.W))
        val mac2accu_dst_mode = Output(UInt(8.W))
        val mac2accu_dst_pd = Output(UInt(9.W))       


        //data signal
        val mac2accu_src_data = Input(Vec(8, UInt(176.W)))
        val mac2accu_dst_data = Input(Vec(8, UInt(176.W)))

        val mac2accu_src_data = Output(Vec(8, UInt(176.W)))
        val mac2accu_dst_data = Output(Vec(8, UInt(176.W)))


    })

    withClockAndReset(io.nvdla_core_clk, !io.nvdla_core_rstn) {
        for (i <- 0 until 7) { 
            when(mac2accu_src_mask(i)){

            }

        }
        


        val mac2accu_data_d = RegNext()
        io.cacc2glb_done_intr_dst_pd := RegNext(cacc2glb_done_intr_pd_d)
    } 
  }