package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;

@Getter
@Setter
public class DailySettlementRequest {
    
    @NotNull(message = "profile_nm is required.")
    private String profile_nm;
    @NotNull(message = "dt_collection is required.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMAT, timezone = "Asia/Singapore")
    private Date dt_collection;
    @NotNull(message = "total_amt is required.")
    private BigDecimal total_amt;
    @NotNull(message = "daily_stmnt_id is required.")
    private String daily_stmnt_id;

}
