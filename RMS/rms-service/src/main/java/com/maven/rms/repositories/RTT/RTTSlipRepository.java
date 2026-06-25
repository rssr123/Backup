package com.maven.rms.repositories.RTT;

import com.maven.rms.models.RTT.RTTSlipRequest;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Repository
public class RTTSlipRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public RTTSlipRequest getSlipData(RTTSlipRequest rttslipRequest) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_getrttslipinfo(:rtt_wf_id_input)")
            .setParameter("rtt_wf_id_input", rttslipRequest.getRttWfId());

        Object[] result = (Object[]) query.getSingleResult();
        RTTSlipRequest slipData = new RTTSlipRequest();
        slipData.setRttWfId((Integer) result[0]);
        slipData.setRttAppNo((String) result[1]);
        slipData.setSlipNo((String) result[2]);
        slipData.setCustNm((String) result[3]);
        slipData.setEntNo((String) result[4]);
        slipData.setCustPhone((String) result[5]);
        slipData.setCustEmail((String) result[6]);
        slipData.setRmsType((String) result[7]);
        slipData.setRefundTy((String) result[8]);
        slipData.setCustState((String) result[9]);
        slipData.setRefundReason((String) result[10]);
        slipData.setRcptNo((String) result[11]);
        slipData.setOrnNo((String) result[12]);
        slipData.setTxnId((String) result[13]);
        slipData.setRefundAmt((BigDecimal) result[14]);

        return slipData;
    }
}