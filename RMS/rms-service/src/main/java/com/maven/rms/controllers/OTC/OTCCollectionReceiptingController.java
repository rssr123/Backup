package com.maven.rms.controllers.OTC;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.maven.rms.exceptionhandler.ApplicationException;
import com.maven.rms.models.IdamanAPIDownload;
import com.maven.rms.models.IdamanAPIDownloadRequest;
import com.maven.rms.models.OTC.OTCCollectionReceipting;
import com.maven.rms.models.OTC.OTCCollectionReceiptingPymtItem;
import com.maven.rms.models.OTC.OTCEMV;
import com.maven.rms.models.OTC.OTCEMVPaymentReq;
import com.maven.rms.models.OTC.OTCHist;
import com.maven.rms.models.OTC.OTCPayment;
import com.maven.rms.models.OTC.OTCPaymentDetails;
import com.maven.rms.models.OTC.OTCPaymentRequest;
import com.maven.rms.models.OTC.OTCRcpt;
import com.maven.rms.models.OTC.OTCollectionReceiptingRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.IdamanAPIDownloadService;
import com.maven.rms.services.OTC.EMVService;
import com.maven.rms.services.OTC.OTCCollectionReceiptingService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;

@RestController
@RequestMapping("/api/OTCCR/v1")
@Slf4j
public class OTCCollectionReceiptingController {

    @Autowired
    private AuthService authService;

    @Autowired
    private OTCCollectionReceiptingService spService;

    @Autowired
    private IdamanAPIDownloadService idamanAPIDownloadService;


    // @Autowired
    // private final EMVService emvUsb;

    @Autowired
    private EMVService emvUsb;

    // public OTCCollectionReceiptingController(WebClient.Builder webClientBuilder) {
    //     // this.emvUsb = new EMVService();
    //     this.webClient = webClientBuilder.baseUrl("https://localhost:8081").build();
    // }
    
