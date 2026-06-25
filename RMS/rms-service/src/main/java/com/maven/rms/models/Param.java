package com.maven.rms.models;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "rms_param")
public class Param {
 
    @Id
    private String param_cd;
    private String nm_en;
    private String nm_bm;
    private Integer total;
   
    // public String getParam_cd() {
    //     return param_cd;
    // }
    // public void setParam_cd(String param_cd) {
    //     this.param_cd = param_cd;
    // }
    // public String getNm_en() {
    //     return nm_en;
    // }
    // public void setNm_en(String nm_en) {
    //     this.nm_en = nm_en;
    // }
    // public String getNm_bm() {
    //     return nm_bm;
    // }
    // public void setNm_bm(String nm_bm) {
    //     this.nm_bm = nm_bm;
    // }
    // public Integer getTotal() {
    //     return total;
    // }
    // public void setTotal(Integer total) {
    //     this.total = total;
    // }
}