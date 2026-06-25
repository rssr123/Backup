package com.maven.rms.interfaces;

import java.io.IOException;
import java.util.List;

import com.maven.rms.models.IdamanAPIUpload;
import com.maven.rms.models.IdamanAPIUploadReq;

public interface IIdamanApiUploadService {

    List<IdamanAPIUpload> idaman_api_uploadDoc(IdamanAPIUploadReq req) throws IOException;
    
}
