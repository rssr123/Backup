package com.maven.rms.models.OTC;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NonBillingListingRequest {
    private Integer i_page;
    private Integer i_size;
    private String i_ent_nm;
    private String i_ent_no;
    private String i_cust_id;
    private String i_bil_status;
    private String i_non_bil_no;
    private String i_che_id;
    private String i_che_no;

    private Integer i_non_bil_id;
    private Integer i_non_bil_doc_id;
    
}
