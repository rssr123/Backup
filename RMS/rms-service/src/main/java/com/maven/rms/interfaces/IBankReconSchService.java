package com.maven.rms.interfaces;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;

import com.maven.rms.models.BankReconSch;

public interface IBankReconSchService {
        // Scheduler
        int sp_insrcbanktxn() throws SQLException;

        // Get List
        List<Object[]> sp_getBankReconTxn();

        // Upd List
        // BigInteger sp_updRcBank(BankReconSch account);
        BigInteger sp_updRcBank(BankReconSch account);
}
