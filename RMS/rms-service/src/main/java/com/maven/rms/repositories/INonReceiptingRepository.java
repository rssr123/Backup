package com.maven.rms.repositories;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import javax.sql.rowset.serial.SerialException;

import com.maven.rms.models.AgBankTxnReq;
import com.maven.rms.models.NonReceiptingAgTxnRequest;
import com.maven.rms.models.NonReceiptingDocRequest;
import com.maven.rms.models.NonReceiptingRequest;

public interface INonReceiptingRepository {
    List<Object[]> sp_getrmsnonreceipting(NonReceiptingRequest request);
    // List<Integer> sp_insagsaledoc(List<NonReceiptingDocRequest> insertRequests) throws SerialException, SQLException;
    Integer sp_insagsaledoc(NonReceiptingDocRequest insertRequest) throws SerialException, SQLException;
    Integer sp_insagbanktxn(AgBankTxnReq request);
    List<Object[]> sp_getagdoc(AgBankTxnReq req);
    Blob sp_getagfilecontent(AgBankTxnReq req);
    List<Object[]> sp_getagbanktxn(NonReceiptingAgTxnRequest request);
    List<Object[]> sp_getagbanktxnpg(NonReceiptingAgTxnRequest request);
    Integer sp_delagdoc(AgBankTxnReq req);
    List<Object[]> sp_getagdocstatistics(NonReceiptingAgTxnRequest request);
    Integer sp_updagsale(NonReceiptingDocRequest req);
    List<Object[]> sp_getfmsnonrmsrecon();
    Integer sp_insfmsnonrmsrecon(AgBankTxnReq request);
    Integer sp_insfmsnonrmsreconcreditdebit(AgBankTxnReq request);
}
