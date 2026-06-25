package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.StateRequest;

public interface IStateRepository {
    List<Object[]> getState(StateRequest stateRequest);
}
