package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.BranchCodeCounterRequest;

public interface IBranchCodeCounterInterface {
    List<Object[]> sp_getbccmap(BranchCodeCounterRequest getRequest);

    Integer sp_insbccmap(BranchCodeCounterRequest insertRequest);

    Integer sp_updbccmap(BranchCodeCounterRequest updateRequest);

    Integer sp_delbccmap(BranchCodeCounterRequest deleteRequest);
}
