package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
public class BranchCode {
    private Integer bcm_id;
    private String code;
    private String bcmTy;
    private String bcmDesc;
    private Date dtCreated;
    private Date dtModified;
    private String createdBy;      
    private String modifiedBy; 
    private String status;
    private Integer total;
}
