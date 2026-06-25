package com.maven.rms.services;

import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.ICreditControlCaseService;
import com.maven.rms.models.CreditControlCase;
import com.maven.rms.repositories.CreditControlCaseRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CreditControlCaseService implements ICreditControlCaseService{
    private final CreditControlCaseRepository ccCaseRepository;

    public CreditControlCaseService(CreditControlCaseRepository ccCaseRepository)
    {
        this.ccCaseRepository = ccCaseRepository;
    }

    @Override
    public Integer sp_inscccase(CreditControlCase bodyRequest){
        Integer result = 0;
        Integer itemResult = 0;

        result = ccCaseRepository.sp_inscccase(bodyRequest);

        if (result > 0 && bodyRequest.getItemInformation() != null && !bodyRequest.getItemInformation().isEmpty()) {
            for (CreditControlCase.ItemInformation item : bodyRequest.getItemInformation()) {
                itemResult = ccCaseRepository.sp_inscccaseItem(item, result);
            }
        }
        else{
            itemResult = -2;
        }

        return itemResult;
    }
}
