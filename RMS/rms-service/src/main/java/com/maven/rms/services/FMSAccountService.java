package com.maven.rms.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.maven.rms.models.FMSAccount;
import com.maven.rms.models.FMSAccountRequest;
import com.maven.rms.repositories.FMSAccountRepository;
import com.maven.rms.utils.APIResponse;

import java.sql.Timestamp;

@Service
@Slf4j
public class FMSAccountService {
    //private static final Logger logger = LoggerFactory.getLogger(FMSAccountService.class);

    private final FMSAccountRepository fmsAccountRepo;

    public FMSAccountService(FMSAccountRepository fmsAccountRepo) {
		this.fmsAccountRepo = fmsAccountRepo;
	}
    
    public Integer sp_updfmsaccount(FMSAccountRequest fmsAccountRequest) {
		return fmsAccountRepo.sp_updfmsaccount(fmsAccountRequest);
	}

    // public List<FMSAccount> sp_getfmsaccount(Integer i_page, Integer i_size, String i_acct_nm,
    //     String i_acct_type, String i_acct_cd, String i_modified_by,
    //     Date i_dt_modified) {
    //     List<FMSAccount> result = Collections.emptyList();

    //     try {
    //         List<Object[]> objects = fmsAccountRepo.sp_getfmsaccount(i_page, i_size,
    //             i_acct_nm, i_acct_type, i_acct_cd, i_modified_by, i_dt_modified);
    //         result = convertTFMSAccountList(objects);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    //     return result;
    // }

    public List<FMSAccount> sp_getfmsaccount(FMSAccountRequest fmsAccountRequest) {
        List<FMSAccount> result = Collections.emptyList();

        // try {            
            List<Object[]> objects = fmsAccountRepo.sp_getfmsaccount(fmsAccountRequest);
            result = convertTFMSAccountList(objects);
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }

        return result;
    }



    private List<FMSAccount> convertTFMSAccountList(List<Object[]> objects) {
        List<FMSAccount> fmsAccountList = new ArrayList<>();

        for (Object[] obj : objects) {
            FMSAccount fmsAccount = new FMSAccount();
            fmsAccount.setFms_acct_id((Integer) obj[0]);
            fmsAccount.setAcct_nm((String) obj[1]);
            fmsAccount.setAcct_type((String) obj[2]);
            fmsAccount.setAcct_cd((String) obj[3]);
            fmsAccount.setModified_by((String) obj[4]);
            fmsAccount.setDt_modified((Date) obj[5]);
            fmsAccount.setTotal((Integer) obj[6]);
            fmsAccountList.add(fmsAccount);
            log.debug(fmsAccount.toString());
            // System.out.println(fmsAccount.toString());
        }

        return fmsAccountList;
    }

}