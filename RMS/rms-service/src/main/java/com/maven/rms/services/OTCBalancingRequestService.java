package com.maven.rms.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IOTCBalancingRequestService;
import com.maven.rms.models.OTCBalancingRequest;
import com.maven.rms.repositories.OTCBalancingRequestRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OTCBalancingRequestService implements IOTCBalancingRequestService{

    private final OTCBalancingRequestRepository otcBalancingRequestRepository;

    public OTCBalancingRequestService(OTCBalancingRequestRepository otcRequest){
        this.otcBalancingRequestRepository = otcRequest;
    }

    @Override
    public Integer sp_insotcbalcash(List<OTCBalancingRequest> requestBody, String ssm4userrefno){

        int count = 0;

        for(OTCBalancingRequest request : requestBody){
            request.setSsm4uuserrefno(ssm4userrefno);
            count = otcBalancingRequestRepository.sp_insotcbalcash(request);
        }

        return count;
    }

    @Override
    public Integer sp_insotcbalcheque(List<OTCBalancingRequest> requestBody, String ssm4userrefno){

        int count = 0;

        for(OTCBalancingRequest request : requestBody){
            request.setSsm4uuserrefno(ssm4userrefno);
            count = otcBalancingRequestRepository.sp_insotcbalcheque(request);
        }

        for(OTCBalancingRequest request : requestBody){
            request.setSsm4uuserrefno(ssm4userrefno);
            count = otcBalancingRequestRepository.sp_insfmsotcbalphyidv(request);
        }

        return count;
    }
    
    @Override
    public Integer sp_insotcbalbd(List<OTCBalancingRequest> requestBody, String ssm4userrefno){

        int count = 0;
        //OTCBalancingRequest bodyRequestModel = new OTCBalancingRequest();
        // for(int i = 0; i < 1; i ++){
        //     bodyRequestModel.setSsm4uuserrefno(ssm4userrefno);
        //     bodyRequestModel.setId(requestBody.get(i).getId());
        //     bodyRequestModel.setDetail_type(requestBody.get(i).getDetail_type());
        // }

        for(OTCBalancingRequest request : requestBody){
            request.setSsm4uuserrefno(ssm4userrefno);
            count = otcBalancingRequestRepository.sp_insotcbalbd(request);
        }

        for(OTCBalancingRequest request: requestBody){
            request.setSsm4uuserrefno(ssm4userrefno);
            count = otcBalancingRequestRepository.sp_insfmsotcbalphyidv(request);
        }

        return count;
    }

    @Override
    public Integer sp_insotcbalmo(List<OTCBalancingRequest> requestBody, String ssm4userrefno){
        
        int count = 0;
        OTCBalancingRequest bodyRequestModel = new OTCBalancingRequest();

        for(int i = 0; i < 1; i ++){
            bodyRequestModel.setSsm4uuserrefno(ssm4userrefno);
            bodyRequestModel.setBal_date(requestBody.get(i).getBal_date());
            bodyRequestModel.setBranch_code(requestBody.get(i).getBranch_code());
        }

        for(OTCBalancingRequest request : requestBody){
            request.setSsm4uuserrefno(ssm4userrefno);
            count = otcBalancingRequestRepository.sp_insotcbalmo(request);
        }

        if(requestBody.size() > 0)
        {
            otcBalancingRequestRepository.sp_insotcbalmograndtotal(bodyRequestModel);
            bodyRequestModel.setDetail_type("money order");
            otcBalancingRequestRepository.sp_insfmsotcbalphysum(bodyRequestModel);
        }

        return count;
    }

    @Override
    public Integer sp_updotcbalpymtmode(OTCBalancingRequest requestBody){

        return otcBalancingRequestRepository.sp_updotcbalpymtmode(requestBody);
    }

    @Override
    public Integer sp_updotcbalstatus(OTCBalancingRequest requestBody){

        if (requestBody.getTotal_collection() == null || requestBody.getTotal_collection().compareTo(BigDecimal.ZERO) == 0) {
            requestBody.setTotal_collection(BigDecimal.ZERO);
        }

        if(requestBody.getTotal_emv_amt() == null || requestBody.getTotal_emv_amt().compareTo(BigDecimal.ZERO) == 0){
            requestBody.setTotal_emv_amt(BigDecimal.ZERO);
        }

        if(requestBody.getTotal_phy_amt() == null || requestBody.getTotal_phy_amt().compareTo(BigDecimal.ZERO) == 0){
            requestBody.setTotal_phy_amt(BigDecimal.ZERO);
        }

        return otcBalancingRequestRepository.sp_updotcbalstatus(requestBody);
    }

    @Override
    public Integer sp_insfmsotcbalphyidv(OTCBalancingRequest requestBody) {
            
        return otcBalancingRequestRepository.sp_insfmsotcbalphyidv(requestBody);
    }   

    @Override
    public Integer sp_insfmsotcbalphysum(OTCBalancingRequest requestBody) {
                
        return otcBalancingRequestRepository.sp_insfmsotcbalphysum(requestBody);   
    }

    @Override
    public Integer sp_insotcbalemvs(OTCBalancingRequest requestBody){
        int count = 0;

        count = otcBalancingRequestRepository.sp_insotcbalemvs(requestBody);

        if(count > 0){
            requestBody.setDetail_type("emv");
            otcBalancingRequestRepository.sp_insfmsotcbalphysum(requestBody);
        }

        return count;
    }

}
