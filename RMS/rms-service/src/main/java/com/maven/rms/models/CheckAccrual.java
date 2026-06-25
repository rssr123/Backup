package com.maven.rms.models;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckAccrual {

    private String entityType;
    private String entityNo;
    private String cpNo;
    private String litItemRef;
    private String txnType;
    private String calanderYr;
    private String feeDetailsId;


}
