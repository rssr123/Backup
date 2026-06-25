package com.maven.rms.repositories;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import com.maven.rms.models.RICPAgingReportRequest;

import org.apache.commons.collections4.CollectionUtils;

@Repository
@Slf4j
public class RICPAgingReqRepository {
	//private static final Logger logger = LoggerFactory.getLogger(RICPAgingReqRepository.class);
	
    @PersistenceContext
    private EntityManager entityManager;
    
    public Integer sp_insrptricpar(RICPAgingReportRequest req) {
    	
    	Query query = entityManager.createNativeQuery("CALL sp_insrptricpar(:i_p_dt_req, :i_p_dt_iss_fr,"
      	 		+ " :i_p_dt_iss_to, :i_p_exp_status, :i_p_can_v_status, :i_p_dt_rcpt_fr,"
      	 		+ " :i_p_dt_rcpt_to, :i_p_dt_exp_fr, :i_p_dt_exp_to, :i_p_dt_wo_fr, :i_p_dt_wo_to,"
      	 		+ " :i_p_dt_can_fr, :i_p_dt_can_to, :i_p_dt_void_fr, :i_p_dt_void_to, :i_p_ent_ty, :i_p_ent_nm,"
      	 		+ " :i_p_email, :i_p_file_type, :i_status, :i_created_by, :i_modified_by)")
        			 	.setParameter("i_p_dt_req", req.getP_dt_req())
        			 	.setParameter("i_p_dt_iss_fr", req.getP_dt_iss_fr())
        			 	.setParameter("i_p_dt_iss_to", req.getP_dt_iss_to())
        			 	.setParameter("i_p_exp_status", req.getP_exp_status())
        			 	.setParameter("i_p_can_v_status", req.getP_can_v_status())
        			 	.setParameter("i_p_dt_rcpt_fr", req.getP_dt_rcpt_fr())
        			 	.setParameter("i_p_dt_rcpt_to", req.getP_dt_rcpt_to())
        			 	.setParameter("i_p_dt_exp_fr", req.getP_dt_exp_fr())
        			 	.setParameter("i_p_dt_exp_to", req.getP_dt_exp_to())
        			 	.setParameter("i_p_dt_wo_fr", req.getP_dt_wo_fr())
        			 	.setParameter("i_p_dt_wo_to", req.getP_dt_wo_to())
        			 	.setParameter("i_p_dt_can_fr", req.getP_dt_can_fr())
        			 	.setParameter("i_p_dt_can_to", req.getP_dt_can_to())
        			 	.setParameter("i_p_dt_void_fr", req.getP_dt_void_fr())
        			 	.setParameter("i_p_dt_void_to", req.getP_dt_void_to())
        			 	.setParameter("i_p_ent_ty", req.getP_ent_ty())
        			 	.setParameter("i_p_ent_nm", req.getP_ent_nm())
        			 	.setParameter("i_p_email", req.getP_email())
        			 	.setParameter("i_p_file_type", req.getP_file_type())
        			 	.setParameter("i_status", req.getStatus())
        			 	.setParameter("i_created_by", req.getCreated_by())
        			 	.setParameter("i_modified_by",req.getModified_by());    	
        return ((Integer)query.getSingleResult()).intValue();
    }

    public Integer sp_updrptricpagerq(RICPAgingReportRequest req) {
   	 Query query = entityManager.createNativeQuery("CALL sp_updrptricpagerq(:i_rpt_ricp_age_id, :i_status,"
     	 		+ "	:i_p_file_size, :i_p_file_nm, :i_modified_by)")
       			 	.setParameter("i_rpt_ricp_age_id", req.getRpt_ricp_age_id())
       			 	.setParameter("i_status", req.getStatus())
       			 	.setParameter("i_p_file_size", req.getP_file_size())
       			 	.setParameter("i_p_file_nm", req.getP_file_nm())
          			.setParameter("i_modified_by", req.getModified_by());
   	 return ((Integer)query.getSingleResult()).intValue();
   }
    
    public Integer sp_getrptricpagequeue() {
   	 	Query query = entityManager.createNativeQuery("CALL sp_getrptricpagequeue()");
   	 	return ((Integer)query.getSingleResult()).intValue();
    }
    
    public RICPAgingReportRequest sp_getpendingrptricpagerq() {
    	Query query = entityManager.createNativeQuery("CALL sp_getpendingrptricpagerq()");
    	
    	List<RICPAgingReportRequest> data = convertRICPReqObj(query.getResultList());
    	
   	 	// if(data.size() > 1) {
		if(CollectionUtils.size(data) > 1) {
   	 		String idList = data.stream().map(i -> i.getRpt_ricp_age_id().toString()).collect(Collectors.joining(", "));
			log.error("Exception in " + this.getClass().toString() 
					+ " there is more than one RICPAgingReportRequest found! IDs:" + idList);
   	 	}
    	
    	//return data.size() == 0 ? null : data.get(0);
		return CollectionUtils.size(data) == 0 ? null : data.get(0);
    }
    
