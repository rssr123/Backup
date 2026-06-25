package com.maven.rms.scheduler.jobs;

import java.io.File;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
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

import com.maven.rms.models.RiplAging;
import com.maven.rms.models.Email;
import com.maven.rms.models.RIPLAgingRequest;
import com.maven.rms.models.ReportRequest;
import com.maven.rms.models.SchedulerLog;
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.EmailService;
import com.maven.rms.services.RIPLAgingRepService;
import com.maven.rms.services.StoreProcedureService;
import com.maven.rms.utils.reports.RIPLAgingReportGenerator;
import com.maven.rms.utils.reports.ReportReconAndAccountGenerator;

import org.springframework.beans.factory.annotation.Value;


@DisallowConcurrentExecution
@Component
@Slf4j
public class RIPLAging implements Job {
    
    //private static final Logger logger = LoggerFactory.getLogger(RIPLAging.class);

    @Autowired
    private SchedulerLogService schLogSvc;
    @Autowired
    private RIPLAgingRepService riplAgingService;
    @Autowired
    private ReportReconAndAccountGenerator rrAG;
    @Autowired
    private RIPLAgingReportGenerator riplAG;

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
        String createddateendString = null;
        String duedatestartString = null;
        String duedateendString = null;
        String receiptdatestartString = null;
        String receiptdateendString = null;
        String impairdatestartstartString = null;
        String impairdateendString = null;
        String writeoffdatestartString = null;
        String writeoffdateendString = null;
        String subject = "";
        String body = "";
        String generationStatus = "";
        String redirect = url + "ripl-aging-report";
        String filename = "";
        String dateOfFileNameString = "";

