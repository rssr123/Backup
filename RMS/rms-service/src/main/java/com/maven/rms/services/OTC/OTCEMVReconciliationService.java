package com.maven.rms.services.OTC;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.maven.rms.services.AuthService;

import com.maven.rms.repositories.OTC.OTCEMVReconciliationRepository;

import com.maven.rms.models.OTC.OTCEMVReconciliationRequest;
import com.maven.rms.models.OTC.OTCEMVReconciliationDocRequest;
import com.maven.rms.models.OTC.OTCEMVReconciliationDocUpRequest;
import com.maven.rms.models.OTC.OTCEMVReconciliationStatusRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;
import com.maven.rms.models.OTC.OTCEMVReconciliation;
import com.maven.rms.models.OTC.OTCEMVReconciliationBoolean;
import com.maven.rms.models.OTC.OTCEMVReconciliationSummary;
import com.maven.rms.models.OTC.OTCEMVReconciliationRC;
import com.maven.rms.models.OTC.OTCEMVReconciliationSettlement;
import com.maven.rms.models.OTC.OTCEMVReconciliationSettlement2;
import com.maven.rms.models.OTC.OTCEMVReconciliationStatus;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OTCEMVReconciliationService implements IOTCEMVReconciliationServiceInterface {

    private final OTCEMVReconciliationRepository otcEmvReconciliationRepository;

    @Autowired
    private AuthService authService;

    public OTCEMVReconciliationService(OTCEMVReconciliationRepository otcEmvReconciliationRepository) {
        this.otcEmvReconciliationRepository = otcEmvReconciliationRepository;
    }

    @Override
    public List<OTCEMVReconciliation> sp_getotcemvreconciliation(OTCEMVReconciliationRequest otcEmvReconciliationRequest) {
        List<OTCEMVReconciliation> result = Collections.emptyList();
        List<Object[]> objects = otcEmvReconciliationRepository.sp_getotcemvreconciliation(otcEmvReconciliationRequest);
        result = convertOTCEMVReconciliationList(objects);
        return result;
    }

    private List<OTCEMVReconciliation> convertOTCEMVReconciliationList(List<Object[]> objects) {
        List<OTCEMVReconciliation> otcEmvReconciliations = new ArrayList<>();

        for (Object[] obj : objects) {
            OTCEMVReconciliation otcEmvReconciliation = new OTCEMVReconciliation();
            otcEmvReconciliation.setBranch_cd((String) obj[0]);
            otcEmvReconciliation.setBal_status((String) obj[1]);
            otcEmvReconciliation.setEmv_settlement_count((Integer) obj[2]);
            otcEmvReconciliation.setEmv_amt((BigDecimal) obj[3]);
            otcEmvReconciliation.setTotal((Integer) obj[4]);
            otcEmvReconciliations.add(otcEmvReconciliation);
        }
        return otcEmvReconciliations;
    }

    @Override
    public List<OTCEMVReconciliationBoolean> sp_getotcemvreconciliationcheck(OTCEMVReconciliationRequest otcEmvReconciliationRequest) {
        List<OTCEMVReconciliationBoolean> result = Collections.emptyList();
        List<Object[]> objects = otcEmvReconciliationRepository.sp_getotcemvreconciliationcheck(otcEmvReconciliationRequest);
        result = convertOTCEMVReconciliationCheckList(objects);
        return result;
    }

    private List<OTCEMVReconciliationBoolean> convertOTCEMVReconciliationCheckList(List<Object[]> objects) {
        List<OTCEMVReconciliationBoolean> otcEmvReconciliations = new ArrayList<>();

        for (Object[] obj : objects) {
            OTCEMVReconciliationBoolean otcEmvReconciliation = new OTCEMVReconciliationBoolean();
            otcEmvReconciliation.setFlag((Integer) obj[0]);
            otcEmvReconciliations.add(otcEmvReconciliation);
        }
        return otcEmvReconciliations;
    }

    @Override
    public List<OTCEMVReconciliationSummary> sp_getotcemvreconciliationsummary(OTCEMVReconciliationRequest otcEmvReconciliationRequest) {
        List<OTCEMVReconciliationSummary> result = Collections.emptyList();
        List<Object[]> objects = otcEmvReconciliationRepository.sp_getotcemvreconciliationsummary(otcEmvReconciliationRequest);
        result = convertOTCEMVReconciliationSummaryList(objects);
        return result;
    }

    private List<OTCEMVReconciliationSummary> convertOTCEMVReconciliationSummaryList(List<Object[]> objects) {
        List<OTCEMVReconciliationSummary> otcEmvReconciliations = new ArrayList<>();

        for (Object[] obj : objects) {
            OTCEMVReconciliationSummary otcEmvReconciliation = new OTCEMVReconciliationSummary();
            otcEmvReconciliation.setBranch_count((Integer) obj[0]);
            otcEmvReconciliation.setDate_period((Date) obj[1]);
            otcEmvReconciliation.setEmv_settlement_count((Integer) obj[2]);
            otcEmvReconciliation.setEmv_transaction_count((Integer) obj[3]);
            otcEmvReconciliation.setEmv_amt((BigDecimal) obj[4]);
            otcEmvReconciliation.setReceipts_cancelled_count((Integer) obj[5]);
            otcEmvReconciliations.add(otcEmvReconciliation);
        }
        return otcEmvReconciliations;
    }

    @Override
    public List<OTCEMVReconciliationRC> sp_getotcemvreconciliationrc(OTCEMVReconciliationRequest otcEmvReconciliationRequest) {
        List<OTCEMVReconciliationRC> result = Collections.emptyList();
        List<Object[]> objects = otcEmvReconciliationRepository.sp_getotcemvreconciliationrc(otcEmvReconciliationRequest);
        result = convertOTCEMVReconciliationRCList(objects);
        return result;
    }

    private List<OTCEMVReconciliationRC> convertOTCEMVReconciliationRCList(List<Object[]> objects) {
        List<OTCEMVReconciliationRC> otcEmvReconciliations = new ArrayList<>();

        for (Object[] obj : objects) {
            OTCEMVReconciliationRC otcEmvReconciliation = new OTCEMVReconciliationRC();
            otcEmvReconciliation.setBranch_cd((String) obj[0]);
            otcEmvReconciliation.setColl_slip_no((String) obj[1]);
            otcEmvReconciliation.setOrn_no((String) obj[2]);
            otcEmvReconciliation.setRcpt_no((String) obj[3]);
            otcEmvReconciliation.setAmount((BigDecimal) obj[4]);
            otcEmvReconciliation.setPayment_mode((String) obj[5]);
            otcEmvReconciliation.setRequested_by((String) obj[6]);
            otcEmvReconciliation.setApproved_by((String) obj[7]);
            otcEmvReconciliation.setReason((String) obj[8]);
            otcEmvReconciliation.setMtt_id((Integer) obj[9]);
            otcEmvReconciliation.setOtc_id((Integer) obj[10]);
            otcEmvReconciliation.setOtc_counter_id((Integer) obj[11]);
            otcEmvReconciliation.setCounter_id((String) obj[12]);
            otcEmvReconciliation.setOtc_pymt_mode((String) obj[13]);
            otcEmvReconciliations.add(otcEmvReconciliation);
        }
        return otcEmvReconciliations;
    }

    @Override
    public List<OTCEMVReconciliationSettlement> sp_getotcemvreconciliationsettlement(OTCEMVReconciliationRequest otcEmvReconciliationRequest) {
        List<OTCEMVReconciliationSettlement> result = Collections.emptyList();
        List<Object[]> objects = otcEmvReconciliationRepository.sp_getotcemvreconciliationsettlement(otcEmvReconciliationRequest);
        result = convertOTCEMVReconciliationSettlementList(objects);
        return result;
    }

    private List<OTCEMVReconciliationSettlement> convertOTCEMVReconciliationSettlementList(List<Object[]> objects) {
        List<OTCEMVReconciliationSettlement> otcEmvReconciliations = new ArrayList<>();

        for (Object[] obj : objects) {
            OTCEMVReconciliationSettlement otcEmvReconciliation = new OTCEMVReconciliationSettlement();
            otcEmvReconciliation.setBranch_cd((String) obj[0]);
            otcEmvReconciliation.setFile_nm((String) obj[1]);
            otcEmvReconciliation.setTerminal_id((String) obj[2]);
            otcEmvReconciliation.setDate((Date) obj[3]);
            otcEmvReconciliation.setBatch_no((String) obj[4]);
            otcEmvReconciliation.setBatch_count((String) obj[5]);
            otcEmvReconciliation.setBatch_amt((BigDecimal) obj[6]);
            otcEmvReconciliation.setOtc_bal_doc_id((BigInteger) obj[7]);
            otcEmvReconciliations.add(otcEmvReconciliation);
        }
        return otcEmvReconciliations;
    }

    @Override
    public String sp_getotcbaldoccontent(OTCEMVReconciliationDocRequest req) {

        String result = "";

        try {
            Blob blob = (Blob) otcEmvReconciliationRepository.sp_getotcbaldoccontent(req);
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
    public List<OTCEMVReconciliationStatus> sp_getrcemv(OTCEMVReconciliationStatusRequest getRequest) {
        List<OTCEMVReconciliationStatus> result = Collections.emptyList();
        List<Object[]> objects = otcEmvReconciliationRepository.sp_getrcemv(getRequest);
        result = convertOTCEMVReconciliationStatusList(objects);
        return result;
    }

    private List<OTCEMVReconciliationStatus> convertOTCEMVReconciliationStatusList(List<Object[]> objects) {
        List<OTCEMVReconciliationStatus> otcEmvReconciliations = new ArrayList<>();

        for (Object[] obj : objects) {
            OTCEMVReconciliationStatus otcEmvReconciliation = new OTCEMVReconciliationStatus();
            otcEmvReconciliation.setRc_emv_id((BigInteger) obj[0]);
            otcEmvReconciliation.setDt_balancing((Date) obj[1]);
            otcEmvReconciliation.setDt_created((Date) obj[2]);
            otcEmvReconciliation.setDt_modified((Date) obj[3]);
            otcEmvReconciliation.setCreated_by((String) obj[4]);
            otcEmvReconciliation.setModified_by((String) obj[5]);
            otcEmvReconciliation.setStatus((String) obj[6]);
            otcEmvReconciliation.setRc_emv_status((String) obj[7]);
            otcEmvReconciliations.add(otcEmvReconciliation);
        }
        return otcEmvReconciliations;
    }

    @Override
    public Integer sp_insrcemv(OTCEMVReconciliationStatusRequest insertRequest) {
        Integer result = 0;

            result = otcEmvReconciliationRepository.sp_insrcemv(insertRequest);

        return result;
    }

    @Override
    public Integer sp_updrcemv(OTCEMVReconciliationStatusRequest updateRequest) {
        Integer result = 0;

            result = otcEmvReconciliationRepository.sp_updrcemv(updateRequest);

        return result;
    }

    @Override
    public Integer sp_insrcemvdoc(OTCEMVReconciliationDocUpRequest insertRequest) throws SerialException, SQLException {
        byte[] decodedBytes = decodeBase64(insertRequest.getI_file_content());
        Blob blob = new SerialBlob(decodedBytes);

            Integer result = otcEmvReconciliationRepository.sp_insrcemvdoc(insertRequest, blob);

        return result;
    }

    // helper function
    private byte[] decodeBase64(String base64String) {
        if (base64String.startsWith("data:")) {
            base64String = base64String.substring(base64String.indexOf(',') + 1);
        }
        base64String = base64String.replaceAll("\\s", "").replace(":", "");
        return Base64.getDecoder().decode(base64String);
    }

    @Override
    public List<OTCEMVReconciliationSettlement2> sp_getrcemvdoclist(OTCEMVReconciliationDocRequest otcEmvReconciliationRequest) {
        List<OTCEMVReconciliationSettlement2> result = Collections.emptyList();
        List<Object[]> objects = otcEmvReconciliationRepository.sp_getrcemvdoclist(otcEmvReconciliationRequest);
        result = convertOTCEMVReconciliationSettlement2List(objects);
        return result;
    }

    private List<OTCEMVReconciliationSettlement2> convertOTCEMVReconciliationSettlement2List(List<Object[]> objects) {
        List<OTCEMVReconciliationSettlement2> otcEmvReconciliations = new ArrayList<>();

        for (Object[] obj : objects) {
            OTCEMVReconciliationSettlement2 otcEmvReconciliation = new OTCEMVReconciliationSettlement2();
            otcEmvReconciliation.setRc_emv_doc_id((BigInteger) obj[0]);
            otcEmvReconciliation.setDt_balancing((Date) obj[1]);
            otcEmvReconciliation.setFile_nm((String) obj[2]);
            otcEmvReconciliation.setFile_type((String) obj[3]);
            otcEmvReconciliation.setFile_size((Integer) obj[4]);
            otcEmvReconciliation.setDr_count((Integer) obj[5]);
            otcEmvReconciliation.setDr_amt((BigDecimal) obj[6]);
            otcEmvReconciliation.setCr_count((Integer) obj[7]);
            otcEmvReconciliation.setCr_amt((BigDecimal) obj[8]);
            otcEmvReconciliation.setTotal((BigDecimal) obj[9]);
            otcEmvReconciliation.setDt_created((Date) obj[10]);
            otcEmvReconciliation.setDt_modified((Date) obj[11]);
            otcEmvReconciliation.setCreated_by((String) obj[12]);
            otcEmvReconciliation.setModified_by((String) obj[13]);
            otcEmvReconciliation.setStatus((String) obj[14]);
            otcEmvReconciliation.setRc_emv_id((BigInteger) obj[15]);
            otcEmvReconciliations.add(otcEmvReconciliation);
        }
        return otcEmvReconciliations;
    }

    @Override
    public String sp_getrcemvdoccontent(OTCEMVReconciliationDocRequest req) {

        String result = "";

        try {
            Blob blob = (Blob) otcEmvReconciliationRepository.sp_getrcemvdoccontent(req);
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
    
}
