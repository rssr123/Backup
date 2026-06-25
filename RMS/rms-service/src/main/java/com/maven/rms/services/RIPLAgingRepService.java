package com.maven.rms.services;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IDIAgingRepService;
import com.maven.rms.interfaces.IRIPLAgingRepService;
import com.maven.rms.models.RIPLAgingRequest;
import com.maven.rms.models.RiplAging;
import com.maven.rms.repositories.RIPLAgingRepRepository;

@Service
@Slf4j
public class RIPLAgingRepService implements IRIPLAgingRepService {
    //private static final Logger logger = LoggerFactory.getLogger(StoreProcedureService.class);
    private final RIPLAgingRepRepository riplAgingRepository;

    public RIPLAgingRepService(RIPLAgingRepRepository riplAgingRepository) {
        this.riplAgingRepository = riplAgingRepository;

    }
    // #endregion
    // #region RIPL Aging Start

    // public BigInteger sp_insriplagingrpt(Date i_p_dt_req, Integer i_p_imp_status,
    // Integer i_p_exp_status,
    // String i_p_ent_ty, String i_p_ent_nm, Date i_p_dt_due_fr, Date i_p_dt_due_to,
    // Date i_p_dt_rcpt_fr, Date i_p_dt_rcpt_to, Date i_p_dt_imp_fr, Date
    // i_p_dt_imp_to, Date i_p_dt_wo_fr,
    // Date i_p_dt_wo_to, String i_created_by, String i_modified_by, String
    // i_status, String i_p_email,
    // String i_p_file_type, Integer i_p_file_size, String i_p_file_nm, String
    // i_p_batch_no,
    // String i_p_fms_ref_no) {
    @Override
    public BigInteger sp_insriplagingrpt(RIPLAgingRequest RIPLRequest, String i_p_email, String i_created_by,
            String i_modified_by) {
        {
            BigInteger result = BigInteger.ZERO;

                // result = riplAgingRepository.sp_insriplagingrpt(i_p_dt_req,
                // i_p_imp_status, i_p_exp_status,
                // i_p_ent_ty, i_p_ent_nm, i_p_dt_due_fr, i_p_dt_due_to, i_p_dt_rcpt_fr,
                // i_p_dt_rcpt_to,
                // i_p_dt_imp_fr, i_p_dt_imp_to, i_p_dt_wo_fr, i_p_dt_wo_to, i_created_by,
                // i_modified_by, i_status,
                // i_p_email, i_p_file_type, i_p_file_size, i_p_file_nm, i_p_batch_no,
                // i_p_fms_ref_no);
                result = riplAgingRepository.sp_insriplagingrpt(RIPLRequest, i_p_email, i_created_by,
                        i_modified_by);

            return result;
        }
    }

    // public List<RiplAging> sp_getriplaginglistingrpt(Integer i_page, Integer
    // i_size, BigInteger i_rpt_ripl_age_id,
    // Date i_p_dt_req, Integer i_p_imp_status, Integer i_p_exp_status, String
    // i_p_ent_ty, String i_p_ent_nm, Date i_p_dt_due_fr, Date i_p_dt_due_to,
    // Date i_p_dt_rcpt_fr, Date i_p_dt_rcpt_to, Date i_p_dt_imp_fr, Date
    // i_p_dt_imp_to, Date i_p_dt_wo_fr,
    // Date i_p_dt_wo_to, String i_created_by, String i_modified_by, String
    // i_status, String i_p_email,
    // String i_p_file_type,
    // Integer i_p_file_size, String i_p_file_nm) {
    @Override
    public List<RiplAging> sp_getriplaginglistingrpt(RIPLAgingRequest RIPLRequest) {

        List<RiplAging> result = Collections.emptyList();

            // List<Object[]> objects =
            // riplAgingRepository.sp_getriplaginglistingrpt(i_page, i_size,
            // i_rpt_ripl_age_id, i_p_dt_req, i_p_imp_status, i_p_exp_status, i_p_ent_ty,
            // i_p_ent_nm,
            // i_p_dt_due_fr, i_p_dt_due_to, i_p_dt_rcpt_fr, i_p_dt_rcpt_to, i_p_dt_imp_fr,
            // i_p_dt_imp_to,
            // i_p_dt_wo_fr, i_p_dt_wo_to, i_created_by, i_modified_by, i_status, i_p_email,
            // i_p_file_type, i_p_file_size, i_p_file_nm);
            List<Object[]> objects = riplAgingRepository.sp_getriplaginglistingrpt(RIPLRequest);

            result = convertToGetRIPLAgingListing(objects);

        return result;
    }

