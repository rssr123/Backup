package com.maven.rms.services;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IBankReconSchService;
import com.maven.rms.models.BankReconSch;
import com.maven.rms.repositories.BankReconSchRepository;

@Service
@Slf4j
public class BankReconSchService implements IBankReconSchService {

    private final BankReconSchRepository bankReconSchrepository;

    public BankReconSchService(BankReconSchRepository bankReconSchrepository) {
        this.bankReconSchrepository = bankReconSchrepository;
    }

    // Fixed column lengths
    private static final int acct_no_start = 0;
    private static final int acct_no_end = 51;
    private static final int acct_type_start = 51;
    private static final int acct_type_end = 102;
    private static final int acct_nm_start = 102;
    private static final int acct_nm_end = 152;
    private static final int dt_fr_start = 204;
    private static final int dt_fr_end = 214;
    private static final int dt_to_start = 255;
    private static final int dt_to_end = 266;

    private static final int total_debit_start = 306;
    private static final int total_debit_end = 357;
    private static final int total_credit_start = 357;
    private static final int total_credit_end = 408;
    private static final int begin_bal_start = 408;
    private static final int begin_bal_end = 459;
    private static final int end_bal_start = 459;
    private static final int end_bal_end = 510;
    private static final int dt_txn_start = 510;
    private static final int dt_txn_end = 561;
    private static final int time_txn_start = 561;
    private static final int time_txn_end = 566;

    private static final int dt_posting_start = 612;
    private static final int dt_posting_end = 623;
    private static final int time_posting_start = 663;
    private static final int time_posting_end = 668;
    private static final int txn_desc_start = 714;
    private static final int txn_desc_end = 765;
    private static final int txn_ref_start = 765;
    private static final int txn_ref_end = 816;
    private static final int debit_start = 816;
    private static final int debit_end = 867;
    private static final int credit_start = 867;
    private static final int credit_end = 918;

    private static final int source_cd_start = 918;
    private static final int source_cd_end = 969;
    private static final int teller_id_start = 969;
    private static final int teller_id_end = 1020;
    private static final int brn_chn_start = 1020;
    private static final int brn_chn_end = 1071;
    private static final int txn_cd_start = 1071;
    private static final int txn_cd_end = 1122;
    private static final int end_bal2_start = 1122;
    private static final int end_bal2_end = 1224;

    private static final int virtual_acct_start = 1224;
    private static final int virtual_acct_end = 1275;
    private static final int txn_desc2_start = 1275;
    private static final int txn_desc2_end = 1326;
    private static final int txn_desc3_start = 1326;
    private static final int txn_desc3_end = 1377;
    private static final int txn_desc4_start = 1377;
    private static final int txn_desc4_end = 1428;
    private static final int dt_expiry_start = 1428;
    private static final int dt_expiry_end = 1478;

    // Scheduler for extract file
    public int sp_insrcbanktxn() {
        int result = 0;
        int success = 0;
        int failed = 0;
        BigInteger r_insBankTxn = BigInteger.valueOf(0);
        List<BankReconSch> bankReconSchList = new ArrayList<>();

        bankReconSchList = convertToBankReconDocsList();

        if (bankReconSchList != null && !bankReconSchList.isEmpty()) {
            for (BankReconSch blobFiles : bankReconSchList) {
                Blob o_fileContent = blobFiles.getFile_content();
                BigInteger o_rc_bankdoc_id = blobFiles.getRc_bankdoc_id();
                BigInteger o_rc_Bank_id = blobFiles.getRc_bank_id();

                try {
                    blobFiles.setStatus("EIP");
                    bankReconSchrepository.sp_updrcbank(blobFiles);

                    byte[] bytes = o_fileContent.getBytes(1, (int) o_fileContent.length());

                    r_insBankTxn = getTransactionFromFile(bytes, o_rc_bankdoc_id, o_rc_Bank_id);
                    if (r_insBankTxn.compareTo(BigInteger.valueOf(0)) > 0) {
                        // Call SP to update rc bank status
                        blobFiles.setStatus("PBSR");
                        bankReconSchrepository.sp_updrcbank(blobFiles);
                        success++;
                    } else {
                        blobFiles.setStatus("EF");
                        bankReconSchrepository.sp_updrcbank(blobFiles);
                        failed++;
                    }
                } 
                catch (Exception e) {
                    blobFiles.setStatus("EF");
                    bankReconSchrepository.sp_updrcbank(blobFiles);
                    failed++;
                    log.error("Exception in " + this.getClass().toString(), e);
                }
            }
        } else {
            result = 0;
        }

        if (success > failed) {
            result = success;
            return result;
        } else {
            return 0;
        }
    }

