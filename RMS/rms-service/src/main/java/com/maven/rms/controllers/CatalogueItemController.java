package com.maven.rms.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maven.rms.models.CatalogueItem;
import com.maven.rms.models.CatalogueItemRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.CatalogueItemService;
import com.maven.rms.utils.APIResponse;

@RestController
@RequestMapping("api/catalogue/v1")
public class CatalogueItemController {

    @Autowired
    private CatalogueItemService catalogueItemService;

    @Autowired
    private AuthService authService;

    @PostMapping(value = "/getcatalogueitem")
    public ResponseEntity<?> getCatalogueItem(
            HttpServletRequest request,
            @RequestBody CatalogueItemRequest catalogueItemRequest) {

        List<CatalogueItem> result = catalogueItemService.getCatalogueItem(catalogueItemRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound();
        }

        return APIResponse.SuccessResponse(result);
    }


    @PostMapping(value = "/getcatno")
    public ResponseEntity<ApiResponse<String>> sp_getctlrunno
    (HttpServletRequest request) {

        String result = "";

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            result = catalogueItemService.sp_getctlrunno();
            
            if (result.isEmpty()) {
                return APIResponse.NoDataFound();
            }

            return APIResponse.SuccessResponse(result);

    }
}
