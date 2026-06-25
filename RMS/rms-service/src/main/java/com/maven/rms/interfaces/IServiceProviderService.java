package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.ServiceProvider;
import com.maven.rms.models.ServiceProviderEmail;
import com.maven.rms.models.ServiceProviderEmailMtt;
import com.maven.rms.models.ServiceProviderProfile;
import com.maven.rms.models.ServiceProviderProfileRequest;
import com.maven.rms.models.ServiceProviderRequest;

public interface IServiceProviderService {

    List<ServiceProvider> sp_getserviceproviderpayment(ServiceProviderRequest serviceProviderRequest);
    List<ServiceProviderEmail> sp_getserviceprovideremail(ServiceProviderRequest serviceProviderRequest);
    List<ServiceProviderEmailMtt> sp_getserviceprovideremailmtt(ServiceProviderRequest serviceProviderRequest);
    List<ServiceProviderProfile> sp_getserviceprovidermaintenance(ServiceProviderProfileRequest serviceProviderProfileRequest);
    Integer sp_insserviceprovidermaintenance(ServiceProviderProfileRequest serviceProviderProfileRequest);
    Integer sp_updserviceprovidermaintenance(ServiceProviderProfileRequest serviceProviderProfileRequest);
    Integer sp_delserviceprovidermaintenance(ServiceProviderProfileRequest serviceProviderProfileRequest);
    Integer sp_updserviceproviderdatepayment(ServiceProviderRequest serviceProviderRequest);
    
}
