export interface OTCDailyBal{
    branch_code: string;
    bal_date: Date;

    //Validation
    result: number;
    bal_status: string;
    bal_type: string;

    //Listing
    otc_counter_id: number;
    counter_id: string;
    user_id: string;
    counter_bal_status: string;
    check_in: string;
    check_out: string;
    total_amt: number;
    total: number;

}