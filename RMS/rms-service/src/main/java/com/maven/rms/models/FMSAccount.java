package com.maven.rms.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import com.maven.rms.config.Constants;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "rms_fms_acct")
public class FMSAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fms_acct_id;
    private String acct_nm;
    private String acct_type;
    private String acct_cd;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date dt_created;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date dt_modified;
    private String created_by;
    private String modified_by;
    private String status;
    private Integer total;

}
