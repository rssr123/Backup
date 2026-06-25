export interface OTCBalRC{
    coll_slip_no: string;
    orn_no: string;
    rcpt_no: string;
    totalAmount: number;
    otc_pymt_mode: string;
    requested_by: string;
    approved_by: string;
    remark: string;
    mtt_id: number;
    otc_id: number;
    otc_counter_id: number;
    counter_id: string;
}