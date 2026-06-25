package com.maven.rms.interfaces;

import java.sql.Blob;
import java.util.List;

import com.maven.rms.models.PaymentItemDetails;
import com.maven.rms.models.Refund;
import com.maven.rms.models.RefundDoc;
import com.maven.rms.models.RefundList;
import com.maven.rms.models.RefundPTTListingDetReq;
import com.maven.rms.models.RefundWFList;
import com.maven.rms.models.RefundWFListingDetReq;
import com.maven.rms.models.TaxCdRequest;

public interface IRefundPTTListingInterface {
    List<Object[]> sp_getRefundPTTListing(RefundPTTListingDetReq req);

    List<Object[]> sp_getRefundOI_online(RefundPTTListingDetReq req);

    List<Object[]> sp_getRefundOI_otc(RefundPTTListingDetReq req);

    List<Object[]> sp_getRefundPaymentItem(RefundPTTListingDetReq req);

    List<Object[]> sp_getrefundpaymentinfo_online(RefundPTTListingDetReq req);

    List<Object[]> sp_getrefundotcrcpt(RefundPTTListingDetReq req);

    List<Object[]> sp_getrefundpgrcpt(RefundPTTListingDetReq req);

    List<Object[]> sp_getrefundinfo(RefundPTTListingDetReq req);

    List<Object[]> sp_getrefundhist(RefundPTTListingDetReq req);

    Integer sp_insrttwf(RefundWFList insertRequest);

    List<Object[]> sp_getrefundtht(RefundPTTListingDetReq req);

    Integer sp_insrttwf_da(RefundWFList insertRequest);

    Long sp_insrttform_rs02(RefundWFList insertRequest);

    Integer sp_insrttwf_rf(RefundWFList insertRequest);

    Integer sp_insertRefundDoc(RefundDoc refundDoc, Blob blob, Integer rtt_wf_id, String createdBy, String modifiedBy);

    Integer sp_uptrtt_dateexpiry(RefundPTTListingDetReq updateRequest);

    List<Object[]> sp_getrttwfid(RefundPTTListingDetReq req);

       // New methods for update:
    Integer sp_updrttwf_rf(RefundWFList updateRequest);
    Integer sp_updateRefundItem(PaymentItemDetails item, Integer rttWfId, String modifiedBy);


    List<Object[]> sp_getRefundListing(RefundPTTListingDetReq req);
}
