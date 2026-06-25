package com.maven.rms.models.OTC;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCEMVPaymentReq {

    private String amount;
    private String additionalData;
    private String command;
    private String emvResponse;

    private Integer i_mtt_id;
    private Integer i_emv_sale;
    private String i_otc_counter_id;
    private String i_payer_email;
    private String i_otc_pymt_mode;
    private String i_created_by;
    private String i_modified_by;

    
}
