package common
{
   
case class cdpConfiguration()
{
    val pINT8_BW = 8
}

case class cdmaConfiguration()
{
    val rscid = 1
    val width = 8
}

case class slibConfiguration()
{
    val BYPASS_POWER_CG = true
}

}

case class ramSizeCongiguration()
{
    phy_rows = 20
    phy_cols = 288
    phy_rcols_pos = 288'b0
}

case class cmacConfiguration()
{
    CMAC_ATOMK_HALF = 8
    CMAC_RESULT_WIDTH = 176
    RT_CMAC_A2CACC_LATENCY = 2
}

case class cacc2glbConfiguration()

{
    RT_CMAC_CACC2GLB_LATENCY = 2
}

case class csb2caccConfiguration()
{
    RT_CMAC_CACC2GLB_LATENCY = 2   
}