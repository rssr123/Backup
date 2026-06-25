package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OTCReceiptCancellationPaymentInfoDetails {
    

    private BigInteger mtt_id;
    private String orn_no;
    private String coll_slip_no;
    private String payer_email;
    private String otc_pymt_mode;
    private Integer otc_body_id;
    private BigDecimal cash_amt;
    private BigDecimal che_amt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy", timezone = "Asia/Singapore")
    private Date che_date;
    private String che_bank_nm;
    private String che_payer_nm;
    private String che_no;
    private String che_status;
    private BigDecimal mo_amt;
    private String mo_rm_no;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy", timezone = "Asia/Singapore")
    private Date mo_date;
    private String mo_payer_nm;
    private String mo_id_no;
    private String mo_contact_no;
    private BigDecimal bd_amt;
    private String bd_no;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy", timezone = "Asia/Singapore")
    private Date bd_date;
    private String bd_bank_nm;
    private String che_ba_acct_no;
    private String che_id;
    private String trans_trace;
    private String batch_no;
    private String host_no;
    private String t_id;
    private BigDecimal amt;
    private Integer total;

}
