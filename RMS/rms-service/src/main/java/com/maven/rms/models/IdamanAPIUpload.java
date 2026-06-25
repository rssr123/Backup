package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdamanAPIUpload {
    private String id;
    private String code;
    private String desc;
    private String verid;

    private DataModel data;

    @Getter
    @Setter
    public static class DataModel{
        private String verID;
    }
    
    // public Object getStatus() {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'getStatus'");
    // }
    
}
