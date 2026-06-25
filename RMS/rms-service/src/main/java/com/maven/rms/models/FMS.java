package com.maven.rms.models;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FMS {

    private Integer fmsId;
    private String fmsCd;
    private Integer congLedCnt;
    private String modifiedBy;
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT)
    private Date dtModified;
    private Integer isActive;
    private Integer total;
    private Integer mft_total;

}