    private List<RiplAging> convertToGetRIPLAgingListing(List<Object[]> objects) {
        List<RiplAging> RIPLagingList = new ArrayList<>();

        for (Object[] obj : objects) {
            RiplAging riplaging = new RiplAging();

            riplaging.setRpt_ripl_age_id((BigInteger) obj[0]);
            riplaging.setP_dt_req((Date) obj[1]);
            riplaging.setP_imp_status((Integer) obj[2]);
            riplaging.setP_exp_status((Integer) obj[3]);
            riplaging.setP_ent_ty((String) obj[4]);
            riplaging.setP_ent_nm((String) obj[5]);
            riplaging.setP_dt_due_fr((Date) obj[6]);
            riplaging.setP_dt_due_to((Date) obj[7]);
            riplaging.setP_dt_rcpt_fr((Date) obj[8]);
            riplaging.setP_dt_rcpt_to((Date) obj[9]);
            riplaging.setP_dt_imp_fr((Date) obj[10]);
            riplaging.setP_dt_imp_to((Date) obj[11]);
            riplaging.setP_dt_wo_fr((Date) obj[12]);
            riplaging.setP_dt_wo_to((Date) obj[13]);
            riplaging.setDt_created((Date) obj[14]);
            riplaging.setDt_modified((Date) obj[15]);
            riplaging.setCreated_by((String) obj[16]);
            riplaging.setModified_by((String) obj[17]);
            riplaging.setStatus((String) obj[18]);
            riplaging.setP_email((String) obj[19]);
            riplaging.setP_file_type((String) obj[20]);
            riplaging.setP_file_size((Integer) obj[21]);
            riplaging.setP_file_nm((String) obj[22]);
            riplaging.setTask_id((String) obj[23]);
            riplaging.setTotal((Integer) obj[24]);

            RIPLagingList.add(riplaging);
        }
        return RIPLagingList;
    }

    // public Integer sp_updriplagingrpt(BigInteger i_rpt_ripl_age_id, String
    // i_status, Integer i_p_file_size,
    // String i_p_file_nm, String i_modified_by) {
    @Override
    public Integer sp_updriplagingrpt(RIPLAgingRequest riplAgingRequest) {
        Integer result = 0;
 
            result = riplAgingRepository.sp_updriplagingrpt(riplAgingRequest);
        return result;
    }

    @Override
    public List<RiplAging> sp_getriplagingrpt(BigInteger i_rpt_ripl_age_id) {

        List<RiplAging> result = Collections.emptyList();

            List<Object[]> objects = riplAgingRepository.sp_getriplagingrpt(i_rpt_ripl_age_id);
            result = convertToGetRIPLAging(objects);

        return result;
    }

    @Override
    public Integer sp_getriplagequeuerpt() {

        Integer result = 0;

            result = riplAgingRepository.sp_getriplagequeuerpt();
        return result;
    }

    @Override
    public List<RiplAging> sp_getpendingriplagingrpt() {

        List<RiplAging> result = Collections.emptyList();

            List<Object[]> objects = riplAgingRepository.sp_getpendingriplagingrpt();
            result = convertToGetRIPLAging(objects);

        return result;
    }

    private List<RiplAging> convertToGetRIPLAging(List<Object[]> objects) {
        List<RiplAging> RIPLagingList = new ArrayList<>();

        for (Object[] obj : objects) {
            RiplAging riplaging = new RiplAging();

            riplaging.setRpt_ripl_age_id((BigInteger) obj[0]);
            riplaging.setP_dt_req((Date) obj[1]);
            riplaging.setP_imp_status((Integer) obj[2]);
            riplaging.setP_exp_status((Integer) obj[3]);
            riplaging.setP_ent_ty((String) obj[4]);
            riplaging.setP_ent_nm((String) obj[5]);
            riplaging.setP_dt_due_fr((Date) obj[6]);
            riplaging.setP_dt_due_to((Date) obj[7]);
            riplaging.setP_dt_rcpt_fr((Date) obj[8]);
            riplaging.setP_dt_rcpt_to((Date) obj[9]);
            riplaging.setP_dt_imp_fr((Date) obj[10]);
            riplaging.setP_dt_imp_to((Date) obj[11]);
            riplaging.setP_dt_wo_fr((Date) obj[12]);
            riplaging.setP_dt_wo_to((Date) obj[13]);
            riplaging.setDt_created((Date) obj[14]);
            riplaging.setDt_modified((Date) obj[15]);
            riplaging.setCreated_by((String) obj[16]);
            riplaging.setModified_by((String) obj[17]);
            riplaging.setStatus((String) obj[18]);
            riplaging.setP_email((String) obj[19]);
            riplaging.setP_file_type((String) obj[20]);
            riplaging.setP_file_size((Integer) obj[21]);
            riplaging.setP_file_nm((String) obj[22]);
            riplaging.setP_batch_no((String) obj[23]);
            riplaging.setP_fms_ref_no((String) obj[24]);
            riplaging.setTask_id((String) obj[25]);

            RIPLagingList.add(riplaging);
        }
        return RIPLagingList;
    }

    @Override
    public Integer sp_getpendingriplagingrptbyid(BigInteger i_rpt_ripl_age_id) {

        Integer result = 0;

            result = riplAgingRepository.sp_getpendingriplagingrptbyid(i_rpt_ripl_age_id);
        return result;
    }
}
