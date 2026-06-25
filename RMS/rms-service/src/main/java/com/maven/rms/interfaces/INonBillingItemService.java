package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.NonBillingItem;
import com.maven.rms.models.NonBillingItemRequest;

public interface INonBillingItemService {
    List<NonBillingItem> getNonBillingItem(NonBillingItemRequest nonBillingItemRequest);
}
