package com.maven.rms.models.OTC;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCReturnedChequeRequest {
    private Integer i_page;
    private Integer i_size;
    private String i_che_no;
    private String i_che_bank_nm;
    private String i_rcpt_no;
    private String i_mtt_id;
    private String i_orn_no;
}
