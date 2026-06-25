package com.maven.rms.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.ICourtOrderService;
import com.maven.rms.models.CourtOrder;
import com.maven.rms.models.CourtOrderCaseInfo;
import com.maven.rms.models.CourtOrderDocs;
import com.maven.rms.models.CourtOrderHistory;
import com.maven.rms.models.CourtOrderPymtInfo;
import com.maven.rms.models.CourtOrderRequest;
import com.maven.rms.models.CourtOrderRmdrInfo;
import com.maven.rms.repositories.ICourtOrderRepository;
import com.maven.rms.interfaces.ICourtOrderService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CourtOrderService implements ICourtOrderService {

    private final ICourtOrderRepository courtOrderRepository;

    public CourtOrderService(ICourtOrderRepository courtOrderRepository) {
        this.courtOrderRepository = courtOrderRepository;
    }

    @Override
    public List<CourtOrder> sp_getcourtorderlisting(CourtOrderRequest courtOrderRequest) {
        List<CourtOrder> result = Collections.emptyList();

        List<Object[]> objects = courtOrderRepository.sp_getcourtorderlisting(courtOrderRequest);
        result = convertCourtOrderList(objects);

        return result;
    }

    private List<CourtOrder> convertCourtOrderList(List<Object[]> objects) {
        List<CourtOrder> courtOrderList = new ArrayList<>();

        for (Object[] obj : objects) {
            CourtOrder courtOrder = new CourtOrder();
            // t1.task_no, t1.task_status, t2.pymt_status, t2.txn_ty, t1.attr_case_no,
            // t1.assign_to, t2.pymt_amt
            courtOrder.setCc_case_id((Integer) obj[0]);
            courtOrder.setTask_no((String) obj[1]);
            courtOrder.setTask_status((String) obj[2]);
            courtOrder.setPymt_status((String) obj[3]);
            courtOrder.setTxn_ty((String) obj[4]);
            courtOrder.setAttr_case_no((String) obj[5]);
            courtOrder.setAssign_to((String) obj[6]);
            courtOrder.setPymt_amt((BigDecimal) obj[7]);
            courtOrder.setTotal((Integer) obj[8]);

            courtOrderList.add(courtOrder);
        }

        return courtOrderList;
    }

    // case info
    @Override
    public List<CourtOrderCaseInfo> sp_getcreditcontrolcaseinfo(CourtOrderRequest courtOrderRequest) {
        List<CourtOrderCaseInfo> result = Collections.emptyList();

        List<Object[]> objects = courtOrderRepository.sp_getcreditcontrolcaseinfo(courtOrderRequest);
        result = convertCourtOrderCaseInfoList(objects);

        return result;
    }

    private List<CourtOrderCaseInfo> convertCourtOrderCaseInfoList(List<Object[]> objects) {
        List<CourtOrderCaseInfo> courtOrderCaseInfoList = new ArrayList<>();

        for (Object[] obj : objects) {
            CourtOrderCaseInfo courtOrderCaseInfo = new CourtOrderCaseInfo();

            courtOrderCaseInfo.setCc_case_id((Integer) obj[0]);
            courtOrderCaseInfo.setCc_case_a_id((Integer) obj[1]);
            courtOrderCaseInfo.setCc_cs_item_id((Integer) obj[2]);
            courtOrderCaseInfo.setCust_nm((String) obj[3]);
            courtOrderCaseInfo.setCust_email((String) obj[4]);
            courtOrderCaseInfo.setCust_phone((String) obj[5]);
            courtOrderCaseInfo.setCust_addr_1((String) obj[6]);
            courtOrderCaseInfo.setCust_addr_2((String) obj[7]);
            courtOrderCaseInfo.setCust_addr_3((String) obj[8]);
            courtOrderCaseInfo.setCust_postcode((String) obj[9]);
            courtOrderCaseInfo.setCust_city((String) obj[10]);
            courtOrderCaseInfo.setCust_state((String) obj[11]);
            courtOrderCaseInfo.setAttr_case_no((String) obj[12]);
            courtOrderCaseInfo.setDt_assigned((Date) obj[13]);
            courtOrderCaseInfo.setFms_ari_ref_no((String) obj[14]);
            courtOrderCaseInfo.setPymt_status((String) obj[15]);
            courtOrderCaseInfo.setTxn_ty((String) obj[16]);
            courtOrderCaseInfo.setRef_no_txn((String) obj[17]);
            courtOrderCaseInfo.setCn_ref_no((String) obj[18]);
            courtOrderCaseInfo.setRcpt_no((String) obj[19]);
            courtOrderCaseInfo.setPymt_amt((BigDecimal) obj[20]);
            courtOrderCaseInfo.setTask_no((String) obj[21]);
            courtOrderCaseInfo.setInvoice_desc((String) obj[22]);
            courtOrderCaseInfo.setTask_status((String) obj[23]);

            courtOrderCaseInfoList.add(courtOrderCaseInfo);
        }

        return courtOrderCaseInfoList;
    }

    // payment item info
    @Override
    public List<CourtOrderPymtInfo> sp_getcourtorderpymtiteminfo(CourtOrderRequest courtOrderRequest) {
        List<CourtOrderPymtInfo> result = Collections.emptyList();

        List<Object[]> objects = courtOrderRepository.sp_getcourtorderpymtiteminfo(courtOrderRequest);
        result = convertCourtOrderPymtInfoList(objects);

        return result;
    }

    private List<CourtOrderPymtInfo> convertCourtOrderPymtInfoList(List<Object[]> objects) {
        List<CourtOrderPymtInfo> courtOrderPymtInfoList = new ArrayList<>();

        for (Object[] obj : objects) {
            CourtOrderPymtInfo courtOrderPymtInfo = new CourtOrderPymtInfo();

            courtOrderPymtInfo.setCc_cs_item_id((Integer) obj[0]);
            courtOrderPymtInfo.setCc_case_id((Integer) obj[1]);
            courtOrderPymtInfo.setRef_no_txn((String) obj[2]);
            courtOrderPymtInfo.setTxn_item_desc((String) obj[3]);
            courtOrderPymtInfo.setCn_qty((Integer) obj[4]);
            courtOrderPymtInfo.setCn_unit_price((BigDecimal) obj[5]);
            courtOrderPymtInfo.setCn_disc_amt((BigDecimal) obj[6]);
            courtOrderPymtInfo.setCn_amt((BigDecimal) obj[7]);
            courtOrderPymtInfo.setCn_amt_total((BigDecimal) obj[8]);

            courtOrderPymtInfoList.add(courtOrderPymtInfo);
        }

        return courtOrderPymtInfoList;
    }

    // court order reminder info
    @Override
    public List<CourtOrderRmdrInfo> sp_getcourtorderrmdrinfo(CourtOrderRequest courtOrderRequest) {
        List<CourtOrderRmdrInfo> result = Collections.emptyList();

        List<Object[]> objects = courtOrderRepository.sp_getcourtorderrmdrinfo(courtOrderRequest);
        result = convertCourtOrderRmdrInfoList(objects);

        return result;
    }

    private List<CourtOrderRmdrInfo> convertCourtOrderRmdrInfoList(List<Object[]> objects) {
        List<CourtOrderRmdrInfo> courtOrderRmdrInfoList = new ArrayList<>();

        for (Object[] obj : objects) {
            CourtOrderRmdrInfo courtOrderRmdrInfo = new CourtOrderRmdrInfo();

            courtOrderRmdrInfo.setCc_case_id((Integer) obj[0]);
            courtOrderRmdrInfo.setReminder_cnt((Integer) obj[1]);
            courtOrderRmdrInfo.setReminder_dt((Date) obj[2]);
            courtOrderRmdrInfo.setReminder_received_date((Date) obj[3]);
            courtOrderRmdrInfo.setReminder_email_content((String) obj[4]);

            courtOrderRmdrInfoList.add(courtOrderRmdrInfo);
        }

        return courtOrderRmdrInfoList;
    }

    // court order documents
    @Override
    public List<CourtOrderDocs> sp_getcourtorderdocs(CourtOrderRequest courtOrderRequest) {
        List<CourtOrderDocs> result = Collections.emptyList();

        List<Object[]> objects = courtOrderRepository.sp_getcourtorderdocs(courtOrderRequest);
        result = convertCourtOrderDocsList(objects);

        return result;
    }

    private List<CourtOrderDocs> convertCourtOrderDocsList(List<Object[]> objects) {
        List<CourtOrderDocs> courtOrderDocsList = new ArrayList<>();

        for (Object[] obj : objects) {
            CourtOrderDocs courtOrderDocs = new CourtOrderDocs();

            courtOrderDocs.setCc_doc_id((Integer) obj[0]);
            courtOrderDocs.setCc_case_id((Integer) obj[1]);
            courtOrderDocs.setFile_name((String) obj[2]);
            courtOrderDocs.setFile_type((String) obj[3]);
            courtOrderDocs.setFile_size_kb((Integer) obj[4]);
            courtOrderDocs.setDt_created((Date) obj[5]);
            courtOrderDocs.setCreated_by((String) obj[6]);

            courtOrderDocsList.add(courtOrderDocs);
        }

        return courtOrderDocsList;
    }


    //// court order history
    @Override
    public List<CourtOrderHistory> sp_getcourtorderhist(CourtOrderRequest courtOrderRequest) {
        List<CourtOrderHistory> result = Collections.emptyList();

        List<Object[]> objects = courtOrderRepository.sp_getcourtorderhist(courtOrderRequest);
        result = convertCourtOrderHistList(objects);

        return result;
    }

    private List<CourtOrderHistory> convertCourtOrderHistList(List<Object[]> objects) {
        List<CourtOrderHistory> courtOrderHistList = new ArrayList<>();

        for (Object[] obj : objects) {
            CourtOrderHistory courtOrderHistory = new CourtOrderHistory();

    

            courtOrderHistory.setCc_case_id((Integer) obj[0]);
            courtOrderHistory.setCc_msg_id((Integer) obj[1]);
            courtOrderHistory.setCc_case_a_id((Integer) obj[2]);
            courtOrderHistory.setMsg((String) obj[3]);
            courtOrderHistory.setMsg_type((String) obj[4]);
            courtOrderHistory.setDt_created((Date) obj[5]);
            courtOrderHistory.setDt_modified((Date) obj[6]);
            courtOrderHistory.setCreated_by((String) obj[7]);
            courtOrderHistory.setModified_by((String) obj[8]);
            courtOrderHistory.setPick_up((String) obj[9]);
            courtOrderHistory.setAssign_to((String) obj[10]);
            courtOrderHistory.setTask_status((String) obj[11]);

            courtOrderHistList.add(courtOrderHistory);
        }

        return courtOrderHistList;
    }

    //// blob file
    public String sp_getcccasedocblob(Integer ccDocId) throws SQLException, IOException {
        Blob data = courtOrderRepository.sp_getcccasedocblob(ccDocId);
        // String data = bRepo.sp_getunapprovedbilregdocblob(bilDocId);
        System.out.println("TEST: Data presence - " + data.length());

        /*
         * InputStream ins = data.getBinaryStream();
         * byte[] bytes = new byte[(int)data.length()];
         * int count = 0;
         * while(count != 1)
         * count = ins.read(bytes);
         */

        byte[] bytes = data.getBytes(1, (int) data.length());

        data.free();
        return Base64.getEncoder().encodeToString(bytes);
        // return data;
    }

}