        try {
            //check count if got status is pending or in progress exist
            Integer queueExist = riplAgingService.sp_getriplagequeuerpt();
               
            //if no pending or in progress record exist, return
            if(queueExist <=0){
                return;
            }
 
            //get pending and in progress records
            List<RiplAging> listOfPendingAndInprogressRIPL = riplAgingService.sp_getpendingriplagingrpt();
 
            int succProcess = CollectionUtils.size(listOfPendingAndInprogressRIPL);
            int failProcess = 0;
 
            SchedulerLog newLog = new SchedulerLog("Process RIPLAging records.",
                    "This job is called from thread: " + Thread.currentThread().getName(), succProcess);
            newLog = schLogSvc.saveNewScheduleLog(newLog);
 
           
            if (newLog == null || newLog.equals(null))
                throw new IllegalArgumentException("The scheduler log failed to update!");
 
            //if there are more than 1 pending or in progress record, then log error
            if (CollectionUtils.size(listOfPendingAndInprogressRIPL) > 1) {
                log.error("Pending or in-progress RIPL Aging records cannot be more than one. Class: " + this.getClass().toString());
            } else if (listOfPendingAndInprogressRIPL.isEmpty() || CollectionUtils.size(listOfPendingAndInprogressRIPL) <=0) {
                log.error("There are no pending or in-progress RIPL Aging records. Class: " + this.getClass().toString());
                return;
            }
               
            int result = 0;
            RiplAging temp = listOfPendingAndInprogressRIPL.get(0);
            RIPLAgingRequest riplAgingRequest = new RIPLAgingRequest(temp.getRpt_ripl_age_id(), null, null, null, "system");

            if (temp.getStatus().toLowerCase().equals("p")) {
                riplAgingRequest.setI_status(inProgressCode);
                // result = sps.sp_updriplagingrpt(temp.getRpt_ripl_age_id(), inProgressCode, null, null, "system");
               result = riplAgingService.sp_updriplagingrpt(riplAgingRequest);
            }
 
            if (result < 1) {
                log.error("Exception in " + this.getClass().toString()
                        + " - Updating RIPL Aging record to in progress failed!");
                        failProcess += 1;
            }
 
            if (temp.getP_dt_req() != null) {
                createddateendString = convertDateTime(temp.getP_dt_req().toString());
            }

            if (temp.getP_dt_due_fr() != null) {
                duedatestartString = convertDateTime(temp.getP_dt_due_fr().toString());
            }

            if (temp.getP_dt_due_to() != null) {
                duedateendString = convertDateTime(temp.getP_dt_due_to().toString());
            }

            if (temp.getP_dt_rcpt_fr() != null) {
                receiptdatestartString = convertDateTime(temp.getP_dt_rcpt_fr().toString());
            }

            if (temp.getP_dt_rcpt_to() != null) {
                receiptdateendString = convertDateTime(temp.getP_dt_rcpt_to().toString());
            }

            if (temp.getP_dt_imp_fr() != null) {
                impairdatestartstartString = convertDateTime(temp.getP_dt_imp_fr().toString());
            }

            if (temp.getP_dt_imp_to() != null) {
                impairdateendString = convertDateTime(temp.getP_dt_imp_to().toString());
            }

            if (temp.getP_dt_wo_fr() != null) {
                writeoffdatestartString = convertDateTime(temp.getP_dt_wo_fr().toString());
            }

            if (temp.getP_dt_wo_to() != null) {
                writeoffdateendString = convertDateTime(temp.getP_dt_wo_to().toString());
            }
 
            if(createddateendString != null){
                dateOfFileNameString = createddateendString.replace("-", "");
            }

            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
            LocalDateTime localDateTime = LocalDateTime.now();
            dateOfFileNameString = localDateTime.format(outputFormatter);
            filename = "RIPL_Aging_" + dateOfFileNameString + "." + listOfPendingAndInprogressRIPL.get(0).getP_file_type().toLowerCase();
           
            File reportFile = riplAG.generateRIPLAgingReport(new ReportRequest(
                filename, 
                createddateendString, 
                duedatestartString,
                        
                duedateendString,  
                temp.getP_imp_status(), 
                temp.getP_exp_status(), 
                temp.getP_ent_ty(),
                        
                temp.getP_ent_nm(),  
                receiptdatestartString, 
                receiptdateendString, 
                impairdatestartstartString, 
                        
                impairdateendString, 
                writeoffdatestartString, 
                writeoffdateendString, 
                temp.getP_email() ,
                temp.getP_file_type()
                ));
 
                        
            if (reportFile == null || !reportFile.exists()) {
                log.error("Exception in " + this.getClass().toString()
                        + " - Cannot generate RIPL Aging report!");
                // update RIPL Aging record to failed
                riplAgingRequest.setI_status(failedCode);
                // result = sps.sp_updriplagingrpt(temp.getRpt_ripl_age_id(), failedCode, null, null, "system");
                result = riplAgingService.sp_updriplagingrpt(riplAgingRequest);
                generationStatus = failedCode;
                failProcess += 1;
            } else {
                // update RIPL Aging record to success
                riplAgingRequest.setI_status(successCode);
                riplAgingRequest.setI_p_file_size((int) reportFile.length());
                riplAgingRequest.setI_p_file_nm(filename);
                // result = sps.sp_updriplagingrpt(temp.getRpt_ripl_age_id(), successCode, (int) reportFile.length(), filename, "system");
                result = riplAgingService.sp_updriplagingrpt(riplAgingRequest);
                generationStatus = successCode;
            }
 
            if (result < 1) {
                log.error("Exception in " + this.getClass().toString()
                        + " - Updating RIPL Aging record to " + generationStatus + " failed!");
                        failProcess += 1;
            }
 
            // if (temp.getP_email() != null && generationStatus.equals(successCode) && !temp.getP_email().isEmpty()) {
            if (StringUtils.isNotEmpty(temp.getP_email()) && generationStatus.equals(successCode)) {
                subject = "RIPL AGING REPORT GENERATION - TASK ID  " + temp.getTask_id();
 
                body = "Report: RIPL Aging Report Generation"
                        + "<br>Task ID: " + temp.getTask_id()
                        + "<br>Task Status: Success"
                        + "<br><br>Dear Sir/Madam,<br><br>"
                        + "This is to inform you that your RIPL Aging Report Generation task has been SUCCESS."
                        + "<br><br>For more details, please refer to the information available in the system regarding the task."
                        + "<br><br><a href='" + redirect + "'>CLICK HERE</a> to access the report task."
                        + "<br><br>Thank you for using our services.<br><br><br>Tuan/Puan,<br>"
                        + "<br>Ini adalah untuk memaklumkan bahawa RIPL Aging Report Generation Task anda telah BERJAYA."
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
            // int failProcess = 0;
            // SchedulerLog newLog = new SchedulerLog("Process RIPLAging records.",
            //         "This job is called from thread: " + Thread.currentThread().getName(), failProcess);
            // schLogSvc.saveNewScheduleLog(newLog);
            log.error("Exception in " + this.getClass().toString(), e);
        }
    }

    public static String convertDateTime(String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) {
            return null;
        }
        try {
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
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
