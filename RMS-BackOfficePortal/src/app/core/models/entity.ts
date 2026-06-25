import { Time } from "@angular/common";

export interface MFT{
   
    fee_detail_pk: number;
    fee_detail_id: string;
    fee_grp_id:number;
    fee_grp_nm_en:string;
    fee_grp_nm_bm:string;
    fee_detail_nm_e:string;
    fee_detail_nm_b:string;
    unit_fee:number;
    promo_startdt:Date;
    promo_enddt:Date;
    promo_fee:number;
    tax_cd_id:number;
    tax_cd:string;
    allow_otc:number;
    ll_parent_id:string;
    ll_start_day:number;
    ll_start_mth:number;
    ll_end_day:number;
    ll_end_mth:number;
    ledger_cd:string;
    ss_cd:string;
    dt_created:Date;
    dt_modified:Date;
    created_by:string;
    modified_by:string;
    modified_by_nm:string;
    created_by_nm:string;
    status:string;
    total:number;
    isPub: number;
}

export interface MasterTaskList{

    wf_id:number;
    fee_detail_pk:number;
    fee_detail_nm_e:string;
    fee_detail_nm_b:string;
    effective_date:Date;
    dt_modified:Date;
    modified_by:string;
    status:string;
    assign_to:string;
    total:number;
}


export interface MFTWF{
    
    wf_id: number;	  
    fee_detail_pk:number;
    fee_detail_id: string;
    fee_grp_id:number;
    fee_grp_nm_en:string;
    fee_grp_nm_bm:string;
    fee_detail_nm_e:string;
    fee_detail_nm_b:string;
    fee_amt:number;
    promo_startdt:Date;
    promo_enddt:Date;
    promo_fee:number;
    tax_cd_id:number;
    tax_cd:string;
    allow_otc:number;
    ll_parent_id:string;
    ll_start_day:number;
    ll_start_mth:number;
    ll_end_day:number;
    ll_end_mth:number;
    ledger_cd:string;
    ss_cd:string;
    ss_nm:string;
    effective_date:Date;
    dt_created:Date;
    dt_modified:Date;
    created_by:string;
    created_by_nm:string;
    modified_by:string;
    modified_by_nm:string;
    status:string;
    status_en:string;
    status_bm:string;
    assign_to:string;
    assign_to_nm:string;
    action:string;
    r_fee_det_nm : string;
    r_fee_amt : number;
    r_ss_cd : string;
    r_promo_startdt : Date;
    r_promo_enddt : Date;
    r_ll_required : number;
    r_add_notes : string;
    mft_status : string;
    r_promo_fee : number;
    task_id:string
    total:number;
    is_pub: number;
}

export interface MFTWFHist{
    action:String;
    dt_activity:Date;
    act_by:string;
    assign_to:string;
    remark:string;
    dt_created:Date;
    dt_modified:Date;
    created_by:string;
    modified_by:string;
    status_en:string;
    status_bm:string;
    total:number;
    assign_to_nm:string;
    act_by_nm:string;
}


export interface Role{

    role_nm_en:string;
    role_nm_bm:string;
    role_desc:string;
    role_special:number;
    role_owner:string;
    dt_created:Date;
    dt_modified:Date;
    modified_by:string;
    status:string;
    assign_to:string;

}

export interface User{
    ssm4uuserrefno:string;
    nm:string;
    email:string;
}

export interface SourceSystemCode{
    ss_cd:string ;
    ss_id:number ;
    ss_nm:string ; 
    dt_modified:Date ;
    modified_by:string ;
    status:string ;
    status_en:string ;
    status_bm:string ;
    total:number ;
}

export interface Param{


    param_cd:string;
    nm_en:string;
    nm_bm:string;
    total:number;
    
}

export interface TaxCode{

    tax_cd:string;
    tax_cd_id : number;
    tax_cd_nm_en:string;
    tax_cd_nm_bm:string ;
    tax_pct:number;
    dt_modified:Date;
    modified_by:string ;
    status:string ;
    status_en:string ;
    status_bm:string ;
    total:number ;

}

