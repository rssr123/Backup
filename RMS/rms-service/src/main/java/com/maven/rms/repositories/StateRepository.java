package com.maven.rms.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import com.maven.rms.interfaces.IStateRepository;
import com.maven.rms.models.StateRequest;
import com.maven.rms.services.AuthService;

@Repository
public class StateRepository implements IStateRepository {
    @Autowired
    private AuthService authService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> getState(StateRequest stateRequest) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_getstates(:i_page, :i_size, :i_param_id, :i_param_cd, :i_nm_en, :i_nm_bm, :i_param_grp_nm, :i_seq, :i_status)")
            .setParameter("i_page", stateRequest.getI_page())
            .setParameter("i_size", stateRequest.getI_size())
            .setParameter("i_param_id", stateRequest.getI_param_id() != null ? stateRequest.getI_param_id() : null)
            .setParameter("i_param_cd", stateRequest.getI_param_cd() != null ? stateRequest.getI_param_cd() : null)
            .setParameter("i_nm_en", stateRequest.getI_nm_en() != null ? stateRequest.getI_nm_en() : null)
            .setParameter("i_nm_bm", stateRequest.getI_nm_bm() != null ? stateRequest.getI_nm_bm() : null)
            .setParameter("i_param_grp_nm", stateRequest.getI_param_grp_nm() != null ? stateRequest.getI_param_grp_nm() : null)
            .setParameter("i_seq", stateRequest.getI_seq() != null ? stateRequest.getI_seq() : null)
            .setParameter("i_status", stateRequest.getI_status() != null ? stateRequest.getI_status() : null);

        return query.getResultList();
    }
}
