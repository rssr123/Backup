package com.maven.rms.services;

import java.util.Collections;
import java.util.HashMap;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IMyTasksPublicTasksService;
import com.maven.rms.models.MyTaskPublicTask;
import com.maven.rms.models.MyTaskPublicTaskRequest;
import com.maven.rms.models.PickUpTasksRequest;
import com.maven.rms.repositories.MyTasksPublicTasksRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MyTasksPublicTasksService implements IMyTasksPublicTasksService {
    
    private final MyTasksPublicTasksRepository myTasksPublicTasksRepository;

    public MyTasksPublicTasksService(MyTasksPublicTasksRepository myTasksPublicTasksRepository) {
        this.myTasksPublicTasksRepository = myTasksPublicTasksRepository;
    }

    @Override
    public List<MyTaskPublicTask> sp_getpublictasks(MyTaskPublicTaskRequest myTaskPublicTaskRequest) {
        
        List<MyTaskPublicTask> result = Collections.emptyList();

        List<Object[]> objects = myTasksPublicTasksRepository.sp_getpublictasks(myTaskPublicTaskRequest);

        result = convertToGetPublicTasks(objects);

        return result;
    }

    private List<MyTaskPublicTask> convertToGetPublicTasks(List<Object[]> objects) {
    	//Put in set to remove duplicates
        //Map<String, MyTaskPublicTask> publicTasksSet = new HashMap<String, MyTaskPublicTask>();
    	List<MyTaskPublicTask> data = new ArrayList<MyTaskPublicTask>();
    	List<String> taskIds = new ArrayList<String>();

        for (Object[] obj : objects)  {
            MyTaskPublicTask publicTask = new MyTaskPublicTask();

            publicTask.setTask_id((String) obj[0]);
            publicTask.setTask_desc((String) obj[1]);
            publicTask.setRequested_by((String) obj[2]);
            publicTask.setDt_requested(((Timestamp) obj[3]).toLocalDateTime());
            publicTask.setPickup_by((String) obj[4]);
            // publicTask.setDt_pick(((Timestamp) obj[5]).toLocalDateTime());
            Timestamp timestamp = (Timestamp) obj[5];
            if (timestamp != null) {
                publicTask.setDt_pick(timestamp.toLocalDateTime());
            } else {
                publicTask.setDt_pick(null);
            }
            publicTask.setTask_status((String) obj[6]);
            publicTask.setOrigin_table((String) obj[7]);
            publicTask.setPk((BigInteger) obj[8]);
            publicTask.setTotal((Integer) obj[9]);
            
            if(!taskIds.contains(publicTask.getTask_id())) {
                data.add(publicTask);
                taskIds.add(publicTask.getTask_id());
            }
            //publicTasksSet.put(publicTask.getTask_id(), publicTask); //duplicates are determined by taskId
        }
        return data;
        //return new ArrayList<MyTaskPublicTask>(publicTasksSet.values());
    }

    @Override
    public Integer sp_pickuptasks(List<PickUpTasksRequest> pickUpTasksRequests) {
        Integer result = 0;

        result = myTasksPublicTasksRepository.sp_pickuptasks(pickUpTasksRequests);

        return result;
    }

    @Override
    public Map<String, Integer> sp_getallnotificationcounts(String assignTo){
    	return myTasksPublicTasksRepository.sp_getallnotificationcounts(assignTo);
    }
    
}
