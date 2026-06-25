package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdamanAPIDownloadRequest {

    private String refNo1;
    private String verID;
    private String sourceSysDocRefID;

    private String rcptNo;
    private String versionId;
    private String ssdocrefId;
    private String requestorId;
    private String profileName;

    public IdamanAPIDownloadRequest() {
    }

    public IdamanAPIDownloadRequest(String requestorId, String profileName, String refNo1, String verID, String sourceSysDocRefID) {
        this.requestorId = requestorId;
        this.profileName = profileName;
        this.refNo1 = refNo1;
        this.verID = verID;
        this.sourceSysDocRefID = sourceSysDocRefID;
    }

    public IdamanAPIDownloadRequest(String refNo1, String verID, String sourceSysDocRefID) {
        this.refNo1 = refNo1;
        this.verID = verID;
        this.sourceSysDocRefID = sourceSysDocRefID;
    }

    
    
}
