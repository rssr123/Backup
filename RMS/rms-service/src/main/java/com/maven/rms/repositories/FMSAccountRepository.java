package com.maven.rms.repositories;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.maven.rms.interfaces.IFMSAccountInterface;
import com.maven.rms.models.FMSAccount;
import com.maven.rms.models.FMSAccountRequest;
import com.maven.rms.services.AuthService;
import com.maven.rms.utils.SystemStatus;

@Repository
public class FMSAccountRepository implements IFMSAccountInterface{

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AuthService authService;

    // @Override
    // public List<Object[]> sp_getfmsaccount(Integer i_page, Integer i_size,
    //                                            String i_acct_nm, String i_acct_type, String i_acct_cd,
    //                                            String i_modified_by, Date i_dt_modified) {
    //     Query query = entityManager.createNativeQuery(
    //             "CALL sp_getfmsaccount(:i_page, :i_size, :i_acct_nm, :i_acct_type, :i_acct_cd, :i_modified_by, :i_dt_modified)")
    //             .setParameter("i_page", i_page)
    //             .setParameter("i_size", i_size)
    //             .setParameter("i_acct_nm", i_acct_nm)
    //             .setParameter("i_acct_type", i_acct_type)
    //             .setParameter("i_acct_cd", i_acct_cd)
    //             .setParameter("i_modified_by", i_modified_by)
    //             .setParameter("i_dt_modified", i_dt_modified);

    //     // if (i_dt_modified != null) {
    //     //     query.setParameter("i_dt_modified", i_dt_modified);
    //     // } else {
    //     //     query.setParameter("i_dt_modified", null);
    //     // }

    //     return query.getResultList();
    // }

    @Override
    public List<Object[]> sp_getfmsaccount(FMSAccountRequest fmsAccountRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getfmsaccount(:i_page, :i_size, :i_acct_nm, :i_acct_type, :i_acct_cd, :i_modified_by, :i_dt_modified, :i_dt_modified_fr, :i_dt_modified_to)")
                .setParameter("i_page", fmsAccountRequest.getI_page())
                .setParameter("i_size", fmsAccountRequest.getI_size())
                .setParameter("i_acct_nm", fmsAccountRequest.getI_acct_nm())
                .setParameter("i_acct_type", fmsAccountRequest.getI_acct_type())
                .setParameter("i_acct_cd", fmsAccountRequest.getI_acct_cd())
                .setParameter("i_modified_by", fmsAccountRequest.getI_modified_by())
                .setParameter("i_dt_modified", fmsAccountRequest.getI_dt_modified())
                .setParameter("i_dt_modified_fr", fmsAccountRequest.getI_dt_modified_fr())
                .setParameter("i_dt_modified_to", fmsAccountRequest.getI_dt_modified_to());

        return query.getResultList();
    }

    @Override
    public Integer sp_updfmsaccount(FMSAccountRequest fmsAccountRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_updfmsaccount(:i_fms_acct_id, :i_acct_nm, :i_acct_type, :i_acct_cd, :i_modified_by, :i_status)")
                .setParameter("i_fms_acct_id", fmsAccountRequest.getI_fms_acct_id())
                .setParameter("i_acct_nm", fmsAccountRequest.getI_acct_nm())
                .setParameter("i_acct_type", fmsAccountRequest.getI_acct_type())
                .setParameter("i_acct_cd", fmsAccountRequest.getI_acct_cd())
                .setParameter("i_modified_by", authService.getLoginUserName())
                .setParameter("i_status", fmsAccountRequest.getI_status());

        return (Integer) query.getSingleResult();
    }
}


// package com.maven.rms.repositories;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;
// import com.maven.rms.models.FMSAccount;

// @Repository
// public interface FMSAccountRepository extends JpaRepository<FMSAccount, Integer>, FMSAccountRepositoryCustom {
// }
