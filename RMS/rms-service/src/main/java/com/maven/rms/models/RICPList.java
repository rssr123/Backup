package com.maven.rms.models;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RICPList {
	private List<RICP> ricpList;
	private Integer total;
	
	public RICPList(List<RICP> ricpList, Integer total) {
		this.ricpList = ricpList;
		this.total = total;
	}
	
	 //public List<RICP> getRicpList() {
	 //	return ricpList;
	 //}
	 //public void setRicpList(List<RICP> ricpList) {
	 //	this.ricpList = ricpList;
	 //}
	 //public Integer getTotal() {
	 //	return total;
	 //}
	 //public void setTotal(Integer total) {
	 //	this.total = total;
	 //}
}
