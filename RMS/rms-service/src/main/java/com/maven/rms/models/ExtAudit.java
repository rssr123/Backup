package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExtAudit {
    private String i_module_nm; // External System Name
    private String i_rms_batch_no; // RMS Batch Number
    private String i_request_body; // Request Body
    private String i_response_body; // Response
    private String i_direction; // Direction (e.g., IN/OUT)
    private String i_remark; // Additional Remarks
}
