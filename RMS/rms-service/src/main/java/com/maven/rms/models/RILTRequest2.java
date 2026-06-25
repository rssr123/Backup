package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RILTRequest2 {

    private Integer i_page;
    private Integer i_size;
    private Integer i_rilt_id;
    private String i_lit_no;
    private String i_lit_item_ref;
    private BigDecimal i_lit_amount;
    private String i_entity_type;
    private String i_entity_no;
    private String i_dt_due_fr;
    private String i_dt_due_to;
    private String i_dt_created_fr;
    private String i_dt_created_to;
    private String i_dt_modified_fr;
    private String i_dt_modified_to;
    private String i_status;
    
}
