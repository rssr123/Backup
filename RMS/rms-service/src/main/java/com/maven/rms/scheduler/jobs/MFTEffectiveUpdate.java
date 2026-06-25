package com.maven.rms.scheduler.jobs;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maven.rms.controllers.MFTEmailController;
import com.maven.rms.models.MFT;
import com.maven.rms.models.MFTRequest;
import com.maven.rms.models.MFTWF;
import com.maven.rms.models.MFTWFRequest;
import com.maven.rms.models.SchedulerLog;
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.MFTEffectiveEmailService;
import com.maven.rms.services.MFTService;
import com.maven.rms.services.MFTWFService;
import com.maven.rms.services.StoreProcedureService;

@DisallowConcurrentExecution
@Component
@Slf4j
public class MFTEffectiveUpdate implements Job {
	//private static final Logger logger = LoggerFactory.getLogger(MFTEffectiveUpdate.class);

	@Autowired
	private SchedulerLogService schLogSvc;
	@Autowired
	private MFTWFService mftwfSvc;
	@Autowired
	private MFTService mftSvc;
	@Autowired
	private MFTEffectiveEmailService mftEffEmailSvc;


	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String approvalCode = "APV";
		String effectiveCode = "EFT";
		// Param apvParam = sps.sp_getparam(1, 1, approvalCode, "Status-MFT").get(0);
		// Param effParam = sps.sp_getparam(1, 1, effectiveCode, "Status-MFT").get(0);

		List<MFTWF> listOfWFs = mftwfSvc.sp_getmftwfByStatusAndEffDate(approvalCode);
		// int succProcess = listOfWFs.size();
		int succProcess = CollectionUtils.size(listOfWFs);
		int failProcess = 0;

		SchedulerLog newLog = new SchedulerLog("Process Approved MFTWF records.",
				"This job is called from thread: " + Thread.currentThread().getName(),
				succProcess);
		newLog = schLogSvc.saveNewScheduleLog(newLog);

		if (newLog == null || newLog.equals(null))
			throw new IllegalArgumentException("The scheduler log failed to update!");

		for (Iterator<MFTWF> iterator = listOfWFs.iterator(); iterator.hasNext();) {
			MFTWF tmp = iterator.next();
			MFT newMFT = new MFT();
			if (tmp.getAction().toLowerCase().contains("edit")) {
				MFTRequest mftRequest = new MFTRequest();
				mftRequest.setI_fee_detail_pk(tmp.getFee_detail_pk());
				// newMFT = sps.sp_getmftByPK(tmp.getFee_detail_pk());
				newMFT = mftSvc.sp_getmftByPK(mftRequest);
				if (newMFT == null) {
					log.error("Exception in " + this.getClass().toString() + " - Cannot find MFT record (PK:"
							+ tmp.getFee_detail_pk().toString() + ") to update!");
					failProcess += 1;
					continue;
				}
			}

			newMFT.setFee_detail_id(tmp.getFee_detail_id());
			newMFT.setFee_grp_id(tmp.getFee_grp_id());
			newMFT.setFee_detail_nm_e(tmp.getFee_detail_nm_e());
			newMFT.setFee_detail_nm_b(tmp.getFee_detail_nm_b());
			newMFT.setUnit_fee(tmp.getFee_amt());
			newMFT.setPromo_startdt(tmp.getPromo_startdt());
			newMFT.setPromo_enddt(tmp.getPromo_enddt());
			newMFT.setPromo_fee(tmp.getPromo_fee());
			newMFT.setTax_cd_id(tmp.getTax_cd_id());
			newMFT.setAllow_otc(tmp.getAllow_otc());
			newMFT.setLl_parent_id(tmp.getLl_parent_id());
			newMFT.setLl_start_day(tmp.getLl_start_day());
			newMFT.setLl_start_mth(tmp.getLl_start_mth());
			newMFT.setLl_end_day(tmp.getLl_end_day());
			newMFT.setLl_end_mth(tmp.getLl_end_mth());
			newMFT.setLedger_cd(tmp.getLedger_cd());
			newMFT.setSs_cd(tmp.getSs_cd());
			newMFT.setDt_modified(new Date());
			newMFT.setModified_by(tmp.getModified_by());
			newMFT.setStatus(tmp.getMft_status());
			newMFT.setIsPub(tmp.getIs_pub());

			int result = 0;

			if (tmp.getAction().toLowerCase().contains("add")) {
				newMFT.setDt_created(new Date());
				newMFT.setCreated_by(tmp.getCreated_by());
				result = mftSvc.sp_insMFT(newMFT);
			} else if (tmp.getAction().toLowerCase().contains("edit"))
				result = mftSvc.sp_updMFT(newMFT);
			else {
				log.error("Exception in " + this.getClass().toString()
						+ " - Cannot process action (" + tmp.getAction() + ")!");
				failProcess += 1;
				continue;
			}

			if (result < 1) {
				log.error("Exception in " + this.getClass().toString()
						+ " - Inserting/Updating MFT record failed!");
				failProcess += 1;
				continue;
			}

			MFTWFRequest mftwfRequest = new MFTWFRequest();
			mftwfRequest.setI_wf_id(tmp.getWf_id());
			mftwfRequest.setI_status(effectiveCode);
			
			// result = sps.sp_updmftwfStatus(tmp.getWf_id(), effectiveCode);
			result = mftwfSvc.sp_updmftwfStatus(mftwfRequest);
			if (result < 1) {
				log.error("Exception in " + this.getClass().toString()
						+ " - Could not update WF (id:" + tmp.getWf_id().toString()
						+ ") Status to " + effectiveCode + "!");
				failProcess += 1;
			}
			else{
				// mftEffEmailSvc.insMFTEffectiveEmail(tmp.getWf_id());
				mftEffEmailSvc.insMFTEffectiveEmail(mftwfRequest);
			}
		}

		succProcess -= failProcess;
		newLog.setSuccessTxn(succProcess);
		newLog.setFailTxn(failProcess);
		newLog.setDtModified(LocalDateTime.now());

		log.debug("Succ: " + Integer.toString(succProcess) + " || Fail: " + Integer.toString(failProcess));
		schLogSvc.saveNewScheduleLog(newLog);
	}

}
