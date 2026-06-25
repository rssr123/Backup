package com.maven.rms.scheduler.jobs;

import java.time.LocalDateTime;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maven.rms.models.SchedulerLog;
import com.maven.rms.models.SchedulerUpdRequest;
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.BankReconSchService;
import com.maven.rms.services.SchService;

@DisallowConcurrentExecution
@Component
@Slf4j
public class BankReconDetail implements Job {
	private static final Logger logger = LoggerFactory.getLogger(BankReconDetail.class);

	@Autowired
	private SchedulerLogService schLogSvc;
	@Autowired
	private BankReconSchService bankReconDocSvc;

	@Autowired
	private SchService schService;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		SchedulerUpdRequest schedulerUpdRequest = new SchedulerUpdRequest();
		schedulerUpdRequest.setI_function_nm("BankReconDetail");
		if (schService.sp_getjobstatus(schedulerUpdRequest).equals("E")) {
			log.debug("BankReconDetail job has error. Exiting...");
			return;
		}

		int succUpdates = 0;
		int failUpdates = 0;

		try {
			SchedulerLog newLog = new SchedulerLog("Process Bank Reconcilation.",
					"This job is called from thread: " + Thread.currentThread().getName(),
					succUpdates);
			newLog = schLogSvc.saveNewScheduleLog(newLog);

			if (newLog == null || newLog.equals(null))
				throw new IllegalArgumentException("The scheduler log failed to update!");

			succUpdates = bankReconDocSvc.sp_updrcbanktxn();

			succUpdates -= failUpdates;
			newLog.setSuccessTxn(succUpdates);
			newLog.setFailTxn(failUpdates);
			newLog.setDtModified(LocalDateTime.now());

			log.debug("Succ: " + Integer.toString(succUpdates) + " || Fail: " + Integer.toString(failUpdates));
			schLogSvc.saveNewScheduleLog(newLog);

			SchedulerUpdRequest schedulerUpdRequestSucc = new SchedulerUpdRequest();
			schedulerUpdRequestSucc.setI_function_nm("BankReconDetail");
			schedulerUpdRequestSucc.setI_sch_status("C");
			schService.sp_updjobstatus(schedulerUpdRequestSucc);

		} catch (Exception e) {

			log.error("Exception in " + this.getClass().toString(), e);
			SchedulerUpdRequest schedulerUpdRequestErr = new SchedulerUpdRequest();
			schedulerUpdRequestErr.setI_function_nm("BankReconDetail");
			schedulerUpdRequestErr.setI_sch_status("E");
			schService.sp_updjobstatus(schedulerUpdRequestErr);

			schService.sp_upderrorjobs(schedulerUpdRequestErr);
			throw new JobExecutionException(e, false); // Set refireImmediately to false
		}
	}
}
