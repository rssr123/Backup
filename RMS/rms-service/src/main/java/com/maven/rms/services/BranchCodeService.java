package com.maven.rms.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;
import com.maven.rms.interfaces.IBranchCodeService;
import com.maven.rms.models.BranchCode;
import com.maven.rms.models.BranchCodeRequest;
import com.maven.rms.models.BranchCodeUpdateRequest;
import com.maven.rms.repositories.BranchCodeRepository;
import com.maven.rms.models.BranchCodeAddRequest;
import com.maven.rms.models.BranchCodeDeleteRequest;

import java.util.Date;

@Service
public class BranchCodeService implements IBranchCodeService {

    private final BranchCodeRepository branchCodeRepository;

    public BranchCodeService(BranchCodeRepository branchCodeRepository) {
        this.branchCodeRepository = branchCodeRepository;
    }

    @Override
    public List<BranchCode> getBranchCodes(BranchCodeRequest branchCodeRequest) {
        List<BranchCode> result = Collections.emptyList();
        List<Object[]> objects = branchCodeRepository.getBranchCodes(branchCodeRequest);
        result = convertToBranchCodeList(objects);

        return result;
    }

    private List<BranchCode> convertToBranchCodeList(List<Object[]> objects) {
        List<BranchCode> branchCodeList = new ArrayList<>();

        for (Object[] obj : objects) {
            BranchCode branchCode = new BranchCode();
            branchCode.setBcm_id((Integer) obj[0]);
            branchCode.setCode((String) obj[1]);
            branchCode.setBcmTy((String) obj[2]);
            branchCode.setBcmDesc((String) obj[3]);
            branchCode.setDtCreated((Date) obj[4]);
            branchCode.setDtModified((Date) obj[5]);
            branchCode.setCreatedBy((String) obj[6]);
            branchCode.setModifiedBy((String) obj[7]);
            branchCode.setStatus((String) obj[8]);
            branchCode.setTotal((Integer) obj[9]);
            branchCodeList.add(branchCode);
        }

        return branchCodeList;
    }

   @Override
    public Integer sp_insbcm(BranchCodeAddRequest branchCodeAddRequest) {
        Integer result = 0;
        result = branchCodeRepository.sp_insbcm(branchCodeAddRequest);
        return result;
    }

    @Override
    public Integer sp_updatebcm(BranchCodeUpdateRequest updateRequest) {
        Integer result = 0;
        result = branchCodeRepository.sp_updatebcm(updateRequest);
        return result;
    }

    @Override
    public Integer sp_delbranchcode(BranchCodeDeleteRequest deleteRequest) {
        // Call the stored procedure through the repository
        return branchCodeRepository.sp_delbranchcode(deleteRequest);
    }
    
}
