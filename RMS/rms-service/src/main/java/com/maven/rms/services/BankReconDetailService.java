package com.maven.rms.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IBankReconDetailService;
import com.maven.rms.models.BankReconRequest;
import com.maven.rms.models.PGDetailListingRequest;
import com.maven.rms.models.BankReconDetail;
import com.maven.rms.repositories.BankReconDetailRepository;

@Service
public class BankReconDetailService implements IBankReconDetailService {
    private final BankReconDetailRepository bankReconDetailRepository;

    public BankReconDetailService(BankReconDetailRepository bankReconDetailRepository) {
        this.bankReconDetailRepository = bankReconDetailRepository;
    }

    @Override
    public List<BankReconDetail> sp_getrcbankdetails(BankReconDetail bankReconDetail)// String i_task_no
    {
        List<BankReconDetail> result = Collections.emptyList();

        // try
        // {
        List<Object[]> objects = bankReconDetailRepository.sp_getrcbankdetails(bankReconDetail);
        result = convertBankReconDetailListing(objects);
        // }catch (Exception e) {
        // e.printStackTrace();
        // }

        return result;
    }

    private List<BankReconDetail> convertBankReconDetailListing(List<Object[]> objects) {
        List<BankReconDetail> bankReconDetailListing = new ArrayList<>();

        for (Object[] obj : objects) {
            BankReconDetail bankRecon = new BankReconDetail();
            bankRecon.setFile_nm((String) obj[0]);
            bankRecon.setTotal_no_pg_txn((Integer) obj[1]);
            bankRecon.setTotal_gross_amt((BigDecimal) obj[2]);
            bankRecon.setTotal_mdr((BigDecimal) obj[3]);
            bankRecon.setTotal_net_amt((BigDecimal) obj[4]);
            bankRecon.setTotal_no_bk_txn((Integer) obj[5]);
            bankRecon.setTotal_bank_txn((Integer) obj[6]);
            bankRecon.setTotal_pg_file_txn((Integer) obj[7]);
            bankRecon.setTotal_pg_disbursed_amt((BigDecimal) obj[8]);
            bankRecon.setTask_no((String) obj[9]);
            bankRecon.setRecon_status((String) obj[10]);
            bankRecon.setTask_status((String) obj[11]);
            bankRecon.setRemarks((String) obj[12]);
            bankRecon.setStmt_no((String) obj[13]);
            bankRecon.setDt_settlement((Date) obj[14]);
            bankReconDetailListing.add(bankRecon);
        }

        return bankReconDetailListing;
    }

    @Override
    public List<BankReconDetail> sp_getbankpgtxnlisting(PGDetailListingRequest pgDetailListingRequest) {
        List<BankReconDetail> result = Collections.emptyList();

        // try
        // {
        List<Object[]> objects = bankReconDetailRepository.sp_getbankpgtxnlisting(pgDetailListingRequest);
        result = convertBankPgTxnListing(objects);
        // }catch (Exception e) {
        // e.printStackTrace();
        // }

        return result;
    }

    private List<BankReconDetail> convertBankPgTxnListing(List<Object[]> objects) {
        List<BankReconDetail> bankPgTxnListing = new ArrayList<>();

        for (Object[] obj : objects) {
            BankReconDetail bankRecon = new BankReconDetail();
            bankRecon.setDt_txn((Date) obj[0]);
            bankRecon.setTxn_id((String) obj[1]);
            bankRecon.setTxn_type((String) obj[2]);
            bankRecon.setTxn_cd((String) obj[3]);
            bankRecon.setFound_in_pg((String) obj[4]);
            bankRecon.setTxn_amt((BigDecimal) obj[5]);
            bankRecon.setMdr_amt((BigDecimal) obj[6]);
            bankRecon.setSst_amt((BigDecimal) obj[7]);
            bankRecon.setNet_amt((BigDecimal) obj[8]);
            bankRecon.setTotal((Integer) obj[9]);
            bankPgTxnListing.add(bankRecon);
        }

        return bankPgTxnListing;
    }

    @Override
    public List<BankReconDetail> sp_getbanktxnlisting(BankReconDetail bankTxnListingRequest) {
        List<BankReconDetail> result = Collections.emptyList();

        // try
        // {
        List<Object[]> objects = bankReconDetailRepository.sp_getbanktxnlisting(bankTxnListingRequest);
        result = convertBankTxnListing(objects);
        // }catch (Exception e) {
        // e.printStackTrace();
        // }

        return result;
    }

    private List<BankReconDetail> convertBankTxnListing(List<Object[]> objects) {
        List<BankReconDetail> bankTxnListing = new ArrayList<>();

        for (Object[] obj : objects) {
            BankReconDetail bankRecon = new BankReconDetail();
            bankRecon.setTxn_ref((String) obj[0]);
            bankRecon.setAcct_no((String) obj[1]);
            bankRecon.setBrn_chn((String) obj[2]);
            bankRecon.setDt_posting((Date) obj[3]);
            bankRecon.setCredit((BigDecimal) obj[4]);
            bankRecon.setTotal((Integer) obj[5]);
            bankTxnListing.add(bankRecon);
        }

        return bankTxnListing;
    }

