package com.maven.rms.models.OTC;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCollectionReceiptingRequest {
    private Integer i_page;
    private Integer i_size;
    private String i_coll_slip_no;
    private String i_orn_no;
    private String i_cust_nm;
    private String i_cust_phone;
    private Integer i_mtt_id;
    
}
