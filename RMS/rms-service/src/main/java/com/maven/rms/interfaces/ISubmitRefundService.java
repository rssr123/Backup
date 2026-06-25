package com.maven.rms.interfaces;

import com.maven.rms.models.PaymentRequest;
import com.maven.rms.models.RefundRequest;

public interface ISubmitRefundService {

    String sp_insrefund_ss(RefundRequest refundRequest);

    Integer sp_insertRttItem(RefundRequest refundRequest, PaymentRequest paymentRequest);
}
