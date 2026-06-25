// package com.maven.rms.scheduler;
// import static org.quartz.JobBuilder.newJob;
// import static org.quartz.TriggerBuilder.newTrigger;

// import java.io.IOException;

// import javax.annotation.PostConstruct;

// //import org.quartz.*;
// // import org.slf4j.Logger;
// // import org.slf4j.LoggerFactory;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.beans.factory.config.PropertiesFactoryBean;
// import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
// import org.springframework.context.ApplicationContext;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.core.io.ClassPathResource;
// import org.springframework.scheduling.quartz.SchedulerFactoryBean;
// import org.springframework.scheduling.quartz.SpringBeanJobFactory;
// import org.quartz.CronExpression;
// import org.quartz.CronScheduleBuilder;
// import org.quartz.CronTrigger;
// import org.quartz.JobDetail;
// import org.quartz.JobKey;
// import org.quartz.Scheduler;
// import org.quartz.SchedulerException;
// import org.quartz.Trigger;
// import org.quartz.TriggerKey;

// import java.util.Properties;
// import com.maven.rms.config.AutoWiringSpringBeanJobFactory;
// import com.maven.rms.scheduler.jobs.InvokeSendEmail;
// import com.maven.rms.scheduler.jobs.PaymentClearance;
// import com.maven.rms.scheduler.jobs.PaymentClearanceExpired;
// import com.maven.rms.scheduler.jobs.RICPAging;
// import com.maven.rms.scheduler.jobs.RIPLAging;
// import com.maven.rms.scheduler.jobs.RIPLImpairCheck;
// import com.maven.rms.scheduler.jobs.UnmatchAging;
// import com.maven.rms.utils.RMSLogger;
// import com.maven.rms.scheduler.jobs.MFTEffectiveUpdate;
// import com.maven.rms.scheduler.jobs.PGRecon;
// import com.maven.rms.scheduler.jobs.UpdateRICPWriteOff;
// import com.maven.rms.scheduler.jobs.DeferredIncome;
// import com.maven.rms.scheduler.jobs.FMSARI;
// import com.maven.rms.scheduler.jobs.FMSARR;
// import com.maven.rms.scheduler.jobs.FMSCreditMemo;
// import com.maven.rms.scheduler.jobs.FMSDebitMemo;
// import com.maven.rms.scheduler.jobs.FMSJournalDI;
// import com.maven.rms.scheduler.jobs.FMSJournalRICP;
// import com.maven.rms.scheduler.jobs.FMSJournalRIPL;
// import com.maven.rms.scheduler.jobs.BankReconSch;
// import com.maven.rms.scheduler.jobs.DIAging;
// import com.maven.rms.scheduler.jobs.ReUploadReceipt;
// import com.maven.rms.scheduler.jobs.BankReconDetail;

// @Configuration
// @ConditionalOnExpression("'${using.spring.schedulerFactory}'=='false'")
// @Slf4j
// public class QrtzScheduler {

// 	//private static final Logger logger = LoggerFactory.getLogger(QrtzScheduler.class);
	
// 	@Value("${quartz.enable.tasks}")
// 	private boolean enableQuartz;
	
//     @Value("${paymentClearance.pending-payment.schedule.cron}")
//     private String paymentClearancePendingPaymentCron;    

//     @Value("${paymentClearance.expired.schedule.cron}")
//     private String paymentClearanceExpiredCron;

//     @Value("${invoke.send.email.schedule.cron}")
//     private String emailInvokerCron;

// 	@Value("${ripl.schedule.cron}")
//     private String RIPLCron;

// 	// @Value("${ripl.writeoff.schedule.cron}")
//     // private String RIPLWriteOffCron;
  
//     @Value("${update.mftwf.approvals.to.effective}")
//     private String updateMftwfApprovalsCron;

//     @Value("${update.ricp.collectable.to.writeoff}")
//     private String updateRICPWriteOffCron;
    
// 	@Value("${update.di.recognized.to.amortized}")
//     private String updateDIAmortizedCron;

// 	@Value("${update.bank.recon.sch}")
// 	private String updateBankReconSchCron;

// 	@Value("${update.bank.recon.detail}")
// 	private String updateBankReconDetailCron;
	
// 	@Value("${pgrecon.schedule.cron}")
//     private String PGReconCron;

// 	@Value("${update.fms.ari.sch}")
//     private String updateFMSARISchCron;

// 	@Value("${update.fms.arr.sch}")
//     private String updateFMSARRSchCron;

// 	@Value("${update.fms.debit.memo.sch}")
//     private String updateFMSDebitMemoSchCron;

// 	@Value("${update.fms.credit.memo.sch}")
//     private String updateFMSCreditMemoSchCron;

// 	@Value("${update.fms.journal.di.sch}")
//     private String updateFMSJournalDISchCron;

// 	@Value("${update.fms.journal.ripl.sch}")
//     private String updateFMSJournalRIPLSchCron;

// 	@Value("${update.fms.journal.ricp.sch}")
//     private String updateFMSJournalRICPSchCron;

// 	@Value("${update.unmatch.aging.sch}")
//     private String updateUnmatchAgingSchCron;

// 	@Value("${update.di.aging.sch}")
//     private String updateDIAgingSchCron;

// 	@Value("${update.ripl.aging.sch}")
//     private String updateRIPLAgingSchCron;

// 	@Value("${update.ricp.aging.sch}")
//     private String updateRICPAgingSchCron;

// 	@Value("${update.reupload.receipt.sch}")
//     private String updateReUploadReceiptSchCron;

// 	@Value("${r1b1.control}")
//     private Boolean r1b1Control;

//     @Autowired
//     private ApplicationContext applicationContext;
    
//     @PostConstruct
//     public void init() {
//     	if(!CronExpression.isValidExpression(paymentClearancePendingPaymentCron)) {
//     		log.error("Payment Clearance - Pending Payment CRON: '" + paymentClearancePendingPaymentCron 
//     				+ "' is an invalid expression (org.quartz.ConExpression). Defaulting to '0 */15 * ? * *'"
//     				+ " (Every 15th minute)");
//     		paymentClearancePendingPaymentCron = "0 */15 * ? * *";
//     	}
//     	if(!CronExpression.isValidExpression(paymentClearanceExpiredCron)) {
//     		log.error("Payment Clearance - Expired CRON: '" + paymentClearanceExpiredCron 
//     				+ "' is an invalid expression (org.quartz.ConExpression). Defaulting to '0 * * ? * *'"
//     				+ " (Every hour(0th minute))");
//     		paymentClearanceExpiredCron = "0 0 * ? * *";
//     	}
//     	if(!CronExpression.isValidExpression(emailInvokerCron)) {
//     		log.error("Invoke sending emails CRON: '" + emailInvokerCron 
//     				+ "' is an invalid expression (org.quartz.ConExpression). Defaulting to '0 */15 * ? * *'"
//     				+ " (Every 15th minute)");
//     		emailInvokerCron = "0 */15 * ? * *";
			

