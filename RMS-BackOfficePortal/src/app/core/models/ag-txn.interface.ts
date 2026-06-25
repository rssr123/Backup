export interface AgTxnDoc{
    ag_doc_id: number;
    ag_sale_id: number;
    ag_type: string;
    file_nm: string;
    file_type: string;
    file_size: number;
    dt_created: Date;
    dt_modified: Date;
    created_by: string;
    modified_by: string;
    total: number;
}

export interface AgTxn{
    acct_no: string;
    acct_type: string;
    acct_nm: string;
    dt_fr: Date;
    dt_to: Date;
    total_debit: number;
    total_credit: number;
    begin_bal: number;
    end_bal: number;
    dt_txn: Date;
    dt_posting: Date;
    txn_desc: string;
    txn_ref: string;
    debit: number;
    credit: number;
    source_cd: string;
    teller_id: string;
    brn_chn: string;
    txn_cd: string;
    end_bal2: number;
    virtual_acct: string;
    txn_desc2: string;
    txn_desc3: string;
    txn_desc4: string;
    dt_expiry: Date;
    dt_created: Date;
    dt_modified: Date;
    created_by: string;
    modified_by: string;
    status: string;
    total: number;
}

export interface AgDocStatistic{
    ag_doc_id: number;
    file_nm: string;
    bank_stmt_count: number;
    bank_stmt_trans: number;
    pg_settlement_trans: number;
    total_pg_amt: number;
}