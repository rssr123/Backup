package com.maven.rms.models;

// import java.math.BigInteger;
// import java.sql.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RILTRequest {

    // private Integer i_page;
    // private Integer i_size;
    // private BigInteger i_id;
    private String lit_no; // NVARCHAR(20)
    private String lit_item_ref; // NVARCHAR(100)
    private double lit_amount; // DECIMAL(16,2)
    private String entity_type; // NVARCHAR(1)
    private String entity_no; // NVARCHAR(40)
    private String dt_due; // NVARCHAR(100) -> TO_DATE
    // private Date i_dt_created;
    // private Date i_dt_modified;
    // private String i_created_by;
    // private String i_modified_by;
    // private Date i_dt_modified_fr;
    // private Date i_dt_modified_to;
    // private String i_status;
    
}
