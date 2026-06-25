package com.maven.rms.models.OTC;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCReturnedCheque {
    private Integer mtt_id;
    private Integer otc_id;
    private BigDecimal che_amt;
    private Date che_date;
    private String che_bank_nm;
    private String che_no;
    private String che_status;
    private String che_ba_acct_no;
    private String che_id;
    private String che_payer_nm;
    private String counter_id;
    private String branch_cd;
    private String rcpt_no;
    private String orn_no;
    private String coll_slip_no; // to navigate to receipt screen
    private String ss_cd;
    private String cust_nm;
    private String cust_phone;
    private String cust_email;
    private String cust_addr1;
    private String cust_addr2;
    private String cust_addr3;
    private String cust_postcode;
    private String cust_city;
    private String cust_state;
    private BigDecimal total_amt;
    private String order_status;
    private Integer total;
}
