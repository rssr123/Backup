package com.maven.rms.interfaces;

import java.util.List;
import java.util.Map;

import com.maven.rms.models.MyTaskPublicTask;
import com.maven.rms.models.MyTaskPublicTaskRequest;
import com.maven.rms.models.PickUpTasksRequest;

public interface IMyTasksPublicTasksService {
    
    List<MyTaskPublicTask> sp_getpublictasks(MyTaskPublicTaskRequest myTaskPublicTaskRequest);

    Integer sp_pickuptasks(List<PickUpTasksRequest> pickUpTasksRequest);
    
    Map<String, Integer> sp_getallnotificationcounts(String assignTo);
}
