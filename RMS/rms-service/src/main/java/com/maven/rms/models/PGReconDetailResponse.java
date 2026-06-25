package com.maven.rms.models;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PGReconDetailResponse {
    private Integer pg_txn_settlement_no;
    private BigDecimal pg_total_txn_settlement_amt;
    private Integer pg_txn_adj_no;
    private BigDecimal pg_total_txn_adj_amt;
    private BigDecimal pg_total_txn_other;
    private Integer pg_matched_no;
    private BigDecimal pg_matched_total;
    private Integer pg_found_no;
    private BigDecimal pg_found_total;
    private Integer pg_not_found_no;
    private BigDecimal pg_not_found_total;
    private Integer pg_sam_no;
    private BigDecimal pg_sam_total;
    private Integer pg_snm_no;
    private BigDecimal pg_snm_total;
    private Integer pg_txf_no;
    private BigDecimal pg_txf_total;
    private Integer rms_txn_no;
    private Integer rms_paid_no;
    private BigDecimal rms_paid_total;
    private Integer rms_failed_no;
    private BigDecimal rms_failed_total;
    private Integer rms_rcpt_no;
    private BigDecimal rms_rcpt_total;
    private Integer rms_sam_no;
    private BigDecimal rms_sam_total;
    private Integer rms_snm_no;
    private BigDecimal rms_snm_total;
    private Integer rms_txf_no;
    private BigDecimal rms_txf_total;
    private Integer rms_cip_no;
    private BigDecimal rms_cip_total;
    private Integer rms_ncp_no;
    private BigDecimal rms_ncp_total;
    private Integer rms_nfp_no;
    private BigDecimal rms_nfp_total;
    private String task_id;
    private String recon_status;
    private String dt_statement;
    private String dt_settlement_char;
    private String remarks;
    private String task_status;
    // public Integer getPg_txn_settlement_no() {
    //     return pg_txn_settlement_no;
    // }
    // public void setPg_txn_settlement_no(Integer pg_txn_settlement_no) {
    //     this.pg_txn_settlement_no = pg_txn_settlement_no;
    // }
    // public BigDecimal getPg_total_txn_settlement_amt() {
    //     return pg_total_txn_settlement_amt;
    // }
    // public void setPg_total_txn_settlement_amt(BigDecimal pg_total_txn_settlement_amt) {
    //     this.pg_total_txn_settlement_amt = pg_total_txn_settlement_amt;
    // }
    // public Integer getPg_txn_adj_no() {
    //     return pg_txn_adj_no;
    // }
    // public void setPg_txn_adj_no(Integer pg_txn_adj_no) {
    //     this.pg_txn_adj_no = pg_txn_adj_no;
    // }
    // public BigDecimal getPg_total_txn_adj_amt() {
    //     return pg_total_txn_adj_amt;
    // }
    // public void setPg_total_txn_adj_amt(BigDecimal pg_total_txn_adj_amt) {
    //     this.pg_total_txn_adj_amt = pg_total_txn_adj_amt;
    // }
    // public BigDecimal getPg_total_txn_other() {
    //     return pg_total_txn_other;
    // }
    // public void setPg_total_txn_other(BigDecimal pg_total_txn_other) {
    //     this.pg_total_txn_other = pg_total_txn_other;
    // }
    // public Integer getPg_matched_no() {
    //     return pg_matched_no;
    // }
    // public void setPg_matched_no(Integer pg_matched_no) {
    //     this.pg_matched_no = pg_matched_no;
    // }
    // public BigDecimal getPg_matched_total() {
    //     return pg_matched_total;
    // }
    // public void setPg_matched_total(BigDecimal pg_matched_total) {
    //     this.pg_matched_total = pg_matched_total;
    // }
    // public Integer getPg_found_no() {
    //     return pg_found_no;
    // }
    // public void setPg_found_no(Integer pg_found_no) {
    //     this.pg_found_no = pg_found_no;
    // }
    // public BigDecimal getPg_found_total() {
    //     return pg_found_total;
    // }
    // public void setPg_found_total(BigDecimal pg_found_total) {
    //     this.pg_found_total = pg_found_total;
    // }
    // public Integer getPg_not_found_no() {
    //     return pg_not_found_no;
    // }
    // public void setPg_not_found_no(Integer pg_not_found_no) {
    //     this.pg_not_found_no = pg_not_found_no;
    // }
    // public BigDecimal getPg_not_found_total() {
    //     return pg_not_found_total;
    // }
    // public void setPg_not_found_total(BigDecimal pg_not_found_total) {
    //     this.pg_not_found_total = pg_not_found_total;
    // }
    // public Integer getPg_sam_no() {
    //     return pg_sam_no;
    // }
    // public void setPg_sam_no(Integer pg_sam_no) {
    //     this.pg_sam_no = pg_sam_no;
    // }
    // public BigDecimal getPg_sam_total() {
    //     return pg_sam_total;
    // }
    // public void setPg_sam_total(BigDecimal pg_sam_total) {
    //     this.pg_sam_total = pg_sam_total;
    // }
    // public Integer getPg_snm_no() {
    //     return pg_snm_no;
    // }
    // public void setPg_snm_no(Integer pg_snm_no) {
    //     this.pg_snm_no = pg_snm_no;
    // }
    // public BigDecimal getPg_snm_total() {
    //     return pg_snm_total;
    // }
    // public void setPg_snm_total(BigDecimal pg_snm_total) {
    //     this.pg_snm_total = pg_snm_total;
    // }
    // public Integer getPg_txf_no() {
    //     return pg_txf_no;
    // }
    // public void setPg_txf_no(Integer pg_txf_no) {
    //     this.pg_txf_no = pg_txf_no;
    // }
    // public BigDecimal getPg_txf_total() {
    //     return pg_txf_total;
    // }
    // public void setPg_txf_total(BigDecimal pg_txf_total) {
    //     this.pg_txf_total = pg_txf_total;
    // }
    // public Integer getRms_txn_no() {
    //     return rms_txn_no;
    // }
    // public void setRms_txn_no(Integer rms_txn_no) {
    //     this.rms_txn_no = rms_txn_no;
    // }
    // public Integer getRms_paid_no() {
    //     return rms_paid_no;
    // }
    // public void setRms_paid_no(Integer rms_paid_no) {
    //     this.rms_paid_no = rms_paid_no;
    // }
    // public BigDecimal getRms_paid_total() {
    //     return rms_paid_total;
    // }
    // public void setRms_paid_total(BigDecimal rms_paid_total) {
    //     this.rms_paid_total = rms_paid_total;
    // }
    // public Integer getRms_failed_no() {
    //     return rms_failed_no;
    // }
    // public void setRms_failed_no(Integer rms_failed_no) {
    //     this.rms_failed_no = rms_failed_no;
    // }
    // public BigDecimal getRms_failed_total() {
    //     return rms_failed_total;
    // }
    // public void setRms_failed_total(BigDecimal rms_failed_total) {
    //     this.rms_failed_total = rms_failed_total;
    // }
    // public Integer getRms_rcpt_no() {
    //     return rms_rcpt_no;
    // }
    // public void setRms_rcpt_no(Integer rms_rcpt_no) {
    //     this.rms_rcpt_no = rms_rcpt_no;
    // }
    // public BigDecimal getRms_rcpt_total() {
    //     return rms_rcpt_total;
    // }
    // public void setRms_rcpt_total(BigDecimal rms_rcpt_total) {
    //     this.rms_rcpt_total = rms_rcpt_total;
    // }
    // public Integer getRms_sam_no() {
    //     return rms_sam_no;
    // }
    // public void setRms_sam_no(Integer rms_sam_no) {
    //     this.rms_sam_no = rms_sam_no;
    // }
    // public BigDecimal getRms_sam_total() {
    //     return rms_sam_total;
    // }
    // public void setRms_sam_total(BigDecimal rms_sam_total) {
    //     this.rms_sam_total = rms_sam_total;
    // }
    // public Integer getRms_snm_no() {
    //     return rms_snm_no;
    // }
    // public void setRms_snm_no(Integer rms_snm_no) {
    //     this.rms_snm_no = rms_snm_no;
    // }
    // public BigDecimal getRms_snm_total() {
    //     return rms_snm_total;
    // }
    // public void setRms_snm_total(BigDecimal rms_snm_total) {
    //     this.rms_snm_total = rms_snm_total;
    // }
    // public Integer getRms_txf_no() {
    //     return rms_txf_no;
    // }
    // public void setRms_txf_no(Integer rms_txf_no) {
    //     this.rms_txf_no = rms_txf_no;
    // }
    // public BigDecimal getRms_txf_total() {
    //     return rms_txf_total;
    // }
    // public void setRms_txf_total(BigDecimal rms_txf_total) {
    //     this.rms_txf_total = rms_txf_total;
    // }
    // public Integer getRms_cip_no() {
    //     return rms_cip_no;
    // }
    // public void setRms_cip_no(Integer rms_cip_no) {
    //     this.rms_cip_no = rms_cip_no;
    // }
    // public BigDecimal getRms_cip_total() {
    //     return rms_cip_total;
    // }
    // public void setRms_cip_total(BigDecimal rms_cip_total) {
    //     this.rms_cip_total = rms_cip_total;
    // }
    // public Integer getRms_ncp_no() {
    //     return rms_ncp_no;
    // }
    // public void setRms_ncp_no(Integer rms_ncp_no) {
    //     this.rms_ncp_no = rms_ncp_no;
    // }
    // public BigDecimal getRms_ncp_total() {
    //     return rms_ncp_total;
    // }
    // public void setRms_ncp_total(BigDecimal rms_ncp_total) {
    //     this.rms_ncp_total = rms_ncp_total;
    // }
    // public Integer getRms_nfp_no() {
    //     return rms_nfp_no;
    // }
    // public void setRms_nfp_no(Integer rms_nfp_no) {
    //     this.rms_nfp_no = rms_nfp_no;
    // }
    // public BigDecimal getRms_nfp_total() {
    //     return rms_nfp_total;
    // }
    // public void setRms_nfp_total(BigDecimal rms_nfp_total) {
    //     this.rms_nfp_total = rms_nfp_total;
    // }
    // public String getTask_id() {
    //     return task_id;
    // }
    // public void setTask_id(String task_id) {
    //     this.task_id = task_id;
    // }
    // public String getRecon_status() {
    //     return recon_status;
    // }
    // public void setRecon_status(String recon_status) {
    //     this.recon_status = recon_status;
    // }
    // public String getDt_statement() {
    //     return dt_statement;
    // }
    // public void setDt_statement(String dt_statement) {
    //     this.dt_statement = dt_statement;
    // }
    // public String getDt_settlement_char() {
    //     return dt_settlement_char;
    // }
    // public void setDt_settlement_char(String dt_settlement_char) {
    //     this.dt_settlement_char = dt_settlement_char;
    // }
    // public String getRemarks() {
    //     return remarks;
    // }
    // public void setRemarks(String remarks) {
    //     this.remarks = remarks;
    // }
    // public String getTask_status() {
    //     return task_status;
    // }
    // public void setTask_status(String task_status) {
    //     this.task_status = task_status;
    // }
}
