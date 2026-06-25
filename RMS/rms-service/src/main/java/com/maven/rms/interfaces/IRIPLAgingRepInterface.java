package com.maven.rms.interfaces;

import java.math.BigInteger;
import java.util.List;

import com.maven.rms.models.RIPLAgingRequest;

public interface IRIPLAgingRepInterface {
    public BigInteger sp_insriplagingrpt(RIPLAgingRequest RIPLRequest, String i_p_email, String i_created_by,
            String i_modified_by);

    List<Object[]> sp_getriplaginglistingrpt(RIPLAgingRequest RIPLRequest);

    Integer sp_updriplagingrpt(RIPLAgingRequest RIPLRequest);

    List<Object[]> sp_getriplagingrpt(BigInteger i_rpt_ripl_age_id);

    Integer sp_getriplagequeuerpt();

    List<Object[]> sp_getpendingriplagingrpt();

    Integer sp_getpendingriplagingrptbyid(BigInteger i_rpt_ripl_age_id);

}
