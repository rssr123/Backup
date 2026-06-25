package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.FeeGrpRequest;

public interface IFeeGroupInterface {

    List<Object[]> sp_getfeegroup_v2(FeeGrpRequest feeGroupRequest);

    Integer sp_insfeegroup(FeeGrpRequest feeGroupRequest,
            String i_created_by, String i_modified_by, String i_status);

    Integer sp_updfeegroup(FeeGrpRequest feeGroupRequest, String i_fee_grp_nm_en, String i_fee_grp_nm_bm,
            String i_modified_by, String i_status);

    Integer sp_checkfeegrpbyid(FeeGrpRequest feeGroupRequest);

}
