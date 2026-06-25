package com.maven.rms.scheduler.jobs;

import java.time.LocalDateTime;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Component;

import com.maven.rms.models.IdamanAPITokenReq;
import com.maven.rms.models.SchedulerLog;
import com.maven.rms.models.SchedulerUpdRequest;

import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.IdamanAPITokenService;
import com.maven.rms.services.SchService;

import lombok.extern.slf4j.Slf4j;

@DisallowConcurrentExecution
@Component
@Slf4j
public class IdamanToken implements Job{
    
    @Autowired
	private SchedulerLogService schLogSvc;

    @Autowired
    private SchService schService;

    @Autowired
    private IdamanAPITokenService itService;

    @Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		int succUpdates = 0;
        String token = "";
        int maxRetries = 3;
        int failUpdates = 0;
		
		try{
			SchedulerLog newLog = new SchedulerLog("Process Idaman Get Token.",
					"This job is called from thread: " + Thread.currentThread().getName(),
					succUpdates);
			newLog = schLogSvc.saveNewScheduleLog(newLog);	

			if(newLog == null || newLog.equals(null))
				throw new IllegalArgumentException("The scheduler log failed to update!");
			
            //Get OAuth 2.0 Token
            while (failUpdates < maxRetries) {
                token = itService.getOAuth2Token();

                if (token != null && token.contains("401") || token != null && token.contains("Unauthorized")) {
                    failUpdates++;
                    try {
                        Thread.sleep(1000); // Optional: delay before retry
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    break; // Success
                }
            }

            if (token != null && !token.isEmpty()) {
                try {
                    //Update into Database
                    IdamanAPITokenReq bodyReq = new IdamanAPITokenReq();
                    bodyReq.setToken(token);
                    succUpdates = itService.updidamantoken(bodyReq);
                } catch (IllegalStateException | InvalidDataAccessApiUsageException ex) {
                    if (ex.getMessage().contains("Session/EntityManager is closed")) {
                        //log.warn("Skipped Idaman token update due to closed EntityManager in job.");
                    } else {
                        log.error("Idaman Token Sch Update ", ex);
                        throw ex;
                    }
                }
            }

			newLog.setSuccessTxn(succUpdates);
			newLog.setDtModified(LocalDateTime.now());
			
			//log.debug("Succ: " + Integer.toString(succUpdates) + " || Fail: " + Integer.toString(failUpdates));
			schLogSvc.saveNewScheduleLog(newLog);

			SchedulerUpdRequest schedulerUpdRequestSucc = new SchedulerUpdRequest();
            schedulerUpdRequestSucc.setI_function_nm("IdamanToken");
            schedulerUpdRequestSucc.setI_sch_status("C");
            schService.sp_updjobstatus(schedulerUpdRequestSucc);
			
		}catch (Exception e) {
			
			log.error("Exception in " + this.getClass().toString(), e);
			SchedulerUpdRequest schedulerUpdRequestErr = new SchedulerUpdRequest();
            schedulerUpdRequestErr.setI_function_nm("IdamanToken");
            schedulerUpdRequestErr.setI_sch_status("E");
            schService.sp_updjobstatus(schedulerUpdRequestErr);

            schService.sp_upderrorjobs(schedulerUpdRequestErr);
            // Set refireImmediately to false
			throw new JobExecutionException(e, false);  
		}
	}

}