//     	}
// 		if(!CronExpression.isValidExpression(RIPLCron)) {
// 			log.error("RIPL CRON: '" + RIPLCron 
//     				+ "' is an invalid expression (org.quartz.ConExpression). Defaulting to '1 0 * * *"
//     				+ " (Every 12:01 AM)");
//     		//RIPLImpairCron = "1 0 * ? * *";//Every 12:01 AM
// 			RIPLCron = "0 1 * ? * * *";
//     	}
// 		// if(!CronExpression.isValidExpression(RIPLWriteOffCron)) {
//     	// 	logger.error("RIPL WriteOff CRON: '" + RIPLWriteOffCron 
//     	// 			+ "' is an invalid expression (org.quartz.ConExpression). Defaulting to '1 0 * * *'"
//     	// 			+ " (Every 12:01 AM)");
//     	// 	//RIPLWriteOffCron = "1 0 * ? * *"; //Every 12:01 AM
// 		// 	RIPLImpairCron = "0 5 * ? * * *";
//     	// }

//     	if(!CronExpression.isValidExpression(updateMftwfApprovalsCron)) {
//     		log.error("Update MFTWF Approvals CRON: '" + updateMftwfApprovalsCron 
//     				+ "' is an invalid expression (org.quartz.ConExpression). Defaulting to '0 0 0 ? * *'"
//     				+ " (Every day at 12AM)");
//     		updateMftwfApprovalsCron = "0 0 0 ? * *";
//     	}
//     	if(!CronExpression.isValidExpression(updateRICPWriteOffCron)) {
//     		log.error("Update RICP WriteOffs CRON: '" + updateRICPWriteOffCron 
//     				+ "' is an invalid expression (org.quartz.ConExpression). Defaulting to '0 0 0 ? * *'"
//     				+ " (Every day at 12AM)");
//     		updateRICPWriteOffCron = "0 0 0 ? * *";
//     	}
// 		if(!CronExpression.isValidExpression(PGReconCron)) {
// 			log.error("PGRecon CRON: '" + PGReconCron 
//     				+ "' is an invalid expression (org.quartz.ConExpression). Defaulting to '1 0 * * *"
//     				+ " (Every 12:01 AM)");
//     		//RIPLImpairCron = "1 0 * ? * *";//Every 12:01 AM
// 			PGReconCron = "0 1 * ? * *";
//     	}

// 		if(!CronExpression.isValidExpression(updateFMSARISchCron)) {
// 			log.error("Update FMS ARI CRON: '" + updateFMSARISchCron 
//     				+ "' is an invalid expression (org.quartz.ConExpression). Defaulting to '0 0 0 ? * *'"
//     				+ " (Every day at 12AM)");
// 					updateFMSARISchCron = "0 0 0 ? * *";
//     	}
// 		if(!CronExpression.isValidExpression(updateFMSARRSchCron)) {
// 			log.error("Update FMS ARR CRON: '" + updateFMSARRSchCron 
//     				+ "' is an invalid expression (org.quartz.ConExpression). Defaulting to '0 */2 * ? * *'"
//     				+ " (Every day at 12AM)");
// 					updateFMSARRSchCron = "0 */2 * ? * *";
//     	}
// 		if(!CronExpression.isValidExpression(updateFMSDebitMemoSchCron)) {
// 			log.error("Update FMS Debit Memo CRON: '" + updateFMSDebitMemoSchCron 
//     				+ "' is an invalid expression (org.quartz.ConExpression). Defaulting to '0 0 0 ? * *'"
//     				+ " (Every day at 12AM)");
// 					updateFMSDebitMemoSchCron = "0 0 0 ? * *";
//     	}
// 		if(!CronExpression.isValidExpression(updateFMSCreditMemoSchCron)) {
// 			log.error("Update FMS Credit Memo CRON: '" + updateFMSCreditMemoSchCron 
//     				+ "' is an invalid expression (org.quartz.ConExpression). Defaulting to '0 0 0 ? * *'"
//     				+ " (Every day at 12AM)");
// 					updateFMSCreditMemoSchCron = "0 0 0 ? * *";
//     	}
// 		if(!CronExpression.isValidExpression(updateFMSJournalDISchCron)) {
// 			log.error("Update FMS Journal DI CRON: '" + updateFMSJournalDISchCron 
//     				+ "' is an invalid expression (org.quartz.ConExpression). Defaulting to '0 0 0 ? * *'"
//     				+ " (Every day at 12AM)");
// 					updateFMSJournalDISchCron = "0 0 0 ? * *";
//     	}
// 		if(!CronExpression.isValidExpression(updateFMSJournalRIPLSchCron)) {
// 			log.error("Update FMS Journal RIPL CRON: '" + updateFMSJournalRIPLSchCron 
//     				+ "' is an invalid expression (org.quartz.ConExpression). Defaulting to '0 0 0 ? * *'"
//     				+ " (Every day at 12AM)");
// 					updateFMSJournalRIPLSchCron = "0 0 0 ? * *";
//     	}
// 		if(!CronExpression.isValidExpression(updateFMSJournalRICPSchCron)) {
// 			log.error("Update FMS Journal RICP CRON: '" + updateFMSJournalRICPSchCron 
//     				+ "' is an invalid expression (org.quartz.ConExpression). Defaulting to '0 0 0 ? * *'"
//     				+ " (Every day at 12AM)");
// 					updateFMSJournalRICPSchCron = "0 0 0 ? * *";
//     	}
// 		if(!CronExpression.isValidExpression(updateUnmatchAgingSchCron)) {
// 			log.error("Update Unmatch Aging CRON: '" + updateUnmatchAgingSchCron 
//     				+ "' is an invalid expression (org.quartz.ConExpression). Defaulting to '0 0 0 ? * *'"
//     				+ " (Every day at 12AM)");
// 					updateUnmatchAgingSchCron = "0 0 0 ? * *";
//     	}
// 		if(!CronExpression.isValidExpression(updateDIAgingSchCron)) {
// 			log.error("Update DI Aging CRON: '" + updateDIAgingSchCron 
//     				+ "' is an invalid expression (org.quartz.ConExpression). Defaulting to '0 */2 * ? * *'"
//     				+ " (Every day at 12AM)");
// 					updateDIAgingSchCron = "0 */2 * ? * *";
//     	}
// 		if(!CronExpression.isValidExpression(updateRIPLAgingSchCron)) {
// 			log.error("Update RIPL Aging CRON: '" + updateRIPLAgingSchCron 
//     				+ "' is an invalid expression (org.quartz.ConExpression). Defaulting to '0 */2 * ? * *'"
//     				+ " (Every day at 12AM)");
// 					updateRIPLAgingSchCron = "0 */2 * ? * *";
//     	}
// 		if(!CronExpression.isValidExpression(updateRICPAgingSchCron)) {
// 			log.error("Update RICP Aging CRON: '" + updateRICPAgingSchCron 
//     				+ "' is an invalid expression (org.quartz.ConExpression). Defaulting to '0 0 0 ? * *'"
//     				+ " (Every day at 12AM)");
// 					updateRICPAgingSchCron = "0 0 0 ? * *";
//     	}
// 		if(!CronExpression.isValidExpression(updateReUploadReceiptSchCron)) {
// 			log.error("Update ReUpload Receipt CRON: '" + updateReUploadReceiptSchCron 
//     				+ "' is an invalid expression (org.quartz.ConExpression). Defaulting to '0 */15 * ? * *'"
//     				+ " (Every day at 12AM)");
// 					updateReUploadReceiptSchCron = "0 */15 * ? * *";
//     	}
//     }
    
