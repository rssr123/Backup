package com.maven.rms.interfaces;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import com.maven.rms.models.FMSAPIA;

public interface IFMSAPIAInterface {

    public List<Object[]> sp_getrefunddetails();

    public Integer sp_insfmsapia(FMSAPIA fmsapia);

    public List<Object[]> sp_getfmsapia();

    public Integer sp_updfmsapia(FMSAPIA fmsapia);
}
