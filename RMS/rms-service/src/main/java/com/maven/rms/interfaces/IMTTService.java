package com.maven.rms.interfaces;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import com.informix.lang.Decimal;
import com.maven.rms.models.EmailPP;
import com.maven.rms.models.GHLPayment;
import com.maven.rms.models.GHLPaymentResponse;
import com.maven.rms.models.MTTRCPT;
import com.maven.rms.models.OnlinePaymentItem;

public interface IMTTService {

     List<OnlinePaymentItem> sp_getMTTItem(Integer mttId);

    Integer sp_updateMTT(String ornNo, String custNm, String custAddr1, String custAddr2, String custAddr3,
            String custPostCode, String custCity, String custState);

            String sp_checkLatestOrderStatus(String ornNo);
            Integer sp_checkLatestOrderStatus2(String ornNo,BigDecimal totalAmt);

    GHLPayment sp_insertPayment(Integer mttID, String pymtMethod, String serviceID, BigDecimal pymtAmt, String langCd,
            String usernameC, String usernameM);

    Integer sp_updatePayment(GHLPaymentResponse ghlResponse, String usernameM);

    Integer sp_checkPaymentRcpt(String ornNo);

    MTTRCPT sp_insertReceipt(String paymentId, String username);

    Integer sp_updateMTTRcpt(Integer mttRcptID, String verID, String ssDocRefID);
    Integer sp_checkornno(String ornno);
    Integer sp_checktxn(String ornno, String pymt_id);
    String sp_checktxnid(String ornno, String pymt_id);

//     BigInteger sp_updateMTTStatus(String ornNo);
    BigInteger sp_updateMTTStatus(String ornNo, String created_by, String modified_by);

    //scheduler
    List<EmailPP> sp_getemailpp(Integer mttID);

    
}
