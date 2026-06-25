package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Setter
@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
public class RefundPTTListingDetReq {
    private Integer i_page;
    private Integer i_size;
    private String i_orn_no;

    private LocalDate i_orn_dt_fr;
    private LocalDate i_orn_dt_to;
    private String i_order_status;
    private String i_rcpt_no;
    private String i_ent_nm;
    private String i_txn_id;
    private String i_mtt_id;
    private String i_refund_slip_no;
    private String i_created_by;
    private String i_rtt_app_no;
    private String i_modified_by;
    private String i_refund_ty;
    private LocalDate i_dt_created_fr;
    private LocalDate i_dt_created_to;
    private String i_rms_type;
    private String i_platform_call;
}
