package com.maven.rms.services;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Date;

import com.maven.rms.models.RefundMyTaskListingRequest;
import com.maven.rms.models.RefundSlipCheck;
import com.maven.rms.models.OTCReceiptCheck;
import com.maven.rms.models.RefundMyTaskListing;
import com.maven.rms.repositories.RefundRepository;
import com.maven.rms.interfaces.IRefundService;

@Service
@Slf4j
public class RefundService implements IRefundService {

    private final RefundRepository refundRepository;

    public RefundService(RefundRepository refundRepository) {
        this.refundRepository = refundRepository;
    }
    
    @Override
    public List<RefundMyTaskListing> sp_getrefundlisting(RefundMyTaskListingRequest refundRequest) {
        
        List<RefundMyTaskListing> result = Collections.emptyList();

        List<Object[]> objects = refundRepository.sp_getrefundlisting(refundRequest);

        result = convertToGetRefundListing(objects);

        return result;
    }

    private List<RefundMyTaskListing> convertToGetRefundListing(List<Object[]> objects) {
        List<RefundMyTaskListing> refundList = new ArrayList<>();

        for (Object[] obj : objects)  {
            RefundMyTaskListing refund = new RefundMyTaskListing();

            refund.setRtt_wf_id((BigInteger) obj[0]);
            refund.setRefund_ty((String) obj[1]);
            refund.setRtt_app_no((String) obj[2]);
            refund.setRequested_by((String) obj[3]);
            refund.setDt_requested(((Date) obj[4]));
            refund.setDt_pick(((Date) obj[5]));
            refund.setRtt_status((String) obj[6]);
            refund.setCreated_by((String) obj[7]);
            refund.setApproved_by((String) obj[8]);
            refund.setTask_id((String) obj[9]);
            refund.setTotal((Integer) obj[10]);
            refundList.add(refund);
        }

        return refundList;
    }

    public Integer sp_getrefundassignedtaskactivetaskcount(RefundMyTaskListingRequest refundMyTaskListingRequest) {
        
        Integer result = 0;

        result = refundRepository.sp_getrefundassignedtaskactivetaskcount(refundMyTaskListingRequest);
        
        return result;
    }

    public Integer sp_getrefundcreatedtaskactivetaskcount(RefundMyTaskListingRequest refundMyTaskListingRequest) {
        
        Integer result = 0;

        result = refundRepository.sp_getrefundcreatedtaskactivetaskcount(refundMyTaskListingRequest);
        
        return result;
    }

    ///
    @Override
    public List<RefundSlipCheck> sp_getrttwf_id_list() {
        List<Object[]> objects = refundRepository.sp_getrttwf_id_list();
        return convertToRefundSlipCheckList(objects);
    }

    private List<RefundSlipCheck> convertToRefundSlipCheckList(List<Object[]> objects) {
        List<RefundSlipCheck> list = new ArrayList<>();
        for (Object[] obj : objects) {
            RefundSlipCheck item = new RefundSlipCheck();
            item.setRtt_wf_id((Integer) obj[0]);
            item.setCount((Integer) obj[1]);
            item.setSsdocref_id((String) obj[2]);
            list.add(item);
        }
        return list;
    }

}
