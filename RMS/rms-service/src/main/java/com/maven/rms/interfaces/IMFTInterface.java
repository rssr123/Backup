package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.FeeDetailItemsRequest;
import com.maven.rms.models.MFTRequest;

public interface IMFTInterface {
    
       List<Object[]> sp_getmft(MFTRequest mftRequest);

       List<Object[]> sp_getfeedetailitems(FeeDetailItemsRequest feeDetailItemsReq);

       List<Object[]> sp_checkmftexist(MFTRequest mftRequest);
}
