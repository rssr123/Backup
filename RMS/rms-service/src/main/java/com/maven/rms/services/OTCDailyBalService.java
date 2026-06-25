package com.maven.rms.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IOTCDailyBalService;
import com.maven.rms.models.OTCBalancingRequest;
import com.maven.rms.models.OTCDailyBal;
import com.maven.rms.repositories.OTCDailyBalRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OTCDailyBalService implements IOTCDailyBalService {

    private final OTCDailyBalRepository otcDailyBalRepository;

    public OTCDailyBalService(OTCDailyBalRepository otcDailyBalRepository) {
        this.otcDailyBalRepository = otcDailyBalRepository;
    }

    @Override
    public List<OTCDailyBal> sp_getotcbranchcode(String ssm4uuserrefno) {
        List<OTCDailyBal> getotcbranchcode = Collections.emptyList();

        List<String> objects = otcDailyBalRepository.sp_getotcbranchcode(ssm4uuserrefno);

        getotcbranchcode = convertOTCBranchCode(objects);

        return getotcbranchcode;
    }

    @Override
    public List<OTCDailyBal> sp_getotcdailyballist(OTCDailyBal bodyRequest) {
        List<OTCDailyBal> getotcdailyballisting = Collections.emptyList();

        List<Object[]> objects = otcDailyBalRepository.sp_getotcdailyballist(bodyRequest);

        getotcdailyballisting = convertDailyBalListing(objects);

        return getotcdailyballisting;
    }

    @Override
    public Integer sp_checkotcdailybalval(OTCDailyBal bodyRequest) {
        Integer result = otcDailyBalRepository.sp_checkotcdailybalval(bodyRequest);
        return result;
    }

    @Override
    // @Transactional
    public Integer sp_updotcdailybalstatus(OTCBalancingRequest bodyRequest) {
        // added 02-05-2025 Geo
        OTCDailyBal ctrReq = new OTCDailyBal();
        ctrReq.setBranch_code(bodyRequest.getBranch_code());
        ctrReq.setBal_date(bodyRequest.getBal_date());
        // Get all force-checkout counter ID
        List<OTCDailyBal> ctrReqList = sp_getotcdailybalctr(ctrReq);

        if (ctrReqList != null && !ctrReqList.isEmpty()) {
            // Update force-checkout counter ID one by one
            Integer result = 0;
            for (OTCDailyBal ctr : ctrReqList) {
                int retryCount = 0;
                boolean success = false;

                while (retryCount < 3 && !success) {
                    result = otcDailyBalRepository.sp_updotcdailybalctr(ctr);

                    if (result != -1) {
                        success = true; // ✅ Successfully processed
                    } else {
                        retryCount++;
                        //log.warn("Retry {} for DailyBal Force-Checkout: {}", retryCount, ctr.getOtc_counter_id());

                        if (retryCount >= 3) {
                            log.error("🚫 All retry attempts failed for counter: {}", ctr.getOtc_counter_id());
                        }
                    }
                }
            }

        }

        return otcDailyBalRepository.sp_updotcdailybalstatus(bodyRequest);
    }

    @Override
    public List<OTCDailyBal> sp_checkotcbalstatus(OTCDailyBal bodyRequest) {
        List<OTCDailyBal> checkotcbalstatus = Collections.emptyList();

        List<Object[]> objects = otcDailyBalRepository.sp_checkotcbalstatus(bodyRequest);

        checkotcbalstatus = convertOTCbalstatus(objects);

        return checkotcbalstatus;
    }

    // Added 02-05-2025 Geo
    private List<OTCDailyBal> sp_getotcdailybalctr(OTCDailyBal bodyRequest) {
        List<OTCDailyBal> getotcdailybalctr = Collections.emptyList();

        List<Object> objects = otcDailyBalRepository.sp_getotcdailybalctr(bodyRequest);

        if (objects == null || objects.isEmpty()) {
            return Collections.emptyList();
        }

        getotcdailybalctr = convertOTCBalCtr(objects);

        return getotcdailybalctr;
    }

    // Converting
    private List<OTCDailyBal> convertOTCBranchCode(List<String> objects) {
        List<OTCDailyBal> OTCBranchCodeList = new ArrayList<>();

        for (String obj : objects) {
            OTCDailyBal OTCBranchCode = new OTCDailyBal();
            OTCBranchCode.setBranch_code(obj);
            OTCBranchCodeList.add(OTCBranchCode);
        }

        return OTCBranchCodeList;
    }

    private List<OTCDailyBal> convertDailyBalListing(List<Object[]> objects) {
        List<OTCDailyBal> OTCDailyBalList = new ArrayList<>();

        for (Object[] obj : objects) {
            OTCDailyBal OTCDailyBalListing = new OTCDailyBal();
            OTCDailyBalListing.setCounter_id((String) obj[0]);
            OTCDailyBalListing.setUser_id((String) obj[1]);
            OTCDailyBalListing.setCounter_bal_status((String) obj[2]);
            OTCDailyBalListing.setBranch_code((String) obj[3]);
            OTCDailyBalListing.setCheck_in((String) obj[4]);
            OTCDailyBalListing.setCheck_out((String) obj[5]);
            OTCDailyBalListing.setTotal_amt((BigDecimal) obj[6]);
            OTCDailyBalListing.setTotal((Integer) obj[7]);
            OTCDailyBalListing.setOtc_counter_id((BigInteger) obj[8]);
            OTCDailyBalList.add(OTCDailyBalListing);
        }

        return OTCDailyBalList;
    }

    private List<OTCDailyBal> convertOTCbalstatus(List<Object[]> objects) {
        List<OTCDailyBal> OTCbalstatusList = new ArrayList<>();

        for (Object[] obj : objects) {
            OTCDailyBal OTCbalstatusListing = new OTCDailyBal();
            OTCbalstatusListing.setBal_status((String) obj[0]);
            OTCbalstatusListing.setBal_type((String) obj[1]);
            OTCbalstatusList.add(OTCbalstatusListing);
        }

        return OTCbalstatusList;
    }

    private List<OTCDailyBal> convertOTCBalCtr(List<Object> objects) {
        List<OTCDailyBal> OTCBalCtrList = new ArrayList<>();

        for (Object obj : objects) {
            OTCDailyBal OTCBalCtrListing = new OTCDailyBal();
            OTCBalCtrListing.setOtc_counter_id((BigInteger) obj);
            OTCBalCtrList.add(OTCBalCtrListing);
        }

        return OTCBalCtrList;
    }
}
