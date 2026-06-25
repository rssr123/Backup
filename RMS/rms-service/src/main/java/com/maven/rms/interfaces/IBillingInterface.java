package com.maven.rms.interfaces;

import com.maven.rms.models.BillingMyTaskListingRequest;

import java.util.List;

public interface IBillingInterface {
    
    List<Object[]> sp_getbillinglisting(BillingMyTaskListingRequest billingRequest);
}
