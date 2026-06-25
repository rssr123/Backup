export interface ServiceProvider {
    ag_bil: number;
    // entity_nm: string;
    profile_nm: string;
    ag_bil_no: string;
    cust_email: string;
    total_amt_payable: number;
    date_collection: Date;
    pymt_status: string;
    order_status: string;
    dt_pymt: Date;
    date_email_sent: Date;


}

export interface ServiceProviderMaintenance {


    // agPfId: number;
    // profileNm: string;
    // custNm: string;
    // custAddr1: string;
    // custAddr2: string;
    // custAddr3: string;
    // custPostcode: string;
    // custCity: string;
    // custState: string;
    // custEmail: string;
    // custPhone: string;
    // feeDetailId: string;
    // entityType: string;
    // entityNo: string;
    // entityNm: string;
    // status: string;
    // total: number;

    ag_pf_id: number;
    profile_nm: string;
    cust_nm: string;
    cust_addr1: string;
    cust_addr2: string;
    cust_addr3: string;
    cust_postcode: string;
    cust_city: string;
    cust_state: string;
    cust_email: string;
    cust_phone: string;
    fee_detail_id: string;
    entity_type: string;
    entity_no: string;
    entity_nm: string;
    status: string;
    total: number;

    
    
    
    status_en: string;
    status_bm: string;

    isNew?: boolean;
    isEditable?: boolean;

}
