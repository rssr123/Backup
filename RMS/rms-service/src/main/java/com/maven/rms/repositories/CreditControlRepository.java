package com.maven.rms.repositories;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.ICreditControlRepository;
import com.maven.rms.models.CreditControlCase;
import com.maven.rms.models.CreditControlCaseRmd;
import com.maven.rms.models.CreditControlReminderRequest;
import com.maven.rms.models.CreditControlPaidInvoiceRequest;

@Repository
public class CreditControlRepository implements ICreditControlRepository{

    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public Integer sp_insccrmd(CreditControlReminderRequest ccRmdRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_insccrmd(:i_fms_ari_ref_no, :i_reminder_cnt, :i_reminder_dt, :i_reminder_email_content, :i_reminder_received_date)")
                    .setParameter("i_fms_ari_ref_no", ccRmdRequest.getFms_ari_ref_no())
                    .setParameter("i_reminder_cnt", ccRmdRequest.getReminder_cnt())
                    .setParameter("i_reminder_dt", ccRmdRequest.getReminder_dt())
                    .setParameter("i_reminder_email_content", ccRmdRequest.getReminder_email_content())
                    .setParameter("i_reminder_received_date", ccRmdRequest.getReminder_received_date());
                
        return (Integer) query.getSingleResult();
    }
    
    public Integer sp_inscasereminder(CreditControlReminderRequest ccRmdRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_inscasereminder(:fms_ari_ref_no, :reminder_cnt, :reminder_dt, :reminder_email_content, :reminder_received_date)")
                    .setParameter("fms_ari_ref_no", ccRmdRequest.getFms_ari_ref_no())
                    .setParameter("reminder_cnt", ccRmdRequest.getReminder_cnt())
                    .setParameter("reminder_dt", ccRmdRequest.getReminder_dt())
                    .setParameter("reminder_email_content", ccRmdRequest.getReminder_email_content())
                    .setParameter("reminder_received_date", ccRmdRequest.getReminder_received_date());
	        
        return (Integer) query.getSingleResult();
	}

