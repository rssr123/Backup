package com.maven.rms.models;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillingItem {
    private String billingNo;
    private String entNm;
    private String entTy;
    private String entNo;
    private Date dtCreated;
    private BigDecimal final_amt;
    private String bil_status;
    private Integer total; 
}
