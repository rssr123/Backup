package com.maven.rms.interfaces;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;
import com.maven.rms.models.BankReconDetail;
import com.maven.rms.models.BankReconRequest;
import com.maven.rms.models.PGDetailListingRequest;

public interface IBankReconDetailService {
    List<BankReconDetail> sp_getbanktxnlisting(BankReconDetail bankTxnListingRequest);

    List<BankReconDetail> sp_getbankpgtxnlisting(PGDetailListingRequest pgDetailListingRequest);

    List<BankReconDetail> sp_getbankpgfiletxn(BankReconDetail pgfilerelatedtxnRequest);

    List<BankReconDetail> sp_getbanknostmt(BankReconDetail nobankstmtRequest);

    List<BankReconDetail> sp_getrcbankdetails(BankReconDetail bankReconDetail);

    BigInteger sp_updrcbankdetailstatus(BankReconDetail bankDetailStatusRequest);

    String sp_getrcpgdoc(BankReconRequest bankReconDetailRequest) throws SQLException;

    String sp_getrcbkdoc(BankReconDetail bankReconDetailRequest) throws SQLException;
}
