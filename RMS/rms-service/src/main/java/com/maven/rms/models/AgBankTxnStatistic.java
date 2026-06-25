package com.maven.rms.models;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgBankTxnStatistic {
    private Integer ag_doc_id;
    private String file_nm;
    private Integer bank_stmt_count;
    private Integer bank_stmt_trans;
    private Integer pg_settlement_trans;
    private BigDecimal total_pg_amt;
}
