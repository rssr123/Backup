package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.TaxCdRequest;

public interface ITaxCodeInterface {

    
    List<Object[]> sp_gettaxcode_v2(TaxCdRequest taxCdRequest);

    Integer sp_instaxcode(TaxCdRequest insertRequest);

    Integer sp_updtaxcode(TaxCdRequest updateRequest);

    Integer sp_deltaxcode(TaxCdRequest deleteRequest);

    Integer sp_checktaxcdbyid(TaxCdRequest taxCodeRequest);

}
