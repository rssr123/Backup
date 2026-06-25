package com.maven.rms.repositories;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.maven.rms.models.BillingStatusRequest;
import com.maven.rms.models.Billing.Billing;
import com.maven.rms.models.payload.requests.BillingRegistrationIncoming;
import com.maven.rms.services.BillingService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Repository
public class BillingRepository {

    @PersistenceContext
    private EntityManager entityManager;
	@Value("${rms.application.emailExpiryInDay}")
	private Integer emailExpiryDay;
    
    public HashMap<String, List<Object>> sp_getbillingregistrationtypecodelist(){
   	 Query query = entityManager.createNativeQuery("CALL sp_getbillingregistrationtypecodelist()");
   	 HashMap<String, List<Object>> map = new HashMap<String, List<Object>>();
   	 List<Object[]> objects = query.getResultList();
   	 for (Object[] obj : objects) {
   		 if(map.containsKey((String)(obj[0]))){
   			if(obj[2] != null)
   				((List<String[]>)(map.get((String)(obj[0])).get(1))).add(new String[]{(String)(obj[2]),
   						 ((BigDecimal)(obj[3])).toString(),((BigDecimal)(obj[4])).toString(), (String)(obj[5]),
   						 ((Integer)(obj[6])).toString(), obj[7] == null ? null : (String)(obj[7])});
   		 }
   		 else {
   			 List<Object> items = new ArrayList<Object>();
   			 List<String[]> bItems = new ArrayList<String[]>();
   			 if(obj[2] != null) {
   				items.add((String)(obj[1]));
   				bItems.add(new String[]{(String)(obj[2]),
   						((BigDecimal)(obj[3])).toString(),((BigDecimal)(obj[4])).toString(), 
   						(String)(obj[5]), ((Integer)(obj[6])).toString(), obj[7] == null ? null : (String)(obj[7])});
   			 }
   			 else
    			items.add((String)(obj[1]));
			items.add(bItems);

   			 map.put((String)(obj[0]), items);
   		 }
   	 }
   	 return map;
   }
    
    public String sp_getfreerunnofullBilling() {
    	Query query = entityManager.createNativeQuery("CALL sp_getfreerunnofull('BIL')");
   	 	return query.getSingleResult() == null ? "" : (String) query.getSingleResult();
    }
    
    public Integer sp_checkbillreginfo(Map<String, String> paramCheckList){
        Query query = entityManager.createNativeQuery("CALL sp_checkbillreginfo("
        		+ ":i_bt_code,:i_state,:i_entity_type,:i_sscode)")
                  .setParameter("i_bt_code", paramCheckList.get("i_bt_code"))
                  .setParameter("i_state", paramCheckList.get("i_state"))
                  .setParameter("i_entity_type", paramCheckList.get("i_entity_type"))
                  .setParameter("i_sscode", paramCheckList.get("i_sscode"));

        Integer result = (Integer)query.getSingleResult();
        return result;
    }
    
    public String sp_getandreservebillrunno(int reserveBillingNoCount) {
   	 	Query query = entityManager.createNativeQuery("CALL sp_getandreservebillrunno(:i_number_to_reserve)")
                .setParameter("i_number_to_reserve", reserveBillingNoCount);
   	 	
   	 	return query.getSingleResult() == null ? "" : (String) query.getSingleResult();
    }
    
    public Integer sp_rollbacknewbilreg(Integer bilWfId){
    	Query query = entityManager.createNativeQuery("CALL sp_rollbacknewbilreg("
    			+ ":i_bil_wf_id)")
                .setParameter("i_bil_wf_id", bilWfId);
        return (Integer)query.getSingleResult();
    }
    
    public Integer sp_updatebillstatuspaid(String billingNo, String username) {
    	Query query = entityManager.createNativeQuery("CALL sp_updatebillstatuspaid(:i_billing_no,:i_user)")
                  .setParameter("i_billing_no", billingNo)
                  .setParameter("i_user", username);
        return (Integer)query.getSingleResult();
    }
    
    public Integer sp_insnewbilreg(BillingRegistrationIncoming payload, String username){
    	DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    	Query query = entityManager.createNativeQuery("CALL sp_insnewbilreg("
    			+ ":i_bt_cd,:i_req_name,:i_req_email,:i_ss_cd,:i_billing_no,:i_billing_desc,"
    			+ ":i_billing_cnt,:i_billing_freq,:i_loa_id,:i_agm_id,:i_dt_loa_start,"
    			+ ":i_dt_loa_end,:i_dt_agm_start,:i_dt_agm_end,:i_cust_id,:i_cust_nm,:i_cust_email,"
    			+ ":i_cust_phone,:i_cust_addr1,:i_cust_addr2,:i_cust_addr3,:i_cust_postcode,"
    			+ ":i_cust_city,:i_cust_state,:i_ent_nm,:i_ent_no,:i_ent_ty,:i_msg,:i_billing_method,:i_created_by)")
    			.setParameter("i_bt_cd", payload.getI_bt_code())
    			.setParameter("i_req_name", payload.getI_requester_name())
    			.setParameter("i_req_email", payload.getI_requester_email())
    			.setParameter("i_ss_cd", payload.getI_sscode())
    			.setParameter("i_billing_no", payload.getI_billing_no())
    			.setParameter("i_billing_desc", payload.getI_billing_desc())
    			.setParameter("i_billing_cnt", payload.getI_billing_cnt())
    			.setParameter("i_billing_freq", payload.getI_billing_freq())
    			.setParameter("i_loa_id", payload.getI_loa_ref_no())
    			.setParameter("i_agm_id", payload.getI_agmt_ref_no())
    			.setParameter("i_dt_loa_start", payload.getI_billing_method().equals("L") ?
    												payload.getI_date_range().get(0) : null)
    			.setParameter("i_dt_loa_end", payload.getI_billing_method().equals("L") ?
													payload.getI_date_range().get(1) : null)
    			.setParameter("i_dt_agm_start", payload.getI_billing_method().equals("A") ?
													payload.getI_date_range().get(0) : null)
    			.setParameter("i_dt_agm_end", payload.getI_billing_method().equals("A") ?
													payload.getI_date_range().get(1) : null)
    			.setParameter("i_cust_id", payload.getI_cust_id())
    			.setParameter("i_cust_nm", payload.getI_cus_name())
    			.setParameter("i_cust_email", payload.getI_cus_email())
    			.setParameter("i_cust_phone", payload.getI_cus_phno())
    			.setParameter("i_cust_addr1", payload.getI_cus_add1())
    			.setParameter("i_cust_addr2", payload.getI_cus_add2())
    			.setParameter("i_cust_addr3", payload.getI_cus_add3())
    			.setParameter("i_cust_postcode", payload.getI_postcode())
    			.setParameter("i_cust_city", payload.getI_city())
    			.setParameter("i_cust_state", payload.getI_state())
    			.setParameter("i_ent_nm", payload.getI_entity_name())
    			.setParameter("i_ent_no", payload.getI_entity_no())
    			.setParameter("i_ent_ty", payload.getI_entity_type())
    			.setParameter("i_msg", payload.getI_remarks())
    			.setParameter("i_billing_method", payload.getI_billing_method())
    			.setParameter("i_created_by", username);
    	
        return (Integer)query.getSingleResult();
    }
   
