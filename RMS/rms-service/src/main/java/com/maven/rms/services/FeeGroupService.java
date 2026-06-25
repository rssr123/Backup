package com.maven.rms.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IFeeGroupService;
import com.maven.rms.models.FeeGrp;
import com.maven.rms.models.FeeGrpRequest;
import com.maven.rms.repositories.IFeeGroupRepository;

@Service
@Slf4j
public class FeeGroupService implements IFeeGroupService {
    //private static final Logger logger = LoggerFactory.getLogger(StoreProcedureService.class);
    private final IFeeGroupRepository feeGroupRepository;

    public FeeGroupService(IFeeGroupRepository feeGroupRepository) {
        this.feeGroupRepository = feeGroupRepository;

    }

    // #region Fee Group Start
    @Override
    public List<FeeGrp> sp_getfeegroup_v2(FeeGrpRequest feeGroupRequest) {
        List<FeeGrp> result = Collections.emptyList();

        try {
            List<Object[]> objects = feeGroupRepository.sp_getfeegroup_v2(feeGroupRequest);
            result = convertTFeeGrpList(objects);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private List<FeeGrp> convertTFeeGrpList(List<Object[]> objects) {
        List<FeeGrp> feeGrpList = new ArrayList<>();

        for (Object[] obj : objects) {
            FeeGrp feeGrp = new FeeGrp();
            feeGrp.setFee_grp_id((Integer) obj[0]);
            feeGrp.setSs_cd((String) obj[1]);
            feeGrp.setSs_fee_grp_id((Integer) obj[2]);
            feeGrp.setFee_grp_nm_en((String) obj[3]);
            feeGrp.setFee_grp_nm_bm((String) obj[4]);
            feeGrp.setDtModified((Date) obj[5]);
            feeGrp.setModifiedBy((String) obj[6]);
            feeGrp.setStatus((String) obj[7]);
            feeGrp.setStatus_en((String) obj[8]);
            feeGrp.setStatus_bm((String) obj[9]);
            feeGrp.setTotal((Integer) obj[10]);
            feeGrpList.add(feeGrp);
        }

        return feeGrpList;
    }

    @Override
    // public Integer sp_insfeegroup(String i_fee_grp_nm_en, String i_fee_grp_nm_bm,
    // String i_created_by, String i_modified_by, String i_status) {
    public Integer sp_insfeegroup(FeeGrpRequest feeGroupRequest, String i_created_by, String i_modified_by,
            String i_status) {
        Integer result = 0;
        try {
            // result = storeProcedureRepository.sp_insfeegroup(i_fee_grp_nm_en,
            // i_fee_grp_nm_bm, i_created_by,
            // i_modified_by, i_status);
            result = feeGroupRepository.sp_insfeegroup(feeGroupRequest, i_created_by, i_modified_by, i_status);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    // public Integer sp_updfeegroup(Long i_fee_grp_id, String i_fee_grp_nm_en,
    // String i_fee_grp_nm_bm,
    // String i_modified_by, String i_status) {
    public Integer sp_updfeegroup(FeeGrpRequest feeGroupRequest, String i_fee_grp_nm_en, String i_fee_grp_nm_bm,
            String i_modified_by, String i_status) {
        Integer result = 0;
        try {
            result = feeGroupRepository.sp_updfeegroup(feeGroupRequest, i_fee_grp_nm_en, i_fee_grp_nm_bm,
                    i_modified_by, i_status);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Integer sp_checkfeegrpbyid(FeeGrpRequest feeGroupRequest) {
        Integer result = 0;
        try {
            result = feeGroupRepository.sp_checkfeegrpbyid(feeGroupRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // #endregion
}
