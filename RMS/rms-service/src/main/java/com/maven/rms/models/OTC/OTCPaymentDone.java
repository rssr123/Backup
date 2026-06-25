package com.maven.rms.models.OTC;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class OTCPaymentDone {
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
    private BigDecimal total_amount_paid;
    private String counter_id;
    private Date dt_created;
    private String otc_pymt_mode;
    private String branch_cd;
    private Date payment_dt;
    private String emv_terminal_id;
    private String trace_no;
    private Integer total;
    private String formattedDate;
}