//     //Add jobs here:
//     @Bean
//     public Scheduler scheduler(SchedulerFactoryBean factory) throws SchedulerException {
//         log.debug("Getting a handle to the Scheduler");
//         Scheduler scheduler = factory.getScheduler();

//         //Job #1
//         if(!scheduler.checkExists(JobKey.jobKey("Payment_Clearance_Pending_Payment"))) {
//         	JobDetail job = newJob().ofType(PaymentClearance.class)
//             		.storeDurably().withIdentity(JobKey.jobKey("Payment_Clearance_Pending_Payment"))
//             		.usingJobData("isNotExpiredMTTPGList", true)
//             		.withDescription("Query eGHL for status of records that are 'Pending Payment'").build();
//         	Trigger trigger = newTrigger().forJob(job)
//             		.withIdentity(TriggerKey.triggerKey("Payment_Clearance_Pending_Payment"))
//             		.withDescription("Trigger for Payment Clearance Pending Payment")
//             		.withSchedule(CronScheduleBuilder.cronSchedule(paymentClearancePendingPaymentCron)
//             				.withMisfireHandlingInstructionDoNothing()).build();
//         	scheduler.scheduleJob(job, trigger);
//         	RMSLogger.schedulerInfo("Created job: Payment_Clearance_Pending_Payment");
//         }
//         else{
//         	CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey("Payment_Clearance_Pending_Payment"));
//         	if(!paymentClearancePendingPaymentCron.equals(oldTrigger.getCronExpression())) {
//             	Trigger trigger = newTrigger().forJob(scheduler.getJobDetail(JobKey.jobKey("Payment_Clearance_Pending_Payment")))
//                 		.withIdentity(TriggerKey.triggerKey("Payment_Clearance_Pending_Payment"))
//                 		.withDescription("Trigger for Payment Clearance Pending Payment")
//                 		.withSchedule(CronScheduleBuilder.cronSchedule(paymentClearancePendingPaymentCron)
//                 				.withMisfireHandlingInstructionDoNothing()).build();
//         		scheduler.rescheduleJob(TriggerKey.triggerKey("Payment_Clearance_Pending_Payment"), trigger);
//             	RMSLogger.schedulerInfo("Updated CRON for job: Payment_Clearance_Pending_Payment");
//         	}
//         	if(scheduler.getTriggerState(TriggerKey.triggerKey("Payment_Clearance_Pending_Payment")).equals(Trigger.TriggerState.ERROR))
//         		scheduler.resetTriggerFromErrorState(TriggerKey.triggerKey("Payment_Clearance_Pending_Payment"));
//         }
        
//         //Job #2
//         if(!scheduler.checkExists(JobKey.jobKey("Payment_Clearance_Expired"))) {
//         	JobDetail job = newJob().ofType(PaymentClearanceExpired.class)
//             		.storeDurably().withIdentity(JobKey.jobKey("Payment_Clearance_Expired"))
//             		.usingJobData("isNotExpiredMTTPGList", false)
//             		.withDescription("Query eGHL for status of records that are 'Expired'").build();
//         	Trigger trigger = newTrigger().forJob(job)
//             		.withIdentity(TriggerKey.triggerKey("Payment_Clearance_Expired"))
//             		.withDescription("Trigger for Payment Clearance Expired")
//             		.withSchedule(CronScheduleBuilder.cronSchedule(paymentClearanceExpiredCron)
//             				.withMisfireHandlingInstructionFireAndProceed()).build();
//         	scheduler.scheduleJob(job, trigger);
//         	RMSLogger.schedulerInfo("Created job: Payment_Clearance_Expired");
//         }
//         else{
//         	CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey("Payment_Clearance_Expired"));
//         	if(!paymentClearanceExpiredCron.equals(oldTrigger.getCronExpression())) {
//             	Trigger trigger = newTrigger().forJob(scheduler.getJobDetail(JobKey.jobKey("Payment_Clearance_Expired")))
//                 		.withIdentity(TriggerKey.triggerKey("Payment_Clearance_Expired"))
//                 		.withDescription("Trigger for Payment Clearance Expired")
//                 		.withSchedule(CronScheduleBuilder.cronSchedule(paymentClearanceExpiredCron)
//                 				.withMisfireHandlingInstructionFireAndProceed()).build();
//         		scheduler.rescheduleJob(TriggerKey.triggerKey("Payment_Clearance_Expired"), trigger);
//             	RMSLogger.schedulerInfo("Updated CRON for job: Payment_Clearance_Expired");
//         	}
//         	if(scheduler.getTriggerState(TriggerKey.triggerKey("Payment_Clearance_Expired")).equals(Trigger.TriggerState.ERROR))
//         		scheduler.resetTriggerFromErrorState(TriggerKey.triggerKey("Payment_Clearance_Expired"));
//         }

//         //Job #3
//         if(!scheduler.checkExists(JobKey.jobKey("Email_Sender"))) {
//         	JobDetail job = newJob().ofType(InvokeSendEmail.class)
//             		.storeDurably().withIdentity(JobKey.jobKey("Email_Sender"))
//             		.withDescription("To push email out every 15th minute.").build();
//         	Trigger trigger = newTrigger().forJob(job)
//             		.withIdentity(TriggerKey.triggerKey("Email_Sender"))
//             		.withDescription("Trigger for sending emails.")
//             		.withSchedule(CronScheduleBuilder.cronSchedule(emailInvokerCron)
//             				.withMisfireHandlingInstructionDoNothing()).build();
//         	scheduler.scheduleJob(job, trigger);
//         	RMSLogger.schedulerInfo("Created job: Email_Sender");
//         }
//         else{
//         	CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey("Email_Sender"));
//         	if(!paymentClearancePendingPaymentCron.equals(oldTrigger.getCronExpression())) {
//             	Trigger trigger = newTrigger().forJob(scheduler.getJobDetail(JobKey.jobKey("Email_Sender")))
//                 		.withIdentity(TriggerKey.triggerKey("Email_Sender"))
//                 		.withDescription("Trigger for sending emails.")
//                 		.withSchedule(CronScheduleBuilder.cronSchedule(emailInvokerCron)
//                 				.withMisfireHandlingInstructionDoNothing()).build();
//         		scheduler.rescheduleJob(TriggerKey.triggerKey("Email_Sender"), trigger);
//             	RMSLogger.schedulerInfo("Updated CRON for job: Email_Sender");
//         	}
//         }

