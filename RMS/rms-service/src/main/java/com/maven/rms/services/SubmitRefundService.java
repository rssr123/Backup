package com.maven.rms.services;

import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.ISubmitRefundService;
import com.maven.rms.models.PaymentItemDetails;
import com.maven.rms.models.PaymentRequest;
import com.maven.rms.models.RefundRequest;
import com.maven.rms.models.RefundWFList;
import com.maven.rms.repositories.ISubmitRefundRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubmitRefundService implements ISubmitRefundService {

    private final ISubmitRefundRepository submitRefundRepository;

    public SubmitRefundService(ISubmitRefundRepository submitRefundRepository) {
        this.submitRefundRepository = submitRefundRepository;
    }

    @Override
    public String sp_insrefund_ss(RefundRequest refundRequest) {
        // Call repository method to insert refund request
        return submitRefundRepository.sp_insrefund_ss(refundRequest);
    }

    
       public List<PaymentItemDetails> getRefundPaymentItems(Integer mttId) {
        List<Object[]> rows = submitRefundRepository.sp_getRefundPaymentItem(mttId);

        return rows.stream()
            .map(r -> {
                PaymentItemDetails d = new PaymentItemDetails();
                d.setRtt_item_id( toInt(r[0]) );
                d.setItem_desc(  asString(r[1]) );
                d.setQty(        toInt(r[2]) );
                d.setUnit_fee(   toBigDecimal(r[3]) );
                d.setTax_amt(    toBigDecimal(r[4]) );
                d.setTax_pct(    toBigDecimal(r[5]) );
                d.setGrant_cd(   asString(r[6]) );
                d.setDisc_amt(   toBigDecimal(r[7]) );
                d.setGross_amt(  toBigDecimal(r[8]) );
                d.setItem_ref_no(asString(r[9]) );
                
                d.setNet_amt(    toBigDecimal(r[11]) );
                d.setEntity_type(asString(r[12]) );
                d.setEntity_no(  asString(r[13]) );
                d.setEntity_nm(  asString(r[14]) );
                return d;
            })
            .collect(Collectors.toList());
    }

    // helpers to cast safely:
    private Integer toInt(Object o) {
        return o == null ? null : ((Number)o).intValue();
    }
    private String asString(Object o) {
        return o == null ? null : o.toString();
    }
    private BigDecimal toBigDecimal(Object o) {
        return o == null ? null : (BigDecimal)o;
    }

     @Override
    public Integer sp_insertRttItem(RefundRequest refundRequest, PaymentRequest paymentRequest) {
        Integer rttWfId = refundRequest.getRtt_wf_id();
        Integer rtt_item_id = 0;
        String finalResult = "";

        try {
            String createdBy = refundRequest.getCreated_by();
            String modifiedBy = refundRequest.getModified_by();

            if (rttWfId > 0) {
                List<PaymentItemDetails> itemDetailsList = paymentRequest.getPayment_item_details();

                for (PaymentItemDetails item : itemDetailsList) {
                    System.out.println("Inserting refund item: " + item);

                    rtt_item_id = submitRefundRepository.sp_insertRefundItem(item, rttWfId, createdBy, modifiedBy);

                    if (rtt_item_id < 1) {
                        finalResult = "RTT Item table insert failed for item_ref_no: " + item.getItem_ref_no();
                        System.out.println(finalResult);
                        throw new Exception(finalResult);
                    }
                    System.out.println("Successfully inserted item_ref_no: " + item.getItem_ref_no());
                }

                finalResult = "Insert successful";
                System.out.println(finalResult);
            } else {
                finalResult = rttWfId < 0 ? "Update successful" : "RTT table insert failed";
                System.out.println(finalResult);
                throw new Exception(finalResult);
            }

        } catch (Exception e) {
            System.out.println("Error during refund request processing: " + e.getMessage());
            e.printStackTrace();
            rttWfId = -1; // Indicate failure
        }

        return rttWfId;
    }
}