    @Override
    public Integer sp_updcccasestatus(CreditControlPaidInvoiceRequest ccPaidInvRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_updcccasestatus(:i_fms_ari_ref_no)")
                    .setParameter("i_fms_ari_ref_no", ccPaidInvRequest.getFms_ari_ref_no());
                
        return (Integer) query.getSingleResult();
    }

    
    public CreditControlCase sp_getcccase(Map<String, Object> request) {
    	String taskNo = (String)request.get("task_no");
    	Integer page = (Integer)request.get("page");
    	Integer size = (Integer)request.get("size");
    	Boolean skipFK = (Boolean)request.get("skipFK");
    	
    	Query query = entityManager.createNativeQuery("CALL sp_getcccase(:i_task_no)")
                .setParameter("i_task_no", taskNo);   

    	Object[] obj = (Object[]) query.getSingleResult();
    	CreditControlCase data = new CreditControlCase();
    	data.setInvoiceInformation(new CreditControlCase.InvoiceInformation());
    	data.setCustomerInformation(new CreditControlCase.CustomerInformation());
    	data.setPaymentInformation(new CreditControlCase.PaymentInformation());
    	data.setCreditMemoInformation(new CreditControlCase.CreditMemoInformation());
    	data.setDebitMemoInformation(new CreditControlCase.DebitMemoInformation());
    	
    	data.setCc_case_id(obj[0] != null ? (Integer)obj[0] : null);
    	data.setCc_cust_id(obj[1] != null ? (Integer)obj[1] : null);
    	data.getInvoiceInformation().setFms_ari_ref_no(obj[2] != null ? (String)obj[2] : null);
    	data.getInvoiceInformation().setInv_cust_id(obj[3] != null ? (String)obj[3] : null);
    	data.getInvoiceInformation().setCur_doc_bal(obj[4] != null ? (BigDecimal)obj[4] : null);
    	data.getInvoiceInformation().setAttr_case_no(obj[5] != null ? (String)obj[5] : null);
    	data.getPaymentInformation().setPymt_ty(obj[6] != null ? (String)obj[6] : null);
    	data.getPaymentInformation().setPymt_ref_no(obj[7] != null ? (String)obj[7] : null);
    	data.getPaymentInformation().setPymt_dt_application(obj[8] != null ? ((java.sql.Date)obj[8]) : null);
    	data.getPaymentInformation().setPymt_ref(obj[9] != null ? (String)obj[9] : null);
    	data.getPaymentInformation().setPymt_attr_doc_no(obj[10] != null ? (String)obj[10] : null);
    	data.getPaymentInformation().setPymt_attr_doc_ty(obj[11] != null ? (String)obj[11] : null);
    	data.getPaymentInformation().setPymt_amt(obj[12] != null ? (BigDecimal)obj[12] : null);
    	data.getPaymentInformation().setPymt_status(obj[13] != null ? (String)obj[13] : null);
    	data.getPaymentInformation().setTxn_ty(obj[14] != null ? (String)obj[14] : null);
    	data.getPaymentInformation().setRef_no_txn(obj[15] != null ? (String)obj[15] : null);
    	data.getPaymentInformation().setRcpt_no(obj[16] != null ? (String)obj[16] : null);
   		data.getCreditMemoInformation().setCn_type(obj[17] != null ? (String)obj[17] : null);
   		data.getCreditMemoInformation().setCn_ref_no(obj[18] != null ? (String)obj[18] : null);
   		data.getCreditMemoInformation().setCn_cust_orn(obj[19] != null ? (String)obj[19] : null);
   		data.getCreditMemoInformation().setCn_amt(obj[20] != null ? (BigDecimal)obj[20] : null);
   		data.getCreditMemoInformation().setCn_desc(obj[21] != null ? (String)obj[21] : null);
   		data.getCreditMemoInformation().setCn_branch(obj[22] != null ? (String)obj[22] : null);
   		data.getCreditMemoInformation().setCn_coa1(obj[23] != null ? (String)obj[23] : null);
   		data.getCreditMemoInformation().setCn_coa2(obj[24] != null ? (String)obj[24] : null);
   		data.getCreditMemoInformation().setCn_acct(obj[25] != null ? (String)obj[25] : null);
   		data.getCreditMemoInformation().setCn_sub_acct(obj[26] != null ? (String)obj[26] : null);
   		data.getCreditMemoInformation().setCn_qty(obj[27] != null ? (Integer)obj[27] : null);
   		data.getCreditMemoInformation().setCn_unit_price(obj[28] != null ? (BigDecimal)obj[28] : null);
   		data.getCreditMemoInformation().setCn_disc_amt(obj[29] != null ? (BigDecimal)obj[29] : null);
   		data.getDebitMemoInformation().setDn_type(obj[30] != null ? (String)obj[30] : null);
   		data.getDebitMemoInformation().setDn_ref_no(obj[31] != null ? (String)obj[31] : null);
   		data.getDebitMemoInformation().setDn_cust_orn(obj[32] != null ? (String)obj[32] : null);
   		data.getDebitMemoInformation().setDn_amt(obj[33] != null ? (BigDecimal)obj[33] : null);
   		data.getDebitMemoInformation().setDn_desc(obj[34] != null ? (String)obj[34] : null);
   		data.getDebitMemoInformation().setDn_branch(obj[35] != null ? (String)obj[35] : null);
   		data.getDebitMemoInformation().setDn_coa1(obj[36] != null ? (String)obj[36] : null);
   		data.getDebitMemoInformation().setDn_coa2(obj[37] != null ? (String)obj[37] : null);
   		data.getDebitMemoInformation().setDn_acct(obj[38] != null ? (String)obj[38] : null);
   		data.getDebitMemoInformation().setDn_sub_acct(obj[39] != null ? (String)obj[39] : null);
   		data.getDebitMemoInformation().setDn_qty(obj[40] != null ? (Integer)obj[40] : null);
   		data.getDebitMemoInformation().setDn_unit_price(obj[41] != null ? (BigDecimal)obj[41] : null);
   		data.getDebitMemoInformation().setDn_disc_amt(obj[42] != null ? (BigDecimal)obj[42] : null);    	
   		data.getInvoiceInformation().setInvoice_desc(obj[43] != null ? (String)obj[43] : null);
    	data.setTxn_total_amt(obj[44] != null ? (BigDecimal)obj[44] : null);
    	data.setPick_up(obj[45] != null ? (String)obj[45] : null);
    	data.setAssign_to(obj[46] != null ? (String)obj[46] : null);
    	data.setDt_pickup(obj[47] != null ? ((java.sql.Timestamp)obj[47]).toLocalDateTime() : null);
    	data.setDt_assigned(obj[48] != null ? ((java.sql.Timestamp)obj[48]).toLocalDateTime() : null);
    	data.setDecision_amt(obj[49] != null ? (BigDecimal)obj[49] : null);
    	data.setTask_status(obj[50] != null ? (String)obj[50] : null);
    	data.setTask_no(obj[51] != null ? (String)obj[51] : null);
    	data.setDt_created(obj[52] != null ? ((java.sql.Timestamp)obj[52]).toLocalDateTime() : null);
    	data.setDt_modified(obj[53] != null ? ((java.sql.Timestamp)obj[53]).toLocalDateTime() : null);
    	data.setCreated_by(obj[54] != null ? (String)obj[54] : null);
    	data.setModified_by(obj[55] != null ? (String)obj[55] : null);
    	data.setStatus(obj[56] != null ? (String)obj[56] : null);
    	data.setInv_ty(obj[57] != null ? (String)obj[57] : null);
    	
    	if(skipFK)
    		return data;
    	
    	data = sp_getcccasecust(data);

    	Map<String, Object> reminders = sp_getcccasermd(data.getCc_case_id());
    	data.setReminders((List<CreditControlCaseRmd>) reminders.get("reminders"));
    	data.setReminders_size((Integer) reminders.get("total"));
    	
    	Map<String, Object> items = sp_getcccasecsitems(taskNo, page, size);
    	data.setItemInformation((List<CreditControlCase.ItemInformation>) items.get("items"));
    	data.setPayment_items_size((Integer) items.get("total"));
    	
    	Map<String, Object> hist = sp_getcccasehist(taskNo, page, size);
    	data.setHistory((List<Map<String, Object>>) hist.get("history"));
    	data.setHistory_size((Integer) hist.get("total"));
    	
    	Map<String, Object> documents = sp_getcccasedoc(taskNo, page, size);
    	data.setDocuments_list((List<Map<String, Object>>) documents.get("docList"));
    	data.setDocuments_size((Integer) documents.get("total"));
    	
    	return data;
    }
    
