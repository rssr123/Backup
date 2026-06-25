export interface OTCCtrBalInfo{
    counter_id: string;
    check_in: string;
    user_id: string;
    branch_cd: string;
    orders_paid: Int32Array;
    total: number;
    total_emv: number;
    total_phy: number;
    total_col: number;
    total_che: number;
    total_mo: number;
    total_bd: number;

    status: string;
}