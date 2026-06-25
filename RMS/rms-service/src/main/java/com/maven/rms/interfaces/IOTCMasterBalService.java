package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.OTCBalancingRequest;
import com.maven.rms.models.OTCMasterBal;

public interface IOTCMasterBalService {
    
    List<OTCMasterBal> sp_getotcmasterballist(OTCMasterBal bodyRequest);

    Integer sp_checkotcmasterbalval(OTCMasterBal bodyRequest);

    Integer sp_updotcmasterbalstatus(OTCBalancingRequest bodyRequest);
}
