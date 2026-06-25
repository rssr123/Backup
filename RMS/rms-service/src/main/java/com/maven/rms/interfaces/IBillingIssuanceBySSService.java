package com.maven.rms.interfaces;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

import com.maven.rms.models.BillingIssuanceBySBillingDoc;
import com.maven.rms.models.BillingIssuanceBySBillingDocRequest;
import com.maven.rms.models.BillingIssuanceBySSBilCustomerRequest;
import com.maven.rms.models.BillingIssuanceBySSBilStatusRequest;
import com.maven.rms.models.BillingIssuanceBySSBillingDetails;
import com.maven.rms.models.BillingIssuanceBySSBillingDetailsRequest;
import com.maven.rms.models.BillingIssuanceBySSHistory;
import com.maven.rms.models.BillingIssuanceBySSListOfIssuance;
import com.maven.rms.models.BillingIssuanceBySSListing;
import com.maven.rms.models.BillingIssuanceBySSListingRequest;
import com.maven.rms.models.BillingIssuanceBySSListofBilItems;
import com.maven.rms.models.BillingIssuanceBySSPaymentDetails;
import com.maven.rms.models.BillingIssuanceBySSRunnoRequest;
import com.maven.rms.models.BillingTypeCode;
import com.maven.rms.models.BillingTypeCodeRequest;

public interface IBillingIssuanceBySSService {

    List<BillingTypeCode> sp_getbibssbiltypecode(BillingTypeCodeRequest billingTypeCodeRequest);

    Integer sp_insbilissbyssbilcust(BillingIssuanceBySSBilCustomerRequest bilCustRequest, String username, String custIP);

    String sp_getbibssrunno();

    String sp_getandreservebillrunno(BillingIssuanceBySSRunnoRequest runnoRequest);

    String sp_getbilstatus(BillingIssuanceBySSBilStatusRequest bilStatusRequest);

    List<BillingIssuanceBySSPaymentDetails> sp_getbibsspaymentdetails(BillingIssuanceBySSBilStatusRequest bilStatusRequest);

    Integer sp_uploadDoc(BillingIssuanceBySBillingDocRequest bilDocRequest) throws SerialException, SQLException;

    List<BillingIssuanceBySSListing> sp_getbibsslisting(BillingIssuanceBySSListingRequest billingListingRequest);

    List<BillingIssuanceBySSBillingDetails> sp_getbibssbillingdetails(BillingIssuanceBySSBillingDetailsRequest bilDetailsRequest);

    List<BillingIssuanceBySSListofBilItems> sp_getbibsslistofbillingitems(BillingIssuanceBySSBillingDetailsRequest bilDetailsRequest);

    List<BillingIssuanceBySSListOfIssuance> sp_getbibsslistofbillingissuance(BillingIssuanceBySSBillingDetailsRequest bilDetailsRequest);

    List<BillingIssuanceBySBillingDoc> sp_getbibsslistofdoc(BillingIssuanceBySSBillingDetailsRequest bilRequest);

    String sp_getbibssdocfilecontent(BillingIssuanceBySSBillingDetailsRequest bilRequest) throws SQLException;

    List<BillingIssuanceBySSHistory> sp_getbibsshistory(BillingIssuanceBySSBillingDetailsRequest bilDetailsRequest);
    
}
