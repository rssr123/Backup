package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.CourtOrder;
import com.maven.rms.models.CourtOrderCaseInfo;
import com.maven.rms.models.CourtOrderDocs;
import com.maven.rms.models.CourtOrderHistory;
import com.maven.rms.models.CourtOrderPymtInfo;
import com.maven.rms.models.CourtOrderRequest;
import com.maven.rms.models.CourtOrderRmdrInfo;

public interface ICourtOrderService {

    List<CourtOrder> sp_getcourtorderlisting(CourtOrderRequest courtOrderRequest);
    List<CourtOrderCaseInfo> sp_getcreditcontrolcaseinfo(CourtOrderRequest courtOrderRequest);
    List<CourtOrderPymtInfo> sp_getcourtorderpymtiteminfo(CourtOrderRequest courtOrderRequest);
    List<CourtOrderRmdrInfo> sp_getcourtorderrmdrinfo(CourtOrderRequest courtOrderRequest);
    List<CourtOrderDocs> sp_getcourtorderdocs(CourtOrderRequest courtOrderRequest);
    List<CourtOrderHistory> sp_getcourtorderhist(CourtOrderRequest courtOrderRequest);
    
}
