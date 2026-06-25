package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.BillingItemRequest;

public interface IBillingItemRepository {
    List<Object[]> getBillingItem(BillingItemRequest billingItemRequest);
}
