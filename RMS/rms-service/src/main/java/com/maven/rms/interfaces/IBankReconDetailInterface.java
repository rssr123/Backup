package com.maven.rms.interfaces;

import java.math.BigInteger;
import java.sql.Blob;
import java.util.List;
import com.maven.rms.models.BankReconRequest;
import com.maven.rms.models.PGDetailListingRequest;
import com.maven.rms.models.BankReconDetail;

public interface IBankReconDetailInterface {
    List<Object[]> sp_getbanktxnlisting(BankReconDetail bankTxnListingRequest);

    List<Object[]> sp_getbankpgtxnlisting(PGDetailListingRequest pgDetailListingRequest);

    List<Object[]> sp_getrcbankdetails(BankReconDetail bankReconDetail);

    List<Object[]> sp_getbankpgfiletxn(BankReconDetail pgfilerelatedtxnRequest);

    List<Object[]> sp_getbanknostmt(BankReconDetail nobankstmtRequest);

    BigInteger sp_updrcbankdetailstatus(BankReconDetail bankDetailStatusRequest);

    Blob sp_getrcpgdoc(BankReconRequest bankReconDetailRequest);

    Blob sp_getrcbkdoc(BankReconDetail bankReconDetailRequest);
}
