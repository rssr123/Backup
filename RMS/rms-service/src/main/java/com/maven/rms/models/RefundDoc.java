package com.maven.rms.models;

import java.util.Date;
import javax.persistence.Id;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefundDoc {

    @Id
    private int rtt_doc_id;
    private int rtt_wf_id;
    private String file_nm;       // File name
    private String file_content;  // Original file content (optional)
    private String base64Content; // Base64-encoded file content for response
    private String file_type;     // File MIME type (e.g., application/pdf)
    private Integer file_size_kb; // File size in KB

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date dt_created;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date dt_modified;

    private String created_by;
    private String modified_by;
    private Integer total;

    // Convenience method to get file size in bytes
    public long getFileSize() {
        return (file_size_kb == null ? 0 : file_size_kb * 1024L);
    }

    @Override
    public String toString() {
        return "RefundDoc{" +
                "rtt_doc_id=" + rtt_doc_id +
                ", file_nm='" + file_nm + '\'' +
                ", file_type='" + file_type + '\'' +
                ", file_size_kb=" + file_size_kb +
                ", base64Content='" + (base64Content != null ? "[CONTENT]" : "null") + '\'' +
                '}';
    }
}
