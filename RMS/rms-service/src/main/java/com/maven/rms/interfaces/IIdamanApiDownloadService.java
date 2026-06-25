package com.maven.rms.interfaces;

import java.io.IOException;
import java.util.List;

import com.maven.rms.models.IdamanAPIDownload;
import com.maven.rms.models.IdamanAPIDownloadRequest;



public interface IIdamanApiDownloadService {

    // List<IdamanAPIDownload> idaman_api_downloadDoc(String refNo1, String verID, String sourceSysDocRefID);

    List<IdamanAPIDownload> idaman_api_downloadDoc(IdamanAPIDownloadRequest idamanAPIDownloadRequest) throws IOException;

}