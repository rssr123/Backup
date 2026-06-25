package com.maven.rms.models;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JPAScheduler {
    // sp_getfpascheduler
    private int row_number;
    private String job_name;
    private Date last_attempt_date;
    private String resp_status;
    private Date next_attempt_time;
    private int total;

    // public int getRow_number() {
    //     return row_number;
    // }
    // public void setRow_number(int row_number) {
    //     this.row_number = row_number;
    // }
    // public int getTotal() {
    //     return total;
    // }
    // public void setTotal(int total) {
    //     this.total = total;
    // }
    // public String getJob_name() {
    //     return job_name;
    // }
    // public void setJob_name(String job_name) {
    //     this.job_name = job_name;
    // }
    // public Date getLast_attempt_date() {
    //     return last_attempt_date;
    // }
    // public void setLast_attempt_date(Date last_attempt_date) {
    //     this.last_attempt_date = last_attempt_date;
    // }
    // public String getResp_status() {
    //     return resp_status;
    // }
    // public void setResp_status(String resp_status) {
    //     this.resp_status = resp_status;
    // }
    // public Date getNext_attempt_time() {
    //     return next_attempt_time;
    // }
    // public void setNext_attempt_time(Date next_attempt_time) {
    //     this.next_attempt_time = next_attempt_time;
    // }

    
}