    public CreditControlCase sp_getcccasecust(CreditControlCase data) {
    	Query query = entityManager.createNativeQuery("CALL sp_getcccasecust(:i_cc_cust_id)")
                .setParameter("i_cc_cust_id", data.getCc_cust_id());
    	
    	Object[] obj = (Object[]) query.getSingleResult();
    	data.getCustomerInformation().setCust_nm(obj[0] != null ? (String)obj[0] : null);
    	data.getCustomerInformation().setCust_id_ty(obj[1] != null ? (String)obj[1] : null);
    	data.getCustomerInformation().setCust_id_no(obj[2] != null ? (String)obj[2] : null);
    	data.getCustomerInformation().setCust_addr_1(obj[3] != null ? (String)obj[3] : null);
    	data.getCustomerInformation().setCust_addr_2(obj[4] != null ? (String)obj[4] : null);
    	data.getCustomerInformation().setCust_addr_3(obj[5] != null ? (String)obj[5] : null);
    	data.getCustomerInformation().setCust_postcode(obj[6] != null ? (String)obj[6] : null);
    	data.getCustomerInformation().setCust_city(obj[7] != null ? (String)obj[7] : null);
    	data.getCustomerInformation().setCust_state(obj[8] != null ? (String)obj[8] : null);
    	data.getCustomerInformation().setCust_country(obj[9] != null ? (String)obj[9] : null);
    	data.getCustomerInformation().setCust_email(obj[10] != null ? (String)obj[10] : null);
    	data.getCustomerInformation().setCust_phone(obj[11] != null ? (String)obj[11] : null);
    	return data;
    }
    
