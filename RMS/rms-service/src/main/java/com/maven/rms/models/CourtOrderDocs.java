package com.maven.rms.models;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourtOrderDocs {

    private Integer cc_doc_id;
    private Integer cc_case_id;
    private String file_name;
    private String file_type;
    private Integer file_size_kb;
    private Date dt_created;
    private String created_by;
    
}
