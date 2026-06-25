package com.maven.rms.models;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankReconDetail {
     // Bank Recon Details
    private String file_nm;
    private Blob file_content;
    private Integer total_no_pg_txn;
    private BigDecimal total_gross_amt;
    private BigDecimal total_mdr;
    private BigDecimal total_net_amt;
 
    private Integer total_no_bk_txn;
    private Integer total_bank_txn;
    private Integer total_pg_file_txn;
    private BigDecimal total_pg_disbursed_amt;
 
    private String task_no;
    private String recon_status;
    private String task_status;
    @NotNull(message = "Remarks cannot be empty")
    private String remarks;
    private String stmt_no;
    private Date dt_settlement;
 
    //Bank txn listing
    private String txn_ref;
    private String acct_no;
    private String brn_chn;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy HH:mm:ss")
    private Date dt_posting;
    private BigDecimal credit;
    private Integer total;
 
    //Bank PG txn listing
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy HH:mm:ss")
    private Date dt_txn;
    private String txn_id;
    private String txn_type;
    private String txn_cd;
    private String found_in_pg;
    private String sub_criteria;
    private BigDecimal txn_amt;
    private BigDecimal mdr_amt;
    private BigDecimal sst_amt;
    private BigDecimal net_amt;

    //Number Bank statement
    private Integer file_size;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy HH:mm:ss")
    private Date dt_uploaded;
    private String uploaded_by;
 
    private Integer i_page;
    private Integer i_size;
    
    // public String getFile_nm() {
    //     return file_nm;
    // }
    // public void setFile_nm(String file_nm) {
    //     this.file_nm = file_nm;
    // }
    // public Blob getFile_content() {
    //     return file_content;
    // }
    // public void setFile_content(Blob file_content) {
    //     this.file_content = file_content;
    // }
    
    // public Integer getTotal_bank_txn() {
    //     return total_bank_txn;
    // }
    // public void setTotal_bank_txn(Integer total_bank_txn) {
    //     this.total_bank_txn = total_bank_txn;
    // }
    
    // public Integer getTotal_no_pg_txn() {
    //     return total_no_pg_txn;
    // }
    // public void setTotal_no_pg_txn(Integer total_no_pg_txn) {
    //     this.total_no_pg_txn = total_no_pg_txn;
    // }
    // public BigDecimal getTotal_gross_amt() {
    //     return total_gross_amt;
    // }
    // public void setTotal_gross_amt(BigDecimal total_gross_amt) {
    //     this.total_gross_amt = total_gross_amt;
    // }
    // public BigDecimal getTotal_mdr() {
    //     return total_mdr;
    // }
    // public void setTotal_mdr(BigDecimal total_mdr) {
    //     this.total_mdr = total_mdr;
    // }
    // public BigDecimal getTotal_net_amt() {
    //     return total_net_amt;
    // }
    // public void setTotal_net_amt(BigDecimal total_net_amt) {
    //     this.total_net_amt = total_net_amt;
    // }
    // public Integer getTotal_no_bk_txn() {
    //     return total_no_bk_txn;
    // }
    // public void setTotal_no_bk_txn(Integer total_no_bk_txn) {
    //     this.total_no_bk_txn = total_no_bk_txn;
    // }
    // public Integer getTotal_pg_file_txn() {
    //     return total_pg_file_txn;
    // }
    // public void setTotal_pg_file_txn(Integer total_pg_file_txn) {
    //     this.total_pg_file_txn = total_pg_file_txn;
    // }
    // public BigDecimal getTotal_pg_disbursed_amt() {
    //     return total_pg_disbursed_amt;
    // }
    // public void setTotal_pg_disbursed_amt(BigDecimal total_pg_disbursed_amt) {
    //     this.total_pg_disbursed_amt = total_pg_disbursed_amt;
    // }
    // public String getTask_no() {
    //     return task_no;
    // }
    // public void setTask_no(String task_no) {
    //     this.task_no = task_no;
    // }
    // public String getRecon_status() {
    //     return recon_status;
    // }
    // public void setRecon_status(String recon_status) {
    //     this.recon_status = recon_status;
    // }
    // public String getTask_status() {
    //     return task_status;
    // }
    // public void setTask_status(String task_status) {
    //     this.task_status = task_status;
    // }
    // public String getRemarks() {
    //     return remarks;
    // }
    // public void setRemarks(String remarks) {
    //     this.remarks = remarks;
    // }
    // public String getStmt_no() {
    //     return stmt_no;
    // }
    // public void setStmt_no(String stmt_no) {
    //     this.stmt_no = stmt_no;
    // }
    // public Date getDt_settlement() {
    //     return dt_settlement;
    // }
    // public void setDt_settlement(Date dt_settlement) {
    //     this.dt_settlement = dt_settlement;
    // }
    // public String getTxn_ref() {
    //     return txn_ref;
    // }
    // public void setTxn_ref(String txn_ref) {
    //     this.txn_ref = txn_ref;
    // }
    // public String getAcct_no() {
    //     return acct_no;
    // }
    // public void setAcct_no(String acct_no) {
    //     this.acct_no = acct_no;
    // }
    // public String getBrn_chn() {
    //     return brn_chn;
    // }
    // public void setBrn_chn(String brn_chn) {
    //     this.brn_chn = brn_chn;
    // }
    // public Date getDt_posting() {
    //     return dt_posting;
    // }
    // public void setDt_posting(Date dt_posting) {
    //     this.dt_posting = dt_posting;
    // }
    // public BigDecimal getCredit() {
    //     return credit;
    // }
    // public void setCredit(BigDecimal credit) {
    //     this.credit = credit;
    // }
    // public Integer getTotal() {
    //     return total;
    // }
    // public void setTotal(Integer total) {
    //     this.total = total;
    // }
    // public Date getDt_txn() {
    //     return dt_txn;
    // }
    // public void setDt_txn(Date dt_txn) {
    //     this.dt_txn = dt_txn;
    // }
    // public String getTxn_id() {
    //     return txn_id;
    // }
    // public void setTxn_id(String txn_id) {
    //     this.txn_id = txn_id;
    // }
    // public String getTxn_type() {
    //     return txn_type;
    // }
    // public void setTxn_type(String txn_type) {
    //     this.txn_type = txn_type;
    // }
    // public String getTxn_cd() {
    //     return txn_cd;
    // }
    // public void setTxn_cd(String txn_cd) {
    //     this.txn_cd = txn_cd;
    // }
    // public String getFound_in_pg() {
    //     return found_in_pg;
    // }
    // public void setFound_in_pg(String found_in_pg) {
    //     this.found_in_pg = found_in_pg;
    // }
    // public String getSub_criteria() {
    //     return sub_criteria;
    // }
    // public void setSub_criteria(String sub_criteria) {
    //     this.sub_criteria = sub_criteria;
    // }
    // public BigDecimal getTxn_amt() {
    //     return txn_amt;
    // }
    // public void setTxn_amt(BigDecimal txn_amt) {
    //     this.txn_amt = txn_amt;
    // }
    // public BigDecimal getMdr_amt() {
    //     return mdr_amt;
    // }
    // public void setMdr_amt(BigDecimal mdr_amt) {
    //     this.mdr_amt = mdr_amt;
    // }
    // public BigDecimal getSst_amt() {
    //     return sst_amt;
    // }
    // public void setSst_amt(BigDecimal sst_amt) {
    //     this.sst_amt = sst_amt;
    // }
    // public BigDecimal getNet_amt() {
    //     return net_amt;
    // }
    // public void setNet_amt(BigDecimal net_amt) {
    //     this.net_amt = net_amt;
    // }

    // public Integer getFile_size() {
    //     return file_size;
    // }
    // public void setFile_size(Integer file_size) {
    //     this.file_size = file_size;
    // }
    
    // public Date getDt_uploaded() {
    //     return dt_uploaded;
    // }
    // public void setDt_uploaded(Date dt_uploaded) {
    //     this.dt_uploaded = dt_uploaded;
    // }

    // public String getUploaded_by() {
    //     return uploaded_by;
    // }
    // public void setUploaded_by(String uploaded_by) {
    //     this.uploaded_by = uploaded_by;
    // }

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


}
