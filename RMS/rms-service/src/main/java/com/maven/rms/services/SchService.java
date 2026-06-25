package com.maven.rms.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.ISchServiceInterface;
import com.maven.rms.models.SchedulerCustReq;
import com.maven.rms.models.SchedulerSeq;
import com.maven.rms.models.SchedulerUpdRequest;
import com.maven.rms.repositories.SchRepository;
import com.maven.rms.utils.DatabaseRetryUtils;

@Service
public class SchService implements ISchServiceInterface {

    private final SchRepository schRepository;

    @Autowired
    private DatabaseRetryUtils databaseRetryUtils;

    public SchService(SchRepository schRepository) {
        this.schRepository = schRepository;
    }

    @Override
    public List<SchedulerSeq> sp_getschseq() {
        List<SchedulerSeq> result = Collections.emptyList();
        List<Object[]> objects = schRepository.sp_getschseq();
        result = convertSchedulerList(objects);
        return result;
    }

    private List<SchedulerSeq> convertSchedulerList(List<Object[]> objects) {
        List<SchedulerSeq> schedulerList = new ArrayList<>();

        for (Object[] obj : objects) {
            SchedulerSeq schedulerObj = new SchedulerSeq();
            schedulerObj.setCount_group((Integer) obj[0]);
            schedulerObj.setChain_group((Integer) obj[1]);
            schedulerObj.setChain_name((String) obj[2]);
            schedulerObj.setJob_list_id((Integer) obj[3]);
            schedulerObj.setJob_name((String) obj[4]);
            schedulerObj.setFunction_nm((String) obj[5]);
            schedulerObj.setSequence((Integer) obj[6]);
            schedulerObj.setFrequency((String) obj[7]);
            schedulerObj.setStatus((String) obj[8]);
            schedulerObj.setTotal((Integer) obj[9]);

            schedulerList.add(schedulerObj);
        }

        return schedulerList;
    }

    @Override
    public List<SchedulerSeq> sp_getschind() {
        List<SchedulerSeq> result = Collections.emptyList();
        List<Object[]> objects = schRepository.sp_getschind();
        result = convertSchedulerIndList(objects);
        return result;
    }

    private List<SchedulerSeq> convertSchedulerIndList(List<Object[]> objects) {
        List<SchedulerSeq> schedulerList = new ArrayList<>();

        for (Object[] obj : objects) {
            SchedulerSeq schedulerObj = new SchedulerSeq();
            schedulerObj.setJob_list_id((Integer) obj[0]);
            schedulerObj.setJob_name((String) obj[1]);
            schedulerObj.setFunction_nm((String) obj[2]);
            schedulerObj.setFrequency((String) obj[3]);
            schedulerObj.setStatus((String) obj[4]);
            schedulerObj.setTotal((Integer) obj[5]);

            schedulerList.add(schedulerObj);
        }

        return schedulerList;
    }

    @Override
    public List<SchedulerSeq> sp_getschcustom(SchedulerCustReq schedulerCustReq) {
        List<SchedulerSeq> result = Collections.emptyList();
        List<Object[]> objects = schRepository.sp_getschcustom(schedulerCustReq);
        result = convertSchedulerCustList(objects);
        return result;
    }

    private List<SchedulerSeq> convertSchedulerCustList(List<Object[]> objects) {
        List<SchedulerSeq> schedulerList = new ArrayList<>();

        for (Object[] obj : objects) {
            SchedulerSeq schedulerObj = new SchedulerSeq();
            schedulerObj.setChain_group((Integer) obj[0]);
            schedulerObj.setJob_list_id((Integer) obj[1]);
            schedulerObj.setJob_name((String) obj[2]);
            schedulerObj.setFunction_nm((String) obj[3]);
            schedulerObj.setSequence((Integer) obj[4]);
            schedulerObj.setFrequency((String) obj[5]);
            schedulerObj.setStatus((String) obj[6]);

            schedulerList.add(schedulerObj);
        }

        return schedulerList;
    }

    @Override
    public Integer sp_updschstatus(SchedulerUpdRequest schUpdRequest) {
        try {
            return databaseRetryUtils.executeWithRetry(
                    () -> schRepository.sp_updschstatus(schUpdRequest),
                    "sp_updschstatus for chain_group: " + schUpdRequest.getI_chain_group());
        } catch (Exception e) {
            throw new RuntimeException("Failed to update scheduler status after retries", e);
        }
    }

    @Override
    public Integer sp_updjobstatus(SchedulerUpdRequest schUpdRequest) {
        try {
            return databaseRetryUtils.executeWithRetry(
                    () -> schRepository.sp_updjobstatus(schUpdRequest),
                    "sp_updjobstatus for job: " + schUpdRequest.getI_function_nm());
        } catch (Exception e) {
            throw new RuntimeException("Failed to update job status after retries", e);
        }
    }

    @Override
    public Integer sp_upderrorjobs(SchedulerUpdRequest schUpdRequest) {
        try {
            return databaseRetryUtils.executeWithRetry(
                    () -> schRepository.sp_upderrorjobs(schUpdRequest),
                    "sp_upderrorjobs for job: " + schUpdRequest.getI_function_nm());
        } catch (Exception e) {
            throw new RuntimeException("Failed to update error jobs after retries", e);
        }
    }

    @Override
    public String sp_getjobstatus(SchedulerUpdRequest schUpdRequest) {
        String result = "";
        result = schRepository.sp_getjobstatus(schUpdRequest);
        return result;
    }

    @Override
    public List<SchedulerSeq> sp_getchaingroup(SchedulerCustReq schCustRequest) {
        List<SchedulerSeq> result = Collections.emptyList();
        List<Object[]> objects = schRepository.sp_getchaingroup(schCustRequest);
        result = convertChainList(objects);
        return result;
    }

    private List<SchedulerSeq> convertChainList(List<Object[]> objects) {
        List<SchedulerSeq> schedulerList = new ArrayList<>();

        for (Object[] obj : objects) {
            SchedulerSeq schedulerObj = new SchedulerSeq();
            schedulerObj.setCount_group((Integer) obj[0]);
            schedulerObj.setChain_group((Integer) obj[1]);
            schedulerObj.setChain_name((String) obj[2]);
            schedulerObj.setJob_list_id((Integer) obj[3]);
            schedulerObj.setJob_name((String) obj[4]);
            schedulerObj.setFunction_nm((String) obj[5]);
            schedulerObj.setSequence((Integer) obj[6]);
            schedulerObj.setFrequency((String) obj[7]);
            schedulerObj.setStatus((String) obj[8]);
            schedulerObj.setScheduler_status((String) obj[9]);
            schedulerObj.setTotal((Integer) obj[10]);

            schedulerList.add(schedulerObj);
        }

        return schedulerList;
    }

}
