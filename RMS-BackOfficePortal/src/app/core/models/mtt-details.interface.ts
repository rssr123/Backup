export interface MTTDetails {
    ss_cd: String | null;
    orn_no: String | null;
    orn_dt: Date|string;
    cust_ip: String | null;
    cust_nm: String | null;
    cust_addr_1: String | null;
    cust_addr_2: String | null;
    cust_addr_3: String | null;
    cust_postcode: String | null;
    cust_city: String | null;
    cust_state: String | null;
    cust_email: String | null;
    cust_phone: String | null;
    total_amt: String | null;
    order_status: String | null;
}

export interface MTTItem {
    item_desc: String | null;
    item_ref_no: String | null;
    qty: number | null;
    unit_fee: number | null;
    gross_amt: number | null;
    disc_amt: number | null;
    tax_amt: number | null;
    mtt_item_id: number | null;
    net_amt: number | null;
}

export interface MTTItemDetails{
    fee_detail_id: String | null;
    item_ref_no: String | null;
    item_desc: String | null;
    qty: number | null;
    unit_fee: number | null;
    gross_amt: number | null;
    grant_cd: String | null;
    disc_amt: number | null;
    tax_pct: number | null;
    tax_amt: number | null;
    net_amt: number | null;
    entity_type: String | null;
    entity_no: String | null;
    entity_nm: String | null;
    cp_no: String | null;
    cp_tier: number | null;
    cp_tier_amt: number | null;
    cp_tier_discpct: number | null;
    dps_id: String | null;
    dps_task: String | null;
    pymt_case: String | null;
    location: String | null;
    lit_item_ref: String | null;
    txn_type: String | null;
    calendar_yr: String | null;
}

export interface MTTPG {
    pymt_submit_dt: Date | null;
    pg_pymt_id : String | null;
    pg_pymt_amt: number | null;
    pg_txn_status: String | null;
    mtt_pg_id: number | null;
}

export interface MTTPGDetails {
    pymt_submit_dt: Date | null;    
    pg_pymt_method: String | null;
    pg_pymt_id: String | null;
    pg_pymt_desc: String | null;
    pg_pymt_amt: number | null;
    pg_curr_cd: String | null;
    pg_tax_amt: number | null;
    pg_b4tax_amt: number | null;
    pg_txn_id: String | null;
    pg_txn_status: String | null;
}

export interface MTTRCPT {
    rcpt_no: String | null;
    rcpt_dt : Date | null;
    rcpt_reprint: number | null;
    dt_modified: Date | null;
}


export interface MTTRCPTRP {
    rcpt_no: String | null;
    ver_id: String | null;
    ssdocref_id: String | null;
}






