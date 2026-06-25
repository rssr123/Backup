package com.maven.rms.scheduler.jobs;

import com.maven.rms.models.SchedulerLog;
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.SOAFileProcessorService;
import com.maven.rms.utils.RMSLogger;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;

@DisallowConcurrentExecution
@Component
@Slf4j
public class SOAFileProcessor implements Job {

    @Autowired
    private SOAFileProcessorService soaFileProcessor;

    @Autowired
    private SchedulerLogService schedulerLogService;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) throws JobExecutionException {
        RMSLogger.schedulerInfo("SOA File Extracting ...");

        // Log scheduler execution
        SchedulerLog schedulerLog = new SchedulerLog(
                "Extracting SOA File",
                "This Job extracts SOA files and send to FMS via FTP Server",
                1);
        schedulerLog = schedulerLogService.saveNewScheduleLog(schedulerLog);

        try{
            soaFileProcessor.processFiles();
        }
        catch(Exception e){
            log.error("Error occurred while processing SOA Files: ", e);
            throw new JobExecutionException(e);
        }
        finally{
            schedulerLogService.saveNewScheduleLog(schedulerLog);
        }
    }
}