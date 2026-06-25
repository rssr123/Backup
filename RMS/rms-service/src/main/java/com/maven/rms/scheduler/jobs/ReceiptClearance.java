package com.maven.rms.scheduler.jobs;

import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.CommonService;
import com.maven.rms.utils.RMSLogger;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@DisallowConcurrentExecution
@Component
@Slf4j
public class ReceiptClearance implements Job {

    @Autowired
    private SchedulerLogService schLogSvc;

    @Autowired
    private CommonService commonService;

    @Value("${jasper.rcpt.directory}")
    private String rcptDirectory;

    private String SchedulerName = "Receipt Clearance Scheduler";
    private String SchedulerStart = "Scheduler initialization started.";
    private String SchedulerCompleted = "Scheduler is Completed";


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        int deletedCount = 0;
    
        try {
            List<String> receiptsNotToClear = commonService.sp_getuploadedidaman();
            RMSLogger.schedulerInfo(schLogSvc, SchedulerName, SchedulerStart, receiptsNotToClear.size());
    
            File dir = new File(rcptDirectory);
            if (!dir.exists() || !dir.isDirectory()) {
                log.error("Invalid receipt directory: {}", rcptDirectory);
                RMSLogger.schedulerError(schLogSvc, SchedulerName, "Receipt directory not found.", 0);
                return;
            }
    
            File[] files = dir.listFiles();
            if (files == null) {
                log.warn("No files found in directory: {}", rcptDirectory);
                return;
            }
    
            for (File file : files) {
                String fileName = file.getName();
                boolean shouldDelete = receiptsNotToClear.stream()
                        .anyMatch(partial -> fileName.contains(partial));
                if (shouldDelete) {
                    if (file.delete()) {
                        deletedCount++;
                        log.info("Deleted file: {}", fileName);
                    } else {
                        log.warn("Failed to delete file: {}", fileName);
                    }
                }
            }
    
        } catch (Exception ex) {
            String errorMsg = "Unexpected error during Receipt Clearance: " + ex.getMessage();
            log.error(errorMsg, ex);
            RMSLogger.schedulerError(schLogSvc, SchedulerName, errorMsg, deletedCount);
        } finally {
            RMSLogger.schedulerInfo(schLogSvc, SchedulerName, SchedulerCompleted, deletedCount);
        }
    }
}