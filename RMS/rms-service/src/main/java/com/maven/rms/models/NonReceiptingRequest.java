package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NonReceiptingRequest {
    private Integer i_page;
    private Integer i_size;
    private String i_task_id;
    private String i_settlement_date;
    private String i_merchant_id;
    private String i_task_status;
    private String i_date_uploaded;
    private String i_settle_status;
}
