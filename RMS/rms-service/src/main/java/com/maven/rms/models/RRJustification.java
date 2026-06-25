package com.maven.rms.models;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// @Entity(name = "rms_otc")
public class RRJustification {

    // NVARCHAR(50) AS otc_id,
    // NVARCHAR(50) AS otc_rc_rp_id,
    // NVARCHAR(50) AS otc_rcpt_id,
    // NVARCHAR(50) AS justication,
    // DATETIME YEAR TO SECOND AS dt_created,
    // DATETIME YEAR TO SECOND AS dt_modified,
    // NVARCHAR(50) AS created_by,
    // NVARCHAR(50) AS modified_by,
    // NVARCHAR(50) AS status,
    // NVARCHAR(50) AS ssdocref_id,
    // NVARCHAR(50) AS ver_id,
    // NVARCHAR(50) AS rcpt_no;

    private String otc_id;
    private String mtt_id;
    private String otc_rc_rp_id;
    private String otc_rcpt_id;
    private String justication;
    private Date dt_created;
    private Date dt_modified;
    private String created_by;
    private String modified_by;
    private String status;
    private String ssdocref_id;
    private String ver_id_otc;
    private String ver_id_mtt;

    private String rcpt_no;

    private String file_nm;
    private String file_content;
    private String file_type;
    private String idaman_file_name;

  

}
