package com.maven.rms.models;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter

public class StateRequest {
    private Integer i_page;
    private Integer i_size;
    private String i_param_id; 
    private String i_param_cd; 
    private String i_nm_en; 
    private String i_nm_bm; 
    private String i_param_grp_nm; 
    private Integer i_seq; 
    private String i_status; 
}
