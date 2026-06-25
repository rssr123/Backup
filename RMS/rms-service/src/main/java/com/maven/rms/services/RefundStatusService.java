package com.maven.rms.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IRefundStatusService;
import com.maven.rms.models.RefundStatus;
import com.maven.rms.models.RefundStatusRequest;
import com.maven.rms.repositories.RefundStatusRepository;

@Service
public class RefundStatusService implements IRefundStatusService {

    @Autowired
    private RefundStatusRepository refundStatusRepository;

    @Override
    public List<RefundStatus> getRefundStatus(RefundStatusRequest refundStatusRequest) {
        List<Object[]> objects = refundStatusRepository.getRefundStatus(refundStatusRequest);
        return convertToRefundStatusList(objects);
    }

    private List<RefundStatus> convertToRefundStatusList(List<Object[]> objects) {
        List<RefundStatus> refundStatusList = new ArrayList<>();

        for (Object[] obj : objects) {
            RefundStatus refundStatus = new RefundStatus();

            refundStatus.setNo((Integer) obj[0]);
            refundStatus.setRefund_slip_no((String) obj[1]);
            refundStatus.setDt_process((Date) obj[2]);
            refundStatus.setDt_requested((Date) obj[3]);
            refundStatus.setRequested_by((String) obj[4]);
            refundStatus.setId_no((String) obj[5]);
            refundStatus.setRefund_ty((String) obj[6]);
            refundStatus.setSs_cd((String) obj[7]);
            refundStatus.setRtt_app_no((String) obj[8]);
            refundStatus.setOrn_no((String) obj[9]);
            refundStatus.setRcpt_no((String) obj[10]);
            refundStatus.setRms_type((String) obj[11]);
            refundStatus.setPymt_submit_dt((Date) obj[12]);
            refundStatus.setEnt_nm((String) obj[13]);
            refundStatus.setEnt_no((String) obj[14]);
            refundStatus.setBranch((String) obj[15]);
            refundStatus.setRefund_amt((BigDecimal) obj[16]);
            refundStatus.setMsg((String) obj[17]);
            refundStatus.setRtt_status((String) obj[18]);
            refundStatus.setApproved_by((String) obj[19]);
            refundStatus.setDt_approved((Date) obj[20]);
            refundStatus.setFms_p_vou_no((String) obj[21]);

            refundStatusList.add(refundStatus);
        }

        return refundStatusList;
    }
}
