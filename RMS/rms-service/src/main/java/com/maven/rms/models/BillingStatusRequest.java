package com.maven.rms.models;



import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;
import javax.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillingStatusRequest {
    
    @NotNull(message = "billing_no is required.")
    @Size(min = 1, max = 150, message = "billing_no is required.")
    private String billing_no;

}