    public Integer sp_insnewbilregitem(Integer bilWfId, Integer bilId, Map<String, Object> item, String username) {
    	Query query = entityManager.createNativeQuery("CALL sp_insnewbilregitem(:i_bil_wf_id,:i_bil_id,"
    			+ ":i_fee_detail_id,:i_unit_fee,:i_qty,:i_tax_pct,:i_tax_amt,:i_final_amt,:i_status,:i_created_by, :i_mft_pk)")
    			.setParameter("i_bil_wf_id", bilWfId)
    			.setParameter("i_bil_id", bilId)
    			.setParameter("i_fee_detail_id", ((String)item.get("desc")).split(" - ")[0])
    			.setParameter("i_unit_fee", item.get("price"))
    			.setParameter("i_qty", item.get("qty"))
    			.setParameter("i_tax_pct", item.get("tax"))
    			.setParameter("i_tax_amt", item.get("taxc"))
    			.setParameter("i_final_amt", item.get("total"))
    			.setParameter("i_status", item.get("status") == null ? "A" : (String)item.get("status"))
    			.setParameter("i_mft_pk", item.get("mftPk"))
    			.setParameter("i_created_by", username);
    	
        return (Integer)query.getSingleResult();
    }
    
    public Integer sp_updbilwfitem(Map<String, Object> item, String username) {
    	Query query = entityManager.createNativeQuery("CALL sp_updbilwfitem(:i_billing_no,:i_unit_fee,"
    			+ ":i_qty,:i_tax_amt,:i_final_amt,:i_modified_by, :i_mft_pk)")
    			.setParameter("i_billing_no", (String)item.get("billing_no"))
    			.setParameter("i_unit_fee", item.get("price"))
    			.setParameter("i_qty", item.get("qty"))
    			.setParameter("i_tax_amt", item.get("taxc"))
    			.setParameter("i_final_amt", item.get("total"))
    			.setParameter("i_mft_pk", item.get("mftPk"))
    			.setParameter("i_modified_by", username);
    	
        return (Integer)query.getSingleResult();
    }
    
    public Integer sp_updbilwfDet(Map<String, Object> data, String username) {
    	Query query = entityManager.createNativeQuery("CALL sp_updbilwfDet(:i_billing_no,:i_req_name,:i_req_email,"
    			+ ":i_billing_desc,:i_loa_id,:i_agm_id,:i_cust_nm,:i_cust_email,"
    			+ ":i_cust_phone,:i_cust_addr1,:i_cust_addr2,:i_cust_addr3,:i_cust_postcode,"
    			+ ":i_cust_city,:i_cust_state,:i_ent_nm,:i_ent_ty,:i_user)")
    			.setParameter("i_billing_no", (String)data.get("i_billing_no"))
    			.setParameter("i_req_name", (String)data.get("i_req_name"))
    			.setParameter("i_req_email", (String)data.get("i_req_email"))
    			.setParameter("i_billing_desc", (String)data.get("i_billing_desc"))
    			.setParameter("i_loa_id", (String)data.get("i_loa_id"))
    			.setParameter("i_agm_id", (String)data.get("i_agm_id"))
    			.setParameter("i_cust_nm", (String)data.get("i_cust_nm"))
    			.setParameter("i_cust_email", (String)data.get("i_cust_email"))
    			.setParameter("i_cust_phone", (String)data.get("i_cust_phone"))
    			.setParameter("i_cust_addr1", (String)data.get("i_cust_addr1"))
    			.setParameter("i_cust_addr2", (String)data.get("i_cust_addr2"))
    			.setParameter("i_cust_addr3", (String)data.get("i_cust_addr3"))
    			.setParameter("i_cust_postcode", (String)data.get("i_cust_postcode"))
    			.setParameter("i_cust_city", (String)data.get("i_cust_city"))
    			.setParameter("i_cust_state", (String)data.get("i_cust_state"))
    			.setParameter("i_ent_nm", (String)data.get("i_ent_nm"))
    			.setParameter("i_ent_ty", (String)data.get("i_ent_ty"))
    			.setParameter("i_user", username);
    	
        return (Integer)query.getSingleResult();
    }
    
    public Integer sp_insnewbilregchild(Map<String, Object> item) {
    	Query query = entityManager.createNativeQuery("CALL sp_insnewbilregchild("
    			+ ":i_bil_wf_id, :i_bil_id,:i_bil_child_date, :i_billing_no,:i_created_by)")
    			.setParameter("i_bil_wf_id", item.get("i_bil_wf_id") == null ? null : (Integer)item.get("i_bil_wf_id"))
    			.setParameter("i_bil_id", item.get("i_bil_id") == null ? null : (Integer)item.get("i_bil_id"))
    			.setParameter("i_bil_child_date", (LocalDateTime)item.get("i_bil_child_date"))
    			.setParameter("i_billing_no", (String)item.get("i_billing_no"))
    			.setParameter("i_created_by", (String)item.get("i_created_by"));
    	
        return (Integer)query.getSingleResult();
    }
    
    public Integer sp_insnewbilregdoc(Integer bilWfId, Map<String, Object> item, String username) throws SerialException, SQLException {
        byte[] decodedBytes = decodeBase64((String)item.get("i_file_content"));
        Blob blob = new SerialBlob(decodedBytes);

        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_insnewbilregdoc");
        // Register parameters (ensure parameter names and types match those defined in the stored procedure)
        storedProcedureQuery.registerStoredProcedureParameter("i_bil_wf_id", Integer.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_nm", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_content", Blob.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_type", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_size", Integer.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_category", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_created_by", String.class, javax.persistence.ParameterMode.IN);

        // Set parameters
        storedProcedureQuery.setParameter("i_bil_wf_id", bilWfId);
        storedProcedureQuery.setParameter("i_file_nm", item.get("i_file_nm"));
        storedProcedureQuery.setParameter("i_file_content", blob);
        storedProcedureQuery.setParameter("i_file_type", item.get("i_file_type"));
        storedProcedureQuery.setParameter("i_file_size", item.get("i_file_size"));
        storedProcedureQuery.setParameter("i_file_category", item.get("i_file_category"));
        storedProcedureQuery.setParameter("i_created_by", username);
      
        // Execute stored procedure
        storedProcedureQuery.execute();

        // Handle the result (if the stored procedure returns a result set or an output parameter)
        // For example, if the stored procedure returns a single integer result:
        Integer result = 0;
        if (storedProcedureQuery.getResultList().size() > 0) {
            result = (Integer) storedProcedureQuery.getSingleResult();
        }
        return result;
    }

