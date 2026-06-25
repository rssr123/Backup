package com.maven.rms.models;



import java.math.BigInteger;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReprintRcptRequest {
    private Integer i_page;
    private Integer i_size;
    private String i_rcpt_no;
    private String i_mtt_id;
    private String requestorId;
    private String profileNm;
    private int i_otc_id;
    private String i_modified_by;
    private int i_otc_rc_rp_id;
    private int i_otc_rcpt_id;
    private String i_justication;
    private BigInteger mttId;


}
