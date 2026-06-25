export interface OTCBank {
    param_cd: string;
    nm_en: string;
    nm_bm: string;
    param_grp_nm: string;
    total: number;
}

export interface OTCCheque {
    mtt_id: number;
    otc_id: number;
    che_amt: number;
    che_date: Date;
    che_bank_nm: string;
    che_payer_nm: string;
    che_no: string;
    che_status: string;
    che_ba_acct_no: string;
    che_id: string;
    counter_id: string | null;
    branch_cd: string | null;
    rcpt_no: string | null;
    orn_no: string | null;
    coll_slip_no: string | null;
    total: number;
}

export interface NBLTC {
    bt_cd: string;
    bt_desc: string;
    class_id: string;
}

export interface NBLCM {
    classId: string;
    classDesc: string;
}

export interface NBLTCItem{
    bt_cd: string;
    bt_ty: string;
    bt_desc: string;
    class_id: string;
    ss_cd: string;
    mft_pk: number;
    mft_id: string;
    dps_mft_pk: number;
    dps_mft_id: string;
    fee_detail_pk: number;
    fee_detail_id: string;
    fee_detail_nm_e: string;
    fee_detail_nm_b: string;
    unit_fee: number;
    tax_pct: number;
}

export interface NonBillingListing{
    // Columns from rms_nonbil
    non_bill_id: number;
    req_name: string;
    req_email: string;
    non_bil_no: string;
    non_bill_desc: string;
    ret_che_no: string;
    total_bil_amt: number;
    remark: string;
    bil_status: string;
    fms_admin_email: string;
    fms_admin_nm: string;
    dt_created: string;
    dt_modified: string;
    created_by: string;
    modified_by: string;
    status: string;
    bill_action: string;
    dt_action: string;
    performed_by: string;
    otc_counter_id: number;
    otc_body_id: number;
    // Columns from rms_nonbil_cust
    non_bilcust_id: number;
    cust_id: string;
    cust_nm: string;
    cust_email: string;
    cust_phone: string;
    cust_addr_1: string;
    cust_addr_2: string;
    cust_addr_3: string;
    ent_nm: string;
    ent_no: string;
    ent_ty: string;
    cust_postcode: string;
    cust_city: string;
    cust_state: string;
    che_no: string;
    che_id: string;
    total: number;
}

export interface NonBillingItem {
    non_bilitem_id: number;
    non_bil_id: number;
    mft_pk: number;
    fee_detail_nm_e: string;
    unit_fee: number;
    qty: number;
    tax_pct: number;
    tax_amt: number;
    item_total_amt: number;
    item_ref_no: string;
    total: number;
}


export interface NonBillDocs{
    non_bil_doc_id: number; 
    non_bil_id: number;
    file_nm: string;
    file_type: string;
    file_size: number;
    file_category: string;
    dt_created: Date;
    dt_modified: Date;
    created_by: string;
    modified_by: string;
    total: number;
}

export interface NonBilHist{
    non_bil_a_id: number;
    non_bil_id: number;
    req_name: string;
    req_email: string;
    non_bil_no: string;
    non_bil_desc: string;
    ret_che_no: string;
    total_bil_amt: number;
    remark: string;
    bil_status: string;
    fms_admin_email: string;
    fms_admin_nm: string;
    dt_created: Date;
    dt_modified: Date;
    created_by: string;
    modified_by: string;
    status: string;
    bil_action: string;
    dt_action: Date;
    performed_by: string;
    otc_counter_id: number;
    otc_body_id: number;
    total: number;
}