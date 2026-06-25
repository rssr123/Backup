export interface OTCBIS{
    // General fields
    bankInSlipNo: string;
    branch_cd: string;
    dt_bal: Date;
    completed_by: string;
    dt_completed: Date;
    total: number; 
    gtotal_cash: number; 
    no_che: number;
    gtotal_che: number;
    no_bd: number;
    gtotal_bd: number;
    no_mo: number;
    gtotal_mo: number;

    // Cash fields
    param_cd: string;
    denomination: string;
    quantity: number;
    amount: number;
    total_cash: number;

    // Physical fields
    counter_id: string;
    
    total_cash_amt: number;
    col_slip_no: string;
    orn_no: string;
    che_bank_nm: string;
    che_payer_nm: string;
    che_ba_acct_no: string;
    che_no: string;
    che_date: Date;
    che_amt: number;
    bd_bank_nm: string;
    bd_no: string;
    bd_date: Date;
    bd_amt: number;
    mo_rm_no: string;
    mo_date: Date;
    mo_payer_nm: string;
    mo_id_no: string;
    mo_contact_no: string;
    mo_amt: number;
    detail_type: string;

    // ID fields
    id: BigInt;
    otc_id: BigInt;

    otc_counter_id: BigInt;
}
    