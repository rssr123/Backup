package com.maven.rms.controllers;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maven.rms.models.IdamanAPIDownload;
import com.maven.rms.models.IdamanAPIDownloadRequest;
import com.maven.rms.models.IdamanAPISearch;
import com.maven.rms.models.IdamanAPISearchRequest;
import com.maven.rms.models.IdamanAPIUpload;
import com.maven.rms.models.IdamanAPIUploadReq;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.IdamanAPIDownloadService;
import com.maven.rms.services.IdamanAPISearchService;
import com.maven.rms.services.IdamanAPITokenService;
import com.maven.rms.services.IdamanAPIUploadService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;

@RestController
@RequestMapping("/api/idaman/v1")
@Slf4j
public class IdamanAPIController {
    @Autowired
    private AuthService authService;

    @Autowired
    private IdamanAPIUploadService iuService;

    @Autowired
    private IdamanAPIDownloadService idService;

    @Autowired
    private IdamanAPISearchService isService;

    @Autowired
    private IdamanAPITokenService itService;

    @PostMapping(value = "/uploadIdaman")
    public ResponseEntity<ApiResponse<List<IdamanAPIUpload>>> uploadIdaman(
        HttpServletRequest request) throws IOException {

        List<IdamanAPIUpload> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        long min = 10000000000000L; // 14-digit minimum
        long max = 99999999999999L; // 14-digit maximum

        long randomNumber = ThreadLocalRandom.current().nextLong(min, max + 1);
        String refNo1 = "OR" + String.valueOf(randomNumber);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String docDate = LocalDate.now().format(formatter);

        String prefix = "RMS";
        String letters = getRandomUppercaseLetters(2);
        String number = String.format("%06d", new Random().nextInt(1_000_000)); // 6-digit number
        String uuid = UUID.randomUUID().toString().toUpperCase(); // e.g., 810780-3594-4BED-BD0F-72E206FE6C44

        String RMScode = prefix + letters + number + "-" + uuid;

        log.error("Idaman Upload - " + refNo1 + " {}", RMScode);

        result = iuService.idaman_api_uploadDoc(new IdamanAPIUploadReq(
                "RMS",refNo1,"RMSReceipt",docDate,"","","1","","","",RMScode,"","","","","","","",
                "iVBORw0KGgoAAAANSUhEUgAAAuUAAADsCAYAAADTqtgfAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad5mH3gAAAVxSURBVHhe7d3BbeJAAEBRs82FBigmBXCADtJC3AC5cKKBdEADSQvZgJzLbnIL+hZ+TxoxI/mEL1+jsb36+DQAAACZP9MvAAAQEeUAABAT5QAAEBPlAAAQE+UAABAT5QAAEBPlAAAQE+UAABAT5QAAEBPlAAAQE+UAABAT5QAAEBPlAAAQE+UAABAT5QAAEBPlAAAQE+UAABAT5QAAEBPlAAAQE+UAABAT5QAAEBPlAAAQE+UAABAT5QAAEBPlAAAQE+UAABAT5QAAEBPlAAAQE+UAABAT5QAAEBPlAAAQE+UAABAT5QAAEBPlAAAQE+UAABAT5QAAEBPlAAAQE+UAABAT5QAAEBPlAAAQE+UAABAT5QAAEBPlAAAQE+UAABAT5QAAEBPlAAAQE+UAABAT5QAAEBPlAAAQE+UAABAT5QAAEBPlAAAQE+UAABAT5QAAEBPlv2wcx2kGAMBvu9fWEuUAABAT5QAAEBPlAAAQE+UAABAT5QAAEBPlAAAQE+UAABAT5QAAEBPlAAAQE+UAABAT5QAAEBPlAAAQE+UAABAT5QAAEBPlAAAQE+UAABAT5QAAEBPlAAAQE+UAABAT5QAAEBPlAAAQE+UAABBbfXya5osxjuM0u43NZjPNAAD4TffacYuM8jlbrVaDWwIAsCyOrwAAQEyUAwBATJQDAEBMlN/Ier2+ju/s9/vr2fEvr6+vw9PT07QCAGBpRPkMPD8/D4+Pj9MKAIClEeUzsN1uh/f392kFAMDSiPIZuBxn+emoCwAA90+U39Db29twOp3+G+fzeboCAAB8POhmLjvfLy8v0+p7X3/9Zaf8eDwOh8PBx4MAABbITvkNPTw8XAP737Hb7aYrAABAlAMAQE6UAwBATJQDAEBMlAMAQMzbV2bG21cAAJbHTjkAAMREOQAAxEQ5AADERDkAAMRE+cx4yBMAYHlEOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAAxEQ5AADERDkAAMREOQAApIbhL8nxa8rCAJCLAAAAAElFTkSuQmCC",
                "Receipt1999.pdf")
        );

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.DEFERRED_INCOME_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/downloadIdaman")
    public ResponseEntity<ApiResponse<List<IdamanAPIDownload>>> downloadIdaman(
        HttpServletRequest request, 
        @RequestBody IdamanAPIDownloadRequest idamanAPIDownloadRequest) throws IOException {

        List<IdamanAPIDownload> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = idService.idaman_api_downloadDoc(idamanAPIDownloadRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.DEFERRED_INCOME_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/searchIdaman")
    public ResponseEntity<ApiResponse<List<IdamanAPISearch>>> searchIdaman(
        HttpServletRequest request, 
        @RequestBody IdamanAPISearchRequest idamanAPISearchRequest) throws IOException {

        List<IdamanAPISearch> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = isService.idaman_api_searchDoc(idamanAPISearchRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.DEFERRED_INCOME_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping("/getToken")
    public ResponseEntity<ApiResponse<String>> getToken() throws IOException {
        String result = "";

        result = itService.getOAuth2Token();
        
        return APIResponse.SuccessResponse(result);
    }

    private static String getRandomUppercaseLetters(int length) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder result = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            result.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return result.toString();
    }
}
