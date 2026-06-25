package com.maven.rms.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "rms_di")
public class DeferredIncome {

    @Id
    private BigInteger di_id;
    private BigInteger i_di_id;
    private String i_fee_detail_id;
    private String i_entity_no;
    private String i_entity_type;
    private Date i_dt_effective;
    private Date i_dt_expiry;
    private String i_item_ref_no;
    private String i_approval_status;
    private Date i_dt_approval;
    private String i_txn_type;
    private Date i_dt_termination;
    private Integer i_page;
    private Integer i_size;
    private String i_status;

    private Integer fee_detail_pk;

    @NotNull(message = "fee_detail_id is required.")
    @Size(min = 1, max = 10, message="fee_detail_id is required.")
    private String fee_detail_id;

    @NotNull(message = "txn_type is required.")
    @Size(min = 1, max = 10, message="txn_type is required.")
    private String txn_type;

    @NotNull(message = "entity_no is required.")
    @Size(min = 1, max = 40, message="entity_no is required.")
    private String entity_no;

    @NotNull(message = "entity_type is required.")
    @Size(min = 1, max = 1, message="entity_type is required.")
    private String entity_type;

    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy HH:mm:ss")
    @NotNull(message = "dt_effective is required.")
    private Date dt_effective;

    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy HH:mm:ss")
    @NotNull(message = "dt_expiry is required.")
    private Date dt_expiry;

    @NotNull(message = "item_ref_no is required.")
    @Size(min = 1, max = 30, message="item_ref_no is required.")
    private String item_ref_no;

    @NotNull(message = "approval_status is required.")
    @Size(min = 1, max = 10, message="approval_status is required.")
    private String approval_status;

    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy HH:mm:ss")
    @NotNull(message = "dt_approval is required.")
    private Date dt_approval;

    private Date dt_termination;

    private Integer no_of_yr;
    private BigDecimal unit_fee;
    private BigDecimal total_fee;
    private BigDecimal bal_di_amt;
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy HH:mm:ss")
    private Date next_calc_dt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy HH:mm:ss")
    private String dt_created;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy HH:mm:ss")
    private String dt_modified;
    private String created_by;
    private String created_by_nm;
    private String modified_by;
    private String modified_by_nm;
    private String status;
    private String status_nm_en;

    private String status_nm_bm;

    private Integer total;

}
