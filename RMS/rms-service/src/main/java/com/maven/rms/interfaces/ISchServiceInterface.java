package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.SchedulerCustReq;
import com.maven.rms.models.SchedulerSeq;
import com.maven.rms.models.SchedulerUpdRequest;

public interface ISchServiceInterface {

    List<SchedulerSeq> sp_getschseq();
    List<SchedulerSeq> sp_getschind();
    List<SchedulerSeq> sp_getschcustom(SchedulerCustReq schedulerCustReq);

    Integer sp_updschstatus(SchedulerUpdRequest schUpdRequest);
    Integer sp_updjobstatus(SchedulerUpdRequest schUpdRequest);

    Integer sp_upderrorjobs(SchedulerUpdRequest schUpdRequest);
    String sp_getjobstatus(SchedulerUpdRequest schUpdRequest);

    List<SchedulerSeq> sp_getchaingroup(SchedulerCustReq schUpdRequest);
    
}
