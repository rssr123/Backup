package com.maven.rms.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IBillingTypeService;
import com.maven.rms.models.BillingClass;
import com.maven.rms.models.BillingType;
import com.maven.rms.models.BillingTypeRequest;
import com.maven.rms.repositories.BillingTypeRepository;

@Service
public class BillingTypeService implements IBillingTypeService {

    @Autowired
    private BillingTypeRepository billingTypeRepository;

    @Override
    public List<BillingType> getBillingType(BillingTypeRequest billingTypeRequest) {
        List<Object[]> objects = billingTypeRepository.getBillingType(billingTypeRequest);
        return convertToBillingTypeList(objects);
    }

    // private List<BillingType> convertToBillingTypeList(List<Object[]> objects) {
    //     List<BillingType> billingTypeList = new ArrayList<>();

    //     for (Object[] obj : objects) {
    //         BillingType billingType = new BillingType();
    //         billingType.setBtCd((String) obj[0]);
    //         billingType.setBtTy((String) obj[1]);
    //         billingType.setBtDesc((String) obj[2]);
    //         billingType.setClassId((String) obj[3]);
    //         billingType.setSsCd((String) obj[4]);
    //         billingType.setMftPk((Integer) obj[5]);
    //         billingType.setMftId((String) obj[6]);
    //         billingType.setDpsMftPk((Integer) obj[7]);
    //         billingType.setDpsMftId((String) obj[8]);
    //         billingType.setDtCreated((Date) obj[9]);
    //         billingType.setDtModified((Date) obj[10]);
    //         billingType.setCreatedBy((String) obj[11]);
    //         billingType.setModifiedBy((String) obj[12]);
    //         billingType.setStatus((String) obj[13]);
    //         billingType.setTotal((Integer) obj[14]);
    //         billingTypeList.add(billingType);
    //     }

    //     return billingTypeList;
    // }

    private List<BillingType> convertToBillingTypeList(List<Object[]> objects) {
        Map<Integer, BillingType> map = new LinkedHashMap<>();

        for (Object[] obj : objects) {
            // String btCd = (String) obj[0];
            Integer bltc_id = (Integer) obj[0];

            // BillingType billingType = map.get(btCd);
            BillingType billingType = map.get(bltc_id);
            if (billingType == null) {
                billingType = new BillingType();
                billingType.setBltc_id((Integer) obj[0]);
                billingType.setBtCd((String) obj[1]);
                billingType.setBtTy((String) obj[2]);
                billingType.setBtDesc((String) obj[3]);
                billingType.setClassId((String) obj[4]);
                billingType.setSsCd((String) obj[5]);
                billingType.setDtCreated((Date) obj[10]);
                billingType.setDtModified((Date) obj[11]);
                billingType.setCreatedBy((String) obj[12]);
                billingType.setModifiedBy((String) obj[13]);
                billingType.setStatus((String) obj[14]);
                billingType.setTotal((Integer) obj[15]);

                map.put(bltc_id, billingType);
            }

            // Add mft/dps item
            BillingType.MftDpsItem item = new BillingType.MftDpsItem();
            item.setMftPk((Integer) obj[6]);
            item.setMftId((String) obj[7]);
            item.setDpsMftPk((Integer) obj[8]);
            item.setDpsMftId((String) obj[9]);

            billingType.getItems().add(item);
        }

        return new ArrayList<>(map.values());
    }

    @Override
    public Integer sp_insbltc(BillingTypeRequest billingTypeRequest) {
        return billingTypeRepository.sp_insbltc(billingTypeRequest);
    }

    @Override
    public Integer sp_insbltcitem(List<BillingTypeRequest> billingTypeRequest) {
        return billingTypeRepository.sp_insbltcitem(billingTypeRequest);
    }

    @Override
    public Integer sp_updbltcitem(List<BillingTypeRequest> billingTypeRequest) {
        return billingTypeRepository.sp_updbltcitem(billingTypeRequest);
    }

    @Override
    public Integer sp_updatebltc(BillingTypeRequest billingTypeRequest) {
        return billingTypeRepository.sp_updatebltc(billingTypeRequest);
    }

    @Override
    public Integer sp_delbltc(BillingTypeRequest billingTypeRequest) {
        return billingTypeRepository.sp_delbltc(billingTypeRequest);
    }

    @Override
    public List<BillingClass> sp_getnblcm() {
        List<BillingClass> result = Collections.emptyList();
        try {

            List<Object[]> objects = billingTypeRepository.sp_getnblcm();
            result = convertToGetNBCM(objects);

        } catch (Exception e) {
            e.printStackTrace();

        }
        return result;
    }

    private List<BillingClass> convertToGetNBCM(List<Object[]> objects) {
        List<BillingClass> nbltcs = new ArrayList<>();

        for (Object[] obj : objects) {
            // Create a new sourceSystemCode instance using the extracted values
            BillingClass nbltc = new BillingClass();
            nbltc.setClassId((String) obj[0]);
            nbltc.setClassDesc((String) obj[1]);
            nbltcs.add(nbltc);
        }
        return nbltcs;
    }
}
