export interface FPAScheduler{
    row_number:number;
    job_name:string;
    last_attempt_date:Date;
    resp_status:string;
    next_attempt_time:Date;
    total:number;
}