// 		//Job RIPL
//         if(!scheduler.checkExists(JobKey.jobKey("RIPL"))) {
//         	JobDetail job = newJob().ofType(RIPLImpairCheck.class)
//             		.storeDurably().withIdentity(JobKey.jobKey("RIPL"))
//             		.withDescription("Update status to 'Impair' or 'WriteOff' if met target date").build();
//         	Trigger trigger = newTrigger().forJob(job)
//             		.withIdentity(TriggerKey.triggerKey("RIPL"))
//             		.withDescription("Trigger for RIPL Check")
//             		.withSchedule(CronScheduleBuilder.cronSchedule(RIPLCron)
//             				.withMisfireHandlingInstructionFireAndProceed()).build();
//         	scheduler.scheduleJob(job, trigger);
//         	RMSLogger.schedulerInfo("Created job: RIPL");
//         }
//         else{
//         	CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey("RIPL"));
//         	if(!RIPLCron.equals(oldTrigger.getCronExpression())) {
//             	Trigger trigger = newTrigger().forJob(scheduler.getJobDetail(JobKey.jobKey("RIPL")))
//                 		.withIdentity(TriggerKey.triggerKey("RIPL"))
//                 		.withDescription("Trigger for RIPL Cron")
//                 		.withSchedule(CronScheduleBuilder.cronSchedule(RIPLCron)
//                 				.withMisfireHandlingInstructionFireAndProceed()).build();
//         		scheduler.rescheduleJob(TriggerKey.triggerKey("RIPL"), trigger);
//             	RMSLogger.schedulerInfo("Updated CRON for job: RIPL");
//         	}
//         	if(scheduler.getTriggerState(TriggerKey.triggerKey("RIPL")).equals(Trigger.TriggerState.ERROR))
//         		scheduler.resetTriggerFromErrorState(TriggerKey.triggerKey("RIPL"));
//         }

// 		// //Job RIPL WriteOff
//         // if(!scheduler.checkExists(JobKey.jobKey("RIPL_WriteOff"))) {
//         // 	JobDetail job = newJob().ofType(RIPLWriteOffCheck.class)
//         //     		.storeDurably().withIdentity(JobKey.jobKey("RIPL_WriteOff"))
//         //     		.withDescription("Update status to 'WriteOff' if met WriteOff date").build();
//         // 	Trigger trigger = newTrigger().forJob(job)
//         //     		.withIdentity(TriggerKey.triggerKey("RIPL_WriteOff"))
//         //     		.withDescription("Trigger for RIPL WriteOff Check")
//         //     		.withSchedule(CronScheduleBuilder.cronSchedule(RIPLWriteOffCron)
//         //     				.withMisfireHandlingInstructionFireAndProceed()).build();
//         // 	scheduler.scheduleJob(job, trigger);
//         // 	RMSLogger.schedulerInfo("Created job: RIPL_WriteOff");
//         // }
//         // else{
//         // 	CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey("RIPL_WriteOff"));
//         // 	if(!RIPLWriteOffCron.equals(oldTrigger.getCronExpression())) {
//         //     	Trigger trigger = newTrigger().forJob(scheduler.getJobDetail(JobKey.jobKey("RIPL_WriteOff")))
//         //         		.withIdentity(TriggerKey.triggerKey("RIPL_WriteOff"))
//         //         		.withDescription("Trigger for RIPL WriteOff Cron")
//         //         		.withSchedule(CronScheduleBuilder.cronSchedule(RIPLWriteOffCron)
//         //         				.withMisfireHandlingInstructionFireAndProceed()).build();
//         // 		scheduler.rescheduleJob(TriggerKey.triggerKey("RIPL_WriteOff"), trigger);
//         //     	RMSLogger.schedulerInfo("Updated CRON for job: RIPL_WriteOff");
//         // 	}
//         // }

		
        
//         //Job #4
//         if(!scheduler.checkExists(JobKey.jobKey("MFT_Approval"))) {
//         	JobDetail job = newJob().ofType(MFTEffectiveUpdate.class)
//             		.storeDurably().withIdentity(JobKey.jobKey("MFT_Approval"))
//             		.withDescription("Update MFTWF approvals every midnight").build();
//         	Trigger trigger = newTrigger().forJob(job)
//             		.withIdentity(TriggerKey.triggerKey("MFT_Approval"))
//             		.withDescription("Trigger for MFTWF Approvals.")
//             		.withSchedule(CronScheduleBuilder.cronSchedule(updateMftwfApprovalsCron)
//             				.withMisfireHandlingInstructionFireAndProceed()).build();
//         	scheduler.scheduleJob(job, trigger);
//         	log.info("Created job: MFT_Approval");
//         }
//         else{
//         	CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey("MFT_Approval"));
//         	if(!updateMftwfApprovalsCron.equals(oldTrigger.getCronExpression())) {
//             	Trigger trigger = newTrigger().forJob(scheduler.getJobDetail(JobKey.jobKey("MFT_Approval")))
//                 		.withIdentity(TriggerKey.triggerKey("MFT_Approval"))
//                 		.withDescription("Trigger for MFTWF Approvals.")
//                 		.withSchedule(CronScheduleBuilder.cronSchedule(updateMftwfApprovalsCron)
//                 				.withMisfireHandlingInstructionFireAndProceed()).build();
//         		scheduler.rescheduleJob(TriggerKey.triggerKey("MFT_Approval"), trigger);
//             	log.info("Updated CRON for job: MFT_Approval");
//         	}
//         	if(scheduler.getTriggerState(TriggerKey.triggerKey("MFT_Approval")).equals(Trigger.TriggerState.ERROR))
//         		scheduler.resetTriggerFromErrorState(TriggerKey.triggerKey("MFT_Approval"));
//         }
        
//         //Job #5
//         if(!scheduler.checkExists(JobKey.jobKey("RICP_WriteOff"))) {
//         	JobDetail job = newJob().ofType(UpdateRICPWriteOff.class)
//             		.storeDurably().withIdentity(JobKey.jobKey("RICP_WriteOff"))
//             		.withDescription("Update RICP WriteOffs every midnight").build();
//         	Trigger trigger = newTrigger().forJob(job)
//             		.withIdentity(TriggerKey.triggerKey("RICP_WriteOff"))
//             		.withDescription("Trigger for RICP WriteOffs.")
//             		.withSchedule(CronScheduleBuilder.cronSchedule(updateRICPWriteOffCron)
//             				.withMisfireHandlingInstructionFireAndProceed()).build();
//         	scheduler.scheduleJob(job, trigger);
//         	log.info("Created job: MFT_Approval");
//         }
//         else{
//         	CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey("RICP_WriteOff"));
//         	if(!updateRICPWriteOffCron.equals(oldTrigger.getCronExpression())) {
//             	Trigger trigger = newTrigger().forJob(scheduler.getJobDetail(JobKey.jobKey("RICP_WriteOff")))
//                 		.withIdentity(TriggerKey.triggerKey("RICP_WriteOff"))
//                 		.withDescription("Trigger for RICP WriteOff.")
//                 		.withSchedule(CronScheduleBuilder.cronSchedule(updateRICPWriteOffCron)
//                 				.withMisfireHandlingInstructionFireAndProceed()).build();
//         		scheduler.rescheduleJob(TriggerKey.triggerKey("RICP_WriteOff"), trigger);
//             	log.info("Updated CRON for job: RICP_WriteOff");
//         	}
//         	if(scheduler.getTriggerState(TriggerKey.triggerKey("RICP_WriteOff")).equals(Trigger.TriggerState.ERROR))
//         		scheduler.resetTriggerFromErrorState(TriggerKey.triggerKey("RICP_WriteOff"));
//         }

