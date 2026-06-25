package com.maven.rms.models;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MTTListingRcpt {
    private String rcpt_no;
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT)
    private Date rcpt_dt;
    private Integer rcpt_reprint;
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT)
    private Date dt_modified;

    // public String getRcpt_no() {
    //     return rcpt_no;
    // }
    
    // public void setRcpt_no(String rcpt_no) {
    //     this.rcpt_no = rcpt_no;
    // }

    // public Date getRcpt_dt() {
    //     return rcpt_dt;
    // }

    // public void setRcpt_dt(Date rcpt_dt) {
    //     this.rcpt_dt = rcpt_dt;
    // }

    // public Integer getRcpt_reprint() {
    //     return rcpt_reprint;
    // }

    // public void setRcpt_reprint(Integer rcpt_reprint) {
    //     this.rcpt_reprint = rcpt_reprint;
    // }

    // public Date getDt_modified() {
    //     return dt_modified;
    // }

    // public void setDt_modified(Date dt_modified) {
    //     this.dt_modified = dt_modified;
    // }
    
}
