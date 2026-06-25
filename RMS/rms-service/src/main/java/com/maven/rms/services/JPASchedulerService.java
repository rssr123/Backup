package com.maven.rms.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.maven.rms.models.JPAScheduler;
import com.maven.rms.models.JPASchedulerRequest;
import com.maven.rms.repositories.JPASchedulerRepository;

@Service
public class JPASchedulerService {

    private final JPASchedulerRepository jpaSchedulerRepo;

    public JPASchedulerService(JPASchedulerRepository jpaSchedulerRepo) {
        this.jpaSchedulerRepo = jpaSchedulerRepo;
    }

    // public List<JPAScheduler> sp_getfpascheduler(String i_job_name) {
    public List<JPAScheduler> sp_getfpascheduler(JPASchedulerRequest jpaSchedulerRequest) {
        List<JPAScheduler> result = Collections.emptyList();

        // try {
            List<Object[]> objects = jpaSchedulerRepo.sp_getfpascheduler(jpaSchedulerRequest);
            result = convertFPASchedulerList(objects);
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }

        return result;
    }

    private List<JPAScheduler> convertFPASchedulerList(List<Object[]> objects) {
        List<JPAScheduler> jpaSchedulerList = new ArrayList<>();

        for (Object[] obj : objects) {
            JPAScheduler jpaScheduler = new JPAScheduler();
            jpaScheduler.setRow_number((Integer) obj[0]);
            jpaScheduler.setJob_name((String) obj[1]);
            jpaScheduler.setLast_attempt_date((Date) obj[2]);
            jpaScheduler.setResp_status((String) obj[3]);
            jpaScheduler.setNext_attempt_time((Date) obj[4]);
            jpaScheduler.setTotal((Integer) obj[5]);
            jpaSchedulerList.add(jpaScheduler);
        }

        return jpaSchedulerList;
    }
}
