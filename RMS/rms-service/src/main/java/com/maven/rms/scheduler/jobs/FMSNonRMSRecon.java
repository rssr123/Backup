package com.maven.rms.scheduler.jobs;

import java.time.LocalDateTime;

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
import com.maven.rms.services.NonReceiptingService;
import com.maven.rms.services.SchService;
import com.maven.rms.utils.RMSLogger;

@DisallowConcurrentExecution
@Component
@Slf4j
public class FMSNonRMSRecon implements Job {

    @Autowired
    private SchedulerLogService schLogSvc;

    @Autowired
    private NonReceiptingService nonReceiptingService;

    @Autowired
    private SchService schService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        SchedulerUpdRequest schedulerUpdRequest = new SchedulerUpdRequest();
        schedulerUpdRequest.setI_function_nm("FMSNonRMSRecon");
        if (schService.sp_getjobstatus(schedulerUpdRequest).equals("E")) {
            log.info("FMSNonRMSRecon job has error. Exiting...");
            return;
        }

        RMSLogger.schedulerInfo("Non-RMS Recon Post Accounting is Initializing...");

        int result = 0;

        // SchedulerLog newLog = new SchedulerLog("Process Non-RMS Recon Post Accounting",
        //         "This job is called from thread: " + Thread.currentThread().getName(),
        //         1);

        SchedulerLog newLog = new SchedulerLog("FMSNonRMSRecon", "This job is called from thread: " + Thread.currentThread().getName(),1);
        newLog = schLogSvc.saveNewScheduleLog(newLog);

        if (newLog == null || newLog.equals(null))
            throw new IllegalArgumentException("The scheduler log failed to update!");

        try {
            result = nonReceiptingService.fms_non_rms_recon();

            newLog.setSuccessTxn(result);
            newLog.setFailTxn(0);
            log.debug("Succ: " + Integer.toString(result) + " || Fail: " + Integer.toString(0));

            SchedulerUpdRequest schedulerUpdRequestSucc = new SchedulerUpdRequest();
            schedulerUpdRequestSucc.setI_function_nm("FMSNonRMSRecon");
            schedulerUpdRequestSucc.setI_sch_status("C");
            schService.sp_updjobstatus(schedulerUpdRequestSucc);

        } catch (Exception e) {
            RMSLogger.schedulerError(e.getMessage().toString());
            newLog.setSuccessTxn(0);
            newLog.setFailTxn(1);
            log.debug("Succ: " + Integer.toString(0) + " || Fail: " + Integer.toString(1));
            throw new JobExecutionException(e, false); // Set refireImmediately to false
        }

		newLog.setDtModified(LocalDateTime.now());

		schLogSvc.saveNewScheduleLog(newLog);

    }
}

