package com.maven.rms.models;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Entity;
import javax.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeferredIncomeTermination {
    
    @NotNull(message = "txn_type is required.")
    @Size(min = 1, max = 10, message="txn_type is required.")
    private String txn_type;

    @NotNull(message = "entity_no is required.")
    @Size(min = 1, max = 40, message="entity_no is required.")
    private String entity_no;

    @NotNull(message = "entity_type is required.")
    @Size(min = 1, max = 1, message="entity_type is required.")
    private String entity_type;

    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy HH:mm:ss")
    @NotNull(message = "dt_termination is required.")
    private Date dt_termination;

    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy HH:mm:ss")
    @NotNull(message = "dt_approval is required.")
    private Date dt_approval;
    
}
