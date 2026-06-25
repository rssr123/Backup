package com.maven.rms.interfaces;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import com.maven.rms.models.FMSARIImmediateRequest;
import com.maven.rms.models.FMSARV;

public interface IFMSARVInterface {

    public List<Object[]> sp_getfmsrefno();

    public Integer sp_insfmsarv(FMSARV fmsarv);

    public List<Object[]> sp_getfmsarv();

    public Integer sp_updfmsarv(FMSARV fmsarv);

    List<Object[]> sp_getfmsarvimmediate(FMSARIImmediateRequest fmsari);
}
