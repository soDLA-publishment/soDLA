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