package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitBillingCust {
    
    private String cust_id;
    private String cust_nm;
    private String cust_email;
    private String cust_phone;
    private String cust_addr1;
    private String cust_addr2;
    private String cust_addr3;
    private String cust_postcode;
    private String cust_city;
    private String cust_state;
    private String cust_state_nm;
    private String ent_nm;
    private String ent_no;
    private String ent_ty;
    private String ent_ty_nm;
    private String req_name;
    private String req_email;
    private String ss_cd;
    private String ss_cd_nm;
    private String bt_ty;
    private String bt_ty_nm;
    private String billing_desc;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMAT, timezone = "Asia/Singapore")
    private Date dt_start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMAT, timezone = "Asia/Singapore")
    private Date dt_end;
    private String msg;
    private List<SubmitBillingChild> bil_list;
   

    
}
