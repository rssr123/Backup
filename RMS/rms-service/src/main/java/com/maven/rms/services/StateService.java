package com.maven.rms.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maven.rms.models.StateRequest;
import com.maven.rms.interfaces.IStateService;
import com.maven.rms.models.State;
import com.maven.rms.repositories.StateRepository;

@Service
public class StateService implements IStateService {
    @Autowired
    private StateRepository stateRepository;

    @Override
    public List<State> getState(StateRequest stateRequest) {
        List<Object[]> objects = stateRepository.getState(stateRequest);
        return convertToStateList(objects);
    }

    private List<State> convertToStateList(List<Object[]> objects) {
        List<State> stateList = new ArrayList<>();

        for (Object[] obj : objects) {
            State state = new State();
            state.setParamId((String) obj[0]);
            state.setParamCd((String) obj[1]);
            state.setNmEn((String) obj[2]);
            state.setNmBm((String) obj[3]);
            state.setParamGrpNm((String) obj[4]);
            state.setSeq((Integer) obj[5]);
            state.setStatus((String) obj[6]);
            state.setTotal((Integer) obj[7]);
            stateList.add(state);
        }

        return stateList;
    }

}
