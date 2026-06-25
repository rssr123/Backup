export interface MyTasksPublicTasks {

    task_id: String;
    task_desc: String;
    requested_by: String;
    dt_requested: Date;
    pickup_by: String;
    dt_pick: Date;
    task_status: String;
    origin_table: String;
    pk: number;
    total: number
    isSelected?: boolean;
    
}