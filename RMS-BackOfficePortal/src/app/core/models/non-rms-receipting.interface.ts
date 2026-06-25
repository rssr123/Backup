export interface NonRMSReceipting {
    // ag_sale_id: number;
    ss_cd: string;
    cn_cust_id: string;
    dn_cust_id: string;
    cash_acct: string;
    merchant_id: string;
    stmt_no: string;
    fms_ari_ref_no: string;
    ari_total_amt: number;
    mdr_total_amt: number;
    total_net_amt: number;
    dt_settlement: Date;
    total_trx_no: number;
    // batch_size: number;
    // batch_cnt: number;
    task_id: string;
    task_status: string;
    // dt_created: Date;
    // dt_modified: Date;
    status: string;
    dt_upload: Date;
    settle_status: string;
    total: number;
}