export interface RIPLAging {

    rpt_ripl_age_id: number;
    p_dt_req: Date;
    p_imp_status: number;
    p_exp_status: number;
    p_ent_ty: string;
    p_ent_nm: string;
    p_dt_due_fr: Date;
    p_dt_due_to: Date;
    p_dt_rcpt_fr: Date;
    p_dt_rcpt_to: Date;
    p_dt_imp_fr: Date;
    p_dt_imp_to: Date;
    p_dt_wo_fr: Date;
    p_dt_wo_to: Date;
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