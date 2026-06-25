export interface BillingTypeCode{
   
    bltc_id: number;
    bt_cd: string;
    bt_ty: string;
    bt_desc: string;
    class_id: string;
    ss_cd: string;
    bltc_item_id: number;
    mft_pk: number;
    mft_id: string;
    dps_mft_pk: number;
    dps_mft_id: string;
    fee_detail_nm_e: string;
    unit_fee: number;
    tax_cd_id: number;
    tax_pct: number;
    ss_nm: string;
    total: number;

}

export interface BillingIssuanceBySSBillingItemDetails{
   
    // bil_id: number;
    mft_pk: number;
    unit_fee: number;
    qty: number;
    tax_pct: number;
    tax_amt: number;
    final_amt: number;
    status: string;
    // bil_wf_id: number;
}

export interface BillingIssuanceBySSBillingChildDetails{
   
    // bil_id: number;
    bil_child_date: Date;
    bil_child_status: string;
    status: string;
    bil_wf_id: number | null;
    bil_no: string | null;
    bil_status: string | null;
}


// export interface BillingIssuanceBySSBillingItemsDets{
   

//     bltc_item_id: number;
//     mft_pk: number;
//     mft_id: string;
//     dps_mft_pk: number;
//     dps_mft_id: string;
//     fee_detail_nm_e: string;
//     tax_cd_id: number;
//     tax_pct: number;
//     total: number;

// }


export interface BillingIssuanceBySSBillingIssuance{
   
    bil_date: Date;
    bil_no: string;
    remarks: string;
    total: number;


}

export interface ornDetails{
    ss_cd: string;
    orn_no:string;
    orn_dt: Date;
    cust_nm: string;
    cust_addr_1: string;
    cust_addr_2: string;
    cust_addr_3: string;
    cust_postcode: string;
    cust_city: string;
    cust_state: string;
    cust_email:string;
    cust_phone:string;
    total_amt: number;
    ss_return_url:string;
    payment_item_details:paymentItemDetails[];
  }
  
  export interface paymentItemDetails {
    fee_detail_id: string;
    item_ref_no: string;
    line_no: number;
    item_desc: string;
    qty: number;
    unit_fee: number;
    tax_amt: number;
    disc_amt: number;
    gross_amt: number;
    grant_cd: string | null;
    tax_pct: number;
    net_amt:number;
    entity_type:string;
    entity_no:string;
    entity_nm:string;
  }


  export interface BillingIssuanceBySSListing{
   
    cust_id: string;
    ent_nm: string;
    ent_no: string;
    billing_no: string;
    bil_id: number;
    amount: number;
    billing_method: string;
    bil_wf_status: string;
    rcpt_no: string;
    req_name: string;
    bil_child_status: string;
    total: number;

}


export interface BillingIssuanceBySSBillingDetails{

    req_name: string;
    req_email: string;
    ss_cd: string;
    billing_no: string;
    billing_desc: string;
    action: string;
    dps_amt: number;
    billing_cnt: number;
    billing_freq: string;
    loa_id: string;
    dt_loa_start: Date;
    dt_loa_end: Date;
    agm_id: string;
    dt_agm_start: Date;
    dt_agm_end: Date;
    bil_wf_status: string;
    pickup_by: string;
    dt_pick: Date;
    cust_id: string;
    cust_nm: string;
    cust_email: string;
    cust_phone: string;
    cust_addr1: string;
    cust_addr2: string;
    cust_addr3: string;
    cust_postcode: string;
    cust_city: string;
    cust_state: string;
    ent_nm: string;
    ent_no: string;
    ent_ty: string;

}

export interface BillingIssuanceBySSListofBilItems{

    mft_pk: number;
    unit_fee: number;
    qty: number;
    tax_pct: number;
    tax_amt: number;
    final_amt: number;
    fee_detail_id: string;
    fee_detail_nm_e: string;
    total: number;

}

export interface BillingIssuanceBySSListOfIssuance{

    bil_child_date: Date;
    bil_child_status: string;
    bil_wf_id: number;
    bil_no: string;
    bil_status: string;
    total: number;

}

export interface BillingIssuanceBySBillingDoc{

    bil_wf_id: number;
    bil_id: number;
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

export interface BillingIssuanceBySSHistory{

    action: string;
    bil_wf_status: string;
    pickup_by: string;
    dt_pick: Date;
    dt_created: Date;
    dt_modified: Date;
    created_by: string;
    modified_by: string;
    msg: string;
    total: number;

}

export interface BillingClassId{
    
    blcm_id: number;
    classId: string;
    classDesc: string;
    dtCreated: Date;
    dtModified: Date;
    createdBy: string;
    modifiedBy: string;
    status: string;
    total: number;
}







