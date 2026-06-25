package com.maven.rms.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;


import com.maven.rms.interfaces.ITaxCodeService;
import com.maven.rms.models.TaxCd;
import com.maven.rms.models.TaxCdRequest;
// import com.maven.rms.repositories.IStoreProcedureRepository;
import com.maven.rms.repositories.ITaxCodeRepository;

@Service
public class TaxCodeService implements ITaxCodeService {

    private final ITaxCodeRepository taxCodeRepository;

    public TaxCodeService(ITaxCodeRepository taxCodeRepository)
    {
    this.taxCodeRepository = taxCodeRepository;
    }

    @Override
    public List<TaxCd> sp_gettaxcode_v2(TaxCdRequest taxCdRequest) {
        List<TaxCd> result = Collections.emptyList();

   
            List<Object[]> objects = taxCodeRepository.sp_gettaxcode_v2(taxCdRequest);
            result = convertTTaxCdList(objects);

        return result;
    }

    private List<TaxCd> convertTTaxCdList(List<Object[]> objects) {
        List<TaxCd> taxCdList = new ArrayList<>();

        for (Object[] obj : objects) {
            TaxCd taxCd = new TaxCd();
            taxCd.setTax_cd((String) obj[0]);
            taxCd.setTax_cd_id((BigInteger) obj[1]);
            taxCd.setTax_cd_nm_en((String) obj[2]);
            taxCd.setTax_cd_nm_bm((String) obj[3]);
            taxCd.setTax_pct((BigDecimal) obj[4]);
            taxCd.setDtModified((Date) obj[5]);
            taxCd.setModifiedBy((String) obj[6]);
            taxCd.setStatus((String) obj[7]);
            taxCd.setStatus_en((String) obj[8]);
            taxCd.setStatus_bm((String) obj[9]);
            taxCd.setTotal((Integer) obj[10]);
            taxCdList.add(taxCd);
        }

        return taxCdList;
    }

    @Override
    public Integer sp_instaxcode(TaxCdRequest insertRequest) {
        Integer result = 0;

            result = taxCodeRepository.sp_instaxcode(insertRequest);

        return result;
    }

    @Override
    public Integer sp_updtaxcode(TaxCdRequest updateRequest) {
        Integer result = 0;

            result = taxCodeRepository.sp_updtaxcode(updateRequest);

        return result;
    }

    @Override
    public Integer sp_deltaxcode(TaxCdRequest deleteRequest) {
        Integer result = 0;

            result = taxCodeRepository.sp_deltaxcode(deleteRequest);

        return result;
    }

    @Override
    public Integer sp_checktaxcdbyid(TaxCdRequest taxCodeRequest) {
        Integer result = 0;

            result = taxCodeRepository.sp_checktaxcdbyid(taxCodeRequest);

        return result;
    }

    // #endregion

}
