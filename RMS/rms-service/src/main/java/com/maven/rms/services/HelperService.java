package com.maven.rms.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IHelperService;
import com.maven.rms.repositories.HelperRepository;

import com.maven.rms.models.BranchCodeList;
import com.maven.rms.models.BranchCodeListRequest;

import com.maven.rms.models.MFT;
import com.maven.rms.models.FeeDetailListRequest;

import com.maven.rms.models.Param;
import com.maven.rms.models.ParamListRequest;

@Service
public class HelperService implements IHelperService{

    private final HelperRepository helperRepository;

    public HelperService(HelperRepository helperRepository)
    {
        this.helperRepository = helperRepository;
    }
    
    @Override
    public List<BranchCodeList> sp_getbcccodes(BranchCodeListRequest getRequest) {
        List<BranchCodeList> result = Collections.emptyList();
   
            List<Object[]> objects = helperRepository.sp_getbcccodes(getRequest);
            result = convertBranchCodeCounterList(objects);

        return result;
    }

    private List<BranchCodeList> convertBranchCodeCounterList(List<Object[]> objects) {
        List<BranchCodeList> branchCodeListArray = new ArrayList<>();

        for (Object[] obj : objects) {
            BranchCodeList branchCodeList = new BranchCodeList();
            branchCodeList.setBcm_id((Integer) obj[0]);
            branchCodeList.setBcm_code((String) obj[1]);
            branchCodeListArray.add(branchCodeList);
        }

        return branchCodeListArray;
    }

    @Override
    public List<MFT> sp_getfeedetailids(FeeDetailListRequest getRequest) {
        List<MFT> result = Collections.emptyList();
   
            List<Object[]> objects = helperRepository.sp_getfeedetailids(getRequest);
            result = convertFeeDetailList(objects);

        return result;
    }

    private List<MFT> convertFeeDetailList(List<Object[]> objects) {
        List<MFT> feeDetailListArray = new ArrayList<>();

        for (Object[] obj : objects) {
            MFT feeDetailList = new MFT();
            feeDetailList.setFee_detail_pk((Integer) obj[0]);
            feeDetailList.setFee_detail_id((String) obj[1]);
            feeDetailList.setFee_detail_nm_e((String) obj[2]);
            feeDetailList.setFee_detail_nm_b((String) obj[3]);
            feeDetailListArray.add(feeDetailList);
        }

        return feeDetailListArray;
    }

    @Override
    public List<Param> sp_getparamsbygroup(ParamListRequest getRequest) {
        List<Param> result = Collections.emptyList();

            List<Object[]> objects = helperRepository.sp_getparamsbygroup(getRequest);
            result = convertParamList(objects);

        return result;
    }

    private List<Param> convertParamList(List<Object[]> objects) {
        List<Param> paramListArray = new ArrayList<>();

        for (Object[] obj : objects) {
            Param paramList = new Param();
            paramList.setParam_cd((String) obj[0]);
            paramList.setNm_en((String) obj[1]);
            paramList.setNm_bm((String) obj[2]);
            paramListArray.add(paramList);
        }

        return paramListArray;
    }

}
