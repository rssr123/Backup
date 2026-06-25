package com.maven.rms.interfaces;

import java.io.IOException;

import com.maven.rms.models.IdamanAPITokenReq;

public interface IIdamanApiTokenService {
    String getOAuth2Token() throws IOException;

    Integer updidamantoken(IdamanAPITokenReq bodyReq);

    String getidamantoken();
}
