package com.maven.rms.repositories;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import com.maven.rms.models.RICP;
import com.maven.rms.models.RICPAudit;
import com.maven.rms.models.RICPCVLog;
import com.maven.rms.models.RICPList;
import com.maven.rms.models.RICPRPRequest;
import com.maven.rms.models.RICPRequest;
import com.maven.rms.models.payload.requests.SubmitRICPCanRequest;
import org.apache.commons.collections4.CollectionUtils;

@Repository
@Slf4j
public class RICPRepository {
	//private static final Logger logger = LoggerFactory.getLogger(RICPRepository.class);
	
    @PersistenceContext
    private EntityManager entityManager;
    
    public Integer sp_insricpandaudit(RICP ricp) {
      	 Query query = entityManager.createNativeQuery("CALL sp_insricpandaudit(:i_txn_type, :i_entity_type, :i_entity_no,"
      	 		+ ":i_calendar_yr, :i_cp_no, :i_cp_act_id, :i_cp_sect_id, :i_cp_sub_sect_id, :i_dt_issuance,"
      	 		+ ":i_dt_expiry, :i_dt_void, :i_dt_cancel, :i_cp_amt, :i_accr_amt,"
      	 		+ ":i_created_by, :i_modified_by, :i_status, :i_cp_tier, :i_cp_tier_amt)")
      			 	.setParameter("i_txn_type", ricp.getTxn_type())
      			 	.setParameter("i_entity_type", ricp.getEntity_type())
      			 	.setParameter("i_entity_no", ricp.getEntity_no())
      			 	.setParameter("i_calendar_yr", ricp.getCalendar_yr())
      			 	.setParameter("i_cp_no", ricp.getCp_no())
      			 	.setParameter("i_cp_act_id", ricp.getCp_act_id())
      			 	.setParameter("i_cp_sect_id", ricp.getCp_sect_id())
      			 	.setParameter("i_cp_sub_sect_id", ricp.getCp_sub_sect_id())
      			 	.setParameter("i_dt_issuance", ricp.getDt_issuance())
      			 	.setParameter("i_dt_expiry", ricp.getDt_expiry())
      			 	.setParameter("i_dt_void", ricp.getDt_void())
      			 	.setParameter("i_dt_cancel", ricp.getDt_cancel())
      			 	.setParameter("i_cp_amt", ricp.getCp_amt())
      			 	.setParameter("i_accr_amt", ricp.getAccr_amt())
      			 	.setParameter("i_created_by", ricp.getCreated_by())
      			 	.setParameter("i_modified_by", ricp.getModified_by())
      			 	.setParameter("i_status", ricp.getStatus())
      			 	.setParameter("i_cp_tier", ricp.getCp_tier())
      			 	.setParameter("i_cp_tier_amt", ricp.getCp_tier_amt());

           return ((BigInteger)query.getSingleResult()).intValue();
       }
    
    public Integer sp_insricp(RICP ricp) {
   	 Query query = entityManager.createNativeQuery("CALL sp_insricp(:i_txn_type, :i_entity_type, :i_entity_no,"
   	 		+ ":i_calendar_yr, :i_cp_no, :i_cp_act_id, :i_cp_sect_id, :i_cp_sub_sect_id, :i_dt_issuance,"
   	 		+ ":i_dt_expiry, :i_dt_void, :i_dt_cancel, :i_dt_writeoff, :i_cp_amt, :i_accr_amt,"
   	 		+ ":i_created_by, :i_modified_by, :i_status, :i_cp_tier, :i_cp_tier_amt)")
   			 	.setParameter("i_txn_type", ricp.getTxn_type())
   			 	.setParameter("i_entity_type", ricp.getEntity_type())
   			 	.setParameter("i_entity_no", ricp.getEntity_no())
   			 	.setParameter("i_calendar_yr", ricp.getCalendar_yr())
   			 	.setParameter("i_cp_no", ricp.getCp_no())
   			 	.setParameter("i_cp_act_id", ricp.getCp_act_id())
   			 	.setParameter("i_cp_sect_id", ricp.getCp_sect_id())
   			 	.setParameter("i_cp_sub_sect_id", ricp.getCp_sub_sect_id())
   			 	.setParameter("i_dt_issuance", ricp.getDt_issuance())
   			 	.setParameter("i_dt_expiry", ricp.getDt_expiry())
   			 	.setParameter("i_dt_void", ricp.getDt_void())
   			 	.setParameter("i_dt_cancel", ricp.getDt_cancel())
   			 	.setParameter("i_dt_writeoff", ricp.getDt_writeoff())
   			 	.setParameter("i_cp_amt", ricp.getCp_amt())
   			 	.setParameter("i_accr_amt", ricp.getAccr_amt())
   			 	.setParameter("i_created_by", ricp.getCreated_by())
   			 	.setParameter("i_modified_by", ricp.getModified_by())
   			 	.setParameter("i_status", ricp.getStatus())
   			 	.setParameter("i_cp_tier", ricp.getCp_tier())
   			 	.setParameter("i_cp_tier_amt", ricp.getCp_tier_amt());

        return (Integer) query.getSingleResult();
    }
    
