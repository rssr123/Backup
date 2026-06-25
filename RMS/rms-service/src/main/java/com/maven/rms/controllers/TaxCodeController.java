package com.maven.rms.controllers;
import java.math.BigInteger;
import java.sql.Date;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;

import com.maven.rms.models.FeeGrpRequest;
import com.maven.rms.models.TaxCd;
import com.maven.rms.models.TaxCdRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
// import com.maven.rms.services.StoreProcedureService;
import com.maven.rms.services.TaxCodeService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.SystemStatus;
import com.maven.rms.utils.ControllersEnum;

@RestController
@RequestMapping("/api/tc/v1")
@Slf4j
public class TaxCodeController {

    //private static final Logger logger = LoggerFactory.getLogger(TaxCodeController.class);

    @Autowired
    private AuthService authService;

    // @Autowired
    // private StoreProcedureService spService;

    @Autowired
    private TaxCodeService taxCodeService;

    // @Secured("ROLE_USER")
    @PostMapping(value = "/gettaxcode")
    public ResponseEntity<ApiResponse<List<TaxCd>>> getTaxCode(
            HttpServletRequest request,
            @RequestBody TaxCdRequest taxCdRequest) {
        List<TaxCd> result = Collections.emptyList();


            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // result = spService.sp_gettaxcode_v2(
            //         taxCdRequest.getI_page(),
            //         taxCdRequest.getI_size(),
            //         taxCdRequest.getI_tax_cd_id(),
            //         taxCdRequest.getI_tax_cd(),
            //         taxCdRequest.getI_tax_cd_nm_en(),
            //         taxCdRequest.getI_tax_cd_nm_bm(),
            //         taxCdRequest.getI_modified_by(),
            //         taxCdRequest.getI_dt_modified_fr(),
            //         taxCdRequest.getI_dt_modified_to(),
            //         taxCdRequest.getI_status()
            // );

            result = taxCodeService.sp_gettaxcode_v2(taxCdRequest);

            if (result.isEmpty()) {
                return APIResponse.NoDataFound(ControllersEnum.TAX_CODE_CONTROLLER);
            }
            return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/addtaxcode")
    public ResponseEntity<ApiResponse<Integer>> addTaxCode(
            HttpServletRequest request,
            @RequestBody TaxCdRequest insertRequest) {

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Integer result = spService.sp_instaxcode(
            //         insertRequest.getI_tax_cd(),
            //         insertRequest.getI_tax_cd_nm_en(),
            //         insertRequest.getI_tax_cd_nm_bm(),
            //         insertRequest.getI_tax_pct(),                   
            //         authService.getLoginUserName(),
            //         authService.getLoginUserName(),
            //         SystemStatus.Active.getMessage());

            insertRequest.setI_created_by(authService.getLoginUserName());
            insertRequest.setI_modified_by(authService.getLoginUserName());
            insertRequest.setI_status(SystemStatus.Active.getMessage());

            Integer result = taxCodeService.sp_instaxcode(insertRequest);

            if (result <= 0) {
                return APIResponse.InternalServerError();
            }

            return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/updtaxcode")
    public ResponseEntity<ApiResponse<Integer>> updateTaxCode(
            HttpServletRequest request,
            @RequestBody TaxCdRequest updateRequest) {

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Integer result = spService.sp_updtaxcode(
            //         updateRequest.getI_tax_cd(),
            //         updateRequest.getI_tax_cd_nm_en(),
            //         updateRequest.getI_tax_cd_nm_bm(),
            //         updateRequest.getI_tax_pct(),
            //         authService.getLoginUserName(),
            //         SystemStatus.Active.getMessage());

            updateRequest.setI_modified_by(authService.getLoginUserName());
            //updateRequest.setI_status(SystemStatus.Active.getMessage());

            Integer result = taxCodeService.sp_updtaxcode(updateRequest);

            if (result <= 0) {
                return APIResponse.InternalServerError();
            }

            return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/deltaxcode")
    public ResponseEntity<ApiResponse<Integer>> deleteTaxCode(
            HttpServletRequest request,
            @RequestBody TaxCdRequest deleteRequest) {


            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Integer result = spService.sp_deltaxcode(
            //         deleteRequest.getI_tax_cd(),
            //         authService.getLoginUserName(),
            //         // SystemStatus.Active.getMessage()
            //         deleteRequest.getI_status()           
            //         );

            deleteRequest.setI_modified_by(authService.getLoginUserName());
            Integer result = taxCodeService.sp_deltaxcode(deleteRequest);

            if (result <= 0) {
                return APIResponse.InternalServerError();
            }

            return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/checktaxcodeexist")
    public ResponseEntity<ApiResponse<Integer>> checkTaxCodeExist(
            HttpServletRequest request,
            @RequestBody TaxCdRequest taxCodeRequest) {

                Long taxCodeId = taxCodeRequest.getI_tax_cd_id();

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Integer result = spService.sp_checktaxcdbyid(taxCodeId);
            Integer result = taxCodeService.sp_checktaxcdbyid(taxCodeRequest);

            if (result >0) { //got module using it
                return APIResponse.RecordInUsed(result);
            }

            return APIResponse.SuccessResponse(result);
    }
}
