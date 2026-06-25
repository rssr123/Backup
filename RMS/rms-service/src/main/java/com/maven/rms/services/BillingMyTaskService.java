package com.maven.rms.services;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IBillingService;
import com.maven.rms.models.BillingMyTaskListing;
import com.maven.rms.models.BillingMyTaskListingRequest;
import com.maven.rms.repositories.BillingMyTaskRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BillingMyTaskService implements IBillingService {
    
    private final BillingMyTaskRepository billingRepository;

    public BillingMyTaskService(BillingMyTaskRepository billingRepository) {
        this.billingRepository = billingRepository;
    }

    @Override
    public List<BillingMyTaskListing> sp_getbillinglisting(BillingMyTaskListingRequest billingRequest) {
        
        List<BillingMyTaskListing> result = Collections.emptyList();

        List<Object[]> objects = billingRepository.sp_getbillinglisting(billingRequest);

        result = convertToGetBillingListing(objects);

        return result;
    }

    private List<BillingMyTaskListing> convertToGetBillingListing(List<Object[]> objects) {
        List<BillingMyTaskListing> billingList = new ArrayList<>();

        for (Object[] obj : objects)  {
            BillingMyTaskListing billing = new BillingMyTaskListing();

            billing.setBil_wf_id((BigInteger) obj[0]);
            billing.setBil_id((BigInteger) obj[1]);
            billing.setRequested_by((String) obj[2]);
            billing.setBilling_desc((String) obj[3]);
            billing.setBil_wf_status((String) obj[4]);
            billing.setAssigned_to((String) obj[5]);
            billing.setDt_requested(((Timestamp) obj[6]).toLocalDateTime());
            billing.setCreated_by((String) obj[7]);
            billing.setTask_id((String) obj[8]);
            billing.setTotal((Integer) obj[9]);

            billingList.add(billing);
        }

        return billingList;
    }

    public Integer sp_getbillingassignedtaskactivetaskcount(BillingMyTaskListingRequest billingMyTaskListingRequest) {

        Integer result = 0;

        result = billingRepository.sp_getbillingassignedtaskactivetaskcount(billingMyTaskListingRequest);

        return result;
    }

    public Integer sp_getbillingcreatedtaskactivetaskcount(BillingMyTaskListingRequest billingMyTaskListingRequest) {
        
        Integer result = 0;

        result = billingRepository.sp_getbillingcreatedtaskactivetaskcount(billingMyTaskListingRequest);

        return result;
    }

}
