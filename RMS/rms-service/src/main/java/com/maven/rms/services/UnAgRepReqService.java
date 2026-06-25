package com.maven.rms.services;

import java.util.List;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.maven.rms.models.UnmatchedAgingReportRequest;
import com.maven.rms.repositories.UnAgingRepReqRepository;

@Service
@Slf4j
public class UnAgRepReqService {
	//private static final Logger logger = LoggerFactory.getLogger(UnAgRepReqService.class);
			
	private final UnAgingRepReqRepository uarreqRepo;
	
	public UnAgRepReqService(UnAgingRepReqRepository uarreqRepo) {
		this.uarreqRepo = uarreqRepo;
	}
	
	public int addJobToTable(UnmatchedAgingReportRequest r) {
		if(uarreqRepo.sp_getrptumagequeue() > 0) {
			log.warn("Warning in " + this.getClass().toString() 
					+ ": There is already a pending request in the table! Request is invalidated...");
			return 0;
		}
		return uarreqRepo.sp_insrptumage(r);
	}
	
	public UnmatchedAgingReportRequest getPendingRequest() {
		return uarreqRepo.sp_getpendingrptumage();
	}
	
	public List<UnmatchedAgingReportRequest> getListOfRequests(int page, int size){
		return uarreqRepo.sp_getrptumagelisting(page, size);
	}
	
	public Integer updateStatus(UnmatchedAgingReportRequest r) {
		return uarreqRepo.sp_updstatusrptumage(r);
	}
	
	public Integer updateReportDet(UnmatchedAgingReportRequest r) {
		return uarreqRepo.sp_updrptumage(r);
	}
	
	public UnmatchedAgingReportRequest getTask(Integer id) {
		return uarreqRepo.sp_getrptumage(id);
	}
	
	public Integer getTotalRecordsFromUmAgeRReq() {
		return uarreqRepo.sp_getrptumagecount();
	}
}
