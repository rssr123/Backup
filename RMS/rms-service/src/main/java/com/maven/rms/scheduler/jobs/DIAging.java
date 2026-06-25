package com.maven.rms.scheduler.jobs;

import java.io.File;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maven.rms.models.DeferredIncomeAging;
import com.maven.rms.models.DeferredIncomeAgingRequest;
import com.maven.rms.models.Email;
import com.maven.rms.models.DeferredIncomeAgingRequest;
import com.maven.rms.models.ReportRequest;
import com.maven.rms.models.RiplAging;
import com.maven.rms.models.SchedulerLog;
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.DIAgingRepService;
import com.maven.rms.services.EmailService;
import com.maven.rms.utils.reports.DIAgingReportGenerator;
import com.maven.rms.utils.reports.DIAgingReportGenerator;
import com.maven.rms.utils.reports.ReportReconAndAccountGenerator;
import org.apache.commons.collections4.CollectionUtils;

import org.springframework.beans.factory.annotation.Value;
import org.apache.commons.lang3.StringUtils;

@DisallowConcurrentExecution
@Component
@Slf4j
public class DIAging implements Job {

    //private static final Logger logger = LoggerFactory.getLogger(DIAging.class);

    @Autowired
    private SchedulerLogService schLogSvc;

    @Autowired
    private DIAgingRepService diAgingRepSvc;

    @Autowired
    private ReportReconAndAccountGenerator rrAG;

    @Autowired
    private DIAgingReportGenerator diAG;

    @Autowired
    private EmailService emailService;

    @Value("${rms.application.backPortalURL}")
    private String url;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        String successCode = "S";
        String inProgressCode = "I";
        String pendingCode = "P";
        String failedCode = "F";
        String receiptdateendString = null;
        String effectivedatestartString = null;
        String effectivedateendString = null;
        String expirydatestartString = null;
        String expirydateendString = null;
        String approvaldatestartstartString = null;
        String approvaldateendString = null;
        String terminationdatestartString = null;
        String terminationdateendString = null;
        String subject = "";
        String body = "";
        String generationStatus = "";
        String redirect = url + "/deferred-income-aging";
        String filename = "";
        String dateOfFileNameString ="";

