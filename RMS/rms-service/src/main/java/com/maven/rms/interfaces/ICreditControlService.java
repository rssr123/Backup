package com.maven.rms.interfaces;

import com.maven.rms.models.CreditControlPaidInvoiceRequest;
import com.maven.rms.models.CreditControlReminderRequest;

public interface ICreditControlService {

    Integer sp_insccrmd(CreditControlReminderRequest ccRmdRequest);

    Integer sp_updcccasestatus(CreditControlPaidInvoiceRequest ccPaidInvRequest);

} 