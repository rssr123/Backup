package com.maven.rms.models.OTC;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCCollectionReceipting {

    private Integer mtt_id;
    private String ss_cd;
    private String coll_slip_no;
    private String orn_no;
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
    private String payment_mode;
}
