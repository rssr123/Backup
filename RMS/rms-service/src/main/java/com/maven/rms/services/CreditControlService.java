package com.maven.rms.services;

import org.springframework.stereotype.Service;
import com.maven.rms.interfaces.ICreditControlService;
import com.maven.rms.models.CreditControlPaidInvoiceRequest;
import com.maven.rms.models.CreditControlReminderRequest;
import com.maven.rms.repositories.CreditControlRepository;

@Service
public class CreditControlService implements ICreditControlService {

    private final CreditControlRepository ccRepo;

    public CreditControlService(CreditControlRepository ccRepo) {
        this.ccRepo = ccRepo;

    }

    @Override
    public Integer sp_insccrmd(CreditControlReminderRequest ccRmdRequest) {

        Integer result = 0;

        result = ccRepo.sp_insccrmd(ccRmdRequest);

        return result;
    }

    @Override
    public Integer sp_updcccasestatus(CreditControlPaidInvoiceRequest ccPaidInvRequest) {

        Integer result = 0;

        result = ccRepo.sp_updcccasestatus(ccPaidInvRequest);

        return result;
    }
}