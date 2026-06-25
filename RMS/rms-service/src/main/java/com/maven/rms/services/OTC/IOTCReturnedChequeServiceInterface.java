package com.maven.rms.services.OTC;

import java.sql.SQLException;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

import com.maven.rms.models.MTTEmailExpiry;
import com.maven.rms.models.NonBillRCEmail;
import com.maven.rms.models.ServiceProviderRequest;
import com.maven.rms.models.OTC.NBLDocInsRequest;
import com.maven.rms.models.OTC.NBLInsRequest;
import com.maven.rms.models.OTC.NBLItem;
import com.maven.rms.models.OTC.NBLItemInsRequest;
import com.maven.rms.models.OTC.NBLItemRequest;
import com.maven.rms.models.OTC.NBLTC;
import com.maven.rms.models.OTC.NonBilHist;
import com.maven.rms.models.OTC.NonBilResult;
import com.maven.rms.models.OTC.NonBillDoc;
import com.maven.rms.models.OTC.NonBillingItems;
import com.maven.rms.models.OTC.NonBillingListing;
import com.maven.rms.models.OTC.NonBillingListingRequest;
import com.maven.rms.models.OTC.OTCReturnedCheque;
import com.maven.rms.models.OTC.OTCReturnedChequeRequest;

public interface IOTCReturnedChequeServiceInterface {

    List<OTCReturnedCheque> sp_getchequeinfo(OTCReturnedChequeRequest otCollectionReceiptingRequest);
    List<NBLTC> sp_getnbltc();
    List<NBLItem> sp_getnblitem(NBLItemRequest nblItemRequest);
    String sp_getnbrunno();
    // List<Integer> sp_insnonbill(NBLInsRequest insertRequest);
    List<NonBilResult> sp_insnonbill(NBLInsRequest insertRequest);
    Integer sp_insnonbillitem(List<NBLItemInsRequest> insertRequest);
    // Integer sp_insnonbilldoc(List<NBLDocInsRequest> insertRequest);
    Integer sp_insnonbilldoc(NBLDocInsRequest insertRequest) throws SerialException, SQLException;

    List<NonBillingListing> sp_getnonbilllisting(NonBillingListingRequest req);
    List<NonBillingItems> sp_getnonbillitem(NonBillingListingRequest req);
    List<NonBillDoc> sp_getnonbildoc(NonBillingListingRequest req);
    String sp_getnonbildoccontent(NonBillingListingRequest req);
    List<NonBilHist> sp_getnonbilhist(NonBillingListingRequest req);

    Integer sp_updnonbillinsa(NBLInsRequest insertRequest);
    
    //scheduler
    List<NonBillRCEmail> sp_getnonbillreturnche(OTCReturnedChequeRequest getRequest);
    List<MTTEmailExpiry> sp_getmttemaildtexpiry(OTCReturnedChequeRequest getRequest);

    Integer sp_updsp(ServiceProviderRequest insertRequest);


    
    
}
