// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._


// class NV_NVDLA_MCIF_READ_IG_arb extends Module {
//     val io = IO(new Bundle {
//         //general clock
//         val nvdla_core_clk = Input(Clock())      

//         // bpt2arb
//         val bpt2arb_req_valid = Input(Vec(conf.RDMA_NUM, Bool()))
//         val bpt2arb_req_ready = Output(Vec(conf.RDMA_NUM, Bool()))
//         val bpt2arb_req_pd = Input(Vec(conf.RDMA_NUM, UInt(conf.NVDLA_DMA_RD_IG_PW.W)))

//         // arb2spt
//         val arb2spt_req_valid = Output(Bool())
//         val arb2spt_req_ready = Input(Bool())
//         val arb2spt_req_pd = Output(UInt(conf.NVDLA_DMA_RD_IG_PW.W))

//         //
//         val reg2dp_rd_weight = Input(Vec(conf.RDMA_NUM, UInt(8.W)))
        
//     })
// //                             
// //          ┌─┐       ┌─┐
// //       ┌──┘ ┴───────┘ ┴──┐
// //       │                 │              
// //       │       ───       │              
// //       │  ─┬┘       └┬─  │              
// //       │                 │                   
// //       │       ─┴─       │                  
// //       │                 │                  
// //       └───┐         ┌───┘              
// //           │         │               
// //           │         │                  
// //           │         │
// //           │         └──────────────┐
// //           │                        │
// //           │                        ├─┐
// //           │                        ┌─┘    
// //           │                        │
// //           └─┐  ┐  ┌───────┬──┐  ┌──┘         
// //             │ ─┤ ─┤       │ ─┤ ─┤         
// //             └──┴──┘       └──┴──┘ 

// withClock(io.nvdla_core_clk){


// }}