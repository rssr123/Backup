package com.maven.rms.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillingType {
    
    private Integer bltc_id;
    private String btCd;
    private String btTy;
    private String btDesc;
    private String classId;
    private String ssCd;
    // private Integer mftPk;  
    // private String mftId;   
    // private Integer dpsMftPk;   
    // private String dpsMftId; 
    private Date dtCreated;
    private Date dtModified;
    private String createdBy;
    private String modifiedBy;
    private String status;
    private Integer total;
    
    private List<MftDpsItem> items = new ArrayList<>();

    @Getter
    @Setter
    public static class MftDpsItem {
        private Integer mftPk;
        private String mftId;
        private Integer dpsMftPk;
        private String dpsMftId;
    }
}
