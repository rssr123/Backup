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

import com.maven.rms.models.RefundWf;
import com.maven.rms.models.RefundWfHist;
import com.maven.rms.models.SchedulerLog;
import com.maven.rms.repositories.RefundAccountCodeRepository;
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.utils.RMSLogger;

@DisallowConcurrentExecution
@Component
@Slf4j
public class RefundReassign implements Job {

    @Autowired
    private SchedulerLogService schedulerLogService;

    @Autowired
    private RefundAccountCodeRepository refundAccountCodeRepository;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        RMSLogger.schedulerInfo("FMSARR is Initializing...");

        // Log scheduler execution
        SchedulerLog schedulerLog = new SchedulerLog(
                "Update Refund Reassign",
                "This job updates pick up by to null after more than 5 days",
                1
        );

        schedulerLog = schedulerLogService.saveNewScheduleLog(schedulerLog);

        try {
            // Fetch records from RefundWf table
            List<RefundWf> refundWfList = refundAccountCodeRepository.findByPickUpByWf();
            handleRefundUpdates(refundWfList);

            // Fetch records from RefundWfHist table
            List<RefundWfHist> refundWfHistList = refundAccountCodeRepository.findByPickUpByWfHist();
            handleRefundUpdatesHist(refundWfHistList);

            RMSLogger.schedulerInfo("FMSARR job completed successfully.");
        } catch (Exception e) {
            log.error("Error occurred while updating statuses: ", e);
            throw new JobExecutionException(e);
        } finally {
            schedulerLogService.saveNewScheduleLog(schedulerLog);
        }
    }

    private void handleRefundUpdates(List<RefundWf> refundWfList) {
        if (refundWfList.isEmpty()) {
            log.info("No records found in RefundWf");
            return;
        }

        log.info("Fetched {} records from RefundWf", refundWfList.size());
        LocalDateTime simulatedToday = LocalDateTime.of(2025, 2, 1, 0, 0); // Simulated date

        for (RefundWf record : refundWfList) {
            if (record.getDate_pick() != null) {
                LocalDateTime dtPickUp = record.getDate_pick();
                log.info("RefundWf ID: {} - Date Picked: {}", record.getRtt_wf_id(), dtPickUp);

                LocalDateTime dtExpPickUp = dtPickUp.plusDays(5);

                if (simulatedToday.isAfter(dtExpPickUp)) {
                    record.setPickup_by(null);
                    refundAccountCodeRepository.saveRefundWf(record);
                    log.info("Updated RefundWf pickup_by to null for ID: {}", record.getRtt_wf_id());
                } else {
                    log.info("RefundWf ID: {} - Today's date is not after the expiration date: {}", record.getRtt_wf_id(), dtExpPickUp);
                }
            }
        }
    }

    private void handleRefundUpdatesHist(List<RefundWfHist> refundWfHistList) {
        if (refundWfHistList.isEmpty()) {
            log.info("No records found in RefundWfHist");
            return;
        }

        log.info("Fetched {} records from RefundWfHist", refundWfHistList.size());
        LocalDateTime simulatedToday = LocalDateTime.of(2025, 2, 1, 0, 0); // Simulated date

        for (RefundWfHist record : refundWfHistList) {
            if (record.getDate_pick() != null) {
                LocalDateTime dtPickUp = record.getDate_pick();
                log.info("RefundWfHist ID: {} - Date Picked: {}", record.getRtt_wf_hist_id(), dtPickUp);

                LocalDateTime dtExpPickUp = dtPickUp.plusDays(5);

                if (simulatedToday.isAfter(dtExpPickUp)) {
                    record.setPickup_by(null);
                    refundAccountCodeRepository.saveRefundWfHist(record);
                    log.info("Updated RefundWfHist pickup_by to null for ID: {}", record.getRtt_wf_hist_id());
                } else {
                    log.info("RefundWfHist ID: {} - Today's date is not after the expiration date: {}", record.getRtt_wf_hist_id(), dtExpPickUp);
                }
            }
        }
    }
}
