package com.maven.rms.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IRefundAccountCodeService;
import com.maven.rms.models.RefundAccountCode;
import com.maven.rms.models.RefundAccountCodeRequest;
import com.maven.rms.repositories.RefundAccountCodeRepository;

@Service
public class RefundAccountCodeService implements IRefundAccountCodeService {
    private final RefundAccountCodeRepository refundAccountCodeRepository;

    public RefundAccountCodeService(RefundAccountCodeRepository refundAccountCodeRepository)
    {
        this.refundAccountCodeRepository = refundAccountCodeRepository;
    }

    @Override
    public List<RefundAccountCode> sp_getrttacc(RefundAccountCodeRequest getRequest) {
        List<RefundAccountCode> result = Collections.emptyList();
   
            List<Object[]> objects = refundAccountCodeRepository.sp_getrttacc(getRequest);
            result = convertRefundAccountCodeList(objects);

        return result;
    }

    private List<RefundAccountCode> convertRefundAccountCodeList(List<Object[]> objects) {
        List<RefundAccountCode> refundAccountCodeList = new ArrayList<>();

        for (Object[] obj : objects) {
            RefundAccountCode refundAccountCode = new RefundAccountCode();
            refundAccountCode.setRtt_acc_id((BigInteger) obj[0]);
            refundAccountCode.setAcc_cd((String) obj[1]);
            refundAccountCode.setAcc_desc((String) obj[2]);
            refundAccountCode.setDt_created((Date) obj[3]);
            refundAccountCode.setDt_modified((Date) obj[4]);
            refundAccountCode.setCreated_by((String) obj[5]);
            refundAccountCode.setModified_by((String) obj[6]);
            refundAccountCode.setStatus((String) obj[7]);
            refundAccountCode.setStatus_en((String) obj[8]);
            refundAccountCode.setStatus_bm((String) obj[9]);
            refundAccountCode.setTotal((Integer) obj[10]);
            refundAccountCodeList.add(refundAccountCode);
        }

        return refundAccountCodeList;
    }

    @Override
    public Integer sp_insrttacc(RefundAccountCodeRequest insertRequest) {
        Integer result = 0;

            result = refundAccountCodeRepository.sp_insrttacc(insertRequest);

        return result;
    }

    @Override
    public Integer sp_updrttacc(RefundAccountCodeRequest updateRequest) {
        Integer result = 0;

            result = refundAccountCodeRepository.sp_updrttacc(updateRequest);

        return result;
    }

    @Override
    public Integer sp_delrttacc(RefundAccountCodeRequest deleteRequest) {
        Integer result = 0;

            result = refundAccountCodeRepository.sp_delrttacc(deleteRequest);

        return result;
    }
}
