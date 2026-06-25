package com.maven.rms.models;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillingIssuanceBySBillingDocRequest {
    
    private Integer i_bil_id;
    private String i_file_nm;
    private String i_file_content;
    private String i_file_type;
    private Integer i_file_size;
    private String i_file_category;
    private String i_created_by;
    private String i_modified_by;
    private String i_status;
}
