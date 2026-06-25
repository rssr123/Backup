package com.maven.rms.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "rms_mtt")
public class OnlinePayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="mtt_id")
    private Integer mttId;
    @Column(name="orn_no")
    private String ornNo;
    @Column(name="orn_dt")
    private LocalDateTime ornDt;
    private String cust_nm;
    private String cust_addr_1;
    private String cust_addr_2;
    private String cust_addr_3;
    private String cust_postcode;
    private String cust_city;
    private String cust_state;
    private String cust_email;
    private String cust_phone;
    private BigDecimal total_amt;
    @Column(name = "order_status")
    private String order_status;
    private String ss_return_url;
    private String ss_callback_url;
    private String ss_cd;
    private String rms_type;
    private String coll_slip_no;
    private String cust_ip;
    private String modified_by;
    private String created_by;
    private LocalDateTime dt_created;
    private LocalDateTime dt_modified;
    private LocalDateTime dt_otc_expiry;
    private LocalDateTime dt_email_expiry;
    private Integer email_flag;
    
}
