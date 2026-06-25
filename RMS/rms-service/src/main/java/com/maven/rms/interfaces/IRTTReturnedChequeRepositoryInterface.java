package com.maven.rms.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.maven.rms.models.RTTChargeback;
import com.maven.rms.models.RTTChargebackRequest;
import com.maven.rms.models.RTTOnlinePayment;
import com.maven.rms.models.RTTOnlinePaymentResubmit;
import com.maven.rms.models.RTTReturnedChequeRequest;
import com.maven.rms.models.RttAppEmailDto;

public interface IRTTReturnedChequeRepositoryInterface {
    // RTT_WF - HARITH
    List<Object[]> sp_getrttreturnche(RTTReturnedChequeRequest rttReturnedChequeRequest);

    List<RTTOnlinePayment> findByRttStatus(String rttstatus);

    RTTOnlinePayment save(RTTOnlinePayment rttonlinePayment);

    // RTT - HARITH
    List<Object[]> sp_getrttreturncheResubmit(RTTReturnedChequeRequest rttReturnedChequeRequest);

    List<RTTOnlinePaymentResubmit> findByRttStatusResubmit(String rttstatus);

    RTTOnlinePaymentResubmit save(RTTOnlinePaymentResubmit rttonlinePaymentResubmit);

    // RTT_WF - KS
    List<Object[]> sp_getrttwfchargeback(RTTChargebackRequest rttChargebackRequest);

    List<RTTChargeback> findByChargeBackRttStatus(String rttstatus);

    RTTChargeback save(RTTChargeback rttChargeback);

    // New method to retrieve the refund application number by workflow ID
    // String findRttAppNoByWfId(@Param("wfId") int wfId);

    RttAppEmailDto findAppEmailByWfId(int wfId);

    // Finalize — returns int result code
    int sp_updrttslippdf(int rttWfId);

}