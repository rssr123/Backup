package com.maven.rms.models;

import java.math.BigInteger;
import java.sql.Blob;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCBalancingDocRequest {
    //Daily Balancing Status
    private String fileNm;          // i_file_nm
    private String fileContent;     // i_file_content
    private Blob i_file_content;
    private String fileType;        // i_file_type
    private int fileSize;           // i_file_size
    private String fileCategory;    // i_file_category
    private String ssm4uuserrefno;      // i_ssm4urefno

    private String branch_code;
    private Date bal_date;

    private Date i_bal_date;
    private String dtSettlement;      // i_dt_settlement
    private Date i_dtSettlement;
    private String terminalId;      // i_terminal_id
    private String batchNo;         // i_batch_no
    private String batchCount;      // i_batch_count
    private String batchAmt;        // i_batch_amt

    private BigInteger docID;
}
