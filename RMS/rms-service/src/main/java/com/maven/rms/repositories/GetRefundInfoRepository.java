package com.maven.rms.repositories;

import com.maven.rms.models.GetRefundInfo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.sql.Blob;
import java.sql.SQLException;

@Repository
public class GetRefundInfoRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<GetRefundInfo> sp_getrefundinfo(String ornNo, String appNo) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getrefundinfo(:orn_no, :app_no)")
                .setParameter("orn_no", ornNo)
                .setParameter("app_no", appNo);

        List<Object[]> results = query.getResultList();
        List<GetRefundInfo> refundInfoList = new ArrayList<>();

        for (Object[] result : results) {
            GetRefundInfo refundInfo = new GetRefundInfo();
            refundInfo.setAppNo((String) result[0]);
            refundInfo.setAppStatus((String) result[1]);
            refundInfo.setAppMsg((String) result[2]);
            refundInfo.setAppRejectedReason((String) result[3]);
            refundInfo.setSlipNo((String) result[4]);
            Blob fileBlob = (Blob) result[5];
            if (fileBlob != null) {
                try {
                    byte[] fileContent = fileBlob.getBytes(1, (int) fileBlob.length());
                    refundInfo.setFile(fileContent);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            refundInfoList.add(refundInfo);
        }

        return refundInfoList;
    }
}