    // Get listing from rc_bank
    public List<Object[]> sp_getBankReconTxn() {
        List<Object[]> result = new ArrayList<>();

        result = bankReconSchrepository.sp_getBankDoc();
        if (result != null && !result.isEmpty()) {
            return result;
        } else {
            return new ArrayList<>();
        }
    }

    // Update rc_bank
    public BigInteger sp_updRcBank(BankReconSch account) {

        BigInteger result = BigInteger.valueOf(0);

        result = bankReconSchrepository.sp_updrcbank(account);
        if (result != null) {
            result = (BigInteger) result;
        }

        return result;
    }

    // Scheduler for compairison credit
    public Integer sp_updrcbanktxn() {
        Integer result = 0;

        result = bankReconSchrepository.sp_updrcbanktxn();

        return result;
    }

    // Get List
    private List<BankReconSch> convertToBankReconDocsList() {
        List<Object[]> resultList = new ArrayList<>();
        List<BankReconSch> result = new ArrayList<>();

        resultList = bankReconSchrepository.sp_getBankDoc();
        
        if (resultList != null && !resultList.isEmpty()) {
            for (Object[] obj : resultList) {
                BankReconSch bankReconDoc = new BankReconSch();
                bankReconDoc.setRc_bank_id((BigInteger) obj[0]);
                bankReconDoc.setRc_bankdoc_id((BigInteger) obj[1]);
                bankReconDoc.setFile_nm((String) obj[2]);
                bankReconDoc.setFile_type((String) obj[3]);
                bankReconDoc.setFile_content(bankReconSchrepository.sp_getrcbankdoc((BigInteger) obj[1]));
                result.add(bankReconDoc);
            }
        }
        else{
            result = new ArrayList<>();
        }

        return result;
    }

