package com.maven.rms.scheduler.jobs;

import java.time.LocalDateTime;
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
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.DeferredIncomeService;

@DisallowConcurrentExecution
@Component
@Slf4j
public class DeferredIncome implements Job {
//private static final Logger logger = LoggerFactory.getLogger(UpdateRICPWriteOff.class);

	@Autowired
	private SchedulerLogService schLogSvc;
	@Autowired
	private DeferredIncomeService diSvc;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		int succUpdates = 0;
		int failUpdates = 0;
		
		try{
			SchedulerLog newLog = new SchedulerLog("Process DI to Amortization.",
					"This job is called from thread: " + Thread.currentThread().getName(),
					succUpdates);
			newLog = schLogSvc.saveNewScheduleLog(newLog);	

			if(newLog == null || newLog.equals(null))
				throw new IllegalArgumentException("The scheduler log failed to update!");
				
			succUpdates = diSvc.sp_upddi();
			
			succUpdates -= failUpdates;
			newLog.setSuccessTxn(succUpdates);
			newLog.setFailTxn(failUpdates);
			newLog.setDtModified(LocalDateTime.now());
			
			log.debug("Succ: " + Integer.toString(succUpdates) + " || Fail: " + Integer.toString(failUpdates));
			schLogSvc.saveNewScheduleLog(newLog);
		}catch (Exception e) {
			log.error("Exception in " + this.getClass().toString(), e);
		}
	}
}
