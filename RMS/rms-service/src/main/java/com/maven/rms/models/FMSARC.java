package com.maven.rms.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FMSARC {
    @JsonProperty("UsrIdentityNbr")
    private String usrIdentityNbr;

    @JsonProperty("CustomerID")
    private String customerID;

    @JsonProperty("CustomerName")
    private String customerName;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("Date")
    private String date;
}
