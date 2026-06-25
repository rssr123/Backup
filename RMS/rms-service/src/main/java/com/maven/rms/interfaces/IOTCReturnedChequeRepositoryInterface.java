package com.maven.rms.interfaces;

import java.sql.Blob;
import java.util.List;

import com.maven.rms.models.ServiceProviderRequest;
import com.maven.rms.models.OTC.NBLDocInsRequest;
import com.maven.rms.models.OTC.NBLInsRequest;
import com.maven.rms.models.OTC.NBLItemInsRequest;
import com.maven.rms.models.OTC.NBLItemRequest;
import com.maven.rms.models.OTC.NonBilResult;
import com.maven.rms.models.OTC.NonBillingListingRequest;
import com.maven.rms.models.OTC.OTCReturnedChequeRequest;

public interface IOTCReturnedChequeRepositoryInterface {
    List<Object[]> sp_getchequeinfo(OTCReturnedChequeRequest otcReturnedChequeRequest);
    List<Object[]> sp_getnbltc();
    List<Object[]> sp_getnblitem(NBLItemRequest nblItemRequest);
    String sp_getnbrunno();
    // List<Integer> sp_insnonbill(NBLInsRequest insertRequest);
    List<NonBilResult> sp_insnonbill(NBLInsRequest insRequest);
    Integer sp_insnonbillitem(List<NBLItemInsRequest> insRequest);
    // Integer sp_insnonbilldoc(List<NBLDocInsRequest> insRequest);
    Integer sp_insnonbilldoc(NBLDocInsRequest insRequests, Blob blob);

    List<Object[]> sp_getnonbilllisting(NonBillingListingRequest req);
    List<Object[]> sp_getnonbillitem(NonBillingListingRequest req);
    List<Object[]> sp_getnonbildoc(NonBillingListingRequest billDocReq);
    Blob sp_getnonbildoccontent(NonBillingListingRequest billDocReq);
    List<Object[]> sp_getnonbilhist(NonBillingListingRequest req);
    Integer sp_updnonbillinsa(NBLInsRequest insRequest);
    Integer sp_updsp(ServiceProviderRequest insRequest);
    
    //scheduler
    List<Object[]> sp_getnonbillreturnche(OTCReturnedChequeRequest otcReturnedChequeRequest);
    List<Object> sp_getmttemaildtexpiry(OTCReturnedChequeRequest otcReturnedChequeRequest);

}
