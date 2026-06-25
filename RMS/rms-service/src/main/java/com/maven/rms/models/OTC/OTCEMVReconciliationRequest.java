package com.maven.rms.models.OTC;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCEMVReconciliationRequest {
    private Integer i_page;
    private Integer i_size;
    private String i_date_from;
    private String i_date_to;
}
