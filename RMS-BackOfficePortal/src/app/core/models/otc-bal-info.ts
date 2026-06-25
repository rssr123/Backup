export interface OTCBalInfo{
    branch_cd: string;
    bal_type: string;
    no_of_counters: number | 0;
    no_of_txn: number | 0;
    total: number | 0;
    total_emv: number | 0;
    total_phy: number | 0;
    total_cash: number | 0;
    total_che: number | 0;
    total_bd: number | 0;
    total_mo: number | 0;
    no_of_rcpt_can: number | 0;
    bal_date: Date;

    status: string | 'N/A';
}