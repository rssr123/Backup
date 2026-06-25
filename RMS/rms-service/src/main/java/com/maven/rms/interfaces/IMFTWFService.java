package com.maven.rms.interfaces;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

import com.maven.rms.models.MFTWF;
import com.maven.rms.models.MFTWFDoc;
import com.maven.rms.models.MFTWFDocRequest;
import com.maven.rms.models.MFTWFHistory;
import com.maven.rms.models.MFTWFHistoryRequest;
import com.maven.rms.models.MFTWFRequest;

public interface IMFTWFService {

    // public Integer sp_uploadDoc(MFTWFDocRequest mftwfDocRequest,String username);

    public Integer sp_uploadDoc(MFTWFDocRequest mftwfDocRequest)  throws SerialException, SQLException;

    Integer sp_updmftwf_status(MFTWFRequest mftwfRequest);

    List<MFTWF> sp_getmftwf(MFTWFRequest mftwfRequest);

    BigInteger sp_insmftwf(MFTWFRequest mftwfRequest);

    List<MFTWFHistory> sp_getmftwfhis(MFTWFHistoryRequest mftwfHistoryRequest);

    List<MFTWFHistory> sp_getwfh_ast(MFTWFHistoryRequest mftwfHistoryRequest);

    Integer sp_updmftwf(MFTWFRequest mftwfRequest);

    // replace by mftwfService.sp_uploadDoc(mftwfDocRequest)
    // Integer sp_insmftwfdoc(BigInteger i_wf_id, String i_file_nm, Blob
    // i_file_content, String i_file_type, Integer i_file_size,
    // String i_created_by, String i_modified_by, String i_status);

    List<MFTWFHistory> sp_getwfh_status(MFTWFHistoryRequest mftwfHistoryRequest);

    List<MFTWFDoc> sp_getmftwfdoc(MFTWFDocRequest mftwfDocRequest);

    String sp_getmftwfdocfilecontent(MFTWFDocRequest mftwfDocRequest) throws SQLException;

    List<MFTWF> sp_checkmftwfexist(MFTWFRequest mftwfRequest);

    Integer sp_removemftwf(BigInteger wf_id);

}
