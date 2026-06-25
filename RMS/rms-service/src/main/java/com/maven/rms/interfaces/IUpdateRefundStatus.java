package com.maven.rms.interfaces;

import com.maven.rms.models.RefundStatusResult;

public interface IUpdateRefundStatus {
    RefundStatusResult updateRefundStatus(String appNo, String rejectReason);
}