    public Map<String, Object> sp_getcccasehist(String taskNo, int page, int size) {
    	Query query = entityManager.createNativeQuery("CALL sp_getcccasehist(:i_page, :i_size, :i_task_no)")
                .setParameter("i_page", page)
                .setParameter("i_size", size)
                .setParameter("i_task_no", taskNo);   	
    	
	   	List<Object[]> objects = query.getResultList();
	   	List<Map<String, Object>> history = new ArrayList<Map<String, Object>>();
	   	Map<String, Object> data = new HashMap<String, Object>();
	   	Integer total = null;
	   	
	   	for(Object[] obj : objects) {
	   		Map<String, Object> historyData = new HashMap<String, Object>();
	   		historyData.put("remark", (String)obj[2]);
	   		historyData.put("msg_type", (String)obj[3]);
	   		historyData.put("action_timestamp", ((java.sql.Timestamp)obj[4]).toLocalDateTime());
	   		historyData.put("pick_up", (String)obj[8]);
	   		historyData.put("assign_to", (String)obj[9]);
	   		historyData.put("task_status", (String)obj[10]);
	   		historyData.put("performer", (String)obj[7]);
	   			   		
	   		history.add(historyData);
	   		if(total == null)
	   			total = obj[11] != null ? (Integer)obj[11] : null;
	   	}
	   	data.put("total", total);
	   	data.put("history", history);
	   	
    	return data;	
    }

    public Map<String, Object> sp_getcccasermd(Integer i_cc_case_id){
    	Query query = entityManager.createNativeQuery("CALL sp_getcccasermd(:i_cc_case_id)")
                .setParameter("i_cc_case_id", i_cc_case_id);   	
    	
	   	List<Object[]> objects = query.getResultList();
	   	List<CreditControlCaseRmd> reminders = new ArrayList<CreditControlCaseRmd>();
	   	Map<String, Object> data = new HashMap<String, Object>();
	   	Integer total = null;

	   	for(Object[] obj : objects) {
	   		CreditControlCaseRmd rmd = new CreditControlCaseRmd();
	   		rmd.setCc_rmd_id(obj[0] != null ? (Integer)obj[0] : null);
	   		rmd.setReminder_cnt(obj[1] != null ? (Integer)obj[1] : null);
	   		rmd.setReminder_dt(obj[2] != null ? ((java.sql.Timestamp)obj[2]).toLocalDateTime() : null);
	   		rmd.setReminder_email_content(obj[3] != null ? (String)obj[3] : null);
	   		rmd.setReminder_received_date(obj[4] != null ? ((java.sql.Timestamp)obj[4]).toLocalDateTime() : null);
	   		rmd.setDt_created(obj[5] != null ? ((java.sql.Timestamp)obj[5]).toLocalDateTime() : null);
	   		rmd.setDt_modified(obj[6] != null ? ((java.sql.Timestamp)obj[6]).toLocalDateTime() : null);
	   		rmd.setCreated_by(obj[7] != null ? (String)obj[7] : null);
	   		rmd.setModified_by(obj[8] != null ? (String)obj[8] : null);
	   		rmd.setStatus(obj[9] != null ? (String)obj[9] : null);
	   		
	   		reminders.add(rmd);
	   		if(total == null)
	   			total = obj[10] != null ? (Integer)obj[10] : null;
	   	}

	   	data.put("total", total);
	   	data.put("reminders", reminders);
		   	
	   	return data;
    }
    
    public Map<String, Object> sp_getcccasedoc(String taskNo, int page, int size) {
    	Query query = entityManager.createNativeQuery("CALL sp_getcccasedoc(:i_page, :i_size, :i_task_no)")
                .setParameter("i_page", page)
                .setParameter("i_size", size)
                .setParameter("i_task_no", taskNo);   	
    	
	   	List<Object[]> objects = query.getResultList();
	   	List<Map<String, Object>> docList = new ArrayList<Map<String, Object>>();
	   	Map<String, Object> data = new HashMap<String, Object>();
	   	Integer total = null;
	   	
	   	for(Object[] obj : objects) {
	   		Map<String, Object> document = new HashMap<String, Object>();
	   		document.put("cc_doc_id", (Integer)obj[0]);
	   		document.put("cc_doc_type", (String)obj[2]);	   		
	   		document.put("file_nm", (String)obj[3]);
	   		document.put("file_type", (String)obj[4]);
	   		document.put("file_size", (Integer)obj[5]);
	   		document.put("timestamp", ((java.sql.Timestamp)obj[6]).toLocalDateTime());
	   		document.put("uploader", (String)obj[8]);
	   		
	   			   		
	   		docList.add(document);
	   		if(total == null)
	   			total = obj[11] != null ? (Integer)obj[11] : null;
	   	}
	   	data.put("total", total);
	   	data.put("docList", docList);
	   	
    	return data;	
    }
    
