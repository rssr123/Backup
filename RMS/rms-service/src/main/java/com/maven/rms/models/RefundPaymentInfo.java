package com.maven.rms.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefundPaymentInfo {
    private OrnHeader ornHeader;
    private List<OrnBody> ornBody;

    @Getter
    @Setter
    public static class OrnHeader {
        private String ssCd;
        private String ornNo;
        private LocalDateTime ornDt;
        private String custNm;
        private String custAddr1;
        private String custAddr2;
        private String custAddr3;
        private String custPostcode;
        private String custCity;
        private String custState;
        private String custEmail;
        private String custPhone;
        private BigDecimal totalAmt;
        private String ssReturnUrl;
    }

    @Getter
    @Setter
    public static class OrnBody {
        private PaymentWithReceipt paymentWithReceipt;
        private List<PgTxnId> pgTxnIds;
        private List<OrnItemDetail> ornItemDetails;

        @Getter
        @Setter
        public static class PaymentWithReceipt {
            private String rcptNo;
            private LocalDateTime rcptDt;
        }

        @Getter
        @Setter
        public static class PgTxnId {
            private String pgTxnId;
            private Integer pgTxnStatus;
        }

        @Getter
        @Setter
        public static class OrnItemDetail {
            private String feeDetailId;
            private String itemRefNo;
            private String itemDesc;
            private Integer lineNo;
            private Integer qty;
            private BigDecimal unitFee;
            private BigDecimal grossAmt;
            private String grantCd;
            private BigDecimal discAmt;
            private BigDecimal taxPct;
            private BigDecimal taxAmt;
            private BigDecimal netAmt;
            private String entityType;
            private String entityNo;
            private String entityNm;
            private String cpNo;
            private Integer cpTier;
            private BigDecimal cpTierAmt;
            private BigDecimal cpTierDiscpct;
        }
    }
}