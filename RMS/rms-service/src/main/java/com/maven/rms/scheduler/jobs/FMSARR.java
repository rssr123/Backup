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
import com.maven.rms.services.FMSARRService;
import com.maven.rms.services.SchService;
import com.maven.rms.utils.RMSLogger;


@DisallowConcurrentExecution
@Component
@Slf4j
public class FMSARR implements Job {
    //private static final Logger logger = LoggerFactory.getLogger(FMSARR.class);

    @Autowired
    private SchedulerLogService schLogSvc;

    @Autowired
    private FMSARRService fmsArrService;

    @Autowired
    private SchService schService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        SchedulerUpdRequest schedulerUpdRequest = new SchedulerUpdRequest();
        schedulerUpdRequest.setI_function_nm("FMSARR");
        if(schService.sp_getjobstatus(schedulerUpdRequest).equals("E")){
            log.info("FMSARR job has error. Exiting...");
            return;
        }
        
        RMSLogger.schedulerInfo("FMSARRService is Initializing...");
        //set variables
        int result = 0;

        SchedulerLog newLog = new SchedulerLog("FMSARR",
				"This job is called from thread: " + Thread.currentThread().getName(),
				1);

		newLog = schLogSvc.saveNewScheduleLog(newLog);

        if(newLog == null || newLog.equals(null))
			throw new IllegalArgumentException("The scheduler log failed to update!");

        try {
            // result = fmsArrService.fms_arr_sch().size();
            result = CollectionUtils.size(fmsArrService.fms_arr_sch());

            newLog.setSuccessTxn(1);
            newLog.setFailTxn(0);
            log.debug("Succ: " + Integer.toString(1) + " || Fail: " + Integer.toString(0));
            
            SchedulerUpdRequest schedulerUpdRequestSucc = new SchedulerUpdRequest();
            schedulerUpdRequestSucc.setI_function_nm("FMSARR");
            schedulerUpdRequestSucc.setI_sch_status("C");
            schService.sp_updjobstatus(schedulerUpdRequestSucc);
                   
        } catch (Exception e) {
            RMSLogger.schedulerError(e.getMessage().toString());
            newLog.setSuccessTxn(0);
            newLog.setFailTxn(1);
            log.debug("Succ: " + Integer.toString(0) + " || Fail: " + Integer.toString(1));
            
            SchedulerUpdRequest schedulerUpdRequestErr = new SchedulerUpdRequest();
            schedulerUpdRequestErr.setI_function_nm("FMSARR");
            schedulerUpdRequestErr.setI_sch_status("E");
            schService.sp_updjobstatus(schedulerUpdRequestErr);
            schService.sp_upderrorjobs(schedulerUpdRequestErr);
            throw new JobExecutionException(e, false);  // Set refireImmediately to false
        }

        // if(result > 0){
        //     newLog.setSuccessTxn(1);
        //     newLog.setFailTxn(0);
        //     log.debug("Succ: " + Integer.toString(1) + " || Fail: " + Integer.toString(0));
            
        //     SchedulerUpdRequest schedulerUpdRequestSucc = new SchedulerUpdRequest();
        //     schedulerUpdRequestSucc.setI_function_nm("FMSARR");
        //     schedulerUpdRequestSucc.setI_sch_status("C");
        //     schService.sp_updjobstatus(schedulerUpdRequestSucc);

        // }else{
        //     newLog.setSuccessTxn(0);
        //     newLog.setFailTxn(1);
        //     log.debug("Succ: " + Integer.toString(0) + " || Fail: " + Integer.toString(1));
            
        //     SchedulerUpdRequest schedulerUpdRequestErr = new SchedulerUpdRequest();
        //     schedulerUpdRequestErr.setI_function_nm("FMSARR");
        //     schedulerUpdRequestErr.setI_sch_status("E");
        //     schService.sp_updjobstatus(schedulerUpdRequestErr);
        //     schService.sp_upderrorjobs(schedulerUpdRequestErr);
        // }
		newLog.setDtModified(LocalDateTime.now());

		schLogSvc.saveNewScheduleLog(newLog);
     
    }
}
