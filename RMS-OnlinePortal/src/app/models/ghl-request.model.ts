export interface GHLPayment {
    transaction_type: string;
    pymt_method: string;
    service_id: string;
    pymt_id: string;
    ord_no: string;
    pymt_desc: string;
    return_url: string;
    callback_url: string;
    approved_url: string;
    unapproved_url: string;
    amt: string;
    curr_cd: string;
    cust_ip: string;
    cust_nm: string;
    cust_ph: string;
    cust_email: string;
    hash_value: string;
    page_timeout: number;
    ss_return_url: string;
    order_status: string;
    rcpt_no: string;
    rcpt_dt: string;
}