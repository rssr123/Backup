package com.maven.rms.scheduler.jobs;

import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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

import com.maven.rms.models.SchedulerLog;
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.PGReconService;
import com.maven.rms.services.RIPLService;
import com.maven.rms.utils.RMSLogger;

@DisallowConcurrentExecution
@Component
@Slf4j
public class PGRecon implements Job{

    //private static final Logger logger = LoggerFactory.getLogger(PGRecon.class);

    @Value("${rms.application.callBackURL}")
	private String baseURL;

	@Autowired
	private SchedulerLogService schLogSvc;

    @Autowired
    private PGReconService pgReconService;

    @Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

        RMSLogger.schedulerInfo("PGRecon is Initializing...");
        //set variables
        Integer result=null;
        int apiIndex = 0;
        String finalBaseURL = "";

        List<BigInteger> rcPGTxnIds = null;

        SchedulerLog newLog = new SchedulerLog("PGRecon",
				"This job is called from thread: " + Thread.currentThread().getName(),
				1);
        try {
            newLog = schLogSvc.saveNewScheduleLog(newLog);

            if(newLog == null || newLog.equals(null))
                throw new IllegalArgumentException("The scheduler log failed to update!");

            //extract excel file and pg vs rms
            // rcPGTxnIds = pgReconService.sp_insPGTxn();

            // //rms vs pg and not found in this pg, check previous pg
            // result = pgReconService.sp_insMTTTxn(rcPGTxnIds);


            TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
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
            }
            else{
                log.error("Exception in " + this.getClass().toString(), "URL not found");
            }

            URL url = new URL(finalBaseURL + "/api/pgreconsc/v1/pgReconSch");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            
            int responseCode = connection.getResponseCode();

            if(responseCode != 200){
                newLog.setSuccessTxn(0);
                newLog.setFailTxn(1);
                log.debug("Succ: " + Integer.toString(0) + " || Fail: " + Integer.toString(1));
            }
            else{
                newLog.setSuccessTxn(1);
                newLog.setFailTxn(0);
                log.debug("Succ: " + Integer.toString(1) + " || Fail: " + Integer.toString(0));
            }

            newLog.setDtModified(LocalDateTime.now());
    
            schLogSvc.saveNewScheduleLog(newLog);
                   
        } catch (Exception e) {
            RMSLogger.schedulerError(e.getMessage().toString());
            log.error("Exception in " + this.getClass().toString(), e);
            // TODO: handle exception
        }

        
        
	}

    
}
