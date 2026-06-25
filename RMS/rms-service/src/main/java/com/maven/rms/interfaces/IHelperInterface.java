package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.BranchCodeListRequest;
import com.maven.rms.models.FeeDetailListRequest;
import com.maven.rms.models.ParamListRequest;

public interface IHelperInterface {
    List<Object[]> sp_getbcccodes(BranchCodeListRequest getRequest);

    List<Object[]> sp_getfeedetailids(FeeDetailListRequest getRequest);

    List<Object[]> sp_getparamsbygroup(ParamListRequest getRequest);
}
