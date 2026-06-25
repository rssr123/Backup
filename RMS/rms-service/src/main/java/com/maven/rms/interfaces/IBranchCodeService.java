package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.BranchCode;
import com.maven.rms.models.BranchCodeAddRequest;
import com.maven.rms.models.BranchCodeDeleteRequest;
import com.maven.rms.models.BranchCodeRequest;
import com.maven.rms.models.BranchCodeUpdateRequest;

public interface IBranchCodeService {
    List<BranchCode> getBranchCodes(BranchCodeRequest branchCodeRequest);

    Integer sp_insbcm(BranchCodeAddRequest branchCodeAddRequest);

    Integer sp_updatebcm(BranchCodeUpdateRequest updateRequest);

    Integer sp_delbranchcode(BranchCodeDeleteRequest deleteRequest);
}
