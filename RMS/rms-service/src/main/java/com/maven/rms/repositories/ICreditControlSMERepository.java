package com.maven.rms.repositories;

import java.util.List;

import com.maven.rms.models.CCTaskListReq;

public interface ICreditControlSMERepository {
    List<Object[]> sp_getcreditcontroltasklist(CCTaskListReq request);
    Integer sp_assigncctask(List<CCTaskListReq> request);
}
