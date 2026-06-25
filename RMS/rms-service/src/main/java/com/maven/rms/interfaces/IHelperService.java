package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.BranchCodeList;
import com.maven.rms.models.BranchCodeListRequest;

import com.maven.rms.models.MFT;
import com.maven.rms.models.FeeDetailListRequest;

import com.maven.rms.models.Param;
import com.maven.rms.models.ParamListRequest;

public interface IHelperService {
    List<BranchCodeList> sp_getbcccodes(BranchCodeListRequest getRequest);

    List<MFT> sp_getfeedetailids(FeeDetailListRequest getRequest);

    List<Param> sp_getparamsbygroup(ParamListRequest getRequest);
}
