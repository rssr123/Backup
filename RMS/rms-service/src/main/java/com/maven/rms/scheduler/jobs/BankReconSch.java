package com.maven.rms.scheduler.jobs;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.maven.rms.models.SchedulerLog;
import com.maven.rms.models.SchedulerUpdRequest;
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.SchService;

@DisallowConcurrentExecution
@Component
@Slf4j
public class BankReconSch implements Job {
    // private static final Logger logger =
    // LoggerFactory.getLogger(UpdateRICPWriteOff.class);

    @Value("${rms.application.callBackURL}")
    private String baseURL;

    @Autowired
    private SchedulerLogService schLogSvc;

    @Autowired
    private SchService schService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        int succUpdates = 0;
        int failUpdates = 0;
        String finalBaseURL = "";
        int apiIndex = 0;

        SchedulerUpdRequest schedulerUpdRequest = new SchedulerUpdRequest();
        schedulerUpdRequest.setI_function_nm("BankReconSch");
        if (schService.sp_getjobstatus(schedulerUpdRequest).equals('E')) {
            log.debug("BankReconDetail job has error. Exiting...");
            return;
        }

        try {
            SchedulerLog newLog = new SchedulerLog("Process Bank Reconcilation Doc.",
                    "This job is called from thread: " + Thread.currentThread().getName(),
                    succUpdates);
            newLog = schLogSvc.saveNewScheduleLog(newLog);

            if (newLog == null || newLog.equals(null))
                throw new IllegalArgumentException("The scheduler log failed to update!");

            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            HostnameVerifier allHostsValid = (hostname, session) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            apiIndex = baseURL.indexOf("/api");

            if (apiIndex != -1) {
                finalBaseURL = baseURL.substring(0, apiIndex);
            } else {
                log.error("Exception in " + this.getClass().toString(), "URL not found");
            }

            URL url = new URL(finalBaseURL + "/api/brsc/v1/bankReconSch");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                newLog.setFailTxn(failUpdates);
            } else {
                newLog.setSuccessTxn(succUpdates);
            }

            newLog.setDtModified(LocalDateTime.now());

            log.debug("Succ: " + Integer.toString(succUpdates) + " || Fail: " + Integer.toString(failUpdates));
            schLogSvc.saveNewScheduleLog(newLog);

            SchedulerUpdRequest schedulerUpdRequestSucc = new SchedulerUpdRequest();
            schedulerUpdRequestSucc.setI_function_nm("BankReconSch");
            schedulerUpdRequestSucc.setI_sch_status("C");
            schService.sp_updjobstatus(schedulerUpdRequestSucc);

            // Thread.sleep(30000);
        } catch (Exception e) {
            log.error("Exception in " + this.getClass().toString(), e);

            SchedulerUpdRequest schedulerUpdRequestErr = new SchedulerUpdRequest();
            schedulerUpdRequestErr.setI_function_nm("BankReconSch");
            schedulerUpdRequestErr.setI_sch_status("E");
            schService.sp_updjobstatus(schedulerUpdRequestErr);

            schService.sp_upderrorjobs(schedulerUpdRequestErr);

            throw new JobExecutionException(e, false); // Set refireImmediately to false
        }
    }
}
