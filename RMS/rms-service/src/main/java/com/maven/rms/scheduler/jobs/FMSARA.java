package com.maven.rms.scheduler.jobs;

import org.apache.commons.collections.CollectionUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maven.rms.models.SchedulerLog;
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.FMSAPIAService;
import com.maven.rms.services.SchService;
import com.maven.rms.utils.RMSLogger;

@DisallowConcurrentExecution
@Component
@Slf4j
public class FMSARA implements Job {

    @Autowired
    private SchedulerLogService schLogSvc;

    @Autowired
    private FMSAPIAService fmsApiaService;

    @Autowired
    private SchService schService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        RMSLogger.schedulerInfo("FMSAPIAService is Initializing...");
        // set variables
        int result = 0;

        SchedulerLog newLog = new SchedulerLog("FMSARA",
                "This job is called from thread: " + Thread.currentThread().getName(),
                1);

        newLog = schLogSvc.saveNewScheduleLog(newLog);

        if (newLog == null || newLog.equals(null))
            throw new IllegalArgumentException("The scheduler log failed to update!");

        try {
            result = CollectionUtils.size(fmsApiaService.fms_apia_sch());

        } catch (Exception e) {
            RMSLogger.schedulerError(e.getMessage().toString());
            throw new JobExecutionException(e, false); // Set refireImmediately to false
        }

        if (result != 0) {
            newLog.setSuccessTxn(1);
            newLog.setFailTxn(0);
            log.debug("Succ: " + Integer.toString(1) + " || Fail: " + Integer.toString(0));

        } else {
            newLog.setSuccessTxn(0);
            newLog.setFailTxn(1);
            log.debug("Succ: " + Integer.toString(0) + " || Fail: " + Integer.toString(1));

        }

        schLogSvc.saveNewScheduleLog(newLog);

    }
}
