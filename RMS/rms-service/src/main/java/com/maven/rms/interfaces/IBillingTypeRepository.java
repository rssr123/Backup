package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.BillingTypeRequest;

public interface IBillingTypeRepository {
    List<Object[]> getBillingType(BillingTypeRequest billingTypeRequest);
    Integer sp_insbltc(BillingTypeRequest billingTypeRequest);
    Integer sp_updatebltc(BillingTypeRequest billingTypeRequest);
    Integer sp_delbltc(BillingTypeRequest billingTypeRequest);
    List<Object[]> sp_getnblcm();
    Integer sp_insbltcitem(List<BillingTypeRequest> billingTypeRequest);
    Integer sp_updbltcitem(List<BillingTypeRequest> billingTypeRequest);
}
