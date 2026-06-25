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
@Entity(name = "rms_bcm")
public class BranchCodeList {
    @Id
    private Integer bcm_id;
    private String bcm_code;
}
