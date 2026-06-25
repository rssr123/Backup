package com.maven.rms.models;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FMSLedgerDocRequest {

    private Integer i_page;
    private Integer i_size;
    private Integer i_fms_id;
    private String i_file_nm;
    private String i_file_content;
    private String i_file_type;
    private Integer i_file_size;
    private String i_created_by;
    private String i_modified_by;
    private String i_status;
    
    public String toString() {
    	StringBuilder msg = new StringBuilder();
    	msg.append("i_page:" + ((i_page == null || i_page.equals(null))? "-1":i_page.toString())
    			+ "\ni_size:" + ((i_size == null || i_size.equals(null))?"-1":i_size.toString())
    			+ "\ni_fms_id:" + ((i_fms_id == null || i_fms_id.equals(null))?"-1":i_fms_id.toString())
    			+ "\ni_file_nm:" + ((i_file_nm == null || i_file_nm.equals(null))?"-1":i_file_nm)
    			+ "\ni_file_content" + ((i_file_content == null || i_file_content.equals(null))?"-1":i_file_content)
    			+ "\ni_file_type: " + ((i_file_type == null || i_file_type.equals(null))?"-1":i_file_type)
    			+ "\ni_file_size:" + ((i_file_size == null || i_file_size.equals(null))?"-1":i_file_size.toString())
    			+ "\ni_created_by:" + ((i_created_by == null || i_created_by.equals(null))?"-1":i_created_by)
    			+ "\ni_modified_by:" + ((i_modified_by == null || i_modified_by.equals(null))?"-1":i_modified_by)
    			+ "\ni_status:" + ((i_status == null || i_status.equals(null))?"-1":i_status));
    	
    	return msg.toString();
    }
    
    // public Integer getI_size() {
    //     return i_size;
    // }
    // public void setI_size(Integer i_size) {
    //     this.i_size = i_size;
    // }

    // public Integer getI_page() {
    //     return i_page;
    // }
    // public void setI_page(Integer i_page) {
    //     this.i_page = i_page;
    // }

    // public Integer getI_fms_id() {
    //     return i_fms_id;
    // }
    // public void setI_wf_id(Integer i_fms_id) {
    //     this.i_fms_id = i_fms_id;
    // }
    // public String getI_file_nm() {
    //     return i_file_nm;
    // }
    // public void setI_file_nm(String i_file_nm) {
    //     this.i_file_nm = i_file_nm;
    // }
    // public String getI_file_content() {
    //     return i_file_content;
    // }
    // public void setI_file_content(String i_file_content) {
    //     this.i_file_content = i_file_content;
    // }
    // public String getI_file_type() {
    //     return i_file_type;
    // }
    // public void setI_file_type(String i_file_type) {
    //     this.i_file_type = i_file_type;
    // }
    // public Integer getI_file_size() {
    //     return i_file_size;
    // }
    // public void setI_file_size(Integer i_file_size) {
    //     this.i_file_size = i_file_size;
    // }
    // public String getI_created_by() {
    //     return i_created_by;
    // }
    // public void setI_created_by(String i_created_by) {
    //     this.i_created_by = i_created_by;
    // }
    // public String getI_modified_by() {
    //     return i_modified_by;
    // }
    // public void setI_modified_by(String i_modified_by) {
    //     this.i_modified_by = i_modified_by;
    // }
    // public String getI_status() {
    //     return i_status;
    // }
    // public void setI_status(String i_status) {
    //     this.i_status = i_status;
    // }
    
   

    

    
}