        try {
            //check count if got status is pending or in progress exist
            Integer queueExist = diAgingRepSvc.sp_getdiagequeuerpt();
                
            //if no pending or in progress record exist, return
            if(queueExist <=0){
                return;
            }

            //get pending and in progress records
            List<DeferredIncomeAging> listOfPendingAndInprogressDI = diAgingRepSvc.sp_getpendingdiagingrpt();

            // int succProcess = listOfPendingAndInprogressDI.size();
            int succProcess = CollectionUtils.size(listOfPendingAndInprogressDI);
            int failProcess = 0;

            SchedulerLog newLog = new SchedulerLog("Process DIAging records.",
                    "This job is called from thread: " + Thread.currentThread().getName(), succProcess);
            newLog = schLogSvc.saveNewScheduleLog(newLog);

            
            if (newLog == null || newLog.equals(null))
                throw new IllegalArgumentException("The scheduler log failed to update!");

            //if there are more than 1 pending or in progress record, then log error
            if (CollectionUtils.size(listOfPendingAndInprogressDI)> 1) {
                log.error("Pending or in-progress DI Aging records cannot be more than one. Class: " + this.getClass().toString());
            } else if (listOfPendingAndInprogressDI.isEmpty() || CollectionUtils.size(listOfPendingAndInprogressDI) <=0) {
                log.error("There are no pending or in-progress DI Aging records.  Class: " + this.getClass().toString());
                return;
            } 
                
            // int result = 0;
            // DeferredIncomeAging temp = listOfPendingAndInprogressDI.get(0);
            // DeferredIncomeAgingRequest DIRequest = new DeferredIncomeAgingRequest();
            int result = 0;
            DeferredIncomeAging temp = listOfPendingAndInprogressDI.get(0);
            DeferredIncomeAgingRequest diAgingRequest = new DeferredIncomeAgingRequest(temp.getRpt_di_age_id(), null, null, null);

            if (temp.getStatus().toLowerCase().equals("p")) {
                diAgingRequest.setI_status(inProgressCode);
                // result = sps.sp_upddiagingrpt(temp.getRpt_di_age_id(), inProgressCode, null, null, "system");
               result = diAgingRepSvc.sp_upddiagingrpt(diAgingRequest, "system");
            }

            if (result < 1) {
                log.error("Exception in " + this.getClass().toString()
                        + " - Updating DI Aging record to in progress failed!");
                        failProcess += 1;
            }

            if (temp.getP_dt_req() != null) {
                receiptdateendString = convertDateTime(temp.getP_dt_req().toString());
            }

            if (temp.getP_dt_eff_fr() != null) {
                effectivedatestartString = convertDateTime(temp.getP_dt_eff_fr().toString());
            }

            if (temp.getP_dt_eff_to() != null) {
                effectivedateendString = convertDateTime(temp.getP_dt_eff_to().toString());
            }

            if (temp.getP_dt_exp_fr() != null) {
                expirydatestartString = convertDateTime(temp.getP_dt_exp_fr().toString());
            }

            if (temp.getP_dt_exp_to() != null) {
                expirydateendString = convertDateTime(temp.getP_dt_exp_to().toString());
            }

            if (temp.getP_dt_app_fr() != null) {
                approvaldatestartstartString = convertDateTime(temp.getP_dt_app_fr().toString());
            }

            if (temp.getP_dt_app_to() != null) {
                approvaldateendString = convertDateTime(temp.getP_dt_app_to().toString());
            }

            if (temp.getP_dt_tmn_fr() != null) {
                terminationdatestartString = convertDateTime(temp.getP_dt_tmn_fr().toString());
            }

            if (temp.getP_dt_tmn_to() != null) {
                terminationdateendString = convertDateTime(temp.getP_dt_tmn_to().toString());
            }

            if(receiptdateendString != null){
                dateOfFileNameString = receiptdateendString.replace("-", "");
            }

            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
            LocalDateTime localDateTime = LocalDateTime.now();
            dateOfFileNameString = localDateTime.format(outputFormatter);
            filename = "DI_Aging_" + dateOfFileNameString + "." + listOfPendingAndInprogressDI.get(0).getP_file_type().toLowerCase();

            
            File reportFile = diAG.generateDIAgingReport(new ReportRequest(
                filename, 
                receiptdateendString, 
                effectivedatestartString,
                effectivedateendString, 
                temp.getP_status(), 
                temp.getP_tmn_status(), 
                temp.getP_ent_ty(),
                temp.getP_ent_nm(), 
                temp.getP_txn_ty(), 
                expirydatestartString, 
                expirydateendString,
                approvaldatestartstartString, 
                approvaldateendString, 
                terminationdatestartString, 
                terminationdateendString,
                temp.getP_batch_no(), 
                temp.getP_fms_ref_no(), 
                temp.getP_email() , 
                temp.getP_file_type()
            ));

            if (reportFile == null || !reportFile.exists()) {
                log.error("Exception in " + this.getClass().toString()
                        + " - Cannot generate DI Aging report!");
                // update DI Aging record to failed
                diAgingRequest.setI_status(failedCode);
                // result = sps.sp_upddiagingrpt(temp.getRpt_di_age_id(), failedCode, null, null, "system");
                result = diAgingRepSvc.sp_upddiagingrpt(diAgingRequest, "system");
                generationStatus = failedCode;
                failProcess += 1;
            } else {
                // update DI Aging record to success
                diAgingRequest.setI_status(successCode);
                diAgingRequest.setI_p_file_size((int) reportFile.length());
                diAgingRequest.setI_p_file_nm(filename);
                // result = sps.sp_upddiagingrpt(temp.getRpt_di_age_id(), successCode, (int) reportFile.length(), filename, "system");
                result = diAgingRepSvc.sp_upddiagingrpt(diAgingRequest, "system");
                generationStatus = successCode;
            }

            if (result < 1) {
                log.error("Exception in " + this.getClass().toString()
                        + " - Updating DI Aging record to " + generationStatus + " failed!");
                        failProcess += 1; 
            }

            // if (temp.getP_email() != null && generationStatus.equals(successCode) && !temp.getP_email().isEmpty()) {
                if (StringUtils.isNotEmpty(temp.getP_email()) && generationStatus.equals(successCode)) {
                subject = "DEFERRED INCOME AGING REPORT GENERATION - TASK ID  " + temp.getTask_id();

                body = "Report: Deferred Income Aging Report Generation"
                        + "<br>Task ID: " + temp.getTask_id()
                        + "<br>Task Status: Success"
                        + "<br><br>Dear Sir/Madam,<br><br>"
                        + "This is to inform you that your Deferred Income Aging Report Generation task has been SUCCESS."
                        + "<br><br>For more details, please refer to the information available in the system regarding the task."
                        + "<br><br><a href='" + redirect + "'>CLICK HERE</a> to access the report task."
                        + "<br><br>Thank you for using our services.<br><br><br>Tuan/Puan,<br>"
                        + "<br>Ini adalah untuk memaklumkan bahawa Deferred Income Aging Report Generation Task anda telah BERJAYA."
                        + "<br><br>Untuk maklumat lanjut, sila rujuk informasi yang tersedia dalam sistem berkenaan tugasan ini."
                        + "<br><br><a href='" + redirect + "'>KLIK DI SINI</a> untuk mengakses laporan tugasan."
                        + "<br><br>Terima kasih kerana menggunakan perkhidmatan kami."
                        + "<br><br><br>[THIS IS AN AUTOMATED MESSAGE - PLEASE "
                        + "DO NOT REPLY DIRECTLY TO THIS EMAIL] <br>";

                emailService.saveEmailDets((new Email("Report", temp.getP_email(), "", "", subject, body, null)));
            }

            succProcess -= failProcess;
            newLog.setSuccessTxn(succProcess);
            newLog.setFailTxn(failProcess);
            newLog.setDtModified(LocalDateTime.now());

            log.debug("Succ: " + Integer.toString(succProcess) + " || Fail: "
                    + Integer.toString(failProcess));
            schLogSvc.saveNewScheduleLog(newLog);

        } catch (Exception e) {
            log.error("Exception in " + this.getClass().toString(), e);
        }
    }

    public static String convertDateTime(String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) {
            return null;
        }
        // try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            DateTimeFormatter alternativeInputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            LocalDateTime localDateTime;
            try {
                localDateTime = LocalDateTime.parse(dateTime, inputFormatter);
            } catch (DateTimeParseException e) {
                // Parse using the alternative formatter
                localDateTime = LocalDateTime.parse(dateTime, alternativeInputFormatter);
            }
            return localDateTime.format(outputFormatter);
        // } catch (DateTimeParseException e) {
        //     e.printStackTrace();
        //     return null;
        // }
    }
}
