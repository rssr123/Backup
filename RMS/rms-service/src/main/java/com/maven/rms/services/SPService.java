package com.maven.rms.services;

import java.math.BigInteger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.maven.rms.interfaces.ISPService;
import com.maven.rms.models.DailySettlementRequest;
import com.maven.rms.repositories.SPRepository;


@Service
public class SPService implements ISPService{

    private final SPRepository spRepository;
  
    public SPService(SPRepository spRepository) {
        this.spRepository = spRepository;
       
    }


    @Override
    public String sp_insdailysettlement(DailySettlementRequest dailyRequest) {

        String result = "";

        result = spRepository.sp_insdailysettlement(dailyRequest);

        return result;
    }

    @Override
    public String sp_checksp(DailySettlementRequest dailyRequest) {

        String result = "";

        result = spRepository.sp_checksp(dailyRequest);

        return result;
    }







}
