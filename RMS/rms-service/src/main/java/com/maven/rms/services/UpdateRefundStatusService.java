package com.maven.rms.services;

import com.maven.rms.interfaces.IUpdateRefundStatus;
import com.maven.rms.models.RefundStatusResult;
import com.maven.rms.models.RefundStatusUpdateRequest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateRefundStatusService {

    private final IUpdateRefundStatus updateRefundStatusRepository;

    @Autowired
    public UpdateRefundStatusService(IUpdateRefundStatus updateRefundStatusRepository) {
        this.updateRefundStatusRepository = updateRefundStatusRepository;
    }

    public RefundStatusResult updateRefundStatus(RefundStatusUpdateRequest request) {
        return updateRefundStatusRepository.updateRefundStatus(request.getApp_no(), request.getReject_reason());
    }
}