package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.OTCReceiptCheck;
import com.maven.rms.models.RefundMyTaskListing;
import com.maven.rms.models.RefundMyTaskListingRequest;
import com.maven.rms.models.RefundSlipCheck;

public interface IRefundService {
    
    List<RefundMyTaskListing> sp_getrefundlisting(RefundMyTaskListingRequest refundRequest);
    List<RefundSlipCheck> sp_getrttwf_id_list();
    
}
