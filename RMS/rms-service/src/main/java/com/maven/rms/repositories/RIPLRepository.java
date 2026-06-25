package com.maven.rms.repositories;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IRIPLInterface;
import com.maven.rms.models.RIPLRealizedRequest;
import com.maven.rms.models.RIPLRecognitionRequest;
import com.maven.rms.models.RIPLRequest;

@Repository
public class RIPLRepository implements IRIPLInterface{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
//     public BigInteger sp_insRIPL(String i_txn_type, String i_entity_type, String i_entity_no, String i_calendar_yr, 
//                           String i_dt_due, String i_ripl_ctype, String i_created_by, String i_modified_by) {
    public BigInteger sp_insRIPL(RIPLRecognitionRequest riplRecognitionRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_insRIPL(:i_txn_type, :i_entity_type, :i_entity_no, :i_calendar_yr, :i_dt_due, :i_ripl_ctype, :i_created_by, :i_modified_by, :i_ss_cd)")
                .setParameter("i_txn_type", riplRecognitionRequest.getTxn_type())
                .setParameter("i_entity_type", riplRecognitionRequest.getEntity_type())
                .setParameter("i_entity_no", riplRecognitionRequest.getEntity_no())
                .setParameter("i_calendar_yr", riplRecognitionRequest.getCalendar_yr())
                .setParameter("i_dt_due", riplRecognitionRequest.getDt_due())
                .setParameter("i_ripl_ctype", riplRecognitionRequest.getRipl_ctype())
                .setParameter("i_created_by", riplRecognitionRequest.getCreated_by())
                .setParameter("i_modified_by", riplRecognitionRequest.getModified_by())
                .setParameter("i_ss_cd", riplRecognitionRequest.getSs_cd());

        BigInteger result = (BigInteger) query.getSingleResult();
        return result;
    }

    @Override
//     public BigInteger sp_updRIPL(String i_txn_type, String i_entity_type, String i_entity_no, String i_calendar_yr, 
//                           String i_rcpt_no, String i_modified_by) {
    public BigInteger sp_updRIPL(RIPLRealizedRequest riplRealizedRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_updRIPL(:i_txn_type, :i_entity_type, :i_entity_no, :i_calendar_yr, :i_rcpt_no,:i_modified_by)")
                .setParameter("i_txn_type", riplRealizedRequest.getTxn_type())
                .setParameter("i_entity_type", riplRealizedRequest.getEntity_type())
                .setParameter("i_entity_no", riplRealizedRequest.getEntity_no())
                .setParameter("i_calendar_yr", riplRealizedRequest.getCalendar_yr())
                .setParameter("i_rcpt_no", riplRealizedRequest.getRcpt_no())
                .setParameter("i_modified_by", riplRealizedRequest.getModified_by());
                // .setParameter("i_txn_type", i_txn_type)
                // .setParameter("i_entity_type", i_entity_type)
                // .setParameter("i_entity_no", i_entity_no)
                // .setParameter("i_calendar_yr", i_calendar_yr)
                // .setParameter("i_rcpt_no", i_rcpt_no)
                // .setParameter("i_modified_by", i_modified_by);

        BigInteger result = (BigInteger) query.getSingleResult();
        return result;
    }
    
    @Override
    public List<Object[]> sp_getRIPL(RIPLRequest RIPLRequest) {
//     public List<Object[]> sp_getRIPL(Integer i_page,Integer i_size,BigInteger i_ripl_id, String i_txn_type, String i_entity_type, String i_entity_no, String i_calendar_yr, 
//                           String i_dt_due, String i_ripl_ctype, String i_status) {
   
        Query query = entityManager.createNativeQuery(
                "CALL sp_getRIPL(:i_page,:i_size,:i_ripl_id,:i_txn_type, :i_entity_type, :i_entity_no, :i_calendar_yr, :i_dt_due, :i_ripl_ctype, :i_status)")
                .setParameter("i_page", RIPLRequest.getI_page())
                .setParameter("i_size", RIPLRequest.getI_size())
                .setParameter("i_ripl_id", RIPLRequest.getI_ripl_id())
                .setParameter("i_txn_type", RIPLRequest.getI_txn_type())
                .setParameter("i_entity_type", RIPLRequest.getI_entity_type())
                .setParameter("i_entity_no", RIPLRequest.getI_entity_no())
                .setParameter("i_calendar_yr", RIPLRequest.getI_calendar_yr())
                .setParameter("i_dt_due", RIPLRequest.getI_dt_due())
                .setParameter("i_ripl_ctype", RIPLRequest.getI_ripl_ctype())
                .setParameter("i_status", RIPLRequest.getI_status());
        return query.getResultList();
    }

    // @Override
    // public List<Object[]> sp_getRIPLID() {
    //     Query query = entityManager.createNativeQuery("CALL sp_getRIPLID()");

    //     return query.getResultList();
    // }

    
    @Override
    public BigInteger sp_updRIPLImpairStatus() {
        Query query = entityManager.createNativeQuery(
                "CALL sp_updRIPLImpairStatus()");

        BigInteger result = (BigInteger) query.getSingleResult();
        return result;
    }

    
    @Override
    public BigInteger sp_updRIPLWriteOffStatus() {
        Query query = entityManager.createNativeQuery(
                "CALL sp_updRIPLWriteOffStatus()");

        BigInteger result = (BigInteger) query.getSingleResult();
        return result;
    }

    public Integer sp_checkExist(String entityType,String entity,String txnType, String calendarYr){
        Query query = entityManager.createNativeQuery(
                "CALL sp_checkRIPLExist(:entityType,:entity,:txnType,:calendarYr)")
                .setParameter("entityType", entityType)
                .setParameter("entity", entity)
                .setParameter("txnType", txnType)
                .setParameter("calendarYr", calendarYr);
        return (Integer) query.getSingleResult();
    }
}
