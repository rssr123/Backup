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
public class OTCEmailPaymentDate implements Job {

    @Autowired
    private MTTRepository mttRepository;

    // @Autowired
    // private MTTService mttService;

    @Autowired
    private SchedulerLogService schedulerLogService;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) throws JobExecutionException {
        RMSLogger.schedulerInfo("OTCEmailPaymentDate is Initializing...");

        // Log scheduler execution
        SchedulerLog schedulerLog = new SchedulerLog(
                "Update Order Status",
                "This job updates Order Status older than 14 days",
                1
        );
        schedulerLog = schedulerLogService.saveNewScheduleLog(schedulerLog);

        try {
            // Retrieve all entries from rms_mtt where order_status = 'PP'
            List<OnlinePayment> otcRecords = mttRepository.findByOrderStatus("PP");

            if (otcRecords.isEmpty()) {
                log.info("No records with order_status 'PP' found.");
                return;
            }

            log.info("Fetched {} records with order_status 'PP'", otcRecords.size());

            // Iterate over the records and update order_status if the condition is met
            for (OnlinePayment record : otcRecords) {
                if (record.getDt_created() != null) {
                    LocalDateTime dtPymt = record.getDt_created();
                    log.info("Record MTT ID: {} - Expiry Date: {}", record.getMttId(), dtPymt);

                    // Update status if dtPymt > 14 days
                    if (dtPymt.isBefore(LocalDateTime.now().minusDays(14))) {
                        record.setOrder_status("F");
                        mttRepository.save(record);
                        log.info("Updated order_status to 'F' for MTT ID: {}", record.getMttId());
                    } else {
                        log.info("Record MTT ID: {} - Expiry Date {} is not more than 14 days old.", record.getMttId(), dtPymt);
                    }
                }
            }
            
            

            RMSLogger.schedulerInfo("OTCEmailPaymentDate job completed successfully.");
        } catch (Exception e) {
            log.error("Error occurred while updating order status: ", e);
            throw new JobExecutionException(e);
        } finally {
            schedulerLogService.saveNewScheduleLog(schedulerLog);
        }
    }
}
