package com.maven.rms.services.OTC;

import java.io.IOException;
import java.util.List;

import com.maven.rms.models.OTC.OTCCollectionReceipting;
import com.maven.rms.models.OTC.OTCCollectionReceiptingPymtItem;
import com.maven.rms.models.OTC.OTCEMV;
import com.maven.rms.models.OTC.OTCEMVRequest;
import com.maven.rms.models.OTC.OTCHist;
import com.maven.rms.models.OTC.OTCPayment;
import com.maven.rms.models.OTC.OTCPaymentDetails;
import com.maven.rms.models.OTC.OTCPaymentDone;
import com.maven.rms.models.OTC.OTCPaymentRequest;
import com.maven.rms.models.OTC.OTCRcpt;
import com.maven.rms.models.OTC.OTCRcptRequest;
import com.maven.rms.models.OTC.OTCollectionReceiptingRequest;

public interface IOTCCollectionReceiptingServiceInterface {
    List<OTCCollectionReceipting> sp_getcollectioninfo(OTCollectionReceiptingRequest otCollectionReceiptingRequest);

    List<OTCCollectionReceiptingPymtItem> sp_otccrpymtitem(OTCollectionReceiptingRequest otCollectionReceiptingRequest);

    List<OTCCollectionReceiptingPymtItem> sp_otccrpymtitembymtt(
            OTCollectionReceiptingRequest otCollectionReceiptingRequest);

    Integer sp_insotcpymt(OTCPaymentRequest insertRequest);

    Integer sp_insotcpymtbody(List<OTCPaymentRequest> insertRequest) throws IOException;

    List<OTCHist> sp_otccrhist(OTCPaymentRequest otCollectionReceiptingRequest);

    List<OTCPaymentDetails> sp_getotccrpaymentdetails(OTCPaymentRequest otCollectionReceiptingRequest);

    List<OTCPayment> sp_getotccrpaymentheader(OTCPaymentRequest otCollectionReceiptingRequest);

    OTCRcpt sp_insotcrcpt(OTCRcptRequest insertRequest);

    OTCPaymentDone sp_getotcorder(Integer i_mtt_id);

    Integer sp_updotcrcpt(Integer i_otc_rcpt_id, String i_ver_id, String i_ssdocref_id, String file_nm);

    Integer sp_insemvsale(OTCEMVRequest insertRequest);

    List<OTCRcpt> sp_getotcrcpt(OTCPaymentRequest otCollectionReceiptingRequest);

    OTCPaymentDone sp_getotcorderemv(Integer i_mtt_id);

    OTCEMV sp_getotcemvsales(OTCPaymentRequest otCollectionReceiptingRequest);
}
