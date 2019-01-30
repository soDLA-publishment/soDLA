// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Symbol table internal header
//
// Internal details; most calling programs do not need this header

#ifndef _VNV_NVDLA_CMAC_core__Syms_H_
#define _VNV_NVDLA_CMAC_core__Syms_H_

#include "verilated.h"

// INCLUDE MODULE CLASSES
#include "VNV_NVDLA_CMAC_core.h"

// SYMS CLASS
class VNV_NVDLA_CMAC_core__Syms : public VerilatedSyms {
  public:
    
    // LOCAL STATE
    const char* __Vm_namep;
    bool __Vm_activity;  ///< Used by trace routines to determine change occurred
    bool __Vm_didInit;
    
    // SUBCELL STATE
    VNV_NVDLA_CMAC_core*           TOPp;
    
    // CREATORS
    VNV_NVDLA_CMAC_core__Syms(VNV_NVDLA_CMAC_core* topp, const char* namep);
    ~VNV_NVDLA_CMAC_core__Syms() {}
    
    // METHODS
    inline const char* name() { return __Vm_namep; }
    inline bool getClearActivity() { bool r=__Vm_activity; __Vm_activity=false; return r; }
    
} VL_ATTR_ALIGNED(64);

#endif // guard
