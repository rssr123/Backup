package com.maven.rms.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

import com.maven.rms.interfaces.IUnmatchedTransService;

import com.maven.rms.models.UnmatchTrans;
import com.maven.rms.models.UnmatchTransRequest;
import com.maven.rms.repositories.IUnmatchTransRepository;

@Service
@Slf4j
public class UnmatchTransService implements IUnmatchedTransService {
    //private static final Logger logger = LoggerFactory.getLogger(UnmatchTransService.class);

    private IUnmatchTransRepository unmatchTransRepository;

    public UnmatchTransService(IUnmatchTransRepository unmatchTransRepository) {
        this.unmatchTransRepository = unmatchTransRepository;

    }

    @Override
    public List<UnmatchTrans> sp_getutlmonths(UnmatchTransRequest unmatchedTransRequest) {
        List<UnmatchTrans> result = Collections.emptyList();

        // try {
            List<Object[]> objects = unmatchTransRepository.sp_getutlmonths(unmatchedTransRequest);
            result = convertUnmatchTransMonth(objects);
        // } catch (Exception e) {
        //     e.printStackTrace();
        //     log.error("Exception in " + this.getClass().toString(), e);
        // }

        return result;
    }

    private List<UnmatchTrans> convertUnmatchTransMonth(List<Object[]> objects) {
        List<UnmatchTrans> UnmatchTransList = new ArrayList<>();

        for (Object[] obj : objects) {
            UnmatchTrans unmatchTrans = new UnmatchTrans();

            unmatchTrans.setPeriod((String) obj[0]);
            unmatchTrans.setIn((BigDecimal) obj[1]);
            unmatchTrans.setOut((BigDecimal) obj[2]);
            unmatchTrans.setVariance((BigDecimal) obj[3]);
            unmatchTrans.setPeriodbalance((String) obj[4]);
            unmatchTrans.setDummydate((Date) obj[5]);
            UnmatchTransList.add(unmatchTrans);
        }
        return UnmatchTransList;
    }

    @Override
    public List<UnmatchTrans> sp_getutldays(UnmatchTransRequest unmatchedTransRequest) {
        List<UnmatchTrans> result = Collections.emptyList();

        // try {
            List<Object[]> objects = unmatchTransRepository.sp_getutldays(unmatchedTransRequest);
            result = convertUnmatchTransDay(objects);
        // } catch (Exception e) {
        //     e.printStackTrace();
        //     log.error("Exception in " + this.getClass().toString(), e);
        // }

        return result;
    }

    private List<UnmatchTrans> convertUnmatchTransDay(List<Object[]> objects) {
        List<UnmatchTrans> UnmatchTransList = new ArrayList<>();

        for (Object[] obj : objects) {
            UnmatchTrans unmatchTrans = new UnmatchTrans();

            unmatchTrans.setPeriod((String) obj[0]);
            unmatchTrans.setIn((BigDecimal) obj[1]);
            unmatchTrans.setOut((BigDecimal) obj[2]);
            unmatchTrans.setVariance((BigDecimal) obj[3]);
            unmatchTrans.setPeriodbalance((String) obj[4]);
            unmatchTrans.setDummydate((Date) obj[5]);
            UnmatchTransList.add(unmatchTrans);
        }
        return UnmatchTransList;
    }

    

}
