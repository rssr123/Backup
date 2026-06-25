package com.maven.rms.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.maven.rms.models.CCTaskList;
import com.maven.rms.models.CCTaskListReq;
import com.maven.rms.repositories.CreditControlSMERepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CreditControlSMEService implements ICreditControlSMEService {
    private final CreditControlSMERepository creditControlSMERepository;

    // @Autowired
    // private AuthService authService;

    public CreditControlSMEService(CreditControlSMERepository creditControlSMERepository) {
        this.creditControlSMERepository = creditControlSMERepository;
    }

    @Override
    public List<CCTaskList> sp_getcreditcontroltasklist(CCTaskListReq req) {
        List<CCTaskList> result = Collections.emptyList();
        List<Object[]> objects = creditControlSMERepository.sp_getcreditcontroltasklist(req);
        result = convertCCTaskList(objects);
        return result;
    }

    private List<CCTaskList> convertCCTaskList(List<Object[]> objects) {
        List<CCTaskList> ccTaskLists = new ArrayList<>();

        for (Object[] obj : objects) {
            CCTaskList ccTaskList = new CCTaskList();
            ccTaskList.setCc_case_id((Integer) obj[0]);
            ccTaskList.setTask_id((String) obj[1]);
            ccTaskList.setTask_status((String) obj[2]);
            ccTaskList.setAssign_to((String) obj[3]);
            ccTaskList.setPick_up((String) obj[4]);
            ccTaskList.setPymt_status((String) obj[5]);
            ccTaskList.setTxn_ty((String) obj[6]);
            ccTaskList.setAttr_case_no((String) obj[7]);
            ccTaskList.setReminder_cnt((Integer) obj[8]);
            ccTaskList.setReminder_dt((Date) obj[9]);
            ccTaskList.setTxn_total_amt((BigDecimal) obj[10]);
            ccTaskList.setCust_nm((String) obj[11]);
            ccTaskList.setTotal((Integer) obj[12]);
            ccTaskLists.add(ccTaskList);
        }
        return ccTaskLists;
    }

    @Override
    public Integer sp_assigncctask(List<CCTaskListReq> req) {
        Integer result = 0;
        try {
            result = creditControlSMERepository.sp_assigncctask(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
