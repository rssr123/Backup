package com.maven.rms.models.OTC;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NBLDocInsRequest {
    // private Integer i_non_bil_id;
    private String i_file_nm;
    // private Blob i_file_content;
    private String i_file_content;
    private String i_file_type;
    private Integer i_file_size;
    private String i_file_category;
    private String i_created_by;
    private String i_modified_by;
    private String i_non_bil_no;

    //shceduler
    private String i_mtt_id;

}
