package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.BillingClassRequest;

public interface IBillingClassRepository {
    List<Object[]> getBillingClass(BillingClassRequest billingClassRequest);
    Integer sp_insblcm(BillingClassRequest billingClassRequest);
    Integer sp_updateblcm(BillingClassRequest billingClassRequest);
    Integer sp_delblcm(BillingClassRequest billingClassRequest);
}
