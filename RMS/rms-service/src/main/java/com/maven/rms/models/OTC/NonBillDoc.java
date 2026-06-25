package com.maven.rms.models.OTC;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NonBillDoc {
    private Integer non_bil_doc_id;
    private Integer non_bil_id;
    private String file_nm;
    private String file_type;
    private Integer file_size;
    private String file_category;
    private Date dt_created;
    private Date dt_modified;
    private String created_by;
    private String modified_by;
    private Integer total;
}
