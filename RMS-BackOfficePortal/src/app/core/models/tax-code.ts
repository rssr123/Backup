export interface Data{
    taxCd: any;
    tax_cd:string;
    tax_cd_nm_en:string;
    tax_cd_nm_bm:string;
    tax_pct:number;
    dt_created:Date;
    dt_modified:Date;
    created_by:string;
    modified_by:string;
    status:string;

}

export interface TaxCodeResponse{

   requestTimestamp : Date;
   responseTimestamp:Date;
   statusCode : string;
   message : string;
   data : Data[];

}


  /*  private String tax_cd;
    private String tax_cd_nm_en;
    private String tax_cd_nm_bm;
    private BigDecimal tax_pct;
    private Date dt_created;
    private Date dt_modified;
    private String created_by;
    private String modified_by;
    private String status;*/

