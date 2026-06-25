export interface PGReconList{
    i_task_id:string;
    i_file_nm:string;
    i_dt_settlement:Date;
    i_dt_uploaded:Date;
    i_merchant_id:string;
    i_task_status:string;
    i_recon_status:string;
    i_uploadedby:string;
    total:number;
}

export interface PGReconDetail {
    pg_txn_settlement_no: number;
    pg_total_txn_settlement_amt: number;
    pg_txn_adj_no: number;
    pg_total_txn_adj_amt: number;
    pg_total_txn_other: number;
    pg_matched_no: number;
    pg_matched_total: number;
    pg_found_no: number;
    pg_found_total: number;
    pg_not_found_no: number;
    pg_not_found_total: number;
    pg_sam_no: number;
    pg_sam_total: number;
    pg_snm_no: number;
    pg_snm_total: number;
    pg_txf_no: number;
    pg_txf_total: number;
    rms_txn_no: number;
    rms_paid_no: number;
    rms_paid_total: number;
    rms_failed_no: number;
    rms_failed_total: number;
    rms_rcpt_no: number;
    rms_rcpt_total: number;
    rms_sam_no: number;
    rms_sam_total: number;
    rms_snm_no: number;
    rms_snm_total: number;
    rms_txf_no: number;
    rms_txf_total: number;
    rms_cip_no: number;
    rms_cip_total: number;
    rms_ncp_no: number;
    rms_ncp_total: number;
    rms_nfp_no: number;
    rms_nfp_total: number;
    task_id: string;
    recon_status: string;
    dt_statement: string;
    dt_settlement_char: string;
    remarks: string;
    task_status: string;
}

export interface PGDetailListing {
    dt_txn:string;
    found_in_rms:string;
    mdr_amt:number;
    net_amt:number;
    sst_amt:number;
    total: number;
    txn_amt:number;
    txn_cd: string;
    txn_id:string;
    txn_type: string;
}

export interface RMSDetailListing {
    dt_txn:string;
    txn_id:string;
    cust_nm:string;
    orn_no:string;
    found_in_pg:string;
    txn_amt:number;
    sub_criteria:string;
    order_status:string;
    total: number;
}