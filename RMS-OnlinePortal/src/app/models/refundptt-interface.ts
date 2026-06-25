export interface RefundPTT {
    orn_no: string;
    orn_dt: Date | string;
    total_amt: number;
    order_status: string;
    rcpt_no: string;
    total: number;
    mtt_id: number;
    ent_no: string;
    ent_nm: string;
    txn_id: string;
    refund_slip_no: string;
    rms_type: string;
    rtt_app_no: string|null;
    date_expiry: Date|null;
    rtt_status: string|null;
}
export interface RefundPTTOrderDetails {
    mtt_id: number;
    rms_type: string;
    ss_cd: string;
    orn_no: string;
    txn_id: string;
    ent_nm: string;
    ent_type: string;
    ent_no: string;
    cust_nm: string;
    cust_phone: string;
    cust_email: string;
    cust_addr_1: string;
    cust_addr_2: string;
    cust_addr_3: string;
    cust_postcode: string;
    cust_city: string;
    cust_state: string;
    total_amt: number;
    order_status: string;
    total: number;
}

export interface RefundPTTPaymentItemDetails {
    item_desc: string;
    qty: number;
    unit_fee: number;
    tax_pct: number;
    tax_amt: number;
    grant_cd: string;
    disc_amt: number;
    gross_amt: number;
    refund_amt: number;
    total: number;
    isSelected?: boolean;
    mtt_item_id: number;
    item_ref_no: string;
    net_amt: number;
    entity_type: string;
    entity_no: string;
    entity_nm: string;
}

export interface RefundPTTOnlinePaymentInfos {
    rms_type: string;
    cust_email: string;
    pg_payment_id: string;
    pg_payment_status: string;
    pg_payment_date: Date;
    pg_payment_amt: number;
}

export interface PGRcpt {
    otc_rcpt_id: string | null; // Allow null
    otc_id: number | null; // Allow null
    rcptNo: string; // Non-nullable, as it seems required
    rcpt_dt: Date | null; // Allow string or null for date
    rcpt_status: string | null; // Allow null
    rcpt_reprint: number | null; // Allow null
    is_uploaded: number | null; // Allow null
    ver_id: string | null; // Allow null
    ssdocref_id: string | null; // Allow null
    dt_created: Date | string | null; // Allow null
    dt_modified: Date | string | null; // Allow null
    created_by: string | null; // Allow null
    modified_by: string | null; // Allow null
    status: string | null; // Allow null
    file_nm: string | null; // Allow null
    remark: string | null; // Allow null
}

export interface RefundInfo{
    rtt_id: number|null;
    refund_slip_no: string|null;
    requested_by: string|null;
    dt_process: Date| null;
    appeal_cnt: number|null;
    rtt_status: string|null;
}

export interface RefundHist{
    rtt_wf_hist_id: number|null;
    action: string|null;
    rtt_status: string|null;
    dt_action: Date|null; 
    requested_by: string|null;
    pickup_by: string|null;
    msg: string|null;
    total: number|null;
    assign_to: string|null;
    modified_by: string|null;
    modified_by_nm: string|null;

}
export interface RefundWFList{
    rcpt_no: string|null;
    rcpt_dt: Date|null;
    orn_no: string|null;
    txn_id: string|null;
    refund_amt: number|null;
    ent_no: string|null;
    ent_nm: string|null;
    cust_email: string|null;
    sme_email: string|null;
    requested_by: string|null;
    created_by: string|null;
    modified_by: string|null;
    msg: string|null;
}

export interface RefundForm {
    identityType: string;
    identityNumber: string;
    bankAccountNo: string;
    bankAccountType: string;
    bankAccountName: string;
    bankHolderName: string;
    billingAddress1: string;
    billingAddress2: string;
    billingAddress3: string;
    custCity: string;
    custPostcode: string;
    custState: string;
    recEmail: string;
    custNm: string;
    custEmail: string;
    custPhone: string;
    rcptNo: string;
    rcptAmt: number;
    ornNo: string;
    txnId: string;
    entityNm: string;
    entityTy: string;
    entityNo: string;
}