    private byte[] decodeBase64(String base64String) {
        if (base64String.startsWith("data:")) {
            base64String = base64String.substring(base64String.indexOf(',') + 1);
        }
        base64String = base64String.replaceAll("\\s", "").replace(":", "");
        return Base64.getDecoder().decode(base64String);
    }
    
    public Billing sp_getbilcust(Billing data) {
    	Query query = entityManager.createNativeQuery("CALL sp_getbilcust(:i_billing_no)")
                .setParameter("i_billing_no", data.getBilling_no());
    	
    	Object[] obj = (Object[]) query.getSingleResult();
    	data.setCust_id((String)obj[1]);
    	data.setCust_nm((String)obj[2]);
    	data.setCust_email((String)obj[3]);
    	data.setCust_phone((String)obj[4]);
    	data.setCust_addr1((String)obj[5]);
    	data.setCust_addr2((String)obj[6]);
    	data.setCust_addr3((String)obj[7]);
    	data.setCust_postcode((String)obj[8]);
    	data.setCust_city((String)obj[9]);
    	data.setCust_state((String)obj[10]);
    	data.setEnt_nm((String)obj[11]);
    	data.setEnt_no((String)obj[12]);
    	data.setEnt_ty((String)obj[13]);
    	
    	return data;
    }
          
    public List<Map<String, Object>> sp_getbilhist(String billingNo, int page, int size) {
    	Query query = entityManager.createNativeQuery("CALL sp_getbilhist(:i_page, :i_size, :i_billing_no)")
                .setParameter("i_page", page)
                .setParameter("i_size", size)
                .setParameter("i_billing_no", billingNo);   	

	   	List<Object[]> objects = query.getResultList();
	   	List<Map<String, Object>> history = new ArrayList<Map<String, Object>>();
	   	
	   	for(Object[] obj : objects) {
	   		Map<String, Object> historyData = new HashMap<String, Object>();
	   		historyData.put("remark", (String)obj[3]);
	   		historyData.put("msg_type", (String)obj[4]);
	   		historyData.put("action_timestamp", ((java.sql.Timestamp)obj[5]).toLocalDateTime());
	   		historyData.put("performer", (String)obj[11]);
	   		historyData.put("assignee", (String)obj[12]);
	   		historyData.put("action", (String)obj[10]);
	   		historyData.put("bil_wf_status", ((String)obj[13]).trim());
	   			   		
	   		history.add(historyData);
	   	}
	   	
	   	
    	return history;
    }
    
    public Integer sp_getbilhisttotal(String billingNo) {
    	Query query = entityManager.createNativeQuery("CALL sp_getbilhisttotal(:i_billing_no)")
                .setParameter("i_billing_no", billingNo);   	

    	return (Integer)query.getSingleResult();
    }
    
    public List<Map<String, Object>> sp_getbilitems(String billingNo, int page, int size, String bilItemStatus) {
    	Query query = entityManager.createNativeQuery("CALL sp_getbilitems(:i_page, :i_size, :i_billing_no, :i_status)")
                .setParameter("i_page", page)
                .setParameter("i_size", size)
                .setParameter("i_billing_no", billingNo)
                .setParameter("i_status", bilItemStatus);

	   	List<Object[]> objects = query.getResultList();
	   	List<Map<String, Object>> billingItems = new ArrayList<Map<String, Object>>();

	   	for(Object[] obj : objects) {
	   		Map<String, Object> billingItem = new HashMap<String, Object>();
	   		billingItem.put("mft_desc_en", (String)obj[3]);
	   		billingItem.put("mft_desc_bm", (String)obj[4]);
	   		billingItem.put("unit_fee", (BigDecimal)obj[5]);
	   		billingItem.put("qty", (Integer)obj[6]);
	   		billingItem.put("tax_pct", (BigDecimal)obj[7]);
	   		billingItem.put("tax_amt", (BigDecimal)obj[8]);
	   		billingItem.put("final_amt", (BigDecimal)obj[9]);
	   		billingItem.put("mft_pk", (Integer)obj[10]);
	   		billingItems.add(billingItem);
	   	}
    	return billingItems;
    }
    
    public Integer sp_getbilitemstotal(String billingNo) {
    	Query query = entityManager.createNativeQuery("CALL sp_getbilitemstotal(:i_billing_no)")
                .setParameter("i_billing_no", billingNo);   	

    	return (Integer)query.getSingleResult();
    }
    
    public List<Map<String, Object>> sp_getbilchildren(String billingNo, int page, int size) {
    	Query query = entityManager.createNativeQuery("CALL sp_getbilchildren(:i_page, :i_size, :i_billing_no)")
                .setParameter("i_page", page)
                .setParameter("i_size", size)
                .setParameter("i_billing_no", billingNo);    

	   	List<Object[]> objects = query.getResultList();
	   	List<Map<String, Object>> billingList = new ArrayList<Map<String, Object>>();

	   	for(Object[] obj : objects) {
	   		Map<String, Object> billChild = new HashMap<String, Object>();
	   		billChild.put("bil_child_id", (Integer)obj[0]);
	   		billChild.put("bil_child_date", ((java.sql.Timestamp)obj[3]).toLocalDateTime());
	   		billChild.put("bil_no", (String)obj[4]);
	   		billChild.put("bil_status", (String)obj[5]);
	   		billChild.put("billing_img", (Integer)obj[12]);
	   		billChild.put("is_expired", obj[13] != null ? (Integer)obj[13] : 0);
	   		
	   		billingList.add(billChild);
	   	}
    	return billingList;
    }
    