    @Override
    public List<BankReconDetail> sp_getbankpgfiletxn(BankReconDetail pgfilerelatedtxnRequest) {
        List<BankReconDetail> result = Collections.emptyList();

        // try
        // {
        List<Object[]> objects = bankReconDetailRepository.sp_getbankpgfiletxn(pgfilerelatedtxnRequest);
        result = convertbankpgfiletxn(objects);
        // }catch (Exception e) {
        // e.printStackTrace();
        // }

        return result;
    }

    private List<BankReconDetail> convertbankpgfiletxn(List<Object[]> objects) {
        List<BankReconDetail> bankpgfiletxn = new ArrayList<>();

        for (Object[] obj : objects) {
            BankReconDetail bankRecon = new BankReconDetail();
            bankRecon.setTxn_ref((String) obj[0]);
            bankRecon.setCredit((BigDecimal) obj[1]);
            bankRecon.setDt_posting((Date) obj[2]);
            bankRecon.setTotal((Integer) obj[3]);
            bankpgfiletxn.add(bankRecon);
        }

        return bankpgfiletxn;
    }

    @Override
    public List<BankReconDetail> sp_getbanknostmt(BankReconDetail nobankstmtRequest) {
        List<BankReconDetail> result = Collections.emptyList();

        // try
        // {
        List<Object[]> objects = bankReconDetailRepository.sp_getbanknostmt(nobankstmtRequest);
        result = convertbanknostmt(objects);
        // }catch (Exception e) {
        // e.printStackTrace();
        // }

        return result;
    }

    private List<BankReconDetail> convertbanknostmt(List<Object[]> objects) {
        List<BankReconDetail> banknostmt = new ArrayList<>();

        for (Object[] obj : objects) {
            BankReconDetail bankRecon = new BankReconDetail();
            bankRecon.setFile_nm((String) obj[0]);
            bankRecon.setFile_size((Integer) obj[1]);
            bankRecon.setUploaded_by((String) obj[2]);
            bankRecon.setDt_uploaded((Date) obj[3]);
            bankRecon.setTotal((Integer) obj[4]);
            banknostmt.add(bankRecon);
        }

        return banknostmt;
    }

    @Override
    public BigInteger sp_updrcbankdetailstatus(BankReconDetail bankDetailStatusRequest) {
        BigInteger result = BigInteger.valueOf(0);

        // try{
        result = bankReconDetailRepository.sp_updrcbankdetailstatus(bankDetailStatusRequest);
        // }catch(Exception e){
        // result = BigInteger.valueOf(0);
        // e.printStackTrace();
        // }

        return result;
    }

    // @Override
    // public String sp_getrcpgdoc(BankReconRequest bankReconDetailRequest)
    // {
    // String result = "";

    // try{
    // Blob blob = (Blob)
    // bankReconDetailRepository.sp_getrcpgdoc(bankReconDetailRequest);
    // // Convert Blob to byte array
    // byte[] bytes = blob.getBytes(1, (int) blob.length());

    // // Convert byte array to Base64-encoded string
    // String base64Content = Base64.getEncoder().encodeToString(bytes);
    // result = base64Content;

    // }catch(Exception e){
    // e.printStackTrace();
    // }

    // return result;
    // }
    @Override
    public String sp_getrcpgdoc(BankReconRequest bankReconDetailRequest) throws SQLException {
        Blob blob = (Blob) bankReconDetailRepository.sp_getrcpgdoc(bankReconDetailRequest);

        // Convert Blob to byte array
        byte[] bytes = blob.getBytes(1, (int) blob.length());

        // Convert byte array to Base64-encoded string
        String base64Content = Base64.getEncoder().encodeToString(bytes);

        return base64Content;
    }

    // @Override
    // public String sp_getrcbkdoc(BankReconDetail bankReconDetailRequest) {
    // String result = "";

    // try {
    // Blob blob = (Blob)
    // bankReconDetailRepository.sp_getrcbkdoc(bankReconDetailRequest);
    // // Convert Blob to byte array
    // byte[] bytes = blob.getBytes(1, (int) blob.length());

    // // Convert byte array to Base64-encoded string
    // String base64Content = Base64.getEncoder().encodeToString(bytes);
    // result = base64Content;

    // } catch (Exception e) {
    // e.printStackTrace();
    // }

    // return result;
    // }
    @Override
    public String sp_getrcbkdoc(BankReconDetail bankReconDetailRequest) throws SQLException {
        Blob blob = (Blob) bankReconDetailRepository.sp_getrcbkdoc(bankReconDetailRequest);

        // Convert Blob to byte array
        byte[] bytes = blob.getBytes(1, (int) blob.length());

        // Convert byte array to Base64-encoded string
        return Base64.getEncoder().encodeToString(bytes);
    }

}
