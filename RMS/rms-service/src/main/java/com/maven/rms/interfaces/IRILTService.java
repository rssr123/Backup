package com.maven.rms.interfaces;

import java.math.BigInteger;
// import java.util.Date;
// import java.util.List;
import java.util.List;

import com.maven.rms.models.RILTRequest;
import com.maven.rms.models.RILTRequest2;
import com.maven.rms.models.RILT;

public interface IRILTService {
    
    BigInteger sp_insRILT(RILTRequest request);

    BigInteger sp_delRILT(RILTRequest request);

    List<RILT> sp_getRILT(RILTRequest2 request);

    BigInteger sp_updRILT(RILTRequest request);

}
