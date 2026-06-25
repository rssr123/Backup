import { Time } from "@angular/common";

export interface bankReconDetail{
    file_nm: string;
    total_no_pg_txn: number;
    total_gross_amt: number;
    total_mdr: number;
    total_net_amt: number;
    total_no_bk_txn: number;
    total_bank_txn: number;
    total_pg_file_txn: number;
    total_pg_disbursed_amt: number;
    task_no: string;
    task_status: string;
    recon_status: string;
    stmt_no: string;
    dt_settlement: Date | null;
    remarks: string;

    // PG details page
    pg_txn_date: Date | null;
    pg_txn_type: string;
    pg_found_in_rms: string;
    pg_txn_id: number;
    pg_txn_code: string;
    pg_sub_criteria: string;
    pg_txn_amt: number;
    pg_mdr: number;
    pg_sst: number;
    pg_net_amt: number;

    // Bank details page
    bank_txn_ref: string;
    bank_acct_no: string;
    bank_brn_chn: string;
    bank_dt_posting: Date | null;
    bank_tm_posting: Date | null;
    bank_credit: number;
}