    public Integer sp_insricpa(RICPAudit ra) {
      	 Query query = entityManager.createNativeQuery("CALL sp_insricpa(:i_ricp_id, :i_dt_txn,"
      			+ ":i_action_type, :i_cp_no, :i_entity_type, :i_entity_no,"
      	 		+ ":i_accr_amt_b4, :i_accr_amt_af, :i_status_b4, :i_status_af,"
      	 		+ ":i_created_by, :i_modified_by, :i_status)")
      			 	.setParameter("i_ricp_id", ra.getI_ricp_id())
      			 	.setParameter("i_dt_txn", ra.getI_dt_txn())
      			 	.setParameter("i_action_type", ra.getI_action_type())
      			 	.setParameter("i_cp_no", ra.getI_cp_no())
      			 	.setParameter("i_entity_type", ra.getI_entity_type())
      			 	.setParameter("i_entity_no", ra.getI_entity_no())
      			 	.setParameter("i_accr_amt_b4", ra.getI_accr_amt_b4())
      			 	.setParameter("i_accr_amt_af", ra.getI_accr_amt_af())
      			 	.setParameter("i_status_b4", ra.getI_status_b4())
      			 	.setParameter("i_status_af", ra.getI_status_af())
      			 	.setParameter("i_created_by", ra.getI_created_by())
      			 	.setParameter("i_modified_by", ra.getI_modified_by())
      			 	.setParameter("i_status", ra.getI_status());

           return (Integer) query.getSingleResult();
       }
    
    public Integer sp_insricpcvlog(RICPCVLog rl) {
      	 Query query = entityManager.createNativeQuery("CALL sp_insricpcvlog(:i_ricp_id,:i_entity_type,"
      	 		+ ":i_entity_no,:i_cp_no,:i_dt_void,:i_dt_cancel,"
      	 		+ ":i_created_by, :i_modified_by, :i_status)")
      			 	.setParameter("i_ricp_id", rl.getI_ricp_id())
      			 	.setParameter("i_entity_type", rl.getI_entity_type())
      			 	.setParameter("i_entity_no", rl.getI_entity_no())
      			 	.setParameter("i_cp_no", rl.getI_cp_no())
      			 	.setParameter("i_dt_void", rl.getI_dt_void())
      			 	.setParameter("i_dt_cancel", rl.getI_dt_cancel())
      			 	.setParameter("i_created_by", rl.getI_created_by())
      			 	.setParameter("i_modified_by", rl.getI_modified_by())
      			 	.setParameter("i_status", rl.getI_status());

           return (Integer) query.getSingleResult();
       }
    
    public List<Integer> sp_getricpidsbyfilter(String i_entity_type, String i_entity_no, 
    		String i_cp_no, String i_status){
   	 	Query query = entityManager.createNativeQuery("CALL sp_getricpidsbyfilter(:i_entity_type, "
   	 		+ ":i_entity_no, :i_cp_no, :i_status)")
             .setParameter("i_entity_type", i_entity_type)
             .setParameter("i_entity_no", i_entity_no)
   	 		 .setParameter("i_cp_no", i_cp_no)
   	 		 .setParameter("i_status", i_status);
   	 
    	List<Integer> ricpIds = new ArrayList<>();
   	 	for (Iterator<Object> iterator = query.getResultList().iterator(); iterator.hasNext();)
   	 		ricpIds.add((Integer) iterator.next());

   	 	return ricpIds;
    }
    
