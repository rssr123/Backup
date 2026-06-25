package com.maven.rms.models;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillingTypeRequest {
    private Integer i_page;
    private Integer i_size;
    private Integer i_bltc_id;
    private String i_bt_cd;
    private String i_bt_ty;
    private String i_bt_desc;
    private String i_class_id;
    private String i_ss_cd;
    private Integer i_mft_pk;
    private String i_mft_id;
    private Integer i_dps_mft_pk;
    private String i_dps_mft_id;
    private Date i_dt_modified_fr;
    private Date i_dt_modified_to;
    private String i_modified_by;
    private String i_status;
}
