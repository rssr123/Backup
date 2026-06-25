package com.maven.rms.controllers;

import com.maven.rms.models.FMSAPIA;
import com.maven.rms.services.FMSAPIAService;
import com.maven.rms.utils.RMSLogger;

import org.apache.commons.collections.CollectionUtils;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/api/fmsapia")
public class FMSAPIAController {

    private final FMSAPIAService fmsAPIAService;

    @Autowired
    public FMSAPIAController(FMSAPIAService fmsAPIAService) {
        this.fmsAPIAService = fmsAPIAService;
    }

    @PostMapping("/getrefunddetails")
    public List<FMSAPIA> getRefundDetails() {
        return fmsAPIAService.sp_getrefunddetails();
    }

    @PostMapping("/insertfmsapia")
    public Integer insertFmsApia(@RequestBody FMSAPIA fmsAPIA) {
        return fmsAPIAService.sp_insfmsapia(fmsAPIA);
    }

    @PostMapping("/getfmsapia")
    public List<FMSAPIA> getFmsApia() {
        return fmsAPIAService.sp_getfmsapia();
    }

    @PostMapping("/updatefmsapia")
    public Integer updateFmsApia(@RequestBody FMSAPIA fmsAPIA) {
        return fmsAPIAService.sp_updfmsapia(fmsAPIA);
    }

    @PostMapping("/api-apia")
    public List<FMSAPIA> apiApia(@RequestBody String stringBody, FMSAPIA currentItem) {
        return fmsAPIAService.fms_api_apia(stringBody,currentItem);
    }



    @PostMapping("/process-all")
    public String processFMSAPIAWorkflow() {
        int result = 0;

        try {
            // result = fmsArrService.fms_arr_sch().size();
            result = CollectionUtils.size(fmsAPIAService.fms_apia_sch());
                   
        } catch (Exception e) {
            // RMSLogger.schedulerError(e.getMessage().toString());
            System.out.println(e.getMessage().toString());
            return "Error processing FMSAPIA records";
              // Set refireImmediately to false
        }
        return "Processed " + result + " FMSAPIA records";
    }
}
