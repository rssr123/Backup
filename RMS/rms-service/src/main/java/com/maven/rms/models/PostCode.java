package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCode {
    
    private String postcode;
    private String location;
    private String state;
    private String city;
    private Integer total;

}
