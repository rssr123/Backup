package com.maven.rms.interfaces;

import java.util.List;
import java.sql.Blob;

import com.maven.rms.models.BankReconRequest;
import com.maven.rms.models.PGDetailListingRequest;
import com.maven.rms.models.BankDocRequest;
import com.maven.rms.models.BankReconDetail;

public interface IBankReconInterface {

    Integer sp_uploadDoc(BankDocRequest bankDocRequest, Blob blob, String username);

    List<Object[]> sp_getBankReconTaskList(BankReconRequest BankRequest);

    List<String> sp_getPGSettlementDateTaskList();

    List<Object[]> sp_getbanktxnlisting(BankReconDetail bankTxnListingRequest);

    List<Object[]> sp_getbankpgtxnlisting(PGDetailListingRequest pgDetailListingRequest);

    List<Object[]> sp_getrcbankdetails(BankReconDetail bankReconDetail);

    Integer sp_checkbktask(BankReconRequest BankRequest);
}
