export interface RICPAging {

    rpt_ricp_age_id: number;
    p_dt_req: Date;
    p_exp_status: number;
    p_can_v_status: number;
    p_ent_ty: string;
    p_ent_nm: string;
    p_dt_iss_fr: Date;
    p_dt_iss_to: Date;
    p_dt_exp_fr: Date;
    p_dt_exp_to: Date;
    p_dt_wo_fr: Date;
    p_dt_wo_to: Date;
    p_dt_can_fr: Date;
    p_dt_can_to: Date;
    p_dt_void_fr: Date;
    p_dt_void_to: Date;
    dt_created: Date;
    dt_modified: Date;
    created_by: string;
    modified_by: string;
    status: string;
    p_email: string;
    p_dt_rcpt_fr: Date;
    p_dt_rcpt_to: Date;
    p_file_type: string;
    p_file_size: number;
    p_file_nm: string;
    p_batch_no: string;
    p_fms_ref_no: string;
    task_id: string;
}