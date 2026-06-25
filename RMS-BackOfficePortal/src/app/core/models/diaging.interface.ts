export interface DIAging {

    rpt_di_age_id: number;
    p_dt_req: Date;
    p_tmn_status: number;
    p_ent_ty: string;
    p_ent_nm: string;
    p_txn_ty: string;
    p_status: string;
    p_dt_exp_fr: Date;
    p_dt_exp_to: Date;
    p_dt_eff_fr: Date,
    p_dt_eff_to: Date;
    p_dt_app_fr: Date;
    p_dt_app_to: Date;
    p_dt_tmn_fr: Date;
    p_dt_tmn_to: Date;
    dt_created: Date;
    dt_modified: Date;
    created_by: string;
    modified_by: string;
    status: string;
    p_email: string;
    p_file_type: string;
    p_file_size: number
    p_file_nm: string;
    task_id: string;
    total: number;
}