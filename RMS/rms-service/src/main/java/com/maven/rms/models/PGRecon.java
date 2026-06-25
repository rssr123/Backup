package com.maven.rms.models;

import java.math.BigDecimal;

import com.informix.lang.Decimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PGRecon {

    private String i_dtSettlement;
    private String i_merchantId;
    private String i_createdBy;
    private String i_modifiedBy;
    private String i_stmtNo;
    private String i_dtStatement;
    private BigDecimal i_totalTxn;
    private BigDecimal i_totalRefund;
    private BigDecimal i_totalAdj;
    private BigDecimal i_totalOthers;
    private BigDecimal i_totalPaid;
    private BigDecimal i_balBfwd;
    private String i_remarks;
    private BigDecimal i_balCfwd;

    // public String getI_dtSettlement() {
    //     return i_dtSettlement;
    // }
    // public void setI_dtSettlement(String i_dtSettlement) {
    //     this.i_dtSettlement = i_dtSettlement;
    // }
    // public String getI_merchantId() {
    //     return i_merchantId;
    // }
    // public void setI_merchantId(String i_merchantId) {
    //     this.i_merchantId = i_merchantId;
    // }
    // public String getI_createdBy() {
    //     return i_createdBy;
    // }
    // public void setI_createdBy(String i_createdBy) {
    //     this.i_createdBy = i_createdBy;
    // }
    // public String getI_modifiedBy() {
    //     return i_modifiedBy;
    // }
    // public void setI_modifiedBy(String i_modifiedBy) {
    //     this.i_modifiedBy = i_modifiedBy;
    // }
    // public String getI_stmtNo() {
    //     return i_stmtNo;
    // }
    // public void setI_stmtNo(String i_stmtNo) {
    //     this.i_stmtNo = i_stmtNo;
    // }
    // public String getI_dtStatement() {
    //     return i_dtStatement;
    // }
    // public void setI_dtStatement(String i_dtStatement) {
    //     this.i_dtStatement = i_dtStatement;
    // }
    // public BigDecimal getI_totalTxn() {
    //     return i_totalTxn;
    // }
    // public void setI_totalTxn(BigDecimal i_totalTxn) {
    //     this.i_totalTxn = i_totalTxn;
    // }
    // public BigDecimal getI_totalRefund() {
    //     return i_totalRefund;
    // }
    // public void setI_totalRefund(BigDecimal i_totalRefund) {
    //     this.i_totalRefund = i_totalRefund;
    // }
    // public BigDecimal getI_totalAdj() {
    //     return i_totalAdj;
    // }
    // public void setI_totalAdj(BigDecimal i_totalAdj) {
    //     this.i_totalAdj = i_totalAdj;
    // }
    // public BigDecimal getI_totalOthers() {
    //     return i_totalOthers;
    // }
    // public void setI_totalOthers(BigDecimal i_totalOthers) {
    //     this.i_totalOthers = i_totalOthers;
    // }
    // public BigDecimal getI_totalPaid() {
    //     return i_totalPaid;
    // }
    // public void setI_totalPaid(BigDecimal i_totalPaid) {
    //     this.i_totalPaid = i_totalPaid;
    // }
    // public BigDecimal getI_balBfwd() {
    //     return i_balBfwd;
    // }
    // public void setI_balBfwd(BigDecimal i_balBfwd) {
    //     this.i_balBfwd = i_balBfwd;
    // }
    // public String getI_remarks() {
    //     return i_remarks;
    // }
    // public void setI_remarks(String i_remarks) {
    //     this.i_remarks = i_remarks;
    // }
    // public BigDecimal getI_balCfwd() {
    //     return i_balCfwd;
    // }
    // public void setI_balCfwd(BigDecimal i_balCfwd) {
    //     this.i_balCfwd = i_balCfwd;
    // }

}
