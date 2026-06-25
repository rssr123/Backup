package com.maven.rms.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;
import java.math.BigInteger;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.maven.rms.interfaces.IDeferredIncomeService;
import com.maven.rms.models.DeferredIncome;
import com.maven.rms.models.DeferredIncomeTermination;
import com.maven.rms.repositories.DeferredIncomeRepository;

@Service
@Slf4j
public class DeferredIncomeService implements IDeferredIncomeService{

    private final DeferredIncomeRepository deferredIncomeRepository;

    public DeferredIncomeService(DeferredIncomeRepository deferredIncomeRepository) {
        this.deferredIncomeRepository = deferredIncomeRepository;
    }

    @Override
    public BigInteger sp_insdi(DeferredIncome recognitionRequest) {
        BigInteger result = null;

        result = deferredIncomeRepository.sp_insdi(recognitionRequest);

        return result;
    }

    @Override
    public BigInteger sp_insdi_tmn_log(DeferredIncomeTermination terminationRequest) {
        BigInteger result = null;

        result = deferredIncomeRepository.sp_insdi_tmn_log(terminationRequest);

        return result;
    }

    public Integer sp_upddi() 
    {
        Integer result = 0;

        result = deferredIncomeRepository.sp_upddi();
  
        return result;
    }

    @Override//DeferredIncm
    public List<DeferredIncome> sp_getdi(DeferredIncome deferredIncome) {
        List<DeferredIncome> result = Collections.emptyList();

        List<Object[]> objects = deferredIncomeRepository.sp_getdi(deferredIncome);
        result = convertTDeferredIncmList(objects);
        
        return result;
    }

    private List<DeferredIncome> convertTDeferredIncmList(List<Object[]> objects) {
        List<DeferredIncome> deferredIncmList = new ArrayList<>();

        for (Object[] obj : objects) {
            DeferredIncome deferredIncm = new DeferredIncome();
            deferredIncm.setDi_id((BigInteger) obj[0]);
            deferredIncm.setFee_detail_pk((Integer) obj[1]);
            deferredIncm.setFee_detail_id((String) obj[2]);
            deferredIncm.setEntity_type((String) obj[3]);
            deferredIncm.setEntity_no((String) obj[4]);
            deferredIncm.setDt_effective((Date) obj[5]);
            deferredIncm.setDt_expiry((Date) obj[6]);
            deferredIncm.setDt_termination((Date) obj[7]);
            deferredIncm.setItem_ref_no((String) obj[8]);
            deferredIncm.setApproval_status((String) obj[9]);
            deferredIncm.setDt_approval((Date) obj[10]);
            deferredIncm.setNo_of_yr((Integer) obj[11]);
            deferredIncm.setUnit_fee((BigDecimal) obj[12]);
            deferredIncm.setTotal_fee((BigDecimal) obj[13]);
            deferredIncm.setBal_di_amt((BigDecimal) obj[14]);
            deferredIncm.setNext_calc_dt((Date) obj[15]);
            deferredIncm.setDt_created((String) obj[16]);
            deferredIncm.setDt_modified((String) obj[17]);
            deferredIncm.setCreated_by((String) obj[18]);
            deferredIncm.setCreated_by_nm((String) obj[19]);
            deferredIncm.setModified_by((String) obj[20]);
            deferredIncm.setModified_by_nm((String) obj[21]);
            deferredIncm.setStatus((String) obj[22]);
            deferredIncm.setStatus_nm_en((String) obj[23]);
            deferredIncm.setStatus_nm_bm((String) obj[24]);
            deferredIncm.setTxn_type((String) obj[25]);
            deferredIncm.setTotal((Integer) obj[26]);
            deferredIncmList.add(deferredIncm);
        }

        return deferredIncmList;
    }

}
