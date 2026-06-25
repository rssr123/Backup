package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.criteria.CriteriaBuilder.In;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OTCReceiptCancellationListing {
    

    private Integer mtt_id;
    private String rcpt_no;
    private String orn_no;
    private String cust_nm;
    private BigInteger otc_id;
    private String otc_pymt_mode;
    private BigDecimal total_amt;
    private Integer otc_counter_id;
    private String counter_id;
    private String branch_cd;
    private String nm_en;
    private Integer total;
   
}
