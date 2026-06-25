package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PaymentResponse {
    private String orn_no;
    private String pymt_status;
    private String rcpt_no;
    private String rcpt_dt;
    // public String getOrn_no() {
    //     return orn_no;
    // }
    // public void setOrn_no(String orn_no) {
    //     this.orn_no = orn_no;
    // }
    // public String getPymt_status() {
    //     return pymt_status;
    // }
    // public void setPymt_status(String pymt_status) {
    //     this.pymt_status = pymt_status;
    // }
    // public String getRcpt_no() {
    //     return rcpt_no;
    // }
    // public void setRcpt_no(String rcpt_no) {
    //     this.rcpt_no = rcpt_no;
    // }
    // public String getRcpt_dt() {
    //     return rcpt_dt;
    // }
    // public void setRcpt_dt(String rcpt_dt) {
    //     this.rcpt_dt = rcpt_dt;
    // }
}
