package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "rms_bcc")
public class BranchCodeCounter {
    @Id
    private BigInteger bcc_id;
    private String counter_id;
    private String terminal_id;
    private String counter_ip;
    private Integer bcm_id;
    private String bcm_code;
    private Date dt_created;
    private Date dt_modified;
    private String created_by;
    private String modified_by;
    private String status;
    private String status_en;
    private String status_bm;
    private Integer total;
}
