package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.OTCBalancingRequest;

public interface IOTCBalancingRequestService {

    Integer sp_insotcbalcash(List<OTCBalancingRequest> requestBody, String ssm4uuserrefno);

    Integer sp_insotcbalcheque(List<OTCBalancingRequest> requestBody, String ssm4uuserrefno);

    Integer sp_insotcbalbd(List<OTCBalancingRequest> requestBody, String ssm4uuserrefno);

    Integer sp_insotcbalmo(List<OTCBalancingRequest> requestBody, String ssm4uuserrefno);

    Integer sp_updotcbalpymtmode(OTCBalancingRequest requestBody);

    Integer sp_updotcbalstatus(OTCBalancingRequest requestBody);

    Integer sp_insfmsotcbalphyidv(OTCBalancingRequest requestBody);

    Integer sp_insfmsotcbalphysum(OTCBalancingRequest requestBody);

    Integer sp_insotcbalemvs(OTCBalancingRequest requestBody);
}
