package com.maven.rms.interfaces;

import com.maven.rms.models.CreditControlCase;

public interface ICreditControlCaseService {
    Integer sp_inscccase(CreditControlCase bodyRequest);
}