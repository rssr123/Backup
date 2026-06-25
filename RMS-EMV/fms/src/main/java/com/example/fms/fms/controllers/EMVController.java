package com.example.fms.fms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fms.fms.models.ApiResponse;
import com.example.fms.fms.models.OTCEMVPaymentReq;
import com.example.fms.fms.utils.APIResponse;


@RestController
@RequestMapping("/api/emv/v1")
public class EMVController {
    
     @Autowired
    private EMVService emvUsb;

    @PostMapping("/emvPayment")
    public ResponseEntity<ApiResponse<String>> processPayment(@RequestBody OTCEMVPaymentReq paymentRequest) {
        try {
            emvUsb.setCommand(paymentRequest.getCommand());
            emvUsb.setAmount(paymentRequest.getAmount());
            emvUsb.setAdditionalData(paymentRequest.getAdditionalData());
            String result = emvUsb.emvUSB(paymentRequest);
            if(result.isEmpty() || result.length() == 0) {
                return APIResponse.SuccessResponse("EMV Payment Failed");
            }
            return APIResponse.SuccessResponse(result);
        } catch (Exception e) {
            e.printStackTrace();
            return APIResponse.InternalServerError();
        }
    }

    // @PostMapping("/emvscc")
    // public ResponseEntity<ApiResponse<String>> emvscc(@RequestBody OTCEMVPaymentReq paymentRequest) {
    //     System.out.println("hello");
    //         return APIResponse.SuccessResponse("Success");
    // }
}