    public List<RICPAgingReportRequest> sp_getrptricpagelisting(int page, int size) {
    	Query query = entityManager.createNativeQuery("CALL sp_getrptricpagelisting(:i_page, :i_size)")
    			.setParameter("i_page", page)
    			.setParameter("i_size", size);
    	
    	List<RICPAgingReportRequest> data = convertRICPReqObj(query.getResultList());
    	
    	return data;
    }
    
    public List<RICPAgingReportRequest> convertRICPReqObj(List<Object[]> objects){
    	List<RICPAgingReportRequest> data = new ArrayList<>();    	
    	for (Object[] obj : objects) {
    		RICPAgingReportRequest node = new RICPAgingReportRequest(
    				((Timestamp)obj[1]).toLocalDateTime(), ((Timestamp)obj[6]).toLocalDateTime(), 
    				((Timestamp)obj[7]).toLocalDateTime(), (Integer)obj[2], (Integer)obj[3], 
    				(String)obj[18], (String)obj[24]);
    		node.setRpt_ricp_age_id((Integer)obj[0]);
    		node.setP_ent_ty((String)obj[4]);
    		node.setP_ent_nm((String)obj[5]);
    		node.setP_dt_exp_fr(obj[8] == null ? null : ((Timestamp)obj[8]).toLocalDateTime());
    		node.setP_dt_exp_to(obj[9] == null ? null : ((Timestamp)obj[9]).toLocalDateTime());
    		node.setP_dt_wo_fr(obj[10] == null ? null : ((Timestamp)obj[10]).toLocalDateTime());
    		node.setP_dt_wo_to(obj[11] == null ? null : ((Timestamp)obj[11]).toLocalDateTime());
    		node.setP_dt_can_fr(obj[12] == null ? null : ((Timestamp)obj[12]).toLocalDateTime());
    		node.setP_dt_can_to(obj[13] == null ? null : ((Timestamp)obj[13]).toLocalDateTime());
    		node.setP_dt_void_fr(obj[14] == null ? null : ((Timestamp)obj[14]).toLocalDateTime());
    		node.setP_dt_void_to(obj[15] == null ? null : ((Timestamp)obj[15]).toLocalDateTime());
    		node.setDt_created(((Timestamp) obj[16]).toLocalDateTime());
    		node.setDt_modified(((Timestamp) obj[17]).toLocalDateTime());
    		node.setModified_by((String)obj[19]);
    		node.setStatus((String)obj[20]);
    		node.setP_email((String)obj[21]);
    		node.setP_dt_rcpt_fr(obj[22] == null ? null : ((Timestamp)obj[22]).toLocalDateTime());
    		node.setP_dt_rcpt_to(obj[23] == null ? null : ((Timestamp)obj[23]).toLocalDateTime());
    		node.setP_file_size((Integer) obj[25]);
    		node.setP_file_nm((String) obj[26]);
    		node.setP_batch_no((String) obj[27]);
    		node.setP_fms_ref_no((String) obj[28]);
    		node.setTask_id((String) obj[29]);
    		
    		data.add(node);
    	}
    	return data;
    }
    
    public Integer sp_updstatusrptricpagerq(RICPAgingReportRequest req) {
   	 Query query = entityManager.createNativeQuery("CALL sp_updstatusrptricpagerq(:i_rpt_ricp_age_id, :i_status, :i_modified_by)")
       			 	.setParameter("i_rpt_ricp_age_id", req.getRpt_ricp_age_id())
       			 	.setParameter("i_status", req.getStatus())
       			 	.setParameter("i_modified_by", req.getModified_by());
   	 return ((Integer)query.getSingleResult()).intValue();
   }
    
    public RICPAgingReportRequest sp_getrptricpagerq(int id) {
    	Query query = entityManager.createNativeQuery("CALL sp_getrptricpagerq(:i_rpt_ricp_age_id)")
   			 			.setParameter("i_rpt_ricp_age_id", id);
    	// System.out.println(query.unwrap(org.hibernate.Query.class).getQueryString());
		log.debug(query.unwrap(org.hibernate.Query.class).getQueryString());
    	
    	List<RICPAgingReportRequest> data = convertRICPReqObj(query.getResultList());
    	    	
    	// return data.size() == 0 ? null : data.get(0);
		return CollectionUtils.size(data) == 0 ? null : data.get(0);
    }
    
    public Integer sp_getrptricpagecount() {
   	 	Query query = entityManager.createNativeQuery("CALL sp_getrptricpagecount()");
   	 	return ((Integer)query.getSingleResult()).intValue();
    }
}
