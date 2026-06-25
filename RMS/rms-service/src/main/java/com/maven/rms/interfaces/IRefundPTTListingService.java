package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.RefundPTTListingDetReq;
import com.maven.rms.models.RefundRcpt;
import com.maven.rms.models.RefundTHTListing;
import com.maven.rms.models.RefundWFList;
import com.maven.rms.models.RefundWFListingDetReq;
import com.maven.rms.models.RttAppEmailDto;
import com.maven.rms.models.TaxCdRequest;
import com.maven.rms.models.UserRole;
import com.maven.rms.models.OTC.OTCPayment;
import com.maven.rms.models.OTC.OTCPaymentDetails;
import com.maven.rms.models.OTC.OTCPaymentRequest;
import com.maven.rms.models.PaymentItemDetails;
import com.maven.rms.models.PaymentRequest;
import com.maven.rms.models.RefundDetailPymtItem;
import com.maven.rms.models.RefundDetails;
import com.maven.rms.models.RefundDoc;
import com.maven.rms.models.RefundHist;
import com.maven.rms.models.RefundInfo;
import com.maven.rms.models.RefundList;
import com.maven.rms.models.RefundPGPaymentDetails;
import com.maven.rms.models.RefundPTTListing;

public interface IRefundPTTListingService {
      List<RefundPTTListing> sp_getRefundPTTListing(RefundPTTListingDetReq req);

      List<RefundDetails> sp_getRefundOI_online(RefundPTTListingDetReq req);

      List<RefundDetails> sp_getRefundOI_otc(RefundPTTListingDetReq req);

      List<RefundDetailPymtItem> sp_getRefundPaymentItem(RefundPTTListingDetReq req);

      List<OTCPayment> sp_getotccrpaymentheader(OTCPaymentRequest otCollectionReceiptingRequest);

      List<OTCPaymentDetails> sp_getotccrpaymentdetails(OTCPaymentRequest otCollectionReceiptingRequest);

      List<RefundPGPaymentDetails> sp_getrefundpaymentinfo_online(RefundPTTListingDetReq req);

      List<RefundRcpt> sp_getrefundotcrcpt(RefundPTTListingDetReq req);

      List<RefundRcpt> sp_getrefundpgrcpt(RefundPTTListingDetReq req);

      List<RefundInfo> sp_getrefundinfo(RefundPTTListingDetReq req);

      List<RefundHist> sp_getrefundhist(RefundPTTListingDetReq req);

      // Integer sp_insrttwf_fa(RefundWFList insertRequest);

      Integer sp_processRefundRequest(RefundWFList req, PaymentRequest paymentRequest);

      List<RefundTHTListing> sp_getrefundtht(RefundPTTListingDetReq req);

      Integer sp_processRefundRequest_da(RefundWFList req, PaymentRequest paymentRequest);

      Long sp_insrttform_rs02(RefundWFList req);

      Integer sp_processRefundRequest_rf(RefundWFList req, PaymentRequest paymentRequest, RefundWFList refundDoc);

      Integer sp_uptrtt_dateexpiry(RefundPTTListingDetReq updateRequest);

      List<RefundList> sp_getrttwfid(RefundPTTListingDetReq refundPTTListingRequest);

      // New update method
      Integer sp_updateRefundRequest_rf(RefundWFList req, PaymentRequest paymentRequest, RefundWFList refundDoc);

      List<RefundList> sp_getRefundListing(RefundPTTListingDetReq req);

      //String sp_getRttAppNo(Integer rttWfId);

      RttAppEmailDto sp_getRttAppEmail(int rttWfId);
}
