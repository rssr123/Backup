export interface DeferredIncome {
    dt_termination: Date | null;
    di_id: number;
    fee_detail_pk: string;
    entity_type: string;
    entity_no: string;
    fee_detail_id: string;
    txn_type: string;
    dt_effective: Date;
    dt_expiry: Date;
    approval_status: string;
    dt_approval: Date;
    item_ref_no: string;
    di_status: string;
    status_nm_en: string;
}