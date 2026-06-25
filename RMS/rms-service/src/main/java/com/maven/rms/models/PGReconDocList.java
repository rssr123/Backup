package com.maven.rms.models;

import java.math.BigInteger;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PGReconDocList {

    private BigInteger i_rc_pg_id;
    private String i_file_nm;
    private String i_file_content;
    
    // public BigInteger getI_rc_pg_id() {
    //     return i_rc_pg_id;
    // }
    // public void setI_rc_pg_id(BigInteger i_rc_pg_id) {
    //     this.i_rc_pg_id = i_rc_pg_id;
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

}
