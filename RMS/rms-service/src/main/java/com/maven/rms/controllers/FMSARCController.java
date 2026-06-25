package com.maven.rms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maven.rms.models.ARCRequest;
import com.maven.rms.models.FMSARC;
import com.maven.rms.services.FMSARCService;

@RestController
@RequestMapping("/api/fms/v1")
public class FMSARCController {
    @Autowired
    private FMSARCService customerService;

    @PostMapping("/arc")
    public ResponseEntity<FMSARC> sendCustomer(@RequestBody ARCRequest request) {
        FMSARC response = customerService.sendCustomerRequest(request);
        return ResponseEntity.ok(response);
    }
}