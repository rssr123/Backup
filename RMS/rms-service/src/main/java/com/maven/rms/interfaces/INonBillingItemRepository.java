package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.NonBillingItemRequest;

public interface INonBillingItemRepository {
    List<Object[]> getNonBillingItem(NonBillingItemRequest nonBillingItemRequest);
}
