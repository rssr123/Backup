export interface RefundApprovalTaskInfo {
    task_id: string; // Task ID
    rtt_wf_id: number; // RTT Workflow ID
    refund_ty: string; // Refund Type
    orn_no: string; // ORN Number
    rtt_status: string; // RTT Status
    rtt_app_no: string; // RTT Application Number
    dt_created: Date; // Date Created
    ss_cd: string; // SS Code
    rms_tpye: string; // RMS Type
    requested_by: string; // Requested By
    dt_requested: Date; // Date Requested
    date_pick: Date; // Date Pick
    msg: string; // Message
    dt_approved: Date; // Date Approved
    pickup_by: string; // Picked Up By
    approved_by: string; // Approved By
    mtt_id: number; // MTT ID
    refund_cd: string; // Refund Code
    status_param_nm: string; // Status Parameter Name
}

export interface RefundRTTItems {
    item_desc: string;
    qty: number;
    unit_fee: number;
    tax_pct: number;
    tax_amt: number;
    grant_cd: string;
    disc_amt: number;
    refund_amt: number;
    total_refund_amt: number;
    isSelected?: boolean;
    rtt_item_id: number;
    item_ref_no: string;
    entity_no: string;
    net_amt: number;
    gross_amt: number;
}


