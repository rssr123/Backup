package com.maven.rms.controllers;

import com.maven.rms.models.FMSARV;
import com.maven.rms.services.FMSARVService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/fmsarv")
public class FMSARVController {

    private final FMSARVService fmsARVService;

    @Autowired
    public FMSARVController(FMSARVService fmsARVService) {
        this.fmsARVService = fmsARVService;
    }

    @PostMapping("/getfmsrefno")
    public List<FMSARV> getFmsRefNo() {
        return fmsARVService.sp_getfmsrefno();
    }

    @PostMapping("/insert")
    public Integer insertFmsArv(@RequestBody FMSARV fmsARV) {
        return fmsARVService.sp_insfmsarv(fmsARV);
    }

    @PostMapping("/getfmsarv")
    public List<FMSARV> getFmsArv() {
        return fmsARVService.sp_getfmsarv();
    }

    @PostMapping("/update")
    public Integer updateFmsArv(@RequestBody FMSARV fmsARV) {
        return fmsARVService.sp_updfmsarv(fmsARV);
    }

    @PostMapping("/api-arv")
    public List<FMSARV> apiArv(@RequestBody String stringBody, FMSARV currentItem) {
        return fmsARVService.fms_api_arv(stringBody,currentItem);
    }

    @PostMapping("/process-all")
    public String processFMSARVWorkflow() {
        int result = 0;

        try {
            // result = fmsArrService.fms_arr_sch().size();
            result = CollectionUtils.size(fmsARVService.fms_arv_sch());
                   
        } catch (Exception e) {
            // RMSLogger.schedulerError(e.getMessage().toString());
            System.out.println(e.getMessage().toString());
            return "Error processing FMSARV records";
              // Set refireImmediately to false
        }
        return "Processed " + result + " FMSARV records";
    }
}
