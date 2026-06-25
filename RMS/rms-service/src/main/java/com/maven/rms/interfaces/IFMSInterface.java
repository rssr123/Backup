package com.maven.rms.interfaces;

import java.sql.Blob;
import java.util.Date;
import java.util.List;

import com.maven.rms.models.FMS;
import com.maven.rms.models.FMSLedgerDoc;
import com.maven.rms.models.FMSRequest;
import com.maven.rms.models.FMSLedgerRequest;
import com.maven.rms.models.FMSLedgerDocRequest;

public interface IFMSInterface {

        // #region FMS Start
        // List<Object[]> sp_getfms(Integer i_page, Integer i_size, String
        // i_fms_cd,String i_modified_by, Date i_dt_modified_fr, Date i_dt_modified_to,
        // String i_status);
        // List<Integer> sp_uploadDoc(FMSLedgerDocRequest fmsLedgerDocRequest, Blob blob, String username, FMSLedgerDoc fmsDoc);
        List<Integer> sp_uploadDoc(FMSLedgerDocRequest fmsLedgerDocRequest, Blob blob, String username, List<FMSLedgerDoc> fmsDocList);
        List<Object[]> sp_getfms(FMSRequest fmsRequest);

        // Integer sp_insfms(String i_fms_cd, String i_created_by, String i_status,
        // Integer i_is_active);
        Integer sp_insfms(FMSRequest insertRequest, String i_created_by, String i_status, Integer i_is_active);

        // Integer sp_updfms(Integer i_fms_id, String i_fms_cd, String i_modified_by,
        // Integer i_is_active);
        Integer sp_updfms(FMSRequest updateRequest, String i_modified_by);

        // Integer sp_checkfmsbyid(Integer i_fms_id);
        Integer sp_checkfmsbyid(FMSRequest fmsRequest);

        // Integer sp_updfms_activation(Integer i_fms_id, String i_modified_by);
        Integer sp_updfms_activation(FMSRequest fmsRequest, String i_modified_by);

        // #endregion
        // #region FMS Ledger Start
        // List<Object[]> sp_getfmsledger_v2(Integer i_page, Integer i_size, Integer
        // i_fms_id, Integer i_file_index, String i_fms_detail_cd, String
        // i_fms_detail_nm_en, String i_fms_ledger_cd, String i_found);
        List<Object[]> sp_getfmsledger_v2(FMSLedgerRequest fmsLedgerRequest);

        // List<Object[]> sp_getfmsdoc(Integer i_fms_id);
        List<Object[]> sp_getfmsdoc(FMSLedgerDocRequest fmsLedgerDocRequest);

        // Integer sp_insfmsledgerdoc(Integer i_fms_id, String i_file_nm, Blob
        // i_file_content, String i_file_type, Integer i_file_size,
        // String i_created_by, String i_modified_by, String i_status);

        // Integer sp_insfmsledgerdoc(FMSLedgerDocRequest fmsLedgerDocRequest,String
        // username);
        // Integer sp_checkdocexist(String i_file_nm);
        Integer sp_checkdocexist(FMSLedgerDocRequest fmsLedgerDocRequest);

        // Blob sp_getfmsfilecontent(String i_file_nm);
        Blob sp_getfmsfilecontent(FMSLedgerDocRequest fmsLedgerDocRequest);

        List<Integer> sp_getfmsledgersummarycount(FMSLedgerDocRequest fmsLedgerDocRequest);

}
