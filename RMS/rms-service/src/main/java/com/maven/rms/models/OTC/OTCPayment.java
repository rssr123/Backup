package com.maven.rms.models.OTC;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

@Getter
@Setter
public class OTCPayment {
    private BigInteger otc_id;
    private Integer mtt_id;
    private Integer emv_sale_id;
    private Integer otc_counter_id;
    private String payer_email;
    private String otc_pymt_mode;
    private BigDecimal cash_amt;

    // Cheque Information
    private BigDecimal che_amt;
    private Date che_date;
    private String che_bank_nm;
    private String che_payer_nm;
    private String che_no;
    private String che_bank_acct;
    private String che_ba_acct_no;

    // Money Order Information
    private BigDecimal mo_amt;
    private String mo_rm_no;
    private Date mo_date;
    private String mo_payer_nm;
    private String mo_id_no;
    private String mo_contact_no;

    // Bank Draft Information
    private BigDecimal bd_amt;
    private String bd_no;
    private Date bd_date;
    private String bd_bank_nm;

    // Other Information
    private String v_reason_cd;
    private Date dt_created;
    private Date dt_modified;
    private String created_by;
    private String modified_by; // Assuming this field was implied

    private String status;
}

