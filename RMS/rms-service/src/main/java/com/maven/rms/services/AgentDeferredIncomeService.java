package com.maven.rms.services;

import java.math.BigInteger;
import java.util.List;

import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IAgentDeferredIncomeService;
import com.maven.rms.models.AgentDeferredIncome;
import com.maven.rms.models.AgentDetailDeferredIncome;
import com.maven.rms.repositories.AgentDeferredIncomeRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AgentDeferredIncomeService implements IAgentDeferredIncomeService{

    private final AgentDeferredIncomeRepository agentDIRepository;

    public AgentDeferredIncomeService(AgentDeferredIncomeRepository agentDIRepository) {
        this.agentDIRepository = agentDIRepository;
    }

    @Override
    public Integer sp_insagentdi(AgentDeferredIncome item){

        Integer f_result = -1;

        if(item.getPayment_item_details().size() > 0)
        {
            for(AgentDetailDeferredIncome items: item.getPayment_item_details()){
                //Step 1 check MFT exists
                List<Object[]> result1 = agentDIRepository.sp_getmft(items.getFee_detail_id());
                if(result1.size() == 0 || result1.isEmpty()){
                    f_result = 1;
                    break;
                }
                else{
                    //Step 2 Insert Agent MTT
                    Integer result2 = agentDIRepository.sp_inspaymentmtt(item);
        
                    if(result2 == 0 || result2 == null){
                        f_result = 2;
                        break;
                    }
                    else{
                        //Step 3 Insert Agent MTT Item
                        Integer result3 = agentDIRepository.sp_inspaymentmttitem(items, result2);
        
                        if(result3 == 0 || result3 == null){
                            f_result = 3;
                            break;
                        }
                        else{
                            //Step 4 Insert RMS DI
                            BigInteger result4 = agentDIRepository.sp_insagentdi(items);
        
                            if(result4 == null || result4.equals(BigInteger.ZERO)){
                                f_result = 4;
                                break;
                            }
                            else{
                                f_result = 0;
                            }
                        }
                    }
                }
            }
            return f_result;
        }
        else
        {
            return -1;
        }
    }    
}
