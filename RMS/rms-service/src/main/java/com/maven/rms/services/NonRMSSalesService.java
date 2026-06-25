package com.maven.rms.services;

import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.INonRMSSalesService;
import com.maven.rms.models.NonRMSSales;
import com.maven.rms.repositories.NonRMSSalesRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NonRMSSalesService implements INonRMSSalesService{
    
    private final NonRMSSalesRepository nonRMSSalesRepository;

    public NonRMSSalesService(NonRMSSalesRepository nonRMSSalesRepository)
    {
        this.nonRMSSalesRepository = nonRMSSalesRepository;
    }

    @Override
    public Integer sp_insnonrmssales(NonRMSSales bodyRequest){

        Integer result = 1;

        result = nonRMSSalesRepository.sp_insnonrmssales(bodyRequest);

        return result;
    }
}