    public Blob sp_getcccasedocblob(Integer ccDocId) throws SQLException, IOException {
    	Query query = entityManager.createNativeQuery("CALL sp_getcccasedocblob(:i_cc_doc_id)")
                .setParameter("i_cc_doc_id", ccDocId);
        return (Blob) query.getSingleResult();
    }
    
    public Map<String, Object> sp_getcccasecsitems(String taskNo, int page, int size) {
    	Query query = entityManager.createNativeQuery("CALL sp_getcccasecsitems(:i_page, :i_size, :i_task_no)")
                .setParameter("i_page", page)
                .setParameter("i_size", size)
                .setParameter("i_task_no", taskNo);   	
    	
	   	List<Object[]> objects = query.getResultList();
	   	//CreditControlCase.ItemInformation c = new CreditControlCase.ItemInformation();
	   	List<CreditControlCase.ItemInformation> items = new ArrayList<CreditControlCase.ItemInformation>();
	   	Map<String, Object> data = new HashMap<String, Object>();
	   	Integer total = null;
	   	
	   	for(Object[] obj : objects) {
	   		CreditControlCase.ItemInformation item = new CreditControlCase.ItemInformation();
	   		item.setCc_cs_item_id(obj[0] != null ? (Integer)obj[0] : null);
	   		item.setCc_case_id(obj[1] != null ? (Integer)obj[1] : null);
	   		item.setTxn_item_ref(obj[2] != null ? (String)obj[2] : null);
	   		item.setTxn_item_desc(obj[3] != null ? (String)obj[3] : null);
	   		item.setCoa1(obj[4] != null ? (String)obj[4] : null);
	   		item.setCoa2(obj[5] != null ? (String)obj[5] : null);
	   		item.setSub_acct(obj[6] != null ? (String)obj[6] : null);
	   		item.setQty(obj[7] != null ? (Integer)obj[7] : null);
	   		item.setUnit_price(obj[8] != null ? (BigDecimal)obj[8] : null);
	   		item.setDisc_amt(obj[9] != null ? (BigDecimal)obj[9] : null);
	   		item.setDt_created(obj[10] != null ? ((java.sql.Timestamp)obj[10]).toLocalDateTime() : null);
	   		item.setDt_modified(obj[11] != null ? ((java.sql.Timestamp)obj[11]).toLocalDateTime() : null);
	   		item.setCreated_by(obj[12] != null ? (String)obj[12] : null);
	   		item.setModified_by(obj[13] != null ? (String)obj[13] : null);
	   		item.setStatus(obj[14] != null ? (String)obj[14] : null);
	   			   		
	   		items.add(item);
	   		if(total == null)
	   			total = obj[15] != null ? (Integer)obj[15] : null;
	   	}
	   	data.put("total", total);
	   	data.put("items", items);
	   	
    	return data;	
    }

