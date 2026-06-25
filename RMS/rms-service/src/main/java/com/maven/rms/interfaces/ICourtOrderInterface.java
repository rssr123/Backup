package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.CourtOrderRequest;
import com.maven.rms.models.ServiceProviderRequest;

public interface ICourtOrderInterface {
    List<Object[]> sp_getcourtorderlisting(CourtOrderRequest courtOrderRequest);
    List<Object[]> sp_getcreditcontrolcaseinfo(CourtOrderRequest courtOrderRequest);
    List<Object[]> sp_getcourtorderpymtiteminfo(CourtOrderRequest courtOrderRequest);
    List<Object[]> sp_getcourtorderrmdrinfo(CourtOrderRequest courtOrderRequest);
    List<Object[]> sp_getcourtorderdocs(CourtOrderRequest courtOrderRequest);
    List<Object[]> sp_getcourtorderhist(CourtOrderRequest courtOrderRequest);


}
