package com.maven.rms.interfaces;

import java.util.List;

public interface IFMSARROTCService {
    
    List<Object[]> sp_getotcfmsarr(String i_otc_type);
}
