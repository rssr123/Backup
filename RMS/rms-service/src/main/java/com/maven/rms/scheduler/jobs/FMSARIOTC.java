package com.maven.rms.scheduler.jobs;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maven.rms.models.FMSARIModel;
import com.maven.rms.models.SchedulerLog;
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.FMSARIService;
import com.maven.rms.services.OTCBankInSlipService;
import com.maven.rms.utils.RMSLogger;
import lombok.extern.slf4j.Slf4j;

@DisallowConcurrentExecution
@Component
@Slf4j
public class FMSARIOTC implements Job {
    @Autowired
    private SchedulerLogService schLogSvc;

    @Autowired
    private FMSARIService fmsARIService;

    @Autowired
    private OTCBankInSlipService OTCARISchService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        
        RMSLogger.schedulerInfo("FMSARIOTC Service is Initializing...");

        SchedulerLog newLog = new SchedulerLog("Process FMSARIOTC.",
				"This job is called from thread: " + Thread.currentThread().getName(),1);

		newLog = schLogSvc.saveNewScheduleLog(newLog);

        if(newLog == null || newLog.equals(null))
        {
			throw new IllegalArgumentException("The scheduler log failed to update!");
        }

        try {
            List<FMSARIModel> fmsARIM = OTCARISchService.sp_getotcfmsari("m", null);
            if (CollectionUtils.size(fmsARIM) > 0) {
                log.info("FMS OTC ARI M: " + CollectionUtils.size(fmsARIM) + " records found.");
                fmsARIService.generateStringBody(fmsARIM);
            }

            List<FMSARIModel> fmsARIE = OTCARISchService.sp_getotcfmsari("e", null);
            if (CollectionUtils.size(fmsARIE) > 0) {
                fmsARIService.generateStringBody(fmsARIE);
            }

            newLog.setSuccessTxn(1);
            newLog.setFailTxn(0);
            log.debug("Succ: " + Integer.toString(1) + " || Fail: " + Integer.toString(0));
                   
        } catch (Exception e) {
            RMSLogger.schedulerError(e.getMessage().toString());
            newLog.setSuccessTxn(0);
            newLog.setFailTxn(1);
            
            throw new JobExecutionException(e, false);
        }

		newLog.setDtModified(LocalDateTime.now());

		schLogSvc.saveNewScheduleLog(newLog);
    }
}