    @PostMapping(value = "/getcollectioninfo")
    public ResponseEntity<ApiResponse<List<OTCCollectionReceipting>>> sp_getcollectioninfo(HttpServletRequest request,
            @RequestBody OTCollectionReceiptingRequest getRequest) {

        List<OTCCollectionReceipting> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getcollectioninfo(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COLLECTION_RECEIPTING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getpymtitems")
    public ResponseEntity<ApiResponse<List<OTCCollectionReceiptingPymtItem>>> sp_otccrpymtitem(HttpServletRequest request,
            @RequestBody OTCollectionReceiptingRequest getRequest) {

        List<OTCCollectionReceiptingPymtItem> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_otccrpymtitem(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COLLECTION_RECEIPTING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/insotcpayment")
    public ResponseEntity<ApiResponse<Integer>> sp_insotcpymt(
            HttpServletRequest request,
            @RequestBody OTCPaymentRequest insertRequest) throws ApplicationException{

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = spService.sp_insotcpymt(insertRequest);

        if (result < 0) {
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(result);
    }


    @PostMapping(value = "/insotcbodypayment")
    public ResponseEntity<ApiResponse<Integer>> sp_insotcbodypymt(
            HttpServletRequest request,
            @RequestBody List<OTCPaymentRequest> insertRequest) throws ApplicationException, IOException{

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = spService.sp_insotcpymtbody(insertRequest);

        if (result <= 0) {
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(result);
    }
    
    @PostMapping(value = "/getotccrhist")
    public ResponseEntity<ApiResponse<List<OTCHist>>> sp_otccrhist(HttpServletRequest request,
            @RequestBody OTCPaymentRequest getRequest) {

        List<OTCHist> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_otccrhist(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COLLECTION_RECEIPTING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getOTCPaymentHeader")
    public ResponseEntity<ApiResponse<List<OTCPayment>>> sp_getotccrpaymentheader(HttpServletRequest request,
            @RequestBody OTCPaymentRequest getRequest) {

        List<OTCPayment> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getotccrpaymentheader(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COLLECTION_RECEIPTING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getOTCPaymentDetails")
    public ResponseEntity<ApiResponse<List<OTCPaymentDetails>>> sp_getotccrpaymentdetails(HttpServletRequest request,
            @RequestBody OTCPaymentRequest getRequest) {

        List<OTCPaymentDetails> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getotccrpaymentdetails(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COLLECTION_RECEIPTING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    // @PostMapping("/emvPayment")
    // public ResponseEntity<ApiResponse<String>> processPayment(@RequestBody OTCEMVPaymentReq paymentRequest) {
    //     // Set port and amount dynamically
    //     // EMVService.portName = portName;
    //     // EMVService.amount = amount;

    //     try {
    //         emvUsb.setAmount(paymentRequest.getAmount());
    //         emvUsb.setAdditionalData(paymentRequest.getAdditionalData());
    //         emvUsb.emvUSB(paymentRequest);
    //         return APIResponse.SuccessResponse("Payment successful");
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return APIResponse.InternalServerError();
    //     }
    // }

    // @PostMapping("/emvPayment")
    // public ResponseEntity<ApiResponse<Integer>> processPayment(@RequestBody OTCEMVPaymentReq paymentRequest) {
    //     try {
    //         // Call local EMV API to get response
    //         RestTemplate restTemplate = new RestTemplate();
    //         // String url = "http://localhost:8081/emv/emvPayment"; // Local EMV API URL
    //         String url = "http://localhost:8081/emv/api/emv/v1/emvPayment"; // Local EMV API URL

    //         HttpHeaders headers = new HttpHeaders();
    //         headers.setContentType(MediaType.APPLICATION_JSON);
    //         HttpEntity<OTCEMVPaymentReq> requestEntity = new HttpEntity<>(paymentRequest, headers);

    //         // ResponseEntity<String> responseEntity = restTemplate.exchange(
    //         //         url, HttpMethod.POST, requestEntity, String.class); // Expect a String response

    //         ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

    //         ObjectMapper objectMapper = new ObjectMapper();
    //         JsonNode rootNode = objectMapper.readTree(response.getBody());

    //         // Extract only "data" part
    //         String extractedData = rootNode.path("data").asText();
    //         System.out.println("Extracted data: " + extractedData);

    //         // String emvResponse = response.getBody(); // Extract the EMV machine response (e.g., "R200")

    //         if (extractedData != null && !extractedData.isEmpty()) {
    //             // Pass the EMV response to the service for further processing
    //             Integer result = emvUsb.emvUSB(extractedData, paymentRequest);
    //             return APIResponse.SuccessResponse(result);
    //         } else {
    //             return APIResponse.InternalServerError();
    //         }
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return APIResponse.InternalServerError();
    //     }
    // }

    @PostMapping("/emvPayment")
    public ResponseEntity<ApiResponse<Integer>> processPayment(@RequestBody OTCEMVPaymentReq paymentRequest) {
        try {
            // Extract EMV response from Angular's request payload
            String extractedData = paymentRequest.getEmvResponse(); // Angular should send this
    
            if (extractedData != null && !extractedData.isEmpty()) {
                System.out.println("Received EMV Response from Client: " + extractedData);
    
                // Process EMV response
                Integer result = emvUsb.emvUSB(extractedData, paymentRequest);
    
                return APIResponse.SuccessResponse(result);
            } else {
                return APIResponse.InternalServerError();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return APIResponse.InternalServerError();
        }
    }

    @PostMapping(value = "/getotccrrcpt")
    public ResponseEntity<ApiResponse<List<OTCRcpt>>> sp_getotcrcpt(HttpServletRequest request,
            @RequestBody OTCPaymentRequest getRequest) {

        List<OTCRcpt> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getotcrcpt(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COLLECTION_RECEIPTING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }


    // @PostMapping(value = "/downloadOTCRcpt")
    // public ResponseEntity<byte[]> downloadFile(@RequestBody IdamanAPIDownloadRequest request) {
    //     try {
    //         // Fetch the file details
    //         List<IdamanAPIDownload> downloads = idamanAPIDownloadService.idaman_api_downloadDoc(request);
    
    //         if (!downloads.isEmpty() && downloads.get(0).getFile_content() != null) {
    //             IdamanAPIDownload file = downloads.get(0);
    
    //             // Convert file content from Base64 to binary
    //             byte[] fileContent = Base64.getDecoder().decode(file.getFile_content());
    
    //             // Prepare headers
    //             HttpHeaders headers = new HttpHeaders();
    //             headers.setContentType(MediaType.APPLICATION_PDF);
    //             headers.setContentDispositionFormData("attachment", file.getFile_nm());
    
    //             return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    //         } else {
    //             log.error("No file content returned for request: {}", request);
    //             return new ResponseEntity<>("File not found".getBytes(), HttpStatus.NOT_FOUND);
    //         }
    //     } catch (IOException e) {
    //         log.error("Error occurred while downloading the file", e);
    //         return new ResponseEntity<>("Error downloading file".getBytes(), HttpStatus.INTERNAL_SERVER_ERROR);
    //     }
    // }

    @PostMapping(value = "/downloadOTCRcpt", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse<String>> downloadFile(HttpServletResponse response, HttpServletRequest request,
										@Valid @RequestBody IdamanAPIDownloadRequest payload) throws IOException, JRException, SQLException{

		// 	if (!authService.isAuthenticated(request))
		// 	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		// MTTRCPT rcpt = null;

		// rcpt = mttService.getExistingReceipt((String)payload.get("i_orn_no")).orElse(null);

		// System.out.println("rcpt no: " + rcpt.getRcptNo());
		// System.out.println("version id: " + rcpt.getVersionId());
		// System.out.println("rcpt uuid: " + rcpt.getRcptUUID());
		// if(rcpt!=null){
			List<IdamanAPIDownload> data = idamanAPIDownloadService.idaman_api_downloadDoc(payload);

			if (data.size() > 0) {
				return APIResponse.SuccessResponse(data.get(0).getFile_content());
			}

			
		// }

		return APIResponse.SuccessResponse("No data found");
	}

    @PostMapping(value = "/getotcemv")
    public ResponseEntity<ApiResponse<OTCEMV>> sp_getotcemvsales(HttpServletRequest request,
            @RequestBody OTCPaymentRequest getRequest) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        OTCEMV result = spService.sp_getotcemvsales(getRequest);

        if (result == null) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COLLECTION_RECEIPTING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }
}