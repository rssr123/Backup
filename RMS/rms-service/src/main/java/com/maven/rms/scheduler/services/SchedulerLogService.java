package com.maven.rms.scheduler.services;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maven.rms.models.SchedulerLog;
import com.maven.rms.repositories.SchedulerLogRepository;
import com.maven.rms.utils.ServerInetUtils;

@Service
@Slf4j
public class SchedulerLogService {
	//private static final Logger logger = LoggerFactory.getLogger(SchedulerLogService.class);

	@Autowired
	private ServerInetUtils adapter;	
	private final SchedulerLogRepository schLogRepo;
	
	@Autowired
	public SchedulerLogService(SchedulerLogRepository schLogRepo) {
		this.schLogRepo = schLogRepo;
	}
	
	@Transactional
	public SchedulerLog saveNewScheduleLog(SchedulerLog schLog) {
		// try {
			schLog.setServerIp(adapter.getServerIP());
			return schLogRepo.save(schLog);
		// }catch (Exception e) {
		//     log.error("Exception in " + this.getClass().toString(), e);
		//     return null;
		// }
	}

	/*
	@Transactional
	public void updateSchLog(Long schLogId, String msg, Boolean isFail) {
		SchedulerLog currLog = schLogRepo.findSchedulerLogBySchLogId(schLogId).orElse(null);
		
		if(currLog == null) {
			logger.error("Cannot find Scheduler Log object from repo ID-" + Long.toString(schLogId));
			throw new IllegalArgumentException("Cannot find scheduler log record!");	
		}
		
		if(isFail) 
			currLog.setFailTxn(currLog.getFailTxn() + 1);
		else
			currLog.setSuccessTxn(currLog.getSuccessTxn() + 1);
		
		if(!msg.isEmpty())
			currLog.setMessage((currLog.getMessage().equals("") ? "" : currLog.getMessage() + "\n")
								+ msg);
		try {
			schLogRepo.save(currLog);
		}catch (Exception e) {
		    logger.error("Exception in " + this.getClass().toString(), e);
		}
	}
	*/
}
