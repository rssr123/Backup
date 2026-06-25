export interface CourtOrder {
    
    cc_case_id: number;
    cc_case_a_id: number;
    cc_cs_item_id: number;
    task_no: string;
    task_status: string;
    pymt_status: string;
    txn_ty: string;
    attr_case_no: string;
    assign_to: string;
    pymt_amt: number;


}

export interface CourtOrderCaseInfo{
    

    cc_case_id: number;
    cc_case_a_id: number;
    cc_cs_item_id: number;
    cust_nm: string;
    cust_email: string;
    cust_phone: string;
    cust_addr_1: string;
    cust_addr_2: string;
    cust_addr_3: string;
    cust_postcode: string;
    cust_city: string;
    cust_state: string;
    attr_case_no: string;
    dt_assigned: Date;
    fms_ari_ref_no: string;
    pymt_status: string;
    txn_ty: string;
    txn_item_ref: string;
    cn_ref_no: string;
    rcpt_no: string;
    pymt_amt: number;
    task_no: string;
    invoice_desc: string;
    task_status: string;


}

export interface CourtOrderPaymentItemInfo{
  

    cc_cs_item_id: number; 
    cc_case_id: number;
    txn_item_ref: string;
    txn_item_desc: string;
    cn_qty: number;
    cn_unit_price: number;
    cn_disc_amt: number;
    cn_amt: number;
    cn_amt_total: number;


}

export interface CourtOrderReminderInfo{
    

    cc_case_id: number;
    reminder_cnt: number;
    reminder_dt: string;
    reminder_received_date: string;
    reminder_email_content: string;

}


export interface CourtOrderDoc{
    cc_doc_id: number;
    cc_case_id: number;
    reminder_cnt: number;
    file_name: string;
    file_type: string;
    file_size_kb: number;
    dt_created: string;
    created_by: string;
    

}

export interface CourtOrderHist{
   

    cc_case_id: number;
    cc_msg_id: number;
    cc_case_a_id: number;
    msg: string;
    msg_type: string;
    dt_created: string;
    dt_modified: string;
    created_by: string;
    modified_by: string;
    pick_up: string;
    assign_to: string;
    task_status: string;

}


