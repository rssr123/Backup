package com.maven.rms.interfaces;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

import com.maven.rms.models.FMS;
import com.maven.rms.models.FMSLedger;
import com.maven.rms.models.FMSLedgerDoc;
import com.maven.rms.models.FMSLedgerDocRequest;
import com.maven.rms.models.FMSLedgerDocWithoutFile;
import com.maven.rms.models.FMSLedgerRequest;
import com.maven.rms.models.FMSRequest;

public interface IFMSService {

    // #region fms start
    // List<FMS> sp_getfms(Integer i_page, Integer i_size, String i_fms_cd,
    // String i_modified_by, Date i_dt_modified_fr, Date i_dt_modified_to, String
    // i_status);
    List<FMS> sp_getfms(FMSRequest fmsRequest);

    // Integer sp_insfms(String i_fms_cd, String i_created_by, String i_status,
    // Integer i_is_active);
    Integer sp_insfms(FMSRequest fmsRequest, String i_created_by, String i_status, Integer i_is_active);

    // Integer sp_updfms(Integer i_fms_id, String i_fms_cd, String i_modified_by,
    // Integer i_is_active);
    Integer sp_updfms(FMSRequest fmsRequest, String i_modified_by);

    // Integer sp_checkfmsbyid(Integer i_fms_id);
    Integer sp_checkfmsbyid(FMSRequest fmsRequest);

    // Integer sp_updfms_activation(Integer i_fms_id, String i_modified_by);
    Integer sp_updfms_activation(FMSRequest fmsRequest, String i_modified_by);

    // #endregion

    // #region fms ledger start
    // List<FMSLedger> sp_getfmsledger_v2(Integer i_page, Integer i_size, Integer
    // i_fms_id, Integer i_file_index, String i_fms_detail_id,
    // String i_fms_detail_nm_en, String i_fms_ledger_cd, String i_found);
    List<FMSLedger> sp_getfmsledger_v2(FMSLedgerRequest fmsLedgerRequest);

    // Integer sp_insfmsledgerdoc(Integer i_fms_id, String i_file_nm, Blob
    // i_file_content, String i_file_type, Integer i_file_size,
    // String i_created_by, String i_modified_by, String i_status);

    // ArrayList<Integer> sp_insfmsledgerdoc(FMSLedgerDocRequest fmsLedgerDocRequest, String username) throws SerialException, SQLException;
    List<Integer> sp_insfmsledgerdoc(FMSLedgerDocRequest fmsLedgerDocRequest, String username)throws SerialException, SQLException;

    // List<FMSLedgerDocWithoutFile> sp_getfmsdoc(Integer i_fms_id);
    List<FMSLedgerDocWithoutFile> sp_getfmsdoc(FMSLedgerDocRequest fmsLedgerDocRequest);

    // Integer sp_checkdocexist(String i_file_nm);
    Integer sp_checkdocexist(FMSLedgerDocRequest fmsLedgerDocRequest);

    // String sp_getfmsfilecontent(String i_file_nm);
    String sp_getfmsfilecontent(FMSLedgerDocRequest fmsLedgerDocRequest);

    ArrayList<Integer> sp_getfmsledgersummarycount(FMSLedgerDocRequest fmsLedgerDocRequest);
}
