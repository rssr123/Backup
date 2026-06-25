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
public class OTCEMVReconciliationSettlement2 {
    private BigInteger rc_emv_doc_id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date dt_balancing;
    private String file_nm;
    private String file_type;
    private Integer file_size;
    private Integer dr_count;
    private BigDecimal dr_amt;
    private Integer cr_count;
    private BigDecimal cr_amt;
    private BigDecimal total;
    private Date dt_created;
    private Date dt_modified;
    private String created_by;
    private String modified_by;
    private String status;
    private BigInteger rc_emv_id;
}
