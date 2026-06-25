package com.maven.rms.controllers;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.maven.rms.models.NonBillingItem;
import com.maven.rms.models.NonBillingItemRequest;
import com.maven.rms.services.NonBillingItemService;
import com.maven.rms.utils.APIResponse;

@RestController
@RequestMapping("api/nonblitem/v1")
public class NonBillingItemController {

    @Autowired
    private NonBillingItemService nonBillingItemService;

    @PostMapping(value = "/getnonbilitem")
    public ResponseEntity<?> getNonBillingItem(
            HttpServletRequest request,
            @RequestBody NonBillingItemRequest nonBillingItemRequest) {

        List<NonBillingItem> result = nonBillingItemService.getNonBillingItem(nonBillingItemRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound();
        }

        return APIResponse.SuccessResponse(result);
    }


}