// 		//Job #6
// 		if(!scheduler.checkExists(JobKey.jobKey("DI_Amortization"))) {
// 			JobDetail job = newJob().ofType(DeferredIncome.class)
// 					.storeDurably().withIdentity(JobKey.jobKey("DI_Amortization"))
// 					.withDescription("Update DI Amortization every midnight").build();
// 			Trigger trigger = newTrigger().forJob(job)
// 					.withIdentity(TriggerKey.triggerKey("DI_Amortization"))
// 					.withDescription("Trigger for DI Amortization.")
// 					.withSchedule(CronScheduleBuilder.cronSchedule(updateDIAmortizedCron)
// 							.withMisfireHandlingInstructionFireAndProceed()).build();
// 			scheduler.scheduleJob(job, trigger);
// 			log.info("Created job: DI Amortized");
// 		}
// 		else{
// 			CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey("DI_Amortization"));
// 			if(!updateDIAmortizedCron.equals(oldTrigger.getCronExpression())) {
// 				Trigger trigger = newTrigger().forJob(scheduler.getJobDetail(JobKey.jobKey("DI_Amortization")))
// 						.withIdentity(TriggerKey.triggerKey("DI_Amortization"))
// 						.withDescription("Trigger for DI Amortization.")
// 						.withSchedule(CronScheduleBuilder.cronSchedule(updateDIAmortizedCron)
// 								.withMisfireHandlingInstructionFireAndProceed()).build();
// 				scheduler.rescheduleJob(TriggerKey.triggerKey("DI_Amortization"), trigger);
// 				log.info("Updated CRON for job: DI_Amortization");

// 				}
// 			}

// 		//Job #7
// 		if(!scheduler.checkExists(JobKey.jobKey("Bank_Recon_Doc"))) {
// 			JobDetail job = newJob().ofType(BankReconSch.class)
// 					.storeDurably().withIdentity(JobKey.jobKey("Bank_Recon_Doc"))
// 					.withDescription("Update Bank Reconciliation Doc every midnight").build();
// 			Trigger trigger = newTrigger().forJob(job)
// 					.withIdentity(TriggerKey.triggerKey("Bank_Recon_Doc"))
// 					.withDescription("Trigger for Bank Reconciliation Doc.")
// 					.withSchedule(CronScheduleBuilder.cronSchedule(updateBankReconSchCron)
// 							.withMisfireHandlingInstructionFireAndProceed()).build();
// 			scheduler.scheduleJob(job, trigger);
// 			log.info("Created job: Bank Reconciliation");
// 		}
// 		else{
// 			CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey("Bank_Recon_Doc"));
// 			if(!updateBankReconSchCron.equals(oldTrigger.getCronExpression())) {
// 				Trigger trigger = newTrigger().forJob(scheduler.getJobDetail(JobKey.jobKey("Bank_Recon_Doc")))
// 						.withIdentity(TriggerKey.triggerKey("Bank_Recon_Doc"))
// 						.withDescription("Trigger for Bank Reconciliation Doc.")
// 						.withSchedule(CronScheduleBuilder.cronSchedule(updateBankReconSchCron)
// 								.withMisfireHandlingInstructionFireAndProceed()).build();
// 				scheduler.rescheduleJob(TriggerKey.triggerKey("Bank_Recon_Doc"), trigger);
// 				log.info("Updated CRON for job: Bank_Recon_Doc");

// 			}
// 		}

// 		//Job PGRecon
//         if(!scheduler.checkExists(JobKey.jobKey("PGRecon"))) {
//         	JobDetail job = newJob().ofType(PGRecon.class)
//             		.storeDurably().withIdentity(JobKey.jobKey("PGRecon"))
//             		.withDescription("PGRecon").build();
//         	Trigger trigger = newTrigger().forJob(job)
//             		.withIdentity(TriggerKey.triggerKey("PGRecon"))
//             		.withDescription("Trigger for PGRecon")
//             		.withSchedule(CronScheduleBuilder.cronSchedule(PGReconCron)
//             				.withMisfireHandlingInstructionFireAndProceed()).build();
//         	scheduler.scheduleJob(job, trigger);
//         	RMSLogger.schedulerInfo("Created job: PGRecon");
//         }
//         else{
//         	CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey("PGRecon"));
//         	if(!PGReconCron.equals(oldTrigger.getCronExpression())) {
//             	Trigger trigger = newTrigger().forJob(scheduler.getJobDetail(JobKey.jobKey("PGRecon")))
//                 		.withIdentity(TriggerKey.triggerKey("PGRecon"))
//                 		.withDescription("Trigger for PGRecon Cron")
//                 		.withSchedule(CronScheduleBuilder.cronSchedule(PGReconCron)
//                 				.withMisfireHandlingInstructionFireAndProceed()).build();
//         		scheduler.rescheduleJob(TriggerKey.triggerKey("PGRecon"), trigger);
//             	RMSLogger.schedulerInfo("Updated CRON for job: PGRecon");
//         	}
//         	if(scheduler.getTriggerState(TriggerKey.triggerKey("PGRecon")).equals(Trigger.TriggerState.ERROR))
//         		scheduler.resetTriggerFromErrorState(TriggerKey.triggerKey("PGRecon"));
//         }

// 		//Job ReUploadReceipt
// 		if(!scheduler.checkExists(JobKey.jobKey("ReUploadReceipt"))) {
// 			JobDetail job = newJob().ofType(ReUploadReceipt.class)
// 					.storeDurably().withIdentity(JobKey.jobKey("ReUploadReceipt"))
// 					.withDescription("ReUploadReceipt").build();
// 			Trigger trigger = newTrigger().forJob(job)
// 					.withIdentity(TriggerKey.triggerKey("ReUploadReceipt"))
// 					.withDescription("Trigger for ReUploadReceipt")
// 					.withSchedule(CronScheduleBuilder.cronSchedule(updateReUploadReceiptSchCron)
// 							.withMisfireHandlingInstructionFireAndProceed()).build();
// 			scheduler.scheduleJob(job, trigger);
// 			RMSLogger.schedulerInfo("Created job: ReUploadReceipt");
// 		}
// 		else{
// 			CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey("ReUploadReceipt"));
// 			if(!updateReUploadReceiptSchCron.equals(oldTrigger.getCronExpression())) {
// 				Trigger trigger = newTrigger().forJob(scheduler.getJobDetail(JobKey.jobKey("ReUploadReceipt")))
// 						.withIdentity(TriggerKey.triggerKey("ReUploadReceipt"))
// 						.withDescription("Trigger for ReUploadReceipt Cron")
// 						.withSchedule(CronScheduleBuilder.cronSchedule(updateReUploadReceiptSchCron)
// 								.withMisfireHandlingInstructionFireAndProceed()).build();
// 				scheduler.rescheduleJob(TriggerKey.triggerKey("ReUploadReceipt"), trigger);
// 				RMSLogger.schedulerInfo("Updated CRON for job: ReUploadReceipt");
// 			}
// 			if(scheduler.getTriggerState(TriggerKey.triggerKey("ReUploadReceipt")).equals(Trigger.TriggerState.ERROR))
// 				scheduler.resetTriggerFromErrorState(TriggerKey.triggerKey("ReUploadReceipt"));
// 		}

// 		if(r1b1Control){

