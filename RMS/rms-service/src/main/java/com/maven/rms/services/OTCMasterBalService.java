package com.maven.rms.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IOTCMasterBalService;
import com.maven.rms.models.OTCBalancingRequest;
import com.maven.rms.models.OTCMasterBal;
import com.maven.rms.repositories.OTCMasterBalRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OTCMasterBalService implements IOTCMasterBalService{
    private final OTCMasterBalRepository otcMasterBalRepository;

    public OTCMasterBalService(OTCMasterBalRepository otcMasterBalRepository)
    {
        this.otcMasterBalRepository = otcMasterBalRepository;
    }

    @Override
    public List<OTCMasterBal> sp_getotcmasterballist(OTCMasterBal bodyRequest)
    {
        List<OTCMasterBal> getotcmasterballisting = Collections.emptyList();

        List<Object[]> objects = otcMasterBalRepository.sp_getotcmasterballist(bodyRequest);

        getotcmasterballisting = convertMasterBalListing(objects);

        return getotcmasterballisting;
    }

    @Override
    public Integer sp_checkotcmasterbalval(OTCMasterBal bodyRequest)
    {
        return otcMasterBalRepository.sp_checkotcmasterbalval(bodyRequest);
    }

    @Override
    public Integer sp_updotcmasterbalstatus(OTCBalancingRequest bodyRequest)
    {
        return otcMasterBalRepository.sp_updotcmasterbalstatus(bodyRequest);
    }

    private List<OTCMasterBal> convertMasterBalListing(List<Object[]> objects)
    {
        List<OTCMasterBal> OTCMasterBalList = new ArrayList<>();

        for (Object[] obj : objects){
            OTCMasterBal OTCMasterBalListing = new OTCMasterBal();
            OTCMasterBalListing.setBranch_code((String) obj[0]);
            OTCMasterBalListing.setDaily_bal_status((String) obj[2]);
            OTCMasterBalListing.setNo_of_counters((Integer) obj[3]);
            OTCMasterBalListing.setUser_id((String) obj[4]);
            OTCMasterBalListing.setCheck_in((String) obj[5]);
            OTCMasterBalListing.setTotal_amt((BigDecimal) obj[6]);
            OTCMasterBalListing.setTotal((Integer) obj[7]);
            OTCMasterBalList.add(OTCMasterBalListing);
        }

        return OTCMasterBalList;
    }
}
