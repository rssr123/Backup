package com.maven.rms.services;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.maven.rms.interfaces.IDIAgingRepService;
import com.maven.rms.models.DeferredIncomeAging;
import com.maven.rms.models.DeferredIncomeAgingRequest;
import com.maven.rms.repositories.DIAgingRepRepository;

@Service
@Slf4j
public class DIAgingRepService implements IDIAgingRepService {

    //private static final Logger logger = LoggerFactory.getLogger(StoreProcedureService.class);
    private final DIAgingRepRepository diAgingRepRepo;

    public DIAgingRepService(DIAgingRepRepository diAgingRepRepo) {
        this.diAgingRepRepo = diAgingRepRepo;
    }

    // public BigInteger sp_insdiagingrpt(Date i_p_dt_req, Integer i_p_tmn_status,
    // String i_p_ent_ty, String i_p_ent_nm,
    // String i_p_txn_ty, String i_p_status, Date i_p_dt_exp_fr, Date i_p_dt_exp_to,
    // Date i_p_dt_eff_fr, Date i_p_dt_eff_to, Date i_p_dt_app_fr, Date
    // i_p_dt_app_to, Date i_p_dt_tmn_fr,
    // Date i_p_dt_tmn_to, String i_created_by, String i_modified_by,
    // String i_status, String i_p_email, String i_p_file_type, Integer
    // i_p_file_size, String i_p_file_nm,
    // String i_p_batch_no, String i_p_fms_ref_no) {
    @Override
    public BigInteger sp_insdiagingrpt(DeferredIncomeAgingRequest DIRequest, String i_p_email, String i_created_by,
            String i_modified_by) {
        {
            BigInteger result = BigInteger.ZERO;

            try {

                // result = storeProcedureRepository.sp_insdiagingrpt(i_p_dt_req,
                // i_p_tmn_status, i_p_ent_ty, i_p_ent_nm,
                // i_p_txn_ty, i_p_status, i_p_dt_exp_fr, i_p_dt_exp_to, i_p_dt_eff_fr,
                // i_p_dt_eff_to,
                // i_p_dt_app_fr, i_p_dt_app_to, i_p_dt_tmn_fr,
                // i_p_dt_tmn_to, i_created_by, i_modified_by, i_status, i_p_email,
                // i_p_file_type, i_p_file_size,
                // i_p_file_nm, i_p_batch_no, i_p_fms_ref_no);
                result = diAgingRepRepo.sp_insdiagingrpt(DIRequest, i_p_email, i_created_by, i_modified_by);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    // public List<DeferredIncomeAging> sp_getdiaginglistingrpt(Integer i_page,
    // Integer i_size, BigInteger i_rpt_di_age_id,
    // Date i_p_dt_req, Integer i_p_tmn_status, String i_p_ent_ty, String
    // i_p_ent_nm, String i_p_txn_ty,
    // String i_p_status, Date i_p_dt_exp_fr, Date i_p_dt_exp_to,
    // Date i_p_dt_eff_fr, Date i_p_dt_eff_to, Date i_p_dt_app_fr, Date
    // i_p_dt_app_to, Date i_p_dt_tmn_fr,
    // Date i_p_dt_tmn_to, String i_created_by, String i_modified_by, String
    // i_status, String i_p_email,
    // String i_p_file_type,
    // Integer i_p_file_size, String i_p_file_nm, String i_p_batch_no, String
    // i_p_fms_ref_no) {
    @Override
    public List<DeferredIncomeAging> sp_getdiaginglistingrpt(DeferredIncomeAgingRequest DIRequest) {

        List<DeferredIncomeAging> result = Collections.emptyList();

        try {

            // List<Object[]> objects =
            // storeProcedureRepository.sp_getdiaginglistingrpt(i_page, i_size,
            // i_rpt_di_age_id,
            // i_p_dt_req, i_p_tmn_status, i_p_ent_ty, i_p_ent_nm, i_p_txn_ty, i_p_status,
            // i_p_dt_exp_fr,
            // i_p_dt_exp_to, i_p_dt_eff_fr, i_p_dt_eff_to, i_p_dt_app_fr, i_p_dt_app_to,
            // i_p_dt_tmn_fr,
            // i_p_dt_tmn_to, i_created_by, i_modified_by, i_status, i_p_email,
            // i_p_file_type, i_p_file_size,
            // i_p_file_nm, i_p_batch_no, i_p_fms_ref_no);
            List<Object[]> objects = diAgingRepRepo.sp_getdiaginglistingrpt(DIRequest);

            result = convertToGetDIAgingListing(objects);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private List<DeferredIncomeAging> convertToGetDIAgingListing(List<Object[]> objects) {
        List<DeferredIncomeAging> DIagingList = new ArrayList<>();

        for (Object[] obj : objects) {
            DeferredIncomeAging diaging = new DeferredIncomeAging();

            diaging.setRpt_di_age_id((BigInteger) obj[0]);
            diaging.setP_dt_req((Date) obj[1]);
            diaging.setP_tmn_status((Integer) obj[2]);
            diaging.setP_ent_ty((String) obj[3]);
            diaging.setP_ent_nm((String) obj[4]);
            diaging.setP_txn_ty((String) obj[5]);
            diaging.setP_status((String) obj[6]);
            diaging.setP_dt_exp_fr((Date) obj[7]);
            diaging.setP_dt_exp_to((Date) obj[8]);
            diaging.setP_dt_eff_fr((Date) obj[9]);
            diaging.setP_dt_eff_to((Date) obj[10]);
            diaging.setP_dt_app_fr((Date) obj[11]);
            diaging.setP_dt_app_to((Date) obj[12]);
            diaging.setP_dt_tmn_fr((Date) obj[13]);
            diaging.setP_dt_tmn_to((Date) obj[14]);
            diaging.setDt_created((Date) obj[15]);
            diaging.setDt_modified((Date) obj[16]);
            diaging.setCreated_by((String) obj[17]);
            diaging.setModified_by((String) obj[18]);
            diaging.setStatus((String) obj[19]);
            diaging.setP_email((String) obj[20]);
            diaging.setP_file_type((String) obj[21]);
            diaging.setP_file_size((Integer) obj[22]);
            diaging.setP_file_nm((String) obj[23]);
            diaging.setP_batch_no((String) obj[24]);
            diaging.setP_fms_ref_no((String) obj[25]);
            diaging.setTask_id((String) obj[26]);
            diaging.setTotal((Integer) obj[27]);

            DIagingList.add(diaging);
        }
        return DIagingList;
    }

    @Override
    public Integer sp_upddiagingrpt(DeferredIncomeAgingRequest DIRequest, String i_modified_by) {
        Integer result = 0;
        try {
            result = diAgingRepRepo.sp_upddiagingrpt(DIRequest, i_modified_by);
        } catch (Exception e) {
            log.error("Exception in " + this.getClass().toString(), e);
        }
        return result;
    }

    @Override
    public List<DeferredIncomeAging> sp_getdiagingrpt(BigInteger i_rpt_di_age_id) {

        List<DeferredIncomeAging> result = Collections.emptyList();
        try {
            List<Object[]> objects = diAgingRepRepo.sp_getdiagingrpt(i_rpt_di_age_id);
            result = convertToGetDIAging(objects);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Integer sp_getdiagequeuerpt() {

        Integer result = 0;
        try {
            result = diAgingRepRepo.sp_getdiagequeuerpt();
        } catch (Exception e) {
            log.error("Exception in " + this.getClass().toString(), e);
        }
        return result;
    }

    @Override
    public List<DeferredIncomeAging> sp_getpendingdiagingrpt() {

        List<DeferredIncomeAging> result = Collections.emptyList();
        try {
            List<Object[]> objects = diAgingRepRepo.sp_getpendingdiagingrpt();
            result = convertToGetDIAging(objects);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private List<DeferredIncomeAging> convertToGetDIAging(List<Object[]> objects) {
        List<DeferredIncomeAging> DIagingList = new ArrayList<>();

        for (Object[] obj : objects) {
            DeferredIncomeAging diaging = new DeferredIncomeAging();

            diaging.setRpt_di_age_id((BigInteger) obj[0]);
            diaging.setP_dt_req((Date) obj[1]);
            diaging.setP_tmn_status((Integer) obj[2]);
            diaging.setP_ent_ty((String) obj[3]);
            diaging.setP_ent_nm((String) obj[4]);
            diaging.setP_txn_ty((String) obj[5]);
            diaging.setP_status((String) obj[6]);
            diaging.setP_dt_exp_fr((Date) obj[7]);
            diaging.setP_dt_exp_to((Date) obj[8]);
            diaging.setP_dt_eff_fr((Date) obj[9]);
            diaging.setP_dt_eff_to((Date) obj[10]);
            diaging.setP_dt_app_fr((Date) obj[11]);
            diaging.setP_dt_app_to((Date) obj[12]);
            diaging.setP_dt_tmn_fr((Date) obj[13]);
            diaging.setP_dt_tmn_to((Date) obj[14]);
            diaging.setDt_created((Date) obj[15]);
            diaging.setDt_modified((Date) obj[16]);
            diaging.setCreated_by((String) obj[17]);
            diaging.setModified_by((String) obj[18]);
            diaging.setStatus((String) obj[19]);
            diaging.setP_email((String) obj[20]);
            diaging.setP_file_type((String) obj[21]);
            diaging.setP_file_size((Integer) obj[22]);
            diaging.setP_file_nm((String) obj[23]);
            diaging.setP_batch_no((String) obj[24]);
            diaging.setP_fms_ref_no((String) obj[25]);
            diaging.setTask_id((String) obj[26]);

            DIagingList.add(diaging);
        }
        return DIagingList;
    }

    @Override
    public Integer sp_getpendingdiagingrptbyid(BigInteger i_rpt_di_age_id) {

        Integer result = 0;
        try {
            result = diAgingRepRepo.sp_getpendingdiagingrptbyid(i_rpt_di_age_id);
        } catch (Exception e) {
            log.error("Exception in " + this.getClass().toString(), e);
        }
        return result;
    }
}