// 			//Job FMSARI
// 			if(!scheduler.checkExists(JobKey.jobKey("FMSARI"))) {
// 				JobDetail job = newJob().ofType(FMSARI.class)
// 						.storeDurably().withIdentity(JobKey.jobKey("FMSARI"))
// 						.withDescription("FMSARI").build();
// 				Trigger trigger = newTrigger().forJob(job)
// 						.withIdentity(TriggerKey.triggerKey("FMSARI"))
// 						.withDescription("Trigger for FMSARI")
// 						.withSchedule(CronScheduleBuilder.cronSchedule(updateFMSARISchCron)
// 								.withMisfireHandlingInstructionFireAndProceed()).build();
// 				scheduler.scheduleJob(job, trigger);
// 				RMSLogger.schedulerInfo("Created job: FMSARI");
// 			}
// 			else{
// 				CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey("FMSARI"));
// 				if(!updateFMSARISchCron.equals(oldTrigger.getCronExpression())) {
// 					Trigger trigger = newTrigger().forJob(scheduler.getJobDetail(JobKey.jobKey("FMSARI")))
// 							.withIdentity(TriggerKey.triggerKey("FMSARI"))
// 							.withDescription("Trigger for FMSARI Cron")
// 							.withSchedule(CronScheduleBuilder.cronSchedule(updateFMSARISchCron)
// 									.withMisfireHandlingInstructionFireAndProceed()).build();
// 					scheduler.rescheduleJob(TriggerKey.triggerKey("FMSARI"), trigger);
// 					RMSLogger.schedulerInfo("Updated CRON for job: FMSARI");
// 				}
// 				if(scheduler.getTriggerState(TriggerKey.triggerKey("FMSARI")).equals(Trigger.TriggerState.ERROR))
// 					scheduler.resetTriggerFromErrorState(TriggerKey.triggerKey("FMSARI"));
// 			}

// 			//Job FMSARR
// 			if(!scheduler.checkExists(JobKey.jobKey("FMSARR"))) {
// 				JobDetail job = newJob().ofType(FMSARR.class)
// 						.storeDurably().withIdentity(JobKey.jobKey("FMSARR"))
// 						.withDescription("FMSARR").build();
// 				Trigger trigger = newTrigger().forJob(job)
// 						.withIdentity(TriggerKey.triggerKey("FMSARR"))
// 						.withDescription("Trigger for FMSARR")
// 						.withSchedule(CronScheduleBuilder.cronSchedule(updateFMSARRSchCron)
// 								.withMisfireHandlingInstructionFireAndProceed()).build();
// 				scheduler.scheduleJob(job, trigger);
// 				RMSLogger.schedulerInfo("Created job: FMSARR");
// 			}
// 			else{
// 				CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey("FMSARR"));
// 				if(!updateFMSARRSchCron.equals(oldTrigger.getCronExpression())) {
// 					Trigger trigger = newTrigger().forJob(scheduler.getJobDetail(JobKey.jobKey("FMSARR")))
// 							.withIdentity(TriggerKey.triggerKey("FMSARR"))
// 							.withDescription("Trigger for FMSARR Cron")
// 							.withSchedule(CronScheduleBuilder.cronSchedule(updateFMSARRSchCron)
// 									.withMisfireHandlingInstructionFireAndProceed()).build();
// 					scheduler.rescheduleJob(TriggerKey.triggerKey("FMSARR"), trigger);
// 					RMSLogger.schedulerInfo("Updated CRON for job: FMSARR");
// 				}
// 				if(scheduler.getTriggerState(TriggerKey.triggerKey("FMSARR")).equals(Trigger.TriggerState.ERROR))
// 					scheduler.resetTriggerFromErrorState(TriggerKey.triggerKey("FMSARR"));
// 			}

// 			//Job FMSDebitMemo
// 			if(!scheduler.checkExists(JobKey.jobKey("FMSDebitMemo"))) {
// 				JobDetail job = newJob().ofType(FMSDebitMemo.class)
// 						.storeDurably().withIdentity(JobKey.jobKey("FMSDebitMemo"))
// 						.withDescription("FMSDebitMemo").build();
// 				Trigger trigger = newTrigger().forJob(job)
// 						.withIdentity(TriggerKey.triggerKey("FMSDebitMemo"))
// 						.withDescription("Trigger for FMSDebitMemo")
// 						.withSchedule(CronScheduleBuilder.cronSchedule(updateFMSDebitMemoSchCron)
// 								.withMisfireHandlingInstructionFireAndProceed()).build();
// 				scheduler.scheduleJob(job, trigger);
// 				RMSLogger.schedulerInfo("Created job: FMSDebitMemo");
// 			}
// 			else{
// 				CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey("FMSDebitMemo"));
// 				if(!updateFMSDebitMemoSchCron.equals(oldTrigger.getCronExpression())) {
// 					Trigger trigger = newTrigger().forJob(scheduler.getJobDetail(JobKey.jobKey("FMSDebitMemo")))
// 							.withIdentity(TriggerKey.triggerKey("FMSDebitMemo"))
// 							.withDescription("Trigger for FMSDebitMemo Cron")
// 							.withSchedule(CronScheduleBuilder.cronSchedule(updateFMSDebitMemoSchCron)
// 									.withMisfireHandlingInstructionFireAndProceed()).build();
// 					scheduler.rescheduleJob(TriggerKey.triggerKey("FMSDebitMemo"), trigger);
// 					RMSLogger.schedulerInfo("Updated CRON for job: FMSDebitMemo");
// 				}
// 				if(scheduler.getTriggerState(TriggerKey.triggerKey("FMSDebitMemo")).equals(Trigger.TriggerState.ERROR))
// 					scheduler.resetTriggerFromErrorState(TriggerKey.triggerKey("FMSDebitMemo"));
// 			}

// 			//Job FMSCreditMemo
// 			if(!scheduler.checkExists(JobKey.jobKey("FMSCreditMemo"))) {
// 				JobDetail job = newJob().ofType(FMSCreditMemo.class)
// 						.storeDurably().withIdentity(JobKey.jobKey("FMSCreditMemo"))
// 						.withDescription("FMSCreditMemo").build();
// 				Trigger trigger = newTrigger().forJob(job)
// 						.withIdentity(TriggerKey.triggerKey("FMSCreditMemo"))
// 						.withDescription("Trigger for FMSCreditMemo")
// 						.withSchedule(CronScheduleBuilder.cronSchedule(updateFMSCreditMemoSchCron)
// 								.withMisfireHandlingInstructionFireAndProceed()).build();
// 				scheduler.scheduleJob(job, trigger);
// 				RMSLogger.schedulerInfo("Created job: FMSCreditMemo");
// 			}
// 			else{
// 				CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey("FMSCreditMemo"));
// 				if(!updateFMSCreditMemoSchCron.equals(oldTrigger.getCronExpression())) {
// 					Trigger trigger = newTrigger().forJob(scheduler.getJobDetail(JobKey.jobKey("FMSCreditMemo")))
// 							.withIdentity(TriggerKey.triggerKey("FMSCreditMemo"))
// 							.withDescription("Trigger for FMSCreditMemo Cron")
// 							.withSchedule(CronScheduleBuilder.cronSchedule(updateFMSCreditMemoSchCron)
// 									.withMisfireHandlingInstructionFireAndProceed()).build();
// 					scheduler.rescheduleJob(TriggerKey.triggerKey("FMSCreditMemo"), trigger);
// 					RMSLogger.schedulerInfo("Updated CRON for job: FMSCreditMemo");
// 				}
// 				if(scheduler.getTriggerState(TriggerKey.triggerKey("FMSCreditMemo")).equals(Trigger.TriggerState.ERROR))
// 					scheduler.resetTriggerFromErrorState(TriggerKey.triggerKey("FMSCreditMemo"));
// 			}


