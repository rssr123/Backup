export interface RefundMyTaskListing {

    rtt_wf_id: number;
    refund_ty: string;
    rtt_app_no: string;
    requested_by: string;
    dt_requested: Date;
    dt_pick: Date;
    rtt_status: string;
    created_by: string;
    approved_by: string;
    task_id: String;
    total: number;

}
