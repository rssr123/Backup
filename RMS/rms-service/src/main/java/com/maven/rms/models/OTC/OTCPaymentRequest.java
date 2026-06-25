package com.maven.rms.models.OTC;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class OTCPaymentRequest {
    private Integer i_mtt_id;
    private Integer i_emv_sale;
    private String i_otc_counter_id;
    private String i_payer_email;
    private String i_otc_pymt_mode;
    private BigDecimal i_cash_amt;
    private String i_created_by;
    private String i_modified_by;


    private BigDecimal i_che_amt;
    private Date i_che_date;
    private String i_che_bank_nm;
    private String i_che_payer_nm;
    private String i_che_no;
    private String i_che_ba_acct_no;
    private String i_che_id;
    private String i_che_status;

    private BigDecimal i_bd_amt;
    private Date i_bd_date;
    private String i_bd_bank_nm;
    private String i_bd_no;

    private BigDecimal i_mo_amt;
    private String i_mo_rm_no;
    private String i_mo_payer_nm;
    private String i_mo_id_no;
    private String i_mo_contact_no;
    private Date i_mo_date;
    
}