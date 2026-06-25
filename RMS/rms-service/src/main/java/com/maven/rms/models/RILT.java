package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "rms_rilt")
public class RILT {
    @Id
    private BigInteger rilt_id;
    private String lit_no;
    private String lit_item_ref;
    private BigDecimal lit_amount;
    private String entity_type;
    private String entity_no;
    private Date dt_due;
    private Date dt_created;
    private Date dt_modified;
    private String created_by;
    private String modified_by;
    private String status;
    private Integer total;
}
