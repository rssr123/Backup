package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MFTTypeSearchRequest {

    private Integer i_page;
    private Integer i_size;
    private String i_status;
    private String i_ss_cd;
    private String i_searchTerm;
    
}
