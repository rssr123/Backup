package com.maven.rms.models;

import java.math.BigInteger;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MFTWFHistoryRequest {
    


    private Integer i_page;
    private Integer i_size;
    private BigInteger i_wf_id;
    private String i_status;
    private String i_task_id;
    
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
    // public BigInteger getI_wf_id() {
    //     return i_wf_id;
    // }
    // public void setI_wf_id(BigInteger i_wf_id) {
    //     this.i_wf_id = i_wf_id;
    // }
    // public String getI_status() {
    //     return i_status;
    // }
    // public void setI_status(String i_status) {
    //     this.i_status = i_status;
    // }
    // public String getI_task_id() {
    //     return i_task_id;
    // }
    // public void setI_task_id(String i_task_id) {
    //     this.i_task_id = i_task_id;
    // }
   

   




}
