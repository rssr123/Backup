package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PickUpTasksRequest {
    
    private Integer i_pk;
    private String i_pickup_person;
    private String i_origin_table;

}
