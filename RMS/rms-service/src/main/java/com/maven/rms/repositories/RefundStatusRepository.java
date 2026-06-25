package com.maven.rms.repositories;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;
import com.maven.rms.interfaces.IRefundStatusRepository;
import com.maven.rms.models.RefundStatusRequest;

@Repository
public class RefundStatusRepository implements IRefundStatusRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> getRefundStatus(RefundStatusRequest refundStatusRequest) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_getrsdr(:i_date_range_start, :i_date_range_end, :i_refund_status, :i_refund_ty)")
            .setParameter("i_date_range_start", refundStatusRequest.getDateStart() != null ? refundStatusRequest.getDateStart() : null)
            .setParameter("i_date_range_end", refundStatusRequest.getDateEnd() != null ? refundStatusRequest.getDateEnd() : null)
            .setParameter("i_refund_status", refundStatusRequest.getRefundStatus() != null ? refundStatusRequest.getRefundStatus() : null)
            .setParameter("i_refund_ty", refundStatusRequest.getRefundType() != null ? refundStatusRequest.getRefundType() : null);

        return query.getResultList();
    }
}
