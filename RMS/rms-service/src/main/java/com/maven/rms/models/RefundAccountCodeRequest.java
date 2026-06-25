package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefundAccountCodeRequest {
    private Integer i_page;
    private Integer i_size;
    private BigInteger i_rtt_acc_id;
    private String i_acc_cd;
    private String i_acc_desc;
    private Date i_dt_created;
    private Date i_dt_modified;
    private String i_created_by;
    private String i_modified_by;
    private Date i_dt_modified_fr;
    private Date i_dt_modified_to;
    private String i_status;
}
