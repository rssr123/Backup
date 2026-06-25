package com.maven.rms.interfaces;

import java.math.BigInteger;
import java.util.List;

import com.maven.rms.models.AgentDeferredIncome;
import com.maven.rms.models.AgentDetailDeferredIncome;

public interface IAgentDeferredIncomeInterface {
    
    Integer sp_inspaymentmtt(AgentDeferredIncome item);

    Integer sp_inspaymentmttitem(AgentDetailDeferredIncome item, Integer mtt_id);

    List<Object[]> sp_getmft(String fee_detail_id);

    BigInteger sp_insagentdi(AgentDetailDeferredIncome item);
    

}
