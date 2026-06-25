export interface ApiResponse {
  header: ResponseHeader;
  data: MTT;
}

export interface  ResponseHeader {
  requestTimestamp: string;
  responseTimestamp: string;
  statusCode: string;
  message: string;
}

export interface  MTT {
  mtt_id: number;
  orn_no: string;
  cust_nm: string;
  cust_addr_1: string;
  cust_addr_2: string;
  cust_addr_3: string;
  cust_postcode: string;
  cust_city: string;
  cust_state: string;
  total_amt: number;
}

export interface MTTItem {
  mtt_item_id: number;
  line_no: number;
  item_desc: string;
  qty: number;
  unit_fee: number;
  tax_amt: number;
  disc_amt: number;
  gross_amt: number;
}

export interface ApiRequest {
  data: ornDetails;
}

export interface ornDetails{
  ss_cd: string;
  pymt_method: string;
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
  ss_callback_url:string;
  email_flag: number;
  order_status: string;
  payment_item_details:paymentItemDetails[];
}

export interface paymentItemDetails {
  fee_detail_pk: number;
  fee_detail_id: string;
  item_ref_no: string;
  line_no: number;
  item_desc: string;
  qty: number;
  unit_fee: number;
  tax_amt: number;
  disc_amt: number;
  gross_amt: number;
  grant_cd: string;
  tax_pct: number;
  net_amt:number;
  entity_type:string;
  entity_no:string;
  entity_nm:string;
  cp_no:string;
  cp_tier: number,
  cp_tier_amt: number,
  cp_tier_disc_pct: number,
  dps_id: string,
  dps_task: string,
  pymt_case: string,
  location: string,
  lit_item_ref: string,
  txn_type: string,
  calendar_yr: string,
}

export interface EmailExpiryData {
  dt_email_expiry: string;
}
