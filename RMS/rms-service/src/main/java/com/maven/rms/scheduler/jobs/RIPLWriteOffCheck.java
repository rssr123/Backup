package com.maven.rms.scheduler.jobs;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.hibernate.mapping.Collection;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.maven.rms.models.Email;
import com.maven.rms.models.RIPLResponse;
import com.maven.rms.models.SchedulerLog;
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.EmailService;
import com.maven.rms.services.RIPLService;
import com.maven.rms.utils.RMSLogger;
import com.maven.rms.utils.ServerInetUtils;

@DisallowConcurrentExecution
@Component
@Slf4j
public class RIPLWriteOffCheck implements Job{

    //private static final Logger logger = LoggerFactory.getLogger(InvokeSendEmail.class);

	@Autowired
	private SchedulerLogService schLogSvc;
    @Autowired
    private RIPLService riplService;

    @Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

        RMSLogger.schedulerInfo("RIPLService is Initializing...");
        //set variables
        BigInteger result=null;

        SchedulerLog newLog = new SchedulerLog("RIPL - Checking WriteOff Status...",
				"This job is called from thread: " + Thread.currentThread().getName(),
				1);

		newLog = schLogSvc.saveNewScheduleLog(newLog);

        if(newLog == null || newLog.equals(null))
			throw new IllegalArgumentException("The scheduler log failed to update!");

        try {
            result = riplService.sp_updRIPLWriteOffStatus();
                   
        } catch (Exception e) {
            RMSLogger.schedulerError(e.getMessage().toString());
            // TODO: handle exception
        }

        if(result!=null){
            newLog.setSuccessTxn(1);
            newLog.setFailTxn(0);
            log.debug("Succ: " + Integer.toString(1) + " || Fail: " + Integer.toString(0));
        }else{
            newLog.setSuccessTxn(0);
            newLog.setFailTxn(1);
            log.debug("Succ: " + Integer.toString(0) + " || Fail: " + Integer.toString(1));
        }
		newLog.setDtModified(LocalDateTime.now());

		schLogSvc.saveNewScheduleLog(newLog);
        
	}
}