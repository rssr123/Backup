package com.maven.rms.interfaces;

import com.maven.rms.models.OTCBalancingRequest;

public interface IOTCBalancingRequestInterface {
        
    Integer sp_insotcbalcash(OTCBalancingRequest requestBody);

    Integer sp_insotcbalcheque(OTCBalancingRequest requestBody);

    Integer sp_insotcbalbd(OTCBalancingRequest requestBody);

    Integer sp_insotcbalmo(OTCBalancingRequest requestBody);

    Integer sp_updotcbalpymtmode(OTCBalancingRequest requestBody);

    Integer sp_updotcbalstatus(OTCBalancingRequest requestBody);
    
    Integer sp_insotcbalmograndtotal(OTCBalancingRequest requestBody);

    Integer sp_insfmsotcbalphyidv(OTCBalancingRequest requestBody);

    Integer sp_insfmsotcbalphysum(OTCBalancingRequest requestBody);

    Integer sp_insotcbalemvs(OTCBalancingRequest requestBody);
}
