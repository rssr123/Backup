export interface ReprintReceipt{
    mtt_id:string;
    rcpt_no:string;
    orn_no:string;
    counter_id:string;
    branch_cd:string;
    otc_rcpt_id:number;
    otc_pymt_mode:string;
    total_amt:number;
    total:number;

}


export interface RROrderInfo{

    mtt_id:string;
    ss_cd:string;
    order_status:string;
    coll_slip_no:string;
    orn_no:string;
    cust_nm:string;
    cust_phone:string;
    cust_email:string;
    cust_addr_1:string;
    cust_addr_2:string;
    cust_addr_3:string;
    cust_postcode:string;
    cust_city:string;
    cust_state:string;
    
}


export interface RRPaymentItems{

    mtt_id:string;
    item_desc:string;
    qty:number;
    net_amt:number;
    tax_amt:number;
    grant_cd:string;
    disc_amt:number;
    gross_amt:number;
    gross_amt_total:number;

}


export interface RRPaymentInfo{

    mtt_id:string;
    otc_id:string;
    payer_email:string;
    otc_pymt_mode:string;
    cash_amt:number;
    cash_amt_total:number;
    otc_che_id:string;
    che_status:string;
    che_bank_nm:string;
    che_no:string;
    che_date:string;
    che_ba_acct_no:string;
    che_amt:number;
    che_amt_total:number;

}

export interface RRPaymentInfoV2{
 

    mtt_id:string;
    orn_no:string;
    coll_slip_no:string;
    payer_email:string;
    otc_pymt_mode:string;
    otc_body_id:number;
    cash_amt:number;
    che_amt:number;
    che_date:string;
    che_bank_nm:string;
    che_payer_nm:string;
    che_no:string;
    che_status:string;
    mo_amt:number;
    mo_rm_no:string;
    mo_date:string;
    mo_payer_nm:string;
    mo_id_no:string;
    mo_contact_no:string;
    bd_amt:number;
    bd_no:string;
    bd_date:string;
    bd_bank_nm:string;
    che_ba_acct_no:string;
    che_id:string;
    trans_trace:string;
    batch_no:string;
    host_no:string;
    t_id:string;
    amt:number;
    total:number;
}


export interface RRReceiptInfo{



    mtt_id:string;
    rcpt_no:string;
    file_nm:string;
    rcpt_dt:string;
    rcpt_status:string;
    rcpt_reprint:number;
    ssdocref_id: string;
    ver_id_otc: string;
    ver_id_mtt: string;
    ver_id: string;
    orn_no: string;
    file_content: string;
    file_type: string;
    idaman_file_name: string;

    


}

export interface RRHistoryTable{




    mtt_id:string;
    action:string;
    dt_action:string;
    otc_status:string;
    counter_id:string;
    act_by:string;
    status:string;

}

export interface RRJustification{
    
    otc_id:string;
    otc_rc_rp_id:string;
    otc_rcpt_id:string;
    justification:string;
    dt_created:string;
    dt_modified:string;
    created_by:string;
    modified_by:string;
    status:string;
    ssdocref_id:string;
    ver_id:string;
    rcpt_no:string;
    file_content: string;
    file_type: string;
    idaman_file_name: string;

}

export interface RRHistoryTable_v2{

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
