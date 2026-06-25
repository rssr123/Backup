package com.maven.rms.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IBillingClassService;
import com.maven.rms.models.BillingClass;
import com.maven.rms.models.BillingClassRequest;
import com.maven.rms.repositories.BillingClassRepository;

@Service
public class BillingClassService implements IBillingClassService {

    @Autowired
    private BillingClassRepository billingClassRepository;

    @Override
    public List<BillingClass> getBillingClass(BillingClassRequest billingClassRequest) {
        List<Object[]> objects = billingClassRepository.getBillingClass(billingClassRequest);
        return convertToBillingClassList(objects);
    }

    private List<BillingClass> convertToBillingClassList(List<Object[]> objects) {
        List<BillingClass> billingClassList = new ArrayList<>();

        for (Object[] obj : objects) {
            BillingClass billingClass = new BillingClass();
            billingClass.setBlcm_id((Integer) obj[0]);
            billingClass.setClassId((String) obj[1]);
            billingClass.setClassDesc((String) obj[2]);
            billingClass.setDtCreated((Date) obj[3]);
            billingClass.setDtModified((Date) obj[4]);
            billingClass.setCreatedBy((String) obj[5]);
            billingClass.setModifiedBy((String) obj[6]);
            billingClass.setStatus((String) obj[7]);
            billingClass.setTotal((Integer) obj[8]);
            billingClassList.add(billingClass);
        }

        return billingClassList;
    }

    @Override
    public Integer sp_insblcm(BillingClassRequest billingClassRequest) {
        return billingClassRepository.sp_insblcm(billingClassRequest);
    }

    @Override
    public Integer sp_updateblcm(BillingClassRequest billingClassRequest) {
        return billingClassRepository.sp_updateblcm(billingClassRequest);
    }

    @Override
    public Integer sp_delblcm(BillingClassRequest billingClassRequest) {
        return billingClassRepository.sp_delblcm(billingClassRequest);
    }
}
