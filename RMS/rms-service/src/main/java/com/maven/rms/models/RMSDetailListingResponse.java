package com.maven.rms.models;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RMSDetailListingResponse {

    private String dt_txn;
    private String txn_id;
    private String cust_nm;
    private String orn_no;
    private String found_in_pg;
    private String sub_criteria;
    private BigDecimal txn_amt;
    private String order_status;
    private Integer total;
    
    // public String getDt_txn() {
    //     return dt_txn;
    // }
    // public void setDt_txn(String dt_txn) {
    //     this.dt_txn = dt_txn;
    // }
    // public String getTxn_id() {
    //     return txn_id;
    // }
    // public void setTxn_id(String txn_id) {
    //     this.txn_id = txn_id;
    // }
    // public String getCust_nm() {
    //     return cust_nm;
    // }
    // public void setCust_nm(String cust_nm) {
    //     this.cust_nm = cust_nm;
    // }
    // public String getOrn_no() {
    //     return orn_no;
    // }
    // public void setOrn_no(String orn_no) {
    //     this.orn_no = orn_no;
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
    // public String getOrder_status() {
    //     return order_status;
    // }
    // public void setOrder_status(String order_status) {
    //     this.order_status = order_status;
    // }
    // public Integer getTotal() {
    //     return total;
    // }
    // public void setTotal(Integer total) {
    //     this.total = total;
    // }
    
}
