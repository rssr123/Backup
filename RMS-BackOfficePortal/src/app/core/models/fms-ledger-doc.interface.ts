export interface FmsLedgerDoc{
    file_nm: string;
    // file_content: string;
    file_type: String;
    file_size_kb: number;
    dt_modified: Date | string;
    modified_by: String;
}

export interface FMSLedgerDocResponse{
    file_content: string;
}