package com.maven.rms.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IFMSJournalInterface;
import com.maven.rms.models.FMSJournal;

@Repository
public class FMSJournalRepository implements IFMSJournalInterface{
     @PersistenceContext
     private EntityManager entityManager;

    @Override
    public List<Object[]> sp_getfmsjn(){
            Query query = entityManager.createNativeQuery(
            "CALL sp_getfmsjn()"
            );
  
          return query.getResultList();
    };

//     @Override
//     public Integer sp_updfmsjn(String i_attr_ext_ref_no, String i_resp_attr_ext_sys, String i_fms_ref_no, 
//                         String i_resp_ext_ref_no, String i_resp_status, String i_resp_msg, String i_resp_dt){
        
//         // Define the expected format of the input date-time string
//         //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

//         // Parse the input string to LocalDateTime
//         //LocalDateTime f_resp_dt = LocalDateTime.parse(i_resp_dt, formatter);
        
//         Query query = entityManager.createNativeQuery(
//         "CALL sp_updfmsjn(:i_attr_ext_ref_no, :i_resp_attr_ext_sys, :i_fms_ref_no, :i_resp_ext_ref_no, :i_resp_status,:i_resp_msg, :i_resp_dt)"
// )
//             .setParameter("i_attr_ext_ref_no", i_attr_ext_ref_no)
//             .setParameter("i_resp_attr_ext_sys", i_resp_attr_ext_sys)
//             .setParameter("i_fms_ref_no", i_fms_ref_no)
//             .setParameter("i_resp_ext_ref_no", i_resp_ext_ref_no)
//             .setParameter("i_resp_status", i_resp_status)
//             .setParameter("i_resp_msg", i_resp_msg)
//             .setParameter("i_resp_dt", i_resp_dt);

//           Integer result = (Integer) query.getSingleResult();
//           return result;
//     }
        @Override
        public Integer sp_updfmsjn(FMSJournal fmsJournal) {

          // Define the expected format of the input date-time string
          // DateTimeFormatter formatter =
          // DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

          // Parse the input string to LocalDateTime
          // LocalDateTime f_resp_dt = LocalDateTime.parse(i_resp_dt, formatter);

          Query query = entityManager.createNativeQuery(
              "CALL sp_updfmsjn(:i_attr_ext_ref_no, :i_resp_attr_ext_sys, :i_fms_ref_no, :i_resp_ext_ref_no, :i_resp_status,:i_resp_msg, :i_resp_dt)")
              .setParameter("i_attr_ext_ref_no", fmsJournal.getAttr_ext_ref_no())
              .setParameter("i_resp_attr_ext_sys", fmsJournal.getAttr_ext_sys())
              .setParameter("i_fms_ref_no", fmsJournal.getBatchNbr())
              .setParameter("i_resp_ext_ref_no", fmsJournal.getExtRefNbr())
              .setParameter("i_resp_status", fmsJournal.getStatus())
              .setParameter("i_resp_msg", fmsJournal.getMessage())
              .setParameter("i_resp_dt", fmsJournal.getDate());

          Integer result = (Integer) query.getSingleResult();
          return result;
        }
}
