package com.maven.rms.models;

import java.math.BigInteger;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MTTOrderStatusRequest {
    
    private BigInteger i_mtt_id;
    private String i_order_status;
    private String i_modified_by;

}
