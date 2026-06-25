package com.maven.rms.services;

import java.util.List;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.maven.rms.models.RICPAgingReportRequest;
import com.maven.rms.repositories.RICPAgingReqRepository;

@Service
@Slf4j
public class RICPAgRepReqService {
	//private static final Logger logger = LoggerFactory.getLogger(RICPAgRepReqService.class);
	
	private final RICPAgingReqRepository rarRepo;
	
	public RICPAgRepReqService(RICPAgingReqRepository rarRepo) {
		this.rarRepo = rarRepo;
	}
	
	public int addJobToTable(RICPAgingReportRequest r) {
		if(rarRepo.sp_getrptricpagequeue() > 0) {
			log.warn("Warning in " + this.getClass().toString() 
					+ ": There is already a pending request in the table! Request is invalidated...");
			return 0;
		}
		return rarRepo.sp_insrptricpar(r);
	}
	
	public RICPAgingReportRequest getPendingRequest() {
		return rarRepo.sp_getpendingrptricpagerq();
	}
	
	public List<RICPAgingReportRequest> getListOfRequests(int page, int size){
		return rarRepo.sp_getrptricpagelisting(page, size);
	}
	
	public Integer updateStatus(RICPAgingReportRequest r) {
		return rarRepo.sp_updstatusrptricpagerq(r);
	}
	
	public Integer updateReportDet(RICPAgingReportRequest r) {
		return rarRepo.sp_updrptricpagerq(r);
	}
	
	public RICPAgingReportRequest getTask(Integer id) {
		return rarRepo.sp_getrptricpagerq(id);
	}
	
	public Integer getTotalRecordsFromRICPAgeRReq() {
		return rarRepo.sp_getrptricpagecount();
	}
	
}
