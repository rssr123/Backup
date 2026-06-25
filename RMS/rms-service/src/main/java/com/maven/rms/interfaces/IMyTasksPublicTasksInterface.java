package com.maven.rms.interfaces;

import java.util.List;
import java.util.Map;

import com.maven.rms.models.MyTaskPublicTaskRequest;
import com.maven.rms.models.PickUpTasksRequest;

public interface IMyTasksPublicTasksInterface {
    
    List<Object[]> sp_getpublictasks(MyTaskPublicTaskRequest myTaskPublicTaskRequest);

    Integer sp_pickuptasks(List<PickUpTasksRequest> pickUpTasksRequests);
    
    public Map<String, Integer> sp_getallnotificationcounts(String assignTo);
    
}
