package com.maven.rms.interfaces;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.maven.rms.models.BankDocRequest;
import com.maven.rms.models.BankReconRequest;
import com.maven.rms.models.BankReconResponse;

public interface IBankReconService {
    public List<BankReconResponse> sp_getBankReconTaskList(BankReconRequest BankRequest);

    public List<String> sp_getPGSettlementDateTaskList();

    public Integer sp_uploadDoc(BankDocRequest bankDocRequest,String username) throws SQLException, IllegalArgumentException, IOException;
    
    public Integer sp_checkbktask(BankReconRequest BankRequest);
}
