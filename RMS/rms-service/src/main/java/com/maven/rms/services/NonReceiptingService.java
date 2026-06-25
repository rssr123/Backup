package com.maven.rms.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.maven.rms.models.AgBankTxnModel;
import com.maven.rms.models.AgBankTxnReq;
import com.maven.rms.models.AgBankTxnStatistic;
import com.maven.rms.models.AgDoc;
import com.maven.rms.models.FMSARR;
import com.maven.rms.models.FMSDRMemo;
import com.maven.rms.models.NonReceipting;
import com.maven.rms.models.NonReceiptingAgTxnRequest;
import com.maven.rms.models.NonReceiptingDocRequest;
import com.maven.rms.models.NonReceiptingRequest;
import com.maven.rms.repositories.NonReceiptingRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NonReceiptingService implements INonReceiptingService {

    private final NonReceiptingRepository nonReceiptingRepository;

    @Autowired
    private FMSDRMemoService fmsdrMemoService;

    @Autowired
    private FMSARRService fmsarrService;
    // @Autowired
    // private AuthService authService;

    public NonReceiptingService(NonReceiptingRepository nonReceiptingRepository) {
        this.nonReceiptingRepository = nonReceiptingRepository;
    }

    @Override
    public List<NonReceipting> sp_getrmsnonreceipting(NonReceiptingRequest otcReturnedChequeRequest) {
        List<NonReceipting> result = Collections.emptyList();
        List<Object[]> objects = nonReceiptingRepository.sp_getrmsnonreceipting(otcReturnedChequeRequest);
        result = convertNonReceiptings(objects);
        return result;
    }

    private List<NonReceipting> convertNonReceiptings(List<Object[]> objects) {
        List<NonReceipting> nonReceiptings = new ArrayList<>();

        for (Object[] obj : objects) {
            NonReceipting nonReceipting = new NonReceipting();
            // nonReceipting.setAg_sale_id((Integer) obj[0]);
            nonReceipting.setSs_cd((String) obj[0]);
            nonReceipting.setCn_cust_id((String) obj[1]);
            nonReceipting.setDn_cust_id((String) obj[2]);
            nonReceipting.setCash_acct((String) obj[3]);
            nonReceipting.setMerchant_id((String) obj[4]);
            nonReceipting.setStmt_no((String) obj[5]);
            // nonReceipting.setFms_ari_ref_no((String) obj[6]);
            nonReceipting.setAri_total_amt((BigDecimal) obj[6]);
            nonReceipting.setMdr_total_amt((BigDecimal) obj[7]);
            nonReceipting.setTotal_net_amt((BigDecimal) obj[8]);
            nonReceipting.setDt_settlement((Date) obj[9]);
            nonReceipting.setTotal_trx_no((Integer) obj[10]);
            // nonReceipting.setBatch_size((Integer) obj[13]);
            // nonReceipting.setBatch_cnt((Integer) obj[14]);
            nonReceipting.setTask_id((String) obj[11]);
            nonReceipting.setTask_status((String) obj[12]);
            // nonReceipting.setDt_created((Date) obj[17]);
            // nonReceipting.setDt_modified((Date) obj[18]);
            // nonReceipting.setStatus((String) obj[14]);
            nonReceipting.setDt_upload((Date) obj[13]);
            nonReceipting.setSettle_status((String) obj[14]);
            nonReceipting.setRemarks((String) obj[15]);
            nonReceipting.setTotal((Integer) obj[16]);

            nonReceiptings.add(nonReceipting);
        }
        return nonReceiptings;
    }

    // @Override
    // public Integer sp_insagsaledoc(NonReceiptingDocRequest insertRequest) throws
    // SerialException, SQLException {
    // // Decode Base64 content
    // byte[] decodedBytes = decodeBase64(insertRequest.getI_file_content());
    // Blob blob = new SerialBlob(decodedBytes);

    // // Call the repository method
    // Integer result = nonReceiptingRepository.sp_insagsaledoc(insertRequest,
    // blob);
    // return result;
    // }

    // @Override
    // public List<Integer> sp_insagsaledoc(List<NonReceiptingDocRequest>
    // insertRequests)
    // throws SerialException, SQLException {
    // return nonReceiptingRepository.sp_insagsaledoc(insertRequests);
    // }

    @Override
    public Integer sp_insagsaledoc(NonReceiptingDocRequest insertRequests) throws SerialException, SQLException {
        return nonReceiptingRepository.sp_insagsaledoc(insertRequests);
    }

    // private byte[] decodeBase64(String base64String) {
    // if (base64String.startsWith("data:")) {
    // base64String = base64String.substring(base64String.indexOf(',') + 1);
    // }
    // base64String = base64String.replaceAll("\\s", "").replace(":", "");
    // return Base64.getDecoder().decode(base64String);
    // }

    @Override
    public Integer sp_insagbanktxn(AgBankTxnReq req) {
        Integer result = 0;
        try {
            result = nonReceiptingRepository.sp_insagbanktxn(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<AgDoc> sp_getagdoc(AgBankTxnReq req) {
        List<AgDoc> result = Collections.emptyList();
        try {
            List<Object[]> objects = nonReceiptingRepository.sp_getagdoc(req);
            result = convertToGetBillDoc(objects);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private List<AgDoc> convertToGetBillDoc(List<Object[]> objects) {
        List<AgDoc> agdoclList = new ArrayList<>();

        for (Object[] obj : objects) {
            AgDoc doc = new AgDoc();
            doc.setAg_doc_id((Integer) obj[0]);
            doc.setStmt_no((String) obj[1]);
            doc.setAg_type((String) obj[2]);
            doc.setFile_nm((String) obj[3]);
            doc.setFile_type((String) obj[4]);
            doc.setFile_size((Integer) obj[5]);
            doc.setDt_created((Date) obj[6]);
            doc.setDt_modified((Date) obj[7]);
            doc.setCreated_by((String) obj[8]);
            doc.setModified_by((String) obj[9]);
            doc.setTotal((Integer) obj[10]);
            agdoclList.add(doc);
        }
        return agdoclList;
    }

    @Override
    public String sp_getagfilecontent(AgBankTxnReq req) {

        String result = "";

        try {
            Blob blob = (Blob) nonReceiptingRepository.sp_getagfilecontent(req);
            try {
                // Convert Blob to byte array
                byte[] bytes = blob.getBytes(1, (int) blob.length());

                // Convert byte array to Base64-encoded string
                String base64Content = Base64.getEncoder().encodeToString(bytes);
                result = base64Content;

            } catch (SQLException e) {
                e.printStackTrace();
                result = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<AgBankTxnModel> sp_getagbanktxn(NonReceiptingAgTxnRequest req) {
        List<AgBankTxnModel> result = Collections.emptyList();
        List<Object[]> objects = nonReceiptingRepository.sp_getagbanktxn(req);
        result = convertAgtBankTxn(objects);
        return result;
    }

    @Override
    public List<AgBankTxnModel> sp_getagbanktxnpg(NonReceiptingAgTxnRequest req) {
        List<AgBankTxnModel> result = Collections.emptyList();
        List<Object[]> objects = nonReceiptingRepository.sp_getagbanktxnpg(req);
        result = convertAgtBankTxn(objects);
        return result;
    }

    private List<AgBankTxnModel> convertAgtBankTxn(List<Object[]> objects) {
        List<AgBankTxnModel> agBankTxnModels = new ArrayList<>();

        for (Object[] obj : objects) {
            AgBankTxnModel agBankTxnModel = new AgBankTxnModel();
            agBankTxnModel.setAcct_no((String) obj[0]);
            agBankTxnModel.setAcct_type((String) obj[1]);
            agBankTxnModel.setAcct_nm((String) obj[2]);
            agBankTxnModel.setDt_fr((Date) obj[3]);
            agBankTxnModel.setDt_to((Date) obj[4]);
            agBankTxnModel.setTotal_debit((Integer) obj[5]);
            agBankTxnModel.setTotal_credit((Integer) obj[6]);
            agBankTxnModel.setBegin_bal((BigDecimal) obj[7]);
            agBankTxnModel.setEnd_bal((BigDecimal) obj[8]);
            agBankTxnModel.setDt_txn((Date) obj[9]);
            agBankTxnModel.setDt_posting((Date) obj[10]);
            agBankTxnModel.setTxn_desc((String) obj[11]);
            agBankTxnModel.setTxn_ref((String) obj[12]);
            agBankTxnModel.setDebit((BigDecimal) obj[13]);
            agBankTxnModel.setCredit((BigDecimal) obj[14]);
            agBankTxnModel.setSource_cd((String) obj[15]);
            agBankTxnModel.setTeller_id((String) obj[16]);
            agBankTxnModel.setBrn_chn((String) obj[17]);
            agBankTxnModel.setTxn_cd((String) obj[18]);
            agBankTxnModel.setEnd_bal2((BigDecimal) obj[19]);
            agBankTxnModel.setVirtual_acct((String) obj[20]);
            agBankTxnModel.setTxn_desc2((String) obj[21]);
            agBankTxnModel.setTxn_desc3((String) obj[22]);
            agBankTxnModel.setTxn_desc4((String) obj[23]);
            agBankTxnModel.setDt_expiry((Date) obj[24]);
            agBankTxnModel.setDt_created((Date) obj[25]);
            agBankTxnModel.setDt_modified((Date) obj[26]);
            agBankTxnModel.setCreated_by((String) obj[27]);
            agBankTxnModel.setModified_by((String) obj[28]);
            agBankTxnModel.setStatus((String) obj[29]);
            agBankTxnModel.setTotal((Integer) obj[30]);

            agBankTxnModels.add(agBankTxnModel);
        }
        return agBankTxnModels;
    }

    @Override
    public Integer sp_delagdoc(AgBankTxnReq req) {
        Integer result = 0;
        result = nonReceiptingRepository.sp_delagdoc(req);
        return result;
    }

    @Override
    public List<AgBankTxnStatistic> sp_getagdocstatistics(NonReceiptingAgTxnRequest req) {
        List<AgBankTxnStatistic> result = Collections.emptyList();
        List<Object[]> objects = nonReceiptingRepository.sp_getagdocstatistics(req);
        result = convertAgDocStatistic(objects);
        return result;
    }

    private List<AgBankTxnStatistic> convertAgDocStatistic(List<Object[]> objects) {
        List<AgBankTxnStatistic> agBankTxnStatistics = new ArrayList<>();

        for (Object[] obj : objects) {
            AgBankTxnStatistic agBankTxnStatistic = new AgBankTxnStatistic();
            agBankTxnStatistic.setAg_doc_id((Integer) obj[0]);
            agBankTxnStatistic.setFile_nm((String) obj[1]);
            agBankTxnStatistic.setBank_stmt_count((Integer) obj[2]);
            agBankTxnStatistic.setBank_stmt_trans((Integer) obj[3]);
            agBankTxnStatistic.setPg_settlement_trans((Integer) obj[4]);
            agBankTxnStatistic.setTotal_pg_amt((BigDecimal) obj[5]);

            agBankTxnStatistics.add(agBankTxnStatistic);
        }
        return agBankTxnStatistics;
    }

    @Override
    public Integer sp_updagsale(NonReceiptingDocRequest req) {
        Integer result = 0;
        result = nonReceiptingRepository.sp_updagsale(req);
        return result;
    }

    @Override
    public List<NonReceipting> sp_getfmsnonrmsrecon() {
        List<NonReceipting> result = Collections.emptyList();
        List<Object[]> objects = nonReceiptingRepository.sp_getfmsnonrmsrecon();
        result = convNonReceiptingsFMS(objects);
        return result;
    }

    private List<NonReceipting> convNonReceiptingsFMS(List<Object[]> objects) {
        List<NonReceipting> nonRMNonReceiptings = new ArrayList<>();

        for (Object[] obj : objects) {
            NonReceipting nonRMNonReceipting = new NonReceipting();
            nonRMNonReceipting.setAg_sale_id((Integer) obj[0]);
            nonRMNonReceipting.setCash_acct((String) obj[1]);
            nonRMNonReceipting.setCn_cust_id((String) obj[2]);
            nonRMNonReceipting.setDn_cust_id((String) obj[3]);
            nonRMNonReceipting.setFms_ari_ref_no((String) obj[4]);
            nonRMNonReceipting.setAri_total_amt((BigDecimal) obj[5]);
            nonRMNonReceipting.setMdr_total_amt((BigDecimal) obj[6]);
            nonRMNonReceipting.setDiscrepancy_amt((BigDecimal) obj[7]);
            nonRMNonReceipting.setStmt_no((String) obj[8]);
            nonRMNonReceipting.setTotal_net_amt((BigDecimal) obj[9]);

            nonRMNonReceiptings.add(nonRMNonReceipting);
        }
        return nonRMNonReceiptings;
    }

    @Override
    public Integer sp_insfmsnonrmsrecon(AgBankTxnReq req) {
        Integer result = 0;
        try {
            result = nonReceiptingRepository.sp_insfmsnonrmsrecon(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Integer sp_insfmsnonrmsreconcreditdebit(AgBankTxnReq req) {
        Integer result = 0;
        try {
            result = nonReceiptingRepository.sp_insfmsnonrmsreconcreditdebit(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Integer fms_non_rms_recon() {
        List<NonReceipting> nonReceiptingList = this.sp_getfmsnonrmsrecon();
        Integer drMemoHid = 0;

        String cashAcctForDN = null; // ADD THIS

        if (nonReceiptingList.isEmpty())
            return 0;

        // Group by stmt_no, preserving original group order
        Map<String, List<NonReceipting>> byStmtNo = nonReceiptingList.stream()
                .collect(Collectors.groupingBy(
                        NonReceipting::getStmt_no,
                        LinkedHashMap::new,
                        Collectors.toList()));

        int successCount = 0;

        for (Map.Entry<String, List<NonReceipting>> entry : byStmtNo.entrySet()) {
            String stmtNo = entry.getKey();
            List<NonReceipting> group = new ArrayList<>(entry.getValue());

            // Ensure deterministic order within the statement group
            group.sort(Comparator.comparing(NonReceipting::getAg_sale_id));

            // ----- Compute totals *for this stmt_no only* -----
            BigDecimal totalNetAmt = group.stream()
                    .map(NonReceipting::getTotal_net_amt)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal discrepancy = Optional.ofNullable(group.get(0).getDiscrepancy_amt())
                    .orElse(BigDecimal.ZERO);

            // Target for ARR for this stmt_no
            // BigDecimal amtForARR = totalNetAmt.subtract(discrepancy);

            BigDecimal amtForARR;
            if (discrepancy.compareTo(BigDecimal.ZERO) < 0) {
                // Negative discrepancy: ALL goes to ARR
                amtForARR = totalNetAmt;
                log.info("({}) Negative discrepancy ({}): All amount ({}) goes to ARR",
                        stmtNo, discrepancy, totalNetAmt);
            } else {
                // Positive or zero discrepancy: deduct from total
                amtForARR = totalNetAmt.subtract(discrepancy);
            }

            // Edge case: nothing to allocate to ARR -> all go to CN/DN
            if (amtForARR.compareTo(BigDecimal.ZERO) <= 0 || discrepancy.compareTo(BigDecimal.ZERO) <= 0) {
                int index = 0;
                int group_size = group.size();
                
                for (NonReceipting nr : group) {
                    index ++;
                    try {
                        AgBankTxnReq cnDnReq = new AgBankTxnReq();
                        cnDnReq.setI_ag_sale_id(nr.getAg_sale_id());
                        cnDnReq.setI_cash_acct(nr.getCash_acct());
                        cnDnReq.setI_cn_cust_id(nr.getCn_cust_id());
                        cnDnReq.setI_dn_cust_id(nr.getDn_cust_id());
                        cnDnReq.setI_fms_ari_ref_no(nr.getFms_ari_ref_no());
                        cnDnReq.setI_ari_total_amt(nr.getAri_total_amt());
                        cnDnReq.setI_mdr_total_amt(nr.getMdr_total_amt());
                        cnDnReq.setI_discrepancy_amt(nr.getDiscrepancy_amt());
                        cnDnReq.setI_total_net_amt(nr.getTotal_net_amt());

                        if(index == group_size){
                            drMemoHid = this.sp_insfmsnonrmsreconcreditdebit(cnDnReq);  
                        }

                        cashAcctForDN = nr.getCash_acct();

                        log.info("({}) CN/DN full: ag_sale_id={}, fms_ari_ref_no={}, amt={}",
                                stmtNo, nr.getAg_sale_id(), nr.getFms_ari_ref_no(), nr.getAri_total_amt());
                    } catch (Exception e) {
                        log.error("({}) CN/DN failed ag_sale_id {}: {}", stmtNo, nr.getAg_sale_id(), e.getMessage());
                    }
                }
                if(amtForARR.compareTo(BigDecimal.ZERO) <= 0){
                    continue; // next stmt_no
                }
            }

            // ----- Find threshold index for this stmt_no -----
            BigDecimal runningSum = BigDecimal.ZERO;
            int thresholdIndex = -1;
            for (int i = 0; i < group.size(); i++) {
                BigDecimal amt = Optional.ofNullable(group.get(i).getTotal_net_amt()).orElse(BigDecimal.ZERO);
                runningSum = runningSum.add(amt);
                if (runningSum.compareTo(amtForARR) >= 0) {
                    thresholdIndex = i;
                    log.info("({}) Threshold at index {}, fms_ari_ref_no={}, running={}, target={}",
                            stmtNo, i, group.get(i).getFms_ari_ref_no(), runningSum, amtForARR);
                    break;
                }
            }

            BigDecimal cumulativeForARR = BigDecimal.ZERO;
            Integer i_fms_arr_hid = null; // reset per stmt_no

            // ----- Process this stmt_no -----
            for (int i = 0; i < group.size(); i++) {
                NonReceipting nr = group.get(i);

                AgBankTxnReq req = new AgBankTxnReq();
                req.setI_ag_sale_id(nr.getAg_sale_id());
                req.setI_cash_acct(nr.getCash_acct());
                req.setI_cn_cust_id(nr.getCn_cust_id());
                req.setI_dn_cust_id(nr.getDn_cust_id());
                req.setI_fms_ari_ref_no(nr.getFms_ari_ref_no());
                req.setI_mdr_total_amt(nr.getMdr_total_amt());
                req.setI_discrepancy_amt(nr.getDiscrepancy_amt());
                req.setI_header_amt(amtForARR); // header is stmt_no-level ARR target

                try {
                    if (thresholdIndex == -1 || i > thresholdIndex) {
                        // After threshold: full to CN/DN
                        req.setI_ari_total_amt(nr.getAri_total_amt());
                        req.setI_total_net_amt(nr.getTotal_net_amt());
                        this.sp_insfmsnonrmsreconcreditdebit(req);
                        log.info("({}) CN/DN full: ag_sale_id={}, fms_ari_ref_no={}, amt={}",
                                stmtNo, nr.getAg_sale_id(), nr.getFms_ari_ref_no(), nr.getAri_total_amt());
                        continue;
                    }

                    // Before threshold: full to ARR
                    if (i < thresholdIndex) {
                        // BigDecimal currentAmt = nr.getAri_total_amt();
                        BigDecimal currentAmt = nr.getTotal_net_amt();
                        req.setI_ari_total_amt(currentAmt);
                        req.setI_total_net_amt(currentAmt);
                        if (i == 0) {
                            req.setI_is_first(1);
                            req.setI_arr_hid(null);
                        } else {
                            req.setI_is_first(0);
                            req.setI_arr_hid(i_fms_arr_hid);
                        }
                        i_fms_arr_hid = this.sp_insfmsnonrmsrecon(req);
                        if (i_fms_arr_hid != null && i_fms_arr_hid > 0)
                            successCount++;

                        cumulativeForARR = cumulativeForARR.add(currentAmt);
                        log.info("({}) ARR full: ag_sale_id={}, fms_ari_ref_no={}, amt={}",
                                stmtNo, nr.getAg_sale_id(), nr.getFms_ari_ref_no(), currentAmt);
                        continue;
                    }

                    // At threshold: split
                    if (i == thresholdIndex) {
                        // BigDecimal currentAmt = nr.getAri_total_amt();
                        BigDecimal currentAmt = nr.getTotal_net_amt();
                        BigDecimal remainingForARR = amtForARR.subtract(cumulativeForARR);
                        BigDecimal partialForARR = remainingForARR.max(BigDecimal.ZERO)
                                .min(currentAmt); // guard
                        BigDecimal remainingForCNDN = currentAmt.subtract(partialForARR);

                        if (partialForARR.compareTo(BigDecimal.ZERO) > 0) {
                            req.setI_ari_total_amt(partialForARR);
                            req.setI_total_net_amt(partialForARR);
                            req.setI_is_first((i_fms_arr_hid == null) ? 1 : 0);
                            req.setI_arr_hid(i_fms_arr_hid);
                            Integer res = this.sp_insfmsnonrmsrecon(req);
                            if (res != null && res > 0)
                                successCount++;
                            // ensure arr_hid is set for any possible continuation
                            if (i_fms_arr_hid == null)
                                i_fms_arr_hid = res;

                            log.info("({}) ARR partial: ag_sale_id={}, fms_ari_ref_no={}, amt={} (of {})",
                                    stmtNo, nr.getAg_sale_id(), nr.getFms_ari_ref_no(), partialForARR, currentAmt);
                        }

                        if (remainingForCNDN.compareTo(BigDecimal.ZERO) > 0) {
                            AgBankTxnReq cnDnReq = new AgBankTxnReq();
                            cnDnReq.setI_ag_sale_id(nr.getAg_sale_id());
                            cnDnReq.setI_cash_acct(nr.getCash_acct());
                            cnDnReq.setI_cn_cust_id(nr.getCn_cust_id());
                            cnDnReq.setI_dn_cust_id(nr.getDn_cust_id());
                            cnDnReq.setI_fms_ari_ref_no(nr.getFms_ari_ref_no());
                            cnDnReq.setI_ari_total_amt(remainingForCNDN);
                            cnDnReq.setI_mdr_total_amt(nr.getMdr_total_amt());
                            cnDnReq.setI_discrepancy_amt(nr.getDiscrepancy_amt());
                            cnDnReq.setI_total_net_amt(remainingForCNDN);
                            this.sp_insfmsnonrmsreconcreditdebit(cnDnReq);

                            log.info("({}) CN/DN remainder: ag_sale_id={}, fms_ari_ref_no={}, amt={} (of {})",
                                    stmtNo, nr.getAg_sale_id(), nr.getFms_ari_ref_no(), remainingForCNDN, currentAmt);
                        }
                    }

                } catch (Exception e) {
                    log.error("({}) Failed ag_sale_id {}: {}", stmtNo, nr.getAg_sale_id(), e.getMessage());
                }
            }

            log.info("({}) Summary: records={}, ARR up to index={}, CN/DN after={}",
                    stmtNo, group.size(), thresholdIndex,
                    (thresholdIndex == -1 ? group.size() : group.size() - (thresholdIndex + 1)));

            try {

                if (drMemoHid > 0) {
                    fmsdrMemoService.drMemoCallAPI(BigInteger.valueOf(drMemoHid));

                    List<FMSDRMemo> fmsDRMemo = fmsdrMemoService.sp_getfmsdrmemobyhid(BigInteger.valueOf(drMemoHid));

                    // Use ARR to close the DN
                    FMSARR fmsARR = new FMSARR();
                    fmsARR.setI_cust_id(fmsDRMemo.get(0).getCust());
                    fmsARR.setI_cash_acct(cashAcctForDN);
                    fmsARR.setI_h_amt(fmsDRMemo.get(0).getAmt());
                    fmsARR.setI_fms_ref_no(fmsDRMemo.get(0).getH_fms_ref_no());
                    fmsARR.setI_b_amt(fmsDRMemo.get(0).getAmt());
                    fmsARR.setI_c_amt(BigDecimal.valueOf(0));

                    fmsarrService.sp_insfmsarrnonrmsrecon(fmsARR);

                    drMemoHid = 0; // reset for next iteration
                }

            } catch (Exception e) {
                log.error("Failed to post Debit/ Credit Note for Non RMS Recon");
            }
        }

        // Post DN (if there is any)
        // try {

        //     if (drMemoHid > 0) {
        //         fmsdrMemoService.drMemoCallAPI(BigInteger.valueOf(drMemoHid));
        //     }

        //     List<FMSDRMemo> fmsDRMemo = fmsdrMemoService.sp_getfmsdrmemobyhid(BigInteger.valueOf(drMemoHid));

        //     // Use ARR to close the DN
        //     FMSARR fmsARR = new FMSARR();
        //     fmsARR.setI_cust_id(fmsDRMemo.get(0).getCust());
        //     fmsARR.setI_cash_acct(cashAcctForDN);
        //     fmsARR.setI_h_amt(fmsDRMemo.get(0).getAmt());
        //     fmsARR.setI_fms_ref_no(fmsDRMemo.get(0).getH_fms_ref_no());
        //     fmsARR.setI_b_amt(fmsDRMemo.get(0).getAmt());
        //     fmsARR.setI_c_amt(BigDecimal.valueOf(0));

        //     fmsarrService.sp_insfmsarrnonrmsrecon(fmsARR);

        //     //#region
        //     // if(!fmsDRMemo.get(0).getFms_ref_no().isEmpty() || fmsDRMemo.get(0).getFms_ref_no() != null) {
        //     //     List<FMSCRMemo> crMemo = new ArrayList<>();
        //     //     FMSCRMemo fmscrMemo = new FMSCRMemo();

        //     //     // Header for CN
        //     //     fmscrMemo.setType("Credit Memo");
        //     //     fmscrMemo.setLink_branch(fmsDRMemo.get(0).getLink_branch());
        //     //     fmscrMemo.setPg_pymt_amt(fmsDRMemo.get(0).getAmt());
        //     //     fmscrMemo.setDesc("");
        //     //     fmscrMemo.setGenPdf(0);
        //     //     fmscrMemo.setAttr_ext_sys("");
        //     //     fmscrMemo.setCust(fmsDRMemo.get(0).getCust());

        //     //     // Body for CN
        //     //     fmscrMemo.setPg_pymt_method("");
        //     //     fmscrMemo.setMtt_pg_id(BigInteger.valueOf(0));
        //     //     fmscrMemo.setQty(fmsDRMemo.get(0).getQty_drmemo());
        //     //     fmscrMemo.setItem_desc(fmsDRMemo.get(0).getTxn_desc());
        //     //     fmscrMemo.setUnit_fee(fmsDRMemo.get(0).getUnit_price());
        //     //     fmscrMemo.setEnt_nm(fmsDRMemo.get(0).getEnt_nm());
        //     //     fmscrMemo.setEnt_no(fmsDRMemo.get(0).getEnt_no());
        //     //     fmscrMemo.setEnt_ty(fmsDRMemo.get(0).getEnt_ty());
        //     //     fmscrMemo.setDepositID(fmsDRMemo.get(0).getDepositID());
        //     //     fmscrMemo.setDepositTask(fmsDRMemo.get(0).getDepositTask());
        //     //     fmscrMemo.setCoa1(fmsDRMemo.get(0).getCoa1());
        //     //     fmscrMemo.setFee_detail_id(fmsDRMemo.get(0).getCoa2());
        //     //     fmscrMemo.setSub_acct(fmsDRMemo.get(0).getSub_acct());
        //     //     fmscrMemo.setAcct(fmsDRMemo.get(0).getAcct());
        //     //     fmscrMemo.setBranch(fmsDRMemo.get(0).getBranch());
        //     //     fmscrMemo.setGross_amt(fmsDRMemo.get(0).getUnit_price());
        //     //     fmscrMemo.setCust_nm("");
        //     //     fmscrMemo.setTax_amt(BigDecimal.valueOf(0));

        //     //     // Footer for CN
        //     //     fmscrMemo.setFms_ref_no(fmsDRMemo.get(0).getH_fms_ref_no());
        //     //     fmscrMemo.setDoc_ty("Debit Memo");                

        //     //     crMemo.add(fmscrMemo);

        //     //     // Create Credit Note to close Debit Note
        //     //     Integer crmemoHid = fmscrMemoService.newCrMemo(crMemo);

        //     //     // Direct send Credit Note to close Debit Note
        //     //     fmscrMemoService.crMemoCallAPI(BigInteger.valueOf(crmemoHid));
        //     // }
        //     //#endregion

        // } catch (Exception e) {
        //     log.error("Failed to post Debit/ Credit Note for Non RMS Recon");
        // }

        return successCount;
    }
}