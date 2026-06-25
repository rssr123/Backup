package com.maven.rms.models;

import java.math.BigInteger;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCReceiptCheck {

    private BigInteger otc_rc_id ;
    private BigInteger mtt_id;
    private String ssdocref_id;
    
}
