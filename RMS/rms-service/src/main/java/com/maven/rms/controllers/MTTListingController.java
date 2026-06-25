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

import com.maven.rms.models.MTTDetails;
import com.maven.rms.models.MTTItem;
import com.maven.rms.models.MTTItemDetails;
import com.maven.rms.models.MTTItemPgRequest;
import com.maven.rms.models.MTTListing;
import com.maven.rms.models.MTTListingDetReq;
import com.maven.rms.models.MTTListingPG;
import com.maven.rms.models.MTTListingRcpt;
import com.maven.rms.models.MTTPGDetails;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.MTTListingService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;

@RestController
@RequestMapping("/api/mttl/v1")
public class MTTListingController {

    @Autowired
    private AuthService authService;

    @Autowired
    private MTTListingService spService;

    @PostMapping("/getmttlisting")
    public ResponseEntity<ApiResponse<List<MTTListing>>> sp_getMTTListing(HttpServletRequest request,
            @RequestBody MTTListingDetReq mttListingRequest) {
        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // try {
            // List<MTTListing> result = spService.sp_getMTTListing(new MTTListingDetReq(
            //         mttListingRequest.getI_page(),
            //         mttListingRequest.getI_size(),
            //         mttListingRequest.getI_ss_cd(),
            //         mttListingRequest.getI_orn_no(),
            //         mttListingRequest.getI_orn_dt_fr(),
            //         mttListingRequest.getI_orn_dt_to(),
            //         mttListingRequest.getI_total_amt(),
            //         mttListingRequest.getI_order_status(),
            //         mttListingRequest.getI_rcpt_no(),
            //         mttListingRequest.getI_rcpt_dt_fr(),
            //         mttListingRequest.getI_rcpt_dt_to()));

            List<MTTListing> result = spService.sp_getMTTListing(mttListingRequest);

            if (result.isEmpty()) {
                return APIResponse.NoDataFound(ControllersEnum.MTT_LISTING_CONTROLLER);
            }
            return APIResponse.SuccessResponse(result);
        // } catch (DataAccessException e) {

        //     return APIResponse.InternalServerError();
        // } catch (NumberFormatException e) {
        //     return APIResponse.InvalidFormat();
        // } catch (Exception e) {
        //     return APIResponse.InternalServerError();
        // }
    }

    @PostMapping("/getmttdetails")
    public ResponseEntity<ApiResponse<List<MTTDetails>>> sp_getMTTDetails(HttpServletRequest request,
            @RequestBody MTTListingDetReq mttListingRequest) {
        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // try {
            // List<MTTDetails> result = spService.sp_getMTTDetails(new MTTListingDetReq(
            //         mttListingRequest.getI_ss_cd(),
            //         mttListingRequest.getI_orn_no(),
            //         mttListingRequest.getI_total_amt(),
            //         mttListingRequest.getI_order_status(),
            //         mttListingRequest.getI_rcpt_no()));

            List<MTTDetails> result = spService.sp_getMTTDetails(mttListingRequest);

            if (result.isEmpty()) {
                return APIResponse.NoDataFound(ControllersEnum.MTT_LISTING_CONTROLLER);
            }
            return APIResponse.SuccessResponse(result);
        // } catch (DataAccessException e) {

        //     return APIResponse.InternalServerError();
        // } catch (NumberFormatException e) {
        //     return APIResponse.InvalidFormat();
        // } catch (Exception e) {
        //     return APIResponse.InternalServerError();
        // }
    }

    @PostMapping("/getmttitem")
    public ResponseEntity<ApiResponse<List<MTTItem>>> sp_getmttlistingitem(HttpServletRequest request,
            @RequestBody MTTItemPgRequest mttListingRequest) {
        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // try {
            List<MTTItem> result = spService.sp_getmttlistingitem(
                mttListingRequest.getI_mtt_id()
            );

            if (result.isEmpty()) {
                return APIResponse.NoDataFound(ControllersEnum.MTT_LISTING_CONTROLLER);
            }
            return APIResponse.SuccessResponse(result);
        // } catch (DataAccessException e) {

        //     return APIResponse.InternalServerError();
        // } catch (NumberFormatException e) {
        //     return APIResponse.InvalidFormat();
        // } catch (Exception e) {
        //     return APIResponse.InternalServerError();
        // }
    }

    @PostMapping("/getmttpg")
    public ResponseEntity<ApiResponse<List<MTTListingPG>>> sp_getmttpg(HttpServletRequest request,
            @RequestBody MTTItemPgRequest mttListingRequest) {
        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // try {
            List<MTTListingPG> result = spService.sp_getmttpg(
                mttListingRequest.getI_mtt_id()
            );

            if (result.isEmpty()) {
                return APIResponse.NoDataFound(ControllersEnum.MTT_LISTING_CONTROLLER);
            }
            return APIResponse.SuccessResponse(result);
        // } catch (DataAccessException e) {

        //     return APIResponse.InternalServerError();
        // } catch (NumberFormatException e) {
        //     return APIResponse.InvalidFormat();
        // } catch (Exception e) {
        //     return APIResponse.InternalServerError();
        // }
    }

    @PostMapping("/getmttrcpt")
    public ResponseEntity<ApiResponse<List<MTTListingRcpt>>> sp_getmttrcpt(HttpServletRequest request,
            @RequestBody MTTItemPgRequest mttListingRequest) {
        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // try {
            List<MTTListingRcpt> result = spService.sp_getmttrcpt(
                mttListingRequest.getI_mtt_id()
            );

            if (result.isEmpty()) {
                return APIResponse.NoDataFound(ControllersEnum.MTT_LISTING_CONTROLLER);
            }
            return APIResponse.SuccessResponse(result);
        // } catch (DataAccessException e) {

        //     return APIResponse.InternalServerError();
        // } catch (NumberFormatException e) {
        //     return APIResponse.InvalidFormat();
        // } catch (Exception e) {
        //     return APIResponse.InternalServerError();
        // }
    }
    
    @PostMapping("/getmttpgdetails")
    public ResponseEntity<ApiResponse<List<MTTPGDetails>>> sp_getmttpg_details(HttpServletRequest request,
            @RequestBody MTTItemPgRequest mttListingRequest) {
        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // try {
            List<MTTPGDetails> result = spService.sp_getmttpg_details(
                mttListingRequest.getI_mtt_pg_id()
            );

            if (result.isEmpty()) {
                return APIResponse.NoDataFound(ControllersEnum.MTT_LISTING_CONTROLLER);
            }
            return APIResponse.SuccessResponse(result);
        // } catch (DataAccessException e) {

        //     return APIResponse.InternalServerError();
        // } catch (NumberFormatException e) {
        //     return APIResponse.InvalidFormat();
        // } catch (Exception e) {
        //     return APIResponse.InternalServerError();
        // }
    }

    @PostMapping("/getmttitemdetails")
    public ResponseEntity<ApiResponse<List<MTTItemDetails>>> sp_getmttitem_details(HttpServletRequest request,
            @RequestBody MTTItemPgRequest mttListingRequest) {
        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // try {

        Integer i_mtt_item_id = mttListingRequest.getI_mtt_item_id();
        List<MTTItemDetails> result = spService.sp_getmttitem_details(i_mtt_item_id);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.MTT_LISTING_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);
        // } catch (DataAccessException e) {

        // return APIResponse.InternalServerError();
        // } catch (NumberFormatException e) {
        // return APIResponse.InvalidFormat();
        // } catch (Exception e) {
        // return APIResponse.InternalServerError();
        // }
    }

}
