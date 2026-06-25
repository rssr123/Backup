package com.maven.rms.controllers;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maven.rms.models.BillingItem;
import com.maven.rms.models.BillingItemRequest;
import com.maven.rms.services.BillingItemService;
import com.maven.rms.utils.APIResponse;

@RestController
@RequestMapping("api/blitem/v1")
public class BillingItemController {

    @Autowired
    private BillingItemService billingItemService;

    @PostMapping(value = "/getbilitem")
    public ResponseEntity<?> getBillingItem(
            HttpServletRequest request,
            @RequestBody BillingItemRequest billingItemRequest) {

        List<BillingItem> result = billingItemService.getBillingItem(billingItemRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound();
        }

        return APIResponse.SuccessResponse(result);
    }
}
