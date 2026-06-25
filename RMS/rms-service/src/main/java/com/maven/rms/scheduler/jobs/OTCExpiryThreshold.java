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
public class OTCExpiryThreshold implements Job {

    @Autowired
    private MTTRepository mttRepository;

    // @Autowired
    // private MTTService mttService;

    @Autowired
    private SchedulerLogService schedulerLogService;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) throws JobExecutionException {
        RMSLogger.schedulerInfo("OTCExpiryThreshold is Initializing...");

        // Log scheduler execution
        SchedulerLog schedulerLog = new SchedulerLog(
                "Update OTC Status",
                "This job updates OTC statuses older than 14 days",
                1
        );
        schedulerLog = schedulerLogService.saveNewScheduleLog(schedulerLog);

        try {
            // Retrieve all entries from rms_mtt where order_status = 'PO'
            List<OnlinePayment> otcRecords = mttRepository.findByOrderStatus("PO");

            if (otcRecords.isEmpty()) {
                log.info("No records with order_status 'PO' found.");
                return;
            }

            log.info("Fetched {} records with order_status 'PO'", otcRecords.size());

            // Iterate over the records and update order_status if the condition is met
            for (OnlinePayment record : otcRecords) {
                if (record.getDt_otc_expiry() != null) {
                    LocalDateTime dtOtcExpiry = record.getDt_otc_expiry();
                    log.info("Record MTT ID: {} - Expiry Date: {}", record.getMttId(), dtOtcExpiry);

                    // Update status if dtOtcExpiry > current date
                    if (dtOtcExpiry.isBefore(LocalDateTime.now())) {
                        record.setOrder_status("OE");
                        mttRepository.save(record);
                        log.info("Updated order_status to 'OE' for MTT ID: {}", record.getMttId());
                    } else {
                        log.info("Record MTT ID: {} - Expiry Date {} is not after the current date.", record.getMttId(), dtOtcExpiry);
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
