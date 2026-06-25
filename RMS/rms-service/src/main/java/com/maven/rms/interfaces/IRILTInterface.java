package com.maven.rms.interfaces;

import java.math.BigInteger;
// import java.util.Date;
// import java.util.List;
import java.util.List;

import com.maven.rms.models.RILTRequest;
import com.maven.rms.models.RILTRequest2;

public interface IRILTInterface {

    public BigInteger sp_insRILT(RILTRequest request);

    public BigInteger sp_delRILT(RILTRequest request);

    public List<Object[]> sp_getRILT(RILTRequest2 request);

    public BigInteger sp_updRILT(RILTRequest request);

}
