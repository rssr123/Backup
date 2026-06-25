package com.maven.rms.services;

import com.maven.rms.models.RefundPaymentInfo;
import com.maven.rms.repositories.IRequestPaymentInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RequestPaymentInfoService {

    private final IRequestPaymentInfoRepository requestPaymentInfoRepository;

    @Autowired
    public RequestPaymentInfoService(IRequestPaymentInfoRepository requestPaymentInfoRepository) {
        this.requestPaymentInfoRepository = requestPaymentInfoRepository;
    }

    public List<RefundPaymentInfo> getRefundPaymentInfo(String ornNo) {
        List<Object[]> objects = requestPaymentInfoRepository.sp_getrequestPaymentInfo(ornNo);
        return convertToRefundPaymentInfoList(objects);
    }

    private List<RefundPaymentInfo> convertToRefundPaymentInfoList(List<Object[]> objects) {
        if (objects.isEmpty()) {
            return null;
        }

        List<RefundPaymentInfo> paymentInfoList = new ArrayList<>();
        RefundPaymentInfo paymentInfo = new RefundPaymentInfo();
        RefundPaymentInfo.OrnHeader ornHeader = new RefundPaymentInfo.OrnHeader();
        List<RefundPaymentInfo.OrnBody> ornBodyList = new ArrayList<>();
        RefundPaymentInfo.OrnBody currentOrnBody = null;
        List<RefundPaymentInfo.OrnBody.PgTxnId> pgTxnIds = new ArrayList<>();
        List<RefundPaymentInfo.OrnBody.OrnItemDetail> ornItemDetails = new ArrayList<>();

        for (Object[] obj : objects) {
            // MTT
            ornHeader.setSsCd((String) obj[0]);
            ornHeader.setOrnNo((String) obj[1]);
            ornHeader.setOrnDt(((java.sql.Timestamp) obj[2]).toLocalDateTime());
            ornHeader.setCustNm((String) obj[3]);
            ornHeader.setCustAddr1((String) obj[4]);
            ornHeader.setCustAddr2((String) obj[5]);
            ornHeader.setCustAddr3((String) obj[6]);
            ornHeader.setCustPostcode((String) obj[7]);
            ornHeader.setCustCity((String) obj[8]);
            ornHeader.setCustState((String) obj[9]);
            ornHeader.setCustEmail((String) obj[10]);
            ornHeader.setCustPhone((String) obj[11]);
            ornHeader.setTotalAmt((java.math.BigDecimal) obj[12]);
            ornHeader.setSsReturnUrl((String) obj[13]);

            if (currentOrnBody == null) {
                currentOrnBody = new RefundPaymentInfo.OrnBody();
                RefundPaymentInfo.OrnBody.PaymentWithReceipt paymentWithReceipt = new RefundPaymentInfo.OrnBody.PaymentWithReceipt();
                paymentWithReceipt.setRcptNo((String) obj[14]);
                paymentWithReceipt.setRcptDt(((java.sql.Timestamp) obj[15]).toLocalDateTime());
                currentOrnBody.setPaymentWithReceipt(paymentWithReceipt);
            }

            RefundPaymentInfo.OrnBody.PgTxnId pgTxnId = new RefundPaymentInfo.OrnBody.PgTxnId();
            pgTxnId.setPgTxnId((String) obj[16]);
            pgTxnId.setPgTxnStatus((Integer) obj[17]);
            pgTxnIds.add(pgTxnId);
            currentOrnBody.setPgTxnIds(pgTxnIds);

            RefundPaymentInfo.OrnBody.OrnItemDetail ornItemDetail = new RefundPaymentInfo.OrnBody.OrnItemDetail();
            ornItemDetail.setFeeDetailId((String) obj[18]);
            ornItemDetail.setItemRefNo((String) obj[19]);
            ornItemDetail.setItemDesc((String) obj[20]);
            ornItemDetail.setLineNo((Integer) obj[21]);
            ornItemDetail.setQty((Integer) obj[22]);
            ornItemDetail.setUnitFee((java.math.BigDecimal) obj[23]);
            ornItemDetail.setGrossAmt((java.math.BigDecimal) obj[24]);
            ornItemDetail.setGrantCd((String) obj[25]);
            ornItemDetail.setDiscAmt((java.math.BigDecimal) obj[26]);
            ornItemDetail.setTaxPct((java.math.BigDecimal) obj[27]);
            ornItemDetail.setTaxAmt((java.math.BigDecimal) obj[28]);
            ornItemDetail.setNetAmt((java.math.BigDecimal) obj[29]);
            ornItemDetail.setEntityType((String) obj[30]);
            ornItemDetail.setEntityNo((String) obj[31]);
            ornItemDetail.setEntityNm((String) obj[32]);
            ornItemDetail.setCpNo((String) obj[33]);
            ornItemDetail.setCpTier((Integer) obj[34]);
            ornItemDetail.setCpTierAmt((java.math.BigDecimal) obj[35]);
            ornItemDetail.setCpTierDiscpct((java.math.BigDecimal) obj[36]);
            ornItemDetails.add(ornItemDetail);
            currentOrnBody.setOrnItemDetails(ornItemDetails);
        }

        ornBodyList.add(currentOrnBody);
        paymentInfo.setOrnHeader(ornHeader);
        paymentInfo.setOrnBody(ornBodyList);
        paymentInfoList.add(paymentInfo);

        return paymentInfoList;
    }
}