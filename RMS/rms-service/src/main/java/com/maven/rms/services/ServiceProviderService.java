package com.maven.rms.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Service;

import com.maven.rms.models.NonBillRCEmail;
import com.maven.rms.models.ServiceProvider;
import com.maven.rms.models.ServiceProviderEmail;
import com.maven.rms.models.ServiceProviderEmailMtt;
import com.maven.rms.models.ServiceProviderProfile;
import com.maven.rms.models.ServiceProviderProfileRequest;
import com.maven.rms.models.ServiceProviderRequest;
import com.maven.rms.models.TaxCd;
import com.maven.rms.models.TaxCdRequest;
import com.maven.rms.models.OTC.OTCReturnedChequeRequest;
import com.maven.rms.repositories.IReprintReceiptRepository;
import com.maven.rms.repositories.IServiceProviderRepository;
import com.maven.rms.repositories.MTTRCPTRepository;
import com.maven.rms.interfaces.IServiceProviderService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ServiceProviderService implements IServiceProviderService {

    private final IServiceProviderRepository serviceProviderRepository;

    public ServiceProviderService(IServiceProviderRepository serviceProviderRepository) {
        this.serviceProviderRepository = serviceProviderRepository;
    }

    @Override
    public List<ServiceProvider> sp_getserviceproviderpayment(ServiceProviderRequest serviceProviderRequest) {
        List<ServiceProvider> result = Collections.emptyList();

        List<Object[]> objects = serviceProviderRepository.sp_getserviceproviderpayment(serviceProviderRequest);
        result = convertServiceProviderList(objects);

        return result;
    }

    private List<ServiceProvider> convertServiceProviderList(List<Object[]> objects) {
        List<ServiceProvider> serviceProviderList = new ArrayList<>();

        for (Object[] obj : objects) {
            ServiceProvider serviceProvider = new ServiceProvider();
            //profile_nm, cust_email, total_amt_payable, date_collection,pymt_status, dt_pymt, date_email_sent
            serviceProvider.setAg_bil((Integer) obj[0]);
            serviceProvider.setProfile_nm((String) obj[1]);
            serviceProvider.setAg_bil_no((String) obj[2]);
            serviceProvider.setCust_email((String) obj[3]);
            serviceProvider.setTotal_amt_payable((BigDecimal) obj[4]);
            serviceProvider.setDate_collection((Date) obj[5]);
            serviceProvider.setPymt_status((String) obj[6]);
            serviceProvider.setDt_pymt((Date) obj[7]);
            serviceProvider.setDate_email_sent((Date) obj[8]);
            serviceProvider.setOrder_status((String) obj[9]);
            serviceProvider.setTotal((Integer) obj[10]);

            serviceProviderList.add(serviceProvider);
        }

        return serviceProviderList;
    }


    @Override
    public List<ServiceProviderEmail> sp_getserviceprovideremail(ServiceProviderRequest serviceProviderRequest) {
        List<ServiceProviderEmail> result = Collections.emptyList();

        List<Object[]> objects = serviceProviderRepository.sp_getserviceprovideremail(serviceProviderRequest);
        result = convertServiceProviderEmailList(objects);

        return result;
    }

    private List<ServiceProviderEmail> convertServiceProviderEmailList(List<Object[]> objects) {
        List<ServiceProviderEmail> serviceProviderEmailList = new ArrayList<>();

        for (Object[] obj : objects) {
            ServiceProviderEmail serviceProviderEmail = new ServiceProviderEmail();
            //profile_nm, cust_email, total_amt_payable, date_collection,pymt_status, dt_pymt, date_email_sent
            serviceProviderEmail.setAg_bil((Integer) obj[0]);
            serviceProviderEmail.setEntity_nm((String) obj[1]);
            serviceProviderEmail.setAg_bil_no((String) obj[2]);
            serviceProviderEmail.setTotal_amt_payable((BigDecimal) obj[3]);
            serviceProviderEmail.setCust_email((String) obj[4]);

            serviceProviderEmailList.add(serviceProviderEmail);
        }

        return serviceProviderEmailList;
    }


    @Override
    public List<ServiceProviderProfile> sp_getserviceprovidermaintenance(ServiceProviderProfileRequest serviceProviderProfileRequest) {
        List<ServiceProviderProfile> result = Collections.emptyList();

   
            List<Object[]> objects = serviceProviderRepository.sp_getserviceprovidermaintenance(serviceProviderProfileRequest);
            result = convertServiceProviderProfileList(objects);

        return result;
    }

    private List<ServiceProviderProfile> convertServiceProviderProfileList(List<Object[]> objects) {
        List<ServiceProviderProfile> serviceProviderProfileList = new ArrayList<>();



        for (Object[] obj : objects) {
            ServiceProviderProfile serviceProviderProfile = new ServiceProviderProfile();
            serviceProviderProfile.setAg_pf_id((Integer) obj[0]);
            serviceProviderProfile.setProfile_nm((String) obj[1]);
            serviceProviderProfile.setCust_nm((String) obj[2]);
            serviceProviderProfile.setCust_addr_1((String) obj[3]);
            serviceProviderProfile.setCust_addr_2((String) obj[4]);
            serviceProviderProfile.setCust_addr_3((String) obj[5]);
            serviceProviderProfile.setCust_postcode((String) obj[6]);
            serviceProviderProfile.setCust_city((String) obj[7]);
            serviceProviderProfile.setCust_state((String) obj[8]);
            serviceProviderProfile.setCust_email((String) obj[9]);
            serviceProviderProfile.setCust_phone((String) obj[10]);
            serviceProviderProfile.setFee_detail_id((String) obj[11]);
            serviceProviderProfile.setEntity_type((String) obj[12]);
            serviceProviderProfile.setEntity_no((String) obj[13]);
            serviceProviderProfile.setEntity_nm((String) obj[14]);
            serviceProviderProfile.setStatus((String) obj[15]);
            serviceProviderProfile.setTotal((Integer) obj[16]);

            
            serviceProviderProfileList.add(serviceProviderProfile);
        }

        return serviceProviderProfileList;
    }


    @Override
    public Integer sp_insserviceprovidermaintenance(ServiceProviderProfileRequest serviceProviderProfileRequest) {
        Integer result = 0;

            result = serviceProviderRepository.sp_insserviceprovidermaintenance(serviceProviderProfileRequest);

        return result;
    }

    @Override
    public Integer sp_updserviceprovidermaintenance(ServiceProviderProfileRequest serviceProviderProfileRequest) {
        Integer result = 0;

            result = serviceProviderRepository.sp_updserviceprovidermaintenance(serviceProviderProfileRequest);

        return result;
    }


    @Override
    public Integer sp_delserviceprovidermaintenance(ServiceProviderProfileRequest serviceProviderProfileRequest) {
        Integer result = 0;

            result = serviceProviderRepository.sp_delserviceprovidermaintenance(serviceProviderProfileRequest);

        return result;
    }

    @Override
    public Integer sp_updserviceproviderdatepayment(ServiceProviderRequest serviceProviderRequest) {
        Integer result = 0;

            result = serviceProviderRepository.sp_updserviceproviderdatepayment(serviceProviderRequest);

        return result;
    }

    @Override
    public List<ServiceProviderEmailMtt> sp_getserviceprovideremailmtt(ServiceProviderRequest serviceProviderRequest) {
        List<ServiceProviderEmailMtt> result = Collections.emptyList();

        List<Object[]> objects = serviceProviderRepository.sp_getserviceprovideremailmtt(serviceProviderRequest);
        result = convertServiceProviderEmailMttList(objects);

        return result;
    }

    private List<ServiceProviderEmailMtt> convertServiceProviderEmailMttList(List<Object[]> objects) {
        List<ServiceProviderEmailMtt> serviceProviderEmailMttList = new ArrayList<>();

        for (Object[] obj : objects) {
            ServiceProviderEmailMtt serviceProviderEmailMtt = new ServiceProviderEmailMtt();
        
            serviceProviderEmailMtt.setMtt_id((String) obj[0]);
            serviceProviderEmailMtt.setEntity_name((String) obj[1]);
            serviceProviderEmailMtt.setOrn_no((String) obj[2]);
            serviceProviderEmailMtt.setTotal_amount((BigDecimal) obj[3]);
            serviceProviderEmailMtt.setProfile_name((String) obj[4]);
            serviceProviderEmailMtt.setCust_email((String) obj[5]);
            serviceProviderEmailMtt.setAg_bil((String) obj[6]);

            

            serviceProviderEmailMttList.add(serviceProviderEmailMtt);
        }

        return serviceProviderEmailMttList;
    }

    







    
}
