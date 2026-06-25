package com.maven.rms.interfaces;

import com.maven.rms.models.CreditControlCase;

public interface ICreditControlCaseInterface {
    Integer sp_inscccase(CreditControlCase bodyRequest);
    
    Integer sp_inscccaseItem(CreditControlCase.ItemInformation item, Integer case_id);
}
