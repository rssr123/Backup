export interface OTCMasterBal{
    branch_code: string;
    bal_date: Date;

    //Validation
    result: number;

    //Listing
    daily_bal_status: string;
    no_of_counters: number;
    user_id: string;
    check_in: string;
    total_amt: number;
    total: number;

}