    public Map<String, Object> sp_getbilchild(String billingNo) {
    	Query query = entityManager.createNativeQuery("CALL sp_getbilchild(:i_billing_no)")
                .setParameter("i_billing_no", billingNo);    

	   	Object[] obj = (Object[]) query.getSingleResult();

   		Map<String, Object> billChild = new HashMap<String, Object>();
   		billChild.put("bil_child_id", (Integer)obj[0]);
   		billChild.put("bil_child_date", ((java.sql.Timestamp)obj[3]).toLocalDateTime());
   		billChild.put("bil_no", (String)obj[4]);
   		billChild.put("bil_status", (String)obj[5]);
   		billChild.put("billing_img", (Integer)obj[12]);
   		billChild.put("is_expired", obj[13] != null ? (Integer)obj[13] : 0);
	   		
    	return billChild;
    }
    
    public Integer sp_getbilchildrentotal(String billingNo) {
    	Query query = entityManager.createNativeQuery("CALL sp_getbilchildrentotal(:i_billing_no)")
                .setParameter("i_billing_no", billingNo);   	

    	return (Integer)query.getSingleResult();
    }
    
    public byte[] sp_getbilchildimgblob(Integer bilChildId) throws SQLException, IOException {
    	Query query = entityManager.createNativeQuery("CALL sp_getbilchildimgblob(:i_bil_child_id)")
                .setParameter("i_bil_child_id", bilChildId);
        Blob data = (Blob) query.getSingleResult();
        if(data == null) {
        	//throw new NullPointerException("No blob data found!");
			log.error("Exception in " + this.getClass().toString() 
			+ "No BLOB data found!");
			return null;
        }
        byte[] bytes = data.getBytes(1, (int) data.length());
        data.free();
        return bytes;
    }
    
