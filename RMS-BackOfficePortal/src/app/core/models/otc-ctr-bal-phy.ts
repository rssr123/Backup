export interface OTCPHYInfo{
    //2025-05-20 New added by Geo
    bal_date: Date;
    branch_code: String;
    
    total_cash_amt: number;       // Corresponds to BigDecimal in Java
    col_slip_no: string;
    orn_no: string;
    che_bank_nm: string;
    che_payer_nm: string;
    che_ba_acct_no: string;
    che_no: string;
    che_date: Date;               // Corresponds to Date in Java
    che_amt: number;              // Corresponds to BigDecimal in Java
    bd_bank_nm: string;
    bd_no: string;
    bd_date: Date;               // Corresponds to Date in Java
    bd_amt: number;              // Corresponds to BigDecimal in Java
    mo_rm_no: string;
    mo_date: Date;               // Corresponds to Date in Java
    mo_payer_nm: string;
    mo_id_no: string;
    mo_contact_no: string;
    mo_amt: number;              // Corresponds to BigDecimal in Java
    detail_type: string;
    n_detail_type: string;
    id: number;
    otc_id: number;

    totalFromEMV: number;
}