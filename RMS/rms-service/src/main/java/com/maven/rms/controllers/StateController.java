package com.maven.rms.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.maven.rms.models.State;
import com.maven.rms.models.StateRequest;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.StateService;
import com.maven.rms.utils.APIResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/state/v1")
public class StateController {
    @Autowired
    private AuthService authService;

    @Autowired
    private StateService stateService;

    @PostMapping(value = "/getState")
    public ResponseEntity<?> getState(
            HttpServletRequest request,
            @RequestBody StateRequest stateRequest) {

        List<State> result = stateService.getState(stateRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound();
        }

        return APIResponse.SuccessResponse(result);
    }
}
