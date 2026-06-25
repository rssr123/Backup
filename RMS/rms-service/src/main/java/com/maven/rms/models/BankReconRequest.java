package com.maven.rms.models;

import java.math.BigInteger;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankReconRequest {
    
    private Integer i_page;
    private Integer i_size;
    private BigInteger i_rc_bank_id;
    private String i_task_no;
    private String i_dt_settlement;
    private String i_merchant_id;
    private String i_task_status;
    private String i_dt_uploaded;
    private String i_recon_status;

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

    // public BigInteger getI_rc_bank_id() {
    //     return i_rc_bank_id;
    // }
    // public void setI_rc_bank_id(BigInteger i_rc_bank_id) {
    //     this.i_rc_bank_id = i_rc_bank_id;
    // }

    // public String getI_task_no() {
    //     return i_task_no;
    // }
    // public void setI_task_no(String i_task_no) {
    //     this.i_task_no = i_task_no;
    // }

    // public String getI_dt_settlement() {
    //     return i_dt_settlement;
    // }
    // public void setI_dt_settlement(String i_dt_settlement) {
    //     this.i_dt_settlement = i_dt_settlement;
    // }

    // public String getI_merchant_id() {
    //     return i_merchant_id;
    // }
    // public void setI_merchant_id(String i_merchant_id) {
    //     this.i_merchant_id = i_merchant_id;
    // }

    // public String getI_task_status() {
    //     return i_task_status;
    // }
    // public void setI_task_status(String i_task_status) {
    //     this.i_task_status = i_task_status;
    // }

    // public String getI_dt_uploaded() {
    //     return i_dt_uploaded;
    // }
    // public void setI_dt_uploaded(String i_dt_uploaded) {
    //     this.i_dt_uploaded = i_dt_uploaded;
    // }

    // public String getI_recon_status() {
    //     return i_recon_status;
    // }
    // public void setI_recon_status(String i_recon_status) {
    //     this.i_recon_status = i_recon_status;
    // }

}
