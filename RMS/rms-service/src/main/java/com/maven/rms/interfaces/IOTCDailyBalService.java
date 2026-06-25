package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.OTCBalancingRequest;
import com.maven.rms.models.OTCDailyBal;

public interface IOTCDailyBalService {

    List<OTCDailyBal> sp_getotcbranchcode(String ssm4uuserrefno);

    List<OTCDailyBal> sp_getotcdailyballist(OTCDailyBal bodyRequest);

    Integer sp_checkotcdailybalval(OTCDailyBal bodyRequest);

    Integer sp_updotcdailybalstatus(OTCBalancingRequest bodyRequest);

    List<OTCDailyBal> sp_checkotcbalstatus(OTCDailyBal bodyRequest);
}
