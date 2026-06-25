package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "rms_rtt")
public class Refund {
    @Id
    private BigInteger rtt_id;
    private String rtt_status;
    private LocalDateTime dt_requested;
    private Date dt_modified;
    private String created_by;
    private String modified_by;
}
