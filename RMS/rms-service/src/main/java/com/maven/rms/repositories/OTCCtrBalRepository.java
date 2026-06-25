package com.maven.rms.repositories;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IOTCCtrBalInfoInterface;

@Repository
public class OTCCtrBalRepository implements IOTCCtrBalInfoInterface{
    @PersistenceContext
     private EntityManager entityManager;

     @Override
     public List<Object[]> sp_getotcbalctrinfo(String i_counter_id, BigInteger i_otc_counter_id) 
     {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getotcctrbalinfo(:i_counter_id, :i_otc_counter_id)"
                    )
                    .setParameter("i_counter_id", i_counter_id)
                    .setParameter("i_otc_counter_id", i_otc_counter_id);

          return query.getResultList();
     }

     @Override
     public List<Object[]> sp_getotcrmscol(Integer i_page, Integer i_size, String i_counter_id, BigInteger i_otc_counter_id) 
     {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getotcctrrmscol(:i_page, :i_size, :i_counter_id, :i_otc_counter_id)"
                    )
                    .setParameter("i_page", i_page)
                    .setParameter("i_size", i_size)
                    .setParameter("i_counter_id", i_counter_id)
                    .setParameter("i_otc_counter_id", i_otc_counter_id);

          return query.getResultList();
     }

     @Override
     public List<Object[]> sp_getotcctrcol(Integer i_page, Integer i_size, String i_counter_id, BigInteger i_otc_counter_id) 
     {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getotcctrcol(:i_page, :i_size, :i_counter_id, :i_otc_counter_id)"
                    )
                    .setParameter("i_page", i_page)
                    .setParameter("i_size", i_size)
                    .setParameter("i_counter_id", i_counter_id)
                    .setParameter("i_otc_counter_id", i_otc_counter_id);

          return query.getResultList();
     }

     @Override
     public List<Object[]> sp_getotcphyinfo(String i_counter_id, BigInteger i_otc_counter_id) 
     {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getotcctrphyinfo(:i_counter_id, :i_otc_counter_id)"
                    )
                    .setParameter("i_counter_id", i_counter_id)
                    .setParameter("i_otc_counter_id", i_otc_counter_id);
                    

          return query.getResultList();
     }

     @Override
     public List<Object[]> sp_getotccashinfo(BigInteger i_otc_counter_id)
     {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getotccashinfo(:i_otc_counter_id)"
                    )
                    .setParameter("i_otc_counter_id", i_otc_counter_id);
                    
          return query.getResultList();
     }

}
