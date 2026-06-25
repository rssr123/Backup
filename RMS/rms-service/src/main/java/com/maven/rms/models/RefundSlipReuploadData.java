package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefundSlipReuploadData {
    private String fileNm;
    private String refundSlipNo;
    private String ornNo;
    private String rttAppNo;
    private String ssdocrefId;
}