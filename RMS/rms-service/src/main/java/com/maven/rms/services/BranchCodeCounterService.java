package com.maven.rms.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IBranchCodeCounterService;
import com.maven.rms.models.BranchCodeCounter;
import com.maven.rms.models.BranchCodeCounterRequest;
import com.maven.rms.repositories.BranchCodeCounterRepository;

@Service
public class BranchCodeCounterService implements IBranchCodeCounterService {

    private final BranchCodeCounterRepository branchCodeCounterRepository;

    public BranchCodeCounterService(BranchCodeCounterRepository branchCodeCounterRepository)
    {
        this.branchCodeCounterRepository = branchCodeCounterRepository;
    }

    @Override
    public List<BranchCodeCounter> sp_getbccmap(BranchCodeCounterRequest getRequest) {
        List<BranchCodeCounter> result = Collections.emptyList();
   
            List<Object[]> objects = branchCodeCounterRepository.sp_getbccmap(getRequest);
            result = convertBranchCodeCounter(objects);

        return result;
    }

    private List<BranchCodeCounter> convertBranchCodeCounter(List<Object[]> objects) {
        List<BranchCodeCounter> branchCodeCounterList = new ArrayList<>();

        for (Object[] obj : objects) {
            BranchCodeCounter branchCodeCounter = new BranchCodeCounter();
            branchCodeCounter.setBcc_id((BigInteger) obj[0]);
            branchCodeCounter.setCounter_id((String) obj[1]);
            branchCodeCounter.setTerminal_id((String) obj[2]);
            branchCodeCounter.setCounter_ip((String) obj[3]);
            branchCodeCounter.setBcm_id((Integer) obj[4]);
            branchCodeCounter.setBcm_code((String) obj[5]);
            branchCodeCounter.setDt_created((Date) obj[6]);
            branchCodeCounter.setDt_modified((Date) obj[7]);
            branchCodeCounter.setCreated_by((String) obj[8]);
            branchCodeCounter.setModified_by((String) obj[9]);
            branchCodeCounter.setStatus((String) obj[10]);
            branchCodeCounter.setStatus_en((String) obj[11]);
            branchCodeCounter.setStatus_bm((String) obj[12]);
            branchCodeCounter.setTotal((Integer) obj[13]);
            branchCodeCounterList.add(branchCodeCounter);
        }

        return branchCodeCounterList;
    }

    @Override
    public Integer sp_insbccmap(BranchCodeCounterRequest insertRequest) {
        Integer result = 0;

            result = branchCodeCounterRepository.sp_insbccmap(insertRequest);

        return result;
    }

    @Override
    public Integer sp_updbccmap(BranchCodeCounterRequest updateRequest) {
        Integer result = 0;

            result = branchCodeCounterRepository.sp_updbccmap(updateRequest);

        return result;
    }

    @Override
    public Integer sp_delbccmap(BranchCodeCounterRequest deleteRequest) {
        Integer result = 0;

            result = branchCodeCounterRepository.sp_delbccmap(deleteRequest);

        return result;
    }
    
}
