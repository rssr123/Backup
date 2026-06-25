package com.maven.rms.interfaces;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import com.maven.rms.models.ParamRequest;
import com.maven.rms.models.RMSUserRequest;

public interface IUserRoleInterface {
    //#region param
        List<Object[]> sp_getparam(ParamRequest paramRequest);
        List<Object[]> sp_getsourcesystem(Integer i_page, Integer i_size, BigInteger i_ss_id, String i_ss_cd, String i_ss_nm,
                                          String i_modified_by, Date i_dt_modified_fr, Date i_dt_modified_to, String i_status);
        //#endregion

        //#region user role
        List<Object[]> sp_getuserbyrole(RMSUserRequest rmsUserRequest);
        Object sp_getuserdetail(RMSUserRequest rmsUserRequest);
        //#endregion
}
