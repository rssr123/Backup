export interface OTCEMVReconciliation {
    branch_cd: string;
    bal_status: string;
    emv_settlement_count: number;
    emv_amt: number;
    total: number;
}

export interface OTCEMVReconciliationCheck {
    flag: number;
}

export interface OTCEMVReconciliationStatus {
    rc_emv_id: number;
    dt_balancing: Date;
    dt_created: Date;
    dt_modified: Date;
    created_by: string;
    modified_by: string;
    status: string;
    rc_emv_status: string;
}

export interface OTCEMVReconciliationSummary {
    branch_count: number;
    date_period: Date;
    emv_settlement_count: number;
    emv_transaction_count: number;
    emv_amt: number;
    receipts_cancelled_count: number;
}

export interface OTCEMVReconciliationRC {
    branch_cd: string;
    coll_slip_no: string;
    orn_no: string;
    rcpt_no: string;
    amount: number;
    payment_mode: string;
    requested_by: string;
    approved_by: string;
    reason: string;
    mtt_id: number;
    otc_id: number;
    otc_counter_id: number;
    counter_id: string;
    otc_pymt_mode: string;
}

export interface OTCEMVReconciliationSettlement {
    branch_cd: string;
    file_nm: string;
    terminal_id: string;
    date: Date;
    batch_no: string;
    batch_count: string;
    batch_amt: number;
    otc_bal_doc_id: number;
}

export interface OTCEMVReconciliationSettlement2 {
    file: File;
    file_nm: string;
    debit_card_lines: number;
    debit_card_amount: number;
    credit_card_lines: number;
    credit_card_amount: number;
    total_amt: number;
}

export interface OTCEMVReconciliationSettlement3 {
    rc_emv_doc_id: number;
    dt_balancing: Date;
    file_nm: string;
    file_type: string;
    file_size: number;
    dr_count: number;
    dr_amt: number;
    cr_count: number;
    cr_amt: number;
    total: number;
    dt_created: Date;
    dt_modified: Date;
    created_by: string;
    modified_by: string;
    status: string;
    rc_emv_id: number;
}
