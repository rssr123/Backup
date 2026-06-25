export interface OTCReceiptCancellationListing {

    mtt_id: number
    rcpt_no: string;
    orn_no: string;
    cust_nm: string;
    otc_pymt_mode: string;
    total_amt: number;
    otc_counter_id: number
    counter_id: string;
    branch_cd: string;
    nm_en: string;
    total: number;

}

export interface OTCReceiptCancellationOrderInfoDetails {

    mtt_id: number
    ss_cd: string;
    orn_no: string;
    cust_nm: string;
    cust_addr_1: string;
    cust_addr_2: string;
    cust_addr_3: string;
    cust_postcode: string;
    cust_city: string;
    cust_state: string;
    cust_email: string;
    cust_phone: string;
    total_amt: number;
    order_status: string;
    coll_slip_no: string;

}

export interface OTCReceiptCancellationPaymentItemsDetails{
    item_desc: string;
    qty: number;
    unit_fee: number;
    tax_pct: number;
    tax_amt: number;
    grant_cd: string;
    disc_amt: number;
    gross_amt: number;
    net_amt: number;
    total: number;
   
}

export interface OTCReceiptCancellationPaymentInfoDetails{

    mtt_id: number;
    orn_no: string;
    coll_slip_no: string;
    payer_email: string;
    otc_pymt_mode: string;
    otc_body_id: number;
    cash_amt: number;
    che_amt: number;
    che_date: Date;
    che_bank_nm: string;
    che_payer_nm: string;
    che_no: string;
    che_status: string;
    mo_amt: number;
    mo_rm_no: string;
    mo_date: Date;
    mo_payer_nm: string;
    mo_id_no: string;
    mo_contact_no: string;
    bd_amt: number;
    bd_no: string;
    bd_date: Date;
    bd_bank_nm: string;
    che_ba_acct_no: string;
    che_id: string;
    trans_trace: string;
    batch_no: string;
    host_no: string;
    t_id: string;
    amt: number;
    total: number;
}

export interface OTCReceiptCancellationRecepitInfoDetails{

    mtt_id: number;
    otc_id: number;
    rcpt_no: string;
    rcpt_dt: Date;
    rcpt_status: string;
    rcpt_reprint: number;
    ver_id: string;
    ssdocref_id: string;
    file_nm: string;
    remark: string;
    orn_no: string;
    file_content: string;
    file_type: string;
    idaman_file_name: string;
    total: number;

}


export interface OTCReceiptCancellationHistoryDetails{

    mtt_id: number;
    otc_id: number;
    action: string;
    dt_action: Date;
    otc_status: string;
    counter_id: string;
    act_by: string;
    nm_en: string;
    total: number;

}


export interface OTCReceiptCancellationBalStatusDetails{

    allow_cancel: number;
    error_msg: string;
}

export interface OTCReceiptCancellatioRCStatusDetails{

    count: number;
    rc_status: string;
}

export interface OTCReceiptCancellationMyTaskListing{

    otc_rc_id: number;
    otc_id: number;
    justication: string;
    rc_type: number;
    rc_status: string;
    task_id: string;
    counter_id: string;
    requested_by: string;
    requested_by_nm: string;
    date_requested: Date;
    approved_by: string;
    approved_by_nm: string;
    dt_approved: Date;
    dt_created: Date;
    dt_modified: Date;
    created_by: string;
    created_by_nm: string;
    modified_by: string;
    modified_by_nm: string;
    assigned_to: string;
    assigned_to_nm: string;
    mtt_id: number;
    otc_pymt_mode: string;
    task_description: string;
    status: string;
    total: number;
    
}


export interface OTCReceiptCancellationTaskAndReqInfoApproval{

    otc_rc_id: number;
    otc_id: number;
    justication: string;
    others: string;
    rc_type: number;
    rc_status: string;
    task_id: string;
    date_assigned: Date;
    counter_id: string;
    requested_by: string;
    requested_by_nm: string;
    requester_id: string;
    date_requested: Date;
    approved_by: string;
    approved_by_nm: string;
    approver_id: string;
    dt_approved: Date;
    dt_created: Date;
    dt_modified: Date;
    created_by: string;
    created_by_nm: string;
    modified_by: string;
    modified_by_nm: string;
    mtt_id: number;
    otc_counter_id: number;
    nm_en: string;

}

export interface OTCReceiptCancellationHistoryDetailsAudit{

    mtt_id: number;
    otc_id: number;
    action: string;
    dt_action: Date;
    bcm_desc: string;
    counter_id: string;
    hist_status: string;
    status: string;
    justification: string;
    rc_type: number;
    others: string;
    remark: string;
    performed_by: string;
    performed_by_nm: string;
    assigned_to: string;
    assigned_to_nm: string;
    total: number;
}


export interface OTCReceiptCancellationSupervisor{

    ssm4uuserrefno: string;
    nm: string;
    email: string;
    role_branch_id: number;
    bcm_desc: string;
    role_id: number;
    role_nm_en: string;

}