// 			//Job FMSJournalDI
// 			if(!scheduler.checkExists(JobKey.jobKey("FMSJournalDI"))) {
// 				JobDetail job = newJob().ofType(FMSJournalDI.class)
// 						.storeDurably().withIdentity(JobKey.jobKey("FMSJournalDI"))
// 						.withDescription("FMSJournalDI").build();
// 				Trigger trigger = newTrigger().forJob(job)
// 						.withIdentity(TriggerKey.triggerKey("FMSJournalDI"))
// 						.withDescription("Trigger for FMSJournalDI")
// 						.withSchedule(CronScheduleBuilder.cronSchedule(updateFMSJournalDISchCron)
// 								.withMisfireHandlingInstructionFireAndProceed()).build();
// 				scheduler.scheduleJob(job, trigger);
// 				RMSLogger.schedulerInfo("Created job: FMSJournalDI");
// 			}
// 			else{
// 				CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey("FMSJournalDI"));
// 				if(!updateFMSJournalDISchCron.equals(oldTrigger.getCronExpression())) {
// 					Trigger trigger = newTrigger().forJob(scheduler.getJobDetail(JobKey.jobKey("FMSJournalDI")))
// 							.withIdentity(TriggerKey.triggerKey("FMSJournalDI"))
// 							.withDescription("Trigger for FMSJournalDI Cron")
// 							.withSchedule(CronScheduleBuilder.cronSchedule(updateFMSJournalDISchCron)
// 									.withMisfireHandlingInstructionFireAndProceed()).build();
// 					scheduler.rescheduleJob(TriggerKey.triggerKey("FMSJournalDI"), trigger);
// 					RMSLogger.schedulerInfo("Updated CRON for job: FMSJournalDI");
// 				}
// 				if(scheduler.getTriggerState(TriggerKey.triggerKey("FMSJournalDI")).equals(Trigger.TriggerState.ERROR))
// 					scheduler.resetTriggerFromErrorState(TriggerKey.triggerKey("FMSJournalDI"));
// 			}


// 			//Job FMSJournalRIPL
// 			if(!scheduler.checkExists(JobKey.jobKey("FMSJournalRIPL"))) {
// 				JobDetail job = newJob().ofType(FMSJournalRIPL.class)
// 						.storeDurably().withIdentity(JobKey.jobKey("FMSJournalRIPL"))
// 						.withDescription("FMSJournalRIPL").build();
// 				Trigger trigger = newTrigger().forJob(job)
// 						.withIdentity(TriggerKey.triggerKey("FMSJournalRIPL"))
// 						.withDescription("Trigger for FMSJournalRIPL")
// 						.withSchedule(CronScheduleBuilder.cronSchedule(updateFMSJournalRIPLSchCron)
// 								.withMisfireHandlingInstructionFireAndProceed()).build();
// 				scheduler.scheduleJob(job, trigger);
// 				RMSLogger.schedulerInfo("Created job: FMSJournalRIPL");
// 			}
// 			else{
// 				CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey("FMSJournalRIPL"));
// 				if(!updateFMSJournalRIPLSchCron.equals(oldTrigger.getCronExpression())) {
// 					Trigger trigger = newTrigger().forJob(scheduler.getJobDetail(JobKey.jobKey("FMSJournalRIPL")))
// 							.withIdentity(TriggerKey.triggerKey("FMSJournalRIPL"))
// 							.withDescription("Trigger for FMSJournalRIPL Cron")
// 							.withSchedule(CronScheduleBuilder.cronSchedule(updateFMSJournalRIPLSchCron)
// 									.withMisfireHandlingInstructionFireAndProceed()).build();
// 					scheduler.rescheduleJob(TriggerKey.triggerKey("FMSJournalRIPL"), trigger);
// 					RMSLogger.schedulerInfo("Updated CRON for job: FMSJournalRIPL");
// 				}
// 				if(scheduler.getTriggerState(TriggerKey.triggerKey("FMSJournalRIPL")).equals(Trigger.TriggerState.ERROR))
// 					scheduler.resetTriggerFromErrorState(TriggerKey.triggerKey("FMSJournalRIPL"));
// 			}


// 			//Job FMSJournalRICP
// 			if(!scheduler.checkExists(JobKey.jobKey("FMSJournalRICP"))) {
// 				JobDetail job = newJob().ofType(FMSJournalRICP.class)
// 						.storeDurably().withIdentity(JobKey.jobKey("FMSJournalRICP"))
// 						.withDescription("FMSJournalRICP").build();
// 				Trigger trigger = newTrigger().forJob(job)
// 						.withIdentity(TriggerKey.triggerKey("FMSJournalRICP"))
// 						.withDescription("Trigger for FMSJournalRICP")
// 						.withSchedule(CronScheduleBuilder.cronSchedule(updateFMSJournalRICPSchCron)
// 								.withMisfireHandlingInstructionFireAndProceed()).build();
// 				scheduler.scheduleJob(job, trigger);
// 				RMSLogger.schedulerInfo("Created job: FMSJournalRICP");
// 			}
// 			else{
// 				CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey("FMSJournalRICP"));
// 				if(!updateFMSJournalRICPSchCron.equals(oldTrigger.getCronExpression())) {
// 					Trigger trigger = newTrigger().forJob(scheduler.getJobDetail(JobKey.jobKey("FMSJournalRICP")))
// 							.withIdentity(TriggerKey.triggerKey("FMSJournalRICP"))
// 							.withDescription("Trigger for FMSJournalRICP Cron")
// 							.withSchedule(CronScheduleBuilder.cronSchedule(updateFMSJournalRICPSchCron)
// 									.withMisfireHandlingInstructionFireAndProceed()).build();
// 					scheduler.rescheduleJob(TriggerKey.triggerKey("FMSJournalRICP"), trigger);
// 					RMSLogger.schedulerInfo("Updated CRON for job: FMSJournalRICP");
// 				}
// 				if(scheduler.getTriggerState(TriggerKey.triggerKey("FMSJournalRICP")).equals(Trigger.TriggerState.ERROR))
// 					scheduler.resetTriggerFromErrorState(TriggerKey.triggerKey("FMSJournalRICP"));
// 			}


// 			//Job UnmatchAging
// 			if(!scheduler.checkExists(JobKey.jobKey("UnmatchAging"))) {
// 				JobDetail job = newJob().ofType(UnmatchAging.class)
// 						.storeDurably().withIdentity(JobKey.jobKey("UnmatchAging"))
// 						.withDescription("UnmatchAging").build();
// 				Trigger trigger = newTrigger().forJob(job)
// 						.withIdentity(TriggerKey.triggerKey("UnmatchAging"))
// 						.withDescription("Trigger for UnmatchAging")
// 						.withSchedule(CronScheduleBuilder.cronSchedule(updateUnmatchAgingSchCron)
// 								.withMisfireHandlingInstructionFireAndProceed()).build();
// 				scheduler.scheduleJob(job, trigger);
// 				RMSLogger.schedulerInfo("Created job: UnmatchAging");
// 			}
// 			else{
// 				CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey("UnmatchAging"));
// 				if(!updateUnmatchAgingSchCron.equals(oldTrigger.getCronExpression())) {
// 					Trigger trigger = newTrigger().forJob(scheduler.getJobDetail(JobKey.jobKey("UnmatchAging")))
// 							.withIdentity(TriggerKey.triggerKey("UnmatchAging"))
// 							.withDescription("Trigger for UnmatchAging Cron")
// 							.withSchedule(CronScheduleBuilder.cronSchedule(updateUnmatchAgingSchCron)
// 									.withMisfireHandlingInstructionFireAndProceed()).build();
// 					scheduler.rescheduleJob(TriggerKey.triggerKey("UnmatchAging"), trigger);
// 					RMSLogger.schedulerInfo("Updated CRON for job: UnmatchAging");
// 				}
// 				if(scheduler.getTriggerState(TriggerKey.triggerKey("UnmatchAging")).equals(Trigger.TriggerState.ERROR))
// 					scheduler.resetTriggerFromErrorState(TriggerKey.triggerKey("UnmatchAging"));
// 			}


