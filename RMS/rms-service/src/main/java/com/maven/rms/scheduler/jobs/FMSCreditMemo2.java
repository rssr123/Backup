package com.maven.rms.scheduler.jobs;

import java.time.LocalDateTime;

import org.apache.commons.collections.CollectionUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maven.rms.models.SchedulerLog;
import com.maven.rms.models.SchedulerUpdRequest;
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.FMSCRMemoService;
import com.maven.rms.services.SchService;
import com.maven.rms.utils.RMSLogger;


@DisallowConcurrentExecution
@Component
@Slf4j
public class FMSCreditMemo2 implements Job {

    @Autowired
    private SchedulerLogService schLogSvc;

    @Autowired
    private FMSCRMemoService fmsCRMemoService;
    
    @Autowired
    private SchService schService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        SchedulerUpdRequest schedulerUpdRequest = new SchedulerUpdRequest();
        schedulerUpdRequest.setI_function_nm("FMSCreditMemo2");
        if(schService.sp_getjobstatus(schedulerUpdRequest).equals("E")){
            log.info("FMSCreditMemo2 job has error. Exiting...");
            return;
        }
        
        RMSLogger.schedulerInfo("FMSCRMemo2 is Initializing...");
        //set variables
        int result = 0;

        SchedulerLog newLog = new SchedulerLog("FMSCreditMemo2",
				"This job is called from thread: " + Thread.currentThread().getName(),
				1);

		newLog = schLogSvc.saveNewScheduleLog(newLog);

        if(newLog == null || newLog.equals(null))
			throw new IllegalArgumentException("The scheduler log failed to update!");

        try {
            // result = fmsCRMemoService.fms_crmemo_sch().size();
            result = CollectionUtils.size(fmsCRMemoService.fms_crmemo_sch());

            newLog.setSuccessTxn(1);
            newLog.setFailTxn(0);
            log.debug("Succ: " + Integer.toString(1) + " || Fail: " + Integer.toString(0));

            SchedulerUpdRequest schedulerUpdRequestSucc = new SchedulerUpdRequest();
            schedulerUpdRequestSucc.setI_function_nm("FMSCreditMemo2");
            schedulerUpdRequestSucc.setI_sch_status("C");
            schService.sp_updjobstatus(schedulerUpdRequestSucc);
                   
        } catch (Exception e) {
            RMSLogger.schedulerError(e.getMessage().toString());

            newLog.setSuccessTxn(0);
            newLog.setFailTxn(1);
            SchedulerUpdRequest schedulerUpdRequestErr = new SchedulerUpdRequest();
            schedulerUpdRequestErr.setI_function_nm("FMSCreditMemo2");
            schedulerUpdRequestErr.setI_sch_status("E");
            schService.sp_updjobstatus(schedulerUpdRequestErr);
            schService.sp_upderrorjobs(schedulerUpdRequestErr);
            log.debug("Succ: " + Integer.toString(0) + " || Fail: " + Integer.toString(1));
            // TODO: handle exception
            throw new JobExecutionException(e, false);  // Set refireImmediately to false
        }

		newLog.setDtModified(LocalDateTime.now());

		schLogSvc.saveNewScheduleLog(newLog);
    }
}
