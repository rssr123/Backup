package com.maven.rms.interfaces;

import java.math.BigInteger;
import java.util.List;

import com.maven.rms.models.OTCBalCash;
import com.maven.rms.models.OTCCtrBalCol;
import com.maven.rms.models.OTCCtrBalInfo;
import com.maven.rms.models.OTCCtrBalPhy;
import com.maven.rms.models.OTCCtrBalRMS;

public interface IOTCCtrBalInfoService {
    
    List<OTCCtrBalInfo> sp_getotcbalctrinfo(String i_counter_id, BigInteger i_otc_counter_id);

    List<OTCCtrBalRMS> sp_getotcrmscol(Integer i_page, Integer i_size, String i_counter_id, BigInteger i_otc_counter_id);

    List<OTCCtrBalCol> sp_getotcctrcol(Integer i_page, Integer i_size, String i_counter_id, BigInteger i_otc_counter_id);

    List<OTCCtrBalPhy> sp_getotcphyinfo(String i_counter_id, BigInteger i_otc_counter_id);

    List<OTCBalCash> sp_getotccashinfo(BigInteger i_otc_counter_id);
}
