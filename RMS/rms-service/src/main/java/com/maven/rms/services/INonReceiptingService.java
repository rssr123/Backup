package com.maven.rms.services;

import java.sql.SQLException;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

import com.maven.rms.models.AgBankTxnModel;
import com.maven.rms.models.AgBankTxnReq;
import com.maven.rms.models.AgBankTxnStatistic;
import com.maven.rms.models.AgDoc;
import com.maven.rms.models.NonReceipting;
import com.maven.rms.models.NonReceiptingAgTxnRequest;
import com.maven.rms.models.NonReceiptingDocRequest;
import com.maven.rms.models.NonReceiptingRequest;

public interface INonReceiptingService {
    List<NonReceipting> sp_getrmsnonreceipting(NonReceiptingRequest request);
    // List<Integer> sp_insagsaledoc(List<NonReceiptingDocRequest> insertRequests) throws SerialException, SQLException;
    Integer sp_insagsaledoc(NonReceiptingDocRequest insertRequests) throws SerialException, SQLException;
    Integer sp_insagbanktxn(AgBankTxnReq req);
    List<AgDoc> sp_getagdoc(AgBankTxnReq req);
    String sp_getagfilecontent(AgBankTxnReq req);
    List<AgBankTxnModel> sp_getagbanktxn(NonReceiptingAgTxnRequest req);
    List<AgBankTxnModel> sp_getagbanktxnpg(NonReceiptingAgTxnRequest req);
    Integer sp_delagdoc(AgBankTxnReq req);
    List<AgBankTxnStatistic> sp_getagdocstatistics(NonReceiptingAgTxnRequest req);
    Integer sp_updagsale(NonReceiptingDocRequest req);
    List<NonReceipting> sp_getfmsnonrmsrecon();
    Integer sp_insfmsnonrmsrecon(AgBankTxnReq req);
    Integer sp_insfmsnonrmsreconcreditdebit(AgBankTxnReq req);
}
