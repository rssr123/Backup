package com.maven.rms.services;

import java.math.BigDecimal;
// import java.math.BigDecimal;
import java.math.BigInteger;
// import java.util.ArrayList;
// import java.util.Collections;
// import java.util.Date;
// import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.maven.rms.repositories.RILTRepository;
import com.maven.rms.interfaces.IRILTService;
import com.maven.rms.models.RILTRequest;
import com.maven.rms.models.RILTRequest2;
import com.maven.rms.models.RILT;

@Service
public class RILTService implements IRILTService { 
    
    private final RILTRepository riltRepository;

    public RILTService(RILTRepository riltRepository)
    {
        this.riltRepository = riltRepository;
    }

    @Override
    public BigInteger sp_insRILT(RILTRequest request) {
        BigInteger result = null;

        result = riltRepository.sp_insRILT(request);

        return result;
    }

    @Override
    public BigInteger sp_delRILT(RILTRequest request) {
        BigInteger result = null;

        result = riltRepository.sp_delRILT(request);

        return result;
    }

    @Override
    public List<RILT> sp_getRILT(RILTRequest2 getRequest) {
        List<RILT> result = Collections.emptyList();
   
            List<Object[]> objects = riltRepository.sp_getRILT(getRequest);
            result = convertRILT(objects);

        return result;
    }

    private List<RILT> convertRILT(List<Object[]> objects) {
        List<RILT> riltListList = new ArrayList<>();

        for (Object[] obj : objects) {
            RILT riltList = new RILT();
            riltList.setRilt_id((BigInteger) obj[0]);
            riltList.setLit_no((String) obj[1]);
            riltList.setLit_item_ref((String) obj[2]);
            riltList.setLit_amount((BigDecimal) obj[3]);
            riltList.setEntity_type((String) obj[4]);
            riltList.setEntity_no((String) obj[5]);
            riltList.setDt_due((Date) obj[6]);
            riltList.setDt_created((Date) obj[7]);
            riltList.setDt_modified((Date) obj[8]);
            riltList.setCreated_by((String) obj[9]);
            riltList.setModified_by((String) obj[10]);
            riltList.setStatus((String) obj[11]);
            riltList.setTotal((Integer) obj[12]);
            riltListList.add(riltList);
        }

        return riltListList;
    }

    @Override
    public BigInteger sp_updRILT(RILTRequest request) {
        BigInteger result = null;

        result = riltRepository.sp_updRILT(request);

        return result;
    }
    
}
