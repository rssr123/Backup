package com.maven.rms.scheduler.jobs;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.maven.rms.models.SchedulerLog;
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.BillingService;
//import com.maven.rms.services.BillingIssuanceBySSService;

import lombok.extern.slf4j.Slf4j;

@DisallowConcurrentExecution
@Component
@Slf4j
public class BillingIssuance implements Job {

	@Autowired
	private SchedulerLogService schLogSvc;
	@Autowired
	private BillingService bSvc;
	// @Autowired
	// private BillingIssuanceBySSService bibssSvc;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		List<String> billingNoForARI = bSvc.sp_getapprovedbilltoissue();
		// int succSent = toSend.size();
		int succCreatedBilling = CollectionUtils.size(billingNoForARI);
		int failCreateBilling = 0;
		SchedulerLog newLog = new SchedulerLog("Billing issuance loading...",
				"This job is called from thread: " + Thread.currentThread().getName(),
				succCreatedBilling);

		newLog = schLogSvc.saveNewScheduleLog(newLog);

		if (newLog == null || newLog.equals(null))
			throw new IllegalArgumentException("The scheduler log failed to update!");

		for (String billingNum : billingNoForARI) {
			Integer statusCode = bSvc.confirmBill(billingNum, "system", bSvc.sp_getbilmethod(billingNum) == 1 ? true : false);
			if (statusCode < 1) {
				failCreateBilling += 1;
				log.error("BillingIssuance Scheduler job error: " + billingNum
						+ " failed to issue billing due to statusCode: " + Integer.toString(statusCode));
			}
		}

		succCreatedBilling -= failCreateBilling;
		newLog.setSuccessTxn(succCreatedBilling);
		newLog.setFailTxn(failCreateBilling);
		newLog.setDtModified(LocalDateTime.now());
		log.debug("Succ: " + Integer.toString(succCreatedBilling)

				+ " || Fail: " + Integer.toString(failCreateBilling));
		schLogSvc.saveNewScheduleLog(newLog);
	}

}
