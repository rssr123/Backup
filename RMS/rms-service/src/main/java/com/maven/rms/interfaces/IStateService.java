package com.maven.rms.interfaces;

import java.util.List;
import com.maven.rms.models.State;
import com.maven.rms.models.StateRequest;

public interface IStateService {
    List<State> getState(StateRequest stateRequest);
}
