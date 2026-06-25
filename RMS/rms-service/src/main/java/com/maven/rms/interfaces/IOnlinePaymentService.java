package com.maven.rms.interfaces;

import java.math.BigDecimal;
import java.util.List;

import com.maven.rms.models.CheckAccrual;
import com.maven.rms.models.GHLPaymentResponse;
import com.maven.rms.models.GHLRequest;
import com.maven.rms.models.OnlinePayment;
import com.maven.rms.models.OnlinePaymentItem;
import com.maven.rms.models.PaymentRequest;

public interface IOnlinePaymentService {

    Integer sp_insertPaymentMTT(PaymentRequest paymentRequest, String username, String custIP);

    OnlinePayment sp_getMTT(String ornNo);

    OnlinePayment sp_getMTT(Integer mttid);

    List<OnlinePaymentItem> sp_getMTTItem(Integer mttID);

    //String sp_insertPaymentMTTItem(PaymentItemDetails itemDetails,String Username);
    Integer sp_insghlresp(GHLRequest insertRequest);

    Integer sp_checkAccrual(CheckAccrual checkAccrual);

    Integer sp_updghlresp(GHLRequest insertRequest);

    List<GHLPaymentResponse> sp_getghlresp();

    BigDecimal sp_getrmsfee(OnlinePaymentItem request);
    String sp_getmttornno(Integer mtt_id);
    Integer sp_checkemailsent(String orn_no);
}
