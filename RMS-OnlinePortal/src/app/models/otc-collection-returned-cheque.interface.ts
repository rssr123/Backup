export interface OTCBank {
    param_cd: string;
    nm_en: string;
    nm_bm: string;
    param_grp_nm: string;
    total: number;
}

export interface OTCCheque {
    mtt_id: number;
    otc_id: number;
    che_amt: number;
    che_date: Date;
    che_bank_nm: string;
    che_payer_nm: string;
    che_no: string;
    che_status: string;
    che_ba_acct_no: string;
    che_id: string;
    counter_id: string | null;
    branch_cd: string | null;
    rcpt_no: string | null;
    orn_no: string | null;
    coll_slip_no: string | null;
    total: number;
}

export interface NBLTC {
    bt_cd: string;
    bt_desc: string;
}

export interface NBLTCItem{
    bt_cd: string;
    bt_ty: string;
    bt_desc: string;
    class_id: string;
    ss_cd: string;
    mft_pk: number;
    mft_id: string;
    dps_mft_pk: number;
    dps_mft_id: string;
    fee_detail_pk: number;
    fee_detail_id: string;
    fee_detail_nm_e: string;
    fee_detail_nm_b: string;
    unit_fee: number;
    tax_pct: number;
}