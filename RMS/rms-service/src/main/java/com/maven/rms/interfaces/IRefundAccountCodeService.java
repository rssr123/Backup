package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.RefundAccountCode;
import com.maven.rms.models.RefundAccountCodeRequest;

public interface IRefundAccountCodeService {
    List<RefundAccountCode> sp_getrttacc(RefundAccountCodeRequest getRequest);

    Integer sp_insrttacc(RefundAccountCodeRequest insertRequest);

    public Integer sp_updrttacc(RefundAccountCodeRequest updateRequest);

    public Integer sp_delrttacc(RefundAccountCodeRequest deleteRequest);
}
