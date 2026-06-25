package com.maven.rms.scheduler.jobs;

import com.maven.rms.models.GHLPaymentResponse;
import com.maven.rms.models.SchedulerLog;
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.OnlinePaymentService;
import com.maven.rms.utils.RMSLogger;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@DisallowConcurrentExecution
@Component
@Slf4j
public class GHLResp implements Job {

    @Autowired
    private OnlinePaymentService onlinePaymentService;

    @Autowired
    private SchedulerLogService schedulerLogService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        RMSLogger.schedulerInfo("Processing non-processed GHL Responses ...");

        // Log scheduler execution
        SchedulerLog schedulerLog = new SchedulerLog(
                "Processing non-processed GHL Responses",
                "This Job processes non-processed GHL Responses",
                1);
        schedulerLog = schedulerLogService.saveNewScheduleLog(schedulerLog);

       try {
            // Get the list of non-processed GHL responses
            List<GHLPaymentResponse> ghlResponses = onlinePaymentService.sp_getghlresp();

            for (GHLPaymentResponse ghlResponse : ghlResponses) {

                onlinePaymentService.processPayment(ghlResponse, null, false);
                // Call the processPayment method in the controller
                log.info("Processing GHL Response for Order Number: " + ghlResponse.getOrderNumber());
            }
        } catch (Exception e) {
            log.error("Error occurred while processing GHL Responses: ", e);
            throw new JobExecutionException(e);
        } finally {
            schedulerLogService.saveNewScheduleLog(schedulerLog);
        }
    }
}

