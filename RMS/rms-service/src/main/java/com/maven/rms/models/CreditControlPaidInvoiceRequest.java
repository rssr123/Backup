package com.maven.rms.models;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;

@Getter
@Setter
public class CreditControlPaidInvoiceRequest {
    
    @NotNull(message = "fms_ari_ref_no is required.")
    private String fms_ari_ref_no;

}
