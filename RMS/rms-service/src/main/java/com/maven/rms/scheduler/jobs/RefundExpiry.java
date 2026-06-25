package com.maven.rms.scheduler.jobs;

import java.time.LocalDateTime;
import java.util.List;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maven.rms.models.Refund;
import com.maven.rms.models.SchedulerLog;
import com.maven.rms.repositories.RefundAccountCodeRepository;
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.utils.RMSLogger;

@DisallowConcurrentExecution
@Component
@Slf4j
public class RefundExpiry implements Job {


    @Autowired
    private SchedulerLogService schedulerLogService;

    @Autowired
    private RefundAccountCodeRepository refundAccountCodeRepository;

  

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        RMSLogger.schedulerInfo("RefundExpiry is Initializing...");


        // Log scheduler execution
        SchedulerLog schedulerLog = new SchedulerLog(
                "Update Refund Status",
                "This job updates Refund statuses older than 6 months",
                1
        );
        

        schedulerLog = schedulerLogService.saveNewScheduleLog(schedulerLog);

        try {
            List<Refund> refunds = refundAccountCodeRepository.findByRttStatus();

            if (refunds.isEmpty()) {
                log.info("No records");
                return;
            }

            log.info("Fetched {} records", refunds.size());

            //LocalDateTime simulatedToday = LocalDateTime.of(2027, 1, 1, 0, 0); // 2027-01-01

            // Iterate over the records and update order_status if the condition is met
            for (Refund record : refunds) {
                if (record.getDt_requested() != null) {
                    LocalDateTime dtRttRequested = record.getDt_requested();
                    log.info("Record RTT ID: {} - Requested Date: {}", record.getRtt_id(), dtRttRequested);
            
                    // Create a new date 6 months after dtRttRequested
                    LocalDateTime dtExpRttRequested = dtRttRequested.plusMonths(6);
            
                    // Compare today's date with dtExpRttRequested
                    //if (simulatedToday.isAfter(dtExpRttRequested)) {
                    if (LocalDateTime.now().isAfter(dtExpRttRequested)) {
                        record.setRtt_status("RE");
                        refundAccountCodeRepository.saveRefund(record);
                        log.info("Updated order_status to 'RE' for RTT ID: {}", record.getRtt_id());
                    } else {
                        log.info("Record RTT ID: {} - Today's date is not after the expiration date: {}", record.getRtt_id(), dtExpRttRequested);
                    }
                }
            }
            
            

            RMSLogger.schedulerInfo("RefundExpiry job completed successfully.");
        } catch (Exception e) {
            log.error("Error occurred while updating RTT statuses: ", e);
            throw new JobExecutionException(e);
        } finally {
            schedulerLogService.saveNewScheduleLog(schedulerLog);
        }

    }
}
