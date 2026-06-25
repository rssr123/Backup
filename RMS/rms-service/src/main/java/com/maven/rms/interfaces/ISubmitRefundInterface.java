package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.RefundPTTListingDetReq;
import com.maven.rms.models.RefundRequest;


public interface ISubmitRefundInterface {

    
    String sp_insrefund_ss(RefundRequest refundRequest);

}