// 			//Job DI Aging
// 			if(!scheduler.checkExists(JobKey.jobKey("DIAging"))) {
// 				JobDetail job = newJob().ofType(DIAging.class)
// 						.storeDurably().withIdentity(JobKey.jobKey("DIAging"))
// 						.withDescription("DIAging").build();
// 				Trigger trigger = newTrigger().forJob(job)
// 						.withIdentity(TriggerKey.triggerKey("DIAging"))
// 						.withDescription("Trigger for DIAging")
// 						.withSchedule(CronScheduleBuilder.cronSchedule(updateDIAgingSchCron)
// 								.withMisfireHandlingInstructionFireAndProceed()).build();
// 				scheduler.scheduleJob(job, trigger);
// 				RMSLogger.schedulerInfo("Created job: DIAging");
// 			}
// 			else{
// 				CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey("DIAging"));
// 				if(!updateDIAgingSchCron.equals(oldTrigger.getCronExpression())) {
// 					Trigger trigger = newTrigger().forJob(scheduler.getJobDetail(JobKey.jobKey("DIAging")))
// 							.withIdentity(TriggerKey.triggerKey("DIAging"))
// 							.withDescription("Trigger for DIAging Cron")
// 							.withSchedule(CronScheduleBuilder.cronSchedule(updateDIAgingSchCron)
// 									.withMisfireHandlingInstructionFireAndProceed()).build();
// 					scheduler.rescheduleJob(TriggerKey.triggerKey("DIAging"), trigger);
// 					RMSLogger.schedulerInfo("Updated CRON for job: DIAging");
// 				}
// 				if(scheduler.getTriggerState(TriggerKey.triggerKey("DIAging")).equals(Trigger.TriggerState.ERROR))
// 					scheduler.resetTriggerFromErrorState(TriggerKey.triggerKey("DIAging"));
// 			}


// 			//Job RIPL Aging
// 			if(!scheduler.checkExists(JobKey.jobKey("RIPLAging"))) {
// 				JobDetail job = newJob().ofType(RIPLAging.class)
// 							.storeDurably().withIdentity(JobKey.jobKey("RIPLAging"))
// 							.withDescription("RIPLAging").build();
// 				Trigger trigger = newTrigger().forJob(job)
// 							.withIdentity(TriggerKey.triggerKey("RIPLAging"))
// 							.withDescription("Trigger for RIPLAging")
// 							.withSchedule(CronScheduleBuilder.cronSchedule(updateRIPLAgingSchCron)
// 									.withMisfireHandlingInstructionFireAndProceed()).build();
// 				scheduler.scheduleJob(job, trigger);
// 				RMSLogger.schedulerInfo("Created job: RIPLAging");
// 			}
// 			else{
// 				CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey("RIPLAging"));
// 				if(!updateRIPLAgingSchCron.equals(oldTrigger.getCronExpression())) {
// 					Trigger trigger = newTrigger().forJob(scheduler.getJobDetail(JobKey.jobKey("RIPLAging")))
// 								.withIdentity(TriggerKey.triggerKey("RIPLAging"))
// 								.withDescription("Trigger for RIPLAging Cron")
// 								.withSchedule(CronScheduleBuilder.cronSchedule(updateRIPLAgingSchCron)
// 										.withMisfireHandlingInstructionFireAndProceed()).build();
// 					scheduler.rescheduleJob(TriggerKey.triggerKey("RIPLAging"), trigger);
// 					RMSLogger.schedulerInfo("Updated CRON for job: RIPLAging");
// 				}
// 				if(scheduler.getTriggerState(TriggerKey.triggerKey("RIPLAging")).equals(Trigger.TriggerState.ERROR))
// 					scheduler.resetTriggerFromErrorState(TriggerKey.triggerKey("RIPLAging"));
// 			}
 
// 				//Job RICPAging
// 			if(!scheduler.checkExists(JobKey.jobKey("RICPAging"))) {
// 				JobDetail job = newJob().ofType(RICPAging.class)
// 						.storeDurably().withIdentity(JobKey.jobKey("RICPAging"))
// 						.withDescription("RICPAging").build();
// 				Trigger trigger = newTrigger().forJob(job)
// 						.withIdentity(TriggerKey.triggerKey("RICPAging"))
// 						.withDescription("Trigger for RICPAging")
// 						.withSchedule(CronScheduleBuilder.cronSchedule(updateRICPAgingSchCron)
// 								.withMisfireHandlingInstructionFireAndProceed()).build();
// 				scheduler.scheduleJob(job, trigger);
// 				RMSLogger.schedulerInfo("Created job: RICPAging");
// 			}
// 			else{
// 				CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey("RICPAging"));
// 				if(!updateRICPAgingSchCron.equals(oldTrigger.getCronExpression())) {
// 					Trigger trigger = newTrigger().forJob(scheduler.getJobDetail(JobKey.jobKey("RICPAging")))
// 							.withIdentity(TriggerKey.triggerKey("RICPAging"))
// 							.withDescription("Trigger for RICPAging Cron")
// 							.withSchedule(CronScheduleBuilder.cronSchedule(updateRICPAgingSchCron)
// 									.withMisfireHandlingInstructionFireAndProceed()).build();
// 					scheduler.rescheduleJob(TriggerKey.triggerKey("RICPAging"), trigger);
// 					RMSLogger.schedulerInfo("Updated CRON for job: RICPAging");
// 				}
// 				if(scheduler.getTriggerState(TriggerKey.triggerKey("RICPAging")).equals(Trigger.TriggerState.ERROR))
// 					scheduler.resetTriggerFromErrorState(TriggerKey.triggerKey("RICPAging"));
// 			}

// 		}

//         if(enableQuartz)
//         	scheduler.resumeAll();
//         else
//         	scheduler.pauseAll();
        
//         return scheduler;

// }
	
        
//     @Bean
//     public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
//         SchedulerFactoryBean factory = new SchedulerFactoryBean();
//         factory.setJobFactory(springBeanJobFactory());
//         factory.setQuartzProperties(quartzProperties());
//         return factory;
//     }
    
//     @Bean
//     public SpringBeanJobFactory springBeanJobFactory() {
//         AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();

//         jobFactory.setApplicationContext(applicationContext);
//         return jobFactory;
//     }
    
//     public Properties quartzProperties() throws IOException {
//         PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
//         propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
//         propertiesFactoryBean.afterPropertiesSet();
//         return propertiesFactoryBean.getObject();
//     }
// }
