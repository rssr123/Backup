package com.maven.rms.interfaces;

import com.maven.rms.models.BillingMyTaskListing;
import com.maven.rms.models.BillingMyTaskListingRequest;

import java.util.List;

public interface IBillingService {
    
    List<BillingMyTaskListing> sp_getbillinglisting(BillingMyTaskListingRequest billingRequest);
    
}
