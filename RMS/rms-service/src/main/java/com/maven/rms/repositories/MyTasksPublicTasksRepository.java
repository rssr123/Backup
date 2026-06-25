package com.maven.rms.repositories;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maven.rms.models.MyTaskPublicTaskRequest;
import com.maven.rms.models.PickUpTasksRequest;

import lombok.extern.slf4j.Slf4j;

import com.maven.rms.interfaces.IMyTasksPublicTasksInterface;

@Repository
@Slf4j
public class MyTasksPublicTasksRepository implements IMyTasksPublicTasksInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> sp_getpublictasks(MyTaskPublicTaskRequest myTaskPublicTaskRequest) {

        /*String[] userRoleList = myTaskPublicTaskRequest.getI_userrole().split(",");
        List<Object[]> resultList = new ArrayList<>();
        for (String userRole : userRoleList) {
            Query query = entityManager.createNativeQuery(
                    "CALL sp_getpublictasks(:i_page, :i_size, :i_username, :i_userrole, :i_task_id, :i_task_desc, :i_requested_by, :i_dt_requested, :i_status)")
                    .setParameter("i_page", myTaskPublicTaskRequest.getI_page())
                    .setParameter("i_size", myTaskPublicTaskRequest.getI_size())
                    .setParameter("i_username", myTaskPublicTaskRequest.getI_username().trim())
                    .setParameter("i_userrole", userRole.trim())
                    .setParameter("i_task_id",
                            myTaskPublicTaskRequest.getI_task_id() == null ? null
                                    : myTaskPublicTaskRequest.getI_task_id().trim())
                    .setParameter("i_task_desc",
                            myTaskPublicTaskRequest.getI_task_desc() == null ? null
                                    : myTaskPublicTaskRequest.getI_task_desc().trim())
                    .setParameter("i_requested_by",
                            myTaskPublicTaskRequest.getI_requested_by() == null ? null
                                    : myTaskPublicTaskRequest.getI_requested_by().trim())
                    .setParameter("i_dt_requested",
                            myTaskPublicTaskRequest.getI_dt_requested() == null ? null
                                    : myTaskPublicTaskRequest.getI_dt_requested())
                    .setParameter("i_status", myTaskPublicTaskRequest.getI_status() == null ? null
                            : myTaskPublicTaskRequest.getI_status().trim());

            List<Object[]> result = query.getResultList();

            resultList.addAll(result);
        }*/
    	
        Query query = entityManager.createNativeQuery(
            "CALL sp_getpublictasks(:i_page, :i_size, :i_username, :i_userrole, :i_task_id, :i_task_desc, :i_requested_by, :i_dt_requested, :i_status)")
            .setParameter("i_page", myTaskPublicTaskRequest.getI_page())
            .setParameter("i_size", myTaskPublicTaskRequest.getI_size())
            .setParameter("i_username", myTaskPublicTaskRequest.getI_username().trim())
            .setParameter("i_userrole", myTaskPublicTaskRequest.getI_userrole().trim())
            .setParameter("i_task_id",
                    myTaskPublicTaskRequest.getI_task_id() == null ? null
                            : myTaskPublicTaskRequest.getI_task_id().trim())
            .setParameter("i_task_desc",
                    myTaskPublicTaskRequest.getI_task_desc() == null ? null
                            : myTaskPublicTaskRequest.getI_task_desc().trim())
            .setParameter("i_requested_by",
                    myTaskPublicTaskRequest.getI_requested_by() == null ? null
                            : myTaskPublicTaskRequest.getI_requested_by().trim())
            .setParameter("i_dt_requested",
                    myTaskPublicTaskRequest.getI_dt_requested() == null ? null
                            : myTaskPublicTaskRequest.getI_dt_requested())
            .setParameter("i_status", myTaskPublicTaskRequest.getI_status() == null ? null
                    : myTaskPublicTaskRequest.getI_status().trim());

        List<Object[]> result = query.getResultList();


