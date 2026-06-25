package com.maven.rms.scheduler.jobs;

import java.math.BigInteger;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maven.rms.models.FMSARIModel;
import com.maven.rms.models.SchedulerLog;
import com.maven.rms.models.SchedulerUpdRequest;
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.FMSARIService;
import com.maven.rms.services.SchService;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;

@DisallowConcurrentExecution
@Component
@Slf4j
public class FMSARI implements Job {
    //private static final Logger logger = LoggerFactory.getLogger(UpdateRICPWriteOff.class);

    @Autowired
    private SchedulerLogService schLogSvc;

    @Autowired
    private FMSARIService fmsARIService;

    @Autowired
    private SchService schService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        int succUpdates = 0;
        int failUpdates = 0;
        BigInteger result = null;

        Scheduler scheduler = context.getScheduler();
        
        SchedulerUpdRequest schedulerUpdRequest = new SchedulerUpdRequest();
        schedulerUpdRequest.setI_function_nm("FMSARI");
        if(schService.sp_getjobstatus(schedulerUpdRequest).equals('E')){
            log.info("FMSARI job has error. Exiting...");
            return;
        }

        try {
            SchedulerLog newLog = new SchedulerLog("FMSARI",
                    "This job is called from thread: " + Thread.currentThread().getName() + "ID: " + scheduler.getSchedulerInstanceId(),
                    succUpdates);
            newLog = schLogSvc.saveNewScheduleLog(newLog);

            if (newLog == null || newLog.equals(null))
                throw new IllegalArgumentException("The scheduler log failed to update!");

            // get info and insert into fmsari table
            List<FMSARIModel> fmsMTT = fmsARIService.sp_getfmsmtt();

            while (CollectionUtils.size(fmsMTT) > 0) {
            // while (fmsMTT.size() > 0) {
                // log.info("FMSARI: " + fmsMTT.size() + " records found.");
                log.info("FMSARI: " + (CollectionUtils.size(fmsMTT) + " records found."));
                BigInteger ari_hid = null;

                // for (int i = 0; i < fmsMTT.size(); i++) {
                for (int i = 0; i < CollectionUtils.size(fmsMTT); i++) {

                    // insert to header table
                    if (i == 0) {
                        ari_hid = fmsARIService.sp_insfmsmtth(fmsMTT.get(i).getCustomer(), fmsMTT.get(i).getInv_dt());
                    }

                    BigInteger current_mtt_pg_id = fmsMTT.get(i).getMtt_pg_id();
                    Integer flag = 0;
            
                    // If this is not the last record, compare with the next record's mtt_pg_id
                    if (i < CollectionUtils.size(fmsMTT) - 1) {
                        BigInteger next_mtt_pg_id = fmsMTT.get(i + 1).getMtt_pg_id();
                        if (!current_mtt_pg_id.equals(next_mtt_pg_id)) {
                            flag = 1; // Set the flag if current mtt_pg_id is different from the next one
                        }
                    } else {
                        // If it's the last record, set flag to 1 (assuming there's no next record)
                        flag = 1;
                    }

                    // insert to detail table
                    // result = fmsARIService.sp_insfmsmttb(fmsMTT.get(i).getMtt_pg_id(),
                    // fmsMTT.get(i).getPg_pymt_amt(), fmsMTT.get(i).getQty(),
                    // fmsMTT.get(i).getItem_desc(),
                    // fmsMTT.get(i).getUnit_fee(), fmsMTT.get(i).getRcpt_no(),
                    // fmsMTT.get(i).getCust_nm(), fmsMTT.get(i).getEntity_nm(),
                    // fmsMTT.get(i).getEntity_no(),
                    // fmsMTT.get(i).getEntity_type(), fmsMTT.get(i).getGross_amt(),
                    // fmsMTT.get(i).getFee_detail_id(), fmsMTT.get(i).getPg_pymt_method(),
                    // fmsMTT.get(i).getTax_amt(),
                    // fmsMTT.get(i).getCustomer(), ari_hid, fmsMTT.get(i).getItem_ref_no(),
                    // fmsMTT.get(i).getCp_no());

                    result = fmsARIService.sp_insfmsmttb(fmsMTT.get(i), ari_hid, flag);

                }

                fmsMTT = fmsARIService.sp_getfmsmtt();

            }

            // call fms posting api
            List<FMSARIModel> fmsARI = fmsARIService.sp_getfmsari();
            if (CollectionUtils.size(fmsARI) > 0) {
            // if (fmsARI.size() > 0) {
                // log.info("FMSARI: " + fmsARI.size() + " records found.");
                log.info("FMSARI: " + CollectionUtils.size(fmsARI) + " records found.");
                String body = fmsARIService.generateStringBody(fmsARI);
            }
            SchedulerUpdRequest schedulerUpdRequestSucc = new SchedulerUpdRequest();
            schedulerUpdRequestSucc.setI_function_nm("FMSARI");
            schedulerUpdRequestSucc.setI_sch_status("C");
            schService.sp_updjobstatus(schedulerUpdRequestSucc);

        } catch (Exception e) {
            log.error("Exception in " + this.getClass().toString(), e);

            SchedulerUpdRequest schedulerUpdRequestErr = new SchedulerUpdRequest();
            schedulerUpdRequestErr.setI_function_nm("FMSARI");
            schedulerUpdRequestErr.setI_sch_status("E");
            schService.sp_updjobstatus(schedulerUpdRequestErr);

            schService.sp_upderrorjobs(schedulerUpdRequestErr);
            throw new JobExecutionException(e, false);  // Set refireImmediately to false
        }
    }
}