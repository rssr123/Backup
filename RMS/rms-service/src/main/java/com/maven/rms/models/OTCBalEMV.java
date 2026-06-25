package com.maven.rms.models;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCBalEMV {
    private BigInteger docID;
    private String fileNm;        // nvarchar(255) as file_nm
    private String terminalId;    // nvarchar(50) as terminal_id
    private Date dtSettlement;    // date as dt_settlement
    private String batchNo;       // nvarchar(6) as batch_no
    private String batchCount;    // nvarchar(3) as batch_count
    private String batchAmt;      // nvarchar(12) as batch_amt
    private BigDecimal total;            // int as total
}
