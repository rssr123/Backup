package com.maven.rms.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import com.maven.rms.models.FMSRICP;
import com.maven.rms.interfaces.IFMSRICPService;

import com.maven.rms.repositories.FMSRICPRepository;

@Service
public class FMSRICPService implements IFMSRICPService {
    private final FMSRICPRepository fmsRICPRepository;

    public FMSRICPService(FMSRICPRepository fmsRICPRepository) {
        this.fmsRICPRepository = fmsRICPRepository;
    }

    public List<Object[]> sp_getfmsricpa() {

        List<Object[]> result = new ArrayList<>();

        result = fmsRICPRepository.sp_getfmsricpa();

        if (result == null || result.isEmpty()) {
            return new ArrayList<>();
        } else {
            return result;
        }
    }

    // public BigInteger sp_insfmsricpa(BigInteger i_audit_id, BigInteger i_ricp_id,
    // Date i_dt_txn, BigDecimal i_accr_amt_af, String i_action_type){

    // BigInteger result = fmsRICPRepository.sp_insfmsricpa(i_audit_id, i_ricp_id,
    // i_dt_txn, i_accr_amt_af, i_action_type);

    // return result;
    // }

    public BigInteger sp_insfmsricpa(FMSRICP fmsricp) {

        BigInteger result = fmsRICPRepository.sp_insfmsricpa(fmsricp);

        return result;
    }

    public int sp_fmsricpSch() {

        List<Object[]> listresult = new ArrayList<>();

        listresult = sp_getfmsricpa();

        if (listresult != null && !listresult.isEmpty()) {
            BigInteger result;
            int counter = 0;

            for (Object[] obj : listresult) {
                FMSRICP fmsRICP = new FMSRICP();
                fmsRICP.setAudit_id((BigInteger) obj[0]);
                fmsRICP.setRicp_id((BigInteger) obj[1]);
                fmsRICP.setDt_txn((java.sql.Date) obj[2]);
                fmsRICP.setAccr_amt_af((BigDecimal) obj[3]);
                fmsRICP.setAction_type((String) obj[4]);
                // result.add(fmsDI);

                // result = sp_insfmsricpa(fmsRICP.getAudit_id(),fmsRICP.getRicp_id(),
                // fmsRICP.getDt_txn(),
                // fmsRICP.getAccr_amt_af(), fmsRICP.getAction_type());

                result = sp_insfmsricpa(fmsRICP);

                if (result.compareTo(BigInteger.valueOf(0)) > 0) {
                    counter = counter + 1;
                }
            }

            return counter;
        } else {
            return 0;
        }
    }
}