    public Integer sp_insnewcccdoc(Integer ccCaseId, Map<String, Object> item, String username) throws SerialException, SQLException {
        byte[] decodedBytes = decodeBase64((String)item.get("i_file_content"));
        Blob blob = new SerialBlob(decodedBytes);

        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_insnewcccdoc");
        // Register parameters (ensure parameter names and types match those defined in the stored procedure)
        storedProcedureQuery.registerStoredProcedureParameter("i_cc_case_id", Integer.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_nm", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_content", Blob.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_type", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_size", Integer.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_category", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_created_by", String.class, javax.persistence.ParameterMode.IN);

        // Set parameters
        storedProcedureQuery.setParameter("i_cc_case_id", ccCaseId);
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
    
    public Integer sp_rollbackcccdocs(Integer ccCaseId){
    	Query query = entityManager.createNativeQuery("CALL sp_rollbackcccdocs("
    			+ ":i_cc_case_id)")
                .setParameter("i_cc_case_id", ccCaseId);
        return (Integer)query.getSingleResult();
    }
    
    public Integer sp_inscccasehist(Integer ccCaseId, String username, String remark, String i_msg_type) {
    	Query query = entityManager.createNativeQuery("CALL sp_inscccasehistP("
    			+ ":i_cc_case_id, :i_running_time, :i_user, :i_remark, :i_msg_type)")
                .setParameter("i_cc_case_id", ccCaseId)
                .setParameter("i_running_time", LocalDateTime.now())
                .setParameter("i_user", username)
                .setParameter("i_remark", remark)
                .setParameter("i_msg_type", i_msg_type);
        return (Integer)query.getSingleResult();
    }

    public Integer sp_updcccasetaskstatus(String taskNo, String taskStatus, String assignTo, String username, String remark, String i_msg_type) {
    	Query query = entityManager.createNativeQuery("CALL sp_updcccasetaskstatus("
    			+ ":i_task_no, :i_task_status, :i_assign_to, :i_user, :i_remark, :i_msg_type)")
                .setParameter("i_task_no", taskNo)
                .setParameter("i_task_status", taskStatus)
                .setParameter("i_assign_to", assignTo)
                .setParameter("i_user", username)
                .setParameter("i_remark", remark)
                .setParameter("i_msg_type", i_msg_type);
        return (Integer)query.getSingleResult();	
    }
    
    public List<String> sp_getsmerolelist(){
    	Query query = entityManager.createNativeQuery("select role_nm_en from rms_role where role_nm_en like '%SME%' order by role_nm_en asc");
    	List<String> result = query.getResultList();
    	return result;
    }
    
    public Integer sp_getcccassignedtaskactivetaskcount(String username) {
        Query query = entityManager.createNativeQuery("CALL sp_getcccassignedtaskactivetaskcount(:i_assigned_to)")
        .setParameter("i_assigned_to", username);

        return (Integer) query.getSingleResult();
    }

    public Integer sp_getccccreatedtaskactivetaskcount(String username) {
        Query query = entityManager.createNativeQuery("CALL sp_getccccreatedtaskactivetaskcount(:i_created_by)")
        .setParameter("i_created_by", username);

        return (Integer) query.getSingleResult();
    }
    
    public List<Map<String, Object>>sp_getccctaskslisting(Map<String, Object> request) {
        Query query = entityManager.createNativeQuery("CALL sp_getccctaskslisting(:i_page, :i_size, :i_username, "
        		+ ":i_task_mode, :i_task_id, :i_task_status, :i_payment_status, :i_txn_type, :i_case_no)")
        .setParameter("i_page", request.get("i_page"))
        .setParameter("i_size", request.get("i_size"))
        .setParameter("i_username", request.get("username"))
        .setParameter("i_task_mode", request.get("i_task_mode"))
        .setParameter("i_task_id", request.get("i_task_id"))
        .setParameter("i_task_status", request.get("i_task_status"))
        .setParameter("i_payment_status", request.get("i_payment_status"))
        .setParameter("i_txn_type", request.get("i_txn_type"))
        .setParameter("i_case_no", request.get("i_case_no"));
        
        List<Object[]> result = query.getResultList();
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        
        for(Object[] obj : result) {
        	Map<String, Object> entry = new HashMap<String, Object>();

        	entry.put("cc_doc_id", (Integer)obj[0]);
        	entry.put("taskId", (String)obj[1]);	   		
	   		entry.put("taskStatus", (String)obj[2]);
	   		entry.put("pymtStatus", (String)obj[3]);
	   		entry.put("txnType", (String)obj[4]);
	   		entry.put("caseNo", (String)obj[5]);
        	entry.put("reminderCount", (Integer)obj[6]);
	   		entry.put("emailSent", obj[7] != null ? ((java.sql.Timestamp)obj[7]).toLocalDateTime() : null);
	   		entry.put("amount", (BigDecimal)obj[8]);
	   		entry.put("tableOrigin", (String)obj[9]);
        	entry.put("total", (Integer)obj[10]);
        	
        	data.add(entry);
        }

        return data;
    }
    
    public Integer sp_updcccasetaskcurdocbal(Integer ccCaseId, BigDecimal amt, BigDecimal dnAmt, BigDecimal cnAmt) {
        Query query = entityManager.createNativeQuery("CALL sp_updcccasetaskcurdocbal(:i_cc_case_id, :i_amount, :i_dn_amount, :i_cn_amount)")
        .setParameter("i_cc_case_id", ccCaseId)
        .setParameter("i_amount", amt)
        .setParameter("i_dn_amount", dnAmt)
        .setParameter("i_cn_amount", cnAmt);
        return (Integer) query.getSingleResult();
    }
}
