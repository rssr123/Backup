package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BranchCodeCounterRequest {
    private Integer i_page;
    private Integer i_size;
    private BigInteger i_bcc_id;
    private String i_counter_id;
    private String i_terminal_id;
    private String i_counter_ip;
    private Integer i_bcm_id;
    private String i_bcm_code;
    private Date i_dt_created;
    private Date i_dt_modified;
    private String i_created_by;
    private String i_modified_by;
    private Date i_dt_modified_fr;
    private Date i_dt_modified_to;
    private String i_status;
}
