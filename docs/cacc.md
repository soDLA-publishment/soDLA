# CACC

CACC is to gather the result from k lanes of cmac, do sum calculation between current result with last result(get the stripe sum), send it to the sdp. CACC consists of u_reg, assembly controller, assembly buffer, calculator, delivery controller, delivery buffer. 

Assembly buffer is to gather the partial sum of a stripe. Delivery buffer is to make those data ready for delivery to sdp module. 

## CACC_CALCULATOR

cacc_calculator's function is to gather kernel results(totally k from cmac_a and cmac_b), add it to the previous result and send them to delivery buffer.  

From CACC, cmac_a and cmac_b each has k/2 kernels of atomic operation results. 








