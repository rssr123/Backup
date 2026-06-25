package com.maven.rms.models;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.maven.rms.config.Constants;

@Getter
@Setter
public class CreditControlReminderRequest {

    @NotNull(message = "fms_ari_ref_no is required.")
    private String fms_ari_ref_no;
    @NotNull(message = "reminder_cnt is required.")
    private Integer reminder_cnt;
    @NotNull(message = "reminder_dt is required.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMAT, timezone = "Asia/Singapore")
    private Date reminder_dt;
    @NotNull(message = "reminder_email_content is required.")
    private String reminder_email_content;
    @NotNull(message = "reminder_email_sent_date is required.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMAT, timezone = "Asia/Singapore")
    private Date reminder_received_date;

// @JsonSetter("reminder_email_content")
// public void setReminder_email_content(String content) {
//     if (content != null) {
//         // Replace common smart punctuation
//         content = content
//                 .replace("’", "'")
//                 .replace("‘", "'")
//                 .replace("“", "\"")
//                 .replace("”", "\"")
//                 .replace("–", "-")         // en-dash
//                 .replace("—", "-")         // em-dash
//                 .replace("\u00A0", " ");   // non-breaking space

//         // Remove all non-ASCII characters (outside 32–126 range)
//         content = content.replaceAll("[^\\x20-\\x7E\\r\\n]", "");
//     }
//     this.reminder_email_content = content;
// }


}
