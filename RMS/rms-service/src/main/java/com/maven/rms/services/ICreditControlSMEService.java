package com.maven.rms.services;

import java.util.List;

import com.maven.rms.models.CCTaskList;
import com.maven.rms.models.CCTaskListReq;

public interface ICreditControlSMEService {
    List<CCTaskList> sp_getcreditcontroltasklist(CCTaskListReq req);
    Integer sp_assigncctask(List<CCTaskListReq> req);
}
