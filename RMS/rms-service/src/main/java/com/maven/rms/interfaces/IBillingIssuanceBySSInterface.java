package com.maven.rms.interfaces;

import java.math.BigInteger;
import java.sql.Blob;
import java.util.List;

import com.maven.rms.models.BillingIssuanceBySBillingDocRequest;
import com.maven.rms.models.BillingIssuanceBySSBilCustomerRequest;
import com.maven.rms.models.BillingIssuanceBySSBilStatusRequest;
import com.maven.rms.models.BillingIssuanceBySSBillingChildDetails;
import com.maven.rms.models.BillingIssuanceBySSBillingDetailsRequest;
import com.maven.rms.models.BillingIssuanceBySSBillingItemDetails;
import com.maven.rms.models.BillingIssuanceBySSListingRequest;
import com.maven.rms.models.BillingIssuanceBySSRunnoRequest;
import com.maven.rms.models.BillingTypeCodeRequest;

public interface IBillingIssuanceBySSInterface {


    List<Object[]> sp_getbibssbiltypecode( BillingTypeCodeRequest billingTypeCodeRequest);

    Integer sp_insbilissbyssbilcust(BillingIssuanceBySSBilCustomerRequest bilCustRequest);

    Integer sp_insbilissbyssbilitem(BillingIssuanceBySSBillingItemDetails bilItemDets, Integer bilId ,String username);

    Integer sp_insbilissbyssbilchild(BillingIssuanceBySSBillingChildDetails bilChildDets, Integer bilId ,String username);
    
    String sp_getbibssrunno();

    String sp_getandreservebillrunno(BillingIssuanceBySSRunnoRequest runnoRequest);

    String sp_getbilstatus(BillingIssuanceBySSBilStatusRequest bilStatusRequest);

    List<Object[]> sp_getbibsspaymentdetails(BillingIssuanceBySSBilStatusRequest bilStatusRequest);

    Integer sp_uploadDoc(BillingIssuanceBySBillingDocRequest bilDocRequest, Blob blob);

    List<Object[]> sp_getbibsslisting(BillingIssuanceBySSListingRequest billingListingRequest);

    List<Object[]> sp_getbibssbillingdetails(BillingIssuanceBySSBillingDetailsRequest bilDetailsRequest);

    List<Object[]> sp_getbibsslistofbillingitems(BillingIssuanceBySSBillingDetailsRequest bilDetailsRequest);

    List<Object[]> sp_getbibsslistofbillingissuance(BillingIssuanceBySSBillingDetailsRequest bilDetailsRequest);

    List<Object[]> sp_getbibsslistofdoc(BillingIssuanceBySSBillingDetailsRequest bilDetailsRequest);

    Blob sp_getbibssdocfilecontent(BillingIssuanceBySSBillingDetailsRequest bilDetailsRequest);

    List<Object[]> sp_getbibsshistory(BillingIssuanceBySSBillingDetailsRequest bilDetailsRequest);

    Integer sp_removebilissbyss(Integer bilId);

    

 
} 
