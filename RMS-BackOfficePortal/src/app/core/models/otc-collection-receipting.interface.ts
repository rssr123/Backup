export interface OTCCollectionReceipting {
    mtt_id: number;
    ss_cd: string;
    coll_slip_no: string;
    orn_no: string;
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
    payment_mode: string;
}

export interface OTCCollectionReceiptingPymtItem {
    item_desc: string;
    qty: number;
    unit_fee: number;
    tax_pct: number;
    tax_amt: number;
    grant_cd: string;
    disc_amt: number;
    gross_amt: number;
    net_amt: number;
    total: number
}


export interface OTCCollectionReceiptingCheque {
    otc_body_id: number;
    che_bank_nm: string;
    che_no: string;
    che_date: Date;
    che_ba_acct_no: string;
    che_amt: number;
    che_payer_nm: string;
    che_id: string;
    che_status: string;
    isEditable: boolean;
    isNew: boolean;
}

export interface OTCCollectionReceiptingBankDraft {
    bd_bank_nm: string;
    bd_no: string;
    bd_date: Date;
    bd_amt: number;
    isEditable: boolean;
    isNew: boolean;
}

export interface OTCCollectionReceiptingMoneyOrder {
    mo_rm_no: string;
    mo_payer_nm: string;
    mo_id_no: string;
    mo_contact_no: string;
    mo_amt: number;
    mo_date: Date;
    isEditable: boolean;
    isNew: boolean;
}

export interface OTCPaymentModel{
    payer_email: string;
    pymt_mode: string;
    cash_amt: number;
}

export interface PaymentRequestBody {
    i_mtt_id: number;
    chequeDetails: OTCCollectionReceiptingCheque[];
    bankDraftDetails: OTCCollectionReceiptingBankDraft[];
    moneyOrderDetails: OTCCollectionReceiptingMoneyOrder[];
  }

export interface OTCHist{
    otc_id: number;
    action: string;
    dt_action: Date;
    otc_status: string;
    counter_id: string;
    act_by: string;
    branch: string;
    justification: string;
    remark: string;
    others: string;
    total: number;
}

export interface OTCPaymentDetails{
    otc_body_id: number;
    otc_id: number;
    cash_amt: number;
    che_amt: number;
    cheDate: Date;
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
    dt_created: Date;
    dt_modified: Date;
    created_by: string;
    modified_by: string;
    status: string;
    che_ba_acct_no: string;
    che_id: string;
    total: number;
}

export interface OTCPaymentHeader{
    mtt_id: number;
    emv_sale_id: number;
    otc_counter_id: number;
    payer_email: string;
    otc_pymt_mode: string;
    dt_created: Date;
    dt_modified: Date;
    created_by: string;
    modified_by: string;
    status: string;
    v_reason_cd: string;
}

export interface OTCRcpt {
    otc_rcpt_id: string; // Assuming it's a string based on naming convention
    otc_id: number; // Number type as provided
    rcptNo: string; // Receipt number as string
    rcpt_dt: Date; // ISO Date as string
    rcpt_status: string; // Receipt status as string
    rcpt_reprint: number; // Count as number
    is_uploaded: number; // Status as number
    ver_id: string; // Version ID as string
    ssdocref_id: string; // Document reference ID as string
    dt_created: Date; // ISO DateTime as string
    dt_modified: Date; // ISO DateTime as string
    created_by: string; // Created by as string
    modified_by: string; // Modified by as string
    status: string; // Status as string
    file_nm: string; // File name as string
    remark: string | null; // Remark can be string or null
  }

  export interface OTCEMV {
    resp_cd: string;
    card_no: string;
    dt_expiry: string;
    status_cd: string;
    approval_cd: string;
    rrn: string;
    trans_trace: string;
    batch_no: string;
    host_no: string;
    t_id: string;
    mer_id: string;
    aid: string;
    tc: string;
    cardholder_nm: string;
    card_ty: string;
    prtnr_txn_id: string;
    apay_txn_id: string;
    cust_id: string;
    amt: number; // DECIMAL in SQL corresponds to number in TypeScript
    add_data: string;
    dt_created: Date; // DATETIME corresponds to Date in TypeScript
    dt_modified: Date; // DATETIME corresponds to Date in TypeScript
    created_by: string;
    modified_by: string;
}

  