package com.maven.rms.scheduler.jobs;

import com.maven.rms.config.RMSProperties;
import com.maven.rms.models.OTCBalancingRequest;
import com.maven.rms.models.SchedulerLog;
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.OTCDailyBalService;
import com.maven.rms.services.OTCService; // Service for accessing the SP
import com.maven.rms.utils.RMSLogger;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

//import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@DisallowConcurrentExecution
@Component
@Slf4j
public class OTCCounterCheckout implements Job {

    @Autowired
    private OTCService otcService; // Service for stored procedure calls

    @Autowired
    private OTCDailyBalService otcDailyBalService;

    @Autowired
    private SchedulerLogService schLogSvc;

    private String SchedulerName = "OTCCounterCheckout";
    private String SchedulerStart = "Scheduler initialization started.";
    private String SchedulerCompleted = "Scheduler is Completed";
    private String Msg = "";

    @Override
    @Transactional
    public void execute(JobExecutionContext context) throws JobExecutionException {
        RMSLogger.schedulerInfo(schLogSvc, SchedulerName, SchedulerStart, 0);
        RMSLogger.schedulerInfo("OTCCounterCheckout is Initializing...");

        // Log scheduler execution
        // SchedulerLog schedulerLog = new SchedulerLog(
        // "Retrieve Open OTC Counters",
        // "This job retrieves data from sp_getotccheckout and call
        // sp_updotcdailybalstatus after 10 PM.",
        // 1);
        // schedulerLog = schLogSvc.saveNewScheduleLog(schedulerLog);

        try {
            // Fetch open counters using the stored procedure
            List<HashMap<String, String>> openCounters = otcService.sp_getotccheckout();

            // Process each open counter and call the `sp_updotcdailybalstatus` procedure
            for (HashMap<String, String> counter : openCounters) {
                String branchCode = counter.get("branch_cd");
                String balDateStr = counter.get("check_in");
                String balStatus = counter.get("bal_status");
                String balType = counter.get("bal_type");
                String ssm4uUserRefNo = counter.get("modified_by");

                RMSLogger.schedulerInfo("Processing counter with Branch Code: " + branchCode);

                // Convert balDateStr to java.sql.Date
                LocalDateTime localDateTime = LocalDateTime.parse(balDateStr,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));
                Date balDate = Date.valueOf(localDateTime.toLocalDate());

                Msg = "Try to process Branch "
                        + (branchCode == null || branchCode.isEmpty() ? "[Unknown Branch]" : branchCode) +
                        " Balancing Date " + Objects.toString(balDate, "[No Date]");
                RMSLogger.schedulerInfo(schLogSvc, SchedulerName, Msg, 0);

                // Create and populate the request object
                OTCBalancingRequest request = new OTCBalancingRequest();
                request.setBranch_code(branchCode);
                request.setBal_date(balDate);
                request.setBal_status(balStatus);
                request.setBal_type(balType);
                request.setSsm4uuserrefno(ssm4uUserRefNo);

                // Call the update stored procedure
                otcDailyBalService.sp_updotcdailybalstatus(request);

                Msg = "Branch " + (branchCode == null || branchCode.isEmpty() ? "[Unknown Branch]" : branchCode)
                        + " Updated successfully.";
                RMSLogger.schedulerInfo(schLogSvc, SchedulerName, Msg, 0);

            }

            RMSLogger.schedulerInfo("OTCCounterCheckout completed successfully.");
        } catch (Exception ex) {
            String ErrorMsg = "Unexpected error: " + ex.getMessage() + ex.getStackTrace();
            log.error(ErrorMsg);

            RMSLogger.schedulerError(schLogSvc, SchedulerName, ErrorMsg, 0);
        } finally {
            RMSLogger.schedulerInfo(schLogSvc, SchedulerName, SchedulerCompleted, 0);
        }
    }
}