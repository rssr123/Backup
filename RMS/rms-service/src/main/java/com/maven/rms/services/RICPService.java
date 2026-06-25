package com.maven.rms.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.maven.rms.models.CreditControlReminderRequest;
import com.maven.rms.models.RICP;
import com.maven.rms.models.RICPAudit;
import com.maven.rms.models.RICPCVLog;
import com.maven.rms.models.RICPList;
import com.maven.rms.models.RICPRPRequest;
import com.maven.rms.models.RICPRequest;
import com.maven.rms.models.payload.requests.SubmitRICPCanRequest;
import com.maven.rms.repositories.RICPRepository;
import com.maven.rms.utils.APIResponse;

import java.sql.Timestamp;

@Service
@Slf4j
public class RICPService {
	//private static final Logger logger = LoggerFactory.getLogger(RICPService.class);

    //@Value("#{new Integer('${ricp.writeoff.months}')}")
    //private Integer writeOffMonths;
    
	private final RICPRepository ricpRepo;
	
	public RICPService(RICPRepository ricpRepo) {
		this.ricpRepo = ricpRepo;
	}
	
	public Integer issueRCIP(RICP ricp) {
		return ricpRepo.sp_insricpandaudit(ricp);
	}
	
	public Integer updateRICPCollected(SubmitRICPCanRequest req, String auditAction, String oldStatus, String username) {
		return ricpRepo.sp_updricp(req, auditAction, oldStatus, username);
	}

	// public Integer updateRICPCollected(SubmitRICPCanRequest payload, String auditAction, String oldStatus) {
	// 	return ricpRepo.sp_updricp(payload, auditAction, oldStatus);
	// }
	
	public List<RICP> ricpToWriteOff(){
		return ricpRepo.sp_getricpwriteoffs("CA");		//Where CA is Collectable
	}
	
	//Additional methods; leftover from pre-Informix re-roll SPs
	public RICP fullRICPbyFilters(String entity_type, String entity_no, String cp_no, String status) {
		return ricpRepo.sp_getfullricpidsbyfilter(entity_type, entity_no, cp_no, status);
	}
	
	public RICP fullRICPbyCpNo(String cp_no, String status) {
		return ricpRepo.sp_getfullricpidsbycpno(cp_no, status);
	}
	
	public Integer updateRICPStatus(Integer ricp_id, String status, String username) {
		return ricpRepo.sp_updricpstatus(ricp_id, status, username);
	}
	
	public Integer insRICPAudit(RICPAudit ra) {
		return ricpRepo.sp_insricpa(ra);
				//new RICPAudit(ricp_id, new Date(), auditAction , cp_no, entity_type, entity_no
				//, accr_amt_b4, accr_amt_af, status_b4, status_af, created_by, modified_by, status));
	}
	
	public Integer insRICPCVLog(RICPCVLog rl) {
		return ricpRepo.sp_insricpcvlog(rl);
				//new RICPCVLog(ricp_id, entity_type, entity_no, cp_no,
				//auditAction.equals("CPV")? new Date() : null, auditAction.equals("CPC") ? new Date() : null,
				//created_by, modified_by, status));
	}
	
	// public RICPList sp_getricp(Integer i_page, Integer i_size, RICP ricp) {
    //     return ricpRepo.sp_getricp( i_page, i_size, ricp);
    // }

	public RICPList sp_getricp(RICPRequest ricp) {
        return ricpRepo.sp_getricp(ricp);
    }

    public Integer sp_updricprp(RICPRPRequest ricprpRequestRequest) {

        Integer result = 0;

        result = ricpRepo.sp_updricprp(ricprpRequestRequest);

        return result;
    }

}
