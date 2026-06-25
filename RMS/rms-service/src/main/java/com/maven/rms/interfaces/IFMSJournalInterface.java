package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.FMSJournal;

public interface IFMSJournalInterface {

    List<Object[]> sp_getfmsjn();

    // Integer sp_updfmsjn(String i_attr_ext_ref_no, String i_resp_attr_ext_sys, String i_fms_ref_no, 
    //                     String i_resp_ext_ref_no, String i_resp_status, String i_resp_msg, String i_resp_dt);

    Integer sp_updfmsjn(FMSJournal fmsJournal);
}
