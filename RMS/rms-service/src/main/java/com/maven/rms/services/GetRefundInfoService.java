package com.maven.rms.services;

import com.maven.rms.models.GetRefundInfo;
import com.maven.rms.repositories.GetRefundInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
public class GetRefundInfoService {

    @Autowired
    private GetRefundInfoRepository getRefundInfoRepository;

    public List<GetRefundInfo> getRefundInfo(String ornNo, String appNo) {
        List<GetRefundInfo> refundInfoList = getRefundInfoRepository.sp_getrefundinfo(ornNo, appNo);
        for (GetRefundInfo refundInfo : refundInfoList) {
            if (refundInfo.getFile() != null) {
                byte[] fileContent = refundInfo.getFile();
                String base64FileContent = Base64.getEncoder().encodeToString(fileContent);
                refundInfo.setFileBase64(base64FileContent);
            }
        }
        return refundInfoList;
    }
}