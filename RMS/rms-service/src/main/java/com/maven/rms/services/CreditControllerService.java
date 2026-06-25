package com.maven.rms.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.maven.rms.models.CreditControlCase;
import com.maven.rms.models.CreditControlReminderRequest;
import com.maven.rms.models.FMSCRMemo;
import com.maven.rms.models.FMSDRMemo;
import com.maven.rms.models.MFT;
import com.maven.rms.models.MFTRequest;
import com.maven.rms.repositories.CreditControlRepository;
import com.maven.rms.utils.RMSLogger;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CreditControllerService {
	private final CreditControlRepository ccRepo;
	
	@Autowired
	private MFTService mftSvc;
	@Autowired
	private FMSCRMemoService crSvc;
	@Autowired
	private FMSDRMemoService drSvc;
	
	public CreditControllerService(CreditControlRepository ccRepo) {
		this.ccRepo = ccRepo;
	}
	
	public Integer sp_inscasereminder(CreditControlReminderRequest ccRmdRequest) {
		return ccRepo.sp_inscasereminder(ccRmdRequest);
	}
	
	public CreditControlCase sp_getcccase(Map<String, Object> request) {
		return ccRepo.sp_getcccase(request);
	}
		
	public String sp_getCCdocblob(Integer ccDocId) throws SQLException, IOException {
        Blob data = ccRepo.sp_getcccasedocblob(ccDocId);
        byte[] bytes = data.getBytes(1, (int) data.length());
        
        data.free();
        return Base64.getEncoder().encodeToString(bytes);
	}
	
	 public Map<String, Object> sp_getcccasehist(String taskNo, int page, int size) {
		 return ccRepo.sp_getcccasehist(taskNo, page, size);
	 }
	 
	 public Integer insertSupportingCCCDocuments(Integer ccCaseId, List<Map<String, Object>> documents, String username) {
		 Integer statusCode = 0;
		 if(documents != null && documents.size() > 0)
			 for(Map<String, Object> doc : documents) {
				try {
					 statusCode = ccRepo.sp_insnewcccdoc(ccCaseId, doc, username);
				}catch(EmptyResultDataAccessException | SQLException e) {
						RMSLogger.error("Exception in " + this.getClass().toString() + "insertSupportingCCCDocuments func - "
								+ "sp_insnewcccdoc - item: " + (String)doc.get("i_file_nm") + " - "
								+ "status code: " + statusCode.toString() + "!");
						ccRepo.sp_rollbackcccdocs(ccCaseId);
						return statusCode;
				}
				if(statusCode < 1) {
					RMSLogger.error("Exception in " + this.getClass().toString() + "insNewBilReg func - "
							+ "sp_insnewbilregdoc - item: " + (String)doc.get("i_file_nm") + " - "
							+ "status code: " + statusCode.toString() + "!");
					ccRepo.sp_rollbackcccdocs(ccCaseId);
					return statusCode;
				 }
			 }
		 return statusCode;
	 }

	 public Integer sp_inscccasehist(Integer ccCaseId, String username, String remark, String i_msg_type) {
		 return ccRepo.sp_inscccasehist(ccCaseId, username, remark, i_msg_type);
	 }
	 
	 public Integer updCaseTaskStatus(String taskNo, String taskStatus, String assignTo, String username, String remark, String i_msg_type) {
		 return ccRepo.sp_updcccasetaskstatus(taskNo, taskStatus, assignTo, username, remark, i_msg_type);
	 }
	 
	 public List<String> sp_getsmerolelist(){
		 return ccRepo.sp_getsmerolelist();
	 }
	 
	 public Integer invokeCreditControlCaseCreditMemo(CreditControlCase data, BigDecimal amount) throws JsonProcessingException {
		 //API for FMS creditMemo goes here [Step 27]
		 Integer statusCode = 0;
		 List<FMSCRMemo> memos = new ArrayList<FMSCRMemo>();
		 //for(CreditControlCase.ItemInformation item: data.getItemInformation()) {
			 FMSCRMemo memo = new FMSCRMemo();
			 memo.setType("Credit Memo");
			 memo.setLink_branch("HQ");
			 memo.setPg_pymt_amt(amount);
			 memo.setDesc(data.getCreditMemoInformation().getCn_desc());
			 memo.setAttr_ext_sys("Source System");	
			 memo.setFms_ref_no(data.getInvoiceInformation().getFms_ari_ref_no());
			 memo.setAcct_nm("RMS PG MID Account ID");
			 memo.setDoc_ty("Invoice");
			 memo.setGenPdf(0);
				
			 memo.setPg_pymt_method("ANY");
			 //memo.setQty(item.getQty());
			 //memo.setItem_desc(item.getTxn_item_desc());
			 //memo.setUnit_fee(item.getUnit_price());
			 //memo.setQty(data.getCreditMemoInformation().getCn_qty());
			 memo.setQty(1);
			 memo.setItem_desc("Credit Control Credit Memo");
			 //memo.setUnit_fee(data.getCreditMemoInformation().getCn_unit_price());
			 memo.setUnit_fee(amount);
			 memo.setGross_amt(memo.getUnit_fee().multiply(BigDecimal.valueOf(memo.getQty())));

			 memo.setCoa1(data.getCreditMemoInformation().getCn_coa1());
			 memo.setCoa2(data.getCreditMemoInformation().getCn_coa2());
			 memo.setCust(data.getCustomerInformation().getCust_id_no());
			 memo.setCust_nm(data.getCustomerInformation().getCust_nm());
			 memo.setTax_amt(BigDecimal.ZERO);
			 //memo.setBranch("00000");
			 memo.setBranch(data.getCreditMemoInformation().getCn_branch());
			 //memo.setSub_acct("000000");
			 memo.setSub_acct(data.getCreditMemoInformation().getCn_sub_acct());
			 memos.add(memo);
		 //}
			
		 statusCode = crSvc.newCrMemo(memos);
		 if(statusCode < 1) {
    		log.error("Exception in " + this.getClass().toString() 
					+ "invokeCreditControlCaseCreditMemo func - newCrMemo failed with code " + Integer.toString(statusCode) + " !");
    		return -1;
		 }
		 statusCode = crSvc.crMemoCallAPI(BigInteger.valueOf(statusCode));
		 
		 return ccRepo.sp_updcccasetaskcurdocbal(data.getCc_case_id(), data.getInvoiceInformation().getCur_doc_bal().subtract(amount), amount, null);
	 }
	 
	 public Integer invokeCreditControlCaseDebitMemo(CreditControlCase data, BigDecimal amount) throws JsonProcessingException {
		 //API for FMS debitMemo goes here [Step 29]
		 Integer statusCode = 0;
		 List<FMSDRMemo> memos = new ArrayList<FMSDRMemo>();
		 //for(CreditControlCase.ItemInformation item: data.getItemInformation()) {
			 FMSDRMemo memo = new FMSDRMemo();
			 memo.setType("Debit Memo");
			 memo.setLink_branch("00000");
			 memo.setPg_pymt_amt(amount);
			 memo.setDesc(data.getDebitMemoInformation().getDn_desc());
			 memo.setAttr_ext_sys("Source System");	
			 memo.setFms_ref_no(data.getInvoiceInformation().getFms_ari_ref_no());
			 memo.setAcct_nm("RMS PG MID Account ID");
			 memo.setDoc_ty("Invoice");
			 memo.setGenPdf(0);
				
			 memo.setPg_pymt_method("ANY");
			 //memo.setQty(item.getQty());
			 //memo.setItem_desc(item.getTxn_item_desc());
			 //memo.setUnit_fee(item.getUnit_price());
			 //memo.setQty(data.getDebitMemoInformation().getDn_qty());
			 memo.setQty(1);
			 memo.setItem_desc("Credit Control Debit Memo");
			 memo.setUnit_fee(amount);
			 //memo.setUnit_fee(data.getDebitMemoInformation().getDn_unit_price());
			 memo.setGross_amt(memo.getUnit_fee().multiply(BigDecimal.valueOf(memo.getQty())));

			 memo.setCoa1(data.getDebitMemoInformation().getDn_coa1());
			 memo.setCoa2(data.getDebitMemoInformation().getDn_coa2());
			 memo.setCust(data.getCustomerInformation().getCust_id_no());
			 memo.setCust_nm(data.getCustomerInformation().getCust_nm());
			 memo.setTax_amt(BigDecimal.ZERO);
			 //memo.setBranch("00000");
			 memo.setBranch(data.getDebitMemoInformation().getDn_branch());
			 //memo.setSub_acct("000000");
			 memo.setSub_acct(data.getDebitMemoInformation().getDn_sub_acct());
			 
			 memos.add(memo);
		 //}
			
		 statusCode = drSvc.newDrMemo(memos);
		 if(statusCode < 1) {
    		log.error("Exception in " + this.getClass().toString() 
					+ "invokeCreditControlCaseDebitMemo func - newDrMemo failed with code " + Integer.toString(statusCode) + " !");
    		return -1;
		 }
		 statusCode = drSvc.drMemoCallAPI(BigInteger.valueOf(statusCode));
		 
		 
		 return ccRepo.sp_updcccasetaskcurdocbal(data.getCc_case_id(), data.getInvoiceInformation().getCur_doc_bal().add(amount), null, amount);
	 }
	 /*
	 public Integer invokeCreditControlCaseImpair(CreditControlCase data) {
		 //API for FMS impair api goes here (AR Journal API [Step 25])
		 return 1;
	 }
	 */
	 public Integer invokeCreditControlCaseWriteOff(CreditControlCase data) {
		 //API for FMS debitMemo goes here (Rrigger CP Approval [Step 34])
		 return 1;
	 }
	 
    public Integer sp_getcccassignedtaskactivetaskcount(String username) {
    	return ccRepo.sp_getcccassignedtaskactivetaskcount(username);
    }

    public Integer sp_getccccreatedtaskactivetaskcount(String username) {
    	return ccRepo.sp_getccccreatedtaskactivetaskcount(username);
    }
    
    public List<Map<String, Object>> sp_getccctaskslisting(Map<String, Object> request) {
    	return ccRepo.sp_getccctaskslisting(request);
    }
}
