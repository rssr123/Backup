package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.Refund;
import com.maven.rms.models.RefundWf;
import com.maven.rms.models.RefundWfHist;
import com.maven.rms.models.RefundAccountCodeRequest;

public interface IRefundAccountCodeInterface {
    List<Object[]> sp_getrttacc(RefundAccountCodeRequest getRequest);

    Integer sp_insrttacc(RefundAccountCodeRequest insertRequest);

    Integer sp_updrttacc(RefundAccountCodeRequest updateRequest);

    Integer sp_delrttacc(RefundAccountCodeRequest deleteRequest);

    //scheduler
    List<Refund> findByRttStatus();
    List<RefundWf> findByPickUpByWf();
    List<RefundWfHist> findByPickUpByWfHist();
    Refund saveRefund(Refund refund);
    RefundWf saveRefundWf(RefundWf refundWf);
    RefundWfHist saveRefundWfHist(RefundWfHist refundWfHist);


}
