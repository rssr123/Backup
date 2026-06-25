package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.BillingItem;
import com.maven.rms.models.BillingItemRequest;


public interface IBillingItemService {
    List<BillingItem> getBillingItem(BillingItemRequest billingItemRequest);
}
