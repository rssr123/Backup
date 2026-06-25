package com.maven.rms.scheduler.jobs;

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
import com.maven.rms.services.FMSDeferredIncomeService;
import com.maven.rms.services.FMSJournalService;
import com.maven.rms.services.FMSRICPService;
import com.maven.rms.services.FMSRIPLService;
import com.maven.rms.services.SchService;

@DisallowConcurrentExecution
@Component
@Slf4j
public class FMSJournal implements Job {
    @Autowired
    private SchedulerLogService schLogSvc;

    @Autowired
    private SchService schService;

    @Autowired
    private FMSDeferredIncomeService fmsDIService;

    @Autowired
    private FMSRICPService fmsRICPService;

    @Autowired
    private FMSRIPLService fmsRIPLService;

    @Autowired
    private FMSJournalService fmsJournalService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        SchedulerUpdRequest schedulerUpdRequest = new SchedulerUpdRequest();
        schedulerUpdRequest.setI_function_nm("FMSJournal");
        if (schService.sp_getjobstatus(schedulerUpdRequest).equals("E")) {
            log.info("FMSJournal job has error. Exiting...");
            return;
        }
        int succUpdates = 0;
        int failUpdates = 0;
        int resultDI = 0;
        int resultRICP = 0;
        int resultRIPL = 0;

        try {
            SchedulerLog newLog = new SchedulerLog("Process FMSJournal.",
                    "This job is called from thread: " + Thread.currentThread().getName(),
                    succUpdates);
            newLog = schLogSvc.saveNewScheduleLog(newLog);

            if (newLog == null || newLog.equals(null))
                throw new IllegalArgumentException("The scheduler log failed to update!");

            // (1) Execute DI
            try {
                resultDI = fmsDIService.sp_fmsDiSch();

            } catch (Exception e) {
                log.error("Exception in FMSJournalDI ", e);

            }

            // (2) Execute RICP
            try {
                resultRICP = fmsRICPService.sp_fmsricpSch();

            } catch (Exception e) {
                log.error("Exception in FMSJournalRICP ", e);

            }

            // (3) Execute RIPL
            try {
                resultRIPL = fmsRIPLService.sp_fmsriplSch();

            } catch (Exception e) {
                log.error("Exception in FMSJournalRIPL ", e);

            }

            // (4) Execute RILT
            try {
                resultRIPL = fmsRIPLService.sp_fmsriltSch();

            } catch (Exception e) {
                log.error("Exception in FMSJournalRILT ", e);

            }

            // (5) Execute Journal
            try {
                failUpdates = fmsJournalService.fmsJnSch();

            } catch (Exception e) {
                log.error("Exception in FMSJournal ", e);

            }
        } catch (Exception e) {
            log.error("Exception in " + this.getClass().toString(), e);
            throw new JobExecutionException(e, false); // Set refireImmediately to false
        }
    }
}
