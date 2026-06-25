package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BranchCodeUpdateRequest {
    private Integer i_bcm_id;
    private String i_code;       
    private String i_bcm_ty;      
    private String i_bcm_desc;    
    private String i_modified_by;  
    private String i_status;    
}