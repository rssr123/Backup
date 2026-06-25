package com.maven.rms.scheduler.jobs;

import com.maven.rms.models.OnlinePayment;
import com.maven.rms.models.SchedulerLog;
import com.maven.rms.repositories.MTTRepository;
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.MTTService;
import com.maven.rms.utils.RMSLogger;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import javax.transaction.Transactional;

@DisallowConcurrentExecution
@Component
@Slf4j
public class ESEmailExpiry implements Job {

    @Autowired
    private MTTRepository mttRepository;

    @Autowired
    private SchedulerLogService schedulerLogService;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) throws JobExecutionException {
        RMSLogger.schedulerInfo("ESEmailExpiry is Initializing...");

        // Log scheduler execution
        SchedulerLog schedulerLog = new SchedulerLog(
                "Update OTC Status",
                "This job updates order status",
                1);
        schedulerLog = schedulerLogService.saveNewScheduleLog(schedulerLog);

        try {
            // Retrieve all entries from rms_mtt where order_status = 'ES'
            List<OnlinePayment> otcRecords = mttRepository.findByOrderStatus("ES");

            if (otcRecords.isEmpty()) {
                log.debug("No records with order_status 'ES' found.");
                return;
            }

            log.debug("Fetched {} records with order_status 'ES'", otcRecords.size());

            // Iterate over the records and update order_status if the condition is met
            for (OnlinePayment record : otcRecords) {
                if (record.getDt_email_expiry() != null) {
                    LocalDateTime dtEmailExpiry = record.getDt_email_expiry();
                    log.debug("Record MTT ID: {} - Expiry Date: {}", record.getMttId(), dtEmailExpiry);

                    // Update status if dtOtcExpiry > current date
                    if (dtEmailExpiry.isBefore(LocalDateTime.now())) {
                        record.setOrder_status("EE");
                        mttRepository.save(record);
                        log.info("Updated order_status to 'EE' for MTT ID: {}", record.getMttId());
                    } else {
                        log.info("Record MTT ID: {} - Expiry Date {} is not after the current date.", record.getMttId(),
                                dtEmailExpiry);
                    }
                }
            }

            RMSLogger.schedulerInfo("OTCExpiryThreshold job completed successfully.");
        } catch (Exception e) {
            log.error("Error occurred while updating OTC statuses: ", e);
            throw new JobExecutionException(e);
        } finally {
            schedulerLogService.saveNewScheduleLog(schedulerLog);
        }
    }
}
