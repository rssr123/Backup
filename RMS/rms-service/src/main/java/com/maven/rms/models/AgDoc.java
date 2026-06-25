package com.maven.rms.models;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgDoc {
    private Integer ag_doc_id;
    private Integer ag_sale_id;
    private String stmt_no;
    private String ag_type;
    private String file_nm;
    private String file_type;
    private Integer file_size;
    private Date dt_created;
    private Date dt_modified;
    private String created_by;
    private String modified_by;
    private Integer total;
}
