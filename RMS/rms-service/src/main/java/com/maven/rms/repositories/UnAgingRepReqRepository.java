package com.maven.rms.repositories;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import com.maven.rms.models.UnmatchedAgingReportRequest;

import org.apache.commons.collections4.CollectionUtils;

@Repository
@Slf4j
public class UnAgingRepReqRepository{
	//private static final Logger logger = LoggerFactory.getLogger(UnAgingRepReqRepository.class);
	
    @PersistenceContext
    private EntityManager entityManager;
    
    public Integer sp_insrptumage(UnmatchedAgingReportRequest req) {
     	 Query query = entityManager.createNativeQuery("CALL sp_insrptumage(:i_p_dt_req, :i_p_txn_id,"
     	 		+ "	:i_p_rcpt_no, :i_p_stmt_no, :i_p_recon_status, :i_p_dt_stmt_fr,"
     	 		+ "	:i_p_dt_stmt_to, :i_p_dup, :i_p_email, :i_p_file_type,"
     	 		+ "	:i_status, :i_created_by, :i_modified_by)")
       			 	.setParameter("i_p_dt_req", req.getP_dt_req())
       			 	.setParameter("i_p_txn_id", req.getP_txn_id())
       			 	.setParameter("i_p_rcpt_no", req.getP_rcpt_no())
       			 	.setParameter("i_p_stmt_no", req.getP_stmt_no())
       			 	.setParameter("i_p_recon_status", req.getP_recon_status())
       			 	.setParameter("i_p_dt_stmt_fr", req.getP_dt_stmt_fr())
       			 	.setParameter("i_p_dt_stmt_to", req.getP_dt_stmt_to())
       			 	.setParameter("i_p_dup", req.getP_dup())
       			 	.setParameter("i_p_email", req.getP_email())
       			 	.setParameter("i_p_file_type", req.getP_file_type())
       			 	.setParameter("i_status", req.getStatus())
       			 	.setParameter("i_created_by", req.getCreated_by())
       			 	.setParameter("i_modified_by",req.getModified_by());

            return ((Integer)query.getSingleResult()).intValue();
    }
    
    public Integer sp_updrptumage(UnmatchedAgingReportRequest req) {
    	 Query query = entityManager.createNativeQuery("CALL sp_updrptumage(:i_rpt_um_age_id, :i_status,"
      	 		+ "	:i_p_file_size, :i_p_file_nm, :i_modified_by)")
        			 	.setParameter("i_rpt_um_age_id", req.getRpt_um_age_id())
        			 	.setParameter("i_status", req.getStatus())
        			 	.setParameter("i_p_file_size", req.getP_file_size())
        			 	.setParameter("i_p_file_nm", req.getP_file_nm())
           			 	.setParameter("i_modified_by", req.getModified_by());
    	 return ((Integer)query.getSingleResult()).intValue();
    }
    
    public Integer sp_getrptumagequeue() {
   	 	Query query = entityManager.createNativeQuery("CALL sp_getrptumagequeue()");
   	 	return ((Integer)query.getSingleResult()).intValue();
    }
    
    public UnmatchedAgingReportRequest sp_getpendingrptumage() {
    	Query query = entityManager.createNativeQuery("CALL sp_getpendingrptumage()");
    	
    	List<UnmatchedAgingReportRequest> data = convertUMReqObj(query.getResultList());
    	
   	 	//if(data.size() > 1) {
		if(CollectionUtils.size(data) > 1) {
   	 		String idList = data.stream().map(i -> i.getRpt_um_age_id().toString()).collect(Collectors.joining(", "));
			log.error("Exception in " + this.getClass().toString() 
					+ " there is more than one UnmatchedAgingReportRequest found! IDs:" + idList);
   	 	}
    	
    	// return data.size() == 0 ? null : data.get(0);
		return CollectionUtils.size(data) == 0 ? null : data.get(0);
    }
    
    public List<UnmatchedAgingReportRequest> sp_getrptumagelisting(int page, int size) {
    	Query query = entityManager.createNativeQuery("CALL sp_getrptumagelisting(:i_page, :i_size)")
    			.setParameter("i_page", page)
    			.setParameter("i_size", size);
    	
    	List<UnmatchedAgingReportRequest> data = convertUMReqObj(query.getResultList());
    	
    	return data;
    }
    
    public List<UnmatchedAgingReportRequest> convertUMReqObj(List<Object[]> objects){
    	List<UnmatchedAgingReportRequest> data = new ArrayList<>();
    	for (Object[] obj : objects) {
    		UnmatchedAgingReportRequest node = new UnmatchedAgingReportRequest(
    				((Timestamp) obj[1]).toLocalDateTime(), (String) obj[10], (String) obj[2],
    				(String) obj[8], (String) obj[9], 
    				obj[11] == null ? null : ((Timestamp) obj[11]).toLocalDateTime(),
    				obj[12] == null ? null : ((Timestamp) obj[12]).toLocalDateTime(), 
    				(Integer) obj[13], (String) obj[14],
    				(String) obj[15], (String) obj[5]);
    		node.setRpt_um_age_id((Integer)obj[0]);
    		node.setDtCreated(((Timestamp) obj[3]).toLocalDateTime());
    		node.setDtModified(((Timestamp) obj[4]).toLocalDateTime());
    		node.setModified_by((String) obj[6]);
    		node.setStatus((String) obj[7]);
    		node.setP_file_size((Integer) obj[16]);
    		node.setP_file_nm((String) obj[17]);
    		node.setTask_id((String) obj[18]);
    		node.setP_batch_no((String) obj[19]);
    		node.setP_fms_ref_no((String) obj[20]);
    		data.add(node);
    	}
    	return data;
    }
    
    public Integer sp_updstatusrptumage(UnmatchedAgingReportRequest req) {
   	 Query query = entityManager.createNativeQuery("CALL sp_updstatusrptumage(:i_rpt_um_age_id, :i_status, :i_modified_by)")
       			 	.setParameter("i_rpt_um_age_id", req.getRpt_um_age_id())
       			 	.setParameter("i_status", req.getStatus())
       			 	.setParameter("i_modified_by", req.getModified_by());
   	 return ((Integer)query.getSingleResult()).intValue();
   }
    
    public UnmatchedAgingReportRequest sp_getrptumage(int id) {
    	Query query = entityManager.createNativeQuery("CALL sp_getrptumage(:i_rpt_um_age_id)")
   			 			.setParameter("i_rpt_um_age_id", id);
    	
    	List<UnmatchedAgingReportRequest> data = convertUMReqObj(query.getResultList());
    	    	
    	//return data.size() == 0 ? null : data.get(0);
		return CollectionUtils.size(data) == 0 ? null : data.get(0);
    }
    
    public Integer sp_getrptumagecount() {
   	 	Query query = entityManager.createNativeQuery("CALL sp_getrptumagecount()");
   	 	return ((Integer)query.getSingleResult()).intValue();
    }
}
