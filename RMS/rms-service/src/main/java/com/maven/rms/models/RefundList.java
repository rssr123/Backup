package com.maven.rms.models;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RefundList {
    
   private Integer rtt_wf_id;
   private String refund_ty;
   private String orn_no;
   private String refund_cd;
   private String rtt_status;
   private String rtt_app_no;
   private Date   dt_created;
   private String txn_id;
   private BigDecimal refund_amt;
   private String rcpt_no;
   private Integer total;
   private Integer mtt_id;
   private String rms_type;
}
