package com.maven.rms.scheduler.jobs;

import java.time.LocalDateTime;

import org.apache.commons.collections.CollectionUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
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
import com.maven.rms.services.FMSARVService;
import com.maven.rms.services.SchService;
import com.maven.rms.utils.RMSLogger;


@DisallowConcurrentExecution
@Component
@Slf4j
public class FMSARV implements Job {
    //private static final Logger logger = LoggerFactory.getLogger(FMSARV.class);

    @Autowired
    private SchedulerLogService schLogSvc;

    @Autowired
    private FMSARVService fmsArvService;

    @Autowired
    private SchService schService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        
        RMSLogger.schedulerInfo("FMSARVService is Initializing...");
        //set variables
        int result = 0;

        SchedulerLog newLog = new SchedulerLog("FMSARV",
				"This job is called from thread: " + Thread.currentThread().getName(),
				1);

		newLog = schLogSvc.saveNewScheduleLog(newLog);

        if(newLog == null || newLog.equals(null))
			throw new IllegalArgumentException("The scheduler log failed to update!");

        try {
            // result = fmsArvService.fms_arv_sch().size();
            result = CollectionUtils.size(fmsArvService.fms_arv_sch());

            newLog.setSuccessTxn(1);
            newLog.setFailTxn(0);
            log.debug("Succ: " + Integer.toString(1) + " || Fail: " + Integer.toString(0));
                   
        } catch (Exception e) {
            RMSLogger.schedulerError(e.getMessage().toString());

            newLog.setSuccessTxn(0);
            newLog.setFailTxn(1);
            log.debug("Succ: " + Integer.toString(0) + " || Fail: " + Integer.toString(1));
            
            throw new JobExecutionException(e, false);  // Set refireImmediately to false
        }
        
		newLog.setDtModified(LocalDateTime.now());

		schLogSvc.saveNewScheduleLog(newLog);
     
    }
}