export interface ExportMFT{
   
   
    fee_detail_id: string;
    fee_grp_id:number;
    fee_grp_nm_en:string;
    fee_grp_nm_bm:string;
    fee_detail_nm_e:string;
    fee_detail_nm_b:string;
    unit_fee:number;
    promo_startdt:Date;
    promo_enddt:Date;
    promo_fee:number;
    tax_cd_id:number;
    tax_cd:string;
    allow_otc:number | string;
    ll_parent_id:string;
    ll_start_day:number;
    ll_start_mth:number;
    ll_end_day:number;
    ll_end_mth:number;
    ledger_cd:string;
    ss_cd:string;
    dt_created:Date;
    dt_modified:Date;
    created_by:string;
    modified_by:string;
    modified_by_nm:string;
    created_by_nm:string;
    status:string;
    isPub: number | string;

    
}

export interface Roles{
    status: string;
    total: number;
    dtModified: Date;
    modifiedBy: string;
    roleNmEn: string;
    roleNmBm: string;
    roleId: number;
}

export interface UserRole{
    ssm4uuserrefno: string;
    name: string;
    email: string;
    created_by: string;
    modified_by: string;
    status: string;
    role_nm_en: string;
    totalRoles: number;
}

export interface MFTWFDoc{
    wfdoc_id:number;
    file_nm:string;
    file_content:string;
    file_type:string;
    file_size_kb:number;
    dt_created:Date;
    dt_modified:Date;
    created_by:string;
    modified_by:string;
    total:number;

}

export interface DeferredIncome {
    di_id: number;
    fee_detail_pk: string;
    fee_detail_id: string;
    entity_type: string;
    entity_no: string;
    dt_effective: Date;
    dt_expiry: Date;
    dt_termination: Date;
    item_ref_no: string;
    approval_status: string;
    dt_approval: Date;
    no_of_yr: number;
    unit_fee: number;
    total_fee: number;
    bal_di_amt: number;
    next_calc_dt: Date;
    dt_created: Date;
    dt_modified: Date;
    created_by: string;
    created_by_nm: string;
    modified_by: string;
    modified_by_nm: string;
    status: string;
    status_nm_en: string;
    status_nm_bm: string;
    txn_type: string;
    total: number;
}



export interface RICP{
    ricp_id:number;
    txn_type:string;
    entity_type:string;
    entity_no:string;
    calendar_yr:string;
    cp_no:string;
    cp_act_id:string;
    cp_sect_id:string;
    cp_sub_sect_id:string;
    dt_issuance:Date;
    dt_expiry:Date;
    dt_void:Date;
    dt_cancel:Date;
    dt_writeoff:Date;
    cp_amt:number;
    accr_amt:number;
    dt_created:Date;
    dt_modified:Date;
    created_by:string;
    modified_by:string;
    status:string;
    cp_tier:number;
    cp_tier_amt:number;
    total:number;

}

export interface bankReconDetails{
    i_file_nm: string;
    file_nm: string;
    no_pg_txn_in: number;
    total_pg_amt_in: number;
    no_pg_txn_out: number;
    total_pg_amt_out: number;
    no_bank_txn_in: number;
    total_bank_txn: number;
    bank_txn: number;
    bank_txn_amt: number;
    task_no: string;
    task_status: string;
    recon_status: string;
    stmt_no: string;
    dt_settlement: Date | null;
    remarks: string;

    // PG details page
    pg_txn_date: Date | null;
    pg_txn_type: string;
    pg_found_in_rms: string;
    pg_txn_id: number;
    pg_txn_code: string;
    pg_sub_criteria: string;
    pg_txn_amt: number;
    pg_mdr: number;
    pg_sst: number;
    pg_net_amt: number;

    // Bank details page
    bank_txn_ref: string;
    bank_acct_no: string;
    bank_brn_chn: string;
    bank_dt_posting: Date | null;
    bank_tm_posting: Date | null;
    bank_credit: number;


    //Bank Number Statemet
    bank_file_nm: string;
    bank_file_size_kb: string;
    bank_uploaded_by: string;
    bank_dt_uploaded: string;

}

export interface Permissions{
    // perm_cd: string;
    module_nm: string;
    function_nm: string;
    status: string;
    total: number;
}

export interface PermissionByID {
    perm_id: number;
    perm_cd: string;
    module_nm: string;
    function_nm: string;
    status: string;
    total: number;
}

export interface rolePerm{
    perm_id: number;
    is_allow: number;
    status: string;
    total: number;
}

export interface UnmatchTransMonth {
    
    period: string;    
    in: number;
    out: number;
    variance: number;
    periodbalance: string;
    

}

export interface Breadcrumb {
    label: string;
    url: string;
}