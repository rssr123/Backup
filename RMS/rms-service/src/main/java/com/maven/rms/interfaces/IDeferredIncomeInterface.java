package com.maven.rms.interfaces;

import java.math.BigInteger;
import java.util.List;
import com.maven.rms.models.DeferredIncome;
import com.maven.rms.models.DeferredIncomeTermination;

public interface IDeferredIncomeInterface {

    BigInteger sp_insdi(DeferredIncome recognitionRequest);

    BigInteger sp_insdi_tmn_log(DeferredIncomeTermination terminationRequest);

    List<Object[]> sp_getdi(DeferredIncome deferredIncome);
}
