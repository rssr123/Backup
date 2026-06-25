package com.maven.rms.models.OTC;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCEMVReconciliationDocRequest {
    private Integer i_otc_bal_doc_id;
    private Integer i_rc_emv_id;
    private Integer i_rc_emv_doc_id;
}
