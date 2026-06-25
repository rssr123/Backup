package com.maven.rms.repositories;

import java.sql.Blob;
import java.util.List;

import com.maven.rms.models.Billing.BillAdjUpdReq;
import com.maven.rms.models.Billing.BillDocReq;
import com.maven.rms.models.Billing.BillGetItemReq;
import com.maven.rms.models.Billing.BillListingRequest;
import com.maven.rms.models.Billing.BillSearchRequest;
import com.maven.rms.models.Billing.BillingAdjustmentRequest;

public interface IBillingRefundAdjustmentSSRepoInterface {
    List<Object[]> sp_getbillsearch(BillSearchRequest billSearchRequest);
    List<Object[]> sp_getbillitem(BillGetItemReq billSearchRequest);
    List<Object[]> sp_getbilsuppdoc(BillDocReq billDocReq);
    Blob sp_getbillsuppfilecontent(BillDocReq billDocReq);
    Integer sp_updbillcancel(BillDocReq billDocReq);
    List<Object[]> sp_getbillcancellisting(BillListingRequest billSearchRequest);
    List<Object[]> sp_getbilladjustment(BillingAdjustmentRequest billSearchRequest);
    Integer sp_updbilladjust(List<BillAdjUpdReq> billAdjUpdReqs);
    List<Object[]> sp_getbillhist(BillAdjUpdReq billSearchRequest);
    List<Object[]> sp_getbillingloaagm(BillDocReq billSearchRequest);
}
