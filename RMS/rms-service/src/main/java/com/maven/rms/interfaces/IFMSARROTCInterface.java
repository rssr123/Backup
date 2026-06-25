package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.FMSARR;

public interface IFMSARROTCInterface {

    List<String> sp_getotcfmsarirefno();

    List<Object[]> sp_getotcfmsarr(String i_otc_type);

    Integer sp_updfmsarr(FMSARR fmsarr);

    Integer sp_insotcfmsarr(String i_fms_ref_no);
}
