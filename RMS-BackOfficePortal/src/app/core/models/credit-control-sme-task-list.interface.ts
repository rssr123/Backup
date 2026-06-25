export interface CCTaskList {
    cc_case_id: number;
    task_id: string;
    task_status: string;
    assign_to: string;
    pick_up: string;
    pymt_status: string;
    txn_ty: string;
    attr_case_no: string;
    reminder_cnt: number;
    reminder_dt: Date;
    txn_total_amt: number;
    cust_nm: string;
    total: number;
}