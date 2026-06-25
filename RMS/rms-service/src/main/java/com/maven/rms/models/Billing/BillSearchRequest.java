package com.maven.rms.models.Billing;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillSearchRequest {
    private Integer i_page;
    private Integer i_size;
    private String i_cust_id;
    private String i_bil_no;
    private String i_orn_no;
    private String i_ent_ty;
    private String i_ent_no;
    
}
