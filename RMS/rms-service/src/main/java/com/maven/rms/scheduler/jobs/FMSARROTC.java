package com.maven.rms.scheduler.jobs;
import java.time.LocalDateTime;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.maven.rms.models.SchedulerLog;
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.FMSARROTCService;
import com.maven.rms.utils.RMSLogger;
import lombok.extern.slf4j.Slf4j;

@DisallowConcurrentExecution
@Component
@Slf4j
public class FMSARROTC implements Job {
    @Autowired
    private SchedulerLogService schLogSvc;

    @Autowired
    private FMSARROTCService fmsArrOTCService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        
        RMSLogger.schedulerInfo("FMSARROTC Service is Initializing...");

        SchedulerLog newLog = new SchedulerLog("Process FMSARROTC.",
				"This job is called from thread: " + Thread.currentThread().getName(),1);

		newLog = schLogSvc.saveNewScheduleLog(newLog);

        if(newLog == null || newLog.equals(null))
        {
			throw new IllegalArgumentException("The scheduler log failed to update!");
        }

        try {
            // result = fmsArrService.fms_arr_sch().size();
            CollectionUtils.size(fmsArrOTCService.fms_arr_otc_sch());

            newLog.setSuccessTxn(1);
            newLog.setFailTxn(0);
                   
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
