package com.maven.rms.services;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import java.util.Base64;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.informix.jdbc.IfxLobDescriptor;
import com.informix.jdbc.IfxLocator;
import com.informix.jdbc.IfxSmartBlob;
// import com.lowagie.text.pdf.codec.Base64;
import com.maven.rms.interfaces.IBankReconService;
import com.maven.rms.models.BankDocRequest;
import com.maven.rms.models.BankReconDetail;
import com.maven.rms.models.BankReconRequest;
import com.maven.rms.models.BankReconResponse;
import com.maven.rms.models.PGRecon;
import com.maven.rms.models.PGReconUploadRequest;
import com.maven.rms.repositories.BankReconRepository;

@Service
@Slf4j
public class BankReconService implements IBankReconService {
    @Autowired
    private DataSource ds;

    private final BankReconRepository bankReconRepository;

    public BankReconService(BankReconRepository bankReconRepository) {
        this.bankReconRepository = bankReconRepository;
    }

    @Override
    public Integer sp_uploadDoc(BankDocRequest bankDocRequest, String username) throws SQLException, IllegalArgumentException, IOException {
        // Decode Base64 content
        byte[] decodedBytes = decodeBase64(bankDocRequest.getI_file_content());

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedBytes);
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            // Start check fixed length header
            String firstLine = br.readLine();
            Integer lineCount = firstLine.length();
            if (firstLine.length() != 1481 && firstLine.length() != 1478 && firstLine.length() != 1479) {
                return 1; // Return early if header length is incorrect
            }
        }

        Blob blob = new SerialBlob(decodedBytes);

        // Call the repository method
        Integer result = bankReconRepository.sp_uploadDoc(bankDocRequest, blob, username);
        return result;
    }

    public List<String> sp_getPGSettlementDateTaskList() {
        return bankReconRepository.sp_getPGSettlementDateTaskList();
    }

    public List<BankReconResponse> sp_getBankReconTaskList(BankReconRequest BankRequest) {
        List<BankReconResponse> result = Collections.emptyList();
        List<Object[]> objects = bankReconRepository.sp_getBankReconTaskList(BankRequest);
        result = convertToBankReconResponseList(objects);

        return result;
    }

    @Override
    public Integer sp_checkbktask(BankReconRequest bodyRequest){
        Integer result = 0;

        result = bankReconRepository.sp_checkbktask(bodyRequest);

        return result;
    }
    
    private byte[] decodeBase64(String base64String) {
        if (base64String.startsWith("data:")) {
            base64String = base64String.substring(base64String.indexOf(',') + 1);
        }
        base64String = base64String.replaceAll("\\s", "").replace(":", "");
        return Base64.getDecoder().decode(base64String);
    }

    private List<String> convertToPGSettlementDateList(List<Object[]> objects) {
        List<String> bankReconResponsesList = new ArrayList<>();
        for (Object[] obj : objects) {
            String bankReconResponse;
            bankReconResponse = (String) obj[0];
            bankReconResponsesList.add(bankReconResponse);
        }
        return bankReconResponsesList;
    }

    private List<BankReconResponse> convertToBankReconResponseList(List<Object[]> objects) {
        List<BankReconResponse> bankReconResponsesList = new ArrayList<>();

        for (Object[] obj : objects) {
            BankReconResponse bankReconResponse = new BankReconResponse();
            bankReconResponse.setI_rc_bank_id((BigInteger) obj[0]);
            bankReconResponse.setI_task_no((String) obj[1]);
            bankReconResponse.setI_dt_settlement((String) obj[2]);
            bankReconResponse.setI_merchant_id((String) obj[3]);
            bankReconResponse.setI_task_status((String) obj[4]);
            bankReconResponse.setI_dt_uploaded((String) obj[5]);
            bankReconResponse.setI_recon_status((String) obj[6]);
            bankReconResponse.setTotal((Integer) obj[7]);
            bankReconResponse.setI_uploaded_by((String) obj[8]);
            bankReconResponsesList.add(bankReconResponse);
        }

        return bankReconResponsesList;
    }

}
