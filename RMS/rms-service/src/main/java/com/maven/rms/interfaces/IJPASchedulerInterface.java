package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.JPASchedulerRequest;

public interface IJPASchedulerInterface {
    public List<Object[]> sp_getfpascheduler(JPASchedulerRequest jpaSchedulerRequest);
}
