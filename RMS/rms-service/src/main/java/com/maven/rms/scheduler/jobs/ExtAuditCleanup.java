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
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.CommonService;
import com.maven.rms.services.SchService;
import com.maven.rms.utils.RMSLogger;

@DisallowConcurrentExecution
@Component
@Slf4j
public class ExtAuditCleanup implements Job {

    @Autowired
    private SchedulerLogService schLogSvc;

    @Autowired
    private CommonService commonService;

    @Autowired
    private SchService schService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        RMSLogger.schedulerInfo(
                "Cleaning up External Audit, Audit Logs, Error Logs Records for past 7 days initializing...");

        int result = 0;

        SchedulerLog newLog = new SchedulerLog(
                "Cleaning up External Audit, Audit Logs, Error Logs Records for past 7 days",
                "This job is called from thread: " + Thread.currentThread().getName(),
                1);

        newLog = schLogSvc.saveNewScheduleLog(newLog);

        if (newLog == null || newLog.equals(null))
            throw new IllegalArgumentException("The scheduler log failed to update!");

        try {
            Integer result2 = commonService.sp_cleanextaudit();
            // log.error("ExtAuditCleanup Result: " + result2);

            newLog.setSuccessTxn(1);
            newLog.setFailTxn(0);
            log.debug("Succ: " + Integer.toString(result) + " || Fail: " + Integer.toString(0));

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
