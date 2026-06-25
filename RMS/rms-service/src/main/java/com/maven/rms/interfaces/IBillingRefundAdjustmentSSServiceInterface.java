package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.Billing.BillAdjUpdReq;
import com.maven.rms.models.Billing.BillDocReq;
import com.maven.rms.models.Billing.BillDocWithoutFile;
import com.maven.rms.models.Billing.BillGetItem;
import com.maven.rms.models.Billing.BillGetItemReq;
import com.maven.rms.models.Billing.BillLOAAGM;
import com.maven.rms.models.Billing.BillListing;
import com.maven.rms.models.Billing.BillListingRequest;
import com.maven.rms.models.Billing.BillSearch;
import com.maven.rms.models.Billing.BillSearchRequest;
import com.maven.rms.models.Billing.BillingAdjustment;
import com.maven.rms.models.Billing.BillingAdjustmentRequest;
import com.maven.rms.models.Billing.BillingHistory;

public interface IBillingRefundAdjustmentSSServiceInterface {
    List<BillSearch> sp_getbillsearch(BillSearchRequest req);
    List<BillGetItem> sp_getbillitem(BillGetItemReq req);
    List<BillDocWithoutFile> sp_getbilsuppdoc(BillDocReq fmsLedgerDocRequest);
    String sp_getbillsuppfilecontent(BillDocReq req);
    Integer sp_updbillcancel(BillDocReq updateRequest);
    List<BillListing> sp_getbillcancellisting(BillListingRequest req);
    List<BillingAdjustment> sp_getbilladjustment(BillingAdjustmentRequest req);
    Integer sp_updbilladjust(List<BillAdjUpdReq> updateRequest);
    List<BillingHistory> sp_getbillhist(BillAdjUpdReq req);
    List<BillLOAAGM> sp_getbillingloaagm(BillDocReq req);
    
}