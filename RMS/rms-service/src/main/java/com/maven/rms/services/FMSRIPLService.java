package com.maven.rms.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.sql.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import com.maven.rms.interfaces.IFMSRIPLService;
import com.maven.rms.models.FMSRILTJN;
import com.maven.rms.models.FMSRIPL;
import com.maven.rms.repositories.FMSRIPLRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FMSRIPLService implements IFMSRIPLService {
    private final FMSRIPLRepository fmsRIPLRepository;

    public FMSRIPLService(FMSRIPLRepository fmsRIPLRepository) {
        this.fmsRIPLRepository = fmsRIPLRepository;
    }

    public List<Object[]> sp_getfmsripla() {

        List<Object[]> result = new ArrayList<>();

        result = fmsRIPLRepository.sp_getfmsripla();

        if (result == null || result.isEmpty()) {
            return new ArrayList<>();
        } else {
            return result;
        }
    }

    // public BigInteger sp_insfmsripla(BigInteger i_audit_id, BigInteger i_ripl_id,
    // Date i_dt_txn, BigDecimal i_accr_amt_af, String i_action_type){

    // BigInteger result = fmsRIPLRepository.sp_insfmsripla(i_audit_id, i_ripl_id,
    // i_dt_txn, i_accr_amt_af, i_action_type);

    // return result;
    // }

    public BigInteger sp_insfmsripla(FMSRIPL fmsripl) {

        BigInteger result = fmsRIPLRepository.sp_insfmsripla(fmsripl);

        return result;
    }

    public int sp_fmsriplSch() {

        List<Object[]> listresult = new ArrayList<>();

        listresult = sp_getfmsripla();

        if (listresult != null && !listresult.isEmpty()) {
            BigInteger result;
            int counter = 0;

            for (Object[] obj : listresult) {
                FMSRIPL fmsRIPL = new FMSRIPL();
                fmsRIPL.setAudit_id((BigInteger) obj[0]);
                fmsRIPL.setRipl_id((BigInteger) obj[1]);
                fmsRIPL.setDt_txn((java.sql.Date) obj[2]);
                fmsRIPL.setAccr_amt_af((BigDecimal) obj[3]);
                fmsRIPL.setAction_type((String) obj[4]);
                // result.add(fmsDI);

                // result = sp_insfmsripla(fmsRIPL.getAudit_id(),fmsRIPL.getRipl_id(),
                // fmsRIPL.getDt_txn(),
                // fmsRIPL.getAccr_amt_af(), fmsRIPL.getAction_type());

                result = sp_insfmsripla(fmsRIPL);

                if (result.compareTo(BigInteger.valueOf(0)) > 0) {
                    counter = counter + 1;
                }
            }

            return counter;
        } else {
            return 0;
        }
    }

    @Override
    public List<FMSRILTJN> sp_getfmsjnrilt() {
        List<FMSRILTJN> result = Collections.emptyList();
        List<Object[]> objects = fmsRIPLRepository.sp_getfmsjnrilt();
        result = convertFMSJNRILT(objects);
        return result;
    }

    private List<FMSRILTJN> convertFMSJNRILT(List<Object[]> objects) {
        List<FMSRILTJN> fmsriltjns = new ArrayList<>();

        for (Object[] obj : objects) {
            FMSRILTJN Fmsriltjn = new FMSRILTJN();
            Fmsriltjn.setRilt_a_id((Integer) obj[0]);
            Fmsriltjn.setRilt_id((Integer) obj[1]);
            Fmsriltjn.setLit_amt_bf((BigDecimal) obj[2]);
            Fmsriltjn.setLit_amt_af((BigDecimal) obj[3]);
            Fmsriltjn.setDt_txn((java.sql.Date) obj[4]);
            Fmsriltjn.setStatus((String) obj[5]);

            fmsriltjns.add(Fmsriltjn);
        }
        return fmsriltjns;
    }

    @Override
    public Integer sp_insfmsjnrilt(FMSRILTJN req) {
        Integer result = 0;
        try {
            result = fmsRIPLRepository.sp_insfmsjnrilt(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Integer sp_fmsriltSch() {
        List<FMSRILTJN> Fmsriltjns = this.sp_getfmsjnrilt();
        int successCount = 0;

        for (FMSRILTJN noFmsriltjn : Fmsriltjns) {
            FMSRILTJN req = new FMSRILTJN();
            req.setI_rilt_a_id(noFmsriltjn.getRilt_a_id());
            req.setI_rilt_id(noFmsriltjn.getRilt_id());
            req.setI_lit_amt_bf(noFmsriltjn.getLit_amt_bf());
            req.setI_lit_amt_af(noFmsriltjn.getLit_amt_af());
            req.setI_dt_txn(noFmsriltjn.getDt_txn());
            req.setI_status(noFmsriltjn.getStatus());

            try {
                Integer result = this.sp_insfmsjnrilt(req);
                if (result > 0) {
                    successCount++;
                }
            } catch (Exception e) {
                log.error("Failed to insert for rilt_a_id {}: {}", req.getI_rilt_a_id(), e.getMessage());
            }
        }
        return successCount;
    }
}
