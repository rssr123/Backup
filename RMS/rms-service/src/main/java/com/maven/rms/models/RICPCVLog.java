package com.maven.rms.models;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RICPCVLog {
	private Integer i_ricp_id;
	private String i_entity_type;
	private String i_entity_no;
	private String i_cp_no;
	private Date i_dt_void;
	private Date i_dt_cancel;
	private String i_created_by;
	private String i_modified_by;
	private String i_status;
	
	public RICPCVLog(Integer i_ricp_id, String i_entity_type, String i_entity_no, 
    		String i_cp_no, Date i_dt_void, Date i_dt_cancel,
    		String i_created_by, String i_modified_by, String i_status) {
		this.i_ricp_id = i_ricp_id;
		this.i_entity_type = i_entity_type;
		this.i_entity_no = i_entity_no;
		this.i_cp_no = i_cp_no;
		this.i_dt_void = i_dt_void;
		this.i_dt_cancel = i_dt_cancel;
		this.i_created_by = i_created_by;
		this.i_modified_by = i_modified_by;
		this.i_status = i_status;
	}

	// public Integer getI_ricp_id() {
	// 	return i_ricp_id;
	// }

	// public void setI_ricp_id(Integer i_ricp_id) {
	// 	this.i_ricp_id = i_ricp_id;
	// }

	// public String getI_entity_type() {
	// 	return i_entity_type;
	// }

	// public void setI_entity_type(String i_entity_type) {
	// 	this.i_entity_type = i_entity_type;
	// }

	// public String getI_entity_no() {
	// 	return i_entity_no;
	// }

	// public void setI_entity_no(String i_entity_no) {
	// 	this.i_entity_no = i_entity_no;
	// }

	// public String getI_cp_no() {
	// 	return i_cp_no;
	// }

	// public void setI_cp_no(String i_cp_no) {
	// 	this.i_cp_no = i_cp_no;
	// }

	// public Date getI_dt_void() {
	// 	return i_dt_void;
	// }

	// public void setI_dt_void(Date i_dt_void) {
	// 	this.i_dt_void = i_dt_void;
	// }

	// public Date getI_dt_cancel() {
	// 	return i_dt_cancel;
	// }

	// public void setI_dt_cancel(Date i_dt_cancel) {
	// 	this.i_dt_cancel = i_dt_cancel;
	// }

	// public String getI_created_by() {
	// 	return i_created_by;
	// }

	// public void setI_created_by(String i_created_by) {
	// 	this.i_created_by = i_created_by;
	// }

	// public String getI_modified_by() {
	// 	return i_modified_by;
	// }

	// public void setI_modified_by(String i_modified_by) {
	// 	this.i_modified_by = i_modified_by;
	// }

	// public String getI_status() {
	// 	return i_status;
	// }

	// public void setI_status(String i_status) {
	// 	this.i_status = i_status;
	// }
}
