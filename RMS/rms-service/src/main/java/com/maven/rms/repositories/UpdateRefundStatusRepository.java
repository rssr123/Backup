package com.maven.rms.repositories;

import com.maven.rms.interfaces.IUpdateRefundStatus;
import com.maven.rms.models.RefundStatusResult;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public class UpdateRefundStatusRepository implements IUpdateRefundStatus {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public RefundStatusResult updateRefundStatus(String app_no, String reject_reason) {
        try {
            Query query = entityManager.createNativeQuery(
                    "CALL sp_updaterefundstatus(:i_rtt_app_no, :i_reject_reason)")
                    .setParameter("i_rtt_app_no", app_no)
                    .setParameter("i_reject_reason", reject_reason);

            List<Object[]> resultList = query.getResultList();

            // Debug: Print the result to the console
            if (resultList != null && !resultList.isEmpty()) {
                System.out.println("Query Result:");
                for (Object[] row : resultList) {
                    System.out.println(Arrays.toString(row));
                }
                
                // Process the first row
                Object[] firstRow = resultList.get(0);
                String status = firstRow.length > 0 && firstRow[0] != null ? firstRow[0].toString() : "NA";
                String refundTy = firstRow.length > 1 && firstRow[1] != null ? firstRow[1].toString() : "NA";
                
                System.out.println("Parsed - Status: " + status + ", RefundTy: " + refundTy);
                
                return new RefundStatusResult(status, refundTy);
            } else {
                System.out.println("Query returned no results or null.");
                return new RefundStatusResult("NA", "NA");
            }
            
        } catch (Exception e) {
            System.err.println("Error in updateRefundStatus: " + e.getMessage());
            e.printStackTrace();
            return new RefundStatusResult("ERROR", "ERROR");
        }
    }
}