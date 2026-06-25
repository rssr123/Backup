package com.maven.rms.models.OTC;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCEMVReconciliationSummary {
    private Integer branch_count;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date date_period;
    private Integer emv_settlement_count;
    private Integer emv_transaction_count;
    private BigDecimal emv_amt;
    private Integer receipts_cancelled_count;
}
