package com.maven.rms.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;
import com.maven.rms.interfaces.ICatalogueItemRepository;
import com.maven.rms.models.CatalogueItemRequest;

@Repository
public class CatalogueItemRepository implements ICatalogueItemRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> getCatalogueItem(CatalogueItemRequest catalogueItemRequest) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_getcatitem(:i_page, :i_size, :i_fee_detail_nm_e, :i_quantity)")
            .setParameter("i_page", catalogueItemRequest.getI_page())
            .setParameter("i_size", catalogueItemRequest.getI_size())
            .setParameter("i_fee_detail_nm_e", catalogueItemRequest.getI_fee_detail_nm_e())
            .setParameter("i_quantity", catalogueItemRequest.getI_quantity());

        return query.getResultList();
    }

    @Override
    public String sp_getctlrunno() {
         Query query = entityManager.createNativeQuery("CALL sp_getctlrunno()");
         return (String) query.getSingleResult();

    }
}
