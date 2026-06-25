package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.RoleRequest;
import com.maven.rms.models.SchedulerCustReq;
import com.maven.rms.models.SchedulerUpdRequest;

public interface SchRepositoryInterface {

    List<Object[]> sp_getschseq();
    List<Object[]> sp_getschind();
    List<Object[]> sp_getschcustom(SchedulerCustReq schedulerCustReq);

    Integer sp_updschstatus(SchedulerUpdRequest schedulerUpdRequest);
    Integer sp_updjobstatus(SchedulerUpdRequest schedulerUpdRequest);

    Integer sp_upderrorjobs(SchedulerUpdRequest schedulerUpdRequest);
    String sp_getjobstatus(SchedulerUpdRequest schedulerUpdRequest);

    List<Object[]> sp_getchaingroup(SchedulerCustReq schedulerCustRequest);

}
