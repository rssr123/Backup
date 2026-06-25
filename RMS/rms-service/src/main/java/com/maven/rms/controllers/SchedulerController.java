package com.maven.rms.controllers;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.validation.Valid;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cc.cielo.authgen.TokenGenerator;
import com.maven.rms.models.Email;
import com.maven.rms.models.MFT;
import com.maven.rms.models.MFTWF;
import com.maven.rms.models.Param;
import com.maven.rms.scheduler.jobs.InvokeSendEmail;
import com.maven.rms.scheduler.jobs.MFTEffectiveUpdate;
import com.maven.rms.scheduler.jobs.UpdateRICPWriteOff;
import com.maven.rms.scheduler.services.PaymentClearanceService;
import com.maven.rms.services.EmailService;
import com.maven.rms.services.StoreProcedureService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.EGHLPostUtility;
import com.maven.rms.utils.ServerInetUtils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@RestController
@RequestMapping("/api/scheduler")
@Slf4j
public class SchedulerController {
	
	//private static final Logger logger = LoggerFactory.getLogger(SchedulerController.class);
		
    @Autowired
    private Scheduler scheduler;
	
	public SchedulerController () {}
	
	//@Secured("ROLE_REQUESTER")
	@PostMapping(value = "/info", 
	consumes = MediaType.APPLICATION_JSON_VALUE, 
	produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> checkScheduler() throws SchedulerException{
		return APIResponse.SuccessResponse(
				Collections.singletonMap("jobs", scheduler.getJobGroupNames().stream().map(group -> {
					try { return scheduler.getJobKeys(GroupMatcher.jobGroupEquals(group))
							.stream().map(key -> {try { return extracted(group, key);
								} catch (SchedulerException e) {log.error("Exception in " + this.getClass().toString(), e);}
								return null;});
					} catch (SchedulerException e) {log.error("Exception in " + this.getClass().toString(), e);}
					return null;
				}).collect(Collectors.toList())));
	}
	
	private Map<String, String> extracted(String group, JobKey key) throws SchedulerException{
		Map<String,String> jobInfo = new HashMap<String,String>();
		jobInfo.put("Job", key.getName());
		jobInfo.put("Group", group);
		List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(key);
		jobInfo.put("Trigger", triggers.get(0).getNextFireTime().toString());
		return jobInfo;
	}
	
	//@Secured("ROLE_REQUESTER")
	@PostMapping(value = "/resumePause", 
	consumes = MediaType.APPLICATION_JSON_VALUE, 
	produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> pauseOrResumeJob(@Valid @RequestBody Map<String, Object> payload) throws SchedulerException{
		if(!payload.containsKey("job_name") || !payload.containsKey("pause") || payload.size() != 2)
			return APIResponse.InternalServerError();
		if(!((payload.get("job_name") instanceof String) && (payload.get("pause") instanceof Boolean)))
			return APIResponse.InternalServerError();
		String isPaused = (boolean) payload.get("pause") ? " paused.":" resumed.";
		
		if((boolean) payload.get("pause"))
			scheduler.pauseJob(JobKey.jobKey((String) payload.get("job_name")));
		else
			scheduler.resumeJob(JobKey.jobKey((String) payload.get("job_name")));
		
		return APIResponse.SuccessResponse("Job " + payload.get("job_name") + isPaused);
	}
	
	//@Secured("ROLE_REQUESTER")
	@PostMapping(value = "/trigger", 
	consumes = MediaType.APPLICATION_JSON_VALUE, 
	produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> triggerJobNow(@Valid @RequestBody Map<String, Object> payload) throws SchedulerException{
		if(!payload.containsKey("job_name") || payload.size() != 1)
			return APIResponse.InternalServerError();
		if(!(payload.get("job_name") instanceof String))
			return APIResponse.InternalServerError();
		
		scheduler.triggerJob(JobKey.jobKey((String) payload.get("job_name")));
		
		return APIResponse.SuccessResponse("Job " + payload.get("job_name") + " is triggered.");
	}
	
	//@Secured("ROLE_REQUESTER")
	@PostMapping(value = "/recoverErrorState", 
	consumes = MediaType.APPLICATION_JSON_VALUE, 
	produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> recoverErrorState(@Valid @RequestBody Map<String, Object> payload) throws SchedulerException{
		if(!payload.containsKey("job_name") || payload.size() != 1)
			return APIResponse.InternalServerError();
		if(!(payload.get("job_name") instanceof String))
			return APIResponse.InternalServerError();
		
    	if(scheduler.getTriggerState(TriggerKey.triggerKey((String) payload.get("job_name"))).equals(Trigger.TriggerState.ERROR))
    		scheduler.resetTriggerFromErrorState(TriggerKey.triggerKey((String) payload.get("job_name")));
		
		return APIResponse.SuccessResponse("Job " + payload.get("job_name") + " has recovered.");
	}
	
	
	/*
	@RequestMapping("/start")
	public ResponseEntity<?> startScheduler() throws SchedulerException {
        System.out.println("isInStandbyMode() bf start(): " + Boolean.toString(scheduler.isInStandbyMode()));
        System.out.println("isStarted() bf start(): " + Boolean.toString(scheduler.isStarted()));
        
		if(!scheduler.isStarted() || scheduler.isInStandbyMode())
			scheduler.start();
		return ResponseEntity.ok("true");
	}
	
	@RequestMapping("/stop")
	public ResponseEntity<?> stopScheduler() throws SchedulerException {
        System.out.println("isInStandbyMode() bf start(): " + Boolean.toString(scheduler.isInStandbyMode()));
        System.out.println("isStarted() bf start(): " + Boolean.toString(scheduler.isStarted()));
        
		if(!scheduler.isInStandbyMode() || scheduler.isStarted())
			scheduler.standby();
		return ResponseEntity.ok("true");
	}
	
	@RequestMapping("/remove")
	public ResponseEntity<?> removeJob() throws SchedulerException {
        if(scheduler.checkExists(JobKey.jobKey("Qrtz_Job_Detail")))
        	scheduler.deleteJob(JobKey.jobKey("Qrtz_Job_Detail"));
        if(scheduler.checkExists(JobKey.jobKey("Payment_Clearance_Pending_Payment")))
        	scheduler.deleteJob(JobKey.jobKey("Payment_Clearance_Pending_Payment"));
        if(scheduler.checkExists(JobKey.jobKey("Payment_Clearance_Expired")))
        	scheduler.deleteJob(JobKey.jobKey("Payment_Clearance_Expired"));
		return ResponseEntity.ok("true");
	}
	
	
	//add new scheduler for the PaymentClearance job runs once, after 10 seconds called
	@RequestMapping("/add")
	public ResponseEntity<?> addJob() throws SchedulerException {
        if(scheduler.checkExists(JobKey.jobKey("Qrtz_Job_Detail")))
        	scheduler.deleteJob(JobKey.jobKey("Qrtz_Job_Detail"));
        
        if(!scheduler.checkExists(JobKey.jobKey("Qrtz_Job_Detail"))) {
        	JobDetail job = newJob().ofType(PaymentClearance.class)
            		.storeDurably().withIdentity(JobKey.jobKey("Qrtz_Job_Detail"))
            		.usingJobData("isNotExpiredMTTPGList", false)
            		.withDescription("Invoke Sample Job service...").build();
        	Trigger trigger = newTrigger().forJob(job)
            		.withIdentity(TriggerKey.triggerKey("Qrtz_Trigger"))
            		.withDescription("Sample trigger")
     */
            		//.withSchedule(CronScheduleBuilder.cronSchedule("0 */1 * ? * *")).build();
     /*       		.withSchedule(simpleSchedule().withIntervalInSeconds(10).repeatSecondlyForTotalCount(1)).build();
            		
            		//.repeatForever()).build();
        	scheduler.scheduleJob(job, trigger);
        	
        }
		return ResponseEntity.ok("true");
	}
	
	//Used to add rms_mtt_pg records
	@RequestMapping("addpg")
	public ResponseEntity<?> addPG() {
		int[] listOfInitStatuses = IntStream.generate(() -> (new Random().nextInt(2)) + 1)
											.limit(20).toArray();
		int k = 3;
		String pgPymtId = "VICKYTANROY2309220";	//"VICKYTANROY23092203";
		
		for(int i : listOfInitStatuses)
			pcService.newDemoMTTPG(i, pgPymtId + Integer.toString(k++));
		
		return ResponseEntity.ok("true");
	}
	
	//Calls eghl api and returns the string body the api gives
	@RequestMapping("callAPI")
	public ResponseEntity<?> callPostTest() throws IOException{

		Response response = new EGHLPostUtility().eGHLPaymentAPI("application/x-www-form-urlencoded", "QUERY"
				, "ANY", "sit", "VICKYTANROY23092203", "1.00", "MYR");
		String data = java.net.URLDecoder.decode(response.body().string(), StandardCharsets.UTF_8.name());

		System.out.println("With toString: " + data);
		response.close();
		return ResponseEntity.ok(data);
	}
	
	@RequestMapping("email")
	public ResponseEntity<?> callEmail() throws IOException{
		String body = "Entity Name: Influitive Sdn. Bhd."
				+ "\nReceipt No: OR20230901656323"
				+ "\nOrder Reference No.: ORN2023090183467"
				+ "\nTotal Amount Paid: RM169.60"
				+ "\n\nDear Sir/Madam,\nWe are pleased to inform you that your "
				+ "online payment has been successfully processed. An official payment receipt "
				+ "has been generated for your records. Please find the attached receipt for "
				+ "your reference.\nThank you for using our services.\n\nTuan/Puan,\n"
				+ "Dengan hormatnya,\nKami berbesar hati ingin memaklumkan bahawa pembayaran "
				+ "dalam talian anda telah berjaya diproses. Bersama-sama ini disertakan resit "
				+ "pembayaran untuk perhatian selanjutnya pihak Tuan/Puan.\r\nTerima kasih kerana"
				+ " menggunakan perkhidmatan kami.\n\n\n[THIS IS AN AUTOMATED MESSAGE - PLEASE "
				+ "DO NOT REPLY DIRECTLY TO THIS EMAIL] \n";
		
		//emailer.sendMailWithAttachment("brianho@persys-tech.com", "", "", "RMS SSM Receipt"
				//, body, new File("C:\\RMS-Receipts\\SSM-Receipt-OR20231103000001.pdf"), false);
		
		emailer.saveEmailWithAttDets(new EmailWithAttachment(
				new Email("Receipt", "brianho@persys-tech.com", "", "", "RMS SSM Receipt", body)
					, new File("C:\\RMS-Receipts\\SSM-Receipt-OR20231103000001.pdf")));
		
		return ResponseEntity.ok("OK");
	}
	
	@RequestMapping("sendEmail")
	public ResponseEntity<?> sendEmail() throws SchedulerException {
        if(scheduler.checkExists(JobKey.jobKey("Qrtz_Job_Detail")))
        	scheduler.deleteJob(JobKey.jobKey("Qrtz_Job_Detail"));
        
        if(!scheduler.checkExists(JobKey.jobKey("Qrtz_Job_Detail"))) {
        	JobDetail job = newJob().ofType(InvokeSendEmail.class)
            		.storeDurably().withIdentity(JobKey.jobKey("Qrtz_Job_Detail"))
            		.withDescription("Invoke Sample Job service...").build();
        	Trigger trigger = newTrigger().forJob(job)
            		.withIdentity(TriggerKey.triggerKey("Qrtz_Trigger"))
            		.withDescription("Sample trigger")
    */
            		//.withSchedule(CronScheduleBuilder.cronSchedule("0 */1 * ? * *")).build();
	/*
            		.withSchedule(simpleSchedule().withIntervalInSeconds(10).repeatSecondlyForTotalCount(1)).build();

            		//.repeatForever()).build();
        	scheduler.scheduleJob(job, trigger);
        }

		return ResponseEntity.ok("OK");
	}
	*/
}
