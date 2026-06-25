package com.maven.rms.models.OTC;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCEMVReconciliationSettlement {
    private String branch_cd;
    private String file_nm;
    private String terminal_id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date date;
    private String batch_no;
    private String batch_count;
    private BigDecimal batch_amt;
    private BigInteger otc_bal_doc_id; 
}
