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
@Entity(name = "rms_rtt_acc")
public class RefundAccountCode {
    @Id
    private BigInteger rtt_acc_id;
    private String acc_cd;
    private String acc_desc;
    private Date dt_created;
    private Date dt_modified;
    private String created_by;
    private String modified_by;
    private String status;
    private String status_en;
    private String status_bm;
    private Integer total;
}
