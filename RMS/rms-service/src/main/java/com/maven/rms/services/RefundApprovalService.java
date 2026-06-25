package com.maven.rms.services;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import com.maven.rms.interfaces.IRefundApprovalService;
import com.maven.rms.models.BankReconRequest;
import com.maven.rms.models.RefundApprovalDetReq;
import com.maven.rms.models.RefundApprovalInfo;
import com.maven.rms.models.RefundDetailPymtItem;
import com.maven.rms.models.RefundDoc;
import com.maven.rms.models.RefundPTTListingDetReq;
import com.maven.rms.models.RttForm;
import com.maven.rms.models.RttItem;
import com.maven.rms.repositories.IRefundApprovalRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RefundApprovalService implements IRefundApprovalService {
    private final IRefundApprovalRepository storeProcedureRepository;

    public RefundApprovalService(IRefundApprovalRepository storeProcedureRepository) {
        this.storeProcedureRepository = storeProcedureRepository;

    }

    @Override
    public List<RefundApprovalInfo> sp_getrefundapproval(RefundApprovalDetReq req) {
        List<RefundApprovalInfo> result = Collections.emptyList();
        List<Object[]> objects = storeProcedureRepository.sp_getrefundapproval(req);
        result = convertRefundApprovalInfoList(objects);
        return result;
    }

    private List<RefundApprovalInfo> convertRefundApprovalInfoList(List<Object[]> objects) {
        List<RefundApprovalInfo> refundApprovalInfos = new ArrayList<>();

        for (Object[] obj : objects) {
            try {
                System.out.println("Processing row: " + Arrays.toString(obj));

                RefundApprovalInfo refundApprovalInfo = new RefundApprovalInfo();

                refundApprovalInfo.setRtt_wf_id(obj[0] != null ? (int) obj[0] : 0);
                refundApprovalInfo.setRtt_app_no(obj[1] != null ? (String) obj[1] : null);
                refundApprovalInfo.setOrn_no(obj[2] != null ? (String) obj[2] : null);
                refundApprovalInfo.setRefund_ty(obj[3] != null ? (String) obj[3] : null);
                refundApprovalInfo.setRtt_status(obj[4] != null ? (String) obj[4] : null);
                refundApprovalInfo.setMtt_id(obj[5] != null ? (int) obj[5] : 0);
                refundApprovalInfo.setSs_cd(obj[6] != null ? (String) obj[6] : null);
                refundApprovalInfo.setRms_tpye(obj[7] != null ? (String) obj[7] : null);
                refundApprovalInfo.setRequested_by(obj[8] != null ? (String) obj[8] : null);

                // Use converter for date fields
                refundApprovalInfo.setDt_requested(convertToDate(obj[9]));
                refundApprovalInfo.setDt_created(convertToDate(obj[10]));
                refundApprovalInfo.setDate_pick(convertToDate(obj[11]));
                refundApprovalInfo.setMsg(obj[12] != null ? (String) obj[12] : null);
                refundApprovalInfo.setDt_approved(convertToDate(obj[13]));
                refundApprovalInfo.setPickup_by(obj[14] != null ? (String) obj[14] : null);
                refundApprovalInfo.setApproved_by(obj[15] != null ? (String) obj[15] : null);
                refundApprovalInfo.setRefund_cd(obj[16] != null ? (String) obj[16] : null);
                refundApprovalInfo.setStatus_param_nm(obj[17] != null ? (String) obj[17] : null);
                refundApprovalInfos.add(refundApprovalInfo);

                System.out.println("Processed RefundApprovalInfo: " + refundApprovalInfo);
            } catch (Exception e) {
                System.out.println("Error processing row: " + Arrays.toString(obj));
                e.printStackTrace();
            }
        }
        return refundApprovalInfos;
    }

    // Utility Method to Convert Timestamp to Date
    private java.util.Date convertToDate(Object dateObj) {
        if (dateObj == null) {
            return null; // Return null for null values
        }
        if (dateObj instanceof java.sql.Timestamp) {
            return new java.util.Date(((java.sql.Timestamp) dateObj).getTime());
        }
        if (dateObj instanceof java.sql.Date) {
            return new java.util.Date(((java.sql.Date) dateObj).getTime());
        }
        return (java.util.Date) dateObj; // Return as-is if already java.util.Date
    }

    @Override
    public List<RttItem> sp_getrttitem(
            RefundApprovalDetReq req) {
        List<RttItem> result = Collections.emptyList();
        List<Object[]> objects = storeProcedureRepository.sp_getrttitem(req);
        result = convertrttitemlist(objects);
        return result;
    }

    private List<RttItem> convertrttitemlist(List<Object[]> objects) {
        List<RttItem> rttitems = new ArrayList<>();

        for (Object[] obj : objects) {
            RttItem rttitem = new RttItem();
            rttitem.setRtt_item_id((int) obj[0]);
            rttitem.setRtt_wf_id((int) obj[1]);
            rttitem.setItem_ref_no((String) obj[2]);
            rttitem.setItem_desc((String) obj[3]);
            rttitem.setQty((Integer) obj[4]);
            rttitem.setUnit_fee((BigDecimal) obj[5]);
            rttitem.setTax_pct((BigDecimal) obj[6]);
            rttitem.setTax_amt((BigDecimal) obj[7]);
            rttitem.setGrant_cd((String) obj[8]);
            rttitem.setDisc_amt((BigDecimal) obj[9]);
            rttitem.setRefund_amt((BigDecimal) obj[10]);
            rttitem.setTotal_refund_amt((BigDecimal) obj[11]);
            rttitem.setNet_amt((BigDecimal) obj[12]);
            rttitem.setEntity_no((String) obj[13]);
            rttitem.setEntity_nm((String) obj[14]);
            rttitem.setEntity_type((String) obj[15]);
            rttitem.setGross_amt((BigDecimal) obj[16]);
            rttitems.add(rttitem);
        }
        return rttitems;
    }

    @Override
    public Integer sp_updrttwf_status(RefundApprovalDetReq rttwfrequest) {

        Integer result = 0;

        result = storeProcedureRepository.sp_updrttwf_status(rttwfrequest);

        return result;
    }

    @Override
    public List<RttForm> sp_getrttform(RefundApprovalDetReq req) {
        List<RttForm> result = Collections.emptyList();
        List<Object[]> objects = storeProcedureRepository.sp_getrttform(req);
        result = convertRTTFormList(objects);
        return result;
    }

    private List<RttForm> convertRTTFormList(List<Object[]> objects) {
        List<RttForm> rttForms = new ArrayList<>();

        for (Object[] obj : objects) {
            try {
                System.out.println("Processing row: " + Arrays.toString(obj));

                RttForm rttForm = new RttForm();

                rttForm.setIdentityType(obj[0] != null ? (String) obj[0] : null);
                rttForm.setIdentityNumber(obj[1] != null ? (String) obj[1] : null);
                rttForm.setBankAccountName(obj[2] != null ? (String) obj[2] : null);
                rttForm.setBankAccountNo(obj[3] != null ? (String) obj[3] : null);
                rttForm.setBankAccountType(obj[4] != null ? (String) obj[4] : null);
                rttForm.setBankHolderName(obj[5] != null ? (String) obj[5] : null);
                rttForm.setBillingAddress1(obj[6] != null ? (String) obj[6] : null);
                rttForm.setBillingAddress2(obj[7] != null ? (String) obj[7] : null);
                rttForm.setBillingAddress3(obj[8] != null ? (String) obj[8] : null);
                rttForm.setCustCity(obj[9] != null ? (String) obj[9] : null);
                rttForm.setCustPostcode(obj[10] != null ? (String) obj[10] : null);
                rttForm.setCustState(obj[11] != null ? (String) obj[11] : null);
                rttForm.setRecEmail(obj[12] != null ? (String) obj[12] : null);
                rttForm.setCustNm(obj[13] != null ? (String) obj[13] : null);
                rttForm.setCustEmail(obj[14] != null ? (String) obj[14] : null);
                rttForm.setCustPhone(obj[15] != null ? (String) obj[15] : null);
                rttForm.setRcptNo(obj[16] != null ? (String) obj[16] : null);
                rttForm.setRcptAmt(obj[17] != null ? (BigDecimal) obj[17] : null);
                rttForm.setOrnNo(obj[18] != null ? (String) obj[18] : null);
                rttForm.setTxnId(obj[19] != null ? (String) obj[19] : null);
                rttForm.setEntityNm(obj[20] != null ? (String) obj[20] : null);
                rttForm.setEntityTy(obj[21] != null ? (String) obj[21] : null);
                rttForm.setEntityNo(obj[22] != null ? (String) obj[22] : null);

                rttForms.add(rttForm);

                System.out.println("Processed RttForm: " + rttForm);
            } catch (Exception e) {
                System.out.println("Error processing row: " + Arrays.toString(obj));
                e.printStackTrace();
            }
        }
        return rttForms;
    }

    @Override
    public List<RefundDoc> sp_getrttdoc(RefundApprovalDetReq req) throws SQLException {
        System.out.println("Entering sp_getrttdoc with request: " + req);

        List<RefundDoc> refundDocs = new ArrayList<>();

        try {
            List<Object[]> results = storeProcedureRepository.sp_getrttdoc(req); // Assume this returns a List of Object
                                                                                 // arrays
            System.out.println("Repository returned results: " + results);

            if (results == null || results.isEmpty()) {
                throw new SQLException("No data returned from stored procedure.");
            }

            // Process each row and map to RefundDoc
            for (Object[] result : results) {
                System.out.println("Processing row: " + java.util.Arrays.toString(result));

                RefundDoc refundDoc = new RefundDoc();

                // Map each field to RefundDoc
                refundDoc.setFile_nm((String) result[0]); // String

                Blob blob = (Blob) result[1]; // Blob
                if (blob != null) {
                    byte[] bytes = blob.getBytes(1, (int) blob.length());
                    refundDoc.setFile_content(Base64.getEncoder().encodeToString(bytes));
                    System.out.println("Base64 content successfully generated.");
                } else {
                    System.out.println("Blob content is null.");
                }

                refundDoc.setFile_type((String) result[2]); // String
                refundDoc.setFile_size_kb(result[3] != null ? Integer.parseInt(result[3].toString()) : null); // String
                                                                                                              // ->
                                                                                                              // Integer
                refundDoc.setDt_created(
                        result[4] != null ? new java.util.Date(((Timestamp) result[4]).getTime()) : null); // Timestamp
                                                                                                           // -> Date
                refundDoc.setDt_modified(
                        result[5] != null ? new java.util.Date(((Timestamp) result[5]).getTime()) : null); // Timestamp
                                                                                                           // -> Date
                refundDoc.setCreated_by((String) result[6]); // String
                refundDoc.setModified_by((String) result[7]); // String
                refundDoc.setRtt_doc_id((Integer) result[8]); // Object -> Integer

                refundDocs.add(refundDoc);
            }

            System.out.println("Final RefundDoc list: " + refundDocs);
            return refundDocs;
        } catch (Exception e) {
            System.out.println("Error in service layer sp_getrttdoc: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw the exception to propagate it up
        }
    }

    @Override
    public Integer sp_updrttwf_returntask(RefundApprovalDetReq rttwfrequest) {

        Integer result = 0;

        result = storeProcedureRepository.sp_updrttwf_returntask(rttwfrequest);

        return result;
    }

    @Override
    public String sp_getRttAppEmail(String app_no) {
        String result = storeProcedureRepository.sp_getRttAppEmail(app_no);
        return result;
    }


}
