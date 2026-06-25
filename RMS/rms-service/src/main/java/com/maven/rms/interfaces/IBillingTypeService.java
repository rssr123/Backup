package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.BillingClass;
import com.maven.rms.models.BillingType;
import com.maven.rms.models.BillingTypeRequest;

public interface IBillingTypeService {
    List<BillingType> getBillingType(BillingTypeRequest billingTypeRequest);
    Integer sp_insbltc(BillingTypeRequest billingTypeRequest);
    Integer sp_updatebltc(BillingTypeRequest billingTypeRequest);
    Integer sp_delbltc(BillingTypeRequest billingTypeRequest);
    List<BillingClass> sp_getnblcm();
    Integer sp_insbltcitem(List<BillingTypeRequest> billingTypeRequest);
    Integer sp_updbltcitem(List<BillingTypeRequest> billingTypeRequest);
}
