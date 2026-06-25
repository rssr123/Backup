package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdamanAPISearchRequest {

    private String refNo1;

    // public String getRefNo1() {
    //     return refNo1;
    // }

    // public void setRefNo1(String refNo1) {
    //     this.refNo1 = refNo1;
    // }

    public IdamanAPISearchRequest(String refNo1) {
        this.refNo1 = refNo1;
    }


    
}