        return result;
    }

    @Override
    public Integer sp_pickuptasks(List<PickUpTasksRequest> pickUpTasksRequests) {
        Integer totalResult = 0;

        for (PickUpTasksRequest pickUpTasksRequest : pickUpTasksRequests) {
            Query query = entityManager
                    .createNativeQuery("CALL sp_pickuptasks(:i_pk, :i_pickup_person, :i_origin_table)")
                    .setParameter("i_pk", pickUpTasksRequest.getI_pk())
                    .setParameter("i_pickup_person", pickUpTasksRequest.getI_pickup_person())
                    .setParameter("i_origin_table", pickUpTasksRequest.getI_origin_table());

            Integer result = (Integer) query.getSingleResult();
            totalResult += result;
        }

        return totalResult;

    }

    @Override
    public Map<String, Integer> sp_getallnotificationcounts(String assignTo) {
        // return getNotificationCount_v1(assignTo);

        // 20250717, Roy, enhance to handle null pointer issue
        Map<String, Integer> result = getNotificationCount_v2(assignTo);

        return result;

    }

    // this one always hit null pointed at ((String)
    // query.getSingleResult()).split(",");
    // then create alot error in error log
    private Map<String, Integer> getNotificationCount_v1(String assignTo) {
        Query query = entityManager.createNativeQuery("CALL sp_getntcnts(:i_assign_to)")
                .setParameter("i_assign_to", assignTo);

        String[] result = ((String) query.getSingleResult()).split(",");
        Map<String, Integer> data = new HashMap<String, Integer>();
        data.put("mft_a", Integer.parseInt(result[0]));
        data.put("mft_c", Integer.parseInt(result[1]));
        data.put("otc_rc_a", Integer.parseInt(result[2]));
        data.put("otc_rc_c", Integer.parseInt(result[3]));
        data.put("rtt_a", Integer.parseInt(result[4]));
        data.put("rtt_c", Integer.parseInt(result[5]));
        data.put("bil_a", Integer.parseInt(result[6]));
        data.put("bil_c", Integer.parseInt(result[7]));
        data.put("cc_a", Integer.parseInt(result[8]));
        data.put("cc_c", Integer.parseInt(result[9]));

        return data;

    }

    // ver2 GetNotificationCount, 20250717, Roy
    private Map<String, Integer> getNotificationCount_v2(String assignTo) {
        try {
            Query query = entityManager.createNativeQuery("CALL sp_getntcnts(:i_assign_to)")
                    .setParameter("i_assign_to", assignTo);

            Object queryResult = query.getSingleResult();

            // Handle null result
            if (queryResult == null) {
                log.info("[GetNotificationCount] Stored Procedure returned Null");
                return createDefaultNotificationCounts();
            }

            String resultString = queryResult.toString();

            // Check if result string is empty or just whitespace
            if (resultString == null || resultString.trim().isEmpty()) {
                log.info("[GetNotificationCount] result is empty");
                return createDefaultNotificationCounts();
            }

            String[] result = resultString.split(",");

            // Validate we have enough elements
            if (result.length < 10) {
                log.info("[GetNotificationCount] lack of data from sp result");
                return createDefaultNotificationCounts();
            }

            Map<String, Integer> data = new HashMap<>();
            try {
                data.put("mft_a", Integer.parseInt(result[0].trim()));
                data.put("mft_c", Integer.parseInt(result[1].trim()));
                data.put("otc_rc_a", Integer.parseInt(result[2].trim()));
                data.put("otc_rc_c", Integer.parseInt(result[3].trim()));
                data.put("rtt_a", Integer.parseInt(result[4].trim()));
                data.put("rtt_c", Integer.parseInt(result[5].trim()));
                data.put("bil_a", Integer.parseInt(result[6].trim()));
                data.put("bil_c", Integer.parseInt(result[7].trim()));
                data.put("cc_a", Integer.parseInt(result[8].trim()));
                data.put("cc_c", Integer.parseInt(result[9].trim()));
            } catch (NumberFormatException | NullPointerException e) {
                log.error("[GetNotificationCount] unable to parse numeric value", e);
                return createDefaultNotificationCounts();
            }

            return data;

        } catch (NoResultException e) {
            log.warn("No result returned from stored procedure for assignTo: " + assignTo);
            return createDefaultNotificationCounts();
        } catch (Exception e) {
            log.error("Error executing stored procedure sp_getntcnts for assignTo: " + assignTo, e);
            return createDefaultNotificationCounts();
        }
    }

    private Map<String, Integer> createDefaultNotificationCounts() {
        Map<String, Integer> defaults = new HashMap<>();
        defaults.put("mft_a", 0);
        defaults.put("mft_c", 0);
        defaults.put("otc_rc_a", 0);
        defaults.put("otc_rc_c", 0);
        defaults.put("rtt_a", 0);
        defaults.put("rtt_c", 0);
        defaults.put("bil_a", 0);
        defaults.put("bil_c", 0);
        defaults.put("cc_a", 0);
        defaults.put("cc_c", 0);
        return defaults;
    }

}
