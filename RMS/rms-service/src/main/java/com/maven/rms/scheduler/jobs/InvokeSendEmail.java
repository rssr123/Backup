package com.maven.rms.scheduler.jobs;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

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
import com.maven.rms.models.SchedulerLog;
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.EmailService;
import com.maven.rms.services.MTTService;
import com.maven.rms.utils.RMSLogger;

import org.apache.commons.collections4.CollectionUtils;

@DisallowConcurrentExecution
@Component
@Slf4j
public class InvokeSendEmail implements Job {

	@Autowired
	private EmailService emailService;
	@Autowired
	private SchedulerLogService schLogSvc;
	@Autowired
	private MTTService mttSvc;

	private String SchedulerName = "InvokeSendEmail";

	@Value("${ssm4u.authgen.username}")
	private String authGenUsername;
	@Value("${ssm4u.authgen.password}")
	private String authGenPassword;
	@Value("${ssm4u.authgen.notify.user.url}")
	private String ssm4uNotifyUserUrl;

	private String prefixSystemMsg = "[Scheduler Exception:InvokeSendEmail] ";

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		RMSLogger.schedulerInfo(schLogSvc, SchedulerName, "Scheduler initialization started.", 0);

		List<Email> toSend = emailService.getListOfEmailsToSendByStatus("P");
		// int succSent = toSend.size();
		int succSent = CollectionUtils.size(toSend);
		int failSent = 0;

		String emailTo = "";

		SchedulerLog newLog = new SchedulerLog("Sending out emails...",
				"This job is called from thread: " + Thread.currentThread().getName(),
				succSent);

		newLog = schLogSvc.saveNewScheduleLog(newLog);

		if (newLog == null || newLog.equals(null))
			throw new IllegalArgumentException("The scheduler log failed to update!");

		for (Email email : toSend) {
			emailTo = Optional.ofNullable(email.getTo())
					.filter(s -> !s.trim().isEmpty())
					.orElse("N/A");

			try {
				if (email.getAttachmentPath() == null || email.getAttachmentPath().isEmpty()) {
					if (email.getEmailType().equals("Notification") || email.getEmailType().equals("Report")) {
						emailService.sendMailHTML(email);
					} else {
						emailService.sendMailHTML(email);
					}
				}
				// else
				// emailService.sendMail(email);
				else {
					/*
					EmailWithAttachment attachment = emailService.getAttachment(email.getEmailId()).orElse(null);
					if (attachment == null)
						throw new NullPointerException("NullPointerException found for local variable attachment.");
					attachment.setEmail(email); // Negate lazy loading issue by loading the email from parent
					// Vicky 20240716: change isHTML to true for attachment
					*/
					RMSLogger.schedulerInfo(schLogSvc, SchedulerName, "System is trying send to " + emailTo, 0);
					emailService.sendMailWithAttachment(email, true);
				}
				email.setStatus("S");
				RMSLogger.schedulerInfo(schLogSvc, SchedulerName,
						emailTo + " is sent successfully.", 0);
				// P=Pending, S=Sent, F=Failed, D=Deleted
			} catch (Exception e) {
				//email.setStatus("F");
				emailTo = Optional.ofNullable(email.getTo())
						.filter(s -> !s.trim().isEmpty())
						.orElse("N/A");

				String errorDetails = Optional.ofNullable(e.getMessage())
						.filter(msg -> !msg.trim().isEmpty())
						.orElse("No message. Exception type: " + e.getClass().getName());

				String ErrorMsg = emailTo + " is failed to send. hit error > " + errorDetails;

				RMSLogger.schedulerError(schLogSvc, SchedulerName,
						ErrorMsg, 0);

				log.error(prefixSystemMsg + ErrorMsg, e);
				email.setRetryCnt(email.getRetryCnt() + 1);
				if (email.getRetryCnt() > 2)
					email.setStatus("F");
				failSent += 1;
			} finally {
				email.setDtModified(LocalDateTime.now());
				emailService.saveEmailDets(email);

				if ("S".equals(email.getStatus()) && email.getRef_info() != null && !email.getRef_info().isEmpty()) {
					String ref = email.getRef_info();
					if ((ref.length() >= 3 && ref.substring(0, 3).contains("BIL")) ||
							(ref.length() >= 2 && ref.substring(0, 2).contains("NB"))) {

						RMSLogger.schedulerInfo(schLogSvc, SchedulerName,
								ref + " in MTT table will update to ES order status", 0);
						mttSvc.sp_updatemttordstatus(ref, "ES", "system");
					}
				}

				// Send notification via API here. Email table needs username to reference to
				// which user it is sent to.
				/*RMSUser user = uamSvc.returnUserRepo().findRMSUserByEmail(email.getTo()).orElse(null);

				if (user == null) {
					String userErrorMessage = "Cannot find this email in RMSUser " + email.getTo() + " in "
							+ this.getClass().toString();
					RMSLogger.schedulerError(schLogSvc, SchedulerName, userErrorMessage, 0);

					log.error(prefixSystemMsg + userErrorMessage);
					continue;
				}*/
			}
		}

		succSent -= failSent;
		newLog.setSuccessTxn(succSent);
		newLog.setFailTxn(failSent);
		newLog.setDtModified(LocalDateTime.now());
		int totalRecord = toSend.size();

		String FinalResult = "Total Email Required To Send: " + totalRecord + " || Succ: "
				+ Optional.ofNullable(succSent).orElse(0)
				+ " || Fail: " + Optional.ofNullable(failSent).orElse(0);

		RMSLogger.schedulerInfo(schLogSvc, SchedulerName, FinalResult, 0);

		log.debug(FinalResult);
		schLogSvc.saveNewScheduleLog(newLog);
	}
}
