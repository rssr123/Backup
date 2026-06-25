package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.RefundStatusRequest;
import com.maven.rms.models.RefundStatus;

public interface IRefundStatusService {
    List<RefundStatus> getRefundStatus(RefundStatusRequest refundStatusRequest);
}
