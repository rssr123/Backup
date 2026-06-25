export interface OTCBalSettlementEMV{
  docID: number;
  fileNm: string;           // nvarchar(255) as file_nm
  terminalId: string;       // nvarchar(50) as terminal_id
  dtSettlement: Date;       // date as dt_settlement
  batchNo: string;          // nvarchar(6) as batch_no
  batchCount: string;       // nvarchar(3) as batch_count
  batchAmt: string;         // nvarchar(12) as batch_amt
  total: number;            // int as total
}