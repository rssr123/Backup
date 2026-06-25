package com.maven.rms.interfaces;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import com.maven.rms.models.FMSJournal;

public interface IFMSJournalService {
    
    // List<FMSJournal> fms_journal_entry(
    //     String branch_id, String desc, String ledger_id, String module, Date dt_txn, String attr_ext_ref_no, String attr_ext_sys,
    //     String acct1, String branch1, BigDecimal credit1, BigDecimal debit1, String sub_acct1, String txn_desc1,
    //     String acct2, String branch2, BigDecimal credit2, BigDecimal debit2, String sub_acct2, String txn_desc2);
    
    List<FMSJournal> fms_journal_entry(FMSJournal fmsJournal);
}
