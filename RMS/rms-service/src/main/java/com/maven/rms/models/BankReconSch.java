package com.maven.rms.models;

import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankReconSch {
    private BigInteger rc_bank_id;
    private BigInteger rc_bankdoc_id;
    private String acct_no;
    private String acct_type;
    private String acct_nm;
    private Date dt_fr;
    private Date dt_to;
    private Integer total_debit;
    private Integer total_credit;
    private String begin_bal;
    private String end_bal;
    private String dt_txn;
    private String dt_posting;
    private String txn_desc;
    private String txn_ref;
    private String debit;
    private String credit;
    private String source_cd;
    private String teller_id;
    private String brn_chn;
    private String txn_cd;
    private String end_bal2;
    private String virtual_acct;
    private String txn_desc2;
    private String txn_desc3;
    private String txn_desc4;
    private Date dt_expiry;
 
    private String file_nm;
    private Blob file_content;
    private String file_type;
    private Integer file_size_kb;
    
    private String status;
    // public BigInteger getRc_bank_id() {
    //     return rc_bank_id;
    // }
    // public void setRc_bank_id(BigInteger rc_bank_id) {
    //     this.rc_bank_id = rc_bank_id;
    // }
    // public BigInteger getRc_bankdoc_id() {
    //     return rc_bankdoc_id;
    // }
    // public void setRc_bankdoc_id(BigInteger rc_bankdoc_id) {
    //     this.rc_bankdoc_id = rc_bankdoc_id;
    // }
    // public String getAcct_no() {
    //     return acct_no;
    // }
    // public void setAcct_no(String acct_no) {
    //     this.acct_no = acct_no;
    // }
    // public String getAcct_type() {
    //     return acct_type;
    // }
    // public void setAcct_type(String acct_type) {
    //     this.acct_type = acct_type;
    // }
    // public String getAcct_nm() {
    //     return acct_nm;
    // }
    // public void setAcct_nm(String acct_nm) {
    //     this.acct_nm = acct_nm;
    // }
    // public Date getDt_fr() {
    //     return dt_fr;
    // }
    // public void setDt_fr(Date dt_fr) {
    //     this.dt_fr = dt_fr;
    // }
    // public Date getDt_to() {
    //     return dt_to;
    // }
    // public void setDt_to(Date dt_to) {
    //     this.dt_to = dt_to;
    // }
    // public Integer getTotal_debit() {
    //     return total_debit;
    // }
    // public void setTotal_debit(Integer total_debit) {
    //     this.total_debit = total_debit;
    // }
    // public Integer getTotal_credit() {
    //     return total_credit;
    // }
    // public void setTotal_credit(Integer total_credit) {
    //     this.total_credit = total_credit;
    // }
    // public String getBegin_bal() {
    //     return begin_bal;
    // }
    // public void setBegin_bal(String begin_bal) {
    //     this.begin_bal = begin_bal;
    // }
    // public String getEnd_bal() {
    //     return end_bal;
    // }
    // public void setEnd_bal(String end_bal) {
    //     this.end_bal = end_bal;
    // }
    // public String getDt_txn() {
    //     return dt_txn;
    // }
    // public void setDt_txn(String dt_txn) {
    //     this.dt_txn = dt_txn;
    // }
    // public String getDt_posting() {
    //     return dt_posting;
    // }
    // public void setDt_posting(String dt_posting) {
    //     this.dt_posting = dt_posting;
    // }
    // public String getTxn_desc() {
    //     return txn_desc;
    // }
    // public void setTxn_desc(String txn_desc) {
    //     this.txn_desc = txn_desc;
    // }
    // public String getTxn_ref() {
    //     return txn_ref;
    // }
    // public void setTxn_ref(String txn_ref) {
    //     this.txn_ref = txn_ref;
    // }
    // public String getDebit() {
    //     return debit;
    // }
    // public void setDebit(String debit) {
    //     this.debit = debit;
    // }
    // public String getCredit() {
    //     return credit;
    // }
    // public void setCredit(String credit) {
    //     this.credit = credit;
    // }
    // public String getSource_cd() {
    //     return source_cd;
    // }
    // public void setSource_cd(String source_cd) {
    //     this.source_cd = source_cd;
    // }
    // public String getTeller_id() {
    //     return teller_id;
    // }
    // public void setTeller_id(String teller_id) {
    //     this.teller_id = teller_id;
    // }
    // public String getBrn_chn() {
    //     return brn_chn;
    // }
    // public void setBrn_chn(String brn_chn) {
    //     this.brn_chn = brn_chn;
    // }
    // public String getTxn_cd() {
    //     return txn_cd;
    // }
    // public void setTxn_cd(String txn_cd) {
    //     this.txn_cd = txn_cd;
    // }
    // public String getEnd_bal2() {
    //     return end_bal2;
    // }
    // public void setEnd_bal2(String end_bal2) {
    //     this.end_bal2 = end_bal2;
    // }
    // public String getVirtual_acct() {
    //     return virtual_acct;
    // }
    // public void setVirtual_acct(String virtual_acct) {
    //     this.virtual_acct = virtual_acct;
    // }
    // public String getTxn_desc2() {
    //     return txn_desc2;
    // }
    // public void setTxn_desc2(String txn_desc2) {
    //     this.txn_desc2 = txn_desc2;
    // }
    // public String getTxn_desc3() {
    //     return txn_desc3;
    // }
    // public void setTxn_desc3(String txn_desc3) {
    //     this.txn_desc3 = txn_desc3;
    // }
    // public String getTxn_desc4() {
    //     return txn_desc4;
    // }
    // public void setTxn_desc4(String txn_desc4) {
    //     this.txn_desc4 = txn_desc4;
    // }
    // public Date getDt_expiry() {
    //     return dt_expiry;
    // }
    // public void setDt_expiry(Date dt_expiry) {
    //     this.dt_expiry = dt_expiry;
    // }
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
    // public String getFile_type() {
    //     return file_type;
    // }
    // public void setFile_type(String file_type) {
    //     this.file_type = file_type;
    // }
    // public Integer getFile_size_kb() {
    //     return file_size_kb;
    // }
    // public void setFile_size_kb(Integer file_size_kb) {
    //     this.file_size_kb = file_size_kb;
    // }

    
}
