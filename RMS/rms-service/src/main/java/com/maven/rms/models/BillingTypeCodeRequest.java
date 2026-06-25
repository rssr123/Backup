package com.maven.rms.models;

import java.math.BigInteger;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillingTypeCodeRequest {
    

    private Integer i_page;
    private Integer i_size;
    private String i_bt_ty;
    private String i_class_id;
    private String i_ss_cd;

}
