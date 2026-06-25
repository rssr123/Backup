package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.OTCBalancingRequest;
import com.maven.rms.models.OTCDailyBal;

public interface IOTCDailyBalInterface {

    List<String> sp_getotcbranchcode(String ssm4uuserrefno);

    List<Object[]> sp_getotcdailyballist(OTCDailyBal bodyRequest);

    Integer sp_checkotcdailybalval(OTCDailyBal bodyRequest);

    Integer sp_updotcdailybalstatus(OTCBalancingRequest bodyRequest);

    List<Object[]> sp_checkotcbalstatus(OTCDailyBal bodyRequest);

    // Added 02-05-2025 Geo
    List<Object> sp_getotcdailybalctr(OTCDailyBal bodyRequest);

    Integer sp_updotcdailybalctr(OTCDailyBal bodyRequest);
}