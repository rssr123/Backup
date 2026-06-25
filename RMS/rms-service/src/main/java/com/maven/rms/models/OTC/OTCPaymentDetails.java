package com.maven.rms.models.OTC;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class OTCPaymentDetails {
    private Integer otc_body_id;
    private Integer otc_id;
    private BigDecimal cash_amt;
    private BigDecimal che_amt;
    private Date cheDate;
    private String che_bank_nm;
    private String che_payer_nm;
    private String che_no;
    private String che_status;
    private BigDecimal mo_amt;
    private String mo_rm_no;
    private Date mo_date;
    private String mo_payer_nm;
    private String mo_id_no;
    private String mo_contact_no;
    private BigDecimal bd_amt;
    private String bd_no;
    private Date bd_date;
    private String bd_bank_nm;
    private Date dt_created;
    private Date dt_modified;
    private String created_by;
    private String modified_by;
    private String status;
    private String che_ba_acct_no;
    private String che_id;
    private Integer total;
}
