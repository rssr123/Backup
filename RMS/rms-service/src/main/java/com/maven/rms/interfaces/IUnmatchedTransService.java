package com.maven.rms.interfaces;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.maven.rms.models.TaxCd;
import com.maven.rms.models.UnmatchTrans;
import com.maven.rms.models.UnmatchTransRequest;

public interface IUnmatchedTransService {

    List<UnmatchTrans> sp_getutlmonths(UnmatchTransRequest unmatchedTransRequest);

    List<UnmatchTrans> sp_getutldays(UnmatchTransRequest unmatchedTransRequest);

    
    
}