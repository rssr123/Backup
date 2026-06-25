package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.ReprintRcptRequest;
import com.maven.rms.models.ServiceProviderProfileRequest;
import com.maven.rms.models.ServiceProviderRequest;

public interface IServiceProviderInterface {

    List<Object[]> sp_getserviceproviderpayment(ServiceProviderRequest serviceProviderRequest);
    List<Object[]> sp_getserviceprovideremail(ServiceProviderRequest serviceProviderRequest);
    List<Object[]> sp_getserviceprovideremailmtt(ServiceProviderRequest serviceProviderRequest);
    List<Object[]> sp_getserviceprovidermaintenance(ServiceProviderProfileRequest serviceProviderProfileRequest);
    
    Integer sp_insserviceprovidermaintenance(ServiceProviderProfileRequest serviceProviderProfileRequest);
    Integer sp_updserviceprovidermaintenance(ServiceProviderProfileRequest serviceProviderProfileRequest);
    Integer sp_delserviceprovidermaintenance(ServiceProviderProfileRequest serviceProviderProfileRequest);
    Integer sp_updserviceproviderdatepayment(ServiceProviderRequest serviceProviderRequest);
    
}
