package com.maven.rms.interfaces;

import java.math.BigInteger;
import java.sql.Blob;
import java.util.List;

import com.maven.rms.models.MFTRequest;
import com.maven.rms.models.MFTWFDocRequest;
import com.maven.rms.models.MFTWFHistoryRequest;
import com.maven.rms.models.MFTWFRequest;

public interface IMFTWFInterface {

        Integer sp_uploadDoc(MFTWFDocRequest mftwfDocRequest, Blob blob);

        Integer sp_updmftwf_status(MFTWFRequest mftwfRequest);

        List<Object[]> sp_getmftwf(MFTWFRequest mftwfRequest);

        BigInteger sp_insmftwf(MFTWFRequest mftwfRequest);

        List<Object[]> sp_getmftwfhis(MFTWFHistoryRequest mftwfHistoryRequest);

        List<Object[]> sp_getwfh_ast(MFTWFHistoryRequest mftwfHistoryRequest);

        Integer sp_updmftwf(MFTWFRequest mftwfRequest);

        //replace by mftwfService.sp_uploadDoc(mftwfDocRequest)
        // Integer sp_insmftwfdoc(BigInteger i_wf_id, String i_file_nm, Blob i_file_content, String i_file_type, Integer i_file_size,
        // String i_created_by, String i_modified_by, String i_status);

        List<Object[]> sp_getmftwfdoc(MFTWFDocRequest mftwfDocRequest);

        List<Object[]> sp_getwfh_status(MFTWFHistoryRequest mftwfHistoryRequest);

        Blob sp_getmftwfdocfilecontent(MFTWFDocRequest mftwfDocRequest);

        List<Object[]> sp_checkmftwfexist(MFTWFRequest mftwfRequest);
   
        Integer sp_removemftwf(BigInteger wf_id);

}
