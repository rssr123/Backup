package com.maven.rms.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IFMSDeferredIncomeService;
import com.maven.rms.models.FMSDeferredIncome;
import com.maven.rms.repositories.FMSDeferredIncomeRepository;

@Service
public class FMSDeferredIncomeService implements IFMSDeferredIncomeService {
    private final FMSDeferredIncomeRepository fmsDeferredIncomeRepository;

    public FMSDeferredIncomeService(FMSDeferredIncomeRepository fmsDeferredIncomeRepository) {
        this.fmsDeferredIncomeRepository = fmsDeferredIncomeRepository;
    }

    public List<Object[]> sp_getfmsdia() {

        List<Object[]> result = new ArrayList<>();

        result = fmsDeferredIncomeRepository.sp_getfmsdia();

        if (result == null || result.isEmpty()) {
            return new ArrayList<>();
        } else {
            return result;
        }
    }

    // public BigInteger sp_insfmsdia(BigInteger i_audit_id, BigInteger i_di_id,
    // Date i_dt_txn, BigDecimal i_bal_di_amt_af, BigDecimal i_unit_fee, String
    // i_action_type){

    // BigInteger result = fmsDeferredIncomeRepository.sp_insfmsdia(i_audit_id,
    // i_di_id, i_dt_txn, i_bal_di_amt_af, i_unit_fee, i_action_type);

    // return result;
    // }

    public BigInteger sp_insfmsdia(FMSDeferredIncome fmsDeferredIncome) {

        BigInteger result = fmsDeferredIncomeRepository.sp_insfmsdia(fmsDeferredIncome);

        return result;
    }

    public int sp_fmsDiSch() {

        List<Object[]> listresult = new ArrayList<>();

        listresult = sp_getfmsdia();

        if (listresult != null && !listresult.isEmpty()) {
            BigInteger result;
            int counter = 0;

            for (Object[] obj : listresult) {
                FMSDeferredIncome fmsDI = new FMSDeferredIncome();
                fmsDI.setAudit_id((BigInteger) obj[0]);
                fmsDI.setDi_id((BigInteger) obj[1]);
                fmsDI.setDt_txn((java.sql.Date) obj[2]);
                fmsDI.setBal_di_amt_af((BigDecimal) obj[3]);
                fmsDI.setUnit_fee((BigDecimal) obj[4]);
                fmsDI.setAction_type((String) obj[5]);
                // result.add(fmsDI);

                // result = sp_insfmsdia(fmsDI.getAudit_id(),fmsDI.getDi_id(),
                // fmsDI.getDt_txn(),
                // fmsDI.getBal_di_amt_af(), fmsDI.getUnit_fee(), fmsDI.getAction_type());

                result = sp_insfmsdia(fmsDI);

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
