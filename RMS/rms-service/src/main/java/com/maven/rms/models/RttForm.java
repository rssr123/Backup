package com.maven.rms.models;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RttForm {
    private String identityType;
    private String identityNumber;
    private String bankAccountNo;
    private String bankAccountType;
    private String bankAccountName;
    private String bankHolderName;
    private String billingAddress1;
    private String billingAddress2;
    private String billingAddress3;
    private String custCity;
    private String custPostcode;
    private String custState;
    private String recEmail;
    private String custNm;
    private String custEmail;
    private String custPhone;
    private String rcptNo;
    private BigDecimal rcptAmt;
    private String ornNo;
    private String txnId;
    private String entityNm;
    private String entityTy;
    private String entityNo;
}