    private BigInteger getTransactionFromFile(byte[] fileInBytes, BigInteger rc_bankdoc_id, BigInteger rc_Bank_id)
            throws IOException, SQLException {

        List<BankReconSch> accounts = new ArrayList<>();
        BigInteger result = BigInteger.valueOf(0);

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(fileInBytes);
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            // Skip header if present
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                BankReconSch account = getFileContent(line);
                accounts.add(account);
                result = bankReconSchrepository.sp_insrcbankdoc(account, rc_Bank_id, rc_bankdoc_id);
            }
        }

        return result;
    }

    // Fixed length file content
    private BankReconSch getFileContent(String line) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        BankReconSch account = new BankReconSch();

        // try{
        account.setAcct_no(line.substring(acct_no_start, acct_no_end).trim());
        account.setAcct_type(line.substring(acct_type_start, acct_type_end).trim());
        account.setAcct_nm(line.substring(acct_nm_start, acct_nm_end).trim());
        account.setDt_fr(java.sql.Date.valueOf(line.substring(dt_fr_start, dt_fr_end).trim()));
        account.setDt_to(java.sql.Date.valueOf(line.substring(dt_to_start, dt_to_end).trim()));

        account.setTotal_debit(
                Integer.parseInt(line.substring(total_debit_start, total_debit_end).trim().replace(",", "")));
        account.setTotal_credit(
                Integer.parseInt(line.substring(total_credit_start, total_credit_end).trim().replace(",", "")));
        account.setBegin_bal(line.substring(begin_bal_start, begin_bal_end).trim().replace(",", ""));
        account.setEnd_bal(line.substring(end_bal_start, end_bal_end).trim().replace(",", ""));

        if ("-".equals(line.substring(dt_txn_start, dt_txn_end).trim())) {
            account.setDt_txn(null);
        } else {
            String o_dt_txn = line.substring(dt_txn_start, dt_txn_end).trim();
            LocalDate o_date_txn = LocalDate.parse(o_dt_txn, inputFormatter);
            o_dt_txn = o_date_txn.format(outputFormatter);
            if ("-".equals(line.substring(time_txn_start, time_txn_end).trim() + ":00")) {
                account.setDt_txn(o_dt_txn);
            } else {
                String f_dt_txn = o_dt_txn + " " + line.substring(time_txn_start, time_txn_end).trim() + ":00";
                account.setDt_txn(f_dt_txn);
            }
        }

        String o_dt_posting = line.substring(dt_posting_start, dt_posting_end).trim();
        LocalDate o_date_posting = LocalDate.parse(o_dt_posting, inputFormatter);
        o_dt_posting = o_date_posting.format(outputFormatter);
        String f_dt_posting = o_dt_posting + " " + line.substring(time_posting_start, time_posting_end).trim() + ":00";
        account.setDt_posting(f_dt_posting);
        account.setTxn_desc(line.substring(txn_desc_start, txn_desc_end).trim());
        account.setTxn_ref(line.substring(txn_ref_start, txn_ref_end).trim());

        if ("-".equals(line.substring(debit_start, debit_end).trim())) {
            account.setDebit("0.00");
        } else {
            account.setDebit(line.substring(debit_start, debit_end).trim().replace(",", ""));
        }

        if ("-".equals(line.substring(credit_start, credit_end).trim())) {
            account.setCredit("0.00");
        } else {
            account.setCredit(line.substring(credit_start, credit_end).trim().replace(",", ""));
        }

        account.setSource_cd(line.substring(source_cd_start, source_cd_end).trim());
        account.setTeller_id(line.substring(teller_id_start, teller_id_end).trim());
        account.setBrn_chn(line.substring(brn_chn_start, brn_chn_end).trim());
        account.setTxn_cd(line.substring(txn_cd_start, txn_cd_end).trim());
        account.setEnd_bal2(line.substring(end_bal2_start, end_bal2_end).trim().replace(",", ""));

        if ("-".equals(line.substring(virtual_acct_start, virtual_acct_end).trim())) {
            account.setVirtual_acct(null);
        } else {
            account.setVirtual_acct(line.substring(virtual_acct_start, virtual_acct_end).trim());
        }

        if ("-".equals(line.substring(txn_desc2_start, txn_desc2_end).trim())) {
            account.setTxn_desc2(null);
        } else {
            account.setTxn_desc2(line.substring(txn_desc2_start, txn_desc2_end).trim());
        }

        if ("-".equals(line.substring(txn_desc3_start, txn_desc3_end).trim())) {
            account.setTxn_desc3(null);
        } else {
            account.setTxn_desc3(line.substring(txn_desc3_start, txn_desc3_end).trim());
        }

        if ("-".equals(line.substring(txn_desc4_start, txn_desc4_end).trim())) {
            account.setTxn_desc4(null);
        } else {
            account.setTxn_desc4(line.substring(txn_desc4_start, txn_desc4_end).trim());
        }

        if ("-".equals(line.substring(dt_expiry_start, dt_expiry_end).trim())) {
            account.setDt_expiry(null);
        } else {
            account.setDt_expiry(java.sql.Date.valueOf(line.substring(dt_expiry_start, dt_expiry_end).trim()));
        }

        return account;
    }
}
