package com.maven.rms.scheduler.jobs;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maven.rms.models.SchedulerLog;
import com.maven.rms.models.SchedulerUpdRequest;
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.SchService;

// 20250819 - Geo - 90% not using anymore.
@DisallowConcurrentExecution
@Component
@Slf4j
public class FMSJournalRIPL implements Job {
    //private static final Logger logger = LoggerFactory.getLogger(UpdateRICPWriteOff.class);

    @Autowired
    private SchedulerLogService schLogSvc;
    @Autowired
    private SchService schService;
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        SchedulerUpdRequest schedulerUpdRequest = new SchedulerUpdRequest();
        schedulerUpdRequest.setI_function_nm("FMSJournalRIPL");
        if(schService.sp_getjobstatus(schedulerUpdRequest).equals("E")){
            log.info("FMSJournalRIPL job has error. Exiting...");
            return;
        }
        int succUpdates = 0;
        int failUpdates = 0;

        try {
            SchedulerLog newLog = new SchedulerLog("Process FMSJournalRIPL.",
                    "This job is called from thread: " + Thread.currentThread().getName(),
                    succUpdates);
            newLog = schLogSvc.saveNewScheduleLog(newLog);

            if (newLog == null || newLog.equals(null))
                throw new IllegalArgumentException("The scheduler log failed to update!");
            
            SchedulerUpdRequest schedulerUpdRequestSucc = new SchedulerUpdRequest();
            schedulerUpdRequestSucc.setI_function_nm("FMSJournalRIPL");
            schedulerUpdRequestSucc.setI_sch_status("C");
            schService.sp_updjobstatus(schedulerUpdRequestSucc);


        } catch (Exception e) {
            log.error("Exception in " + this.getClass().toString(), e);

            SchedulerUpdRequest schedulerUpdRequestErr = new SchedulerUpdRequest();
            schedulerUpdRequestErr.setI_function_nm("FMSJournalRIPL");
            schedulerUpdRequestErr.setI_sch_status("E");
            schService.sp_updjobstatus(schedulerUpdRequestErr);
            schService.sp_upderrorjobs(schedulerUpdRequestErr);
            throw new JobExecutionException(e, false);  // Set refireImmediately to false
        }
    }
}
