package com.maven.rms.interfaces;

import com.maven.rms.models.RefundMyTaskListingRequest;

import java.util.List;

public interface IRefundInterface {
    
    List<Object[]> sp_getrefundlisting(RefundMyTaskListingRequest refundRequest);
    List<Object[]> sp_getrttwf_id_list();
    
}
