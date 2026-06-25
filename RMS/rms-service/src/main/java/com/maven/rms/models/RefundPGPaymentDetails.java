package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter

public class RefundPGPaymentDetails {
    private String rms_type;
    private String cust_email;
    private String pg_payment_id;
    private String pg_payment_status;
    private Date pg_payment_date;
    private BigDecimal pg_payment_amt;
    
}
