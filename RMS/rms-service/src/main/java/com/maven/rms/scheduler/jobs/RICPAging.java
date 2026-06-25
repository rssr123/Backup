package com.maven.rms.scheduler.jobs;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.maven.rms.models.Email;
import com.maven.rms.models.RICPAgingReportRequest;
import com.maven.rms.models.ReportRequest;
import com.maven.rms.models.SchedulerLog;
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.EmailService;
import com.maven.rms.services.RICPAgRepReqService;
import com.maven.rms.utils.reports.RICPAgingReportGenerator;

@DisallowConcurrentExecution
@Component
@Slf4j
public class RICPAging implements Job {
    //private static final Logger logger = LoggerFactory.getLogger(UpdateRICPWriteOff.class);

    @Autowired
    private SchedulerLogService schLogSvc;
    @Autowired
    private RICPAgRepReqService rarRSvc;
    @Autowired
    private RICPAgingReportGenerator rarGen;
	@Autowired
	private EmailService emailer;
	
	@Value("${rms.application.backPortalURL}")
    private String url;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
    	try {
    		RICPAgingReportRequest request = rarRSvc.getPendingRequest();
        	if(request != null) {
	        	request.setStatus("I");
	        	request.setModified_by("system");
	        	rarRSvc.updateStatus(request);
        	}
        	    		
            SchedulerLog newLog = new SchedulerLog("Report - RICP Aging Report.",
                    "This job is called from thread: " + Thread.currentThread().getName()
                    + (request != null ? " | Detected request to generate report." : " | No request pending."),
                    request != null ? 1 : 0);
            newLog = schLogSvc.saveNewScheduleLog(newLog);

            if (newLog == null || newLog.equals(null))
                throw new IllegalArgumentException("The scheduler log failed to update!");

            if(request != null) {
            	File report = rarGen.generateReport(new ReportRequest("RICP_Aging_" 
            			//+ request.getP_dt_req().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")) 
            			+ request.getDt_created().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))
            			+ "." + request.getP_file_type(), request));
            	
            	if(report != null) {
            		if(request.getP_email() == null || !request.getP_email().equals("")) {
            			String redirect = url + "/ricp-aging";
            			String subject = "RICP AGING REPORT GENERATION - TASK ID  " + request.getTask_id();
            			  
            			String body = "Report: RICP Aging Report Generation"
                                  + "<br>Task ID: " + request.getTask_id()
                                  + "<br>Task Status: Success"
                                  + "<br><br>Dear Sir/Madam,<br><br>"
                                  + "This is to inform you that your RICP Aging Report Generation task has been SUCCESS."
                                  + "<br><br>For more details, please refer to the information available in the system regarding the task."
                                  + "<br><br><a href='" + redirect + "'>CLICK HERE</a> to access the report task."
                                  + "<br><br>Thank you for using our services.<br><br><br>Tuan/Puan,<br>"
                                  + "<br>Ini adalah untuk memaklumkan bahawa RICP Aging Report Generation Task anda telah BERJAYA."
                                  + "<br><br>Untuk maklumat lanjut, sila rujuk informasi yang tersedia dalam sistem berkenaan tugasan ini."
                                  + "<br><br><a href='" + redirect + "'>KLIK DI SINI</a> untuk mengakses laporan tugasan."
                                  + "<br><br>Terima kasih kerana menggunakan perkhidmatan kami."
                                  + "<br><br><br>[THIS IS AN AUTOMATED MESSAGE - PLEASE "
                                  + "DO NOT REPLY DIRECTLY TO THIS EMAIL] <br>";
            			
            			emailer.saveEmailDets(new Email("Report", request.getP_email(), "", "", subject, body, null));
            		}
            		
            		request.setStatus("S");
            		request.setP_file_nm(report.getName());
            		request.setP_file_size((int)report.length());
            		rarRSvc.updateReportDet(request);
            		newLog.setSuccessTxn(1);
            	}
            	else {
            		request.setStatus("F");
            		rarRSvc.updateStatus(request);
            		newLog.setFailTxn(1);
            	}
        		newLog.setDtModified(LocalDateTime.now());
        		schLogSvc.saveNewScheduleLog(newLog);
            }
                
        } catch (Exception e) {
            log.error("Exception in " + this.getClass().toString(), e);
        }
    }
}
