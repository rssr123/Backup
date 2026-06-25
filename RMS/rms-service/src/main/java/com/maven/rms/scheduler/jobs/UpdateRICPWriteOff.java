package com.maven.rms.scheduler.jobs;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maven.rms.models.RICP;
import com.maven.rms.models.SchedulerLog;
import com.maven.rms.models.payload.requests.SubmitRICPCanRequest;
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.RICPService;
import com.maven.rms.utils.ServerInetUtils;
import org.apache.commons.collections4.CollectionUtils;

@DisallowConcurrentExecution
@Component
@Slf4j
public class UpdateRICPWriteOff implements Job{
	//private static final Logger logger = LoggerFactory.getLogger(UpdateRICPWriteOff.class);

	@Autowired
	private SchedulerLogService schLogSvc;
	@Autowired
	private RICPService ricpSvc;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		List<RICP> ricps = ricpSvc.ricpToWriteOff();
		//int succUpdates = ricps.size();
		int succUpdates = CollectionUtils.size(ricps);
		int failUpdates = 0;
		
		SchedulerLog newLog = new SchedulerLog("Process RICP Records to Write Off.",
				"This job is called from thread: " + Thread.currentThread().getName(),
				succUpdates);
		newLog = schLogSvc.saveNewScheduleLog(newLog);	

		if(newLog == null || newLog.equals(null))
			throw new IllegalArgumentException("The scheduler log failed to update!");
		
		for (Iterator<RICP> iterator = ricps.iterator(); iterator.hasNext();) {
			RICP ricp = iterator.next();
			int updateSuccess = ricpSvc.updateRICPCollected(new SubmitRICPCanRequest(ricp.getEntity_type(), ricp.getEntity_no(), ricp.getCp_no()
					, "WO", null), "CPWO", "CA", "system");
			if(updateSuccess != 1) {
				log.error("Exception in " + this.getClass().toString() 
						+ " updating the record for RICP ID:" + ricp.getRicp_id()
						+ ", returned with error. Row number from insert is: " + Integer.toString(updateSuccess));
				failUpdates += 1;
				continue;
			}
		}
		
		succUpdates -= failUpdates;
		newLog.setSuccessTxn(succUpdates);
		newLog.setFailTxn(failUpdates);
		newLog.setDtModified(LocalDateTime.now());
		
		log.debug("Succ: " + Integer.toString(succUpdates) + " || Fail: " + Integer.toString(failUpdates));
		schLogSvc.saveNewScheduleLog(newLog);
	}
}
