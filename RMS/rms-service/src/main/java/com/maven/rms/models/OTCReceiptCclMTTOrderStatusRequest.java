package com.maven.rms.models;

import java.math.BigInteger;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OTCReceiptCclMTTOrderStatusRequest {

    private BigInteger i_mtt_id;
    private String i_order_status;
    private String i_modified_by;
   // private Boolean i_update_MTT_status;
    private BigInteger i_otc_rc_id;
}
