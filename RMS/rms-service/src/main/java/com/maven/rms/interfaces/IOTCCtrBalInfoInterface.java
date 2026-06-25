package com.maven.rms.interfaces;

import java.math.BigInteger;
import java.util.List;

public interface IOTCCtrBalInfoInterface {
    
    List<Object[]> sp_getotcbalctrinfo(String i_counter_id, BigInteger i_otc_counter_id);

    List<Object[]> sp_getotcrmscol(Integer i_page, Integer i_size, String i_counter_id, BigInteger i_otc_counter_id);

    List<Object[]> sp_getotcctrcol(Integer i_page, Integer i_size, String i_counter_id, BigInteger i_otc_counter_id);

    List<Object[]> sp_getotcphyinfo(String i_counter_id, BigInteger i_otc_counter_id);

    List<Object[]> sp_getotccashinfo(BigInteger i_otc_counter_id);

}
