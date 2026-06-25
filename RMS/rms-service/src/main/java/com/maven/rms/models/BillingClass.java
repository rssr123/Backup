package com.maven.rms.models;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter

public class BillingClass {
    private Integer blcm_id;
    private String classId;
    private String classDesc;
    private Date dtCreated;
    private Date dtModified;
    private String createdBy;      
    private String modifiedBy; 
    private String status;
    private Integer total;
}