    public List<Integer> sp_getricpidsbyfilterwostatus(String i_entity_type, String i_entity_no, 
    		String i_cp_no){
   	 	Query query = entityManager.createNativeQuery("CALL sp_getricpidsbyfilterwostatus(:i_entity_type, "
   	 		+ ":i_entity_no, :i_cp_no)")
             .setParameter("i_entity_type", i_entity_type)
             .setParameter("i_entity_no", i_entity_no)
   	 		 .setParameter("i_cp_no", i_cp_no);
   	 
    	List<Integer> ricpIds = new ArrayList<>();
   	 	for (Iterator<Object> iterator = query.getResultList().iterator(); iterator.hasNext();)
   	 		ricpIds.add((Integer) iterator.next());

   	 	return ricpIds;
    }
    
    public List<Integer> sp_getricpidsbycpno(String i_cp_no){
   	 	Query query = entityManager.createNativeQuery("CALL sp_getricpidsbycpno(:i_cp_no)")
   	 		 .setParameter("i_cp_no", i_cp_no);
   	 
    	List<Integer> ricpIds = new ArrayList<>();
   	 	for (Iterator<Object> iterator = query.getResultList().iterator(); iterator.hasNext();)
   	 		ricpIds.add((Integer) iterator.next());

   	 	return ricpIds;
    }
    
    public RICP sp_getfullricpidsbyfilter(String i_entity_type, String i_entity_no, 
    		String i_cp_no, String i_status){
   	 	Query query = entityManager.createNativeQuery("CALL sp_getfullricpidsbyfilter(:i_entity_type, "
   	 		+ ":i_entity_no, :i_cp_no, :i_status)")
             .setParameter("i_entity_type", i_entity_type)
             .setParameter("i_entity_no", i_entity_no)
   	 		 .setParameter("i_cp_no", i_cp_no)
   	 		 .setParameter("i_status", i_status);
   	 	
   	 	List<RICP> ricps = convertRICPObject(query.getResultList());
   	 	
   	 	//if(ricps.size() > 1) {
		if(CollectionUtils.size(ricps) > 1) {
   	 		String idList = ricps.stream().map(i -> i.getRicp_id().toString()).collect(Collectors.joining(", "));
			log.error("Exception in " + this.getClass().toString() 
					+ " there is more than one RICP found! IDS:" + idList);
   	 	}
   	 	
   	 	//return ricps.size() > 0 ? ricps.get(0) : null;
		return CollectionUtils.size(ricps) > 0 ? ricps.get(0) : null;
    }
    
    public RICP sp_getfullricpidsbycpno(String i_cp_no, String i_status){
   	 	Query query = entityManager.createNativeQuery("CALL sp_getfullricpidsbycpno(:i_cp_no, :i_status)")
   	 		 .setParameter("i_cp_no", i_cp_no)
   	 		 .setParameter("i_status", i_status);
   	 	
   	 	List<RICP> ricps = convertRICPObject(query.getResultList());
   	 	
   	 	//if(ricps.size() > 1) {
		if(CollectionUtils.size(ricps) > 1) {
   	 		String idList = ricps.stream().map(i -> i.getRicp_id().toString()).collect(Collectors.joining(", "));
			log.error("Exception in " + this.getClass().toString() 
					+ " there is more than one RICP found! IDS:" + idList);
   	 	}
   	 	
   	 	// return ricps.size() > 0 ? ricps.get(0) : null;
		return CollectionUtils.size(ricps) > 0 ? ricps.get(0) : null;
    }
    
    public List<RICP> sp_getricpwriteoffs(String i_status){
    	Query query = entityManager.createNativeQuery("CALL sp_getricpwriteoffs(:i_status)")
       	 		 .setParameter("i_status", i_status);
       	 	
       	 	List<RICP> ricps = convertRICPObject(query.getResultList());
       	 	return ricps;
    }
    
