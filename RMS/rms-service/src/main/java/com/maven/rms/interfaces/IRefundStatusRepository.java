package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.RefundStatusRequest;

public interface IRefundStatusRepository {
    List<Object[]> getRefundStatus(RefundStatusRequest refundStatusRequest);
}
