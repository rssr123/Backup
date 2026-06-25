package com.maven.rms.models;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PGReconTaskRequest {

    private String i_task_id;
    @NotNull(message = "Remarks cannot be empty")
    private String i_remarks;
    private String i_task_status;
    private String i_modified_by;
    
    // public String getI_task_id() {
    //     return i_task_id;
    // }
    // public void setI_task_id(String i_task_id) {
    //     this.i_task_id = i_task_id;
    // }
    // public String getI_remarks() {
    //     return i_remarks;
    // }
    // public void setI_remarks(String i_remarks) {
    //     this.i_remarks = i_remarks;
    // }
    // public String getI_task_status() {
    //     return i_task_status;
    // }
    // public void setI_task_status(String i_task_status) {
    //     this.i_task_status = i_task_status;
    // }
    // public String getI_modified_by() {
    //     return i_modified_by;
    // }
    // public void setI_modified_by(String i_modified_by) {
    //     this.i_modified_by = i_modified_by;
    // }
    
    
}