    private List<RICP> convertRICPObject(List<Object[]> objects){

   	 	List<RICP> ricps = new ArrayList<>();
   	 	for (Object[] obj : objects) {
   	 		RICP ricp = new RICP();
   	 		ricp.setRicp_id((Integer) obj[0]);
   	 		ricp.setTxn_type((String) obj[1]);
   	 		ricp.setEntity_type((String) obj[2]);
   	 		ricp.setEntity_no((String) obj[3]);
   	 		ricp.setCalendar_yr((String) obj[4]);
   	 		ricp.setCp_no((String) obj[5]);
   	 		ricp.setCp_act_id((String) obj[6]);   	 		
   	 		ricp.setCp_sect_id((String) obj[7]);
   	 		ricp.setCp_sub_sect_id((String) obj[8]);   	 		
   	 		ricp.setDt_issuance((Date) obj[9]);
   	 		ricp.setDt_expiry((Date) obj[10]);   	 		
   	 		ricp.setDt_void((Date) obj[11]);
   	 		ricp.setDt_cancel((Date) obj[12]);   	 		
   	 		ricp.setDt_writeoff((Date) obj[13]);
   	 		ricp.setCp_amt((BigDecimal) obj[14]); 		
   	 		ricp.setAccr_amt((BigDecimal) obj[15]);
   	 		ricp.setDt_created(((Timestamp) obj[16]).toLocalDateTime());	
   	 		ricp.setDt_modified(((Timestamp)obj[17]).toLocalDateTime());
   	 		ricp.setCreated_by((String) obj[18]);   	 		
   	 		ricp.setModified_by((String) obj[19]);
   	 		ricp.setStatus((String) obj[20]);
   	 		ricp.setCp_tier((Integer) obj[21]);   	 		
   	 		ricp.setCp_tier_amt((BigDecimal) obj[22]);

   	 		ricps.add(ricp);
   	 	}
   	 	return ricps;
    }
    
    public Integer sp_updricp(SubmitRICPCanRequest req, String auditAction, String oldStatus, String username){
		//log.error("sp_updricp called mtt_item_id: " + req.getMtt_item_id() + ", entity_type: " + req.getEntity_type() + ", entity_no: " + req.getEntity_no() + ", cp_no: " + req.getCp_no() + ", status: " + req.getStatus() + ", auditAction: " + auditAction + ", oldStatus: " + oldStatus + ", username: " + username);
    	Query query = entityManager.createNativeQuery("CALL sp_updricp(:i_mtt_item_id, :i_entity_type, :i_entity_no,"
   	 			+ ":i_cp_no, :i_status, :i_audit_action, :i_old_status, :i_modified_by)")
    		 .setParameter("i_mtt_item_id", req.getMtt_item_id())
             .setParameter("i_entity_type", req.getEntity_type())
   	 		 .setParameter("i_entity_no", req.getEntity_no())
   	 		 .setParameter("i_cp_no", req.getCp_no())
   	 		 .setParameter("i_status", req.getStatus())
   	 		 .setParameter("i_audit_action", auditAction)
   	 		 .setParameter("i_old_status", oldStatus)
   	 		 .setParameter("i_modified_by", username);
		//log.error(((BigInteger)query.getSingleResult()).toString());
        return ((BigInteger)query.getSingleResult()).intValue();
    }   
    
    public Integer sp_updricpstatus(Integer i_ricp_id, String i_status, String username){
   	 	Query query = entityManager.createNativeQuery("CALL sp_updricpstatus(:i_ricp_id, :i_status, :i_modified_by)")
             .setParameter("i_ricp_id", i_ricp_id)
   	 		 .setParameter("i_status", i_status)
   	 		 .setParameter("i_modified_by", username);
   	 
   	 	return (Integer) query.getSingleResult();
    }
    
    public Integer sp_updricpmttitemid(Integer i_ricp_id, Integer i_mtt_item_id, String username){
   	 	Query query = entityManager.createNativeQuery("CALL sp_updricpmttitemid(:i_ricp_id, :i_mtt_item_id, :i_modified_by)")
             .setParameter("i_ricp_id", i_ricp_id)
   	 		 .setParameter("i_mtt_item_id", i_mtt_item_id)
   	 		 .setParameter("i_modified_by", username);
   	 
   	 	return (Integer) query.getSingleResult();
    }

	// public RICPList sp_getricp(Integer i_page, Integer i_size, RICP ricp) {

