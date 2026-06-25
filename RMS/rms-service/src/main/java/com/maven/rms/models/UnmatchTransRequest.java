package com.maven.rms.models;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnmatchTransRequest {

    
    @JsonFormat(pattern = "yyyy-MM-dd") // Change the pattern to match the format of the date string
    private Date i_period_key;

    // public Date getI_period_key() {
    //     return i_period_key;
    // }

    // public void setI_period_key(Date i_period_key) {
    //     this.i_period_key = i_period_key;
    // }

}