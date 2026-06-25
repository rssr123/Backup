package com.maven.rms.interfaces;

import java.math.BigInteger;
import java.sql.Blob;
import java.util.List;

import com.maven.rms.models.BankReconSch;

public interface IBankReconSchInterface {

    // Get List
    List<Object[]> sp_getBankDoc();

    // Blob sp_getrcbankdoc(BigInteger i_rc_bankdoc_id);
    Blob sp_getrcbankdoc(BigInteger i_rc_bankdoc_id);

    // BigInteger sp_insrcbankdoc(BigInteger i_rc_bank_id, BigInteger
    // i_rc_bankdoc_id, BankReconSch account);
    BigInteger sp_insrcbankdoc(BankReconSch account, BigInteger i_rc_bank_id, BigInteger i_rc_bankdoc_id);

    Integer sp_updrcbanktxn();

    // Upd List
    // BigInteger sp_updrcbank(BigInteger i_rc_bank_id);
    BigInteger sp_updrcbank(BankReconSch account);

}
