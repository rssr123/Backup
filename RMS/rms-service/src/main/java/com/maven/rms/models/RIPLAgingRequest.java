package com.maven.rms.models;

import java.math.BigInteger;
import java.sql.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RIPLAgingRequest {

    private Integer i_page;
    private Integer i_size;
    private BigInteger i_rpt_ripl_age_id;
    private Date i_p_dt_req;
    private Integer i_p_imp_status;
    private Integer i_p_exp_status;
    private String i_p_ent_ty;
    private String i_p_ent_nm;
    private Date i_p_dt_due_fr;
    private Date i_p_dt_due_to;
    private Date i_p_dt_rcpt_fr;
    private Date i_p_dt_rcpt_to;
    private Date i_p_dt_imp_fr;
    private Date i_p_dt_imp_to;
    private Date i_p_dt_wo_fr;
    private Date i_p_dt_wo_to;
    private String i_created_by;
    private String i_modified_by;
    private String i_status;
    private String i_p_email;
    private String i_p_file_type;
    private Integer i_p_file_size;
    private String i_p_file_nm;
    private String i_p_batch_no;
    private String i_p_fms_ref_no;
    private Integer i_email_ntfn; //not from db, added to know need insert email address or not

    
    
    // public Integer getI_page() {
    //     return i_page;
    // }
    // public void setI_page(Integer i_page) {
    //     this.i_page = i_page;
    // }
    // public Integer getI_size() {
    //     return i_size;
    // }
    // public void setI_size(Integer i_size) {
    //     this.i_size = i_size;
    // }
    // public BigInteger getI_rpt_ripl_age_id() {
    //     return i_rpt_ripl_age_id;
    // }
    // public void setI_rpt_ripl_age_id(BigInteger i_rpt_ripl_age_id) {
    //     this.i_rpt_ripl_age_id = i_rpt_ripl_age_id;
    // }
    // public Date getI_p_dt_req() {
    //     return i_p_dt_req;
    // }
    // public void setI_p_dt_req(Date i_p_dt_req) {
    //     this.i_p_dt_req = i_p_dt_req;
    // }
    // public Integer getI_p_imp_status() {
    //     return i_p_imp_status;
    // }
    // public void setI_p_imp_status(Integer i_p_imp_status) {
    //     this.i_p_imp_status = i_p_imp_status;
    // }
    // public Integer getI_p_exp_status() {
    //     return i_p_exp_status;
    // }
    // public void setI_p_exp_status(Integer i_p_exp_status) {
    //     this.i_p_exp_status = i_p_exp_status;
    // }
    // public String getI_p_ent_ty() {
    //     return i_p_ent_ty;
    // }
    // public void setI_p_ent_ty(String i_p_ent_ty) {
    //     this.i_p_ent_ty = i_p_ent_ty;
    // }
    // public String getI_p_ent_nm() {
    //     return i_p_ent_nm;
    // }
    // public void setI_p_ent_nm(String i_p_ent_nm) {
    //     this.i_p_ent_nm = i_p_ent_nm;
    // }
    // public Date getI_p_dt_due_fr() {
    //     return i_p_dt_due_fr;
    // }
    // public void setI_p_dt_due_fr(Date i_p_dt_due_fr) {
    //     this.i_p_dt_due_fr = i_p_dt_due_fr;
    // }
    // public Date getI_p_dt_due_to() {
    //     return i_p_dt_due_to;
    // }
    // public void setI_p_dt_due_to(Date i_p_dt_due_to) {
    //     this.i_p_dt_due_to = i_p_dt_due_to;
    // }
    // public Date getI_p_dt_rcpt_fr() {
    //     return i_p_dt_rcpt_fr;
    // }
    // public void setI_p_dt_rcpt_fr(Date i_p_dt_rcpt_fr) {
    //     this.i_p_dt_rcpt_fr = i_p_dt_rcpt_fr;
    // }
    // public Date getI_p_dt_rcpt_to() {
    //     return i_p_dt_rcpt_to;
    // }
    // public void setI_p_dt_rcpt_to(Date i_p_dt_rcpt_to) {
    //     this.i_p_dt_rcpt_to = i_p_dt_rcpt_to;
    // }
    // public Date getI_p_dt_imp_fr() {
    //     return i_p_dt_imp_fr;
    // }
    // public void setI_p_dt_imp_fr(Date i_p_dt_imp_fr) {
    //     this.i_p_dt_imp_fr = i_p_dt_imp_fr;
    // }
    // public Date getI_p_dt_imp_to() {
    //     return i_p_dt_imp_to;
    // }
    // public void setI_p_dt_imp_to(Date i_p_dt_imp_to) {
    //     this.i_p_dt_imp_to = i_p_dt_imp_to;
    // }
    // public Date getI_p_dt_wo_fr() {
    //     return i_p_dt_wo_fr;
    // }
    // public void setI_p_dt_wo_fr(Date i_p_dt_wo_fr) {
    //     this.i_p_dt_wo_fr = i_p_dt_wo_fr;
    // }
    // public Date getI_p_dt_wo_to() {
    //     return i_p_dt_wo_to;
    // }
    // public void setI_p_dt_wo_to(Date i_p_dt_wo_to) {
    //     this.i_p_dt_wo_to = i_p_dt_wo_to;
    // }
    // public String getI_created_by() {
    //     return i_created_by;
    // }
    // public void setI_created_by(String i_created_by) {
    //     this.i_created_by = i_created_by;
    // }
    // public String getI_modified_by() {
    //     return i_modified_by;
    // }
    // public void setI_modified_by(String i_modified_by) {
    //     this.i_modified_by = i_modified_by;
    // }
    // public String getI_status() {
    //     return i_status;
    // }
    // public void setI_status(String i_status) {
    //     this.i_status = i_status;
    // }
    // public String getI_p_email() {
    //     return i_p_email;
    // }
    // public void setI_p_email(String i_p_email) {
    //     this.i_p_email = i_p_email;
    // }
    // public String getI_p_file_type() {
    //     return i_p_file_type;
    // }
    // public void setI_p_file_type(String i_p_file_type) {
    //     this.i_p_file_type = i_p_file_type;
    // }

    
    // public Integer getI_p_file_size() {
    //     return i_p_file_size;
    // }
    // public void setI_p_file_size(Integer i_p_file_size) {
    //     this.i_p_file_size = i_p_file_size;
    // }
    // public String getI_p_file_nm() {
    //     return i_p_file_nm;
    // }
    // public void setI_p_file_nm(String i_p_file_nm) {
    //     this.i_p_file_nm = i_p_file_nm;
    // }
    // public String getI_p_batch_no() {
    //     return i_p_batch_no;
    // }
    // public void setI_p_batch_no(String i_p_batch_no) {
    //     this.i_p_batch_no = i_p_batch_no;
    // }
    // public String getI_p_fms_ref_no() {
    //     return i_p_fms_ref_no;
    // }
    // public void setI_p_fms_ref_no(String i_p_fms_ref_no) {
    //     this.i_p_fms_ref_no = i_p_fms_ref_no;
    // }
    // public Integer getI_email_ntfn() {
    //     return i_email_ntfn;
    // }
    // public void setI_email_ntfn(Integer i_email_ntfn) {
    //     this.i_email_ntfn = i_email_ntfn;
    // }

    public RIPLAgingRequest() {}

    public RIPLAgingRequest(BigInteger i_rpt_ripl_age_id,  String i_status, Integer i_p_file_size,
            String i_p_file_nm, String i_modified_by) {
        this.i_rpt_ripl_age_id = i_rpt_ripl_age_id;
        this.i_status = i_status;
        this.i_p_file_size = i_p_file_size;
        this.i_p_file_nm = i_p_file_nm;
        this.i_modified_by = i_modified_by;
    }

    


    
}
