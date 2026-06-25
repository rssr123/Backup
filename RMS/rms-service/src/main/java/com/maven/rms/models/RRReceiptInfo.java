package com.maven.rms.models;

import java.math.BigInteger;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter


public class RRReceiptInfo {


    // private String mtt_id;
    // private String otc_id;
    private String otc_rcpt_id;
    //private String rcpt_no;
    //private Date rcpt_dt;
   // private String rcpt_status;
  //  private Integer rcpt_reprint;
    //private String  ssdocref_id;
    private String ver_id_otc;
    private String ver_id_mtt;
    //private String orn_no;
    
   // private String file_nm;
   // private String file_content;
  //  private String file_type;
   // private String idaman_file_name;



    private BigInteger mtt_id;
    private Integer otc_id;
    private String rcpt_no;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy", timezone = "Asia/Singapore")
    private Date rcpt_dt;
    private String rcpt_status;
    private Integer rcpt_reprint;
    private String ver_id;
    private String ssdocref_id;
    private String file_nm;
    private String remark;
    private String orn_no;

    
    private String file_content;
    private String file_type;
    private String idaman_file_name;
    private Integer total;

    
    
}