    public Integer sp_insbilchildimg(Integer bilChildId, String data) throws SerialException, SQLException {
        byte[] decodedBytes = decodeBase64(data);
        Blob blob = new SerialBlob(decodedBytes);

        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_insbilchildimg");
        // Register parameters (ensure parameter names and types match those defined in the stored procedure)
        storedProcedureQuery.registerStoredProcedureParameter("i_bil_child_id", Integer.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_billing_img", Blob.class, javax.persistence.ParameterMode.IN);

        // Set parameters
        storedProcedureQuery.setParameter("i_bil_child_id", bilChildId);
        storedProcedureQuery.setParameter("i_billing_img", blob);
      
        // Execute stored procedure
        storedProcedureQuery.execute();

        // Handle the result (if the stored procedure returns a result set or an output parameter)
        // For example, if the stored procedure returns a single integer result:
        /*
        Integer result = 0;
        if (storedProcedureQuery.getResultList().size() > 0) {
            result = (Integer) storedProcedureQuery.getSingleResult();
        }
         */
        return 1;
    }
    
    public Integer sp_checkbilchildimg(Integer bilChildId) {
    	Query query = entityManager.createNativeQuery("CALL sp_checkbilchildimg(:i_bil_child_id)")
                .setParameter("i_bil_child_id", bilChildId);   	

    	return (Integer)query.getSingleResult();
    }
    
    public List<Map<String, Object>> sp_getbildoc(String billingNo, int page, int size) {
    	Query query = entityManager.createNativeQuery("CALL sp_getbildoc(:i_page, :i_size, :i_billing_no)")
                .setParameter("i_page", page)
                .setParameter("i_size", size)
                .setParameter("i_billing_no", billingNo);    

	   	List<Object[]> objects = query.getResultList();
	   	List<Map<String, Object>> docList = new ArrayList<Map<String, Object>>();

	   	for(Object[] obj : objects) {
	   		Map<String, Object> document = new HashMap<String, Object>();
	   		document.put("bil_doc_id", (Integer)obj[0]);
	   		document.put("file_nm", (String)obj[3]);
	   		document.put("file_type", (String)obj[4]);
	   		document.put("file_size", (Integer)obj[5]);
	   		document.put("file_category", (String)obj[6]);
	   		document.put("timestamp", ((java.sql.Timestamp)obj[7]).toLocalDateTime());
	   		document.put("uploader", (String)obj[9]);
	   		
	   		docList.add(document);
	   	}
    	return docList;
    }
    
    public Integer sp_getbildoctotal(String billingNo) {
    	Query query = entityManager.createNativeQuery("CALL sp_getbildoctotal(:i_billing_no)")
                .setParameter("i_billing_no", billingNo);   	

    	return (Integer)query.getSingleResult();
    }

    public Blob sp_getbildocblob(Integer bilDocId) throws SQLException, IOException {
    	Query query = entityManager.createNativeQuery("CALL sp_getbildocblob(:i_bil_doc_id)")
                .setParameter("i_bil_doc_id", bilDocId);
        return (Blob) query.getSingleResult();
    }
    
    public Integer sp_rejectunapprovedbilreg(String billingNo, String username, String remark) {
        Query query = entityManager.createNativeQuery("CALL sp_rejectunapprovedbilreg(:i_billing_no, :i_user, :i_remark)")
                .setParameter("i_billing_no", billingNo)
                .setParameter("i_user", username)
                .setParameter("i_remark", remark);
    	return (Integer)query.getSingleResult();
    }
    
    public Integer sp_cancelunapprovedbilreg(String billingNo, String username, String remark) {
        Query query = entityManager.createNativeQuery("CALL sp_cancelunapprovedbilreg(:i_billing_no, :i_user, :i_remark)")
                .setParameter("i_billing_no", billingNo)
                .setParameter("i_user", username)
                .setParameter("i_remark", remark);
    	return (Integer)query.getSingleResult();
    }
    
    public Integer sp_queryunapprovedbilreg(String billingNo, String username, String remark) {
        Query query = entityManager.createNativeQuery("CALL sp_queryunapprovedbilreg(:i_billing_no, :i_user, :i_remark)")
                .setParameter("i_billing_no", billingNo)
                .setParameter("i_user", username)
                .setParameter("i_remark", remark);
    	return (Integer)query.getSingleResult();
    }
    
    public Integer sp_approvebilreg(String billingNo, String username, String remark) {
        Query query = entityManager.createNativeQuery("CALL sp_approvebilreg(:i_billing_no, :i_user, :i_remark)")
                .setParameter("i_billing_no", billingNo)
                .setParameter("i_user", username)
                .setParameter("i_remark", remark);
    	return (Integer)query.getSingleResult();
    }
    
    public Integer sp_confirmnewbill(String billingNo, String username) {
        Query query = entityManager.createNativeQuery("CALL sp_confirmnewbill(:i_billing_no, :i_user, :i_expiry)")
                .setParameter("i_billing_no", billingNo)
                .setParameter("i_user", username)
                .setParameter("i_expiry", LocalDateTime.now().plusDays(emailExpiryDay.longValue()));
    	return (Integer)query.getSingleResult();
    }
    
    public Map<String, Object> sp_getbilllisting(Map<String, Object> request){
    	DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    	Query query = entityManager.createNativeQuery("CALL sp_getbilllisting(:i_page, :i_size, :i_ent_nm, :i_ent_no,"
    			+ " :i_ss_cd, :i_receipt_no, :i_billing_mthd, :i_bil_wf_status, :i_dt_start, :i_dt_end, :i_b_type,"
    			+ " :i_billing_no, :i_created_by)")
    			.setParameter("i_page", request.get("i_page"))
                .setParameter("i_size", request.get("i_size"))
                .setParameter("i_ent_nm", request.get("i_ent_nm") == null ? "" : request.get("i_ent_nm"))
                .setParameter("i_ent_no", request.get("i_ent_no") == null ? "" : request.get("i_ent_no"))
                .setParameter("i_ss_cd", request.get("i_ss_cd") == null ? "" : request.get("i_ss_cd"))
                .setParameter("i_receipt_no", request.get("i_ss_cd") == null ? "" : request.get("i_receipt_no"))
                .setParameter("i_billing_mthd", request.get("i_billing_mthd") == null ? "" : request.get("i_billing_mthd"))
                .setParameter("i_bil_wf_status", request.get("i_bil_wf_status") == null ? "" : request.get("i_bil_wf_status"))
                .setParameter("i_dt_start", request.get("i_dt_start") != null ? LocalDateTime.parse((String)request.get("i_dt_start"),inputFormatter):null)
		        .setParameter("i_dt_end", request.get("i_dt_end") != null ? LocalDateTime.parse((String)request.get("i_dt_end"),inputFormatter):null)
		        .setParameter("i_b_type", request.get("i_b_type") == null ? "" : request.get("i_b_type"))
		        .setParameter("i_billing_no", request.get("i_billing_no") == null ? "" : request.get("i_billing_no"))
		        .setParameter("i_created_by", request.get("i_created_by") == null ? "" : request.get("i_created_by"));
    	
     	List<Object[]> objects = query.getResultList();
   		Map<String, Object> data = new HashMap<String, Object>();  

	   	List<Map<String, Object>> billList = new ArrayList<Map<String, Object>>();
	   	
	   	for(Object[] obj : objects) {
	   		Map<String, Object> bill = new HashMap<String, Object>();    	
	   		bill.put("cust_id", (String)obj[0]);
	   		bill.put("ent_nm", (String)obj[1]);
	   		bill.put("ent_no", (String)obj[2]);
	   		bill.put("billing_no", (String)obj[3]);
	   		bill.put("amount", obj[4] == null ? new BigDecimal(0) : (BigDecimal)obj[4]);
	   		bill.put("billing_method", (String)obj[5]);
	   		bill.put("bil_wf_status", ((String)obj[6]).trim());
	   		bill.put("receipt_no", (String)obj[7]);
	   		bill.put("req_name", (String)obj[8]);
	   		bill.put("issuance", (String)obj[9]);
	   		bill.put("action", (String)obj[10]);
	   		bill.put("created_by", (String)obj[11]);
	   		bill.put("bil_id", obj[12] != null ? (Integer)obj[12] : 0);
	   		billList.add(bill);
	   	}
	   	  	
	   	data.put("billing_list", billList);
	   	
	   	Query query2 = entityManager.createNativeQuery("CALL sp_getbilllistingtotal(:i_ent_nm, :i_ent_no,"
    			+ " :i_ss_cd, :i_receipt_no, :i_billing_mthd, :i_bil_wf_status, :i_dt_start, :i_dt_end, :i_b_type,"
    			+ " :i_billing_no, :i_created_by)")
                .setParameter("i_ent_nm", request.get("i_ent_nm") == null ? "" : request.get("i_ent_nm"))
                .setParameter("i_ent_no", request.get("i_ent_no") == null ? "" : request.get("i_ent_no"))
                .setParameter("i_ss_cd", request.get("i_ss_cd") == null ? "" : request.get("i_ss_cd"))
                .setParameter("i_receipt_no", request.get("i_ss_cd") == null ? "" : request.get("i_receipt_no"))
                .setParameter("i_billing_mthd", request.get("i_billing_mthd") == null ? "" : request.get("i_billing_mthd"))
                .setParameter("i_bil_wf_status", request.get("i_bil_wf_status") == null ? "" : request.get("i_bil_wf_status"))
                .setParameter("i_dt_start", request.get("i_dt_start") != null ? LocalDateTime.parse((String)request.get("i_dt_start"),inputFormatter):null)
		        .setParameter("i_dt_end", request.get("i_dt_end") != null ? LocalDateTime.parse((String)request.get("i_dt_end"),inputFormatter):null)
		        .setParameter("i_b_type", request.get("i_b_type") == null ? "" : request.get("i_b_type"))
		        .setParameter("i_billing_no", request.get("i_billing_no") == null ? "" : request.get("i_billing_no"))
		        .setParameter("i_created_by", request.get("i_created_by") == null ? "" : request.get("i_created_by"));

	   	data.put("total", (Integer)query2.getSingleResult());
    	return data;
    }
    
    public List<String> sp_getapprovedbilltoissue(){
    	Query query = entityManager.createNativeQuery("CALL sp_getapprovedbilltoissue()");
    	/*
    	List<Object> objects = query.getResultList(); 
    	List<String> billingNumbers = new ArrayList<String>();
    	for(Object obj : objects)
    		billingNumbers.add((String)obj);
    	 */
		// Since the stored procedure returns a single column, cast the result directly to a list of String
		List<String> billingNumbers = query.getResultList();
    	return billingNumbers;
    }
    
    public Map<String, Object> sp_getbilllistingcanadj(Map<String, Object> request){
    	DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

    	Query query = entityManager.createNativeQuery("CALL sp_getbilllistingcanadj(:i_page, :i_size, :i_ent_nm, :i_ent_no,"
    			+ " :i_ss_cd, :i_receipt_no, :i_billing_mthd, :i_bil_wf_status, :i_dt_start, :i_dt_end, :i_b_type, :i_billing_no"
    			+ ", :i_cust_id, :i_orn_no, :i_ent_ty, :i_created_by)")
    			.setParameter("i_page", request.get("i_page"))
                .setParameter("i_size", request.get("i_size"))
                .setParameter("i_ent_nm", request.get("i_ent_nm") == null ? "" : request.get("i_ent_nm"))
                .setParameter("i_ent_no", request.get("i_ent_no") == null ? "" : request.get("i_ent_no"))
                .setParameter("i_ss_cd", request.get("i_ss_cd") == null ? "" : request.get("i_ss_cd"))
                .setParameter("i_receipt_no", request.get("i_ss_cd") == null ? "" : request.get("i_receipt_no"))
                .setParameter("i_billing_mthd", request.get("i_billing_mthd") == null ? "" : request.get("i_billing_mthd"))
                .setParameter("i_bil_wf_status", request.get("i_bil_wf_status") == null ? "" : request.get("i_bil_wf_status"))
                .setParameter("i_dt_start", request.get("i_dt_start") != null ? LocalDateTime.parse((String)request.get("i_dt_start"),inputFormatter):null)
		        .setParameter("i_dt_end", request.get("i_dt_end") != null ? LocalDateTime.parse((String)request.get("i_dt_end"),inputFormatter):null)
		        .setParameter("i_b_type", request.get("i_b_type") == null ? "" : request.get("i_b_type"))
		        .setParameter("i_billing_no", request.get("i_billing_no") == null ? "" : request.get("i_billing_no"))
		        .setParameter("i_cust_id", request.get("i_cust_id") == null ? "" : request.get("i_cust_id"))
		        .setParameter("i_orn_no", request.get("i_orn_no") == null ? "" : request.get("i_orn_no"))
		        .setParameter("i_ent_ty", request.get("i_ent_ty") == null ? "" : request.get("i_ent_ty"))
		        .setParameter("i_created_by", request.get("i_created_by") == null ? "" : request.get("i_created_by"));
    	
     	List<Object[]> objects = query.getResultList();
   		Map<String, Object> data = new HashMap<String, Object>();    

	   	List<Map<String, Object>> billList = new ArrayList<Map<String, Object>>();
	   	
	   	for(Object[] obj : objects) {
	   		Map<String, Object> bill = new HashMap<String, Object>();    	
	   		bill.put("cust_id", (String)obj[0]);
	   		bill.put("ent_nm", (String)obj[1]);
	   		bill.put("ent_no", (String)obj[2]);
	   		bill.put("billing_no", (String)obj[3]);
	   		bill.put("amount", obj[4] == null ? new BigDecimal(0) : (BigDecimal)obj[4]);
	   		bill.put("billing_method", (String)obj[5]);
	   		bill.put("bil_wf_status", ((String)obj[6]).trim());
	   		bill.put("receipt_no", (String)obj[7]);
	   		bill.put("req_name", (String)obj[8]);
	   		bill.put("issuance", (String)obj[9]);
	   		bill.put("action", (String)obj[10]);
	   		bill.put("created_by", (String)obj[11]);
	   		bill.put("ent_ty", (String)obj[12]);
	   		bill.put("bt_cd", (String)obj[13]);
	   		bill.put("bt_desc", (String)obj[14]);
	   		bill.put("unpaid", (Integer)obj[15]);
	   		bill.put("dt_modified", ((java.sql.Timestamp)obj[16]).toLocalDateTime());
	   		bill.put("modified_by", (String)obj[17]);
	   		billList.add(bill);
	   	}
	   	
   		data.put("billing_list", billList);
	   	
	   	Query query2 = entityManager.createNativeQuery("CALL sp_getbilllistingcanadjtotal(:i_ent_nm, :i_ent_no,"
    			+ " :i_ss_cd, :i_receipt_no, :i_billing_mthd, :i_bil_wf_status, :i_dt_start, :i_dt_end, :i_b_type, :i_billing_no"
    			+ ", :i_cust_id, :i_orn_no, :i_ent_ty, :i_created_by)")

                .setParameter("i_ent_nm", request.get("i_ent_nm") == null ? "" : request.get("i_ent_nm"))
                .setParameter("i_ent_no", request.get("i_ent_no") == null ? "" : request.get("i_ent_no"))
                .setParameter("i_ss_cd", request.get("i_ss_cd") == null ? "" : request.get("i_ss_cd"))
                .setParameter("i_receipt_no", request.get("i_ss_cd") == null ? "" : request.get("i_receipt_no"))
                .setParameter("i_billing_mthd", request.get("i_billing_mthd") == null ? "" : request.get("i_billing_mthd"))
                .setParameter("i_bil_wf_status", request.get("i_bil_wf_status") == null ? "" : request.get("i_bil_wf_status"))
                .setParameter("i_dt_start", request.get("i_dt_start") != null ? LocalDateTime.parse((String)request.get("i_dt_start"),inputFormatter):null)
		        .setParameter("i_dt_end", request.get("i_dt_end") != null ? LocalDateTime.parse((String)request.get("i_dt_end"),inputFormatter):null)
		        .setParameter("i_b_type", request.get("i_b_type") == null ? "" : request.get("i_b_type"))
		        .setParameter("i_billing_no", request.get("i_billing_no") == null ? "" : request.get("i_billing_no"))
		        .setParameter("i_cust_id", request.get("i_cust_id") == null ? "" : request.get("i_cust_id"))
		        .setParameter("i_orn_no", request.get("i_orn_no") == null ? "" : request.get("i_orn_no"))
		        .setParameter("i_ent_ty", request.get("i_ent_ty") == null ? "" : request.get("i_ent_ty"))
		        .setParameter("i_created_by", request.get("i_created_by") == null ? "" : request.get("i_created_by"));

   		data.put("total", (Integer)query2.getSingleResult());
    	return data;
    }
    
    public Map<String, Object> sp_getcancelbillist(Map<String, Object> request){
    	DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

    	Query query = entityManager.createNativeQuery("CALL sp_getcancelbillist(:i_page, :i_size,"
    			+ " :i_billing_no, :i_cust_id, :i_bil_wf_status, :i_dt_start, :i_dt_end, :i_created_by)")
    			.setParameter("i_page", request.get("i_page"))
                .setParameter("i_size", request.get("i_size"))
		        .setParameter("i_billing_no", request.get("i_billing_no") == null ? "" : request.get("i_billing_no"))
		        .setParameter("i_cust_id", request.get("i_cust_id") == null ? "" : request.get("i_cust_id"))
                .setParameter("i_bil_wf_status", request.get("i_bil_wf_status") == null ? "" : request.get("i_bil_wf_status"))
                .setParameter("i_dt_start", request.get("i_dt_start") != null ? LocalDateTime.parse((String)request.get("i_dt_start"),inputFormatter):null)
		        .setParameter("i_dt_end", request.get("i_dt_end") != null ? LocalDateTime.parse((String)request.get("i_dt_end"),inputFormatter):null)
		        .setParameter("i_created_by", request.get("i_created_by") == null ? "" : request.get("i_created_by"));
    	
     	List<Object[]> objects = query.getResultList();
     	Map<String, Object> data =  new HashMap<String, Object>();
	   	List<Map<String, Object>> billList = new ArrayList<Map<String, Object>>();
	   	
	   	for(Object[] obj : objects) {
	   		Map<String, Object> bill = new HashMap<String, Object>();    	
	   		bill.put("cust_id", (String)obj[0]);
	   		bill.put("billing_no", (String)obj[1]);
	   		bill.put("bt_cd", (String)obj[2]);
	   		bill.put("bt_desc", (String)obj[3]);
	   		bill.put("amount", obj[4] == null ? new BigDecimal(0) : (BigDecimal)obj[4]);
	   		bill.put("bil_wf_status", ((String)obj[5]).trim());
	   		bill.put("can_status", (String)obj[6]);
	   		bill.put("req_name", (String)obj[7]);
	   		bill.put("action", (String)obj[8]);
	   		bill.put("created_by", (String)obj[9]);
	   		bill.put("dt_modified", ((java.sql.Timestamp)obj[10]).toLocalDateTime());
	   		bill.put("modified_by", (String)obj[11]);
	   		bill.put("unpaid", (Integer)obj[12]);
	   		billList.add(bill);
	   	}
	   	
   		data.put("billing_list", billList);
	   	
	   	Query query2 = entityManager.createNativeQuery("CALL sp_getcancelbillistotal("
    			+ ":i_billing_no, :i_cust_id, :i_bil_wf_status, :i_dt_start, :i_dt_end, :i_created_by)")
	   			.setParameter("i_billing_no", request.get("i_billing_no") == null ? "" : request.get("i_billing_no"))
		        .setParameter("i_cust_id", request.get("i_cust_id") == null ? "" : request.get("i_cust_id"))
                .setParameter("i_bil_wf_status", request.get("i_bil_wf_status") == null ? "" : request.get("i_bil_wf_status"))
                .setParameter("i_dt_start", request.get("i_dt_start") != null ? LocalDateTime.parse((String)request.get("i_dt_start"),inputFormatter):null)
		        .setParameter("i_dt_end", request.get("i_dt_end") != null ? LocalDateTime.parse((String)request.get("i_dt_end"),inputFormatter):null)
		        .setParameter("i_created_by", request.get("i_created_by") == null ? "" : request.get("i_created_by"));

   		data.put("total", (Integer)query2.getSingleResult());
    	return data;
    }
        

    public Billing sp_getbill(Map<String, Object> request) {
    	String billingNo = (String)request.get("billing_no");
    	Integer page = (Integer)request.get("page");
    	Integer size = (Integer)request.get("size");
    	String bilItemStatus = (String)request.get("bil_item_status");
    	boolean moreInfo = (Boolean)request.get("more_info");

    	Query query = entityManager.createNativeQuery("CALL sp_getbill(:i_billing_no)")
                .setParameter("i_billing_no", billingNo);   

    	Object[] obj = (Object[]) query.getSingleResult();
    	Billing data = new Billing();
    	data.setBil_wf_id(obj[0] != null ? (Integer)obj[0] : null);
    	data.setBltc_id((Integer)obj[1]);
    	data.setBilcust_id((Integer)obj[2]);
    	data.setReq_name((String)obj[3]);
    	data.setReq_email((String)obj[4]);
    	data.setSs_cd((String)obj[5]);
    	data.setBilling_no((String)obj[6]);
    	data.setBilling_desc((String)obj[7]);
    	data.setAction((String)obj[8]);
    	data.setDps_amt((BigDecimal) obj[9]);
    	data.setBilling_cnt((Integer)obj[10]);
    	data.setBilling_freq((String)obj[11]);
    	data.setLoa_id(obj[12] != null ?(String)obj[12] : null);
    	data.setDt_loa_start(obj[13] != null ? ((java.sql.Timestamp)obj[13]).toLocalDateTime() : null);
    	data.setDt_loa_end(obj[14] != null ? ((java.sql.Timestamp)obj[14]).toLocalDateTime() : null);
    	data.setAgm_id(obj[15] != null ?(String)obj[15] : null);
    	data.setDt_agm_start(obj[16] != null ? ((java.sql.Timestamp)obj[16]).toLocalDateTime() : null);
    	data.setDt_agm_end(obj[17] != null ? ((java.sql.Timestamp)obj[17]).toLocalDateTime() : null);
    	data.setBil_wf_status(((String)obj[18]).trim());
    	data.setPickup_by(obj[19] != null ? (String)obj[19] : null);
    	data.setDt_pick(obj[20] != null ? ((java.sql.Timestamp)obj[20]).toLocalDateTime() : null);
    	data.setBilling_mthd(obj[21] != null ? (String)obj[21] : "");
    	data.setDt_created(((java.sql.Timestamp)obj[22]).toLocalDateTime());
    	data.setDt_modified(((java.sql.Timestamp)obj[23]).toLocalDateTime());
    	data.setCreated_by((String)obj[24]);
    	data.setModified_by((String)obj[25]);
    	data.setStatus((String)obj[26]);
    	data.setBil_id(obj[27] != null ? (Integer)obj[27] : null);
    	data.setBilling_origin(obj[28] != null ? (String)obj[28] : "S");
    	data.setHas_query(obj[29] != null ? (Integer)obj[29] : null);
    	
		data = sp_getbilcust(data);
		data.setHistory_size(sp_getbilhisttotal(data.getBilling_no()));
		data.setItems_size(sp_getbilitemstotal(data.getBilling_no()));
		data.setIssuance_size(sp_getbilchildrentotal(data.getBilling_no()));
		data.setDocuments_size(sp_getbildoctotal(data.getBilling_no()));
    	if(moreInfo) {
    		data.setHistory(sp_getbilhist(data.getBilling_no(), page, size));
    		data.setBilling_items(sp_getbilitems(data.getBilling_no(), page, size, bilItemStatus));
    		data.setBilling_list(sp_getbilchildren(data.getBilling_no(), page, size));
    		data.setDocuments_list(sp_getbildoc(data.getBilling_no(), page, size));
    	}
    		
    	return data;
    }
    
    public Integer sp_checkbilstatusvalid(String billingNo) {
    	Query query = entityManager.createNativeQuery("CALL sp_checkbilstatusvalid(:i_billing_no)")
                .setParameter("i_billing_no", billingNo); 
        Integer result = (Integer)query.getSingleResult();
        return result == null ? -1 : result;
    }
    
    public Integer sp_cancelbilreq(String billingNo, String username, String remark) {
        Query query = entityManager.createNativeQuery("CALL sp_cancelbilreq(:i_billing_no, :i_user, :i_remark)")
                .setParameter("i_billing_no", billingNo)
                .setParameter("i_user", username)
                .setParameter("i_remark", remark);
    	return (Integer)query.getSingleResult();
    }
    
    public Integer sp_querybilcan(String billingNo, String username, String remark) {
        Query query = entityManager.createNativeQuery("CALL sp_querybilcan(:i_billing_no, :i_user, :i_remark)")
                .setParameter("i_billing_no", billingNo)
                .setParameter("i_user", username)
                .setParameter("i_remark", remark);
    	return (Integer)query.getSingleResult();
    }
    
    public Integer sp_rejectbilcan(String billingNo, String username, String remark) {
        Query query = entityManager.createNativeQuery("CALL sp_rejectbilcan(:i_billing_no, :i_user, :i_remark)")
                .setParameter("i_billing_no", billingNo)
                .setParameter("i_user", username)
                .setParameter("i_remark", remark);
    	return (Integer)query.getSingleResult();
    }
    
    public Integer sp_approvebilcan(String billingNo, String username, String remark) {
		//System.out.println("CALL sp_approvebilcan('" + billingNo + "', '" + username + "', '" + remark + "');");
        Query query = entityManager.createNativeQuery("CALL sp_approvebilcan(:i_billing_no, :i_user, :i_remark)")
                .setParameter("i_billing_no", billingNo)
                .setParameter("i_user", username)
                .setParameter("i_remark", remark);
    	return (Integer)query.getSingleResult();
    }
    
    public Integer sp_adjbilreq(String billingNo, String username, String remark) {
        Query query = entityManager.createNativeQuery("CALL sp_adjbilreq(:i_billing_no, :i_user, :i_remark)")
                .setParameter("i_billing_no", billingNo)
                .setParameter("i_user", username)
                .setParameter("i_remark", remark);
    	return (Integer)query.getSingleResult();
    }
    
    public Integer sp_querybiladj(String billingNo, String username, String remark) {
        Query query = entityManager.createNativeQuery("CALL sp_querybiladj(:i_billing_no, :i_user, :i_remark)")
                .setParameter("i_billing_no", billingNo)
                .setParameter("i_user", username)
                .setParameter("i_remark", remark);
    	return (Integer)query.getSingleResult();
    }
    
    public Integer sp_rejectbiladj(String billingNo, String username, String remark) {
        Query query = entityManager.createNativeQuery("CALL sp_rejectbiladj(:i_billing_no, :i_user, :i_remark)")
                .setParameter("i_billing_no", billingNo)
                .setParameter("i_user", username)
                .setParameter("i_remark", remark);
    	return (Integer)query.getSingleResult();
    }
    
    public Integer sp_approvebiladj(String billingNo, String username, String remark) {
        Query query = entityManager.createNativeQuery("CALL sp_approvebiladj(:i_billing_no, :i_user, :i_remark)")
                .setParameter("i_billing_no", billingNo)
                .setParameter("i_user", username)
                .setParameter("i_remark", remark);
        try {
        	return (Integer)query.getSingleResult();
        }catch(NoResultException e) {
        	return -5;
        }
    }
    
    public Integer sp_rollbackadjbilreq(Integer bilWfId, Integer bilId) {
        Query query = entityManager.createNativeQuery("CALL sp_rollbackadjbilreq(:i_bil_wf_id, :i_bil_id)")
                .setParameter("i_bil_wf_id", bilWfId)
                .setParameter("i_bil_id", bilId);
    	return (Integer)query.getSingleResult();
    }
    
    public List<Map<String, Object>> sp_latestDifferenceAftAdj(String billingNo){
       	Query query = entityManager.createNativeQuery("CALL sp_latestDifferenceAftAdj(:i_billing_no)")
                .setParameter("i_billing_no", billingNo);   
       	
     	List<Object[]> objects = query.getResultList();
     	List<Map<String, Object>> data =  new ArrayList<Map<String, Object>>();
     	
    	for(Object[] obj : objects) {
	   		Map<String, Object> set = new HashMap<String, Object>();    	
	   		set.put("bil_id", (Integer)obj[0]);
	   		set.put("mft_pk", (Integer)obj[1]);
	   		set.put("unit_fee", (BigDecimal) obj[2]);
	   		set.put("qty", (Integer)obj[3]);
	   		set.put("tax_pct", (BigDecimal) obj[4]);
	   		set.put("tax_amt", (BigDecimal) obj[5]);
	   		set.put("final_amount", (BigDecimal) obj[6]);
	   		
	   		data.add(set);
    	}
    	return data;
    }
    
    public Integer sp_getexistloa(String loaRef) { //LOA Ref that exists in both rms_wf_bil and rms_bil table
        Query query = entityManager.createNativeQuery("CALL sp_getexistloa(:i_loa_ref)")
                .setParameter("i_loa_ref", loaRef);
    	return (Integer)query.getSingleResult();
    }
    
    public Integer sp_getregisteredloa(String loaRef) {	//Only LOA that is in rms_bil table
        Query query = entityManager.createNativeQuery("CALL sp_getregisteredloa(:i_loa_ref)")
                .setParameter("i_loa_ref", loaRef);
    	return (Integer)query.getSingleResult();
    }

	public Integer sp_insbilhistbybillno(String billNo, String username, String remark, String msgType) {
		//System.out.println("CALL sp_insbilhistbybillno('" + billNo + "', '" + username + "', '" + remark +  "', '" + msgType + "');");
        Query query = entityManager.createNativeQuery("CALL sp_insbilhistbybillno(:i_bill_no, :i_user, :i_remark, :i_msg_type)")
                .setParameter("i_bill_no", billNo)
                .setParameter("i_user", username)
                .setParameter("i_remark", remark)
                .setParameter("i_msg_type", msgType);
        Integer data = -1;
        try {
        	data = (Integer)query.getSingleResult();
        } catch(EmptyResultDataAccessException | NoResultException  e) {
        	log.error("Exception in " + this.getClass().toString() + ": No Result detected for sp_insbilhistbybillno", e);
        }
        
    	return data;
	}

    //billing issuance by source system start
    
    public Integer sp_getbilmethod(String billingNo) {
        Query query = entityManager.createNativeQuery("CALL sp_getbilmethod(:i_billing_no)")
                .setParameter("i_billing_no", billingNo);
    	return (Integer)query.getSingleResult();
    }

	public List<Object[]> sp_getbillingstatus(BillingStatusRequest bilRequest) {
		Query query = entityManager.createNativeQuery("CALL sp_getbillingstatus(:billing_no)")
			.setParameter("billing_no", bilRequest.getBilling_no());
		return query.getResultList();
	}
	
	//billing issuance by source system end
}