    //       Query query = entityManager.createNativeQuery(
    //                 "CALL sp_getricp(:i_page, :i_size,:i_ricp_id, :i_entity_type, :i_entity_no, :i_cp_no, :i_dt_issuance, :i_dt_expiry, :i_cp_amt, :i_accr_amt, :i_cp_tier, :i_cp_tier_amt, :i_status)")
    //                 .setParameter("i_page", i_page)
    //                 .setParameter("i_size", i_size)
	// 				.setParameter("i_ricp_id", ricp.getRicp_id())
    //                 .setParameter("i_entity_type", ricp.getEntity_type())
    //                 .setParameter("i_entity_no", ricp.getEntity_no())
    //                 .setParameter("i_cp_no", ricp.getCp_no())
    //                 .setParameter("i_dt_issuance", ricp.getDt_issuance())
    //                 .setParameter("i_dt_expiry", ricp.getDt_expiry())
    //                 .setParameter("i_cp_amt", ricp.getCp_amt())
    //                 .setParameter("i_accr_amt", ricp.getAccr_amt())
    //                 .setParameter("i_cp_tier", ricp.getCp_tier())
    //                 .setParameter("i_cp_tier_amt", ricp.getCp_tier_amt())
	// 				.setParameter("i_status", ricp.getStatus());

    //       // Check if the dates are null and set accordingly
    //       if (ricp.getDt_issuance() != null) {
    //            query.setParameter("i_dt_issuance", ricp.getDt_issuance());
    //       } else {
    //            query.setParameter("i_dt_issuance", null);
    //       }

    //       if (ricp.getDt_issuance() != null) {
    //            query.setParameter("i_dt_expiry", ricp.getDt_expiry());
    //       } else {
    //            query.setParameter("i_dt_expiry", null);
    //       }
          
    //       List<Object[]> objList = query.getResultList();
          
    //       return new RICPList(convertRICPObject(objList), (Integer) objList.get(0)[23]);
    //  }

	public RICPList sp_getricp(RICPRequest ricp) {

		Query query = entityManager.createNativeQuery(
				  "CALL sp_getricp(:i_page, :i_size,:i_ricp_id, :i_entity_type, :i_entity_no, :i_cp_no, :i_dt_issuance, :i_dt_expiry, :i_cp_amt, :i_accr_amt, :i_cp_tier, :i_cp_tier_amt, :i_status)")
				  .setParameter("i_page", ricp.getI_page())
				  .setParameter("i_size", ricp.getI_size())
				  .setParameter("i_ricp_id", ricp.getI_ricp_id())
				  .setParameter("i_entity_type", ricp.getI_entity_type())
				  .setParameter("i_entity_no", ricp.getI_entity_no())
				  .setParameter("i_cp_no", ricp.getI_cp_no())
				  .setParameter("i_dt_issuance", ricp.getI_dt_issuance())
				  .setParameter("i_dt_expiry", ricp.getI_dt_expiry())
				  .setParameter("i_cp_amt", ricp.getI_cp_amt())
				  .setParameter("i_accr_amt", ricp.getI_accr_amt())
				  .setParameter("i_cp_tier", ricp.getI_cp_tier())
				  .setParameter("i_cp_tier_amt", ricp.getI_cp_tier_amt())
				  .setParameter("i_status", ricp.getI_status());

		// Check if the dates are null and set accordingly
		if (ricp.getI_dt_issuance() != null) {
			 query.setParameter("i_dt_issuance", ricp.getI_dt_issuance());
		} else {
			 query.setParameter("i_dt_issuance", null);
		}

		if (ricp.getI_dt_issuance() != null) {
			 query.setParameter("i_dt_expiry", ricp.getI_dt_expiry());
		} else {
			 query.setParameter("i_dt_expiry", null);
		}
		
		List<Object[]> objList = query.getResultList();
		
		return new RICPList(convertRICPObject(objList), (Integer) objList.get(0)[23]);
   }


   public Integer sp_updricprp(RICPRPRequest ricprpRequestRequest){
		Query query = entityManager.createNativeQuery("CALL sp_updricprp(:i_cp_no, :i_cp_tier, :i_cp_tier_amt, :i_cp_tier_disc_pct, :i_accr_amt, :i_dt_collection)")
					.setParameter("i_cp_no", ricprpRequestRequest.getCp_no())
					.setParameter("i_cp_tier", ricprpRequestRequest.getCp_tier())
					.setParameter("i_cp_tier_amt", ricprpRequestRequest.getCp_tier_amt())
					.setParameter("i_cp_tier_disc_pct", ricprpRequestRequest.getCp_tier_disc_pct())
					.setParameter("i_accr_amt", ricprpRequestRequest.getAccr_amt())
					.setParameter("i_dt_collection", ricprpRequestRequest.getDt_collection());
		
		return ((BigInteger)query.getSingleResult()).intValue();
	}  

}
